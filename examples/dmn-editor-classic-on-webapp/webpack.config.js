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
const { ProvidePlugin } = require("webpack");
const CopyPlugin = require("copy-webpack-plugin");
const patternflyBase = require("@kie-tools-core/patternfly-base");
const common = require("@kie-tools-core/webpack-base/webpack.common.config");
const { merge } = require("webpack-merge");
const stunnerEditors = require("@kie-tools/stunner-editors");
const { env } = require("./env");

module.exports = (webpackEnv) => [
  merge(common(webpackEnv), {
    entry: {
      index: "./src/index.tsx",
      "dmn-editor-classic-envelope": "./src/DmnEditorClassicEnvelope.ts",
    },
    output: {
      publicPath: "/",
    },
    module: {
      rules: [...patternflyBase.webpackModuleRules],
    },
    plugins: [
      new CopyPlugin({
        patterns: [
          { from: "./static", to: "." },
          {
            from: stunnerEditors.dmnEditorPath(),
            to: "./dmn-editor-classic",
            globOptions: { ignore: ["WEB-INF/**/*"] },
          },
        ],
      }),
      new ProvidePlugin({
        process: require.resolve("process/browser.js"),
        Buffer: ["buffer", "Buffer"],
      }),
    ],
    devServer: {
      historyApiFallback: false,
      static: [{ directory: path.join(__dirname, "./dist") }, { directory: path.join(__dirname, "./static") }],
      compress: true,
      port: env.dmnEditorClassicOnWebappExample.port,
    },
    ignoreWarnings: [/Failed to parse source map/],
  }),
];
