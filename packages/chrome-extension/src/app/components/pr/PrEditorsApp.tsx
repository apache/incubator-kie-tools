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
import { Router } from "@kogito-tooling/microeditor-envelope-protocol";
import { Dependencies } from "../../Dependencies";
import { getOriginalFilePath, IsolatedPrEditor, PrInfo } from "./IsolatedPrEditor";
import { Logger } from "../../../Logger";

export function PrEditorsApp(props: { prInfo: PrInfo; contentPath: string }) {
  const globals = useGlobals();

  const [prFileContainers, setPrFileContainers] = useState<HTMLElement[]>([]);

  useEffect(() => {
    setPrFileContainers(supportedPrFileElements(globals.logger, globals.router, globals.dependencies));
  }, []);

  useEffect(() => {
    const observer = new MutationObserver(mutations => {
      const addedNodes = mutations.reduce((l, r) => [...l, ...Array.from(r.addedNodes)], []);
      if (addedNodes.length <= 0) {
        return;
      }

      const newContainers = supportedPrFileElements(globals.logger, globals.router, globals.dependencies);
      if (newContainers.length === prFileContainers.length) {
        globals.logger.log("Found new unsupported containers");
        return;
      }

      globals.logger.log("Found new containers...");
      setPrFileContainers(newContainers);
    });

    observer.observe(globals.dependencies.all.pr__mutationObserverTarget()!, {
      childList: true,
      subtree: true
    });

    return () => {
      observer.disconnect();
    };
  }, [prFileContainers]);

  return (
    <>
      {prFileContainers.map(container => (
        <IsolatedPrEditor
          key={getUnprocessedFilePath(container, globals.dependencies)}
          prInfo={props.prInfo}
          contentPath={props.contentPath}
          prFileContainer={container}
          fileExtension={getFileExtension(container, globals.dependencies)}
          unprocessedFilePath={getUnprocessedFilePath(container, globals.dependencies)}
          githubTextEditorToReplace={
            globals.dependencies.prView.githubTextEditorToReplaceElement(container) as HTMLElement
          }
        />
      ))}
    </>
  );
}

function supportedPrFileElements(logger: Logger, router: Router, dependencies: Dependencies) {
  return prFileElements(logger, dependencies).filter(container =>
    router.getLanguageData(getFileExtension(container, dependencies))
  );
}

function prFileElements(logger: Logger, dependencies: Dependencies) {
  const elements = dependencies.all.array.pr__supportedPrFileContainers();
  if (!elements) {
    logger.log("Could not find file containers...");
    return [];
  }

  return elements;
}

function getFileExtension(prFileContainer: HTMLElement, dependencies: Dependencies) {
  const unprocessedFilePath = getUnprocessedFilePath(prFileContainer, dependencies);
  return getOriginalFilePath(unprocessedFilePath)
    .split(".")
    .pop()!;
}

export function getUnprocessedFilePath(prFileContainer: HTMLElement, dependencies: Dependencies) {
  return dependencies.all.pr__unprocessedFilePathContainer(prFileContainer)!.title;
}
