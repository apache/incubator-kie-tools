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
import { ChangeEvent, useCallback, useMemo, useState } from "react";
import * as _ from "lodash";
import { useBoxedExpressionEditorI18n } from "../../i18n";

export interface EditTextInlineProps {
  /** Text value */
  value: string;
  /** Callback executed when text changes */
  onTextChange: (updatedValue: string) => void;
}

export const EditTextInline: React.FunctionComponent<EditTextInlineProps> = ({ onTextChange, value }) => {
  const { i18n } = useBoxedExpressionEditorI18n();

  const [toggle, setToggle] = useState(true);

  const onValueBlur = useCallback(
    (event: ChangeEvent<HTMLInputElement>) => {
      const changedText = event.target.value;
      onTextChange(changedText);
      setToggle(true);
    },
    [onTextChange]
  );

  const onKeyDown = useMemo(
    () => (event: React.KeyboardEvent<HTMLInputElement>) => {
      const pressedEnter = _.lowerCase(event.key) === "enter";
      const pressedEscape = _.lowerCase(event.key) === "escape";
      if (pressedEnter) {
        event.currentTarget.blur();
      }
      if (pressedEscape) {
        setToggle(true);
      }
    },
    []
  );

  const onClick = useMemo(
    () => () => {
      setToggle(false);
    },
    []
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
      type="text"
      autoFocus
      defaultValue={value}
      onBlur={onValueBlur}
      style={{ borderRadius: "0.5em", width: "100%" }}
      onKeyDown={onKeyDown}
    />
  );
};
