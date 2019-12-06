/*
 * Copyright 2019 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import * as React from "react";
import { useCallback, useContext, useEffect, useMemo, useRef, useState } from "react";
import { useHistory } from "react-router-dom";
import { EditorToolbar } from "./EditorToolbar";
import { FullScreenToolbar } from "./EditorFullScreenToolbar";
import { Editor, EditorRef } from "./Editor";
import { GlobalContext } from "../common/GlobalContext";
import { Page, PageSection, Stack, StackItem } from "@patternfly/react-core";
import "@patternfly/patternfly/patternfly.css";
import { useLocation } from "react-router";

interface Props {
  onFileNameChanged: (fileName: string) => void;
}

enum ActionType {
  NONE,
  SAVE,
  DOWNLOAD
}

let action = ActionType.NONE;

export function EditorPage(props: Props) {
  const context = useContext(GlobalContext);
  const location = useLocation();
  const history = useHistory();
  const editorRef = useRef<EditorRef>(null);
  const downloadRef = useRef<HTMLAnchorElement>(null);
  const [fullscreen, setFullscreen] = useState(false);

  const close = useCallback(() => history.replace(context.routes.home.url({}), {}), []);

  const requestSave = useCallback(() => {
    action = ActionType.SAVE;
    editorRef.current!.requestContent();
  }, []);

  const requestDownload = useCallback(() => {
    action = ActionType.DOWNLOAD;
    editorRef.current!.requestContent();
  }, []);

  const enterFullscreen = useCallback(() => {
    document.documentElement.requestFullscreen?.();
  }, []);

  const exitFullscreen = useCallback(() => {
    document.exitFullscreen?.();
  }, []);

  const toggleFullScreen = useCallback(() => {
    setFullscreen(!fullscreen);
  }, [fullscreen]);

  const editorType = useMemo(() => {
    return context.routes.editor.args(location.pathname).type;
  }, [location.pathname]);

  const fileNameWithExtension = useMemo(() => {
    return context.file.fileName + "." + editorType;
  }, [context.file.fileName, editorType]);

  const onContentResponse = useCallback(
    (content: string) => {
      if (action === ActionType.SAVE) {
        window.dispatchEvent(
          new CustomEvent("saveOnlineEditor", {
            detail: {
              fileName: fileNameWithExtension,
              fileContent: content,
              senderTabId: context.senderTabId!
            }
          })
        );
      } else if (action === ActionType.DOWNLOAD && downloadRef.current) {
        const fileBlob = new Blob([content], { type: "text/plain" });
        downloadRef.current.href = URL.createObjectURL(fileBlob);
        downloadRef.current.click();
      }
    },
    [fileNameWithExtension]
  );

  useEffect(() => {
    if (downloadRef.current) {
      downloadRef.current.download = fileNameWithExtension;
    }
  }, [fileNameWithExtension]);

  useEffect(() => {
    document.addEventListener("fullscreenchange", toggleFullScreen);
    document.addEventListener("mozfullscreenchange", toggleFullScreen);
    document.addEventListener("webkitfullscreenchange", toggleFullScreen);
    document.addEventListener("msfullscreenchange", toggleFullScreen);

    return () => {
      document.removeEventListener("fullscreenchange", toggleFullScreen);
      document.removeEventListener("webkitfullscreenchange", toggleFullScreen);
      document.removeEventListener("mozfullscreenchange", toggleFullScreen);
      document.removeEventListener("msfullscreenchange", toggleFullScreen);
    };
  });

  return (
    <Page>
      <PageSection variant="light" noPadding={true}>
        <Stack>
          <StackItem>
            {!fullscreen && (
              <EditorToolbar
                onFullScreen={enterFullscreen}
                onSave={requestSave}
                onDownload={requestDownload}
                onClose={close}
                onFileNameChanged={props.onFileNameChanged}
              />
            )}

            {fullscreen && <FullScreenToolbar onExitFullScreen={exitFullscreen} />}
          </StackItem>

          <StackItem className="pf-m-fill">
            <Editor ref={editorRef} fullscreen={fullscreen} onContentResponse={onContentResponse} />
          </StackItem>
        </Stack>
        <a ref={downloadRef} />
      </PageSection>
    </Page>
  );
}
