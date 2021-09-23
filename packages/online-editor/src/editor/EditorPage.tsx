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
import { EditorToolbar } from "./EditorToolbar";
import { useDmnTour } from "../tour";
import { useOnlineI18n } from "../common/i18n";
import { ChannelType } from "@kie-tooling-core/editor/dist/api";
import {
  EmbeddedEditor,
  EmbeddedEditorRef,
  useDirtyState,
  useStateControlSubscription,
} from "@kie-tooling-core/editor/dist/embedded";
import { DmnRunnerContext } from "./DmnRunner/DmnRunnerContext";
import { DmnRunnerContextProvider } from "./DmnRunner/DmnRunnerContextProvider";
import { NotificationsPanel } from "./NotificationsPanel/NotificationsPanel";
import { DmnRunnerStatus } from "./DmnRunner/DmnRunnerStatus";
import { NotificationsPanelContextProvider } from "./NotificationsPanel/NotificationsPanelContextProvider";
import { NotificationsPanelContextType } from "./NotificationsPanel/NotificationsPanelContext";
import { Alert, AlertActionCloseButton, AlertActionLink } from "@patternfly/react-core/dist/js/components/Alert";
import { Page, PageSection } from "@patternfly/react-core/dist/js/components/Page";
import { DmnDevSandboxContextProvider } from "./DmnDevSandbox/DmnDevSandboxContextProvider";
import { useQueryParams } from "../queryParams/QueryParamsContext";
import { extractFileExtension, removeDirectories, removeFileExtension } from "../common/utils";
import { useSettings } from "../settings/SettingsContext";
import { File } from "@kie-tooling-core/editor/dist/channel";
import { QueryParams } from "../common/Routes";
import { EditorFetchFileErrorEmptyState, FetchFileError, FetchFileErrorReason } from "./EditorFetchFileErrorEmptyState";
import { MonacoEditorModal } from "./Monaco/MonacoEditorModal";
import { DmnRunnerDrawer } from "./DmnRunner/DmnRunnerDrawer";
import { Alerts, AlertsController, useAlert } from "./Alerts/Alerts";
import { useController } from "../common/Hooks";

export function EditorPage(props: { forExtension: SupportedFileExtensions }) {
  const globals = useGlobals();
  const settings = useSettings();
  const history = useHistory();
  const queryParams = useQueryParams();
  const { locale, i18n } = useOnlineI18n();
  const [editor, editorRef] = useController<EmbeddedEditorRef>();
  const [alerts, alertsRef] = useController<AlertsController>();
  const notificationsPanelRef = useRef<NotificationsPanelContextType>(null);
  const downloadRef = useRef<HTMLAnchorElement>(null);
  const isDirty = useDirtyState(editor);
  const [isTextEditorModalOpen, setTextEditorModalOpen] = useState(false);
  const [fetchFileError, setFetchFileError] = useState<FetchFileError | undefined>(undefined);

  const queryParamFile = useMemo(() => {
    return queryParams.get(QueryParams.FILE);
  }, [queryParams]);

  const queryParamReadonly = useMemo(() => {
    return queryParams.has(QueryParams.READONLY) ? queryParams.get(QueryParams.READONLY) === `${true}` : false;
  }, [queryParams]);

  const [currentFile, setCurrentFile] = useState<File>(() => ({
    fileName: "new-file",
    fileExtension: props.forExtension,
    getFileContents: () => Promise.resolve(""),
    isReadOnly: false,
    kind: "local",
  }));

  useEffect(() => {
    alerts?.closeAll();
  }, [alerts]);

  useDmnTour(!currentFile.isReadOnly && !editor?.isReady && currentFile.fileExtension === "dmn");

  useEffect(() => {
    let canceled = false;

    setFetchFileError(undefined);

    if (globals.externalFile) {
      setCurrentFile({ ...globals.externalFile, kind: "external" });
      return;
    }

    if (globals.uploadedFile) {
      setCurrentFile({ ...globals.uploadedFile, kind: "local" });
      return;
    }

    if (!queryParamFile) {
      return;
    }

    const filePathExtension = extractFileExtension(queryParamFile);
    if (!filePathExtension) {
      return;
    }

    if (filePathExtension !== props.forExtension) {
      setFetchFileError({ reason: FetchFileErrorReason.DIFFERENT_EXTENSION, filePath: queryParamFile });
      return;
    }

    const extractedFileName = removeFileExtension(removeDirectories(queryParamFile) ?? "unknown");

    if (settings.github.service.isGist(queryParamFile)) {
      settings.github.service
        .fetchGistFile(settings.github.octokit, queryParamFile)
        .then((content) => {
          if (canceled) {
            return;
          }

          setCurrentFile({
            kind: "gist",
            isReadOnly: queryParamReadonly,
            fileExtension: filePathExtension,
            fileName: extractedFileName,
            getFileContents: () => Promise.resolve(content),
          });
        })
        .catch((error) =>
          setFetchFileError({ details: error, reason: FetchFileErrorReason.CANT_FETCH, filePath: queryParamFile })
        );
      return;
    }

    if (settings.github.service.isGithub(queryParamFile) || settings.github.service.isGithubRaw(queryParamFile)) {
      settings.github.service
        .fetchGithubFile(settings.github.octokit, queryParamFile)
        .then((response) => {
          if (canceled) {
            return;
          }

          setCurrentFile({
            kind: "external",
            isReadOnly: queryParamReadonly,
            fileExtension: filePathExtension,
            fileName: extractedFileName,
            getFileContents: () => Promise.resolve(response),
          });
        })
        .catch((error) => {
          setFetchFileError({ details: error, reason: FetchFileErrorReason.CANT_FETCH, filePath: queryParamFile });
        });
      return;
    }

    fetch(queryParamFile)
      .then((response) => {
        if (canceled) {
          return;
        }

        if (!response.ok) {
          setFetchFileError({
            details: `${response.status} - ${response.statusText}`,
            reason: FetchFileErrorReason.CANT_FETCH,
            filePath: queryParamFile,
          });
          return;
        }

        // do not inline this variable.
        const content = response.text();

        setCurrentFile({
          kind: "external",
          isReadOnly: queryParamReadonly,
          fileExtension: filePathExtension,
          fileName: extractedFileName,
          getFileContents: () => content,
        });
      })
      .catch((error) => {
        setFetchFileError({ details: error, reason: FetchFileErrorReason.CANT_FETCH, filePath: queryParamFile });
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
    globals.routes.editor,
    history,
  ]);

  const setContentErrorAlert = useAlert(
    alerts,
    useCallback(() => {
      return (
        <div className={"kogito--alert-container"}>
          <Alert
            ouiaId="invalid-content-alert"
            variant="danger"
            title={i18n.editorPage.alerts.setContentError.title}
            actionLinks={
              <AlertActionLink data-testid="unsaved-alert-save-button" onClick={() => setTextEditorModalOpen(true)}>
                {i18n.editorPage.alerts.setContentError.action}
              </AlertActionLink>
            }
          />
        </div>
      );
    }, [i18n])
  );

  const closeWithoutSaving = useCallback(() => {
    history.push({ pathname: globals.routes.home.path({}) });
  }, [globals, history]);

  const requestDownload = useCallback(() => {
    editor?.getStateControl().setSavedCommand();
    editor
      ?.getContent()
      .then((content) => {
        if (downloadRef.current) {
          const fileBlob = new Blob([content], { type: "text/plain" });
          downloadRef.current.href = URL.createObjectURL(fileBlob);
          downloadRef.current.click();
        }
      })
      .then(() => {
        history.push({ pathname: globals.routes.home.path({}) });
      });
  }, [history, globals, editor]);

  useEffect(() => {
    if (downloadRef.current) {
      downloadRef.current.download = `${currentFile.fileName}.${currentFile.fileExtension}`;
    }
  }, [currentFile]);

  const unsavedAlert = useAlert(
    alerts,
    useCallback(
      ({ close }) => (
        <div className={"kogito--alert-container"} data-testid="unsaved-alert">
          <Alert
            className={"kogito--alert"}
            variant="warning"
            title={i18n.editorPage.alerts.unsaved.title}
            actionClose={<AlertActionCloseButton data-testid="unsaved-alert-close-button" onClose={close} />}
            actionLinks={
              <>
                <AlertActionLink data-testid="unsaved-alert-save-button" onClick={requestDownload}>
                  {i18n.terms.save}
                </AlertActionLink>
                <AlertActionLink data-testid="unsaved-alert-close-without-save-button" onClick={closeWithoutSaving}>
                  {i18n.editorPage.alerts.unsaved.closeWithoutSaving}
                </AlertActionLink>
              </>
            }
          >
            <p>{i18n.editorPage.alerts.unsaved.message}</p>
          </Alert>
        </div>
      ),
      [i18n, requestDownload, closeWithoutSaving]
    )
  );

  const onClose = useCallback(() => {
    if (isDirty) {
      unsavedAlert.show();
      return;
    }

    history.push({ pathname: globals.routes.home.path({}) });
  }, [unsavedAlert, globals, history, isDirty]);

  const refreshDiagramEditor = useCallback(() => {
    alerts?.closeAll();
    setTextEditorModalOpen(false);
  }, [alerts]);

  const notificationPanelTabNames = useCallback(
    (dmnRunnerStatus: DmnRunnerStatus) => {
      if (currentFile.fileExtension === "dmn" && globals.isChrome && dmnRunnerStatus === DmnRunnerStatus.AVAILABLE) {
        return [i18n.terms.validation, i18n.terms.execution];
      }
      return [i18n.terms.validation];
    },
    [currentFile.fileExtension, globals.isChrome, i18n]
  );

  const validate = useCallback(() => {
    editor?.validate().then((notifications) => {
      notificationsPanelRef.current
        ?.getTabRef(i18n.terms.validation)
        ?.kogitoNotifications_setNotifications("", Array.isArray(notifications) ? notifications : []);
    });
  }, [editor, i18n]);

  useStateControlSubscription(editor, validate, { throttle: 200 });

  return (
    <>
      {fetchFileError && <EditorFetchFileErrorEmptyState fetchFileError={fetchFileError} currentFile={currentFile} />}
      {!fetchFileError && (
        <NotificationsPanelContextProvider ref={notificationsPanelRef}>
          <DmnRunnerContextProvider currentFile={currentFile} editor={editor}>
            <DmnDevSandboxContextProvider currentFile={currentFile} editor={editor}>
              <Page
                header={
                  <EditorToolbar
                    editor={editor}
                    alerts={alerts}
                    currentFile={currentFile}
                    onRename={(newName) => setCurrentFile((prev) => ({ ...prev, fileName: newName }))}
                    onClose={onClose}
                  />
                }
              >
                <PageSection
                  isFilled={true}
                  padding={{ default: "noPadding" }}
                  className={"kogito--editor__page-section"}
                >
                  <DmnRunnerDrawer editor={editor}>
                    <Alerts ref={alertsRef} />
                    <EmbeddedEditor
                      ref={editorRef}
                      file={currentFile}
                      kogitoEditor_setContentError={setContentErrorAlert.show}
                      editorEnvelopeLocator={globals.editorEnvelopeLocator}
                      channelType={ChannelType.ONLINE}
                      locale={locale}
                    />
                    <DmnRunnerContext.Consumer>
                      {(dmnRunner) => <NotificationsPanel tabNames={notificationPanelTabNames(dmnRunner.status)} />}
                    </DmnRunnerContext.Consumer>
                  </DmnRunnerDrawer>
                </PageSection>
                <a ref={downloadRef} />
              </Page>
              <MonacoEditorModal
                alerts={alerts}
                editor={editor}
                currentFile={currentFile}
                refreshDiagramEditor={refreshDiagramEditor}
                isOpen={isTextEditorModalOpen}
              />
            </DmnDevSandboxContextProvider>
          </DmnRunnerContextProvider>
        </NotificationsPanelContextProvider>
      )}
    </>
  );
}
