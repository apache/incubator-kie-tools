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
import { useContext, useState } from "react";
import * as ReactDOM from "react-dom";
import { FullScreenToolbar } from "./FullScreenToolbar";
import { SingleEditorToolbar } from "./SingleEditorToolbar";
import { IsolatedEditor, useIsolatedEditorTogglingEffect } from "../common/IsolatedEditor";
import { IsolatedEditorContext } from "../common/IsolatedEditorContext";
import { iframeFullscreenContainer } from "../../utils";
import { GlobalContext } from "../common/GlobalContext";
import { useLayoutEffectWithDependencies } from "../common/useEffectWithDependencies";
import { Feature } from "../common/Feature";

function useFullScreenEditorTogglingEffect(fullscreen: boolean) {
  useLayoutEffectWithDependencies(
    "Toggle fullscreen",
    dependencies => ({ body: () => dependencies.common.body() }),
    resolvedDependencies => {
      if (!fullscreen) {
        iframeFullscreenContainer(resolvedDependencies.body()!).classList.add("hidden");
      } else {
        iframeFullscreenContainer(resolvedDependencies.body()!).classList.remove("hidden");
      }
    },
    [fullscreen]
  );
}

export function SingleEditorApp(props: {
  githubDomElements: GitHubDomElements;
  openFileExtension: string;
  readonly: boolean;
}) {
  const globalContext = useContext(GlobalContext);
  const [textMode, setTextMode] = useState(false);
  const [textModeEnabled, setTextModeEnabled] = useState(false);
  const [fullscreen, setFullscreen] = useState(false);

  useFullScreenEditorTogglingEffect(fullscreen);
  useIsolatedEditorTogglingEffect(textMode, props.githubDomElements);

  const IsolatedEditorComponent = (
    <IsolatedEditor
      getFileContents={props.githubDomElements.getFileContents}
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
        <Feature
          name={"Toolbar container"}
          dependencies={deps => ({ container: () => deps.common.toolbarContainerTarget() })}
          component={deps => (
            <>
              {ReactDOM.createPortal(
                <SingleEditorToolbar
                  textMode={textMode}
                  textModeEnabled={textModeEnabled}
                  onSeeAsDiagram={() => setTextMode(false)}
                  onSeeAsSource={() => setTextMode(true)}
                  onFullScreen={() => setFullscreen(true)}
                  readonly={props.readonly}
                />,
                props.githubDomElements.toolbarContainer(deps.container()!)
              )}
            </>
          )}
        />

        {fullscreen && (
          <Feature
            name={"Fullscreen toolbar"}
            dependencies={deps => ({ container: deps.common.body })}
            component={deps =>
              ReactDOM.createPortal(
                <FullScreenToolbar onExitFullScreen={() => setFullscreen(false)} />,
                iframeFullscreenContainer(deps.container()!)
              )
            }
          />
        )}

        {fullscreen && (
          <Feature
            name={"Fullscreen editor"}
            dependencies={deps => ({ container: deps.common.body })}
            component={deps =>
              ReactDOM.createPortal(IsolatedEditorComponent, iframeFullscreenContainer(deps.container()!))
            }
          />
        )}

        {!fullscreen && (
          <Feature
            name={"Editor"}
            dependencies={deps => ({ container: deps.common.iframeContainerTarget })}
            component={deps =>
              ReactDOM.createPortal(
                IsolatedEditorComponent,
                props.githubDomElements.iframeContainer(deps.container()!)
              )
            }
          />
        )}
      </IsolatedEditorContext.Provider>
    </>
  );
}
