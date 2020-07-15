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
const nodeExternals = require("webpack-node-externals");
const pfWebpackOptions = require("@kogito-tooling/patternfly-base/patternflyWebpackOptions");

module.exports = async (env, argv) => {
  return {
    mode: "development",
    devtool: "inline-source-map",
    entry: {
      index: "./src/index.tsx"
    },
    output: {
      path: path.resolve(__dirname, "./dist"),
      filename: "[name].js"
    },
    externals: [nodeExternals({ modulesDir: "../../node_modules" })],
    plugins: [
      new CopyPlugin([
        { from: "./static/resources", to: "./resources" },
        { from: "./static/images", to: "./images" },
        { from: "./static/index.html", to: "./index.html" },
        { from: "./static/favicon.ico", to: "./favicon.ico" }
      ])
    ],
    module: {
      rules: [
        {
          test: /\.tsx?$/,
          include: path.resolve(__dirname, "src"),
          use: [
            {
              loader: "ts-loader",
              options: {
                configFile: path.resolve("./tsconfig.json")
              }
            }
          ]
        },
        {
          test: /\.jsx?$/,
          exclude: /node_modules/,
          use: ["babel-loader"]
        },
        ...pfWebpackOptions.patternflyRules
      ]
    },
    devServer: {
      historyApiFallback: {
        disableDotRule: true
      },
      disableHostCheck: true,
      watchContentBase: true,
      contentBase: [path.join(__dirname, "./dist"), path.join(__dirname, "./static")],
      port: 9001
    },
    resolve: {
      extensions: [".tsx", ".ts", ".js", ".jsx"],
      modules: [path.resolve("../../node_modules"), path.resolve("./node_modules"), path.resolve("./src")]
    }
  };
};
