/*
 * Copyright 2022 Red Hat, Inc. and/or its affiliates.
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

const CopyPlugin = require("copy-webpack-plugin");
const ZipPlugin = require("zip-webpack-plugin");
const packageJson = require("./package.json");
const patternflyBase = require("@kie-tools-core/patternfly-base");
const { merge } = require("webpack-merge");
const common = require("@kie-tools-core/webpack-base/webpack.common.config");
const { EnvironmentPlugin } = require("webpack");
const buildEnv = require("@kie-tools/build-env");
const path = require("path");
const MonacoWebpackPlugin = require("monaco-editor-webpack-plugin");

function getRouterArgs() {
  const targetOrigin = buildEnv.chromeExtension.routerSWTargetOrigin;
  const relativePath = buildEnv.chromeExtension.routerSWRelativePath;

  console.info(`Chrome Extension :: Router target origin: ${targetOrigin}`);
  console.info(`Chrome Extension :: Router relative path: ${relativePath}`);

  return [targetOrigin, relativePath];
}

function getManifestFile() {
  const manifestFile = buildEnv.chromeExtension.manifestFile;

  console.info(`Chrome Extension :: Manifest file: ${manifestFile}`);

  return manifestFile;
}

module.exports = async (env) => {
  const [router_targetOrigin, router_relativePath] = getRouterArgs(env);
  const manifestFile = getManifestFile(env);

  return merge(common(env), {
    entry: {
      "content_scripts/github": "./src/github-content-script.ts",
      background: "./src/background.ts",
      "serverless-workflow-editor-envelope": "./src/envelope/ServerlessWorkflowEditorEnvelopeApp.ts",
    },
    devServer: {
      static: [{ directory: path.join(__dirname, "./dist") }],
      compress: true,
      https: true,
      port: buildEnv.chromeExtension.dev.port,
    },
    plugins: [
      new EnvironmentPlugin({
        WEBPACK_REPLACE__targetOrigin: router_targetOrigin,
        WEBPACK_REPLACE__relativePath: router_relativePath,
      }),
      new CopyPlugin({
        patterns: [
          { from: "./static", to: "." },
          { from: `./${manifestFile}`, to: "./manifest.json" },
          { from: `./rules.json`, to: "./rules.json" },
        ],
      }),
      new ZipPlugin({
        filename: "chrome_extension_serverless_workflow_editor_" + packageJson.version + ".zip",
        include: ["manifest.json", "background.js", "content_scripts", "resources", "rules.json"],
      }),
      new MonacoWebpackPlugin({
        languages: ["json"],
        customLanguages: [
          {
            label: "yaml",
            entry: ["monaco-yaml", "vs/basic-languages/yaml/yaml.contribution"],
            worker: {
              id: "monaco-yaml/yamlWorker",
              entry: "monaco-yaml/lib/esm/yaml.worker",
            },
          },
        ],
      }),
    ],
    module: {
      rules: [...patternflyBase.webpackModuleRules],
    },
  });
};
