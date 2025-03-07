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
import { useAddFormElementToContext } from "./CodeGenContext";
import { FormInput, InputReference } from "../api";
import { buildDefaultInputElement, getInputReference, renderField } from "./utils/Utils";
import { DEFAULT_DATA_TYPE_STRING } from "./utils/dataTypes";
import { getListItemName, getListItemOnChange, getListItemValue, ListItemProps } from "./rendering/ListItemField";

export type RadioFieldProps = HTMLFieldProps<
  string,
  HTMLDivElement,
  {
    name: string;
    label: string;
    transform?: (string?: string) => string;
    allowedValues: string[];
    required: boolean;
    disabled: boolean;
    itemProps?: ListItemProps;
  }
>;

const Radio = (props: RadioFieldProps) => {
  const ref: InputReference = getInputReference(props.name, DEFAULT_DATA_TYPE_STRING);

  const radios: string[] = [];

  props.allowedValues?.forEach((item) => {
    const radio = `<Radio
      key={'${item}'}
      id={'${props.id}-${item}'}
      name={${props.itemProps?.isListItem ? getListItemName({ itemProps: props.itemProps, name: props.name }) : `'${props.name}'`}}
      isChecked={${props.itemProps?.isListItem ? getListItemValue({ itemProps: props.itemProps, name: props.name }) : `'${item}' === ${ref.stateName}`}}
      isDisabled={${props.disabled || false}}
      label={'${props.transform ? props.transform(item) : item}'}
      aria-label={'${props.name}'}
      onChange={${props.itemProps?.isListItem ? getListItemOnChange({ itemProps: props.itemProps, name: props.name, overrideNewValue: item }) : `() => ${ref.stateSetter}('${item}')`}}
    />`;
    radios.push(radio);
  });

  const jsxCode = `<div>${radios.join("\n")}</div>`;

  const element: FormInput = buildDefaultInputElement({
    pfImports: ["Radio"],
    inputJsxCode: jsxCode,
    ref,
    wrapper: {
      id: props.id,
      label: props.label,
      required: props.required,
    },
    disabled: props.disabled,
    itemProps: props.itemProps,
  });

  useAddFormElementToContext(element);

  return renderField(element);
};

export default connectField(Radio);
