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
import { useCallback, useEffect, useImperativeHandle, useRef, useState } from "react";
import {
  Drawer,
  DrawerContent,
  DrawerContentBody,
  DrawerPanelBody,
  DrawerPanelContent,
} from "@patternfly/react-core/dist/js/components/Drawer";
import { WorkspaceEdit } from "@kie-tools-core/workspace/dist/api";
import { Notification } from "@kie-tools-core/notifications/dist/api";
import { MonacoEditorOperation, DashbuilderMonacoEditorApi } from "../monaco/DashbuilderMonacoEditorApi";
import { DashbuilderMonacoEditor } from "../monaco/DashbuilderMonacoEditor";
import { ChannelType, EditorTheme, StateControlCommand } from "@kie-tools-core/editor/dist/api";
import { Dashbuilder } from "../dashbuilder/Dashbuilder";
import { Toolbar } from "./Toolbar";
import { Position } from "monaco-editor";

const INITIAL_CONTENT = `datasets:
- uuid: products
  content: >-
            [
              ["Computers", "Scanner", 5, 3],
              ["Computers", "Printer", 7, 4],
              ["Computers", "Laptop", 3, 2],
              ["Electronics", "Camera", 10, 7],
              ["Electronics", "Headphones", 5, 9]
            ]
  columns:
    - id: Section
      type: LABEL
    - id: Product
      type: LABEL
    - id: Quantity
      type: NUMBER
    - id: Quantity2
      type: NUMBER
pages:
- components:
    - html: Welcome to Dashbuilder!
      properties:
        font-size: xx-large
        margin-bottom: 30px
    - settings:
        type: BARCHART
        dataSetLookup:
            uuid: products
            group:
                - columnGroup:
                    source: Product
                  groupFunctions:
                    - source: Product
                    - source: Quantity
                      function: SUM
                    - source: Quantity2
                      function: SUM
    - settings:
        type: TABLE
        dataSetLookup:
            uuid: products`;

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
   * @param normalizedPosixPathRelativeToTheWorkspaceRoot The path that references the Notification
   * @param notifications List of Notifications
   */
  setNotifications: (normalizedPosixPathRelativeToTheWorkspaceRoot: string, notifications: Notification[]) => void;

  /**
   * ChannelType where the component is running.
   */
  channelType: ChannelType;
}

const UPDATE_TIME = 1000;

export type DashbuilderEditorRef = {
  setContent(normalizedPosixPathRelativeToTheWorkspaceRoot: string, content: string): Promise<void>;
  moveCursorToPosition(position: Position): void;
};

const RefForwardingDashbuilderEditor: React.ForwardRefRenderFunction<DashbuilderEditorRef | undefined, Props> = (
  props,
  forwardedRef
) => {
  const [initialContent, setInitialContent] = useState({ originalContent: INITIAL_CONTENT, path: "empty.dash.yml" });
  const [renderContent, setRenderContent] = useState("");
  const [showPreview, setShowPreview] = useState<boolean>(false);
  const dashbuilderMonacoEditorRef = useRef<DashbuilderMonacoEditorApi>(null);

  useEffect(() => {
    const timer = setInterval(() => {
      setRenderContent(dashbuilderMonacoEditorRef.current?.getContent() || "");
    }, UPDATE_TIME);
    return () => clearTimeout(timer);
  }, [renderContent]);

  const isVsCode = useCallback(() => {
    return props.channelType === ChannelType.VSCODE_DESKTOP || props.channelType === ChannelType.VSCODE_WEB;
  }, [props]);

  useImperativeHandle(
    forwardedRef,
    () => {
      return {
        setContent: (normalizedPosixPathRelativeToTheWorkspaceRoot: string, newContent: string): Promise<void> => {
          try {
            setInitialContent({
              originalContent: newContent,
              path: normalizedPosixPathRelativeToTheWorkspaceRoot,
            });
            return Promise.resolve();
          } catch (e) {
            console.error(e);
            return Promise.reject();
          }
        },
        getContent: (): Promise<string> => {
          return Promise.resolve(dashbuilderMonacoEditorRef.current?.getContent() || "");
        },
        getPreview: (): Promise<string> => {
          // TODO: implement it on Dashbuilder
          return Promise.resolve("");
        },
        undo: (): Promise<void> => {
          return dashbuilderMonacoEditorRef.current?.undo() || Promise.resolve();
        },
        redo: (): Promise<void> => {
          return dashbuilderMonacoEditorRef.current?.redo() || Promise.resolve();
        },
        validate: (): Notification[] => {
          return [];
        },
        setTheme: (theme: EditorTheme): Promise<void> => {
          return dashbuilderMonacoEditorRef.current?.setTheme(theme) || Promise.resolve();
        },
        moveCursorToPosition: (position: Position) => {
          dashbuilderMonacoEditorRef.current?.moveCursorToPosition(position);
        },
      };
    },
    []
  );

  const onContentChanged = useCallback(
    (newContent: string, operation?: MonacoEditorOperation) => {
      if (operation === MonacoEditorOperation.EDIT) {
        props.onNewEdit(new WorkspaceEdit(newContent));
      } else if (operation === MonacoEditorOperation.UNDO) {
        if (!isVsCode()) {
          dashbuilderMonacoEditorRef.current?.undo();
        }
        props.onStateControlCommandUpdate(StateControlCommand.UNDO);
      } else if (operation === MonacoEditorOperation.REDO) {
        if (!isVsCode()) {
          dashbuilderMonacoEditorRef.current?.redo();
        }
        props.onStateControlCommandUpdate(StateControlCommand.REDO);
      }
    },
    [props, isVsCode]
  );

  useEffect(() => {
    props.onReady.call(null);
    onContentChanged(initialContent.originalContent);
  }, [initialContent, onContentChanged, props.onReady]);

  const panelContent = (
    <DrawerPanelContent isResizable={true} defaultSize={showPreview ? "100%" : "50%"}>
      <DrawerPanelBody hasNoPadding={true}>
        <Dashbuilder content={renderContent} />
      </DrawerPanelBody>
    </DrawerPanelContent>
  );

  return (
    <div style={{ height: "100vh", position: "relative" }}>
      <Toolbar onPreviewChange={(v) => setShowPreview(v)} preview={showPreview} />
      <Drawer isExpanded={true} isInline={true}>
        <DrawerContent panelContent={panelContent}>
          <DrawerContentBody style={{ overflow: "hidden" }}>
            {initialContent.path !== "" && (
              <DashbuilderMonacoEditor
                channelType={props.channelType}
                content={initialContent.originalContent}
                fileName={initialContent.path}
                onContentChange={onContentChanged}
                ref={dashbuilderMonacoEditorRef}
              />
            )}
          </DrawerContentBody>
        </DrawerContent>
      </Drawer>
    </div>
  );
};

export const DashbuilderEditor = React.forwardRef(RefForwardingDashbuilderEditor);
