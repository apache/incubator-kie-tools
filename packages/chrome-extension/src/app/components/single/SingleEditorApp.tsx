/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

import * as React from "react";
import { useCallback, useEffect, useLayoutEffect, useMemo, useState } from "react";
import * as ReactDOM from "react-dom";
import { FullScreenToolbar } from "./FullScreenToolbar";
import { SingleEditorToolbar } from "./SingleEditorToolbar";
import { useIsolatedEditorTogglingEffect } from "../common/customEffects";
import { IsolatedEditorContext } from "../common/IsolatedEditorContext";
import { iframeFullscreenContainer } from "../../utils";
import { IsolatedEditor } from "../common/IsolatedEditor";
import { useGlobals } from "../common/GlobalContext";
import { useIsolatedEditorRef } from "../common/IsolatedEditorRef";
import { FileInfo } from "./singleEditorView";

function useFullScreenEditorTogglingEffect(fullscreen: boolean) {
  const globals = useGlobals();
  useLayoutEffect(() => {
    if (!fullscreen) {
      iframeFullscreenContainer(globals.id, globals.dependencies.all.body()).classList.add("hidden");
    } else {
      iframeFullscreenContainer(globals.id, globals.dependencies.all.body()).classList.remove("hidden");
    }
  }, [fullscreen, globals.dependencies.all, globals.id]);
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
  const [errorOpeningFile, setErrorOpeningFile] = useState(false);
  const [textModeAvailable, setTextModeAvailable] = useState(false);
  const [fullscreen, setFullscreen] = useState(false);
  const globals = useGlobals();
  const { isolatedEditor, isolatedEditorRef } = useIsolatedEditorRef();

  useFullScreenEditorTogglingEffect(fullscreen);
  useIsolatedEditorTogglingEffect(textMode, props.iframeContainer, props.githubTextEditorToReplace);

  const onSetContentError = useCallback(() => {
    setErrorOpeningFile(true);
  }, []);

  const IsolatedEditorComponent = useMemo(
    () => (
      <IsolatedEditor
        ref={isolatedEditorRef}
        getFileContents={props.getFileContents}
        contentPath={props.fileInfo.path}
        openFileExtension={props.openFileExtension}
        textMode={textMode}
        readonly={props.readonly}
        keepRenderedEditorInTextMode={true}
        onSetContentError={onSetContentError}
      />
    ),
    [
      isolatedEditorRef,
      props.getFileContents,
      props.fileInfo.path,
      props.openFileExtension,
      props.readonly,
      textMode,
      onSetContentError,
    ]
  );

  const exitFullScreen = useCallback(() => {
    setFullscreen(false);
    setTextModeAvailable(false);
    globals.dependencies.all.showDocumentBody();
  }, [globals.dependencies.all]);

  const deactivateTextMode = useCallback(() => {
    setTextMode(false);
    setErrorOpeningFile((prev) => (props.readonly ? prev : false));
  }, [props.readonly]);

  const activateTextMode = useCallback(() => {
    setTextMode(true);
  }, []);

  const goFullScreen = useCallback(() => {
    setFullscreen(true);
    globals.dependencies.all.hideDocumentBody();
  }, [globals.dependencies.all]);

  const { getFileContents, getFileName } = props;

  const openExternalEditor = useMemo(
    () =>
      globals.externalEditorManager?.open &&
      (() => {
        getFileContents().then((fileContent) => {
          globals.externalEditorManager?.open?.(getFileName(), fileContent!, props.readonly);
        });
      }),
    [globals.externalEditorManager, getFileContents, getFileName, props.readonly]
  );

  const linkToExternalEditor = useMemo(() => {
    return globals.externalEditorManager?.getLink?.(
      `${props.fileInfo.org}/${props.fileInfo.repo}/${props.fileInfo.gitRef}/${props.fileInfo.path}`
    );
  }, [globals.externalEditorManager, props.fileInfo]);

  const onEditorReady = useCallback(() => {
    setTextModeAvailable(true);
  }, []);

  const repoInfo = useMemo(() => {
    return {
      gitref: props.fileInfo.gitRef,
      owner: props.fileInfo.org,
      repo: props.fileInfo.repo,
    };
  }, [props.fileInfo.gitRef, props.fileInfo.org, props.fileInfo.repo]);

  return (
    <>
      <IsolatedEditorContext.Provider
        value={{
          onEditorReady: onEditorReady,
          fullscreen: fullscreen,
          textMode: textMode,
          repoInfo: repoInfo,
        }}
      >
        {!fullscreen && (
          <>
            {ReactDOM.createPortal(IsolatedEditorComponent, props.iframeContainer)}
            {ReactDOM.createPortal(
              <SingleEditorToolbar
                textMode={textMode}
                textModeAvailable={textModeAvailable}
                onSeeAsDiagram={deactivateTextMode}
                onSeeAsSource={activateTextMode}
                onOpenInExternalEditor={openExternalEditor}
                linkToExternalEditor={linkToExternalEditor}
                onFullScreen={goFullScreen}
                readonly={props.readonly}
                errorOpeningFile={errorOpeningFile}
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
