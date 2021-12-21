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
const buildEnv = require("@kogito-tooling/build-env");
const webpack = require("webpack");
const MiniCssExtractPlugin = require("mini-css-extract-plugin");

const commonConfig = (devMode, options = {}) => {
  const wirePath = buildEnv.stunnerEditors.dmnLoader.wirePath;
  console.info(`Stunner Editors :: DMN Loader :: Wire path: '${wirePath}'`);

  return {
    entry: "./src/index.tsx",

    target: "web",

    output: {
      path: path.resolve(__dirname, wirePath),
      filename: `dmn-loader.js`,
      library: {
        type: "umd",
        name: `__KIE__DMN_LOADER__`,
      },
    },

    plugins: [
      new MiniCssExtractPlugin({
        filename: `dmn-loader.css`,
        chunkFilename: `dmn-loader.[id].css]`,
      }),
      new webpack.DefinePlugin({
        __IS_WIRED__: JSON.stringify(wirePath !== buildEnv.vars().ENV_VARS.DMN_LOADER__wirePath.default),
      }),
      new webpack.optimize.LimitChunkCountPlugin({
        maxChunks: 1,
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
              outDir: path.resolve(path.resolve(__dirname, wirePath), `dmn-loader`),
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
        {
          test: /\.(png|svg|jpe?g|gif)$/i,
          use: [
            {
              loader: "file-loader",
              options: {
                name: "[name].[ext]",
                outputPath: "images",
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
