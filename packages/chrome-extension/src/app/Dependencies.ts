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

export class Dependencies {
  public readonly singleEdit = {
    iframeContainerTarget: () => {
      return (
        document.querySelector("[class$='react-code-view-edit']")?.parentElement?.parentElement ??
        (null as HTMLElement | null)
      );
    },
    toolbarContainerTarget: () => {
      return (
        document.querySelector("[class$='react-code-view-edit']")?.parentElement?.parentElement ??
        (null as HTMLElement | null)
      );
    },
    githubTextEditorToReplaceElement: () => {
      return document.querySelector("[class$='react-code-view-edit']")?.parentElement ?? (null as HTMLElement | null);
    },
  };

  public readonly singleView = {
    iframeContainerTarget: () => {
      return document.getElementById("highlighted-line-menu-positioner") as HTMLElement | null;
    },
    toolbarContainerTarget: () => {
      return document.querySelector("[class$='react-code-size-details-banner']") as HTMLElement | null;
    },
    githubTextEditorToReplaceElement: () => {
      return document.getElementById("highlighted-line-menu-positioner") as HTMLElement | null;
    },
  };

  public readonly prView = {
    iframeContainerTarget: (container: HTMLElement) => {
      return container as HTMLElement | null;
    },
    toolbarContainerTarget: (container: HTMLElement) => {
      return container.querySelector(".file-info") as HTMLElement | null;
    },
    githubTextEditorToReplaceElement: (container: HTMLElement) => {
      return container.querySelector(".js-file-content") as HTMLElement | null;
    },
  };

  public readonly openRepoInExternalEditor = {
    buttonContainerOnRepoHome: () => {
      return document.querySelector(".pagehead-actions") as HTMLElement | null;
    },
    buttonContainerOnRepoFilesList: () => {
      return document.querySelector(".d-flex.gap-2")?.parentElement as HTMLElement | null;
    },
    buttonContainerOnPrs: () => {
      return document.querySelector(".gh-header-actions") as HTMLElement | null;
    },
  };

  public readonly all = {
    octiconMarkGitHub: () => {
      return document.querySelector(".octicon-mark-github") as HTMLElement | null;
    },
    notificationIndicator: () => {
      return (document.querySelector(".notification-indicator") ??
        document.querySelector(".AppHeader-search")) as HTMLElement | null;
    },
    notLoggedInNotificationIndicator: () => {
      return document.querySelector("#repository-details-container") as HTMLInputElement | null;
    },
    body: () => {
      return document.body;
    },
    edit__githubFileNameInput: () => {
      return document.querySelector("[aria-describedby=file-name-editor-breadcrumb]") as HTMLInputElement | null;
    },
    edit__githubTextAreaWithFileContents: () => {
      return document.getElementById("kie-tools__initial-content") as HTMLTextAreaElement | null;
    },
    pr__filesMutationObserverTarget: () => {
      return document.getElementById("files") as HTMLElement | null;
    },
    pr__commitsMutationObserverTarget: () => {
      return document.getElementById("commits_bucket") as HTMLElement | null;
    },
    pr__homeMutationObserverTarget: () => {
      return document.querySelector(".pull-discussion-timeline") as HTMLElement | null;
    },
    pr__openWithExternalEditorLinkContainer: (container: HTMLElement) => {
      return container.querySelectorAll("details-menu a")[0] as HTMLAnchorElement | null;
    },
    pr__viewOriginalFileLinkContainer: (container: HTMLElement) => {
      return container.querySelectorAll("details-menu a")[0] as HTMLAnchorElement | null;
    },
    pr__unprocessedFilePathContainer: (container: HTMLElement) => {
      return container.querySelector(".file-info a.Link--primary") as HTMLAnchorElement | null;
    },

    array: {
      pr__supportedPrFileContainers: () => {
        const elements = Array.from(document.querySelectorAll(".file.js-file.js-details-container")).map(
          (e) => e as HTMLElement
        );
        return elements.length > 0 ? (elements as HTMLElement[]) : null;
      },

      pr__prInfoContainer: () => {
        const elements = Array.from(document.querySelectorAll(".gh-header-meta .css-truncate-target"));
        return elements.length > 0 ? (elements as HTMLElement[]) : null;
      },
    },
    hideDocumentBody: () => {
      (document.querySelector("[data-turbo-body]") as HTMLElement).style.display = "none";
    },
    showDocumentBody: () => {
      (document.querySelector("[data-turbo-body]") as HTMLElement).style.display = "unset";
    },
    getViewFileButton: () => {
      return document.querySelector("a[class='pl-5 dropdown-item btn-link']") as HTMLAnchorElement | null;
    },
  };
}
