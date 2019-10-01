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

import { GitHubDomElements } from "./GitHubDomElements";

export class GitHubDomElementsView implements GitHubDomElements {
  public toolbarContainer() {
    const element = () => document.getElementById("kogito-toolbar-container")!;
    if (!element()) {
      document
        .querySelector(".Box.mt-3.position-relative")!
        .insertAdjacentHTML(
          "beforebegin",
          `<div id="kogito-toolbar-container" class="d-flex flex-column flex-items-start flex-md-row"></div>`
        );
    }
    return element();
  }

  public async getFileContents() {
    // FIXME: fetch raw file from github api
    //   const res = await fetch((document.getElementById("raw-url") as HTMLAnchorElement)!.href);
    //   return res.body;
    return Promise.resolve("");
  }

  public githubTextEditor() {
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

  public mainContainer() {
    const element = () => document.getElementById("kogito-container")!;
    if (!element()) {
      document.body.insertAdjacentHTML("beforeend", `<div id="kogito-container"></div>`);
    }
    return element();
  }

  public iframeFullscreenContainer() {
    const element = () => document.getElementById("kogito-iframe-fullscreen-container")!;
    if (!element()) {
      document.body.insertAdjacentHTML("afterbegin", `<div id="kogito-iframe-fullscreen-container"></div>`);
    }
    return element();
  }
}
