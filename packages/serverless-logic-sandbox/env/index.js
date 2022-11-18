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
    require("@kie-tools/serverless-logic-sandbox-base-image-env/env"),
    require("@kie-tools/openjdk11-mvn-image-env/env"),
  ],
  {
    vars: varsWithName({
      SERVERLESS_LOGIC_SANDBOX__buildInfo: {
        default: `dev (${process.env.USER}) @ ${new Date().toISOString()}`,
        description: "",
      },
      SERVERLESS_LOGIC_SANDBOX__gtmId: {
        default: undefined,
        description: "",
      },
      SERVERLESS_LOGIC_SANDBOX__kieSandboxExtendedServicesDownloadUrlLinux: {
        default: `https://github.com/kiegroup/kie-tools/releases/download/${version}/kie_sandbox_extended_services_linux_${version}.tar.gz`,
        description: "",
      },
      SERVERLESS_LOGIC_SANDBOX__kieSandboxExtendedServicesDownloadUrlMacOs: {
        default: `https://github.com/kiegroup/kie-tools/releases/download/${version}/kie_sandbox_extended_services_macos_${version}.dmg`,
        description: "",
      },
      SERVERLESS_LOGIC_SANDBOX__kieSandboxExtendedServicesDownloadUrlWindows: {
        default: `https://github.com/kiegroup/kie-tools/releases/download/${version}/kie_sandbox_extended_services_windows_${version}.exe`,
        description: "",
      },
      SERVERLESS_LOGIC_SANDBOX__kieSandboxExtendedServicesCompatibleVersion: {
        default: version,
        description: "",
      },
      SERVERLESS_LOGIC_SANDBOX__baseImageTag: {
        default: "latest",
        description: "",
      },
      SERVERLESS_LOGIC_SANDBOX__openJdk11MvnImageTag: {
        default: "latest",
        description: "",
      },
      SERVERLESS_LOGIC_SANDBOX__gitCorsProxyUrl: {
        default: "https://cors.isomorphic-git.org",
        description: "",
      },
      SERVERLESS_LOGIC_SANDBOX__cypressUrl: {
        default: "https://localhost:9020/",
        description: "",
      },
    }),
    get env() {
      return {
        serverlessLogicSandbox: {
          buildInfo: getOrDefault(this.vars.SERVERLESS_LOGIC_SANDBOX__buildInfo),
          gtmId: getOrDefault(this.vars.SERVERLESS_LOGIC_SANDBOX__gtmId),
          dev: {
            cypressUrl: getOrDefault(this.vars.SERVERLESS_LOGIC_SANDBOX__cypressUrl),
            port: 9020,
          },
          baseImage: {
            tag: getOrDefault(this.vars.SERVERLESS_LOGIC_SANDBOX__baseImageTag),
          },
          openJdk11MvnImage: {
            tag: getOrDefault(this.vars.SERVERLESS_LOGIC_SANDBOX__openJdk11MvnImageTag),
          },
          kieSandboxExtendedServices: {
            compatibleVersion: getOrDefault(
              this.vars.SERVERLESS_LOGIC_SANDBOX__kieSandboxExtendedServicesCompatibleVersion
            ),
            downloadUrl: {
              linux: getOrDefault(this.vars.SERVERLESS_LOGIC_SANDBOX__kieSandboxExtendedServicesDownloadUrlLinux),
              macOs: getOrDefault(this.vars.SERVERLESS_LOGIC_SANDBOX__kieSandboxExtendedServicesDownloadUrlMacOs),
              windows: getOrDefault(this.vars.SERVERLESS_LOGIC_SANDBOX__kieSandboxExtendedServicesDownloadUrlWindows),
            },
          },
          gitCorsProxyUrl: getOrDefault(this.vars.SERVERLESS_LOGIC_SANDBOX__gitCorsProxyUrl),
        },
      };
    },
  }
);
