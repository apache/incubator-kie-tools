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

import _ from "lodash";
import * as React from "react";
import { useCallback } from "react";
import * as ReactTable from "react-table";
import { BeeTableHeaderVisibility, BoxedExpression, InsertRowColumnsDirection } from "../../api";
import { BeeTableTh } from "./BeeTableTh";
import { BeeTableThResizable } from "./BeeTableThResizable";
import { ResizerStopBehavior } from "../../resizing/ResizingWidthsContext";
import { getCanvasFont, getTextWidth } from "../../resizing/WidthsToFitData";
import { BeeTableThController } from "./BeeTableThController";
import { assertUnreachable } from "../../expressions/ExpressionDefinitionRoot/ExpressionDefinitionLogicTypeSelector";
import { InlineEditableTextInput } from "./InlineEditableTextInput";
import { DEFAULT_EXPRESSION_VARIABLE_NAME } from "../../expressionVariable/ExpressionVariableMenu";

export interface BeeTableColumnUpdate<R extends object> {
  typeRef: string | undefined;
  name: string;
  column: ReactTable.ColumnInstance<R>;
  columnIndex: number;
}

export interface BeeTableCellUpdate<R extends object> {
  value: string;
  column: ReactTable.ColumnInstance<R>;
  columnIndex: number;
  row: R;
  rowIndex: number;
}

export interface BeeTableHeaderProps<R extends object> {
  /** Table instance */
  reactTableInstance: ReactTable.TableInstance<R>;
  /** Optional label, that may depend on column, to be used for the popover that appears when clicking on column header */
  editColumnLabel?: string | { [groupType: string]: string };
  /** The way in which the header will be rendered */
  headerVisibility?: BeeTableHeaderVisibility;
  /** True, for skipping the creation in the DOM of the last defined header group */
  skipLastHeaderGroup: boolean;
  /** Custom function for getting column key prop, and avoid using the column index */
  getColumnKey: (column: ReactTable.ColumnInstance<R>) => string;
  /** Columns instance */
  tableColumns: ReactTable.Column<R>[];
  /** Function to be executed when columns are modified */
  onColumnUpdates?: (columnUpdates: BeeTableColumnUpdate<R>[]) => void;
  /** Function to be executed when a column's header is clicked */
  onHeaderClick?: (columnKey: string) => void;
  /** Function to be executed when a key up event occurs in a column's header */
  onHeaderKeyUp?: (columnKey: string) => void;
  /** Option to enable or disable header edits */
  isEditableHeader: boolean;
  /** */
  onColumnAdded?: (args: {
    beforeIndex: number;
    groupType: string | undefined;
    columnsCount: number;
    insertDirection: InsertRowColumnsDirection;
  }) => void;

  shouldRenderRowIndexColumn: boolean;

  shouldShowRowsInlineControls: boolean;

  resizerStopBehavior: ResizerStopBehavior;
  lastColumnMinWidth?: number;
  setActiveCellEditing: (isEditing: boolean) => void;

  isReadOnly: boolean;
}

export function BeeTableHeader<R extends object>({
  reactTableInstance,
  editColumnLabel,
  headerVisibility = BeeTableHeaderVisibility.AllLevels,
  skipLastHeaderGroup,
  getColumnKey,
  onColumnUpdates,
  isEditableHeader,
  onColumnAdded,
  onHeaderClick,
  onHeaderKeyUp,
  shouldRenderRowIndexColumn,
  shouldShowRowsInlineControls,
  resizerStopBehavior,
  lastColumnMinWidth,
  setActiveCellEditing,
  isReadOnly,
}: BeeTableHeaderProps<R>) {
  const getColumnLabel: (groupType: string) => string | undefined = useCallback(
    (groupType) => {
      if (_.isObject(editColumnLabel) && _.has(editColumnLabel, groupType)) {
        return editColumnLabel[groupType];
      }
      if (typeof editColumnLabel === "string") {
        return editColumnLabel;
      }
    },
    [editColumnLabel]
  );

  const onExpressionHeaderUpdated = useCallback<
    (
      column: ReactTable.ColumnInstance<R>,
      columnIndex: number
    ) => (args: Pick<BoxedExpression, "@_label" | "@_typeRef">) => void
  >(
    (column, columnIndex) => {
      return ({ "@_label": name = DEFAULT_EXPRESSION_VARIABLE_NAME, "@_typeRef": typeRef = undefined }) => {
        onColumnUpdates?.([
          {
            // Subtract one because of the rowIndex column.
            columnIndex: columnIndex - 1,
            typeRef,
            name,
            column,
          },
        ]);
      };
    },
    [onColumnUpdates]
  );

  const renderRowIndexColumn = useCallback<
    (column: ReactTable.ColumnInstance<R>, rowIndex: number, rowSpan: number) => JSX.Element
  >(
    (column, rowIndex, rowSpan) => {
      const columnKey = getColumnKey(column);
      const classNames = `${columnKey} fixed-column no-clickable-cell counter-header-cell`;

      return (
        <BeeTableTh
          rowSpan={rowSpan}
          key={columnKey}
          column={column}
          columnKey={columnKey}
          columnIndex={0}
          rowIndex={rowIndex}
          thProps={column.getHeaderProps()}
          className={classNames}
          groupType={column.groupType}
          isLastLevelColumn={(column.columns?.length ?? 0) <= 0}
          shouldShowColumnsInlineControls={!isReadOnly && shouldShowRowsInlineControls}
          isReadOnly={isReadOnly}
        >
          <div className="header-cell" data-ouia-component-type="expression-column-header">
            {column.label}
          </div>
        </BeeTableTh>
      );
    },
    [getColumnKey, isReadOnly, shouldShowRowsInlineControls]
  );

  const renderColumn = useCallback<
    (
      rowIndex: number,
      column: ReactTable.ColumnInstance<R>,
      columnIndex: number,
      visitedColumns: Set<ReactTable.ColumnInstance<R>>,
      rowSpan: number
    ) => JSX.Element
  >(
    (rowIndex, column, columnIndex, visitedColumns, rowSpan) => {
      const thRef = React.createRef<HTMLTableCellElement>();

      const ret = column.isRowIndexColumn ? (
        <React.Fragment key={"row-index-column"}>
          {shouldRenderRowIndexColumn && renderRowIndexColumn(column, rowIndex, rowSpan)}
        </React.Fragment>
      ) : (
        <React.Fragment key={getColumnKey(column)}>
          {!visitedColumns.has(column) && (
            <BeeTableThResizable
              forwardRef={thRef}
              onHeaderClick={onHeaderClick}
              onHeaderKeyUp={onHeaderKeyUp}
              resizerStopBehavior={resizerStopBehavior}
              rowSpan={rowSpan}
              shouldRenderRowIndexColumn={shouldRenderRowIndexColumn}
              isEditableHeader={isEditableHeader}
              isReadOnly={isReadOnly}
              shouldShowColumnsInlineControls={shouldShowRowsInlineControls}
              getColumnKey={getColumnKey}
              getColumnLabel={getColumnLabel}
              reactTableInstance={reactTableInstance}
              column={column}
              columnIndex={columnIndex}
              rowIndex={rowIndex}
              onColumnAdded={onColumnAdded}
              onExpressionHeaderUpdated={({ name, typeRef }) =>
                onExpressionHeaderUpdated(column, columnIndex)({ "@_label": name, "@_typeRef": typeRef })
              }
              lastColumnMinWidth={
                columnIndex === reactTableInstance.allColumns.length - 1 ? lastColumnMinWidth : undefined
              }
              onGetWidthToFitData={() => {
                if (column.isInlineEditable) {
                  const inlineEditablePreview = thRef.current!.querySelector(".inline-editable-preview")!;
                  return Math.ceil(
                    getTextWidth(inlineEditablePreview.textContent ?? "", getCanvasFont(inlineEditablePreview))
                  );
                } else {
                  const name = thRef.current!.querySelector(".expression-info-name")!;
                  const typeRef = thRef.current!.querySelector(".expression-info-data-type")!;
                  return Math.ceil(
                    Math.max(
                      getTextWidth(name.textContent ?? "", getCanvasFont(name)),
                      getTextWidth(typeRef.textContent ?? "", getCanvasFont(typeRef))
                    )
                  );
                }
              }}
              headerCellInfo={
                <div
                  className="expression-info header-cell-info"
                  data-ouia-component-type="expression-column-header-cell-info"
                >
                  {column.headerCellElement ? (
                    column.headerCellElement
                  ) : column.isInlineEditable && !isReadOnly ? (
                    <InlineEditableTextInput
                      setActiveCellEditing={setActiveCellEditing}
                      columnIndex={columnIndex}
                      rowIndex={rowIndex}
                      value={column.label}
                      onChange={(value) => {
                        onExpressionHeaderUpdated(
                          column,
                          columnIndex
                        )({ "@_label": value, "@_typeRef": column.dataType });
                      }}
                      isReadOnly={isReadOnly}
                    />
                  ) : (
                    <p
                      data-testid={"kie-tools--bee--expression-info-name"}
                      className="expression-info-name pf-v5-u-text-truncate name"
                    >
                      {column.label}
                    </p>
                  )}
                  {column.dataType ? (
                    <p
                      data-testid={"kie-tools--bee--expression-info-data-type"}
                      className="expression-info-data-type pf-v5-u-text-truncate data-type"
                    >
                      ({column.dataType})
                    </p>
                  ) : null}
                  {column.headerCellElementExtension !== undefined && (
                    <div className="header-cell-element-extension">{column.headerCellElementExtension}</div>
                  )}
                </div>
              }
            />
          )}
        </React.Fragment>
      );

      visitedColumns.add(column);
      return ret;
    },
    [
      shouldRenderRowIndexColumn,
      renderRowIndexColumn,
      getColumnKey,
      reactTableInstance,
      onHeaderClick,
      onHeaderKeyUp,
      resizerStopBehavior,
      isEditableHeader,
      shouldShowRowsInlineControls,
      getColumnLabel,
      onColumnAdded,
      lastColumnMinWidth,
      setActiveCellEditing,
      onExpressionHeaderUpdated,
      isReadOnly,
    ]
  );

  const shouldRenderHeaderGroup = useCallback(
    (rowIndex: number) => {
      if (rowIndex === -1 && skipLastHeaderGroup) {
        return false;
      }

      switch (headerVisibility) {
        case BeeTableHeaderVisibility.None:
          return false;
        case BeeTableHeaderVisibility.AllLevels:
          return true;
        case BeeTableHeaderVisibility.SecondToLastLevel:
          return rowIndex === -2;
        case BeeTableHeaderVisibility.LastLevel:
          return rowIndex === -1;
        default:
          assertUnreachable(headerVisibility);
      }
    },
    [headerVisibility, skipLastHeaderGroup]
  );

  const renderHeaderGroups = useCallback(() => {
    const visitedColumns = new Set<ReactTable.ColumnInstance<R>>();

    return reactTableInstance.headerGroups.map((headerGroup, index) => {
      // rowIndex === -1 --> Last headerGroup
      // rowIndex === -2 --> Second to last headerGroup
      // ... and so on
      const rowIndex = -(reactTableInstance.headerGroups.length - index);
      let lastParentalHeaderCellIndex = 0;

      const { key, ...props } = { ...headerGroup.getHeaderGroupProps(), style: {} };
      if (shouldRenderHeaderGroup(rowIndex)) {
        return (
          <tr key={key} {...props}>
            {headerGroup.headers.map((column) => {
              const { placeholder, depth } = getDeepestPlaceholder(column);
              const columnIndex =
                getColumnIndexOfHeader(reactTableInstance, placeholder) >= 0
                  ? getColumnIndexOfHeader(reactTableInstance, placeholder)
                  : lastParentalHeaderCellIndex++;

              if (placeholder.isRowIndexColumn) {
                if (headerVisibility === BeeTableHeaderVisibility.AllLevels) {
                  if (rowIndex === -reactTableInstance.headerGroups.length) {
                    return renderColumn(
                      rowIndex + depth - 1,
                      placeholder,
                      columnIndex,
                      visitedColumns,
                      reactTableInstance.headerGroups.length
                    );
                  }
                } else {
                  return renderColumn(rowIndex + depth - 1, placeholder, columnIndex, visitedColumns, depth);
                }
              } else {
                return renderColumn(rowIndex + depth - 1, placeholder, columnIndex, visitedColumns, depth);
              }
            })}
          </tr>
        );
      } else {
        return (
          <React.Fragment key={key}>
            {headerGroup.headers.map((column) => {
              const { placeholder } = getDeepestPlaceholder(column);
              const columnIndex =
                getColumnIndexOfHeader(reactTableInstance, placeholder) >= 0
                  ? getColumnIndexOfHeader(reactTableInstance, placeholder)
                  : lastParentalHeaderCellIndex++;

              return (
                <BeeTableThController
                  key={getColumnKey(column)}
                  columnIndex={columnIndex}
                  column={column}
                  reactTableInstance={reactTableInstance}
                  shouldRenderRowIndexColumn={shouldRenderRowIndexColumn}
                />
              );
            })}
          </React.Fragment>
        );
      }
    });
  }, [
    getColumnKey,
    headerVisibility,
    reactTableInstance,
    renderColumn,
    shouldRenderHeaderGroup,
    shouldRenderRowIndexColumn,
  ]);

  return <>{<thead>{renderHeaderGroups()}</thead>}</>;
}

function getDeepestPlaceholder<R extends object>(column: ReactTable.ColumnInstance<R>) {
  let currentDepth = 1;

  while (column.placeholderOf) {
    column = column.placeholderOf;
    currentDepth++;
  }

  return {
    placeholder: column,
    depth: currentDepth,
  };
}

function getColumnIndexOfHeader<R extends object>(
  reactTableInstance: ReactTable.TableInstance<R>,
  column: ReactTable.ColumnInstance<R>
) {
  return reactTableInstance.allColumns.indexOf(column);
}
