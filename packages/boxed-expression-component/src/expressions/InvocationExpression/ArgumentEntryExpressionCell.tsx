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

import "../ContextExpression/ContextEntryExpressionCell.css";
import * as React from "react";
import {
  ContextExpressionDefinitionEntry,
  ExpressionDefinitionLogicType,
  InvocationExpressionDefinition,
} from "../../api";
import {
  NestedExpressionDispatchContextProvider,
  useBoxedExpressionEditorDispatch,
} from "../BoxedExpressionEditor/BoxedExpressionEditorContext";
import { useCallback } from "react";
import { ExpressionContainer } from "../ExpressionDefinitionRoot/ExpressionContainer";

export interface ArgumentEntryExpressionCellProps {
  // This name ('data') can't change, as this is used on "cellComponentByColumnAccessor".
  data: readonly ContextExpressionDefinitionEntry[];
  rowIndex: number;
  columnIndex: number;
  parentElementId: string;
}

export const ArgumentEntryExpressionCell: React.FunctionComponent<ArgumentEntryExpressionCellProps> = ({
  data: argumentEntries,
  rowIndex,
  columnIndex,
  parentElementId,
}) => {
  const { setExpression } = useBoxedExpressionEditorDispatch();

  const onSetExpression = useCallback(
    ({ getNewExpression }) => {
      setExpression((prev: InvocationExpressionDefinition) => {
        const argumentEntries = [...(prev.bindingEntries ?? [])];
        argumentEntries[rowIndex].entryExpression = getNewExpression(
          argumentEntries[rowIndex]?.entryExpression ?? { logicType: ExpressionDefinitionLogicType.Undefined }
        );
        return { ...prev, bindingEntries: argumentEntries };
      });
    },
    [rowIndex, setExpression]
  );

  return (
    <NestedExpressionDispatchContextProvider onSetExpression={onSetExpression}>
      <ExpressionContainer
        expression={argumentEntries[rowIndex]?.entryExpression}
        isResetSupported={true}
        isNested={true}
        rowIndex={rowIndex}
        columnIndex={columnIndex}
        parentElementId={parentElementId}
      />
    </NestedExpressionDispatchContextProvider>
  );
};
