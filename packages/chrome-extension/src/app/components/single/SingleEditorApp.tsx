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
import { useState } from "react";
import * as ReactDOM from "react-dom";
import { FullScreenToolbar } from "./FullScreenToolbar";
import { SingleEditorToolbar } from "./SingleEditorToolbar";
import { useIsolatedEditorTogglingEffect, useLayoutEffectWithDependencies } from "../common/customEffects";
import { IsolatedEditorContext } from "../common/IsolatedEditorContext";
import { iframeFullscreenContainer } from "../../utils";
import { Feature } from "../common/Feature";
import { IsolatedEditor } from "../common/IsolatedEditor";
import { ResolvedDomDependency } from "../../dependencies";

function useFullScreenEditorTogglingEffect(fullscreen: boolean) {
  useLayoutEffectWithDependencies(
    "Toggle fullscreen",
    deps => ({ body: () => deps.all.body() }),
    deps => {
      if (!fullscreen) {
        iframeFullscreenContainer(deps.body as ResolvedDomDependency).classList.add("hidden");
      } else {
        iframeFullscreenContainer(deps.body as ResolvedDomDependency).classList.remove("hidden");
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
  githubTextEditorToReplace: ResolvedDomDependency;
}) {
  const [textMode, setTextMode] = useState(false);
  const [textModeEnabled, setTextModeEnabled] = useState(false);
  const [fullscreen, setFullscreen] = useState(false);

  useFullScreenEditorTogglingEffect(fullscreen);
  useIsolatedEditorTogglingEffect(textMode, props.iframeContainer, props.githubTextEditorToReplace.element);
  const exitFullScreen = () => {
    setFullscreen(false);
    setTextModeEnabled(false);
  };

  const IsolatedEditorComponent = (
    <IsolatedEditor
      getFileContents={props.getFileContents}
      openFileExtension={props.openFileExtension}
      textMode={textMode}
      readonly={props.readonly}
      keepRenderedEditorInTextMode={true}
    />
  );

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
            {ReactDOM.createPortal(IsolatedEditorComponent, props.iframeContainer)}
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
          <Feature
            name={"Fullscreen editor"}
            dependencies={deps => ({ container: deps.all.body })}
            component={resolved => (
              <>
                {ReactDOM.createPortal(
                  <FullScreenToolbar onExitFullScreen={exitFullScreen} />,
                  iframeFullscreenContainer(resolved.container as ResolvedDomDependency)
                )}
                {ReactDOM.createPortal(
                  IsolatedEditorComponent,
                  iframeFullscreenContainer(resolved.container as ResolvedDomDependency)
                )}
              </>
            )}
          />
        )}
      </IsolatedEditorContext.Provider>
    </>
  );
}
