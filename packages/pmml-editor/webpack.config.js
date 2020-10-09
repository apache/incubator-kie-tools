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

const path = require("path");
const CopyPlugin = require("copy-webpack-plugin");
const nodeExternals = require("webpack-node-externals");
const { merge } = require("webpack-merge");
const common = require("../../webpack.common.config");
const pfWebpackOptions = require("@kogito-tooling/patternfly-base/patternflyWebpackOptions");

let config = merge(common, {
  entry: {
    "editor/index": "./src/editor/index.ts"
  },
  output: {
    libraryTarget: "umd",
    globalObject: "this"
  },
  module: {
    rules: [...pfWebpackOptions.patternflyRules]
  }
});

module.exports = (env, argv) => {
  if (argv.mode === "development") {
    config = merge(config, {
      entry: {
        "showcase/index": "./src/showcase/index.tsx"
      },
      plugins: [
        new CopyPlugin([
          { from: "./src/showcase/static/resources", to: "./showcase/resources" },
          { from: "./src/showcase/static/index.html", to: "./showcase/index.html" },
          { from: "./src/showcase/static/favicon.ico", to: "./showcase/favicon.ico" }
        ])
      ],
      devtool: "source-map",
      devServer: {
        historyApiFallback: true,
        disableHostCheck: true,
        watchContentBase: true,
        contentBase: path.join(__dirname, "./dist/showcase"),
        compress: true,
        port: 9001,
        open: true,
        inline: true,
        hot: true,
        overlay: true
      }
    });
  }

  if (argv.mode === "production") {
    config = merge(config, {
      externals: [nodeExternals({ modulesDir: "../../node_modules" })]
    });
  }

  return config;
};
