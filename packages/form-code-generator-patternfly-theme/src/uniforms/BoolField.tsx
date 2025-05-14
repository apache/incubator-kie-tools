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

import { getInputReference, getStateCodeFromRef, renderField } from "./utils/Utils";
import { DEFAULT_DATA_TYPE_BOOLEAN } from "./utils/dataTypes";
import { getListItemName, getListItemOnChange, getListItemValue, ListItemProps } from "./rendering/ListItemField";

export type BoolFieldProps = HTMLFieldProps<
  boolean,
  HTMLDivElement,
  {
    name: string;
    label: string;
    itemProps?: ListItemProps;
  }
>;

const Bool: React.FC<BoolFieldProps> = (props: BoolFieldProps) => {
  const ref: InputReference = getInputReference(props.name, DEFAULT_DATA_TYPE_BOOLEAN);

  const jsxCode = `<FormGroup fieldId='${props.id}'>
    <Checkbox
      isChecked={${props.itemProps?.isListItem ? getListItemValue({ itemProps: props.itemProps, name: props.name }) : ref.stateName}}
      isDisabled={${props.disabled || "false"}}
      id={'${props.id}'}
      name={${props.itemProps?.isListItem ? getListItemName({ itemProps: props.itemProps, name: props.name }) : `'${props.name}'`}}
      label={'${props.label}'}
      onChange={${props.itemProps?.isListItem ? getListItemOnChange({ itemProps: props.itemProps, name: props.name }) : `(e, newValue) => ${ref.stateSetter}(newValue)`}}
    />
  </FormGroup>`;

  const element: FormInput = {
    ref,
    pfImports: ["Checkbox", "FormGroup"],
    reactImports: ["useState"],
    requiredCode: undefined,
    jsxCode,
    stateCode: props.itemProps ? "" : getStateCodeFromRef(ref),
    isReadonly: props.disabled,
  };

  useAddFormElementToContext(element);

  return renderField(element);
};

export default connectField(Bool);
