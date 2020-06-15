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
const nodeExternals = require("webpack-node-externals");

module.exports = async (env, argv) => {
  return {
    mode: "development",
    entry: {
      index: "./src/index.ts"
    },
    output: {
      path: path.resolve(__dirname, "./dist"),
      filename: "[name].js",
      libraryTarget: "umd",
      globalObject: "this"
    },
    externals: [nodeExternals({ modulesDir: "../../node_modules" })],
    module: {
      rules: [
        {
          test: /\.ts$/,
          include: path.resolve(__dirname, "src"),
          use: [
            {
              loader: "ts-loader",
              options: {
                configFile: path.resolve("./tsconfig.json")
              }
            }
          ]
        }
      ]
    },
    resolve: {
      extensions: [".ts"],
      modules: [path.resolve("../../node_modules"), path.resolve("./node_modules"), path.resolve("./src")]
    }
  };
};
