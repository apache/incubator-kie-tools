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
import * as React from "react";
import { useContext, useRef, useState } from "react";
import { FileStatusOnPr } from "./FileStatusOnPr";
import {
  useEffectAfterFirstRender,
  useInitialAsyncCallEffect,
  useIsolatedEditorTogglingEffect
} from "../common/customEffects";
import { IsolatedEditorRef } from "../common/IsolatedEditorRef";
import { IsolatedEditorContext } from "../common/IsolatedEditorContext";
import * as ReactDOM from "react-dom";
import { PrToolbar } from "./PrToolbar";
import { IsolatedEditor } from "../common/IsolatedEditor";
import {
  GITHUB_RENAMED_FILE_ARROW,
  KOGITO_IFRAME_CONTAINER_PR_CLASS,
  KOGITO_TOOLBAR_CONTAINER_PR_CLASS,
  KOGITO_VIEW_ORIGINAL_LINK_CONTAINER_PR_CLASS
} from "../../constants";
import { GlobalContext } from "../common/GlobalContext";

export interface PrInformation {
  repository: string;
  targetOrganization: string;
  targetGitReference: string;
  organization: string;
  gitReference: string;
}

export function IsolatedPrEditor(props: {
  prInfo: PrInformation;
  prFileContainer: HTMLElement;
  fileExtension: string;
  githubTextEditorToReplace: HTMLElement;
  unprocessedFilePath: string;
}) {
  const [showOriginal, setShowOriginal] = useState(false);
  const [textMode, setTextMode] = useState(true);
  const [editorReady, setEditorReady] = useState(false);
  const [fileStatusOnPr, setFileStatusOnPr] = useState(FileStatusOnPr.UNKNOWN);

  const isolatedEditorRef = useRef<IsolatedEditorRef>(null);
  const originalFilePath = getOriginalFilePath(props.unprocessedFilePath);
  const modifiedFilePath = getModifiedFilePath(props.unprocessedFilePath);

  useIsolatedEditorTogglingEffect(textMode, iframeContainer(props.prFileContainer), props.githubTextEditorToReplace);

  useInitialAsyncCallEffect(
    () => discoverFileStatusOnPr(props.prInfo, originalFilePath, modifiedFilePath),
    setFileStatusOnPr
  );

  useEffectAfterFirstRender(
    () => {
      getFileContents().then(c => {
        if (isolatedEditorRef.current) {
          isolatedEditorRef.current.setContent(c || "");
        }
      });
    },
    [showOriginal]
  );

  const closeDiagram = () => {
    setTextMode(true);
    setEditorReady(false);
  };

  const getFileContents =
    showOriginal || fileStatusOnPr === FileStatusOnPr.DELETED
      ? () => getOriginalFileContents(props.prInfo, originalFilePath)
      : () => getModifiedFileContents(props.prInfo, modifiedFilePath);

  const shouldAddLinkToOriginalFile =
    fileStatusOnPr === FileStatusOnPr.CHANGED || fileStatusOnPr === FileStatusOnPr.DELETED;

  const globalContext = useContext(GlobalContext);

  return (
    <IsolatedEditorContext.Provider
      value={{ textMode: textMode, fullscreen: false, onEditorReady: () => setEditorReady(true) }}
    >
      {shouldAddLinkToOriginalFile &&
        ReactDOM.createPortal(
          <a className={"pl-5 dropdown-item btn-link"} href={viewOriginalFileHref(props.prInfo, originalFilePath)}>
            View original file
          </a>,
          viewOriginalFileLinkContainer(
            props.prFileContainer,
            dependencies__.all.pr__viewOriginalFileLinkContainer(props.prFileContainer)!
          )
        )}

      {ReactDOM.createPortal(
        <PrToolbar
          showOriginalChangesToggle={editorReady}
          fileStatusOnPr={fileStatusOnPr}
          textMode={textMode}
          originalDiagram={showOriginal}
          toggleOriginal={() => setShowOriginal(prev => !prev)}
          closeDiagram={closeDiagram}
          onSeeAsDiagram={() => setTextMode(false)}
        />,
        toolbarContainer(props.prFileContainer, dependencies__.prView.toolbarContainerTarget(
          props.prFileContainer
        ) as HTMLElement)
      )}

      {ReactDOM.createPortal(
        <IsolatedEditor
          ref={isolatedEditorRef}
          textMode={textMode}
          getFileContents={getFileContents}
          openFileExtension={props.fileExtension}
          readonly={true}
          keepRenderedEditorInTextMode={false}
        />,
        iframeContainer(dependencies__.prView.iframeContainerTarget(props.prFileContainer) as HTMLElement)
      )}
    </IsolatedEditorContext.Provider>
  );
}

async function discoverFileStatusOnPr(prInfo: PrInformation, originalFilePath: string, modifiedFilePath: string) {
  const hasOriginal = await getOriginalFileContents(prInfo, originalFilePath);
  const hasModified = await getModifiedFileContents(prInfo, modifiedFilePath);

  if (hasOriginal && hasModified) {
    return FileStatusOnPr.CHANGED;
  }

  if (hasOriginal) {
    return FileStatusOnPr.DELETED;
  }

  if (hasModified) {
    return FileStatusOnPr.ADDED;
  }

  throw new Error("Impossible status for file on PR");
}

export function getOriginalFilePath(path: string) {
  if (path.includes(GITHUB_RENAMED_FILE_ARROW)) {
    return path.split(` ${GITHUB_RENAMED_FILE_ARROW} `)[0];
  } else {
    return path;
  }
}

export function getModifiedFilePath(path: string) {
  if (path.includes(GITHUB_RENAMED_FILE_ARROW)) {
    return path.split(` ${GITHUB_RENAMED_FILE_ARROW} `)[1];
  } else {
    return path;
  }
}

function viewOriginalFileLinkContainer(prFileContainer: HTMLElement, container: HTMLElement) {
  const div = `<div class="${KOGITO_VIEW_ORIGINAL_LINK_CONTAINER_PR_CLASS}"></div>`;
  const element = () => prFileContainer.querySelector(`.${KOGITO_VIEW_ORIGINAL_LINK_CONTAINER_PR_CLASS}`);

  if (!element()) {
    container.insertAdjacentHTML("afterend", div);
  }

  return element()!;
}

function toolbarContainer(prFileContainer: HTMLElement, container: HTMLElement) {
  const div = `<div class="${KOGITO_TOOLBAR_CONTAINER_PR_CLASS}"></div>`;
  const element = () => prFileContainer.querySelector(`.${KOGITO_TOOLBAR_CONTAINER_PR_CLASS}`);

  if (!element()) {
    container.insertAdjacentHTML("afterend", div);
  }

  return element()!;
}

function iframeContainer(container: HTMLElement) {
  const div = `<div class="${KOGITO_IFRAME_CONTAINER_PR_CLASS}"></div>`;
  const element = () => container.querySelector(`.${KOGITO_IFRAME_CONTAINER_PR_CLASS}`);

  if (!element()!) {
    container.insertAdjacentHTML("beforeend", div);
  }

  return element() as HTMLElement;
}

function getModifiedFileContents(prInfo: PrInformation, modifiedFilePath: string) {
  const org = prInfo.organization;
  const repo = prInfo.repository;
  const branch = prInfo.gitReference;
  return fetch(`https://raw.githubusercontent.com/${org}/${repo}/${branch}/${modifiedFilePath}`).then(res => {
    return res.ok ? res.text() : Promise.resolve(undefined);
  });
}

function getOriginalFileContents(prInfo: PrInformation, originalFilePath: string) {
  const org = prInfo.targetOrganization;
  const repo = prInfo.repository;
  const branch = prInfo.targetGitReference;
  return fetch(`https://raw.githubusercontent.com/${org}/${repo}/${branch}/${originalFilePath}`).then(res => {
    return res.ok ? res.text() : Promise.resolve(undefined);
  });
}

function viewOriginalFileHref(prInfo: PrInformation, originalFilePath: string) {
  const org = prInfo.targetOrganization;
  const repo = prInfo.repository;
  const branch = prInfo.targetGitReference;

  return `/${org}/${repo}/blob/${branch}/${originalFilePath}`;
}
