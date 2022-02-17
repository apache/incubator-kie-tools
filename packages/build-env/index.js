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
  KIE_TOOLS_BUILD_lint: {
    name: "KIE_TOOLS_BUILD_lint",
    default: `${true}`,
    description: "",
  },
  KIE_TOOLS_BUILD_test: {
    name: "KIE_TOOLS_BUILD_test",
    default: `${true}`,
    description: "",
  },
  KIE_TOOLS_BUILD_testIT: {
    name: "KIE_TOOLS_BUILD_testIT",
    default: `${false}`,
    description: "",
  },
  KIE_TOOLS_BUILD_docker: {
    name: "KIE_TOOLS_BUILD_docker",
    default: `${false}`,
    description: "",
  },
  KIE_TOOLS_BUILD_examples: {
    name: "KIE_TOOLS_BUILD_examples",
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
    default: "https://localhost:9001",
    description: "",
  },
  CHROME_EXTENSION__manifestFile: {
    name: "CHROME_EXTENSION__manifestFile",
    default: "manifest.dev.json",
    description: "",
  },
  ONLINE_EDITOR__buildInfo: {
    name: "ONLINE_EDITOR__buildInfo",
    default: `dev (${process.env.USER}) @ ${new Date().toISOString()}`,
    description: "",
  },
  ONLINE_EDITOR__kieSandboxExtendedServicesDownloadUrlLinux: {
    name: "ONLINE_EDITOR__kieSandboxExtendedServicesDownloadUrlLinux",
    default: `https://github.com/kiegroup/kie-tools/releases/download/${version}/kie_sandbox_extended_services_linux_${version}.tar.gz`,
    description: "",
  },
  ONLINE_EDITOR__kieSandboxExtendedServicesDownloadUrlMacOs: {
    name: "ONLINE_EDITOR__kieSandboxExtendedServicesDownloadUrlMacOs",
    default: `https://github.com/kiegroup/kie-tools/releases/download/${version}/kie_sandbox_extended_services_macos_${version}.dmg`,
    description: "",
  },
  ONLINE_EDITOR__kieSandboxExtendedServicesDownloadUrlWindows: {
    name: "ONLINE_EDITOR__kieSandboxExtendedServicesDownloadUrlWindows",
    default: `https://github.com/kiegroup/kie-tools/releases/download/${version}/kie_sandbox_extended_services_windows_${version}.exe`,
    description: "",
  },
  ONLINE_EDITOR__kieSandboxExtendedServicesCompatibleVersion: {
    name: "ONLINE_EDITOR__kieSandboxExtendedServicesCompatibleVersion",
    default: version,
    description: "",
  },
  ONLINE_EDITOR__gtmId: {
    name: "ONLINE_EDITOR__gtmId",
    default: undefined,
    description: "",
  },
  ONLINE_EDITOR__cypressUrl: {
    name: "ONLINE_EDITOR__cypressUrl",
    default: "https://localhost:9001/",
    description: "",
  },
  DMN_DEV_SANDBOX__baseImageRegistry: {
    name: "DMN_DEV_SANDBOX__baseImageRegistry",
    default: "quay.io",
    description: "",
  },
  DMN_DEV_SANDBOX__baseImageAccount: {
    name: "DMN_DEV_SANDBOX__baseImageAccount",
    default: "kie-tools",
    description: "",
  },
  DMN_DEV_SANDBOX__baseImageName: {
    name: "DMN_DEV_SANDBOX__baseImageName",
    default: "dmn-dev-sandbox-deployment-base-image",
    description: "",
  },
  DMN_DEV_SANDBOX__baseImageTag: {
    name: "DMN_DEV_SANDBOX__baseImageTag",
    default: "latest",
    description: "",
  },
  DMN_DEV_SANDBOX__baseImageBuildTags: {
    name: "DMN_DEV_SANDBOX__baseImageBuildTags",
    default: "latest",
    description: "",
  },
  DMN_DEV_SANDBOX__onlineEditorUrl: {
    name: "DMN_DEV_SANDBOX__onlineEditorUrl",
    default: "https://0.0.0.0:9001",
    description: "",
  },
  DMN_DEV_SANDBOX__gtmId: {
    name: "DMN_DEV_SANDBOX__gtmId",
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
  QUARKUS_PLATFORM_version: {
    name: "QUARKUS_PLATFORM_version",
    default: "2.4.0.Final",
    description: "",
  },
  KOGITO_RUNTIME_version: {
    name: "KOGITO_RUNTIME_version",
    default: "1.12.0.Final",
    description: "",
  },
  DASHBUILDER__baseImageRegistry: {
    name: "DASHBUILDER__baseImageRegistry",
    default: "quay.io",
    description: "",
  },
  DASHBUILDER__baseImageAccount: {
    name: "DASHBUILDER__baseImageAccount",
    default: "kie-tools",
    description: "",
  },
  DASHBUILDER_RUNTIME__baseImageName: {
    name: "DASHBUILDER_RUNTIME__baseImageName",
    default: "dashbuilder-runtime",
    description: "",
  },
  DASHBUILDER_AUTHORING__baseImageName: {
    name: "DASHBUILDER_AUTHORING__baseImageName",
    default: "dashbuilder-authoring",
    description: "",
  },
  DASHBUILDER__baseImageTag: {
    name: "DASHBUILDER__baseImageTag",
    default: "latest",
    description: "",
  },
  DASHBUILDER__baseImageBuildTags: {
    name: "DASHBUILDER__baseImageBuildTags",
    default: "latest",
    description: "",
  },
  KIE_SANDBOX__imageRegistry: {
    name: "KIE_SANDBOX__imageRegistry",
    default: "quay.io",
    description: "",
  },
  KIE_SANDBOX__imageAccount: {
    name: "KIE_SANDBOX__imageAccount",
    default: "kie-tools",
    description: "",
  },
  KIE_SANDBOX__imageName: {
    name: "KIE_SANDBOX__imageName",
    default: "kie-sandbox-image",
    description: "",
  },
  KIE_SANDBOX__imageBuildTags: {
    name: "KIE_SANDBOX__imageBuildTags",
    default: "latest",
    description: "",
  },
  KIE_SANDBOX_EXTENDED_SERVICES__imageRegistry: {
    name: "KIE_SANDBOX_EXTENDED_SERVICES__imageRegistry",
    default: "quay.io",
    description: "",
  },
  KIE_SANDBOX_EXTENDED_SERVICES__imageAccount: {
    name: "KIE_SANDBOX_EXTENDED_SERVICES__imageAccount",
    default: "kie-tools",
    description: "",
  },
  KIE_SANDBOX_EXTENDED_SERVICES__imageName: {
    name: "KIE_SANDBOX_EXTENDED_SERVICES__imageName",
    default: "kie-sandbox-extended-services-image",
    description: "",
  },
  KIE_SANDBOX_EXTENDED_SERVICES__imageBuildTags: {
    name: "KIE_SANDBOX_EXTENDED_SERVICES__imageBuildTags",
    default: "latest",
    description: "",
  },
  CORS_PROXY__imageRegistry: {
    name: "CORS_PROXY__imageRegistry",
    default: "quay.io",
    description: "",
  },
  CORS_PROXY__imageAccount: {
    name: "CORS_PROXY__imageAccount",
    default: "kie-tools",
    description: "",
  },
  CORS_PROXY__imageName: {
    name: "CORS_PROXY__imageName",
    default: "cors-proxy-image",
    description: "",
  },
  CORS_PROXY__imageBuildTags: {
    name: "CORS_PROXY__imageBuildTags",
    default: "latest",
    description: "",
  },
  DMN_LOADER__outputPath: {
    name: "DMN_LOADER__outputPath",
    default: "dist",
    description: "Directory path used to output build artifacts of stunner-editors-dmn-loader",
  },
};

module.exports = {
  global: {
    version: version,
    build: {
      lint: str2bool(getOrDefault(ENV_VARS.KIE_TOOLS_BUILD_lint)),
      test: str2bool(getOrDefault(ENV_VARS.KIE_TOOLS_BUILD_test)),
      testIT: str2bool(getOrDefault(ENV_VARS.KIE_TOOLS_BUILD_testIT)),
      docker: str2bool(getOrDefault(ENV_VARS.KIE_TOOLS_BUILD_docker)),
      examples: str2bool(getOrDefault(ENV_VARS.KIE_TOOLS_BUILD_examples)),
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

  boxedExpressionComponent: {
    dev: {
      port: 3015,
    },
  },

  feelInputComponent: {
    dev: {
      port: 3016,
      REACT_APP_FEEL_SERVER: "",
    },
  },

  importJavaClassesComponent: {
    dev: {
      port: 3017,
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
      cypressUrl: getOrDefault(ENV_VARS.ONLINE_EDITOR__cypressUrl),
      port: 9001,
    },
    gtmId: getOrDefault(ENV_VARS.ONLINE_EDITOR__gtmId),
    buildInfo: getOrDefault(ENV_VARS.ONLINE_EDITOR__buildInfo),
    kieSandboxExtendedServices: {
      compatibleVersion: getOrDefault(ENV_VARS.ONLINE_EDITOR__kieSandboxExtendedServicesCompatibleVersion),
      downloadUrl: {
        linux: getOrDefault(ENV_VARS.ONLINE_EDITOR__kieSandboxExtendedServicesDownloadUrlLinux),
        macOs: getOrDefault(ENV_VARS.ONLINE_EDITOR__kieSandboxExtendedServicesDownloadUrlMacOs),
        windows: getOrDefault(ENV_VARS.ONLINE_EDITOR__kieSandboxExtendedServicesDownloadUrlWindows),
      },
    },
  },

  dmnDevSandbox: {
    gtmId: getOrDefault(ENV_VARS.DMN_DEV_SANDBOX__gtmId),
    onlineEditorUrl: getOrDefault(ENV_VARS.DMN_DEV_SANDBOX__onlineEditorUrl),
    baseImage: {
      registry: getOrDefault(ENV_VARS.DMN_DEV_SANDBOX__baseImageRegistry),
      account: getOrDefault(ENV_VARS.DMN_DEV_SANDBOX__baseImageAccount),
      name: getOrDefault(ENV_VARS.DMN_DEV_SANDBOX__baseImageName),
      tag: getOrDefault(ENV_VARS.DMN_DEV_SANDBOX__baseImageTag),
      buildTags: getOrDefault(ENV_VARS.DMN_DEV_SANDBOX__baseImageBuildTags),
    },
  },

  dashbuilder: {
    baseImage: {
      registry: getOrDefault(ENV_VARS.DASHBUILDER__baseImageRegistry),
      account: getOrDefault(ENV_VARS.DASHBUILDER__baseImageAccount),
      runtimeName: getOrDefault(ENV_VARS.DASHBUILDER_RUNTIME__baseImageName),
      authoringName: getOrDefault(ENV_VARS.DASHBUILDER_AUTHORING__baseImageName),
      tag: getOrDefault(ENV_VARS.DASHBUILDER__baseImageTag),
      buildTags: getOrDefault(ENV_VARS.DASHBUILDER__baseImageBuildTags),
    },
  },

  kieSandbox: {
    image: {
      registry: getOrDefault(ENV_VARS.KIE_SANDBOX__imageRegistry),
      account: getOrDefault(ENV_VARS.KIE_SANDBOX__imageAccount),
      name: getOrDefault(ENV_VARS.KIE_SANDBOX__imageName),
      buildTags: getOrDefault(ENV_VARS.KIE_SANDBOX__imageBuildTags),
    },
  },

  extendedServices: {
    image: {
      registry: getOrDefault(ENV_VARS.KIE_SANDBOX_EXTENDED_SERVICES__imageRegistry),
      account: getOrDefault(ENV_VARS.KIE_SANDBOX_EXTENDED_SERVICES__imageAccount),
      name: getOrDefault(ENV_VARS.KIE_SANDBOX_EXTENDED_SERVICES__imageName),
      buildTags: getOrDefault(ENV_VARS.KIE_SANDBOX_EXTENDED_SERVICES__imageBuildTags),
    },
  },

  corsProxy: {
    image: {
      registry: getOrDefault(ENV_VARS.CORS_PROXY__imageRegistry),
      account: getOrDefault(ENV_VARS.CORS_PROXY__imageAccount),
      name: getOrDefault(ENV_VARS.CORS_PROXY__imageName),
      buildTags: getOrDefault(ENV_VARS.CORS_PROXY__imageBuildTags),
    },
  },

  dmnFormWebApp: {
    dev: {
      port: 9008,
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

  serverlessWorkflowEditor: {
    dev: {
      port: 9007,
    },
  },

  kogitoRuntime: {
    version: getOrDefault(ENV_VARS.KOGITO_RUNTIME_version),
  },

  quarkusPlatform: {
    version: getOrDefault(ENV_VARS.QUARKUS_PLATFORM_version),
  },

  examples: {
    chromeExtensionEnvelope: {
      port: 9101,
    },
    webapp: {
      port: 9100,
    },
  },

  stunnerEditors: {
    dmnLoader: {
      outputPath: getOrDefault(ENV_VARS.DMN_LOADER__outputPath),
    },
  },

  vars: () => ({
    ENV_VARS,
    getOrDefault: getOrDefault,
  }),
};
