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
import {
  createAndGetMainContainer,
  extractOpenFileExtension,
  iframeFullscreenContainer,
  removeAllChildren
} from "../../utils";
import { SingleEditorApp } from "./SingleEditorApp";
import { Main } from "../common/Main";
import { Router } from "@kogito-tooling/core-api";
import * as dependencies__ from "../../dependencies";
import { KOGITO_IFRAME_CONTAINER_ID, KOGITO_TOOLBAR_CONTAINER_ID } from "../../constants";
import { Logger } from "../../../Logger";

export function renderSingleEditorApp(args: {
  logger: Logger;
  editorIndexPath: string;
  extensionIconUrl: string;
  router: Router;
}) {
  // Checking whether this text editor exists is a good way to determine if the page is "ready",
  // because that would mean that the user could see the default GitHub page.
  if (!dependencies__.singleEdit.githubTextEditorToReplaceElement()) {
    args.logger.log(`Doesn't look like the GitHub page is ready yet.`);
    return;
  }

  // Necessary because GitHub apparently "caches" DOM structures between changes on History.
  // Without this method you can observe duplicated elements when using back/forward browser buttons.
  cleanup();

  const openFileExtension = extractOpenFileExtension(window.location.href);
  if (!openFileExtension) {
    args.logger.log(`Unable to determine file extension from URL.`);
    return;
  }

  if (!args.router.getLanguageData(openFileExtension)) {
    args.logger.log(`No enhanced editor available for "${openFileExtension}" format.`);
    return;
  }

  ReactDOM.render(
    <Main
      router={args.router}
      logger={args.logger}
      extensionIconUrl={args.extensionIconUrl}
      editorIndexPath={args.editorIndexPath}
    >
      <SingleEditorViewApp openFileExtension={openFileExtension} />
    </Main>,
    createAndGetMainContainer(dependencies__.all.body()),
    () => args.logger.log("Mounted.")
  );
}

function SingleEditorViewApp(props: { openFileExtension: string }) {
  return (
    <SingleEditorApp
      readonly={false}
      openFileExtension={props.openFileExtension}
      getFileContents={getFileContents}
      iframeContainer={iframeContainer()}
      toolbarContainer={toolbarContainer()}
      githubTextEditorToReplace={dependencies__.singleEdit.githubTextEditorToReplaceElement()!}
    />
  );
}

function cleanup() {
  //FIXME: Unchecked dependency use
  removeAllChildren(iframeContainer());
  removeAllChildren(toolbarContainer());
  removeAllChildren(iframeFullscreenContainer(dependencies__.all.body()));
  removeAllChildren(createAndGetMainContainer(dependencies__.all.body()));
}

function getFileContents() {
  return Promise.resolve(dependencies__.all.edit__githubTextAreaWithFileContents()!.value);
}

function toolbarContainer() {
  const div = `<div id="${KOGITO_TOOLBAR_CONTAINER_ID}" class="edit d-flex flex-column flex-items-start flex-md-row"></div>`;
  const element = () => document.getElementById(KOGITO_TOOLBAR_CONTAINER_ID)!;

  if (!element()) {
    dependencies__.singleEdit.toolbarContainerTarget()!.insertAdjacentHTML("beforeend", div);
  }

  return element();
}

function iframeContainer() {
  const div = `<div id="${KOGITO_IFRAME_CONTAINER_ID}" class="edit"></div>`;
  const element = () => document.getElementById(KOGITO_IFRAME_CONTAINER_ID)!;

  if (!element()) {
    dependencies__.singleEdit.iframeContainerTarget()!.insertAdjacentHTML("afterend", div);
  }

  return element();
}
