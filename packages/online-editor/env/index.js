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

const { varsWithName, getOrDefault, composeEnv } = require("@kie-tools-build/build-env");

const buildEnv = require("@kie-tools/root-env/env");
const extendedServicesEnv = require("@kie-tools/extended-services/env");

module.exports = composeEnv(
  [buildEnv, require("@kie-tools/dmn-dev-sandbox-deployment-base-image-env/env"), extendedServicesEnv],
  {
    vars: varsWithName({
      ONLINE_EDITOR__buildInfo: {
        default: `dev (${process.env.USER}) @ ${new Date().toISOString()}`,
        description: "",
      },
      ONLINE_EDITOR__kieSandboxExtendedServicesDownloadUrlLinux: {
        default: `https://github.com/kiegroup/kie-tools/releases/download/${buildEnv.env.root.version}/kie_sandbox_extended_services_linux_${extendedServicesEnv.env.extendedServices.version}.tar.gz`,
        description: "",
      },
      ONLINE_EDITOR__kieSandboxExtendedServicesDownloadUrlMacOs: {
        default: `https://github.com/kiegroup/kie-tools/releases/download/${buildEnv.env.root.version}/kie_sandbox_extended_services_macos_${extendedServicesEnv.env.extendedServices.version}.dmg`,
        description: "",
      },
      ONLINE_EDITOR__kieSandboxExtendedServicesDownloadUrlWindows: {
        default: `https://github.com/kiegroup/kie-tools/releases/download/${buildEnv.env.root.version}/kie_sandbox_extended_services_windows_${extendedServicesEnv.env.extendedServices.version}.exe`,
        description: "",
      },
      ONLINE_EDITOR__kieSandboxExtendedServicesCompatibleVersion: {
        default: extendedServicesEnv.env.extendedServices.version,
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
      ONLINE_EDITOR__corsProxyUrl: {
        default:
          "https://cors-proxy-kie-sandbox.rhba-cluster-0ad6762cc85bcef5745bb684498c2436-0000.us-south.containers.appdomain.cloud",
        description: "",
      },
      DMN_DEV_SANDBOX__baseImageTag: {
        default: "latest",
        description: "",
      },
      DMN_DEV_SANDBOX__onlineEditorUrl: {
        default: `https://0.0.0.0:9001`,
        description: "",
      },
    }),
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
          corsProxyUrl: getOrDefault(this.vars.ONLINE_EDITOR__corsProxyUrl),
        },
        dmnDevSandbox: {
          onlineEditorUrl: getOrDefault(this.vars.DMN_DEV_SANDBOX__onlineEditorUrl),
          baseImage: {
            tag: getOrDefault(this.vars.DMN_DEV_SANDBOX__baseImageTag),
          },
        },
      };
    },
  }
);
