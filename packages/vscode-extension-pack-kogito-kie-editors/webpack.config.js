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
const { merge } = require("webpack-merge");
const common = require("../../config/webpack.common.config");
const externalAssets = require("@kogito-tooling/external-assets-base");

module.exports = async (env) => [
  merge(common(env), {
    output: {
      library: "AppFormer.VsCodePack",
      libraryTarget: "umd",
      umdNamedDefine: true,
    },
    externals: {
      vscode: "commonjs vscode",
    },
    target: "node",
    entry: {
      "extension/extension": "./src/extension/extension.ts",
    },
    plugins: [],
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
      new CopyWebpackPlugin({
        patterns: [
          { from: "./static", to: "static" },
          {
            from: externalAssets.dmnEditorPath(),
            to: "webview/editors/dmn",
            globOptions: { ignore: ["WEB-INF/**/*"] },
          },
          {
            from: externalAssets.bpmnEditorPath(),
            to: "webview/editors/bpmn",
            globOptions: { ignore: ["WEB-INF/**/*"] },
          },
          {
            from: externalAssets.scesimEditorPath(),
            to: "webview/editors/scesim",
            globOptions: { ignore: ["WEB-INF/**/*"] },
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
        // `@kie-tooling-core/monaco-editor`. This way, everything works as expected.
        "monaco-editor/esm/vs/editor/editor.api": require.resolve("@kie-tooling-core/monaco-editor"),
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
  }),
];
