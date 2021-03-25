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
import { useCallback, useContext, useEffect, useLayoutEffect, useMemo, useRef, useState } from "react";
import { useLocation } from "react-router";
import { GithubTokenModal } from "../common/GithubTokenModal";
import { GlobalContext } from "../common/GlobalContext";
import { FullScreenToolbar } from "./EditorFullScreenToolbar";
import { EditorToolbar } from "./EditorToolbar";
import { useDmnTour } from "../tour";
import { useOnlineI18n } from "../common/i18n";
import { UpdateGistErrors } from "../common/GithubService";
import { EmbedModal } from "./EmbedModal";
import { useFileUrl } from "../common/Hooks";
import { ChannelType } from "@kogito-tooling/channel-common-api";
import { EmbeddedEditor, useDirtyState, useEditorRef } from "@kogito-tooling/editor/dist/embedded";
import {
  Alert,
  AlertActionCloseButton,
  AlertActionLink,
  Drawer,
  DrawerContent,
  DrawerContentBody,
  DrawerPanelContent,
  DrawerPanelBody,
  Page,
  PageSection
} from "@patternfly/react-core";
import { DmnRunner } from "../common/DmnRunner";
import { DmnRunnerDrawer } from "./DmnRunnerDrawer";
import { DmnRunnerModal } from "./DmnRunnerModal";
import JSONSchemaBridge from "../common/Bridge";

export enum Alerts {
  NONE,
  COPY,
  SUCCESS_UPDATE_GIST,
  SUCCESS_UPDATE_GIST_FILENAME,
  INVALID_CURRENT_GIST,
  INVALID_GIST_FILENAME,
  UNSAVED,
  SUCCESS_DMN_RUNNER,
  ERROR
}

export enum OpenedModal {
  NONE,
  GITHUB_TOKEN,
  EMBED,
  DMN_RUNNER_HELPER
}

enum DmnRunnerStatus {
  DISABLED,
  AVAILABLE,
  RUNNING,
  NOT_RUNNING,
  STOPPED
}

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
  const [updateGistFilenameUrl, setUpdateGistFilenameUrl] = useState("");
  const [alert, setAlert] = useState(Alerts.NONE);
  const [modal, setModal] = useState(OpenedModal.NONE);
  const isDirty = useDirtyState(editor);
  const { locale, i18n } = useOnlineI18n();

  const close = useCallback(() => {
    if (!isDirty) {
      window.location.href = window.location.href.split("?")[0].split("#")[0];
    } else {
      setAlert(Alerts.UNSAVED);
    }
  }, [isDirty]);

  const closeWithoutSaving = useCallback(() => {
    setAlert(Alerts.NONE);
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
    setAlert(Alerts.NONE);
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

  const fileUrl = useFileUrl();

  const requestGistIt = useCallback(async () => {
    if (editor) {
      const content = await editor.getContent();

      // update gist
      if (fileUrl && context.githubService.isGist(fileUrl)) {
        const userLogin = context.githubService.extractUserLoginFromFileUrl(fileUrl);
        if (userLogin === context.githubService.getLogin()) {
          try {
            const filename = `${context.file.fileName}.${context.file.fileExtension}`;
            const updateResponse = await context.githubService.updateGist({ filename, content });

            if (updateResponse === UpdateGistErrors.INVALID_CURRENT_GIST) {
              setAlert(Alerts.INVALID_CURRENT_GIST);
              return;
            }

            if (updateResponse === UpdateGistErrors.INVALID_GIST_FILENAME) {
              setAlert(Alerts.INVALID_GIST_FILENAME);
              return;
            }

            editor.getStateControl().setSavedCommand();
            if (filename !== context.githubService.getCurrentGist()?.filename) {
              // FIXME: KOGITO-1202
              setUpdateGistFilenameUrl(
                `${window.location.origin}${window.location.pathname}?file=${updateResponse}#/editor/${fileExtension}`
              );
              setAlert(Alerts.SUCCESS_UPDATE_GIST_FILENAME);
              return;
            }

            setAlert(Alerts.SUCCESS_UPDATE_GIST);
            return;
          } catch (err) {
            console.error(err);
            setAlert(Alerts.ERROR);
            return;
          }
        }
      }

      // create gist
      try {
        const newGistUrl = await context.githubService.createGist({
          filename: `${context.file.fileName}.${context.file.fileExtension}`,
          content: content,
          description: `${context.file.fileName}.${context.file.fileExtension}`,
          isPublic: true
        });

        setAlert(Alerts.NONE);
        // FIXME: KOGITO-1202
        window.location.href = `?file=${newGistUrl}#/editor/${fileExtension}`;
        return;
      } catch (err) {
        console.error(err);
        setAlert(Alerts.ERROR);
        return;
      }
    }
  }, [fileUrl, context, editor]);

  const fileExtension = useMemo(() => {
    return context.routes.editor.args(location.pathname).type;
  }, [location.pathname]);

  const requestSetGitHubToken = useCallback(() => {
    setModal(OpenedModal.GITHUB_TOKEN);
  }, []);

  const requestEmbed = useCallback(() => {
    setModal(OpenedModal.EMBED);
  }, []);

  const closeModal = useCallback(() => {
    setModal(OpenedModal.NONE);
  }, []);

  const requestCopyContentToClipboard = useCallback(() => {
    editor?.getContent().then(content => {
      if (copyContentTextArea.current) {
        copyContentTextArea.current.value = content;
        copyContentTextArea.current.select();
        if (document.execCommand("copy")) {
          setAlert(Alerts.COPY);
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
      downloadRef.current.download = `${context.file.fileName}.${context.file.fileExtension}`;
    }
    if (downloadPreviewRef.current) {
      const fileName = context.file.fileName;
      downloadPreviewRef.current.download = `${fileName}-svg.svg`;
    }
  }, [context.file.fileName]);

  // TODO: extract to custom hook
  const [isDmnRunnerDrawerOpen, setDmnRunnerDrawerOpen] = useState(false);
  const [dmnRunnerJsonSchemaBridge, setDmnRunnerJsonSchemaBridge] = useState<JSONSchemaBridge>();

  // This state saves the current status of the Dmn Runner server on the user machine.
  const [dmnRunnerStatus, setDmnRunnerStatus] = useState(DmnRunnerStatus.DISABLED);
  const DMN_RUNNER_POLLING_TIME = 500;

  const setJsonSchemaBridge = useCallback(
    () =>
      editor
        ?.getContent()
        .then(content => DmnRunner.getJsonSchemaBridge(content ?? ""))
        .then(jsonSchemaBridge => setDmnRunnerJsonSchemaBridge(jsonSchemaBridge)),
    [editor]
  );

  useEffect(() => {
    if (context.file.fileExtension === "dmn") {
      // detect dmn runner
      let polling2: number | undefined;
      if (dmnRunnerStatus !== DmnRunnerStatus.RUNNING) {
        polling2 = window.setInterval(() => {
          DmnRunner.checkServer().then(() => {
            setDmnRunnerStatus(DmnRunnerStatus.RUNNING);
            if (modal === OpenedModal.DMN_RUNNER_HELPER) {
              setDmnRunnerDrawerOpen(true);
            }
            window.clearInterval(polling2);
          });
        }, DMN_RUNNER_POLLING_TIME);

        return () => window.clearInterval(polling2);
      }

      // check crashes / manual stops
      let polling1: number | undefined;
      if (dmnRunnerStatus === DmnRunnerStatus.RUNNING) {
        polling1 = window.setInterval(() => {
          DmnRunner.checkServer().catch(() => {
            setModal(OpenedModal.DMN_RUNNER_HELPER);
            setDmnRunnerStatus(DmnRunnerStatus.STOPPED);
            window.clearInterval(polling1);
          });
        }, DMN_RUNNER_POLLING_TIME);

        setJsonSchemaBridge();

        return () => window.clearInterval(polling1);
      }
    }
  }, [editor, dmnRunnerStatus, isEditorReady]);

  useEffect(() => {
    if (!editor || context.file.fileExtension !== "dmn") {
      return;
    }

    const subscription = editor.getStateControl().subscribe(() => setJsonSchemaBridge());

    return () => {
      editor.getStateControl().unsubscribe(subscription);
    };
  }, [editor, context.file.fileExtension, setJsonSchemaBridge]);

  const requestSetupDmnRunner = useCallback(() => {
    setModal(OpenedModal.DMN_RUNNER_HELPER);
  }, []);

  const requestCloseDmnRunnerDrawer = useCallback(() => {
    setDmnRunnerDrawerOpen(false);
  }, []);

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

  const closeAlert = useCallback(() => setAlert(Alerts.NONE), []);

  const [dmnRunnerFlexDirection, setDmnRunnerFlexDirection] = useState<"row" | "column">("row");
  const handlePanelContentResize = useCallback((width: number) => {
    const iframe = document.getElementById("kogito-iframe");
    if (iframe) {
      iframe.style.pointerEvents = "visible";
    }

    // FIXME: Patternfly bug. The first interaction without resizing the splitter will result in width === 0.
    if (width === 0) {
      return;
    }

    if (width > 711) {
      setDmnRunnerFlexDirection("row");
    } else {
      setDmnRunnerFlexDirection("column");
    }
  }, []);

  useEffect(() => {
    if (window.innerWidth < 1200) {
      setDmnRunnerFlexDirection("column");
    }
  }, []);

  const closeDmnRunnerModal = useCallback(() => {
    if (dmnRunnerStatus === DmnRunnerStatus.STOPPED) {
      setDmnRunnerStatus(DmnRunnerStatus.NOT_RUNNING);
    }
    closeModal();
  }, [dmnRunnerStatus, closeModal]);

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
          onGistIt={requestGistIt}
          onEmbed={requestEmbed}
          isEdited={isDirty}
          isDmnRunning={dmnRunnerStatus === DmnRunnerStatus.RUNNING}
          setDmnRunnerDrawerOpen={setDmnRunnerDrawerOpen}
          onSetupDmnRunner={requestSetupDmnRunner}
        />
      }
    >
      <PageSection isFilled={true} padding={{ default: "noPadding" }} className={"kogito--editor__page-section"}>
        <Drawer isInline={true} isExpanded={isDmnRunnerDrawerOpen}>
          <DrawerContent
            className={
              !isDmnRunnerDrawerOpen ? "kogito--editor__drawer-content-close" : "kogito--editor__drawer-content-open"
            }
            panelContent={
              <DrawerPanelContent
                id={"kogito-panel-content"}
                className={"kogito--editor__drawer-content-panel"}
                defaultSize={"711px"}
                onResize={handlePanelContentResize}
                isResizable={true}
              >
                <DmnRunnerDrawer
                  editor={editor}
                  jsonSchemaBridge={dmnRunnerJsonSchemaBridge}
                  onStopRunDmn={requestCloseDmnRunnerDrawer}
                  flexDirection={dmnRunnerFlexDirection}
                />
              </DrawerPanelContent>
            }
          >
            <DrawerContentBody className={"kogito--editor__drawer-content-body"}>
              {!fullscreen && alert === Alerts.COPY && (
                <div className={"kogito--alert-container"}>
                  <Alert
                    className={"kogito--alert"}
                    variant="success"
                    title={i18n.editorPage.alerts.copy}
                    actionClose={<AlertActionCloseButton onClose={closeAlert} />}
                  />
                </div>
              )}
              {!fullscreen && alert === Alerts.SUCCESS_UPDATE_GIST && (
                <div className={"kogito--alert-container"}>
                  <Alert
                    className={"kogito--alert"}
                    variant="success"
                    title={i18n.editorPage.alerts.updateGist}
                    actionClose={<AlertActionCloseButton onClose={closeAlert} />}
                  />
                </div>
              )}
              {!fullscreen && alert === Alerts.SUCCESS_UPDATE_GIST_FILENAME && (
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
              {!fullscreen && alert === Alerts.SUCCESS_DMN_RUNNER && (
                <div className={"kogito--alert-container"}>
                  <Alert
                    className={"kogito--alert"}
                    variant="success"
                    title={"Success connecting with DMN Runner"}
                    actionClose={<AlertActionCloseButton onClose={closeAlert} />}
                  />
                </div>
              )}
              {!fullscreen && alert === Alerts.INVALID_CURRENT_GIST && (
                <div className={"kogito--alert-container"}>
                  <Alert
                    className={"kogito--alert"}
                    variant="danger"
                    title={i18n.editorPage.alerts.invalidCurrentGist}
                    actionClose={<AlertActionCloseButton onClose={closeAlert} />}
                  />
                </div>
              )}
              {!fullscreen && alert === Alerts.INVALID_GIST_FILENAME && (
                <div className={"kogito--alert-container"}>
                  <Alert
                    className={"kogito--alert"}
                    variant="danger"
                    title={i18n.editorPage.alerts.invalidGistFilename}
                    actionClose={<AlertActionCloseButton onClose={closeAlert} />}
                  />
                </div>
              )}
              {!fullscreen && alert === Alerts.ERROR && (
                <div className={"kogito--alert-container"}>
                  <Alert
                    className={"kogito--alert"}
                    variant="danger"
                    title={i18n.editorPage.alerts.error}
                    actionClose={<AlertActionCloseButton onClose={closeAlert} />}
                  />
                </div>
              )}
              {!fullscreen && alert === Alerts.UNSAVED && (
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
                <DmnRunnerModal
                  stopped={dmnRunnerStatus === DmnRunnerStatus.STOPPED}
                  isDmnRunning={dmnRunnerStatus === DmnRunnerStatus.RUNNING}
                  isOpen={modal === OpenedModal.DMN_RUNNER_HELPER}
                  onClose={closeDmnRunnerModal}
                />
              )}
              {!fullscreen && <GithubTokenModal isOpen={modal === OpenedModal.GITHUB_TOKEN} onClose={closeModal} />}
              {!fullscreen && (
                <EmbedModal
                  isOpen={modal === OpenedModal.EMBED}
                  onClose={closeModal}
                  editor={editor}
                  fileExtension={fileExtension}
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
            </DrawerContentBody>
          </DrawerContent>
        </Drawer>
      </PageSection>
      <textarea ref={copyContentTextArea} style={{ height: 0, position: "absolute", zIndex: -1 }} />
      <a ref={downloadRef} />
      <a ref={downloadPreviewRef} />
    </Page>
  );
}
