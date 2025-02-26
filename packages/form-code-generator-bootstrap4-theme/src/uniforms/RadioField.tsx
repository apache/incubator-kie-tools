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
import { RADIOGROUP, renderCodeGenElement } from "./templates/templates";
import { useAddFormElementToBootstrapContext } from "./BootstrapCodeGenContext";
import { FormInput } from "../api";
import { ListItemProps } from "./rendering/ListFieldInput";

export type RadioFieldProps = HTMLFieldProps<
  string,
  HTMLDivElement,
  {
    name: string;
    label: string;
    transform?: (string?: string) => string;
    allowedValues?: string[];
    required: boolean;
    disabled: boolean;
    itemProps?: ListItemProps;
  }
>;

const Radio = (props: RadioFieldProps) => {
  const element: FormInput = renderCodeGenElement(RADIOGROUP, {
    id: props.name,
    name: props.name,
    label: props.label,
    disabled: props.disabled,
    itemProps: props.itemProps,
    options: (props.allowedValues ?? [])?.map((option) => {
      return {
        value: option,
        label: props.transform ? props.transform(option) : option,
        checked: props.value === option,
      };
    }),
  });
  useAddFormElementToBootstrapContext(element);
  return <>{JSON.stringify(element)}</>;
};

export default connectField(Radio);
