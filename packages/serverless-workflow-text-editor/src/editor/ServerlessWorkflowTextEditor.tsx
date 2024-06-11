/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
import * as React from "react";
import { useCallback, useImperativeHandle, useRef, useState } from "react";
import { WorkspaceEdit } from "@kie-tools-core/workspace/dist/api";
import { Notification } from "@kie-tools-core/notifications/dist/api";
import { SwfTextEditorApi, SwfTextEditorOperation } from "./textEditor/SwfTextEditorController";
import { SwfTextEditor } from "./textEditor/SwfTextEditor";
import { ChannelType, EditorTheme, StateControlCommand } from "@kie-tools-core/editor/dist/api";
import { editor, Position } from "monaco-editor";

interface Props {
  /**
   * Delegation for KogitoEditorChannelApi.kogitoEditor_stateControlCommandUpdate(command) to signal to the Channel
   * that the editor is performing an undo/redo operation. Increases the decoupling of the ServerlessWorkflowEditor
   * from the Channel.
   */
  onStateControlCommandUpdate: (command: StateControlCommand) => void;

  /**
   * Delegation for WorkspaceChannelApi.kogitoWorkspace_newEdit(edit) to signal to the Channel
   * that a change has taken place. Increases the decoupling of the ServerlessWorkflowEditor from the Channel.
   * @param edit An object representing the unique change.
   */
  onNewEdit: (edit: WorkspaceEdit) => void;

  /**
   * Delegation for NotificationsChannelApi.kogitoNotifications_setNotifications(normalizedPosixPathRelativeToTheWorkspaceRoot, notifications) to report all validation
   * notifications to the Channel that will replace existing notification for the path. Increases the
   * decoupling of the ServerlessWorkflowEditor from the Channel.
   * @param normalizedPosixPathRelativeToTheWorkspaceRoot The path that references the Notification
   * @param notifications List of Notifications
   */
  setNotifications: (normalizedPosixPathRelativeToTheWorkspaceRoot: string, notifications: Notification[]) => void;

  /**
   * ChannelType where the component is running.
   */
  channelType: ChannelType;
  isReadOnly: boolean;
}

export type ServerlessWorkflowEditorRef = {
  setContent(normalizedPosixPathRelativeToTheWorkspaceRoot: string, content: string): Promise<void>;
};

type ServerlessWorkflowEditorContent = {
  originalContent: string;
  normalizedPosixPathRelativeToTheWorkspaceRoot: string;
};

const RefForwardingServerlessWorkflowTextEditor: React.ForwardRefRenderFunction<
  ServerlessWorkflowEditorRef | undefined,
  Props
> = (props, forwardedRef) => {
  const { onStateControlCommandUpdate, onNewEdit } = props;
  const [initialContent, setInitialContent] = useState<ServerlessWorkflowEditorContent | undefined>(undefined);
  const swfTextEditorRef = useRef<SwfTextEditorApi>(null);

  useImperativeHandle(
    forwardedRef,
    () => {
      return {
        setContent: (normalizedPosixPathRelativeToTheWorkspaceRoot: string, newContent: string): Promise<void> => {
          try {
            setInitialContent({
              originalContent: newContent,
              normalizedPosixPathRelativeToTheWorkspaceRoot,
            });
            return Promise.resolve();
          } catch (e) {
            console.error(e);
            return Promise.reject();
          }
        },
        getContent: (): Promise<string> => {
          return Promise.resolve(swfTextEditorRef.current?.getContent() || "");
        },
        getPreview: (): Promise<string> => {
          return Promise.resolve("");
        },
        undo: async (): Promise<void> => {
          if (!swfTextEditorRef.current) {
            return;
          }
          swfTextEditorRef.current.undo();
          onStateControlCommandUpdate(StateControlCommand.UNDO);
        },
        redo: async (): Promise<void> => {
          if (!swfTextEditorRef.current) {
            return;
          }
          swfTextEditorRef.current.redo();
          onStateControlCommandUpdate(StateControlCommand.REDO);
        },
        validate: (): Promise<Notification[]> => {
          return Promise.resolve([]);
        },
        setTheme: (theme: EditorTheme): Promise<void> => {
          return swfTextEditorRef.current?.setTheme(theme) || Promise.resolve();
        },
        moveCursorToNode: (nodeName: string): void => {
          swfTextEditorRef.current?.moveCursorToNode(nodeName);
        },
        moveCursorToPosition: (position: Position): void => {
          swfTextEditorRef.current?.moveCursorToPosition(position);
        },
      };
    },
    [onStateControlCommandUpdate]
  );

  const setValidationErrors = useCallback(
    (errors: editor.IMarker[]) => {
      if (!initialContent) {
        return;
      }

      const notifications: Notification[] = errors.map((error: editor.IMarker) => ({
        type: "PROBLEM",
        normalizedPosixPathRelativeToTheWorkspaceRoot: initialContent.normalizedPosixPathRelativeToTheWorkspaceRoot,
        severity: "ERROR",
        message: `${error.message}`,
        position: {
          startLineNumber: error.startLineNumber,
          startColumn: error.startColumn,
          endLineNumber: error.endLineNumber,
          endColumn: error.endColumn,
        },
      }));
      props.setNotifications.apply(initialContent.normalizedPosixPathRelativeToTheWorkspaceRoot, notifications);
    },
    [initialContent, props.setNotifications]
  );

  const isVscode = useCallback(() => {
    return props.channelType === ChannelType.VSCODE_DESKTOP || props.channelType === ChannelType.VSCODE_WEB;
  }, [props.channelType]);

  const onContentChanged = useCallback(
    (args: { content: string; operation: SwfTextEditorOperation }) => {
      switch (args.operation) {
        case SwfTextEditorOperation.EDIT:
          onNewEdit(new WorkspaceEdit(args.content));
          break;
        case SwfTextEditorOperation.UNDO:
          if (!isVscode()) {
            swfTextEditorRef.current?.undo();
          }
          onStateControlCommandUpdate(StateControlCommand.UNDO);
          break;
        case SwfTextEditorOperation.REDO:
          if (!isVscode()) {
            swfTextEditorRef.current?.redo();
          }
          onStateControlCommandUpdate(StateControlCommand.REDO);
          break;
      }
    },
    [onNewEdit, isVscode, onStateControlCommandUpdate]
  );

  return (
    <>
      {initialContent && (
        <SwfTextEditor
          channelType={props.channelType}
          content={initialContent.originalContent}
          fileName={initialContent.normalizedPosixPathRelativeToTheWorkspaceRoot}
          onContentChange={onContentChanged}
          setValidationErrors={setValidationErrors}
          ref={swfTextEditorRef}
          isReadOnly={props.isReadOnly}
        />
      )}
    </>
  );
};

export const ServerlessWorkflowTextEditor = React.forwardRef(RefForwardingServerlessWorkflowTextEditor);
