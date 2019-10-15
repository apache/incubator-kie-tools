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
import { getOriginalFilePath, getUnprocessedFilePath } from "./GitHubDomElementsPr";
import { GlobalContext } from "../common/GlobalContext";
import { useEffectWithDependencies } from "../common/customEffects";
import { Router } from "@kogito-tooling/core-api";
import * as dependencies__ from "../../dependencies";
import { ResolvedDomDependency } from "../../dependencies";
import { IsolatedPrEditor } from "./IsolatedPrEditor";
import { Feature } from "../common/Feature";

export function PrEditorsApp() {
  const prFileElements = () => dependencies__.array.supportedPrFileContainers()!.map(e => ({ name: "", element: e }));

  const globalContext = useContext(GlobalContext);
  const [prFileContainers, setPrFileContainers] = useState(
    supportedPrFileElements(prFileElements, globalContext.router)
  );

  useMutationObserverEffect(
    new MutationObserver(mutations => {
      const addedNodes = mutations.reduce((l, r) => [...l, ...Array.from(r.addedNodes)], []);

      if (addedNodes.length <= 0) {
        return;
      }

      const newContainers = supportedPrFileElements(prFileElements, globalContext.router);
      if (newContainers.length !== prFileContainers.length) {
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
          name={`PR editor for ${getUnprocessedFilePath(container)}`}
          dependencies={deps => ({
            githubTextEditorToReplace: () => deps.common.githubTextEditorToReplaceElement(container)
          })}
          component={resolved => (
            <IsolatedPrEditor
                key={getUnprocessedFilePath(container)}
                prFileContainer={container}
                fileExtension={getFileExtension(container)}
                githubTextEditorToReplace={resolved.githubTextEditorToReplace}
            />
          )}
        />
      ))}
    </>
  );
}

function supportedPrFileElements(prFileElements: () => ResolvedDomDependency[], router: Router) {
  return prFileElements().filter(container => router.getLanguageData(getFileExtension(container)));
}

function useMutationObserverEffect(observer: MutationObserver, options: MutationObserverInit) {
  useEffectWithDependencies(
    "PR files mutation observer",
    deps => ({ target: () => deps.all.pr__mutationObserverTarget() }),
    deps => {
      observer.observe(deps.target.element, options);
      return () => observer.disconnect();
    },
    []
  );
}

function getFileExtension(container: ResolvedDomDependency) {
  return getOriginalFilePath(container)
    .split(".")
    .pop()!;
}
