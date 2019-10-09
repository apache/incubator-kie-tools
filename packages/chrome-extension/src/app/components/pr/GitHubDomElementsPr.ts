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

import { GitHubDomElements } from "../../github/GitHubDomElements";

export function getFilePath(prFileElement: HTMLElement) {
  return (prFileElement.querySelector(".file-info > .link-gray-dark") as HTMLAnchorElement).title;
}
export function getPrFileElements() {
  return document.querySelectorAll(".file.js-file.js-details-container");
}

export class GitHubDomElementsPr implements GitHubDomElements {
  private readonly container: HTMLElement;
  private readonly info: {
    repository: string;
    targetOrganization: string;
    targetGitReference: string;
    organization: string;
    gitReference: string;
    filePath: string;
  };

  constructor(container: HTMLElement) {
    const metaInfos = document.querySelector(".gh-header-meta")!.querySelectorAll(".css-truncate-target");
    this.container = container;
    this.info = {
      repository: window.location.pathname.split("/")[2],
      targetOrganization: metaInfos[1].textContent!,
      targetGitReference: metaInfos[2].textContent!,
      organization: metaInfos[4].textContent!,
      gitReference: metaInfos[5].textContent!,
      filePath: getFilePath(container)
    };
  }

  public getFileContents(): Promise<string> {
    const org = this.info.organization;
    const repo = this.info.repository;
    const branch = this.info.gitReference;
    const path = this.info.filePath;

    return fetch(`https://raw.githubusercontent.com/${org}/${repo}/${branch}/${path}`).then(res => res.text());
  }

  public getOriginalFileContents() {
    const org = this.info.targetOrganization;
    const repo = this.info.repository;
    const branch = this.info.targetGitReference;
    const path = this.info.filePath;

    return fetch(`https://raw.githubusercontent.com/${org}/${repo}/${branch}/${path}`).then(res => res.text());
  }

  public githubTextEditorToReplace(): HTMLElement {
    return this.container.querySelector(".js-file-content") as HTMLElement;
  }

  public iframeContainer(): HTMLElement {
    const element = () => this.container.querySelector(".kogito-iframe-container-pr");
    if (!element()!) {
      this.container.insertAdjacentHTML("beforeend", '<div class="kogito-iframe-container-pr"></div>');
    }
    return element() as HTMLElement;
  }

  public toolbarContainer(): Element {
    const element = () => this.container.querySelector(".kogito-toolbar-container-pr");
    if (!element()) {
      this.container
        .querySelector(".file-info")!
        .insertAdjacentHTML("afterend", `<div class="kogito-toolbar-container-pr"></div>`);
    }
    return element()!;
  }

  public viewOriginalFileHref() {
    const org = this.info.targetOrganization;
    const repo = this.info.repository;
    const branch = this.info.targetGitReference;
    const path = this.info.filePath;

    return `https://github.com/${org}/${repo}/blob/${branch}/${path}`;
  }

  public viewOriginalFileLinkContainer() {
    const element = () => this.container.querySelector(".kogito-view-original-link-container-pr");
    if (!element()) {
      this.container
        .querySelectorAll("details-menu a")[0]!
        .insertAdjacentHTML("afterend", `<div class="kogito-view-original-link-container-pr"></div>`);
    }
    return element()!;
  }
}
