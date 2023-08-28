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

import * as Monaco from "@kie-tools-core/monaco-editor";
import * as React from "react";
import { useEffect, useImperativeHandle, useMemo, useRef } from "react";
import {
  feelDefaultConfig,
  feelDefaultSuggestions,
  feelTheme,
  feelTokensConfig,
  MONACO_FEEL_LANGUAGE,
  MONACO_FEEL_THEME,
} from "./FeelConfigs";
import { FeelVariables, VariableType } from "@kie-tools/dmn-language-service";

export type SuggestionProvider = (
  feelExpression: string,
  row: number,
  col: number
) => Monaco.languages.CompletionItem[];

export interface FeelInputProps {
  enabled: boolean;
  value?: string;
  suggestionProvider?: SuggestionProvider;
  onBlur?: (value: string) => void;
  onPreviewChanged?: (preview: string) => void;
  onKeyDown?: (event: Monaco.IKeyboardEvent, value: string) => void;
  onChange?: (event: Monaco.editor.IModelContentChangedEvent, value: string, preview: string) => void;
  options?: Monaco.editor.IStandaloneEditorConstructionOptions;
  feelVariables?: FeelVariables;
  expressionId?: string;
}

export interface FeelInputRef {
  setMonacoValue: (newValue: string) => void;
  getMonacoValue: () => string | undefined;
  /**
   * insert a newline character to the editor in the current position and move the cursor to new position
   */
  insertNewLineToMonaco: () => void;
  isSuggestionWidgetOpen: () => boolean;
}

Monaco.languages.register({
  aliases: [MONACO_FEEL_LANGUAGE, "feel", "feel-dmn"],
  id: MONACO_FEEL_LANGUAGE,
  mimetypes: ["text/feel"],
});

Monaco.languages.setMonarchTokensProvider(MONACO_FEEL_LANGUAGE, feelTokensConfig());

Monaco.editor.defineTheme(MONACO_FEEL_THEME, feelTheme());

// Don't remove this mechanism. It's necessary for Monaco to initialize correctly and display correct colors for FEEL.
let __firstTimeInitializingMonacoToEnableColorizingCorrectly = true;

function getTokenTypeIndex(variableType: VariableType) {
  switch (variableType) {
    default:
    case VariableType.LocalVariable:
    case VariableType.Input:
      return 0;
    case VariableType.Unknown:
      return 1;
    case VariableType.BusinessKnowledgeModel:
      return 2;
    case VariableType.Parameter:
      return 3;
  }
}

export const FeelInput = React.forwardRef<FeelInputRef, FeelInputProps>(
  (
    {
      enabled,
      value,
      suggestionProvider,
      onBlur,
      onPreviewChanged,
      onKeyDown,
      onChange,
      options,
      feelVariables,
      expressionId,
    },
    forwardRef
  ) => {
    const monacoContainer = useRef<HTMLDivElement>(null);

    const monacoRef = useRef<Monaco.editor.IStandaloneCodeEditor>();

    useEffect(() => {
      if (!__firstTimeInitializingMonacoToEnableColorizingCorrectly) {
        return;
      }

      console.info("Registered FEEL language on Monaco Editor to enable correct 'colorize' call.");
      Monaco.editor
        .create(monacoContainer.current!, {
          theme: MONACO_FEEL_THEME,
          language: MONACO_FEEL_LANGUAGE,
          "semanticHighlighting.enabled": true,
        })
        .dispose();
      __firstTimeInitializingMonacoToEnableColorizingCorrectly = false;
    }, []);

    useEffect(() => {
      if (!enabled) {
        return;
      }
      const disposable = Monaco.languages.registerCompletionItemProvider(MONACO_FEEL_LANGUAGE, {
        provideCompletionItems: (model: Monaco.editor.ITextModel, position: Monaco.Position) => {
          let completionItems = feelDefaultSuggestions();

          if (suggestionProvider) {
            const items = suggestionProvider(model.getValue(), position.lineNumber, position.column - 1);
            if (items.length > 0) {
              completionItems = items;
            }
          }

          return {
            suggestions: completionItems,
          };
        },
      });
      return () => {
        disposable.dispose();
      };
    }, [enabled, suggestionProvider]);

    useEffect(() => {
      if (!enabled) {
        return;
      }

      const disposable = Monaco.languages.registerDocumentSemanticTokensProvider(
        { language: MONACO_FEEL_LANGUAGE },
        {
          provideDocumentSemanticTokens: function (model) {
            const tokenTypes = new Array<number>();

            if (feelVariables) {
              const text = model.getValue();

              let lastPosition = 0;
              let offset = 0;
              for (const variable of feelVariables.parser.parse(expressionId ?? "", text)) {
                lastPosition = variable.startIndex;

                tokenTypes.push(
                  0, // lineIndex
                  lastPosition - offset, // columnIndex (it's relative to the PREVIOUS token NOT to the start of the line)
                  variable.length, // tokenLength
                  getTokenTypeIndex(variable.variableType), // token type
                  0 // token modifier
                );
                offset = lastPosition;
              }
            }
            return {
              data: new Uint32Array(tokenTypes),
              resultId: undefined,
            };
          },
          getLegend: function (): Monaco.languages.SemanticTokensLegend {
            return {
              tokenTypes: ["input-data-variable", "unknown-variable", "bkm-variable", "function-parameter-variable"],
              tokenModifiers: [],
            };
          },
          releaseDocumentSemanticTokens: function (resultId: string | undefined): void {
            // do nothing
          },
        }
      );
      return () => {
        disposable.dispose();
      };
    }, [enabled]);

    const config = useMemo(() => {
      return feelDefaultConfig(options);
    }, [options]);

    // This creates the Monaco Editor
    useEffect(() => {
      if (enabled && !monacoRef.current) {
        const element = monacoContainer.current!;
        const editor = Monaco.editor.create(element, config);

        editor.onDidChangeModelContent((event) => {
          const value = editor.getValue();
          Monaco.editor.colorize(value, MONACO_FEEL_LANGUAGE, {}).then((colorizedValue) => {
            onChange?.(event, value, colorizedValue);
          });
        });

        editor.onDidBlurEditorWidget(() => {
          onBlur?.(editor.getValue());
        });

        editor.onKeyDown((e) => {
          onKeyDown?.(e, editor.getValue());
        });

        editor.focus();
        monacoRef.current = editor;
      }
    }, [config, enabled, onBlur, onChange, onKeyDown]);

    // This updates the Monaco Editor instance if the value is changed externally
    useEffect(() => {
      if (enabled) {
        monacoRef.current?.setValue(value ?? "");
        monacoRef.current?.setPosition(calculatePosition(value ?? ""));
      }
    }, [value, enabled]);

    // When the Monaco Editor instance is not enabled anymore, we update the value.
    useEffect(() => {
      if (!enabled && monacoRef.current) {
        onBlur?.(monacoRef.current?.getValue());
        monacoRef.current?.dispose();
        monacoRef.current = undefined;
      }
    }, [enabled, onBlur]);

    useEffect(() => {
      Monaco.editor.colorize(value ?? "", MONACO_FEEL_LANGUAGE, {}).then((colorizedValue) => {
        onPreviewChanged?.(colorizedValue);
      });
    }, [onPreviewChanged, value]);

    useImperativeHandle(forwardRef, () => ({
      setMonacoValue: (newValue: string) => monacoRef.current?.setValue(newValue),
      getMonacoValue: () => monacoRef.current?.getValue(),
      insertNewLineToMonaco: () => monacoRef.current?.trigger("keyboard", "type", { text: "\n" }),
      isSuggestionWidgetOpen: () =>
        (monacoRef.current as any)?._contentWidgets["editor.widget.suggestWidget"]?.position,
    }));

    return (
      <div className="feel-input">
        <div ref={monacoContainer} />
      </div>
    );
  }
);

function calculatePosition(value: string) {
  const lines = value.split("\n");
  const lineNumber = lines.length;
  const column = lines[lineNumber - 1].length + 1;
  return { lineNumber, column };
}
