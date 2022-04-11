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
import { FeelEditorService, SuggestionProvider } from "../Monaco";

export interface FeelInputProps {
  enabled: boolean;
  value?: string;
  suggestionProvider?: SuggestionProvider;
  onBlur?: (value: string) => void;
  onLoad?: (preview: string) => void;
  onKeyDown?: (event: Monaco.IKeyboardEvent, value: string) => void;
  onChange?: (event: Monaco.editor.IModelContentChangedEvent, value: string, preview: string) => void;
  options?: Monaco.editor.IStandaloneEditorConstructionOptions;
}

export interface FeelInputRef {
  setMonacoValue: (newValue: string) => void;
  getMonacoValue: () => string | undefined;
}

export const FeelInput = React.forwardRef<FeelInputRef, FeelInputProps>(
  ({ enabled, value, suggestionProvider, onBlur, onLoad, onKeyDown, onChange, options }, forwardRef) => {
    const monacoContainer = useRef<HTMLDivElement>(null);

    const calculatePosition = useCallback((value: string) => {
      const lines = value.split("\n");
      const lineNumber = lines.length;
      const column = lines[lineNumber - 1].length + 1;
      return { lineNumber, column };
    }, []);

    useEffect(() => {
      if (enabled) {
        const editor = FeelEditorService.getEditorBuilder(suggestionProvider)
          .withDomElement(monacoContainer.current!)
          .withOnBlur(onBlur)
          .withOnChange(onChange)
          .withOptions(options)
          .withOnKeyDown(onKeyDown)
          .createEditor();

        editor.setValue(value ?? "");
        editor.setPosition(calculatePosition(value ?? ""));
        editor.focus();
      }

      if (value !== undefined && onLoad !== undefined) {
        FeelEditorService.getEditorBuilder(suggestionProvider)
          .withDomElement(monacoContainer.current!)
          .colorize(value)
          .then(onLoad);
      }

      return () => {
        FeelEditorService.dispose();
      };
    }, [enabled, suggestionProvider, value, calculatePosition, options, onLoad, onChange, onKeyDown, onBlur]);

    useImperativeHandle(forwardRef, () => ({
      setMonacoValue: (newValue: string) => FeelEditorService.getStandaloneEditor()?.setValue(newValue),
      getMonacoValue: () => FeelEditorService.getStandaloneEditor()?.getValue(),
    }));

    return (
      <div className="feel-input">
        <div ref={monacoContainer} />
      </div>
    );
  }
);
