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
const pfWebpackOptions = require("@kie-tooling-core/patternfly-base/patternflyWebpackOptions");
const { merge } = require("webpack-merge");
const common = require("../../webpack.common.config");
const externalAssets = require("@kogito-tooling/external-assets-base");
const { EnvironmentPlugin } = require("webpack");
const buildEnv = require("@kogito-tooling/build-env");

function getDownloadHubArgs() {
  const linuxUrl = buildEnv.onlineEditor.downloadHubUrl.linux;
  const macOsUrl = buildEnv.onlineEditor.downloadHubUrl.macOs;
  const windowsUrl = buildEnv.onlineEditor.downloadHubUrl.windows;

  console.info(`Online Editor :: Download Hub URL (Linux): ${linuxUrl}`);
  console.info(`Online Editor :: Download Hub URL (macOS): ${macOsUrl}`);
  console.info(`Online Editor :: Download Hub URL (Windows): ${windowsUrl}`);

  return [linuxUrl, macOsUrl, windowsUrl];
}

module.exports = async (env) => {
  const [downloadHub_linuxUrl, downloadHub_macOsUrl, downloadHub_windowsUrl] = getDownloadHubArgs();

  return merge(common(env), {
    entry: {
      index: "./src/index.tsx",
      "bpmn-envelope": "./src/envelope/BpmnEditorEnvelopeApp.ts",
      "dmn-envelope": "./src/envelope/DmnEditorEnvelopeApp.ts",
      "pmml-envelope": "./src/envelope/PMMLEditorEnvelopeApp.ts",
    },
    plugins: [
      new EnvironmentPlugin({
        WEBPACK_REPLACE__hubLinuxUrl: downloadHub_linuxUrl,
        WEBPACK_REPLACE__hubMacOsUrl: downloadHub_macOsUrl,
        WEBPACK_REPLACE__hubWindowsUrl: downloadHub_windowsUrl,
      }),
      new CopyPlugin({
        patterns: [
          { from: "./static/resources", to: "./resources" },
          { from: "./static/images", to: "./images" },
          { from: "./static/samples", to: "./samples" },
          { from: "./static/index.html", to: "./index.html" },
          { from: "./static/favicon.ico", to: "./favicon.ico" },
          {
            from: externalAssets.dmnEditorPath(),
            to: "./gwt-editors/dmn",
            globOptions: { ignore: ["WEB-INF/**/*"] },
          },
          {
            from: externalAssets.bpmnEditorPath(),
            to: "./gwt-editors/bpmn",
            globOptions: { ignore: ["WEB-INF/**/*"] },
          },
          { from: "./static/envelope/pmml-envelope.html", to: "./pmml-envelope.html" },
          { from: "./static/envelope/bpmn-envelope.html", to: "./bpmn-envelope.html" },
          { from: "./static/envelope/dmn-envelope.html", to: "./dmn-envelope.html" },
          { from: "../../node_modules/@kogito-tooling/pmml-editor/dist/images", to: "./images" },
        ],
      }),
    ],
    resolve: {
      alias: {
        // `react-monaco-editor` points to the `monaco-editor` package by default, therefore doesn't use our minified
        // version. To solve that, we fool webpack, saying that every import for Monaco directly should actually point to
        // `@kie-tooling-core/monaco-editor`. This way, everything works as expected.
        "monaco-editor/esm/vs/editor/editor.api": path.resolve(
          __dirname,
          "../../node_modules/@kie-tooling-core/monaco-editor"
        ),
      },
    },
    module: {
      rules: [...pfWebpackOptions.patternflyRules],
    },
    devServer: {
      historyApiFallback: false,
      disableHostCheck: true,
      watchContentBase: true,
      contentBase: [path.join(__dirname, "./dist"), path.join(__dirname, "./static")],
      compress: true,
      port: buildEnv.onlineEditor.dev.port,
    },
  });
};
