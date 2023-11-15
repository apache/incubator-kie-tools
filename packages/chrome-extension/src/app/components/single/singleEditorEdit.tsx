/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

import * as React from "react";
import { useCallback, useEffect } from "react";
import * as ReactDOM from "react-dom";
import {
  createAndGetMainContainer,
  extractOpenFileExtension,
  extractOpenFilePath,
  iframeFullscreenContainer,
  removeAllChildren,
  runScriptOnPage,
  waitForElementToBeReady,
} from "../../utils";
import { SingleEditorApp } from "./SingleEditorApp";
import { Globals, Main } from "../common/Main";
import { Dependencies } from "../../Dependencies";
import { KOGITO_IFRAME_CONTAINER_CLASS, KOGITO_TOOLBAR_CONTAINER_CLASS } from "../../constants";
import { useGlobals } from "../common/GlobalContext";
import { FileInfo } from "./singleEditorView";

export async function renderSingleEditorApp(args: Globals & { fileInfo: FileInfo }) {
  // wait for the dom element to be ready before rendering.
  await waitForElementToBeReady(".cm-content");
  // Checking whether this text editor exists is a good way to determine if the page is "ready",
  // because that would mean that the user could see the default GitHub page.
  if (!args.dependencies.singleEdit.githubTextEditorToReplaceElement()) {
    args.logger.log(`Doesn't look like the GitHub page is ready yet.`);
    return;
  }

  const openFileExtension = extractOpenFileExtension(window.location.href);
  const openFilePath = extractOpenFilePath(window.location.href);
  if (!openFileExtension) {
    args.logger.log(`Unable to determine file extension from URL.`);
    return;
  }

  if (!args.editorEnvelopeLocator.hasMappingFor(openFilePath)) {
    args.logger.log(`No enhanced editor available for "${openFilePath}" format.`);
    return;
  }

  // Necessary because GitHub apparently "caches" DOM structures between changes on History.
  // Without this method you can observe duplicated elements when using back/forward browser buttons.
  cleanup(args.id, args.dependencies);

  ReactDOM.render(
    <Main
      id={args.id}
      editorEnvelopeLocator={args.editorEnvelopeLocator}
      dependencies={args.dependencies}
      logger={args.logger}
      githubAuthTokenCookieName={args.githubAuthTokenCookieName}
      extensionIconUrl={args.extensionIconUrl}
      resourceContentServiceFactory={args.resourceContentServiceFactory}
      externalEditorManager={args.externalEditorManager}
      customChannelApiImpl={args.customChannelApiImpl}
      stateControl={args.stateControl}
    >
      <SingleEditorEditApp openFileExtension={openFileExtension} fileInfo={args.fileInfo} />
    </Main>,
    createAndGetMainContainer(args.id, args.dependencies.all.body()),
    () => args.logger.log("Mounted.")
  );
}

function SingleEditorEditApp(props: { openFileExtension: string; fileInfo: FileInfo }) {
  const globals = useGlobals();

  useEffect(() => {
    runScriptOnPage(chrome.runtime.getURL("scripts/set_content.js"));
  }, [props.fileInfo]);

  const getFileName = useCallback(() => {
    return globals.dependencies.all.edit__githubFileNameInput()!.value;
  }, [globals.dependencies]);

  const getFileContents = useCallback(() => {
    return Promise.resolve(globals.dependencies.all.edit__githubTextAreaWithFileContents()?.textContent ?? "");
  }, [globals.dependencies]);

  return (
    <SingleEditorApp
      readonly={false}
      openFileExtension={props.openFileExtension}
      getFileName={getFileName}
      getFileContents={getFileContents}
      iframeContainer={iframeContainer(globals.id, globals.dependencies)}
      toolbarContainer={toolbarContainer(globals.id, globals.dependencies)}
      githubTextEditorToReplace={globals.dependencies.singleEdit.githubTextEditorToReplaceElement()!}
      fileInfo={props.fileInfo}
    />
  );
}

function cleanup(id: string, dependencies: Dependencies) {
  removeAllChildren(iframeContainer(id, dependencies));
  removeAllChildren(toolbarContainer(id, dependencies));
  removeAllChildren(iframeFullscreenContainer(id, dependencies.all.body()));
  removeAllChildren(createAndGetMainContainer(id, dependencies.all.body()));
}

function toolbarContainer(id: string, dependencies: Dependencies) {
  const element = () => document.querySelector(`.${KOGITO_TOOLBAR_CONTAINER_CLASS}.${id}`)!;
  if (element) {
    element()?.remove();
  }
  dependencies.singleEdit
    .toolbarContainerTarget()!
    .insertAdjacentHTML(
      "beforebegin",
      `<div style="width:100%; padding-bottom: 10px;" class="${KOGITO_TOOLBAR_CONTAINER_CLASS} ${id} edit d-flex flex-column flex-items-start flex-md-row"></div>`
    );

  return element() as HTMLElement;
}

function iframeContainer(id: string, dependencies: Dependencies) {
  const element = () => document.querySelector(`.${KOGITO_IFRAME_CONTAINER_CLASS}.${id}`)!;
  if (element) {
    element()?.remove();
  }
  dependencies.singleEdit
    .iframeContainerTarget()!
    .insertAdjacentHTML("afterend", `<div class="${KOGITO_IFRAME_CONTAINER_CLASS} ${id} edit"></div>`);

  return element() as HTMLElement;
}
