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

export type DomDependency = (...args: any[]) => HTMLElement | HTMLElement[] | null;

export interface ResolvedDomDependency {
  name: string;
  element: HTMLElement;
}

export interface ResolvedDomDependencyArray {
  name: string;
  element: HTMLElement[];
}

export type AnyResolvedDomDependency = ResolvedDomDependency | ResolvedDomDependencyArray;

export interface DomDependencyMap {
  [k: string]: DomDependency;
}

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

export const singleView = {
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

export const prView = {
  iframeContainerTarget: (container: ResolvedDomDependency) => {
    return container.element as HTMLElement | null;
  },
  toolbarContainerTarget: (container: ResolvedDomDependency) => {
    return container.element.querySelector(".file-info") as HTMLElement | null;
  },
  githubTextEditorToReplaceElement: (container: ResolvedDomDependency) => {
    return container.element.querySelector(".js-file-content") as HTMLElement | null;
  }
};

//

export const all = {
  body: () => {
    return document.body;
  },
  edit__githubTextAreaWithFileContents: () => {
    return document.querySelector(".file-editor-textarea") as HTMLTextAreaElement | null;
  },
  view__rawUrlLink: () => {
    return document.getElementById("raw-url") as HTMLAnchorElement | null;
  },
  pr__mutationObserverTarget: () => {
    return document.getElementById("files") as HTMLElement | null;
  },
  pr__viewOriginalFileLinkContainer: (container: ResolvedDomDependency) => {
    return container.element.querySelectorAll("details-menu a")[0] as HTMLAnchorElement | null;
  },
  pr__unprocessedFilePathContainer: (container: HTMLElement) => {
    return container.querySelector(".file-info > .link-gray-dark") as HTMLAnchorElement | null;
  },

  array: {
    supportedPrFileContainers: () => {
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

export function dependenciesAllSatisfied(dependencies: DomDependencyMap) {
  return (
    Object.keys(dependencies)
      .map(k => dependencies[k])
      .filter(d => !!d()).length > 0
  );
}

export function resolveDependencies<T extends DomDependencyMap>(deps: T) {
  return Object.keys(deps).reduce((o, key) => ({ ...o, [key]: { name: key, element: deps[key]()! } }), {}) as {
    [J in keyof T]: AnyResolvedDomDependency
  };
}
