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

const { merge } = require("webpack-merge");
const common = require("../../config/webpack.common.config");
const nodeExternals = require("webpack-node-externals");
const CopyPlugin = require("copy-webpack-plugin");
const path = require("path");

module.exports = (env, args) => [
  merge(common(env, args), {
    entry: {
      index: "./src/index.ts",
    },
    plugins: [new CopyPlugin({ patterns: [{ from: "./src/resources", to: "./resources" }] })],
    module: {
      rules: [
        {
          test: /\.template$/,
          include: [path.resolve(__dirname, "src")],
          use: ["raw-loader"],
        },
      ],
    },
    output: {
      libraryTarget: "commonjs2",
    },
    externals: [nodeExternals({ modulesDir: "../../node_modules" })],
  }),
];
