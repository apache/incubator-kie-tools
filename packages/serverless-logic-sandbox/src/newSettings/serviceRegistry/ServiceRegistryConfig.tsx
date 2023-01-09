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

import { getCookie, makeCookieName, setCookie } from "../../cookies";

export const SERVICE_REGISTRY_NAME_COOKIE_NAME = makeCookieName("service-registry", "name");
export const SERVICE_REGISTRY_CORE_REGISTRY_API_COOKIE_NAME = makeCookieName("service-registry", "core-registry-api");

export interface ServiceRegistrySettingsConfig {
  name: string;
  coreRegistryApi: string;
}

export const EMPTY_CONFIG: ServiceRegistrySettingsConfig = {
  name: "",
  coreRegistryApi: "",
};

export function isServiceRegistryConfigValid(config: ServiceRegistrySettingsConfig): boolean {
  return isNameValid(config.name) && isCoreRegistryApiValid(config.coreRegistryApi);
}

export function isNameValid(name: string): boolean {
  return name !== undefined && name.trim().length > 0;
}

export function isCoreRegistryApiValid(coreRegistryApi: string): boolean {
  return coreRegistryApi !== undefined && coreRegistryApi.trim().length > 0;
}

export function readServiceRegistryConfigCookie(): ServiceRegistrySettingsConfig {
  return {
    name: getCookie(SERVICE_REGISTRY_NAME_COOKIE_NAME) ?? "",
    coreRegistryApi: getCookie(SERVICE_REGISTRY_CORE_REGISTRY_API_COOKIE_NAME) ?? "",
  };
}

export function resetConfigCookie(): void {
  saveConfigCookie(EMPTY_CONFIG);
}

export function saveNameCookie(name: string): void {
  setCookie(SERVICE_REGISTRY_NAME_COOKIE_NAME, name);
}

export function saveCoreRegistryApiCookie(coreRegistryApi: string): void {
  setCookie(SERVICE_REGISTRY_CORE_REGISTRY_API_COOKIE_NAME, coreRegistryApi);
}

export function saveConfigCookie(config: ServiceRegistrySettingsConfig): void {
  saveNameCookie(config.name);
  saveCoreRegistryApiCookie(config.coreRegistryApi);
}
