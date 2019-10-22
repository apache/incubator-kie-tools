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
const CopyPlugin = require("copy-webpack-plugin");
const ZipPlugin = require("zip-webpack-plugin");

module.exports = (env, argv) => {
  const isProd = argv.mode === "production";

  return {
    mode: "development",
    devtool: "inline-source-map",
    entry: {
      contentscript: "./src/contentscript.ts",
      background: "./src/background.ts",
      "envelope/index": "./src/envelope/index.ts"
    },
    output: {
      path: path.resolve(__dirname, "./dist"),
      filename: "[name].js"
    },
    externals: {},
    devServer: {
      contentBase: [path.join(__dirname, "..", "unpacked-gwt-editors")],
      compress: true,
      hot: false,
      liveReload: false,
      watchContentBase: true,
      https: true,
      port: 9000
    },
    plugins: [
      new CopyPlugin([
        { from: "./static/manifest.json" },
        { from: "./static/resources", to: "./resources" },
        { from: "./static/envelope", to: "./envelope" }
      ]),
      new ZipPlugin({
        filename: "kiegroup_kogito_chrome_extension_" + packageJson.version + ".zip",
        pathPrefix: "dist"
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
          test: /ChromeRouter\.ts$/,
          loader: "string-replace-loader",
          options: {
            multiple: [
              {
                search: "$_{WEBPACK_REPLACE__targetOrigin}",
                replace: isProd ? "https://raw.githubusercontent.com" : "https://localhost:9000"
              },
              {
                search: "$_{WEBPACK_REPLACE__relativePath}",
                replace: isProd ? "kiegroup/kogito-online/chrome-extension-resources-0.2.0/" : ""
              }
            ]
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
};
