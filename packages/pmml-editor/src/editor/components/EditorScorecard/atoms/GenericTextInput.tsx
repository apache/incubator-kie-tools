/*
 * Copyright 2020 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
import * as React from "react";
import { useState } from "react";
import { TextInput } from "@patternfly/react-core";

interface GenericTextInputProps {
  id: string;
  value: string;
  valid: boolean;
  onChange: (_value: string) => void;
}

export const GenericTextInput = (props: GenericTextInputProps) => {
  const [state, setState] = useState({ value: props.value });

  const onChange = (_value: string) => {
    props.onChange(_value);
    setState({ value: _value });
  };

  return (
    <TextInput
      id={props.id}
      value={state.value}
      onChange={onChange}
      validated={props.valid ? "default" : "error"}
      isRequired={true}
      className="text-input"
      type="text"
    />
  );
};
