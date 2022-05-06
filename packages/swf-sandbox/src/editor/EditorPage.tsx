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

import { ChannelType, KogitoEditorEnvelopeApi } from "@kie-tools-core/editor/dist/api";
import { EmbeddedEditorFile } from "@kie-tools-core/editor/dist/channel";
import { EmbeddedEditor, EmbeddedEditorRef, useStateControlSubscription } from "@kie-tools-core/editor/dist/embedded";
import { EnvelopeServer } from "@kie-tools-core/envelope-bus/dist/channel";
import { ResourceContentRequest, ResourceListRequest } from "@kie-tools-core/workspace/dist/api";
import { SwfServiceCatalogChannelApi } from "@kie-tools/serverless-workflow-service-catalog/dist/api";
import { Divider } from "@patternfly/react-core/dist/js/components/Divider";
import { Page, PageSection } from "@patternfly/react-core/dist/js/components/Page";
import { Spinner } from "@patternfly/react-core/dist/js/components/Spinner";
import { Text, TextContent, TextVariants } from "@patternfly/react-core/dist/js/components/Text";
import { Bullseye } from "@patternfly/react-core/dist/js/layouts/Bullseye";
import * as React from "react";
import { useCallback, useEffect, useMemo, useRef, useState } from "react";
import { useHistory } from "react-router";
import { AlertsController } from "../alerts/Alerts";
import { LoadingSpinner } from "../common/LoadingSpinner";
import { useEditorEnvelopeLocator } from "../envelopeLocator/EditorEnvelopeLocatorContext";
import { isServerlessDecision, isServerlessWorkflow } from "../fixme";
import { useAppI18n } from "../i18n";
import { useRoutes } from "../navigation/Hooks";
import { OnlineEditorPage } from "../pageTemplate/OnlineEditorPage";
import { useQueryParams } from "../queryParams/QueryParamsContext";
import { useCancelableEffect, useController, usePrevious } from "../reactExt/Hooks";
import { SwfServiceCatalogStore } from "../serviceCatalog/SwfServiceCatalogStore";
import { useSettings } from "../settings/SettingsContext";
import { PromiseStateWrapper } from "../workspace/hooks/PromiseState";
import { useWorkspaceFilePromise } from "../workspace/hooks/WorkspaceFileHooks";
import { useWorkspaces } from "../workspace/WorkspacesContext";
import { EditorPageErrorPage } from "./EditorPageErrorPage";
import { EditorToolbar } from "./EditorToolbar";

export interface Props {
  workspaceId: string;
  fileRelativePath: string;
}

export function EditorPage(props: Props) {
  const settings = useSettings();
  const routes = useRoutes();
  const editorEnvelopeLocator = useEditorEnvelopeLocator();
  const history = useHistory();
  const workspaces = useWorkspaces();
  const { locale } = useAppI18n();
  const [editor, editorRef] = useController<EmbeddedEditorRef>();
  const [alerts, alertsRef] = useController<AlertsController>();
  const lastContent = useRef<string>();
  const workspaceFilePromise = useWorkspaceFilePromise(props.workspaceId, props.fileRelativePath);
  const [embeddedEditorFile, setEmbeddedEditorFile] = useState<EmbeddedEditorFile>();
  const isEditorReady = useMemo(() => editor?.isReady, [editor]);

  const queryParams = useQueryParams();

  // keep the page in sync with the name of `workspaceFilePromise`, even if changes
  useEffect(() => {
    if (!workspaceFilePromise.data) {
      return;
    }

    history.replace({
      pathname: routes.workspaceWithFilePath.path({
        workspaceId: workspaceFilePromise.data.workspaceId,
        fileRelativePath: workspaceFilePromise.data.relativePathWithoutExtension,
        extension: workspaceFilePromise.data.extension,
      }),
      search: queryParams.toString(),
    });
  }, [history, routes, workspaceFilePromise, queryParams]);

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

          lastContent.current = content;

          setEmbeddedEditorFile({
            path: workspaceFilePromise.data.relativePath,
            getFileContents: async () => content,
            isReadOnly: !(
              isServerlessWorkflow(workspaceFilePromise.data.relativePath) ||
              isServerlessDecision(workspaceFilePromise.data.relativePath)
            ),
            fileExtension: workspaceFilePromise.data.extension,
            fileName: workspaceFilePromise.data.relativePath, //FIXME
          });
        });
      },
      [workspaceFilePromise]
    )
  );

  // auto-save
  const uniqueFileId = workspaceFilePromise.data
    ? workspaces.getUniqueFileIdentifier(workspaceFilePromise.data)
    : undefined;

  const prevUniqueFileId = usePrevious(uniqueFileId);
  if (prevUniqueFileId !== uniqueFileId) {
    lastContent.current = undefined;
  }

  const saveContent = useCallback(async () => {
    if (!workspaceFilePromise.data || !editor) {
      return;
    }

    const content = await editor.getContent();
    // FIXME: Uncomment when KOGITO-6181 is fixed
    // const svgString = await editor.getPreview();

    lastContent.current = content;

    // FIXME: Uncomment when KOGITO-6181 is fixed
    // if (svgString) {
    //   await workspaces.svgService.createOrOverwriteSvg(workspaceFilePromise.data, svgString);
    // }

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

  const swfServiceCatalogEnvelopeServer = useMemo(
    () =>
      editor?.getEnvelopeServer() as unknown as EnvelopeServer<SwfServiceCatalogChannelApi, KogitoEditorEnvelopeApi>,
    [editor]
  );

  useEffect(() => {
    swfServiceCatalogEnvelopeServer?.shared?.kogitoSwfServiceCatalog_serviceRegistryUrl.set(
      settings.serviceRegistry.config.coreRegistryApi
    );
  }, [settings.serviceRegistry.config.coreRegistryApi, swfServiceCatalogEnvelopeServer]);

  useEffect(() => {
    swfServiceCatalogEnvelopeServer?.shared?.kogitoSwfServiceCatalog_user.set({
      username: settings.serviceAccount.config.clientId,
    });
  }, [
    settings.serviceAccount.config.clientId,
    settings.serviceRegistry.config.coreRegistryApi,
    swfServiceCatalogEnvelopeServer,
  ]);

  useEffect(() => {
    SwfServiceCatalogStore.refresh(
      settings.kieSandboxExtendedServices.config.buildUrl(),
      settings.serviceRegistry.config,
      settings.serviceAccount.config
    ).then((services) => {
      swfServiceCatalogEnvelopeServer?.shared?.kogitoSwfServiceCatalog_services.set(services);
    });
  }, [
    settings.kieSandboxExtendedServices.config,
    settings.serviceAccount.config,
    settings.serviceRegistry.config,
    swfServiceCatalogEnvelopeServer,
  ]);

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
    // Nothing to do for now
  }, []);

  return (
    <OnlineEditorPage>
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
            <Page>
              <EditorToolbar workspaceFile={file} editor={editor} alerts={alerts} alertsRef={alertsRef} />
              <Divider />
              <PageSection hasOverflowScroll={true} padding={{ default: "noPadding" }}>
                <div style={{ height: "100%" }}>
                  {!isEditorReady && <LoadingSpinner />}
                  {embeddedEditorFile && (
                    <div style={{ display: isEditorReady ? "inline" : "none" }}>
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
                        editorEnvelopeLocator={editorEnvelopeLocator}
                        channelType={ChannelType.ONLINE_MULTI_FILE}
                        locale={locale}
                      />
                    </div>
                  )}
                </div>
              </PageSection>
            </Page>
          </>
        )}
      />
    </OnlineEditorPage>
  );
}
