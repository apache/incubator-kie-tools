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
import { useEffect, useLayoutEffect, useState } from "react";
import { Router } from "@kogito-tooling/core-api";
import { GlobalContext } from "./GlobalContext";
import { KogitoEditorIframe } from "./KogitoEditorIframe";
import { FullScreenToolbar } from "./FullScreenToolbar";
import { Toolbar } from "./Toolbar";

export function ChromeExtensionApp(props: {
  githubDomElements: GitHubDomElements;
  openFileExtension: string;
  router: Router;
  readonly: boolean;
}) {
  const [globalState, setGlobalState] = useState({ fullscreen: false, textMode: false, textModeEnabled: false });

  useLayoutEffect(
    () => {
      if (!globalState.fullscreen) {
        props.githubDomElements.iframeFullscreenContainer().classList.add("hidden");
      } else {
        props.githubDomElements.iframeFullscreenContainer().classList.remove("hidden");
      }
    },
    [globalState]
  );

  useEffect(
    () => {
      if (globalState.textMode) {
        props.githubDomElements.githubTextEditor().classList.remove("hidden");
        props.githubDomElements.iframeContainer().classList.add("hidden");
      } else {
        props.githubDomElements.githubTextEditor().classList.add("hidden");
        props.githubDomElements.iframeContainer().classList.remove("hidden");
      }
    },
    [globalState]
  );

  return (
    <GlobalContext.Provider value={[globalState, setGlobalState]}>
      {ReactDOM.createPortal(<Toolbar readonly={props.readonly} />, props.githubDomElements.toolbarContainer())}

      {globalState.fullscreen &&
        ReactDOM.createPortal(<FullScreenToolbar />, props.githubDomElements.iframeFullscreenContainer())}

      {ReactDOM.createPortal(
        <KogitoEditorIframe
          openFileExtension={props.openFileExtension}
          router={props.router}
          githubDomElements={props.githubDomElements}
          readonly={props.readonly}
        />,
        globalState.fullscreen
          ? props.githubDomElements.iframeFullscreenContainer()
          : props.githubDomElements.iframeContainer()
      )}
    </GlobalContext.Provider>
  );
}
