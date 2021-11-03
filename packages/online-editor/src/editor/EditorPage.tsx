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
import { useGlobals } from "../common/GlobalContext";
import { EditorToolbar } from "./EditorToolbar";
import { useDmnTour } from "../tour";
import { useOnlineI18n } from "../common/i18n";
import { ChannelType } from "@kie-tooling-core/editor/dist/api";
import { EmbeddedEditor, EmbeddedEditorRef, useStateControlSubscription } from "@kie-tooling-core/editor/dist/embedded";
import { DmnRunnerContextProvider } from "./DmnRunner/DmnRunnerContextProvider";
import { Alert, AlertActionLink } from "@patternfly/react-core/dist/js/components/Alert";
import { Page, PageSection } from "@patternfly/react-core/dist/js/components/Page";
import { DmnDevSandboxContextProvider } from "./DmnDevSandbox/DmnDevSandboxContextProvider";
import { EmbeddedEditorFile } from "@kie-tooling-core/editor/dist/channel";
import { DmnRunnerDrawer } from "./DmnRunner/DmnRunnerDrawer";
import { AlertsController, useAlert } from "./Alerts/Alerts";
import { useCancelableEffect, useController, usePrevious } from "../common/Hooks";
import { TextEditorModal } from "./TextEditor/TextEditorModal";
import { useWorkspaces } from "../workspace/WorkspacesContext";
import { ResourceContentRequest, ResourceListRequest } from "@kie-tooling-core/workspace/dist/api";
import { useWorkspaceFilePromise } from "../workspace/hooks/WorkspaceFileHooks";
import { PromiseStateWrapper } from "../workspace/hooks/PromiseState";
import { EditorPageErrorPage } from "./EditorPageErrorPage";
import { BusinessAutomationStudioPage } from "../home/pageTemplate/BusinessAutomationStudioPage";
import { useQueryParams } from "../queryParams/QueryParamsContext";
import { Text, TextContent, TextVariants } from "@patternfly/react-core/dist/js/components/Text";
import { Bullseye } from "@patternfly/react-core/dist/js/layouts/Bullseye";
import { Spinner } from "@patternfly/react-core/dist/js/components/Spinner";
import { Divider } from "@patternfly/react-core/dist/js/components/Divider";
import { EditorPageDockDrawer, EditorPageDockDrawerController } from "./EditorPageDockDrawer";

export interface Props {
  workspaceId: string;
  fileRelativePath: string;
}

export function EditorPage(props: Props) {
  const globals = useGlobals();
  const history = useHistory();
  const workspaces = useWorkspaces();
  const { locale, i18n } = useOnlineI18n();
  const [editor, editorRef] = useController<EmbeddedEditorRef>();
  const [alerts, alertsRef] = useController<AlertsController>();
  const [editorPageDock, editorPageDockRef] = useController<EditorPageDockDrawerController>();
  const [isTextEditorModalOpen, setTextEditorModalOpen] = useState(false);

  const lastContent = useRef<string>();
  const workspaceFilePromise = useWorkspaceFilePromise(props.workspaceId, props.fileRelativePath);

  const [embeddedEditorFile, setEmbeddedEditorFile] = useState<EmbeddedEditorFile>();

  useDmnTour(!editor?.isReady && workspaceFilePromise.data?.extension === "dmn");

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

  const queryParams = useQueryParams();

  // keep the page in sync with the name of `workspaceFilePromise`, even if changes
  useEffect(() => {
    if (!workspaceFilePromise.data) {
      return;
    }

    history.replace({
      pathname: globals.routes.workspaceWithFilePath.path({
        workspaceId: workspaceFilePromise.data.workspaceId,
        fileRelativePath: workspaceFilePromise.data.relativePathWithoutExtension,
        extension: workspaceFilePromise.data.extension,
      }),
      search: queryParams.toString(),
    });
  }, [history, globals, workspaceFilePromise, queryParams]);

  // update EmbeddedEditorFile, but only if content is different than what was saved
  useCancelableEffect(
    useCallback(
      ({ canceled }) => {
        if (!workspaceFilePromise.data) {
          return;
        }

        workspaceFilePromise.data.getFileContentsAsString().then((content) => {
          if (canceled.get()) {
            return;
          }

          if (content === lastContent.current) {
            return;
          }

          setEmbeddedEditorFile({
            path: workspaceFilePromise.data.relativePath,
            getFileContents: workspaceFilePromise.data.getFileContentsAsString,
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
  const uniqueIdentifierOfFile = workspaceFilePromise.data
    ? workspaces.getUniqueFileIdentifier(workspaceFilePromise.data)
    : undefined;

  const prev = usePrevious(uniqueIdentifierOfFile);
  if (prev !== uniqueIdentifierOfFile) {
    lastContent.current = undefined;
  }

  const saveContent = useCallback(async () => {
    if (!workspaceFilePromise.data || !editor) {
      return;
    }

    const content = await editor.getContent();
    const svgString = await editor.getPreview();

    lastContent.current = content;

    if (svgString) {
      await workspaces.svgService.createOrOverwriteSvg(workspaceFilePromise.data, svgString);
    }

    await workspaces.updateFile({
      fs: await workspaces.fsService.getWorkspaceFs(workspaceFilePromise.data.workspaceId),
      file: workspaceFilePromise.data,
      getNewContents: () => Promise.resolve(content),
    });
    editor?.getStateControl().setSavedCommand();
  }, [workspaces, editor, workspaceFilePromise]);

  useStateControlSubscription(
    editor,
    useCallback(
      (isDirty) => {
        if (!isDirty) {
          return;
        }

        saveContent();
      },
      [saveContent]
    ),
    { throttle: 200 }
  );

  useEffect(() => {
    alerts?.closeAll();
  }, [alerts]);

  useEffect(() => {
    setContentErrorAlert.close();
  }, [uniqueIdentifierOfFile]);

  useEffect(() => {
    if (!editor?.isReady || !workspaceFilePromise.data) {
      return;
    }

    workspaceFilePromise.data.getFileContentsAsString().then((content) => {
      if (content !== "") {
        return;
      }
      saveContent();
    });
  }, [editor, saveContent, workspaceFilePromise]);

  const handleResourceContentRequest = useCallback(
    async (request: ResourceContentRequest) => {
      return workspaces.resourceContentGet({
        fs: await workspaces.fsService.getWorkspaceFs(props.workspaceId),
        workspaceId: props.workspaceId,
        relativePath: request.path,
        opts: request.opts,
      });
    },
    [props.workspaceId, workspaces]
  );

  const handleResourceListRequest = useCallback(
    async (request: ResourceListRequest) => {
      return workspaces.resourceContentList({
        fs: await workspaces.fsService.getWorkspaceFs(props.workspaceId),
        workspaceId: props.workspaceId,
        globPattern: request.pattern,
        opts: request.opts,
      });
    },
    [workspaces, props.workspaceId]
  );

  const refreshEditor = useCallback(() => {
    alerts?.closeAll();
    setTextEditorModalOpen(false);
  }, [alerts]);

  // validate
  useEffect(() => {
    if (workspaceFilePromise.data?.extension === "dmn" || !workspaceFilePromise.data || !editor?.isReady) {
      return;
    }

    //FIXME: Removing this timeout makes the notifications not work some times. Need to investigate.
    setTimeout(() => {
      editor?.validate().then((notifications) => {
        editorPageDock
          ?.getNotificationsPanel()
          ?.getTab(i18n.terms.validation)
          ?.kogitoNotifications_setNotifications("", Array.isArray(notifications) ? notifications : []);
      });
    }, 200);
  }, [workspaceFilePromise, editor, i18n, editorPageDock]);

  const handleOpenFile = useCallback(
    async (relativePath: string) => {
      if (!workspaceFilePromise.data) {
        return;
      }

      const file = await workspaces.getFile({
        fs: await workspaces.fsService.getWorkspaceFs(workspaceFilePromise.data.workspaceId),
        workspaceId: workspaceFilePromise.data.workspaceId,
        relativePath,
      });

      if (!file) {
        throw new Error(`Can't find ${relativePath} on Workspace '${workspaceFilePromise.data.workspaceId}'`);
      }

      history.push({
        pathname: globals.routes.workspaceWithFilePath.path({
          workspaceId: file.workspaceId,
          fileRelativePath: file.relativePathWithoutExtension,
          extension: file.extension,
        }),
      });
    },
    [workspaceFilePromise, workspaces, history, globals]
  );

  const handleSetContentError = useCallback(() => {
    setContentErrorAlert.show();
  }, [setContentErrorAlert]);

  return (
    <BusinessAutomationStudioPage>
      <PageSection variant={"light"} isFilled={true} padding={{ default: "noPadding" }}>
        <PromiseStateWrapper
          promise={workspaceFilePromise}
          pending={
            <Bullseye>
              <TextContent>
                <Bullseye>
                  <Spinner />
                </Bullseye>
                <br />
                <Text component={TextVariants.p}>{`Loading...`}</Text>
              </TextContent>
            </Bullseye>
          }
          rejected={(errors) => <EditorPageErrorPage errors={errors} path={props.fileRelativePath} />}
          resolved={(file) => (
            <>
              <DmnRunnerContextProvider workspaceFile={file} editorPageDock={editorPageDock}>
                <DmnDevSandboxContextProvider workspaceFile={file} alerts={alerts}>
                  <Page>
                    <EditorToolbar
                      workspaceFile={file}
                      editor={editor}
                      alerts={alerts}
                      alertsRef={alertsRef}
                      editorPageDock={editorPageDock}
                    />
                    <Divider />
                    <PageSection isFilled={true} padding={{ default: "noPadding" }}>
                      <DmnRunnerDrawer workspaceFile={file} editorPageDock={editorPageDock}>
                        <EditorPageDockDrawer
                          ref={editorPageDockRef}
                          isEditorReady={editor?.isReady}
                          workspaceFile={file}
                        >
                          {embeddedEditorFile && (
                            <EmbeddedEditor
                              /* FIXME: By providing a different `key` everytime, we avoid calling `setContent` twice on the same Editor.
                               * This is by design, and after setContent supports multiple calls on the same instance, we can remove that.
                               */
                              key={workspaces.getUniqueFileIdentifier(file)}
                              ref={editorRef}
                              file={embeddedEditorFile}
                              kogitoWorkspace_openFile={handleOpenFile}
                              kogitoWorkspace_resourceContentRequest={handleResourceContentRequest}
                              kogitoWorkspace_resourceListRequest={handleResourceListRequest}
                              kogitoEditor_setContentError={handleSetContentError}
                              editorEnvelopeLocator={globals.editorEnvelopeLocator}
                              channelType={ChannelType.VSCODE} // TODO: Change to ONLINE_MULTI_FILE when we upgrade the Editors.
                              locale={locale}
                            />
                          )}
                        </EditorPageDockDrawer>
                      </DmnRunnerDrawer>
                    </PageSection>
                  </Page>
                </DmnDevSandboxContextProvider>
              </DmnRunnerContextProvider>
              <TextEditorModal
                editor={editor}
                workspaceFile={file}
                refreshEditor={refreshEditor}
                isOpen={isTextEditorModalOpen}
              />
            </>
          )}
        />
      </PageSection>
    </BusinessAutomationStudioPage>
  );
}
