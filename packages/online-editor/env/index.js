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

const rootEnv = require("@kie-tools/root-env/env");
const extendedServicesEnv = require("@kie-tools/extended-services/env");
const extendedServicesJavaEnv = require("@kie-tools/extended-services-java/env");
const corsProxyEnv = require("@kie-tools/cors-proxy/env");
const kieSandboxAcceleratorQuarkusEnv = require("@kie-tools/kie-sandbox-accelerator-quarkus/env");

module.exports = composeEnv([rootEnv, extendedServicesJavaEnv, corsProxyEnv, kieSandboxAcceleratorQuarkusEnv], {
  vars: varsWithName({
    ONLINE_EDITOR__buildInfo: {
      default: `dev (${process.env.USER}) @ ${new Date().toISOString()}`,
      description: "Build information to be shown at the bottom of Home page.",
    },
    ONLINE_EDITOR__extendedServicesDownloadUrlLinux: {
      default: `https://github.com/apache/incubator-kie-tools/releases/download/${rootEnv.env.root.version}/kie_sandbox_extended_services_linux_${extendedServicesEnv.env.extendedServices.version}.tar.gz`,
      description: "Download URL for Extended Services for Linux.",
    },
    ONLINE_EDITOR__extendedServicesDownloadUrlMacOs: {
      default: `https://github.com/apache/incubator-kie-tools/releases/download/${rootEnv.env.root.version}/kie_sandbox_extended_services_macos_${extendedServicesEnv.env.extendedServices.version}.dmg`,
      description: "Download URL for Extended Services for macOS.",
    },
    ONLINE_EDITOR__extendedServicesDownloadUrlWindows: {
      default: `https://github.com/apache/incubator-kie-tools/releases/download/${rootEnv.env.root.version}/kie_sandbox_extended_services_windows_${extendedServicesEnv.env.extendedServices.version}.exe`,
      description: "Download URL for Extended Services for Windows.",
    },
    ONLINE_EDITOR__extendedServicesCompatibleVersion: {
      default: extendedServicesEnv.env.extendedServices.version,
      description:
        "Version Extended Services compatile with KIE Sandbox. Exact match only. No version ranges are supported.",
    },
    ONLINE_EDITOR__corsProxyUrl: {
      default: `http://localhost:${corsProxyEnv.env.corsProxy.dev.port}`,
      description: "CORS Proxy URL.",
    },
    ONLINE_EDITOR__extendedServicesUrl: {
      default: `http://${extendedServicesJavaEnv.env.extendedServicesJava.host}:${extendedServicesJavaEnv.env.extendedServicesJava.port}`,
      description: "Extended Services URL.",
    },
    ONLINE_EDITOR__disableExtendedServicesWizard: {
      default: `${false}`,
      description: "Disables the Extended Services Wizard.",
    },
    ONLINE_EDITOR__feedbackUrl: {
      default: "https://github.com/apache/incubator-kie-issues/issues/439#issuecomment-1821845917",
      description: "URL where users can give feedback, currently present in the New DMN Editor dropdown.",
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
      default: "Apache KIE™ Sandbox",
      description: "The name used to refer to a particular KIE Sandbox distribution.",
    },
    ONLINE_EDITOR__devDeploymentBaseImageRegistry: {
      default: "docker.io",
      description: "Image registry to be used by Dev Deployments when deploying models.",
    },
    ONLINE_EDITOR__devDeploymentBaseImageAccount: {
      default: "apache",
      description: "Image account to be used by Dev Deployments when deploying models.",
    },
    ONLINE_EDITOR__devDeploymentBaseImageName: {
      default: "incubator-kie-sandbox-dev-deployment-base",
      description: "Image name to be used by Dev Deployments when deploying models.",
    },
    ONLINE_EDITOR__devDeploymentBaseImageTag: {
      default: rootEnv.env.root.streamName,
      description: "Image tag to be used by Dev Deployments when deploying models.",
    },
    ONLINE_EDITOR__devDeploymentQuarkusBlankAppImageRegistry: {
      default: "docker.io",
      description: "Image registry to be used by Dev Deployments when deploying models.",
    },
    ONLINE_EDITOR__devDeploymentQuarkusBlankAppImageAccount: {
      default: "apache",
      description: "Image account to be used by Dev Deployments when deploying models.",
    },
    ONLINE_EDITOR__devDeploymentQuarkusBlankAppImageName: {
      default: "incubator-kie-sandbox-dev-deployment-quarkus-blank-app",
      description: "Image name to be used by Dev Deployments when deploying models.",
    },
    ONLINE_EDITOR__devDeploymentQuarkusBlankAppImageTag: {
      default: rootEnv.env.root.streamName,
      description: "Image tag to be used by Dev Deployments when deploying models.",
    },
    ONLINE_EDITOR__devDeploymentDmnFormWebappImageRegistry: {
      default: "docker.io",
      description: "Image registry to be used by Dev Deployments to display a form for deployed DMN models.",
    },
    ONLINE_EDITOR__devDeploymentDmnFormWebappImageAccount: {
      default: "apache",
      description: "Image account to be used by Dev Deployments to display a form for deployed DMN models.",
    },
    ONLINE_EDITOR__devDeploymentDmnFormWebappImageName: {
      default: "incubator-kie-sandbox-dev-deployment-dmn-form-webapp",
      description: "Image name to be used by Dev Deployments to display a form for deployed DMN models.",
    },
    ONLINE_EDITOR__devDeploymentDmnFormWebappImageTag: {
      default: rootEnv.env.root.streamName,
      description: "Image tag to be used by Dev Deployments to display a form for deployed DMN models.",
    },
    ONLINE_EDITOR__devDeploymentImagePullPolicy: {
      default: "IfNotPresent",
      description: "The image pull policy. Can be 'Always', 'IfNotPresent', or 'Never'.",
    },
    ONLINE_EDITOR__quarkusAcceleratorGitRepoUrl: {
      default: `http://localhost:${kieSandboxAcceleratorQuarkusEnv.env.kieSandboxAcceleratorQuarkus.dev.port}/git-repo-bare.git`,
      description: "Default Quarkus Accelerator's Git repository URL.",
    },
    ONLINE_EDITOR__quarkusAcceleratorGitRef: {
      default: "main",
      description: "Default Quarkus Accelerator's Git ref to be used when cloning it.",
    },
    ONLINE_EDITOR_DEV__port: {
      default: 9001,
      description: "The development web server port",
    },
    ONLINE_EDITOR_DEV__https: {
      default: "false",
      description: "Tells if the development web server should use https",
    },
    ONLINE_EDITOR__skipPlaywrightTestsForArm64: {
      default: "false",
      description: "Skip Playwright tests for ARM64 architecture.",
    },
  }),
  get env() {
    return {
      onlineEditor: {
        dev: {
          port: getOrDefault(this.vars.ONLINE_EDITOR_DEV__port),
          https: str2bool(getOrDefault(this.vars.ONLINE_EDITOR_DEV__https)),
        },
        test: {
          skipForArm64: getOrDefault(this.vars.ONLINE_EDITOR__skipPlaywrightTestsForArm64),
        },
        buildInfo: getOrDefault(this.vars.ONLINE_EDITOR__buildInfo),
        extendedServices: {
          compatibleVersion: getOrDefault(this.vars.ONLINE_EDITOR__extendedServicesCompatibleVersion),
          downloadUrl: {
            linux: getOrDefault(this.vars.ONLINE_EDITOR__extendedServicesDownloadUrlLinux),
            macOs: getOrDefault(this.vars.ONLINE_EDITOR__extendedServicesDownloadUrlMacOs),
            windows: getOrDefault(this.vars.ONLINE_EDITOR__extendedServicesDownloadUrlWindows),
          },
        },
        accelerators: {
          quarkus: {
            gitRepoUrl: getOrDefault(this.vars.ONLINE_EDITOR__quarkusAcceleratorGitRepoUrl),
            gitRef: getOrDefault(this.vars.ONLINE_EDITOR__quarkusAcceleratorGitRef),
          },
        },
        appName: getOrDefault(this.vars.ONLINE_EDITOR__appName),
        extendedServicesUrl: getOrDefault(this.vars.ONLINE_EDITOR__extendedServicesUrl),
        disableExtendedServicesWizard: str2bool(getOrDefault(this.vars.ONLINE_EDITOR__disableExtendedServicesWizard)),
        corsProxyUrl: getOrDefault(this.vars.ONLINE_EDITOR__corsProxyUrl),
        feedbackUrl: getOrDefault(this.vars.ONLINE_EDITOR__feedbackUrl),
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
        quarkusBlankAppImage: {
          tag: getOrDefault(this.vars.ONLINE_EDITOR__devDeploymentQuarkusBlankAppImageTag),
          registry: getOrDefault(this.vars.ONLINE_EDITOR__devDeploymentQuarkusBlankAppImageRegistry),
          account: getOrDefault(this.vars.ONLINE_EDITOR__devDeploymentQuarkusBlankAppImageAccount),
          name: getOrDefault(this.vars.ONLINE_EDITOR__devDeploymentQuarkusBlankAppImageName),
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
});
