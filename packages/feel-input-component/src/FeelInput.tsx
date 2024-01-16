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

import { FeelSyntacticSymbolNature, FeelVariables, ParsedExpression } from "@kie-tools/dmn-feel-antlr4-parser";
import { Element } from "./themes/Element";

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

function getTokenTypeIndex(symbolType: FeelSyntacticSymbolNature) {
  switch (symbolType) {
    default:
    case FeelSyntacticSymbolNature.LocalVariable:
    case FeelSyntacticSymbolNature.GlobalVariable:
      return Element.InputDataVariable;
    case FeelSyntacticSymbolNature.Unknown:
      return Element.UnknownVariable;
    case FeelSyntacticSymbolNature.Invocable:
      return Element.FunctionCall;
    case FeelSyntacticSymbolNature.Parameter:
      return Element.FunctionParameterVariable;
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

    const [currentParsedExpression, setCurrentParsedExpression] = useState<ParsedExpression>();

    const getLastValidSymbolAtPosition = useCallback((currentParsedExpression: ParsedExpression, position: number) => {
      let lastValidSymbol;
      for (let i = 0; i < currentParsedExpression.feelVariables.length; i++) {
        const feelVariable = currentParsedExpression.feelVariables[i];

        if (feelVariable.startIndex < position && position <= feelVariable.startIndex + feelVariable.length) {
          lastValidSymbol = feelVariable;
          const target = i - 1;
          if (
            target < currentParsedExpression.feelVariables.length &&
            0 <= target &&
            lastValidSymbol.feelSymbolNature === FeelSyntacticSymbolNature.Unknown
          ) {
            lastValidSymbol = currentParsedExpression.feelVariables[target];
          }
          break;
        }
      }
      return lastValidSymbol;
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
        triggerCharacters: [" ", EXPRESSION_PROPERTIES_SEPARATOR],
        provideCompletionItems: (model: Monaco.editor.ITextModel, position: Monaco.Position) => {
          const completionItems = getDefaultCompletionItems(suggestionProvider, model, position);
          const variablesSuggestions = new Array<Monaco.languages.CompletionItem>();

          if (currentParsedExpression) {
            let pos = position.column - 1; // The columns start at position 1 not 0
            const expression = model.getValue();

            const currentChar = expression.charAt(pos - 1);
            if (currentChar === EXPRESSION_PROPERTIES_SEPARATOR) {
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
                } as Monaco.languages.CompletionItem);
              }
            } else {
              for (const scopeSymbol of currentParsedExpression.availableSymbols) {
                variablesSuggestions.push({
                  kind: Monaco.languages.CompletionItemKind.Variable,
                  label: scopeSymbol.name,
                  insertText: scopeSymbol.name,
                  detail: scopeSymbol.type,
                } as Monaco.languages.CompletionItem);
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
    }, [currentParsedExpression, getDefaultCompletionItems, getLastValidSymbolAtPosition, suggestionProvider]);

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
        {
          provideDocumentSemanticTokens: function (model) {
            const tokenTypes = new Array<number>();

            if (feelVariables) {
              const text = model.getValue();
              const contentByLines = model.getLinesContent();
              let startOfPreviousToken = 0;
              let previousLine = 0;
              let lineOffset = 0;
              let currentLine = 0;
              const parsedExpression = feelVariables.parser.parse(expressionId ?? "", text);
              setCurrentParsedExpression(parsedExpression);

              for (const variable of parsedExpression.feelVariables) {
                if (variable.startLine != currentLine) {
                  lineOffset += contentByLines[currentLine].length + 1; // +1 = the line break
                  currentLine = variable.startLine;
                }
                variable.startIndex -= lineOffset;
              }

              for (const variable of parsedExpression.feelVariables) {
                if (previousLine != variable.startLine) {
                  startOfPreviousToken = 0;
                }
                if (variable.startLine === variable.endLine) {
                  tokenTypes.push(
                    variable.startLine - previousLine, // lineIndex = relative to the PREVIOUS line
                    variable.startIndex - startOfPreviousToken, // columnIndex = relative to the start of the PREVIOUS token NOT to the start of the line
                    variable.length,
                    getTokenTypeIndex(variable.feelSymbolNature),
                    0 // token modifier = not used so we keep it 0
                  );

                  previousLine = variable.startLine;
                  startOfPreviousToken = variable.startIndex;
                } else {
                  tokenTypes.push(
                    variable.startLine - previousLine,
                    variable.startIndex - startOfPreviousToken,
                    variable.length,
                    getTokenTypeIndex(variable.feelSymbolNature),
                    0
                  );

                  const length =
                    variable.length - (contentByLines[variable.startLine].length - variable.startIndex + 1); // +1 = the line break

                  tokenTypes.push(
                    variable.endLine - variable.startLine,
                    0,
                    length,
                    getTokenTypeIndex(variable.feelSymbolNature),
                    0
                  );

                  startOfPreviousToken = 0;
                  previousLine = variable.endLine;
                }
              }
            }

            return {
              data: new Uint32Array(tokenTypes),
              resultId: undefined,
            };
          },
          getLegend: function (): Monaco.languages.SemanticTokensLegend {
            return {
              tokenTypes: Object.values(Element).filter((x) => typeof x === "string") as string[],
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
    }, [enabled, expressionId, feelVariables]);

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
