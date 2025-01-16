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
const { version } = require("@kie-tools-scripts/build-env/package.json");

const rootEnv = require("@kie-tools/root-env/env");
const corsProxyEnv = require("@kie-tools/cors-proxy/env");

module.exports = composeEnv(
  [
    rootEnv,
    require("@kie-tools/serverless-logic-web-tools-swf-builder-image-env/env"),
    require("@kie-tools/serverless-logic-web-tools-swf-dev-mode-image-env/env"),
    require("@kie-tools/serverless-logic-web-tools-base-builder-image-env/env"),
    require("@kie-tools/dashbuilder-viewer-image-env/env"),
  ],
  {
    vars: varsWithName({
      SERVERLESS_LOGIC_WEB_TOOLS__buildInfo: {
        default: `dev (${process.env.USER}) @ ${new Date().toISOString()}`,
        description: "Build information",
      },
      SERVERLESS_LOGIC_WEB_TOOLS__version: {
        default: version,
        description: "Version of the application",
      },
      SERVERLESS_LOGIC_WEB_TOOLS__samplesRepositoryOrg: {
        default: "kiegroup",
        description: "Org owner for `kiegroup/kie-samples` repository",
      },
      SERVERLESS_LOGIC_WEB_TOOLS__samplesRepositoryName: {
        default: "kie-samples",
        description: "Repository name for `kiegroup/kie-samples` repository",
      },
      SERVERLESS_LOGIC_WEB_TOOLS__samplesRepositoryRef: {
        default: "main",
        description: "Tag/branch to fetch samples from `kiegroup/kie-samples` repository",
      },
      SERVERLESS_LOGIC_WEB_TOOLS__gtmId: {
        default: undefined,
        description: "Google Tag Manager ID for Analytics",
      },
      SERVERLESS_LOGIC_WEB_TOOLS__swfBuilderImageTag: {
        default: rootEnv.env.root.streamName,
        description:
          "Tag for the Serverless Workflow Builder Image that has a pre-configured Serverless Workflow project",
      },
      SERVERLESS_LOGIC_WEB_TOOLS__baseBuilderImageTag: {
        default: rootEnv.env.root.streamName,
        description: "Tag for the Base Builder Image that is able to build Java projects with Maven",
      },
      SERVERLESS_LOGIC_WEB_TOOLS__dashbuilderViewerImageTag: {
        default: rootEnv.env.root.streamName,
        description: "Tag for the Dashbuilder Viewer Image that has a pre-configured project to load Dashbuilder files",
      },
      SERVERLESS_LOGIC_WEB_TOOLS__swfDevModeImageTag: {
        default: rootEnv.env.root.streamName,
        description:
          "Tag for the Serverless Workflow Dev Mode Image that runs a pre-configured Serverless Workflow project in Quarkus Dev Mode",
      },
      SERVERLESS_LOGIC_WEB_TOOLS__corsProxyUrl: {
        default: `http://localhost:${corsProxyEnv.env.corsProxy.dev.port}`,
        description: "CORS Proxy URL.",
      },
      SERVERLESS_LOGIC_WEB_TOOLS__cypressUrl: {
        default: "https://localhost:9020/",
        description: "The application URL for Cypress",
      },
    }),
    get env() {
      return {
        serverlessLogicWebTools: {
          version: getOrDefault(this.vars.SERVERLESS_LOGIC_WEB_TOOLS__version),
          buildInfo: getOrDefault(this.vars.SERVERLESS_LOGIC_WEB_TOOLS__buildInfo),
          gtmId: getOrDefault(this.vars.SERVERLESS_LOGIC_WEB_TOOLS__gtmId),
          dev: {
            cypressUrl: getOrDefault(this.vars.SERVERLESS_LOGIC_WEB_TOOLS__cypressUrl),
            port: 9020,
          },
          slwtBuilderImageEnv: {
            tag: getOrDefault(this.vars.SERVERLESS_LOGIC_WEB_TOOLS__swfBuilderImageTag),
          },
          slwtBaseBuilderImage: {
            tag: getOrDefault(this.vars.SERVERLESS_LOGIC_WEB_TOOLS__baseBuilderImageTag),
          },
          dashbuilderViewerImage: {
            tag: getOrDefault(this.vars.SERVERLESS_LOGIC_WEB_TOOLS__dashbuilderViewerImageTag),
          },
          slwtDevModeImage: {
            tag: getOrDefault(this.vars.SERVERLESS_LOGIC_WEB_TOOLS__swfDevModeImageTag),
          },
          corsProxyUrl: getOrDefault(this.vars.SERVERLESS_LOGIC_WEB_TOOLS__corsProxyUrl),
          samplesRepositoryOrg: getOrDefault(this.vars.SERVERLESS_LOGIC_WEB_TOOLS__samplesRepositoryOrg),
          samplesRepositoryName: getOrDefault(this.vars.SERVERLESS_LOGIC_WEB_TOOLS__samplesRepositoryName),
          samplesRepositoryRef: getOrDefault(this.vars.SERVERLESS_LOGIC_WEB_TOOLS__samplesRepositoryRef),
        },
      };
    },
  }
);
