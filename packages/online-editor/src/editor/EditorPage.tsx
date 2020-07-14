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

import { ChannelType } from "@kogito-tooling/microeditor-envelope-protocol";
import { EmbeddedEditor, EmbeddedEditorRef, useDirtyState } from "@kogito-tooling/embedded-editor";
import { Alert, AlertActionCloseButton, Page, PageSection } from "@patternfly/react-core";
import * as React from "react";
import { useCallback, useContext, useEffect, useMemo, useRef, useState } from "react";
import { useLocation } from "react-router";
import { GithubTokenModal } from "../common/GithubTokenModal";
import { GlobalContext } from "../common/GlobalContext";
import { extractFileExtension, removeFileExtension } from "../common/utils";
import { FullScreenToolbar } from "./EditorFullScreenToolbar";
import { EditorToolbar } from "./EditorToolbar";
import { useDmnTour } from "../tour";

interface Props {
  onFileNameChanged: (fileName: string) => void;
}

const ALERT_AUTO_CLOSE_TIMEOUT = 3000;

export function EditorPage(props: Props) {
  const context = useContext(GlobalContext);
  const location = useLocation();
  const editorRef = useRef<EmbeddedEditorRef>(null);
  const downloadRef = useRef<HTMLAnchorElement>(null);
  const downloadPreviewRef = useRef<HTMLAnchorElement>(null);
  const copyContentTextArea = useRef<HTMLTextAreaElement>(null);
  const [isEditorReady, setIsEditorReady] = useState(false);
  const [fullscreen, setFullscreen] = useState(false);
  const [copySuccessAlertVisible, setCopySuccessAlertVisible] = useState(false);
  const [githubTokenModalVisible, setGithubTokenModalVisible] = useState(false);
  const [showUnsavedAlert, setShowUnsavedAlert] = useState(false);
  const isDirty = useDirtyState(editorRef);

  const close = useCallback(() => {
    if (!isDirty) {
      window.location.href = window.location.href.split("?")[0].split("#")[0];
    } else {
      setShowUnsavedAlert(true);
    }
  }, [isDirty]);

  const closeWithoutSaving = useCallback(() => {
    setShowUnsavedAlert(false);
    window.location.href = window.location.href.split("?")[0].split("#")[0];
  }, []);

  const requestSave = useCallback(() => {
    editorRef.current?.requestContent().then(content => {
      window.dispatchEvent(
        new CustomEvent("saveOnlineEditor", {
          detail: {
            fileName: fileNameWithExtension,
            fileContent: content.content,
            senderTabId: context.senderTabId!
          }
        })
      );
    });
  }, []);

  const requestDownload = useCallback(() => {
    editorRef.current?.getStateControl().setSavedCommand();
    setShowUnsavedAlert(false);
    editorRef.current?.requestContent().then(content => {
      if (downloadRef.current) {
        const fileBlob = new Blob([content.content], { type: "text/plain" });
        downloadRef.current.href = URL.createObjectURL(fileBlob);
        downloadRef.current.click();
      }
    });
  }, []);

  const requestPreview = useCallback(() => {
    editorRef.current?.requestPreview().then(previewSvg => {
      if (downloadPreviewRef.current) {
        const fileBlob = new Blob([previewSvg], { type: "image/svg+xml" });
        downloadPreviewRef.current.href = URL.createObjectURL(fileBlob);
        downloadPreviewRef.current.click();
      }
    });
  }, []);

  const requestExportGist = useCallback(() => {
    editorRef.current?.requestContent().then(content => {
      if (!context.githubService.isAuthenticated()) {
        setGithubTokenModalVisible(true);
        return;
      }

      context.githubService
        .createGist({
          filename: fileNameWithExtension,
          content: content.content,
          description: content.path ?? fileNameWithExtension,
          isPublic: true
        })
        .then(gistUrl => {
          setGithubTokenModalVisible(false);
          const fileExtension = extractFileExtension(new URL(gistUrl).pathname);
          // FIXME: KOGITO-1202
          window.location.href = `?file=${gistUrl}#/editor/${fileExtension}`;
        })
        .catch(() => setGithubTokenModalVisible(true));
    });
  }, []);

  const requestCopyContentToClipboard = useCallback(() => {
    editorRef.current?.requestContent().then(content => {
      if (copyContentTextArea.current) {
        copyContentTextArea.current.value = content.content;
        copyContentTextArea.current.select();
        if (document.execCommand("copy")) {
          setCopySuccessAlertVisible(true);
        }
      }
    });
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

  const closeGithubTokenModal = useCallback(() => setGithubTokenModalVisible(false), []);

  const continueExport = useCallback(() => {
    closeGithubTokenModal();
    requestExportGist();
  }, [closeGithubTokenModal, requestExportGist]);

  const onReady = useCallback(() => setIsEditorReady(true), []);

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
      const fileName = removeFileExtension(fileNameWithExtension);
      downloadPreviewRef.current.download = `${fileName}-svg.svg`;
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

  useEffect(() => {
    (async function tryAuthenticate() {
      if (!context.githubService.isAuthenticated()) {
        await context.githubService.authenticate();
      }
    })();
  });

  useDmnTour(isEditorReady, context.file);

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
          onExportGist={requestExportGist}
          isEdited={isDirty}
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
        {!fullscreen && showUnsavedAlert && (
          <div className={"kogito--alert-container-unsaved"} data-testid="unsaved-alert">
            <Alert
              variant="warning"
              title="Unsaved changes will be lost."
              action={
                <AlertActionCloseButton
                  data-testid="unsaved-alert-close-button"
                  onClose={() => setShowUnsavedAlert(false)}
                />
              }
            >
              <div>
                <p>
                  Click Save to download your progress before closing.{" "}
                  <a data-testid="unsaved-alert-save-button" onClick={requestDownload}>
                    Save
                  </a>
                </p>
                <a data-testid="unsaved-alert-close-without-save-button" onClick={closeWithoutSaving}>
                  {" Close without saving"}
                </a>
              </div>
            </Alert>
          </div>
        )}
        {!fullscreen && githubTokenModalVisible && (
          <GithubTokenModal
            isOpen={githubTokenModalVisible}
            onClose={closeGithubTokenModal}
            onContinue={continueExport}
          />
        )}
        {fullscreen && <FullScreenToolbar onExitFullScreen={exitFullscreen} />}
        <EmbeddedEditor
          ref={editorRef}
          file={context.file}
          router={context.router}
          onReady={onReady}
          channelType={ChannelType.ONLINE}
        />
      </PageSection>
      <textarea ref={copyContentTextArea} style={{ height: 0, position: "absolute", zIndex: -1 }} />
      <a ref={downloadRef} />
      <a ref={downloadPreviewRef} />
    </Page>
  );
}
