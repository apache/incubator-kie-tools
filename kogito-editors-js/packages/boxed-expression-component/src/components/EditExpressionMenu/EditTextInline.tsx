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

export interface EditTextInlineProps {
  /** Text value */
  value: string;
  /** Callback executed when text changes */
  onTextChange: (updatedValue: string) => void;
}

export const EditTextInline: React.FunctionComponent<EditTextInlineProps> = ({ onTextChange, value }) => {
  const [toggle, setToggle] = useState(true);

  const onValueBlur = useCallback(
    (event: ChangeEvent<HTMLInputElement>) => {
      const changedText = event.target.value;
      if (_.size(changedText.trim())) {
        onTextChange(changedText);
      }
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

  return toggle ? (
    <p className="pf-u-text-truncate" onClick={onClick}>
      {value}
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
