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

import { KOGITO_IFRAME_FULLSCREEN_CONTAINER_ID, KOGITO_MAIN_CONTAINER_ID } from "./constants";
import { ResolvedDomDependency } from "./dependencies";
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

export function mainContainer(container: ResolvedDomDependency) {
  return container.element.querySelector(`#${KOGITO_MAIN_CONTAINER_ID}`);
}

export function createAndGetMainContainer(container: ResolvedDomDependency) {
  if (!mainContainer(container)) {
    container.element.insertAdjacentHTML("beforeend", `<div id="${KOGITO_MAIN_CONTAINER_ID}"></div>`);
  }
  return mainContainer(container)!;
}

export function iframeFullscreenContainer(container: ResolvedDomDependency) {
  const element = () => document.getElementById(KOGITO_IFRAME_FULLSCREEN_CONTAINER_ID)!;
  if (!element()) {
    container.element.insertAdjacentHTML(
      "afterbegin",
      `<div id="${KOGITO_IFRAME_FULLSCREEN_CONTAINER_ID}" class="hidden"></div>`
    );
  }
  return element();
}

export function waitUntil(halt: () => boolean, times: { interval: number; timeout: number }) {
  return new Promise((res, rej) => {
    asyncLoop(halt, times.interval, times.timeout, new Date().getTime(), res, rej);
  });
}

function asyncLoop(
  halt: () => boolean,
  interval: number,
  timeout: number,
  start: number,
  onHalt: () => void,
  onTimeout: (...args: any[]) => void
) {
  //timeout check
  if (new Date().getTime() - start >= timeout) {
    onTimeout("async loop timeout");
    return;
  }

  //check condition
  if (halt()) {
    onHalt();
    return;
  }

  //loop
  setTimeout(() => asyncLoop(halt, interval, timeout, start, onHalt, onTimeout), interval);
}

export function extractOpenFileExtension(url: string) {
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
