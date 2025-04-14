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
const { EnvironmentPlugin } = require("webpack");
const common = require("@kie-tools-core/webpack-base/webpack.common.config");
const HtmlWebpackPlugin = require("html-webpack-plugin");
const CopyPlugin = require("copy-webpack-plugin");
const childProcess = require("child_process");
const { defaultEnvJson } = require("./build/defaultEnvJson");

const { env } = require("./env");
const buildEnv = env;

const BG_IMAGES_DIRNAME = "bgimages";

module.exports = async (webpackEnv) => {
  const buildInfo = getBuildInfo();
  let lastCommitSha = "";
  try {
    lastCommitSha = childProcess.execSync("git rev-parse --short HEAD").toString().trim();
    JSON.stringify(lastCommitSha);
  } catch (e) {
    lastCommitSha = "unavailable";
  }

  return merge(common(webpackEnv), {
    entry: {
      index: "./src/index.tsx",
      "resources/form-displayer-envelope": "./src/forms/form-displayer-envelope.ts",
    },
    plugins: [
      new EnvironmentPlugin({
        WEBPACK_REPLACE__commitHash: lastCommitSha,
        WEBPACK_REPLACE__buildInfo: buildInfo,
        WEBPACK_REPLACE__kogitoVersion: buildEnv.versions.kogito,
      }),
      new HtmlWebpackPlugin({
        template: "./static/index.html",
        inject: false,
        minify: false,
      }),
      new CopyPlugin({
        patterns: [
          { from: "./static/resources", to: "./resources" },
          { from: "./static/images", to: "./images" },
          { from: "./static/favicon.svg", to: "./favicon.svg" },
          {
            from: "./static/env.json",
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
            loader: require.resolve("file-loader"),
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
              loader: require.resolve("url-loader"),
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
            loader: require.resolve("svg-url-loader"),
            options: {},
          },
        },
        {
          test: /\.(jpg|jpeg|png|gif)$/i,
          use: [
            {
              loader: require.resolve("url-loader"),
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
    devServer: {
      server: "http",
      host: buildEnv.runtimeToolsManagementConsoleWebapp.dev.host,
      port: buildEnv.runtimeToolsManagementConsoleWebapp.dev.port,
      historyApiFallback: true, // FIXME: Tiago --> This is making the router work.
      static: [{ directory: path.join(__dirname, "./dist") }, { directory: path.join(__dirname, "./static") }],
      compress: true,
      client: {
        overlay: false,
        progress: true,
      },
      allowedHosts: "all",
    },
    watchOptions: {
      poll: 1000,
    },
  });
};

function getBuildInfo() {
  const buildInfo = buildEnv.runtimeToolsManagementConsoleWebapp.buildInfo;
  console.info(`Management Console Webapp :: Build info: ${buildInfo}`);
  return buildInfo;
}
