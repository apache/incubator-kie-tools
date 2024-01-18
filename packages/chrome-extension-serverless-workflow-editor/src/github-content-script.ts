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

import { startExtension } from "@kie-tools-core/chrome-extension";
import { FileInfo } from "@kie-tools-core/chrome-extension/dist/app/components/single/singleEditorView";
import { Dependencies } from "@kie-tools-core/chrome-extension/dist/app/Dependencies";
import { GitHubPageType } from "@kie-tools-core/chrome-extension/dist/app/github/GitHubPageType";
import { EditorEnvelopeLocator, EnvelopeContentType, EnvelopeMapping } from "@kie-tools-core/editor/dist/api";
import { EmbeddedEditorFile, StateControl } from "@kie-tools-core/editor/dist/channel";
import { EmbeddedEditorChannelApiImpl } from "@kie-tools-core/editor/dist/embedded";
import { SwfCombinedEditorChannelApiImpl } from "@kie-tools/serverless-workflow-combined-editor/dist/channel";
import { getFileLanguage } from "@kie-tools/serverless-workflow-language-service/dist/api";
import { SwfLanguageServiceChannelApiImpl } from "./api/SwfLanguageServiceChannelApiImpl";
import { ChromeRouter } from "./ChromeRouter";
import { ChromeExtensionSwfLanguageService } from "./languageService/ChromeExtensionSwfLanguageService";
import { extractFileExtension, removeDirectories } from "./utils";

const resourcesPathPrefix = new ChromeRouter().getResourcesPathPrefix();

function getCustomChannelApiImpl(
  pageType: GitHubPageType,
  fileInfo: FileInfo,
  stateControl: StateControl
): SwfCombinedEditorChannelApiImpl | undefined {
  if (!getFileLanguage(fileInfo.path) || pageType !== GitHubPageType.EDIT) {
    return;
  }

  const dependencies = new Dependencies();

  const embeddedEditorFile: EmbeddedEditorFile = {
    normalizedPosixPathRelativeToTheWorkspaceRoot: fileInfo.path,
    getFileContents: () => {
      return Promise.resolve(dependencies.all.edit__githubTextAreaWithFileContents()?.textContent ?? "");
    },
    isReadOnly: false,
    fileExtension: `sw.${extractFileExtension(fileInfo.path)}`,
    fileName: `${removeDirectories(fileInfo.path)}`,
  };
  const channelApiImpl = new EmbeddedEditorChannelApiImpl(stateControl, embeddedEditorFile, "en", {});

  const chromeExtensionSwfLanguageService = new ChromeExtensionSwfLanguageService();
  const languageService = chromeExtensionSwfLanguageService.getLs(fileInfo.path);
  return new SwfCombinedEditorChannelApiImpl({
    defaultApiImpl: channelApiImpl,
    swfLanguageServiceChannelApiImpl: new SwfLanguageServiceChannelApiImpl(languageService),
  });
}

startExtension({
  name: "Kogito :: Serverless workflow editor",
  extensionIconUrl: chrome.runtime.getURL("/resources/kie_icon_rgb_fullcolor_default.svg"),
  githubAuthTokenCookieName: "github-oauth-token-kie-editors",
  editorEnvelopeLocator: new EditorEnvelopeLocator(window.location.origin, [
    new EnvelopeMapping({
      type: "swf",
      filePathGlob: "**/*.sw.+(json|yml|yaml)",
      resourcesPathPrefix: `${resourcesPathPrefix}`,
      envelopeContent: {
        type: EnvelopeContentType.PATH,
        path: `${resourcesPathPrefix}/serverless-workflow-combined-editor-envelope.html`,
      },
    }),
  ]),
  getCustomChannelApiImpl,
});
