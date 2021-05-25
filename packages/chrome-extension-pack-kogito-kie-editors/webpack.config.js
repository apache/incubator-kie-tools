/*
 * Copyright 2019 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

const path = require("path");
const CopyPlugin = require("copy-webpack-plugin");
const ZipPlugin = require("zip-webpack-plugin");
const packageJson = require("./package.json");
const pfWebpackOptions = require("@kogito-tooling/patternfly-base/patternflyWebpackOptions");
const { merge } = require("webpack-merge");
const common = require("../../webpack.common.config");
const externalAssets = require("@kogito-tooling/external-assets-base");
const { EnvironmentPlugin } = require("webpack");

function getLatestGitTag() {
  const tagName = require("child_process").execSync("git rev-list --tags --max-count=1").toString().trim();

  return require("child_process")
    .execSync("git describe --tags " + tagName)
    .toString()
    .trim();
}

function getRouterArgs(env, argv) {
  let targetOrigin = argv["ROUTER_targetOrigin"] ?? process.env["ROUTER_targetOrigin"];
  let relativePath = argv["ROUTER_relativePath"] ?? process.env["ROUTER_relativePath"];

  if (env.dev) {
    targetOrigin = targetOrigin ?? "https://localhost:9000";
    relativePath = relativePath ?? "";
  } else {
    targetOrigin = targetOrigin ?? "https://kiegroup.github.io";
    relativePath = relativePath ?? `kogito-online/editors/${getLatestGitTag()}/`;
  }

  console.info("EditorEnvelopeLocator :: target origin: " + targetOrigin);
  console.info("EditorEnvelopeLocator :: relative path: " + relativePath);

  return [targetOrigin, relativePath];
}

function getOnlineEditorArgs(env, argv) {
  let onlineEditorUrl = argv["ONLINEEDITOR_url"] ?? process.env["ONLINEEDITOR_url"];
  let manifestFile;

  if (env.dev) {
    onlineEditorUrl = onlineEditorUrl ?? "http://localhost:9001";
    manifestFile = "manifest.dev.json";
  } else {
    onlineEditorUrl = onlineEditorUrl ?? "https://kiegroup.github.io/kogito-online";
    manifestFile = "manifest.prod.json";
  }

  console.info("Online Editor :: URL: " + onlineEditorUrl);

  return [onlineEditorUrl, manifestFile];
}

module.exports = async (env, argv) => {
  const [router_targetOrigin, router_relativePath] = getRouterArgs(env, argv);
  const [onlineEditor_url, manifestFile] = getOnlineEditorArgs(env, argv);

  return merge(common(env, argv), {
    entry: {
      "content_scripts/github": "./src/github-content-script.ts",
      "content_scripts/online-editor": "./src/online-editor-content-script.ts",
      background: "./src/background.ts",
      "envelope/bpmn-envelope": "./src/envelope/BpmnEditorEnvelopeApp.ts",
      "envelope/dmn-envelope": "./src/envelope/DmnEditorEnvelopeApp.ts",
      "envelope/scesim-envelope": "./src/envelope/SceSimEditorEnvelopeApp.ts",
    },
    devServer: {
      contentBase: ["dist"],
      compress: true,
      watchContentBase: true,
      https: true,
      port: 9000,
    },
    plugins: [
      new EnvironmentPlugin({
        WEBPACK_REPLACE__targetOrigin: router_targetOrigin,
        WEBPACK_REPLACE__relativePath: router_relativePath,
        WEBPACK_REPLACE__onlineEditor_url: onlineEditor_url,
      }),
      new CopyPlugin({
        patterns: [
          { from: "./static", to: "." },
          { from: `./${manifestFile}`, to: "./manifest.json" },

          // These are used for development only.
          { from: externalAssets.dmnEditorPath(argv), to: "dmn", globOptions: { ignore: ["WEB-INF/**/*"] } },
          { from: externalAssets.bpmnEditorPath(argv), to: "bpmn", globOptions: { ignore: ["WEB-INF/**/*"] } },
          { from: externalAssets.scesimEditorPath(argv), to: "scesim", globOptions: { ignore: ["WEB-INF/**/*"] } },
        ],
      }),
      new ZipPlugin({
        filename: "chrome_extension_kogito_kie_editors_" + packageJson.version + ".zip",
        pathPrefix: "dist",

        // These are used for development only,
        // therefore should not be included in the ZIP file.
        exclude: ["dmn", "bpmn", "scesim"],
      }),
    ],
    module: {
      rules: [...pfWebpackOptions.patternflyRules],
    },
  });
};
