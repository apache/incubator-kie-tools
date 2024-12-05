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
const patternflyBase = require("@kie-tools-core/patternfly-base");
const stunnerEditors = require("@kie-tools/stunner-editors");
const vscodeJavaCodeCompletionExtensionPlugin = require("@kie-tools/vscode-java-code-completion-extension-plugin");
const { merge } = require("webpack-merge");
const common = require("@kie-tools-core/webpack-base/webpack.common.config");
const { ProvidePlugin } = require("webpack");

const commonConfig = (webpackEnv) =>
  merge(common(webpackEnv), {
    output: {
      library: "DmnEditor",
      libraryTarget: "umd",
      umdNamedDefine: true,
      globalObject: "this",
    },
    plugins: [
      new ProvidePlugin({
        process: require.resolve("process/browser.js"),
        Buffer: ["buffer", "Buffer"],
      }),
    ],
    externals: {
      vscode: "commonjs vscode",
    },
  });

module.exports = async (webpackEnv) => [
  merge(commonConfig(webpackEnv), {
    target: "node",
    entry: {
      "extension/extension": "./src/extension/extension.ts",
    },
  }),
  merge(commonConfig(webpackEnv), {
    target: "webworker",
    entry: {
      "extension/extensionWeb": "./src/extension/extension.ts",
    },
    plugins: [
      new ProvidePlugin({
        process: require.resolve("process/browser.js"),
        Buffer: ["buffer", "Buffer"],
      }),
    ],
  }),
  merge(commonConfig(webpackEnv), {
    target: "web",
    entry: {
      "webview/DmnEditorEnvelopeApp": "./src/webview/DmnEditorEnvelopeApp.ts",
      "webview/SceSimEditorEnvelopeApp": "./src/webview/SceSimEditorEnvelopeApp.ts",
      "webview/NewDmnEditorEnvelopeApp": "./src/webview/NewDmnEditorEnvelopeApp.ts",
      "webview/NewTestScenarioEditorEnvelopeApp": "./src/webview/NewTestScenarioEditorEnvelopeApp.ts",
    },
    module: {
      rules: [...patternflyBase.webpackModuleRules],
    },
    plugins: [
      new CopyWebpackPlugin({
        patterns: [
          {
            from: stunnerEditors.dmnEditorPath(),
            to: "webview/editors/dmn",
            globOptions: { ignore: ["**/WEB-INF/**/*", "**/*.html"] },
          },
          {
            from: stunnerEditors.scesimEditorPath(),
            to: "webview/editors/scesim",
            globOptions: { ignore: ["**/WEB-INF/**/*", "**/*.html"] },
          },
          {
            from: stunnerEditors.dmnEditorPath(),
            to: "target/dmn",
            globOptions: { ignore: ["**/WEB-INF/**/*", "**/*.html"] },
          },
          {
            from: vscodeJavaCodeCompletionExtensionPlugin.path(),
            to: "server/",
            globOptions: { ignore: ["**/WEB-INF/**/*", "**/*.html"] },
          },
        ],
      }),
    ],
  }),
];
