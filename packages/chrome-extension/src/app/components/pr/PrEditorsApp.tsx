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
import { useContext, useEffect, useState } from "react";
import * as ReactDOM from "react-dom";
import { IsolatedEditor, useIsolatedEditorTogglingEffect } from "../common/IsolatedEditor";
import { PrToolbar } from "./PrToolbar";
import { IsolatedEditorContext } from "../common/IsolatedEditorContext";
import {
  getOriginalFilePath,
  getPrFileElements,
  getUnprocessedFilePath,
  GitHubDomElementsPr
} from "./GitHubDomElementsPr";
import { GlobalContext } from "../common/GlobalContext";
import { FileStatusOnPr } from "./FileStatusOnPr";
import { useInitialAsyncCallEffect } from "../../utils";

function getFileExtension(prFileElement: HTMLElement) {
  return getOriginalFilePath(prFileElement)
    .split(".")
    .pop()!;
}

export function PrEditorsApp() {
  const globalContext = useContext(GlobalContext);
  const supportedPrFileElements = () =>
    Array.from(getPrFileElements()).filter(prFileElement => {
      return globalContext.router.getLanguageData(getFileExtension(prFileElement as HTMLElement));
    }) as HTMLElement[];

  const [elements, setElements] = useState(supportedPrFileElements());

  const observer = new MutationObserver(mutations => {
    const newFiles = mutations.reduce((l, r) => [...l, ...Array.from(r.addedNodes)], []).filter(n => {
      return n instanceof HTMLElement && n.className.includes("js-file");
    });

    if (newFiles.length > 0) {
      setElements(supportedPrFileElements());
    }
  });

  useEffect(() => {
    observer.observe(document.getElementById("files")!, { childList: true, subtree: true });
    return () => {
      observer.disconnect();
    };
  }, []);

  return (
    <>
      {elements.map(e => (
        <IsolatedPrEditor key={getUnprocessedFilePath(e)} container={e} />
      ))}
    </>
  );
}

async function discoverFileStatusOnPr(githubDomElements: GitHubDomElementsPr) {
  const hasOriginal = await githubDomElements.getOriginalFileContents();
  const hasModified = await githubDomElements.getFileContents();

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

function IsolatedPrEditor(props: { container: HTMLElement }) {
  const githubDomElements = new GitHubDomElementsPr(props.container as HTMLElement);

  const [showOriginal, setShowOriginal] = useState(false);
  const [isTextMode, setTextMode] = useState(true);
  const [fileStatusOnPr, setFileStatusOnPr] = useState(FileStatusOnPr.UNKNOWN);

  useIsolatedEditorTogglingEffect(isTextMode, githubDomElements);
  useInitialAsyncCallEffect(() => discoverFileStatusOnPr(githubDomElements), setFileStatusOnPr);

  const getFileContents =
    showOriginal || fileStatusOnPr === FileStatusOnPr.DELETED
      ? () => githubDomElements.getOriginalFileContents()
      : () => githubDomElements.getFileContents();

  const shouldAddLinkToOriginalFile =
    fileStatusOnPr === FileStatusOnPr.CHANGED || fileStatusOnPr === FileStatusOnPr.DELETED;

  return (
    <IsolatedEditorContext.Provider value={{ textMode: isTextMode, fullscreen: false }}>
      {shouldAddLinkToOriginalFile &&
        ReactDOM.createPortal(
          <a className={"pl-5 dropdown-item btn-link"} href={githubDomElements.viewOriginalFileHref()}>
            View original file
          </a>,
          githubDomElements.viewOriginalFileLinkContainer()
        )}

      {ReactDOM.createPortal(
        <PrToolbar
          fileStatusOnPr={fileStatusOnPr}
          textMode={isTextMode}
          originalDiagram={showOriginal}
          toggleOriginal={() => setShowOriginal(prev => !prev)}
          closeDiagram={() => setTextMode(true)}
          onSeeAsDiagram={() => setTextMode(false)}
        />,
        githubDomElements.toolbarContainer()
      )}

      {ReactDOM.createPortal(
        <IsolatedEditor
          textMode={isTextMode}
          key={`${showOriginal}`}
          getFileContents={getFileContents}
          openFileExtension={getFileExtension(props.container)}
          readonly={true}
          keepRenderedEditorInTextMode={false}
        />,
        githubDomElements.iframeContainer()
      )}
    </IsolatedEditorContext.Provider>
  );
}
