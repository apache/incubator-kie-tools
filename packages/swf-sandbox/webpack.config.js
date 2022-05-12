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
const HtmlWebpackPlugin = require("html-webpack-plugin");
const { ProvidePlugin } = require("webpack");
const buildEnv = require("@kie-tools/build-env");
const { EnvironmentPlugin } = require("webpack");
const HtmlReplaceWebpackPlugin = require("html-replace-webpack-plugin");
const MonacoWebpackPlugin = require("monaco-editor-webpack-plugin");

module.exports = async (env) => {
  const buildInfo = getBuildInfo();
  const gtmResource = getGtmResource();
  const [swfSandbox_baseImageRegistry, swfSandbox_baseImageAccount, swfSandbox_baseImageName, swfSandbox_baseImageTag] =
    getSwfSandboxBaseImageArgs();
  const [
    kieSandboxExtendedServices_linuxDownloadUrl,
    kieSandboxExtendedServices_macOsDownloadUrl,
    kieSandboxExtendedServices_windowsDownloadUrl,
    kieSandboxExtendedServices_compatibleVersion,
  ] = getKieSandboxExtendedServicesArgs();

  return merge(common(env), {
    entry: {
      index: "./src/index.tsx",
      "swf-editor-envelope": "./src/envelope/SwfEditorEnvelopeApp.ts",
      "dashbuilder-editor-envelope": "./src/envelope/DashbuilderEditorEnvelopeApp.ts",
      "text-editor-envelope": "./src/envelope/TextEditorEnvelopeApp.ts",
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
        WEBPACK_REPLACE__swfSandbox_baseImageFullUrl: `${swfSandbox_baseImageRegistry}/${swfSandbox_baseImageAccount}/${swfSandbox_baseImageName}:${swfSandbox_baseImageTag}`,
        WEBPACK_REPLACE__kieSandboxExtendedServicesLinuxDownloadUrl: kieSandboxExtendedServices_linuxDownloadUrl,
        WEBPACK_REPLACE__kieSandboxExtendedServicesMacOsDownloadUrl: kieSandboxExtendedServices_macOsDownloadUrl,
        WEBPACK_REPLACE__kieSandboxExtendedServicesWindowsDownloadUrl: kieSandboxExtendedServices_windowsDownloadUrl,
        WEBPACK_REPLACE__kieSandboxExtendedServicesCompatibleVersion: kieSandboxExtendedServices_compatibleVersion,
        WEBPACK_REPLACE__quarkusPlatformVersion: buildEnv.quarkusPlatform.version,
        WEBPACK_REPLACE__kogitoRuntimeVersion: buildEnv.kogitoRuntime.version,
      }),
      new CopyPlugin({
        patterns: [
          { from: "./static/resources", to: "./resources" },
          { from: "./static/images", to: "./images" },
          { from: "./static/samples", to: "./samples" },
          { from: "./static/envelope/swf-editor-envelope.html", to: "./swf-editor-envelope.html" },
          { from: "./static/envelope/dashbuilder-editor-envelope.html", to: "./dashbuilder-editor-envelope.html" },
          { from: "./static/envelope/text-editor-envelope.html", to: "./text-editor-envelope.html" },
          { from: "./static/favicon.svg", to: "./favicon.svg" },
          { from: "./static/env.json", to: "./env.json" },
          // dashbuilder bundle
          { from: "../dashbuilder-editor/dist/dashbuilder-runtime-client", to: "./dashbuilder-runtime-client" },
        ],
      }),
      new ProvidePlugin({
        Buffer: ["buffer", "Buffer"],
      }),
      new MonacoWebpackPlugin({
        languages: ["json"],
        customLanguages: [
          {
            label: "yaml",
            entry: ["monaco-yaml", "vs/basic-languages/yaml/yaml.contribution"],
            worker: {
              id: "monaco-yaml/yamlWorker",
              entry: "monaco-yaml/lib/esm/yaml.worker",
            },
          },
        ],
      }),
    ],
    module: {
      rules: [
        {
          test: /\.svg$/,
          use: [
            {
              loader: require.resolve("@svgr/webpack"),
              options: {
                prettier: false,
                svgo: false,
                svgoConfig: {
                  plugins: [{ removeViewBox: false }],
                },
                titleProp: true,
                ref: true,
              },
            },
          ],
        },
        ...patternflyBase.webpackModuleRules,
      ],
    },
    resolve: {
      alias: {
        react: path.resolve(__dirname, "./node_modules/react"),
      },
    },
    ignoreWarnings: [/Failed to parse source map/],
    devServer: {
      server: "https",
      historyApiFallback: false,
      static: [{ directory: path.join(__dirname, "./dist") }, { directory: path.join(__dirname, "./static") }],
      compress: true,
      port: buildEnv.swfSandbox.dev.port,
    },
  });
};

function getSwfSandboxBaseImageArgs() {
  const baseImageRegistry = buildEnv.swfSandbox.baseImage.registry;
  const baseImageAccount = buildEnv.swfSandbox.baseImage.account;
  const baseImageName = buildEnv.swfSandbox.baseImage.name;
  const baseImageTag = buildEnv.swfSandbox.baseImage.tag;

  console.info("SWF Sandbox :: Base Image Registry: " + baseImageRegistry);
  console.info("SWF Sandbox :: Base Image Account: " + baseImageAccount);
  console.info("SWF Sandbox :: Base Image Name: " + baseImageName);
  console.info("SWF Sandbox :: Base Image Tag: " + baseImageTag);

  return [baseImageRegistry, baseImageAccount, baseImageName, baseImageTag];
}

function getGtmResource() {
  const gtmId = buildEnv.swfSandbox.gtmId;
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
  console.info(`SWF Sandbox :: Build info: ${buildInfo}`);
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
