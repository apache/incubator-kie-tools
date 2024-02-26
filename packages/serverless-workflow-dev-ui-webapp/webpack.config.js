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
const swEditor = require("@kie-tools/serverless-workflow-diagram-editor-assets");
const { merge } = require("webpack-merge");
const common = require("@kie-tools-core/webpack-base/webpack.common.config");
const { env } = require("./env");
const buildEnv = env;

module.exports = async (env) =>
  merge(common(env), {
    entry: {
      standalone: path.resolve(__dirname, "src", "standalone", "standalone.ts"),
      envelope: path.resolve(__dirname, "src", "standalone", "EnvelopeApp.ts"),
      "resources/form-displayer": "./src/resources/form-displayer.ts",
      "serverless-workflow-text-editor-envelope": "./src/resources/ServerlessWorkflowTextEditorEnvelopeApp.ts",
      "serverless-workflow-combined-editor-envelope": "./src/resources/ServerlessWorkflowCombinedEditorEnvelopeApp.ts",
      "serverless-workflow-diagram-editor-envelope": "./src/resources/ServerlessWorkflowDiagramEditorEnvelopeApp.ts",
    },
    devServer: {
      static: {
        directory: "./dist",
      },
      port: buildEnv.runtimeToolsDevUiWebapp.dev.port,
      compress: true,
      historyApiFallback: true,
      hot: true,
      client: {
        overlay: false,
        progress: true,
      },
      proxy: [
        {
          context: ["/svg", "/forms", "/customDashboard"],
          target: "http://localhost:4000",
          secure: false,
          changeOrigin: true,
        },
      ],
    },
    plugins: [
      new MonacoWebpackPlugin({
        languages: ["html", "typescript", "json"],
        customLanguages: [
          {
            label: "yaml",
            entry: ["monaco-yaml", "vs/basic-languages/yaml/yaml.contribution"],
            worker: {
              id: "monaco-yaml/yamlWorker",
              entry: "../../monaco-yaml/yaml.worker.js",
            },
          },
        ],
        globalAPI: true,
      }),
      new webpack.EnvironmentPlugin({
        KOGITO_APP_VERSION: "DEV",
        KOGITO_APP_NAME: "Runtime tools dev-ui",
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
          { from: "./src/static", to: "./static" },
          { from: "./src/components/styles.css", to: "./components/styles.css" },
          {
            from: path.join(
              path.dirname(require.resolve("@kie-tools/serverless-workflow-dev-ui-monitoring-webapp/package.json")),
              "/dist"
            ),
            to: "./monitoring-webapp",
          },
          {
            from: path.join(
              path.dirname(require.resolve("@kie-tools/runtime-tools-swf-enveloped-components/package.json")),
              "/dist/customDashboardView"
            ),
            to: "./custom-dashboard-view",
          },
          {
            from: swEditor.swEditorPath(),
            to: "./diagram",
            globOptions: { ignore: ["**/WEB-INF/**/*"] },
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
              {
                source: "./dist/custom-dashboard-view",
                destination: "./dist/resources/webapp/custom-dashboard-view",
              },
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
    ],
    module: {
      rules: [
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
