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
import { ChangeEvent, useCallback, useMemo, useState, FocusEvent, useEffect, useRef } from "react";
import * as _ from "lodash";
import { useBoxedExpressionEditorI18n } from "../../i18n";

export interface EditTextInlineProps {
  /** Text value */
  value: string;
  /** Callback executed when text changes */
  onTextChange: (updatedValue: string, event?: ChangeEvent<HTMLInputElement>) => void;
  /** Callback executed when user cancel by pressing escape */
  onCancel?: (event: KeyboardEvent) => void;
  /** Callback executed when user toggle the state to edit/read mode */
  onToggle?: (isReadMode: boolean) => void;
  /** Callback executed when user press a key */
  onKeyDown?: (event: KeyboardEvent) => void;
}

export const EditTextInline: React.FunctionComponent<EditTextInlineProps> = ({
  value,
  onTextChange,
  onCancel = () => {},
  onToggle = () => {},
  onKeyDown = () => {},
}) => {
  const { i18n } = useBoxedExpressionEditorI18n();
  const inputRef = useRef<HTMLInputElement>(null);

  const [toggle, setToggle] = useState(true);

  const onValueBlur = useCallback(
    (event: FocusEvent<HTMLInputElement>) => {
      const changedText = event.target.value;
      onTextChange(changedText, event);
      setToggle(true);
      onToggle(true);
    },
    [onTextChange, onToggle]
  );

  const onInputKeyDown = useMemo(
    () => (event: KeyboardEvent) => {
      const pressedEnter = _.lowerCase(event.key) === "enter";
      const pressedEscape = _.lowerCase(event.key) === "escape";

      onKeyDown(event);

      if (pressedEnter) {
        (event.currentTarget as HTMLElement)?.blur();
      }
      if (pressedEscape) {
        onCancel(event);
        setToggle(true);
        onToggle(true);
      }
    },
    [onKeyDown, onCancel, onToggle]
  );

  useEffect(() => {
    const onKeyDownForInput = onInputKeyDown;
    const input = inputRef.current;
    input?.addEventListener("keydown", onKeyDownForInput);
    return () => {
      input?.removeEventListener("keydown", onKeyDownForInput);
    };
  }, [onInputKeyDown]);

  const onClick = useMemo(
    () => () => {
      setToggle(false);
      onToggle(false);
    },
    [onToggle]
  );

  const getTextStyle = useMemo(() => {
    if (_.isEmpty(value)) {
      return { fontStyle: "italic" };
    }
  }, [value]);

  return toggle ? (
    <p className="pf-u-text-truncate" style={getTextStyle} onClick={onClick}>
      {value || i18n.enterText}
    </p>
  ) : (
    <input
      ref={inputRef}
      type="text"
      autoFocus
      defaultValue={value}
      onBlur={onValueBlur}
      style={{ borderRadius: "0.5em", width: "100%" }}
    />
  );
};
