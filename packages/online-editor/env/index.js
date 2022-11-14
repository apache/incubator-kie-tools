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

const { varsWithName, getOrDefault, composeEnv } = require("@kie-tools-scripts/build-env");

const buildEnv = require("@kie-tools/root-env/env");
const extendedServicesEnv = require("@kie-tools/extended-services/env");
const gitCorsProxyImageEnv = require("@kie-tools/git-cors-proxy-image/env");

const devPort = 9001;

module.exports = composeEnv(
  [
    buildEnv,
    require("@kie-tools/dmn-dev-sandbox-deployment-base-image-env/env"),
    extendedServicesEnv,
    gitCorsProxyImageEnv,
  ],
  {
    vars: varsWithName({
      ONLINE_EDITOR__buildInfo: {
        default: `dev (${process.env.USER}) @ ${new Date().toISOString()}`,
        description: "Build information to be shown at the bottom of Home page.",
      },
      ONLINE_EDITOR__kieSandboxExtendedServicesDownloadUrlLinux: {
        default: `https://github.com/kiegroup/kie-tools/releases/download/${buildEnv.env.root.version}/kie_sandbox_extended_services_linux_${extendedServicesEnv.env.extendedServices.version}.tar.gz`,
        description: "Download URL for Extended Services for Linux.",
      },
      ONLINE_EDITOR__kieSandboxExtendedServicesDownloadUrlMacOs: {
        default: `https://github.com/kiegroup/kie-tools/releases/download/${buildEnv.env.root.version}/kie_sandbox_extended_services_macos_${extendedServicesEnv.env.extendedServices.version}.dmg`,
        description: "Download URL for Extended Services for macOS.",
      },
      ONLINE_EDITOR__kieSandboxExtendedServicesDownloadUrlWindows: {
        default: `https://github.com/kiegroup/kie-tools/releases/download/${buildEnv.env.root.version}/kie_sandbox_extended_services_windows_${extendedServicesEnv.env.extendedServices.version}.exe`,
        description: "Download URL for Extended Services for Windows.",
      },
      ONLINE_EDITOR__kieSandboxExtendedServicesCompatibleVersion: {
        default: extendedServicesEnv.env.extendedServices.version,
        description:
          "Version Extended Services compatile with KIE Sandbox. Exact match only. No version ranges are supported.",
      },
      ONLINE_EDITOR__gtmId: {
        default: undefined,
        description: "Google Tag Manager ID. Used for analytics.",
      },
      ONLINE_EDITOR__cypressUrl: {
        default: `https://localhost:${devPort}/`,
        description: "Cypress URL to be used on integrationt tests.",
      },
      ONLINE_EDITOR__gitCorsProxyUrl: {
        default: `http://localhost:${gitCorsProxyImageEnv.env.gitCorsProxy.dev.port}`,
        description: "Git CORS Proxy URL.",
      },
      ONLINE_EDITOR__extendedServicesUrl: {
        default: `http://localhost:${extendedServicesEnv.env.extendedServices.dev.port}`,
        description: "Extended Services URL.",
      },
      DMN_DEV_SANDBOX__baseImageTag: {
        default: "latest",
        description: "Image tag to be used by DMN Dev Sandbox when deploying DMN models to OpenShift.",
      },
      DMN_DEV_SANDBOX__onlineEditorUrl: {
        default: `https://0.0.0.0:${devPort}`,
        description: "URL that DMN Dev Sandbox deployments will use to open KIE Sandbox from its deployments.",
      },
    }),
    get env() {
      return {
        onlineEditor: {
          dev: {
            cypressUrl: getOrDefault(this.vars.ONLINE_EDITOR__cypressUrl),
            port: devPort,
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
          extendedServicesUrl: getOrDefault(this.vars.ONLINE_EDITOR__extendedServicesUrl),
          gitCorsProxyUrl: getOrDefault(this.vars.ONLINE_EDITOR__gitCorsProxyUrl),
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
