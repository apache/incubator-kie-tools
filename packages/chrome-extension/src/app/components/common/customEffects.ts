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

import { DependencyList, EffectCallback, useEffect, useLayoutEffect, useRef } from "react";

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

export function useIsolatedEditorTogglingEffect(
  textMode: boolean,
  iframeContainer: HTMLElement,
  githubTextEditorToReplace: HTMLElement
) {
  useLayoutEffect(() => {
    if (textMode) {
      githubTextEditorToReplace.classList.remove("hidden");
      iframeContainer.classList.add("hidden");
    } else {
      githubTextEditorToReplace.classList.add("hidden");
      iframeContainer.classList.remove("hidden");
    }
  }, [textMode]);
}

export function useInitialAsyncCallEffect<T>(promise: () => Promise<T>, callback: (a: T) => void) {
  useEffect(() => {
    let canceled = false;
    promise().then(arg => {
      if (canceled) {
        return;
      }

      callback(arg);
    });

    return () => {
      canceled = true;
    };
  }, []);
}
