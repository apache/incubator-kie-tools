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
import MonacoEditor, { EditorWillMount } from "react-monaco-editor";
import { CancellationToken, editor, languages, Position } from "monaco-editor/esm/vs/editor/editor.api";
import { EditorDidMount } from "react-monaco-editor/src/types";
import * as monacoEditor from "monaco-editor";
import CompletionItemKind = languages.CompletionItemKind;
import CompletionItemInsertTextRule = languages.CompletionItemInsertTextRule;

interface PredicateEditorProps {
  text: ValidatedType<string | undefined>;
  setText: (_text: ValidatedType<string | undefined>) => void;
  validateText: (text: string | undefined) => boolean;
}

export const PredicateEditor = (props: PredicateEditorProps) => {
  const { text, setText, validateText } = props;

  const monaco = useRef<MonacoEditor>(null);

  const editorWillMount: EditorWillMount = _monaco => {
    const theme: editor.IStandaloneThemeData = {
      base: "vs",
      inherit: false,
      rules: [
        { token: "sc-numeric", foreground: "3232E7" },
        {
          token: "sc-boolean",
          foreground: "26268D",
          fontStyle: "bold"
        },
        {
          token: "sc-string",
          foreground: "2A9343",
          fontStyle: "bold"
        },
        {
          token: "sc-operator",
          foreground: "3232E8"
        },
        {
          token: "sc-keyword",
          foreground: "0000ff",
          fontStyle: "bold"
        }
      ],
      colors: { "editorLineNumber.foreground": "00ff00" }
    };
    const tokens: languages.IMonarchLanguage = {
      tokenizer: {
        root: [
          {
            regex: "[0-9]+",
            action: "sc-numeric"
          },
          {
            regex: "(?:(\\btrue\\b)|(\\bfalse\\b))",
            action: "sc-boolean"
          },
          {
            regex: "True|False",
            action: "sc-keyword"
          },
          {
            regex: '(?:\\"(?:.*?)\\")',
            action: "sc-string"
          },
          {
            regex: "==|!=|<|<=|>|>=|isMissing|isNotMissing",
            action: "sc-operator"
          }
        ]
      }
    };
    const provider: languages.CompletionItemProvider = {
      provideCompletionItems(
        model: editor.ITextModel,
        position: Position,
        context: languages.CompletionContext,
        token: CancellationToken
      ): languages.ProviderResult<languages.CompletionList> {
        return {
          suggestions: [
            {
              label: "True",
              insertText: "True",
              kind: CompletionItemKind.Keyword,
              insertTextRules: CompletionItemInsertTextRule.InsertAsSnippet,
              range: { startLineNumber: 1, endLineNumber: 1, startColumn: 1, endColumn: 1 }
            },
            {
              label: "False",
              insertText: "False",
              kind: CompletionItemKind.Keyword,
              insertTextRules: CompletionItemInsertTextRule.InsertAsSnippet,
              range: { startLineNumber: 1, endLineNumber: 1, startColumn: 1, endColumn: 1 }
            }
          ]
        };
      }
    };
    _monaco.editor.defineTheme("scorecards", theme);
    _monaco.languages.register({ id: "scorecards" });
    _monaco.languages.setMonarchTokensProvider("scorecards", tokens);
    _monaco.languages.registerCompletionItemProvider("scorecards", provider);
  };

  const editorDidMount: EditorDidMount = (editor: monacoEditor.editor.IStandaloneCodeEditor) => {
    editor.focus();
  };

  return (
    <MonacoEditor
      ref={monaco}
      height="150px"
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
      editorWillMount={editorWillMount}
      editorDidMount={editorDidMount}
    />
  );
};
