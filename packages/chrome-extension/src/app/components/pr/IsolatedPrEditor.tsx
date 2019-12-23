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
import { useCallback, useMemo, useRef, useState } from "react";
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
  KOGITO_OPEN_WITH_ONLINE_EDITOR_LINK_CONTAINER_PR_CLASS,
  KOGITO_VIEW_ORIGINAL_LINK_CONTAINER_PR_CLASS
} from "../../constants";
import * as Octokit from "@octokit/rest";
import { fetchFile } from "../../github/api";
import { useGitHubApi } from "../common/GitHubContext";
import { useGlobals } from "../common/GlobalContext";

export interface PrInfo {
  repo: string;
  targetOrg: string;
  targetGitRef: string;
  org: string;
  gitRef: string;
}

export function IsolatedPrEditor(props: {
  prInfo: PrInfo;
  prFileContainer: HTMLElement;
  fileExtension: string;
  contentPath: string;
  githubTextEditorToReplace: HTMLElement;
  unprocessedFilePath: string;
}) {
  const githubApi = useGitHubApi();
  const globals = useGlobals();

  const [showOriginal, setShowOriginal] = useState(false);
  const [textMode, setTextMode] = useState(true);
  const [editorReady, setEditorReady] = useState(false);
  const [fileStatusOnPr, setFileStatusOnPr] = useState(FileStatusOnPr.UNKNOWN);

  const isolatedEditorRef = useRef<IsolatedEditorRef>(null);
  const originalFilePath = getOriginalFilePath(props.unprocessedFilePath);
  const modifiedFilePath = getModifiedFilePath(props.unprocessedFilePath);

  useIsolatedEditorTogglingEffect(
    textMode,
    iframeContainer(globals.id, props.prFileContainer),
    props.githubTextEditorToReplace
  );

  useInitialAsyncCallEffect(() => {
    return discoverFileStatusOnPr(githubApi.octokit(), props.prInfo, originalFilePath, modifiedFilePath);
  }, setFileStatusOnPr);

  useEffectAfterFirstRender(() => {
    getFileContents().then(c => {
      if (isolatedEditorRef.current) {
        isolatedEditorRef.current.setContent(c || "");
      }
    });
  }, [showOriginal]);

  const closeDiagram = useCallback(() => {
    setTextMode(true);
    setEditorReady(false);
  }, []);

  const filePath = showOriginal || fileStatusOnPr === FileStatusOnPr.DELETED ? originalFilePath : modifiedFilePath;

  const getFileContents =
    showOriginal || fileStatusOnPr === FileStatusOnPr.DELETED
      ? () => getOriginalFileContents(githubApi.octokit(), props.prInfo, originalFilePath)
      : () => getModifiedFileContents(githubApi.octokit(), props.prInfo, modifiedFilePath);

  const shouldAddLinkToOriginalFile =
    fileStatusOnPr === FileStatusOnPr.CHANGED || fileStatusOnPr === FileStatusOnPr.DELETED;

  const openExternalEditor = () => {
    getFileContents().then(fileContent => globals.externalEditorManager ?.open(filePath, fileContent!, true));
  };
  const repoInfo = useMemo(() => {
    return showOriginal
      ? {
        owner: props.prInfo.targetOrg,
        gitref: props.prInfo.targetGitRef,
        repo: props.prInfo.repo
      }
      : {
        owner: props.prInfo.org,
        gitref: props.prInfo.gitRef,
        repo: props.prInfo.repo
      };
  }, [showOriginal]);

  return (
    <IsolatedEditorContext.Provider
      value={{ textMode: textMode, fullscreen: false, repoInfo: repoInfo, onEditorReady: () => setEditorReady(true) }}
    >
      {shouldAddLinkToOriginalFile &&
        ReactDOM.createPortal(
          <a className={"pl-5 dropdown-item btn-link"} href={viewOriginalFileHref(props.prInfo, originalFilePath)}>
            View original file
          </a>,
          viewOriginalFileLinkContainer(
            globals.id,
            props.prFileContainer,
            dependencies__.all.pr__viewOriginalFileLinkContainer(props.prFileContainer)!
          )
        )}

      {globals.externalEditorManager &&
        ReactDOM.createPortal(
          <a className={"pl-5 dropdown-item btn-link"} onClick={openExternalEditor}>
            Open in {globals.externalEditorManager.name}
          </a>,
          openWithExternalEditorLinkContainer(
            props.prFileContainer,
            dependencies__.all.pr__openWithExternalEditorLinkContainer(props.prFileContainer)!
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
        toolbarContainer(
          globals.id,
          props.prFileContainer,
          dependencies__.prView.toolbarContainerTarget(props.prFileContainer) as HTMLElement
        )
      )}

      {ReactDOM.createPortal(
        <IsolatedEditor
          ref={isolatedEditorRef}
          textMode={textMode}
          getFileContents={getFileContents}
          contentPath={props.contentPath}
          openFileExtension={props.fileExtension}
          readonly={true}
          keepRenderedEditorInTextMode={false}
        />,
        iframeContainer(globals.id, dependencies__.prView.iframeContainerTarget(props.prFileContainer) as HTMLElement)
      )}
    </IsolatedEditorContext.Provider>
  );
}

async function discoverFileStatusOnPr(
  octokit: Octokit,
  prInfo: PrInfo,
  originalFilePath: string,
  modifiedFilePath: string
) {
  const hasOriginal = await getOriginalFileContents(octokit, prInfo, originalFilePath);
  const hasModified = await getModifiedFileContents(octokit, prInfo, modifiedFilePath);

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

function viewOriginalFileLinkContainer(id: string, prFileContainer: HTMLElement, container: HTMLElement) {
  const element = () => prFileContainer.querySelector(`.${KOGITO_VIEW_ORIGINAL_LINK_CONTAINER_PR_CLASS}.${id}`);

  if (!element()) {
    container.insertAdjacentHTML(
      "afterend",
      `<div class="${KOGITO_VIEW_ORIGINAL_LINK_CONTAINER_PR_CLASS} ${id}"></div>`
    );
  }

  return element()!;
}

function openWithExternalEditorLinkContainer(prFileContainer: HTMLElement, container: HTMLElement) {
  const div = `<div class="${KOGITO_OPEN_WITH_ONLINE_EDITOR_LINK_CONTAINER_PR_CLASS}"></div>`;
  const element = () => prFileContainer.querySelector(`.${KOGITO_OPEN_WITH_ONLINE_EDITOR_LINK_CONTAINER_PR_CLASS}`);

  if (!element()) {
    container.insertAdjacentHTML("beforebegin", div);
  }

  return element()!;
}

function toolbarContainer(id: string, prFileContainer: HTMLElement, container: HTMLElement) {
  const element = () => prFileContainer.querySelector(`.${KOGITO_TOOLBAR_CONTAINER_PR_CLASS}.${id}`);

  if (!element()) {
    container.insertAdjacentHTML("afterend", `<div class="${KOGITO_TOOLBAR_CONTAINER_PR_CLASS} ${id}"></div>`);
  }

  return element()!;
}

function iframeContainer(id: string, container: HTMLElement) {
  const element = () => container.querySelector(`.${KOGITO_IFRAME_CONTAINER_PR_CLASS}.${id}`);

  if (!element()!) {
    container.insertAdjacentHTML("beforeend", `<div class="${KOGITO_IFRAME_CONTAINER_PR_CLASS} ${id}"></div>`);
  }

  return element() as HTMLElement;
}

function getModifiedFileContents(octokit: Octokit, prInfo: PrInfo, modifiedFilePath: string) {
  return fetchFile(octokit, prInfo.org, prInfo.repo, prInfo.gitRef, modifiedFilePath);
}

function getOriginalFileContents(octokit: Octokit, prInfo: PrInfo, originalFilePath: string) {
  return fetchFile(octokit, prInfo.targetOrg, prInfo.repo, prInfo.targetGitRef, originalFilePath);
}

function viewOriginalFileHref(prInfo: PrInfo, originalFilePath: string) {
  const org = prInfo.targetOrg;
  const repo = prInfo.repo;
  const branch = prInfo.targetGitRef;

  return `/${org}/${repo}/blob/${branch}/${originalFilePath}`;
}
