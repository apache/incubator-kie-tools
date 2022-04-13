/*
 * Copyright 2022 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import * as React from "react";
import { useCallback, useMemo, useState, useRef, useEffect } from "react";
import { ChannelType, KogitoEditorEnvelopeApi } from "@kie-tools-core/editor/dist/api";
import { useChromeExtensionI18n } from "../../i18n";
import { SW_JSON_EXTENSION } from "../../openshift/OpenShiftContext";
import { useGlobals } from "../../common/GlobalContext";
import { LoadingSpinner } from "../../common/LoadingSpinner";
import { Page, PageSection, PageSectionVariants } from "@patternfly/react-core/dist/js/components/Page";
import { useWorkspaceFilePromise } from "../../workspace/hooks/WorkspaceFileHooks";
import { OnlineEditorPage } from "../../pageTemplate/OnlineEditorPage";
import { EmbeddedEditorFile } from "@kie-tools-core/editor/dist/channel";
import { PromiseStateWrapper } from "../../workspace/hooks/PromiseState";
import { EditorPageErrorPage } from "./EditorPageErrorPage";
import { useCancelableEffect, useController, usePrevious } from "../../reactExt/Hooks";
import { Bullseye } from "@patternfly/react-core/dist/js/layouts/Bullseye";
import { Spinner } from "@patternfly/react-core/dist/js/components/Spinner";
import { Text, TextContent, TextVariants } from "@patternfly/react-core/dist/js/components/Text";
import { AlertsController } from "../../alerts/Alerts";
import { useWorkspaces } from "../../workspace/WorkspacesContext";
import { EditorToolbar } from "./EditorToolbar";
import { SwfServiceCatalogChannelApi } from "@kie-tools/serverless-workflow-service-catalog/dist/api";
import { useSettings } from "../../settings/SettingsContext";
import { EmbeddedEditor, useEditorRef, useStateControlSubscription } from "@kie-tools-core/editor/dist/embedded";
import { SwfServiceCatalogStore } from "../../serviceCatalog/SwfServiceCatalogStore";
import { EnvelopeServer } from "@kie-tools-core/envelope-bus/dist/channel";

const Loading = () => (
  <Bullseye>
    <TextContent>
      <Bullseye>
        <Spinner />
      </Bullseye>
      <br />
      <Text component={TextVariants.p}>{`Loading...`}</Text>
    </TextContent>
  </Bullseye>
);

export interface ServerlessWorkflowEditorProps {
  workspaceId: string;
  fileRelativePath: string;
  extension?: string;
}

export function ServerlessWorkflowEditor(props: ServerlessWorkflowEditorProps) {
  const globals = useGlobals();
  const { locale } = useChromeExtensionI18n();
  const { editor, editorRef } = useEditorRef();
  const settings = useSettings();
  const workspaces = useWorkspaces();
  const workspaceFilePromise = useWorkspaceFilePromise(props.workspaceId, props.fileRelativePath);
  const [embeddedEditorFile, setEmbeddedEditorFile] = useState<EmbeddedEditorFile>();
  const [alerts, alertsRef] = useController<AlertsController>();
  const lastContent = useRef<string>();

  const isEditorReady = useMemo(() => editor?.isReady, [editor]);

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
            isReadOnly: false,
            fileExtension: SW_JSON_EXTENSION,
            fileName: workspaceFilePromise.data.relativePath,
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
    SwfServiceCatalogStore.refresh(settings.serviceRegistry.config, settings.serviceAccount.config).then((services) => {
      swfServiceCatalogEnvelopeServer?.shared?.kogitoSwfServiceCatalog_services.set(services);
    });
  }, [
    settings.serviceAccount.config,
    settings.serviceRegistry.config,
    settings.serviceRegistry.config.coreRegistryApi,
    swfServiceCatalogEnvelopeServer,
  ]);

  return (
    <OnlineEditorPage>
      <PromiseStateWrapper
        promise={workspaceFilePromise}
        pending={<Loading />}
        rejected={(errors) => <EditorPageErrorPage errors={errors} path={props.fileRelativePath} />}
        resolved={(file) => (
          <Page>
            <EditorToolbar workspaceFile={file} editor={editor} />
            <PageSection variant={PageSectionVariants.default}>
              <div style={{ height: "100%" }}>
                {!isEditorReady && <LoadingSpinner />}
                {embeddedEditorFile && (
                  <div style={{ display: isEditorReady ? "inline" : "none" }}>
                    <EmbeddedEditor
                      ref={editorRef}
                      file={embeddedEditorFile}
                      channelType={ChannelType.EMBEDDED}
                      editorEnvelopeLocator={globals.envelopeLocator}
                      locale={locale}
                    />
                  </div>
                )}
              </div>
            </PageSection>
          </Page>
        )}
      />
    </OnlineEditorPage>
  );
}
