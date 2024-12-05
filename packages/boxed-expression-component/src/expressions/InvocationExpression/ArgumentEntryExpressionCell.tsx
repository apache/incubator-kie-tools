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
import { BeeTableCellProps, BoxedInvocation, Normalized } from "../../api";
import { ExpressionContainer } from "../ExpressionDefinitionRoot/ExpressionContainer";
import {
  NestedExpressionDispatchContextProvider,
  OnSetExpression,
  useBoxedExpressionEditor,
  useBoxedExpressionEditorDispatch,
} from "../../BoxedExpressionEditorContext";
import { ROWTYPE } from "./InvocationExpression";
import "../ContextExpression/ContextEntryExpressionCell.css";
import { useBeeTableSelectableCellRef } from "../../selection/BeeTableSelectionContext";

export const ArgumentEntryExpressionCell: React.FunctionComponent<
  BeeTableCellProps<ROWTYPE> & { parentElementId: string }
> = ({ data, rowIndex, columnIndex, parentElementId }) => {
  const { setExpression } = useBoxedExpressionEditorDispatch();
  const { expression, variable, index } = data[rowIndex];
  const { isActive } = useBeeTableSelectableCellRef(rowIndex, columnIndex, undefined);
  const { beeGwtService } = useBoxedExpressionEditor();

  useEffect(() => {
    if (isActive) {
      expression ? beeGwtService?.selectObject(expression["@_id"]) : "";
    }
  }, [beeGwtService, columnIndex, expression, isActive]);

  const onSetExpression = useCallback<OnSetExpression>(
    ({ getNewExpression, expressionChangedArgs }) => {
      setExpression({
        setExpressionAction: (prev: Normalized<BoxedInvocation>) => {
          const newBindings = [...(prev.binding ?? [])];
          newBindings[index] = {
            ...newBindings[index],
            expression: getNewExpression(newBindings[index]?.expression ?? undefined!),
          };

          // Do not inline this variable for type safety. See https://github.com/microsoft/TypeScript/issues/241
          const ret: Normalized<BoxedInvocation> = {
            ...prev,
            binding: newBindings,
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
        parentElementId={parentElementId}
        parentElementTypeRef={variable["@_typeRef"]}
        parentElementName={variable["@_name"]}
      />
    </NestedExpressionDispatchContextProvider>
  );
};
