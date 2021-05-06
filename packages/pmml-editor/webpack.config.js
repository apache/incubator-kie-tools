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

const path = require("path");
const CopyPlugin = require("copy-webpack-plugin");
const { merge } = require("webpack-merge");
const common = require("../../webpack.common.config");
const pfWebpackOptions = require("@kogito-tooling/patternfly-base/patternflyWebpackOptions");
const nodeExternals = require("webpack-node-externals");

module.exports = [
  merge(common, {
    entry: {
      "editor/index": "./src/editor/index.ts",
    },
    output: {
      libraryTarget: "commonjs2",
    },
    externals: [nodeExternals({ modulesDir: "../../node_modules" })],
    module: {
      rules: [...pfWebpackOptions.patternflyRules],
    },
  }),
  merge(common, {
    entry: {
      index: "./src/showcase/index.tsx",
    },
    plugins: [
      new CopyPlugin({patterns: [
        { from: "./src/showcase/static/resources", to: "./resources" },
        { from: "./src/showcase/static/index.html", to: "./index.html" },
        { from: "./src/showcase/static/favicon.ico", to: "./favicon.ico" },
        { from: "./static/images", to: "./images" },
      ]}),
    ],
    module: {
      rules: [
        {
          test: /\.ttf$/,
          use: ["file-loader"],
        },
        ...pfWebpackOptions.patternflyRules,
      ],
    },
    resolve: {
      alias: {
        // `react-monaco-editor` points to the `monaco-editor` package by default, therefore doesn't use our minified
        // version. To solve that, we fool webpack, saying that every import for Monaco directly should actually point to
        // `@kiegroup/monaco-editor`. This way, everything works as expected.
        "monaco-editor/esm/vs/editor/editor.api": path.resolve(__dirname, "../../node_modules/@kiegroup/monaco-editor"),
      },
    },
    devServer: {
      historyApiFallback: true,
      disableHostCheck: true,
      watchContentBase: true,
      contentBase: path.join(__dirname),
      compress: true,
      port: 9001,
      open: true,
      inline: true,
      hot: true,
      overlay: true,
    },
  }),
];
