/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

const { varsWithName, getOrDefault, composeEnv, str2bool } = require("@kie-tools-scripts/build-env");

const buildEnv = require("@kie-tools/root-env/env");
const extendedServicesEnv = require("@kie-tools/extended-services/env");
const corsProxyEnv = require("@kie-tools/cors-proxy/env");

module.exports = composeEnv(
  [
    // dependencies
    buildEnv,
    extendedServicesEnv,
    corsProxyEnv,
  ],
  {
    vars: varsWithName({
      ONLINE_EDITOR__buildInfo: {
        default: `dev (${process.env.USER}) @ ${new Date().toISOString()}`,
        description: "Build information to be shown at the bottom of Home page.",
      },
      ONLINE_EDITOR__extendedServicesDownloadUrlLinux: {
        default: `https://github.com/apache/incubator-kie-tools/releases/download/${buildEnv.env.root.version}/kie_sandbox_extended_services_linux_${extendedServicesEnv.env.extendedServices.version}.tar.gz`,
        description: "Download URL for Extended Services for Linux.",
      },
      ONLINE_EDITOR__extendedServicesDownloadUrlMacOs: {
        default: `https://github.com/apache/incubator-kie-tools/releases/download/${buildEnv.env.root.version}/kie_sandbox_extended_services_macos_${extendedServicesEnv.env.extendedServices.version}.dmg`,
        description: "Download URL for Extended Services for macOS.",
      },
      ONLINE_EDITOR__extendedServicesDownloadUrlWindows: {
        default: `https://github.com/apache/incubator-kie-tools/releases/download/${buildEnv.env.root.version}/kie_sandbox_extended_services_windows_${extendedServicesEnv.env.extendedServices.version}.exe`,
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
      ONLINE_EDITOR__corsProxyUrl: {
        default: `http://localhost:${corsProxyEnv.env.corsProxy.dev.port}`,
        description: "CORS Proxy URL.",
      },
      ONLINE_EDITOR__extendedServicesUrl: {
        default: `http://${extendedServicesEnv.env.extendedServices.host}:${extendedServicesEnv.env.extendedServices.port}`,
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
      ONLINE_EDITOR__devDeploymentBaseImageRegistry: {
        default: "quay.io",
        description: "Image registry to be used by Dev deployments when deploying models.",
      },
      ONLINE_EDITOR__devDeploymentBaseImageAccount: {
        default: "kie-tools",
        description: "Image account to be used by Dev deployments when deploying models.",
      },
      ONLINE_EDITOR__devDeploymentBaseImageName: {
        default: "dev-deployment-base-image",
        description: "Image name to be used by Dev deployments when deploying models.",
      },
      ONLINE_EDITOR__devDeploymentBaseImageTag: {
        default: "daily-dev",
        description: "Image tag to be used by Dev deployments when deploying models.",
      },
      ONLINE_EDITOR__devDeploymentKogitoQuarkusBlankAppImageRegistry: {
        default: "quay.io",
        description: "Image registry to be used by Dev deployments when deploying models.",
      },
      ONLINE_EDITOR__devDeploymentKogitoQuarkusBlankAppImageAccount: {
        default: "kie-tools",
        description: "Image account to be used by Dev deployments when deploying models.",
      },
      ONLINE_EDITOR__devDeploymentKogitoQuarkusBlankAppImageName: {
        default: "dev-deployment-kogito-quarkus-blank-app-image",
        description: "Image name to be used by Dev deployments when deploying models.",
      },
      ONLINE_EDITOR__devDeploymentKogitoQuarkusBlankAppImageTag: {
        default: "daily-dev",
        description: "Image tag to be used by Dev deployments when deploying models.",
      },
      ONLINE_EDITOR__devDeploymentDmnFormWebappImageRegistry: {
        default: "quay.io",
        description: "Image registry to be used by Dev deployments to display a form for deployed DMN models.",
      },
      ONLINE_EDITOR__devDeploymentDmnFormWebappImageAccount: {
        default: "kie-tools",
        description: "Image account to be used by Dev deployments to display a form for deployed DMN models.",
      },
      ONLINE_EDITOR__devDeploymentDmnFormWebappImageName: {
        default: "dev-deployment-dmn-form-webapp-image",
        description: "Image name to be used by Dev deployments to display a form for deployed DMN models.",
      },
      ONLINE_EDITOR__devDeploymentDmnFormWebappImageTag: {
        default: "daily-dev",
        description: "Image tag to be used by Dev deployments to display a form for deployed DMN models.",
      },
      ONLINE_EDITOR__devDeploymentImagePullPolicy: {
        default: "IfNotPresent",
        description: "The image pull policy. Can be 'Always', 'IfNotPresent', or 'Never'.",
      },
      ONLINE_EDITOR_DEV__port: {
        default: 9001,
        description: "The development web server port",
      },
      ONLINE_EDITOR_DEV__https: {
        default: "true",
        description: "Tells if the development web server should use https",
      },
    }),
    get env() {
      return {
        onlineEditor: {
          dev: {
            port: getOrDefault(this.vars.ONLINE_EDITOR_DEV__port),
            https: str2bool(getOrDefault(this.vars.ONLINE_EDITOR_DEV__https)),
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
          corsProxyUrl: getOrDefault(this.vars.ONLINE_EDITOR__corsProxyUrl),
          requireCustomCommitMessage: str2bool(getOrDefault(this.vars.ONLINE_EDITOR__requireCustomCommitMessage)),
          customCommitMessageValidationServiceUrl: getOrDefault(
            this.vars.ONLINE_EDITOR__customCommitMessageValidationServiceUrl
          ),
        },
        devDeployments: {
          imagePullPolicy: getOrDefault(this.vars.ONLINE_EDITOR__devDeploymentImagePullPolicy),
          baseImage: {
            tag: getOrDefault(this.vars.ONLINE_EDITOR__devDeploymentBaseImageTag),
            registry: getOrDefault(this.vars.ONLINE_EDITOR__devDeploymentBaseImageRegistry),
            account: getOrDefault(this.vars.ONLINE_EDITOR__devDeploymentBaseImageAccount),
            name: getOrDefault(this.vars.ONLINE_EDITOR__devDeploymentBaseImageName),
          },
          kogitoQuarkusBlankAppImage: {
            tag: getOrDefault(this.vars.ONLINE_EDITOR__devDeploymentKogitoQuarkusBlankAppImageTag),
            registry: getOrDefault(this.vars.ONLINE_EDITOR__devDeploymentKogitoQuarkusBlankAppImageRegistry),
            account: getOrDefault(this.vars.ONLINE_EDITOR__devDeploymentKogitoQuarkusBlankAppImageAccount),
            name: getOrDefault(this.vars.ONLINE_EDITOR__devDeploymentKogitoQuarkusBlankAppImageName),
          },
          dmnFormWebappImage: {
            tag: getOrDefault(this.vars.ONLINE_EDITOR__devDeploymentDmnFormWebappImageTag),
            registry: getOrDefault(this.vars.ONLINE_EDITOR__devDeploymentDmnFormWebappImageRegistry),
            account: getOrDefault(this.vars.ONLINE_EDITOR__devDeploymentDmnFormWebappImageAccount),
            name: getOrDefault(this.vars.ONLINE_EDITOR__devDeploymentDmnFormWebappImageName),
          },
        },
      };
    },
  }
);
