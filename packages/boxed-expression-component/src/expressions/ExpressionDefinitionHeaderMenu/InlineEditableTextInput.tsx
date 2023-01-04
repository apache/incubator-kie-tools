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
import { useCallback, useMemo, useState, FocusEvent, useEffect, useRef } from "react";
import * as _ from "lodash";
import { useBoxedExpressionEditorI18n } from "../../i18n";
import { NavigationKeysUtils } from "../../keysUtils";

export interface InlineEditableTextInputProps {
  /** Text value */
  value: string;
  /** Callback executed when text changes */
  onChange: (updatedValue: string) => void;
}

export const InlineEditableTextInput: React.FunctionComponent<InlineEditableTextInputProps> = ({ value, onChange }) => {
  const { i18n } = useBoxedExpressionEditorI18n();
  const inputRef = useRef<HTMLInputElement>(null);

  const [toggle, setToggle] = useState(true);

  const onInputBlur = useCallback(
    (event: FocusEvent<HTMLInputElement>) => {
      const changedText = event.target.value;
      onChange(changedText);
      setToggle(true);
    },
    [onChange]
  );

  const onInputKeyDown = useMemo(
    () => (e: React.KeyboardEvent) => {
      e.stopPropagation();

      if (NavigationKeysUtils.isEnter(e.key)) {
        (e.currentTarget as HTMLElement)?.blur();
      }

      if (NavigationKeysUtils.isEsc(e.key)) {
        setToggle(true);
      }
    },
    []
  );

  const onLabelClick = useCallback(() => {
    setToggle(false);
  }, []);

  const getTextStyle = useMemo(() => {
    if (_.isEmpty(value)) {
      return { fontStyle: "italic", cursor: "pointer", color: "gray" };
    } else {
      return { cursor: "pointer" };
    }
  }, [value]);

  return toggle ? (
    <p className={"pf-u-text-truncate"} style={getTextStyle} onClick={onLabelClick}>
      {value || i18n.enterText}
    </p>
  ) : (
    <input
      ref={inputRef}
      type={"text"}
      autoFocus={true}
      defaultValue={value}
      onBlur={onInputBlur}
      onKeyDown={onInputKeyDown}
    />
  );
};
