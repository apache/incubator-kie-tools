/*
 * Copyright 2021 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import * as React from "react";
import { connectField, HTMLFieldProps } from "uniforms/cjs";
import { renderCodeGenElement, SELECT } from "./templates/templates";
import { useAddFormElementToBootstrapContext } from "./BootstrapCodeGenContext";
import { FormInput } from "../api";

export type SelectInputProps = HTMLFieldProps<
  string[],
  HTMLDivElement,
  {
    name: string;
    label: string;
    placeHolder: string;
    allowedValues?: string[];
    required: boolean;
    transform?(value: string): string;
  }
>;

const Select: React.FC<SelectInputProps> = (props: SelectInputProps) => {
  const options =
    props.allowedValues?.map((option) => {
      return {
        value: option,
        label: props.transform ? props.transform(option) : option,
        checked: props.value?.includes(option),
      };
    }) || [];

  const inputProps = {
    id: props.name,
    name: props.name,
    label: props.label,
    multiple: props.fieldType === Array,
    placeHolder: props.placeHolder,
    disabled: props.disabled,
    options: options,
    value: props.value,
  };

  const element: FormInput = renderCodeGenElement(SELECT, inputProps);
  useAddFormElementToBootstrapContext(element);
  return <>{JSON.stringify(element)}</>;
};

export default connectField(Select);
