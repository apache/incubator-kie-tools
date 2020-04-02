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
import { useCallback, useEffect, useLayoutEffect, useMemo, useRef, useState } from "react";
import * as ReactDOM from "react-dom";
import { FullScreenToolbar } from "./FullScreenToolbar";
import { SingleEditorToolbar } from "./SingleEditorToolbar";
import { useIsolatedEditorTogglingEffect } from "../common/customEffects";
import { IsolatedEditorContext } from "../common/IsolatedEditorContext";
import { iframeFullscreenContainer } from "../../utils";
import { IsolatedEditor } from "../common/IsolatedEditor";
import { useGlobals } from "../common/GlobalContext";
import { IsolatedEditorRef } from "../common/IsolatedEditorRef";
import { FileInfo } from "./singleEditorView";

function useFullScreenEditorTogglingEffect(fullscreen: boolean) {
  const globals = useGlobals();
  useLayoutEffect(() => {
    if (!fullscreen) {
      iframeFullscreenContainer(globals.id, globals.dependencies.all.body()).classList.add("hidden");
    } else {
      iframeFullscreenContainer(globals.id, globals.dependencies.all.body()).classList.remove("hidden");
    }
  }, [fullscreen]);
}

export function SingleEditorApp(props: {
  openFileExtension: string;
  readonly: boolean;
  getFileName: () => string;
  getFileContents: () => Promise<string | undefined>;
  toolbarContainer: HTMLElement;
  iframeContainer: HTMLElement;
  githubTextEditorToReplace: HTMLElement;
  fileInfo: FileInfo;
}) {
  const [textMode, setTextMode] = useState(false);
  const [textModeEnabled, setTextModeEnabled] = useState(false);
  const [fullscreen, setFullscreen] = useState(false);
  const globals = useGlobals();
  const isolatedEditorRef = useRef<IsolatedEditorRef>(null);

  useFullScreenEditorTogglingEffect(fullscreen);
  useIsolatedEditorTogglingEffect(textMode, props.iframeContainer, props.githubTextEditorToReplace);

  const IsolatedEditorComponent = useMemo(
    () => (
      <IsolatedEditor
        getFileContents={props.getFileContents}
        contentPath={props.fileInfo.path}
        openFileExtension={props.openFileExtension}
        textMode={textMode}
        readonly={props.readonly}
        keepRenderedEditorInTextMode={true}
        ref={isolatedEditorRef}
      />
    ),
    [textMode]
  );

  const exitFullScreen = useCallback(() => {
    setFullscreen(false);
    setTextModeEnabled(false);
  }, []);

  const deactivateTextMode = useCallback(() => setTextMode(false), []);
  const activateTextMode = useCallback(() => setTextMode(true), []);
  const goFullScreen = useCallback(() => setFullscreen(true), []);

  const openExternalEditor = useCallback(() => {
    props.getFileContents().then(fileContent => {
      globals.externalEditorManager?.open(props.getFileName(), fileContent!, props.readonly);
    });
  }, [globals.externalEditorManager]);

  const linkToExternalEditor = useMemo(() => {
    return globals.externalEditorManager?.getLink(
      `${props.fileInfo.org}/${props.fileInfo.repo}/${props.fileInfo.gitRef}/${props.fileInfo.path}`
    );
  }, [globals.externalEditorManager]);

  useEffect(() => {
    const listener = globals.externalEditorManager?.listenToComeBack(fileName => {
      globals.dependencies.all.edit__githubFileNameInput()!.value = fileName;
    }, isolatedEditorRef.current?.setContent!);

    return () => {
      listener?.stopListening();
    };
  }, [globals.externalEditorManager]);

  const onEditorReady = useCallback(() => {
    setTextModeEnabled(true);
  }, []);

  const repoInfo = useMemo(() => {
    return {
      gitref: props.fileInfo.gitRef,
      owner: props.fileInfo.org,
      repo: props.fileInfo.repo
    };
  }, []);

  return (
    <>
      <IsolatedEditorContext.Provider
        value={{
          onEditorReady: onEditorReady,
          fullscreen: fullscreen,
          textMode: textMode,
          repoInfo: repoInfo
        }}
      >
        {!fullscreen && (
          <>
            {ReactDOM.createPortal(IsolatedEditorComponent, props.iframeContainer)}
            {ReactDOM.createPortal(
              <SingleEditorToolbar
                textMode={textMode}
                textModeEnabled={textModeEnabled}
                onSeeAsDiagram={deactivateTextMode}
                onSeeAsSource={activateTextMode}
                onOpenInExternalEditor={openExternalEditor}
                linkToExternalEditor={linkToExternalEditor}
                onFullScreen={goFullScreen}
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
              iframeFullscreenContainer(globals.id, globals.dependencies.all.body())
            )}
            {ReactDOM.createPortal(
              IsolatedEditorComponent,
              iframeFullscreenContainer(globals.id, globals.dependencies.all.body())
            )}
          </>
        )}
      </IsolatedEditorContext.Provider>
    </>
  );
}
