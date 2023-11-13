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

const path = require("path");
const CopyPlugin = require("copy-webpack-plugin");
const { merge } = require("webpack-merge");
const common = require("@kie-tools-core/webpack-base/webpack.common.config");
const patternflyBase = require("@kie-tools-core/patternfly-base");
const { env } = require("../env");
const buildEnv = env;

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
    ],
    module: {
      rules: [
        {
          test: /\.ttf$/,
          use: ["file-loader"],
        },
        ...patternflyBase.webpackModuleRules,
      ],
    },
    resolve: {
      alias: {
        // `react-monaco-editor` points to the `monaco-editor` package by default, therefore doesn't use our minified
        // version. To solve that, we fool webpack, saying that every import for Monaco directly should actually point to
        // `@kie-tools-core/monaco-editor`. This way, everything works as expected.
        "monaco-editor/esm/vs/editor/editor.api": require.resolve("@kie-tools-core/monaco-editor"),
      },
    },
    devServer: {
      client: {
        overlay: true,
      },
      historyApiFallback: true,
      compress: true,
      port: buildEnv.pmmlEditor.dev.port,
      open: false,
      hot: true,
    },
  });
