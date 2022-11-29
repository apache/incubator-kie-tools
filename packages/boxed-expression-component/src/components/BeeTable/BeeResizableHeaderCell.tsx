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
import { useState, useCallback } from "react";
import * as ReactTable from "react-table";
import { DEFAULT_MIN_WIDTH, Resizer } from "../Resizer";
import { BeeThCell } from "./BeeThCell";
import { ExpressionDefinitionHeaderMenu } from "../ExpressionDefinitionHeaderMenu";

export interface BeeResizableHeaderCellProps {
  column: any;
  columnIndex: number;
  editColumnLabel?: string | { [groupType: string]: string };
  editableHeader: boolean;
  getColumnKey: (column: ReactTable.Column) => string;
  getColumnLabel: (groupType: string) => string | undefined;
  onCellKeyDown: () => (e: KeyboardEvent) => void;
  onColumnNameOrDataTypeUpdate: (column: ReactTable.ColumnInstance, columnIndex: number) => any;
  onHeaderClick: (columnKey: string) => () => void;
  onHorizontalResizeStop: (column: ReactTable.Column, columnWidth: number) => void;
  rowIndex: number;
  tableInstance: ReactTable.TableInstance;
  thProps: (column: ReactTable.ColumnInstance) => any;
  xPosition: number;
  yPosition: number;
  renderHeaderCellInfo: (
    column: ReactTable.Column,
    columnIndex: number,
    onAnnotationCellToggle?: (isReadMode: boolean) => void
  ) => React.ReactElement;
}

export const BeeResizableHeaderCell = ({
  column,
  columnIndex,
  editableHeader,
  getColumnKey,
  getColumnLabel,
  onCellKeyDown,
  onColumnNameOrDataTypeUpdate,
  onHeaderClick,
  onHorizontalResizeStop,
  renderHeaderCellInfo,
  rowIndex,
  tableInstance,
  thProps,
  xPosition,
  yPosition,
}: BeeResizableHeaderCellProps) => {
  const headerProps = {
    ...column.getHeaderProps(),
    style: {},
  };
  const width = column.width || DEFAULT_MIN_WIDTH;
  const isColspan = (column.columns?.length ?? 0) > 0 || false;
  const columnKey = getColumnKey(column);
  const isFocusable = /^(_\w{8}-(\w{4}-){3}\w{12}|parameters|functionDefinition)$/.test(columnKey);
  const [isAnnotationCellEditMode, setIsAnnotationCellEditMode] = useState(false);

  const getCssClass = useCallback(() => {
    const cssClasses = [columnKey, "data-header-cell"];
    if (!column.dataType) {
      cssClasses.push("no-clickable-cell");
    }
    if (isColspan) {
      cssClasses.push("colspan-header");
    }
    if (column.placeholderOf) {
      cssClasses.push("colspan-header");
      cssClasses.push(column.placeholderOf.cssClasses);
      cssClasses.push(column.placeholderOf.groupType);
    }
    cssClasses.push(column.groupType || "");
    cssClasses.push(column.cssClasses || "");
    cssClasses.push(isAnnotationCellEditMode ? "focused" : "");
    return cssClasses.join(" ");
  }, [column, columnKey, isAnnotationCellEditMode, isColspan]);

  /**
   * Get the rowspan value.
   *
   * @param cssClasses the classes of the cell
   * @returns the value, default is 1
   */
  const getRowSpan = useCallback(
    (cssClasses: string): number => {
      if (
        rowIndex === tableInstance.headerGroups.length - 1 &&
        (cssClasses.includes("decision-table--input") || cssClasses.includes("decision-table--annotation"))
      ) {
        return 2;
      }

      return 1;
    },
    [tableInstance, rowIndex]
  );

  /**
   * Callback called when the annotation cell toggle edit/read mode.
   *
   * @param isReadMode true if is read mode, false otherwise
   */
  const onAnnotationCellToggle = useCallback((isReadMode: boolean) => {
    setIsAnnotationCellEditMode(!isReadMode);
  }, []);

  const cssClasses = getCssClass();

  return (
    <BeeThCell
      className={cssClasses}
      headerProps={headerProps}
      isFocusable={isFocusable}
      key={columnKey}
      onClick={onHeaderClick(columnKey)}
      onKeyDown={onCellKeyDown}
      rowIndex={rowIndex}
      cellIndex={columnIndex}
      rowSpan={getRowSpan(cssClasses)}
      thProps={thProps(column)}
      xPosition={xPosition}
      yPosition={yPosition}
    >
      <Resizer width={width} onHorizontalResizeStop={(columnWidth) => onHorizontalResizeStop(column, columnWidth)}>
        <div className="header-cell" data-ouia-component-type="expression-column-header">
          {column.dataType && editableHeader ? (
            <ExpressionDefinitionHeaderMenu
              title={getColumnLabel(column.groupType)}
              selectedExpressionName={column.label}
              selectedDataType={column.dataType}
              onExpressionUpdate={(expression) => onColumnNameOrDataTypeUpdate(column, columnIndex)(expression)}
              key={columnKey}
            >
              {renderHeaderCellInfo(column, columnIndex)}
            </ExpressionDefinitionHeaderMenu>
          ) : (
            renderHeaderCellInfo(column, columnIndex, onAnnotationCellToggle)
          )}
        </div>
      </Resizer>
    </BeeThCell>
  );
};
