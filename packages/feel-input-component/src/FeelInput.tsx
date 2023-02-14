/*
 * Copyright 2021 Red Hat, Inc. and/or its affiliates.
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

import * as Monaco from "@kie-tools-core/monaco-editor";
import * as React from "react";
import { useCallback, useEffect, useImperativeHandle, useMemo, useRef } from "react";
import {
  feelDefaultConfig,
  feelDefaultSuggestions,
  feelTheme,
  feelTokensConfig,
  MONACO_FEEL_LANGUAGE,
  MONACO_FEEL_THEME,
} from "./FeelConfigs";

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
  id: MONACO_FEEL_LANGUAGE,
  aliases: [MONACO_FEEL_LANGUAGE, "feel", "feel-dmn"],
  mimetypes: ["text/feel"],
});

Monaco.editor.defineTheme(MONACO_FEEL_THEME, feelTheme());

Monaco.languages.setMonarchTokensProvider(MONACO_FEEL_LANGUAGE, feelTokensConfig());

export const FeelInput = React.forwardRef<FeelInputRef, FeelInputProps>(
  ({ enabled, value, suggestionProvider, onBlur, onPreviewChanged, onKeyDown, onChange, options }, forwardRef) => {
    const monacoContainer = useRef<HTMLDivElement>(null);

    const monacoRef = useRef<Monaco.editor.IStandaloneCodeEditor>();

    useEffect(() => {
      Monaco.languages.registerCompletionItemProvider(MONACO_FEEL_LANGUAGE, {
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
    }, [suggestionProvider]);

    const config = useMemo(() => {
      return feelDefaultConfig(options);
    }, [options]);

    useEffect(() => {
      monacoRef.current?.dispose();
      monacoRef.current = undefined;

      if (enabled) {
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

    useEffect(() => {
      if (enabled) {
        monacoRef.current?.setValue(value ?? "");
        monacoRef.current?.setPosition(calculatePosition(value ?? ""));
      }
    }, [value, enabled]);

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
