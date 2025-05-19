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

import * as path from "path";
import { merge } from "webpack-merge";
import NodePolyfillPlugin from "node-polyfill-webpack-plugin";
import HtmlWebpackPlugin from "html-webpack-plugin";
import MonacoWebpackPlugin from "monaco-editor-webpack-plugin";
import CopyPlugin from "copy-webpack-plugin";
import FileManagerPlugin from "filemanager-webpack-plugin";
import common from "@kie-tools-core/webpack-base/webpack.common.config";
import * as swEditorAssets from "@kie-tools/serverless-workflow-diagram-editor-assets";
import { defaultEnvJson } from "./build/defaultEnvJson";

// eslint-disable-next-line @typescript-eslint/ban-ts-comment
// @ts-ignore
import { env } from "./env";

const BG_IMAGES_DIRNAME = "bgimages";
const buildEnv: any = env; // build-env is not typed

export default async (webpackEnv: any, webpackArgv: any) => {
  return [
    {
      ...merge(common(webpackEnv), {
        entry: {
          index: path.resolve(__dirname, "src", "index.tsx"),
          "serverless-workflow-combined-editor-envelope":
            "./src/envelope/ServerlessWorkflowCombinedEditorEnvelopeApp.ts",
          "serverless-workflow-diagram-editor-envelope": "./src/envelope/ServerlessWorkflowDiagramEditorEnvelopeApp.ts",
          "serverless-workflow-text-editor-envelope": "./src/envelope/ServerlessWorkflowTextEditorEnvelopeApp.ts",
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
              { from: "./src/static/favicon.svg", to: "./favicon.svg" },
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
                from: path.join(path.dirname(require.resolve("@kie-tools/dashbuilder-client/package.json")), "/dist"),
                to: "./monitoring-webapp",
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
      }),
      devServer: {
        static: {
          directory: "./dist",
        },
        host: buildEnv.sonataflowManagementConsoleWebapp.host,
        port: buildEnv.sonataflowManagementConsoleWebapp.port,
        compress: true,
        historyApiFallback: false,
        hot: true,
        client: {
          overlay: {
            warnings: false,
            errors: true,
            runtimeErrors: false,
          },
          progress: true,
        },
        proxy: [
          {
            context: (path, req) => req.method === "POST" || path.startsWith("/graphql") || path === "/q/openapi.json",
            target: "http://localhost:4000",
            secure: false,
            changeOrigin: true,
          },
        ],
      },
    },
  ];
};
