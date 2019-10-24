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
import { GlobalContext } from "../common/GlobalContext";
import { useEffectWithDependencies } from "../common/customEffects";
import { Router } from "@kogito-tooling/core-api";
import * as dependencies__ from "../../dependencies";
import { ResolvedDomDependency } from "../../dependencies";
import { getOriginalFilePath, IsolatedPrEditor, PrInformation } from "./IsolatedPrEditor";
import { Feature } from "../common/Feature";
import {Logger} from "../../../Logger";

export function PrEditorsApp(props: { prInfo: PrInformation }) {
  const globalContext = useContext(GlobalContext);
  const [prFileContainers, setPrFileContainers] = useState(supportedPrFileElements(globalContext.logger, globalContext.router));

  useMutationObserverEffect(
    new MutationObserver(mutations => {
      const addedNodes = mutations.reduce((l, r) => [...l, ...Array.from(r.addedNodes)], []);

      if (addedNodes.length <= 0) {
        return;
      }

      const newContainers = supportedPrFileElements(globalContext.logger, globalContext.router);
      if (newContainers.length !== prFileContainers.length) {
        globalContext.logger.log("Found new containers...");
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
        <Feature
          key={prFileContainers.indexOf(container)}
          name={`PR editor for file #${prFileContainers.indexOf(container)}`}
          dependencies={deps => ({
            githubTextEditorToReplace: () => deps.common.githubTextEditorToReplaceElement(container),
            unprocessedFilePath: () => deps.all.pr__unprocessedFilePathContainer(container)
          })}
          component={resolved => (
            <IsolatedPrEditor
              prInfo={props.prInfo}
              prFileContainer={container}
              fileExtension={getFileExtension(container)}
              unprocessedFilePath={getUnprocessedFilePath(resolved.unprocessedFilePath as ResolvedDomDependency)}
              githubTextEditorToReplace={resolved.githubTextEditorToReplace as ResolvedDomDependency}
            />
          )}
        />
      ))}
    </>
  );
}

function supportedPrFileElements(logger: Logger, router: Router) {
  return prFileElements(logger).filter(container => router.getLanguageData(getFileExtension(container)));
}

function useMutationObserverEffect(observer: MutationObserver, options: MutationObserverInit) {
  useEffectWithDependencies(
    "PR files mutation observer",
    deps => ({ target: () => deps.all.pr__mutationObserverTarget() }),
    deps => {
      observer.observe((deps.target as ResolvedDomDependency).element, options);
      return () => observer.disconnect();
    },
    []
  );
}

function prFileElements(logger: Logger) {
  const elements = dependencies__.all.array.supportedPrFileContainers();
  if (!elements) {
    logger.log("Could not find file containers...");
    return [];
  }

  return elements!.map(e => ({ name: "", element: e }));
}

function getFileExtension(prFileContainer: ResolvedDomDependency) {
  const element = dependencies__.all.pr__unprocessedFilePathContainer(prFileContainer)!;
  if (!element) {
    console.error("Could not find file name here...", prFileContainer);
    return "";
  }

  const unprocessedFilePath = getUnprocessedFilePath({ name: "", element: element });
  return getOriginalFilePath(unprocessedFilePath)
    .split(".")
    .pop()!;
}

export function getUnprocessedFilePath(container: ResolvedDomDependency) {
  return container.element.title;
}
