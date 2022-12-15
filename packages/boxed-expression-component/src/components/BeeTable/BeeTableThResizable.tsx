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

import * as React from "react";
import { useState, useCallback, useMemo } from "react";
import * as ReactTable from "react-table";
import * as PfReactTable from "@patternfly/react-table";
import { DEFAULT_MIN_WIDTH, Resizer } from "../Resizer";
import { BeeTableTh } from "./BeeTableTh";
import { ExpressionDefinitionHeaderMenu } from "../ExpressionDefinitionHeaderMenu";
import { ExpressionDefinition } from "../../api";

export interface BeeTableThResizableProps<R extends object> {
  column: ReactTable.ColumnInstance<R>;
  columnIndex: number;
  editColumnLabel?: string | { [groupType: string]: string };
  editableHeader: boolean;
  getColumnKey: (column: ReactTable.ColumnInstance<R>) => string;
  getColumnLabel: (groupType: string | undefined) => string | undefined;
  onCellKeyDown: () => (e: KeyboardEvent) => void;
  onExpressionHeaderUpdated: (args: Pick<ExpressionDefinition, "name" | "dataType">) => void;
  onHeaderClick: (columnKey: string) => () => void;
  rowIndex: number;
  reactTableInstance: ReactTable.TableInstance<R>;
  getThProps: (column: ReactTable.ColumnInstance<R>) => Partial<PfReactTable.ThProps>;
  xPosition: number;
  yPosition: number;
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
  onCellKeyDown,
  onExpressionHeaderUpdated,
  onHeaderClick,
  renderHeaderCellInfo,
  rowIndex,
  reactTableInstance,
  getThProps,
  xPosition,
  yPosition,
}: BeeTableThResizableProps<R>) {
  const headerProps = useMemo(
    () => ({
      ...column.getHeaderProps(),
      style: column.width ? {} : { flexGrow: 1 },
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
   * Get the rowspan value.
   *
   * @param cssClasses the classes of the cell
   * @returns the value, default is 1
   */
  const getRowSpan = useCallback(
    (cssClasses: string): number => {
      if (
        // FIXME: Tiago -> CSS class names should not be used for logic.
        // FIXME: Tiago -> DecisionTable-specific logic
        rowIndex === reactTableInstance.headerGroups.length - 1 &&
        (cssClasses.includes("decision-table--input") || cssClasses.includes("decision-table--annotation"))
      ) {
        return 2;
      }

      return 1;
    },
    [reactTableInstance, rowIndex]
  );

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

  const rowSpan = useMemo(() => {
    return getRowSpan(cssClasses);
  }, [cssClasses, getRowSpan]);

  const thProps = useMemo(() => {
    return getThProps(column);
  }, [column, getThProps]);

  const columnLabel = useMemo(() => {
    return getColumnLabel(column.groupType);
  }, [column.groupType, getColumnLabel]);

  return (
    <BeeTableTh
      className={cssClasses}
      headerProps={headerProps}
      isFocusable={isFocusable}
      key={columnKey}
      onClick={onClick}
      onKeyDown={onCellKeyDown}
      rowIndex={rowIndex}
      columnIndex={columnIndex}
      rowSpan={rowSpan}
      thProps={thProps}
      xPosition={xPosition}
      yPosition={yPosition}
    >
      <Resizer
        width={column.width}
        setWidth={column.setWidth}
        minWidth={column.minWidth}
        resizingWidth={column.resizingWidth}
        setResizingWidth={column.setResizingWidth}
      >
        <div className="header-cell" data-ouia-component-type="expression-column-header">
          {column.dataType && editableHeader ? (
            <ExpressionDefinitionHeaderMenu
              title={columnLabel}
              selectedExpressionName={column.label}
              selectedDataType={column.dataType}
              onExpressionHeaderUpdated={onExpressionHeaderUpdated}
              key={columnKey}
            >
              {renderHeaderCellInfo(column, columnIndex)}
            </ExpressionDefinitionHeaderMenu>
          ) : (
            renderHeaderCellInfo(column, columnIndex, onAnnotationCellToggle)
          )}
        </div>
      </Resizer>
    </BeeTableTh>
  );
}
