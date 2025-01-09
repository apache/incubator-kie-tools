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

const { merge } = require("webpack-merge");
const common = require("@kie-tools-core/webpack-base/webpack.common.config");
const path = require("path");
const patternflyBase = require("@kie-tools-core/patternfly-base");
const { ProvidePlugin } = require("webpack");

module.exports = (webpackEnv) =>
  merge(common(webpackEnv), {
    output: {
      path: path.join(__dirname, "dist"),
      filename: "[name].js",
      publicPath: "/",
    },
    entry: {
      envelope: "./src/envelope/DmnEditorStandaloneEnvelopeInit.ts",
    },
    plugins: [
      new ProvidePlugin({
        process: require.resolve("process/browser.js"),
        Buffer: ["buffer", "Buffer"],
      }),
    ],
    module: {
      rules: [
        patternflyBase.individualRules.scssRule,
        patternflyBase.individualRules.cssRule,
        ...[
          patternflyBase.individualRules.fontsRule,
          patternflyBase.individualRules.backgroundFilterSvgRule,
          patternflyBase.individualRules.backgroundSvgRule,
          patternflyBase.individualRules.genericSvgRule,
          patternflyBase.individualRules.imagesRule,
        ].map((rule) => ({
          ...rule,
          use: undefined,
          type: "asset/inline",
        })),
      ],
    },
    optimization: {
      removeAvailableModules: true,
      sideEffects: true,
    },
  });
