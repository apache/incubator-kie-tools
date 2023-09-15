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
import { FeelInput, FeelInputRef } from "@kie-tools/feel-input-component";
import * as React from "react";
import { useCallback, useEffect, useMemo, useRef, useState } from "react";
import { NavigationKeysUtils } from "../../keysUtils";
import "./BeeTableEditableCellContent.css";
import { FeelVariables } from "@kie-tools/dmn-feel-antlr4-parser";

const CELL_LINE_HEIGHT = 20;

const MONACO_OPTIONS: Monaco.editor.IStandaloneEditorConstructionOptions = {
  fixedOverflowWidgets: true,
  lineNumbers: "off",
  fontSize: 13,
  renderLineHighlight: "none",
  lineDecorationsWidth: 1,
  automaticLayout: true,
  "semanticHighlighting.enabled": true,
};

enum Mode {
  Read,
  Edit,
}

export interface BeeTableEditableCellContentProps {
  value: string;
  onChange: (value: string) => void;
  isReadOnly: boolean;
  isEditing: boolean;
  isActive: boolean;
  setEditing: React.Dispatch<React.SetStateAction<boolean>>;
  onFeelTabKeyDown?: (args: { isShiftPressed: boolean }) => void;
  onFeelEnterKeyDown?: (args: { isShiftPressed: boolean }) => void;
  variables?: FeelVariables;
  expressionId?: string;
}

export function BeeTableEditableCellContent({
  value,
  onChange,
  isReadOnly,
  isEditing,
  isActive,
  setEditing,
  onFeelTabKeyDown,
  onFeelEnterKeyDown,
  variables,
  expressionId,
}: BeeTableEditableCellContentProps) {
  const [cellHeight, setCellHeight] = useState(CELL_LINE_HEIGHT * 3);
  const [preview, setPreview] = useState<string>(value);
  const [previousValue, setPreviousValue] = useState(value);
  const [editingValue, setEditingValue] = useState(value);
  const feelInputRef = useRef<FeelInputRef>(null);

  const mode = useMemo(() => {
    return isEditing && !isReadOnly ? Mode.Edit : Mode.Read;
  }, [isEditing, isReadOnly]);

  useEffect(() => {
    setPreviousValue((prev) => (isEditing ? prev : value));
    setEditingValue((prev) => (isEditing ? prev : value));
  }, [isEditing, value]);

  const updateValue = useCallback(
    (newValue: string) => {
      if (value !== newValue) {
        onChange(newValue);
        setCellHeight(calculateCellHeight(newValue));
      }
    },
    [value, onChange]
  );

  const onFeelBlur = useCallback(
    (valueOnBlur: string) => {
      updateValue(valueOnBlur);
      setEditing(false);
    },
    [setEditing, updateValue]
  );

  const onFeelKeyDown = useCallback(
    (e: Monaco.IKeyboardEvent, newValue: string) => {
      const eventKey = e?.code ?? "";

      if (NavigationKeysUtils.isTab(eventKey)) {
        if (feelInputRef.current?.isSuggestionWidgetOpen()) {
          // Do nothing.
        } else {
          updateValue(newValue);
          setEditing(false);
          onFeelTabKeyDown?.({ isShiftPressed: e.shiftKey });
          e.preventDefault();
        }
      }

      if (NavigationKeysUtils.isEnter(eventKey)) {
        if (e.ctrlKey || e.metaKey || e.altKey) {
          feelInputRef.current?.insertNewLineToMonaco();
        } else if (feelInputRef.current?.isSuggestionWidgetOpen()) {
          // Do nothing;
        } else {
          updateValue(newValue);
          setEditing(false);
          onFeelEnterKeyDown?.({ isShiftPressed: e.shiftKey });
        }
      }

      if (NavigationKeysUtils.isEsc(eventKey)) {
        if (feelInputRef.current?.isSuggestionWidgetOpen()) {
          // Do nothing.
        } else {
          // We need to restore the content on Monaco, because when
          // we disable it, it changes the cell content with the
          // last value it had.
          feelInputRef.current?.setMonacoValue(previousValue);
          updateValue(previousValue);
          setEditing(false);
        }
      }
    },
    [updateValue, setEditing, onFeelTabKeyDown, onFeelEnterKeyDown, previousValue]
  );

  useEffect(() => {
    setCellHeight(calculateCellHeight(value));
  }, [value]);

  const onFeelChange = useCallback(
    (_e: Monaco.editor.IModelContentChangedEvent, newValue: string, newPreview: string) => {
      setCellHeight(calculateCellHeight(newValue));
      setPreview(newPreview);
    },
    []
  );

  const editableCellRef = useRef<HTMLDivElement>(null);
  useEffect(() => {
    if (isActive && !isEditing) {
      editableCellRef.current?.focus();
    }
  }, [isActive, isEditing]);

  const cssClass = useMemo(() => {
    return `editable-cell ${mode === Mode.Edit ? "editable-cell--edit-mode" : "editable-cell--read-mode"}`;
  }, [mode]);

  const onKeyDown = useCallback(
    (e) => {
      // When inside FEEL Input, all keyboard events should be kept inside it.
      // Exceptions to this strategy are handled on `onFeelKeyDown`.
      if (isEditing) {
        e.stopPropagation();
      }

      // This is used to start editing a cell without being in edit mode.
      if (isActive && !isEditing && isEditModeTriggeringKey(e)) {
        setEditingValue("");
        setEditing(true);
      }
    },
    [isActive, isEditing, setEditing]
  );

  return (
    <>
      <div
        ref={editableCellRef}
        tabIndex={-1}
        style={{ height: `${cellHeight}px`, outline: "none" }}
        className={cssClass}
        onKeyDown={onKeyDown}
      >
        <span className="editable-cell-value pf-u-text-break-word" dangerouslySetInnerHTML={{ __html: preview }} />
        <span data-ouia-component-id={"editable-cell-raw-value"} className={"editable-cell-raw-value"}>
          {value}
        </span>
        <FeelInput
          ref={feelInputRef}
          enabled={mode === Mode.Edit}
          value={isEditing ? editingValue : value}
          onKeyDown={onFeelKeyDown}
          onChange={onFeelChange}
          onPreviewChanged={setPreview}
          options={MONACO_OPTIONS}
          onBlur={onFeelBlur}
          feelVariables={variables}
          expressionId={expressionId}
        />
      </div>
    </>
  );
}

function isEditModeTriggeringKey(e: React.KeyboardEvent) {
  if (e.altKey || e.ctrlKey || e.metaKey) {
    return false;
  }

  return /^[\s\S]$/.test(e.key);
}

function calculateCellHeight(value: string) {
  const numberOfValueLines = `${value}`.split("\n").length;
  const numberOfLines = numberOfValueLines <= 2 ? 2 : numberOfValueLines;

  // Always add one extra line at the end to compensate for the scrollbar.
  return (numberOfLines + 1) * CELL_LINE_HEIGHT;
}
