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
const { env } = require("../../env");
const { ProvidePlugin, EnvironmentPlugin } = require("webpack");
const NodePolyfillPlugin = require("node-polyfill-webpack-plugin");

const buildEnv = env;

module.exports = (env) =>
  merge(common(env), {
    mode: "development",
    entry: {
      index: path.resolve(__dirname, "./index.tsx"),
    },
    output: {
      path: path.resolve(__dirname, "../dist-dev"),
    },
    plugins: [
      new CopyPlugin({
        patterns: [{ from: path.resolve(__dirname, "./static"), to: "./" }],
      }),
      new ProvidePlugin({
        process: require.resolve("process/browser.js"),
        Buffer: ["buffer", "Buffer"],
      }),
      new EnvironmentPlugin({
        WEBPACK_REPLACE__quarkusPort: buildEnv.devDeploymentDmnFormWebapp.dev.quarkusPort,
      }),
      new NodePolyfillPlugin(),
    ],
    module: {
      rules: [...patternflyBase.webpackModuleRules],
    },
    devServer: {
      static: { directory: path.join(__dirname, "./dist") },
      historyApiFallback: true,
      compress: true,
      port: buildEnv.devDeploymentDmnFormWebapp.dev.webpackPort,
      open: false,
      hot: true,
      client: {
        overlay: true,
      },
    },
  });
