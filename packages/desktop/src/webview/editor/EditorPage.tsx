/*
 * Copyright 2020 Red Hat, Inc. and/or its affiliates.
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
import { EmbeddedEditor, EmbeddedEditorRef, useDirtyState } from "@kogito-tooling/editor/dist/embedded";
import {
  Alert,
  AlertActionCloseButton,
  AlertActionLink,
  Page,
  PageSection,
  Stack,
  StackItem
} from "@patternfly/react-core";
import * as electron from "electron";
import * as React from "react";
import { useCallback, useContext, useEffect, useMemo, useRef, useState } from "react";
import { File, FileSaveActions } from "../../common/File";
import { GlobalContext } from "../common/GlobalContext";
import { EditorToolbar } from "./EditorToolbar";
import IpcRendererEvent = Electron.IpcRendererEvent;
import { useDesktopI18n } from "../common/i18n";

interface Props {
  fileExtension: string;
  onClose: () => void;
}

const ALERT_AUTO_CLOSE_TIMEOUT = 3000;

let contentRequestData: { action: FileSaveActions; file?: File };

export function EditorPage(props: Props) {
  const context = useContext(GlobalContext);
  const editorRef = useRef<EmbeddedEditorRef>(null);
  const copyContentTextArea = useRef<HTMLTextAreaElement>(null);
  const [copySuccessAlertVisible, setCopySuccessAlertVisible] = useState(false);
  const [saveFileSuccessAlertVisible, setSaveFileSuccessAlertVisible] = useState(false);
  const [savePreviewSuccessAlertVisible, setSavePreviewSuccessAlertVisible] = useState(false);
  const isDirty = useDirtyState(editorRef);
  const [showUnsavedAlert, setShowUnsavedAlert] = useState(false);
  const { locale, i18n } = useDesktopI18n();

  const onClose = useCallback(() => {
    if (!isDirty) {
      props.onClose();
    } else {
      setShowUnsavedAlert(true);
    }
  }, [isDirty]);

  const onCloseWithoutSave = useCallback(() => {
    setShowUnsavedAlert(false);
    props.onClose();
  }, []);

  const requestSaveFile = useCallback(() => {
    setShowUnsavedAlert(false);
    editorRef.current?.getContent().then(content => {
      contentRequestData.file = {
        filePath: context.file!.filePath,
        fileType: context.file!.fileType,
        fileContent: content
      };
      electron.ipcRenderer.send("saveFile", contentRequestData);
    });
  }, []);

  const requestCopyContentToClipboard = useCallback(() => {
    editorRef.current?.getContent().then(content => {
      if (copyContentTextArea.current) {
        copyContentTextArea.current.value = content;
        copyContentTextArea.current.select();
        if (document.execCommand("copy")) {
          setCopySuccessAlertVisible(true);
        }
      }
    });
  }, []);

  const requestSavePreview = useCallback(() => {
    editorRef.current?.getPreview().then(previewSvg => {
      electron.ipcRenderer.send("savePreview", {
        filePath: context.file!.filePath,
        fileType: "svg",
        fileContent: previewSvg
      });
    });
  }, []);

  const requestThumbnailPreview = useCallback(() => {
    editorRef.current?.getPreview().then(previewSvg => {
      electron.ipcRenderer.send("saveThumbnail", {
        filePath: context.file!.filePath,
        fileType: "svg",
        fileContent: previewSvg
      });
    });
  }, []);

  const closeCopySuccessAlert = useCallback(() => setCopySuccessAlertVisible(false), []);
  const closeSaveFileSuccessAlert = useCallback(() => setSaveFileSuccessAlertVisible(false), []);
  const closeSavePreviewSuccessAlert = useCallback(() => setSavePreviewSuccessAlertVisible(false), []);

  const onSave = useCallback(() => {
    contentRequestData = {
      action: FileSaveActions.SAVE
    };
    requestSaveFile();
  }, [requestSaveFile]);

  useEffect(() => {
    if (copySuccessAlertVisible) {
      const autoCloseCopySuccessAlert = setTimeout(closeCopySuccessAlert, ALERT_AUTO_CLOSE_TIMEOUT);
      return () => clearInterval(autoCloseCopySuccessAlert);
    }

    return () => {
      /* Do nothing */
    };
  }, [copySuccessAlertVisible, closeCopySuccessAlert]);

  useEffect(() => {
    if (saveFileSuccessAlertVisible) {
      const autoCloseSaveFileSuccessAlert = setTimeout(closeSaveFileSuccessAlert, ALERT_AUTO_CLOSE_TIMEOUT);
      return () => clearInterval(autoCloseSaveFileSuccessAlert);
    }

    return () => {
      /* Do nothing */
    };
  }, [saveFileSuccessAlertVisible, closeSaveFileSuccessAlert]);

  useEffect(() => {
    if (savePreviewSuccessAlertVisible) {
      const autoCloseSavePreviewSuccessAlert = setTimeout(closeSavePreviewSuccessAlert, ALERT_AUTO_CLOSE_TIMEOUT);
      return () => clearInterval(autoCloseSavePreviewSuccessAlert);
    }

    return () => {
      /* Do nothing */
    };
  }, [savePreviewSuccessAlertVisible, closeSavePreviewSuccessAlert]);

  useEffect(() => {
    electron.ipcRenderer.on(
      "requestOpenedFile",
      (event: IpcRendererEvent, data: { action: FileSaveActions; file?: File }) => {
        contentRequestData = data;
        requestSaveFile();
      }
    );

    return () => {
      electron.ipcRenderer.removeAllListeners("requestOpenedFile");
    };
  }, []);

  useEffect(() => {
    electron.ipcRenderer.on("copyContentToClipboard", () => {
      requestCopyContentToClipboard();
    });

    return () => {
      electron.ipcRenderer.removeAllListeners("copyContentToClipboard");
    };
  }, []);

  useEffect(() => {
    electron.ipcRenderer.on("savePreview", () => {
      requestSavePreview();
    });

    return () => {
      electron.ipcRenderer.removeAllListeners("savePreview");
    };
  }, []);

  useEffect(() => {
    electron.ipcRenderer.on("saveThumbnail", () => {
      requestThumbnailPreview();
    });

    return () => {
      electron.ipcRenderer.removeAllListeners("saveThumbnail");
    };
  }, [requestThumbnailPreview]);

  useEffect(() => {
    electron.ipcRenderer.on("saveFileSuccess", () => {
      editorRef.current?.getStateControl().setSavedCommand();
      setSaveFileSuccessAlertVisible(true);
      requestThumbnailPreview();
    });

    return () => {
      electron.ipcRenderer.removeAllListeners("saveFileSuccess");
    };
  }, [requestThumbnailPreview]);

  useEffect(() => {
    electron.ipcRenderer.on("savePreviewSuccess", () => {
      setSavePreviewSuccessAlertVisible(true);
    });

    return () => {
      electron.ipcRenderer.removeAllListeners("savePreviewSuccess");
    };
  }, []);

  const getFileContents = useMemo(() => () => Promise.resolve(context.file?.fileContent ?? ""), [
    context.file?.fileContent
  ]);

  const file = useMemo(
    () => ({
      fileName: context.file?.filePath ?? "",
      fileExtension: context.file?.fileType!,
      getFileContents,
      isReadOnly: false
    }),
    [context.file?.filePath, context.file?.fileType]
  );

  return (
    <Page className={"kogito--editor-page"}>
      <PageSection variant="dark" padding={{ default: "noPadding" }} style={{ flexBasis: "100%" }}>
        <Stack>
          <StackItem>
            <EditorToolbar onClose={onClose} onSave={onSave} isEdited={isDirty} />
          </StackItem>
          <StackItem className="pf-m-fill">
            {showUnsavedAlert && (
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
                      <AlertActionLink data-testid="unsaved-alert-save-button" onClick={requestSaveFile}>
                        {i18n.terms.save}
                      </AlertActionLink>
                      <AlertActionLink
                        data-testid="unsaved-alert-close-without-save-button"
                        onClick={onCloseWithoutSave}
                      >
                        {i18n.editorPage.alerts.unsaved.closeWithoutSaving}
                      </AlertActionLink>
                    </React.Fragment>
                  }
                >
                  <p>{i18n.editorPage.alerts.unsaved.message}</p>
                </Alert>
              </div>
            )}
            {copySuccessAlertVisible && (
              <div className={"kogito--alert-container"}>
                <Alert
                  variant="success"
                  title={i18n.editorPage.alerts.copy}
                  actionClose={<AlertActionCloseButton onClose={closeCopySuccessAlert} />}
                />
              </div>
            )}
            {saveFileSuccessAlertVisible && (
              <div className={"kogito--alert-container"}>
                <Alert
                  variant="success"
                  title={i18n.editorPage.alerts.saved}
                  actionClose={<AlertActionCloseButton onClose={closeSaveFileSuccessAlert} />}
                />
              </div>
            )}
            {savePreviewSuccessAlertVisible && (
              <div className={"kogito--alert-container"}>
                <Alert
                  variant="success"
                  title={i18n.editorPage.alerts.previewSaved}
                  actionClose={<AlertActionCloseButton onClose={closeSavePreviewSuccessAlert} />}
                />
              </div>
            )}
            <EmbeddedEditor
              ref={editorRef}
              file={file}
              channelType={ChannelType.DESKTOP}
              receive_ready={requestThumbnailPreview}
              editorEnvelopeLocator={context.editorEnvelopeLocator}
              locale={locale}
            />
          </StackItem>
        </Stack>
        <textarea ref={copyContentTextArea} style={{ opacity: 0, width: 0, height: 0 }} />
      </PageSection>
    </Page>
  );
}
