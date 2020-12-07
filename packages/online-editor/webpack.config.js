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
  const tagName = require("child_process")
    .execSync("git rev-list --tags --max-count=1")
    .toString()
    .trim();

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

module.exports = async (env, argv) => {
  const [downloadHub_linuxUrl, downloadHub_macOsUrl, downloadHub_windowsUrl] = getDownloadHubArgs(argv);

  return merge(common, {
    entry: {
      index: "./src/index.tsx"
    },
    plugins: [
      new CopyPlugin([
        { from: "./static/resources", to: "./resources" },
        { from: "./static/images", to: "./images" },
        { from: "./static/samples", to: "./samples" },
        { from: "./static/index.html", to: "./index.html" },
        { from: "./static/favicon.ico", to: "./favicon.ico" },
        { from: "../../node_modules/@kogito-tooling/kie-bc-editors/dist/envelope-dist", to: "./envelope" },
        { from: externalAssets.dmnEditorPath(argv), to: "./gwt-editors/dmn" },
        { from: externalAssets.bpmnEditorPath(argv), to: "./gwt-editors/bpmn" }
      ])
    ],
    module: {
      rules: [
        {
          test: /DownloadHubModal\.tsx$/,
          loader: "string-replace-loader",
          options: {
            multiple: [
              {
                search: "$_{WEBPACK_REPLACE__hubLinuxUrl}",
                replace: downloadHub_linuxUrl
              },
              {
                search: "$_{WEBPACK_REPLACE__hubMacOsUrl}",
                replace: downloadHub_macOsUrl
              },
              {
                search: "$_{WEBPACK_REPLACE__hubWindowsUrl}",
                replace: downloadHub_windowsUrl
              }
            ]
          }
        },
        ...pfWebpackOptions.patternflyRules
      ]
    },
    devServer: {
      historyApiFallback: false,
      disableHostCheck: true,
      watchContentBase: true,
      contentBase: [path.join(__dirname, "./dist"), path.join(__dirname, "./static")],
      compress: true,
      port: 9001
    }
  });
};
