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

declare var __IS_WIRED__: boolean;

declare global {
  interface Window {
    isWired: boolean;
    wireStyle: (href: string) => void;
  }
}

export const setupWire = () => {
  const linkElement = (href: string) => {
    const link = document.createElement("link");
    link.rel = "stylesheet";
    link.href = href;
    return link;
  };

  window.isWired = __IS_WIRED__;

  window.wireStyle = (href: string) => {
    if (!window.isWired) {
      document.head.appendChild(linkElement(href));
    }
  };
};
