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

const { envVars, getOrDefault, compositeEnv } = require("@kie-tools/build-env");

const buildEnv = require("@kie-tools/build-env/env");
const { version } = require("@kie-tools/build-env/package.json");

module.exports = compositeEnv([buildEnv], {
  vars: envVars({
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
  }),
  get env() {
    return {
      serverlessLogicSandbox: {
        buildInfo: getOrDefault(this.vars.SERVERLESS_LOGIC_SANDBOX__buildInfo),
        gtmId: getOrDefault(this.vars.SERVERLESS_LOGIC_SANDBOX__gtmId),
        dev: {
          port: 9020,
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
      },
    };
  },
});
