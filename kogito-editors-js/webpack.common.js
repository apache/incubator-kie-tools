/*
 * Copyright 2021 Red Hat, Inc. and/or its affiliates.
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
const webpack = require("webpack");
const MiniCssExtractPlugin = require("mini-css-extract-plugin");

const commonConfig = (name, devMode, options = {}) => {
  const wire = process.env["wire"];
  const outputDir = path.resolve(__dirname, wire ? `${wire}` : "dist");
  const upperCasedName = name.toUpperCase().replace(/\-/g, "_");

  return {
    entry: "./src/index.tsx",

    target: "web",

    output: {
      path: outputDir,
      filename: `${name}.js`,
      library: {
        type: "umd",
        name: `__KIE__${upperCasedName}__`,
      },
    },

    externals: [/^react.*/, /^@patternfly\/.+$/i],

    plugins: [
      new MiniCssExtractPlugin({
        filename: `${name}.css`,
        chunkFilename: `${name}.[id].css]`,
      }),
      new webpack.DefinePlugin({
        __IS_WIRED__: JSON.stringify(!!wire),
      }),
    ],

    module: {
      rules: [
        {
          test: /\.tsx?$/i,
          loader: "ts-loader",
          options: {
            configFile: path.resolve("./tsconfig.json"),
            compilerOptions: {
              declaration: true,
              outDir: path.resolve(outputDir, `${name}`),
            },
          },
        },
        {
          test: /\.css$/,
          use: [devMode ? "style-loader" : MiniCssExtractPlugin.loader, "css-loader"],
        },
        {
          test: /\.(woff(2)?|ttf|eot)(\?v=\d+\.\d+\.\d+)?$/,
          use: [
            {
              loader: "file-loader",
              options: {
                name: "[name].[ext]",
                outputPath: "fonts",
              },
            },
          ],
        },
      ],
    },
    resolve: {
      extensions: [".tsx", ".ts", ".js", ".jsx"],
      modules: [path.resolve("../../node_modules"), path.resolve("../node_modules"), path.resolve("./src")],
    },

    ...options,
  };
};

module.exports = { commonConfig };
