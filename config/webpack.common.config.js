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
const buildEnv = require("@kogito-tooling/build-env");

module.exports = (env) => {
  const transpileOnly = buildEnv.global.webpack(env).transpileOnly;
  const minimize = buildEnv.global.webpack(env).minimize;
  const sourceMaps = buildEnv.global.webpack(env).sourceMaps;
  const mode = buildEnv.global.webpack(env).mode;
  const live = buildEnv.global.webpack(env).live;

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
          use: ["source-map-loader"],
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
          loader: path.resolve(path.join(__dirname, "./multi-package-live-reload-loader.js")),
        },
      ]
    : [];

  const importsNotUsedAsValues = live ? { importsNotUsedAsValues: "preserve" } : {};

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
          test: /\.tsx?$/,
          use: [
            {
              loader: "ts-loader",
              options: {
                transpileOnly,
                compilerOptions: {
                  ...importsNotUsedAsValues,
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
      fallback: { path: require.resolve("path-browserify") }, // Required for `minimatch`, as Webpack 5 doesn't add polyfills automatically anymore.
      extensions: [".tsx", ".ts", ".js", ".jsx"],
      modules: [path.resolve("../../node_modules"), path.resolve("./node_modules"), path.resolve("./src")],
    },
  };
};
