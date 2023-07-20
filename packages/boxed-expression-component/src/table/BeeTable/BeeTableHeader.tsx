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

import * as _ from "lodash";
import * as React from "react";
import { useCallback } from "react";
import * as ReactTable from "react-table";
import { DmnBuiltInDataType, BeeTableHeaderVisibility, ExpressionDefinition } from "../../api";
import { useBoxedExpressionEditor } from "../../expressions/BoxedExpressionEditor/BoxedExpressionEditorContext";
import { BeeTableTh } from "./BeeTableTh";
import { BeeTableThResizable } from "./BeeTableThResizable";
import { InlineEditableTextInput } from "../../expressions/ExpressionDefinitionHeaderMenu";
import { ResizerStopBehavior } from "../../resizing/ResizingWidthsContext";
import { getCanvasFont, getTextWidth } from "../../resizing/WidthsToFitData";
import { BeeTableThController } from "./BeeTableThController";
import { assertUnreachable } from "../../expressions/ExpressionDefinitionRoot/ExpressionDefinitionLogicTypeSelector";

export interface BeeTableColumnUpdate<R extends object> {
  dataType: DmnBuiltInDataType;
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
  /** Option to enable or disable header edits */
  isEditableHeader: boolean;
  /** */
  onColumnAdded?: (args: { beforeIndex: number; groupType: string | undefined }) => void;

  shouldRenderRowIndexColumn: boolean;

  shouldShowRowsInlineControls: boolean;

  resizerStopBehavior: ResizerStopBehavior;
  lastColumnMinWidth?: number;
  setEditing: React.Dispatch<React.SetStateAction<boolean>>;
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
  shouldRenderRowIndexColumn,
  shouldShowRowsInlineControls,
  resizerStopBehavior,
  lastColumnMinWidth,
  setEditing,
}: BeeTableHeaderProps<R>) {
  const { beeGwtService } = useBoxedExpressionEditor();

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
    ) => (args: Pick<ExpressionDefinition, "name" | "dataType">) => void
  >(
    (column, columnIndex) => {
      return ({ name = "", dataType = DmnBuiltInDataType.Undefined }) => {
        onColumnUpdates?.([
          {
            // Subtract one because of the rowIndex column.
            columnIndex: columnIndex - 1,
            dataType,
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
          columnIndex={0}
          rowIndex={rowIndex}
          thProps={column.getHeaderProps()}
          className={classNames}
          groupType={column.groupType}
          isLastLevelColumn={(column.columns?.length ?? 0) <= 0}
          shouldShowColumnsInlineControls={shouldShowRowsInlineControls}
        >
          <div className="header-cell" data-ouia-component-type="expression-column-header">
            {column.label}
          </div>
        </BeeTableTh>
      );
    },
    [getColumnKey, shouldShowRowsInlineControls]
  );

  const onHeaderClick = useCallback(
    (columnKey: string) => () => {
      beeGwtService?.selectObject(columnKey);
    },
    [beeGwtService]
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
              resizerStopBehavior={resizerStopBehavior}
              rowSpan={rowSpan}
              shouldRenderRowIndexColumn={shouldRenderRowIndexColumn}
              isEditableHeader={isEditableHeader}
              shouldShowColumnsInlineControls={shouldShowRowsInlineControls}
              getColumnKey={getColumnKey}
              getColumnLabel={getColumnLabel}
              onHeaderClick={onHeaderClick}
              reactTableInstance={reactTableInstance}
              column={column}
              columnIndex={columnIndex}
              rowIndex={rowIndex}
              onColumnAdded={onColumnAdded}
              onExpressionHeaderUpdated={({ name, dataType }) =>
                onExpressionHeaderUpdated(column, columnIndex)({ name, dataType })
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
                  const dataType = thRef.current!.querySelector(".expression-info-data-type")!;
                  return Math.ceil(
                    Math.max(
                      getTextWidth(name.textContent ?? "", getCanvasFont(name)),
                      getTextWidth(dataType.textContent ?? "", getCanvasFont(dataType))
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
                  ) : column.isInlineEditable ? (
                    <InlineEditableTextInput
                      setEditing={setEditing}
                      columnIndex={columnIndex}
                      rowIndex={rowIndex}
                      value={column.label}
                      onChange={(value) => {
                        onExpressionHeaderUpdated(column, columnIndex)({ name: value, dataType: column.dataType });
                      }}
                    />
                  ) : (
                    <p className="expression-info-name pf-u-text-truncate name">{column.label}</p>
                  )}
                  {column.dataType ? (
                    <p className="expression-info-data-type pf-u-text-truncate data-type">({column.dataType})</p>
                  ) : null}
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
      resizerStopBehavior,
      isEditableHeader,
      shouldShowRowsInlineControls,
      getColumnLabel,
      onHeaderClick,
      onColumnAdded,
      lastColumnMinWidth,
      setEditing,
      onExpressionHeaderUpdated,
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
  }, [getColumnKey, reactTableInstance, renderColumn, shouldRenderHeaderGroup, shouldRenderRowIndexColumn]);

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
