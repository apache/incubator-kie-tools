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
const patternflyBase = require("@kie-tools-core/patternfly-base");
const { merge } = require("webpack-merge");
const { stylePaths } = require("./stylePaths");
const common = require("@kie-tools-core/webpack-base/webpack.common.config");
const HtmlWebpackPlugin = require("html-webpack-plugin");
const MiniCssExtractPlugin = require("mini-css-extract-plugin");

module.exports = (env) => {
  const devResources = [];
  const isDev = env.WEBPACK_SERVE;

  if (isDev) {
    devResources.push({ from: "./static/manifest.dev.json", to: "./manifest.dev.json" });
  }

  console.log(stylePaths);
  return merge(common(env), {
    entry: {
      index: isDev ? "./src/index-dev.tsx" : "./src/index.tsx",
    },
    plugins: [
      new HtmlWebpackPlugin({
        template: "./static/index.html",
        minify: false,
      }),
      new CopyPlugin({
        patterns: [
          { from: path.resolve(__dirname, "./static/resources"), to: "./resources" },
          { from: "./static/manifest.json", to: "manifest.json" },
          /*
          // main
          { from: "../../node_modules/@patternfly/patternfly/patternfly.min.css", to: "patternfly.min.css" },
          // fonts
          {
            from: "../../node_modules/@patternfly/react-core/dist/styles/assets/fonts/RedHatText/RedHatText-Regular.woff2",
            to: "assets/fonts/RedHatText/RedHatText-Regular.woff2",
          },
          {
            from: "../../node_modules/@patternfly/react-core/dist/styles/assets/fonts/RedHatText/RedHatText-Medium.woff2",
            to: "assets/fonts/RedHatText/RedHatText-Medium.woff2",
          },*/
          ...devResources,
        ],
      }),
      new MiniCssExtractPlugin({
        filename: "[name].css",
        chunkFilename: "[name].bundle.css",
      }),
    ],
    module: {
      rules: [
        {
          test: /\.css$/,
          include: [...stylePaths],
          use: ["style-loader", "css-loader"],
        },
        ...patternflyBase.webpackModuleRules,
      ],
    },
    devServer: {
      historyApiFallback: false,
      static: [{ directory: path.join(__dirname, "./dist") }, { directory: path.join(__dirname, "./static") }],
      compress: true,
      port: 9001,
    },
  });
};
