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

import { Router } from "@kogito-tooling/core-api";
import {
  GITHUB_RENAMED_FILE_ARROW,
  KOGITO_IFRAME_CONTAINER_PR_CLASS,
  KOGITO_TOOLBAR_CONTAINER_PR_CLASS,
  KOGITO_VIEW_ORIGINAL_LINK_CONTAINER_PR_CLASS
} from "../../constants";
import * as dependencies__ from "../../dependencies";
import { ResolvedDomDependency } from "../../dependencies";

export class GitHubDomElementsPr {
  private readonly router: Router;
  private readonly container: ResolvedDomDependency;
  private readonly info: {
    repository: string;
    targetOrganization: string;
    targetGitReference: string;
    organization: string;
    gitReference: string;
    originalFilePath: string;
    modifiedFilePath: string;
  };

  constructor(container: ResolvedDomDependency, router: Router) {
    this.router = router;
    this.container = container;

    const metaInfo = getMetaInfo();
    const targetOrganization = window.location.pathname.split("/")[1];
    const repository = window.location.pathname.split("/")[2];

    // PR is within the same organization
    if (metaInfo.length < 6) {
      this.info = {
        repository: repository,
        targetOrganization: targetOrganization,
        targetGitReference: metaInfo[1],
        organization: targetOrganization,
        gitReference: metaInfo[3],
        originalFilePath: getOriginalFilePath(container),
        modifiedFilePath: getModifiedFilePath(container)
      };
    }

    // PR is from a fork to an upstream
    else {
      this.info = {
        repository: repository,
        targetOrganization: targetOrganization,
        targetGitReference: metaInfo[2],
        organization: metaInfo[4],
        gitReference: metaInfo[5],
        originalFilePath: getOriginalFilePath(container),
        modifiedFilePath: getModifiedFilePath(container)
      };
    }
  }

  public getFileContents() {
    const org = this.info.organization;
    const repo = this.info.repository;
    const branch = this.info.gitReference;
    const path = this.info.modifiedFilePath;

    return fetch(`https://raw.githubusercontent.com/${org}/${repo}/${branch}/${path}`).then(res => {
      return res.ok ? res.text() : Promise.resolve(undefined);
    });
  }

  public getOriginalFileContents() {
    const org = this.info.targetOrganization;
    const repo = this.info.repository;
    const branch = this.info.targetGitReference;
    const path = this.info.originalFilePath;

    return fetch(`https://raw.githubusercontent.com/${org}/${repo}/${branch}/${path}`).then(res => {
      return res.ok ? res.text() : Promise.resolve(undefined);
    });
  }

  public iframeContainer(domDependency: ResolvedDomDependency) {
    const div = `<div class="${KOGITO_IFRAME_CONTAINER_PR_CLASS}"></div>`;
    const element = () => this.container.element.querySelector(`.${KOGITO_IFRAME_CONTAINER_PR_CLASS}`);

    if (!element()!) {
      domDependency.element.insertAdjacentHTML("beforeend", div);
    }

    return element() as HTMLElement;
  }

  public toolbarContainer(domDependency: ResolvedDomDependency) {
    const div = `<div class="${KOGITO_TOOLBAR_CONTAINER_PR_CLASS}"></div>`;
    const element = () => this.container.element.querySelector(`.${KOGITO_TOOLBAR_CONTAINER_PR_CLASS}`);

    if (!element()) {
      domDependency.element.insertAdjacentHTML("afterend", div);
    }

    return element()!;
  }

  public viewOriginalFileHref() {
    const org = this.info.targetOrganization;
    const repo = this.info.repository;
    const branch = this.info.targetGitReference;
    const path = this.info.originalFilePath;

    return `/${org}/${repo}/blob/${branch}/${path}`;
  }

  public viewOriginalFileLinkContainer(domDependency: ResolvedDomDependency) {
    const div = `<div class="${KOGITO_VIEW_ORIGINAL_LINK_CONTAINER_PR_CLASS}"></div>`;
    const element = () => this.container.element.querySelector(`.${KOGITO_VIEW_ORIGINAL_LINK_CONTAINER_PR_CLASS}`);

    if (!element()) {
      domDependency.element.insertAdjacentHTML("afterend", div);
    }

    return element()!;
  }
}

export function getUnprocessedFilePath(container: ResolvedDomDependency) {
  return dependencies__.all.unprocessedFilePathElement(container).title;
}

function getMetaInfo() {
  return dependencies__.all.getMetaInfoElement()!.map(e => e.textContent!);
}

export function getOriginalFilePath(container: ResolvedDomDependency) {
  const path = getUnprocessedFilePath(container);
  if (path.includes(GITHUB_RENAMED_FILE_ARROW)) {
    return path.split(` ${GITHUB_RENAMED_FILE_ARROW} `)[0];
  } else {
    return path;
  }
}

export function getModifiedFilePath(container: ResolvedDomDependency) {
  const path = getUnprocessedFilePath(container);
  if (path.includes(GITHUB_RENAMED_FILE_ARROW)) {
    return path.split(` ${GITHUB_RENAMED_FILE_ARROW} `)[1];
  } else {
    return path;
  }
}
