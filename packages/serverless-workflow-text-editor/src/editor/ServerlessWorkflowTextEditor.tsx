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
import { useCallback, useEffect, useImperativeHandle, useMemo, useRef, useState } from "react";
import { KogitoEdit } from "@kie-tools-core/workspace/dist/api";
import { Notification } from "@kie-tools-core/notifications/dist/api";
import { SwfTextEditorApi, SwfTextEditorOperation } from "../editor/textEditor/SwfTextEditorController";
import { SwfTextEditor } from "../editor/textEditor/SwfTextEditor";
import { ChannelType, EditorTheme, StateControlCommand } from "@kie-tools-core/editor/dist/api";
import { editor } from "monaco-editor";

interface Props {
  /**
   * Delegation for KogitoEditorChannelApi.kogitoEditor_stateControlCommandUpdate(command) to signal to the Channel
   * that the editor is performing an undo/redo operation. Increases the decoupling of the ServerlessWorkflowEditor
   * from the Channel.
   */
  onStateControlCommandUpdate: (command: StateControlCommand) => void;

  /**
   * Delegation for KogitoToolingWorkspaceApi.kogitoWorkspace_newEdit(edit) to signal to the Channel
   * that a change has taken place. Increases the decoupling of the ServerlessWorkflowEditor from the Channel.
   * @param edit An object representing the unique change.
   */
  onNewEdit: (edit: KogitoEdit) => void;

  /**
   * Delegation for NotificationsApi.setNotifications(path, notifications) to report all validation
   * notifications to the Channel that will replace existing notification for the path. Increases the
   * decoupling of the ServerlessWorkflowEditor from the Channel.
   * @param path The path that references the Notification
   * @param notifications List of Notifications
   */
  setNotifications: (path: string, notifications: Notification[]) => void;

  /**
   * ChannelType where the component is running.
   */
  channelType: ChannelType;
  isReadOnly: boolean;
}

export type ServerlessWorkflowEditorRef = {
  setContent(path: string, content: string): Promise<void>;
};

type ServerlessWorkflowEditorContent = {
  originalContent: string;
  path: string;
};

const RefForwardingServerlessWorkflowTextEditor: React.ForwardRefRenderFunction<
  ServerlessWorkflowEditorRef | undefined,
  Props
> = (props, forwardedRef) => {
  const [initialContent, setInitialContent] = useState<ServerlessWorkflowEditorContent | undefined>(undefined);
  const swfTextEditorRef = useRef<SwfTextEditorApi>(null);

  useImperativeHandle(
    forwardedRef,
    () => {
      return {
        setContent: (path: string, newContent: string): Promise<void> => {
          try {
            setInitialContent({
              originalContent: newContent,
              path: path,
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
        undo: (): Promise<void> => {
          return swfTextEditorRef.current?.undo() || Promise.resolve();
        },
        redo: (): Promise<void> => {
          return swfTextEditorRef.current?.redo() || Promise.resolve();
        },
        validate: (): Notification[] => {
          return [];
        },
        setTheme: (theme: EditorTheme): Promise<void> => {
          return swfTextEditorRef.current?.setTheme(theme) || Promise.resolve();
        },
      };
    },
    []
  );

  const setValidationErrors = (errors: editor.IMarker[]) => {
    if (!initialContent) {
      return;
    }
    const notifications: Notification[] = errors.map((error: editor.IMarker) => ({
      type: "PROBLEM",
      path: initialContent.path,
      severity: "ERROR",
      message: `${error.message}`,
      position: {
        startLineNumber: error.startLineNumber,
        startColumn: error.startColumn,
        endLineNumber: error.endLineNumber,
        endColumn: error.endColumn,
      },
    }));
    props.setNotifications(initialContent.path, notifications);
  };

  const isVscode = useCallback(() => {
    return props.channelType === ChannelType.VSCODE_DESKTOP || props.channelType === ChannelType.VSCODE_WEB;
  }, [props.channelType]);

  const onContentChanged = useCallback(
    (newContent: string, operation?: SwfTextEditorOperation) => {
      switch (operation) {
        case SwfTextEditorOperation.EDIT:
          props.onNewEdit(new KogitoEdit(newContent));
          break;
        case SwfTextEditorOperation.UNDO:
          if (!isVscode()) {
            swfTextEditorRef.current?.undo();
          }
          props.onStateControlCommandUpdate(StateControlCommand.UNDO);
          break;
        case SwfTextEditorOperation.REDO:
          if (!isVscode()) {
            swfTextEditorRef.current?.redo();
          }
          props.onStateControlCommandUpdate(StateControlCommand.REDO);
          break;
      }
    },
    [props, isVscode]
  );

  return (
    <>
      {isVscode() ||
        (initialContent && (
          <SwfTextEditor
            channelType={props.channelType}
            content={initialContent.originalContent}
            fileName={initialContent.path}
            onContentChange={onContentChanged}
            setValidationErrors={setValidationErrors}
            ref={swfTextEditorRef}
            isReadOnly={props.isReadOnly}
          />
        ))}
    </>
  );
};

export const ServerlessWorkflowTextEditor = React.forwardRef(RefForwardingServerlessWorkflowTextEditor);
