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

const { varsWithName, getOrDefault, composeEnv } = require("@kie-tools-scripts/build-env");

const rootEnv = require("@kie-tools/root-env/env");
const extendedServicesImageEnv = require("@kie-tools/kie-sandbox-extended-services-image/env");
const corsProxyImageEnv = require("@kie-tools/cors-proxy-image/env");
const kieSandboxWebappImageEnv = require("@kie-tools/kie-sandbox-webapp-image/env");

module.exports = composeEnv([rootEnv, extendedServicesImageEnv, corsProxyImageEnv, kieSandboxWebappImageEnv], {
  vars: varsWithName({
    KIE_SANDBOX_DISTRIBUTION__kieSandboxImageRegistry: {
      default: kieSandboxWebappImageEnv.env.kieSandboxWebappImage.registry,
      description: "For the KIE Sandbox webapp image. E.g., `docker.io` or `quay.io`.",
    },
    KIE_SANDBOX_DISTRIBUTION__kieSandboxImageAccount: {
      default: kieSandboxWebappImageEnv.env.kieSandboxWebappImage.account,
      description: "For the KIE Sandbox webapp image. E.g,. `apache` or `kie-tools-bot`",
    },
    KIE_SANDBOX_DISTRIBUTION__kieSandboxImageName: {
      default: kieSandboxWebappImageEnv.env.kieSandboxWebappImage.name,
      description: "Name of the KIE Sandbox webapp image.",
    },
    KIE_SANDBOX_DISTRIBUTION__kieSandboxImageTag: {
      default: kieSandboxWebappImageEnv.env.kieSandboxWebappImage.buildTag,
      description: "Tag version of the KIE Sandbox webapp image. E.g., `main` or `10.0.x` or `10.0.0",
    },
    KIE_SANDBOX_DISTRIBUTION__kieSandboxContainerPort: {
      default: kieSandboxWebappImageEnv.env.kieSandboxWebappImage.port,
      description: "Internal port in the KIE Sandbox webapp container.",
    },
    KIE_SANDBOX_DISTRIBUTION__kieSandboxExposedPort: {
      default: "9090",
      description: "Exposed port of the KIE Sandbox webapp container.",
    },
    KIE_SANDBOX_DISTRIBUTION__extendedServicesImageRegistry: {
      default: extendedServicesImageEnv.env.extendedServicesImage.registry,
      description: "For the Extended Services image. E.g., `docker.io` or `quay.io`.",
    },
    KIE_SANDBOX_DISTRIBUTION__extendedServicesImageAccount: {
      default: extendedServicesImageEnv.env.extendedServicesImage.account,
      description: "For the Extended Services image. E.g,. `apache` or `kie-tools-bot`",
    },
    KIE_SANDBOX_DISTRIBUTION__extendedServicesImageName: {
      default: extendedServicesImageEnv.env.extendedServicesImage.name,
      description: "Name of the of the Extended Services image.",
    },
    KIE_SANDBOX_DISTRIBUTION__extendedServicesImageTag: {
      default: extendedServicesImageEnv.env.extendedServicesImage.buildTag,
      description: "Tag version of the Extended Services image. E.g., `main` or `10.0.x` or `10.0.0",
    },
    KIE_SANDBOX_DISTRIBUTION__extendedServicesContainerPort: {
      default: "21345",
      description: "Internal HTTP port in the Extended Services container.",
    },
    KIE_SANDBOX_DISTRIBUTION__extendedServicesExposedPort: {
      default: "21345",
      description: "Exposed HTTP port of the Extended Services container.",
    },
    KIE_SANDBOX_DISTRIBUTION__corsProxyImageRegistry: {
      default: corsProxyImageEnv.env.corsProxyImage.image.registry,
      description: "For the CORS proxy image. E.g., `docker.io` or `quay.io`.",
    },
    KIE_SANDBOX_DISTRIBUTION__corsProxyImageAccount: {
      default: corsProxyImageEnv.env.corsProxyImage.image.account,
      description: "For the CORS proxy image. E.g,. `apache` or `kie-tools-bot`",
    },
    KIE_SANDBOX_DISTRIBUTION__corsProxyImageName: {
      default: corsProxyImageEnv.env.corsProxyImage.image.name,
      description: "Name of the CORS proxy image.",
    },
    KIE_SANDBOX_DISTRIBUTION__corsProxyImageTag: {
      default: corsProxyImageEnv.env.corsProxyImage.image.buildTag,
      description: "Tag version of the CORS proxy image. E.g., `main` or `10.0.x` or `10.0.0",
    },
    KIE_SANDBOX_DISTRIBUTION__corsProxyContainerPort: {
      default: corsProxyImageEnv.env.corsProxyImage.image.port,
      description: "Internal HTTP port in the CORS proxy container.",
    },
    KIE_SANDBOX_DISTRIBUTION__corsProxyExposedPort: {
      default: "7081",
      description: "Exposed HTTP port of the CORS proxy container.",
    },
  }),
  get env() {
    return {
      kieSandboxDistribution: {
        kieSandboxWebapp: {
          imageRegistry: getOrDefault(this.vars.KIE_SANDBOX_DISTRIBUTION__kieSandboxImageRegistry),
          imageAccount: getOrDefault(this.vars.KIE_SANDBOX_DISTRIBUTION__kieSandboxImageAccount),
          imageName: getOrDefault(this.vars.KIE_SANDBOX_DISTRIBUTION__kieSandboxImageName),
          imageTag: getOrDefault(this.vars.KIE_SANDBOX_DISTRIBUTION__kieSandboxImageTag),
          containerPort: getOrDefault(this.vars.KIE_SANDBOX_DISTRIBUTION__kieSandboxContainerPort),
          exposedPort: getOrDefault(this.vars.KIE_SANDBOX_DISTRIBUTION__kieSandboxExposedPort),
        },
        extendedServices: {
          imageRegistry: getOrDefault(this.vars.KIE_SANDBOX_DISTRIBUTION__extendedServicesImageRegistry),
          imageAccount: getOrDefault(this.vars.KIE_SANDBOX_DISTRIBUTION__extendedServicesImageAccount),
          imageName: getOrDefault(this.vars.KIE_SANDBOX_DISTRIBUTION__extendedServicesImageName),
          imageTag: getOrDefault(this.vars.KIE_SANDBOX_DISTRIBUTION__extendedServicesImageTag),
          containerPort: getOrDefault(this.vars.KIE_SANDBOX_DISTRIBUTION__extendedServicesContainerPort),
          exposedPort: getOrDefault(this.vars.KIE_SANDBOX_DISTRIBUTION__extendedServicesExposedPort),
        },
        corsProxy: {
          imageRegistry: getOrDefault(this.vars.KIE_SANDBOX_DISTRIBUTION__corsProxyImageRegistry),
          imageAccount: getOrDefault(this.vars.KIE_SANDBOX_DISTRIBUTION__corsProxyImageAccount),
          imageName: getOrDefault(this.vars.KIE_SANDBOX_DISTRIBUTION__corsProxyImageName),
          imageTag: getOrDefault(this.vars.KIE_SANDBOX_DISTRIBUTION__corsProxyImageTag),
          containerPort: getOrDefault(this.vars.KIE_SANDBOX_DISTRIBUTION__corsProxyContainerPort),
          exposedPort: getOrDefault(this.vars.KIE_SANDBOX_DISTRIBUTION__corsProxyExposedPort),
        },
      },
    };
  },
});
