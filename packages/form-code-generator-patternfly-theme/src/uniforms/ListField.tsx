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

import React, { useContext } from "react";
import { connectField, context, HTMLFieldProps } from "uniforms/cjs";
import { getInputReference, getStateCode, renderField } from "./utils/Utils";
import { codeGenContext } from "./CodeGenContext";
import { FormInput, InputReference } from "../api";
import {
  DEFAULT_DATA_TYPE_ANY_ARRAY,
  DEFAULT_DATA_TYPE_BOOLEAN_ARRAY,
  DEFAULT_DATA_TYPE_NUMBER_ARRAY,
  DEFAULT_DATA_TYPE_OBJECT_ARRAY,
  DEFAULT_DATA_TYPE_STRING_ARRAY,
} from "./utils/dataTypes";
import { renderListItemFragmentWithContext } from "./rendering/RenderingUtils";
import { getNextIndexVariableName, ListItemProps } from "./rendering/ListItemField";

export type ListFieldProps = HTMLFieldProps<
  unknown[],
  HTMLDivElement,
  {
    itemProps?: ListItemProps;
    maxCount?: number;
    minCount?: number;
  }
>;

const List: React.FC<ListFieldProps> = (props: ListFieldProps) => {
  const ref: InputReference = getInputReference(props.name, DEFAULT_DATA_TYPE_ANY_ARRAY, props.itemProps);

  const uniformsContext = useContext(context);
  const codegenCtx = useContext(codeGenContext);

  const indexVariableName = getNextIndexVariableName(props.itemProps);
  const listItem = renderListItemFragmentWithContext(
    uniformsContext,
    "$",
    {
      isListItem: true,
      indexVariableName,
      listName: props.name,
      listStateName: ref.stateName,
      listStateSetter: ref.stateSetter,
    },
    props.disabled
  );

  const getDefaultItemValue = () => {
    const typeName = listItem?.ref.dataType.name;
    if (typeName?.endsWith("[]")) {
      return listItem?.ref.dataType.defaultValue ?? [];
    }
    switch (typeName) {
      case "string":
        ref.dataType = DEFAULT_DATA_TYPE_STRING_ARRAY;
        return listItem?.ref.dataType.defaultValue ?? "";
      case "number":
        ref.dataType = DEFAULT_DATA_TYPE_NUMBER_ARRAY;
        return listItem?.ref.dataType.defaultValue ?? null;
      case "boolean":
        ref.dataType = DEFAULT_DATA_TYPE_BOOLEAN_ARRAY;
        return listItem?.ref.dataType.defaultValue ?? false;
      case "object":
        ref.dataType = DEFAULT_DATA_TYPE_OBJECT_ARRAY;
        return listItem?.ref.dataType.defaultValue ?? {};
      default: // any
        ref.dataType = DEFAULT_DATA_TYPE_ANY_ARRAY;
        return listItem?.ref.dataType.defaultValue;
    }
  };
  const listItemValue = getDefaultItemValue();

  const addElementsIsDisabled = `${
    props.maxCount === undefined
      ? props.disabled
      : `${props.disabled} || !(${props.maxCount} <= (${ref.stateName}?.length ?? -1))`
  }`;

  const onAddElementCallback = (prefix: string) => {
    return props.itemProps
      ? `${prefix}${ref.stateSetter}((s) => {
  const newState = [...s];
  (newState${ref.stateName.split(".").splice(1).join(".")}) = [...(newState${ref.stateName.split(".").splice(1).join(".")} ?? []), ${listItemValue}];
  return newState;
})`
      : `${prefix}${ref.stateSetter}((${ref.stateName} ?? []).concat([${listItemValue}]))`;
  };

  const onAddElement = `!${props.disabled} && 
    ${
      props.maxCount === undefined
        ? onAddElementCallback("")
        : onAddElementCallback(`!(${props.maxCount} <= (${ref.stateName}?.length ?? -1)) && `)
    };`;

  const removeElementIsDisabled = `${
    props.minCount === undefined
      ? props.disabled
      : `${props.disabled} || (${props.minCount} >= (${ref.stateName}?.length ?? -1))`
  }`;

  const onRemoveElementCallback = (prefix: string) => {
    return props.itemProps
      ? `${prefix}${ref.stateSetter}((s) => {
  const newState = [...s];
  (newState${ref.stateName.split(".").splice(1).join(".")}) = value;
  return newState;
})`
      : `${prefix}${ref.stateSetter}(value)`;
  };

  const onRemoveElement = `!${props.disabled} && 
  ${
    props.minCount === undefined
      ? onRemoveElementCallback("")
      : onRemoveElementCallback(`!(${props.minCount} >= (${ref.stateName}?.length ?? -1)) && `)
  };`;

  const jsxCode = `<div>
      <Split hasGutter>
        <SplitItem>
          {'${props.label}' && (
            <label className={"pf-c-form__label"}>
              <span className={"pf-c-form__label-text"}>
                ${props.label}
              </span>
            </label>
          )}
        </SplitItem>
        <SplitItem isFilled />
        <SplitItem>
          <Button
            name='$'
            variant='plain'
            style={{ paddingLeft: '0', paddingRight: '0' }}
            disabled={${addElementsIsDisabled}}
            onClick={() => {
              ${onAddElement}
            }}
          >
            <PlusCircleIcon color='#0088ce' />
          </Button>
        </SplitItem>
      </Split>
      <div>
        {${ref.stateName}?.map((_, ${indexVariableName}) =>
          (<div
            key={${indexVariableName}}
            style={{
              marginBottom: '1rem',
              display: 'flex',
              justifyContent: 'space-between',
            }}
          >
            <div style={{ width: '100%', marginRight: '10px' }}>${listItem?.jsxCode}</div>
            <div>
              <Button
                disabled={${removeElementIsDisabled}}
                variant='plain'
                style={{ paddingLeft: '0', paddingRight: '0' }}
                onClick={() => {
                  const value = [...${ref.stateName}]
                  value.splice(${indexVariableName}, 1);
                  ${onRemoveElement}
                }}
              >
                <MinusCircleIcon color='#cc0000' />
              </Button>
            </div>
          </div>)
        )}
      </div>
    </div>`;

  function getListStateCode() {
    let stateCode = getStateCode(ref.stateName, ref.stateSetter, ref.dataType.name, "[]");
    stateCode = stateCode.includes("?.[itemIndex]") ? "" : stateCode;
    stateCode = stateCode + "\n" + (listItem?.stateCode ?? "");
    return stateCode;
  }

  const element: FormInput = {
    ref,
    pfImports: [...new Set(["Split", "SplitItem", "Button", ...(listItem?.pfImports ?? [])])],
    pfIconImports: [...new Set(["PlusCircleIcon", "MinusCircleIcon", ...(listItem?.pfIconImports ?? [])])],
    reactImports: [...new Set([...(listItem?.reactImports ?? [])])],
    requiredCode: [...new Set([...(listItem?.requiredCode ?? [])])],
    jsxCode,
    stateCode: getListStateCode(),
    isReadonly: props.disabled,
  };

  codegenCtx?.rendered.push(element);

  return renderField(element);
};

export default connectField(List);
