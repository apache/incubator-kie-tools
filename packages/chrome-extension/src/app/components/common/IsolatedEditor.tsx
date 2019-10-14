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
import { KogitoEditorIframe } from "./KogitoEditorIframe";
import { GitHubDomElements } from "../../github/GitHubDomElements";
import { useLayoutEffectWithDependencies } from "./useEffectWithDependencies";

export function IsolatedEditor(props: {
  getFileContents: () => Promise<string | undefined>;
  openFileExtension: string;
  readonly: boolean;
  textMode: boolean;
  keepRenderedEditorInTextMode: boolean;
}) {
  const shouldRenderIframe = (props.keepRenderedEditorInTextMode && props.textMode) || !props.textMode;

  return (
    <>
      {shouldRenderIframe && (
        <KogitoEditorIframe
          openFileExtension={props.openFileExtension}
          getFileContents={props.getFileContents}
          readonly={props.readonly}
        />
      )}
    </>
  );
}

export function useIsolatedEditorTogglingEffect(textMode: boolean, githubDomElements: GitHubDomElements, container?: HTMLElement) {
  useLayoutEffectWithDependencies(
    "Editor toggling effect",
    dependencies => ({
      iframeContainer: () => dependencies.common.iframeContainer(),
      githubTextEditorToReplaceElement: () => dependencies.common.githubTextEditorToReplaceElement(container)
    }),
    resolvedDependencies => {
      if (textMode) {
        resolvedDependencies.githubTextEditorToReplaceElement()!.classList.remove("hidden");
        githubDomElements.iframeContainer(resolvedDependencies.iframeContainer()!).classList.add("hidden");
      } else {
        resolvedDependencies.githubTextEditorToReplaceElement()!.classList.add("hidden");
        githubDomElements.iframeContainer(resolvedDependencies.iframeContainer()!).classList.remove("hidden");
      }
    },
    [textMode]
  );
}
