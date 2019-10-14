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

import { DomDependencyMap, GlobalDomDependencies } from "../../dependencies";
import { DependencyList, EffectCallback, useContext, useEffect, useLayoutEffect, useRef } from "react";
import { dependenciesAllSatisfied } from "./Feature";
import { GlobalContext } from "./GlobalContext";
import { GitHubDomElements } from "../../github/GitHubDomElements";

export function useEffectWithDependencies<T extends DomDependencyMap>(
  name: string,
  dependenciesProducer: (d: GlobalDomDependencies) => T,
  effect: (resolvedDependencies: T) => ReturnType<EffectCallback>,
  effectDependencies: DependencyList
) {
  use(useEffect, dependenciesProducer, effect, name, effectDependencies);
}

export function useLayoutEffectWithDependencies<T extends DomDependencyMap>(
  name: string,
  dependenciesProducer: (d: GlobalDomDependencies) => T,
  effect: (resolvedDependencies: T) => ReturnType<EffectCallback>,
  effectDependencies: DependencyList
) {
  use(useLayoutEffect, dependenciesProducer, effect, name, effectDependencies);
}

export function useEffectAfterFirstRender(func: () => ReturnType<EffectCallback>, deps: DependencyList) {
  const firstRender = useRef(true);

  useEffect(() => {
    if (!firstRender.current) {
      func();
    } else {
      firstRender.current = false;
    }
  }, deps);
}

function use<T extends DomDependencyMap>(
  useEffectFunction: typeof useEffect,
  dependenciesProducer: (d: GlobalDomDependencies) => T,
  effect: (resolvedDependencies: T) => ReturnType<React.EffectCallback>,
  name: string,
  effectDependencies: React.DependencyList
) {
  const globalContext = useContext(GlobalContext);
  const dependencies = dependenciesProducer(globalContext.dependencies);

  useEffectFunction(() => {
    if (dependenciesAllSatisfied(dependencies)) {
      return effect(dependencies);
    } else {
      console.debug(`Could not use effect '${name}' because because its dependencies were not satisfied.`);
      return () => {
        /**/
      };
    }
  }, effectDependencies);
}

export function useIsolatedEditorTogglingEffect(
  textMode: boolean,
  githubDomElements: GitHubDomElements,
  container?: HTMLElement
) {
  useLayoutEffectWithDependencies(
    "Editor toggling effect",
    deps => ({
      iframeContainer: () => deps.common.iframeContainerTarget(container),
      githubTextEditorToReplaceElement: () => deps.common.githubTextEditorToReplaceElement(container)
    }),
    deps => {
      if (textMode) {
        deps.githubTextEditorToReplaceElement()!.classList.remove("hidden");
        githubDomElements.iframeContainer(deps.iframeContainer()!).classList.add("hidden");
      } else {
        deps.githubTextEditorToReplaceElement()!.classList.add("hidden");
        githubDomElements.iframeContainer(deps.iframeContainer()!).classList.remove("hidden");
      }
    },
    [textMode]
  );
}
