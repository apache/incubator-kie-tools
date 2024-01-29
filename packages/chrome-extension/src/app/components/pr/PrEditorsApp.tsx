/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

import * as React from "react";
import { useCallback, useEffect, useState } from "react";
import { useGlobals } from "../common/GlobalContext";
import { EditorEnvelopeLocator } from "@kie-tools-core/editor/dist/api";
import { Dependencies } from "../../Dependencies";
import { getOriginalFilePath, IsolatedPrEditor, PrInfo } from "./IsolatedPrEditor";
import { Logger } from "../../../Logger";
import { GitHubPageType } from "../../github/GitHubPageType";

export function PrEditorsApp(props: {
  prInfo: PrInfo;
  pageType: GitHubPageType.PR_COMMITS | GitHubPageType.PR_FILES | GitHubPageType.PR_HOME;
}) {
  const globals = useGlobals();

  const [prFileContainers, setPrFileContainers] = useState<HTMLElement[]>([]);

  useEffect(() => {
    setPrFileContainers(supportedPrFileElements(globals.logger, globals.envelopeLocator, globals.dependencies));
  }, [globals.dependencies, globals.envelopeLocator, globals.logger]);

  const mutationObserverTargetNode = useCallback(() => {
    if (props.pageType === GitHubPageType.PR_COMMITS) {
      return globals.dependencies.all.pr__commitsMutationObserverTarget()!;
    } else if (props.pageType === GitHubPageType.PR_FILES) {
      return globals.dependencies.all.pr__filesMutationObserverTarget()!;
    } else {
      return globals.dependencies.all.pr__homeMutationObserverTarget()!;
    }
  }, [globals.dependencies.all, props.pageType]);

  useEffect(() => {
    const observer = new MutationObserver((mutations) => {
      const addedNodes = mutations.reduce((l, r) => [...l, ...Array.from(r.addedNodes)], []);
      if (addedNodes.length <= 0) {
        return;
      }

      const newContainers = supportedPrFileElements(globals.logger, globals.envelopeLocator, globals.dependencies);
      if (newContainers.length === prFileContainers.length) {
        globals.logger.log("Found new unsupported containers");
        return;
      }

      globals.logger.log("Found new containers...");
      setPrFileContainers(newContainers);
    });

    observer.observe(mutationObserverTargetNode(), {
      childList: true,
      subtree: true,
    });

    return () => {
      observer.disconnect();
    };
  }, [globals.dependencies, globals.envelopeLocator, globals.logger, mutationObserverTargetNode, prFileContainers]);

  return (
    <>
      {prFileContainers.map((container) => (
        <IsolatedPrEditor
          key={getUnprocessedFilePath(container, globals.dependencies)}
          prInfo={props.prInfo}
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

function supportedPrFileElements(logger: Logger, envelopeLocator: EditorEnvelopeLocator, dependencies: Dependencies) {
  return prFileElements(logger, dependencies).filter((container) =>
    envelopeLocator.hasMappingFor(getFilePath(container, dependencies))
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
  return getOriginalFilePath(unprocessedFilePath).split(".").pop()!;
}

function getFilePath(prFileContainer: HTMLElement, dependencies: Dependencies) {
  const unprocessedFilePath = getUnprocessedFilePath(prFileContainer, dependencies);
  return getOriginalFilePath(unprocessedFilePath);
}

export function getUnprocessedFilePath(prFileContainer: HTMLElement, dependencies: Dependencies) {
  return dependencies.all.pr__unprocessedFilePathContainer(prFileContainer)!.title;
}
