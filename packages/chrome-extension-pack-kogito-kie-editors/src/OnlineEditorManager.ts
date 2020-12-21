/*
 * Copyright 2019 Red Hat, Inc. and/or its affiliates.
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

import { ExternalEditorManager } from "@kogito-tooling/chrome-extension";
import { extractFileExtension } from "./utils";

export class OnlineEditorManager implements ExternalEditorManager {
  public name = "Online Editor";

  public open(filePath: string, fileContent: string, readonly: boolean) {
    chrome.runtime.sendMessage(
      chrome.runtime.id,
      { messageId: "OPEN_ONLINE_EDITOR", filePath, fileContent, readonly },
      response => {
        if (!response?.success) {
          console.debug("Error during online editor opening.");
        }
      }
    );
  }

  public getLink(filePath: string) {
    return `$_{WEBPACK_REPLACE__onlineEditor_url}/?file=https://raw.githubusercontent.com/${filePath}#/editor/${extractFileExtension(filePath)}`;
  }

  public listenToComeBack(setFileName: (fileName: string) => void, setFileContent: (fileContent: string) => void) {
    const listener = (request: any, sender: chrome.runtime.MessageSender, sendResponse: (response: any) => void) => {
      if (request.messageId === "RETURN_FROM_EXTERNAL_EDITOR") {
        setFileName(request.fileName);
        setFileContent(request.fileContent);
      }
      sendResponse({ success: true });
    };

    chrome.runtime.onMessage.addListener(listener);

    return { stopListening: () => chrome.runtime.onMessage.removeListener(listener) };
  }
}
