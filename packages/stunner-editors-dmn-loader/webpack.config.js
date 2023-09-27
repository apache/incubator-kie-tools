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
const { merge } = require("webpack-merge");
const common = require("@kie-tools-core/webpack-base/webpack.common.config");
const patternflyBase = require("@kie-tools-core/patternfly-base");
const { env } = require("./env");
const buildEnv = env;

module.exports = (env) => {
  const outputPath = buildEnv.stunnerEditors.dmnLoader.outputPath;
  console.info(`Stunner Editors :: DMN Loader :: Output path: '${outputPath}'`);

  return merge(common(env), {
    entry: "./src/index.tsx",

    target: "web",

    output: {
      path: path.resolve(__dirname, outputPath),
      publicPath: "",
      filename: `dmn-loader.js`,
      library: {
        type: "umd",
        name: `__KIE__DMN_LOADER__`,
      },
    },

    module: {
      rules: patternflyBase.webpackModuleRules,
    },
  });
};
