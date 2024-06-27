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

const { execSync } = require("child_process");
const fs = require("fs");
const path = require("path");

const MVN_CONFIG_ORIGINAL_FILE_PATH = path.join(".mvn", "maven.config.original");
const MVN_CONFIG_FILE_PATH = path.join(".mvn", "maven.config");
const MVN_POM_FILE_PATH = path.resolve("./pom.xml");
const MVN_FLAT_POM_XML = ".flat_pom.xml";
const MVN_PARENT_RELATIVE_PATH = `<relativePath>../${MVN_FLAT_POM_XML}</relativePath>`;

module.exports = {
  setRevisionVersion: (newVersion) => {
    if (!newVersion) {
      console.error("[maven-config-setup-helper] Wrong values provided");
      process.exit(1);
    }
    if (!fs.existsSync(MVN_POM_FILE_PATH)) {
      console.error("[maven-config-setup-helper] pom.xml not found");
      process.exit(1);
    }

    const rootPath = path.dirname(MVN_POM_FILE_PATH);

    const processPomXML = (pomPath) => {
      if (path.basename(pomPath) === "pom.xml") {
        const newPomPath = path.resolve(path.dirname(pomPath), MVN_FLAT_POM_XML);

        console.info(`[maven-config-setup-helper] Creating "${MVN_FLAT_POM_XML}" for "${pomPath}"`);

        if (fs.existsSync(newPomPath)) {
          console.info(`[maven-config-setup-helper] Found existing "${MVN_FLAT_POM_XML}"... removing`);
          fs.rmSync(newPomPath);
        }

        fs.copyFileSync(pomPath, newPomPath);

        let pomContent = fs
          .readFileSync(newPomPath, "utf-8")
          .replace(/\${revision}/g, newVersion)
          .replace(/<\/module>/g, `/${MVN_FLAT_POM_XML}</module>`);

        if (pomContent.includes("<relativePath>")) {
          pomContent = pomContent.replace(
            /.\/node_modules\/@kie-tools\/maven-base\/pom.xml/,
            `./node_modules/@kie-tools/maven-base/${MVN_FLAT_POM_XML}`
          );
        } else if (path.dirname(pomPath) !== rootPath) {
          pomContent = pomContent.replace(/<\/parent>/, `${MVN_PARENT_RELATIVE_PATH}</parent>`);
        }

        fs.writeFileSync(newPomPath, pomContent);
      }
    };

    const processMavenModule = (modulePath) => {
      if (!modulePath) {
        console.error("[maven-config-setup-helper] module path not found");
        process.exit(1);
      }

      const modulePom = path.resolve(modulePath, "./pom.xml");

      if (fs.existsSync(modulePom)) {
        processPomXML(modulePom);

        fs.readdirSync(modulePath).forEach((file) => {
          const filePath = path.resolve(modulePath, file);
          const stat = fs.statSync(filePath);
          if (stat.isDirectory()) {
            processMavenModule(filePath);
          }
        });
      }
    };

    processMavenModule(rootPath);
  },
  setPomProperty: ({ key, value }) => {
    if (!key || !value) {
      console.error("[maven-config-setup-helper] Wrong values provided");
      process.exit(1);
    }

    if (process.platform === "win32") {
      execSync(`mvn versions:set-property \`-Dproperty=${key} \`-DnewVersion=${value} \`-DgenerateBackupPoms=false`, {
        stdio: "inherit",
        shell: "powershell.exe",
      });
    } else {
      execSync(`mvn versions:set-property -Dproperty=${key} -DnewVersion=${value} -DgenerateBackupPoms=false`, {
        stdio: "inherit",
      });
    }
  },
  setup: (mavenConfigString) => {
    let originalMvnConfigString;
    if (fs.existsSync(MVN_CONFIG_ORIGINAL_FILE_PATH)) {
      console.info(`[maven-config-setup-helper] Found '${MVN_CONFIG_ORIGINAL_FILE_PATH}'.`);
      originalMvnConfigString = fs.readFileSync(MVN_CONFIG_ORIGINAL_FILE_PATH, "utf-8");
    } else if (fs.existsSync(MVN_CONFIG_FILE_PATH)) {
      console.info(`[maven-config-setup-helper] Found '${MVN_CONFIG_FILE_PATH}'.`);
      originalMvnConfigString = fs.readFileSync(MVN_CONFIG_FILE_PATH, "utf-8");
    } else {
      console.info(`[maven-config-setup-helper] No previous config found.`);
      originalMvnConfigString = "";
    }

    fs.mkdirSync(".mvn", { recursive: true });

    console.info(`[maven-config-setup-helper] Writing '${MVN_CONFIG_ORIGINAL_FILE_PATH}'...`);
    console.info(`${originalMvnConfigString}` || "<empty>");
    fs.writeFileSync(MVN_CONFIG_ORIGINAL_FILE_PATH, originalMvnConfigString);

    const trimmedMavenConfigString = mavenConfigString
      .trim()
      .split("\n")
      .map((l) => l.trim())
      .join("\n");

    const newMavenConfigString = `${originalMvnConfigString.trim()}\n${trimmedMavenConfigString.trim()}`.trim();
    console.info(`[maven-config-setup-helper] Writing '${MVN_CONFIG_FILE_PATH}'...`);
    console.info(newMavenConfigString);
    fs.writeFileSync(MVN_CONFIG_FILE_PATH, newMavenConfigString);

    console.info(`[maven-config-setup-helper] Done.`);
  },
};
