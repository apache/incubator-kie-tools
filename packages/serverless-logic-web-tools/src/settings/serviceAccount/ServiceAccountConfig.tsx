/*
 * Copyright 2023 Red Hat, Inc. and/or its affiliates.
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

import { makeCookieName, getCookie, setCookie } from "../../cookies";

export const SERVICE_ACCOUNT_CLIENT_ID_COOKIE_NAME = makeCookieName("service-account", "client-id");
export const SERVICE_ACCOUNT_CLIENT_SECRET_COOKIE_NAME = makeCookieName("service-account", "client-secret");

export interface ServiceAccountSettingsConfig {
  clientId: string;
  clientSecret: string;
}

export const EMPTY_CONFIG: ServiceAccountSettingsConfig = {
  clientId: "",
  clientSecret: "",
};

export function isServiceAccountConfigValid(config: ServiceAccountSettingsConfig): boolean {
  return isClientIdValid(config.clientId) && isClientSecretValid(config.clientSecret);
}

export function isClientIdValid(clientId: string): boolean {
  return clientId !== undefined && clientId.trim().length > 0;
}

export function isClientSecretValid(clientSecret: string): boolean {
  return clientSecret !== undefined && clientSecret.trim().length > 0;
}

export function readServiceAccountConfigCookie(): ServiceAccountSettingsConfig {
  return {
    clientId: getCookie(SERVICE_ACCOUNT_CLIENT_ID_COOKIE_NAME) ?? "",
    clientSecret: getCookie(SERVICE_ACCOUNT_CLIENT_SECRET_COOKIE_NAME) ?? "",
  };
}

export function resetConfigCookie(): void {
  saveConfigCookie(EMPTY_CONFIG);
}

export function saveClientIdCookie(clientId: string): void {
  setCookie(SERVICE_ACCOUNT_CLIENT_ID_COOKIE_NAME, clientId);
}

export function saveClientSecretCookie(clientSecret: string): void {
  setCookie(SERVICE_ACCOUNT_CLIENT_SECRET_COOKIE_NAME, clientSecret);
}

export function saveConfigCookie(config: ServiceAccountSettingsConfig): void {
  saveClientIdCookie(config.clientId);
  saveClientSecretCookie(config.clientSecret);
}
