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
const { EnvironmentPlugin } = require("webpack");
const HtmlWebpackPlugin = require("html-webpack-plugin");
const HtmlReplaceWebpackPlugin = require("html-replace-webpack-plugin");

function getGtmResource(argv) {
  const gtmId = argv["KOGITO_ONLINE_EDITOR_GTM_ID"] ?? process.env["KOGITO_ONLINE_EDITOR_GTM_ID"];
  console.info("Google Tag Manager :: ID: " + gtmId);
  return {
    id: gtmId,
    header: `<!-- Google Tag Manager -->
    <script>
      (function(w,d,s,l,i){w[l]=w[l]||[];w[l].push({'gtm.start':
      new Date().getTime(),event:'gtm.js'});var f=d.getElementsByTagName(s)[0],
      j=d.createElement(s),dl=l!='dataLayer'?'&l='+l:'';j.async=true;j.src=
      'https://www.googletagmanager.com/gtm.js?id='+i+dl;f.parentNode.insertBefore(j,f);
      })(window,document,'script','dataLayer','${gtmId}');
    </script>
    <!-- End Google Tag Manager -->`,
    body: `<!-- Google Tag Manager (noscript) -->
    <noscript>
      <iframe
        src="https://www.googletagmanager.com/ns.html?id=${gtmId}"
        height="0"
        width="0"
        style="display:none;visibility:hidden"
      >
      </iframe>
    </noscript>
    <!-- End Google Tag Manager (noscript) -->`,
  };
}

function getLatestGitTag() {
  const tagName = require("child_process").execSync("git rev-list --tags --max-count=1").toString().trim();

  return require("child_process")
    .execSync("git describe --tags " + tagName)
    .toString()
    .trim();
}

function getDownloadHubArgs(argv) {
  let linuxUrl = argv["DOWNLOAD_HUB_linuxUrl"] ?? process.env["DOWNLOAD_HUB_linuxUrl"];
  let macOsUrl = argv["DOWNLOAD_HUB_macOsUrl"] ?? process.env["DOWNLOAD_HUB_macOsUrl"];
  let windowsUrl = argv["DOWNLOAD_HUB_windowsUrl"] ?? process.env["DOWNLOAD_HUB_windowsUrl"];

  linuxUrl =
    linuxUrl ??
    `https://github.com/kiegroup/kogito-tooling/releases/download/${getLatestGitTag()}/business_modeler_hub_preview_linux_${getLatestGitTag()}.zip`;
  macOsUrl =
    macOsUrl ??
    `https://github.com/kiegroup/kogito-tooling/releases/download/${getLatestGitTag()}/business_modeler_hub_preview_macos_${getLatestGitTag()}.zip`;
  windowsUrl =
    windowsUrl ??
    `https://github.com/kiegroup/kogito-tooling/releases/download/${getLatestGitTag()}/business_modeler_hub_preview_windows_${getLatestGitTag()}.zip`;

  console.info("Download Hub :: Linux URL: " + linuxUrl);
  console.info("Download Hub :: macOS URL: " + macOsUrl);
  console.info("Download Hub :: Windows URL: " + windowsUrl);

  return [linuxUrl, macOsUrl, windowsUrl];
}

function getDmnRunnerArgs(argv) {
  let linuxDownloadUrl = argv["DMN_RUNNER__linuxDownloadUrl"] || process.env["DMN_RUNNER__linuxDownloadUrl"];
  let macOsDownloadUrl = argv["DMN_RUNNER__macOsDownloadUrl"] || process.env["DMN_RUNNER__macOsDownloadUrl"];
  let windowsDownloadUrl = argv["DMN_RUNNER__windowsDownloadUrl"] || process.env["DMN_RUNNER__windowsDownloadUrl"];
  let compatibleVersion = argv["DMN_RUNNER__compatibleVersion"] || process.env["DMN_RUNNER__compatibleVersion"];

  compatibleVersion = compatibleVersion || `0.0.0`;
  macOsDownloadUrl =
    macOsDownloadUrl ||
    `https://github.com/kiegroup/kogito-tooling-go/releases/download/${compatibleVersion}/dmn_runner_macos_${compatibleVersion}.dmg`;
  windowsDownloadUrl =
    windowsDownloadUrl ||
    `https://github.com/kiegroup/kogito-tooling-go/releases/download/${compatibleVersion}/dmn_runner_windows_${compatibleVersion}.exe`;
  linuxDownloadUrl =
    linuxDownloadUrl ||
    `https://github.com/kiegroup/kogito-tooling-go/releases/download/${compatibleVersion}/dmn_runner_linux_${compatibleVersion}.tar.gz`;

  console.info("DMN Runner :: Linux download URL: " + linuxDownloadUrl);
  console.info("DMN Runner :: macOS download URL: " + macOsDownloadUrl);
  console.info("DMN Runner :: Windows download URL: " + windowsDownloadUrl);
  console.info("DMN Runner :: Compatible version: " + compatibleVersion);

  return [linuxDownloadUrl, macOsDownloadUrl, windowsDownloadUrl, compatibleVersion];
}

module.exports = async (env, argv) => {
  const [downloadHub_linuxUrl, downloadHub_macOsUrl, downloadHub_windowsUrl] = getDownloadHubArgs(argv);
  const [
    dmnRunner_linuxDownloadUrl,
    dmnRunner_macOsDownloadUrl,
    dmnRunner_windowsDownloadUrl,
    dmnRunner_compatibleVersion,
  ] = getDmnRunnerArgs(argv);
  const gtmResource = getGtmResource(argv);

  return merge(common(env, argv), {
    entry: {
      index: "./src/index.tsx",
      "bpmn-envelope": "./src/envelope/BpmnEditorEnvelopeApp.ts",
      "dmn-envelope": "./src/envelope/DmnEditorEnvelopeApp.ts",
      "pmml-envelope": "./src/envelope/PMMLEditorEnvelopeApp.ts",
    },
    plugins: [
      new HtmlWebpackPlugin({
        template: "./static/index.html",
        inject: false,
        minify: false,
      }),
      new HtmlReplaceWebpackPlugin([
        {
          pattern: /(<!-- gtm):([\w-\/]+)(\s*-->)?/g,
          replacement: (match, gtm, type) => {
            if (gtmResource.id) {
              return gtmResource[type] ?? `${match}`;
            }
            return `${match}`;
          },
        },
      ]),
      new EnvironmentPlugin({
        WEBPACK_REPLACE__hubLinuxUrl: downloadHub_linuxUrl,
        WEBPACK_REPLACE__hubMacOsUrl: downloadHub_macOsUrl,
        WEBPACK_REPLACE__hubWindowsUrl: downloadHub_windowsUrl,
        WEBPACK_REPLACE__dmnRunnerLinuxDownloadUrl: dmnRunner_linuxDownloadUrl,
        WEBPACK_REPLACE__dmnRunnerMacOsDownloadUrl: dmnRunner_macOsDownloadUrl,
        WEBPACK_REPLACE__dmnRunnerWindowsDownloadUrl: dmnRunner_windowsDownloadUrl,
        WEBPACK_REPLACE__dmnRunnerCompatibleVersion: dmnRunner_compatibleVersion,
      }),
      new CopyPlugin({
        patterns: [
          { from: "./static/resources", to: "./resources" },
          { from: "./static/images", to: "./images" },
          { from: "./static/samples", to: "./samples" },
          { from: "./static/favicon.ico", to: "./favicon.ico" },
          {
            from: externalAssets.dmnEditorPath(argv),
            to: "./gwt-editors/dmn",
            globOptions: { ignore: ["WEB-INF/**/*"] },
          },
          {
            from: externalAssets.bpmnEditorPath(argv),
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
        // `@kiegroup/monaco-editor`. This way, everything works as expected.
        "monaco-editor/esm/vs/editor/editor.api": path.resolve(__dirname, "../../node_modules/@kiegroup/monaco-editor"),
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
      port: 9001,
    },
  });
};
