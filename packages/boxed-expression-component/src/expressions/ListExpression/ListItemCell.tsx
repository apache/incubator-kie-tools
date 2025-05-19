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
import { useCallback } from "react";
import { BeeTableCellProps, BoxedList, Normalized } from "../../api";
import {
  useBoxedExpressionEditorDispatch,
  NestedExpressionDispatchContextProvider,
  OnSetExpression,
} from "../../BoxedExpressionEditorContext";
import { ExpressionContainer } from "../ExpressionDefinitionRoot/ExpressionContainer";
import { ROWTYPE } from "./ListExpression";
import { DMN15__tList } from "@kie-tools/dmn-marshaller/dist/schemas/dmn-1_5/ts-gen/types";

export function ListItemCell({
  rowIndex,
  data: items,
  columnIndex,
  parentElementId,
  listExpression,
}: BeeTableCellProps<ROWTYPE> & { parentElementId: string; listExpression: Normalized<DMN15__tList> }) {
  const { setExpression } = useBoxedExpressionEditorDispatch();

  const onSetExpression = useCallback<OnSetExpression>(
    ({ getNewExpression, expressionChangedArgs }) => {
      setExpression({
        setExpressionAction: (prev: Normalized<BoxedList>) => {
          const newItems = [...(prev.expression ?? [])];
          newItems[rowIndex] = getNewExpression(newItems[rowIndex])!; // SPEC DISCREPANCY: Allowing undefined expression

          // Do not inline this variable for type safety. See https://github.com/microsoft/TypeScript/issues/241
          const ret: Normalized<BoxedList> = {
            ...prev,
            expression: newItems,
          };

          return ret;
        },
        expressionChangedArgs,
      });
    },
    [rowIndex, setExpression]
  );

  return (
    <NestedExpressionDispatchContextProvider onSetExpression={onSetExpression}>
      <ExpressionContainer
        expression={items[rowIndex]?.expression}
        isResetSupported={true}
        isNested={true}
        rowIndex={rowIndex}
        columnIndex={columnIndex}
        parentElementId={parentElementId}
        parentElementTypeRef={listExpression["@_typeRef"]}
      />
    </NestedExpressionDispatchContextProvider>
  );
}
