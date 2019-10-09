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
import * as React from "react";
import { useLayoutEffect, useState } from "react";
import * as ReactDOM from "react-dom";
import { FullScreenToolbar } from "./FullScreenToolbar";
import { SingleEditorToolbar } from "./SingleEditorToolbar";
import { IsolatedEditor, useIsolatedEditorTogglingEffect } from "../common/IsolatedEditor";
import { Router } from "@kogito-tooling/core-api";
import { IsolatedEditorContext } from "../common/IsolatedEditorContext";
import { iframeFullscreenContainer } from "../../utils";

function useFullScreenEditorTogglingEffect(fullscreen: boolean) {
  useLayoutEffect(
    () => {
      if (!fullscreen) {
        iframeFullscreenContainer().classList.add("hidden");
      } else {
        iframeFullscreenContainer().classList.remove("hidden");
      }
    },
    [fullscreen]
  );
}

export function SingleEditorApp(props: {
  githubDomElements: GitHubDomElements;
  openFileExtension: string;
  router: Router;
  readonly: boolean;
}) {
  const [textMode, setTextMode] = useState(false);
  const [textModeEnabled, setTextModeEnabled] = useState(false);
  const [fullscreen, setFullscreen] = useState(false);

  useFullScreenEditorTogglingEffect(fullscreen);
  useIsolatedEditorTogglingEffect(textMode, props.githubDomElements);

  const isolatedEditorContainer = fullscreen ? iframeFullscreenContainer() : props.githubDomElements.iframeContainer();

  return (
    <IsolatedEditorContext.Provider
      value={{
        onEditorReady: () => setTextModeEnabled(true),
        fullscreen: fullscreen,
        textMode: textMode
      }}
    >
      {ReactDOM.createPortal(
        <SingleEditorToolbar
          textMode={textMode}
          textModeEnabled={textModeEnabled}
          onSeeAsDiagram={() => setTextMode(false)}
          onSeeAsSource={() => setTextMode(true)}
          onFullScreen={() => setFullscreen(true)}
          readonly={props.readonly}
        />,
        props.githubDomElements.toolbarContainer()
      )}

      {fullscreen &&
        ReactDOM.createPortal(
          <FullScreenToolbar onExitFullScreen={() => setFullscreen(false)} />,
          iframeFullscreenContainer()
        )}

      {ReactDOM.createPortal(
        <IsolatedEditor
          getFileContents={props.githubDomElements.getFileContents}
          openFileExtension={props.openFileExtension}
          router={props.router}
          textMode={textMode}
          readonly={props.readonly}
          keepRenderedEditorInTextMode={true}
        />,
        isolatedEditorContainer
      )}
    </IsolatedEditorContext.Provider>
  );
}
