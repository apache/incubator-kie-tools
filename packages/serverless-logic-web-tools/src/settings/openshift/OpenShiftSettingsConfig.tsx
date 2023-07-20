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

import { makeCookieName, getCookie, setCookie } from "../../cookies";
import { KubernetesConnection } from "@kie-tools-core/kubernetes-bridge/dist/service";

export const OPENSHIFT_NAMESPACE_COOKIE_NAME = makeCookieName("openshift", "namespace");
export const OPENSHIFT_HOST_COOKIE_NAME = makeCookieName("openshift", "host");
export const OPENSHIFT_TOKEN_COOKIE_NAME = makeCookieName("openshift", "token");
export const OPENSHIFT_DEV_MODE_ENABLED_NAME = makeCookieName("openshift", "dev-mode-enabled");

export const EMPTY_CONFIG: KubernetesConnection = {
  namespace: "",
  host: "",
  token: "",
};

export function readOpenShiftConfigCookie(): KubernetesConnection {
  return {
    namespace: getCookie(OPENSHIFT_NAMESPACE_COOKIE_NAME) ?? "",
    host: getCookie(OPENSHIFT_HOST_COOKIE_NAME) ?? "",
    token: getCookie(OPENSHIFT_TOKEN_COOKIE_NAME) ?? "",
  };
}

export function resetConfigCookie(): void {
  saveConfigCookie(EMPTY_CONFIG);
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

export function saveConfigCookie(config: KubernetesConnection): void {
  saveNamespaceCookie(config.namespace);
  saveHostCookie(config.host);
  saveTokenCookie(config.token);
}

export function readDevModeEnabledConfigCookie(): boolean {
  const devModeEnabledCookie = getCookie(OPENSHIFT_DEV_MODE_ENABLED_NAME);
  return devModeEnabledCookie ? devModeEnabledCookie === "true" : false;
}

export function saveDevModeEnabledConfigCookie(isEnabled: boolean): void {
  setCookie(OPENSHIFT_DEV_MODE_ENABLED_NAME, String(isEnabled));
}
