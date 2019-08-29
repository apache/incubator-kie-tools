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

import { ChromeRouter } from "./app/ChromeRouter";
import * as React from "react";
import * as ReactDOM from "react-dom";
import { ChromeExtensionApp } from "./app/components/ChromeExtensionApp";
import { GwtEditorRoutes } from "appformer-js-gwt-editors";

function extractOpenFileExtension(url: string) {
  const splitLocationHref = url.split(".").pop();
  if (!splitLocationHref) {
    return undefined;
  }

  const openFileExtensionRegex = splitLocationHref.match(/[\w\d]+/);
  if (!openFileExtensionRegex) {
    return undefined;
  }

  const openFileExtension = openFileExtensionRegex.pop();
  if (!openFileExtension) {
    return undefined;
  }

  return openFileExtension;
}

async function init() {
  const githubDomElements = new GitHubDomElementsFactory().create();
  if (!githubDomElements.githubEditor) {
    console.info(`[Kogito] Not GitHub edit page.`);
    return;
  }

  if (!githubDomElements.allFound()) {
    console.info(`[Kogito] One of the necessary GitHub elements was not found.`);
    return;
  }

  const openFileExtension = extractOpenFileExtension(window.location.href);
  if (!openFileExtension) {
    console.info(`[Kogito] Unable to determine file extension from URL`);
    return;
  }

  const router = new ChromeRouter(new GwtEditorRoutes({ bpmnPath: "bpmn" }));
  if (!router.getLanguageData(openFileExtension)) {
    console.info(`[Kogito] No enhanced editor available for "${openFileExtension}" format.`);
    return;
  }

  const app = React.createElement(ChromeExtensionApp, {
    githubDomElements: githubDomElements,
    openFileExtension: openFileExtension,
    router: router
  });

  ReactDOM.render(app, githubDomElements.main);
}

export interface GitHubDomElements {
  iframe: HTMLElement;
  iframeFullscreen: HTMLElement;
  toolbar: Element;
  main: Element;
  githubEditor: HTMLElement;

  allFound(): boolean;
}

export class GitHubDomElementsFactory {
  //FIXME: cannot call twice
  public create(): GitHubDomElements {
    document.body.insertAdjacentHTML("afterbegin", `<div id="kogito-iframe-fullscreen-container"></div>`);
    document.body.insertAdjacentHTML("beforeend", `<div id="kogito-container"></div>`);
    document.querySelector(".file")!.insertAdjacentHTML("afterend", `<div id="kogito-iframe-container"</div>`);

    return {
      iframe: document.getElementById("kogito-iframe-container")!,
      iframeFullscreen: document.getElementById("kogito-iframe-fullscreen-container")!,
      toolbar: document.querySelector(".breadcrumb.d-flex.flex-items-center")!,
      githubEditor: document.querySelector(".js-code-editor")! as HTMLElement,
      main: document.getElementById("kogito-container")!,
      allFound() {
        return Object.keys(this).reduce((p, k) => p && !!this[k], true);
      }
    };
  }
}

init();
