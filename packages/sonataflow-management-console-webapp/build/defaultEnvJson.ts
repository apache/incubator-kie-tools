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

import { EnvJson } from "../src/env/EnvJson";

// eslint-disable-next-line @typescript-eslint/ban-ts-comment
// @ts-ignore
import { env } from "../env";
const buildEnv: any = env; // build-env is not typed

const version = require("../package.json").version;

export const defaultEnvJson: EnvJson = {
  KOGITO_CONSOLES_KEYCLOAK_CLIENT_ID: "kogito-console-react",
  KOGITO_CONSOLES_KEYCLOAK_DISABLE_HEALTH_CHECK: false,
  KOGITO_CONSOLES_KEYCLOAK_HEALTH_CHECK_URL: "http://localhost:8280/realms/kogito/.well-known/openid-configuration",
  KOGITO_CONSOLES_KEYCLOAK_REALM: "kogito",
  KOGITO_CONSOLES_KEYCLOAK_UPDATE_TOKEN_VALIDITY: 30,
  KOGITO_CONSOLES_KEYCLOAK_URL: "http://localhost:8280",
  SONATAFLOW_MANAGEMENT_CONSOLE_KOGITO_APP_NAME: "SonataFlow Management Console",
  SONATAFLOW_MANAGEMENT_CONSOLE_KOGITO_APP_VERSION: version,
  SONATAFLOW_MANAGEMENT_CONSOLE_KOGITO_ENV_MODE: buildEnv.sonataflowManagementConsoleWebapp.sonataflowEnvMode,
};
