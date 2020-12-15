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

const CopyWebpackPlugin = require("copy-webpack-plugin");
const pfWebpackOptions = require("@kogito-tooling/patternfly-base/patternflyWebpackOptions");
const { merge } = require("webpack-merge");
const common = require("../../webpack.common.config");
const externalAssets = require("@kogito-tooling/external-assets-base");

module.exports = async (argv, env) => [
  merge(common, {
    output: {
      library: "AppFormer.VsCodePack",
      libraryTarget: "umd",
      umdNamedDefine: true
    },
    externals: {
      vscode: "commonjs vscode"
    },
    target: "node",
    entry: {
      "extension/extension": "./src/extension/extension.ts"
    },
    plugins: []
  }),
  merge(common, {
    output: {
      library: "AppFormer.VsCodePackWebview",
      libraryTarget: "umd",
      umdNamedDefine: true
    },
    externals: {
      vscode: "commonjs vscode"
    },
    target: "web",
    entry: {
      "webview/GwtEditorsEnvelopeApp": "./src/webview/GwtEditorsEnvelopeApp.ts"
    },
    module: {
      rules: [...pfWebpackOptions.patternflyRules]
    },
    plugins: [
      new CopyWebpackPlugin([
        { from: "./static", to: "static" },
        { from: externalAssets.dmnEditorPath(argv), to: "webview/editors/dmn", ignore: ["WEB-INF/**/*"] },
        { from: externalAssets.bpmnEditorPath(argv), to: "webview/editors/bpmn", ignore: ["WEB-INF/**/*"] },
        { from: externalAssets.scesimEditorPath(argv), to: "webview/editors/scesim", ignore: ["WEB-INF/**/*"] }
      ])
    ]
  })
];
