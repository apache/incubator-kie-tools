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
import { useCallback, useEffect, useImperativeHandle, useRef, useState } from "react";
import { WorkspaceEdit } from "@kie-tools-core/workspace/dist/api";
import { Notification } from "@kie-tools-core/notifications/dist/api";
import { ChannelType, EditorTheme, StateControlCommand } from "@kie-tools-core/editor/dist/api";
import { Dashbuilder } from "../dashbuilder/Dashbuilder";

interface Props {
  /**
   * Delegation for KogitoEditorChannelApi.kogitoEditor_ready() to signal to the Channel
   * that the editor is ready. Increases the decoupling of the DashbuilderEditor from the Channel.
   */
  onReady: () => void;

  /**
   * Delegation for KogitoEditorChannelApi.kogitoEditor_stateControlCommandUpdate(command) to signal to the Channel
   * that the editor is performing an undo/redo operation. Increases the decoupling of the DashbuilderEditor
   * from the Channel.
   */
  onStateControlCommandUpdate: (command: StateControlCommand) => void;

  /**
   * Delegation for WorkspaceChannelApi.kogitoWorkspace_newEdit(edit) to signal to the Channel
   * that a change has taken place. Increases the decoupling of the DashbuilderEditor from the Channel.
   * @param edit An object representing the unique change.
   */
  onNewEdit: (edit: WorkspaceEdit) => void;

  /**
   * Delegation for NotificationsChannelApi.kogigotNotifications_setNotifications(path, notifications) to report all validation
   * notifications to the Channel that will replace existing notification for the path. Increases the
   * decoupling of the DashbuilderEditor from the Channel.
   * @param path The path that references the Notification
   * @param notifications List of Notifications
   */
  setNotifications: (path: string, notifications: Notification[]) => void;

  /**
   * ChannelType where the component is running.
   */
  channelType: ChannelType;
}

export type DashbuilderEditorViewRef = {
  setContent(path: string, content: string): Promise<void>;
};

const RefForwardingDashbuilderViewer: React.ForwardRefRenderFunction<DashbuilderEditorViewRef | undefined, Props> = (
  props,
  forwardedRef
) => {
  const [renderContent, setRenderContent] = useState("");

  useImperativeHandle(
    forwardedRef,
    () => {
      return {
        setContent: (path: string, newContent: string): Promise<void> => {
          try {
            setRenderContent(newContent);
            return Promise.resolve();
          } catch (e) {
            console.error(e);
            return Promise.reject();
          }
        },
        getContent: (): Promise<string> => {
          return Promise.resolve(renderContent);
        },
        getPreview: (): Promise<string> => {
          return Promise.resolve("");
        },
        undo: (): Promise<void> => {
          return Promise.resolve();
        },
        redo: (): Promise<void> => {
          return Promise.resolve();
        },
        validate: (): Notification[] => {
          return [];
        },
        setTheme: (theme: EditorTheme): Promise<void> => {
          return Promise.resolve();
        },
      };
    },
    []
  );

  useEffect(() => {
    props.onReady.call(null);
  }, [props.onReady]);

  return (
    <div style={{ height: "100vh", position: "relative" }}>
      <Dashbuilder content={renderContent} />
    </div>
  );
};

export const DashbuilderViewer = React.forwardRef(RefForwardingDashbuilderViewer);
