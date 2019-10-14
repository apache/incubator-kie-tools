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
import * as dependencies__ from "../../dependencies";
import { GitHubPageType } from "../../github/GitHubPageType";

export function renderSingleEditorApp(args: { editorIndexPath: string; router: Router }) {
  const githubDomElements = new GitHubDomElementsEdit();

  // Checking whether this text editor exists is a good way to determine if the page is "ready",
  // because that would mean that the user could see the default GitHub page.
  if (!dependencies__.singleEdit.githubTextEditorToReplaceElement()) {
    console.info(`[Kogito] Doesn't look like the GitHub page is ready yet.`);
    return false;
  }

  // Necessary because GitHub apparently "caches" DOM structures between changes on History.
  // Without this method you can observe duplicated elements when using back/forward browser buttons.
  //FIXME: Unchecked dependency use
  removeAllChildren(githubDomElements.iframeContainer(dependencies__.singleEdit.iframeContainerTarget()!));
  removeAllChildren(githubDomElements.toolbarContainer(dependencies__.singleEdit.toolbarContainerTarget()!));
  removeAllChildren(iframeFullscreenContainer(dependencies__.common.body()));
  removeAllChildren(createAndGetMainContainer(dependencies__.common.body()));

  render({
    pageType: GitHubPageType.EDIT,
    editorIndexPath: args.editorIndexPath,
    router: args.router,
    readonly: false,
    githubDomElements: githubDomElements
  });
}

export function renderSingleEditorReadonlyApp(args: { editorIndexPath: string; router: Router }) {
  const githubDomElements = new GitHubDomElementsView();

  // Checking whether this text editor exists is a good way to determine if the page is "ready",
  // because that would mean that the user could see the default GitHub page.
  if (!dependencies__.singleView.githubTextEditorToReplaceElement()) {
    console.info(`[Kogito] Doesn't look like the GitHub page is ready yet.`);
    return false;
  }

  // Necessary because GitHub apparently "caches" DOM structures between changes on History.
  // Without this method you can observe duplicated elements when using back/forward browser buttons.
  //FIXME: Unchecked dependency use
  removeAllChildren(githubDomElements.iframeContainer(dependencies__.singleView.iframeContainerTarget()!));
  removeAllChildren(githubDomElements.toolbarContainer(dependencies__.singleView.toolbarContainerTarget()!));
  removeAllChildren(iframeFullscreenContainer(dependencies__.common.body()));
  removeAllChildren(createAndGetMainContainer(dependencies__.common.body()));

  render({
    pageType: GitHubPageType.VIEW,
    editorIndexPath: args.editorIndexPath,
    router: args.router,
    readonly: true,
    githubDomElements: githubDomElements
  });
}

function render(args: {
  editorIndexPath: string;
  router: Router;
  readonly: boolean;
  pageType: GitHubPageType;
  githubDomElements: GitHubDomElements;
}) {
  const openFileExtension = extractOpenFileExtension(window.location.href);

  if (!openFileExtension) {
    console.info(`[Kogito] Unable to determine file extension from URL`);
    return false;
  }

  if (!args.router.getLanguageData(openFileExtension)) {
    console.info(`[Kogito] No enhanced editor available for "${openFileExtension}" format.`);
    return false;
  }

  ReactDOM.render(
    <Main router={args.router} editorIndexPath={args.editorIndexPath} pageType={args.pageType}>
      <SingleEditorApp
        openFileExtension={openFileExtension}
        githubDomElements={args.githubDomElements}
        readonly={args.readonly}
      />
    </Main>,
    createAndGetMainContainer(dependencies__.common.body()),
    () => console.info("[Kogito] Mounted.")
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
