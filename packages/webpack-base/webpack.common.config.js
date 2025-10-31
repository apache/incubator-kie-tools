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
const webpackBaseEnv = require("./env");

module.exports = (webpackEnv) => {
  const { transpileOnly, minimize, sourceMaps, mode } = webpackEnv.dev
    ? webpackBaseEnv.env.webpack.dev
    : webpackBaseEnv.env.webpack.prod;

  const live = webpackEnv.live;

  console.info(`Webpack :: ts-loader :: transpileOnly: ${transpileOnly}`);
  console.info(`Webpack :: minimize: ${minimize}`);
  console.info(`Webpack :: sourceMaps: ${sourceMaps}`);
  console.info(`Webpack :: mode: ${mode}`);
  console.info(`Webpack :: live: ${live}`);

  const sourceMapsLoader = sourceMaps
    ? [
        {
          test: /\.js$/,
          enforce: "pre",
          use: [require.resolve("source-map-loader")],
        },
      ]
    : [];

  const devtool = sourceMaps
    ? {
        devtool: "inline-source-map",
      }
    : {};

  const multiPackageLiveReloadLoader = live
    ? [
        {
          loader: require.resolve("./multi-package-live-reload-loader.js"),
        },
      ]
    : [];

  // importsNotUsedAsValues was deprecated, verbatimModuleSyntax replaces it
  // see https://www.typescriptlang.org/tsconfig/#importsNotUsedAsValues
  const verbatimModuleSyntax = live ? { verbatimModuleSyntax: "preserve" } : {};

  return {
    mode,
    optimization: {
      minimize,
    },
    ...devtool,
    module: {
      rules: [
        ...sourceMapsLoader,
        {
          test: /\.m?js$/,
          resolve: {
            fullySpecified: false,
          },
        },
        {
          test: /\.tsx?$/,
          use: [
            {
              loader: require.resolve("ts-loader"),
              options: {
                transpileOnly,
                compilerOptions: {
                  ...verbatimModuleSyntax,
                  sourceMap: sourceMaps,
                },
              },
            },
            ...multiPackageLiveReloadLoader,
          ],
        },
      ],
    },
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
      // Required for github.dev and `minimatch`, as Webpack 5 doesn't add polyfills automatically anymore.
      fallback: {
        path: require.resolve("path-browserify"),
        os: require.resolve("os-browserify/browser"),
        fs: false,
        child_process: false,
        net: false,
        buffer: require.resolve("buffer/"),
        stream: require.resolve("stream-browserify"),
        querystring: require.resolve("querystring-es3"),
      },
      extensions: [".tsx", ".ts", ".js", ".jsx"],
      modules: ["node_modules"],
    },
  };
};
