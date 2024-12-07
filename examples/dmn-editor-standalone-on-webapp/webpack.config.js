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

const { merge } = require("webpack-merge");
const CopyWebpackPlugin = require("copy-webpack-plugin");
const common = require("@kie-tools-core/webpack-base/webpack.common.config");
const path = require("path");
const HtmlWebpackPlugin = require("html-webpack-plugin");
const { env } = require("./env");

module.exports = (webpackEnv) =>
  merge(common(webpackEnv), {
    output: {
      path: path.join(__dirname, "dist"),
      filename: "[name].js",
    },
    entry: {
      state_control: "./src/stateControl/index.ts",
      read_only: "./src/readOnly/index.ts",
      with_included_models: "./src/withIncludedModels/index.ts",
    },
    plugins: [
      new HtmlWebpackPlugin({
        filename: "state_control.html",
        template: "./src/stateControl/index.html",
        chunks: ["state_control"],
        minify: false,
      }),
      new HtmlWebpackPlugin({
        filename: "read_only.html",
        template: "./src/readOnly/index.html",
        chunks: ["read_only"],
        minify: false,
      }),
      new HtmlWebpackPlugin({
        filename: "with_included_models.html",
        template: "./src/withIncludedModels/index.html",
        chunks: ["with_included_models"],
        minify: false,
      }),
      new CopyWebpackPlugin({
        patterns: [{ from: "./static", to: "static" }],
      }),
    ],
    devServer: {
      static: [{ directory: path.join(__dirname, "./dist") }],
      compress: true,
      https: false,
      port: env.dmnEditorStandaloneOnWebappExample.port,
      client: {
        overlay: false,
      },
    },
  });
