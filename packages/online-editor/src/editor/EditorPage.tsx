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
import { useHistory, useLocation } from "react-router";
import { useGlobals } from "../common/GlobalContext";
import { FullScreenToolbar } from "./EditorFullScreenToolbar";
import { EditorToolbar } from "./EditorToolbar";
import { useDmnTour } from "../tour";
import { useOnlineI18n } from "../common/i18n";
import { UpdateGistErrors } from "../common/GithubService";
import { EmbedModal } from "./EmbedModal";
import { useFileUrl } from "../common/Hooks";
import { ChannelType } from "@kie-tooling-core/editor/dist/api";
import { EmbeddedEditor, useDirtyState, useEditorRef } from "@kie-tooling-core/editor/dist/embedded";
import { Drawer, DrawerContent, DrawerContentBody } from "@patternfly/react-core/dist/js/components/Drawer";
import { DmnRunnerDrawer } from "./DmnRunner/DmnRunnerDrawer";
import { DmnRunnerContext } from "./DmnRunner/DmnRunnerContext";
import { DmnRunnerContextProvider } from "./DmnRunner/DmnRunnerContextProvider";
import { KieToolingExtendedServicesContextProvider } from "./KieToolingExtendedServices/KieToolingExtendedServicesContextProvider";
import { NotificationsPanel } from "./NotificationsPanel/NotificationsPanel";
import { DmnRunnerStatus } from "./DmnRunner/DmnRunnerStatus";
import { NotificationsPanelContextProvider } from "./NotificationsPanel/NotificationsPanelContextProvider";
import { NotificationsPanelContextType } from "./NotificationsPanel/NotificationsPanelContext";
import { Alert, AlertActionCloseButton, AlertActionLink } from "@patternfly/react-core/dist/js/components/Alert";
import { Button } from "@patternfly/react-core/dist/js/components/Button";
import { Page, PageSection } from "@patternfly/react-core/dist/js/components/Page";
import { Modal } from "@patternfly/react-core/dist/js/components/Modal";
import { DmnDevSandboxContextProvider } from "./DmnDevSandbox/DmnDevSandboxContextProvider";
import { QueryParams, useQueryParams } from "../queryParams/QueryParamsContext";
import { extractFileExtension, removeDirectories, removeFileExtension } from "../common/utils";
import { useSettings } from "../settings/SettingsContext";

const importMonacoEditor = () => import(/* webpackChunkName: "monaco-editor" */ "@kie-tooling-core/monaco-editor");

export enum AlertTypes {
  NONE,
  COPY,
  SUCCESS_UPDATE_GIST,
  SUCCESS_UPDATE_GIST_FILENAME,
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

export function EditorPage() {
  const globals = useGlobals();
  const settings = useSettings();
  const location = useLocation();
  const { editor, editorRef } = useEditorRef();
  const downloadRef = useRef<HTMLAnchorElement>(null);
  const downloadPreviewRef = useRef<HTMLAnchorElement>(null);
  const copyContentTextArea = useRef<HTMLTextAreaElement>(null);
  const [isEditorReady, setIsEditorReady] = useState(false);
  const [fullscreen, setFullscreen] = useState(false);
  const [updateGistFilenameUrl, setUpdateGistFilenameUrl] = useState("");
  const [openAlert, setOpenAlert] = useState(AlertTypes.NONE);
  const [openModalType, setOpenModalType] = useState(ModalType.NONE);
  const isDirty = useDirtyState(editor);
  const { locale, i18n } = useOnlineI18n();
  const textEditorContainerRef = useRef<HTMLDivElement>(null);
  const history = useHistory();
  const queryParams = useQueryParams();

  useEffect(() => {
    if (globals.externalFile) {
      globals.setFile(globals.externalFile);
    }
  }, [globals]);

  useEffect(() => {
    const filePath = queryParams.get(QueryParams.FILE)!;
    const readonly = queryParams.has(QueryParams.READONLY)
      ? queryParams.get(QueryParams.READONLY) === `${true}`
      : false;

    if (!filePath || !isEditorReady) {
      return;
    }

    if (settings.github.service.isGist(filePath)) {
      settings.github.service
        .fetchGistFile(settings.github.octokit, filePath)
        .then((content) =>
          globals.setFile(
            getFileToOpen({
              filePath: filePath,
              readonly: readonly,
              getFileContent: Promise.resolve(content),
            })
          )
        )
        .catch((error) => {
          //FIXME: tiago
          console.info("error");
        });
    } else if (settings.github.service.isGithub(filePath) || settings.github.service.isGithubRaw(filePath)) {
      settings.github.service
        .fetchGithubFile(settings.github.octokit, filePath)
        .then((response) => {
          globals.setFile(
            getFileToOpen({
              filePath: filePath,
              readonly: readonly,
              getFileContent: Promise.resolve(response),
            })
          );
        })
        .catch((error) => {
          //FIXME: tiago
          console.info("error");
        });
    } else {
      fetch(filePath)
        .then((response) => {
          if (response.ok) {
            globals.setFile(
              getFileToOpen({
                filePath: filePath,
                readonly: readonly,
                getFileContent: response.text(),
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
    }
  }, [isEditorReady]);

  const close = useCallback(() => {
    if (!isDirty) {
      history.push(globals.routes.home.url({}));
    } else {
      setOpenAlert(AlertTypes.UNSAVED);
    }
  }, [globals, history, isDirty]);

  const closeWithoutSaving = useCallback(() => {
    setOpenAlert(AlertTypes.NONE);
    history.push(globals.routes.home.url({}));
  }, [globals, history]);

  const requestSave = useCallback(() => {
    editor?.getContent().then((content) => {
      window.dispatchEvent(
        new CustomEvent("saveOnlineEditor", {
          detail: {
            fileName: `${globals.file.fileName}.${globals.file.fileExtension}`,
            fileContent: content,
            senderTabId: globals.senderTabId!,
          },
        })
      );
    });
  }, [globals.file.fileName, editor]);

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

  const fileUrl = useFileUrl();

  const requestGistIt = useCallback(async () => {
    if (editor) {
      const content = await editor.getContent();

      // update gist
      if (fileUrl && settings.github.service.isGist(fileUrl)) {
        const userLogin = settings.github.service.extractUserLoginFromFileUrl(fileUrl);
        if (userLogin === settings.github.user) {
          try {
            const filename = `${globals.file.fileName}.${globals.file.fileExtension}`;
            const updateResponse = await settings.github.service.updateGist(settings.github.octokit, {
              filename,
              content,
            });

            if (updateResponse === UpdateGistErrors.INVALID_CURRENT_GIST) {
              setOpenAlert(AlertTypes.INVALID_CURRENT_GIST);
              return;
            }

            if (updateResponse === UpdateGistErrors.INVALID_GIST_FILENAME) {
              setOpenAlert(AlertTypes.INVALID_GIST_FILENAME);
              return;
            }

            editor.getStateControl().setSavedCommand();
            if (filename !== settings.github.service.getCurrentGist()?.filename) {
              setUpdateGistFilenameUrl(
                `${window.location.origin}${window.location.pathname}?${QueryParams.FILE}=${updateResponse}`
              );
              setOpenAlert(AlertTypes.SUCCESS_UPDATE_GIST_FILENAME);
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
          filename: `${globals.file.fileName}.${globals.file.fileExtension}`,
          content: content,
          description: `${globals.file.fileName}.${globals.file.fileExtension}`,
          isPublic: true,
        });

        setOpenAlert(AlertTypes.NONE);
        history.push({
          pathname: globals.routes.editor.url({ type: fileExtension }),
          search: `?${QueryParams.FILE}=${newGistUrl}`,
        });
        return;
      } catch (err) {
        console.error(err);
        setOpenAlert(AlertTypes.ERROR);
        return;
      }
    }
  }, [fileUrl, globals, editor]);

  const fileExtension = useMemo(() => {
    return globals.routes.editor.args(location.pathname).type;
  }, [globals.routes, location.pathname]);

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
    setFullscreen(!fullscreen);
  }, [fullscreen]);

  const onReady = useCallback(() => setIsEditorReady(true), []);

  useEffect(() => {
    if (downloadRef.current) {
      downloadRef.current.download = `${globals.file.fileName}.${globals.file.fileExtension}`;
    }
    if (downloadPreviewRef.current) {
      const fileName = globals.file.fileName;
      downloadPreviewRef.current.download = `${fileName}-svg.svg`;
    }
  }, [globals.file.fileName]);

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

  useDmnTour(!globals.readonly && isEditorReady && openAlert === AlertTypes.NONE, globals.file);

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
  }, [editor]);

  const [textEditorContent, setTextEditorContext] = useState<string | undefined>(undefined);

  useEffect(() => {
    globals.file.getFileContents().then((content) => {
      setTextEditorContext(content);
    });
  }, [globals.file]);

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
        ?.setContent(globals.file.fileName, contentAfterFix)
        .then(() => {
          editor?.getStateControl().updateCommandStack({
            id: "fix-from-text-editor",
            undo: () => {
              editor?.setContent(globals.file.fileName, textEditorContent!);
            },
            redo: () => {
              editor?.setContent(globals.file.fileName, contentAfterFix).then(() => setOpenAlert(AlertTypes.NONE));
            },
          });
        })
        .catch(() => {
          setTextEditorContext(contentAfterFix);
        });
    };
  }, [openModalType, editor, globals.file, textEditorContent]);

  const notificationsPanelRef = useRef<NotificationsPanelContextType>(null);

  const notificationPanelTabNames = useCallback(
    (dmnRunnerStatus: DmnRunnerStatus) => {
      if (globals.file.fileExtension === "dmn" && globals.isChrome && dmnRunnerStatus === DmnRunnerStatus.AVAILABLE) {
        return [i18n.terms.validation, i18n.terms.execution];
      }
      return [i18n.terms.validation];
    },
    [globals.file.fileExtension, globals.isChrome, i18n]
  );

  useEffect(() => {
    if (!editor) {
      return;
    }

    const validate = () => {
      editor.validate().then((notifications) => {
        if (!Array.isArray(notifications)) {
          notifications = [];
        }
        notificationsPanelRef.current?.getTabRef("Validation")?.kogitoNotifications_setNotifications("", notifications);
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
  }, [editor, isEditorReady]);

  return (
    <KieToolingExtendedServicesContextProvider>
      <NotificationsPanelContextProvider ref={notificationsPanelRef}>
        <DmnRunnerContextProvider editor={editor} isEditorReady={isEditorReady}>
          <DmnRunnerContext.Consumer>
            {(dmnRunner) => (
              <DmnDevSandboxContextProvider editor={editor} isEditorReady={isEditorReady}>
                <Page
                  header={
                    <EditorToolbar
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
                  <PageSection
                    isFilled={true}
                    padding={{ default: "noPadding" }}
                    className={"kogito--editor__page-section"}
                  >
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
                          {!fullscreen && openAlert === AlertTypes.SUCCESS_UPDATE_GIST_FILENAME && (
                            <div className={"kogito--alert-container"}>
                              <Alert
                                className={"kogito--alert"}
                                variant="warning"
                                title={i18n.editorPage.alerts.updateGistFilename.title}
                                actionClose={<AlertActionCloseButton onClose={closeAlert} />}
                              >
                                <p>{i18n.editorPage.alerts.updateGistFilename.message}</p>
                                <p>{i18n.editorPage.alerts.updateGistFilename.yourNewUrl}:</p>
                                <p>{updateGistFilenameUrl}</p>
                              </Alert>
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
                                  <AlertActionCloseButton
                                    data-testid="unsaved-alert-close-button"
                                    onClose={closeAlert}
                                  />
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
                              isOpen={openModalType === ModalType.EMBED}
                              onClose={closeModal}
                              editor={editor}
                              fileExtension={fileExtension}
                            />
                          )}
                          {fullscreen && <FullScreenToolbar onExitFullScreen={exitFullscreen} />}
                          <EmbeddedEditor
                            ref={editorRef}
                            file={globals.file}
                            kogitoEditor_ready={onReady}
                            kogitoEditor_setContentError={onSetContentError}
                            editorEnvelopeLocator={globals.editorEnvelopeLocator}
                            channelType={ChannelType.ONLINE}
                            locale={locale}
                          />
                          <Modal
                            showClose={false}
                            width={"100%"}
                            height={"100%"}
                            title={i18n.editorPage.textEditorModal.title(globals.file.fileName.split("/").pop()!)}
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
                  </PageSection>
                  <textarea ref={copyContentTextArea} style={{ height: 0, position: "absolute", zIndex: -1 }} />
                  <a ref={downloadRef} />
                  <a ref={downloadPreviewRef} />
                </Page>
              </DmnDevSandboxContextProvider>
            )}
          </DmnRunnerContext.Consumer>
        </DmnRunnerContextProvider>
      </NotificationsPanelContextProvider>
    </KieToolingExtendedServicesContextProvider>
  );
}

function getFileToOpen(args: { filePath: string; readonly: boolean; getFileContent: Promise<string> }) {
  return {
    isReadOnly: args.readonly,
    fileExtension: extractFileExtension(removeDirectories(args.filePath) ?? "")!,
    fileName: removeFileExtension(removeDirectories(args.filePath) ?? ""),
    getFileContents: () => args.getFileContent,
  };
}
