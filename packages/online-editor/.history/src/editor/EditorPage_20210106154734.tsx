window.Form = {
  "Credit Score": {
    "FICO": 501
  },
  "Applicant Data": {
    "Monthly": {}
  },
  "Requested Product": {}
}

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
import { Alert, AlertActionCloseButton, AlertActionLink, Page, PageSection } from "@patternfly/react-core";


export enum Alerts {
  NONE,
  COPY,
  SUCCESS_UPDATE_GIST,
  SUCCESS_UPDATE_GIST_FILENAME,
  INVALID_CURRENT_GIST,
  INVALID_GIST_FILENAME,
  UNSAVED,
  ERROR
}

export enum Modal {
  NONE,
  GITHUB_TOKEN,
  EMBED
}

interface Props {
  onFileNameChanged: (fileName: string, fileExtension: string) => void;
}

declare global {
  interface Window { Form: any; }
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
  const [modal, setModal] = useState(Modal.NONE);
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


  const runDMN = useCallback(async () => {

    if (editor) {
      console.log("Run DMN!");
      const model = await editor.getContent();


      const payload = {
        "model": model,
        // "context": {
        //   "Credit Score": {
        //     "FICO": 501
        //   },
        //   "Applicant Data": {
        //     "Monthly": {}
        //   },
        //   "Requested Product": {}
        // }
        "context": window.Form
      }

      const url = 'http://localhost:8080/jitdmn';



      fetch(url, {
        method: 'OPTIONS',
        // // mode: 'no-cors',
        // body: JSON.stringify(payload),
      })
        .then((data) => {
          console.log('Success:', data);
        })
        //Then with the error genereted...
        .catch((error) => {
          console.error('NOT INSTALLED');


          fetch('http://bit.ly/dmn-runner', {
            method: 'GET',
            // // mode: 'no-cors',
            // body: JSON.stringify(payload),
          })
            .then((data) => {
              console.log('Success:', data);
            })
            //Then with the error genereted...
            .catch((error) => {
              console.error('ERROR DOWNLOADING');
              
            });








        });



      fetch(url, {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
          'Accept': "application/json, text/plain, */*",
        },
        // mode: 'no-cors',
        body: JSON.stringify(payload),
      })
        .then((response) => response.json())
        .then((data) => {
          console.log('Success:', data);
        })
        //Then with the error genereted...
        .catch((error) => {
          console.error('Error:', error);
        });


    }
  }, [fileUrl, context, editor]);

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
    setModal(Modal.GITHUB_TOKEN);
  }, []);

  const requestEmbed = useCallback(() => {
    setModal(Modal.EMBED);
  }, []);

  const closeModal = useCallback(() => {
    setModal(Modal.NONE);
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
          onRun={runDMN}
          onGistIt={requestGistIt}
          onEmbed={requestEmbed}
          isEdited={isDirty}
        />
      }
    >
      <PageSection isFilled={true} padding={{ default: "noPadding" }} style={{ flexBasis: "100%" }}>
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
              actionClose={<AlertActionCloseButton data-testid="unsaved-alert-close-button" onClose={closeAlert} />}
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
        {!fullscreen && <GithubTokenModal isOpen={modal === Modal.GITHUB_TOKEN} onClose={closeModal} />}
        {!fullscreen && (
          <EmbedModal
            isOpen={modal === Modal.EMBED}
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
      </PageSection>
      <textarea ref={copyContentTextArea} style={{ height: 0, position: "absolute", zIndex: -1 }} />
      <a ref={downloadRef} />
      <a ref={downloadPreviewRef} />
    </Page>
  );
}
