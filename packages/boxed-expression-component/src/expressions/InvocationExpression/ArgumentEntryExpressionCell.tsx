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
import { BoxedInvocation, BeeTableCellProps } from "../../api";
import {
  NestedExpressionDispatchContextProvider,
  OnSetExpression,
  useBoxedExpressionEditorDispatch,
} from "../../BoxedExpressionEditorContext";
import { useCallback } from "react";
import { ExpressionContainer } from "../ExpressionDefinitionRoot/ExpressionContainer";
import { ROWTYPE } from "./InvocationExpression";
import "../ContextExpression/ContextEntryExpressionCell.css";

export const ArgumentEntryExpressionCell: React.FunctionComponent<
  BeeTableCellProps<ROWTYPE> & { parentElementId: string }
> = ({ data, rowIndex, columnIndex, parentElementId }) => {
  const { setExpression } = useBoxedExpressionEditorDispatch();

  const { expression, variable, index } = data[rowIndex];

  const onSetExpression = useCallback<OnSetExpression>(
    ({ getNewExpression }) => {
      setExpression((prev: BoxedInvocation) => {
        const newBindings = [...(prev.binding ?? [])];
        newBindings[index] = {
          ...newBindings[index],
          expression: getNewExpression(newBindings[index]?.expression ?? undefined!),
        };

        // Do not inline this variable for type safety. See https://github.com/microsoft/TypeScript/issues/241
        const ret: BoxedInvocation = {
          ...prev,
          binding: newBindings,
        };

        return ret;
      });
    },
    [index, setExpression]
  );

  return (
    <NestedExpressionDispatchContextProvider onSetExpression={onSetExpression}>
      <ExpressionContainer
        expression={expression}
        isResetSupported={true}
        isNested={true}
        rowIndex={rowIndex}
        columnIndex={columnIndex}
        parentElementId={parentElementId}
        parentElementTypeRef={variable["@_typeRef"]}
        parentElementName={variable["@_name"]}
      />
    </NestedExpressionDispatchContextProvider>
  );
};
