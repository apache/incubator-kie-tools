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

const patternflyBase = require("@kie-tooling-core/patternfly-base");
const { merge } = require("webpack-merge");

const commonConfig = {
  mode: "development",
  output: {
    path: path.resolve(__dirname, "./dist"),
    filename: "[name].js",
    library: "PmmlEditor",
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
    ],
  },
  resolve: {
    extensions: [".tsx", ".ts", ".js", ".jsx"],
    modules: [path.resolve("../../node_modules"), path.resolve("./node_modules"), path.resolve("./src")],
  },
};

module.exports = async (argv) => [
  merge(commonConfig, {
    target: "node",
    entry: {
      "extension/extension": "./src/extension/extension.ts",
    },
  }),
  merge(commonConfig, {
    target: "web",
    entry: {
      "webview/PmmlEditorEnvelopeApp": "./src/webview/PmmlEditorEnvelopeApp.ts",
    },
    resolve: {
      alias: {
        // `react-monaco-editor` points to the `monaco-editor` package by default, therefore doesn't use our minified
        // version. To solve that, we fool webpack, saying that every import for Monaco directly should actually point to
        // `@kiegroup/monaco-editor`. This way, everything works as expected.
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
