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
import { Alert, AlertActionCloseButton, Page, PageSection, Title } from "@patternfly/react-core";
import "@patternfly/patternfly/patternfly.css";
import { useLocation } from "react-router";
import { EditorContent } from "@kogito-tooling/core-api";

interface Props {
  onFileNameChanged: (fileName: string) => void;
}

enum ActionType {
  NONE,
  SAVE,
  DOWNLOAD,
  COPY,
  PREVIEW
}

const ALERT_AUTO_CLOSE_TIMEOUT = 3000;

// FIXME: This action should be moved inside the React hooks lifecycle.
let action = ActionType.NONE;

export function EditorPage(props: Props) {
  const context = useContext(GlobalContext);
  const location = useLocation();
  const history = useHistory();
  const editorRef = useRef<EditorRef>(null);
  const downloadRef = useRef<HTMLAnchorElement>(null);
  const downloadPreviewRef = useRef<HTMLAnchorElement>(null);
  const copyContentTextArea = useRef<HTMLTextAreaElement>(null);
  const [fullscreen, setFullscreen] = useState(false);
  const [copySuccessAlertVisible, setCopySuccessAlertVisible] = useState(false);

  const close = useCallback(() => {
    window.location.href = window.location.href.split("?")[0].split("#")[0];
  }, []);

  const requestSave = useCallback(() => {
    action = ActionType.SAVE;
    editorRef.current?.requestContent();
  }, []);

  const requestDownload = useCallback(() => {
    action = ActionType.DOWNLOAD;
    editorRef.current?.requestContent();
  }, []);

  const requestPreview = useCallback(() => {
    action = ActionType.PREVIEW;
    editorRef.current?.requestPreview();
  }, []);

  const requestCopyContentToClipboard = useCallback(() => {
    action = ActionType.COPY;
    editorRef.current?.requestContent();
  }, []);

  const enterFullscreen = useCallback(() => {
    document.documentElement.requestFullscreen?.();
    (document.documentElement as any).webkitRequestFullscreen?.();
  }, []);

  const exitFullscreen = useCallback(() => {
    document.exitFullscreen?.();
    (document as any).webkitExitFullscreen?.();
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

  const closeCopySuccessAlert = useCallback(() => setCopySuccessAlertVisible(false), []);

  const onContentResponse = useCallback(
    (content: EditorContent) => {
      if (action === ActionType.SAVE) {
        window.dispatchEvent(
          new CustomEvent("saveOnlineEditor", {
            detail: {
              fileName: fileNameWithExtension,
              fileContent: content.content,
              senderTabId: context.senderTabId!
            }
          })
        );
      } else if (action === ActionType.DOWNLOAD && downloadRef.current) {
        const fileBlob = new Blob([content.content], { type: "text/plain" });
        downloadRef.current.href = URL.createObjectURL(fileBlob);
        downloadRef.current.click();
      } else if (action === ActionType.COPY && copyContentTextArea.current) {
        copyContentTextArea.current.value = content.content;
        copyContentTextArea.current.select();
        if (document.execCommand("copy")) {
          setCopySuccessAlertVisible(true);
        }
      }
    },
    [fileNameWithExtension]
  );

  const onPreviewResponse = useCallback(
    preview => {
      if (action === ActionType.PREVIEW && downloadPreviewRef.current) {
        const fileBlob = new Blob([preview], { type: "image/svg+xml" });
        downloadPreviewRef.current.href = URL.createObjectURL(fileBlob);
        downloadPreviewRef.current.click();
      }
    },
    [fileNameWithExtension]
  );

  useEffect(() => {
    if (closeCopySuccessAlert) {
      const autoCloseCopySuccessAlert = setTimeout(closeCopySuccessAlert, ALERT_AUTO_CLOSE_TIMEOUT);
      return () => clearInterval(autoCloseCopySuccessAlert);
    }

    return () => {
      /* Do nothing */
    };
  }, [copySuccessAlertVisible]);

  useEffect(() => {
    if (downloadRef.current) {
      downloadRef.current.download = fileNameWithExtension;
    }
    if (downloadPreviewRef.current) {
      downloadPreviewRef.current.download = `${fileNameWithExtension}.svg`;
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
    <Page
      header={
        <EditorToolbar
          onFullScreen={enterFullscreen}
          onSave={requestSave}
          onDownload={requestDownload}
          onClose={close}
          onFileNameChanged={props.onFileNameChanged}
          onCopyContentToClipboard={requestCopyContentToClipboard}
          isPageFullscreen={fullscreen}
          onPreview={requestPreview}
        />
      }
    >
      <PageSection isFilled={true} noPadding={true} noPaddingMobile={true} style={{ flexBasis: "100%" }}>
        {!fullscreen && copySuccessAlertVisible && (
            <div className={"kogito--alert-container"}>
              <Alert
                  variant="success"
                  title="Content copied to clipboard"
                  action={<AlertActionCloseButton onClose={closeCopySuccessAlert} />}
              />
            </div>
        )}
        {fullscreen && <FullScreenToolbar onExitFullScreen={exitFullscreen} />}
        <Editor
          ref={editorRef}
          fullscreen={fullscreen}
          onContentResponse={onContentResponse}
          onPreviewResponse={onPreviewResponse}
        />
      </PageSection>
      <textarea ref={copyContentTextArea} style={{ height: 0, position: "absolute", zIndex: -1 }} />
      <a ref={downloadRef} />
      <a ref={downloadPreviewRef} />
    </Page>
  );
}
