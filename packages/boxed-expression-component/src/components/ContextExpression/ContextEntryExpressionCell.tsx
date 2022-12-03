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
import { DmnBuiltInDataType, ContextExpressionDefinitionEntry, ROWGENERICTYPE, ExpressionDefinition } from "../../api";
import { ContextEntryExpression } from "./ContextEntryExpression";
import * as _ from "lodash";

export interface ContextEntryExpressionCellProps {
  // This name ('data') can't change, as this is used as a "defaultCell" on "defaultCellByColumnName".
  data: ContextExpressionDefinitionEntry[];
  onRowUpdate: (rowIndex: number, updatedRow: ROWGENERICTYPE) => void;
  rowIndex: number;
  columnId: string;
}

export const ContextEntryExpressionCell: React.FunctionComponent<ContextEntryExpressionCellProps> = ({
  data: contextEntries,
  rowIndex,
  onRowUpdate,
}) => {
  // FIXME: Tiago
  const onUpdatingRecursiveExpression = useCallback(
    (expression: ExpressionDefinition) => {
      const updatedEntryInfo = { ...contextEntries[rowIndex].entryInfo };
      if (expression.name && expression.dataType) {
        updatedEntryInfo.name = expression.name;
        updatedEntryInfo.dataType = expression.dataType;
      }

      onRowUpdate(rowIndex, { ...contextEntries[rowIndex], entryInfo: updatedEntryInfo, entryExpression: expression });
    },
    [onRowUpdate, contextEntries, rowIndex]
  );

  return (
    <div className="context-entry-expression-cell">
      <ContextEntryExpression
        expression={contextEntries[rowIndex].entryExpression}
        onUpdatingRecursiveExpression={onUpdatingRecursiveExpression}
        onExpressionResetting={contextEntries[rowIndex].onExpressionResetting}
      />
    </div>
  );
};
