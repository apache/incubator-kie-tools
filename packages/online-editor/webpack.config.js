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
const patternflyBase = require("@kie-tooling-core/patternfly-base");
const { merge } = require("webpack-merge");
const common = require("../../config/webpack.common.config");
const externalAssets = require("@kogito-tooling/external-assets-base");
const { EnvironmentPlugin } = require("webpack");
const buildEnv = require("@kogito-tooling/build-env");
const HtmlWebpackPlugin = require("html-webpack-plugin");
const HtmlReplaceWebpackPlugin = require("html-replace-webpack-plugin");

module.exports = async (env, argv) => {
  const [downloadHub_linuxUrl, downloadHub_macOsUrl, downloadHub_windowsUrl] = getDownloadHubArgs();
  const buildInfo = getBuildInfo();
  const [
    kieToolingExtendedServices_linuxDownloadUrl,
    kieToolingExtendedServices_macOsDownloadUrl,
    kieToolingExtendedServices_windowsDownloadUrl,
    kieToolingExtendedServices_compatibleVersion,
  ] = getKieToolingExtendedServicesArgs(argv);

  const gtmResource = getGtmResource(argv);

  return merge(common(env), {
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
            if (gtmResource) {
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
        WEBPACK_REPLACE__buildInfo: buildInfo,
        WEBPACK_REPLACE__dmnRunnerLinuxDownloadUrl: kieToolingExtendedServices_linuxDownloadUrl,
        WEBPACK_REPLACE__dmnRunnerMacOsDownloadUrl: kieToolingExtendedServices_macOsDownloadUrl,
        WEBPACK_REPLACE__dmnRunnerWindowsDownloadUrl: kieToolingExtendedServices_windowsDownloadUrl,
        WEBPACK_REPLACE__dmnRunnerCompatibleVersion: kieToolingExtendedServices_compatibleVersion,
      }),
      new CopyPlugin({
        patterns: [
          { from: "./static/resources", to: "./resources" },
          { from: "./static/images", to: "./images" },
          { from: "./static/samples", to: "./samples" },
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
          {
            from: path.join(
              path.dirname(require.resolve("@kogito-tooling/pmml-editor/package.json")),
              "/static/images"
            ),
            to: "./images",
          },
        ],
      }),
    ],
    resolve: {
      alias: {
        // `react-monaco-editor` points to the `monaco-editor` package by default, therefore doesn't use our minified
        // version. To solve that, we fool webpack, saying that every import for Monaco directly should actually point to
        // `@kie-tooling-core/monaco-editor`. This way, everything works as expected.
        "monaco-editor/esm/vs/editor/editor.api": require.resolve("@kie-tooling-core/monaco-editor"),
      },
    },
    module: {
      rules: [...patternflyBase.webpackModuleRules],
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

function getGtmResource() {
  const gtmId = buildEnv.onlineEditor.gtmId;
  console.info(`Google Tag Manager :: ID: ${gtmId}`);

  if (!gtmId) {
    return undefined;
  }

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

function getDownloadHubArgs() {
  const linuxUrl = buildEnv.onlineEditor.downloadHubUrl.linux;
  const macOsUrl = buildEnv.onlineEditor.downloadHubUrl.macOs;
  const windowsUrl = buildEnv.onlineEditor.downloadHubUrl.windows;

  console.info(`Online Editor :: Download Hub URL (Linux): ${linuxUrl}`);
  console.info(`Online Editor :: Download Hub URL (macOS): ${macOsUrl}`);
  console.info(`Online Editor :: Download Hub URL (Windows): ${windowsUrl}`);

  return [linuxUrl, macOsUrl, windowsUrl];
}

function getBuildInfo() {
  const buildInfo = buildEnv.onlineEditor.buildInfo;
  console.info(`Online Editor :: Build info: ${buildInfo}`);
  return buildInfo;
}

function getKieToolingExtendedServicesArgs() {
  const linuxDownloadUrl = buildEnv.onlineEditor.kieToolingExtendedServices.downloadUrl.linux;
  const macOsDownloadUrl = buildEnv.onlineEditor.kieToolingExtendedServices.downloadUrl.macOs;
  const windowsDownloadUrl = buildEnv.onlineEditor.kieToolingExtendedServices.downloadUrl.windows;
  const compatibleVersion = buildEnv.onlineEditor.kieToolingExtendedServices.compatibleVersion;

  console.info("KIE Tooling Extended Services :: Linux download URL: " + linuxDownloadUrl);
  console.info("KIE Tooling Extended Services :: macOS download URL: " + macOsDownloadUrl);
  console.info("KIE Tooling Extended Services :: Windows download URL: " + windowsDownloadUrl);
  console.info("KIE Tooling Extended Services :: Compatible version: " + compatibleVersion);

  return [linuxDownloadUrl, macOsDownloadUrl, windowsDownloadUrl, compatibleVersion];
}
