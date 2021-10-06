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

import { getCookie, setCookie } from "../../common/utils";

const NAMESPACE_COOKIE_NAME = "dmn-dev-sandbox--connection-namespace";
const HOST_COOKIE_NAME = "dmn-dev-sandbox--connection-host";
const TOKEN_COOKIE_NAME = "dmn-dev-sandbox--connection-token";

export interface DmnDevSandboxConnectionConfig {
  namespace: string;
  host: string;
  token: string;
}

export const EMPTY_CONFIG: DmnDevSandboxConnectionConfig = {
  namespace: "",
  host: "",
  token: "",
};

export function isConfigValid(config: DmnDevSandboxConnectionConfig): boolean {
  return isNamespaceValid(config.namespace) && isHostValid(config.host) && isTokenValid(config.token);
}

export function isNamespaceValid(username: string): boolean {
  return username !== undefined && username.trim().length > 0;
}

export function isHostValid(host: string): boolean {
  if (!host || host.trim().length === 0) {
    return false;
  }
  try {
    new URL(host);
    return true;
  } catch (_) {
    return false;
  }
}

export function isTokenValid(token: string): boolean {
  return token !== undefined && token.trim().length > 0;
}

export function readConfigCookie(): DmnDevSandboxConnectionConfig {
  return {
    namespace: getCookie(NAMESPACE_COOKIE_NAME) ?? "",
    host: getCookie(HOST_COOKIE_NAME) ?? "",
    token: getCookie(TOKEN_COOKIE_NAME) ?? "",
  };
}

export function resetConfigCookie(): void {
  saveConfigCookie(EMPTY_CONFIG);
}

export function saveConfigCookie(config: DmnDevSandboxConnectionConfig): void {
  setCookie(NAMESPACE_COOKIE_NAME, config.namespace);
  setCookie(HOST_COOKIE_NAME, config.host);
  setCookie(TOKEN_COOKIE_NAME, config.token);
}
