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

const { merge } = require("webpack-merge");
const { EnvironmentPlugin } = require("webpack");
const { env } = require("./env");
const common = require("@kie-tools-core/webpack-base/webpack.common.config");

const commonConfig = (webpackEnv) =>
  merge(common(webpackEnv), {
    output: {
      library: "ExtendedServices",
      libraryTarget: "umd",
      umdNamedDefine: true,
      globalObject: "this",
    },
    externals: {
      vscode: "commonjs vscode",
      plugins: [],
    },
  });

// eslint-disable-next-line @typescript-eslint/ban-ts-comment
// @ts-ignore
const buildEnv = env; // build-env is not typed

module.exports = async (webpackEnv) => {
  const [extendedServices_urlHost, extendedServices_urlPort] = getExtendedServicesArgs();

  return [
    merge(commonConfig(webpackEnv), {
      target: "node",
      entry: {
        "extension/extension-main": "./src/extension/extension-main.ts",
      },
      plugins: [
        new EnvironmentPlugin({
          WEBPACK_REPLACE__extendedServicesUrlHost: extendedServices_urlHost,
          WEBPACK_REPLACE__extendedServicesUrlPort: extendedServices_urlPort,
        }),
      ],
    }),
    merge(commonConfig(webpackEnv), {
      target: "web",
      entry: {
        "extension/extension-browser": "./src/extension/extension-browser.ts",
      },
      plugins: [
        new EnvironmentPlugin({
          WEBPACK_REPLACE__extendedServicesUrlHost: extendedServices_urlHost,
          WEBPACK_REPLACE__extendedServicesUrlPort: extendedServices_urlPort,
        }),
      ],
    }),
  ];
};

function getExtendedServicesArgs() {
  const extendedServicesJavaHost = buildEnv.extendedServicesJava.host;
  const extendedServicesJavaPort = buildEnv.extendedServicesJava.port;

  console.info("Extended Services :: URL HOST " + extendedServicesJavaHost);
  console.info("Extended Services :: URL PORT: " + extendedServicesJavaPort);

  return [extendedServicesJavaHost, extendedServicesJavaPort];
}
