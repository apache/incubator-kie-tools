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

const CopyWebpackPlugin = require("copy-webpack-plugin");
const { ProvidePlugin } = require("webpack");
const patternflyBase = require("@kie-tools-core/patternfly-base");
const dashbuilderClient = require("@kie-tools/dashbuilder-client");
const { merge } = require("webpack-merge");
const common = require("@kie-tools-core/webpack-base/webpack.common.config");

const commonConfig = (env) =>
  merge(common(env), {
    output: {
      library: "DashbuilderEditor",
      libraryTarget: "umd",
      umdNamedDefine: true,
      globalObject: "this",
    },
    externals: {
      vscode: "commonjs vscode",
    },
  });

module.exports = async (env) => [
  merge(commonConfig(env), {
    target: "node",
    entry: {
      "extension/extension": "./src/extension/extension.ts",
    },
    plugins: [],
  }),
  merge(commonConfig(env), {
    target: "webworker",
    entry: {
      "browser/extension": "./src/browser/extension.ts",
    },
    plugins: [
      new ProvidePlugin({
        process: require.resolve("process/browser.js"),
        Buffer: ["buffer", "Buffer"],
      }),
    ],
  }),
  merge(commonConfig(env), {
    target: "web",
    entry: {
      "webview/DashbuilderEditorEnvelopeApp": "./src/webview/DashbuilderEditorEnvelopeApp.ts",
    },
    module: {
      rules: [...patternflyBase.webpackModuleRules],
    },
    plugins: [
      new CopyWebpackPlugin({
        patterns: [
          {
            from: "./src/setup.js",
            to: "webview/",
            globOptions: { ignore: ["**/WEB-INF/**/*"] },
          },
          {
            from: dashbuilderClient.dashbuilderPath(),
            to: "webview/",
            globOptions: { ignore: ["**/WEB-INF/**/*"] },
          },
        ],
      }),
    ],
  }),
];
