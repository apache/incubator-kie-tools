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
import { useAddFormElementToContext } from "./CodeGenContext";
import { FormInput, InputReference } from "../api";
import { getInputReference, getStateCode, getStateCodeFromRef, NS_SEPARATOR, renderField } from "./utils/Utils";
import { MULTIPLE_SELECT_FUNCTIONS, SELECT_FUNCTIONS } from "./staticCode/staticCodeBlocks";
import { ARRAY, STRING } from "./utils/dataTypes";

export type SelectInputProps = HTMLFieldProps<
  string | string[],
  HTMLDivElement,
  {
    allowedValues?: string[];
    transform?(value: string): string;
  }
>;

export const SELECT_IMPORTS: string[] = ["SelectOption", "SelectOptionObject", "Select", "SelectVariant", "FormGroup"];

const Select: React.FC<SelectInputProps> = (props: SelectInputProps) => {
  const isArray: boolean = props.fieldType === Array;

  const ref: InputReference = getInputReference(props.name, isArray ? ARRAY : STRING);

  const selectedOptions: string[] = [];

  props.allowedValues!.forEach((value) => {
    const option = `<SelectOption key={'${value}'} value={'${value}'}>${
      props.transform ? props.transform(value) : value
    }</SelectOption>`;
    selectedOptions.push(option);
  });

  if (props.placeholder) {
    const placeHolderOption = `<SelectOption key={'${selectedOptions.length}'} isPlaceholder value={'${props.placeholder}'}/>`;
    selectedOptions.unshift(placeHolderOption);
  }

  let stateCode = getStateCodeFromRef(ref);

  const expandedStateName = `${ref.stateName}${NS_SEPARATOR}expanded`;
  const expandedStateNameSetter = `${ref.stateSetter}${NS_SEPARATOR}expanded`;
  const expandedState = getStateCode(expandedStateName, expandedStateNameSetter, "boolean", "false");

  stateCode += `\n${expandedState}`;

  let hanldeSelect;
  if (isArray) {
    hanldeSelect = `handleMultipleSelect(value, isPlaceHolder, ${ref.stateName}, ${ref.stateSetter})`;
  } else {
    hanldeSelect = `handleSelect(value, isPlaceHolder, ${ref.stateName}, ${ref.stateSetter}, ${expandedStateNameSetter})`;
  }
  const jsxCode = `<FormGroup
      fieldId={'${props.id}'}
      label={'${props.label}'}
      isRequired={${props.required}}
    ><Select
      id={'${props.id}'}
      name={'${props.name}'}
      variant={SelectVariant.${isArray ? "typeaheadMulti" : "single"}}
      isDisabled={${props.disabled || false}}
      placeholderText={'${props.placeholder || ""}'}
      isOpen={${expandedStateName}}
      selections={${ref.stateName}}
      onToggle={(isOpen) => ${expandedStateNameSetter}(isOpen)}
      onSelect={(event, value, isPlaceHolder) => {
        ${hanldeSelect}
      }}
      value={${ref.stateName}}
    >
      ${selectedOptions.join("\n")}
    </Select></FormGroup>`;

  const element: FormInput = {
    ref,
    pfImports: SELECT_IMPORTS,
    reactImports: ["useState"],
    requiredCode: [isArray ? MULTIPLE_SELECT_FUNCTIONS : SELECT_FUNCTIONS],
    jsxCode,
    stateCode,
    isReadonly: props.disabled,
  };

  useAddFormElementToContext(element);

  return renderField(element);
};

export default connectField(Select);
