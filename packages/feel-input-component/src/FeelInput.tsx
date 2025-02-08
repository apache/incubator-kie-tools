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
import { useCallback, useEffect, useImperativeHandle, useMemo, useRef, useState } from "react";
import {
  feelDefaultConfig,
  feelDefaultSuggestions,
  feelTheme,
  feelTokensConfig,
  MONACO_FEEL_LANGUAGE,
  MONACO_FEEL_THEME,
} from "./FeelConfigs";

import { FeelSyntacticSymbolNature, FeelIdentifiers, ParsedExpression } from "@kie-tools/dmn-feel-antlr4-parser";
import { SemanticTokensProvider } from "./semanticTokensProvider";

export const EXPRESSION_PROPERTIES_SEPARATOR = ".";

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
  feelIdentifiers?: FeelIdentifiers;
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
      feelIdentifiers,
      expressionId,
    },
    forwardRef
  ) => {
    const monacoContainer = useRef<HTMLDivElement>(null);

    const monacoRef = useRef<Monaco.editor.IStandaloneCodeEditor>();

    const [currentParsedExpression, setCurrentParsedExpression] = useState<ParsedExpression>();

    const semanticTokensProvider = useMemo(
      () => new SemanticTokensProvider(feelIdentifiers, expressionId, setCurrentParsedExpression),
      [expressionId, feelIdentifiers]
    );

    const getLastValidSymbolAtPosition = useCallback((currentParsedExpression: ParsedExpression, position: number) => {
      let lastValidSymbol;
      for (let i = 0; i < currentParsedExpression.feelIdentifiedSymbols.length; i++) {
        const feelVariable = currentParsedExpression.feelIdentifiedSymbols[i];

        if (feelVariable.startIndex < position && position <= feelVariable.startIndex + feelVariable.length) {
          lastValidSymbol = feelVariable;
          const target = i - 1;
          if (
            target < currentParsedExpression.feelIdentifiedSymbols.length &&
            0 <= target &&
            lastValidSymbol.feelSymbolNature === FeelSyntacticSymbolNature.Unknown
          ) {
            lastValidSymbol = currentParsedExpression.feelIdentifiedSymbols[target];
          }
          break;
        }
      }
      return lastValidSymbol;
    }, []);

    const getSymbolAtPosition = useCallback((currentParsedExpression: ParsedExpression, position: number) => {
      for (const feelVariable of currentParsedExpression.feelIdentifiedSymbols) {
        if (feelVariable.startIndex < position && position <= feelVariable.startIndex + feelVariable.length) {
          return feelVariable;
        }
      }
      return undefined;
    }, []);

    const getDefaultCompletionItems = useCallback(
      (
        suggestionProvider:
          | ((feelExpression: string, row: number, col: number) => Monaco.languages.CompletionItem[])
          | undefined,
        model: Monaco.editor.ITextModel,
        position: Monaco.Position
      ) => {
        if (suggestionProvider) {
          const items = suggestionProvider(model.getValue(), position.lineNumber, position.column - 1);
          if (items.length > 0) {
            return items;
          }
        }
        return feelDefaultSuggestions();
      },
      []
    );

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

    const completionItemProvider = useCallback(() => {
      return {
        triggerCharacters: [EXPRESSION_PROPERTIES_SEPARATOR],
        provideCompletionItems: (model: Monaco.editor.ITextModel, position: Monaco.Position) => {
          const completionItems = getDefaultCompletionItems(suggestionProvider, model, position);
          const variablesSuggestions = new Array<Monaco.languages.CompletionItem>();

          if (currentParsedExpression) {
            let pos = position.column - 1; // The columns start at position 1 not 0
            const expression = model.getValue();

            const currentChar = expression.charAt(pos - 1);
            if (currentChar === EXPRESSION_PROPERTIES_SEPARATOR || currentChar === " ") {
              pos--;
            }

            const lastValidSymbol = getLastValidSymbolAtPosition(currentParsedExpression, pos);

            if (
              lastValidSymbol &&
              lastValidSymbol.feelSymbolNature !== FeelSyntacticSymbolNature.Unknown &&
              expression.charAt(lastValidSymbol.startIndex + lastValidSymbol.length) === "."
            ) {
              for (const scopeSymbol of lastValidSymbol.scopeSymbols) {
                variablesSuggestions.push({
                  kind: Monaco.languages.CompletionItemKind.Variable,
                  label: scopeSymbol.name,
                  insertText: scopeSymbol.name,
                  detail: scopeSymbol.type,
                  range: {
                    startLineNumber: lastValidSymbol.startLine + 1,
                    endLineNumber: lastValidSymbol.endLine + 1,
                    startColumn: lastValidSymbol.startIndex + lastValidSymbol.length + 2, // It is +2 because of the . (dot)
                    endColumn: lastValidSymbol.startIndex + lastValidSymbol.length + 2 + scopeSymbol.name.length,
                  },
                } as Monaco.languages.CompletionItem);
              }
            } else {
              const currentSymbol = getSymbolAtPosition(currentParsedExpression, pos);
              for (const scopeSymbol of currentParsedExpression.availableSymbols) {
                // Consider this scenario:
                // 1. User typed: Tax In
                // 2. Available symbols: [Tax Incoming, Tax Input, Tax InSomethingElse, Tax Out, Tax Output]
                // 3. "Tax In" is an invalid symbol (unrecognized symbol)
                // In this case, we want to show all symbols that starts with "Tax In"
                if (currentSymbol && scopeSymbol.name.startsWith(currentSymbol.text)) {
                  variablesSuggestions.push({
                    kind: Monaco.languages.CompletionItemKind.Variable,
                    label: scopeSymbol.name,
                    insertText: scopeSymbol.name,
                    detail: scopeSymbol.type,
                    sortText: "1", // We want the variables to be at top of autocomplete
                    // We want to replace the current symbol with the available scopeSymbol (Tax Incoming, for example)
                    // Note: Monaco is NOT zero-indexed. It starts with 1 but FEEL parser is zero indexed,
                    // that's why were incrementing 1 at each position.
                    range: {
                      startLineNumber: currentSymbol.startLine + 1,
                      endLineNumber: currentSymbol.endLine + 1,
                      startColumn: currentSymbol.startIndex + 1,
                      endColumn: currentSymbol.startIndex + 1 + scopeSymbol.name.length,
                    },
                  } as Monaco.languages.CompletionItem);
                } else {
                  variablesSuggestions.push({
                    kind: Monaco.languages.CompletionItemKind.Variable,
                    label: scopeSymbol.name,
                    insertText: scopeSymbol.name,
                    detail: scopeSymbol.type,
                    sortText: "2", // The others variables at second level
                  } as Monaco.languages.CompletionItem);
                }
              }

              variablesSuggestions.push(...completionItems);
            }
            return {
              suggestions: variablesSuggestions,
            };
          }
          return {
            suggestions: completionItems,
          };
        },
      };
    }, [
      currentParsedExpression,
      getDefaultCompletionItems,
      getLastValidSymbolAtPosition,
      getSymbolAtPosition,
      suggestionProvider,
    ]);

    useEffect(() => {
      if (!enabled) {
        return;
      }
      const disposable = Monaco.languages.registerCompletionItemProvider(
        MONACO_FEEL_LANGUAGE,
        completionItemProvider()
      );
      return () => {
        disposable.dispose();
      };
    }, [completionItemProvider, currentParsedExpression, enabled, suggestionProvider]);

    useEffect(() => {
      if (!enabled) {
        return;
      }

      const disposable = Monaco.languages.registerDocumentSemanticTokensProvider(
        { language: MONACO_FEEL_LANGUAGE },
        semanticTokensProvider
      );
      return () => {
        disposable.dispose();
      };
    }, [enabled, expressionId, feelIdentifiers, semanticTokensProvider]);

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
        <div ref={monacoContainer} role={"textbox"} data-testid={"monaco-container"} />
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
