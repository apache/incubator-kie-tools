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
import { useRoutes } from "../navigation/Hooks";
import { EditorToolbar } from "./Toolbar/EditorToolbar";
import { useOnlineI18n } from "../i18n";
import { ChannelType } from "@kie-tools-core/editor/dist/api";
import { EmbeddedEditor, EmbeddedEditorRef, useStateControlSubscription } from "@kie-tools-core/editor/dist/embedded";
import { Alert, AlertActionLink } from "@patternfly/react-core/dist/js/components/Alert";
import { Page, PageSection } from "@patternfly/react-core/dist/js/components/Page";
import { DevDeploymentsConfirmDeployModal } from "../devDeployments/DevDeploymentsConfirmDeployModal";
import { EmbeddedEditorFile } from "@kie-tools-core/editor/dist/channel";
import { DmnRunnerDrawer } from "../dmnRunner/DmnRunnerDrawer";
import { useGlobalAlert, useGlobalAlertsDispatchContext } from "../alerts";
import { useCancelableEffect } from "@kie-tools-core/react-hooks/dist/useCancelableEffect";
import { useController } from "@kie-tools-core/react-hooks/dist/useController";
import { usePrevious } from "@kie-tools-core/react-hooks/dist/usePrevious";
import { TextEditorModal } from "./TextEditor/TextEditorModal";
import { useWorkspaces } from "@kie-tools-core/workspaces-git-fs/dist/context/WorkspacesContext";
import { ResourceContentRequest, ResourceListRequest } from "@kie-tools-core/workspace/dist/api";
import { useWorkspaceFilePromise } from "@kie-tools-core/workspaces-git-fs/dist/hooks/WorkspaceFileHooks";
import { PromiseStateWrapper } from "@kie-tools-core/react-hooks/dist/PromiseState";
import { EditorPageErrorPage } from "./EditorPageErrorPage";
import { OnlineEditorPage } from "../pageTemplate/OnlineEditorPage";
import { useQueryParams } from "../queryParams/QueryParamsContext";
import { Text, TextContent, TextVariants } from "@patternfly/react-core/dist/js/components/Text";
import { Bullseye } from "@patternfly/react-core/dist/js/layouts/Bullseye";
import { Spinner } from "@patternfly/react-core/dist/js/components/Spinner";
import { Divider } from "@patternfly/react-core/dist/js/components/Divider";
import { EditorPageDockDrawer } from "./EditorPageDockDrawer";
import { DmnRunnerContextProvider } from "../dmnRunner/DmnRunnerContextProvider";
import { useEditorEnvelopeLocator } from "../envelopeLocator/hooks/EditorEnvelopeLocatorContext";
import { usePreviewSvgs } from "../previewSvgs/PreviewSvgsContext";
import { DmnLanguageService } from "@kie-tools/dmn-language-service";
import { decoder } from "@kie-tools-core/workspaces-git-fs/dist/encoderdecoder/EncoderDecoder";
import { EditorPageDockContextProvider } from "./EditorPageDockContextProvider";
import { ErrorBoundary } from "../reactExt/ErrorBoundary";
import { EmptyState, EmptyStateBody, EmptyStateIcon } from "@patternfly/react-core/dist/js/components/EmptyState";
import { I18nWrapped } from "@kie-tools-core/i18n/dist/react-components";
import { ExclamationTriangleIcon } from "@patternfly/react-icons/dist/js/icons/exclamation-triangle-icon";
import { useEnv } from "../env/hooks/EnvContext";

export interface Props {
  workspaceId: string;
  fileRelativePath: string;
}

let saveVersion = 1;
let refreshVersion = 0;

const KOGITO_JIRA_LINK = "https://issues.jboss.org/projects/KOGITO";

export function EditorPage(props: Props) {
  const { env } = useEnv();
  const routes = useRoutes();
  const editorEnvelopeLocator = useEditorEnvelopeLocator();
  const history = useHistory();
  const workspaces = useWorkspaces();
  const { previewSvgService } = usePreviewSvgs();
  const { locale, i18n } = useOnlineI18n();
  const [editor, editorRef] = useController<EmbeddedEditorRef>();
  const alertsDispatch = useGlobalAlertsDispatchContext();
  const [isTextEditorModalOpen, setTextEditorModalOpen] = useState(false);
  const [isFileBroken, setFileBroken] = useState(false);
  const [_, setEditorPageError] = useState(false);
  const lastContent = useRef<string>();
  const workspaceFilePromise = useWorkspaceFilePromise(props.workspaceId, props.fileRelativePath);

  const [embeddedEditorFile, setEmbeddedEditorFile] = useState<EmbeddedEditorFile>();

  useEffect(() => {
    document.title = `${env.KIE_SANDBOX_APP_NAME} :: ${props.fileRelativePath}`;
  }, [env.KIE_SANDBOX_APP_NAME, props.fileRelativePath]);

  const setContentErrorAlert = useGlobalAlert(
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
      pathname: routes.workspaceWithFilePath.path({
        workspaceId: workspaceFilePromise.data.workspaceFile.workspaceId,
        fileRelativePath: workspaceFilePromise.data.workspaceFile.relativePathWithoutExtension,
        extension: workspaceFilePromise.data.workspaceFile.extension,
      }),
      search: queryParams.toString(),
    });
  }, [history, routes, workspaceFilePromise, queryParams]);

  // begin (REFRESH)
  // Update EmbeddedEditorFile, but only if content is different from what was saved
  // This effect handles the case where a file was edited in another tab.
  // It has its own version pointer to ignore stale executions.
  useCancelableEffect(
    useCallback(
      ({ canceled }) => {
        if (!workspaceFilePromise.data) {
          return;
        }

        const version = refreshVersion++;
        console.debug(`Refreshing @ new version (${refreshVersion}).`);

        workspaceFilePromise.data.workspaceFile.getFileContentsAsString().then((content) => {
          if (canceled.get()) {
            console.debug(`Refreshing @ canceled; ignoring.`);
            return;
          }

          if (content === lastContent.current) {
            console.debug(`Refreshing @ unchanged content; ignoring.`);
            return;
          }

          if (version + 1 < saveVersion) {
            console.debug(`Refreshing @ stale version (${version}); ignoring.`);
            return;
          }

          console.debug(`Refreshing @ current version (${saveVersion}).`);
          refreshVersion = saveVersion;
          lastContent.current = content;

          // FIXME: KOGITO-7958: PMML Editor doesn't work well after this is called. Can't edit using multiple tabs.
          setEmbeddedEditorFile({
            path: workspaceFilePromise.data.workspaceFile.relativePath,
            getFileContents: async () => content,
            isReadOnly: false,
            fileExtension: workspaceFilePromise.data.workspaceFile.extension,
            fileName: workspaceFilePromise.data.workspaceFile.nameWithoutExtension,
          });
        });
      },
      [workspaceFilePromise]
    )
  );
  // end (REFRESH)

  // begin (AUTO-SAVE)
  const uniqueFileId = workspaceFilePromise.data?.uniqueId;
  const prevUniqueFileId = usePrevious(uniqueFileId);
  if (prevUniqueFileId !== uniqueFileId) {
    lastContent.current = undefined;
    saveVersion = 1;
    refreshVersion = 0;
  }

  const saveContent = useCallback(async () => {
    if (!workspaceFilePromise.data || !editor) {
      return;
    }

    const version = saveVersion++;
    console.debug(`Saving @ new version (${saveVersion}).`);

    const content = await editor.getContent();
    if (version + 1 < saveVersion) {
      console.debug(`Saving @ stale version (${version}); ignoring before writing.`);
      return;
    }

    console.debug(`Saving @ current version (${version}); updating content.`);
    lastContent.current = content;

    await workspaces.updateFile({
      workspaceId: workspaceFilePromise.data.workspaceFile.workspaceId,
      relativePath: workspaceFilePromise.data.workspaceFile.relativePath,
      newContent: content,
    });

    if (version + 1 < saveVersion) {
      console.debug(`Saving @ stale version (${version}); ignoring before marking as saved.`);
      return;
    }

    console.debug(`Saving @ current version (${version}); marking as saved.`);
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
  // end (AUTO-SAVE)

  // being (UPDATE PREVIEW SVGS)
  const updatePreviewSvg = useCallback(() => {
    editor?.getPreview()?.then((svgString) => {
      if (!workspaceFilePromise.data || !svgString) {
        return;
      }

      return previewSvgService.companionFsService.createOrOverwrite(
        {
          workspaceId: workspaceFilePromise.data.workspaceFile.workspaceId,
          workspaceFileRelativePath: workspaceFilePromise.data.workspaceFile.relativePath,
        },
        svgString
      );
    });
  }, [editor, previewSvgService, workspaceFilePromise.data]);

  // Update the SVG
  useStateControlSubscription(editor, updatePreviewSvg, { throttle: 200 });

  // Save SVG when opening a file for the first time, even without changing it.
  useEffect(updatePreviewSvg, [updatePreviewSvg]);
  //end (UPDATE PREVIEW SVGS)

  useEffect(() => {
    alertsDispatch.closeAll();
  }, [alertsDispatch]);

  useEffect(() => {
    setFileBroken(false);
    setContentErrorAlert.close();
  }, [setContentErrorAlert, uniqueFileId]);

  const handleResourceContentRequest = useCallback(
    async (request: ResourceContentRequest) => {
      return workspaces.resourceContentGet({
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
        workspaceId: props.workspaceId,
        globPattern: request.pattern,
        opts: request.opts,
      });
    },
    [workspaces, props.workspaceId]
  );

  const refreshEditor = useCallback(() => {
    alertsDispatch.closeAll();
    setTextEditorModalOpen(false);
  }, [alertsDispatch]);

  const handleOpenFile = useCallback(
    async (relativePath: string) => {
      if (!workspaceFilePromise.data) {
        return;
      }

      const file = await workspaces.getFile({
        workspaceId: workspaceFilePromise.data.workspaceFile.workspaceId,
        relativePath,
      });

      if (!file) {
        throw new Error(
          `Can't find ${relativePath} on Workspace '${workspaceFilePromise.data.workspaceFile.workspaceId}'`
        );
      }

      history.push({
        pathname: routes.workspaceWithFilePath.path({
          workspaceId: file.workspaceId,
          fileRelativePath: file.relativePathWithoutExtension,
          extension: file.extension,
        }),
      });
    },
    [workspaceFilePromise, workspaces, history, routes]
  );

  const handleSetContentError = useCallback(() => {
    setFileBroken(true);
    setContentErrorAlert.show();
  }, [setContentErrorAlert]);

  const dmnLanguageService = useMemo(() => {
    if (!workspaceFilePromise.data?.workspaceFile) {
      return;
    }

    if (workspaceFilePromise.data?.workspaceFile.extension.toLocaleLowerCase() !== "dmn") {
      return;
    }

    return new DmnLanguageService({
      getDmnImportedModel: async (importedModelRelativePath: string) => {
        const fileContent = await workspaces.getFileContent({
          workspaceId: workspaceFilePromise.data?.workspaceFile.workspaceId,
          relativePath: importedModelRelativePath,
        });

        return { content: decoder.decode(fileContent), relativePath: importedModelRelativePath };
      },
    });
  }, [workspaces, workspaceFilePromise.data?.workspaceFile]);

  const onKeyDown = useCallback(
    (ke: React.KeyboardEvent) => {
      editor?.onKeyDown(ke);
    },
    [editor]
  );

  const errorMessage = useMemo(
    () => (
      <div>
        <EmptyState>
          <EmptyStateIcon icon={ExclamationTriangleIcon} />
          <TextContent>
            <Text component={"h2"}>{i18n.editorPage.error.title}</Text>
          </TextContent>
          <EmptyStateBody>
            <TextContent>{i18n.editorPage.error.explanation}</TextContent>
            <br />
            <TextContent>
              <I18nWrapped
                components={{
                  jira: (
                    <a href={KOGITO_JIRA_LINK} target={"_blank"}>
                      {KOGITO_JIRA_LINK}
                    </a>
                  ),
                }}
              >
                {i18n.editorPage.error.message}
              </I18nWrapped>
            </TextContent>
          </EmptyStateBody>
        </EmptyState>
      </div>
    ),
    [i18n]
  );

  return (
    <OnlineEditorPage onKeyDown={onKeyDown}>
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
        rejected={(errors) => (
          <EditorPageErrorPage title={"Can't open file"} errors={errors} path={props.fileRelativePath} />
        )}
        resolved={(file) => (
          <ErrorBoundary error={errorMessage} setHasError={setEditorPageError}>
            <Page>
              <EditorPageDockContextProvider
                workspaceFile={file.workspaceFile}
                workspaces={workspaces}
                dmnLanguageService={dmnLanguageService}
                isEditorReady={editor?.isReady ?? false}
                editorValidate={editor?.validate}
              >
                <DmnRunnerContextProvider
                  workspaceFile={file.workspaceFile}
                  isEditorReady={editor?.isReady}
                  dmnLanguageService={dmnLanguageService}
                >
                  <EditorToolbar workspaceFile={file.workspaceFile} editor={editor} />
                  <Divider />
                  <PageSection hasOverflowScroll={true} padding={{ default: "noPadding" }} aria-label="Editor section">
                    <DmnRunnerDrawer>
                      <EditorPageDockDrawer>
                        {embeddedEditorFile && (
                          <EmbeddedEditor
                            /* FIXME: By providing a different `key` everytime, we avoid calling `setContent` twice on the same Editor.
                             * This is by design, and after setContent supports multiple calls on the same instance, we can remove that.
                             */
                            key={uniqueFileId}
                            ref={editorRef}
                            file={embeddedEditorFile}
                            kogitoWorkspace_openFile={handleOpenFile}
                            kogitoWorkspace_resourceContentRequest={handleResourceContentRequest}
                            kogitoWorkspace_resourceListRequest={handleResourceListRequest}
                            kogitoEditor_setContentError={handleSetContentError}
                            editorEnvelopeLocator={editorEnvelopeLocator}
                            channelType={ChannelType.ONLINE_MULTI_FILE}
                            locale={locale}
                          />
                        )}
                      </EditorPageDockDrawer>
                    </DmnRunnerDrawer>
                  </PageSection>
                </DmnRunnerContextProvider>
              </EditorPageDockContextProvider>
            </Page>
            <TextEditorModal
              editor={editor}
              workspaceFile={file.workspaceFile}
              refreshEditor={refreshEditor}
              isOpen={isTextEditorModalOpen}
            />
            <DevDeploymentsConfirmDeployModal workspaceFile={file.workspaceFile} />
          </ErrorBoundary>
        )}
      />
    </OnlineEditorPage>
  );
}
