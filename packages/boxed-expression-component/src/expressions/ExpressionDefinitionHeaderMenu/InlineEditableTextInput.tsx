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

import * as React from "react";
import { useCallback, useMemo, useEffect, useRef } from "react";
import * as _ from "lodash";
import { useBoxedExpressionEditorI18n } from "../../i18n";
import { NavigationKeysUtils } from "../../keysUtils";
import { useBeeTableSelectableCellRef } from "../../selection/BeeTableSelectionContext";

export interface InlineEditableTextInputProps {
  value: string;
  onChange: (updatedValue: string) => void;
  setEditing: React.Dispatch<React.SetStateAction<boolean>>;
  rowIndex: number;
  columnIndex: number;
}

export const InlineEditableTextInput: React.FunctionComponent<InlineEditableTextInputProps> = ({
  rowIndex,
  columnIndex,
  value,
  setEditing,
  onChange,
}) => {
  const { i18n } = useBoxedExpressionEditorI18n();
  const inputRef = useRef<HTMLInputElement>(null);

  const { isEditing } = useBeeTableSelectableCellRef(rowIndex, columnIndex, undefined, undefined);

  const stopEditingPersistingValue = useCallback(() => {
    const newValue = inputRef.current?.value;
    if (newValue && newValue !== value) {
      onChange(newValue);
    }
    setEditing(false);
  }, [onChange, setEditing, value]);

  const onInputKeyDown = useMemo(
    () => (e: React.KeyboardEvent) => {
      e.stopPropagation();

      if (NavigationKeysUtils.isEnter(e.key)) {
        stopEditingPersistingValue();
      }

      if (NavigationKeysUtils.isEsc(e.key)) {
        setEditing(false);
      }
    },
    [setEditing, stopEditingPersistingValue]
  );

  const onLabelClick = useCallback(() => {
    setEditing(true);
  }, [setEditing]);

  const getTextStyle = useMemo(() => {
    if (_.isEmpty(value)) {
      return { fontStyle: "italic", cursor: "pointer", color: "gray" };
    } else {
      return { cursor: "pointer" };
    }
  }, [value]);

  // Runs every time this component re-renders.
  useEffect(() => {
    if (isEditing) {
      inputRef.current?.select();
    }
  });

  return !isEditing ? (
    <p className={"inline-editable-preview pf-u-text-truncate"} style={getTextStyle} onClick={onLabelClick}>
      {value || i18n.enterText}
    </p>
  ) : (
    <input
      style={{ width: "100%" }}
      ref={inputRef}
      type={"text"}
      autoFocus={true}
      defaultValue={value}
      onBlur={stopEditingPersistingValue}
      onKeyDown={onInputKeyDown}
    />
  );
};
