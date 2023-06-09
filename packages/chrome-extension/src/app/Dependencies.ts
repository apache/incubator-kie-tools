/*
 * Copyright 2020 Red Hat, Inc. and/or its affiliates.
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
      return document.querySelector(".Box-sc-g0xbh4-0.cpPHtE") as HTMLElement | null;
    },
    toolbarContainerTarget: () => {
      return document.querySelector(".Box-sc-g0xbh4-0.cpPHtE") as HTMLElement | null;
    },
    githubTextEditorToReplaceElement: () => {
      return document.querySelector(".Box-sc-g0xbh4-0.iexDVb") as HTMLElement | null;
    },
  };

  public readonly singleView = {
    iframeContainerTarget: () => {
      // TODO: This if can be removed once github unifies the ui for logged in/out users.
      if (document.body.classList.contains("logged-out")) {
        return document.querySelector(".Box.mt-3.position-relative") as HTMLElement | null;
      }
      return document.querySelector(".Box-sc-g0xbh4-0.cluMzC") as HTMLElement | null;
    },
    toolbarContainerTarget: () => {
      // TODO: This if can be removed once github unifies the ui for logged in/out users.
      if (document.body.classList.contains("logged-out")) {
        return document.querySelector(".Box.mt-3.position-relative") as HTMLElement | null;
      }
      return document.querySelector(".Box-sc-g0xbh4-0.izfgQu") as HTMLElement | null;
    },
    githubTextEditorToReplaceElement: () => {
      // TODO: This if can be removed once github unifies the ui for logged in/out users.
      if (document.body.classList.contains("logged-out")) {
        return document.querySelector(".Box-body.p-0.blob-wrapper.data") as HTMLElement | null;
      }
      return document.querySelector(".Box-sc-g0xbh4-0.eRkHwF") as HTMLElement | null;
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
    buttonContainerOnRepoFilesList: () => {
      // TODO: This if can be removed once github unifies the ui for logged in/out users.
      if (document.body.classList.contains("logged-out")) {
        return document.querySelector(".file-navigation") as HTMLElement | null;
      }
      return document.querySelector(".Box-sc-g0xbh4-0.gtBUEp") as HTMLElement | null;
    },
    buttonContainerOnPrs: () => {
      return document.querySelector(".gh-header-actions") as HTMLElement | null;
    },
  };

  public readonly all = {
    notificationIndicator: () => {
      return document.querySelector(".notification-indicator") as HTMLElement | null;
    },
    body: () => {
      return document.body;
    },
    edit__githubFileNameInput: () => {
      return document.querySelector(
        "._UnstyledTextInput__UnstyledTextInput-sc-31b2um-0.dFGJZq"
      ) as HTMLInputElement | null;
    },
    edit__githubTextAreaWithFileContents: () => {
      return document.querySelector(".js-react-code-editor") as HTMLTextAreaElement | null;
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
  };
}
