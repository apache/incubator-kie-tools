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
import { EmbeddedEditorFile } from "@kie-tools-core/editor/dist/channel";
import { useStateControlSubscription } from "@kie-tools-core/editor/dist/embedded";
import { Notification } from "@kie-tools-core/notifications/dist/api";
import { Divider } from "@patternfly/react-core/dist/js/components/Divider";
import { Page, PageSection } from "@patternfly/react-core/dist/js/components/Page";
import { useCallback, useEffect, useMemo, useRef, useState } from "react";
import { useHistory } from "react-router";
import { LoadingSpinner } from "./LoadingSpinner";
import { useEditorEnvelopeLocator } from "../envelopeLocator/EditorEnvelopeLocatorContext";
import { isEditable } from "../extension";
import { useAppI18n } from "../i18n";
import { useRoutes } from "../navigation/Hooks";
import { useQueryParams } from "../queryParams/QueryParamsContext";
import { useCancelableEffect } from "@kie-tools-core/react-hooks/dist/useCancelableEffect";
import { useController } from "@kie-tools-core/react-hooks/dist/useController";
import { usePrevious } from "@kie-tools-core/react-hooks/dist/usePrevious";
import { PromiseStateWrapper } from "@kie-tools-core/react-hooks/dist/PromiseState";
import { useWorkspaceFilePromise } from "@kie-tools-core/workspaces-git-fs/dist/hooks/WorkspaceFileHooks";
import { useWorkspaces } from "@kie-tools-core/workspaces-git-fs/dist/context/WorkspacesContext";
import { EditorPageDockDrawer, EditorPageDockDrawerRef } from "./EditorPageDockDrawer";
import { EditorPageErrorPage } from "./EditorPageErrorPage";
import { EditorToolbar } from "./EditorToolbar";
import { APP_NAME } from "../AppConstants";
import { WebToolsEmbeddedEditor, WebToolsEmbeddedEditorRef } from "./WebToolsEmbeddedEditor";
import { useEditorNotifications } from "./hooks/useEditorNotifications";
import { useGlobalAlertsDispatchContext } from "../alerts/GlobalAlertsContext";
import { setPageTitle } from "../PageTitle";

export interface Props {
  workspaceId: string;
  fileRelativePath: string;
}

let saveVersion = 1;
let refreshVersion = 0;

export function EditorPage(props: Props) {
  const routes = useRoutes();
  const editorEnvelopeLocator = useEditorEnvelopeLocator();
  const history = useHistory();
  const workspaces = useWorkspaces();
  const { i18n, locale } = useAppI18n();
  const [webToolsEditor, webToolsEditorRef] = useController<WebToolsEmbeddedEditorRef>();
  const [editorPageDock, editorPageDockRef] = useController<EditorPageDockDrawerRef>();
  const lastContent = useRef<string>();
  const workspaceFilePromise = useWorkspaceFilePromise(props.workspaceId, props.fileRelativePath);
  const [embeddedEditorFile, setEmbeddedEditorFile] = useState<EmbeddedEditorFile>();
  const isEditorReady = useMemo(() => webToolsEditor?.editor?.isReady, [webToolsEditor]);
  const queryParams = useQueryParams();
  const alertsDispatch = useGlobalAlertsDispatchContext();

  const notifications = useEditorNotifications({
    webToolsEditor,
    content: lastContent.current,
    fileRelativePath: props.fileRelativePath,
  });

  useEffect(() => {
    if (!workspaceFilePromise.data) {
      return;
    }
    setPageTitle([workspaceFilePromise.data.workspaceFile.name]);
  }, [workspaceFilePromise.data]);

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
    if (!workspaceFilePromise.data || !webToolsEditor?.editor) {
      return;
    }

    const version = saveVersion++;
    console.debug(`Saving @ new version (${saveVersion}).`);

    const content = await webToolsEditor.editor.getContent();

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
    webToolsEditor.editor.getStateControl().setSavedCommand();
  }, [workspaces, webToolsEditor, workspaceFilePromise]);

  useStateControlSubscription(
    webToolsEditor?.editor,
    useCallback(
      (isDirty) => {
        if (!isDirty) {
          return;
        }

        saveContent();
      },
      [saveContent]
    ),
    { throttle: 400 }
  );
  // end (AUTO-SAVE)

  useEffect(() => {
    alertsDispatch.closeAll();
  }, [alertsDispatch]);

  useEffect(() => {
    editorPageDock?.setNotifications(i18n.terms.validation, "", notifications);
  }, [editorPageDock, notifications, i18n]);

  const onNotificationClick = useCallback(
    (notification: Notification) => {
      if (webToolsEditor?.notificationHandler.isSupported) {
        webToolsEditor.notificationHandler.onClick(notification);
      }
    },
    [webToolsEditor]
  );

  return (
    <PromiseStateWrapper
      promise={workspaceFilePromise}
      pending={<LoadingSpinner />}
      rejected={(errors) => <EditorPageErrorPage errors={errors} path={props.fileRelativePath} />}
      resolved={(file) => (
        <>
          <Page>
            <EditorToolbar workspaceFile={file.workspaceFile} editor={webToolsEditor?.editor} />
            <Divider />
            <EditorPageDockDrawer
              ref={editorPageDockRef}
              isEditorReady={isEditorReady}
              workspaceFile={file.workspaceFile}
              onNotificationClick={onNotificationClick}
              isDisabled={!webToolsEditor?.notificationHandler.isSupported}
            >
              <PageSection hasOverflowScroll={true} padding={{ default: "noPadding" }} aria-label="Editor Section">
                <div style={{ height: "100%" }}>
                  {!isEditorReady && <LoadingSpinner />}
                  <div style={{ display: isEditorReady ? "inline" : "none" }}>
                    {embeddedEditorFile && (
                      <WebToolsEmbeddedEditor
                        uniqueFileId={uniqueFileId}
                        ref={webToolsEditorRef}
                        file={embeddedEditorFile}
                        workspaceFile={file.workspaceFile}
                        editorEnvelopeLocator={editorEnvelopeLocator}
                        channelType={ChannelType.ONLINE_MULTI_FILE}
                        locale={locale}
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
  );
}
