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

import * as path from "path";
import CopyPlugin from "copy-webpack-plugin";
import { merge } from "webpack-merge";
import * as stunnerEditors from "@kie-tools/stunner-editors";
import { EnvironmentPlugin } from "webpack";

import HtmlWebpackPlugin from "html-webpack-plugin";
import { ProvidePlugin } from "webpack";
import { defaultEnvJson } from "./build/defaultEnvJson";

import common from "@kie-tools-core/webpack-base/webpack.common.config";
import patternflyBase from "@kie-tools-core/patternfly-base";
import childProcess from "child_process";

// eslint-disable-next-line @typescript-eslint/ban-ts-comment
// @ts-ignore
import HtmlReplaceWebpackPlugin from "html-replace-webpack-plugin";

// eslint-disable-next-line @typescript-eslint/ban-ts-comment
// @ts-ignore
import { env } from "./env";
const buildEnv: any = env; // build-env is not typed

export default async (env: any, argv: any) => {
  const buildInfo = getBuildInfo();
  const [
    extendedServices_linuxDownloadUrl,
    extendedServices_macOsDownloadUrl,
    extendedServices_windowsDownloadUrl,
    extendedServices_compatibleVersion,
  ] = getExtendedServicesArgs();
  const dmnDevDeployment_imagePullPolicy = getDmnDevDeploymentImagePullPolicy();
  const gtmResource = getGtmResource();

  let lastCommitHash = "";
  try {
    lastCommitHash = childProcess.execSync("git rev-parse --short HEAD").toString().trim();
    JSON.stringify(lastCommitHash);
  } catch (e) {
    throw new Error(e);
  }

  return [
    merge(common(env), {
      entry: {
        "workspace/worker/sharedWorker": "./src/workspace/worker/sharedWorker.ts",
      },
      target: "webworker",
      plugins: [
        new ProvidePlugin({
          Buffer: ["buffer", "Buffer"],
        }),
        new CopyPlugin({
          patterns: [
            {
              from: path.join(path.dirname(require.resolve("@kie-tools/emscripten-fs/package.json")), "/dist"),
              to: "workspace/worker",
            },
          ],
        }),
      ],
    }),
    {
      ...merge(common(env), {
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
              pattern: /(<!-- gtm):([\w-/]+)(\s*-->)?/g,
              replacement: (match: any, gtm: any, type: keyof typeof gtmResource) => gtmResource?.[type] ?? `${match}`,
            },
          ]),
          new EnvironmentPlugin({
            WEBPACK_REPLACE__commitHash: lastCommitHash,
            WEBPACK_REPLACE__buildInfo: buildInfo,
            WEBPACK_REPLACE__extendedServicesLinuxDownloadUrl: extendedServices_linuxDownloadUrl,
            WEBPACK_REPLACE__extendedServicesMacOsDownloadUrl: extendedServices_macOsDownloadUrl,
            WEBPACK_REPLACE__extendedServicesWindowsDownloadUrl: extendedServices_windowsDownloadUrl,
            WEBPACK_REPLACE__extendedServicesCompatibleVersion: extendedServices_compatibleVersion,
            WEBPACK_REPLACE__dmnDevDeployment_imagePullPolicy: dmnDevDeployment_imagePullPolicy,
            WEBPACK_REPLACE__quarkusPlatformVersion: buildEnv.quarkusPlatform.version,
            WEBPACK_REPLACE__kogitoRuntimeVersion: buildEnv.kogitoRuntime.version,
          }),
          new CopyPlugin({
            patterns: [
              { from: "./static/resources", to: "./resources" },
              { from: "./static/images", to: "./images" },
              { from: "./static/samples", to: "./samples" },
              { from: "./static/kubernetes", to: "./kubernetes" },
              { from: "./static/favicon.svg", to: "./favicon.svg" },
              {
                from: "./static/env.json",
                to: "./env.json",
                transform: () => JSON.stringify(defaultEnvJson, null, 2),
              },
              {
                from: stunnerEditors.dmnEditorPath(),
                to: "./gwt-editors/dmn",
                globOptions: { ignore: ["**/WEB-INF/**/*", "**/*.html"] },
              },
              {
                from: stunnerEditors.bpmnEditorPath(),
                to: "./gwt-editors/bpmn",
                globOptions: { ignore: ["**/WEB-INF/**/*", "**/*.html"] },
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
            process: require.resolve("process/browser.js"),
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
        ignoreWarnings: [
          {
            // The @kubernetes-models sub-packages source maps are not published, so we need to ignore their warnings for now.
            module: /@kubernetes-models/,
          },
        ],
      }),
      devServer: {
        https: true,
        historyApiFallback: false,
        static: [{ directory: path.join(__dirname, "./dist") }, { directory: path.join(__dirname, "./static") }],
        compress: true,
        port: buildEnv.onlineEditor.dev.port,
      },
    },
  ];
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

function getExtendedServicesArgs() {
  const linuxDownloadUrl = buildEnv.onlineEditor.extendedServices.downloadUrl.linux;
  const macOsDownloadUrl = buildEnv.onlineEditor.extendedServices.downloadUrl.macOs;
  const windowsDownloadUrl = buildEnv.onlineEditor.extendedServices.downloadUrl.windows;
  const compatibleVersion = buildEnv.onlineEditor.extendedServices.compatibleVersion;

  console.info("Extended Services :: Linux download URL: " + linuxDownloadUrl);
  console.info("Extended Services :: macOS download URL: " + macOsDownloadUrl);
  console.info("Extended Services :: Windows download URL: " + windowsDownloadUrl);
  console.info("Extended Services :: Compatible version: " + compatibleVersion);

  return [linuxDownloadUrl, macOsDownloadUrl, windowsDownloadUrl, compatibleVersion];
}

function getDmnDevDeploymentImagePullPolicy() {
  const baseImagePullPolicy = buildEnv.devDeployments.dmn.imagePullPolicy;
  console.info("DMN Dev deployment :: Image pull policy: " + baseImagePullPolicy);
  return baseImagePullPolicy;
}
