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

import * as dependencies__ from "../../dependencies";
import {
  createAndGetMainContainer,
  extractOpenFileExtension,
  iframeFullscreenContainer,
  removeAllChildren
} from "../../utils";
import * as ReactDOM from "react-dom";
import { Globals, Main } from "../common/Main";
import { SingleEditorApp } from "./SingleEditorApp";
import * as React from "react";
import { useCallback } from "react";
import { KOGITO_IFRAME_CONTAINER_CLASS, KOGITO_TOOLBAR_CONTAINER_CLASS } from "../../constants";
import { fetchFile } from "../../github/api";
import { useGitHubApi } from "../common/GitHubContext";
import { useGlobals } from "../common/GlobalContext";

export interface FileInfo {
  repo: string;
  org: string;
  path: string;
  gitRef: string;
}

export function renderSingleEditorReadonlyApp(args: Globals & { fileInfo: FileInfo }) {
  // Checking whether this text editor exists is a good way to determine if the page is "ready",
  // because that would mean that the user could see the default GitHub page.
  if (!dependencies__.singleView.githubTextEditorToReplaceElement()) {
    args.logger.log(`Doesn't look like the GitHub page is ready yet.`);
    return;
  }

  const openFileExtension = extractOpenFileExtension(window.location.href);
  if (!openFileExtension) {
    args.logger.log(`Unable to determine file extension from URL`);
    return;
  }

  if (!args.router.getLanguageData(openFileExtension)) {
    args.logger.log(`No enhanced editor available for "${openFileExtension}" format.`);
    return;
  }

  // Necessary because GitHub apparently "caches" DOM structures between changes on History.
  // Without this method you can observe duplicated elements when using back/forward browser buttons.
  cleanup(args.id);

  ReactDOM.render(
    <Main
      id={args.id}
      router={args.router}
      logger={args.logger}
      githubAuthTokenCookieName={args.githubAuthTokenCookieName}
      extensionIconUrl={args.extensionIconUrl}
      editorIndexPath={args.editorIndexPath}
      resourceContentServiceFactory={args.resourceContentServiceFactory}
      externalEditorManager={args.externalEditorManager}
    >
      <SingleEditorViewApp fileInfo={args.fileInfo} openFileExtension={openFileExtension} />
    </Main>,
    createAndGetMainContainer(args.id, dependencies__.all.body()!),
    () => args.logger.log("Mounted.")
  );
}

function SingleEditorViewApp(props: { fileInfo: FileInfo; openFileExtension: string }) {
  const githubApi = useGitHubApi();
  const globals = useGlobals();
  const getFileContents = useCallback(
    () =>
      fetchFile(
        githubApi.octokit(),
        props.fileInfo.org,
        props.fileInfo.repo,
        props.fileInfo.gitRef,
        props.fileInfo.path
      ),
    []
  );
  const getFileName = useCallback(() => {
    return decodeURIComponent(props.fileInfo.path.split("/").pop()!);
  }, [props.fileInfo.path]);

  return (
    <SingleEditorApp
      readonly={true}
      openFileExtension={props.openFileExtension}
      getFileName={getFileName}
      getFileContents={getFileContents}
      iframeContainer={iframeContainer(globals.id)}
      toolbarContainer={toolbarContainer(globals.id)}
      githubTextEditorToReplace={dependencies__.singleView.githubTextEditorToReplaceElement()!}
      fileInfo={props.fileInfo}
    />
  );
}

function cleanup(id: string) {
  //FIXME: Unchecked dependency use
  removeAllChildren(iframeContainer(id));
  removeAllChildren(toolbarContainer(id));
  removeAllChildren(iframeFullscreenContainer(id, dependencies__.all.body()));
  removeAllChildren(createAndGetMainContainer(id, dependencies__.all.body()));
}

function toolbarContainer(id: string) {
  const element = () => document.querySelector(`.${KOGITO_TOOLBAR_CONTAINER_CLASS}.${id}`)!;

  if (!element()) {
    dependencies__.singleView
      .toolbarContainerTarget()!
      .insertAdjacentHTML(
        "beforebegin",
        `<div class="${KOGITO_TOOLBAR_CONTAINER_CLASS} ${id} view d-flex flex-column flex-items-start flex-md-row"></div>`
      );
  }

  return element() as HTMLElement;
}

function iframeContainer(id: string) {
  const element = () => document.querySelector(`.${KOGITO_IFRAME_CONTAINER_CLASS}.${id}`)!;

  if (!element()) {
    dependencies__.singleView
      .iframeContainerTarget()!
      .insertAdjacentHTML("afterend", `<div class="${KOGITO_IFRAME_CONTAINER_CLASS} ${id} view"></div>`);
  }

  return element() as HTMLElement;
}
