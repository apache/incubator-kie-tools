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
import { buildDefaultInputElement, getInputReference, renderField } from "./utils/Utils";
import { FormInput, InputReference } from "../api";
import { useAddFormElementToContext } from "./CodeGenContext";
import { CHECKBOX_GROUP_FUNCTIONS } from "./staticCode/staticCodeBlocks";
import { DEFAULT_DATA_TYPE_STRING_ARRAY } from "./utils/dataTypes";
import { getListItemName, getListItemOnChange, getListItemValue, ListItemProps } from "./rendering/ListItemField";

export type CheckBoxGroupProps = HTMLFieldProps<
  string[],
  HTMLDivElement,
  {
    name: string;
    label: string;
    allowedValues?: string[];
    required: boolean;
    transform?(value: string): string;
    itemProps: ListItemProps;
  }
>;

const CheckBoxGroup: React.FC<CheckBoxGroupProps> = (props: CheckBoxGroupProps) => {
  const ref: InputReference = getInputReference(props.name, DEFAULT_DATA_TYPE_STRING_ARRAY);

  const jsxCode = props.allowedValues
    ?.map((value) => {
      return `<Checkbox
  key={'${props.id}-${value}'}
  id={'${props.id}-${value}'}
  name={${props.itemProps?.isListItem ? getListItemName({ itemProps: props.itemProps, name: props.name }) : `'${props.name}'`}}
  aria-label={'${props.name}'}
  label={'${props.transform ? props.transform(value) : value}'} 
  isDisabled={${props.disabled || false}} 
  isChecked={${ref.stateName}.indexOf('${value}') !== -1}
  onChange={${props.itemProps?.isListItem ? getListItemOnChange({ itemProps: props.itemProps, name: props.name, callback: (internalValue: string) => `handleCheckboxGroupChange(${internalValue}, ${ref.stateName}, ${ref.stateSetter})`, overrideNewValue: `'${value}'` }) : `() => handleCheckboxGroupChange('${value}', ${ref.stateName}, ${ref.stateSetter})`}}
  value={${props.itemProps?.isListItem ? getListItemValue({ itemProps: props.itemProps, name: props.name }) : `'${value}'`}}
/>`;
    })
    .join("\n");

  const element: FormInput = buildDefaultInputElement({
    pfImports: ["Checkbox"],
    inputJsxCode: jsxCode || "",
    ref: ref,
    requiredCode: [CHECKBOX_GROUP_FUNCTIONS],
    wrapper: {
      id: props.id,
      label: props.label,
      required: props.required,
    },
    disabled: props.disabled,
  });

  useAddFormElementToContext(element);

  return renderField(element);
};

export default connectField(CheckBoxGroup);
