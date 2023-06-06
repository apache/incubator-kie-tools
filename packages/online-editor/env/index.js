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

const { varsWithName, getOrDefault, composeEnv, str2bool } = require("@kie-tools-scripts/build-env");

const buildEnv = require("@kie-tools/root-env/env");
const extendedServicesEnv = require("@kie-tools/extended-services/env");
const gitCorsProxyImageEnv = require("@kie-tools/git-cors-proxy-image/env");
const devPort = 9001;

module.exports = composeEnv(
  [
    // dependencies
    buildEnv,
    extendedServicesEnv,
    gitCorsProxyImageEnv,
  ],
  {
    vars: varsWithName({
      ONLINE_EDITOR__buildInfo: {
        default: `dev (${process.env.USER}) @ ${new Date().toISOString()}`,
        description: "Build information to be shown at the bottom of Home page.",
      },
      ONLINE_EDITOR__extendedServicesDownloadUrlLinux: {
        default: `https://github.com/kiegroup/kie-tools/releases/download/${buildEnv.env.root.version}/kie_sandbox_extended_services_linux_${extendedServicesEnv.env.extendedServices.version}.tar.gz`,
        description: "Download URL for Extended Services for Linux.",
      },
      ONLINE_EDITOR__extendedServicesDownloadUrlMacOs: {
        default: `https://github.com/kiegroup/kie-tools/releases/download/${buildEnv.env.root.version}/kie_sandbox_extended_services_macos_${extendedServicesEnv.env.extendedServices.version}.dmg`,
        description: "Download URL for Extended Services for macOS.",
      },
      ONLINE_EDITOR__extendedServicesDownloadUrlWindows: {
        default: `https://github.com/kiegroup/kie-tools/releases/download/${buildEnv.env.root.version}/kie_sandbox_extended_services_windows_${extendedServicesEnv.env.extendedServices.version}.exe`,
        description: "Download URL for Extended Services for Windows.",
      },
      ONLINE_EDITOR__extendedServicesCompatibleVersion: {
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
        default: `http://localhost:${extendedServicesEnv.env.extendedServices.port}`,
        description: "Extended Services URL.",
      },
      ONLINE_EDITOR__requireCustomCommitMessage: {
        default: `${false}`,
        description: "Require users to type a custom commit message when creating a new commit.",
      },
      ONLINE_EDITOR__customCommitMessageValidationServiceUrl: {
        default: "",
        description: "Service URL to validate commit messages.",
      },
      ONLINE_EDITOR__appName: {
        default: "KIE Sandbox",
        description: "The name used to refer to a particular KIE Sandbox distribution.",
      },
      ONLINE_EDITOR__dmnDevDeploymentBaseImageRegistry: {
        default: "quay.io",
        description: "Image registry to be used by DMN Dev deployments when deploying DMN models.",
      },
      ONLINE_EDITOR__dmnDevDeploymentBaseImageAccount: {
        default: "kie-tools",
        description: "Image account to be used by DMN Dev deployments when deploying DMN models.",
      },
      ONLINE_EDITOR__dmnDevDeploymentBaseImageName: {
        default: "dmn-dev-deployment-base-image",
        description: "Image name to be used by DMN Dev deployments when deploying DMN models.",
      },
      ONLINE_EDITOR__dmnDevDeploymentBaseImageTag: {
        default: "daily-dev",
        description: "Image tag to be used by DMN Dev deployments when deploying DMN models.",
      },
      ONLINE_EDITOR__dmnDevDeploymentBaseImagePullPolicy: {
        default: "Always",
        description: "The image pull policy. Can be 'Always', 'IfNotPresent', or 'Never'.",
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
          extendedServices: {
            compatibleVersion: getOrDefault(this.vars.ONLINE_EDITOR__extendedServicesCompatibleVersion),
            downloadUrl: {
              linux: getOrDefault(this.vars.ONLINE_EDITOR__extendedServicesDownloadUrlLinux),
              macOs: getOrDefault(this.vars.ONLINE_EDITOR__extendedServicesDownloadUrlMacOs),
              windows: getOrDefault(this.vars.ONLINE_EDITOR__extendedServicesDownloadUrlWindows),
            },
          },
          appName: getOrDefault(this.vars.ONLINE_EDITOR__appName),
          extendedServicesUrl: getOrDefault(this.vars.ONLINE_EDITOR__extendedServicesUrl),
          gitCorsProxyUrl: getOrDefault(this.vars.ONLINE_EDITOR__gitCorsProxyUrl),
          requireCustomCommitMessage: str2bool(getOrDefault(this.vars.ONLINE_EDITOR__requireCustomCommitMessage)),
          customCommitMessageValidationServiceUrl: getOrDefault(
            this.vars.ONLINE_EDITOR__customCommitMessageValidationServiceUrl
          ),
        },
        devDeployments: {
          dmn: {
            imagePullPolicy: getOrDefault(this.vars.ONLINE_EDITOR__dmnDevDeploymentBaseImagePullPolicy),
            baseImage: {
              tag: getOrDefault(this.vars.ONLINE_EDITOR__dmnDevDeploymentBaseImageTag),
              registry: getOrDefault(this.vars.ONLINE_EDITOR__dmnDevDeploymentBaseImageRegistry),
              account: getOrDefault(this.vars.ONLINE_EDITOR__dmnDevDeploymentBaseImageAccount),
              name: getOrDefault(this.vars.ONLINE_EDITOR__dmnDevDeploymentBaseImageName),
            },
          },
        },
      };
    },
  }
);
