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
import { DATE, renderCodeGenElement } from "./templates/templates";
import { useAddFormElementToBootstrapContext } from "./BootstrapCodeGenContext";
import { FormInput } from "../api";
import { ListItemProps } from "./rendering/ListFieldInput";

export type DateFieldProps = HTMLFieldProps<
  Date,
  HTMLDivElement,
  {
    name: string;
    label: string;
    required: boolean;
    max?: Date;
    min?: Date;
    itemProps?: ListItemProps;
  }
>;

const Date: React.FC<DateFieldProps> = (props: DateFieldProps) => {
  function formatDate(date?: Date) {
    return date?.toISOString().slice(0, -8);
  }

  const element: FormInput = renderCodeGenElement(DATE, {
    id: props.name,
    name: props.name,
    label: props.label,
    disabled: props.disabled ?? false,
    placeholder: props.placeholder,
    autoComplete: props.autoComplete ?? false,
    value: formatDate(props.value),
    max: formatDate(props.max),
    min: formatDate(props.min),
    itemProps: props.itemProps,
  });
  useAddFormElementToBootstrapContext(element);
  return <>{JSON.stringify(element)}</>;
};

export default connectField(Date);
