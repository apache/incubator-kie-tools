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

const path = require("path");
const { merge } = require("webpack-merge");
const common = require("@kie-tools-core/webpack-base/webpack.common.config");
const HtmlWebpackPlugin = require("html-webpack-plugin");
const CopyPlugin = require("copy-webpack-plugin");
const { env: buildEnv } = require("./env");
const { defaultEnvJson } = require("./build/defaultEnvJson");

const BG_IMAGES_DIRNAME = "bgimages";

module.exports = async (env) => {
  return merge(common(env), {
    entry: {
      index: path.resolve(__dirname, "src", "index.tsx"),
      "resources/form-displayer": "./src/resources/form-displayer.ts",
    },
    devServer: {
      static: {
        directory: "./dist",
      },
      host: buildEnv.runtimeToolsTaskConsoleWebapp.host,
      port: buildEnv.runtimeToolsTaskConsoleWebapp.port,
      compress: true,
      historyApiFallback: true,
      hot: true,
      client: {
        overlay: {
          warnings: false,
          errors: true,
          runtimeErrors: false,
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
      new HtmlWebpackPlugin({
        template: "./src/index.html",
        inject: false,
        minify: false,
      }),
      new CopyPlugin({
        patterns: [
          {
            from: "./src/static/env.json",
            to: "./env.json",
            transform: () => JSON.stringify(defaultEnvJson, null, 2),
          },
        ],
      }),
    ],
    module: {
      rules: [
        {
          test: /\.(css|sass|scss)$/,
          use: [require.resolve("style-loader"), require.resolve("css-loader"), require.resolve("sass-loader")],
        },
        {
          test: /\.(svg|ttf|eot|woff|woff2)$/,
          use: {
            loader: "file-loader",
            options: {
              // Limit at 50k. larger files emited into separate files
              limit: 5000,
              outputPath: "fonts",
              name: "[path][name].[ext]",
            },
          },
        },
        {
          test: /\.svg$/,
          include: (input) => input.indexOf("background-filter.svg") > 1,
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
    resolve: {
      fallback: {
        https: require.resolve("https-browserify"),
        http: require.resolve("stream-http"),
      },
    },
    ignoreWarnings: [/Failed to parse source map/],
  });
};
