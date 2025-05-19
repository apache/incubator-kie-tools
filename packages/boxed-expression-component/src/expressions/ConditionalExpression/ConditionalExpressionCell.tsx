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

import { BeeTableCellProps, BoxedConditional, Normalized } from "../../api";

import {
  NestedExpressionDispatchContextProvider,
  OnSetExpression,
  useBoxedExpressionEditorDispatch,
} from "../../BoxedExpressionEditorContext";
import * as React from "react";
import { useCallback } from "react";
import { ExpressionContainer } from "../ExpressionDefinitionRoot/ExpressionContainer";
import { ROWTYPE } from "./ConditionalExpression";

export function ConditionalExpressionCell({
  data,
  rowIndex,
  columnIndex,
  parentElementId,
}: BeeTableCellProps<ROWTYPE> & { parentElementId: string }) {
  const { setExpression } = useBoxedExpressionEditorDispatch();

  const onSetExpression = useCallback<OnSetExpression>(
    ({ getNewExpression, expressionChangedArgs }) => {
      setExpression({
        setExpressionAction: (prev: Normalized<BoxedConditional>) => {
          if (rowIndex === 0) {
            // Do not inline this variable for type safety. See https://github.com/microsoft/TypeScript/issues/241
            const ret: Normalized<BoxedConditional> = {
              ...prev,
              if: { ...prev.if, expression: getNewExpression(prev.if.expression)! },
            };
            return ret;
          } else if (rowIndex === 1) {
            // Do not inline this variable for type safety. See https://github.com/microsoft/TypeScript/issues/241
            const ret: Normalized<BoxedConditional> = {
              ...prev,
              then: { ...prev.then, expression: getNewExpression(prev.then.expression)! },
            };
            return ret;
          } else if (rowIndex === 2) {
            // Do not inline this variable for type safety. See https://github.com/microsoft/TypeScript/issues/241
            const ret: Normalized<BoxedConditional> = {
              ...prev,
              else: { ...prev.else, expression: getNewExpression(prev.else.expression)! },
            };
            return ret;
          } else {
            throw new Error("ConditionalExpression shouldn't have more than 3 rows.");
          }
        },
        expressionChangedArgs,
      });
    },
    [rowIndex, setExpression]
  );

  return (
    <NestedExpressionDispatchContextProvider onSetExpression={onSetExpression}>
      <ExpressionContainer
        expression={data[rowIndex].part.expression}
        isResetSupported={true}
        isNested={true}
        rowIndex={rowIndex}
        columnIndex={columnIndex}
        parentElementId={parentElementId}
        parentElementTypeRef={undefined}
      />
    </NestedExpressionDispatchContextProvider>
  );
}
