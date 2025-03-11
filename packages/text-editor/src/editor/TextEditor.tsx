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

import { ChannelType, EditorTheme, StateControlCommand } from "@kie-tools-core/editor/dist/api";
import { Notification } from "@kie-tools-core/notifications/dist/api";
import { WorkspaceEdit } from "@kie-tools-core/workspace/dist/api";
import { editor } from "@kie-tools-core/monaco-editor";
import * as React from "react";
import { useCallback, useImperativeHandle, useRef, useState } from "react";
import { MonacoEditor } from "./monaco";
import { MonacoEditorApi, MonacoEditorOperation } from "./monaco";

interface Props {
  onStateControlCommandUpdate: (command: StateControlCommand) => void;
  onNewEdit: (edit: WorkspaceEdit) => void;
  setNotifications: (normalizedPosixPathRelativeToTheWorkspaceRoot: string, notifications: Notification[]) => void;
  channelType: ChannelType;
  isReadOnly: boolean;
}

export type TextEditorRef = {
  setContent(normalizedPosixPathRelativeToTheWorkspaceRoot: string, content: string): Promise<void>;
};

type TextEditorContent = {
  originalContent: string;
  normalizedPosixPathRelativeToTheWorkspaceRoot: string;
};

const RefForwardingTextEditor: React.ForwardRefRenderFunction<TextEditorRef | undefined, Props> = (
  props,
  forwardedRef
) => {
  const [initialContent, setInitialContent] = useState<TextEditorContent | undefined>(undefined);
  const swfTextEditorRef = useRef<MonacoEditorApi>(null);

  useImperativeHandle(forwardedRef, () => {
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
  }, []);

  const setValidationErrors = (errors: editor.IMarker[]) => {
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
    props.setNotifications(initialContent.normalizedPosixPathRelativeToTheWorkspaceRoot, notifications);
  };

  const isVscode = useCallback(() => {
    return props.channelType === ChannelType.VSCODE_DESKTOP || props.channelType === ChannelType.VSCODE_WEB;
  }, [props.channelType]);

  const onContentChanged = useCallback(
    (newContent: string, operation?: MonacoEditorOperation) => {
      switch (operation) {
        case MonacoEditorOperation.EDIT:
          props.onNewEdit(new WorkspaceEdit(newContent));
          break;
        case MonacoEditorOperation.UNDO:
          if (!isVscode()) {
            swfTextEditorRef.current?.undo();
          }
          props.onStateControlCommandUpdate(StateControlCommand.UNDO);
          break;
        case MonacoEditorOperation.REDO:
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
          <MonacoEditor
            channelType={props.channelType}
            content={initialContent.originalContent}
            fileName={initialContent.normalizedPosixPathRelativeToTheWorkspaceRoot}
            onContentChange={onContentChanged}
            setValidationErrors={setValidationErrors}
            ref={swfTextEditorRef}
            isReadOnly={props.isReadOnly}
          />
        ))}
    </>
  );
};

export const TextEditor = React.forwardRef(RefForwardingTextEditor);
