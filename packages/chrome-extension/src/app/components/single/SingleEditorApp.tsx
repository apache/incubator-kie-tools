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
import { useLayoutEffect, useState } from "react";
import * as ReactDOM from "react-dom";
import { FullScreenToolbar } from "./FullScreenToolbar";
import { SingleEditorToolbar } from "./SingleEditorToolbar";
import { useIsolatedEditorTogglingEffect } from "../common/customEffects";
import { IsolatedEditorContext } from "../common/IsolatedEditorContext";
import { iframeFullscreenContainer } from "../../utils";
import { IsolatedEditor } from "../common/IsolatedEditor";
import * as dependencies__ from "../../dependencies";

function useFullScreenEditorTogglingEffect(fullscreen: boolean) {
  useLayoutEffect(
    () => {
      if (!fullscreen) {
        iframeFullscreenContainer(dependencies__.all.body()).classList.add("hidden");
      } else {
        iframeFullscreenContainer(dependencies__.all.body()).classList.remove("hidden");
      }
    },
    [fullscreen]
  );
}

export function SingleEditorApp(props: {
  openFileExtension: string;
  readonly: boolean;
  getFileContents: () => Promise<string | undefined>;
  toolbarContainer: HTMLElement;
  iframeContainer: HTMLElement;
  githubTextEditorToReplace: HTMLElement;
}) {
  const [textMode, setTextMode] = useState(false);
  const [textModeEnabled, setTextModeEnabled] = useState(false);
  const [fullscreen, setFullscreen] = useState(false);

  useFullScreenEditorTogglingEffect(fullscreen);
  useIsolatedEditorTogglingEffect(textMode, props.iframeContainer, props.githubTextEditorToReplace);
  const exitFullScreen = () => {
    setFullscreen(false);
    setTextModeEnabled(false);
  };

  function IsolatedEditorComponent() {
    return (
      <IsolatedEditor
        getFileContents={props.getFileContents}
        openFileExtension={props.openFileExtension}
        textMode={textMode}
        readonly={props.readonly}
        keepRenderedEditorInTextMode={true}
      />
    );
  }

  return (
    <>
      <IsolatedEditorContext.Provider
        value={{
          onEditorReady: () => setTextModeEnabled(true),
          fullscreen: fullscreen,
          textMode: textMode
        }}
      >
        {!fullscreen && (
          <>
            {ReactDOM.createPortal(<IsolatedEditorComponent />, props.iframeContainer)}
            {ReactDOM.createPortal(
              <SingleEditorToolbar
                textMode={textMode}
                textModeEnabled={textModeEnabled}
                onSeeAsDiagram={() => setTextMode(false)}
                onSeeAsSource={() => setTextMode(true)}
                onFullScreen={() => setFullscreen(true)}
                readonly={props.readonly}
              />,
              props.toolbarContainer
            )}
          </>
        )}

        {fullscreen && (
          <>
            {ReactDOM.createPortal(
              <FullScreenToolbar onExitFullScreen={exitFullScreen} />,
              iframeFullscreenContainer(dependencies__.all.body())
            )}
            {ReactDOM.createPortal(<IsolatedEditorComponent />, iframeFullscreenContainer(dependencies__.all.body()))}
          </>
        )}
      </IsolatedEditorContext.Provider>
    </>
  );
}
