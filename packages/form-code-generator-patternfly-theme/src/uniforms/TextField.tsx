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
import { DATE_FUNCTIONS } from "./staticCode/staticCodeBlocks";
import { DEFAULT_DATA_TYPE_DATE, DEFAULT_DATA_TYPE_STRING } from "./utils/dataTypes";
import { getListItemName, getListItemOnChange, getListItemValue, ListItemProps } from "./rendering/ListItemField";

export type TextFieldProps = HTMLFieldProps<
  string,
  HTMLInputElement,
  {
    label: string;
    required: boolean;
    itemProps?: ListItemProps;
  }
>;

const Text: React.FC<TextFieldProps> = (props: TextFieldProps) => {
  const isDate: boolean = props.type === "date" || props.field.format === "date";

  const ref: InputReference = getInputReference(props.name, isDate ? DEFAULT_DATA_TYPE_DATE : DEFAULT_DATA_TYPE_STRING);

  const getDateElement = (): FormInput => {
    const inputJsxCode = `<DatePicker
          id={'date-picker-${props.id}'}
          isDisabled={${props.disabled || false}}
          name={${props.itemProps?.isListItem ? getListItemName({ itemProps: props.itemProps, name: props.name }) : `'${props.name}'`}}
          value={${props.itemProps?.isListItem ? `parseDate(${getListItemValue({ itemProps: props.itemProps, name: props.name })})` : `parseDate(${ref.stateName})`}}
          onChange={${props.itemProps?.isListItem ? getListItemOnChange({ itemProps: props.itemProps, name: props.name, callback: (value: string) => `onDateChange(${value}, ${ref.stateSetter},  ${ref.stateName})` }) : `(e, newDate) => onDateChange(newDate, ${ref.stateSetter},  ${ref.stateName})`}}
        />`;
    return buildDefaultInputElement({
      pfImports: ["DatePicker"],
      inputJsxCode,
      ref,
      requiredCode: [DATE_FUNCTIONS],
      wrapper: {
        id: props.id,
        label: props.label,
        required: props.required,
      },
      disabled: props.disabled,
      itemProps: props.itemProps,
    });
  };

  const getTextInputElement = (): FormInput => {
    const inputJsxCode = `<TextInput
        name={${props.itemProps?.isListItem ? getListItemName({ itemProps: props.itemProps, name: props.name }) : `'${props.name}'`}}
        id={'${props.id}'}
        isDisabled={${props.disabled || "false"}}
        placeholder={'${props.placeholder}'}
        type={'${props.type || "text"}'}
        value={${props.itemProps?.isListItem ? getListItemValue({ itemProps: props.itemProps, name: props.name }) : ref.stateName}}
        onChange={${props.itemProps?.isListItem ? getListItemOnChange({ itemProps: props.itemProps, name: props.name }) : `(e, newValue) => ${ref.stateSetter}(newValue)`}}
        />`;

    return buildDefaultInputElement({
      pfImports: ["TextInput"],
      inputJsxCode,
      ref,
      wrapper: {
        id: props.id,
        label: props.label,
        required: props.required,
      },
      disabled: props.disabled,
      itemProps: props.itemProps,
    });
  };

  const element = isDate ? getDateElement() : getTextInputElement();

  useAddFormElementToContext(element);

  return renderField(element);
};

export default connectField(Text, { kind: "leaf" });
