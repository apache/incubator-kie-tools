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

import { GitHubDomElements } from "../../github/GitHubDomElements";
import * as ReactDOM from "react-dom";
import * as React from "react";
import { useLayoutEffect, useState } from "react";
import { Router } from "@kogito-tooling/core-api";
import { IsolatedEditorContext } from "./IsolatedEditorContext";
import { KogitoEditorIframe } from "./KogitoEditorIframe";
import { FullScreenToolbar } from "./FullScreenToolbar";

export function SingleEditorApp(props: {
  githubDomElements: GitHubDomElements;
  openFileExtension: string;
  router: Router;
  toolbar: () => React.FunctionComponentElement<any>;
  readonly: boolean;
  textModeAsDefault: boolean;
  keepRenderedEditorInTextMode: boolean;
}) {
  const [state, setState] = useState({
    fullscreen: false,
    textMode: props.textModeAsDefault,
    textModeEnabled: false
  });

  useLayoutEffect(
    () => {
      if (!state.fullscreen) {
        props.githubDomElements.iframeFullscreenContainer().classList.add("hidden");
      } else {
        props.githubDomElements.iframeFullscreenContainer().classList.remove("hidden");
      }
    },
    [state]
  );

  useLayoutEffect(
    () => {
      if (state.textMode) {
        props.githubDomElements.githubTextEditorToReplace().classList.remove("hidden");
        props.githubDomElements.iframeContainer().classList.add("hidden");
      } else {
        props.githubDomElements.githubTextEditorToReplace().classList.add("hidden");
        props.githubDomElements.iframeContainer().classList.remove("hidden");
      }
    },
    [state]
  );

  const shouldRenderIframe = (props.keepRenderedEditorInTextMode && state.textMode) || !state.textMode;

  return (
    <IsolatedEditorContext.Provider value={[state, setState]}>
      {ReactDOM.createPortal(props.toolbar(), props.githubDomElements.toolbarContainer())}

      {state.fullscreen &&
        ReactDOM.createPortal(<FullScreenToolbar />, props.githubDomElements.iframeFullscreenContainer())}

      {shouldRenderIframe &&
        ReactDOM.createPortal(
          <KogitoEditorIframe
            openFileExtension={props.openFileExtension}
            router={props.router}
            getFileContents={props.githubDomElements.getFileContents}
            readonly={props.readonly}
          />,
          state.fullscreen
            ? props.githubDomElements.iframeFullscreenContainer()
            : props.githubDomElements.iframeContainer()
        )}
    </IsolatedEditorContext.Provider>
  );
}
