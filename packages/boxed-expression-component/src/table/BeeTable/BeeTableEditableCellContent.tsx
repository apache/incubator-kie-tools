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

import { FeelInput, FeelInputRef, FeelEditorService } from "@kie-tools/feel-input-component";
import * as Monaco from "@kie-tools-core/monaco-editor";
import * as React from "react";
import { useCallback, useEffect, useMemo, useRef, useState } from "react";
import {
  blurActiveElement,
  focusCurrentCell,
  focusLowerCell,
  focusNextCellByTabKey,
  focusPrevCellByTabKey,
  focusTextInput,
  paste,
} from "./common";
import "./BeeTableEditableCellContent.css";
import { useBoxedExpressionEditor } from "../../expressions/BoxedExpressionEditor/BoxedExpressionEditorContext";
import { NavigationKeysUtils } from "../../keysUtils";

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

export interface BeeTableEditableCellContentProps {
  /** Cell's value */
  value: string;
  /** Function executed each time a cell gets updated */
  onCellUpdate: (rowIndex: number, columnId: string, value: string) => void;
  /** Enable/Disable readonly cells */
  isReadOnly?: boolean;
  rowIndex: number;
  columnId: string;
}

export function BeeTableEditableCellContent({
  value,
  rowIndex,
  columnId,
  onCellUpdate,
  isReadOnly,
}: BeeTableEditableCellContentProps) {
  const [isSelected, setSelected] = useState(false);
  const [mode, setMode] = useState(READ_MODE);
  const [cellHeight, setCellHeight] = useState(CELL_LINE_HEIGHT * 3);
  const [preview, setPreview] = useState<string>("");
  const textarea = useRef<HTMLTextAreaElement>(null);
  const [previousValue, setPreviousValue] = useState("");
  const feelInputRef = useRef<FeelInputRef>(null);
  const boxedExpressionEditor = useBoxedExpressionEditor();
  const [commandStack, setCommand] = useState<Array<string>>([]);

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
        onCellUpdate(rowIndex, columnId, newValue ?? value);
      }

      focusTextInput(textarea.current);
    },
    [mode, columnId, onCellUpdate, rowIndex, value]
  );

  const triggerEditMode = useCallback(() => {
    boxedExpressionEditor.beeGwtService?.notifyUserAction();
    setPreviousValue(value);
    blurActiveElement();
    setMode(EDIT_MODE);
  }, [boxedExpressionEditor.beeGwtService, value]);

  const cssClass = useCallback(() => {
    const selectedClass = isSelected ? "editable-cell--selected" : "";
    return `editable-cell ${selectedClass} ${mode}`;
  }, [isSelected, mode]);

  const onFocus = useCallback(() => {
    if (mode === EDIT_MODE) {
      return;
    }
    setSelected(true);
    focusTextInput(textarea.current);
  }, [mode]);

  const onClick = useCallback(() => {
    if (document.activeElement !== textarea.current) {
      onFocus();
    }
  }, [onFocus]);

  // TextArea Handlers =======================================================

  const onTextAreaBlur = useCallback(() => setSelected(false), []);

  const onTextAreaChange = useCallback(
    (event) => {
      const newValue: string = event.target.value.trim("") ?? "";
      const isPastedValue = newValue.includes("\t") || newValue.includes("\n");

      // event.nativeEvent.inputType==="insertFromPaste" ensure that this block is not executed on cells with newlines inside
      if (textarea.current && isPastedValue && event.nativeEvent.inputType === "insertFromPaste") {
        const pasteValue = newValue.slice(value.length);
        paste(pasteValue, textarea.current, boxedExpressionEditor.editorRef.current!);
        triggerReadMode();
        return;
      }

      triggerEditMode();
      onCellUpdate(rowIndex, columnId, newValue ?? value);
    },
    [triggerEditMode, value, triggerReadMode, onCellUpdate, rowIndex, columnId, boxedExpressionEditor.editorRef]
  );

  const onTextAreaKeyDown = useCallback((e: React.KeyboardEvent<HTMLElement>) => {
    const key = e.key;
    const isFiredFromThis = e.currentTarget === e.target;

    if (!isFiredFromThis) {
      return;
    }

    if (!NavigationKeysUtils.isEnter(key)) {
      (e.target as HTMLTextAreaElement).value = "";
    }
  }, []);

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
      const key = event?.code ?? "";
      const isEnter = NavigationKeysUtils.isEnter(key);
      const isTab = NavigationKeysUtils.isTab(key);
      const isEsc = NavigationKeysUtils.isEscape(key);

      if (isEnter || isTab || isEsc) {
        event.preventDefault();
      }

      if (isTab) {
        triggerReadMode(newValue);
        setMode(READ_MODE);
      }

      if (isEnter) {
        if (event.ctrlKey) {
          feelInputRef.current?.insertNewLineToMonaco();
        } else if (!FeelEditorService.isSuggestWidgetOpen()) {
          triggerReadMode(newValue);
          setMode(READ_MODE);
          focusLowerCell(textarea.current);
        }
      }

      if (isEsc) {
        feelInputRef.current?.setMonacoValue(previousValue);
        triggerReadMode(previousValue);
        setMode(READ_MODE);
        focusCurrentCell(textarea.current);
      }

      if (isTab) {
        if (!event.shiftKey) {
          //this setTimeout fixes the focus outside of the table when the suggestions opens
          setTimeout(() => focusNextCellByTabKey(textarea.current, 1), 0);
        } else {
          //this setTimeout fixes the focus outside of the table when the suggestions opens
          setTimeout(() => focusPrevCellByTabKey(textarea.current, 1), 0);
        }
      }

      if (event.shiftKey && event.ctrlKey && key === "keyz") {
        const monacoValue = feelInputRef.current?.getMonacoValue() ?? "";
        if (commandStack.length > 0 && monacoValue.length - previousValue.length <= 0) {
          onCellUpdate(rowIndex, columnId, commandStack[commandStack.length - 1]);
          setCommand([...commandStack.slice(0, -1)]);
        }
      } else if (event.ctrlKey && key === "keyz") {
        const monacoValue = feelInputRef.current?.getMonacoValue() ?? "";
        if (monacoValue.length - previousValue.length >= 0) {
          onCellUpdate(rowIndex, columnId, previousValue !== monacoValue ? previousValue : "");
          setCommand([...commandStack, monacoValue]);
        }
      }
    },
    [triggerReadMode, previousValue, rowIndex, commandStack, onCellUpdate, columnId]
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
    if (typeof value === "object") {
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
          tabIndex={-1}
          value={textValue}
          onChange={onTextAreaChange}
          onBlur={onTextAreaBlur}
          readOnly={isReadOnly}
          onKeyDown={onTextAreaKeyDown}
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
