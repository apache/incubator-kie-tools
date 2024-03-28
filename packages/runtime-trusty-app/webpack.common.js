/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
const path = require("path");
const TsconfigPathsPlugin = require("tsconfig-paths-webpack-plugin");
const webpack = require("webpack");
const BG_IMAGES_DIRNAME = "bgimages";

module.exports = {
  entry: {
    app: path.resolve(__dirname, "src", "index.ts"),
  },
  plugins: [
    new webpack.EnvironmentPlugin({
      KOGITO_APP_VERSION: "DEV",
      KOGITO_APP_NAME: "Trusty",
      KOGITO_TRUSTY_API_HTTP_URL: "http://localhost:1336",
    }),
  ],
  module: {
    rules: [
      {
        test: /\.(tsx|ts)?$/,
        include: [path.resolve(__dirname, "src")],
        use: [
          {
            loader: "ts-loader",
            options: {
              configFile: path.resolve("./tsconfig.json"),
              allowTsInNodeModules: true,
              onlyCompileBundledFiles: true,
            },
          },
        ],
      },
      {
        test: /\.(svg|ttf|eot|woff|woff2)$/,
        include: [/fonts|pficon/],
        use: {
          loader: "file-loader",
          options: {
            // Limit at 50k. larger files emitted into separate files
            limit: 5000,
            outputPath: "fonts",
            name: "[name].[ext]",
          },
        },
      },
      {
        test: /\.svg$/,
        use: [
          {
            loader: "url-loader",
            options: {
              limit: 5000,
              outputPath: "svgs",
              name: "[name].[ext]",
            },
          },
        ],
      },
      {
        test: /\.svg$/,
        include: (input) => input.indexOf(BG_IMAGES_DIRNAME) > -1,
        use: {
          loader: "svg-url-loader",
          options: {},
        },
      },
      {
        test: /\.(jpg|jpeg|png|gif)$/i,
        use: [
          {
            loader: "url-loader",
            options: {
              limit: 5000,
              outputPath: "images",
              name: "[name].[ext]",
            },
          },
        ],
      },
    ],
  },
  output: {
    filename: "[name].bundle.js",
    path: path.resolve(__dirname, "dist"),
    publicPath: "/",
    libraryTarget: "umd",
    globalObject: "this",
  },
  resolve: {
    extensions: [".ts", ".tsx", ".js"],
    plugins: [
      new TsconfigPathsPlugin({
        configFile: path.resolve(__dirname, "./tsconfig.json"),
      }),
    ],
    cacheWithContext: false,
  },
  externals: {
    react: "umd react",
    "react-dom": "umd react-dom",
    "react-router-dom": "umd react-router-dom",
    "react-router": "umd react-router",
    "@patternfly/react-core": "umd @patternfly/react-core",
  },
};
