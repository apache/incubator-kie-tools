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

import { FeelInput } from "feel-input-component";
import * as Monaco from "monaco-editor";
import "monaco-editor/dev/vs/editor/editor.main.css";
import * as React from "react";
import { useCallback, useEffect, useMemo, useRef, useState } from "react";
import { CellProps } from "../../api";
import { blurActiveElement, focusNextTextArea, focusTextArea } from "./common/FocusUtils";
import "./EditableCell.css";

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
}

export const EditableCell: React.FunctionComponent<EditableCellProps> = React.memo(
  ({ value: initialValue, row: { index }, column: { id }, onCellUpdate }: EditableCellProps) => {
    const [value, setValue] = useState(initialValue || "");
    const [isSelected, setIsSelected] = useState(false);
    const [mode, setMode] = useState(READ_MODE);
    const textarea = useRef<HTMLTextAreaElement>(null);
    const [cellHeight, setCellHeight] = useState(CELL_LINE_HEIGHT * 3);
    const [preview, setPreview] = useState(initialValue || "");
    const [previousValue, setPreviousValue] = useState("");

    // Common Handlers =========================================================

    useEffect(() => {
      if (!initialValue) {
        setPreview("");
      }
    }, [initialValue, value, setPreview]);

    useEffect(() => {
      if (initialValue !== value) {
        setValue(initialValue);
      }
      // Watching for initialValue prop change for updating value properly
      // eslint-disable-next-line react-hooks/exhaustive-deps
    }, [initialValue]);

    useEffect(() => {
      if (textarea.current) {
        textarea.current.value = value || "";
      }
    }, [value, textarea]);

    const isEditMode = useCallback(() => mode === EDIT_MODE, [mode]);

    const triggerReadMode = useCallback(
      (newValue?: string) => {
        if (!isEditMode()) {
          return;
        }

        setMode(READ_MODE);

        if (initialValue !== newValue) {
          onCellUpdate(index, id, newValue || value);
        }

        focusTextArea(textarea.current);
      },
      [id, initialValue, isEditMode, setMode, onCellUpdate, index, value]
    );

    const triggerEditMode = useCallback(() => {
      setPreviousValue(value);
      blurActiveElement();
      setMode(EDIT_MODE);
    }, [setPreviousValue, value, setMode]);

    const cssClass = useCallback(() => {
      const selectedClass = isSelected ? "editable-cell--selected" : "";
      return `editable-cell ${selectedClass} ${mode}`;
    }, [isSelected, mode]);

    const focus = useCallback(() => {
      if (isEditMode()) {
        return;
      }

      setIsSelected(true);

      focusTextArea(textarea.current);
    }, [isEditMode, setIsSelected, textarea]);

    const onClick = useCallback(() => {
      if (document.activeElement !== textarea.current) {
        focus();
      }
    }, [focus]);

    const onDoubleClick = useCallback(triggerEditMode, [triggerEditMode]);

    const height = useCallback(
      (value: string) => {
        const numberOfValueLines = `${value}`.split("\n").length + 1;
        const numberOfLines = numberOfValueLines < 3 ? 3 : numberOfValueLines;
        setCellHeight(numberOfLines * CELL_LINE_HEIGHT);
      },
      [setCellHeight]
    );

    // TextArea Handlers =======================================================

    const onTextAreaFocus = useCallback(focus, [focus]);

    const onTextAreaBlur = useCallback(() => setIsSelected(false), [setIsSelected]);

    const onTextAreaChange = useCallback(
      (event) => {
        setValue(event.target.value);
        triggerEditMode();
      },
      [setValue, triggerEditMode]
    );

    // Feel Handlers ===========================================================

    const onFeelBlur = useCallback(
      (newValue: string) => {
        setValue(newValue);
        triggerReadMode(newValue);
      },
      [triggerReadMode]
    );

    const onFeelKeyDown = useCallback(
      (event: Monaco.IKeyboardEvent, newValue: string) => {
        const key = event?.code.toLowerCase() || "";
        const isModKey = event.altKey || event.ctrlKey || event.shiftKey;
        const isEnter = isModKey && key === "enter";
        const isTab = key === "tab";
        const isEsc = !!key.match("esc");

        if (isEnter || isTab || isEsc) {
          event.preventDefault();
        }

        if (isEnter || isTab) {
          setValue(newValue);
          triggerReadMode(newValue);
        }

        if (isEsc) {
          setValue(previousValue);
          triggerReadMode(previousValue);
        }

        if (isTab) {
          focusNextTextArea(textarea.current);
        }
      },
      [triggerReadMode, setValue, previousValue]
    );

    const onFeelChange = useCallback(
      (_e, newValue, preview) => {
        height(newValue);
        setPreview(preview);
      },
      [setPreview, height]
    );
    const onFeelLoad = useCallback((preview) => setPreview(preview), [setPreview]);

    // Sub Components ==========================================================

    const readOnlyElement = useMemo(() => {
      return <span className="editable-cell-value" dangerouslySetInnerHTML={{ __html: preview }}></span>;
    }, [preview]);

    const eventHandlerElement = useMemo(() => {
      return (
        <textarea
          className="editable-cell-textarea"
          ref={textarea}
          value={value}
          onChange={onTextAreaChange}
          onFocus={onTextAreaFocus}
          onBlur={onTextAreaBlur}
        />
      );
    }, [textarea, value, onTextAreaFocus, onTextAreaBlur, onTextAreaChange]);

    const feelInputElement = useMemo(() => {
      return (
        <FeelInput
          enabled={isEditMode()}
          value={value}
          onKeyDown={onFeelKeyDown}
          onChange={onFeelChange}
          onLoad={onFeelLoad}
          options={MONACO_OPTIONS}
          onBlur={onFeelBlur}
        />
      );
    }, [isEditMode, value, onFeelChange, onFeelLoad, onFeelKeyDown, onFeelBlur]);

    return useMemo(
      () => (
        <>
          <div
            onDoubleClick={onDoubleClick}
            onClick={onClick}
            style={{ height: `${cellHeight}px` }}
            className={cssClass()}
          >
            {readOnlyElement}
            {eventHandlerElement}
            {feelInputElement}
          </div>
        </>
      ),
      [onDoubleClick, onClick, cellHeight, cssClass, readOnlyElement, eventHandlerElement, feelInputElement]
    );
  },
  ({ value: oldValue }, { value }) => oldValue === value
);
