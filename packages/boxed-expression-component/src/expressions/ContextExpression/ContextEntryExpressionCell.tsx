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
import { useCallback, useEffect } from "react";
import { BeeTableCellProps, BoxedContext, BoxedExpression, generateUuid, Normalized } from "../../api";
import {
  NestedExpressionDispatchContextProvider,
  OnSetExpression,
  useBoxedExpressionEditor,
  useBoxedExpressionEditorDispatch,
} from "../../BoxedExpressionEditorContext";
import { ExpressionContainer } from "../ExpressionDefinitionRoot/ExpressionContainer";
import { ROWTYPE } from "./ContextExpression";
import "./ContextEntryExpressionCell.css";
import { useBeeTableSelectableCellRef } from "../../selection/BeeTableSelectionContext";

export const ContextEntryExpressionCell: React.FunctionComponent<BeeTableCellProps<ROWTYPE>> = ({
  data,
  rowIndex,
  columnIndex,
}) => {
  const { setExpression } = useBoxedExpressionEditorDispatch();
  const { variable, expression, index } = data[rowIndex];
  const { isActive } = useBeeTableSelectableCellRef(rowIndex, columnIndex, undefined);
  const { beeGwtService } = useBoxedExpressionEditor();

  useEffect(() => {
    if (isActive) {
      beeGwtService?.selectObject((expression as BoxedExpression)?.["@_id"]);
    }
  }, [beeGwtService, columnIndex, expression, isActive]);

  const onSetExpression = useCallback<OnSetExpression>(
    ({ getNewExpression, expressionChangedArgs }) => {
      setExpression({
        setExpressionAction: (prev: Normalized<BoxedContext>) => {
          const newContextEntries = [...(prev.contextEntry ?? [])];
          const newExpression = getNewExpression(newContextEntries[index]?.expression ?? undefined);
          newContextEntries[index] = {
            ...newContextEntries[index],
            expression: newExpression!, // SPEC DISCREPANCY: Accepting undefined expression
            variable: {
              ...newContextEntries[index].variable,
              "@_id": newContextEntries[index]?.variable?.["@_id"] ?? generateUuid(),
              "@_name": newExpression?.["@_label"] ?? newContextEntries[index].variable!["@_name"],
              "@_typeRef": newExpression?.["@_typeRef"] ?? newContextEntries[index].variable!["@_typeRef"],
            },
          };

          // Do not inline this variable for type safety. See https://github.com/microsoft/TypeScript/issues/241
          const ret: Normalized<BoxedContext> = {
            ...prev,
            contextEntry: newContextEntries,
          };

          return ret;
        },
        expressionChangedArgs,
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
        parentElementId={variable["@_id"]}
        parentElementTypeRef={variable["@_typeRef"]}
        parentElementName={variable["@_name"]}
      />
    </NestedExpressionDispatchContextProvider>
  );
};
