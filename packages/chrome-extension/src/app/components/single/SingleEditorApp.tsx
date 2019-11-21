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
import { useCallback, useLayoutEffect, useState } from "react";
import * as ReactDOM from "react-dom";
import { FullScreenToolbar } from "./FullScreenToolbar";
import { SingleEditorToolbar } from "./SingleEditorToolbar";
import { useIsolatedEditorTogglingEffect } from "../common/customEffects";
import { IsolatedEditorContext } from "../common/IsolatedEditorContext";
import { iframeFullscreenContainer } from "../../utils";
import { IsolatedEditor } from "../common/IsolatedEditor";
import * as dependencies__ from "../../dependencies";
import {useGlobals} from "../common/GlobalContext";

function useFullScreenEditorTogglingEffect(fullscreen: boolean) {
    const globals = useGlobals();
  useLayoutEffect(
    () => {
      if (!fullscreen) {
        iframeFullscreenContainer(globals.id, dependencies__.all.body()).classList.add("hidden");
      } else {
        iframeFullscreenContainer(globals.id, dependencies__.all.body()).classList.remove("hidden");
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
  const globals = useGlobals();

  useFullScreenEditorTogglingEffect(fullscreen);
  useIsolatedEditorTogglingEffect(textMode, props.iframeContainer, props.githubTextEditorToReplace);

  const IsolatedEditorComponent = (
    <IsolatedEditor
      getFileContents={props.getFileContents}
      openFileExtension={props.openFileExtension}
      textMode={textMode}
      readonly={props.readonly}
      keepRenderedEditorInTextMode={true}
    />
  );

  const exitFullScreen = useCallback(() => {
    setFullscreen(false);
    setTextModeEnabled(false);
  }, []);

  const deactivateTextMode = useCallback(() => setTextMode(false), []);
  const activateTextMode = useCallback(() => setTextMode(true), []);
  const goFullScreen = useCallback(() => setFullscreen(true), []);

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
            {ReactDOM.createPortal(
              <SingleEditorToolbar
                textMode={textMode}
                textModeEnabled={textModeEnabled}
                onSeeAsDiagram={deactivateTextMode}
                onSeeAsSource={activateTextMode}
                onFullScreen={goFullScreen}
                readonly={props.readonly}
              />,
              props.toolbarContainer
            )}
            {ReactDOM.createPortal(IsolatedEditorComponent, props.iframeContainer)}
          </>
        )}

        {fullscreen && (
          <>
            {ReactDOM.createPortal(
              <FullScreenToolbar onExitFullScreen={exitFullScreen} />,
              iframeFullscreenContainer(globals.id, dependencies__.all.body())
            )}
            {ReactDOM.createPortal(IsolatedEditorComponent, iframeFullscreenContainer(globals.id, dependencies__.all.body()))}
          </>
        )}
      </IsolatedEditorContext.Provider>
    </>
  );
}
