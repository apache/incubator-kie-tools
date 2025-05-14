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
import { useBoxedExpressionEditor } from "../BoxedExpressionEditorContext";
import { useCallback, useMemo, useRef } from "react";
import { NavigationKeysUtils } from "../keysUtils/keyUtils";
import "./FeelInputTextbox.css";
import { FeelInput, FeelInputRef } from "@kie-tools/feel-input-component";
import * as Monaco from "@kie-tools-core/monaco-editor";

export interface FeelInputComponentProps {
  value: string;
  onChange: (value: string) => void;
  expressionId?: string;
}

export function FeelInputTextbox({ value, onChange, expressionId }: FeelInputComponentProps) {
  const feelInputRef = useRef<FeelInputRef>(null);

  const MONACO_OPTIONS: Monaco.editor.IStandaloneEditorConstructionOptions = {
    fixedOverflowWidgets: true,
    lineNumbers: "off",
    fontSize: 13,
    renderLineHighlight: "none",
    lineDecorationsWidth: 1,
    automaticLayout: true,
    "semanticHighlighting.enabled": true,
  };

  const { onRequestFeelIdentifiers } = useBoxedExpressionEditor();

  const feelIdentifiers = useMemo(() => {
    return onRequestFeelIdentifiers?.();
  }, [onRequestFeelIdentifiers]);

  const updateValue = useCallback(
    (newValue: string) => {
      if (value !== newValue) {
        onChange(newValue);
      }
    },
    [value, onChange]
  );

  const onFeelKeyDown = useCallback(
    (e: Monaco.IKeyboardEvent, newValue: string) => {
      const eventKey = e?.code ?? "";

      if (NavigationKeysUtils.isTab(eventKey) || NavigationKeysUtils.isEnter(eventKey)) {
        if (feelInputRef.current?.isSuggestionWidgetOpen()) {
          // Do nothing.
        } else {
          updateValue(newValue);
          e.preventDefault();
        }
      }

      if (NavigationKeysUtils.isEsc(eventKey)) {
        if (feelInputRef.current?.isSuggestionWidgetOpen()) {
          // Do nothing.
        } else {
          // We need to restore the content on Monaco, because when
          // we disable it, it changes the cell content with the
          // last value it had.
          feelInputRef.current?.setMonacoValue(value);
          updateValue(value);
        }
      }
    },
    [updateValue, value]
  );

  const onFeelBlur = useCallback(
    (valueOnBlur: string) => {
      updateValue(valueOnBlur);
    },
    [updateValue]
  );

  return (
    <div className="form-control pf-v5-c-form-control feel-input-textbox">
      <FeelInput
        ref={feelInputRef}
        enabled={true}
        value={value}
        onKeyDown={onFeelKeyDown}
        options={MONACO_OPTIONS}
        onBlur={onFeelBlur}
        feelIdentifiers={feelIdentifiers}
        expressionId={expressionId}
      />
    </div>
  );
}
