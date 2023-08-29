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
const patternflyBase = require("@kie-tools-core/patternfly-base");
const { merge } = require("webpack-merge");
const common = require("@kie-tools-core/webpack-base/webpack.common.config");
const { env } = require("./env");
const buildEnv = env;
const dashbuilderClient = require("@kie-tools/dashbuilder-client");

module.exports = async (env) =>
  merge(common(env), {
    entry: {
      index: "./src/index.tsx",
      "dashbuilder-viewer-envelope-app": "./src/envelope/DashbuilderViewerEnvelopeApp.ts",
    },
    plugins: [
      new CopyPlugin({
        patterns: [
          { from: "./static/resources", to: "./resources" },
          { from: "./static/favicon.svg", to: "./favicon.svg" },
          { from: "./static/index.html", to: "./index.html" },
          { from: "./static/dashboard.dash.yaml", to: "./dashboard.dash.yaml" },
          {
            from: "./static/dashbuilder-viewer-deployment-webapp-data.json",
            to: "./dashbuilder-viewer-deployment-webapp-data.json",
          },
          { from: "./static/setup.js", to: "./setup.js" },
          {
            from: "./static/envelope/dashbuilder-viewer-envelope.html",
            to: "./dashbuilder-viewer-envelope.html",
          },
          {
            from: dashbuilderClient.dashbuilderPath(),
            to: "./",
            globOptions: { ignore: ["**/WEB-INF/**/*"] },
          },
        ],
      }),
    ],
    module: {
      rules: [...patternflyBase.webpackModuleRules],
    },
    devServer: {
      historyApiFallback: false,
      static: [{ directory: path.join(__dirname, "./dist") }, { directory: path.join(__dirname, "./static") }],
      compress: true,
      port: buildEnv.dashbuilderViewerDeploymentWebApp.dev.port,
    },
  });
