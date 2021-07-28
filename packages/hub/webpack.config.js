/*
 * Copyright 2020 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

const CopyPlugin = require("copy-webpack-plugin");
const patternflyBase = require("@kie-tooling-core/patternfly-base");
const os = require("os");
const { merge } = require("webpack-merge");
const common = require("../../config/webpack.common.config");

module.exports = (env) => [
  merge(common(env), {
    externals: {
      electron: "commonjs electron",
    },
    target: "electron-main",
    entry: {
      index: "./src/electron/index.ts",
    },
    node: {
      __dirname: false,
      __filename: false,
    },
    plugins: [
      new CopyPlugin({
        patterns: [
          { from: "./static/resources", to: "./resources" },
          { from: "./static/images", to: "./images" },
          { from: "./static/index.html", to: "./index.html" },
          {
            from: "../desktop/out/Business Modeler Preview-" + os.platform() + "-x64",
            to: "./lib/Business Modeler Preview-" + os.platform() + "-x64",
            noErrorOnMissing: true,
          },
          { from: "./build", to: "./build" },
        ],
      }),
    ],
  }),
  merge(common(env), {
    externals: {
      electron: "commonjs electron",
    },
    target: "web",
    entry: {
      "webview/index": "./src/webview/index.tsx",
    },
    module: {
      rules: [...patternflyBase.webpackModuleRules],
    },
    plugins: [new CopyPlugin({ patterns: [{ from: "static/index.html" }] })],
  }),
];
