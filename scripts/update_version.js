/*
 * Copyright 2019 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

const fs = require("fs");
const path = require("path");
const execSync = require("child_process").execSync;
const { getPackagesSync } = require("@lerna/project");
const yaml = require("js-yaml");
const buildEnv = require("@kogito-tooling/build-env");

const CHROME_EXTENSION_MANIFEST_DEV_JSON = path.resolve(
  "./packages/chrome-extension-pack-kogito-kie-editors/manifest.dev.json"
);
const CHROME_EXTENSION_MANIFEST_PROD_JSON = path.resolve(
  "./packages/chrome-extension-pack-kogito-kie-editors/manifest.prod.json"
);
const EXTENDED_SERVICES_CONFIG_FILE = path.resolve("./packages/extended-services/pkg/config/config.yaml");
const LERNA_JSON = path.resolve("./lerna.json");

// MAIN

const newVersion = process.argv[2];
if (!newVersion) {
  console.error("[update-version] Missing version argument.");
  return 1;
}

let execOpts = {};
const opts = process.argv[3];
if (opts === "--verbose") {
  execOpts = { stdio: "inherit" };
} else {
  execOpts = { stdio: "pipe" };
}

Promise.resolve()
  .then(() => updateNpmPackages(newVersion))
  .then((version) => updateMvnPackages(version))
  .then((version) => updateChromeExtensionManifestFiles(version))
  .then((version) => updateExtendedServicesConfigFile(version))
  .then(async (version) => {
    console.info(`[update-version] Formatting files...`);
    execSync(`yarn format`, execOpts);
    return version;
  })
  .then((version) => {
    console.info(`[update-version] Updated to '${version}'.`);
  })
  .catch((error) => {
    console.error(error);
    console.error("");
    console.error(`[update-version] Error updating versions. There might be undesired unstaged changes.`);
  });

//

async function updateNpmPackages(version) {
  console.info("[update-version] Updating NPM packages...");

  execSync(`lerna version ${version} --no-push --no-git-tag-version --exact --yes`, execOpts);
  return require(LERNA_JSON).version;
}

async function updateMvnPackages(version) {
  console.info("[update-version] Updating Maven packages...");

  const mvnPackages = getPackagesSync().filter((pkg) => fs.existsSync(path.resolve(pkg.location, "pom.xml")));
  const mvnPackagesLernaScope = mvnPackages.map((pkg) => `--scope="${pkg.name}"`).join(" ");
  execSync(
    `lerna exec 'mvn versions:set versions:commit -DnewVersion=${version} -DKOGITO_RUNTIME_VERSION=${buildEnv.kogitoRuntime.version} -DQUARKUS_PLATFORM_VERSION=${buildEnv.quarkusPlatform.version}' ${mvnPackagesLernaScope} --concurrency 1`,
    execOpts
  );
  return version;
}

async function updateChromeExtensionManifestFiles(version) {
  console.info("[update-version] Updating Chrome Extension manifest files...");

  await updateChromeExtensionManifest(version, CHROME_EXTENSION_MANIFEST_DEV_JSON);
  await updateChromeExtensionManifest(version, CHROME_EXTENSION_MANIFEST_PROD_JSON);

  return version;
}

async function updateChromeExtensionManifest(version, manifestPath) {
  const manifest = { ...require(manifestPath), version };
  manifest.version = version;

  fs.writeFileSync(manifestPath, JSON.stringify(manifest));
}

async function updateExtendedServicesConfigFile(version) {
  console.info("[update-version] Updating Extended Services config file...");
  const config = yaml.load(fs.readFileSync(EXTENDED_SERVICES_CONFIG_FILE, "utf-8"));
  config.app.version = version;
  fs.writeFileSync(EXTENDED_SERVICES_CONFIG_FILE, yaml.dump(config));

  return version;
}
