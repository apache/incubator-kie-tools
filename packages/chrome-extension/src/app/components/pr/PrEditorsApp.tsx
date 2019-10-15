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
import { useContext, useRef, useState } from "react";
import * as ReactDOM from "react-dom";
import { IsolatedEditor } from "../common/IsolatedEditor";
import { PrToolbar } from "./PrToolbar";
import { IsolatedEditorContext } from "../common/IsolatedEditorContext";
import { getOriginalFilePath, getUnprocessedFilePath, GitHubDomElementsPr } from "./GitHubDomElementsPr";
import { GlobalContext } from "../common/GlobalContext";
import { FileStatusOnPr } from "./FileStatusOnPr";
import {
  useEffectAfterFirstRender,
  useEffectWithDependencies,
  useInitialAsyncCallEffect,
  useIsolatedEditorTogglingEffect
} from "../common/customEffects";
import { Router } from "@kogito-tooling/core-api";
import * as dependencies__ from "../../dependencies";
import { ResolvedDomDependency } from "../../dependencies";
import { Feature } from "../common/Feature";
import { IsolatedEditorRef } from "../common/IsolatedEditorRef";

export function PrEditorsApp() {
  const prFileElements = () => dependencies__.all.supportedPrFileContainers().map(e => ({ name: "", element: e }));

  const globalContext = useContext(GlobalContext);
  const [containers, setContainers] = useState(supportedPrFileElements(prFileElements, globalContext.router));

  useMutationObserverEffect(
    newPrFileContainersMutationObserver(prFileElements, containers, setContainers, globalContext.router),
    {
      childList: true,
      subtree: true
    }
  );

  return (
    <>
      {containers.map(e => (
        <IsolatedPrEditor key={getUnprocessedFilePath(e)} container={e} />
      ))}
    </>
  );
}

function IsolatedPrEditor(props: { container: ResolvedDomDependency }) {
  const globalContext = useContext(GlobalContext);
  const githubDomElements = new GitHubDomElementsPr(props.container, globalContext.router);

  const [showOriginal, setShowOriginal] = useState(false);
  const [isTextMode, setTextMode] = useState(true);
  const [fileStatusOnPr, setFileStatusOnPr] = useState(FileStatusOnPr.UNKNOWN);

  useIsolatedEditorTogglingEffect(isTextMode, c => githubDomElements.iframeContainer(c), props.container);

  useInitialAsyncCallEffect(() => discoverFileStatusOnPr(githubDomElements), setFileStatusOnPr);

  useEffectAfterFirstRender(
    () => {
      getFileContents().then(c => {
        if (ref.current) {
          ref.current.setContent(c || "");
        }
      });
    },
    [showOriginal]
  );

  const getFileContents =
    showOriginal || fileStatusOnPr === FileStatusOnPr.DELETED
      ? () => githubDomElements.getOriginalFileContents()
      : () => githubDomElements.getFileContents();

  const shouldAddLinkToOriginalFile =
    fileStatusOnPr === FileStatusOnPr.CHANGED || fileStatusOnPr === FileStatusOnPr.DELETED;

  const ref = useRef<IsolatedEditorRef>(null);

  return (
    <IsolatedEditorContext.Provider value={{ textMode: isTextMode, fullscreen: false }}>
      {shouldAddLinkToOriginalFile && (
        <Feature
          name={"Link to original file"}
          dependencies={deps => ({ container: () => dependencies__.all.viewOriginalFileLinkContainer(props.container) })}
          component={deps =>
            ReactDOM.createPortal(
              <a className={"pl-5 dropdown-item btn-link"} href={githubDomElements.viewOriginalFileHref()}>
                View original file
              </a>,
              githubDomElements.viewOriginalFileLinkContainer(deps.container)
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
            githubDomElements.toolbarContainer(deps.container)
          )
        }
      />

      {ReactDOM.createPortal(
        <IsolatedEditor
          ref={ref}
          textMode={isTextMode}
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

function getFileExtension(container: ResolvedDomDependency) {
  return getOriginalFilePath(container)
    .split(".")
    .pop()!;
}

function newPrFileContainersMutationObserver(
  prFileElements: () => ResolvedDomDependency[],
  containers: ResolvedDomDependency[],
  setContainers: (e: ResolvedDomDependency[]) => void,
  router: Router
) {
  return new MutationObserver(mutations => {
    const addedNodes = mutations.reduce((l, r) => [...l, ...Array.from(r.addedNodes)], []);

    if (addedNodes.length <= 0) {
      return;
    }

    const newContainers = supportedPrFileElements(prFileElements, router);
    if (newContainers.length !== containers.length) {
      setContainers(newContainers);
    }
  });
}

function supportedPrFileElements(prFileElements: () => ResolvedDomDependency[], router: Router) {
  return prFileElements().filter(container => router.getLanguageData(getFileExtension(container)));
}

function useMutationObserverEffect(observer: MutationObserver, options: MutationObserverInit) {
  useEffectWithDependencies(
    "Mutation observer",
    dependencies => ({ target: () => dependencies__.all.mutationObserverTarget() }),
    resolvedDependencies => {
      observer.observe(resolvedDependencies.target.element, options);
      return () => {
        observer.disconnect();
      };
    },
    []
  );
}
