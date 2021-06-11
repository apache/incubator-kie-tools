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

const version = require("./package.json").version;

const str2bool = (str) => str === "true";

module.exports = {
  global: {
    webpack: (webpackEnv) => {
      if (webpackEnv.dev) {
        return {
          minimize: str2bool(process.env["WEBPACK__minimize"] ?? `${false}`),
          transpileOnly: str2bool(process.env["WEBPACK__tsLoaderTranspileOnly"] ?? `${false}`),
          sourceMaps: str2bool(process.env["WEBPACK__sourceMaps"] ?? `${true}`),
          mode: process.env["WEBPACK__mode"] ?? "development",
        };
      } else {
        return {
          minimize: str2bool(process.env["WEBPACK__minimize"] ?? `${true}`),
          transpileOnly: str2bool(process.env["WEBPACK__tsLoaderTranspileOnly"] ?? `${false}`),
          sourceMaps: str2bool(process.env["WEBPACK__sourceMaps"] ?? `${false}`),
          mode: process.env["WEBPACK__mode"] ?? "production",
        };
      }
    },
  },
  chromeExtension: {
    dev: {
      port: 9000,
    },
    routerTargetOrigin: process.env["CHROME_EXTENSION__routerTargetOrigin"] ?? "https://localhost:9000",
    routerRelativePath: process.env["CHROME_EXTENSION__routerRelativePath"] ?? "",
    onlineEditorUrl: process.env["CHROME_EXTENSION__onlineEditorUrl"] ?? "http://localhost:9001",
    manifestFile: process.env["CHROME_EXTENSION__manifestFile"] ?? "manifest.dev.json",
  },

  onlineEditor: {
    dev: {
      port: 9001,
    },
    downloadHubUrl: {
      linux:
        process.env["ONLINE_EDITOR__downloadHubUrlLinux"] ??
        `https://github.com/kiegroup/kogito-tooling/releases/download/${version}/business_modeler_hub_preview_linux_${version}.zip`,
      macOs:
        process.env["ONLINE_EDITOR__downloadHubUrlLMacOs"] ??
        `https://github.com/kiegroup/kogito-tooling/releases/download/${version}/business_modeler_hub_preview_macos_${version}.zip`,
      windows:
        process.env["ONLINE_EDITOR__downloadHubUrlWindows"] ??
        `https://github.com/kiegroup/kogito-tooling/releases/download/${version}/business_modeler_hub_preview_windows_${version}.zip`,
    },
  },

  standaloneEditors: {
    dev: {
      port: 9006,
    },
  },

  pmmlEditor: {
    dev: {
      port: 9005,
    },
  },
};
