/*
 * Copyright 2021 Red Hat, Inc. and/or its affiliates.
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
import {
  Drawer,
  DrawerContent,
  DrawerContentBody,
  DrawerPanelBody,
  DrawerPanelContent,
} from "@patternfly/react-core/dist/js/components/Drawer";
import { KogitoEdit } from "@kie-tools-core/workspace/dist/api";
import { Notification } from "@kie-tools-core/notifications/dist/api";
import { Specification } from "@severlessworkflow/sdk-typescript";
import { MermaidDiagram } from "../diagram";
import svgPanZoom from "svg-pan-zoom";
import mermaid from "mermaid";
import { SwfTextEditorController, SwfTextEditorOperation } from "../textEditor/SwfTextEditorController";
import { SwfTextEditor } from "../textEditor/SwfTextEditor";
import { ChannelType, EditorTheme, StateControlCommand } from "@kie-tools-core/editor/dist/api";

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
}

export type ServerlessWorkflowEditorRef = {
  setContent(path: string, content: string): Promise<void>;
};

const RefForwardingServerlessWorkflowEditor: React.ForwardRefRenderFunction<
  ServerlessWorkflowEditorRef | undefined,
  Props
> = (props, forwardedRef) => {
  const [fileContent, setFileContent] = useState<{ content: string; path: string } | undefined>(undefined);
  const [diagramEditorOutOfSync, setDiagramEditorOutOfSync] = useState<boolean>(false);
  const diagramEditorContainerRef = useRef<HTMLDivElement>(null);
  const swfTextEditorRef = useRef<SwfTextEditorController>(null);

  useImperativeHandle(
    forwardedRef,
    () => {
      return {
        setContent: async (path: string, content: string) => {
          try {
            setFileContent({ content, path });
          } catch (e) {
            console.error(e);
            throw e;
          }
        },
        getContent: async () => swfTextEditorRef.current?.getContent(),
        getPreview: async () => diagramEditorContainerRef.current!.innerHTML.replaceAll("<br>", "<br/>"), // Line breaks replaced due to https://github.com/mermaid-js/mermaid/issues/1766,
        undo: async () => swfTextEditorRef.current?.undo(),
        redo: async () => swfTextEditorRef.current?.redo(),
        validate: (): Notification[] => [],
        setTheme: async (theme: EditorTheme) => swfTextEditorRef.current?.setTheme(theme),
      };
    },
    []
  );

  const updateDiagramEditor = useCallback((newContent: string) => {
    try {
      const workflow: Specification.Workflow = Specification.Workflow.fromSource(newContent);
      const mermaidSourceCode = workflow.states ? new MermaidDiagram(workflow).sourceCode() : "";

      if (mermaidSourceCode?.length > 0) {
        diagramEditorContainerRef.current!.innerHTML = mermaidSourceCode;
        diagramEditorContainerRef.current!.removeAttribute("data-processed");
        mermaid.init(diagramEditorContainerRef.current!);
        svgPanZoom(diagramEditorContainerRef.current!.getElementsByTagName("svg")[0]);
        diagramEditorContainerRef.current!.getElementsByTagName("svg")[0].style.maxWidth = "";
        diagramEditorContainerRef.current!.getElementsByTagName("svg")[0].style.height = "100%";
        setDiagramEditorOutOfSync(false);
      } else {
        diagramEditorContainerRef.current!.innerHTML = "Create a workflow to see its preview here.";
        setDiagramEditorOutOfSync(true);
      }
    } catch (e) {
      console.error(e);
      setDiagramEditorOutOfSync(true);
    }
  }, []);

  const onSwfTextEditorContentChanged = useCallback(
    (newContent: string, operation: SwfTextEditorOperation) => {
      if (operation === SwfTextEditorOperation.EDIT) {
        props.onNewEdit.call(undefined, new KogitoEdit(new Date().getTime().toString()));
      } else if (operation === SwfTextEditorOperation.UNDO) {
        props.onStateControlCommandUpdate.call(undefined, StateControlCommand.UNDO);
      } else if (operation === SwfTextEditorOperation.REDO) {
        props.onStateControlCommandUpdate.call(undefined, StateControlCommand.REDO);
      }

      updateDiagramEditor(newContent);
    },
    [props.onNewEdit, props.onStateControlCommandUpdate, updateDiagramEditor]
  );

  useEffect(() => {
    if (fileContent?.content !== undefined) {
      updateDiagramEditor(fileContent.content);
    }
  }, [fileContent?.content, updateDiagramEditor]);

  const panelContent = (
    <DrawerPanelContent isResizable={true} defaultSize={"50%"}>
      <DrawerPanelBody>
        <div
          style={{ height: "100%", textAlign: "center", opacity: diagramEditorOutOfSync ? 0.5 : 1 }}
          ref={diagramEditorContainerRef}
          className={"mermaid"}
        />
      </DrawerPanelBody>
    </DrawerPanelContent>
  );

  return (
    <Drawer isExpanded={true} isInline={true}>
      <DrawerContent panelContent={panelContent}>
        <DrawerContentBody style={{ overflowY: "hidden" }}>
          {fileContent !== undefined && (
            <SwfTextEditor
              channelType={props.channelType}
              content={fileContent.content}
              filePath={fileContent.path}
              onContentChange={onSwfTextEditorContentChanged}
              ref={swfTextEditorRef}
            />
          )}
        </DrawerContentBody>
      </DrawerContent>
    </Drawer>
  );
};

export const ServerlessWorkflowEditor = React.forwardRef(RefForwardingServerlessWorkflowEditor);
