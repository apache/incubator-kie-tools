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
import { EmbeddedEditor, EmbeddedEditorRef, useStateControlSubscription } from "@kie-tooling-core/editor/dist/embedded";
import { DmnRunnerContextProvider } from "./DmnRunner/DmnRunnerContextProvider";
import { NotificationsPanel, NotificationsPanelController } from "./NotificationsPanel/NotificationsPanel";
import { Alert, AlertActionLink } from "@patternfly/react-core/dist/js/components/Alert";
import { Page, PageSection } from "@patternfly/react-core/dist/js/components/Page";
import { DmnDevSandboxContextProvider } from "./DmnDevSandbox/DmnDevSandboxContextProvider";
import { EmbeddedEditorFile } from "@kie-tooling-core/editor/dist/channel";
import { DmnRunnerDrawer } from "./DmnRunner/DmnRunnerDrawer";
import { Alerts, AlertsController, useAlert } from "./Alerts/Alerts";
import { useCancelableEffect, useController, usePrevious } from "../common/Hooks";
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
  const [isTextEditorModalOpen, setTextEditorModalOpen] = useState(false);

  const lastContent = useRef<string>();
  const workspacePromise = useWorkspacePromise(props.workspaceId);
  const workspaceFilePromise = useWorkspaceFilePromise(workspacePromise.data?.descriptor.workspaceId, props.filePath);

  const [embeddedEditorFile, setEmbeddedEditorFile] = useState<EmbeddedEditorFile>();

  useEffect(() => {
    alerts?.closeAll();
  }, [alerts]);

  useDmnTour(!editor?.isReady && props.forExtension === "dmn");

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

  // keep the page in sync with the name of `workspaceFilePromise`, even if changes
  useEffect(() => {
    if (!workspaceFilePromise.data) {
      return;
    }

    history.replace({
      pathname: globals.routes.workspaceWithFilePath.path({
        workspaceId: workspaceFilePromise.data.workspaceId,
        filePath: workspaceFilePromise.data.pathRelativeToWorkspaceRootWithoutExtension,
        extension: workspaceFilePromise.data.extension,
      }),
    });
  }, [history, globals, workspaceFilePromise]);

  // update EmbeddedEditorFile, but only if content is different than what was saved
  useCancelableEffect(
    useCallback(
      ({ canceled }) => {
        if (!workspaceFilePromise.data) {
          return;
        }

        workspaceFilePromise.data.getFileContents().then((content) => {
          if (canceled.get()) {
            return;
          }

          if (content === lastContent.current) {
            return;
          }

          setEmbeddedEditorFile({
            path: workspaceFilePromise.data.path,
            getFileContents: workspaceFilePromise.data.getFileContents,
            kind: "local",
            isReadOnly: false,
            fileExtension: workspaceFilePromise.data.extension,
            fileName: workspaceFilePromise.data.nameWithoutExtension,
          });
        });
      },
      [workspaceFilePromise]
    )
  );

  // auto-save
  const prevPath = usePrevious(workspaceFilePromise.data?.path);
  if (prevPath !== workspaceFilePromise.data?.path) {
    lastContent.current = undefined;
  }

  useStateControlSubscription(
    editor,
    useCallback(
      async (isDirty) => {
        if (!isDirty || !workspaceFilePromise.data) {
          return;
        }

        const content = await editor?.getContent();
        lastContent.current = content;

        await workspaces.updateFile(workspaceFilePromise.data, () => Promise.resolve(content));
        editor?.getStateControl().setSavedCommand();
      },
      [editor, workspaceFilePromise]
    ),
    { throttle: 200 }
  );

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

  const refreshEditor = useCallback(() => {
    alerts?.closeAll();
    setTextEditorModalOpen(false);
  }, [alerts]);

  // validate
  useEffect(() => {
    if (props.forExtension === "dmn" || !workspaceFilePromise.data || !editor?.isReady) {
      return;
    }

    //FIXME: tiago What to do?
    setTimeout(() => {
      editor?.validate().then((notifications) => {
        notificationsPanel
          ?.getTab(i18n.terms.validation)
          ?.kogitoNotifications_setNotifications("", Array.isArray(notifications) ? notifications : []);
      });
    }, 200);
  }, [workspaceFilePromise, props.forExtension, notificationsPanel, editor, i18n]);

  return (
    <>
      <DmnRunnerContextProvider workspaceFile={workspaceFilePromise.data} notificationsPanel={notificationsPanel}>
        <DmnDevSandboxContextProvider workspaceFile={workspaceFilePromise.data} alerts={alerts}>
          <Page
            header={
              <EditorToolbar
                workspace={workspacePromise.data}
                workspaceFile={workspaceFilePromise.data}
                editor={editor}
                alerts={alerts}
              />
            }
          >
            <PageSection isFilled={true} padding={{ default: "noPadding" }} className={"kogito--editor__page-section"}>
              <DmnRunnerDrawer workspaceFile={workspaceFilePromise.data} notificationsPanel={notificationsPanel}>
                <Alerts ref={alertsRef} />
                {embeddedEditorFile && (
                  <EmbeddedEditor
                    //FIXME: There's a bug on the PMML Editor that prevents is from working after a setContent call.
                    key={
                      embeddedEditorFile.fileExtension === "pmml"
                        ? embeddedEditorFile.path
                        : embeddedEditorFile.fileExtension
                    }
                    ref={editorRef}
                    file={embeddedEditorFile}
                    kogitoWorkspace_resourceContentRequest={onResourceContentRequest}
                    kogitoWorkspace_resourceListRequest={onResourceListRequest}
                    kogitoEditor_setContentError={setContentErrorAlert.show}
                    editorEnvelopeLocator={globals.editorEnvelopeLocator}
                    channelType={ChannelType.VSCODE} // TODO CAPONETTO: Changed the channel type to test the Included Models (undo/redo do not work)
                    locale={locale}
                  />
                )}
                <NotificationsPanel
                  ref={notificationsPanelRef}
                  tabNames={useMemo(() => [i18n.terms.validation, i18n.terms.execution], [i18n])}
                />
              </DmnRunnerDrawer>
            </PageSection>
          </Page>
        </DmnDevSandboxContextProvider>
      </DmnRunnerContextProvider>
      <TextEditorModal
        editor={editor}
        workspaceFile={workspaceFilePromise.data}
        refreshEditor={refreshEditor}
        isOpen={isTextEditorModalOpen}
      />
    </>
  );
}
