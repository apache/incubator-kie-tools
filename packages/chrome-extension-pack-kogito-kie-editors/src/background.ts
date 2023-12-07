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

import { extractFileExtension, removeDirectories } from "./utils";

chrome.runtime.onInstalled.addListener(() => {
  console.log("Apache KIE Tools extension is running.");
});

/* Active tab management */

let activeTabId: number;

chrome.tabs.onActivated.addListener((activeInfo) => {
  activeTabId = activeInfo.tabId;
});

function getActiveTab(callback: (tab: any) => void) {
  chrome.tabs.query({ currentWindow: true, active: true }, (tabs) => {
    const activeTab = tabs[0];

    if (activeTab) {
      callback(activeTab);
    } else {
      chrome.tabs.get(activeTabId, (tab) => {
        if (tab) {
          callback(tab);
        } else {
          console.debug("No active tab identified.");
        }
      });
    }
  });
}

chrome.runtime.onMessage.addListener((request, sender, sendResponse) => {
  if (request.messageId === "OPEN_ONLINE_EDITOR") {
    openOnlineEditor(request, sender, sendResponse);
  } else if (request.messageId === "RETURN_FROM_EXTERNAL_EDITOR") {
    updateGitHub(request, sender);
  }
  sendResponse({ success: true });
});

function openOnlineEditor(request: any, sender: chrome.runtime.MessageSender, sendResponse: (response: any) => void) {
  chrome.tabs.create(
    { url: `${process.env.WEBPACK_REPLACE__onlineEditor_url}/?ext#/editor/` + extractFileExtension(request.filePath) },
    (tab) => {
      let newTabReady = () => {
        newTabReady = () => {
          /**/
        };
        chrome.tabs.onUpdated.removeListener(tabUpdateListener);
        chrome.tabs.sendMessage(tab.id!, {
          messageId: "LOAD_ONLINE_EDITOR",
          filePath: removeDirectories(request.filePath),
          fileContent: request.fileContent,
          readonly: request.readonly,
          senderTabId: sender.tab!.id!,
        });
      };

      chrome.tabs.get(tab.id!, (newTab) => {
        if (newTab.status === "complete") {
          newTabReady();
        }
      });

      const tabUpdateListener = (updatedTabId: number, changeInfo: any) => {
        if (updatedTabId === tab.id && changeInfo.status === "complete") {
          newTabReady();
        }
        sendResponse({ success: true });
      };

      chrome.tabs.onUpdated.addListener(tabUpdateListener);
    }
  );
}

function updateGitHub(request: any, sender: chrome.runtime.MessageSender) {
  chrome.tabs.sendMessage(
    request.senderTabId,
    {
      messageId: "RETURN_FROM_EXTERNAL_EDITOR",
      fileName: request.fileName,
      fileContent: request.fileContent,
    },
    (response) => {
      if (response?.success) {
        chrome.tabs.remove(sender.tab!.id!, () => {
          chrome.tabs.update(request.senderTabId, { active: true, selected: true });
        });
      }
    }
  );
}
