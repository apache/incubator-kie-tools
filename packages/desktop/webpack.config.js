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
const pfWebpackOptions = require("@kogito-tooling/patternfly-base/patternflyWebpackOptions");
const { merge } = require("webpack-merge");
const common = require("../../webpack.common.config");
const externalAssets = require("@kogito-tooling/external-assets-base");

module.exports = async (env, argv) => [
  merge(common(env, argv), {
    target: "electron-main",
    entry: {
      index: "./src/backend/index.ts",
    },
    externals: {
      electron: "commonjs electron",
    },
    plugins: [new CopyPlugin({ patterns: [{ from: "./build", to: "./build" }] })],
    node: {
      __dirname: false,
      __filename: false,
    },
  }),
  merge(common(env, argv), {
    target: "web",
    entry: {
      "envelope/bpmn-envelope": "./src/envelope/BpmnEditorEnvelopeApp.ts",
      "envelope/dmn-envelope": "./src/envelope/DmnEditorEnvelopeApp.ts",
    },
    module: { rules: [...pfWebpackOptions.patternflyRules] },
    plugins: [
      new CopyPlugin({
        patterns: [
          {
            from: externalAssets.dmnEditorPath(argv),
            to: "./gwt-editors/dmn",
            globOptions: { ignore: ["WEB-INF/**/*"] },
          },
          {
            from: externalAssets.bpmnEditorPath(argv),
            to: "./gwt-editors/bpmn",
            globOptions: { ignore: ["WEB-INF/**/*"] },
          },
        ],
      }),
    ],
  }),
  merge(common(env, argv), {
    target: "electron-renderer",
    entry: {
      "webview/index": "./src/webview/index.tsx",
    },
    externals: {
      electron: "commonjs electron",
    },
    module: { rules: [...pfWebpackOptions.patternflyRules] },
    plugins: [
      new CopyPlugin({
        patterns: [
          { from: "./static/samples", to: "./samples" },
          { from: "./static/resources", to: "./resources" },
          { from: "./static/images", to: "./images" },
          { from: "./static/envelope", to: "./envelope" },
          { from: "./static/index.html", to: "./index.html" },
        ],
      }),
    ],
  }),
];
