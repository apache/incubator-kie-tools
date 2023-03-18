/*
 * Copyright 2023 Red Hat, Inc. and/or its affiliates.
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
import { Notification } from "@kie-tools-core/notifications/dist/api";
import { StateControl } from "@kie-tools-core/editor/dist/channel";
import { useMemo, useState, ForwardRefRenderFunction, useImperativeHandle, forwardRef, useEffect } from "react";
import { WorkspaceFile } from "@kie-tools-core/workspaces-git-fs/dist/context/WorkspacesContext";
import { SwfChannelComponent } from "./channel/SwfChannelComponent";
import { DashChannelComponent } from "./channel/DashChannelComponent";
import { useController } from "@kie-tools-core/react-hooks/dist/useController";
import { Case, Switch } from "react-if";
import { isDashbuilder, isServerlessWorkflow } from "../extension";
import { ChannelType, KogitoEditorChannelApi } from "@kie-tools-core/editor/dist/api";
import {
  SwfJsonLanguageService,
  SwfYamlLanguageService,
} from "@kie-tools/serverless-workflow-language-service/dist/channel";
import { DashbuilderLanguageService } from "@kie-tools/dashbuilder-language-service/dist/channel";
import { EmbeddedEditor, EmbeddedEditorChannelApiImpl, EmbeddedEditorRef } from "@kie-tools-core/editor/dist/embedded";
import { MessageBusClientApi } from "@kie-tools-core/envelope-bus/dist/api";
import { ServerlessWorkflowCombinedEditorChannelApi } from "@kie-tools/serverless-workflow-combined-editor/dist/api";
import { Props } from "@kie-tools-core/editor/dist/embedded/embedded/EmbeddedEditor";
import { DashbuilderEditorChannelApi } from "@kie-tools/dashbuilder-editor";

type SupportedEditorChannelApi = ServerlessWorkflowCombinedEditorChannelApi | DashbuilderEditorChannelApi;
type SupportedLanguageService = SwfYamlLanguageService | SwfJsonLanguageService | DashbuilderLanguageService;

type NotificationHandler =
  | { isSupported: false }
  | {
      isSupported: true;
      onClick: (notification: Notification) => void;
    };

export interface WebToolsEmbeddedEditorRef {
  isReady: boolean;
  notificationHandler: NotificationHandler;
  editor?: EmbeddedEditorRef;
  languageService?: SupportedLanguageService;
}

export type WebToolsEmbeddedEditorProps = Props & {
  uniqueFileId?: string;
  workspaceFile: WorkspaceFile;
};

export interface EditorChannelComponentRef {
  isReady: boolean;
  notificationHandler: NotificationHandler;
  kogitoEditorChannelApi?: KogitoEditorChannelApi;
  messageBusClient?: MessageBusClientApi<SupportedEditorChannelApi>;
  languageService?: SupportedLanguageService;
}
export type EditorChannelComponentProps = {
  channelApiImpl: EmbeddedEditorChannelApiImpl;
  editor: EmbeddedEditorRef;
  workspaceFile: WorkspaceFile;
};

const RefForwardingWebToolsEmbeddedEditor: ForwardRefRenderFunction<
  WebToolsEmbeddedEditorRef,
  WebToolsEmbeddedEditorProps
> = (props, fowardedRef) => {
  const [isReady, setReady] = useState(false);

  const [editor, editorRef] = useController<EmbeddedEditorRef>();
  const [swfChannelComponent, swfChannelComponentRef] = useController<EditorChannelComponentRef>();
  const [dashChannelComponent, dashChannelComponentRef] = useController<EditorChannelComponentRef>();

  const stateControl = useMemo(() => new StateControl(), [props.file?.getFileContents]);
  const channelApiImpl = useMemo(
    () =>
      new EmbeddedEditorChannelApiImpl(stateControl, props.file, props.locale, {
        kogitoEditor_ready: () => {
          setReady(true);
        },
      }),
    [props.file, props.locale, stateControl]
  );

  const component = useMemo<EditorChannelComponentRef>(() => {
    if (swfChannelComponent) {
      return { ...swfChannelComponent };
    }

    if (dashChannelComponent) {
      return { ...dashChannelComponent };
    }

    return {
      isReady: true,
      notificationHandler: {
        isSupported: false,
      },
    };
  }, [dashChannelComponent, swfChannelComponent]);

  useImperativeHandle(
    fowardedRef,
    () => {
      return {
        editor,
        ...component,
        isReady: isReady && !!component.isReady,
      };
    },
    [component, editor, isReady]
  );

  useEffect(() => {
    if (!component.kogitoEditorChannelApi && props.file && !isReady) {
      setReady(true);
    }
  }, [component, isReady, props.file]);

  return (
    <>
      {editor && (
        <Switch>
          <Case condition={isServerlessWorkflow(props.workspaceFile.name)}>
            <SwfChannelComponent
              ref={swfChannelComponentRef}
              channelApiImpl={channelApiImpl}
              editor={editor}
              workspaceFile={props.workspaceFile}
            />
          </Case>
          <Case condition={isDashbuilder(props.workspaceFile.name)}>
            <DashChannelComponent
              ref={dashChannelComponentRef}
              channelApiImpl={channelApiImpl}
              editor={editor}
              workspaceFile={props.workspaceFile}
            />
          </Case>
        </Switch>
      )}
      <EmbeddedEditor
        /* FIXME: By providing a different `key` everytime, we avoid calling `setContent` twice on the same Editor.
         * This is by design, and after setContent supports multiple calls on the same instance, we can remove that.
         */
        key={props.uniqueFileId}
        ref={editorRef}
        file={props.file}
        editorEnvelopeLocator={props.editorEnvelopeLocator}
        channelType={ChannelType.ONLINE_MULTI_FILE}
        locale={props.locale}
        customChannelApiImpl={component.kogitoEditorChannelApi}
        stateControl={stateControl}
        isReady={isReady}
      />
    </>
  );
};

export const WebToolsEmbeddedEditor = forwardRef(RefForwardingWebToolsEmbeddedEditor);
