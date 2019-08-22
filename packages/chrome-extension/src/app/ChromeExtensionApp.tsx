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

import { ChromeAppContainers } from "./utils";
import * as ReactDOM from "react-dom";
import * as React from "react";
import { useEffect, useLayoutEffect, useState } from "react";
import { Router } from "appformer-js-core/src";
import { GlobalContext } from "./GlobalContext";
import { KogitoEditorIframe } from "./KogitoEditorIframe";
import { FullScreenToolbar } from "./FullScreenToolbar";
import { Toolbar } from "./Toolbar";

export function ChromeExtensionApp(props: {
  containers: ChromeAppContainers;
  openFileExtension: string;
  githubEditor: HTMLElement;
  router: Router;
}) {
  const [globalState, setGlobalState] = useState({ fullscreen: false, textMode: false, textModeEnabled: false });

  useLayoutEffect(
    () => {
      if (!globalState.fullscreen) {
        props.containers.iframeFullscreen.classList.add("hidden");
      } else {
        props.containers.iframeFullscreen.classList.remove("hidden");
      }
    },
    [globalState]
  );

  useEffect(
    () => {
      if (globalState.textMode) {
        props.githubEditor.classList.remove("hidden");
        props.containers.iframe.classList.add("hidden");
      } else {
        props.githubEditor.classList.add("hidden");
        props.containers.iframe.classList.remove("hidden");
      }
    },
    [globalState]
  );

  return (
    <GlobalContext.Provider value={[globalState, setGlobalState]}>
      {ReactDOM.createPortal(<Toolbar />, props.containers.toolbar)}
      {globalState.fullscreen && ReactDOM.createPortal(<FullScreenToolbar />, props.containers.iframeFullscreen)}
      {ReactDOM.createPortal(
        <KogitoEditorIframe
          openFileExtension={props.openFileExtension}
          router={props.router}
          githubEditor={props.githubEditor}
        />,
        globalState.fullscreen ? props.containers.iframeFullscreen : props.containers.iframe
      )}
    </GlobalContext.Provider>
  );
}
