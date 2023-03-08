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
import { useCallback, useMemo } from "react";
import * as ReactTable from "react-table";
import { DmnBuiltInDataType, BeeTableHeaderVisibility, ExpressionDefinition } from "../../api";
import { useBoxedExpressionEditor } from "../../expressions/BoxedExpressionEditor/BoxedExpressionEditorContext";
import { BeeTableTh } from "./BeeTableTh";
import { BeeTableThResizable } from "./BeeTableThResizable";
import { InlineEditableTextInput } from "../../expressions/ExpressionDefinitionHeaderMenu";
import { ResizerStopBehavior } from "../../resizing/ResizingWidthsContext";
import { getCanvasFont, getTextWidth } from "../../resizing/WidthsToFitData";
import { BeeTableThController } from "./BeeTableThController";

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

  const renderRowIndexColumn = useCallback<(column: ReactTable.ColumnInstance<R>, rowIndex: number) => JSX.Element>(
    (column, rowIndex) => {
      const columnKey = getColumnKey(column);
      const classNames = `${columnKey} fixed-column no-clickable-cell counter-header-cell`;

      return (
        <BeeTableTh
          rowSpan={1}
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
      done: Set<ReactTable.ColumnInstance<R>>
    ) => JSX.Element
  >(
    (rowIndex, _column, columnIndex, done) => {
      const column = _column;
      const rowSpan = 1;

      const thRef = React.createRef<HTMLTableCellElement>();

      const ret = column.isRowIndexColumn ? (
        <React.Fragment key={"row-index-column"}>
          {shouldRenderRowIndexColumn && renderRowIndexColumn(column, rowIndex)}
        </React.Fragment>
      ) : (
        <React.Fragment key={getColumnKey(column)}>
          {!done.has(column) && (
            <BeeTableThResizable
              firstColumnIndexOfGroup={reactTableInstance.allColumns.indexOf(column.columns?.[0] ?? column)}
              forwardRef={thRef}
              resizerStopBehavior={resizerStopBehavior}
              rowSpan={rowSpan}
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
                      columnIndex={columnIndex}
                      rowIndex={rowIndex}
                      value={column.label}
                      onChange={(value) => {
                        onExpressionHeaderUpdated(column, columnIndex)({ name: value, dataType: column.dataType });
                      }}
                    />
                  ) : (
                    <p className="expression-info-name pf-u-text-truncate label">{column.label}</p>
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

      done.add(column);
      return ret;
    },
    [
      shouldRenderRowIndexColumn,
      renderRowIndexColumn,
      getColumnKey,
      resizerStopBehavior,
      isEditableHeader,
      shouldShowRowsInlineControls,
      getColumnLabel,
      onHeaderClick,
      reactTableInstance,
      onColumnAdded,
      lastColumnMinWidth,
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
      }
    },
    [headerVisibility, skipLastHeaderGroup]
  );

  const renderHeaderGroups = useCallback(() => {
    const done = new Set<ReactTable.ColumnInstance<R>>();

    return reactTableInstance.headerGroups.map((headerGroup, index) => {
      // rowIndex === -1 --> Last headerGroup
      // rowIndex === -2 --> Second to last headerGroup
      // ... and so on
      const rowIndex = -(reactTableInstance.headerGroups.length - 1 - index + 1);

      const { key, ...props } = { ...headerGroup.getHeaderGroupProps(), style: {} };
      if (shouldRenderHeaderGroup(rowIndex)) {
        return (
          <tr key={key} {...props}>
            {headerGroup.headers.map((column, columnIndex) => renderColumn(rowIndex, column, columnIndex, done))}
          </tr>
        );
      } else {
        return (
          <React.Fragment key={key}>
            {headerGroup.headers.map((column, columnIndex) => (
              <BeeTableThController
                key={getColumnKey(column)}
                columnIndex={columnIndex}
                column={column}
                reactTableInstance={reactTableInstance}
              />
            ))}
          </React.Fragment>
        );
      }
    });
  }, [getColumnKey, reactTableInstance, renderColumn, shouldRenderHeaderGroup]);

  return <>{<thead>{renderHeaderGroups()}</thead>}</>;
}
