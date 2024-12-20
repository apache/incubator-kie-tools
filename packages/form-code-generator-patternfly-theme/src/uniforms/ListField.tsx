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
import { connectField, context, HTMLFieldProps, joinName } from "uniforms/cjs";
import { getInputReference, getStateCode, renderField } from "./utils/Utils";
import { codeGenContext } from "./CodeGenContext";
import { FormInput, InputReference } from "../api";
import { ARRAY } from "./utils/dataTypes";
import { renderListItemFragmentWithContext } from "./rendering/RenderingUtils";
import { ListItemProps } from "./rendering/ListItemField";

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
  const ref: InputReference = getInputReference(props.name, ARRAY);

  const uniformsContext = useContext(context);
  const codegenCtx = useContext(codeGenContext);

  const listItem = renderListItemFragmentWithContext(
    uniformsContext,
    "$",
    {
      isListItem: true,
      indexVariableName: "itemIndex",
      listName: props.name,
      listStateName: ref.stateName,
      listStateSetter: ref.stateSetter,
    },
    props.disabled
  );
  const jsxCode = `<div fieldId={'${props.id}'}>
      <Split hasGutter>
        <SplitItem>
          {'${props.label}' && (
            <label>
              '${props.label}'
            </label>
          )}
        </SplitItem>
        <SplitItem isFilled />
        <SplitItem>
          <Button
            name='$'
            variant='plain'
            style={{ paddingLeft: '0', paddingRight: '0' }}
            disabled={${props.maxCount === undefined ? props.disabled : `${props.disabled} || !(${props.maxCount} <= (${ref.stateName}?.length ?? -1))`}}
            onClick={() => {
              !${props.disabled} && ${props.maxCount === undefined ? `${ref.stateSetter}((${ref.stateName} ?? []).concat([]))` : `!(${props.maxCount} <= (${ref.stateName}?.length ?? -1)) && ${ref.stateSetter}((${ref.stateName} ?? []).concat([]))`};
            }}
          >
            +
            {/* <PlusCircleIcon color='#0088ce' /> */} 
          </Button>
        </SplitItem>
      </Split>
      <div>
        {${ref.stateName}?.map((_, itemIndex) =>
          (<div
            key={itemIndex}
            style={{
              marginBottom: '1rem',
              display: 'flex',
              justifyContent: 'space-between',
            }}
          >
            <div style={{ width: '100%', marginRight: '10px' }}>${listItem?.jsxCode}</div>
            <div>
              <Button
                disabled={${props.minCount === undefined ? props.disabled : `${props.disabled} || (${props.minCount} >= (${ref.stateName}?.length ?? -1))`}}
                variant='plain'
                style={{ paddingLeft: '0', paddingRight: '0' }}
                onClick={() => {
                  const value = ${ref.stateName}!.slice();
                  value.splice(${+joinName(null, "")[joinName(null, "").length - 1]}, 1);
                  !${props.disabled} && ${props.minCount === undefined ? `${ref.stateSetter}(value)` : `!(${props.minCount} >= (${ref.stateName}?.length ?? -1)) && ${ref.stateSetter}(value)`};
                }}
              >
                - { /* <MinusCircleIcon color='#cc0000' /> */}
              </Button>
            </div>
          </div>)
        )}
      </div>
    </div>`;

  // TODO ADD PLUS AND MINUS ICONS
  const element: FormInput = {
    ref,
    pfImports: [...new Set(["Split", "SplitItem", "Button", ...(listItem?.pfImports ?? [])])],
    reactImports: [...new Set([...(listItem?.reactImports ?? [])])],
    requiredCode: [...new Set([...(listItem?.requiredCode ?? [])])],
    // requiredIcons: ["PlusCircleIcon", "MinusCircleIcon"],
    jsxCode,
    stateCode: getStateCode(ref.stateName, ref.stateSetter, "any[]", "[]"),
    isReadonly: props.disabled,
  };

  codegenCtx?.rendered.push(element);

  return renderField(element);
};

export default connectField(List);
