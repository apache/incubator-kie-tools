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

module.exports = composeEnv([require("@kie-tools/root-env/env")], {
  vars: varsWithName({
    WORKFLOW_ENDPOINTS_WEBAPP_title: {
      default: "WORKFLOW ENDPOINTS",
      description: "Application title",
    },
    WORKFLOW_ENDPOINTS_WEBAPP_logo: {
      default: "favicon.svg",
      description: "Path to logo image",
    },
    WORKFLOW_ENDPOINTS_WEBAPP_docLinkHref: {
      default: "https://sonataflow.org/serverlessworkflow/latest/index.html",
      description: "Documentation link URL",
    },
    WORKFLOW_ENDPOINTS_WEBAPP_docLinkText: {
      default: "Sonataflow docs",
      description: "Documentation link text",
    },
  }),
  get env() {
    return {
      sonataflowWorkflowEndpointsWebapp: {
        dev: {
          port: 9027,
        },
        title: getOrDefault(this.vars.WORKFLOW_ENDPOINTS_WEBAPP_title),
        logo: getOrDefault(this.vars.WORKFLOW_ENDPOINTS_WEBAPP_logo),
        docLink: {
          href: getOrDefault(this.vars.WORKFLOW_ENDPOINTS_WEBAPP_docLinkHref),
          text: getOrDefault(this.vars.WORKFLOW_ENDPOINTS_WEBAPP_docLinkText),
        },
      },
    };
  },
});
