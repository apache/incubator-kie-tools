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
import { useContext, useState } from "react";
import * as ReactDOM from "react-dom";
import { IsolatedEditor, useIsolatedEditorTogglingEffect } from "../common/IsolatedEditor";
import { PrToolbar } from "./PrToolbar";
import { IsolatedEditorContext } from "../common/IsolatedEditorContext";
import { getFilePath, getPrFileElements, GitHubDomElementsPr } from "./GitHubDomElementsPr";
import { GlobalContext } from "../common/GlobalContext";

function getFileExtension(prFileElement: HTMLElement) {
  return getFilePath(prFileElement)
    .split(".")
    .pop()!;
}

export function PrEditorsApp() {
  const globalContext = useContext(GlobalContext);
  const supportedPrFileElements = Array.from(getPrFileElements()).filter(prFileElement => {
    return globalContext.router.getLanguageData(getFileExtension(prFileElement as HTMLElement));
  });

  return (
    <>
      {supportedPrFileElements.map(e => (
        <IsolatedPrEditor key={getFilePath(e as HTMLElement)} container={e as HTMLElement} />
      ))}
    </>
  );
}

function IsolatedPrEditor(props: { container: HTMLElement }) {
  const githubDomElements = new GitHubDomElementsPr(props.container as HTMLElement);

  const [original, setOriginal] = useState(false);
  const [textMode, setTextMode] = useState(true);

  useIsolatedEditorTogglingEffect(textMode, githubDomElements);

  const getFileContents = original
    ? () => githubDomElements.getOriginalFileContents()
    : () => githubDomElements.getFileContents();

  return (
    <IsolatedEditorContext.Provider value={{ textMode: textMode, fullscreen: false }}>
      {ReactDOM.createPortal(
        <a className={"pl-5 dropdown-item btn-link"} href={githubDomElements.viewOriginalFileHref()}>
          View original file
        </a>,
        githubDomElements.viewOriginalFileLinkContainer()
      )}

      {ReactDOM.createPortal(
        <PrToolbar
          textMode={textMode}
          originalDiagram={original}
          toggleOriginal={() => setOriginal(prev => !prev)}
          onSeeAsSource={() => setTextMode(true)}
          onSeeAsDiagram={() => setTextMode(false)}
        />,
        githubDomElements.toolbarContainer()
      )}

      {ReactDOM.createPortal(
        <IsolatedEditor
          textMode={textMode}
          key={`${original}`}
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
