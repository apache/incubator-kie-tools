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
const nodeExternals = require("webpack-node-externals");
const CircularDependencyPlugin = require("circular-dependency-plugin");

module.exports = {
  mode: "development",
  devtool: "inline-source-map",
  target: "node",
  entry: {
    index: "./src/index.ts"
  },
  output: {
    path: path.resolve(__dirname, "./dist"),
    filename: "[name].js",
    libraryTarget: "commonjs2"
  },
  externals: [nodeExternals({ modulesDir: "../../node_modules" })],
  plugins: [
    new CircularDependencyPlugin({
      exclude: /node_modules/, // exclude detection of files based on a RegExp
      failOnError: false, // add errors to webpack instead of warnings
      cwd: process.cwd() // set the current working directory for displaying module paths
    })
  ],
  module: {
    rules: [
      {
        test: /\.tsx?$/,
        loader: "ts-loader",
        options: {
          configFile: path.resolve("./tsconfig.json")
        }
      },
      {
        test: /\.jsx?$/,
        exclude: /node_modules/,
        use: ["babel-loader"]
      }
    ]
  },
  resolve: {
    extensions: [".tsx", ".ts", ".js", ".jsx"],
    modules: [path.resolve("../../node_modules"), path.resolve("./node_modules"), path.resolve("./src")]
  }
};
