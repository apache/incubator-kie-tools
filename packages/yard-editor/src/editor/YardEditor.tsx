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
import { useCallback, useImperativeHandle, useMemo, useRef, useState } from "react";
import {
  Drawer,
  DrawerContent,
  DrawerContentBody,
  DrawerPanelBody,
  DrawerPanelContent,
} from "@patternfly/react-core/dist/js/components/Drawer";
import { yardEditorDictionaries, YardEditorI18nContext, yardEditorI18nDefaults } from "../i18n";
import { WorkspaceEdit } from "@kie-tools-core/workspace/dist/api";
import { Notification } from "@kie-tools-core/notifications/dist/api";
import { YardTextEditorApi, YardTextEditorOperation } from "../textEditor/YardTextEditorController";
import { YardTextEditor } from "../textEditor/YardTextEditor";
import { ChannelType, EditorTheme, StateControlCommand } from "@kie-tools-core/editor/dist/api";
import { editor } from "monaco-editor";
import { I18nDictionariesProvider } from "@kie-tools-core/i18n/dist/react-components";
import { YardUIEditor } from "../uiEditor";
import { YardFile } from "../types";
import { Position } from "monaco-editor";
import "./YardEditor.css";
import { deserialize, YardModel } from "../model";

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

export type YardEditorRef = {
  setContent(normalizedPosixPathRelativeToTheWorkspaceRoot: string, content: string): Promise<void>;
  moveCursorToPosition(position: Position): void;
};

const RefForwardingYardEditor: React.ForwardRefRenderFunction<YardEditorRef | undefined, Props> = (
  props,
  forwardedRef
) => {
  const [file, setFile] = useState<YardFile | undefined>(undefined);
  const [yardData, setYardData] = useState<YardModel | undefined>();
  const yardTextEditorRef = useRef<YardTextEditorApi>(null);

  useImperativeHandle(
    forwardedRef,
    () => {
      return {
        setContent: (normalizedPosixPathRelativeToTheWorkspaceRoot: string, newContent: string): Promise<void> => {
          try {
            setFile({
              content: newContent,
              normalizedPosixPathRelativeToTheWorkspaceRoot,
            });
            setYardData(deserialize(newContent));
            return Promise.resolve();
          } catch (e) {
            console.error(e);
            return Promise.reject();
          }
        },
        getContent: (): Promise<string> => {
          return Promise.resolve(yardTextEditorRef.current?.getContent() || "");
        },
        getPreview: (): Promise<string> => {
          return Promise.resolve(""); // Should we define a preview here ?
        },
        undo: (): Promise<void> => {
          return yardTextEditorRef.current?.undo() || Promise.resolve();
        },
        redo: (): Promise<void> => {
          return yardTextEditorRef.current?.redo() || Promise.resolve();
        },
        validate: (): Notification[] => {
          return [];
        },
        setTheme: (theme: EditorTheme): Promise<void> => {
          return yardTextEditorRef.current?.setTheme(theme) || Promise.resolve();
        },
        moveCursorToPosition: (position: Position) => {
          yardTextEditorRef.current?.moveCursorToPosition(position);
        },
      };
    },
    []
  );

  const setValidationErrors = useCallback(
    (errors: editor.IMarker[]) => {
      if (!file) {
        return;
      }
      const notifications: Notification[] = errors.map((error: editor.IMarker) => ({
        type: "PROBLEM",
        normalizedPosixPathRelativeToTheWorkspaceRoot: file.normalizedPosixPathRelativeToTheWorkspaceRoot,
        severity: "ERROR",
        message: `${error.message}`,
        position: {
          startLineNumber: error.startLineNumber,
          startColumn: error.startColumn,
          endLineNumber: error.endLineNumber,
          endColumn: error.endColumn,
        },
      }));
      props.setNotifications.apply(file.normalizedPosixPathRelativeToTheWorkspaceRoot, notifications);
    },
    [file, props.setNotifications]
  );

  const isVscode = useCallback(() => {
    return props.channelType === ChannelType.VSCODE_DESKTOP || props.channelType === ChannelType.VSCODE_WEB;
  }, [props.channelType]);

  const onContentChanged = useCallback(
    (newContent: string, operation?: YardTextEditorOperation) => {
      switch (operation) {
        case YardTextEditorOperation.EDIT:
          props.onNewEdit(new WorkspaceEdit(newContent));
          break;
        case YardTextEditorOperation.UNDO:
          if (!isVscode()) {
            yardTextEditorRef.current?.undo();
          }
          props.onStateControlCommandUpdate(StateControlCommand.UNDO);
          break;
        case YardTextEditorOperation.REDO:
          if (!isVscode()) {
            yardTextEditorRef.current?.redo();
          }
          props.onStateControlCommandUpdate(StateControlCommand.REDO);
          break;
      }
      setYardData(deserialize(newContent));
    },
    [props, isVscode]
  );

  const yardTextEditor = useMemo(
    () =>
      file && (
        <YardTextEditor
          channelType={props.channelType}
          file={file}
          onContentChange={onContentChanged}
          setValidationErrors={setValidationErrors}
          ref={yardTextEditorRef}
          isReadOnly={props.isReadOnly}
        />
      ),
    [file, props.channelType, onContentChanged, setValidationErrors, props.isReadOnly]
  );

  const yardUIContainer = useMemo(
    () =>
      file && (
        <I18nDictionariesProvider
          defaults={yardEditorI18nDefaults}
          dictionaries={yardEditorDictionaries}
          initialLocale={navigator.language}
          ctx={YardEditorI18nContext}
        >
          <YardUIEditor yardData={yardData} isReadOnly={props.isReadOnly} />
        </I18nDictionariesProvider>
      ),
    [file, yardData, props.isReadOnly]
  );

  return (
    <>
      {(isVscode() && yardUIContainer) || (
        <Drawer className={"yard-drawer"} isExpanded={true} isInline={true}>
          <DrawerContent
            panelContent={
              <DrawerPanelContent isResizable={true} defaultSize={"50%"}>
                <DrawerPanelBody>{yardUIContainer}</DrawerPanelBody>
              </DrawerPanelContent>
            }
          >
            <DrawerContentBody className={"drawer-content-body"}>{yardTextEditor}</DrawerContentBody>
          </DrawerContent>
        </Drawer>
      )}
    </>
  );
};

export const YardEditor = React.forwardRef(RefForwardingYardEditor);
