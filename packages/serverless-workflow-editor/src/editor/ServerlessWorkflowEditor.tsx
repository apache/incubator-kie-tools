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
import { useEffect, useImperativeHandle, useRef, useState } from "react";
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
import { MonacoEditor, MonacoEditorRef } from "../monaco/MonacoEditor";

interface Props {
  /**
   * Delegation for KogitoEditorChannelApi.kogitoEditor_ready() to signal to the Channel
   * that the editor is ready. Increases the decoupling of the ServerlessWorkflowEditor from the Channel.
   */
  ready: () => void;

  /**
   * Delegation for KogitoToolingWorkspaceApi.kogitoWorkspace_newEdit(edit) to signal to the Channel
   * that a change has taken place. Increases the decoupling of the ServerlessWorkflowEditor from the Channel.
   * @param edit An object representing the unique change.
   */
  newEdit: (edit: KogitoEdit) => void;

  /**
   * Delegation for NotificationsApi.setNotifications(path, notifications) to report all validation
   * notifications to the Channel that will replace existing notification for the path. Increases the
   * decoupling of the ServerlessWorkflowEditor from the Channel.
   * @param path The path that references the Notification
   * @param notifications List of Notifications
   */
  setNotifications: (path: string, notifications: Notification[]) => void;
}

export type ServerlessWorkflowEditorRef = {
  setContent(path: string, content: string): Promise<void>;
};

const RefForwardingServerlessWorkflowEditor: React.ForwardRefRenderFunction<
  ServerlessWorkflowEditorRef | undefined,
  Props
> = (props, forwardedRef) => {
  const [originalContent, setOriginalContent] = useState<string>("");
  const [path, setPath] = useState<string>("");
  const [content, setContent] = useState<string>("");
  const [diagramOutOfSync, setDiagramOutOfSync] = useState<boolean>(false);
  const svgContainer = useRef<HTMLDivElement>(null);
  const monacoEditorRef = useRef<MonacoEditorRef>(null);

  useImperativeHandle(
    forwardedRef,
    () => {
      return {
        setContent: (path: string, newContent: string): Promise<void> => {
          try {
            setOriginalContent(newContent);
            setContent(newContent);
            setPath(path);
            return Promise.resolve();
          } catch (e) {
            console.error(e);
            return Promise.reject();
          }
        },
        getContent: (): Promise<string> => {
          return Promise.resolve(content);
        },
        getPreview: (): Promise<string> => {
          // Line breaks replaced due to https://github.com/mermaid-js/mermaid/issues/1766
          const svgContent = svgContainer.current!.innerHTML.replaceAll("<br>", "<br/>");
          return Promise.resolve(svgContent);
        },
        undo: (): Promise<void> => {
          return monacoEditorRef.current?.undo() || Promise.resolve();
        },
        redo: (): Promise<void> => {
          return monacoEditorRef.current?.redo() || Promise.resolve();
        },
        validate: (): Notification[] => {
          return [];
        },
      };
    },
    [content]
  );

  useEffect(() => {
    props.ready();
  }, [originalContent, props]);

  useEffect(() => {
    try {
      const workflow: Specification.Workflow = Specification.Workflow.fromSource(content);
      const mermaidSourceCode = workflow.states ? new MermaidDiagram(workflow).sourceCode() : "";

      if (mermaidSourceCode?.length > 0) {
        svgContainer.current!.innerHTML = mermaidSourceCode;
        svgContainer.current!.removeAttribute("data-processed");
        mermaid.init(svgContainer.current!);
        svgContainer.current!.getElementsByTagName("svg")[0].setAttribute("style", "height: 100%;");
        svgPanZoom(svgContainer.current!.getElementsByTagName("svg")[0]);
        setDiagramOutOfSync(false);
      } else {
        setDiagramOutOfSync(true);
      }
    } catch (e) {
      console.error(e);
      setDiagramOutOfSync(true);
    }
  }, [content]);

  const panelContent = (
    <DrawerPanelContent isResizable={true} defaultSize={"50%"}>
      <DrawerPanelBody>
        <div style={{ height: "100%", opacity: diagramOutOfSync ? 0.5 : 1 }} ref={svgContainer} className={"mermaid"} />
      </DrawerPanelBody>
    </DrawerPanelContent>
  );

  return (
    <Drawer isExpanded={true} isInline={true}>
      <DrawerContent panelContent={panelContent}>
        <DrawerContentBody style={{ overflowY: "hidden" }}>
          {path !== "" && (
            <MonacoEditor
              content={originalContent}
              fileName={path}
              onContentChange={setContent}
              ref={monacoEditorRef}
            />
          )}
        </DrawerContentBody>
      </DrawerContent>
    </Drawer>
  );
};

export const ServerlessWorkflowEditor = React.forwardRef(RefForwardingServerlessWorkflowEditor);
