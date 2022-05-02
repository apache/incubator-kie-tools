/*
 * Copyright 2022 Red Hat, Inc. and/or its affiliates.
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

import { getCookie, setCookie } from "../../cookies";

export const OPENSHIFT_PROXY_COOKIE_NAME = "KIE-TOOLS-COOKIE__swf-sandbox--connection-proxy";
export const OPENSHIFT_NAMESPACE_COOKIE_NAME = "KIE-TOOLS-COOKIE__swf-sandbox--connection-namespace";
export const OPENSHIFT_HOST_COOKIE_NAME = "KIE-TOOLS-COOKIE__swf-sandbox--connection-host";
export const OPENSHIFT_TOKEN_COOKIE_NAME = "KIE-TOOLS-COOKIE__swf-sandbox--connection-token";

export interface OpenShiftSettingsConfig {
  proxy: string;
  namespace: string;
  host: string;
  token: string;
}

export const EMPTY_CONFIG: OpenShiftSettingsConfig = {
  proxy: "",
  namespace: "",
  host: "",
  token: "",
};

export function isOpenShiftConfigValid(config: OpenShiftSettingsConfig): boolean {
  return (
    isProxyValid(config.proxy) &&
    isNamespaceValid(config.namespace) &&
    isHostValid(config.host) &&
    isTokenValid(config.token)
  );
}

export function isProxyValid(proxy: string): boolean {
  if (!proxy || proxy.trim().length === 0) {
    return false;
  }
  try {
    new URL(proxy);
    return true;
  } catch (_) {
    return false;
  }
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

export function readOpenShiftConfigCookie(): OpenShiftSettingsConfig {
  return {
    proxy:
      getCookie(OPENSHIFT_PROXY_COOKIE_NAME) ??
      "https://daily-dev-kie-sandbox-extended-services-kie-sandbox.rhba-cluster-0ad6762cc85bcef5745bb684498c2436-0000.us-south.containers.appdomain.cloud",
    namespace: getCookie(OPENSHIFT_NAMESPACE_COOKIE_NAME) ?? "",
    host: getCookie(OPENSHIFT_HOST_COOKIE_NAME) ?? "",
    token: getCookie(OPENSHIFT_TOKEN_COOKIE_NAME) ?? "",
  };
}

export function resetConfigCookie(): void {
  saveConfigCookie(EMPTY_CONFIG);
}

export function saveProxyCookie(proxy: string): void {
  setCookie(OPENSHIFT_PROXY_COOKIE_NAME, proxy);
}

export function saveNamespaceCookie(namespace: string): void {
  setCookie(OPENSHIFT_NAMESPACE_COOKIE_NAME, namespace);
}

export function saveHostCookie(host: string): void {
  setCookie(OPENSHIFT_HOST_COOKIE_NAME, host);
}

export function saveTokenCookie(token: string): void {
  setCookie(OPENSHIFT_TOKEN_COOKIE_NAME, token);
}

export function saveConfigCookie(config: OpenShiftSettingsConfig): void {
  saveProxyCookie(config.proxy);
  saveNamespaceCookie(config.namespace);
  saveHostCookie(config.host);
  saveTokenCookie(config.token);
}
