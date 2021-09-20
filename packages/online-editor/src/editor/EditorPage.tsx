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
import { File, newFile } from "@kie-tooling-core/editor/dist/channel";

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

export function EditorPage(props: { forExtension?: SupportedFileExtensions }) {
  const globals = useGlobals();
  const settings = useSettings();
  const { editor, editorRef } = useEditorRef();
  const downloadRef = useRef<HTMLAnchorElement>(null);
  const downloadPreviewRef = useRef<HTMLAnchorElement>(null);
  const copyContentTextArea = useRef<HTMLTextAreaElement>(null);
  const [isEditorReady, setIsEditorReady] = useState(false);
  const [fullscreen, setFullscreen] = useState(false);
  const [openAlert, setOpenAlert] = useState(AlertTypes.NONE);
  const [openModalType, setOpenModalType] = useState(ModalType.NONE);
  const isDirty = useDirtyState(editor);
  const { locale, i18n } = useOnlineI18n();
  const textEditorContainerRef = useRef<HTMLDivElement>(null);
  const history = useHistory();
  const queryParams = useQueryParams();
  const fileExtension = props.forExtension!;

  const queryParamFile = useMemo(() => queryParams.get(QueryParams.FILE), [queryParams]);
  const queryParamReadonly = useMemo(
    () => (queryParams.has(QueryParams.READONLY) ? queryParams.get(QueryParams.READONLY) === `${true}` : false),
    [queryParams]
  );

  useEffect(() => {
    let canceled = false;
    if (globals.externalFile) {
      globals.setFile({ ...globals.externalFile, kind: "external" });
      return;
    }

    if (!queryParamFile && globals.uploadedFile) {
      globals.setFile(globals.uploadedFile);
      return;
    }

    if (!queryParamFile) {
      globals.setFile(newFile(fileExtension, "local"));
      return;
    }

    if (settings.github.service.isGist(queryParamFile)) {
      settings.github.service
        .fetchGistFile(settings.github.octokit, queryParamFile)
        .then((content) => {
          if (canceled) return;
          globals.setFile(
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
          globals.setFile(
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
          globals.setFile(
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
  }, [fileExtension, queryParamFile, queryParamReadonly, settings.github.octokit]);

  const close = useCallback(() => {
    if (!isDirty) {
      history.push({
        pathname: globals.routes.home(),
      });
    } else {
      setOpenAlert(AlertTypes.UNSAVED);
    }
  }, [globals, history, isDirty]);

  const closeWithoutSaving = useCallback(() => {
    setOpenAlert(AlertTypes.NONE);
    history.push({
      pathname: globals.routes.home(),
    });
  }, [globals, history]);

  const requestSave = useCallback(() => {
    editor?.getContent().then((content) => {
      window.dispatchEvent(
        new CustomEvent("saveOnlineEditor", {
          detail: {
            fileName: `${globals.file.fileName}.${fileExtension}`,
            fileContent: content,
            senderTabId: globals.senderTabId!,
          },
        })
      );
    });
  }, [fileExtension, globals.file, globals.senderTabId, editor]);

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
            const filename = `${globals.file.fileName}.${fileExtension}`;
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
              setOpenAlert(AlertTypes.SUCCESS_UPDATE_GIST);
              queryParams.set(QueryParams.FILE, updateResponse);
              history.push({
                pathname: globals.routes.editor({ extension: fileExtension }),
                search: decodeURIComponent(queryParams.toString()),
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
          filename: `${globals.file.fileName}.${fileExtension}`,
          content: content,
          description: `${globals.file.fileName}.${fileExtension}`,
          isPublic: true,
        });

        setOpenAlert(AlertTypes.SUCCESS_CREATE_GIST);
        queryParams.set(QueryParams.FILE, newGistUrl);
        history.push({
          pathname: globals.routes.editor({ extension: fileExtension }),
          search: decodeURIComponent(queryParams.toString()),
        });
        return;
      } catch (err) {
        console.error(err);
        setOpenAlert(AlertTypes.ERROR);
        return;
      }
    }
  }, [history, globals, settings, queryParamFile, queryParams, fileExtension, editor]);

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

  const onReady = useCallback(() => setIsEditorReady(true), []);

  useEffect(() => {
    if (downloadRef.current) {
      downloadRef.current.download = `${globals.file.fileName}.${fileExtension}`;
    }
    if (downloadPreviewRef.current) {
      downloadPreviewRef.current.download = `${globals.file.fileName}-svg.svg`;
    }
  }, [globals.file]);

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

  useDmnTour(!globals.file.isReadOnly && isEditorReady && openAlert === AlertTypes.NONE, globals.file);

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
      if (fileExtension === "dmn" && globals.isChrome && dmnRunnerStatus === DmnRunnerStatus.AVAILABLE) {
        return [i18n.terms.validation, i18n.terms.execution];
      }
      return [i18n.terms.validation];
    },
    [fileExtension, globals.isChrome, i18n]
  );

  useEffect(() => {
    if (!editor || !isEditorReady) {
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
        <DmnRunnerContextProvider editor={editor}>
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
