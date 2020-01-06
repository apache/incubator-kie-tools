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

module.exports = {
  mode: "development",
  devtool: "inline-source-map",
  entry: {
    index: "./src/index.tsx",
    "envelope/index": "./src/envelope/index.ts"
  },
  output: {
    path: path.resolve(__dirname, "./dist"),
    filename: "[name].js"
  },
  externals: {},
  plugins: [
    new CopyPlugin([
      { from: "./static/resources", to: "./resources" },
      { from: "./static/envelope", to: "./envelope" },
      { from: "./static/images", to: "./images" },
      { from: "./static/samples", to: "./samples" },
      { from: "./static/index.html", to: "./index.html" },
      { from: "../kie-bc-editors-unpacked", to: "./gwt-editors" }
    ])
  ],
  module: {
    rules: [
      {
        test: /\.tsx?$/,
        include: path.resolve(__dirname, "src"),
        use: [
          {
            loader: "ts-loader",
            options: {
              configFile: path.resolve("./tsconfig.json")
            }
          }
        ]
      },
      {
        test: /\.jsx?$/,
        exclude: /node_modules/,
        use: ["babel-loader"]
      },
      {
        test: /\.s[ac]ss$/i,
        use: ["style-loader", "css-loader", "sass-loader"]
      },
      {
        test: /\.css$/,
        use: ["style-loader", "css-loader"]
      },
      {
        test: /\.(woff)$/,
        include: [
          path.resolve(__dirname, "../../node_modules/@patternfly/patternfly/assets/fonts/RedHatDisplay"),
          path.resolve(__dirname, "../../node_modules/@patternfly/patternfly/assets/fonts/RedHatText")
        ],
        use: {
          loader: "file-loader",
          options: {
            limit: 244,
            outputPath: "fonts",
            name: "[name].[ext]"
          }
        }
      },
      {
        test: /RedHat.*\.(woff2|ttf|eot|otf|svg)/,
        loader: "null-loader"
      },
      {
        test: /overpass-.*\.(woff2?|ttf|eot|otf)(\?.*$|$)/,
        loader: "null-loader"
      },
      {
        test: /pficon\.(woff2?|ttf|eot|otf|svg)/,
        loader: "null-loader"
      },
      {
        test: /fa-solid-900\.(woff2?|ttf|eot|otf|svg)/,
        loader: "null-loader"
      },
      {
        test: /pfbg_.*\.jpg$/,
        loader: "null-loader"
      }
    ]
  },
  devServer: {
    historyApiFallback: {
      disableDotRule: true
    },
    disableHostCheck: true,
    watchContentBase: true,
    contentBase: [path.join(__dirname, "./dist"), path.join(__dirname, "./static")],
    compress: true,
    port: 9001
  },
  resolve: {
    extensions: [".tsx", ".ts", ".js", ".jsx"],
    modules: [path.resolve("../../node_modules"), path.resolve("./node_modules"), path.resolve("./src")]
  }
};
