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

import * as Monaco from "monaco-editor";
import * as React from "react";
import { useCallback, useEffect, useMemo, useRef } from "react";
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

export const FeelInput: React.FunctionComponent<FeelInputProps> = ({
  enabled,
  value,
  suggestionProvider,
  onBlur,
  onLoad,
  onKeyDown,
  onChange,
  options,
}: FeelInputProps) => {
  const monacoContainer = useRef<HTMLDivElement>(null);

  const editorService = useCallback(() => FeelEditorService.getEditorBuilder(suggestionProvider), [suggestionProvider]);
  const dispose = useCallback(() => FeelEditorService.dispose(), []);
  const isInitialized = useCallback(() => FeelEditorService.isInitialized(), []);

  const calculatePosition = useCallback((value: string) => {
    const lines = value.split("\n");
    const lineNumber = lines.length;
    const column = lines[lineNumber - 1].length + 1;

    return { lineNumber, column };
  }, []);

  const colorizeOnLoad = useCallback(() => {
    if (!value || !onLoad) {
      return;
    }
    editorService().withDomElement(monacoContainer.current!).colorize(value).then(onLoad);
  }, [editorService, value, onLoad]);

  const initializeEditor = useCallback(
    (value: string) => {
      const editor = editorService()
        .withDomElement(monacoContainer.current!)
        .withOnBlur(onBlur)
        .withOnChange(onChange)
        .withOptions(options)
        .withOnKeyDown(onKeyDown)
        .createEditor();

      editor.setValue(value);
      editor.setPosition(calculatePosition(value));
      editor.focus();
    },
    [monacoContainer, calculatePosition, editorService, onBlur, onChange, options, onKeyDown]
  );

  useEffect(() => {
    if (enabled) {
      if (!isInitialized()) {
        initializeEditor(value || "");
      }
      return;
    }

    colorizeOnLoad();

    if (isInitialized()) {
      dispose();
    }
  }, [enabled, initializeEditor, colorizeOnLoad, dispose, isInitialized, value]);

  return useMemo(
    () => (
      <div className="feel-input">
        <div ref={monacoContainer}></div>
      </div>
    ),
    [monacoContainer]
  );
};
