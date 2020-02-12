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

import {
  KOGITO_IFRAME_FULLSCREEN_CONTAINER_CLASS,
  KOGITO_MAIN_CONTAINER_CLASS,
  KOGITO_MENU_CONTAINER_CLASS
} from "./constants";
import { Logger } from "../Logger";

export function runScriptOnPage(scriptString: string) {
  const scriptTag = document.createElement("script");
  scriptTag.setAttribute("type", "text/javascript");
  scriptTag.innerText = scriptString;
  document.body.appendChild(scriptTag);
  scriptTag.remove();
}

let lastUri = window.location.pathname;

export function runAfterUriChange(logger: Logger, callback: () => void) {
  const checkUriThenCallback = () => {
    const currentUri = window.location.pathname;

    if (lastUri === currentUri) {
      return;
    }

    logger.log(`URI changed from '${lastUri}' to '${currentUri}'. Restarting the extension.`);
    lastUri = currentUri;
    callback();
  };

  runScriptOnPage(`
  var _wr = function(type) {
      var orig = history[type];
      return function() {
          var rv = orig.apply(this, arguments);
          var e = new Event(type);
          e.arguments = arguments;
          window.dispatchEvent(e);
          return rv;
      };
  };
  history.replaceState = _wr('replaceState');`);

  window.addEventListener("replaceState", () => {
    logger.log("replaceState event happened");
    checkUriThenCallback();
  });
  window.addEventListener("popstate", () => {
    logger.log("popstate event happened");
    checkUriThenCallback();
  });
}

export function removeAllChildren(node: Node) {
  while (node.firstChild) {
    node.removeChild(node.firstChild);
  }
}

export function mainContainer(id: string, container: HTMLElement) {
  return container.querySelector(`.${KOGITO_MAIN_CONTAINER_CLASS}.${id}`);
}

export function createAndGetMainContainer(id: string, container: HTMLElement) {
  if (!mainContainer(id, container)) {
    container.insertAdjacentHTML("beforeend", `<div class="${KOGITO_MAIN_CONTAINER_CLASS} ${id}"></div>`);
  }
  return mainContainer(id, container)!;
}

export function iframeFullscreenContainer(id: string, container: HTMLElement) {
  const element = () => document.querySelector(`.${KOGITO_IFRAME_FULLSCREEN_CONTAINER_CLASS}.${id}`)!;
  if (!element()) {
    container.insertAdjacentHTML(
      "afterbegin",
      `<div class="${KOGITO_IFRAME_FULLSCREEN_CONTAINER_CLASS} ${id}" class="hidden"></div>`
    );
  }
  return element();
}

export function kogitoMenuContainer(id: string, container: HTMLElement) {
  const element = () => document.querySelector(`.${KOGITO_MENU_CONTAINER_CLASS}.${id}`)!;

  if (!element()) {
    container.insertAdjacentHTML("beforebegin", `<div class="${KOGITO_MENU_CONTAINER_CLASS} ${id} Header-item"></div>`);
  }

  return element();
}

export function extractOpenFileExtension(url: string) {
  return url
    .split(".")
    .pop()
    ?.match(/[\w\d]+/)
    ?.pop();
}
