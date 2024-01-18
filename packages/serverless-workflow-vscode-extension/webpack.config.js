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
const swEditor = require("@kie-tools/serverless-workflow-diagram-editor-assets");
const { merge } = require("webpack-merge");
const { ProvidePlugin } = require("webpack");
const common = require("@kie-tools-core/webpack-base/webpack.common.config");

const commonConfig = (env) =>
  merge(common(env), {
    output: {
      library: "ServerlessWorkflowEditor",
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
  }),
  merge(commonConfig(env), {
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
  merge(commonConfig(env), {
    target: "web",
    entry: {
      "webview/editors/serverless-workflow/serverless-workflow-diagram-editor-envelope":
        "./src/webview/ServerlessWorkflowDiagramEditorEnvelopeApp.ts",
    },
    plugins: [
      new CopyWebpackPlugin({
        patterns: [
          {
            from: swEditor.swEditorPath(),
            to: "webview/editors/serverless-workflow/diagram",
            globOptions: { ignore: ["**/WEB-INF/**/*", "**/*.html"] },
          },
        ],
      }),
      new ProvidePlugin({
        process: require.resolve("process/browser.js"),
        Buffer: ["buffer", "Buffer"],
      }),
    ],
    module: {
      rules: [...patternflyBase.webpackModuleRules],
    },
  }),
];
