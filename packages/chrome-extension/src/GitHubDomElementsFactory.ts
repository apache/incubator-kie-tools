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

import { GitHubCodeMirrorEditor } from "./GitHubCodeMirrorEditor";

export class GitHubDomElements {
  public toolbar() {
    return document.querySelector(".breadcrumb.d-flex.flex-items-center")!;
  }

  public githubContentTextArea() {
    return document.querySelector(".file-editor-textarea")! as HTMLTextAreaElement;
  }

  public githubEditor() {
    return document.querySelector(".js-code-editor")! as HTMLElement;
  }

  public iframe() {
    const element = () => document.getElementById("kogito-iframe-container")!;
    if (!element()) {
      document.querySelector(".file")!.insertAdjacentHTML("afterend", `<div id="kogito-iframe-container"</div>`);
    }
    return element();
  }

  public main() {
    const element = () => document.getElementById("kogito-container")!;
    if (!element()) {
      document.body.insertAdjacentHTML("beforeend", `<div id="kogito-container"></div>`);
    }
    return element();
  }

  public iframeFullscreen() {
    const element = () => document.getElementById("kogito-iframe-fullscreen-container")!;
    if (!element()) {
      document.body.insertAdjacentHTML("afterbegin", `<div id="kogito-iframe-fullscreen-container"></div>`);
    }
    return element();
  }

  public githubEditorCodeMirror() {
    return document.querySelector(".file-editor-textarea + .CodeMirror")! as HTMLElement & GitHubCodeMirrorEditor;
  }

  public allFound() {
    return Object.keys(this).reduce((p, k) => p && (!!(this as any)[k] || !!(this as any)[k]()), true);
  }
}

export class GitHubDomElementsFactory {
  public create() {
    return new GitHubDomElements();
  }
}
