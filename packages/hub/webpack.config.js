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
const envelope = require("../microeditor-envelope/webpackUtils");
const packageJson = require("./package.json");
const os = require("os");

const commonConfig = {
  mode: "development",
  devtool: "inline-source-map",
  entry: {
    index: "./src/index.tsx"
  },
  output: {
    path: path.resolve(__dirname, "./dist"),
    filename: "[name].js"
  },
  externals: {
    electron: "commonjs electron"
  },
  plugins: [
    new CopyPlugin([
      { from: "./static/resources", to: "./resources" },
      { from: "./static/images", to: "./images" },
      { from: "./static/index.html", to: "./index.html" },
      {
        from:
          "../vscode-extension-pack-kogito-kie-editors/dist/vscode_extension_kogito_kie_editors_" +
          packageJson.version +
          ".vsix",
        to: "./lib/vscode_extension.vsix"
      },
      {
        from: "../desktop/out/Business Modeler Preview-" + os.platform() + "-x64",
        to: "./lib/Business Modeler Preview-" + os.platform() + "-x64"
      }
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

module.exports = [
  {
    ...commonConfig,
    target: "electron-main",
    entry: {
      index: "./src/electron/index.ts"
    },
    node: {
      __dirname: false,
      __filename: false
    }
  },
  {
    ...commonConfig,
    target: "web",
    entry: {
      "webview/index": "./src/webview/index.tsx"
    },
    module: {
      rules: [...commonConfig.module.rules, ...envelope.patternflyLoaders]
    },
    plugins: [new CopyPlugin([{ from: "static/index.html" }])]
  }
];
