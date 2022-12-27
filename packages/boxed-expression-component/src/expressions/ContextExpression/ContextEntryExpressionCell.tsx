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
import { ContextExpressionDefinitionEntry } from "../../api";
import { ContextEntryExpression } from "./ContextEntryExpression";
import * as _ from "lodash";
import { useBeeTableCell } from "../../table/BeeTable/BeeTableSelectionContext";

export interface ContextEntryExpressionCellProps {
  // This name ('data') can't change, as this is used on "cellComponentByColumnId".
  data: readonly ContextExpressionDefinitionEntry[];
  rowIndex: number;
  columnIndex: number;
}

export const ContextEntryExpressionCell: React.FunctionComponent<ContextEntryExpressionCellProps> = ({
  data: contextEntries,
  rowIndex,
  columnIndex,
}) => {
  const { isActive, isEditing } = useBeeTableCell(rowIndex, columnIndex);
  return (
    <ContextEntryExpression
      expression={contextEntries[rowIndex].entryExpression}
      isActiveOrEditing={isActive || isEditing}
    />
  );
};
