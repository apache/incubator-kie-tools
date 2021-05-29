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
import { useRef } from "react";
import MonacoEditor from "react-monaco-editor";
import { EditorDidMount } from "react-monaco-editor/src/types";
import { bootstrapMonaco } from "./PredicateEditorSetup";
import * as monacoEditor from "@kie-tooling-core/monaco-editor";

interface PredicateEditorProps {
  text: string | undefined;
  setText: (_text: string | undefined) => void;
}

bootstrapMonaco();

export const PredicateEditor = (props: PredicateEditorProps) => {
  const { text, setText } = props;

  const monaco = useRef<MonacoEditor>(null);

  const editorDidMount: EditorDidMount = (editor: monacoEditor.editor.IStandaloneCodeEditor) => {
    editor.focus();
  };

  return (
    <MonacoEditor
      ref={monaco}
      height="300px"
      language="scorecards"
      theme="scorecards"
      options={{
        glyphMargin: false,
        scrollBeyondLastLine: false,
      }}
      value={text ?? ""}
      onChange={(e) => setText(e)}
      editorDidMount={editorDidMount}
    />
  );
};
