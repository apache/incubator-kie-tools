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
import { useCallback, useMemo, useEffect, useRef } from "react";
import * as _ from "lodash";
import { useBoxedExpressionEditorI18n } from "../../i18n";
import { NavigationKeysUtils } from "../../keysUtils/keyUtils";
import { useBeeTableSelectableCellRef } from "../../selection/BeeTableSelectionContext";

export interface InlineEditableTextInputProps {
  value: string;
  onChange: (updatedValue: string) => void;
  setActiveCellEditing: (isEditing: boolean) => void;
  rowIndex: number;
  columnIndex: number;
}

export const InlineEditableTextInput: React.FunctionComponent<InlineEditableTextInputProps> = ({
  rowIndex,
  columnIndex,
  value,
  setActiveCellEditing,
  onChange,
}) => {
  const { i18n } = useBoxedExpressionEditorI18n();
  const inputRef = useRef<HTMLInputElement>(null);

  const { isEditing } = useBeeTableSelectableCellRef(
    rowIndex,
    columnIndex,
    onChange,
    useCallback(() => value, [value])
  );

  const stopEditingPersistingValue = useCallback(() => {
    const newValue = inputRef.current?.value;
    if (newValue && newValue !== value) {
      onChange(newValue);
    }
    setActiveCellEditing(false);
  }, [onChange, setActiveCellEditing, value]);

  const onInputKeyDown = useMemo(
    () => (e: React.KeyboardEvent) => {
      e.stopPropagation();

      if (NavigationKeysUtils.isEnter(e.key)) {
        stopEditingPersistingValue();
      }

      if (NavigationKeysUtils.isEsc(e.key)) {
        setActiveCellEditing(false);
      }
    },
    [setActiveCellEditing, stopEditingPersistingValue]
  );

  const onLabelClick = useCallback(() => {
    setActiveCellEditing(true);
  }, [setActiveCellEditing]);

  const textStyle = useMemo(() => {
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
    <p
      className={"inline-editable-preview pf-u-text-truncate"}
      style={{ ...textStyle, width: "100%" }}
      onClick={onLabelClick}
    >
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
