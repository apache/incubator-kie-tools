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

import CopyPlugin from "copy-webpack-plugin";
import patternflyBase from "@kie-tools-core/patternfly-base";
import { merge } from "webpack-merge";
import common from "@kie-tools-core/webpack-base/webpack.common.config";
import HtmlWebpackPlugin from "html-webpack-plugin";
import NodePolyfillPlugin from "node-polyfill-webpack-plugin";
import { defaultEnvJson } from "./build/defaultEnvJson";
import path from "path";
// Resolved through webpack-cli's own module scope so plugins built here come from the exact same
// webpack instance the CLI uses to run the Compiler. pnpm can install more than one physical copy
// of the same webpack version split by peer-dependency signature, and webpack's
// internal `instanceof` checks throw when the plugin and the Compiler differ.
// eslint-disable-next-line @typescript-eslint/no-require-imports
const { ProvidePlugin } = require(
  require.resolve("webpack", { paths: [path.dirname(require.resolve("webpack-cli/package.json"))] })
);

// eslint-disable-next-line @typescript-eslint/ban-ts-comment
// @ts-ignore
import { env } from "./env";
const buildEnv: any = env; // build-env is not typed

export default async (webpackEnv: any) => {
  // Detect if webpack-dev-server is running (not just dev mode)
  // WEBPACK_SERVE is set by webpack-dev-server, not by regular webpack builds
  const isDevServer = process.env.WEBPACK_SERVE === "true";

  return {
    ...merge(common(webpackEnv), {
      entry: {
        index: "./src/index.tsx",
      },
      plugins: [
        new HtmlWebpackPlugin({
          template: "./static/index.html",
          inject: false,
          minify: false,
        }),
        new CopyPlugin({
          patterns: [
            { from: "./static/resources", to: "./resources" },
            { from: "./static/images", to: "./images" },
            { from: "./static/favicon.svg", to: "./favicon.svg" },
            ...(isDevServer
              ? []
              : [
                  {
                    from: "./static/env.json",
                    to: "./env.json",
                    transform: () => JSON.stringify(defaultEnvJson, null, 2),
                  },
                ]),
          ],
        }),
        new ProvidePlugin({
          process: require.resolve("process/browser.js"),
          Buffer: ["buffer", "Buffer"],
        }),
        new NodePolyfillPlugin(),
      ],
      ignoreWarnings: [
        {
          // The @apidevtools sub-packages source maps are not published, so we need to ignore their warnings for now.
          module: /@apidevtools/,
        },
        {
          // The @jsdevtools sub-packages source maps are not published, so we need to ignore their warnings for now.
          module: /@jsdevtools/,
        },
      ],
      module: {
        rules: [...patternflyBase.webpackModuleRules],
      },
      watchOptions: {
        poll: 1000,
      },
    }),
    devServer: {
      server: "http",
      host: "localhost",
      port: buildEnv.devDeploymentDmnFormWebapp.dev.webpackPort,
      historyApiFallback: false,
      static: [{ directory: path.join(__dirname, "./dist") }, { directory: path.join(__dirname, "./static") }],
      compress: true,
      client: {
        overlay: false,
      },
      allowedHosts: "all",
    },
  };
};
