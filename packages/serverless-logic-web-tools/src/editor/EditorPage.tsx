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
import { isDashbuilder, isEditable, isServerlessWorkflow } from "../extension";
import { useAppI18n } from "../i18n";
import { useRoutes } from "../navigation/Hooks";
import { OnlineEditorPage } from "../pageTemplate/OnlineEditorPage";
import { useQueryParams } from "../queryParams/QueryParamsContext";
import { useCancelableEffect } from "@kie-tools-core/react-hooks/dist/useCancelableEffect";
import { useController } from "@kie-tools-core/react-hooks/dist/useController";
import { usePrevious } from "@kie-tools-core/react-hooks/dist/usePrevious";
import { useSettingsDispatch } from "../settings/SettingsContext";
import { PromiseStateWrapper } from "@kie-tools-core/react-hooks/dist/PromiseState";
import { useWorkspaceFilePromise } from "@kie-tools-core/workspaces-git-fs/dist/hooks/WorkspaceFileHooks";
import { useWorkspaces } from "@kie-tools-core/workspaces-git-fs/dist/context/WorkspacesContext";
import { SwfLanguageServiceChannelApiImpl } from "./api/SwfLanguageServiceChannelApiImpl";
import { SwfServiceCatalogChannelApiImpl } from "./api/SwfServiceCatalogChannelApiImpl";
import { EditorPageDockDrawer, EditorPageDockDrawerRef } from "./EditorPageDockDrawer";
import { EditorPageErrorPage } from "./EditorPageErrorPage";
import { EditorToolbar } from "./EditorToolbar";
import {
  useUpdateVirtualServiceRegistryOnVsrFileEvent as useUpdateVirtualServiceRegistryOnVsrFileEvents,
  useUpdateVirtualServiceRegistryOnVsrWorkspaceEvent as useUpdateVirtualServiceRegistryOnVsrWorkspaceEvents,
  useUpdateVirtualServiceRegistryOnWorkspaceFileEvents,
} from "../virtualServiceRegistry/hooks/useUpdateVirtualServiceRegistry";
import { useVirtualServiceRegistry } from "../virtualServiceRegistry/VirtualServiceRegistryContext";
import { DiagnosticSeverity } from "vscode-languageserver-types";
import { useSwfFeatureToggle } from "./hooks/useSwfFeatureToggle";
import {
  SwfCombinedEditorChannelApiImpl,
  SwfFeatureToggleChannelApiImpl,
} from "@kie-tools/serverless-workflow-combined-editor/dist/impl";
import { WebToolsSwfLanguageService } from "./api/WebToolsSwfLanguageService";
import { APP_NAME } from "../AppConstants";
import { MessageBusClientApi } from "@kie-tools-core/envelope-bus/dist/api";
import { ServerlessWorkflowCombinedEditorChannelApi } from "@kie-tools/serverless-workflow-combined-editor/dist/api";
import { Position } from "monaco-editor";
import { DashbuilderLanguageServiceChannelApiImpl } from "./api/DashbuilderLanguageServiceChannelApiImpl";
import { DashbuilderLanguageService } from "@kie-tools/dashbuilder-language-service/dist/channel";
import { DashbuilderEditorChannelApiImpl } from "@kie-tools/dashbuilder-editor/dist/impl";
import { DashbuilderLanguageServiceChannelApi } from "@kie-tools/dashbuilder-language-service/dist/api";
export interface Props {
  workspaceId: string;
  fileRelativePath: string;
}

let saveVersion = 1;
let refreshVersion = 0;

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
  const [isServiceRegistryReady, setServiceRegistryReady] = useState(false);
  const swfFeatureToggle = useSwfFeatureToggle(editor);

  const queryParams = useQueryParams();
  const virtualServiceRegistry = useVirtualServiceRegistry();

  const isSwf = useMemo(
    () => workspaceFilePromise.data && isServerlessWorkflow(workspaceFilePromise.data.workspaceFile.name),
    [workspaceFilePromise.data]
  );

  const isDash = useMemo(
    () => workspaceFilePromise.data && isDashbuilder(workspaceFilePromise.data.workspaceFile.name),
    [workspaceFilePromise.data]
  );

  useUpdateVirtualServiceRegistryOnWorkspaceFileEvents({ workspaceFile: workspaceFilePromise.data?.workspaceFile });
  useUpdateVirtualServiceRegistryOnVsrWorkspaceEvents({ catalogStore: settingsDispatch.serviceRegistry.catalogStore });
  useUpdateVirtualServiceRegistryOnVsrFileEvents({
    workspaceId: props.workspaceId,
    catalogStore: settingsDispatch.serviceRegistry.catalogStore,
  });

  useEffect(() => {
    document.title = `${APP_NAME} :: ${props.fileRelativePath}`;
  }, [props.fileRelativePath]);

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

          setEmbeddedEditorFile({
            path: workspaceFilePromise.data.workspaceFile.relativePath,
            getFileContents: async () => content,
            isReadOnly: !isEditable(workspaceFilePromise.data.workspaceFile.relativePath),
            fileExtension: workspaceFilePromise.data.workspaceFile.extension,
            fileName: workspaceFilePromise.data.workspaceFile.name,
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
    // FIXME: Uncomment when working on KOGITO-7805
    // const svgString = await editor.getPreview();

    if (version + 1 < saveVersion) {
      console.debug(`Saving @ stale version (${version}); ignoring before writing.`);
      return;
    }

    // FIXME: Uncomment when working on KOGITO-7805
    // if (svgString) {
    //   await svgService.createOrOverwriteSvg(workspaceFilePromise.data, svgString);
    // }
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
    { throttle: isSwf && swfFeatureToggle.stunnerEnabled ? 400 : 200 }
  );
  // end (AUTO-SAVE)

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
      workspaceFilePromise.data &&
      (!settingsDispatch.serviceRegistry.catalogStore.virtualServiceRegistry ||
        settingsDispatch.serviceRegistry.catalogStore.currentFile !== workspaceFilePromise.data.workspaceFile)
    ) {
      settingsDispatch.serviceRegistry.catalogStore.setVirtualServiceRegistry(
        virtualServiceRegistry,
        workspaceFilePromise.data.workspaceFile
      );
    }
  }, [settingsDispatch.serviceRegistry.catalogStore, virtualServiceRegistry, workspaceFilePromise.data]);

  // SWF-specific code should be isolated when having more capabilities for other editors.

  useEffect(() => {
    setServiceRegistryReady(false);
    if (isSwf && isReady) {
      settingsDispatch.serviceRegistry.catalogStore.refresh().then(() => setServiceRegistryReady(true));
    }
  }, [isSwf, isReady, settingsDispatch.serviceRegistry.catalogStore]);

  const swfLanguageService = useMemo(() => {
    if (!isSwf || !workspaceFilePromise.data) {
      return;
    }

    const webToolsSwfLanguageService = new WebToolsSwfLanguageService(settingsDispatch.serviceRegistry.catalogStore);

    return webToolsSwfLanguageService.getLs(workspaceFilePromise.data.workspaceFile.relativePath);
  }, [isSwf, workspaceFilePromise.data, settingsDispatch.serviceRegistry.catalogStore]);

  const swfLanguageServiceChannelApiImpl = useMemo(
    () => swfLanguageService && new SwfLanguageServiceChannelApiImpl(swfLanguageService),
    [swfLanguageService]
  );

  const dashbuilderLanguageService = useMemo(() => {
    if (!isDash || !workspaceFilePromise.data) {
      return;
    }
    return new DashbuilderLanguageService();
  }, [isDash, workspaceFilePromise.data]);

  const dashbuilderLanguageServiceChannelApiImpl = useMemo(
    () => dashbuilderLanguageService && new DashbuilderLanguageServiceChannelApiImpl(dashbuilderLanguageService),
    [dashbuilderLanguageService]
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
    if (!channelApiImpl || (!swfLanguageService && !dashbuilderLanguageService) || !swfServiceCatalogChannelApiImpl) {
      return;
    }
    if (isDash) {
      return new DashbuilderEditorChannelApiImpl(
        channelApiImpl,
        dashbuilderLanguageServiceChannelApiImpl as DashbuilderLanguageServiceChannelApi
      );
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
    dashbuilderLanguageService,
    swfServiceCatalogChannelApiImpl,
    swfFeatureToggleChannelApiImpl,
    swfLanguageServiceChannelApiImpl,
    dashbuilderLanguageServiceChannelApiImpl,
  ]);

  useEffect(() => {
    if (!editor?.isReady || lastContent.current === undefined || !workspaceFilePromise.data || !swfLanguageService) {
      return;
    }

    swfLanguageService
      .getDiagnostics({
        content: lastContent.current,
        uriPath: workspaceFilePromise.data.workspaceFile.relativePath,
      })
      .then((lsDiagnostics) => {
        const diagnostics = lsDiagnostics.map(
          (lsDiagnostic) =>
            ({
              path: "", // empty to not group them by path, as we're only validating one file.
              severity: lsDiagnostic.severity === DiagnosticSeverity.Error ? "ERROR" : "WARNING",
              message: `${lsDiagnostic.message} [Line ${lsDiagnostic.range.start.line + 1}]`,
              type: "PROBLEM",
              position: {
                startLineNumber: lsDiagnostic.range.start.line + 1,
                startColumn: lsDiagnostic.range.start.character + 1,
                endLineNumber: lsDiagnostic.range.end.line + 1,
                endColumn: lsDiagnostic.range.end.character + 1,
              },
            } as Notification)
        );

        editorPageDock?.setNotifications(i18n.terms.validation, "", diagnostics);
      })
      .catch((e) => console.error(e));
  }, [
    workspaceFilePromise.data,
    editor,
    swfLanguageService,
    editorPageDock,
    i18n.terms.validation,
    isServiceRegistryReady,
  ]);

  const swfEditorChannelApi = useMemo(
    () =>
      embeddedEditorFile && isServerlessWorkflow(embeddedEditorFile?.fileName)
        ? (editor?.getEnvelopeServer()
            .envelopeApi as unknown as MessageBusClientApi<ServerlessWorkflowCombinedEditorChannelApi>)
        : undefined,
    [editor]
  );

  const onNotificationClick = useCallback(
    (notification: Notification) => {
      if (
        !notification.position ||
        !swfEditorChannelApi ||
        !embeddedEditorFile ||
        !isServerlessWorkflow(embeddedEditorFile?.fileName)
      ) {
        return;
      }

      swfEditorChannelApi.notifications.kogitoSwfCombinedEditor_moveCursorToPosition.send(
        new Position(notification.position.startLineNumber, notification.position.startColumn)
      );
    },
    [swfEditorChannelApi]
  );
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
                workspaceFile={file.workspaceFile}
                editor={editor}
                alerts={alerts}
                alertsRef={alertsRef}
                editorPageDock={editorPageDock}
              />
              <Divider />
              <EditorPageDockDrawer
                ref={editorPageDockRef}
                isEditorReady={editor?.isReady}
                workspaceFile={file.workspaceFile}
                onNotificationClick={onNotificationClick}
              >
                <PageSection hasOverflowScroll={true} padding={{ default: "noPadding" }} aria-label="Editor Section">
                  <div style={{ height: "100%" }}>
                    {!isEditorReady && <LoadingSpinner />}
                    <div style={{ display: isEditorReady ? "inline" : "none" }}>
                      {embeddedEditorFile && (
                        <EmbeddedEditor
                          /* FIXME: By providing a different `key` everytime, we avoid calling `setContent` twice on the same Editor.
                           * This is by design, and after setContent supports multiple calls on the same instance, we can remove that.
                           */
                          key={uniqueFileId}
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
