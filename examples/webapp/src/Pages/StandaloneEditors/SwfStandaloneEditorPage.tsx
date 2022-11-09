/*
 * Copyright 2022 Red Hat, Inc. and/or its affiliates.
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
import { useEffect, useRef, useState, useCallback } from "react";
import { Page, PageSection } from "@patternfly/react-core";
import * as SwfEditor from "@kie-tools/serverless-workflow-standalone-editor/dist/swf";
import { ServerlessWorkflowEmptyState } from "./SwfEditorEmptyState";
import { StandaloneEditorApi } from "@kie-tools/serverless-workflow-standalone-editor/dist/common/Editor";
import { extname } from "path";

export type ServerlessWorkflowType = "json" | "yaml";

interface Props {
  isDiagramOnlyEditor: boolean;
}

export const SwfStandaloneEditorPage = (props: Props) => {
  const swfEditorContainer = useRef<HTMLDivElement>(null);
  const unsavedChanges = useRef<HTMLSpanElement>(null);
  const undo = useRef<HTMLButtonElement>(null);
  const redo = useRef<HTMLButtonElement>(null);
  const download = useRef<HTMLButtonElement>(null);
  const downloadSvg = useRef<HTMLButtonElement>(null);
  const [workflowType, setWorkflowType] = useState<ServerlessWorkflowType>();
  const [editor, setEditor] = useState<StandaloneEditorApi>();

  const onSetContent = useCallback((path: string, content: string) => {
    const match = /\.sw\.(json|yaml)$/.exec(path.toLowerCase());
    const dotExtension = match ? match[0] : extname(path);
    const extension = dotExtension.slice(1);

    const editorContent = SwfEditor.open({
      container: swfEditorContainer.current!,
      initialContent: Promise.resolve(content),
      readOnly: false,
      languageType: extension as any,
      isDiagramOnly: props.isDiagramOnlyEditor,
    });
    setWorkflowType(extension as ServerlessWorkflowType);
    setEditor(editorContent);
  }, []);
  console.log("editor", editor);
  const onNewContent = useCallback((serverlessWorkflowType: ServerlessWorkflowType) => {
    const editorContent = SwfEditor.open({
      container: swfEditorContainer.current!,
      initialContent: Promise.resolve(""),
      readOnly: false,
      languageType: serverlessWorkflowType,
    });
    setEditor(editorContent);
  }, []);

  useEffect(() => {
    undo.current?.addEventListener("click", () => {
      editor?.undo();
    });

    redo.current?.addEventListener("click", () => {
      editor?.redo();
    });

    download.current?.addEventListener("click", () => {
      editor?.getContent().then((content) => {
        const elem = window.document.createElement("a");
        elem.href = "data:text/plain;charset=utf-8," + encodeURIComponent(content);
        elem.download = `new-serverless-workflow.sw.${workflowType}`;
        document.body.appendChild(elem);
        elem.click();
        document.body.removeChild(elem);
        editor.markAsSaved();
      });
    });

    editor?.subscribeToContentChanges((isDirty) => {
      if (isDirty) {
        unsavedChanges.current!.style.display = "";
      } else {
        unsavedChanges.current!.style.display = "none";
      }
    });
  }, [editor]);

  return (
    <Page>
      {!editor && (
        <PageSection isFilled={true}>
          <ServerlessWorkflowEmptyState
            newContent={onNewContent}
            setContent={onSetContent}
            isDiagramOnly={props.isDiagramOnlyEditor}
          />
        </PageSection>
      )}
      <PageSection padding={{ default: "noPadding" }}>
        {editor && swfEditorContainer && (
          <div style={{ height: "40px", padding: "5px" }}>
            <button ref={undo}>Undo</button>
            <button ref={redo}>Redo</button>
            <button ref={download}>Get Content</button>
            <span ref={unsavedChanges} style={{ display: "none" }}>
              File contains unsaved changes.
            </span>
          </div>
        )}
        <div ref={swfEditorContainer} style={{ height: "calc(100% - 50px)" }} />
      </PageSection>
    </Page>
  );
};
