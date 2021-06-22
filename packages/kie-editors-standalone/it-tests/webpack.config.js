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
const HtmlWebPackPlugin = require("html-webpack-plugin");
const common = require("../../../config/webpack.common.config");
const { merge } = require("webpack-merge");
const buildEnv = require("@kogito-tooling/build-env");

module.exports = (env) =>
  merge(common(env), {
    mode: "development",
    entry: {
      app: path.resolve(__dirname, "src", "index.tsx"),
    },
    plugins: [
      new HtmlWebPackPlugin({
        template: path.resolve(__dirname, "public/index.html"),
        filename: "index.html",
      }),
    ],
    externals: {
      "@kogito-tooling/kie-editors-standalone/dist/dmn": "DmnEditor",
      "@kogito-tooling/kie-editors-standalone/dist/bpmn": "BpmnEditor",
    },
    devServer: {
      contentBase: [path.join(__dirname, "dist"), path.join(__dirname, "../dist/")],
      compress: true,
      inline: true,
      historyApiFallback: true,
      overlay: true,
      open: false,
      port: buildEnv.standaloneEditors.dev.port,
    },
  });
