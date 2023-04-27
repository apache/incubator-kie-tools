/*
 * Copyright 2023 Red Hat, Inc. and/or its affiliates.
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
import { Ref } from "react";
import { TextInput, TextInputProps } from "@patternfly/react-core/dist/js/components/TextInput";
import { connectField } from "uniforms";
import wrapField from "./wrapField";

export type NumFieldProps = {
  id: string;
  decimal?: boolean;
  inputRef?: Ref<HTMLInputElement>;
  onChange: (value?: number) => void;
  disabled?: boolean;
  value?: number;
  error?: boolean;
} & Omit<TextInputProps, "isDisabled">;

function NumField(props: NumFieldProps) {
  const onChange = (value: string, event: React.FormEvent<HTMLInputElement>) => {
    const parse = props.decimal ? parseFloat : parseInt;
    const v = parse((event.target as any)?.value ?? "");
    props.onChange(isNaN(v) ? undefined : v);
  };

  return wrapField(
    props,
    <TextInput
      aria-label={"uniforms num field"}
      data-testid={"num-field"}
      name={props.name}
      isDisabled={props.disabled}
      id={props.id}
      max={props.max}
      min={props.min}
      onChange={onChange}
      placeholder={props.placeholder}
      ref={props.inputRef}
      step={props.step ?? (props.decimal ? 0.01 : 1)}
      type="number"
      value={`${props.value ?? ""}`}
      validated={props.error ? "error" : "default"}
    />
  );
}

export default connectField(NumField);
