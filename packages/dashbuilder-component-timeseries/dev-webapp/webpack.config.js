/*
 * Copyright 2019 Red Hat, Inc. and/or its affiliates.
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
const patternflyBase = require("@kie-tools-core/patternfly-base");
const { merge } = require("webpack-merge");
const CopyPlugin = require("copy-webpack-plugin");
const common = require("@kie-tools-core/webpack-base/webpack.common.config");
const HtmlWebpackPlugin = require("html-webpack-plugin");

module.exports = async (env) => {
  return merge(common(env), {
    mode: "development",
    entry: {
      index: path.resolve(__dirname, "./index.tsx"),
    },
    output: {
      path: path.resolve("../dist-dev"),
    },
    plugins: [
      new HtmlWebpackPlugin({
        template: "./static/index.html",
        minify: false,
      }),
      new CopyPlugin({
        patterns: [{ from: path.resolve(__dirname, "manifest.dev.json"), to: "." }],
      }),
    ],
    module: {
      rules: [...patternflyBase.webpackModuleRules],
    },
    devServer: {
      historyApiFallback: false,
      static: [{ directory: path.join(__dirname, "./dist") }, { directory: path.join(__dirname, "../static") }],
      compress: false,
      port: 9001,
    },
  });
};
