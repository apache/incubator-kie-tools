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
import { NUMBER, renderCodeGenElement } from "./templates/templates";
import { useAddFormElementToBootstrapContext } from "./BootstrapCodeGenContext";
import { FormInput } from "../api";
import { ListItemProps } from "./rendering/ListFieldInput";

export type NumFieldProps = HTMLFieldProps<
  string,
  HTMLInputElement,
  {
    label: string;
    required: boolean;
    decimal?: boolean;
    min?: string;
    max?: string;
    itemProps?: ListItemProps;
  }
>;
const Num: React.FC<NumFieldProps> = (props: NumFieldProps) => {
  const element: FormInput = renderCodeGenElement(NUMBER, {
    id: props.name,
    name: props.name,
    label: props.label,
    type: props.type ?? "text",
    disabled: props.disabled ?? false,
    placeholder: props.placeholder,
    autoComplete: props.autoComplete ?? false,
    value: props.value,
    max: props.max,
    min: props.min,
    step: props.decimal ? 0.01 : 1,
    itemProps: props.itemProps,
  });
  useAddFormElementToBootstrapContext(element);
  return <>{JSON.stringify(element)}</>;
};

export default connectField(Num);
