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

export type DomDependency = (...args: any[]) => HTMLElement | null;

export interface ResolvedDomDependency {
  name: string;
  element: HTMLElement;
}

// tslint:disable-next-line
export type DomDependencyMap<T = DomDependency> = { [k: string]: T };

export interface GlobalDomDependencies {
  common: GlobalCommonDomDependencies;
  all: typeof all;
}

export interface GlobalCommonDomDependencies {
  iframeContainerTarget: DomDependency;
  toolbarContainerTarget: DomDependency;
  githubTextEditorToReplaceElement: DomDependency;
}

export const singleEdit = {
  iframeContainerTarget: () => document.querySelector(".file") as HTMLElement | null,
  toolbarContainerTarget: () => document.querySelector(".breadcrumb.d-flex.flex-items-center") as HTMLElement | null,
  githubTextEditorToReplaceElement: () => document.querySelector(".js-code-editor") as HTMLElement
};

export const singleView = {
  iframeContainerTarget: () => document.querySelector(".Box.mt-3.position-relative") as HTMLElement | null,
  toolbarContainerTarget: () => document.querySelector(".Box.mt-3.position-relative") as HTMLElement | null,
  githubTextEditorToReplaceElement: () => document.querySelector(".Box-body.p-0.blob-wrapper.data") as HTMLElement
};

export const prView = {
  iframeContainerTarget: (container: ResolvedDomDependency) => container.element as HTMLElement | null,
  toolbarContainerTarget: (container: ResolvedDomDependency) => {
    return container.element.querySelector(".file-info") as HTMLElement | null;
  },
  githubTextEditorToReplaceElement: (container: ResolvedDomDependency) => {
    return container.element.querySelector(".js-file-content") as HTMLElement;
  }
};

//

export const all = {
  body: () => document.body,
  githubTextAreaWithFileContents: () => document.querySelector(".file-editor-textarea") as HTMLTextAreaElement,
  rawUrlLink: () => document.getElementById("raw-url") as HTMLAnchorElement,
  mutationObserverTarget: () => document.getElementById("files") as HTMLElement | null,
  viewOriginalFileLinkContainer: (container: ResolvedDomDependency) => {
    return container.element.querySelectorAll("details-menu a")[0] as HTMLAnchorElement;
  },
  supportedPrFileContainers: () => {
    return Array.from(document.querySelectorAll(".file.js-file.js-details-container")).map(e => e as HTMLElement);
  },
  unprocessedFilePathElement: (container: ResolvedDomDependency) => {
    return container.element.querySelector(".file-info > .link-gray-dark") as HTMLAnchorElement;
  },
  getMetaInfoElement: () => {
    const querySelector = document.querySelector(".gh-header-meta");
    return !querySelector ? undefined : Array.from(querySelector.querySelectorAll(".css-truncate-target"));
  }
};
