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
import { useCallback, useEffect, useRef, useState } from "react";
import { FileLoader, UploadedFile } from "./FileLoader";
import { ContentType } from "@kie-tools-core/workspace/dist/api";
import { Editor, StandaloneEditorApi } from "@kie-tools/kie-editors-standalone/dist/common/Editor";

export interface Props {
  id: string;
  initialContent: Promise<string>;
  readOnly: boolean;
  origin: string;
  resources?: Map<string, { contentType: ContentType; content: Promise<string> }>;
}

export type InternalProps = Props & {
  openEditor: Editor["open"];
  defaultModelName?: string;
};

export const EditorComponent = ({
  id,
  initialContent,
  openEditor,
  origin,
  readOnly,
  defaultModelName,
  resources,
}: InternalProps) => {
  const [isDirty, setDirty] = useState(false);
  const editorRef = useRef<StandaloneEditorApi>(null);
  const [modelName, setModelName] = useState(defaultModelName ?? "Untitled");
  const [files, setFiles] = useState<UploadedFile[]>([]);

  const editorContainerDivRef = useRef<HTMLDivElement>(null);

  useEffect(() => {
    const e = openEditor({
      container: editorContainerDivRef.current!,
      initialContent: initialContent,
      readOnly: readOnly,
      origin: origin,
      resources: resources,
    });

    e.subscribeToContentChanges(setDirty);
    (editorRef as any).current = e;

    return () => {
      e.close();
    };
  }, [id, readOnly, origin, resources, initialContent, openEditor]);

  const setEditorContents = useCallback((resource: UploadedFile) => {
    editorRef.current?.setContent(
      resource.value.normalizedPosixPathRelativeToTheWorkspaceRoot,
      resource.value.content ?? ""
    );
    setModelName(resource.name);
  }, []);

  const editorUndo = () => {
    editorRef.current?.undo();
  };

  const editorRedo = () => {
    editorRef.current?.redo();
  };

  const editorSave = async () => {
    const content = await editorRef.current?.getContent();
    setFiles([
      ...files,
      {
        name: modelName,
        value: {
          normalizedPosixPathRelativeToTheWorkspaceRoot: modelName,
          type: ContentType.TEXT,
          content,
        },
      },
    ]);
    editorRef.current?.markAsSaved();
  };

  const downloadSvg = () => {
    editorRef.current?.getPreview().then((content) => {
      const elem = window.document.createElement("a");
      elem.href = "data:text/svg+xml;charset=utf-8," + encodeURIComponent(content!);
      elem.download = modelName + ".svg";
      document.body.appendChild(elem);
      elem.click();
      document.body.removeChild(elem);
    });
  };

  const downloadXml = () => {
    editorRef.current?.getContent().then((content) => {
      const elem = window.document.createElement("a");
      elem.href = "data:text/plain;charset=utf-8," + encodeURIComponent(content);
      elem.download = modelName;
      document.body.appendChild(elem);
      elem.click();
      document.body.removeChild(elem);
    });
  };

  const buttons = (
    <div id="buttons" style={{ flex: "0 1 auto" }}>
      <button id="undo" onClick={editorUndo} disabled={!isDirty}>
        undo
      </button>
      <button id="redo" onClick={editorRedo} disabled={!isDirty}>
        redo
      </button>
      <button id="save" onClick={editorSave} disabled={!isDirty}>
        save
      </button>
      <button id="xml" onClick={downloadXml}>
        Download XML
      </button>
      <button id="svg" onClick={downloadSvg}>
        Download SVG
      </button>
    </div>
  );

  return (
    <>
      <FileLoader
        allowDownload={true}
        allowUpload={true}
        onView={setEditorContents}
        files={files}
        setFiles={setFiles}
        ouiaId={id}
      />
      {isDirty && (
        <div id="dirty" data-ouia-component-type="content-dirty">
          Unsaved changes.
        </div>
      )}
      {buttons}
      <div
        id={id}
        data-ouia-component-type="editor"
        data-ouia-component-id={id}
        ref={editorContainerDivRef}
        style={{ flex: "1 1 auto" }}
      />
    </>
  );
};
