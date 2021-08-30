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

const path = require("path");
const CopyWebpackPlugin = require("copy-webpack-plugin");
const patternflyBase = require("@kie-tooling-core/patternfly-base");
const externalAssets = require("@kogito-tooling/external-assets-base");

const commonConfig = {
  mode: "development",
  output: {
    path: path.resolve(__dirname, "./dist"),
    filename: "[name].js",
    library: "DmnEditor",
    libraryTarget: "umd",
    umdNamedDefine: true,
  },
  externals: {
    vscode: "commonjs vscode",
  },
  plugins: [],
  module: {
    rules: [
      {
        test: /\.tsx?$/,
        loader: "ts-loader",
      },
      ...patternflyBase.webpackModuleRules,
    ],
  },
  resolve: {
    extensions: [".tsx", ".ts", ".js", ".jsx"],
    modules: [path.resolve("../../node_modules"), path.resolve("./node_modules"), path.resolve("./src")],
  },
};

module.exports = async (argv) => [
  {
    ...commonConfig,
    target: "node",
    entry: {
      "extension/extension": "./src/extension/extension.ts",
    },
    plugins: [],
  },
  {
    ...commonConfig,
    target: "web",
    entry: {
      "webview/DmnEditorEnvelopeApp": "./src/webview/DmnEditorEnvelopeApp.ts",
      "webview/SceSimEditorEnvelopeApp": "./src/webview/SceSimEditorEnvelopeApp.ts",
    },
    plugins: [
      new CopyWebpackPlugin({
        patterns: [
          {
            from: externalAssets.dmnEditorPath(argv),
            to: "webview/editors/dmn",
            globOptions: { ignore: ["WEB-INF/**/*"] },
          },
          {
            from: externalAssets.scesimEditorPath(argv),
            to: "webview/editors/scesim",
            globOptions: { ignore: ["WEB-INF/**/*"] },
          },
        ],
      }),
    ],
  },
];
