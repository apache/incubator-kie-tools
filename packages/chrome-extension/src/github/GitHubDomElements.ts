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

export interface GitHubDomElements {
  toolbarContainer(): Element;

  getFileContents(): Promise<string>;

  githubTextEditorToReplace(): HTMLElement;

  iframeContainer(): HTMLElement;

  iframeFullscreenContainer(): HTMLElement;
}

export function everyFunctionReturnsNonNull(obj: any) {
  try {
    return Object.getOwnPropertyNames(Object.getPrototypeOf(obj))
      .filter(k => k !== "constructor")
      .reduce(
        (p, k) => p && ((typeof (obj as any)[k] === "function" && !!(obj as any)[k]()) || !!(obj as any)[k]),
        true
      );
  } catch (e) {
    console.info("[Kogito] Exception while checking if every function returns non null.", e);
    return false;
  }
}
