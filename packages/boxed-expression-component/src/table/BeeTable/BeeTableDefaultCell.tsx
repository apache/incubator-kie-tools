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
import { BeeTableCellUpdate } from ".";
import { BeeTableEditableCellContent } from "./BeeTableEditableCellContent";
import { useBeeTableSelectableCellRef } from "../../selection/BeeTableSelectionContext";
import { useBoxedExpressionEditor } from "../../BoxedExpressionEditorContext";
import * as ReactTable from "react-table";
import { FeelVariables } from "@kie-tools/dmn-feel-antlr4-parser";

export function BeeTableDefaultCell<R extends object>({
  cellProps,
  onCellUpdates,
  isReadOnly,
  columnIndex,
  setEditing,
  navigateHorizontally,
  navigateVertically,
  variables,
}: {
  isReadOnly: boolean;
  cellProps: ReactTable.CellProps<R, string | { content: string; id: string }>;
  onCellUpdates?: (cellUpdates: BeeTableCellUpdate<R>[]) => void;
  columnIndex: number;
  setEditing: React.Dispatch<React.SetStateAction<boolean>>;
  navigateVertically: (args: { isShiftPressed: boolean }) => void;
  navigateHorizontally: (args: { isShiftPressed: boolean }) => void;
  variables?: FeelVariables;
}) {
  const onCellChanged = useCallback(
    (value: string) => {
      onCellUpdates?.([
        {
          value,
          row: cellProps.row.original,
          rowIndex: cellProps.row.index,
          column: cellProps.column,
          columnIndex: columnIndex - 1, // Subtract one because of the rowIndex column.
        },
      ]);
    },
    [cellProps.column, cellProps.row.index, cellProps.row.original, columnIndex, onCellUpdates]
  );

  const getValue = useCallback(() => {
    return typeof cellProps.value === "string" ? cellProps.value : cellProps.value.content;
  }, [cellProps.value]);

  const { isActive, isEditing } = useBeeTableSelectableCellRef(
    cellProps.row.index,
    columnIndex,
    onCellChanged,
    getValue
  );

  return (
    <BeeTableEditableCellContent
      isEditing={isEditing}
      isActive={isActive}
      setEditing={setEditing}
      onChange={onCellChanged}
      value={typeof cellProps.value === "string" ? cellProps.value : cellProps.value?.content}
      isReadOnly={isReadOnly}
      onFeelEnterKeyDown={navigateVertically}
      onFeelTabKeyDown={navigateHorizontally}
      variables={variables}
      expressionId={typeof cellProps.value === "string" ? "" : cellProps.value?.id}
    />
  );
}
