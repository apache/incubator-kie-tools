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

const CopyPlugin = require("copy-webpack-plugin");
const ZipPlugin = require("zip-webpack-plugin");
const packageJson = require("./package.json");
const patternflyBase = require("@kie-tools-core/patternfly-base");
const { merge } = require("webpack-merge");
const common = require("@kie-tools-core/webpack-base/webpack.common.config");
const { EnvironmentPlugin, ProvidePlugin } = require("webpack");
const path = require("path");
const { env } = require("./env");

function getRouterArgs() {
  const targetOrigin = env.chromeExtension.routerTargetOrigin;
  const relativePath = env.chromeExtension.routerRelativePath;

  console.info(`Chrome Extension :: Router target origin: ${targetOrigin}`);
  console.info(`Chrome Extension :: Router relative path: ${relativePath}`);

  return [targetOrigin, relativePath];
}

function getOnlineEditorArgs() {
  const onlineEditorUrl = env.chromeExtension.onlineEditorUrl;
  const manifestFile = env.chromeExtension.manifestFile;

  console.info(`Chrome Extension :: Online Editor URL: ${onlineEditorUrl}`);
  console.info(`Chrome Extension :: Manifest file: ${manifestFile}`);

  return [onlineEditorUrl, manifestFile];
}

module.exports = async (webpackEnv) => {
  const [router_targetOrigin, router_relativePath] = getRouterArgs(webpackEnv);
  const [onlineEditor_url, manifestFile] = getOnlineEditorArgs(webpackEnv);

  return merge(common(webpackEnv), {
    entry: {
      "content_scripts/github": "./src/github-content-script.ts",
      background: "./src/background.ts",
      "bpmn-envelope": "./src/envelope/BpmnEditorEnvelopeApp.ts",
      "dmn-envelope": "./src/envelope/DmnEditorEnvelopeApp.ts",
      "scesim-envelope": "./src/envelope/SceSimEditorEnvelopeApp.ts",
    },
    devServer: {
      static: [{ directory: path.join(__dirname, "./dist") }],
      compress: true,
      https: true,
      port: env.chromeExtension.dev.port,
    },
    plugins: [
      new ProvidePlugin({
        process: require.resolve("process/browser.js"),
        Buffer: ["buffer", "Buffer"],
      }),
      new EnvironmentPlugin({
        WEBPACK_REPLACE__targetOrigin: router_targetOrigin,
        WEBPACK_REPLACE__relativePath: router_relativePath,
        WEBPACK_REPLACE__onlineEditor_url: onlineEditor_url,
      }),
      new CopyPlugin({
        patterns: [
          { from: "./static", to: "." },
          { from: `./${manifestFile}`, to: "./manifest.json" },
          { from: `./rules.json`, to: "./rules.json" },
        ],
      }),
      new ZipPlugin({
        filename: "chrome_extension_kogito_kie_editors_" + packageJson.version + ".zip",
        include: ["manifest.json", "background.js", "content_scripts", "resources", "scripts", "rules.json"],
      }),
    ],
    module: {
      rules: [...patternflyBase.webpackModuleRules],
    },
  });
};
