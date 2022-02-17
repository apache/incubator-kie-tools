/*
 * Copyright 2021 Red Hat, Inc. and/or its affiliates.
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

const path = require("path");
const CopyPlugin = require("copy-webpack-plugin");
const { merge } = require("webpack-merge");
const common = require("@kie-tools-core/webpack-base/webpack.common.config");
const patternflyBase = require("@kie-tools-core/patternfly-base");
const buildEnv = require("@kie-tools/build-env");
const MonacoWebpackPlugin = require("monaco-editor-webpack-plugin");
const webpack = require("webpack");

module.exports = (env) =>
  merge(common(env), {
    mode: "development",
    entry: {
      index: path.resolve(__dirname, "./index.tsx"),
    },
    output: {
      path: path.resolve("../dist-dev"),
    },
    plugins: [
      new CopyPlugin({
        patterns: [
          { from: path.resolve(__dirname, "./static/resources"), to: "./resources" },
          { from: path.resolve(__dirname, "./static/index.html"), to: "./index.html" },
          { from: path.resolve(__dirname, "./static/favicon.ico"), to: "./favicon.ico" },
          { from: path.resolve(__dirname, "../static/images"), to: "./images" },
        ],
      }),
      new MonacoWebpackPlugin({
        languages: ["json"],
        customLanguages: [
          {
            label: "yaml",
            entry: ["monaco-yaml", "vs/basic-languages/yaml/yaml.contribution"],
            worker: {
              id: "monaco-yaml/yamlWorker",
              entry: "monaco-yaml/lib/esm/yaml.worker",
            },
          },
        ],
      }),
      new webpack.ProvidePlugin({
        mermaid: "mermaid",
      }),
    ],
    module: {
      rules: [
        {
          test: /\.js$/,
          enforce: "pre",
          use: ["source-map-loader"],
        },
        {
          test: /\.ttf$/,
          use: ["file-loader"],
        },
        /*{
          test: /node_modules[\\|/]@severlessworkflow[\\|/]sdk-typescript[\\|/]umd[\\|/]index\.umd\.js$/,
          use: ["umd-compat-loader"]
        },*/
        ...patternflyBase.webpackModuleRules,
      ],
    },
    ignoreWarnings: [/Failed to parse source map/],
    resolve: {
      alias: {
        // `react-monaco-editor` points to the `monaco-editor` package by default, therefore doesn't use our minified
        // version. To solve that, we fool webpack, saying that every import for Monaco directly should actually point to
        // `@kie-tools-core/monaco-editor`. This way, everything works as expected.
        //"monaco-editor/esm/vs/editor/editor.api": require.resolve("@kie-tools-core/monaco-editor"),
        //"@severlessworkflow/sdk-typescript/lib/definitions/workflow": require.resolve("@severlessworkflow/sdk-typescript")
      },
    },
    devServer: {
      historyApiFallback: true,
      static: [{ directory: path.join(__dirname) }],
      compress: true,
      port: buildEnv.serverlessWorkflowEditor.dev.port,
    },
  });
