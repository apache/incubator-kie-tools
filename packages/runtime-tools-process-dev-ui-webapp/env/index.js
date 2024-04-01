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

const { varsWithName, composeEnv, getOrDefault } = require("@kie-tools-scripts/build-env");

module.exports = composeEnv([require("@kie-tools/root-env/env")], {
  vars: varsWithName({
    RUNTIME_TOOLS_PROCESS_DEV_UI_WEBAPP__kogitoDataIndexUrl: {
      default: "http://localhost:4000/graphql",
      description: "URL for the Data Index service",
    },
    RUNTIME_TOOLS_PROCESS_DEV_UI_WEBAPP__kogitoAppUrl: {
      default: null,
      description:
        "URL to a remote Kogito Application. If set, the devUI will use the url to fetch OpenApi doc instead of using the mock value from the server.js.",
    },
    RUNTIME_TOOLS_PROCESS_DEV_UI_WEBAPP__openApiDocPath: {
      default: "/q/openapi.json",
      description: "Relative path to the OpenApi document to load the Process Definitions.",
    },
    RUNTIME_TOOLS_PROCESS_DEV_UI_WEBAPP__host: {
      default: "localhost",
      description: "Webpack server hostname",
    },
    RUNTIME_TOOLS_PROCESS_DEV_UI_WEBAPP__port: {
      default: "9027",
      description: "Webpack server port",
    },
  }),
  get env() {
    return {
      runtimeToolsProcessDevUIWebapp: {
        kogitoDataIndexUrl: getOrDefault(this.vars.RUNTIME_TOOLS_PROCESS_DEV_UI_WEBAPP__kogitoDataIndexUrl),
        kogitoAppUrl: getOrDefault(this.vars.RUNTIME_TOOLS_PROCESS_DEV_UI_WEBAPP__kogitoAppUrl),
        openApiPath: getOrDefault(this.vars.RUNTIME_TOOLS_PROCESS_DEV_UI_WEBAPP__openApiDocPath),
        host: getOrDefault(this.vars.RUNTIME_TOOLS_PROCESS_DEV_UI_WEBAPP__host),
        port: getOrDefault(this.vars.RUNTIME_TOOLS_PROCESS_DEV_UI_WEBAPP__port),
      },
    };
  },
});
