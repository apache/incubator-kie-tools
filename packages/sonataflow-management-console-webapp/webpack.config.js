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
const NodePolyfillPlugin = require("node-polyfill-webpack-plugin");
const HtmlWebpackPlugin = require("html-webpack-plugin");
const MonacoWebpackPlugin = require("monaco-editor-webpack-plugin");
const CopyPlugin = require("copy-webpack-plugin");
const FileManagerPlugin = require("filemanager-webpack-plugin");
const common = require("@kie-tools-core/webpack-base/webpack.common.config");
const swEditorAssets = require("@kie-tools/serverless-workflow-diagram-editor-assets");
const { env: buildEnv } = require("./env");
const { defaultEnvJson } = require("./build/defaultEnvJson");

const BG_IMAGES_DIRNAME = "bgimages";

module.exports = async (env) => {
  return merge(common(env), {
    entry: {
      index: path.resolve(__dirname, "src", "index.tsx"),
      "serverless-workflow-combined-editor-envelope": "./src/envelope/ServerlessWorkflowCombinedEditorEnvelopeApp.ts",
      "serverless-workflow-diagram-editor-envelope": "./src/envelope/ServerlessWorkflowDiagramEditorEnvelopeApp.ts",
      "serverless-workflow-text-editor-envelope": "./src/envelope/ServerlessWorkflowTextEditorEnvelopeApp.ts",
    },
    devServer: {
      static: {
        directory: "./dist",
      },
      host: buildEnv.sonataflowManagementConsoleWebapp.host,
      port: buildEnv.sonataflowManagementConsoleWebapp.port,
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
          { from: "./resources", to: "./resources" },
          {
            from: "./resources/serverless-workflow-combined-editor-envelope.html",
            to: "./serverless-workflow-combined-editor-envelope.html",
          },
          {
            from: "./resources/serverless-workflow-diagram-editor-envelope.html",
            to: "./serverless-workflow-diagram-editor-envelope.html",
          },
          {
            from: "./resources/serverless-workflow-text-editor-envelope.html",
            to: "./serverless-workflow-text-editor-envelope.html",
          },
          {
            from: "./resources/monitoring-webapp",
            to: "./monitoring-webapp",
          },
          {
            from: "./src/static/env.json",
            to: "./env.json",
            transform: () => JSON.stringify(defaultEnvJson, null, 2),
          },
          {
            from: swEditorAssets.swEditorPath(),
            to: "./diagram",
            globOptions: { ignore: ["**/WEB-INF/**/*", "**/*.html"] },
          },
          {
            context: swEditorAssets.swEditorFontsPath(),
            from: "fontawesome-webfont.*",
            to: "./fonts",
            force: true,
          },
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
              {
                source: "./dist/monitoring-webapp",
                destination: "./dist/resources/webapp/monitoring-webapp",
              },
            ],
          },
        },
      }),
      new MonacoWebpackPlugin({
        languages: ["json"],
      }),
      new NodePolyfillPlugin(),
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
  });
};
