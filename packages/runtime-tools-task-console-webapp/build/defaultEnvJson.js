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

const { env: buildEnv } = require("../env");

const version = require("../package.json").version;

module.exports = {
  defaultEnvJson: {
    RUNTIME_TOOLS_TASK_CONSOLE_KOGITO_ENV_MODE: buildEnv.runtimeToolsTaskConsoleWebapp.kogitoEnvMode,
    RUNTIME_TOOLS_TASK_CONSOLE_KOGITO_APP_NAME: "Task Console",
    RUNTIME_TOOLS_TASK_CONSOLE_KOGITO_APP_VERSION: version,
    RUNTIME_TOOLS_TASK_CONSOLE_KOGITO_TASK_STATES_LIST: "Ready,Reserved,Completed,Aborted,Skipped",
    RUNTIME_TOOLS_TASK_CONSOLE_KOGITO_TASK_ACTIVE_STATES_LIST: "Ready,Reserved",
    RUNTIME_TOOLS_TASK_CONSOLE_DATA_INDEX_ENDPOINT: buildEnv.runtimeToolsTaskConsoleWebapp.kogitoDataIndexUrl,
    KOGITO_CONSOLES_KEYCLOAK_DISABLE_HEALTH_CHECK: false,
    KOGITO_CONSOLES_KEYCLOAK_UPDATE_TOKEN_VALIDITY: 30,
    KOGITO_CONSOLES_KEYCLOAK_HEALTH_CHECK_URL:
      "http://localhost:8280/auth/realms/kogito/.well-known/openid-configuration",
    KOGITO_CONSOLES_KEYCLOAK_REALM: "kogito",
    KOGITO_CONSOLES_KEYCLOAK_URL: "http://localhost:8280/auth",
    KOGITO_CONSOLES_KEYCLOAK_CLIENT_ID: "kogito-console-react",
  },
};
