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
import { ValidatedType } from "../../../types";
import MonacoEditor from "react-monaco-editor";
import { EditorDidMount } from "react-monaco-editor/src/types";
import * as monacoEditor from "monaco-editor";
import { bootstrapMonaco } from "./PredicateEditorSetup";

interface PredicateEditorProps {
  text: ValidatedType<string | undefined>;
  setText: (_text: ValidatedType<string | undefined>) => void;
  validateText: (text: string | undefined) => boolean;
}

bootstrapMonaco();

export const PredicateEditor = (props: PredicateEditorProps) => {
  const { text, setText, validateText } = props;

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
        scrollBeyondLastLine: false
      }}
      value={text.value ?? ""}
      onChange={e =>
        setText({
          value: e,
          valid: validateText(e)
        })
      }
      editorDidMount={editorDidMount}
    />
  );
};
