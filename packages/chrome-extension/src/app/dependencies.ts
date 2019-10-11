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

export const singleEdit = {
  iframeContainer: () => document.querySelector(".file"),
  toolbarContainer: () => document.querySelector(".breadcrumb.d-flex.flex-items-center"),
  githubTextEditorToReplace: () => document.querySelector(".js-code-editor") as HTMLElement,
  githubTextAreaWithFileContents: () => document.querySelector(".file-editor-textarea") as HTMLTextAreaElement
};

export const singleView = {
  rawUrlLink: () => document.getElementById("raw-url") as HTMLAnchorElement,
  iframeContainer: () => document.querySelector(".Box.mt-3.position-relative"),
  toolbarContainer: () => document.querySelector(".Box.mt-3.position-relative"),
  githubTextEditorToReplace: () => document.querySelector(".Box-body.p-0.blob-wrapper.data") as HTMLElement
};

export const prView = {
  mutationObserverTarget: () => document.getElementById("files"),
  toolbarContainer: (container: HTMLElement) => container.querySelector(".file-info"),
  viewOriginalFileLinkContainer: (container: HTMLElement) => container.querySelectorAll("details-menu a")[0],
  githubTextEditorToReplace: (container: HTMLElement) => container.querySelector(".js-file-content"),
  supportedPrFileElements: () => {
    return Array.from(document.querySelectorAll(".file.js-file.js-details-container")).map(e => e as HTMLElement);
  },
  unprocessedFilePath: (container: HTMLElement) => {
    return container.querySelector(".file-info > .link-gray-dark") as HTMLAnchorElement;
  },
  getMetaInfo: () => {
    const querySelector = document.querySelector(".gh-header-meta");
    return !querySelector ? undefined : Array.from(querySelector.querySelectorAll(".css-truncate-target"));
  }
};
