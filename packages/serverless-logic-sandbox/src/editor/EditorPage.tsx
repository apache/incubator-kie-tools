/*
 * Copyright 2022 Red Hat, Inc. and/or its affiliates.
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
import { ChannelType } from "@kie-tools-core/editor/dist/api";
import { EmbeddedEditorFile, StateControl } from "@kie-tools-core/editor/dist/channel";
import {
  EmbeddedEditor,
  EmbeddedEditorRef,
  EmbeddedEditorChannelApiImpl,
  useStateControlSubscription,
} from "@kie-tools-core/editor/dist/embedded";
import { Notification } from "@kie-tools-core/notifications/dist/api";
import { Divider } from "@patternfly/react-core/dist/js/components/Divider";
import { Page, PageSection } from "@patternfly/react-core/dist/js/components/Page";
import { useCallback, useEffect, useMemo, useRef, useState } from "react";
import { useHistory } from "react-router";
import { AlertsController } from "../alerts/Alerts";
import { LoadingSpinner } from "./LoadingSpinner";
import { useEditorEnvelopeLocator } from "../envelopeLocator/EditorEnvelopeLocatorContext";
import { isSandboxAsset, isServerlessWorkflow, isServerlessWorkflowJson, isServerlessWorkflowYaml } from "../extension";
import { useAppI18n } from "../i18n";
import { useRoutes } from "../navigation/Hooks";
import { OnlineEditorPage } from "../pageTemplate/OnlineEditorPage";
import { useQueryParams } from "../queryParams/QueryParamsContext";
import { useCancelableEffect, useController, usePrevious } from "../reactExt/Hooks";
import { useSettingsDispatch } from "../settings/SettingsContext";
import { PromiseStateWrapper } from "../workspace/hooks/PromiseState";
import { useWorkspaceFilePromise } from "../workspace/hooks/WorkspaceFileHooks";
import { useWorkspaces } from "../workspace/WorkspacesContext";
import { SwfLanguageServiceChannelApiImpl } from "./api/SwfLanguageServiceChannelApiImpl";
import { SwfServiceCatalogChannelApiImpl } from "./api/SwfServiceCatalogChannelApiImpl";
import { EditorPageDockDrawer, EditorPageDockDrawerRef } from "./EditorPageDockDrawer";
import { EditorPageErrorPage } from "./EditorPageErrorPage";
import { EditorToolbar } from "./EditorToolbar";
import { useUpdateWorkspaceRegistryGroupFile } from "../workspace/services/virtualServiceRegistry/hooks/useUpdateWorkspaceRegistryGroupFile";
import { useVirtualServiceRegistry } from "../workspace/services/virtualServiceRegistry/VirtualServiceRegistryContext";
import { DiagnosticSeverity } from "vscode-languageserver-types";
import { useSwfFeatureToggle } from "./hooks/useSwfFeatureToggle";
import {
  SwfCombinedEditorChannelApiImpl,
  SwfFeatureToggleChannelApiImpl,
} from "@kie-tools/serverless-workflow-combined-editor/dist/impl";
import { WebToolsSwfLanguageService } from "./api/WebToolsSwfLanguageService";

export interface Props {
  workspaceId: string;
  fileRelativePath: string;
}

export function EditorPage(props: Props) {
  const settingsDispatch = useSettingsDispatch();
  const routes = useRoutes();
  const editorEnvelopeLocator = useEditorEnvelopeLocator();
  const history = useHistory();
  const workspaces = useWorkspaces();
  const { i18n, locale } = useAppI18n();
  const [editor, editorRef] = useController<EmbeddedEditorRef>();
  const [alerts, alertsRef] = useController<AlertsController>();
  const [editorPageDock, editorPageDockRef] = useController<EditorPageDockDrawerRef>();
  const lastContent = useRef<string>();
  const workspaceFilePromise = useWorkspaceFilePromise(props.workspaceId, props.fileRelativePath);
  const [embeddedEditorFile, setEmbeddedEditorFile] = useState<EmbeddedEditorFile>();
  const isEditorReady = useMemo(() => editor?.isReady, [editor]);
  const [isReady, setReady] = useState(false);
  const swfFeatureToggle = useSwfFeatureToggle(editor);

  const queryParams = useQueryParams();
  const virtualServiceRegistry = useVirtualServiceRegistry();

  useUpdateWorkspaceRegistryGroupFile({ workspaceFile: workspaceFilePromise.data });

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
            isReadOnly: !isSandboxAsset(workspaceFilePromise.data.relativePath),
            fileExtension: workspaceFilePromise.data.extension,
            fileName: workspaceFilePromise.data.name,
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
      fs: await workspaces.fsService.getFs(workspaceFilePromise.data.workspaceId),
      file: workspaceFilePromise.data,
      getNewContents: () => Promise.resolve(content),
    });
    editor?.getStateControl().setSavedCommand();
  }, [workspaces, editor, workspaceFilePromise]);

  const isSwf = useMemo(
    () => workspaceFilePromise.data && isServerlessWorkflow(workspaceFilePromise.data.name),
    [workspaceFilePromise.data]
  );

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
    { throttle: isSwf && swfFeatureToggle.stunnerEnabled ? 400 : 200 }
  );

  useEffect(() => {
    alerts?.closeAll();
  }, [alerts]);

  const stateControl = useMemo(() => new StateControl(), [embeddedEditorFile?.getFileContents]);

  const channelApiImpl = useMemo(
    () =>
      embeddedEditorFile &&
      new EmbeddedEditorChannelApiImpl(stateControl, embeddedEditorFile, locale, {
        kogitoEditor_ready: () => {
          setReady(true);
        },
      }),
    [embeddedEditorFile, locale, stateControl]
  );

  useEffect(() => {
    if (
      !settingsDispatch.serviceRegistry.catalogStore.virtualServiceRegistry ||
      settingsDispatch.serviceRegistry.catalogStore.currentFile !== workspaceFilePromise.data
    ) {
      settingsDispatch.serviceRegistry.catalogStore.setVirtualServiceRegistry(
        virtualServiceRegistry,
        workspaceFilePromise.data
      );
    }
  }, [settingsDispatch.serviceRegistry.catalogStore, virtualServiceRegistry, workspaceFilePromise.data]);

  // SWF-specific code should be isolated when having more capabilities for other editors.

  const swfLanguageService = useMemo(() => {
    if (!isSwf || !workspaceFilePromise.data) {
      return;
    }

    const webToolsSwfLanguageService = new WebToolsSwfLanguageService(settingsDispatch.serviceRegistry.catalogStore);

    return webToolsSwfLanguageService.getLs(workspaceFilePromise.data.relativePath);
  }, [workspaceFilePromise.data, settingsDispatch.serviceRegistry.catalogStore]);

  const swfLanguageServiceChannelApiImpl = useMemo(
    () => swfLanguageService && new SwfLanguageServiceChannelApiImpl(swfLanguageService),
    [swfLanguageService]
  );

  const swfServiceCatalogChannelApiImpl = useMemo(
    () =>
      settingsDispatch.serviceRegistry.catalogStore &&
      new SwfServiceCatalogChannelApiImpl(settingsDispatch.serviceRegistry.catalogStore),
    [settingsDispatch.serviceRegistry.catalogStore]
  );

  const swfFeatureToggleChannelApiImpl = useMemo(
    () => new SwfFeatureToggleChannelApiImpl(swfFeatureToggle),
    [swfFeatureToggle]
  );

  useEffect(() => {
    if (embeddedEditorFile && !isServerlessWorkflow(embeddedEditorFile.path || "") && !isReady) {
      setReady(true);
    }
  }, [embeddedEditorFile, isReady, settingsDispatch.serviceRegistry.catalogStore, virtualServiceRegistry]);

  const apiImpl = useMemo(() => {
    if (!channelApiImpl || !swfLanguageService || !swfServiceCatalogChannelApiImpl) {
      return;
    }

    return new SwfCombinedEditorChannelApiImpl(
      channelApiImpl,
      swfFeatureToggleChannelApiImpl,
      swfServiceCatalogChannelApiImpl,
      swfLanguageServiceChannelApiImpl
    );
  }, [
    channelApiImpl,
    swfLanguageService,
    swfServiceCatalogChannelApiImpl,
    swfFeatureToggleChannelApiImpl,
    swfLanguageServiceChannelApiImpl,
  ]);

  useEffect(() => {
    if (!editor?.isReady || lastContent.current === undefined || !workspaceFilePromise.data || !swfLanguageService) {
      return;
    }

    swfLanguageService
      .getDiagnostics({
        content: lastContent.current,
        uriPath: workspaceFilePromise.data.relativePath,
      })
      .then((lsDiagnostics) => {
        const diagnostics = lsDiagnostics.map(
          (lsDiagnostic) =>
            ({
              path: "", // empty to not group them by path, as we're only validating one file.
              severity: lsDiagnostic.severity === DiagnosticSeverity.Error ? "ERROR" : "WARNING",
              message: `${lsDiagnostic.message} [Line ${lsDiagnostic.range.start.line + 1}]`,
              type: "PROBLEM",
            } as Notification)
        );

        editorPageDock?.setNotifications(i18n.terms.validation, "", diagnostics);
      })
      .catch((e) => console.error(e));
  }, [workspaceFilePromise.data, editor, swfLanguageService, editorPageDock, i18n.terms.validation]);

  return (
    <OnlineEditorPage>
      <PromiseStateWrapper
        promise={workspaceFilePromise}
        pending={<LoadingSpinner />}
        rejected={(errors) => <EditorPageErrorPage errors={errors} path={props.fileRelativePath} />}
        resolved={(file) => (
          <>
            <Page>
              <EditorToolbar
                workspaceFile={file}
                editor={editor}
                alerts={alerts}
                alertsRef={alertsRef}
                editorPageDock={editorPageDock}
              />
              <Divider />
              <EditorPageDockDrawer ref={editorPageDockRef} isEditorReady={editor?.isReady} workspaceFile={file}>
                <PageSection hasOverflowScroll={true} padding={{ default: "noPadding" }}>
                  <div style={{ height: "100%" }}>
                    {!isEditorReady && <LoadingSpinner />}
                    <div style={{ display: isEditorReady ? "inline" : "none" }}>
                      {embeddedEditorFile && (
                        <EmbeddedEditor
                          /* FIXME: By providing a different `key` everytime, we avoid calling `setContent` twice on the same Editor.
                           * This is by design, and after setContent supports multiple calls on the same instance, we can remove that.
                           */
                          key={workspaces.getUniqueFileIdentifier(file)}
                          ref={editorRef}
                          file={embeddedEditorFile}
                          editorEnvelopeLocator={editorEnvelopeLocator}
                          channelType={ChannelType.ONLINE_MULTI_FILE}
                          locale={locale}
                          customChannelApiImpl={apiImpl}
                          stateControl={stateControl}
                          isReady={isReady}
                        />
                      )}
                    </div>
                  </div>
                </PageSection>
              </EditorPageDockDrawer>
            </Page>
          </>
        )}
      />
    </OnlineEditorPage>
  );
}
