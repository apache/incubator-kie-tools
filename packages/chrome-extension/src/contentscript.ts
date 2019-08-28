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
import { GwtEditorChromeExtensionRoutes } from "./app/GwtEditorChromeExtensionRoutes";

function init() {
  const githubEditor = document.querySelector(".js-code-editor") as HTMLElement;
  if (!githubEditor) {
    console.debug("Not GitHub edit page.");
    return;
  }

  const splitLocationHref = window.location.href.split(".").pop();
  if (!splitLocationHref) {
    throw new Error();
  }

  const openFileExtensionRegex = splitLocationHref.match(/[\w\d]+/);
  if (!openFileExtensionRegex) {
    throw new Error();
  }

  const openFileExtension = openFileExtensionRegex.pop();
  if (!openFileExtension) {
    throw new Error();
  }

  const router = new ChromeRouter(new GwtEditorChromeExtensionRoutes());
  if (!router.getLanguageData(openFileExtension)) {
    console.info(`No enhanced editor available for "${openFileExtension}" format.`);
    return;
  }
  const containers = ChromeAppContainers.create();

  ReactDOM.render(
    React.createElement(ChromeExtensionApp, {
      containers: containers,
      openFileExtension: openFileExtension,
      router: router,
      githubEditor: githubEditor
    }),
    document.getElementById("kogito-container")
  );
}

export class ChromeAppContainers {
  public readonly iframe: HTMLElement;
  public readonly iframeFullscreen: HTMLElement;
  public readonly toolbar: Element;

  public static create() {
    document.body.insertAdjacentHTML("afterbegin", `<div id="kogito-iframe-fullscreen-container"></div>`);
    document.body.insertAdjacentHTML("beforeend", `<div id="kogito-container"></div>`);
    document.querySelector(".file")!.insertAdjacentHTML("afterend", `<div id="kogito-iframe-container"</div>`);

    return {
      iframe: document.getElementById("kogito-iframe-container")!,
      iframeFullscreen: document.getElementById("kogito-iframe-fullscreen-container")!,
      toolbar: document.querySelector(".breadcrumb.d-flex.flex-items-center")!
    };
  }
}

init();
