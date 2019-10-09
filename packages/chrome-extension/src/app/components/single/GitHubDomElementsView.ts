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

export class GitHubDomElementsView implements GitHubDomElements {
  public toolbarContainer() {
    const element = () => document.getElementById("kogito-toolbar-container")!;
    if (!element()) {
      document
        .querySelector(".Box.mt-3.position-relative")!
        .insertAdjacentHTML(
          "beforebegin",
          `<div id="kogito-toolbar-container" class="view d-flex flex-column flex-items-start flex-md-row"></div>`
        );
    }
    return element();
  }

  public getFileContents() {
    const rawUrl = (document.getElementById("raw-url") as HTMLAnchorElement)!.href;
    return fetch(rawUrl).then(res => res.text());
  }

  public githubTextEditorToReplace() {
    return document.querySelector(".Box-body.p-0.blob-wrapper.data.type-xml")! as HTMLElement;
  }

  public iframeContainer() {
    const element = () => document.getElementById("kogito-iframe-container")!;
    if (!element()) {
      document
        .querySelector(".Box.mt-3.position-relative")!
        .insertAdjacentHTML("afterend", `<div id="kogito-iframe-container" class="view"></div>`);
    }
    return element();
  }
}
