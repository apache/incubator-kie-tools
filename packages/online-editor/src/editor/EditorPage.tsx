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

import { ChannelType } from "@kogito-tooling/channel-common-api";
import { EmbeddedEditor, useDirtyState, useEditorRef } from "@kogito-tooling/editor/dist/embedded";
import { Alert, AlertActionCloseButton, AlertActionLink, Page, PageSection } from "@patternfly/react-core";
import * as React from "react";
import { useCallback, useContext, useEffect, useMemo, useRef, useState } from "react";
import { useLocation } from "react-router";
import { GithubTokenModal } from "../common/GithubTokenModal";
import { GlobalContext } from "../common/GlobalContext";
import { FullScreenToolbar } from "./EditorFullScreenToolbar";
import { EditorToolbar } from "./EditorToolbar";
import { useDmnTour } from "../tour";
import { useOnlineI18n } from "../common/i18n";
import { getFileUrl } from "../common/utils";

interface Props {
  onFileNameChanged: (fileName: string, fileExtension: string) => void;
}

export function EditorPage(props: Props) {
  const context = useContext(GlobalContext);
  const location = useLocation();
  const { editor, editorRef } = useEditorRef();
  const downloadRef = useRef<HTMLAnchorElement>(null);
  const downloadPreviewRef = useRef<HTMLAnchorElement>(null);
  const copyContentTextArea = useRef<HTMLTextAreaElement>(null);
  const [isEditorReady, setIsEditorReady] = useState(false);
  const [fullscreen, setFullscreen] = useState(false);
  const [copySuccessAlertVisible, setCopySuccessAlertVisible] = useState(false);
  const [updateGistSuccessAlertVisible, setUpdateGistSuccessAlertVisible] = useState(false);
  const [githubTokenModalVisible, setGithubTokenModalVisible] = useState(false);
  const [showUnsavedAlert, setShowUnsavedAlert] = useState(false);
  const isDirty = useDirtyState(editor);
  const { locale, i18n } = useOnlineI18n();
  const fileUrl = useMemo(() => getFileUrl(), [window.location]);

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
    editor?.getContent().then(content => {
      window.dispatchEvent(
        new CustomEvent("saveOnlineEditor", {
          detail: {
            fileName: `${context.file.fileName}.${context.file.fileExtension}`,
            fileContent: content,
            senderTabId: context.senderTabId!
          }
        })
      );
    });
  }, [context.file.fileName, editor]);

  const requestDownload = useCallback(() => {
    editor?.getStateControl().setSavedCommand();
    setShowUnsavedAlert(false);
    editor?.getContent().then(content => {
      if (downloadRef.current) {
        const fileBlob = new Blob([content], { type: "text/plain" });
        downloadRef.current.href = URL.createObjectURL(fileBlob);
        downloadRef.current.click();
      }
    });
  }, [editor]);

  const requestPreview = useCallback(() => {
    editor?.getPreview().then(previewSvg => {
      if (downloadPreviewRef.current && previewSvg) {
        const fileBlob = new Blob([previewSvg], { type: "image/svg+xml" });
        downloadPreviewRef.current.href = URL.createObjectURL(fileBlob);
        downloadPreviewRef.current.click();
      }
    });
  }, [editor]);

  const requestSetGitHubToken = useCallback(() => {
    setGithubTokenModalVisible(true);
  }, [])

  const requestExportGist = useCallback(() => {
    editor?.getContent().then(content => {
      if (!context.githubService.isAuthenticated()) {
        setGithubTokenModalVisible(true);
        return;
      }

      context.githubService
        .createGist({
          filename: `${context.file.fileName}.${context.file.fileExtension}`,
          content: content,
          description: `${context.file.fileName}.${context.file.fileExtension}`,
          isPublic: true
        })
        .then(gistUrl => {
          setGithubTokenModalVisible(false);
          // FIXME: KOGITO-1202
          window.location.href = `?file=${gistUrl}#/editor/${fileExtension}`;
        })
        .catch(() => setGithubTokenModalVisible(true));
    });
  }, [context.file.fileName, editor]);

  const requestUpdateGist = useCallback(() => {
    editor?.getContent().then(content => {
      if (!context.githubService.isAuthenticated()) {
        setGithubTokenModalVisible(true);
        return;
      }

      context.githubService
        .updateGist({
          gistId: context.githubService.extractGistIdFromRawUrl(fileUrl!),
          filename: `${context.file.fileName}.${context.file.fileExtension}`,
          content: content,
          description: `${context.file.fileName}.${context.file.fileExtension}`,
          isPublic: true
        })
        .then(() => {
          editor?.getStateControl().setSavedCommand();
          closeAllSuccessAlerts();
          setUpdateGistSuccessAlertVisible(true);
        })
        .catch(() => setGithubTokenModalVisible(true));
    });
  }, [context.file.fileName]);

  const fileExtension = useMemo(() => {
    return context.routes.editor.args(location.pathname).type;
  }, [location.pathname]);

  const requestCopyContentToClipboard = useCallback(() => {
    editor?.getContent().then(content => {
      if (copyContentTextArea.current) {
        copyContentTextArea.current.value = content;
        copyContentTextArea.current.select();
        if (document.execCommand("copy")) {
          closeAllSuccessAlerts();
          setCopySuccessAlertVisible(true);
        }
      }
    });
  }, [editor]);

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

  const closeCopySuccessAlert = useCallback(() => setCopySuccessAlertVisible(false), []);

  const closeUpdateGistSuccessAlert = useCallback(() => setUpdateGistSuccessAlertVisible(false), []);

  const closeGithubTokenModal = useCallback(() => setGithubTokenModalVisible(false), []);

  const closeAllSuccessAlerts = useCallback(() => {
    setCopySuccessAlertVisible(false);
    setUpdateGistSuccessAlertVisible(false);
  }, []);

  const continueExport = useCallback(() => {
    closeGithubTokenModal();
    if (fileUrl && !context.githubService.isGistRaw(fileUrl)) {
      requestExportGist();
    }
  }, [closeGithubTokenModal, requestExportGist, window.location]);

  const onReady = useCallback(() => setIsEditorReady(true), []);

  useEffect(() => {
    if (downloadRef.current) {
      downloadRef.current.download = `${context.file.fileName}.${context.file.fileExtension}`;
    }
    if (downloadPreviewRef.current) {
      const fileName = context.file.fileName;
      downloadPreviewRef.current.download = `${fileName}-svg.svg`;
    }
  }, [context.file.fileName]);

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
          onSetGitHubToken={requestSetGitHubToken}
          onExportGist={requestExportGist}
          onUpdateGist={requestUpdateGist}
          isEdited={isDirty}
        />
      }
    >
      <PageSection isFilled={true} padding={{ default: "noPadding" }} style={{ flexBasis: "100%" }}>
        {!fullscreen && copySuccessAlertVisible && (
          <div className={"kogito--alert-container"}>
            <Alert
              variant="success"
              title={i18n.editorPage.alerts.copy}
              actionClose={<AlertActionCloseButton onClose={closeCopySuccessAlert} />}
            />
          </div>
        )}
        {!fullscreen && updateGistSuccessAlertVisible && (
          <div className={"kogito--alert-container"}>
            <Alert
              variant="success"
              title={"Your gist was updated."}
              actionClose={<AlertActionCloseButton onClose={closeUpdateGistSuccessAlert} />}
            />
          </div>
        )}
        {!fullscreen && showUnsavedAlert && (
          <div className={"kogito--alert-container-unsaved"} data-testid="unsaved-alert">
            <Alert
              variant="warning"
              title={i18n.editorPage.alerts.unsaved.title}
              actionClose={
                <AlertActionCloseButton
                  data-testid="unsaved-alert-close-button"
                  onClose={() => setShowUnsavedAlert(false)}
                />
              }
              actionLinks={
                <React.Fragment>
                  <AlertActionLink data-testid="unsaved-alert-save-button" onClick={requestDownload}>
                    {i18n.terms.save}
                  </AlertActionLink>
                  <AlertActionLink data-testid="unsaved-alert-close-without-save-button" onClick={closeWithoutSaving}>
                    {i18n.editorPage.alerts.unsaved.closeWithoutSaving}
                  </AlertActionLink>
                </React.Fragment>
              }
            >
              <p>{i18n.editorPage.alerts.unsaved.message}</p>
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
          editorEnvelopeLocator={context.editorEnvelopeLocator}
          receive_ready={onReady}
          channelType={ChannelType.ONLINE}
          locale={locale}
        />
      </PageSection>
      <textarea ref={copyContentTextArea} style={{ height: 0, position: "absolute", zIndex: -1 }} />
      <a ref={downloadRef} />
      <a ref={downloadPreviewRef} />
    </Page>
  );
}
