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
import { useCallback, useMemo, useState } from "react";
import * as ReactTable from "react-table";
import { ExpressionDefinition } from "../../api";
import { ExpressionDefinitionHeaderMenu } from "../../expressions/ExpressionDefinitionHeaderMenu";
import { Resizer } from "../../resizing/Resizer";
import { useBeeTableColumnWidth } from "./BeeTableColumnResizingWidthsContextProvider";
import { useBeeTableCell } from "./BeeTableSelectionContext";
import { BeeTableTh } from "./BeeTableTh";

export interface BeeTableThResizableProps<R extends object> {
  onColumnAdded?: (args: { beforeIndex: number; groupType: string | undefined }) => void;
  column: ReactTable.ColumnInstance<R>;
  columnIndex: number;
  editColumnLabel?: string | { [groupType: string]: string };
  editableHeader: boolean;
  getColumnKey: (column: ReactTable.ColumnInstance<R>) => string;
  getColumnLabel: (groupType: string | undefined) => string | undefined;
  onExpressionHeaderUpdated: (args: Pick<ExpressionDefinition, "name" | "dataType">) => void;
  onHeaderClick: (columnKey: string) => () => void;
  reactTableInstance: ReactTable.TableInstance<R>;
  xPosition: number;
  renderHeaderCellInfo: (
    column: ReactTable.ColumnInstance<R>,
    columnIndex: number,
    onAnnotationCellToggle?: (isReadMode: boolean) => void
  ) => React.ReactElement;
}

export function BeeTableThResizable<R extends object>({
  column,
  columnIndex,
  editableHeader,
  getColumnKey,
  getColumnLabel,
  onExpressionHeaderUpdated,
  onHeaderClick,
  renderHeaderCellInfo,
  xPosition,
  onColumnAdded,
}: BeeTableThResizableProps<R>) {
  const thProps = useMemo(
    () => ({
      ...column.getHeaderProps(),
      style: {
        position: "relative" as const,
        ...(column.width ? {} : { flexGrow: 1 }),
      },
    }),
    [column]
  );

  const columnKey = useMemo(() => getColumnKey(column), [column, getColumnKey]);

  // FIXME: Tiago -> Specific logic
  const isFocusable = useMemo(
    () => /^(_\w{8}-(\w{4}-){3}\w{12}|parameters|functionDefinition)$/.test(columnKey),
    [columnKey]
  );

  // FIXME: Tiago -> Specific logic
  const [isAnnotationCellEditMode, setIsAnnotationCellEditMode] = useState(false);

  const cssClasses = useMemo(() => {
    const cssClasses = [columnKey, "data-header-cell"];
    if (!column.dataType) {
      cssClasses.push("no-clickable-cell");
    }
    const isColspan = (column.columns?.length ?? 0) > 0 || false;
    if (isColspan) {
      cssClasses.push("colspan-header");
    }
    if (column.placeholderOf?.cssClasses && column.placeholderOf?.groupType) {
      cssClasses.push("colspan-header");
      cssClasses.push(column.placeholderOf.cssClasses);
      cssClasses.push(column.placeholderOf.groupType);
    }
    cssClasses.push(column.groupType || "");
    cssClasses.push(column.cssClasses || "");
    cssClasses.push(isAnnotationCellEditMode ? "focused" : "");
    return cssClasses.join(" ");
  }, [column, columnKey, isAnnotationCellEditMode]);

  /**
   * Callback called when the annotation cell toggle edit/read mode.
   *
   * @param isReadMode true if is read mode, false otherwise
   */
  // FIXME: Tiago -> DecisionTable-specific logic
  const onAnnotationCellToggle = useCallback((isReadMode: boolean) => {
    setIsAnnotationCellEditMode(!isReadMode);
  }, []);

  const onClick = useMemo(() => {
    return onHeaderClick(columnKey);
  }, [columnKey, onHeaderClick]);

  const columnLabel = useMemo(() => {
    return getColumnLabel(column.groupType);
  }, [column.groupType, getColumnLabel]);

  const rowIndex = useMemo(() => {
    return column.depth === 0 ? -2 : -1;
  }, [column.depth]);

  const { resizingWidth, setResizingWidth } = useBeeTableColumnWidth(columnIndex, column.width);
  const { isActive, isEditing } = useBeeTableCell(rowIndex, columnIndex);

  return (
    <BeeTableTh<R>
      className={cssClasses}
      thProps={thProps}
      isFocusable={isFocusable}
      onClick={onClick}
      columnIndex={columnIndex}
      xPosition={xPosition}
      onColumnAdded={onColumnAdded}
      groupType={column.groupType}
      isLastLevelColumn={(column.columns?.length ?? 0) <= 0}
      column={column}
    >
      <div
        className="header-cell"
        data-ouia-component-type="expression-column-header"
        style={{ width: resizingWidth?.value }}
      >
        {column.dataType && editableHeader ? (
          <ExpressionDefinitionHeaderMenu
            isPopoverOpen={isActive || isEditing}
            position={PopoverPosition.bottom}
            title={columnLabel}
            selectedExpressionName={column.label}
            selectedDataType={column.dataType}
            onExpressionHeaderUpdated={onExpressionHeaderUpdated}
          >
            {renderHeaderCellInfo(column, columnIndex)}
          </ExpressionDefinitionHeaderMenu>
        ) : (
          renderHeaderCellInfo(column, columnIndex, onAnnotationCellToggle)
        )}
      </div>
      <Resizer
        minWidth={column.minWidth}
        width={column.width}
        setWidth={column.setWidth}
        resizingWidth={resizingWidth}
        setResizingWidth={setResizingWidth}
      />
    </BeeTableTh>
  );
}
