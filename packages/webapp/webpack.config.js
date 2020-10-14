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
const CopyPlugin = require("copy-webpack-plugin");
const pfWebpackOptions = require("@kogito-tooling/patternfly-base/patternflyWebpackOptions");

module.exports = {
  mode: "development",
  devtool: "inline-source-map",
  entry: {
    index: "./src/index.tsx",
    "envelope/base64-editor": "./src/envelope/base64-editor.ts",
    "envelope/gwt-editors": "./src/envelope/gwt-editors.ts",
    "envelope/ping-pong-view-react-impl": "./src/envelope/ping-pong-view-react-impl.ts",
    "envelope/todo-list-view": "./src/envelope/todo-list-view.ts",
  },
  output: {
    path: path.resolve("./dist"),
    filename: "[name].js",
    publicPath: "/",
  },
  stats: {
    excludeAssets: [(name) => !name.endsWith(".js"), /gwt-editors\/.*/, /editors\/.*/],
    excludeModules: true,
  },
  performance: {
    maxAssetSize: 30000000,
    maxEntrypointSize: 30000000,
  },
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
      ...pfWebpackOptions.patternflyRules,
    ],
  },
  plugins: [
    new CopyPlugin({
      patterns: [
        { from: "./envelope", to: "./envelope" },
        { from: "./static", to: "." },
        { from: "../../node_modules/@kogito-tooling/kie-bc-editors-unpacked/dmn", to: "./gwt-editors/dmn" },
      ],
    }),
  ],
  devServer: {
    historyApiFallback: false,
    disableHostCheck: true,
    watchContentBase: true,
    contentBase: [path.join(__dirname, "./dist"), path.join(__dirname, "./static")],
    compress: true,
    port: 9001,
  },
};
