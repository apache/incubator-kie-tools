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
import { ResolvedDomDependency } from "../../dependencies";
import { Feature } from "../common/Feature";
import { KOGITO_IFRAME_CONTAINER_ID, KOGITO_TOOLBAR_CONTAINER_ID } from "../../constants";

export function renderSingleEditorApp(args: { editorIndexPath: string; router: Router }) {
  // Checking whether this text editor exists is a good way to determine if the page is "ready",
  // because that would mean that the user could see the default GitHub page.
  if (!dependencies__.singleEdit.githubTextEditorToReplaceElement()) {
    console.info(`[Kogito] Doesn't look like the GitHub page is ready yet.`);
    return;
  }

  // Necessary because GitHub apparently "caches" DOM structures between changes on History.
  // Without this method you can observe duplicated elements when using back/forward browser buttons.
  cleanup();

  const openFileExtension = extractOpenFileExtension(window.location.href);
  if (!openFileExtension) {
    console.info(`[Kogito] Unable to determine file extension from URL.`);
    return;
  }

  if (!args.router.getLanguageData(openFileExtension)) {
    console.info(`[Kogito] No enhanced editor available for "${openFileExtension}" format.`);
    return;
  }

  ReactDOM.render(
    <Main router={args.router} editorIndexPath={args.editorIndexPath} commonDependencies={dependencies__.singleEdit}>
      <Feature
        name={"Editable editor"}
        dependencies={deps => ({
          fileContents: () => deps.all.edit__githubTextAreaWithFileContents(),
          iframeContainerTarget: () => deps.common.iframeContainerTarget(),
          toolbarContainerTarget: () => deps.common.toolbarContainerTarget(),
          githubTextEditorToReplace: () => deps.common.githubTextEditorToReplaceElement()
        })}
        component={resolved => (
          <SingleEditorApp
            readonly={false}
            openFileExtension={openFileExtension}
            getFileContents={() => getFileContents(resolved.fileContents)}
            iframeContainer={iframeContainer(resolved.iframeContainerTarget)}
            toolbarContainer={toolbarContainer(resolved.toolbarContainerTarget)}
            githubTextEditorToReplace={resolved.githubTextEditorToReplace}
          />
        )}
      />
    </Main>,
    createAndGetMainContainer({ name: "", element: dependencies__.all.body() }),
    () => console.info("[Kogito] Mounted.")
  );
}

function cleanup() {
  //FIXME: Unchecked dependency use
  removeAllChildren(iframeContainer({ name: "", element: dependencies__.singleEdit.iframeContainerTarget()! }));
  removeAllChildren(toolbarContainer({ name: "", element: dependencies__.singleEdit.toolbarContainerTarget()! }));
  removeAllChildren(iframeFullscreenContainer({ name: "", element: dependencies__.all.body() }));
  removeAllChildren(createAndGetMainContainer({ name: "", element: dependencies__.all.body() }));
}

function getFileContents(domDependency: ResolvedDomDependency) {
  return Promise.resolve((domDependency.element as HTMLTextAreaElement).value);
}

function toolbarContainer(domDependency: ResolvedDomDependency) {
  const div = `<div id="${KOGITO_TOOLBAR_CONTAINER_ID}" class="edit d-flex flex-column flex-items-start flex-md-row"></div>`;
  const element = () => document.getElementById(KOGITO_TOOLBAR_CONTAINER_ID)!;

  if (!element()) {
    domDependency.element.insertAdjacentHTML("beforeend", div);
  }

  return element();
}

function iframeContainer(domDependency: ResolvedDomDependency) {
  const div = `<div id="${KOGITO_IFRAME_CONTAINER_ID}" class="edit"></div>`;
  const element = () => document.getElementById(KOGITO_IFRAME_CONTAINER_ID)!;

  if (!element()) {
    domDependency.element.insertAdjacentHTML("afterend", div);
  }

  return element();
}
