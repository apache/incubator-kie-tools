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

const patternflyBase = require("@kie-tools-core/patternfly-base");
const common = require("@kie-tools-core/webpack-base/webpack.common.config");
const { merge } = require("webpack-merge");

const commonConfig = (webpackEnv) =>
  merge(common(webpackEnv), {
    output: {
      library: "TodoListViewEntrypoint",
      libraryTarget: "umd",
      umdNamedDefine: true,
    },
    externals: {
      vscode: "commonjs vscode",
    },
  });

module.exports = (webpackEnv) => [
  merge(commonConfig(webpackEnv), {
    target: "node",
    entry: {
      extension: "./src/extension.ts",
    },
    plugins: [],
  }),
  merge(commonConfig(webpackEnv), {
    target: "web",
    entry: {
      "todo-list-view-envelope": "./src/TodoListViewEnvelope.ts",
    },
    module: {
      rules: [...patternflyBase.webpackModuleRules],
    },
  }),
];
