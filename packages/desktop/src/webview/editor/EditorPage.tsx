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
import { EmbeddedEditor, useDirtyState, useEditorRef } from "@kogito-tooling/editor/dist/embedded";
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
import { useCallback, useContext, useEffect, useRef, useState } from "react";
import { FileSaveActions } from "../../common/ElectronFile";
import { GlobalContext } from "../common/GlobalContext";
import { EditorToolbar } from "./EditorToolbar";
import IpcRendererEvent = Electron.IpcRendererEvent;
import { useDesktopI18n } from "../common/i18n";

interface Props {
  onClose: () => void;
  onFilenameChange: (filePath: string) => void;
}

const ALERT_AUTO_CLOSE_TIMEOUT = 3000;

export function EditorPage(props: Props) {
  const context = useContext(GlobalContext);
  const { editor, editorRef } = useEditorRef();
  const copyContentTextArea = useRef<HTMLTextAreaElement>(null);
  const [copySuccessAlertVisible, setCopySuccessAlertVisible] = useState(false);
  const [saveFileSuccessAlertVisible, setSaveFileSuccessAlertVisible] = useState(false);
  const [savePreviewSuccessAlertVisible, setSavePreviewSuccessAlertVisible] = useState(false);
  const isDirty = useDirtyState(editor);
  const [showUnsavedAlert, setShowUnsavedAlert] = useState(false);
  const { locale, i18n } = useDesktopI18n();

  const onClose = useCallback(() => {
    if (!isDirty) {
      props.onClose();
    } else {
      setShowUnsavedAlert(true);
    }
  }, [isDirty, props.onClose]);

  const onCloseWithoutSave = useCallback(() => {
    setShowUnsavedAlert(false);
    props.onClose();
  }, [props.onClose]);

  const requestSaveFile = useCallback(
    (action: FileSaveActions) => {
      setShowUnsavedAlert(false);
      editor?.getContent().then(content => {
        electron.ipcRenderer.send("saveFile", {
          file: {
            filePath: context.file.fileName,
            fileType: context.file.fileExtension,
            fileContent: content
          },
          action
        });
      });
    },
    [context.file, editor]
  );

  const requestCopyContentToClipboard = useCallback(() => {
    editor?.getContent().then(content => {
      if (copyContentTextArea.current) {
        copyContentTextArea.current.value = content;
        copyContentTextArea.current.select();
        if (document.execCommand("copy")) {
          setCopySuccessAlertVisible(true);
        }
      }
    });
  }, [editor]);

  const requestSavePreview = useCallback(() => {
    editor?.getPreview().then(previewSvg => {
      electron.ipcRenderer.send("savePreview", {
        filePath: context.file!.fileName,
        fileType: "svg",
        fileContent: previewSvg
      });
    });
  }, [editor, context.file]);

  const closeCopySuccessAlert = useCallback(() => setCopySuccessAlertVisible(false), []);
  const closeSaveFileSuccessAlert = useCallback(() => setSaveFileSuccessAlertVisible(false), []);
  const closeSavePreviewSuccessAlert = useCallback(() => setSavePreviewSuccessAlertVisible(false), []);

  const onSave = useCallback(() => {
    requestSaveFile(FileSaveActions.SAVE);
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
    electron.ipcRenderer.on("requestOpenedFile", (event: IpcRendererEvent, data: { action: FileSaveActions }) => {
      requestSaveFile(data.action);
    });

    return () => {
      electron.ipcRenderer.removeAllListeners("requestOpenedFile");
    };
  }, [requestSaveFile]);

  useEffect(() => {
    electron.ipcRenderer.on("copyContentToClipboard", () => {
      requestCopyContentToClipboard();
    });

    return () => {
      electron.ipcRenderer.removeAllListeners("copyContentToClipboard");
    };
  }, [requestCopyContentToClipboard]);

  useEffect(() => {
    electron.ipcRenderer.on("savePreview", () => {
      requestSavePreview();
    });

    return () => {
      electron.ipcRenderer.removeAllListeners("savePreview");
    };
  }, [requestSavePreview]);

  useEffect(() => {
    electron.ipcRenderer.on("saveFileSuccess", (event: IpcRendererEvent, data: { filePath: string }): void => {
      editor
        ?.getPreview()
        .then(previewSvg => {
          electron.ipcRenderer.send("saveThumbnail", {
            filePath: data.filePath,
            fileType: "svg",
            fileContent: previewSvg
          });
          editor?.getStateControl().setSavedCommand();
          setSaveFileSuccessAlertVisible(true);
          props.onFilenameChange(data.filePath);
        })
        .catch(err => console.log(err));
    });

    return () => {
      electron.ipcRenderer.removeAllListeners("saveFileSuccess");
    };
  }, [editor, props.onFilenameChange]);

  const saveOpenedFileThumbnail = useCallback(async () => {
    const previewSvg = await editor?.getPreview();
    electron.ipcRenderer.send("saveThumbnail", {
      filePath: context.file!.fileName,
      fileType: "svg",
      fileContent: previewSvg
    });
  }, [editor, context.file]);

  useEffect(() => {
    electron.ipcRenderer.on("savePreviewSuccess", () => {
      setSavePreviewSuccessAlertVisible(true);
    });

    return () => {
      electron.ipcRenderer.removeAllListeners("savePreviewSuccess");
    };
  }, []);

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
                      <AlertActionLink data-testid="unsaved-alert-save-button" onClick={onSave}>
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
              file={context.file}
              receive_ready={saveOpenedFileThumbnail}
              editorEnvelopeLocator={context.editorEnvelopeLocator}
              channelType={ChannelType.DESKTOP}
              locale={locale}
            />
          </StackItem>
        </Stack>
        <textarea ref={copyContentTextArea} style={{ opacity: 0, width: 0, height: 0 }} />
      </PageSection>
    </Page>
  );
}
