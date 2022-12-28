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
const { EnvironmentPlugin } = require("webpack");
const HtmlReplaceWebpackPlugin = require("html-replace-webpack-plugin");
const MonacoWebpackPlugin = require("monaco-editor-webpack-plugin");
const swEditor = require("@kie-tools/serverless-workflow-diagram-editor-assets");
const { env } = require("./env");
const buildEnv = env;

module.exports = async (env) => {
  const buildInfo = getBuildInfo();
  const gtmResource = getGtmResource();
  const [swfBuilderImageRegistry, swfBuilderImageAccount, swfBuilderImageName, swfBuilderImageTag] =
    getSwfBuilderImageArgs();
  const [baseBuilderImageRegistry, baseBuilderImageAccount, baseBuilderImageName, baseBuilderImageTag] =
    getBaseBuilderImageArgs();
  const [
    dashbuilderViewerImageRegistry,
    dashbuilderViewerImageAccount,
    dashbuilderViewerImageName,
    dashbuilderViewerImageTag,
  ] = getDashbuilderViewerImageArgs();
  const [
    kieSandboxExtendedServices_linuxDownloadUrl,
    kieSandboxExtendedServices_macOsDownloadUrl,
    kieSandboxExtendedServices_windowsDownloadUrl,
    kieSandboxExtendedServices_compatibleVersion,
  ] = getKieSandboxExtendedServicesArgs();

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
        new EnvironmentPlugin({
          WEBPACK_REPLACE__gitCorsProxyUrl: buildEnv.serverlessLogicWebTools.gitCorsProxyUrl,
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
    merge(common(env), {
      entry: {
        index: "./src/index.tsx",
        "dashbuilder-editor-envelope": "./src/envelope/DashbuilderEditorEnvelopeApp.ts",
        "text-editor-envelope": "./src/envelope/TextEditorEnvelopeApp.ts",
        "serverless-workflow-combined-editor-envelope": "./src/envelope/ServerlessWorkflowCombinedEditorEnvelopeApp.ts",
        "serverless-workflow-diagram-editor-envelope": "./src/envelope/ServerlessWorkflowDiagramEditorEnvelopeApp.ts",
        "serverless-workflow-text-editor-envelope": "./src/envelope/ServerlessWorkflowTextEditorEnvelopeApp.ts",
        "serverless-workflow-mermaid-viewer-envelope": "./src/envelope/ServerlessWorkflowMermaidViewerEnvelopeApp.ts",
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
          WEBPACK_REPLACE__swfBuilderImageFullUrl: `${swfBuilderImageRegistry}/${swfBuilderImageAccount}/${swfBuilderImageName}:${swfBuilderImageTag}`,
          WEBPACK_REPLACE__baseBuilderImageFullUrl: `${baseBuilderImageRegistry}/${baseBuilderImageAccount}/${baseBuilderImageName}:${baseBuilderImageTag}`,
          WEBPACK_REPLACE__dashbuilderViewerImageFullUrl: `${dashbuilderViewerImageRegistry}/${dashbuilderViewerImageAccount}/${dashbuilderViewerImageName}:${dashbuilderViewerImageTag}`,
          WEBPACK_REPLACE__kieSandboxExtendedServicesLinuxDownloadUrl: kieSandboxExtendedServices_linuxDownloadUrl,
          WEBPACK_REPLACE__kieSandboxExtendedServicesMacOsDownloadUrl: kieSandboxExtendedServices_macOsDownloadUrl,
          WEBPACK_REPLACE__kieSandboxExtendedServicesWindowsDownloadUrl: kieSandboxExtendedServices_windowsDownloadUrl,
          WEBPACK_REPLACE__kieSandboxExtendedServicesCompatibleVersion: kieSandboxExtendedServices_compatibleVersion,
          WEBPACK_REPLACE__gitCorsProxyUrl: buildEnv.serverlessLogicWebTools.gitCorsProxyUrl,
        }),
        new CopyPlugin({
          patterns: [
            { from: "./static/resources", to: "./resources" },
            { from: "./static/images", to: "./images" },
            { from: "./static/samples", to: "./samples" },
            { from: "./static/favicon.svg", to: "./favicon.svg" },
            { from: "./static/env.json", to: "./env.json" },
            {
              from: "./static/envelope/serverless-workflow-combined-editor-envelope.html",
              to: "./serverless-workflow-combined-editor-envelope.html",
            },
            {
              from: "./static/envelope/serverless-workflow-diagram-editor-envelope.html",
              to: "./serverless-workflow-diagram-editor-envelope.html",
            },
            {
              from: "./static/envelope/serverless-workflow-mermaid-viewer-envelope.html",
              to: "./serverless-workflow-mermaid-viewer-envelope.html",
            },
            {
              from: "./static/envelope/serverless-workflow-text-editor-envelope.html",
              to: "./serverless-workflow-text-editor-envelope.html",
            },
            { from: "./static/envelope/dashbuilder-editor-envelope.html", to: "./dashbuilder-editor-envelope.html" },
            { from: "./static/envelope/text-editor-envelope.html", to: "./text-editor-envelope.html" },
            { from: "./static/favicon.svg", to: "./favicon.svg" },
            { from: "./static/env.json", to: "./env.json" },
            // These below are used for development only.
            {
              from: swEditor.swEditorPath(),
              to: "./diagram",
              globOptions: { ignore: ["**/WEB-INF/**/*", "**/*.html"] },
            },
            {
              from: "../dashbuilder-client/dist/",
              to: "./dashbuilder-client",
              globOptions: { ignore: ["**/WEB-INF/**/*"] }, // "**/*.html" omitted because dashbuilder-client/index.html is needed
            },
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
                entry: "monaco-yaml/yaml.worker.js",
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
      ignoreWarnings: [/Failed to parse source map/],
      devServer: {
        https: true,
        historyApiFallback: false,
        static: [{ directory: path.join(__dirname, "./dist") }, { directory: path.join(__dirname, "./static") }],
        compress: true,
        port: buildEnv.serverlessLogicWebTools.dev.port,
      },
    }),
  ];
};

function getSwfBuilderImageArgs() {
  const swfBuilderImageRegistry = buildEnv.swfBuilderImageEnv.registry;
  const swfBuilderImageAccount = buildEnv.swfBuilderImageEnv.account;
  const swfBuilderImageName = buildEnv.swfBuilderImageEnv.name;
  const swfBuilderImageTag = buildEnv.serverlessLogicWebTools.swfBuilderImage.tag;

  console.info("Serverless Logic Web Tools :: SWF Builder Image Registry: " + swfBuilderImageRegistry);
  console.info("Serverless Logic Web Tools :: SWF Builder Image Account: " + swfBuilderImageAccount);
  console.info("Serverless Logic Web Tools :: SWF Builder Image Name: " + swfBuilderImageName);
  console.info("Serverless Logic Web Tools :: SWF Builder Image Tag: " + swfBuilderImageTag);

  return [swfBuilderImageRegistry, swfBuilderImageAccount, swfBuilderImageName, swfBuilderImageTag];
}

function getBaseBuilderImageArgs() {
  const baseBuilderImageRegistry = buildEnv.baseBuilderImageEnv.registry;
  const baseBuilderImageAccount = buildEnv.baseBuilderImageEnv.account;
  const baseBuilderImageName = buildEnv.baseBuilderImageEnv.name;
  const baseBuilderImageTag = buildEnv.serverlessLogicWebTools.baseBuilderImage.tag;

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

function getKieSandboxExtendedServicesArgs() {
  const linuxDownloadUrl = buildEnv.serverlessLogicWebTools.kieSandboxExtendedServices.downloadUrl.linux;
  const macOsDownloadUrl = buildEnv.serverlessLogicWebTools.kieSandboxExtendedServices.downloadUrl.macOs;
  const windowsDownloadUrl = buildEnv.serverlessLogicWebTools.kieSandboxExtendedServices.downloadUrl.windows;
  const compatibleVersion = buildEnv.serverlessLogicWebTools.kieSandboxExtendedServices.compatibleVersion;

  console.info("KIE Sandbox Extended Services :: Linux download URL: " + linuxDownloadUrl);
  console.info("KIE Sandbox Extended Services :: macOS download URL: " + macOsDownloadUrl);
  console.info("KIE Sandbox Extended Services :: Windows download URL: " + windowsDownloadUrl);
  console.info("KIE Sandbox Extended Services :: Compatible version: " + compatibleVersion);

  return [linuxDownloadUrl, macOsDownloadUrl, windowsDownloadUrl, compatibleVersion];
}
