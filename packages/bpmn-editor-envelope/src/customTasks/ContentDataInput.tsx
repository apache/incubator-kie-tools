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
import * as Monaco from "@kie-tools-core/monaco-editor";
import { useCallback, useEffect, useRef } from "react";

const MONACO_LANGUAGE_ID = "contentDataLang";
const MONACO_THEME_ID = "contentDataTheme";

Monaco.languages.register({ id: MONACO_LANGUAGE_ID });

Monaco.languages.setMonarchTokensProvider(MONACO_LANGUAGE_ID, {
  tokenizer: {
    root: [[/#\{\s*[\w.-]+\s*\}/, "variable-token"]],
  },
});

Monaco.editor.defineTheme(MONACO_THEME_ID, {
  base: "vs",
  inherit: true,
  rules: [{ token: "variable-token", foreground: "0088FF" }],
  colors: {},
});

const MONACO_OPTIONS: Monaco.editor.IStandaloneEditorConstructionOptions = {
  fixedOverflowWidgets: false,
  lineNumbers: "off",
  fontSize: 13,
  renderLineHighlight: "none",
  lineDecorationsWidth: 1,
  automaticLayout: true,
  minimap: { enabled: false },
  overviewRulerLanes: 0,
  hideCursorInOverviewRuler: true,
  overviewRulerBorder: false,
  wordWrap: "on",
  wrappingStrategy: "advanced",
  scrollBeyondLastColumn: 0,
  suggest: {
    showIcons: true,
  },
};

interface ContentDataInputProps extends Omit<React.HTMLAttributes<HTMLDivElement>, "onChange" | "onBlur"> {
  value?: string;
  onChange: (value: string) => void;
  onBlur?: (value: string) => void;
  variableSuggestions?: string[];
  isDisabled?: boolean;
  placeholder?: string;
}

export const ContentDataInput: React.FC<ContentDataInputProps> = ({
  value = "",
  onChange,
  onBlur,
  variableSuggestions = [],
  isDisabled = false,
  placeholder,
  ...divProps
}) => {
  const monacoContainer = useRef<HTMLDivElement>(null);
  const monacoRef = useRef<Monaco.editor.IStandaloneCodeEditor>();
  const callbacksRef = useRef({ onChange, onBlur });
  callbacksRef.current = { onChange, onBlur };

  const completionItemProvider = useCallback(
    (): Monaco.languages.CompletionItemProvider => ({
      triggerCharacters: ["{"],
      provideCompletionItems: (model, position) => {
        const textUntilPosition = model.getValueInRange({
          startLineNumber: position.lineNumber,
          startColumn: 1,
          endLineNumber: position.lineNumber,
          endColumn: position.column,
        });

        const variableMatch = textUntilPosition.match(/#\{([\w\s.-]*)$/);
        if (!variableMatch) {
          return { suggestions: [] };
        }

        const partialVariableName = variableMatch[1].trim() || "";

        if (!variableSuggestions || variableSuggestions.length === 0) {
          return { suggestions: [] };
        }

        const suggestions = variableSuggestions
          .filter((variableName) => variableName.toLowerCase().startsWith(partialVariableName.toLowerCase()))
          .map(
            (variableName) =>
              ({
                label: variableName,
                kind: Monaco.languages.CompletionItemKind.Variable,
                insertText: variableName + "}",
                insertTextRules: Monaco.languages.CompletionItemInsertTextRule.InsertAsSnippet,
                range: {
                  startLineNumber: position.lineNumber,
                  endLineNumber: position.lineNumber,
                  startColumn: position.column - partialVariableName.length,
                  endColumn: position.column,
                },
                documentation: `Process variable: ${variableName}`,
                sortText: `0${variableName}`,
              }) as Monaco.languages.CompletionItem
          );

        return { suggestions };
      },
    }),
    [variableSuggestions]
  );

  useEffect(() => {
    const disposable = Monaco.languages.registerCompletionItemProvider(MONACO_LANGUAGE_ID, completionItemProvider());
    return () => {
      disposable.dispose();
    };
  }, [completionItemProvider]);

  useEffect(() => {
    if (monacoContainer.current && !monacoRef.current) {
      const editor = Monaco.editor.create(monacoContainer.current, {
        ...MONACO_OPTIONS,
        value: value,
        language: MONACO_LANGUAGE_ID,
        theme: MONACO_THEME_ID,
        readOnly: isDisabled,
      });

      editor.onDidChangeModelContent(() => {
        callbacksRef.current.onChange?.(editor.getValue());
      });
      editor.onDidBlurEditorWidget(() => {
        callbacksRef.current.onBlur?.(editor.getValue());
      });

      monacoRef.current = editor;
    }

    return () => {
      if (monacoRef.current) {
        monacoRef.current.dispose();
        monacoRef.current = undefined;
      }
    };
  }, []);

  useEffect(() => {
    if (monacoRef.current && monacoRef.current.getValue() !== value) {
      monacoRef.current.setValue(value);
    }
  }, [value]);

  useEffect(() => {
    if (monacoRef.current) {
      monacoRef.current.updateOptions({ readOnly: isDisabled });
    }
  }, [isDisabled]);

  return (
    <div
      {...divProps}
      ref={monacoContainer}
      role="textbox"
      data-testid="content-data-input-container"
      className={`pf-v5-c-form-control ${divProps.className || ""}`}
      style={{
        minHeight: "200px",
        height: "100%",
        paddingTop: "1rem",
        paddingLeft: "0.1rem",
        paddingRight: "0.1rem",
        position: "relative",
        ...divProps.style,
      }}
    />
  );
};
