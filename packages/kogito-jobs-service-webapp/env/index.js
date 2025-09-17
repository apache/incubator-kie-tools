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
    KOGITO_JOBS_SERVICE_WEBAPP_title: {
      default: "KOGITO JOBS SERVICE",
      description: "Application title",
    },
    KOGITO_JOBS_SERVICE_WEBAPP_logo: {
      default: "favicon.svg",
      description: "Path to logo image",
    },
    KOGITO_JOBS_SERVICE_WEBAPP_docLinkHref: {
      default: "https://docs.kogito.kie.org/latest/html_single/#con-jobs-service_kogito-configuring",
      description: "Documentation link URL",
    },
    KOGITO_JOBS_SERVICE_WEBAPP_docLinkText: {
      default: "Kogito Jobs Service docs",
      description: "Documentation link text",
    },
    SONATAFLOW_JOBS_SERVICE_WEBAPP_docLinkHref: {
      default: "https://sonataflow.org/serverlessworkflow/latest/job-services/core-concepts.html",
      description: "Documentation link URL",
    },
    SONATAFLOW_JOBS_SERVICE_WEBAPP_docLinkText: {
      default: "SonataFlow Jobs Service docs",
      description: "Documentation link text",
    },
  }),
  get env() {
    return {
      kogitoJobsServiceWebapp: {
        dev: {
          port: 9028,
        },
        title: getOrDefault(this.vars.KOGITO_JOBS_SERVICE_WEBAPP_title),
        logo: getOrDefault(this.vars.KOGITO_JOBS_SERVICE_WEBAPP_logo),
        docLinkKogito: {
          href: getOrDefault(this.vars.KOGITO_JOBS_SERVICE_WEBAPP_docLinkHref),
          text: getOrDefault(this.vars.KOGITO_JOBS_SERVICE_WEBAPP_docLinkText),
        },
        docLinkSonataflow: {
          href: getOrDefault(this.vars.SONATAFLOW_JOBS_SERVICE_WEBAPP_docLinkHref),
          text: getOrDefault(this.vars.SONATAFLOW_JOBS_SERVICE_WEBAPP_docLinkText),
        },
      },
    };
  },
});
