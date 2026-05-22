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
import { useCallback, useMemo } from "react";
import { BoxedIterator, generateUuid, Normalized } from "../../api";
import {
  NestedExpressionDispatchContextProvider,
  OnSetExpression,
  useBoxedExpressionEditorDispatch,
} from "../../BoxedExpressionEditorContext";
import { ExpressionContainer } from "../ExpressionDefinitionRoot/ExpressionContainer";
import { IteratorClause } from "./IteratorExpressionComponent";

export interface IteratorExpressionCellExpressionCellProps {
  iteratorClause: Normalized<IteratorClause>;
  rowIndex: number;
  columnIndex: number;
  columnId: string;
}

export function IteratorExpressionCell({
  rowIndex,
  columnIndex,
  parentElementId,
  iteratorClause,
}: IteratorExpressionCellExpressionCellProps & { parentElementId: string }) {
  const { setExpression } = useBoxedExpressionEditorDispatch();

  const onSetExpression: OnSetExpression = useCallback(
    ({ getNewExpression, expressionChangedArgs }) => {
      setExpression({
        setExpressionAction: (prev: Normalized<BoxedIterator>) => {
          // Do not inline these variables for type safety. See https://github.com/microsoft/TypeScript/issues/241
          switch (rowIndex) {
            case 1: {
              const ret: Normalized<BoxedIterator> = {
                ...prev,
                in: {
                  "@_id": generateUuid(),
                  expression: getNewExpression(prev.in.expression)!, // SPEC DISCREPANCY
                },
              };
              return ret;
            }
            case 2:
            default:
              if (prev.__$$element === "for") {
                const ret: Normalized<BoxedIterator> = {
                  ...prev,
                  return: {
                    "@_id": generateUuid(),
                    expression: getNewExpression(prev.return.expression)!, // SPEC DISCREPANCY
                  },
                };
                return ret;
              } else {
                const ret: Normalized<BoxedIterator> = {
                  ...prev,
                  satisfies: {
                    "@_id": generateUuid(),
                    expression: getNewExpression(prev.satisfies.expression)!, // SPEC DISCREPANCY
                  },
                };
                return ret;
              }
          }
        },
        expressionChangedArgs,
      });
    },
    [rowIndex, setExpression]
  );

  const currentExpression = useMemo(() => {
    if (typeof iteratorClause.child !== "string") {
      return iteratorClause.child?.expression;
    }
  }, [iteratorClause.child]);

  return (
    <NestedExpressionDispatchContextProvider onSetExpression={onSetExpression}>
      <ExpressionContainer
        expression={currentExpression}
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
