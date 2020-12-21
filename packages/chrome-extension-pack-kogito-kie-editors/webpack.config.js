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

function getRouterArgs(argv) {
  let targetOrigin = argv["ROUTER_targetOrigin"] || process.env["ROUTER_targetOrigin"];
  let relativePath = argv["ROUTER_relativePath"] || process.env["ROUTER_relativePath"];

  if (argv.mode === "production") {
    targetOrigin = targetOrigin || "https://kiegroup.github.io";
    relativePath = relativePath || `kogito-online/editors/${getLatestGitTag()}/`;
  } else {
    targetOrigin = targetOrigin || "https://localhost:9000";
    relativePath = relativePath || "";
  }

  console.info("EditorEnvelopeLocator :: target origin: " + targetOrigin);
  console.info("EditorEnvelopeLocator :: relative path: " + relativePath);

  return [targetOrigin, relativePath];
}

function getOnlineEditorArgs(argv) {
  let onlineEditorUrl = argv["ONLINEEDITOR_url"] || process.env["ONLINEEDITOR_url"];
  let manifestFile;

  if (argv.mode === "production") {
    onlineEditorUrl = onlineEditorUrl || "https://kiegroup.github.io/kogito-online";
    manifestFile = "manifest.prod.json";
  } else {
    onlineEditorUrl = onlineEditorUrl || "http://localhost:9001";
    manifestFile = "manifest.dev.json";
  }

  console.info("Online Editor :: URL: " + onlineEditorUrl);

  return [onlineEditorUrl, manifestFile];
}

module.exports = async (env, argv) => {
  const [router_targetOrigin, router_relativePath] = getRouterArgs(argv);
  const [onlineEditor_url, manifestFile] = getOnlineEditorArgs(argv);

  return merge(common, {
    entry: {
      "content_scripts/github": "./src/github-content-script.ts",
      "content_scripts/online-editor": "./src/online-editor-content-script.ts",
      background: "./src/background.ts",
      "envelope/index": "./src/envelope/index.ts"
    },
    devServer: {
      contentBase: ["dist"],
      compress: true,
      watchContentBase: true,
      https: true,
      port: 9000
    },
    plugins: [
      new CopyPlugin([
        { from: "./static", to: "." },
        { from: `./${manifestFile}`, to: "./manifest.json" },

        // These are used for development only.
        { from: externalAssets.dmnEditorPath(argv), to: "dmn", ignore: ["WEB-INF/**/*"] },
        { from: externalAssets.bpmnEditorPath(argv), to: "bpmn", ignore: ["WEB-INF/**/*"] },
        { from: externalAssets.scesimEditorPath(argv), to: "scesim", ignore: ["WEB-INF/**/*"] }
      ]),
      new ZipPlugin({
        filename: "chrome_extension_kogito_kie_editors_" + packageJson.version + ".zip",
        pathPrefix: "dist",

        // These are used for development only,
        // therefore should not be included in the ZIP file.
        exclude: ["dmn", "bpmn", "scesim"]
      })
    ],
    module: {
      rules: [
        {
          test: /ChromeRouter\.ts$/,
          loader: "string-replace-loader",
          options: {
            multiple: [
              {
                search: "$_{WEBPACK_REPLACE__targetOrigin}",
                replace: router_targetOrigin
              },
              {
                search: "$_{WEBPACK_REPLACE__relativePath}",
                replace: router_relativePath
              }
            ]
          }
        },
        {
          test: /background\.ts|OnlineEditorManager\.ts$/,
          loader: "string-replace-loader",
          options: {
            multiple: [
              {
                search: "$_{WEBPACK_REPLACE__onlineEditor_url}",
                replace: onlineEditor_url
              }
            ]
          }
        },
        ...pfWebpackOptions.patternflyRules
      ]
    }
  });
};
