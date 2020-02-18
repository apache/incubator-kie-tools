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

export class Dependencies {
  public readonly singleEdit = {
    iframeContainerTarget: () => {
      return document.querySelector(".file") as HTMLElement | null;
    },
    toolbarContainerTarget: () => {
      return document.querySelector(".breadcrumb.d-flex.flex-items-center") as HTMLElement | null;
    },
    githubTextEditorToReplaceElement: () => {
      return document.querySelector(".js-code-editor") as HTMLElement | null;
    }
  };

  public readonly singleView = {
    iframeContainerTarget: () => {
      return document.querySelector(".Box.mt-3.position-relative") as HTMLElement | null;
    },
    toolbarContainerTarget: () => {
      return document.querySelector(".Box.mt-3.position-relative") as HTMLElement | null;
    },
    githubTextEditorToReplaceElement: () => {
      return document.querySelector(".Box-body.p-0.blob-wrapper.data") as HTMLElement | null;
    }
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
    }
  };

  public readonly treeView = {
    repositoryContainer: () => {
      return document.querySelector("div.repository-content") as HTMLElement | null;
    },

    linksToFiles: () => {
      return Array.from(
        document.querySelectorAll("table.files > tbody > tr > td.content > span > a[href*=blob]")
      ) as HTMLAnchorElement[];
    }
  };

  public readonly all = {
    notificationIndicator: () => {
      return document.querySelector(".notification-indicator") as HTMLElement | null;
    },
    body: () => {
      return document.body;
    },
    edit__githubFileNameInput: () => {
      return document.querySelector(".js-blob-filename") as HTMLInputElement | null;
    },
    edit__githubTextAreaWithFileContents: () => {
      return document.querySelector(".file-editor-textarea") as HTMLTextAreaElement | null;
    },
    pr__mutationObserverTarget: () => {
      return document.getElementById("files") as HTMLElement | null;
    },
    pr__openWithExternalEditorLinkContainer: (container: HTMLElement) => {
      return container.querySelectorAll("details-menu a")[0] as HTMLAnchorElement | null;
    },
    pr__viewOriginalFileLinkContainer: (container: HTMLElement) => {
      return container.querySelectorAll("details-menu a")[0] as HTMLAnchorElement | null;
    },
    pr__unprocessedFilePathContainer: (container: HTMLElement) => {
      return container.querySelector(".file-info > .link-gray-dark") as HTMLAnchorElement | null;
    },

    array: {
      pr__supportedPrFileContainers: () => {
        const elements = Array.from(document.querySelectorAll(".file.js-file.js-details-container")).map(
          e => e as HTMLElement
        );
        return elements.length > 0 ? (elements as HTMLElement[]) : null;
      },

      pr__prInfoContainer: () => {
        const elements = Array.from(document.querySelectorAll(".gh-header-meta .css-truncate-target"));
        return elements.length > 0 ? (elements as HTMLElement[]) : null;
      }
    }
  };
}
