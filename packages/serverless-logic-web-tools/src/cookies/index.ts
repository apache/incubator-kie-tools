/*
 * Copyright 2022 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

export function getCookie(name: string) {
  const value = "; " + document.cookie;
  const parts = value.split("; " + name + "=");

  if (parts.length === 2) {
    return parts.pop()!.split(";").shift();
  }
}

export function setCookie(name: string, value: string) {
  const date = new Date();

  date.setMonth(date.getMonth() + 5);

  document.cookie = name + "=" + value + "; expires=" + date.toUTCString() + "; path=/";
}

export const makeCookieName = (group: string, name: string) => `KIE-TOOLS__serverless-logic-sandbox__${group}--${name}`;

/**
 * Delete all cookies
 */
export const deleteAllCookies = () => {
  document.cookie.split(";").forEach(function (c) {
    document.cookie = c.replace(/^ +/, "").replace(/=.*/, "=;expires=" + new Date().toUTCString() + ";path=/");
  });
};
