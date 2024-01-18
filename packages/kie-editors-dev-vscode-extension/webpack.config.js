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
const { merge } = require("webpack-merge");
const { ProvidePlugin } = require("webpack");
const common = require("@kie-tools-core/webpack-base/webpack.common.config");
const stunnerEditors = require("@kie-tools/stunner-editors");
const vscodeJavaCodeCompletionExtensionPlugin = require("@kie-tools/vscode-java-code-completion-extension-plugin");

module.exports = async (env) => [
  merge(common(env), {
    output: {
      library: "AppFormer.VsCodePack",
      libraryTarget: "umd",
      umdNamedDefine: true,
      globalObject: "this",
    },
    externals: {
      vscode: "commonjs vscode",
    },
    target: "node",
    entry: {
      "extension/extension": "./src/extension/extension.ts",
    },
  }),
  merge(common(env), {
    output: {
      library: "AppFormer.VsCodePack",
      libraryTarget: "umd",
      umdNamedDefine: true,
      globalObject: "this",
    },
    externals: {
      vscode: "commonjs vscode",
    },
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
  merge(common(env), {
    output: {
      library: "AppFormer.VsCodePackWebview",
      libraryTarget: "umd",
      umdNamedDefine: true,
    },
    externals: {
      vscode: "commonjs vscode",
    },
    target: "web",
    entry: {
      "webview/BpmnEditorEnvelopeApp": "./src/webview/BpmnEditorEnvelopeApp.ts",
      "webview/DmnEditorEnvelopeApp": "./src/webview/DmnEditorEnvelopeApp.ts",
      "webview/SceSimEditorEnvelopeApp": "./src/webview/SceSimEditorEnvelopeApp.ts",
    },
    module: {
      rules: [...patternflyBase.webpackModuleRules],
    },
    plugins: [
      new ProvidePlugin({
        process: require.resolve("process/browser.js"),
        Buffer: ["buffer", "Buffer"],
      }),
      new CopyWebpackPlugin({
        patterns: [
          { from: "./static", to: "static" },
          {
            from: stunnerEditors.dmnEditorPath(),
            to: "webview/editors/dmn",
            globOptions: { ignore: ["**/WEB-INF/**/*", "**/*.html"] },
          },
          {
            from: stunnerEditors.bpmnEditorPath(),
            to: "webview/editors/bpmn",
            globOptions: { ignore: ["**/WEB-INF/**/*", "**/*.html"] },
          },
          {
            from: stunnerEditors.scesimEditorPath(),
            to: "webview/editors/scesim",
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
  merge(common(env), {
    output: {
      library: "AppFormer.VsCodePackWebview",
      libraryTarget: "umd",
      umdNamedDefine: true,
    },
    externals: {
      vscode: "commonjs vscode",
    },
    target: "web",
    entry: {
      "webview/PMMLEditorEnvelopeApp": "./src/webview/PMMLEditorEnvelopeApp.ts",
    },
    resolve: {
      alias: {
        // `react-monaco-editor` points to the `monaco-editor` package by default, therefore doesn't use our minified
        // version. To solve that, we fool webpack, saying that every import for Monaco directly should actually point to
        // `@kie-tools-core/monaco-editor`. This way, everything works as expected.
        "monaco-editor/esm/vs/editor/editor.api": require.resolve("@kie-tools-core/monaco-editor"),
      },
    },
    module: {
      rules: [
        {
          test: /\.ttf$/,
          use: ["file-loader"],
        },
        ...patternflyBase.webpackModuleRules,
      ],
    },
    plugins: [
      new ProvidePlugin({
        process: require.resolve("process/browser.js"),
        Buffer: ["buffer", "Buffer"],
      }),
    ],
  }),
];
