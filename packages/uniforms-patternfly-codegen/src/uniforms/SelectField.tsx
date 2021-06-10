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

import React from "react";
import { connectField } from "uniforms";
import { useAddFormElementToContext } from "./CodeGenContext";
import { FormInput, InputReference } from "../api";
import { getInputReference, getStateCode, getStateCodeFromRef, NS_SEPARATOR, renderField } from "./utils/Utils";
import { MULTIPLE_SELECT_FUNCTIONS, SELECT_FUNCTIONS } from "./staticCode/staticCodeBlocks";

export type SelectInputProps = {
  id: string;
  name: string;
  label: string;
  required?: boolean;
  fieldType?: typeof Array | any;
  placeholder: string;
  allowedValues?: string[];
  disabled?: boolean;
  transform?: (value?: string) => string;
};

const Select: React.FC<SelectInputProps> = (props: SelectInputProps) => {
  const ref: InputReference = getInputReference(props.name);

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

  const isArray: boolean = props.fieldType === Array;

  let stateCode = getStateCodeFromRef(ref, isArray ? "string[]" : "string", isArray ? "[]" : "''");

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
      fieldId="${props.id}"
      label="${props.label}"
      isRequired={${props.required}}
    ><Select
      id={'${props.id}'}
      name={props.name}
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
    pfImports: ["SelectOption", "SelectOptionObject", "Select", "SelectVariant", "FormGroup"],
    reactImports: ["useState"],
    requiredCode: [isArray ? MULTIPLE_SELECT_FUNCTIONS : SELECT_FUNCTIONS],
    jsxCode,
    stateCode,
  };

  useAddFormElementToContext(element);

  return renderField(element);
};

export default connectField(Select);
