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
import { KOGITO_IFRAME_CONTAINER_ID, KOGITO_TOOLBAR_CONTAINER_ID } from "../../constants";
import * as dependencies from "../../dependencies";

export class GitHubDomElementsView implements GitHubDomElements {
  public toolbarContainer(container: HTMLElement) {
    const div = `<div id="${KOGITO_TOOLBAR_CONTAINER_ID}" class="view d-flex flex-column flex-items-start flex-md-row"></div>`;
    const element = () => document.getElementById(KOGITO_TOOLBAR_CONTAINER_ID)!;

    if (!element()) {
      container.insertAdjacentHTML("beforebegin", div);
    }

    return element();
  }

  public getFileContents() {
    return fetch(dependencies.singleView.rawUrlLink()!.href).then(res => res.text());
  }

  public githubTextEditorToReplace() {
    return dependencies.singleView.githubTextEditorToReplaceElement()!;
  }

  public iframeContainer(container: HTMLElement) {
    const div = `<div id="${KOGITO_IFRAME_CONTAINER_ID}" class="view"></div>`;
    const element = () => document.getElementById(KOGITO_IFRAME_CONTAINER_ID)!;

    if (!element()) {
      container.insertAdjacentHTML("afterend", div);
    }

    return element();
  }
}
