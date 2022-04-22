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
import { useEffect, useRef } from "react";
import { WorkspaceFile } from "../../workspace/WorkspacesContext";

const importMonacoEditor = () => import(/* webpackChunkName: "monaco-editor" */ "@kie-tools-core/monaco-editor");

interface TextEditorProps {
  file: WorkspaceFile;
  readonly: boolean;
}

export function TextEditor(props: TextEditorProps) {
  const editorContainerRef = useRef<HTMLDivElement>(null);

  useEffect(() => {
    const init = async () => {
      const monaco = await importMonacoEditor();
      const content = await props.file.getFileContentsAsString();
      return monaco.editor.create(editorContainerRef.current!, {
        value: content,
        scrollBeyondLastLine: false,
        language: props.file.extension,
        automaticLayout: true,
        fontSize: 12,
        readOnly: props.readonly,
      });
    };

    let monacoInstance: any;
    init().then((instance) => {
      monacoInstance = instance;
    });

    return () => {
      monacoInstance?.dispose();
    };
  }, [props.file, props.readonly]);

  return (
    <div
      style={{
        width: "100%",
        height: "100%",
        overflow: "hidden",
      }}
      ref={editorContainerRef}
    />
  );
}
