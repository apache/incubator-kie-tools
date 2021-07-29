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
import { useCallback, useEffect, useRef } from "react";
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
  row: { index },
  onRowUpdate,
}) => {
  const contextEntry = data[index];

  const entryInfo = useRef(contextEntry.entryInfo);

  const entryExpression = useRef({
    uid: contextEntry.entryExpression.uid,
    ...contextEntry.entryExpression,
  } as ExpressionProps);

  useEffect(() => {
    entryInfo.current = contextEntry.entryInfo;
  }, [contextEntry.entryInfo]);

  useEffect(() => {
    entryExpression.current = contextEntry.entryExpression;
    onRowUpdate(index, { ...contextEntry, entryInfo: entryInfo.current, entryExpression: entryExpression.current });
    // Every time, for an expression, its logic type changes, it means that corresponding entry has been just updated
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [contextEntry.entryExpression.logicType]);

  const onUpdatingRecursiveExpression = useCallback((expression: ExpressionProps) => {
    entryExpression.current = { ...expression };
    const updatedEntryInfo = { ...entryInfo.current };
    if (contextEntry.nameAndDataTypeSynchronized && _.size(expression.name) && _.size(expression.dataType)) {
      updatedEntryInfo.name = expression.name as string;
      updatedEntryInfo.dataType = expression.dataType as DataType;
    }
    onRowUpdate(index, { ...contextEntry, entryInfo: updatedEntryInfo, entryExpression: expression });
    // Callback should never change
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, []);

  return (
    <div className="context-entry-expression-cell">
      <ContextEntryExpression
        expression={entryExpression.current}
        onUpdatingRecursiveExpression={onUpdatingRecursiveExpression}
        onExpressionResetting={contextEntry.onExpressionResetting}
      />
    </div>
  );
};
