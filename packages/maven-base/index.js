/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

const cp = require("child_process");
const fs = require("fs");
const path = require("path");
const { env } = require("./env");

// Constants relative to consumer packages.
const MVN_CONFIG_ORIGINAL_FILE_PATH = path.join(".mvn", "maven.config.original");
const MVN_CONFIG_FILE_PATH = path.join(".mvn", "maven.config");

// This package's constants.
const EMPTY_POM_XML_PATH = path.join(__dirname, "empty-pom.xml");
const SETTINGS_XML_PATH = path.join(__dirname, "settings.xml");

const DEFAULT_MAVEN_CONFIG = `
-Dstyle.color=always
--batch-mode
--settings=${SETTINGS_XML_PATH}
`.trim();

const DEFAULT_LOCAL_REPO = String(
  cp.execSync(`mvn help:evaluate -Dexpression=settings.localRepository -q -DforceStdout -f ${EMPTY_POM_XML_PATH}`, {
    stdio: "pipe",
    encoding: "utf-8",
  })
).trim();

const BOOTSTRAP_CLI_ARGS = `-P-include-1st-party-dependencies --settings=${SETTINGS_XML_PATH}`;

module.exports = {
  /**
   * Evaluation of ${settings.localRepository}.
   */
  DEFAULT_LOCAL_REPO,

  /**
   * Maven CLI arguments to be passed for `mvn` commands running during `bootstrap` phase.
   */
  BOOTSTRAP_CLI_ARGS,

  /**
   * An absolute path for an empty POM, in case someone needs to run `mvn` scripts without having a pom.xml file.
   */
  EMPTY_POM_XML_PATH,

  /**
   * Installs `mvnw` on the same directory of invocation.
   *  */
  installMvnw: () => {
    console.info(`[maven-base] Installing mvnw...`);
    console.time(`[maven-base] Installing mvnw...`);

    const cmd = `mvn -e org.apache.maven.plugins:maven-wrapper-plugin:${env.mvnw.version}:wrapper ${BOOTSTRAP_CLI_ARGS}`;

    if (process.platform === "win32") {
      cp.execSync(cmd.replaceAll(" -", " `-"), { stdio: "inherit", shell: "powershell.exe" });
    } else {
      cp.execSync(cmd, { stdio: "inherit" });
    }

    console.timeEnd(`[maven-base] Installing mvnw...`);
  },

  /**
   * Helps setting up an array of absolute paths that will be used to configure `-Dmaven.repo.local.tail`.
   *
   * @param dirname Where to locate the first package.json.
   *
   * @returns A comma-separated string containing a flat list of absolute paths of local Maven repositories.
   */
  buildTailFromPackageJsonDependencies: (dirname) => {
    return deepResolveMavenLocalRepoTail(path.resolve(dirname ?? ".")).join(",");
  },

  /**
   * Builds a single Maven repository directory out of multiple local Maven repositories using hard links.
   *
   * @param tmpM2Dir Relative path of this new Maven repository directory. It will be deleted and recreated for each invocation.
   * @param relativePackagePath A list of paths representing additional Maven repository directories, to be concatenated the default one (I.e, `maven.repo.local`)
   *  */
  prepareHardLinkedM2ForPackage: (tmpM2Dir, relativePackagePath) => {
    const resolvedTmpM2Dir = path.resolve(tmpM2Dir);
    if (fs.existsSync(resolvedTmpM2Dir)) {
      fs.rmSync(resolvedTmpM2Dir, { recursive: true, force: true });
    }
    fs.mkdirSync(resolvedTmpM2Dir, { recursive: true });

    // head
    cp.execSync(`cp -nal ${DEFAULT_LOCAL_REPO}/* ${resolvedTmpM2Dir}`, { stdio: "inherit" });

    const cwd = path.resolve(".", relativePackagePath);
    const packageName = require(path.resolve(cwd, "package.json")).name;
    const tail = deepResolveMavenLocalRepoTail(cwd, packageName);

    // tail
    for (const t of tail) {
      if (fs.existsSync(path.resolve(t))) {
        cp.execSync(`cp -al ${path.resolve(t)}/* ${resolvedTmpM2Dir}`, { stdio: "inherit" });
      }
    }
  },

  /**
   * Sets a property on a POM.
   *
   * @param entry An object with `key` and `value` properties
   */
  setPomProperty: ({ key, value }) => {
    if (!key || value === undefined) {
      console.error("[maven-base] Can't set a POM property without proper `key` and `value`.");
      process.exit(1);
    }

    console.info(`[maven-base] Setting property '${key}' with value '${value}'...`);
    console.time(`[maven-base] Setting property '${key}' with value '${value}'...`);

    const cmd = `mvn versions:set-property -Dproperty=${key} -DnewVersion=${value} -DgenerateBackupPoms=false ${BOOTSTRAP_CLI_ARGS}`;

    if (process.platform === "win32") {
      cp.execSync(cmd.replaceAll(" -", " `-"), { stdio: "inherit", shell: "powershell.exe" });
    } else {
      cp.execSync(cmd, { stdio: "inherit" });
    }

    console.timeEnd(`[maven-base] Setting property '${key}' with value '${value}'...`);
  },

  /**
   * Writes to `.mvn/maven.config` idempotently, preserving what was there before this function was called.
   *
   * @param pkgSpecificMvnConfigString New-line-separated string containing arguments to the `mvn` command.
   * @param args An object with a `ignoreDefault: boolean` property.
   */
  setupMavenConfigFile: (pkgSpecificMvnConfigString, args) => {
    console.info(`[maven-base] Configuring Maven through .mvn/maven.config...`);
    console.time(`[maven-base] Configuring Maven through .mvn/maven.config...`);

    let originalMvnConfigString;
    if (fs.existsSync(MVN_CONFIG_ORIGINAL_FILE_PATH)) {
      console.info(`[maven-base] Found '${MVN_CONFIG_ORIGINAL_FILE_PATH}'.`);
      originalMvnConfigString = fs.readFileSync(MVN_CONFIG_ORIGINAL_FILE_PATH, "utf-8");
    } else if (fs.existsSync(MVN_CONFIG_FILE_PATH)) {
      console.info(`[maven-base] Found '${MVN_CONFIG_FILE_PATH}'.`);
      originalMvnConfigString = fs.readFileSync(MVN_CONFIG_FILE_PATH, "utf-8");
    } else {
      console.info(`[maven-base] No previous config found.`);
      originalMvnConfigString = "";
    }

    fs.mkdirSync(".mvn", { recursive: true });

    console.info(`[maven-base] Writing '${MVN_CONFIG_ORIGINAL_FILE_PATH}'...`);
    console.info(`${originalMvnConfigString}` || "<empty>");
    fs.writeFileSync(MVN_CONFIG_ORIGINAL_FILE_PATH, originalMvnConfigString);

    const sanitizedPkgSpecificMvnConfigString = pkgSpecificMvnConfigString
      .trim()
      .split("\n")
      .map((line) => line.trim())
      .join("\n");

    const newMvnConfigString =
      (args?.ignoreDefault ? "" : `${DEFAULT_MAVEN_CONFIG}\n`) +
      (sanitizedPkgSpecificMvnConfigString ? `${sanitizedPkgSpecificMvnConfigString}\n` : "") +
      (originalMvnConfigString ? `${originalMvnConfigString}\n` : "");

    console.info(`[maven-base] Writing '${MVN_CONFIG_FILE_PATH}'...`);
    console.info(newMvnConfigString);

    fs.writeFileSync(MVN_CONFIG_FILE_PATH, newMvnConfigString);
    console.timeEnd(`[maven-base] Configuring Maven through .mvn/maven.config...`);
  },
};

// private functions

function deepResolveMavenLocalRepoTail(cwd) {
  const packageJsonDependencies = require(path.resolve(cwd, "package.json")).dependencies ?? {};
  return [
    ...new Set([
      path.resolve(fs.realpathSync(cwd), "dist/1st-party-m2/repository"),
      ...Object.entries(packageJsonDependencies).flatMap(([depName, depVersion]) =>
        depVersion === "workspace:*" // It's an internal package.
          ? deepResolveMavenLocalRepoTail(cwd + "/node_modules/" + depName)
          : []
      ),
    ]),
  ];
}
