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
import { useCallback, useEffect, useMemo, useRef, useState } from "react";
import { useHistory } from "react-router";
import { SupportedFileExtensions, useGlobals } from "../common/GlobalContext";
import { FullScreenToolbar } from "./EditorFullScreenToolbar";
import { EditorToolbar } from "./EditorToolbar";
import { useDmnTour } from "../tour";
import { useOnlineI18n } from "../common/i18n";
import { UpdateGistErrors } from "../settings/GithubService";
import { EmbedModal } from "./EmbedModal";
import { ChannelType } from "@kie-tooling-core/editor/dist/api";
import { EmbeddedEditor, useDirtyState, useEditorRef } from "@kie-tooling-core/editor/dist/embedded";
import { Drawer, DrawerContent, DrawerContentBody } from "@patternfly/react-core/dist/js/components/Drawer";
import { DmnRunnerDrawer } from "./DmnRunner/DmnRunnerDrawer";
import { DmnRunnerContext } from "./DmnRunner/DmnRunnerContext";
import { DmnRunnerContextProvider } from "./DmnRunner/DmnRunnerContextProvider";
import { NotificationsPanel } from "./NotificationsPanel/NotificationsPanel";
import { DmnRunnerStatus } from "./DmnRunner/DmnRunnerStatus";
import { NotificationsPanelContextProvider } from "./NotificationsPanel/NotificationsPanelContextProvider";
import { NotificationsPanelContextType } from "./NotificationsPanel/NotificationsPanelContext";
import { Alert, AlertActionCloseButton, AlertActionLink } from "@patternfly/react-core/dist/js/components/Alert";
import { Button } from "@patternfly/react-core/dist/js/components/Button";
import { Page, PageSection } from "@patternfly/react-core/dist/js/components/Page";
import { Modal } from "@patternfly/react-core/dist/js/components/Modal";
import { DmnDevSandboxContextProvider } from "./DmnDevSandbox/DmnDevSandboxContextProvider";
import { useQueryParams } from "../queryParams/QueryParamsContext";
import { extractFileExtension, removeDirectories, removeFileExtension } from "../common/utils";
import { useSettings } from "../settings/SettingsContext";
import { File, newFile } from "@kie-tooling-core/editor/dist/channel";
import { QueryParams } from "../common/Routes";

const importMonacoEditor = () => import(/* webpackChunkName: "monaco-editor" */ "@kie-tooling-core/monaco-editor");

export enum AlertTypes {
  NONE,
  COPY,
  SUCCESS_UPDATE_GIST,
  SUCCESS_CREATE_GIST,
  INVALID_CURRENT_GIST,
  INVALID_GIST_FILENAME,
  SET_CONTENT_ERROR,
  UNSAVED,
  ERROR,
}

export enum ModalType {
  NONE,
  TEXT_EDITOR,
  EMBED,
  DMN_RUNNER_HELPER,
}

export function EditorPage(props: { forExtension: SupportedFileExtensions }) {
  const globals = useGlobals();
  const settings = useSettings();
  const { editor, editorRef } = useEditorRef();
  const [currentFile, setCurrentFile] = useState(() => newFile(props.forExtension, "local"));
  const downloadRef = useRef<HTMLAnchorElement>(null);
  const downloadPreviewRef = useRef<HTMLAnchorElement>(null);
  const copyContentTextArea = useRef<HTMLTextAreaElement>(null);
  const [fullscreen, setFullscreen] = useState(false);
  const [openAlert, setOpenAlert] = useState(AlertTypes.NONE);
  const [openModalType, setOpenModalType] = useState(ModalType.NONE);
  const isDirty = useDirtyState(editor);
  const { locale, i18n } = useOnlineI18n();
  const textEditorContainerRef = useRef<HTMLDivElement>(null);
  const history = useHistory();
  const queryParams = useQueryParams();

  const queryParamFile = useMemo(() => queryParams.get(QueryParams.FILE), [queryParams]);
  const queryParamReadonly = useMemo(
    () => (queryParams.has(QueryParams.READONLY) ? queryParams.get(QueryParams.READONLY) === `${true}` : false),
    [queryParams]
  );

  useEffect(() => {
    let canceled = false;
    if (globals.externalFile) {
      setCurrentFile({ ...globals.externalFile, kind: "external" });
      return;
    }

    if (globals.uploadedFile) {
      setCurrentFile({ ...globals.uploadedFile, kind: "local" });
      return;
    }

    if (!queryParamFile) {
      setCurrentFile(newFile(props.forExtension, "local"));
      return;
    }

    if (settings.github.service.isGist(queryParamFile)) {
      settings.github.service
        .fetchGistFile(settings.github.octokit, queryParamFile)
        .then((content) => {
          if (canceled) return;
          setCurrentFile(
            getFileToOpen({
              filePath: queryParamFile,
              readonly: queryParamReadonly,
              getFileContent: Promise.resolve(content),
              kind: "gist",
            })
          );
        })
        .catch((error) => {
          //FIXME: tiago
          console.info("error");
        });
      return;
    }

    if (settings.github.service.isGithub(queryParamFile) || settings.github.service.isGithubRaw(queryParamFile)) {
      settings.github.service
        .fetchGithubFile(settings.github.octokit, queryParamFile)
        .then((response) => {
          if (canceled) return;
          setCurrentFile(
            getFileToOpen({
              filePath: queryParamFile,
              readonly: queryParamReadonly,
              getFileContent: Promise.resolve(response),
              kind: "external",
            })
          );
        })
        .catch((error) => {
          //FIXME: tiago
          console.info("error");
        });
      return;
    }

    fetch(queryParamFile)
      .then((response) => {
        if (canceled) return;
        if (response.ok) {
          setCurrentFile(
            getFileToOpen({
              filePath: queryParamFile,
              readonly: queryParamReadonly,
              getFileContent: response.text(),
              kind: "external",
            })
          );
        } else {
          //FIXME: tiago
          console.info("error");
        }
      })
      .catch((error) => {
        //FIXME: tiago
        console.info("error");
      });

    return () => {
      canceled = true;
    };
  }, [
    props.forExtension,
    globals.externalFile,
    globals.uploadedFile,
    queryParamFile,
    queryParamReadonly,
    settings.github.octokit,
    settings.github.service,
  ]);

  const close = useCallback(() => {
    if (!isDirty) {
      history.push({
        pathname: globals.routes.home.path({}),
      });
    } else {
      setOpenAlert(AlertTypes.UNSAVED);
    }
  }, [globals, history, isDirty]);

  const closeWithoutSaving = useCallback(() => {
    setOpenAlert(AlertTypes.NONE);
    history.push({
      pathname: globals.routes.home.path({}),
    });
  }, [globals, history]);

  const requestSave = useCallback(() => {
    editor?.getContent().then((content) => {
      window.dispatchEvent(
        new CustomEvent("saveOnlineEditor", {
          detail: {
            fileName: `${currentFile.fileName}.${currentFile.fileExtension}`,
            fileContent: content,
            senderTabId: globals.senderTabId!,
          },
        })
      );
    });
  }, [currentFile, globals.senderTabId, editor]);

  const requestDownload = useCallback(() => {
    editor?.getStateControl().setSavedCommand();
    setOpenAlert(AlertTypes.NONE);
    editor?.getContent().then((content) => {
      if (downloadRef.current) {
        const fileBlob = new Blob([content], { type: "text/plain" });
        downloadRef.current.href = URL.createObjectURL(fileBlob);
        downloadRef.current.click();
      }
    });
  }, [editor]);

  const requestPreview = useCallback(() => {
    editor?.getPreview().then((previewSvg) => {
      if (downloadPreviewRef.current && previewSvg) {
        const fileBlob = new Blob([previewSvg], { type: "image/svg+xml" });
        downloadPreviewRef.current.href = URL.createObjectURL(fileBlob);
        downloadPreviewRef.current.click();
      }
    });
  }, [editor]);

  const requestGistIt = useCallback(async () => {
    if (editor) {
      const content = await editor.getContent();

      // update gist
      if (queryParamFile && settings.github.service.isGist(queryParamFile)) {
        const userLogin = settings.github.service.extractUserLoginFromFileUrl(queryParamFile);
        if (userLogin === settings.github.user) {
          try {
            const filename = `${currentFile.fileName}.${currentFile.fileExtension}`;
            const response = await settings.github.service.updateGist(settings.github.octokit, {
              filename,
              content,
            });

            if (response === UpdateGistErrors.INVALID_CURRENT_GIST) {
              setOpenAlert(AlertTypes.INVALID_CURRENT_GIST);
              return;
            }

            if (response === UpdateGistErrors.INVALID_GIST_FILENAME) {
              setOpenAlert(AlertTypes.INVALID_GIST_FILENAME);
              return;
            }

            editor.getStateControl().setSavedCommand();
            if (filename !== settings.github.service.getCurrentGist()?.filename) {
              setOpenAlert(AlertTypes.SUCCESS_UPDATE_GIST);
              history.push({
                pathname: globals.routes.editor.path({ extension: currentFile.fileExtension }),
                search: globals.routes.editor.queryArgs(queryParams).with(QueryParams.FILE, response).toString(),
              });
              return;
            }

            setOpenAlert(AlertTypes.SUCCESS_UPDATE_GIST);
            return;
          } catch (err) {
            console.error(err);
            setOpenAlert(AlertTypes.ERROR);
            return;
          }
        }
      }

      // create gist
      try {
        const newGistUrl = await settings.github.service.createGist(settings.github.octokit, {
          filename: `${currentFile.fileName}.${currentFile.fileExtension}`,
          content: content,
          description: `${currentFile.fileName}.${currentFile.fileExtension}`,
          isPublic: true,
        });

        setOpenAlert(AlertTypes.SUCCESS_CREATE_GIST);

        history.push({
          pathname: globals.routes.editor.path({ extension: currentFile.fileExtension }),
          search: globals.routes.editor.queryArgs(queryParams).with(QueryParams.FILE, newGistUrl).toString(),
        });
        return;
      } catch (err) {
        console.error(err);
        setOpenAlert(AlertTypes.ERROR);
        return;
      }
    }
  }, [currentFile, history, globals, settings, queryParamFile, queryParams, editor]);

  const requestEmbed = useCallback(() => {
    setOpenModalType(ModalType.EMBED);
  }, []);

  const closeModal = useCallback(() => {
    setOpenModalType(ModalType.NONE);
  }, []);

  const requestCopyContentToClipboard = useCallback(() => {
    editor?.getContent().then((content) => {
      if (copyContentTextArea.current) {
        copyContentTextArea.current.value = content;
        copyContentTextArea.current.select();
        if (document.execCommand("copy")) {
          setOpenAlert(AlertTypes.COPY);
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
    setFullscreen((prev) => !prev);
  }, []);

  useEffect(() => {
    if (downloadRef.current) {
      downloadRef.current.download = `${currentFile.fileName}.${currentFile.fileExtension}`;
    }
    if (downloadPreviewRef.current) {
      downloadPreviewRef.current.download = `${currentFile.fileName}-svg.svg`;
    }
  }, [currentFile]);

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

  useDmnTour(!currentFile.isReadOnly && (editor?.isReady ?? false) && openAlert === AlertTypes.NONE, currentFile);

  const closeAlert = useCallback(() => setOpenAlert(AlertTypes.NONE), []);

  const onSetContentError = useCallback(() => {
    setOpenAlert(AlertTypes.SET_CONTENT_ERROR);
  }, []);

  const openFileAsText = useCallback(() => {
    setOpenModalType(ModalType.TEXT_EDITOR);
  }, []);

  const refreshDiagramEditor = useCallback(() => {
    setOpenModalType(ModalType.NONE);
    setOpenAlert(AlertTypes.NONE);
  }, []);

  const [textEditorContent, setTextEditorContext] = useState<string | undefined>(undefined);

  useEffect(() => {
    currentFile.getFileContents().then((content) => {
      setTextEditorContext(content);
    });
  }, [currentFile]);

  useEffect(() => {
    if (openModalType !== ModalType.TEXT_EDITOR) {
      return;
    }

    let monacoInstance: any;

    importMonacoEditor().then((monaco) => {
      monacoInstance = monaco.editor.create(textEditorContainerRef.current!, {
        value: textEditorContent!,
        language: "xml", //FIXME: Not all editors will be XML when converted to text
        scrollBeyondLastLine: false,
      });
    });

    return () => {
      if (!monacoInstance) {
        return;
      }

      const contentAfterFix = monacoInstance.getValue();
      monacoInstance.dispose();

      editor
        ?.setContent(currentFile.fileName, contentAfterFix)
        .then(() => {
          editor?.getStateControl().updateCommandStack({
            id: "fix-from-text-editor",
            undo: () => {
              editor?.setContent(currentFile.fileName, textEditorContent!);
            },
            redo: () => {
              editor?.setContent(currentFile.fileName, contentAfterFix).then(() => setOpenAlert(AlertTypes.NONE));
            },
          });
        })
        .catch(() => {
          setTextEditorContext(contentAfterFix);
        });
    };
  }, [openModalType, editor, currentFile, textEditorContent]);

  const notificationsPanelRef = useRef<NotificationsPanelContextType>(null);

  const notificationPanelTabNames = useCallback(
    (dmnRunnerStatus: DmnRunnerStatus) => {
      if (currentFile.fileExtension === "dmn" && globals.isChrome && dmnRunnerStatus === DmnRunnerStatus.AVAILABLE) {
        return [i18n.terms.validation, i18n.terms.execution];
      }
      return [i18n.terms.validation];
    },
    [currentFile.fileExtension, globals.isChrome, i18n]
  );

  useEffect(() => {
    if (!editor?.isReady) {
      return;
    }

    const validate = () => {
      editor.validate().then((notifications) => {
        if (!Array.isArray(notifications)) {
          notifications = [];
        }
        notificationsPanelRef.current
          ?.getTabRef(i18n.terms.validation)
          ?.kogitoNotifications_setNotifications("", notifications);
      });
    };

    let timeout: number | undefined;
    const subscription = editor.getStateControl().subscribe(() => {
      if (timeout) {
        clearTimeout(timeout);
      }
      timeout = window.setTimeout(validate, 200);
    });
    validate();

    return () => editor.getStateControl().unsubscribe(subscription);
  }, [editor, i18n]);

  return (
    <NotificationsPanelContextProvider ref={notificationsPanelRef}>
      <DmnRunnerContextProvider currentFile={currentFile} editor={editor}>
        <DmnDevSandboxContextProvider currentFile={currentFile} editor={editor}>
          <Page
            header={
              <EditorToolbar
                currentFile={currentFile}
                onRename={(newName) => setCurrentFile((prev) => ({ ...prev, fileName: newName }))}
                onFullScreen={enterFullscreen}
                onSave={requestSave}
                onDownload={requestDownload}
                onClose={close}
                onCopyContentToClipboard={requestCopyContentToClipboard}
                isPageFullscreen={fullscreen}
                onPreview={requestPreview}
                onGistIt={requestGistIt}
                onEmbed={requestEmbed}
                isEdited={isDirty}
              />
            }
          >
            <PageSection isFilled={true} padding={{ default: "noPadding" }} className={"kogito--editor__page-section"}>
              <DmnRunnerContext.Consumer>
                {(dmnRunner) => (
                  <Drawer isInline={true} isExpanded={dmnRunner.isDrawerExpanded}>
                    <DrawerContent
                      className={
                        !dmnRunner.isDrawerExpanded
                          ? "kogito--editor__drawer-content-close"
                          : "kogito--editor__drawer-content-open"
                      }
                      panelContent={<DmnRunnerDrawer editor={editor} />}
                    >
                      <DrawerContentBody className={"kogito--editor__drawer-content-body"}>
                        {!fullscreen && openAlert === AlertTypes.SET_CONTENT_ERROR && (
                          <div className={"kogito--alert-container"}>
                            <Alert
                              ouiaId="invalid-content-alert"
                              variant="danger"
                              title={i18n.editorPage.alerts.setContentError.title}
                              actionLinks={
                                <AlertActionLink data-testid="unsaved-alert-save-button" onClick={openFileAsText}>
                                  {i18n.editorPage.alerts.setContentError.action}
                                </AlertActionLink>
                              }
                            />
                          </div>
                        )}
                        {!fullscreen && openAlert === AlertTypes.COPY && (
                          <div className={"kogito--alert-container"}>
                            <Alert
                              className={"kogito--alert"}
                              variant="success"
                              title={i18n.editorPage.alerts.copy}
                              actionClose={<AlertActionCloseButton onClose={closeAlert} />}
                            />
                          </div>
                        )}
                        {!fullscreen && openAlert === AlertTypes.SUCCESS_UPDATE_GIST && (
                          <div className={"kogito--alert-container"}>
                            <Alert
                              className={"kogito--alert"}
                              variant="success"
                              title={i18n.editorPage.alerts.updateGist}
                              actionClose={<AlertActionCloseButton onClose={closeAlert} />}
                            />
                          </div>
                        )}
                        {!fullscreen && openAlert === AlertTypes.SUCCESS_CREATE_GIST && (
                          <div className={"kogito--alert-container"}>
                            <Alert
                              className={"kogito--alert"}
                              variant="success"
                              title={i18n.editorPage.alerts.createGist}
                              actionClose={<AlertActionCloseButton onClose={closeAlert} />}
                            />
                          </div>
                        )}
                        {!fullscreen && openAlert === AlertTypes.INVALID_CURRENT_GIST && (
                          <div className={"kogito--alert-container"}>
                            <Alert
                              className={"kogito--alert"}
                              variant="danger"
                              title={i18n.editorPage.alerts.invalidCurrentGist}
                              actionClose={<AlertActionCloseButton onClose={closeAlert} />}
                            />
                          </div>
                        )}
                        {!fullscreen && openAlert === AlertTypes.INVALID_GIST_FILENAME && (
                          <div className={"kogito--alert-container"}>
                            <Alert
                              className={"kogito--alert"}
                              variant="danger"
                              title={i18n.editorPage.alerts.invalidGistFilename}
                              actionClose={<AlertActionCloseButton onClose={closeAlert} />}
                            />
                          </div>
                        )}
                        {!fullscreen && openAlert === AlertTypes.ERROR && (
                          <div className={"kogito--alert-container"}>
                            <Alert
                              className={"kogito--alert"}
                              variant="danger"
                              title={i18n.editorPage.alerts.error}
                              actionClose={<AlertActionCloseButton onClose={closeAlert} />}
                            />
                          </div>
                        )}
                        {!fullscreen && openAlert === AlertTypes.UNSAVED && (
                          <div className={"kogito--alert-container-unsaved"} data-testid="unsaved-alert">
                            <Alert
                              className={"kogito--alert"}
                              variant="warning"
                              title={i18n.editorPage.alerts.unsaved.title}
                              actionClose={
                                <AlertActionCloseButton data-testid="unsaved-alert-close-button" onClose={closeAlert} />
                              }
                              actionLinks={
                                <React.Fragment>
                                  <AlertActionLink data-testid="unsaved-alert-save-button" onClick={requestDownload}>
                                    {i18n.terms.save}
                                  </AlertActionLink>
                                  <AlertActionLink
                                    data-testid="unsaved-alert-close-without-save-button"
                                    onClick={closeWithoutSaving}
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
                        {!fullscreen && (
                          <EmbedModal
                            currentFile={currentFile}
                            isOpen={openModalType === ModalType.EMBED}
                            onClose={closeModal}
                            editor={editor}
                          />
                        )}
                        {fullscreen && <FullScreenToolbar onExitFullScreen={exitFullscreen} />}
                        <EmbeddedEditor
                          ref={editorRef}
                          file={currentFile}
                          kogitoEditor_setContentError={onSetContentError}
                          editorEnvelopeLocator={globals.editorEnvelopeLocator}
                          channelType={ChannelType.ONLINE}
                          locale={locale}
                        />
                        <Modal
                          showClose={false}
                          width={"100%"}
                          height={"100%"}
                          title={i18n.editorPage.textEditorModal.title(currentFile.fileName.split("/").pop()!)}
                          isOpen={openModalType === ModalType.TEXT_EDITOR}
                          actions={[
                            <Button key="confirm" variant="primary" onClick={refreshDiagramEditor}>
                              {i18n.terms.done}
                            </Button>,
                          ]}
                        >
                          <div
                            style={{ width: "100%", minHeight: "calc(100vh - 210px)" }}
                            ref={textEditorContainerRef}
                          />
                        </Modal>
                        <NotificationsPanel tabNames={notificationPanelTabNames(dmnRunner.status)} />
                      </DrawerContentBody>
                    </DrawerContent>
                  </Drawer>
                )}
              </DmnRunnerContext.Consumer>
            </PageSection>
            <textarea ref={copyContentTextArea} style={{ height: 0, position: "absolute", zIndex: -1 }} />
            <a ref={downloadRef} />
            <a ref={downloadPreviewRef} />
          </Page>
        </DmnDevSandboxContextProvider>
      </DmnRunnerContextProvider>
    </NotificationsPanelContextProvider>
  );
}

function getFileToOpen(args: {
  kind: File["kind"];
  filePath: string;
  readonly: boolean;
  getFileContent: Promise<string>;
}) {
  return {
    kind: args.kind,
    isReadOnly: args.readonly,
    fileExtension: extractFileExtension(removeDirectories(args.filePath) ?? "")!,
    fileName: removeFileExtension(removeDirectories(args.filePath) ?? ""),
    getFileContents: () => args.getFileContent,
  };
}
