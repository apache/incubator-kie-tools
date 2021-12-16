/*
 * Copyright 2020 Red Hat, Inc. and/or its affiliates.
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

import { FeelInput, FeelInputRef } from "feel-input-component";
import * as Monaco from "monaco-editor";
import "monaco-editor/dev/vs/editor/editor.main.css";
import * as React from "react";
import { useCallback, useEffect, useMemo, useRef, useState } from "react";
import { CellProps } from "../../api";
import { blurActiveElement, focusNextTextArea, focusTextArea, paste } from "./common";
import "./EditableCell.css";
import { useBoxedExpression } from "../../context";

const CELL_LINE_HEIGHT = 20;
const MONACO_OPTIONS: Monaco.editor.IStandaloneEditorConstructionOptions = {
  fixedOverflowWidgets: true,
  lineNumbers: "off",
  fontSize: 13,
  renderLineHighlight: "none",
  lineDecorationsWidth: 1,
  automaticLayout: true,
};

export const READ_MODE = "editable-cell--read-mode";
export const EDIT_MODE = "editable-cell--edit-mode";

export interface EditableCellProps extends CellProps {
  /** Cell's value */
  value: string;
  /** Function executed each time a cell gets updated */
  onCellUpdate: (rowIndex: number, columnId: string, value: string) => void;
  /** Enable/Disable readonly cells */
  readOnly?: boolean;
}

export function EditableCell({ value, rowIndex, columnId, onCellUpdate, readOnly }: EditableCellProps) {
  const [isSelected, setIsSelected] = useState(false);
  const [mode, setMode] = useState(READ_MODE);
  const [cellHeight, setCellHeight] = useState(CELL_LINE_HEIGHT * 3);
  const [preview, setPreview] = useState<string>("");
  const textarea = useRef<HTMLTextAreaElement>(null);
  const [previousValue, setPreviousValue] = useState("");
  const feelInputRef = useRef<FeelInputRef>(null);
  const boxedExpression = useBoxedExpression();

  // Common Handlers =========================================================

  useEffect(() => {
    if (value === "") {
      setPreview("");
    }
  }, [value]);

  const triggerReadMode = useCallback(
    (newValue?: string) => {
      if (mode !== EDIT_MODE) {
        return;
      }

      if (value !== newValue) {
        window.beeApi?.notifyUserAction();
        onCellUpdate(rowIndex, columnId, newValue ?? value);
      }

      focusTextArea(textarea.current);
    },
    [mode, columnId, onCellUpdate, rowIndex, value]
  );

  const triggerEditMode = useCallback(() => {
    setPreviousValue(value);
    blurActiveElement();
    setMode(EDIT_MODE);
  }, [value]);

  const cssClass = useCallback(() => {
    const selectedClass = isSelected ? "editable-cell--selected" : "";
    return `editable-cell ${selectedClass} ${mode}`;
  }, [isSelected, mode]);

  const onFocus = useCallback(() => {
    if (mode === EDIT_MODE) {
      return;
    }
    setIsSelected(true);
    focusTextArea(textarea.current);
  }, [mode]);

  const onClick = useCallback(() => {
    if (document.activeElement !== textarea.current) {
      onFocus();
    }
  }, [onFocus]);

  // TextArea Handlers =======================================================

  const onTextAreaBlur = useCallback(() => setIsSelected(false), []);

  const onTextAreaChange = useCallback(
    (event) => {
      const newValue: string = event.target.value.trim("") ?? "";
      const isPastedValue = newValue.includes("\t") || newValue.includes("\n");

      if (textarea.current && isPastedValue) {
        const pasteValue = newValue.slice(value.length);
        paste(pasteValue, textarea.current, boxedExpression.editorRef.current!);
        triggerReadMode();
        return;
      }

      onCellUpdate(rowIndex, columnId, newValue ?? value);
      triggerEditMode();
    },
    [triggerEditMode, value, triggerReadMode, onCellUpdate, rowIndex, columnId, boxedExpression.editorRef]
  );

  // Feel Handlers ===========================================================

  const onFeelBlur = useCallback(
    (valueOnBlur: string) => {
      triggerReadMode(valueOnBlur);
      setMode(READ_MODE);
    },
    [triggerReadMode]
  );

  const onFeelKeyDown = useCallback(
    (event: Monaco.IKeyboardEvent, newValue: string) => {
      const key = event?.code.toLowerCase() ?? "";
      const isModKey = event.altKey || event.ctrlKey || event.shiftKey;
      const isEnter = isModKey && key === "enter";
      const isTab = key === "tab";
      const isEsc = !!key.match("esc");

      if (isEnter || isTab || isEsc) {
        event.preventDefault();
      }

      if (isEnter || isTab) {
        triggerReadMode(newValue);
        setMode(READ_MODE);
      }

      if (isEsc) {
        feelInputRef.current?.setMonacoValue(previousValue);
        triggerReadMode(previousValue);
        setMode(READ_MODE);
      }

      if (isTab) {
        focusNextTextArea(textarea.current);
      }
    },
    [triggerReadMode, previousValue]
  );

  const onFeelChange = useCallback((_e, newValue, newPreview) => {
    const numberOfValueLines = `${newValue}`.split("\n").length + 1;
    const numberOfLines = numberOfValueLines < 3 ? 3 : numberOfValueLines;
    setCellHeight(numberOfLines * CELL_LINE_HEIGHT);
    setPreview(newPreview);
  }, []);

  const onFeelLoad = useCallback((newPreview) => {
    setPreview(newPreview);
  }, []);

  const textValue = useMemo(() => {
    if (!value) {
      return "";
    }
    if (value !== null && typeof value === "object") {
      return value[columnId];
    }
    return `${value}`;
  }, [value, columnId]);

  return (
    <>
      <div
        onDoubleClick={triggerEditMode}
        onClick={onClick}
        style={{ height: `${cellHeight}px` }}
        className={cssClass()}
      >
        <span className="editable-cell-value" dangerouslySetInnerHTML={{ __html: preview }} />
        <textarea
          data-testid={"editable-cell-textarea"}
          className="editable-cell-textarea"
          ref={textarea}
          value={textValue}
          onChange={onTextAreaChange}
          onFocus={onFocus}
          onBlur={onTextAreaBlur}
          readOnly={readOnly}
        />
        <FeelInput
          ref={feelInputRef}
          enabled={mode === EDIT_MODE}
          value={textValue}
          onKeyDown={onFeelKeyDown}
          onChange={onFeelChange}
          onLoad={onFeelLoad}
          options={MONACO_OPTIONS}
          onBlur={onFeelBlur}
        />
      </div>
    </>
  );
}
