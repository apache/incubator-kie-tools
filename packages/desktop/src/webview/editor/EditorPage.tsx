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

import { ChannelType } from "@kie-tooling-core/editor/dist/api";
import { EmbeddedEditor, useDirtyState, useEditorRef } from "@kie-tooling-core/editor/dist/embedded";
import { Alert, AlertActionCloseButton, AlertActionLink } from "@patternfly/react-core/dist/js/components/Alert";
import { Button } from "@patternfly/react-core/dist/js/components/Button";
import { Page, PageSection } from "@patternfly/react-core/dist/js/components/Page";
import { Modal } from "@patternfly/react-core/dist/js/components/Modal";
import { Stack, StackItem } from "@patternfly/react-core/dist/js/layouts/Stack";
import * as electron from "electron";
import * as React from "react";
import { useCallback, useContext, useEffect, useRef, useState } from "react";
import { FileSaveActions } from "../../common/ElectronFile";
import { GlobalContext } from "../common/GlobalContext";
import { EditorToolbar } from "./EditorToolbar";
import { useDesktopI18n } from "../common/i18n";
import * as monaco from "@kie-tooling-core/monaco-editor";
import IpcRendererEvent = Electron.IpcRendererEvent;

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
  const [setContentError, setSetContentError] = useState(false);
  const [fileOpenedAsText, setFileOpenedAsText] = useState(false);
  const { locale, i18n } = useDesktopI18n();
  const textEditorContainerRef = useRef<HTMLDivElement>(null);

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
      editor?.getContent().then((content) => {
        electron.ipcRenderer.send("saveFile", {
          file: {
            filePath: context.file.fileName,
            fileType: context.file.fileExtension,
            fileContent: content,
          },
          action,
        });
      });
    },
    [context.file, editor]
  );

  const requestCopyContentToClipboard = useCallback(() => {
    editor?.getContent().then((content) => {
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
    editor?.getPreview().then((previewSvg) => {
      electron.ipcRenderer.send("savePreview", {
        filePath: context.file!.fileName,
        fileType: "svg",
        fileContent: previewSvg,
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
        .then((previewSvg) => {
          electron.ipcRenderer.send("saveThumbnail", {
            filePath: data.filePath,
            fileType: "svg",
            fileContent: previewSvg,
          });
          editor?.getStateControl().setSavedCommand();
          setSaveFileSuccessAlertVisible(true);
          props.onFilenameChange(data.filePath);
        })
        .catch((err) => console.log(err));
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
      fileContent: previewSvg,
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

  const onSetContentError = useCallback(() => {
    setSetContentError(true);
  }, []);

  const openFileAsText = useCallback(() => {
    setFileOpenedAsText(true);
  }, []);

  const refreshDiagramEditor = useCallback(() => {
    setFileOpenedAsText(false);
    setSetContentError(false);
  }, []);

  const [textEditorContent, setTextEditorContext] = useState<string | undefined>(undefined);

  useEffect(() => {
    context.file.getFileContents().then((content) => {
      setTextEditorContext(content);
    });
  }, [context.file]);

  useEffect(() => {
    if (!fileOpenedAsText) {
      return;
    }

    const monacoInstance = monaco.editor.create(textEditorContainerRef.current!, {
      value: textEditorContent,
      language: "xml", //FIXME: Not all editors will be XML when converted to text
      scrollBeyondLastLine: false,
    });

    return () => {
      const contentAfterFix = monacoInstance.getValue();
      monacoInstance.dispose();

      editor
        ?.setContent(context.file.fileName, contentAfterFix)
        .then(() => {
          editor?.getStateControl().updateCommandStack({
            id: "fix-from-text-editor",
            undo: () => {
              editor?.setContent(context.file.fileName, textEditorContent!);
            },
            redo: () => {
              editor?.setContent(context.file.fileName, contentAfterFix).then(() => {
                setFileOpenedAsText(false);
                setSetContentError(false);
              });
            },
          });
        })
        .catch(() => {
          setTextEditorContext(contentAfterFix);
        });
    };
  }, [fileOpenedAsText, editor, context.file, textEditorContent]);

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
                      ouiaId="unsaved-changes-close-alert-button"
                    />
                  }
                  actionLinks={
                    <React.Fragment>
                      <AlertActionLink
                        data-testid="unsaved-alert-save-button"
                        onClick={onSave}
                        ouiaId="unsaved-alert-save-button"
                      >
                        {i18n.terms.save}
                      </AlertActionLink>
                      <AlertActionLink
                        data-testid="unsaved-alert-close-without-save-button"
                        onClick={onCloseWithoutSave}
                        ouiaId="unsaved-changes-close-without-saving-button"
                      >
                        {i18n.editorPage.alerts.unsaved.closeWithoutSaving}
                      </AlertActionLink>
                    </React.Fragment>
                  }
                  ouiaId="unsaved-changes-warning-alert"
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
            {setContentError && !fileOpenedAsText && (
              <div className={"kogito--alert-container"}>
                <Alert
                  variant="danger"
                  title={i18n.editorPage.alerts.setContentError.title}
                  actionLinks={
                    <>
                      <AlertActionLink data-testid="unsaved-alert-save-button" onClick={openFileAsText}>
                        {i18n.editorPage.alerts.setContentError.action}
                      </AlertActionLink>
                    </>
                  }
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
              kogitoEditor_ready={saveOpenedFileThumbnail}
              kogitoEditor_setContentError={onSetContentError}
              editorEnvelopeLocator={context.editorEnvelopeLocator}
              channelType={ChannelType.DESKTOP}
              locale={locale}
            />
            <Modal
              showClose={false}
              width={"100%"}
              height={"100%"}
              title={i18n.editorPage.textEditorModal.title(context.file.fileName.split("/").pop()!)}
              isOpen={fileOpenedAsText}
              actions={[
                <Button key="confirm" variant="primary" onClick={refreshDiagramEditor}>
                  {i18n.terms.done}
                </Button>,
              ]}
            >
              <div style={{ width: "100%", minHeight: "calc(100vh - 210px)" }} ref={textEditorContainerRef} />
            </Modal>
          </StackItem>
        </Stack>
        <textarea ref={copyContentTextArea} style={{ opacity: 0, width: 0, height: 0 }} />
      </PageSection>
    </Page>
  );
}
