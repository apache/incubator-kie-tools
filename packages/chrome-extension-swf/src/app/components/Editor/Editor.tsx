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
import { ChannelType } from "@kie-tools-core/editor/dist/api";
import { EmbeddedEditor, useEditorRef, useStateControlSubscription } from "@kie-tools-core/editor/dist/embedded";
import { Button } from "@patternfly/react-core/dist/js/components/Button";
import { Form, FormGroup } from "@patternfly/react-core/dist/js/components/Form";
import { InputGroup, InputGroupText } from "@patternfly/react-core/dist/js/components/InputGroup";
import { Popover } from "@patternfly/react-core/dist/js/components/Popover";
import { TextInput } from "@patternfly/react-core/dist/js/components/TextInput";
import { Alert } from "@patternfly/react-core/dist/js/components/Alert";
import HelpIcon from "@patternfly/react-icons/dist/js/icons/help-icon";
import { TimesIcon } from "@patternfly/react-icons/dist/js/icons/times-icon";
import { useChromeExtensionI18n } from "../../i18n";
import { SW_JSON_EXTENSION } from "../../openshift/OpenShiftContext";
import { useGlobals } from "../../common/GlobalContext";
import { LoadingSpinner } from "../../common/LoadingSpinner";
import { Page, PageSection, PageSectionVariants } from "@patternfly/react-core/dist/js/components/Page";
import { EditorToolbar } from "./EditorToolbar";
import { useWorkspaceFilePromise } from "../../workspace/hooks/WorkspaceFileHooks";
import { OnlineEditorPage } from "../../pageTemplate/OnlineEditorPage";
import { EmbeddedEditorFile } from "@kie-tools-core/editor/dist/channel";
import { PromiseStateWrapper } from "../../workspace/hooks/PromiseState";
import { EditorPageErrorPage } from "./EditorPageErrorPage";
import { useCancelableEffect, useController, usePrevious } from "../../reactExt/Hooks";
import { Bullseye } from "@patternfly/react-core/dist/js/layouts/Bullseye";
import { Spinner } from "@patternfly/react-core/dist/js/components/Spinner";
import { Text, TextContent, TextVariants } from "@patternfly/react-core/dist/js/components/Text";
import { AlertsController, useAlert } from "../../alerts/Alerts";
import { useWorkspaces } from "../../workspace/WorkspacesContext";
import { ResourceContentRequest, ResourceListRequest } from "@kie-tools-core/workspace/dist/api";
import { useRoutes } from "../../navigation/Hooks";
import { useHistory } from "react-router";

enum FormValiationOptions {
  INITIAL = "INITIAL",
  ERROR = "ERROR",
  SUCCESS = "SUCCESS",
}

export interface ServerlessWorkflowEditorProps {
  workspaceId: string;
  fileRelativePath: string;
  extension?: string;
}

export function ServerlessWorkflowEditor(props: ServerlessWorkflowEditorProps) {
  const globals = useGlobals();
  const routes = useRoutes();
  const history = useHistory();
  const { i18n, locale } = useChromeExtensionI18n();
  const { editor, editorRef } = useEditorRef();
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

  const setContentErrorAlert = useAlert(
    alerts,
    useCallback(() => {
      return (
        <Alert ouiaId="set-content-error-alert" variant="danger" title={i18n.editorPage.alerts.setContentError.title} />
      );
    }, [i18n])
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

      history.replace({
        pathname: routes.workspaceWithFilePath.path({
          workspaceId: file.workspaceId,
          fileRelativePath: file.relativePath,
        }),
      });
    },
    [workspaceFilePromise, workspaces, history, routes]
  );

  const handleSetContentError = useCallback(() => {
    setContentErrorAlert.show();
  }, [setContentErrorAlert]);

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
        resolved={() => (
          <Page>
            <EditorToolbar workspaceFilePromise={workspaceFilePromise} editor={editor} />
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
                      kogitoWorkspace_openFile={handleOpenFile}
                      kogitoWorkspace_resourceContentRequest={handleResourceContentRequest}
                      kogitoWorkspace_resourceListRequest={handleResourceListRequest}
                      kogitoEditor_setContentError={handleSetContentError}
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
