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

import patternflyBase from "@kie-tools-core/patternfly-base";
import common from "@kie-tools-core/webpack-base/webpack.common.config";
import * as swEditorAssets from "@kie-tools/serverless-workflow-diagram-editor-assets";
import CopyPlugin from "copy-webpack-plugin";
import HtmlWebpackPlugin from "html-webpack-plugin";
import MonacoWebpackPlugin from "monaco-editor-webpack-plugin";
import NodePolyfillPlugin from "node-polyfill-webpack-plugin";
import * as path from "path";
import { EnvironmentPlugin, ProvidePlugin } from "webpack";
import { merge } from "webpack-merge";
import { defaultEnvJson } from "./build/defaultEnvJson";

// eslint-disable-next-line @typescript-eslint/ban-ts-comment
// @ts-ignore
import HtmlReplaceWebpackPlugin from "html-replace-webpack-plugin";

// eslint-disable-next-line @typescript-eslint/ban-ts-comment
// @ts-ignore
import { env } from "./env";
const buildEnv: any = env; // build-env is not typed

export default async (webpackEnv: any, webpackArgv: any) => {
  const buildInfo = getBuildInfo();
  const gtmResource = getGtmResource();
  const [swfBuilderImageRegistry, swfBuilderImageAccount, swfBuilderImageName, swfBuilderImageTag] =
    getSwfBuilderImageArgs();
  const [baseBuilderImageRegistry, baseBuilderImageAccount, baseBuilderImageName, baseBuilderImageTag] =
    getBaseBuilderImageArgs();
  const [swfDevModeImageRegistry, swfDevModeImageAccount, swfDevModeImageName, swfDevModeImageTag] =
    getSwfDevModeImageArgs();
  const [
    dashbuilderViewerImageRegistry,
    dashbuilderViewerImageAccount,
    dashbuilderViewerImageName,
    dashbuilderViewerImageTag,
  ] = getDashbuilderViewerImageArgs();

  return [
    merge(common(webpackEnv), {
      entry: {
        "workspace/worker/sharedWorker": "./src/workspace/worker/sharedWorker.ts",
      },
      target: "webworker",
      plugins: [
        new ProvidePlugin({
          process: require.resolve("process/browser.js"),
          Buffer: ["buffer", "Buffer"],
        }),
        new EnvironmentPlugin({
          WEBPACK_REPLACE__corsProxyUrl: buildEnv.serverlessLogicWebTools.corsProxyUrl,
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
      ...merge(common(webpackEnv), {
        entry: {
          index: "./src/index.tsx",
          "yard-editor-envelope": "./src/envelope/YardEditorEnvelopeApp.ts",
          "dashbuilder-editor-envelope": "./src/envelope/DashbuilderEditorEnvelopeApp.ts",
          "text-editor-envelope": "./src/envelope/TextEditorEnvelopeApp.ts",
          "serverless-workflow-combined-editor-envelope":
            "./src/envelope/ServerlessWorkflowCombinedEditorEnvelopeApp.ts",
          "serverless-workflow-diagram-editor-envelope": "./src/envelope/ServerlessWorkflowDiagramEditorEnvelopeApp.ts",
          "serverless-workflow-text-editor-envelope": "./src/envelope/ServerlessWorkflowTextEditorEnvelopeApp.ts",
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
            WEBPACK_REPLACE__version: buildEnv.serverlessLogicWebTools.version,
            WEBPACK_REPLACE__buildInfo: buildInfo,
            WEBPACK_REPLACE__swfBuilderImageFullUrl: `${swfBuilderImageRegistry}/${swfBuilderImageAccount}/${swfBuilderImageName}:${swfBuilderImageTag}`,
            WEBPACK_REPLACE__baseBuilderImageFullUrl: `${baseBuilderImageRegistry}/${baseBuilderImageAccount}/${baseBuilderImageName}:${baseBuilderImageTag}`,
            WEBPACK_REPLACE__devModeImageFullUrl: `${swfDevModeImageRegistry}/${swfDevModeImageAccount}/${swfDevModeImageName}:${swfDevModeImageTag}`,
            WEBPACK_REPLACE__dashbuilderViewerImageFullUrl: `${dashbuilderViewerImageRegistry}/${dashbuilderViewerImageAccount}/${dashbuilderViewerImageName}:${dashbuilderViewerImageTag}`,
            WEBPACK_REPLACE__corsProxyUrl: buildEnv.serverlessLogicWebTools.corsProxyUrl,
            WEBPACK_REPLACE__samplesRepositoryOrg: buildEnv.serverlessLogicWebTools.samplesRepositoryOrg,
            WEBPACK_REPLACE__samplesRepositoryName: buildEnv.serverlessLogicWebTools.samplesRepositoryName,
            WEBPACK_REPLACE__samplesRepositoryRef: buildEnv.serverlessLogicWebTools.samplesRepositoryRef,
          }),
          new CopyPlugin({
            patterns: [
              { from: "./static/resources", to: "./resources" },
              { from: "./static/images", to: "./images" },
              { from: "./static/favicon.svg", to: "./favicon.svg" },
              {
                from: "./static/env.json",
                to: "./env.json",
                transform: () => JSON.stringify(defaultEnvJson, null, 2),
              },
              {
                from: "./static/envelope/serverless-workflow-combined-editor-envelope.html",
                to: "./serverless-workflow-combined-editor-envelope.html",
              },
              {
                from: "./static/envelope/serverless-workflow-diagram-editor-envelope.html",
                to: "./serverless-workflow-diagram-editor-envelope.html",
              },
              {
                from: "./static/envelope/serverless-workflow-text-editor-envelope.html",
                to: "./serverless-workflow-text-editor-envelope.html",
              },
              { from: "./static/envelope/yard-editor-envelope.html", to: "./yard-editor-envelope.html" },
              { from: "./static/envelope/dashbuilder-editor-envelope.html", to: "./dashbuilder-editor-envelope.html" },
              { from: "./static/envelope/text-editor-envelope.html", to: "./text-editor-envelope.html" },
              { from: "./static/favicon.svg", to: "./favicon.svg" },
              // These below are used for development only.
              {
                from: swEditorAssets.swEditorPath(),
                to: "./diagram",
                globOptions: { ignore: ["**/WEB-INF/**/*", "**/*.html"] },
              },
              {
                context: swEditorAssets.swEditorFontsPath(),
                from: "fontawesome-webfont.*",
                to: "./fonts",
                force: true,
              },
              {
                from: "../dashbuilder-editor/dist/dashbuilder-client/",
                to: "./dashbuilder-client",
                globOptions: { ignore: ["**/WEB-INF/**/*"] }, // "**/*.html" omitted because dashbuilder-client/index.html is needed
              },
              {
                from: path.resolve(__dirname, "node_modules/@kie-tools/yard-validator/dist/yard-validator-worker.js"),
                to: "./yard-validator-worker.js",
              },
            ],
          }),
          new ProvidePlugin({
            process: require.resolve("process/browser.js"),
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
                  entry: "monaco-yaml/yaml.worker.js",
                },
              },
            ],
          }),
          new NodePolyfillPlugin(),
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
        ignoreWarnings: [/Failed to parse source map/],
      }),
      devServer: {
        https: true,
        historyApiFallback: false,
        static: [{ directory: path.join(__dirname, "./dist") }, { directory: path.join(__dirname, "./static") }],
        compress: true,
        port: buildEnv.serverlessLogicWebTools.dev.port,
        client: {
          overlay: false,
        },
      },
    },
  ];
};

function getSwfBuilderImageArgs() {
  const swfBuilderImageRegistry = buildEnv.slwtBuilderImageEnv.registry;
  const swfBuilderImageAccount = buildEnv.slwtBuilderImageEnv.account;
  const swfBuilderImageName = buildEnv.slwtBuilderImageEnv.name;
  const swfBuilderImageTag = buildEnv.serverlessLogicWebTools.slwtBuilderImageEnv.tag;

  console.info("Serverless Logic Web Tools :: SWF Builder Image Registry: " + swfBuilderImageRegistry);
  console.info("Serverless Logic Web Tools :: SWF Builder Image Account: " + swfBuilderImageAccount);
  console.info("Serverless Logic Web Tools :: SWF Builder Image Name: " + swfBuilderImageName);
  console.info("Serverless Logic Web Tools :: SWF Builder Image Tag: " + swfBuilderImageTag);

  return [swfBuilderImageRegistry, swfBuilderImageAccount, swfBuilderImageName, swfBuilderImageTag];
}

function getSwfDevModeImageArgs() {
  const swfDevModeImageRegistry = buildEnv.slwtDevModeImageEnv.registry;
  const swfDevModeImageAccount = buildEnv.slwtDevModeImageEnv.account;
  const swfDevModeImageName = buildEnv.slwtDevModeImageEnv.name;
  const swfDevModeImageTag = buildEnv.serverlessLogicWebTools.slwtDevModeImage.tag;

  console.info("Serverless Logic Web Tools :: Dev Mode Image Registry: " + swfDevModeImageRegistry);
  console.info("Serverless Logic Web Tools :: Dev Mode Image Account: " + swfDevModeImageAccount);
  console.info("Serverless Logic Web Tools :: Dev Mode Image Name: " + swfDevModeImageName);
  console.info("Serverless Logic Web Tools :: Dev Mode Image Tag: " + swfDevModeImageTag);

  return [swfDevModeImageRegistry, swfDevModeImageAccount, swfDevModeImageName, swfDevModeImageTag];
}

function getBaseBuilderImageArgs() {
  const baseBuilderImageRegistry = buildEnv.slwtBaseBuilderImageEnv.registry;
  const baseBuilderImageAccount = buildEnv.slwtBaseBuilderImageEnv.account;
  const baseBuilderImageName = buildEnv.slwtBaseBuilderImageEnv.name;
  const baseBuilderImageTag = buildEnv.serverlessLogicWebTools.slwtBaseBuilderImage.tag;

  console.info("Serverless Logic Web Tools :: Base Builder Image Registry: " + baseBuilderImageRegistry);
  console.info("Serverless Logic Web Tools :: Base Builder Image Account: " + baseBuilderImageAccount);
  console.info("Serverless Logic Web Tools :: Base Builder Image Name: " + baseBuilderImageName);
  console.info("Serverless Logic Web Tools :: Base Builder Image Tag: " + baseBuilderImageTag);

  return [baseBuilderImageRegistry, baseBuilderImageAccount, baseBuilderImageName, baseBuilderImageTag];
}

function getDashbuilderViewerImageArgs() {
  const dashbuilderViewerImageRegistry = buildEnv.dashbuilderViewerImageEnv.registry;
  const dashbuilderViewerImageAccount = buildEnv.dashbuilderViewerImageEnv.account;
  const dashbuilderViewerImageName = buildEnv.dashbuilderViewerImageEnv.name;
  const dashbuilderViewerImageTag = buildEnv.serverlessLogicWebTools.dashbuilderViewerImage.tag;

  console.info("Serverless Logic Web Tools :: Dashbuilder Viewer Image Registry: " + dashbuilderViewerImageRegistry);
  console.info("Serverless Logic Web Tools :: Dashbuilder Viewer Image Account: " + dashbuilderViewerImageAccount);
  console.info("Serverless Logic Web Tools :: Dashbuilder Viewer Image Name: " + dashbuilderViewerImageName);
  console.info("Serverless Logic Web Tools :: Dashbuilder Viewer Image Tag: " + dashbuilderViewerImageTag);

  return [
    dashbuilderViewerImageRegistry,
    dashbuilderViewerImageAccount,
    dashbuilderViewerImageName,
    dashbuilderViewerImageTag,
  ];
}

function getGtmResource() {
  const gtmId = buildEnv.serverlessLogicWebTools.gtmId;
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
  const buildInfo = buildEnv.serverlessLogicWebTools.buildInfo;
  console.info(`Serverless Logic Web Tools :: Build info: ${buildInfo}`);
  return buildInfo;
}
