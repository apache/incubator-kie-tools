/*
 * Copyright 2020 Red Hat, Inc. and/or its affiliates.
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

const { merge } = require("webpack-merge");
const common = require("../../webpack.common.config");
const CopyPlugin = require("copy-webpack-plugin");
const pfWebpackOptions = require("@kogito-tooling/patternfly-base/patternflyWebpackOptions");

module.exports = [
  merge(common, {
    entry: {
      "preprocessor/preprocessor": "./src/preprocessor/preprocessor.ts",
    },
    plugins: [new CopyPlugin({ patterns: [{ from: "./resources", to: "./resources" }] })],
    target: "node",
    node: {
      __dirname: true, //Uses current working dir
      __filename: true, //Uses current working dir
    },
  }),
  merge(common, {
    output: {
      publicPath: "",
    },
    entry: {
      "envelope/bpmn-envelope": "./src/envelope/BpmnEditorEnvelopeApp.ts",
      "envelope/dmn-envelope": "./src/envelope/DmnEditorEnvelopeApp.ts",
    },
    module: {
      rules: [...pfWebpackOptions.patternflyRules],
    },
  }),
];
