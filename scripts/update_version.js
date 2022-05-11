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
const buildEnv = require("../packages/build-env");

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

const ORIGINAL_LERNA_JSON = require("../lerna.json");

// MAIN

const newVersion = process.argv[2];
if (!newVersion) {
  console.error("[update-version] Missing version argument.");
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
  .then((version) => updateMvnPackages(version))
  .then((version) => updateSpecialInternalMvnPackagesOnStunnerEditors(version))
  .then((version) => updateSpecialInternalMvnPackagesOnSwfDiagramEditor(version))
  .then((version) => updateChromeKieEditorsExtensionManifestFiles(version))
  .then((version) => updateChromeSwEditorsExtensionManifestFiles(version))
  .then((version) => updateExtendedServicesConfigFile(version))
  .then((version) => updateJavaAutocompletionPluginManifestFile(version))
  .then((version) => updateLockfile(version))
  .then(async (version) => {
    console.info(`[update-version] Formatting files...`);
    execSync(`yarn pretty-quick`, execOpts);
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
  return version;
}

async function updateMvnPackages(version) {
  console.info("[update-version] Updating Maven packages...");

  const mvnPackages = getPackagesSync().filter((pkg) => fs.existsSync(path.resolve(pkg.location, "pom.xml")));
  const mvnPackagesPnpmFilters = mvnPackages.map((pkg) => `-F="${pkg.name}"`).join(" ");
  execSync(
    `pnpm -r ${mvnPackagesPnpmFilters} --workspace-concurrency=1 exec 'bash' '-c' 'mvn versions:set versions:commit -DnewVersion=${version} -DKOGITO_RUNTIME_VERSION=${buildEnv.kogitoRuntime.version} -DQUARKUS_PLATFORM_VERSION=${buildEnv.quarkusPlatform.version}'`,
    execOpts
  );
  return version;
}

async function updateSpecialInternalMvnPackagesOnStunnerEditors(version) {
  console.info("[update-version] Updating special Maven packages on Stunner Editors...");

  const modules = [
    `packages/stunner-editors/errai-bom`,
    `packages/stunner-editors/appformer-bom`,
    `packages/stunner-editors/kie-wb-common-bom`,
    `packages/stunner-editors/drools-wb-bom`,
  ];

  for (const module of modules) {
    execSync(
      `mvn versions:set versions:commit -f ${module} -DnewVersion=${version} -DKOGITO_RUNTIME_VERSION=${buildEnv.kogitoRuntime.version} -DQUARKUS_PLATFORM_VERSION=${buildEnv.quarkusPlatform.version}`,
      execOpts
    );
  }

  return version;
}

async function updateSpecialInternalMvnPackagesOnSwfDiagramEditor(version) {
  console.info("[update-version] Updating special Maven packages on Serverless Workflow Diagram Editor...");

  const modules = [
    `packages/serverless-workflow-diagram-editor/errai-bom`,
    `packages/serverless-workflow-diagram-editor/appformer-bom`,
    `packages/serverless-workflow-diagram-editor/kie-wb-common-bom`,
  ];

  for (const module of modules) {
    execSync(
      `mvn versions:set versions:commit -f ${module} -DnewVersion=${version} -DKOGITO_RUNTIME_VERSION=${buildEnv.kogitoRuntime.version} -DQUARKUS_PLATFORM_VERSION=${buildEnv.quarkusPlatform.version}`,
      execOpts
    );
  }

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
    `Bundle-Version: ${ORIGINAL_LERNA_JSON.version}`,
    `Bundle-Version: ${version}`
  );
  fs.writeFileSync(JAVA_AUTOCOMPLETION_PLUGIN_MANIFEST_FILE, newManifestFile);

  return version;
}

async function updateLockfile(version) {
  execSync(`yarn bootstrap`, execOpts);
  return version;
}
