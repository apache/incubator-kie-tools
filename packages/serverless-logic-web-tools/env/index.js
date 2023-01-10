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

const { version } = require("@kie-tools-scripts/build-env/package.json");

module.exports = composeEnv(
  [
    require("@kie-tools/root-env/env"),
    require("@kie-tools/serverless-logic-web-tools-swf-builder-image-env/env"),
    require("@kie-tools/serverless-logic-web-tools-base-builder-image-env/env"),
    require("@kie-tools/dashbuilder-viewer-image-env/env"),
  ],
  {
    vars: varsWithName({
      SERVERLESS_LOGIC_WEB_TOOLS__buildInfo: {
        default: `dev (${process.env.USER}) @ ${new Date().toISOString()}`,
        description: "",
      },
      SERVERLESS_LOGIC_WEB_TOOLS__gtmId: {
        default: undefined,
        description: "",
      },
      SERVERLESS_LOGIC_WEB_TOOLS__kieSandboxExtendedServicesDownloadUrlLinux: {
        default: `https://github.com/kiegroup/kie-tools/releases/download/${version}/kie_sandbox_extended_services_linux_${version}.tar.gz`,
        description: "",
      },
      SERVERLESS_LOGIC_WEB_TOOLS__kieSandboxExtendedServicesDownloadUrlMacOs: {
        default: `https://github.com/kiegroup/kie-tools/releases/download/${version}/kie_sandbox_extended_services_macos_${version}.dmg`,
        description: "",
      },
      SERVERLESS_LOGIC_WEB_TOOLS__kieSandboxExtendedServicesDownloadUrlWindows: {
        default: `https://github.com/kiegroup/kie-tools/releases/download/${version}/kie_sandbox_extended_services_windows_${version}.exe`,
        description: "",
      },
      SERVERLESS_LOGIC_WEB_TOOLS__kieSandboxExtendedServicesCompatibleVersion: {
        default: version,
        description: "",
      },
      SERVERLESS_LOGIC_WEB_TOOLS__swfBuilderImageTag: {
        default: "latest",
        description: "",
      },
      SERVERLESS_LOGIC_WEB_TOOLS__baseBuilderImageTag: {
        default: "latest",
        description: "",
      },
      SERVERLESS_LOGIC_WEB_TOOLS__dashbuilderViewerImageTag: {
        default: "latest",
        description: "",
      },
      SERVERLESS_LOGIC_WEB_TOOLS__gitCorsProxyUrl: {
        default: "https://cors.isomorphic-git.org",
        description: "",
      },
      SERVERLESS_LOGIC_WEB_TOOLS__cypressUrl: {
        default: "https://localhost:9020/",
        description: "",
      },
    }),
    get env() {
      return {
        serverlessLogicWebTools: {
          buildInfo: getOrDefault(this.vars.SERVERLESS_LOGIC_WEB_TOOLS__buildInfo),
          gtmId: getOrDefault(this.vars.SERVERLESS_LOGIC_WEB_TOOLS__gtmId),
          dev: {
            cypressUrl: getOrDefault(this.vars.SERVERLESS_LOGIC_WEB_TOOLS__cypressUrl),
            port: 9020,
          },
          swfBuilderImage: {
            tag: getOrDefault(this.vars.SERVERLESS_LOGIC_WEB_TOOLS__swfBuilderImageTag),
          },
          baseBuilderImage: {
            tag: getOrDefault(this.vars.SERVERLESS_LOGIC_WEB_TOOLS__baseBuilderImageTag),
          },
          dashbuilderViewerImage: {
            tag: getOrDefault(this.vars.SERVERLESS_LOGIC_WEB_TOOLS__dashbuilderViewerImageTag),
          },
          kieSandboxExtendedServices: {
            compatibleVersion: getOrDefault(
              this.vars.SERVERLESS_LOGIC_WEB_TOOLS__kieSandboxExtendedServicesCompatibleVersion
            ),
            downloadUrl: {
              linux: getOrDefault(this.vars.SERVERLESS_LOGIC_WEB_TOOLS__kieSandboxExtendedServicesDownloadUrlLinux),
              macOs: getOrDefault(this.vars.SERVERLESS_LOGIC_WEB_TOOLS__kieSandboxExtendedServicesDownloadUrlMacOs),
              windows: getOrDefault(this.vars.SERVERLESS_LOGIC_WEB_TOOLS__kieSandboxExtendedServicesDownloadUrlWindows),
            },
          },
          gitCorsProxyUrl: getOrDefault(this.vars.SERVERLESS_LOGIC_WEB_TOOLS__gitCorsProxyUrl),
        },
      };
    },
  }
);
