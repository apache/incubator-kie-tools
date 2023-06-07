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
import { When } from "react-if";
import { isOfKind } from "@kie-tools-core/workspaces-git-fs/dist/constants/ExtensionHelper";
import { ChannelType, KogitoEditorChannelApi } from "@kie-tools-core/editor/dist/api";
import {
  SwfJsonLanguageService,
  SwfYamlLanguageService,
} from "@kie-tools/serverless-workflow-language-service/dist/channel";
import { DashbuilderYamlLanguageService } from "@kie-tools/dashbuilder-language-service/dist/channel";
import { EmbeddedEditor, EmbeddedEditorChannelApiImpl, EmbeddedEditorRef } from "@kie-tools-core/editor/dist/embedded";
import { Props } from "@kie-tools-core/editor/dist/embedded/embedded/EmbeddedEditor";

type SupportedLanguageService = SwfYamlLanguageService | SwfJsonLanguageService | DashbuilderYamlLanguageService;

type NotificationHandler =
  | { isSupported: false }
  | {
      isSupported: true;
      onClick: (notification: Notification) => void;
    };

export interface EditorChannelComponentRef {
  isReady: boolean;
  notificationHandler: NotificationHandler;
  kogitoEditorChannelApi?: KogitoEditorChannelApi;
  languageService?: SupportedLanguageService;
}
export type EditorChannelComponentProps = {
  channelApiImpl: EmbeddedEditorChannelApiImpl;
  editor: EmbeddedEditorRef;
  workspaceFile: WorkspaceFile;
};

export type WebToolsEmbeddedEditorRef = Pick<
  EditorChannelComponentRef,
  "isReady" | "notificationHandler" | "languageService"
> & {
  editor?: EmbeddedEditorRef;
};

export type WebToolsEmbeddedEditorProps = Props & {
  uniqueFileId?: string;
  workspaceFile: WorkspaceFile;
};

const RefForwardingWebToolsEmbeddedEditor: ForwardRefRenderFunction<
  WebToolsEmbeddedEditorRef,
  WebToolsEmbeddedEditorProps
> = (props, fowardedRef) => {
  const { file, locale, uniqueFileId, editorEnvelopeLocator, workspaceFile } = { ...props };

  const [isReady, setReady] = useState(false);
  const [editor, editorRef] = useController<EmbeddedEditorRef>();

  const [swfChannelComponent, swfChannelComponentRef] = useController<EditorChannelComponentRef>();
  const [dashChannelComponent, dashChannelComponentRef] = useController<EditorChannelComponentRef>();
  const availableComponents = useMemo(
    () => [swfChannelComponent, dashChannelComponent],
    [dashChannelComponent, swfChannelComponent]
  );

  // Keep getFileContents in the dependency list to update the stateControl instance
  // eslint-disable-next-line react-hooks/exhaustive-deps
  const stateControl = useMemo(() => new StateControl(), [file?.getFileContents]);

  const channelApiImpl = useMemo(
    () =>
      new EmbeddedEditorChannelApiImpl(stateControl, file, locale, {
        kogitoEditor_ready: () => {
          setReady(true);
        },
      }),
    [file, locale, stateControl]
  );

  const channelComponent = useMemo<EditorChannelComponentRef>(() => {
    const loadedComponent = availableComponents.find((c) => !!c);

    if (!loadedComponent) {
      return {
        isReady: true,
        notificationHandler: {
          isSupported: false,
        },
      };
    }

    return { ...loadedComponent };
  }, [availableComponents]);

  useImperativeHandle(
    fowardedRef,
    () => {
      return {
        editor,
        ...channelComponent,
        isReady: isReady && !!channelComponent.isReady,
      };
    },
    [channelComponent, editor, isReady]
  );

  useEffect(() => {
    if (!channelComponent.kogitoEditorChannelApi && file && !isReady) {
      setReady(true);
    }
  }, [channelComponent, isReady, file]);

  return (
    <>
      <EmbeddedEditor
        key={uniqueFileId} /* KOGITO-8892 */
        ref={editorRef}
        file={file}
        editorEnvelopeLocator={editorEnvelopeLocator}
        channelType={ChannelType.ONLINE_MULTI_FILE}
        locale={locale}
        customChannelApiImpl={channelComponent.kogitoEditorChannelApi}
        stateControl={stateControl}
        isReady={isReady}
      />
      {editor && (
        <>
          <When condition={isOfKind("sw", workspaceFile.name)}>
            <SwfChannelComponent
              ref={swfChannelComponentRef}
              channelApiImpl={channelApiImpl}
              editor={editor}
              workspaceFile={workspaceFile}
            />
          </When>
          <When condition={isOfKind("dash", workspaceFile.name)}>
            <DashChannelComponent
              ref={dashChannelComponentRef}
              channelApiImpl={channelApiImpl}
              editor={editor}
              workspaceFile={workspaceFile}
            />
          </When>
        </>
      )}
    </>
  );
};

export const WebToolsEmbeddedEditor = forwardRef(RefForwardingWebToolsEmbeddedEditor);
