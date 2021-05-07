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
const HtmlWebPackPlugin = require("html-webpack-plugin");

module.exports = {
  entry: {
    app: path.resolve(__dirname, "src", "index.tsx"),
  },
  output: {
    path: path.resolve(__dirname, "dist"),
    filename: "[name].js",
    libraryTarget: "umd",
  },
  plugins: [
    new HtmlWebPackPlugin({
      template: path.resolve(__dirname, "public/index.html"),
      filename: "index.html",
    }),
  ],
  resolve: {
    extensions: [".tsx", ".ts", ".js", ".jsx"],
    modules: [path.resolve("../../node_modules"), path.resolve("./node_modules"), path.resolve("./src")],
  },
  module: {
    rules: [
      {
        test: /\.tsx?$/,
        loader: "ts-loader",
      },
    ],
  },
  externals: {
    "@kogito-tooling/kie-editors-standalone/dist/dmn": {
      commonjs: "@kogito-tooling/kie-editors-standalone/dist/dmn",
      commonjs2: "@kogito-tooling/kie-editors-standalone/dist/dmn",
      amd: "@kogito-tooling/kie-editors-standalone/dist/dmn",
      root: "DmnEditor",
    },
    "@kogito-tooling/kie-editors-standalone/dist/bpmn": {
      commonjs: "@kogito-tooling/kie-editors-standalone/dist/bpmn",
      commonjs2: "@kogito-tooling/kie-editors-standalone/dist/bpmn",
      amd: "@kogito-tooling/kie-editors-standalone/dist/bpmn",
      root: "BpmnEditor",
    },
  },
  devServer: {
    contentBase: [path.join(__dirname, "dist"), path.join(__dirname, "../dist/")],
    compress: true,
    inline: true,
    historyApiFallback: true,
    overlay: true,
    open: false,
    port: 9001,
  },
};
