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

import { Context } from "uniforms";
import * as React from "react";
import { CodeGenContext, CodeGenContextProvider } from "../CodeGenContext";
import AutoField from "../AutoField";

export interface ListItemProps {
  isListItem: boolean;
  indexVariableName: string;
  listName: string;
  listStateName: string;
  listStateSetter: string;
}

/**
 * The list item can be nested or not (be part of an object).
 * For non-nested items the `itemName` will have value "$", for nested items it will have its property name
 */
function getItemNameAndWithIsNested(name: string) {
  const itemName = name.split(".").pop() ?? "$";
  const isNested = itemName !== "$";
  return { itemName, isNested };
}

/**
 * This function can either return:
 * `listName.$`
 * `listName.${index}.itemName`
 */
export const getListItemName = (itemProps: ListItemProps, name: string) => {
  const { itemName, isNested } = getItemNameAndWithIsNested(name);
  return `\`${itemProps?.listName}${isNested ? `.$\{${itemProps?.indexVariableName}}.${itemName}` : `.$\{${itemProps?.indexVariableName}}`}\``;
};

/**
 * This function can either return:
 * `listStateName[index]`
 * `listStateName[index].itemName.`
 */
export const getListItemValue = (itemProps: ListItemProps, name: string) => {
  const { itemName, isNested } = getItemNameAndWithIsNested(name);
  return `${itemProps?.listStateName}[${itemProps?.indexVariableName}]${isNested ? `.${itemName}` : ""}`;
};

/**
 * This function can either return:
 * `newValue => listStateSetter(s =>
 *    const newState = [...s];
 *    const newState[index] = newValue;
 *    return newState;
 *  );`
 * `newValue => listStateSetter(s =>
 *    const newState = [...s];
 *    const newState[index].itemName = newValue;
 *    return newState;
 *  );`
 */
export const getListItemOnChange = (itemProps: ListItemProps, name: string, callback?: (value: string) => string) => {
  const { itemName, isNested } = getItemNameAndWithIsNested(name);
  return `newValue => ${itemProps?.listStateSetter}(s => { const newState = [...s]; newState[${itemProps?.indexVariableName}]${isNested ? `.${itemName}` : ""} = newValue; return ${callback ? callback("newState") : "newState"}; })`;
};

export interface Props {
  codegenCtx: CodeGenContext;
  uniformsContext: Context<any>;
  fieldName: any;
  itemProps: ListItemProps;
  disabled?: boolean;
}

export const ListItemField: React.FC<Props> = ({ codegenCtx, uniformsContext, fieldName, itemProps, disabled }) => {
  return (
    <CodeGenContextProvider schema={uniformsContext.schema} codegenCtx={codegenCtx} uniformsCtx={uniformsContext}>
      <AutoField key={fieldName} name={fieldName} disabled={disabled} itemProps={itemProps} />
    </CodeGenContextProvider>
  );
};

export default ListItemField;
