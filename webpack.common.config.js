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
const { merge } = require("webpack-merge");

module.exports = (env) => {
  console.info(`Building '${path.basename(process.cwd())}' for ${env.dev ? "development" : "production"}`);

  const transpileOnly =
    (env["WEBPACK_TS_LOADER_transpileOnly"] ?? process.env["WEBPACK_TS_LOADER_transpileOnly"] ?? "false") === "true";

  // minification is always false when in `dev`. When in `prod`, the default is true but can be overridden by env.
  const minimize = env.dev ? false : (env["WEBPACK_minimize"] ?? process.env["WEBPACK_minimize"] ?? "true") === "true";

  console.info("Webpack :: TS Loader :: transpileOnly: " + transpileOnly);
  console.info("Webpack :: minimize: " + minimize);

  return env.dev
    ? merge(common, {
        mode: "development",
        optimization: {
          minimize,
        },
        devtool: "inline-source-map",
        module: {
          rules: [
            {
              test: /\.js$/,
              enforce: "pre",
              use: ["source-map-loader"],
            },
            {
              test: /\.tsx?$/,
              loader: "ts-loader",
              options: {
                transpileOnly,
                compilerOptions: {
                  sourceMap: true,
                },
              },
            },
          ],
        },
      })
    : merge(common, {
        mode: "production",
        optimization: {
          minimize,
        },
        module: {
          rules: [
            {
              test: /\.tsx?$/,
              loader: "ts-loader",
              options: {
                transpileOnly,
                compilerOptions: {
                  sourceMap: false,
                },
              },
            },
          ],
        },
      });
};

const common = {
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
