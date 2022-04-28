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

  return merge(common(env), {
    entry: {
      index: "./src/index.tsx",
      "swf-envelope": "./src/envelope/SwfEditorEnvelopeApp.ts",
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
      }),
      new CopyPlugin({
        patterns: [
          { from: "./static/resources", to: "./resources" },
          { from: "./static/images", to: "./images" },
          { from: "./static/samples", to: "./samples" },
          { from: "./static/envelope/swf-envelope.html", to: "./swf-envelope.html" },
          { from: "./static/favicon.svg", to: "./favicon.svg" },
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
    resolve: {
      alias: {
        // `react-monaco-editor` points to the `monaco-editor` package by default, therefore doesn't use our minified
        // version. To solve that, we fool webpack, saying that every import for Monaco directly should actually point to
        // `@kie-tools-core/monaco-editor`. This way, everything works as expected.
        // "monaco-editor/esm/vs/editor/editor.api": require.resolve("@kie-tools-core/monaco-editor"),
      },
    },
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
