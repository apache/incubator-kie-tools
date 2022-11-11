/*
 * Copyright 2021 Red Hat, Inc. and/or its affiliates.
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

import * as React from "react";
import { useContext } from "react";

type EnvVarNames = "KIE_SANDBOX_EXTENDED_SERVICES_URL" | "SERVERLESS_LOGIC_SANDBOX_GIT_CORS_PROXY_URL";

export type EnvVars = Record<EnvVarNames, string>;

export const DEFAULT_KIE_SANDBOX_EXTENDED_SERVICES_HOST = "http://localhost";
export const DEFAULT_KIE_SANDBOX_EXTENDED_SERVICES_PORT = "21345";

export const DEFAULT_ENV_VARS: EnvVars = {
  KIE_SANDBOX_EXTENDED_SERVICES_URL: `${DEFAULT_KIE_SANDBOX_EXTENDED_SERVICES_HOST}:${DEFAULT_KIE_SANDBOX_EXTENDED_SERVICES_PORT}`,
  SERVERLESS_LOGIC_SANDBOX_GIT_CORS_PROXY_URL: process.env.WEBPACK_REPLACE__gitCorsProxyUrl ?? "",
};

export interface EnvContextType {
  vars: EnvVars;
}

export const EnvContext = React.createContext<EnvContextType>({} as any);

export function useEnv() {
  return useContext(EnvContext);
}
