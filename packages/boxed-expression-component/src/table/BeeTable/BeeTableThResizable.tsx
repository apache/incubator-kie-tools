/*
 * Copyright 2022 Red Hat, Inc. and/or its affiliates.
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

import { PopoverPosition } from "@patternfly/react-core/dist/js/components/Popover";
import * as React from "react";
import { useMemo } from "react";
import * as ReactTable from "react-table";
import { ExpressionDefinition } from "../../api";
import { ExpressionDefinitionHeaderMenu } from "../../expressions/ExpressionDefinitionHeaderMenu";
import { Resizer } from "../../resizing/Resizer";
import { useBeeTableResizableCell } from "../../resizing/BeeTableResizableColumnsContextProvider";
import { BeeTableTh } from "./BeeTableTh";
import { ResizerStopBehavior } from "../../resizing/ResizingWidthsContext";

export interface BeeTableThResizableProps<R extends object> {
  onColumnAdded?: (args: { beforeIndex: number; groupType: string | undefined }) => void;
  column: ReactTable.ColumnInstance<R>;
  columnIndex: number;
  rowIndex: number;
  rowSpan: number;
  editColumnLabel?: string | { [groupType: string]: string };
  isEditableHeader: boolean;
  getColumnKey: (column: ReactTable.ColumnInstance<R>) => string;
  getColumnLabel: (groupType: string | undefined) => string | undefined;
  onExpressionHeaderUpdated: (args: Pick<ExpressionDefinition, "name" | "dataType">) => void;
  onHeaderClick: (columnKey: string) => () => void;
  reactTableInstance: ReactTable.TableInstance<R>;
  headerCellInfo: React.ReactElement;
  shouldShowColumnsInlineControls: boolean;
  resizerStopBehavior: ResizerStopBehavior;
  lastColumnMinWidth?: number;
}

export function BeeTableThResizable<R extends object>({
  column,
  columnIndex,
  rowIndex,
  rowSpan,
  isEditableHeader,
  getColumnKey,
  onExpressionHeaderUpdated,
  onHeaderClick,
  headerCellInfo,
  onColumnAdded,
  resizerStopBehavior,
  shouldShowColumnsInlineControls,
  lastColumnMinWidth,
}: BeeTableThResizableProps<R>) {
  const columnKey = useMemo(() => getColumnKey(column), [column, getColumnKey]);

  const cssClasses = useMemo(() => {
    const cssClasses = [columnKey, "data-header-cell"];
    if (!column.dataType) {
      cssClasses.push("no-clickable-cell");
    }

    cssClasses.push(column.groupType ?? "");
    // cssClasses.push(column.cssClasses ?? ""); // FIXME: Tiago -> Breaking Decision tables because of positioning of rowSpan=2 column headers
    return cssClasses.join(" ");
  }, [column, columnKey]);

  const onClick = useMemo(() => {
    return onHeaderClick(columnKey);
  }, [columnKey, onHeaderClick]);

  const { resizingWidth, setResizingWidth } = useBeeTableResizableCell(
    columnIndex,
    resizerStopBehavior,
    column.setWidth,
    // If the column specifies a width, then we should respect its minWidth as well.
    column.width ? Math.max(lastColumnMinWidth ?? column.minWidth ?? 0, column.width ?? 0) : undefined
  );

  return (
    <BeeTableTh<R>
      className={cssClasses}
      thProps={{ ...column.getHeaderProps(), style: { position: "relative" } }}
      onClick={onClick}
      columnIndex={columnIndex}
      rowIndex={rowIndex}
      rowSpan={rowSpan}
      onColumnAdded={onColumnAdded}
      groupType={column.groupType}
      isLastLevelColumn={(column.columns?.length ?? 0) <= 0}
      shouldShowColumnsInlineControls={shouldShowColumnsInlineControls}
      column={column}
    >
      <div
        className="header-cell"
        data-ouia-component-type="expression-column-header"
        style={{ width: column.width ? resizingWidth?.value : undefined }}
      >
        {column.dataType && isEditableHeader ? (
          <ExpressionDefinitionHeaderMenu
            position={PopoverPosition.bottom}
            selectedExpressionName={column.label}
            selectedDataType={column.dataType}
            onExpressionHeaderUpdated={onExpressionHeaderUpdated}
          >
            {headerCellInfo}
          </ExpressionDefinitionHeaderMenu>
        ) : (
          headerCellInfo
        )}
      </div>
      <Resizer
        minWidth={lastColumnMinWidth ?? column.minWidth}
        width={column.width}
        setWidth={column.setWidth}
        resizingWidth={resizingWidth}
        setResizingWidth={setResizingWidth}
      />
    </BeeTableTh>
  );
}
