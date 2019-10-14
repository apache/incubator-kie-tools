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
import { getOriginalFilePath, getUnprocessedFilePath, GitHubDomElementsPr } from "./GitHubDomElementsPr";
import { GlobalContext } from "../common/GlobalContext";
import { FileStatusOnPr } from "./FileStatusOnPr";
import { useInitialAsyncCallEffect } from "../../utils";
import { Router } from "@kogito-tooling/core-api";
import * as dependencies__ from "../../dependencies";
import { useEffectWithDependencies } from "../common/useEffectWithDependencies";
import { Feature } from "../common/Feature";

export function PrEditorsApp() {
  const globalContext = useContext(GlobalContext);
  const [containers, setContainers] = useState(supportedPrFileElements(globalContext.router));

  useMutationObserverEffect(newPrFileContainersMutationObserver(containers, setContainers, globalContext.router), {
    childList: true,
    subtree: true
  });

  return (
    <>
      {containers.map(e => (
        <IsolatedPrEditor key={getUnprocessedFilePath(e)} container={e} />
      ))}
    </>
  );
}

function IsolatedPrEditor(props: { container: HTMLElement }) {
  const globalContext = useContext(GlobalContext);
  const githubDomElements = new GitHubDomElementsPr(props.container as HTMLElement, globalContext.router);

  const [showOriginal, setShowOriginal] = useState(false);
  const [isTextMode, setTextMode] = useState(true);
  const [fileStatusOnPr, setFileStatusOnPr] = useState(FileStatusOnPr.UNKNOWN);

  useIsolatedEditorTogglingEffect(isTextMode, githubDomElements, props.container);
  useInitialAsyncCallEffect(() => discoverFileStatusOnPr(githubDomElements), setFileStatusOnPr);

  const getFileContents =
    showOriginal || fileStatusOnPr === FileStatusOnPr.DELETED
      ? () => githubDomElements.getOriginalFileContents()
      : () => githubDomElements.getFileContents();

  const shouldAddLinkToOriginalFile =
    fileStatusOnPr === FileStatusOnPr.CHANGED || fileStatusOnPr === FileStatusOnPr.DELETED;

  return (
    <IsolatedEditorContext.Provider value={{ textMode: isTextMode, fullscreen: false }}>
      {shouldAddLinkToOriginalFile && (
        <Feature
          name={"Link to original file"}
          dependencies={deps => ({ container: () => deps.prView.viewOriginalFileLinkContainer(props.container) })}
          component={deps =>
            ReactDOM.createPortal(
              <a className={"pl-5 dropdown-item btn-link"} href={githubDomElements.viewOriginalFileHref()}>
                View original file
              </a>,
              githubDomElements.viewOriginalFileLinkContainer(deps.container()!)
            )
          }
        />
      )}

      <Feature
        name={"Toolbar"}
        dependencies={deps => ({ container: () => deps.common.toolbarContainerTarget(props.container) })}
        component={deps =>
          ReactDOM.createPortal(
            <PrToolbar
              fileStatusOnPr={fileStatusOnPr}
              textMode={isTextMode}
              originalDiagram={showOriginal}
              toggleOriginal={() => setShowOriginal(prev => !prev)}
              closeDiagram={() => setTextMode(true)}
              onSeeAsDiagram={() => setTextMode(false)}
            />,
            githubDomElements.toolbarContainer(deps.container()!)
          )
        }
      />

      {ReactDOM.createPortal(
        <IsolatedEditor
          textMode={isTextMode}
          key={`${showOriginal}`}
          getFileContents={getFileContents}
          openFileExtension={getFileExtension(props.container)}
          readonly={true}
          keepRenderedEditorInTextMode={false}
        />,
        githubDomElements.iframeContainer(props.container)
      )}
    </IsolatedEditorContext.Provider>
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

function getFileExtension(container: HTMLElement) {
  return getOriginalFilePath(container)
    .split(".")
    .pop()!;
}

function newPrFileContainersMutationObserver(
  containers: HTMLElement[],
  setContainers: (e: HTMLElement[]) => void,
  router: Router
) {
  return new MutationObserver(mutations => {
    const addedNodes = mutations.reduce((l, r) => [...l, ...Array.from(r.addedNodes)], []);

    if (addedNodes.length <= 0) {
      return;
    }

    const newContainers = supportedPrFileElements(router);
    if (newContainers.length !== containers.length) {
      setContainers(newContainers);
    }
  });
}

function supportedPrFileElements(router: Router) {
  return dependencies__.prView
    .supportedPrFileContainers()
    .filter(container => router.getLanguageData(getFileExtension(container)));
}

function useMutationObserverEffect(observer: MutationObserver, options: MutationObserverInit) {
  useEffectWithDependencies(
    "Mutation observer",
    dependencies => ({ target: () => dependencies.prView.mutationObserverTarget() }),
    resolvedDependencies => {
      observer.observe(resolvedDependencies.target()!, options);
      return () => {
        observer.disconnect();
      };
    },
    []
  );
}
