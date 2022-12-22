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

export interface BeeTableEditableCellContentProps<R extends object> {
  value: string;
  onChange: (value: string) => void;
  isReadOnly?: boolean;
}

export function BeeTableEditableCellContent<R extends object>({
  value,
  onChange,
  isReadOnly,
}: BeeTableEditableCellContentProps<R>) {
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
        onChange(newValue ?? value);
      }

      focusTextInput(textarea.current);
    },
    [mode, value, onChange]
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
      onChange(newValue ?? value);
    },
    [triggerEditMode, onChange, value, boxedExpressionEditor.editorRef, triggerReadMode]
  );

  const onTextAreaKeyDown = useCallback(
    (e: React.KeyboardEvent<HTMLElement>) => {
      const key = e.key;

      const isDelete = NavigationKeysUtils.isDelete(key);
      const isBackspace = NavigationKeysUtils.isBackspace(key);
      if (isDelete || isBackspace) {
        onChange("");
      }

      const isFiredFromThis = e.currentTarget === e.target;

      if (!isFiredFromThis) {
        return;
      }

      if (!NavigationKeysUtils.isEnter(key)) {
        (e.target as HTMLTextAreaElement).value = "";
      }
    },
    [onChange]
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
      const key = event?.code ?? "";
      const isEnter = NavigationKeysUtils.isEnter(key);
      const isTab = NavigationKeysUtils.isTab(key);
      const isEsc = NavigationKeysUtils.isEscape(key);

      if (isEnter || isTab || isEsc) {
        event.preventDefault();
      }

      if (isTab) {
        // FIXME: Tiago
        // This is a hack. Ideally, this would be treated inside FeelInput.
        // Tab shouldn't move out from the cell if the autocompletion widget is open.
        if (document.querySelector(".suggest-widget.visible")) {
          // Do nothing.
        } else {
          triggerReadMode(newValue);
          setMode(READ_MODE);
          if (!event.shiftKey) {
            //this setTimeout fixes the focus outside of the table when the suggestions opens
            setTimeout(() => focusNextCellByTabKey(textarea.current, 1), 0);
          } else {
            //this setTimeout fixes the focus outside of the table when the suggestions opens
            setTimeout(() => focusPrevCellByTabKey(textarea.current, 1), 0);
          }
        }
      }

      if (isEnter) {
        if (event.ctrlKey || event.shiftKey) {
          feelInputRef.current?.insertNewLineToMonaco();
        } else if (!FeelEditorService.isSuggestWidgetOpen()) {
          triggerReadMode(newValue);
          setMode(READ_MODE);
          focusLowerCell(textarea.current);
        }
      }

      if (isEsc) {
        // FIXME: Tiago
        // This is a hack. Ideally, this would be treated inside FeelInput.
        // Esc shouldn't move out from the cell if the autocompletion widget is open.
        if (document.querySelector(".suggest-widget.visible")) {
          // Do nothing.
        } else {
          feelInputRef.current?.setMonacoValue(previousValue);
          triggerReadMode(previousValue);
          setMode(READ_MODE);
          focusCurrentCell(textarea.current);
        }
      }

      if (event.shiftKey && event.ctrlKey && key === "keyz") {
        const monacoValue = feelInputRef.current?.getMonacoValue() ?? "";
        if (commandStack.length > 0 && monacoValue.length - previousValue.length <= 0) {
          onChange(commandStack[commandStack.length - 1]);
          setCommand([...commandStack.slice(0, -1)]);
        }
      } else if (event.ctrlKey && key === "keyz") {
        const monacoValue = feelInputRef.current?.getMonacoValue() ?? "";
        if (monacoValue.length - previousValue.length >= 0) {
          onChange(previousValue !== monacoValue ? previousValue : "");
          setCommand([...commandStack, monacoValue]);
        }
      }
    },
    [triggerReadMode, previousValue, commandStack, onChange]
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
          value={value}
          onChange={onTextAreaChange}
          onBlur={onTextAreaBlur}
          readOnly={isReadOnly}
          onKeyDown={onTextAreaKeyDown}
        />
        <FeelInput
          ref={feelInputRef}
          enabled={mode === EDIT_MODE}
          value={value}
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
