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

import { getCookie, makeCookieName, setCookie } from "../../cookies";

export const RUNTIME_TOOLS_DATA_INDEX_URL_COOKIE_NAME = makeCookieName("runtime-tools", "data-index-url");
export const RUNTIME_TOOLS_KOGITO_SERVICE_URL_COOKIE_NAME = makeCookieName("runtime-tools", "kogito-service-url");

export interface RuntimeToolsSettingsConfig {
  dataIndexUrl: string;
  kogitoServiceUrl: string;
}

export const EMPTY_CONFIG: RuntimeToolsSettingsConfig = {
  dataIndexUrl: "",
  kogitoServiceUrl: "",
};

export function isRuntimeToolsConfigValid(config: RuntimeToolsSettingsConfig): boolean {
  return isDataIndexUrlValid(config.dataIndexUrl) && isKogitoServiceUrlValid(config.kogitoServiceUrl);
}

export function isDataIndexUrlValid(dataIndexUrl: string): boolean {
  return dataIndexUrl !== undefined && dataIndexUrl.trim().length > 0;
}

export function isKogitoServiceUrlValid(kogitoServiceUrl: string): boolean {
  return kogitoServiceUrl !== undefined && kogitoServiceUrl.trim().length > 0;
}

export function readRuntimeToolsConfigCookie(): RuntimeToolsSettingsConfig {
  return {
    dataIndexUrl: getCookie(RUNTIME_TOOLS_DATA_INDEX_URL_COOKIE_NAME) ?? "",
    kogitoServiceUrl: getCookie(RUNTIME_TOOLS_KOGITO_SERVICE_URL_COOKIE_NAME) ?? "",
  };
}

export function resetConfigCookie(): void {
  saveConfigCookie(EMPTY_CONFIG);
}

export function saveDataIndexUrlCookie(dataIndexUrl: string): void {
  setCookie(RUNTIME_TOOLS_DATA_INDEX_URL_COOKIE_NAME, dataIndexUrl);
}

export function saveKogitoServiceUrlCookie(kogitoServiceUrl: string): void {
  setCookie(RUNTIME_TOOLS_KOGITO_SERVICE_URL_COOKIE_NAME, kogitoServiceUrl);
}

export function saveConfigCookie(config: RuntimeToolsSettingsConfig): void {
  saveDataIndexUrlCookie(config.dataIndexUrl);
  saveKogitoServiceUrlCookie(config.kogitoServiceUrl);
}
