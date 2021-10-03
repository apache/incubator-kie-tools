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
import { useCallback, useEffect, useRef, useState } from "react";
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
import { EmbeddedEditorFile } from "@kie-tooling-core/editor/dist/channel";
import { DmnRunnerDrawer } from "./DmnRunner/DmnRunnerDrawer";
import { Alerts, AlertsController, useAlert } from "./Alerts/Alerts";
import { useCancelableEffect, useController } from "../common/Hooks";
import { TextEditorModal } from "./TextEditor/TextEditorModal";
import { useWorkspaces } from "../workspace/WorkspacesContext";
import { ResourceContentRequest, ResourceListRequest, ResourcesList } from "@kie-tooling-core/workspace/dist/api";
import { useWorkspacePromise } from "../workspace/hooks/WorkspaceHooks";
import { useWorkspaceFilePromise } from "../workspace/hooks/WorkspaceFileHooks";

export interface Props {
  forExtension: SupportedFileExtensions;
  workspaceId: string;
  filePath: string;
}

export function EditorPage(props: Props) {
  const globals = useGlobals();
  const history = useHistory();
  const workspaces = useWorkspaces();
  const { locale, i18n } = useOnlineI18n();
  const [editor, editorRef] = useController<EmbeddedEditorRef>();
  const [alerts, alertsRef] = useController<AlertsController>();
  const [notificationsPanel, notificationsPanelRef] = useController<NotificationsPanelController>();
  const downloadRef = useRef<HTMLAnchorElement>(null);
  const isDirty = useDirtyState(editor);
  const [isTextEditorModalOpen, setTextEditorModalOpen] = useState(false);

  const { workspacePromise, addEmptyWorkspaceFile } = useWorkspacePromise(props.workspaceId);

  const workspaceFile = useWorkspaceFilePromise(workspacePromise.data?.descriptor.workspaceId, props.filePath);

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
    if (!workspaceFile.data) {
      return;
    }

    history.replace({
      pathname: globals.routes.workspaceWithFilePath.path({
        workspaceId: workspaceFile.data.workspaceId,
        filePath: workspaceFile.data.pathRelativeToWorkspaceRootWithoutExtension,
        extension: workspaceFile.data.extension,
      }),
    });
  }, [history, globals, workspaceFile]);

  const lastContent = useRef<string>();

  // update EmbeddedEditorFile, but only if content is different than what was saved
  useCancelableEffect(
    useCallback(
      ({ canceled }) => {
        if (!workspaceFile.data) {
          return;
        }

        workspaceFile.data.getFileContents().then((content) => {
          if (canceled.get()) {
            return;
          }

          if (content === lastContent.current) {
            return;
          }

          setCurrentFile(() => {
            return {
              path: workspaceFile.data.path,
              getFileContents: workspaceFile.data.getFileContents,
              kind: "local",
              isReadOnly: false,
              fileExtension: workspaceFile.data.extension,
              fileName: workspaceFile.data.nameWithoutExtension,
            };
          });
        });
      },
      [workspaceFile]
    )
  );

  // auto-save
  useEffect(() => {
    if (isDirty) {
      if (!editor?.isReady || !workspaceFile.data) {
        return;
      }

      editor.getStateControl().setSavedCommand();
      alerts?.closeAll();

      editor.getContent().then((content) => {
        lastContent.current = content;
        workspaces.updateFile(workspaceFile.data, () => Promise.resolve(content));
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
      if (!workspacePromise.data) {
        return new ResourcesList(request.pattern, []);
      }
      return workspaces.resourceContentList(
        workspacePromise.data.descriptor.workspaceId,
        request.pattern,
        request.opts
      );
    },
    [workspaces, workspacePromise]
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
      <DmnRunnerContextProvider currentFile={currentFile} editor={editor} notificationsPanel={notificationsPanel}>
        <DmnDevSandboxContextProvider
          currentFile={currentFile}
          workspaceFile={workspaceFile.data}
          editor={editor}
          alerts={alerts}
        >
          <Page
            header={
              <EditorToolbar
                addEmptyWorkspaceFile={addEmptyWorkspaceFile}
                workspace={workspacePromise.data}
                editor={editor}
                alerts={alerts}
                currentFile={currentFile}
                onRename={(newName) => {
                  if (workspaceFile.data) {
                    return workspaces.renameFile(workspaceFile.data, newName);
                  }
                }}
                onClose={onClose}
              />
            }
          >
            <PageSection isFilled={true} padding={{ default: "noPadding" }} className={"kogito--editor__page-section"}>
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
      <TextEditorModal
        editor={editor}
        currentFile={currentFile}
        refreshEditor={refreshEditor}
        isOpen={isTextEditorModalOpen}
      />
    </>
  );
}
