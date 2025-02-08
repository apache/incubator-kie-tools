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
import { useEffect } from "react";
import { Action, BoxedIterator, ExpressionChangedArgs, Normalized } from "../../api";
import { useBoxedExpressionEditor, useBoxedExpressionEditorDispatch } from "../../BoxedExpressionEditorContext";
import { IteratorClause } from "./IteratorExpressionComponent";
import { InlineEditableTextInput } from "../../table/BeeTable/InlineEditableTextInput";
import { BeeTableRef } from "../../table/BeeTable";
import { useBeeTableSelectableCellRef } from "../../selection/BeeTableSelectionContext";

export interface IteratorExpressionVariableCellProps {
  rowIndex: number;
  columnIndex: number;
  currentElementId: string;
  data: readonly IteratorClause[];
  beeTableRef: React.RefObject<BeeTableRef>;
}

export function IteratorExpressionVariableCell({
  rowIndex,
  columnIndex,
  currentElementId,
  data,
  beeTableRef,
}: IteratorExpressionVariableCellProps) {
  const { setExpression } = useBoxedExpressionEditorDispatch();
  const { isActive } = useBeeTableSelectableCellRef(rowIndex ?? 0, columnIndex ?? 0, undefined);
  const { beeGwtService, isReadOnly } = useBoxedExpressionEditor();

  // Selecting the context result cell should be the parent data type
  useEffect(() => {
    if (isActive) {
      beeGwtService?.selectObject(`${currentElementId}-iteratorVariable`);
    }
  }, [beeGwtService, isActive, currentElementId]);

  return (
    <div
      style={{
        minHeight: "60px",
        display: "flex",
        alignItems: "center",
        justifyContent: "center",
      }}
    >
      <InlineEditableTextInput
        value={data[rowIndex].child as string}
        onChange={(updatedValue) => {
          const expressionChangedArgs: ExpressionChangedArgs =
            (data[rowIndex].child as string) === ""
              ? {
                  action: Action.IteratorVariableDefined,
                }
              : {
                  action: Action.VariableChanged,
                  variableUuid: currentElementId,
                  nameChange: {
                    from: data[rowIndex].child as string,
                    to: updatedValue,
                  },
                };

          setExpression({
            setExpressionAction: (prev: Normalized<BoxedIterator>) => {
              // Do not inline this variable for type safety. See https://github.com/microsoft/TypeScript/issues/241
              const ret: Normalized<BoxedIterator> = {
                ...prev,
                "@_iteratorVariable": updatedValue,
              };
              return ret;
            },
            expressionChangedArgs,
          });
        }}
        rowIndex={rowIndex}
        columnIndex={columnIndex}
        setActiveCellEditing={(value) => {
          beeTableRef.current?.setActiveCellEditing(value);
        }}
        isReadOnly={isReadOnly ?? false}
      />
    </div>
  );
}
