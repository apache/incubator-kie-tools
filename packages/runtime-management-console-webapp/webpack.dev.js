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
const { merge } = require("webpack-merge");
const common = require("./webpack.common.js");
const webpack = require("webpack");

const HOST = process.env.HOST || "localhost";
const PORT = process.env.PORT || "9000";

module.exports = function (env) {
  const dataIndexURL = env?.KOGITO_DATAINDEX_HTTP_URL ?? "http://localhost:4000/graphql";
  return merge(common, {
    mode: "development",
    devtool: "source-map",
    devServer: {
      static: {
        directory: "./dist",
      },
      host: HOST,
      port: PORT,
      compress: true,
      historyApiFallback: true,
      hot: true,
      open: true,
      client: {
        overlay: {
          warnings: false,
          errors: true,
        },
        progress: true,
      },
      proxy: {
        "/svg": {
          target: "http://localhost:4000",
          secure: false,
          changeOrigin: true,
        },
      },
    },
    plugins: [
      new webpack.EnvironmentPlugin({
        KOGITO_ENV_MODE: "DEV",
        KOGITO_DATAINDEX_HTTP_URL: dataIndexURL,
      }),
    ],
    module: {
      rules: [
        {
          test: /\.(css|sass|scss)$/,
          use: [require.resolve("style-loader"), require.resolve("css-loader"), require.resolve("sass-loader")],
        },
      ],
    },
    resolve: {
      extensions: [".tsx", ".ts", ".js", ".jsx"],
      modules: [path.resolve("../../node_modules"), path.resolve("./node_modules"), path.resolve("./src")],
    },
  });
};
