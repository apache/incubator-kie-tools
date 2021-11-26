/*
 * Copyright 2020 Red Hat, Inc. and/or its affiliates.
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
import { Page } from "@patternfly/react-core/dist/js/components/Page";
import { HashRouter } from "react-router-dom";
import { KogitoEdit } from "@kie-tooling-core/workspace/dist/api";
import { Notification } from "@kie-tooling-core/notifications/dist/api";
import { Specification } from "@severlessworkflow/sdk-typescript";
import { MermaidDiagram } from "../diagram";
import { useEffect, useImperativeHandle, useRef, useState } from "react";
import { EditorDidMount } from "react-monaco-editor/src/types";
import * as monaco from "@kie-tooling-core/monaco-editor";

declare global {
  interface Window {
    mermaid: any;
  }
}

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
  const [originalContent, setOriginalContent] = useState("");
  const [content, setContent] = useState("");
  const [diagramOutOfSync, setDiagramOutOfSync] = useState(false);
  const mermaidDiv = useRef<HTMLDivElement>(null);
  const monacoEditorContainer = useRef<HTMLDivElement>(null);

  const editorDidMount: EditorDidMount = (editor: monaco.editor.IStandaloneCodeEditor) => {
    editor.focus();
  };

  useImperativeHandle(
    forwardedRef,
    () => {
      return {
        setContent: (path: string, content: string): Promise<void> => {
          try {
            setOriginalContent(content);
            setContent(content);
            return Promise.resolve();
          } catch (e) {
            console.error(e);
            return Promise.reject();
          }
        },
        getContent: () => {
          return content;
        },
        undo: (): Promise<void> => {
          // Monaco undo is bugged
          return Promise.resolve();
        },
        redo: (): Promise<void> => {
          // Monaco redo is bugged
          return Promise.resolve();
        },
        validate: (): Notification[] => {
          return [];
        },
      };
    },
    []
  );

  let monacoInstance: monaco.editor.IStandaloneCodeEditor;

  useEffect(() => {
    monacoInstance = monaco.editor.create(monacoEditorContainer.current!, {
      value: originalContent,
      language: "json",
      scrollBeyondLastLine: false,
      automaticLayout: true,
    });

    monacoInstance.getModel()?.onDidChangeContent((event) => {
      setContent(monacoInstance.getValue());
    });

    props.ready();

    return () => {
      monacoInstance.dispose();
    };
  }, [originalContent]);

  useEffect(() => {
    monacoInstance?.getModel()?.setValue(originalContent);
  }, [originalContent]);

  useEffect(() => {
    try {
      const workflow: Specification.Workflow = Specification.Workflow.fromSource(content);
      const mermaidSourceCode = workflow.states ? new MermaidDiagram(workflow).sourceCode() : "";

      if (mermaidSourceCode?.length > 0) {
        mermaidDiv.current!.innerHTML = mermaidSourceCode;
        mermaidDiv.current!.removeAttribute("data-processed");
        window.mermaid.init(undefined, mermaidDiv.current!);
        setDiagramOutOfSync(false);
      } else {
        setDiagramOutOfSync(true);
      }
    } catch {
      setDiagramOutOfSync(true);
    }
  }, [content]);

  return (
    <HashRouter>
      <Page>
        <div style={{ display: "flex" }}>
          <div style={{ width: "49%", height: "100vh" }} ref={monacoEditorContainer}></div>
          <div
            style={{ width: "49%", height: "100vh", opacity: diagramOutOfSync ? 0.5 : 1 }}
            ref={mermaidDiv}
            className={"mermaid"}
          ></div>
        </div>
      </Page>
    </HashRouter>
  );
};

export const ServerlessWorkflowEditor = React.forwardRef(RefForwardingServerlessWorkflowEditor);
