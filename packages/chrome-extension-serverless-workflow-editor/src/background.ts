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

import HttpHeader = chrome.webRequest.HttpHeader;
import { extractFileExtension, removeDirectories } from "./utils";

chrome.runtime.onInstalled.addListener(() => {
  console.log("KIE Tools extension is running.");
});

function removeHeader(headers: HttpHeader[], name: string) {
  for (let i = 0; i < headers.length; i++) {
    if (headers[i].name.toLowerCase() === name) {
      headers.splice(i, 1);
      break;
    }
  }
}

chrome.webRequest.onHeadersReceived.addListener(
  (details) => {
    removeHeader(details.responseHeaders!, "content-security-policy");
    removeHeader(details.responseHeaders!, "x-frame-options");
    return { responseHeaders: details.responseHeaders };
  },
  { urls: ["https://github.com/*"] },
  ["blocking", "responseHeaders"]
);

/* Active tab management */

let activeTabId: number;

chrome.tabs.onActivated.addListener((activeInfo) => {
  activeTabId = activeInfo.tabId;
});
