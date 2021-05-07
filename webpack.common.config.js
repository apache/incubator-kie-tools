/*
 * Copyright 2020 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

const path = require("path");

module.exports = {
  mode: "production",
  output: {
    path: path.resolve("./dist"),
    filename: "[name].js",
    chunkFilename: "[name].bundle.js",
  },
  stats: {
    excludeAssets: [
      (name) => !name.endsWith(".js"),
      /.*DMNKogitoRuntimeWebapp.*/,
      /.*KogitoBPMNEditor.*/,
      /.*DroolsWorkbenchScenarioSimulationKogitoRuntime.*/,
      /gwt-editors\/.*/,
      /editors\/.*/,
    ],
    excludeModules: true,
  },
  performance: {
    maxAssetSize: 30000000,
    maxEntrypointSize: 30000000,
  },
  resolve: {
    fallback: { path: require.resolve("path-browserify") }, // Required for `minimatch`, as Webpack 5 doesn't add polyfills automatically anymore.
    extensions: [".tsx", ".ts", ".js", ".jsx"],
    modules: [path.resolve("../../node_modules"), path.resolve("./node_modules"), path.resolve("./src")],
  },
  module: {
    rules: [
      {
        test: /\.tsx?$/,
        loader: "ts-loader",
      },
    ],
  },
};
