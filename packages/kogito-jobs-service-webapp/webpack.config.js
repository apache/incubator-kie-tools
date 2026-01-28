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
const { merge } = require("webpack-merge");
const common = require("@kie-tools-core/webpack-base/webpack.common.config");
const { env } = require("./env");
const HtmlWebpackPlugin = require("html-webpack-plugin");

module.exports = async (webpackEnv) =>
  merge(common(webpackEnv), {
    entry: {},
    plugins: [
      new CopyPlugin({
        patterns: [
          { from: "./src/styles.css", to: "./styles.css" },
          { from: "./static/favicon.svg", to: "./favicon.svg" },
        ],
      }),
      new HtmlWebpackPlugin({
        template: path.resolve(__dirname, "src/index.html"),
        filename: "index.html",
        templateParameters: {
          KOGITO_JOBS_SERVICE_WEBAPP_TITLE: env.kogitoJobsServiceWebapp.title,
          KOGITO_JOBS_SERVICE_WEBAPP_LOGO: env.kogitoJobsServiceWebapp.logo,
          KOGITO_JOBS_SERVICE_WEBAPP_DOCLINK_HREF: env.kogitoJobsServiceWebapp.docLinkKogito.href,
          KOGITO_JOBS_SERVICE_WEBAPP_DOCLINK_TEXT: env.kogitoJobsServiceWebapp.docLinkKogito.text,
          SONATAFLOW_JOBS_SERVICE_WEBAPP_DOCLINK_HREF: env.kogitoJobsServiceWebapp.docLinkSonataflow.href,
          SONATAFLOW_JOBS_SERVICE_WEBAPP_DOCLINK_TEXT: env.kogitoJobsServiceWebapp.docLinkSonataflow.text,
          KOGITO_JOBS_SERVICE_WEBAPP_VERSION: env.kogitoJobsServiceWebapp.version,
        },
      }),
    ],
    ignoreWarnings: [/Failed to parse source map/],
    devServer: {
      static: {
        directory: "./dist",
      },
      port: env.kogitoJobsServiceWebapp.dev.port,
    },
  });
