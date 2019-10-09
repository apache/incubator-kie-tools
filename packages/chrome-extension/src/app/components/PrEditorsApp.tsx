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
import { Router } from "@kogito-tooling/core-api";
import { SingleEditorApp } from "./SingleEditorApp";
import { ToolbarPr } from "./ToolbarPr";

function getFilePath(prFileElement: HTMLElement) {
  return (prFileElement.querySelector(".file-info > .link-gray-dark") as HTMLAnchorElement).title;
}

function getFileExtension(prFileElement: HTMLElement) {
  return getFilePath(prFileElement)
    .split(".")
    .pop()!;
}

function createEditorContainer(e: HTMLElement) {
  e.insertAdjacentHTML("beforeend", '<div class="kogito-iframe-container-pr"></div>');
  return e.lastChild as HTMLElement;
}

function getGithubDomElementsPr(container: HTMLElement) {
  const infos = document.querySelector(".gh-header-meta")!.querySelectorAll(".css-truncate-target");

  const organization = infos[4].textContent;
  const repository = window.location.pathname.split("/")[2];
  const gitReference = infos[5].textContent;
  const filePath = getFilePath(container);

  return {
    getFileContents(): Promise<string> {
      return fetch(`https://raw.githubusercontent.com/${organization}/${repository}/${gitReference}/${filePath}`).then(
        res => res.text()
      );
    },
    githubTextEditorToReplace(): HTMLElement {
      return container.querySelector(".js-file-content") as HTMLElement;
    },
    iframeContainer(): HTMLElement {
      return container.querySelector(".kogito-iframe-container-pr") as HTMLElement;
    },
    iframeFullscreenContainer(): HTMLElement {
      //FIXME: Make some outer component manage fullscreen state
      // document.body.insertAdjacentHTML("beforeend", `\<div id="kogito-iframe-fullscreen-container"></div>`);
      // return document.body.querySelector("#kogito-iframe-fullscreen-container") as HTMLElement;
      return document.body.lastChild as HTMLElement;
    },
    toolbarContainer(): Element {
      const element = () => container.querySelector(".kogito-toolbar-container-pr");
      if (!element()) {
        container
          .querySelector(".file-info")!
          .insertAdjacentHTML("afterend", `\<div class="kogito-toolbar-container-pr"></div>`);
      }
      return element()!;
    }
  };
}

export function PrEditorsApp(props: { router: Router }) {
  const prFileElements = document.querySelectorAll(".file.js-file.js-details-container");

  const supportedFileElements = Array.from(prFileElements).filter(prFileElement =>
    props.router.getLanguageData(getFileExtension(prFileElement as HTMLElement))
  );

  return (
    <>
      {supportedFileElements.map(e => {
        return ReactDOM.createPortal(
          <SingleEditorApp
            openFileExtension={getFileExtension(e as HTMLElement)}
            githubDomElements={getGithubDomElementsPr(e as HTMLElement)}
            router={props.router}
            toolbar={() => <ToolbarPr />}
            readonly={true}
            textModeAsDefault={true}
            keepRenderedEditorInTextMode={false}
          />,
          createEditorContainer(e as HTMLElement)
        );
      })}
    </>
  );
}
