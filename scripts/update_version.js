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
const yaml = require("js-yaml");

const CHROME_EXTENSION_KIE_EDITORS_MANIFEST_DEV_JSON = path.resolve(
  "./packages/chrome-extension-pack-kogito-kie-editors/manifest.dev.json"
);
const CHROME_EXTENSION_KIE_EDITORS_MANIFEST_PROD_JSON = path.resolve(
  "./packages/chrome-extension-pack-kogito-kie-editors/manifest.prod.json"
);
const CHROME_EXTENSION_SW_EDITOR_MANIFEST_DEV_JSON = path.resolve(
  "./packages/chrome-extension-serverless-workflow-editor/manifest.dev.json"
);
const CHROME_EXTENSION_SW_EDITOR_MANIFEST_PROD_JSON = path.resolve(
  "./packages/chrome-extension-serverless-workflow-editor/manifest.prod.json"
);
const EXTENDED_SERVICES_CONFIG_FILE = path.resolve("./packages/extended-services/pkg/config/config.yaml");
const JAVA_AUTOCOMPLETION_PLUGIN_MANIFEST_FILE = path.resolve(
  "./packages/vscode-java-code-completion-extension-plugin/vscode-java-code-completion-extension-plugin-core/META-INF/MANIFEST.MF"
);

const ORIGINAL_ROOT_PACKAGE_JSON = require("../package.json");

// MAIN

const newVersion = process.argv[2];
const pnpmFilter = ""; // TODO: `${process.argv.slice(3).join(" ")}`;

if (!newVersion) {
  console.error("Usage 'node update_version.js [version]'");
  return 1;
}

let execOpts = {};
const opts = process.argv[3];
if (opts === "--silent") {
  execOpts = { stdio: "pipe" };
} else {
  execOpts = { stdio: "inherit" };
}

Promise.resolve()
  .then(() => updateNpmPackages(newVersion))
  // TODO: extract to chrome-extension-pack-kogito-kie-editors
  .then((version) => updateChromeKieEditorsExtensionManifestFiles(version))
  // TODO: extract to chrome-extension-serverless-workflow-editor
  .then((version) => updateChromeSwEditorsExtensionManifestFiles(version))
  // TODO: extract to extended-services
  .then((version) => updateExtendedServicesConfigFile(version))
  // TODO: extract to vscode-java-code-completion-extension-plugin
  .then((version) => updateJavaAutocompletionPluginManifestFile(version))
  //
  .then((version) => runBootstrap(version))
  .then(async (version) => {
    console.info(`[update-version] Formatting files...`);
    execSync(`pnpm pretty-quick`, execOpts);
    return version;
  })
  .then((version) => {
    console.info(`[update-version] Updated to '${version}'.`);
    console.info(`[update-version] Done.`);
  })
  .catch((error) => {
    console.error(error);
    console.error("");
    console.error(`[update-version] Error updating versions. There might be undesired unstaged changes.`);
  });

//

async function updateNpmPackages(version) {
  console.info("[update-version] Updating root package...");
  execSync(`pnpm version ${version} --git-tag-version=false --allow-same-version=true`, execOpts);

  console.info("[update-version] Updating workspace packages...");
  execSync(
    `pnpm -r ${pnpmFilter} exec pnpm version ${version} --git-tag-version=false --allow-same-version=true`,
    execOpts
  );

  console.info("[update-version] Running 'update-version-to' script on workspace packages...");
  execSync(`pnpm -r ${pnpmFilter} update-version-to ${version}`, execOpts);
  return version;
}

async function updateChromeKieEditorsExtensionManifestFiles(version) {
  console.info("[update-version] Updating Chrome Extension for kie editors manifest files...");

  await updateChromeExtensionManifest(version, CHROME_EXTENSION_KIE_EDITORS_MANIFEST_DEV_JSON);
  await updateChromeExtensionManifest(version, CHROME_EXTENSION_KIE_EDITORS_MANIFEST_PROD_JSON);

  return version;
}

async function updateChromeSwEditorsExtensionManifestFiles(version) {
  console.info("[update-version] Updating Chrome Extension for sw editor manifest files...");

  await updateChromeExtensionManifest(version, CHROME_EXTENSION_SW_EDITOR_MANIFEST_DEV_JSON);
  await updateChromeExtensionManifest(version, CHROME_EXTENSION_SW_EDITOR_MANIFEST_PROD_JSON);

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

async function updateJavaAutocompletionPluginManifestFile(version) {
  console.info("[update-version] Updating Java Autocompletion Plugin Manifest file...");
  const manifestFile = fs.readFileSync(JAVA_AUTOCOMPLETION_PLUGIN_MANIFEST_FILE, "utf-8");
  const newManifestFile = manifestFile.replace(
    `Bundle-Version: ${ORIGINAL_ROOT_PACKAGE_JSON.version}`,
    `Bundle-Version: ${version}`
  );
  fs.writeFileSync(JAVA_AUTOCOMPLETION_PLUGIN_MANIFEST_FILE, newManifestFile);

  return version;
}

async function runBootstrap(version) {
  // TODO: use pnpmFilter
  execSync(`pnpm bootstrap`, execOpts);
  return version;
}
