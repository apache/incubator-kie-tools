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
const CopyPlugin = require("copy-webpack-plugin");
const NodePolyfillPlugin = require("node-polyfill-webpack-plugin");
const TerserPlugin = require("terser-webpack-plugin");
const patternflyBase = require("@kie-tools-core/patternfly-base");
const { merge } = require("webpack-merge");
const MonacoWebpackPlugin = require("monaco-editor-webpack-plugin");
const common = require("@kie-tools-core/webpack-base/webpack.common.config");
const swEditorAssets = require("@kie-tools/serverless-workflow-diagram-editor-assets");
const { env } = require("./env");
const buildEnv = env;

module.exports = async (env) =>
  merge(common(env), {
    entry: {
      index: "./src/index.tsx",
      "serverless-workflow-combined-editor-envelope": "./src/envelope/ServerlessWorkflowCombinedEditorEnvelopeApp.ts",
      "serverless-workflow-diagram-editor-envelope": "./src/envelope/ServerlessWorkflowDiagramEditorEnvelopeApp.ts",
      "serverless-workflow-text-editor-envelope": "./src/envelope/ServerlessWorkflowTextEditorEnvelopeApp.ts",
    },
    optimization: {
      minimizer: [
        new TerserPlugin({
          terserOptions: {
            format: {
              comments: false,
            },
          },
          extractComments: false,
        }),
      ],
    },
    plugins: [
      new CopyPlugin({
        patterns: [
          { from: "./static/index.html", to: "./index.html" },
          { from: "./static/resources", to: "./resources" },
          { from: "./static/favicon.svg", to: "./favicon.svg" },
          { from: "./static/sonataflow-deployment-webapp-data.json", to: "./sonataflow-deployment-webapp-data.json" },
          {
            from: "./static/envelope/serverless-workflow-combined-editor-envelope.html",
            to: "./serverless-workflow-combined-editor-envelope.html",
          },
          {
            from: "./static/envelope/serverless-workflow-diagram-editor-envelope.html",
            to: "./serverless-workflow-diagram-editor-envelope.html",
          },
          {
            from: "./static/envelope/serverless-workflow-text-editor-envelope.html",
            to: "./serverless-workflow-text-editor-envelope.html",
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
      new MonacoWebpackPlugin({
        languages: ["json"],
      }),
      new NodePolyfillPlugin({
        includeAliases: ["https", "process", "Buffer"],
      }),
    ],
    module: {
      rules: [...patternflyBase.webpackModuleRules],
    },
    devServer: {
      historyApiFallback: false,
      static: [{ directory: path.join(__dirname, "./dist") }, { directory: path.join(__dirname, "./static") }],
      compress: true,
      port: buildEnv.sonataFlowDeploymentWebapp.dev.port,
      client: {
        overlay: false,
      },
    },
    resolve: {
      fallback: {
        http: require.resolve("stream-http"),
      },
    },
    ignoreWarnings: [/Failed to parse source map/],
  });
