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

import * as ReactDOM from "react-dom";
import { GitHubPageType } from "./app/github/GitHubPageType";
import { mainContainer, runAfterPagePushState } from "./app/utils";
import { renderSingleEditorApp, renderSingleEditorReadonlyApp } from "./app/components/single/singleEditor";
import { renderPrEditorsApp } from "./app/components/pr/prEditors";
import { ChromeRouter } from "./app/ChromeRouter";
import { GwtEditorRoutes } from "@kogito-tooling/gwt-editors";

const GITHUB_COM = "http[s]://github.com";

function init() {
  console.info(`[Kogito] ---`);
  console.info(`[Kogito] Starting GitHub extension.`);

  unmountPreviouslyRenderedFeatures();

  const pageType = discoverCurrentGitHubPageType();
  if (pageType === GitHubPageType.ANY) {
    console.info(`[Kogito] This GitHub page is not supported.`);
    return;
  }

  const router = new ChromeRouter(new GwtEditorRoutes({ bpmnPath: "bpmn" }));

  if (pageType === GitHubPageType.EDIT) {
    renderSingleEditorApp({ router: router });
    return;
  }

  if (pageType === GitHubPageType.VIEW) {
    renderSingleEditorReadonlyApp({ router: router });
    return;
  }

  if (pageType === GitHubPageType.PR) {
    renderPrEditorsApp({ router: router });
    return;
  }

  throw new Error(`Unknown GitHubPageType ${pageType}`);
}

runAfterPagePushState(() => setImmediate(init));
setImmediate(() => init());

function uriMatches(regex: string) {
  return !!window.location.href.match(new RegExp(regex));
}

function discoverCurrentGitHubPageType() {
  if (uriMatches(`${GITHUB_COM}/.*/.*/edit/.*`)) {
    return GitHubPageType.EDIT;
  }

  if (uriMatches(`${GITHUB_COM}/.*/.*/blob/.*`)) {
    return GitHubPageType.VIEW;
  }

  if (uriMatches(`${GITHUB_COM}/.*/.*/pull/[0-9]+/files.*`)) {
    return GitHubPageType.PR;
  }

  return GitHubPageType.ANY;
}

function unmountPreviouslyRenderedFeatures() {
  try {
    if (mainContainer()) {
      ReactDOM.unmountComponentAtNode(mainContainer()!);
      console.info("[Kogito] Unmounted previous features.");
    }
  } catch (e) {
    console.info("[Kogito] Ignoring exception while unmounting features.");
  }
}
