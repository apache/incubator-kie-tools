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

const get = (envVar) => process.env[envVar.name];
const getOrDefault = (envVar) => get(envVar) ?? envVar.default;

const ENV_VARS = {
  KOGITO_TOOLING_BUILD_lint: {
    name: "KOGITO_TOOLING_BUILD_lint",
    default: `${true}`,
    description: "",
  },
  KOGITO_TOOLING_BUILD_test: {
    name: "KOGITO_TOOLING_BUILD_test",
    default: `${true}`,
    description: "",
  },
  KOGITO_TOOLING_BUILD_testIT: {
    name: "KOGITO_TOOLING_BUILD_testIT",
    default: `${false}`,
    description: "",
  },
  CHROME_EXTENSION__routerTargetOrigin: {
    name: "CHROME_EXTENSION__routerTargetOrigin",
    default: "https://localhost:9000",
    description: "",
  },
  CHROME_EXTENSION__routerRelativePath: {
    name: "CHROME_EXTENSION__routerRelativePath",
    default: "",
    description: "",
  },
  CHROME_EXTENSION__onlineEditorUrl: {
    name: "CHROME_EXTENSION__onlineEditorUrl",
    default: "http://localhost:9001",
    description: "",
  },
  CHROME_EXTENSION__manifestFile: {
    name: "CHROME_EXTENSION__manifestFile",
    default: "manifest.dev.json",
    description: "",
  },
  ONLINE_EDITOR__downloadHubUrlLinux: {
    name: "ONLINE_EDITOR__downloadHubUrlLinux",
    default: `https://github.com/kiegroup/kogito-tooling/releases/download/${version}/business_modeler_hub_preview_linux_${version}.zip`,
    description: "",
  },
  ONLINE_EDITOR__downloadHubUrlMacOs: {
    name: "ONLINE_EDITOR__downloadHubUrlMacOs",
    default: `https://github.com/kiegroup/kogito-tooling/releases/download/${version}/business_modeler_hub_preview_macos_${version}.zip`,
    description: "",
  },
  ONLINE_EDITOR__downloadHubUrlWindows: {
    name: "ONLINE_EDITOR__downloadHubUrlWindows",
    default: `https://github.com/kiegroup/kogito-tooling/releases/download/${version}/business_modeler_hub_preview_windows_${version}.zip`,
    description: "",
  },
  ONLINE_EDITOR__buildInfo: {
    name: "ONLINE_EDITOR__buildInfo",
    default: `dev (${process.env.USER}) @ ${new Date().toISOString()}`,
    description: "",
  },
  ONLINE_EDITOR__kieToolingExtendedServicesDownloadUrlLinux: {
    name: "ONLINE_EDITOR__kieToolingExtendedServicesDownloadUrlLinux",
    default: `https://github.com/kiegroup/kogito-tooling-go/releases/download/${version}/kie_tooling_extended_services_linux_${version}.dmg`,
    description: "",
  },
  ONLINE_EDITOR__kieToolingExtendedServicesDownloadUrlMacOs: {
    name: "ONLINE_EDITOR__kieToolingExtendedServicesDownloadUrlMacOs",
    default: `https://github.com/kiegroup/kogito-tooling-go/releases/download/${version}/kie_tooling_extended_services_macos_${version}.dmg`,
    description: "",
  },
  ONLINE_EDITOR__kieToolingExtendedServicesDownloadUrlWindows: {
    name: "ONLINE_EDITOR__kieToolingExtendedServicesDownloadUrlWindows",
    default: `https://github.com/kiegroup/kogito-tooling-go/releases/download/${version}/kie_tooling_extended_services_windows_${version}.dmg`,
    description: "",
  },
  ONLINE_EDITOR__kieToolingExtendedServicesCompatibleVersion: {
    name: "ONLINE_EDITOR__kieToolingExtendedServicesCompatibleVersion",
    default: version,
    description: "",
  },
  ONLINE_EDITOR__gtmId: {
    name: "ONLINE_EDITOR__gtmId",
    default: undefined,
    description: "",
  },
  WEBPACK__minimize: {
    name: "WEBPACK__minimize",
    description: "",
  },
  WEBPACK__tsLoaderTranspileOnly: {
    name: "WEBPACK__tsLoaderTranspileOnly",
    description: "",
  },
  WEBPACK__sourceMaps: {
    name: "WEBPACK__sourceMaps",
    description: "",
  },
  WEBPACK__mode: {
    name: "WEBPACK__mode",
    description: "",
  },
};

module.exports = {
  global: {
    version: version,
    build: {
      lint: str2bool(getOrDefault(ENV_VARS.KOGITO_TOOLING_BUILD_lint)),
      test: str2bool(getOrDefault(ENV_VARS.KOGITO_TOOLING_BUILD_test)),
      testIT: str2bool(getOrDefault(ENV_VARS.KOGITO_TOOLING_BUILD_testIT)),
    },
    webpack: (webpackEnv) => {
      if (webpackEnv.dev) {
        return {
          minimize: str2bool(get(ENV_VARS.WEBPACK__minimize) ?? `${false}`),
          transpileOnly: str2bool(get(ENV_VARS.WEBPACK__tsLoaderTranspileOnly) ?? `${false}`),
          sourceMaps: str2bool(get(ENV_VARS.WEBPACK__sourceMaps) ?? `${true}`),
          mode: get(ENV_VARS.WEBPACK__mode) ?? "development",
          live: webpackEnv.live,
        };
      } else {
        return {
          minimize: str2bool(get(ENV_VARS.WEBPACK__minimize) ?? `${true}`),
          transpileOnly: str2bool(get(ENV_VARS.WEBPACK__tsLoaderTranspileOnly) ?? `${false}`),
          sourceMaps: str2bool(get(ENV_VARS.WEBPACK__sourceMaps) ?? `${false}`),
          mode: get(ENV_VARS.WEBPACK__mode) ?? "production",
          live: webpackEnv.live,
        };
      }
    },
  },

  chromeExtension: {
    dev: {
      port: 9000,
    },
    routerTargetOrigin: getOrDefault(ENV_VARS.CHROME_EXTENSION__routerTargetOrigin),
    routerRelativePath: getOrDefault(ENV_VARS.CHROME_EXTENSION__routerRelativePath),
    onlineEditorUrl: getOrDefault(ENV_VARS.CHROME_EXTENSION__onlineEditorUrl),
    manifestFile: getOrDefault(ENV_VARS.CHROME_EXTENSION__manifestFile),
  },

  onlineEditor: {
    dev: {
      port: 9001,
    },
    gtmId: getOrDefault(ENV_VARS.ONLINE_EDITOR__gtmId),
    buildInfo: getOrDefault(ENV_VARS.ONLINE_EDITOR__buildInfo),
    downloadHubUrl: {
      linux: getOrDefault(ENV_VARS.ONLINE_EDITOR__downloadHubUrlLinux),
      macOs: getOrDefault(ENV_VARS.ONLINE_EDITOR__downloadHubUrlMacOs),
      windows: getOrDefault(ENV_VARS.ONLINE_EDITOR__downloadHubUrlWindows),
    },
    kieToolingExtendedServices: {
      compatibleVersion: getOrDefault(ENV_VARS.ONLINE_EDITOR__kieToolingExtendedServicesCompatibleVersion),
      downloadUrl: {
        linux: getOrDefault(ENV_VARS.ONLINE_EDITOR__kieToolingExtendedServicesDownloadUrlLinux),
        macOs: getOrDefault(ENV_VARS.ONLINE_EDITOR__kieToolingExtendedServicesDownloadUrlMacOs),
        windows: getOrDefault(ENV_VARS.ONLINE_EDITOR__kieToolingExtendedServicesDownloadUrlWindows),
      },
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
  vars: () => ({
    ENV_VARS,
    getOrDefault: getOrDefault,
  }),
};
