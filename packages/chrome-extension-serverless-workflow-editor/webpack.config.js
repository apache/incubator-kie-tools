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
const path = require("path");
const MonacoWebpackPlugin = require("monaco-editor-webpack-plugin");
const swEditor = require("@kie-tools/serverless-workflow-diagram-editor-assets");
const { env } = require("./env");
const buildEnv = env;

function getRouterArgs() {
  const targetOrigin = buildEnv.swfChromeExtension.routerTargetOrigin;
  const relativePath = buildEnv.swfChromeExtension.routerRelativePath;

  console.info(`SWF Chrome Extension :: Router target origin: ${targetOrigin}`);
  console.info(`SWF Chrome Extension :: Router relative path: ${relativePath}`);

  return [targetOrigin, relativePath];
}

function getManifestFile() {
  const manifestFile = buildEnv.swfChromeExtension.manifestFile;

  console.info(`SWF Chrome Extension :: Manifest file: ${manifestFile}`);

  return manifestFile;
}

module.exports = async (env) => {
  const [router_targetOrigin, router_relativePath] = getRouterArgs(env);
  const manifestFile = getManifestFile(env);

  return merge(common(env), {
    entry: {
      "content_scripts/github": "./src/github-content-script.ts",
      background: "./src/background.ts",
      "serverless-workflow-combined-editor-envelope": "./src/envelope/ServerlessWorkflowCombinedEditorEnvelopeApp.ts",
      "serverless-workflow-diagram-editor-envelope": "./src/envelope/ServerlessWorkflowDiagramEditorEnvelopeApp.ts",
      "serverless-workflow-text-editor-envelope": "./src/envelope/ServerlessWorkflowTextEditorEnvelopeApp.ts",
      "serverless-workflow-mermaid-viewer-envelope": "./src/envelope/ServerlessWorkflowMermaidViewerEnvelopeApp.ts",
    },
    devServer: {
      static: [{ directory: path.join(__dirname, "./dist") }],
      compress: true,
      https: true,
      port: buildEnv.swfChromeExtension.dev.port,
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
          // This is used for development only.
          {
            from: swEditor.swEditorPath(),
            to: "./diagram",
            globOptions: { ignore: ["**/WEB-INF/**/*"] },
          },
        ],
      }),
      new ZipPlugin({
        filename: "chrome_extension_serverless_workflow_editor_" + packageJson.version + ".zip",
        include: ["manifest.json", "background.js", "content_scripts", "resources", "scripts", "rules.json"],
      }),
      new MonacoWebpackPlugin({
        languages: ["json"],
        customLanguages: [
          {
            label: "yaml",
            entry: ["monaco-yaml", "vs/basic-languages/yaml/yaml.contribution"],
            worker: {
              id: "monaco-yaml/yamlWorker",
              entry: "monaco-yaml/yaml.worker.js",
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
