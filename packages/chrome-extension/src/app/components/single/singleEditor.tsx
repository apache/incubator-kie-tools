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

import * as React from "react";
import * as ReactDOM from "react-dom";
import { createAndGetMainContainer, iframeFullscreenContainer, removeAllChildren } from "../../utils";
import { SingleEditorApp } from "./SingleEditorApp";
import { Main } from "../common/Main";
import { Router } from "@kogito-tooling/core-api";
import { GitHubDomElementsEdit } from "./GitHubDomElementsEdit";
import { GitHubDomElementsView } from "./GitHubDomElementsView";
import { GitHubDomElements } from "../../github/GitHubDomElements";

export function renderSingleEditorApp(args: { router: Router }) {
  render({
    router: args.router,
    readonly: false,
    githubDomElements: new GitHubDomElementsEdit()
  });
}

export function renderSingleEditorReadonlyApp(args: { router: Router }) {
  render({
    router: args.router,
    readonly: true,
    githubDomElements: new GitHubDomElementsView()
  });
}

function render(args: { router: Router; readonly: boolean; githubDomElements: GitHubDomElements }) {
  const openFileExtension = extractOpenFileExtension(window.location.href);

  if (!openFileExtension) {
    console.info(`[Kogito] Unable to determine file extension from URL`);
    return false;
  }

  if (!args.router.getLanguageData(openFileExtension)) {
    console.info(`[Kogito] No enhanced editor available for "${openFileExtension}" format.`);
    return false;
  }

  if (!githubPageLooksReady(args.githubDomElements)) {
    console.info(`[Kogito] Doesn't look like the GitHub page is ready yet.`);
    return false;
  }

  // Necessary because GitHub apparently "caches" DOM structures between changes on History.
  // Without this method you can observe duplicated elements when using back/forward browser buttons.
  cleanupComponentContainers(args.githubDomElements);

  ReactDOM.render(
    <Main router={args.router}>
      <SingleEditorApp
        openFileExtension={openFileExtension!}
        githubDomElements={args.githubDomElements}
        readonly={args.readonly}
      />
    </Main>,
    createAndGetMainContainer()
  );
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

export function cleanupComponentContainers(githubDomElements: GitHubDomElements) {
  removeAllChildren(githubDomElements.iframeContainer());
  removeAllChildren(githubDomElements.toolbarContainer());
  removeAllChildren(iframeFullscreenContainer());
  removeAllChildren(createAndGetMainContainer());
}

function githubPageLooksReady(githubDomElements: GitHubDomElements) {
  /*
   * Checking whether this text editor exists is a good way to determine if the page is "ready",
   * because that would mean that the user could see the default GitHub page.
   */

  return !!githubDomElements.githubTextEditorToReplace();
}
