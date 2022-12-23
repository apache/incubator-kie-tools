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

import * as Monaco from "@kie-tools-core/monaco-editor";
import { FeelEditorService, FeelInput, FeelInputRef } from "@kie-tools/feel-input-component";
import * as React from "react";
import { useCallback, useEffect, useMemo, useRef, useState } from "react";
import { NavigationKeysUtils } from "../../keysUtils";
import "./BeeTableEditableCellContent.css";

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
  value: string;
  onChange: (value: string) => void;
  isReadOnly: boolean;
  isEditing: boolean;
  setEditing: React.Dispatch<React.SetStateAction<boolean>>;
}

export function BeeTableEditableCellContent({
  value,
  onChange,
  isReadOnly,
  isEditing,
  setEditing,
}: BeeTableEditableCellContentProps) {
  const [cellHeight, setCellHeight] = useState(CELL_LINE_HEIGHT * 3);
  const [preview, setPreview] = useState<string>(value);
  const [previousValue, setPreviousValue] = useState("");
  const [commandStack, setCommand] = useState<Array<string>>([]);

  const feelInputRef = useRef<FeelInputRef>(null);

  const mode = useMemo(() => {
    return isEditing ? EDIT_MODE : READ_MODE;
  }, [isEditing]);

  useEffect(() => {
    setPreviousValue((prev) => (isEditing ? prev : value));
  }, [isEditing, value]);

  const triggerReadMode = useCallback(
    (newValue: string) => {
      if (mode !== EDIT_MODE) {
        return;
      }

      if (value !== newValue) {
        onChange(newValue);
      }
    },
    [mode, value, onChange]
  );

  // Feel Handlers ===========================================================

  const onFeelBlur = useCallback(
    (valueOnBlur: string) => {
      triggerReadMode(valueOnBlur);
      setEditing(false);
    },
    [setEditing, triggerReadMode]
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
          setEditing(false);
        }
      }

      if (isEnter) {
        if (event.ctrlKey || event.shiftKey) {
          feelInputRef.current?.insertNewLineToMonaco();
        } else if (!FeelEditorService.isSuggestWidgetOpen()) {
          triggerReadMode(newValue);
          setEditing(false);
          // focusLowerCell(textarea.current);
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
          setEditing(false);
          // focusCurrentCell(textarea.current);
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
    [triggerReadMode, setEditing, previousValue, commandStack, onChange]
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
      <div style={{ height: `${cellHeight}px` }} className={`editable-cell ${mode}`}>
        <span className="editable-cell-value" dangerouslySetInnerHTML={{ __html: preview }} />
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
