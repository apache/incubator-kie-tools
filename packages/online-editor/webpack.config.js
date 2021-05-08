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
const pfWebpackOptions = require("@kogito-tooling/patternfly-base/patternflyWebpackOptions");
const { merge } = require("webpack-merge");
const common = require("../../webpack.common.config");
const externalAssets = require("@kogito-tooling/external-assets-base");

function getLatestGitTag() {
  const tagName = require("child_process").execSync("git rev-list --tags --max-count=1").toString().trim();

  return require("child_process")
    .execSync("git describe --tags " + tagName)
    .toString()
    .trim();
}

function getDownloadHubArgs(argv) {
  let linuxUrl = argv["DOWNLOAD_HUB_linuxUrl"] || process.env["DOWNLOAD_HUB_linuxUrl"];
  let macOsUrl = argv["DOWNLOAD_HUB_macOsUrl"] || process.env["DOWNLOAD_HUB_macOsUrl"];
  let windowsUrl = argv["DOWNLOAD_HUB_windowsUrl"] || process.env["DOWNLOAD_HUB_windowsUrl"];

  linuxUrl =
    linuxUrl ||
    `https://github.com/kiegroup/kogito-tooling/releases/download/${getLatestGitTag()}/business_modeler_hub_preview_linux_${getLatestGitTag()}.zip`;
  macOsUrl =
    macOsUrl ||
    `https://github.com/kiegroup/kogito-tooling/releases/download/${getLatestGitTag()}/business_modeler_hub_preview_macos_${getLatestGitTag()}.zip`;
  windowsUrl =
    windowsUrl ||
    `https://github.com/kiegroup/kogito-tooling/releases/download/${getLatestGitTag()}/business_modeler_hub_preview_windows_${getLatestGitTag()}.zip`;

  console.info("Download Hub :: Linux URL: " + linuxUrl);
  console.info("Download Hub :: macOS URL: " + macOsUrl);
  console.info("Download Hub :: Windows URL: " + windowsUrl);

  return [linuxUrl, macOsUrl, windowsUrl];
}

function getDmnRunnerArgs(argv) {
  let linuxUrl = argv["DMN_RUNNER__linuxUrl"] || process.env["DMN_RUNNER__linuxUrl"];
  let macOsUrl = argv["DMN_RUNNER__macOsUrl"] || process.env["DMN_RUNNER__macOsUrl"];
  let windowsUrl = argv["DMN_RUNNER__windowsUrl"] || process.env["DMN_RUNNER__windowsUrl"];
  let version = argv["DMN_RUNNER__version"] || process.env["DMN_RUNNER__version"];

  version = version || `0.0.0`;
  macOsUrl =
    macOsUrl ||
    `https://github.com/adrielparedes/kogito-local-server/releases/latest/download/dmn_runner_macos_v${version}.dmg`;
  windowsUrl =
    windowsUrl ||
    `https://github.com/adrielparedes/kogito-local-server/releases/latest/download/dmn_runner_windows_v${version}.exe`;
  linuxUrl =
    linuxUrl ||
    `https://github.com/adrielparedes/kogito-local-server/releases/latest/download/dmn_runner_linux_v${version}.tar.gz`;

  console.info("DMN Runner :: Linux URL: " + linuxUrl);
  console.info("DMN Runner :: macOs URL: " + macOsUrl);
  console.info("DMN Runner :: Windows URL: " + windowsUrl);
  console.info("DMN Runner :: Version: " + version);

  return [linuxUrl, macOsUrl, windowsUrl, version];
}

module.exports = async (env, argv) => {
  const [downloadHub_linuxUrl, downloadHub_macOsUrl, downloadHub_windowsUrl] = getDownloadHubArgs(argv);
  const [dmnRunner_linuxUrl, dmnRunner_macOsUrl, dmnRunner_windowsUrl, dmnRunner_version] = getDmnRunnerArgs(argv);

  return merge(common, {
    entry: {
      index: "./src/index.tsx",
      "bpmn-envelope": "./src/envelope/BpmnEditorEnvelopeApp.ts",
      "dmn-envelope": "./src/envelope/DmnEditorEnvelopeApp.ts",
      "pmml-envelope": "./src/envelope/PMMLEditorEnvelopeApp.ts",
    },
    plugins: [
      new CopyPlugin([
        { from: "./static/resources", to: "./resources" },
        { from: "./static/images", to: "./images" },
        { from: "./static/samples", to: "./samples" },
        { from: "./static/index.html", to: "./index.html" },
        { from: "./static/favicon.ico", to: "./favicon.ico" },
        { from: "./static/files", to: "./files" },
        { from: externalAssets.dmnEditorPath(argv), to: "./gwt-editors/dmn", ignore: ["WEB-INF/**/*"] },
        { from: externalAssets.bpmnEditorPath(argv), to: "./gwt-editors/bpmn", ignore: ["WEB-INF/**/*"] },
        { from: "./static/envelope/pmml-envelope.html", to: "./pmml-envelope.html" },
        { from: "./static/envelope/bpmn-envelope.html", to: "./bpmn-envelope.html" },
        { from: "./static/envelope/dmn-envelope.html", to: "./dmn-envelope.html" },
        { from: "../../node_modules/@kogito-tooling/pmml-editor/dist/images", to: "./images" },
      ]),
    ],
    resolve: {
      alias: {
        // `react-monaco-editor` points to the `monaco-editor` package by default, therefore doesn't use our minified
        // version. To solve that, we fool webpack, saying that every import for Monaco directly should actually point to
        // `@kiegroup/monaco-editor`. This way, everything works as expected.
        "monaco-editor/esm/vs/editor/editor.api": path.resolve(__dirname, "../../node_modules/@kiegroup/monaco-editor"),
      },
    },
    module: {
      rules: [
        {
          test: /DownloadHubModal\.tsx$/,
          loader: "string-replace-loader",
          options: {
            multiple: [
              {
                search: "$_{WEBPACK_REPLACE__hubLinuxUrl}",
                replace: downloadHub_linuxUrl,
              },
              {
                search: "$_{WEBPACK_REPLACE__hubMacOsUrl}",
                replace: downloadHub_macOsUrl,
              },
              {
                search: "$_{WEBPACK_REPLACE__hubWindowsUrl}",
                replace: downloadHub_windowsUrl,
              },
            ],
          },
        },
        {
          test: /DmnRunnerModal\.tsx$/,
          loader: "string-replace-loader",
          options: {
            multiple: [
              {
                search: "$_{WEBPACK_REPLACE__dmnRunnerLinuxUrl}",
                replace: dmnRunner_linuxUrl,
              },
              {
                search: "$_{WEBPACK_REPLACE__dmnRunnerMacOsUrl}",
                replace: dmnRunner_macOsUrl,
              },
              {
                search: "$_{WEBPACK_REPLACE__dmnRunnerWindowsUrl}",
                replace: dmnRunner_windowsUrl,
              },
            ],
          },
        },
        {
          test: /DmnRunnerContextProvider\.tsx$/,
          loader: "string-replace-loader",
          options: {
            multiple: [
              {
                search: "$_{WEBPACK_REPLACE__dmnRunnerVersion}",
                replace: dmnRunner_version,
              },
            ],
          },
        },
        ...pfWebpackOptions.patternflyRules,
      ],
    },
    devServer: {
      historyApiFallback: false,
      disableHostCheck: true,
      watchContentBase: true,
      contentBase: [path.join(__dirname, "./dist"), path.join(__dirname, "./static")],
      compress: true,
      port: 9001,
    },
  });
};
