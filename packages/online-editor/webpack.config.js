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
const patternflyBase = require("@kie-tools-core/patternfly-base");
const { merge } = require("webpack-merge");
const common = require("@kie-tools-core/webpack-base/webpack.common.config");
const stunnerEditors = require("@kie-tools/stunner-editors");
const { EnvironmentPlugin } = require("webpack");
const buildEnv = require("@kie-tools/build-env");
const HtmlWebpackPlugin = require("html-webpack-plugin");
const HtmlReplaceWebpackPlugin = require("html-replace-webpack-plugin");
const { ProvidePlugin } = require("webpack");

module.exports = async (env, argv) => {
  const buildInfo = getBuildInfo();
  const [
    kieSandboxExtendedServices_linuxDownloadUrl,
    kieSandboxExtendedServices_macOsDownloadUrl,
    kieSandboxExtendedServices_windowsDownloadUrl,
    kieSandboxExtendedServices_compatibleVersion,
  ] = getKieSandboxExtendedServicesArgs(argv);
  const [
    dmnDevSandbox_baseImageRegistry,
    dmnDevSandbox_baseImageAccount,
    dmnDevSandbox_baseImageName,
    dmnDevSandbox_baseImageTag,
    dmnDevSandbox_onlineEditorUrl,
  ] = getDmnDevSandboxArgs(argv);
  const gtmResource = getGtmResource(argv);

  return merge(common(env), {
    entry: {
      index: "./src/index.tsx",
      "bpmn-envelope": "./src/envelope/BpmnEditorEnvelopeApp.ts",
      "dmn-envelope": "./src/envelope/DmnEditorEnvelopeApp.ts",
      "pmml-envelope": "./src/envelope/PMMLEditorEnvelopeApp.ts",
      "broadcast-channel-single-tab-polyfill": "./src/polyfill/BroadcastChannelSingleTab.ts",
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
        WEBPACK_REPLACE__buildInfo: buildInfo,
        WEBPACK_REPLACE__kieSandboxExtendedServicesLinuxDownloadUrl: kieSandboxExtendedServices_linuxDownloadUrl,
        WEBPACK_REPLACE__kieSandboxExtendedServicesMacOsDownloadUrl: kieSandboxExtendedServices_macOsDownloadUrl,
        WEBPACK_REPLACE__kieSandboxExtendedServicesWindowsDownloadUrl: kieSandboxExtendedServices_windowsDownloadUrl,
        WEBPACK_REPLACE__kieSandboxExtendedServicesCompatibleVersion: kieSandboxExtendedServices_compatibleVersion,
        WEBPACK_REPLACE__dmnDevSandbox_baseImageFullUrl: `${dmnDevSandbox_baseImageRegistry}/${dmnDevSandbox_baseImageAccount}/${dmnDevSandbox_baseImageName}:${dmnDevSandbox_baseImageTag}`,
        WEBPACK_REPLACE__dmnDevSandbox_onlineEditorUrl: dmnDevSandbox_onlineEditorUrl,
        WEBPACK_REPLACE__quarkusPlatformVersion: buildEnv.quarkusPlatform.version,
        WEBPACK_REPLACE__kogitoRuntimeVersion: buildEnv.kogitoRuntime.version,
      }),
      new CopyPlugin({
        patterns: [
          { from: "./static/resources", to: "./resources" },
          { from: "./static/images", to: "./images" },
          { from: "./static/samples", to: "./samples" },
          { from: "./static/favicon.svg", to: "./favicon.svg" },
          { from: "./static/env.json", to: "./env.json" },
          {
            from: stunnerEditors.dmnEditorPath(),
            to: "./gwt-editors/dmn",
            globOptions: { ignore: ["WEB-INF/**/*"] },
          },
          {
            from: stunnerEditors.bpmnEditorPath(),
            to: "./gwt-editors/bpmn",
            globOptions: { ignore: ["WEB-INF/**/*"] },
          },
          { from: "./static/envelope/pmml-envelope.html", to: "./pmml-envelope.html" },
          { from: "./static/envelope/bpmn-envelope.html", to: "./bpmn-envelope.html" },
          { from: "./static/envelope/dmn-envelope.html", to: "./dmn-envelope.html" },
          {
            from: path.join(path.dirname(require.resolve("@kie-tools/pmml-editor/package.json")), "/static/images"),
            to: "./images",
          },
        ],
      }),
      new ProvidePlugin({
        Buffer: ["buffer", "Buffer"],
      }),
    ],
    resolve: {
      alias: {
        // `react-monaco-editor` points to the `monaco-editor` package by default, therefore doesn't use our minified
        // version. To solve that, we fool webpack, saying that every import for Monaco directly should actually point to
        // `@kie-tools-core/monaco-editor`. This way, everything works as expected.
        "monaco-editor/esm/vs/editor/editor.api": require.resolve("@kie-tools-core/monaco-editor"),
      },
    },
    module: {
      rules: [...patternflyBase.webpackModuleRules],
    },
    devServer: {
      https: true,
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

function getBuildInfo() {
  const buildInfo = buildEnv.onlineEditor.buildInfo;
  console.info(`Online Editor :: Build info: ${buildInfo}`);
  return buildInfo;
}

function getKieSandboxExtendedServicesArgs() {
  const linuxDownloadUrl = buildEnv.onlineEditor.kieSandboxExtendedServices.downloadUrl.linux;
  const macOsDownloadUrl = buildEnv.onlineEditor.kieSandboxExtendedServices.downloadUrl.macOs;
  const windowsDownloadUrl = buildEnv.onlineEditor.kieSandboxExtendedServices.downloadUrl.windows;
  const compatibleVersion = buildEnv.onlineEditor.kieSandboxExtendedServices.compatibleVersion;

  console.info("KIE Sandbox Extended Services :: Linux download URL: " + linuxDownloadUrl);
  console.info("KIE Sandbox Extended Services :: macOS download URL: " + macOsDownloadUrl);
  console.info("KIE Sandbox Extended Services :: Windows download URL: " + windowsDownloadUrl);
  console.info("KIE Sandbox Extended Services :: Compatible version: " + compatibleVersion);

  return [linuxDownloadUrl, macOsDownloadUrl, windowsDownloadUrl, compatibleVersion];
}

function getDmnDevSandboxArgs(argv) {
  const baseImageRegistry = buildEnv.dmnDevSandbox.baseImage.registry;
  const baseImageAccount = buildEnv.dmnDevSandbox.baseImage.account;
  const baseImageName = buildEnv.dmnDevSandbox.baseImage.name;
  const baseImageTag = buildEnv.dmnDevSandbox.baseImage.tag;
  const onlineEditorUrl = buildEnv.dmnDevSandbox.onlineEditorUrl;

  console.info("DMN Dev Sandbox :: Base Image Registry: " + baseImageRegistry);
  console.info("DMN Dev Sandbox :: Base Image Account: " + baseImageAccount);
  console.info("DMN Dev Sandbox :: Base Image Name: " + baseImageName);
  console.info("DMN Dev Sandbox :: Base Image Tag: " + baseImageTag);
  console.info("DMN Dev Sandbox :: Online Editor Url: " + onlineEditorUrl);

  return [baseImageRegistry, baseImageAccount, baseImageName, baseImageTag, onlineEditorUrl];
}
