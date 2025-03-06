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
import { getInputReference, getStateCode, getStateCodeFromRef, NS_SEPARATOR, renderField } from "./utils/Utils";
import { DEFAULT_DATA_TYPE_STRING_ARRAY, DEFAULT_DATA_TYPE_STRING } from "./utils/dataTypes";
import {
  getItemNameAndWithIsNested,
  getListItemName,
  getListItemValue,
  ListItemProps,
} from "./rendering/ListItemField";
import { REFUSED } from "dns";

export type SelectInputProps = HTMLFieldProps<
  string | string[],
  HTMLDivElement,
  {
    allowedValues?: string[];
    transform?(value: string): string;
    itemProps?: ListItemProps;
  }
>;

export const SELECT_IMPORTS: string[] = ["SelectOption", "SelectOptionObject", "Select", "SelectVariant", "FormGroup"];

const Select: React.FC<SelectInputProps> = (props: SelectInputProps) => {
  const isArray: boolean = props.fieldType === Array;

  const ref: InputReference = getInputReference(
    props.name,
    isArray ? DEFAULT_DATA_TYPE_STRING_ARRAY : DEFAULT_DATA_TYPE_STRING
  );

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

  function getExpandedStateCode() {
    if (props.itemProps?.isListItem) {
      const [, stateName] = props.itemProps.listStateSetter.split("set__");
      return [`${stateName}${NS_SEPARATOR}expanded`, `${props.itemProps.listStateSetter}${NS_SEPARATOR}expanded`];
    }
    return [`${ref.stateName}${NS_SEPARATOR}expanded`, `${ref.stateSetter}${NS_SEPARATOR}expanded`];
  }
  const [expandedStateName, expandedStateNameSetter] = getExpandedStateCode();

  // Creating a new item on the List will not add a new property to the `expandedStateName` for List items
  // The code above will ensure the `expandedStateName` has the property before attributing it in a set state
  function getVariableInitializer(path: string) {
    // captures all `properties` and `indexNames` from "<property1>[<indexName2>].<property2>[<indexName2>]"
    const parts = [...path.matchAll(/(\w*)(?:\[(\w*)\])?/g)];
    let propertyPath = "newState";
    return parts
      .map(([_, property, indexName]) => {
        if (property === "" && indexName !== undefined) {
          propertyPath += `[${indexName}]`;
          return `${propertyPath} ??= {};`;
        }
        if (property !== "" && indexName !== undefined) {
          propertyPath += `.${property}`;
          return `${propertyPath} ??= [];`;
        }
        return "";
      })
      .join("\n");
  }

  function getHandleSelect() {
    if (props.itemProps?.isListItem) {
      const { itemName, isNested } = getItemNameAndWithIsNested(props.name);
      const propertyPath = props.itemProps?.listStateName.split(".").splice(1).join(".");
      const path = `${propertyPath}[${props.itemProps?.indexVariableName}]${isNested ? `.${itemName}` : ""}`;
      if (isArray) {
        return `(event, value, isPlaceHolder) => {
          if (isPlaceHolder) {
            ${props.itemProps?.listStateSetter}(prev => {
              const newState = [...prev];
              newState${path} = [];
              return newState;
            })
          } else {
            const selectedValue = newSelection.toString ? newSelection.toString() : newSelection as string;
            ${props.itemProps?.listStateSetter}(prev => {
              const newState = [...prev];
              if (newState${path}.indexOf(selectedValue) != -1) {
                const filtered = newState${path}.filter((s) => s !== selectedValue);
                return newState${path} = filtered;
              }
              newState${path} = [selectedValue, ...newState${path}];
              return newState;
            })
          }
        }`;
      } else {
        return `(event, value, isPlaceHolder) => {
          if (isPlaceHolder) {
            ${props.itemProps?.listStateSetter}(prev => {
              const newState = [...prev];
              newState${path} = "";
              return newState;
            })
            ${expandedStateNameSetter}(prev => {
                const newState = [...prev];
                ${getVariableInitializer(path)}
                newState${path} = false;
                return newState;
            });
          } else {
            const parsedSelection = value.toString ? value.toString() : value as string;
            ${props.itemProps?.listStateSetter}(prev => {
              const newState = [...prev];
              newState${path} = parsedSelection || '';
              return newState;
            })
            ${expandedStateNameSetter}(prev => {
              const newState = [...prev];
              ${getVariableInitializer(path)}
              newState${path} = false;
              return newState;
            });
          }
        }`;
      }
    }
    if (isArray) {
      return `(event, value, isPlaceHolder) => {
        if (isPlaceHolder) {
          ${ref.stateSetter}([]);
        } else {
          ${ref.stateSetter}(prev => {
            const selectedValue = value.toString ? value.toString() : value as string;
            if (prev.indexOf(selectedValue) != -1) {
              return prev.filter((s) => s !== selectedValue);
            }
            return [selectedValue, ...prev];
          });
        }
      }`;
    } else {
      return `(event, value, isPlaceHolder) => {
        if (isPlaceHolder) {
          ${ref.stateSetter}('');
          ${expandedStateNameSetter}(false);
        } else {
          const parsedSelection = value.toString ? value.toString() : value as string;
          ${ref.stateSetter}(parsedSelection || '');
          ${expandedStateNameSetter}(false);
        }
      }`;
    }
  }

  function getIsOpen() {
    if (props.itemProps?.isListItem) {
      const { itemName, isNested } = getItemNameAndWithIsNested(props.name);
      const propertyPath = props.itemProps?.listStateName.split(".").splice(1).join(".").replaceAll("].", "]?.");
      return `${expandedStateName}${propertyPath}?.[${props.itemProps?.indexVariableName}]${isNested ? `?.${itemName}` : ""} ?? false`;
    }
    return expandedStateName;
  }

  function getOnToggle() {
    if (props.itemProps?.isListItem) {
      const { itemName, isNested } = getItemNameAndWithIsNested(props.name);
      const propertyPath = props.itemProps?.listStateName.split(".").splice(1).join(".");
      const path = `${propertyPath}[${props.itemProps?.indexVariableName}]${isNested ? `.${itemName}` : ""}`;
      return `(isOpen) => ${expandedStateNameSetter}(prev => {
          const newState = [...prev];
          ${getVariableInitializer(path)}
          newState${path} = isOpen
          return newState;
        })`;
    }
    return `(isOpen) => ${expandedStateNameSetter}(isOpen)`;
  }

  const jsxCode = `<FormGroup
      fieldId={'${props.id}'}
      label={'${props.label}'}
      isRequired={${props.required}}
    ><Select
      id={'${props.id}'}
      name={${props.itemProps?.isListItem ? getListItemName({ itemProps: props.itemProps, name: props.name }) : `'${props.name}'`}}
      variant={SelectVariant.${isArray ? "typeaheadMulti" : "single"}}
      isDisabled={${props.disabled || false}}
      placeholderText={'${props.placeholder || ""}'}
      isOpen={${getIsOpen()}}
      selections={${props.itemProps?.isListItem ? getListItemValue({ itemProps: props.itemProps, name: props.name }) : ref.stateName}}
      onToggle={${getOnToggle()}}
      onSelect={${getHandleSelect()}}
      value={${props.itemProps?.isListItem ? getListItemValue({ itemProps: props.itemProps, name: props.name }) : ref.stateName}}
    >
      ${selectedOptions.join("\n")}
    </Select></FormGroup>`;

  const element: FormInput = {
    ref,
    pfImports: SELECT_IMPORTS,
    reactImports: ["useState"],
    jsxCode,
    requiredCode: undefined,
    stateCode: props.itemProps?.isListItem
      ? getStateCode(expandedStateName, expandedStateNameSetter, "object[]", "[]")
      : `${getStateCodeFromRef(ref)}
        ${getStateCode(expandedStateName, expandedStateNameSetter, "boolean", "false")}`,
    isReadonly: props.disabled,
  };

  useAddFormElementToContext(element);

  return renderField(element);
};

export default connectField(Select);
