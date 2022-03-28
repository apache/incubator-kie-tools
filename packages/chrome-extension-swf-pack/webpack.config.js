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

const path = require("path");
const CopyPlugin = require("copy-webpack-plugin");
const ZipPlugin = require("zip-webpack-plugin");
const packageJson = require("./package.json");
const patternflyBase = require("@kie-tools-core/patternfly-base");
const { merge } = require("webpack-merge");
const common = require("@kie-tools-core/webpack-base/webpack.common.config");
const { EnvironmentPlugin } = require("webpack");
const buildEnv = require("@kie-tools/build-env");

function getRouterArgs() {
  const targetOrigin = buildEnv.chromeExtensionServerlessWorkflow.routerTargetOrigin;
  const relativePath = buildEnv.chromeExtensionServerlessWorkflow.routerRelativePath;

  console.info(`Chrome Extension :: Router target origin: ${targetOrigin}`);
  console.info(`Chrome Extension :: Router relative path: ${relativePath}`);

  return [targetOrigin, relativePath];
}

module.exports = async (env) => {
  const [router_targetOrigin, router_relativePath] = getRouterArgs();
  const manifestFile = buildEnv.chromeExtensionServerlessWorkflow.manifestFile;

  return merge(common(env), {
    entry: {
      contentscript: "./src/contentscript.ts",
      "envelope/index": "./src/envelope/index.ts",
    },
    devServer: {
      compress: true,
      https: true,
      port: buildEnv.chromeExtensionServerlessWorkflow.dev.port,
    },
    plugins: [
      new EnvironmentPlugin({
        WEBPACK_REPLACE__targetOrigin: router_targetOrigin,
        WEBPACK_REPLACE__relativePath: router_relativePath,
      }),
      new CopyPlugin({
        patterns: [
          { from: "./static/resources", to: "./resources" },
          { from: "./static/images", to: "./images" },
          { from: "./static/envelope", to: "./envelope" },
          { from: `./${manifestFile}`, to: "./manifest.json" },
        ],
      }),
      new ZipPlugin({
        filename: "chrome_extension_swf_pack_" + packageJson.version + ".zip",
        pathPrefix: "chrome_extension_swf_pack_" + packageJson.version,
        include: ["contentscript.js", "manifest.json", /resources\/.*/],
      }),
    ],
    module: {
      rules: [...patternflyBase.webpackModuleRules],
    },
    ignoreWarnings: [/Failed to parse source map/],
  });
};
