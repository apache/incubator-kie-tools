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
import { useEffect, useState } from "react";
import { useGlobals } from "../common/GlobalContext";
import { Router } from "@kogito-tooling/core-api";
import * as dependencies__ from "../../dependencies";
import { getOriginalFilePath, IsolatedPrEditor, PrInfo } from "./IsolatedPrEditor";
import { Logger } from "../../../Logger";

export function PrEditorsApp(props: { prInfo: PrInfo, contentPath: string }) {
  const globals = useGlobals();
  const [prFileContainers, setPrFileContainers] = useState(supportedPrFileElements(globals.logger, globals.router));

  useMutationObserverEffect(
    new MutationObserver(mutations => {
      const addedNodes = mutations.reduce((l, r) => [...l, ...Array.from(r.addedNodes)], []);

      if (addedNodes.length <= 0) {
        return;
      }

      const newContainers = supportedPrFileElements(globals.logger, globals.router);
      if (newContainers.length !== prFileContainers.length) {
        globals.logger.log("Found new containers...");
        setPrFileContainers(newContainers);
      }
    }),
    {
      childList: true,
      subtree: true
    }
  );

  return (
    <>
      {prFileContainers.map(container => (
        <IsolatedPrEditor
          key={getUnprocessedFilePath(container)}
          prInfo={props.prInfo}
          contentPath={props.contentPath}
          prFileContainer={container}
          fileExtension={getFileExtension(container)}
          unprocessedFilePath={getUnprocessedFilePath(container)}
          githubTextEditorToReplace={dependencies__.prView.githubTextEditorToReplaceElement(container) as HTMLElement}
        />
      ))}
    </>
  );
}

function supportedPrFileElements(logger: Logger, router: Router) {
  return prFileElements(logger).filter(container => router.getLanguageData(getFileExtension(container)));
}

function useMutationObserverEffect(observer: MutationObserver, options: MutationObserverInit) {
  useEffect(() => {
    observer.observe(dependencies__.all.pr__mutationObserverTarget()!, options);
    return () => observer.disconnect();
  }, []);
}

function prFileElements(logger: Logger) {
  const elements = dependencies__.all.array.pr__supportedPrFileContainers();
  if (!elements) {
    logger.log("Could not find file containers...");
    return [];
  }

  return elements;
}

function getFileExtension(prFileContainer: HTMLElement) {
  const unprocessedFilePath = getUnprocessedFilePath(prFileContainer);
  return getOriginalFilePath(unprocessedFilePath)
    .split(".")
    .pop()!;
}

export function getUnprocessedFilePath(prFileContainer: HTMLElement) {
  return dependencies__.all.pr__unprocessedFilePathContainer(prFileContainer)!.title;
}
