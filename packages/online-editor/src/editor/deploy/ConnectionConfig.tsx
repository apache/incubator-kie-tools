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

const USERNAME_COOKIE_NAME = "deploy-connection-username";
const HOST_COOKIE_NAME = "deploy-connection-host";
const TOKEN_COOKIE_NAME = "deploy-connection-token";

export interface ConnectionConfig {
  username: string;
  host: string;
  token: string;
}

export const EMPTY_CONFIG: ConnectionConfig = {
  username: "",
  host: "",
  token: "",
};

export function isConfigValid(config: ConnectionConfig): boolean {
  return isUsernameValid(config.username) && isHostValid(config.host) && isTokenValid(config.token);
}

export function isUsernameValid(username: string): boolean {
  return username !== undefined && username.trim().length > 0;
}

export function isHostValid(host: string): boolean {
  return host !== undefined && host.trim().length > 0;
}

export function isTokenValid(token: string): boolean {
  return token !== undefined && token.trim().length > 0;
}

export function readConfigCookie(): ConnectionConfig {
  return {
    username: getCookie(USERNAME_COOKIE_NAME) ?? "",
    host: getCookie(HOST_COOKIE_NAME) ?? "",
    token: getCookie(TOKEN_COOKIE_NAME) ?? "",
  };
}

export function resetConfigCookie(): void {
  saveConfigCookie(EMPTY_CONFIG);
}

export function saveConfigCookie(config: ConnectionConfig): void {
  setCookie(USERNAME_COOKIE_NAME, config.username);
  setCookie(HOST_COOKIE_NAME, config.host);
  setCookie(TOKEN_COOKIE_NAME, config.token);
}
