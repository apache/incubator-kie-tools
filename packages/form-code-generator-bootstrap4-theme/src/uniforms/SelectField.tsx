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
import { connectField, HTMLFieldProps } from "uniforms/cjs";
import { renderCodeGenElement, SELECT } from "./templates/templates";
import { useAddFormElementToBootstrapContext } from "./BootstrapCodeGenContext";
import { FormInput } from "../api";
import { ListItemProps } from "./rendering/ListFieldInput";

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
    itemProps?: ListItemProps;
  }
>;

const Select: React.FC<SelectInputProps> = (props: SelectInputProps) => {
  const element: FormInput = renderCodeGenElement(SELECT, {
    id: props.name,
    name: props.name,
    label: props.label,
    multiple: props.fieldType === Array,
    placeHolder: props.placeHolder,
    disabled: props.disabled,
    value: props.value,
    itemProps: props.itemProps,
    options: (props.allowedValues ?? [])?.map((option) => {
      return {
        value: option,
        label: props.transform ? props.transform(option) : option,
        checked: props.value?.includes(option),
      };
    }),
  });
  useAddFormElementToBootstrapContext(element);
  return <>{JSON.stringify(element)}</>;
};

export default connectField(Select);
