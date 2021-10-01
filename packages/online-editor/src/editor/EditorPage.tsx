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
import { NotificationsPanel, NotificationsPanelController } from "./NotificationsPanel/NotificationsPanel";
import { DmnRunnerStatus } from "./DmnRunner/DmnRunnerStatus";
import { Alert, AlertActionCloseButton, AlertActionLink } from "@patternfly/react-core/dist/js/components/Alert";
import { Page, PageSection } from "@patternfly/react-core/dist/js/components/Page";
import { DmnDevSandboxContextProvider } from "./DmnDevSandbox/DmnDevSandboxContextProvider";
import { useQueryParams } from "../queryParams/QueryParamsContext";
import { extractFileExtension, removeDirectories, removeFileExtension } from "../common/utils";
import { useSettings } from "../settings/SettingsContext";
import { EmbeddedEditorFile } from "@kie-tooling-core/editor/dist/channel";
import { QueryParams } from "../common/Routes";
import { EditorFetchFileErrorEmptyState, FetchFileError, FetchFileErrorReason } from "./EditorFetchFileErrorEmptyState";
import { DmnRunnerDrawer } from "./DmnRunner/DmnRunnerDrawer";
import { Alerts, AlertsController, useAlert } from "./Alerts/Alerts";
import { useCancelableEffect, useController } from "../common/Hooks";
import { TextEditorModal } from "./TextEditor/TextEditorModal";
import { useWorkspaces } from "../workspace/WorkspacesContext";
import { ResourceContentRequest, ResourceListRequest, ResourcesList } from "@kie-tooling-core/workspace/dist/api";
import { useWorkspace } from "../workspace/hooks/WorkspaceHooks";
import { useWorkspaceFile } from "../workspace/hooks/WorkspaceFileHooks";

export interface CommonProps {
  forExtension: SupportedFileExtensions;
}

export interface PropsWithSketch extends CommonProps {
  workspaceEnabled: false;
}

export interface PropsWithWorkspace extends CommonProps {
  workspaceEnabled: true;
  workspaceId: string;
  filePath: string;
}

export type Props = PropsWithSketch | PropsWithWorkspace;

export function EditorPage(props: Props) {
  const globals = useGlobals();
  const settings = useSettings();
  const history = useHistory();
  const queryParams = useQueryParams();
  const workspaces = useWorkspaces();
  const { locale, i18n } = useOnlineI18n();
  const [editor, editorRef] = useController<EmbeddedEditorRef>();
  const [alerts, alertsRef] = useController<AlertsController>();
  const [notificationsPanel, notificationsPanelRef] = useController<NotificationsPanelController>();
  const downloadRef = useRef<HTMLAnchorElement>(null);
  const isDirty = useDirtyState(editor);
  const [isTextEditorModalOpen, setTextEditorModalOpen] = useState(false);
  const [fetchFileError, setFetchFileError] = useState<FetchFileError | undefined>(undefined);

  const { workspace, addEmptyWorkspaceFile } = useWorkspace(props.workspaceEnabled ? props.workspaceId : undefined);

  const workspaceFile = useWorkspaceFile(
    workspace?.descriptor.workspaceId,
    props.workspaceEnabled ? props.filePath : undefined
  );

  const queryParamUrl = useMemo(() => {
    return queryParams.get(QueryParams.URL);
  }, [queryParams]);

  const queryParamReadonly = useMemo(() => {
    return queryParams.has(QueryParams.READONLY) ? queryParams.get(QueryParams.READONLY) === `${true}` : false;
  }, [queryParams]);

  const [currentFile, setCurrentFile] = useState<EmbeddedEditorFile>(() => ({
    fileName: "Untitled",
    fileExtension: props.forExtension,
    getFileContents: () => new Promise<string>(() => {}),
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
    alerts?.closeAll();

    if (props.workspaceEnabled) {
      return;
    }

    if (globals.externalFile) {
      setCurrentFile({ ...globals.externalFile, kind: "external" });
      return;
    }

    if (!queryParamUrl) {
      setCurrentFile({
        fileName: "Untitled",
        fileExtension: props.forExtension,
        getFileContents: () => Promise.resolve(""),
        isReadOnly: false,
        kind: "local",
      });
    }

    if (queryParamUrl) {
      const filePathExtension = extractFileExtension(queryParamUrl);
      if (!filePathExtension) {
        return;
      }
      if (filePathExtension !== props.forExtension) {
        setFetchFileError({ reason: FetchFileErrorReason.DIFFERENT_EXTENSION, filePath: queryParamUrl });
        return;
      }
      const extractedFileName = removeFileExtension(removeDirectories(queryParamUrl) ?? "unknown");
      if (settings.github.service.isGist(queryParamUrl)) {
        settings.github.service
          .fetchGistFile(settings.github.octokit, queryParamUrl)
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
            return;
          })
          .catch((error) =>
            setFetchFileError({ details: error, reason: FetchFileErrorReason.CANT_FETCH, filePath: queryParamUrl })
          );
        return;
      }
      if (settings.github.service.isGithub(queryParamUrl) || settings.github.service.isGithubRaw(queryParamUrl)) {
        settings.github.service
          .fetchGithubFile(settings.github.octokit, queryParamUrl)
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
            return;
          })
          .catch((error) => {
            setFetchFileError({ details: error, reason: FetchFileErrorReason.CANT_FETCH, filePath: queryParamUrl });
          });
        return;
      }
      fetch(queryParamUrl)
        .then((response) => {
          if (canceled) {
            return;
          }

          if (!response.ok) {
            setFetchFileError({
              details: `${response.status} - ${response.statusText}`,
              reason: FetchFileErrorReason.CANT_FETCH,
              filePath: queryParamUrl,
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
          return;
        })
        .catch((error) => {
          setFetchFileError({ details: error, reason: FetchFileErrorReason.CANT_FETCH, filePath: queryParamUrl });
        });
    }

    return () => {
      canceled = true;
    };
  }, [
    alerts,
    globals.externalFile,
    props,
    queryParamUrl,
    queryParamReadonly,
    settings.github.octokit,
    settings.github.service,
  ]);

  const setContentErrorAlert = useAlert(
    alerts,
    useCallback(() => {
      return (
        <Alert
          ouiaId="set-content-error-alert"
          variant="danger"
          title={i18n.editorPage.alerts.setContentError.title}
          actionLinks={
            <AlertActionLink data-testid="set-content-error-alert-button" onClick={() => setTextEditorModalOpen(true)}>
              {i18n.editorPage.alerts.setContentError.action}
            </AlertActionLink>
          }
        />
      );
    }, [i18n])
  );

  const closeWithoutSaving = useCallback(() => {
    history.push({ pathname: globals.routes.home.path({}) });
  }, [globals, history]);

  // keep the page in sync with the name of `workspaceFile`, even if changes
  useEffect(() => {
    if (!workspaceFile) {
      return;
    }

    history.replace({
      pathname: globals.routes.workspaceWithFilePath.path({
        workspaceId: workspaceFile.workspaceId,
        filePath: workspaceFile.pathRelativeToWorkspaceRootWithoutExtension,
        extension: workspaceFile.extension,
      }),
    });
  }, [history, globals, workspaceFile]);

  const lastContent = useRef<string>();

  // update EmbeddedEditorFile, but only if content is different than what was saved
  useCancelableEffect(
    useCallback(
      ({ ifNotCanceled }) => {
        if (!workspaceFile) {
          return;
        }

        workspaceFile.getFileContents().then(
          ifNotCanceled.run((content) => {
            if (content === lastContent.current) {
              return;
            }
            setCurrentFile(() => {
              return {
                path: workspaceFile.path,
                getFileContents: workspaceFile.getFileContents,
                kind: "local",
                isReadOnly: false,
                fileExtension: workspaceFile.extension,
                fileName: workspaceFile.nameWithoutExtension,
              };
            });
          })
        );
      },
      [workspaceFile]
    )
  );

  // auto-save
  useEffect(() => {
    if (isDirty) {
      if (!editor?.isReady || !workspaceFile) {
        return;
      }

      editor.getStateControl().setSavedCommand();
      alerts?.closeAll();

      editor.getContent().then((content) => {
        lastContent.current = content;
        workspaces.updateFile(workspaceFile, () => Promise.resolve(content));
      });
    }
  }, [editor, alerts, isDirty, workspaces, workspaceFile]);

  const onResourceContentRequest = useCallback(
    async (request: ResourceContentRequest) => {
      return workspaces.resourceContentGet(request.path, request.opts);
    },
    [workspaces]
  );

  const onResourceListRequest = useCallback(
    async (request: ResourceListRequest) => {
      if (!workspace) {
        return new ResourcesList(request.pattern, []);
      }
      return workspaces.resourceContentList(workspace.descriptor.workspaceId, request.pattern, request.opts);
    },
    [workspaces, workspace]
  );

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
        <Alert
          data-testid="unsaved-alert"
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

  const refreshEditor = useCallback(() => {
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
    if (currentFile.fileExtension === "dmn") {
      return;
    }

    editor?.validate().then((notifications) => {
      notificationsPanel
        ?.getTab(i18n.terms.validation)
        ?.kogitoNotifications_setNotifications("", Array.isArray(notifications) ? notifications : []);
    });
  }, [currentFile, notificationsPanel, editor, i18n]);

  useStateControlSubscription(editor, validate, { throttle: 200 });

  return (
    <>
      {fetchFileError && <EditorFetchFileErrorEmptyState fetchFileError={fetchFileError} currentFile={currentFile} />}
      {!fetchFileError && (
        <DmnRunnerContextProvider currentFile={currentFile} editor={editor} notificationsPanel={notificationsPanel}>
          <DmnDevSandboxContextProvider
            currentFile={currentFile}
            workspaceFile={workspaceFile}
            editor={editor}
            alerts={alerts}
          >
            <Page
              header={
                <EditorToolbar
                  addEmptyWorkspaceFile={addEmptyWorkspaceFile}
                  workspace={workspace}
                  editor={editor}
                  alerts={alerts}
                  currentFile={currentFile}
                  onRename={(newName) => {
                    if (workspaceFile) {
                      return workspaces.renameFile(workspaceFile, newName);
                    }
                  }}
                  onClose={onClose}
                />
              }
            >
              <PageSection
                isFilled={true}
                padding={{ default: "noPadding" }}
                className={"kogito--editor__page-section"}
              >
                <DmnRunnerDrawer editor={editor} notificationsPanel={notificationsPanel}>
                  <Alerts ref={alertsRef} />
                  <EmbeddedEditor
                    ref={editorRef}
                    file={currentFile}
                    kogitoWorkspace_resourceContentRequest={onResourceContentRequest}
                    kogitoWorkspace_resourceListRequest={onResourceListRequest}
                    kogitoEditor_setContentError={setContentErrorAlert.show}
                    editorEnvelopeLocator={globals.editorEnvelopeLocator}
                    channelType={ChannelType.VSCODE} // TODO CAPONETTO: Changed the channel type to test the Included Models (undo/redo do not work)
                    locale={locale}
                  />
                  <DmnRunnerContext.Consumer>
                    {(dmnRunner) => (
                      <NotificationsPanel
                        ref={notificationsPanelRef}
                        tabNames={notificationPanelTabNames(dmnRunner.status)}
                      />
                    )}
                  </DmnRunnerContext.Consumer>
                </DmnRunnerDrawer>
              </PageSection>
              <a ref={downloadRef} />
            </Page>
          </DmnDevSandboxContextProvider>
        </DmnRunnerContextProvider>
      )}
      <TextEditorModal
        editor={editor}
        currentFile={currentFile}
        refreshEditor={refreshEditor}
        isOpen={isTextEditorModalOpen}
      />
    </>
  );
}
