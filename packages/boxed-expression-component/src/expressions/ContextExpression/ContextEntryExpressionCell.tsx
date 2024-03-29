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
import { BeeTableCellProps, BoxedContext } from "../../api";
import {
  NestedExpressionDispatchContextProvider,
  OnSetExpression,
  useBoxedExpressionEditorDispatch,
} from "../../BoxedExpressionEditorContext";
import { ExpressionContainer } from "../ExpressionDefinitionRoot/ExpressionContainer";
import { ROWTYPE } from "./ContextExpression";
import "./ContextEntryExpressionCell.css";

export const ContextEntryExpressionCell: React.FunctionComponent<BeeTableCellProps<ROWTYPE>> = ({
  data,
  rowIndex,
  columnIndex,
}) => {
  const { setExpression } = useBoxedExpressionEditorDispatch();

  const { variable, expression, index } = data[rowIndex];

  const onSetExpression = useCallback<OnSetExpression>(
    ({ getNewExpression }) => {
      setExpression((prev: BoxedContext) => {
        const newContextEntries = [...(prev.contextEntry ?? [])];
        const newExpression = getNewExpression(newContextEntries[index]?.expression ?? undefined);
        newContextEntries[index] = {
          ...newContextEntries[index],
          expression: newExpression!, // SPEC DISCREPANCY: Accepting undefined expression
          variable: {
            "@_name": newExpression?.["@_label"] ?? newContextEntries[index].variable!["@_name"],
            "@_typeRef": newExpression?.["@_typeRef"] ?? newContextEntries[index].variable!["@_typeRef"],
          },
        };

        return { ...prev, contextEntry: newContextEntries };
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
        parentTypeRef={variable["@_typeRef"]}
        expressionName={variable["@_name"]}
      />
    </NestedExpressionDispatchContextProvider>
  );
};
