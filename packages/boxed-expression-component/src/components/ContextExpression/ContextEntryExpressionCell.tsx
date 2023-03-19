/*
 * Copyright 2021 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import "./ContextEntryExpressionCell.css";
import * as React from "react";
import { useCallback } from "react";
import { CellProps, ContextEntries, DataType, ExpressionProps } from "../../api";
import { DataRecord } from "react-table";
import { ContextEntryExpression } from "./ContextEntryExpression";
import * as _ from "lodash";

export interface ContextEntryExpressionCellProps extends CellProps {
  data: ContextEntries;
  onRowUpdate: (rowIndex: number, updatedRow: DataRecord) => void;
}

export const ContextEntryExpressionCell: React.FunctionComponent<ContextEntryExpressionCellProps> = ({
  data,
  rowIndex,
  onRowUpdate,
}) => {
  const onUpdatingRecursiveExpression = useCallback(
    (expression: ExpressionProps) => {
      const updatedEntryInfo = { ...data[rowIndex].entryInfo };
      if (data[rowIndex].nameAndDataTypeSynchronized && _.size(expression.name) && _.size(expression.dataType)) {
        updatedEntryInfo.name = expression.name as string;
        updatedEntryInfo.dataType = expression.dataType as DataType;
      }

      onRowUpdate(rowIndex, { ...data[rowIndex], entryInfo: updatedEntryInfo, entryExpression: expression });
    },
    [onRowUpdate, data, rowIndex]
  );

  return (
    <div className="context-entry-expression-cell">
      <ContextEntryExpression
        expression={data[rowIndex].entryExpression}
        onUpdatingRecursiveExpression={onUpdatingRecursiveExpression}
        onExpressionResetting={data[rowIndex].onExpressionResetting}
      />
    </div>
  );
};
