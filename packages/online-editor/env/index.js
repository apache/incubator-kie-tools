/*
 * Copyright 2022 Red Hat, Inc. and/or its affiliates.
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

const { envVars, getOrDefault, str2bool, compositeEnv } = require("@kie-tools/build-env");

const buildEnv = require("@kie-tools/build-env/env");
const webpackBaseEnv = require("@kie-tools-core/webpack-base/env");

module.exports = compositeEnv([buildEnv, webpackBaseEnv], {
  get vars() {
    return envVars({
      ONLINE_EDITOR__buildInfo: {
        default: `dev (${process.env.USER}) @ ${new Date().toISOString()}`,
        description: "",
      },
      ONLINE_EDITOR__kieSandboxExtendedServicesDownloadUrlLinux: {
        default: `https://github.com/kiegroup/kie-tools/releases/download/${buildEnv.env.global.version}/kie_sandbox_extended_services_linux_${buildEnv.env.global.version}.tar.gz`,
        description: "",
      },
      ONLINE_EDITOR__kieSandboxExtendedServicesDownloadUrlMacOs: {
        default: `https://github.com/kiegroup/kie-tools/releases/download/${buildEnv.env.global.version}/kie_sandbox_extended_services_macos_${buildEnv.env.global.version}.dmg`,
        description: "",
      },
      ONLINE_EDITOR__kieSandboxExtendedServicesDownloadUrlWindows: {
        default: `https://github.com/kiegroup/kie-tools/releases/download/${buildEnv.env.global.version}/kie_sandbox_extended_services_windows_${buildEnv.env.global.version}.exe`,
        description: "",
      },
      ONLINE_EDITOR__kieSandboxExtendedServicesCompatibleVersion: {
        default: buildEnv.env.global.version,
        description: "",
      },
      ONLINE_EDITOR__gtmId: {
        default: undefined,
        description: "",
      },
      ONLINE_EDITOR__cypressUrl: {
        default: "https://localhost:9001/",
        description: "",
      },
      DMN_DEV_SANDBOX__baseImageRegistry: {
        default: "quay.io",
        description: "",
      },
      DMN_DEV_SANDBOX__baseImageAccount: {
        default: "kie-tools",
        description: "",
      },
      DMN_DEV_SANDBOX__baseImageName: {
        default: "dmn-dev-sandbox-deployment-base-image",
        description: "",
      },
      DMN_DEV_SANDBOX__baseImageTag: {
        default: "latest",
        description: "",
      },
      DMN_DEV_SANDBOX__baseImageBuildTags: {
        default: "latest",
        description: "",
      },
      DMN_DEV_SANDBOX__onlineEditorUrl: {
        default: `https://0.0.0.0:9001`,
        description: "",
      },
    });
  },
  get env() {
    return {
      onlineEditor: {
        dev: {
          cypressUrl: getOrDefault(this.vars.ONLINE_EDITOR__cypressUrl),
          port: 9001,
        },
        gtmId: getOrDefault(this.vars.ONLINE_EDITOR__gtmId),
        buildInfo: getOrDefault(this.vars.ONLINE_EDITOR__buildInfo),
        kieSandboxExtendedServices: {
          compatibleVersion: getOrDefault(this.vars.ONLINE_EDITOR__kieSandboxExtendedServicesCompatibleVersion),
          downloadUrl: {
            linux: getOrDefault(this.vars.ONLINE_EDITOR__kieSandboxExtendedServicesDownloadUrlLinux),
            macOs: getOrDefault(this.vars.ONLINE_EDITOR__kieSandboxExtendedServicesDownloadUrlMacOs),
            windows: getOrDefault(this.vars.ONLINE_EDITOR__kieSandboxExtendedServicesDownloadUrlWindows),
          },
        },
      },
      dmnDevSandbox: {
        onlineEditorUrl: getOrDefault(this.vars.DMN_DEV_SANDBOX__onlineEditorUrl),
        baseImage: {
          registry: getOrDefault(this.vars.DMN_DEV_SANDBOX__baseImageRegistry),
          account: getOrDefault(this.vars.DMN_DEV_SANDBOX__baseImageAccount),
          name: getOrDefault(this.vars.DMN_DEV_SANDBOX__baseImageName),
          tag: getOrDefault(this.vars.DMN_DEV_SANDBOX__baseImageTag),
          buildTags: getOrDefault(this.vars.DMN_DEV_SANDBOX__baseImageBuildTags),
        },
      },
    };
  },
});
