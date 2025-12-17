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
const HtmlWebpackPlugin = require("html-webpack-plugin");
const webpack = require("webpack");
const BG_IMAGES_DIRNAME = "bgimages";
const CopyPlugin = require("copy-webpack-plugin");
const FileManagerPlugin = require("filemanager-webpack-plugin");
const MonacoWebpackPlugin = require("monaco-editor-webpack-plugin");
const NodePolyfillPlugin = require("node-polyfill-webpack-plugin");
const { merge } = require("webpack-merge");
const common = require("@kie-tools-core/webpack-base/webpack.common.config");
const HtmlReplaceWebpackPlugin = require("html-replace-webpack-plugin");
const { env } = require("./env");

module.exports = async (webpackEnv) => {
  const dataIndexURL = env.runtimeToolsProcessDevUIWebapp.kogitoDataIndexUrl;
  return merge(common(webpackEnv), {
    entry: {
      standalone: path.resolve(__dirname, "src", "standalone", "standalone.ts"),
      envelope: path.resolve(__dirname, "src", "standalone", "EnvelopeApp.ts"),
      "resources/form-displayer": "./src/resources/form-displayer.ts",
    },
    devServer: {
      static: {
        directory: "./dist",
      },
      port: env.runtimeToolsProcessDevUIWebapp.port,
      compress: true,
      historyApiFallback: true,
      hot: true,
      client: {
        overlay: false,
        progress: true,
      },
      proxy: [
        {
          context: ["/svg", "/forms", "/q", "/hiring/schema"],
          target: "http://localhost:4000",
          secure: false,
          changeOrigin: true,
        },
      ],
    },
    plugins: [
      new MonacoWebpackPlugin({
        languages: ["html", "typescript", "json"],
        features: ["bracketMatching", "folding", "suggest"],
        globalAPI: true,
      }),
      new webpack.EnvironmentPlugin({
        KOGITO_APP_VERSION: "DEV",
        KOGITO_APP_NAME: "Runtime tools dev-ui",
        KOGITO_DATAINDEX_HTTP_URL: dataIndexURL,
        KOGITO_REMOTE_KOGITO_APP_URL: env.runtimeToolsProcessDevUIWebapp.kogitoAppUrl,
      }),
      new CopyPlugin({
        patterns: [
          { from: "./resources", to: "./resources" },
          { from: "./src/static", to: "./static" },
          { from: "./src/components/styles.css", to: "./components/styles.css" },
        ],
      }),
      new FileManagerPlugin({
        events: {
          onEnd: {
            mkdir: ["./dist/resources/webapp/"],
            copy: [
              { source: "./dist/*.js", destination: "./dist/resources/webapp/" },
              { source: "./dist/*.map", destination: "./dist/resources/webapp/" },
              { source: "./dist/fonts", destination: "./dist/resources/webapp/fonts" },
            ],
          },
        },
      }),
      new NodePolyfillPlugin(),
      new HtmlWebpackPlugin({
        template: path.resolve(__dirname, "resources", "index.html"),
        favicon: "src/favicon.ico",
        chunks: ["app"],
      }),
      new HtmlReplaceWebpackPlugin([
        {
          pattern: /\${WEBPACK_REPLACEMENT_WEBAPP_HOST}/g,
          replacement: () => env.runtimeToolsProcessDevUIWebapp.host ?? "",
        },
        {
          pattern: /\${WEBPACK_REPLACEMENT_WEBAPP_PORT}/g,
          replacement: () => env.runtimeToolsProcessDevUIWebapp.port ?? "",
        },
      ]),
    ],
    module: {
      rules: [
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
        {
          test: /\.(css|sass|scss)$/,
          use: [require.resolve("style-loader"), require.resolve("css-loader"), require.resolve("sass-loader")],
        },
        {
          test: /\.css$/,
          include: [path.resolve("../../node_modules/monaco-editor")],
          use: [require.resolve("style-loader"), require.resolve("css-loader")],
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
