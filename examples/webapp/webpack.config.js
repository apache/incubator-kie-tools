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
const CopyPlugin = require("copy-webpack-plugin");
const patternflyBase = require("@kie-tools-core/patternfly-base");
const common = require("@kie-tools-core/webpack-base/webpack.common.config");
const { merge } = require("webpack-merge");
const stunnerEditors = require("@kie-tools/stunner-editors");
const { env } = require("./env");
const buildEnv = env;

module.exports = (env) => [
  merge(common(env), {
    entry: {
      index: "./src/index.tsx",
      "envelope/base64-editor": "./src/envelope/base64-editor.ts",
      "envelope/dmn-editor": "./src/envelope/dmn-editor.ts",
      "envelope/ping-pong-view-react-impl": "./src/envelope/ping-pong-view-react-impl.ts",
      "envelope/todo-list-view": "./src/envelope/todo-list-view.ts",
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
          { from: "./envelope", to: "./envelope" },
          { from: "./static", to: "." },
          { from: "../ping-pong-view-angular/dist/app", to: "./envelope/angular" },
          { from: stunnerEditors.dmnEditorPath(), to: "./dmn-editor/dmn", globOptions: { ignore: ["WEB-INF/**/*"] } },
        ],
      }),
    ],
    devServer: {
      historyApiFallback: false,
      static: [{ directory: path.join(__dirname, "./dist") }, { directory: path.join(__dirname, "./static") }],
      compress: true,
      port: buildEnv.exampleWebapp.port,
    },
    ignoreWarnings: [/Failed to parse source map/],
  }),
];
