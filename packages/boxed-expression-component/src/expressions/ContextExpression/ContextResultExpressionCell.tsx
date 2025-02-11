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
import { BoxedContext, generateUuid, Normalized } from "../../api";
import {
  NestedExpressionDispatchContextProvider,
  OnSetExpression,
  useBoxedExpressionEditorDispatch,
} from "../../BoxedExpressionEditorContext";
import { ExpressionContainer } from "../ExpressionDefinitionRoot/ExpressionContainer";
import { solveResultAndEntriesIndex } from "./ContextExpression";

export function ContextResultExpressionCell(props: {
  contextExpression: Normalized<BoxedContext>;
  rowIndex: number;
  columnIndex: number;
}) {
  const { setExpression } = useBoxedExpressionEditorDispatch();

  const { resultIndex } = solveResultAndEntriesIndex({
    contextEntries: props.contextExpression.contextEntry ?? [],
    rowIndex: props.rowIndex,
  });

  const onSetExpression = useCallback<OnSetExpression>(
    ({ getNewExpression, expressionChangedArgs }) => {
      setExpression({
        setExpressionAction: (prev: Normalized<BoxedContext>) => {
          const newContextEntries = [...(prev.contextEntry ?? [])];

          const newExpression = getNewExpression(newContextEntries[resultIndex]?.expression);

          if (resultIndex <= -1) {
            newContextEntries.push({
              "@_id": generateUuid(),
              expression: newExpression!, // SPEC DISCREPANCY:
            });
          } else if (newExpression) {
            newContextEntries.splice(resultIndex, 1, {
              ...newContextEntries[resultIndex],
              expression: newExpression,
            });
          } else {
            newContextEntries.splice(resultIndex, 1);
          }

          // Do not inline this variable for type safety. See https://github.com/microsoft/TypeScript/issues/241
          const ret: Normalized<BoxedContext> = {
            ...prev,
            contextEntry: newContextEntries,
            "@_label": newExpression?.["@_label"] ?? prev["@_label"],
            "@_typeRef": newExpression?.["@_typeRef"] ?? prev["@_typeRef"],
          };

          return ret;
        },
        expressionChangedArgs,
      });
    },
    [resultIndex, setExpression]
  );

  const resultEntry = resultIndex <= -1 ? undefined : props.contextExpression.contextEntry?.[resultIndex];

  return (
    <NestedExpressionDispatchContextProvider onSetExpression={onSetExpression}>
      <ExpressionContainer
        expression={resultEntry?.expression}
        isResetSupported={true}
        isNested={true}
        rowIndex={resultIndex}
        columnIndex={props.columnIndex}
        parentElementId={props.contextExpression["@_id"]}
        parentElementTypeRef={props.contextExpression["@_typeRef"]}
        parentElementName={props.contextExpression["@_label"]}
      />
    </NestedExpressionDispatchContextProvider>
  );
}
