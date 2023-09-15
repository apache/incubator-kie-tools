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

import * as _ from "lodash";
import * as React from "react";
import { useCallback, useMemo, useRef } from "react";
import * as ReactTable from "react-table";
import { BeeTableHeaderVisibility, BeeTableProps } from "../../api";
import { useBoxedExpressionEditor } from "../../expressions/BoxedExpressionEditor/BoxedExpressionEditorContext";
import { NavigationKeysUtils } from "../../keysUtils";
import { ResizingWidth } from "../../resizing/ResizingWidthsContext";
import { BEE_TABLE_ROW_INDEX_COLUMN_WIDTH } from "../../resizing/WidthConstants";
import "./BeeTable.css";
import { BeeTableBody } from "./BeeTableBody";
import {
  BeeTableResizableColumnsContextProvider,
  BeeTableResizableColumnsDispatchContextType,
} from "../../resizing/BeeTableResizableColumnsContext";
import { BeeTableContextMenuHandler } from "./BeeTableContextMenuHandler";
import { BeeTableDefaultCell } from "./BeeTableDefaultCell";
import { BeeTableHeader } from "./BeeTableHeader";
import {
  BeeTableSelectionContextProvider,
  SelectionPart,
  SELECTION_MIN_ACTIVE_DEPTH,
  useBeeTableSelectionDispatch,
} from "../../selection/BeeTableSelectionContext";
import { BeeTableCellWidthsToFitDataContextProvider } from "../../resizing/BeeTableCellWidthToFitDataContext";
import { getOperatingSystem, OperatingSystem } from "@kie-tools-core/operating-system";

const ROW_INDEX_COLUMN_ACCESOR = "#";
const ROW_INDEX_SUB_COLUMN_ACCESSOR = "0";

export function getColumnsAtLastLevel<R extends ReactTable.Column<any> | ReactTable.ColumnInstance<any>>(
  columns: R[],
  depth: number = 0
): R[] {
  return _.flatMap(columns, (column) => {
    if (!column.columns) {
      return column;
    }

    return depth > 0
      ? getColumnsAtLastLevel(column.columns as R[], depth - 1) // recurse
      : (column.columns as R[]);
  });
}

export function areEqualColumns<R extends object>(
  column: ReactTable.Column<R> | ReactTable.ColumnInstance<R> | undefined
): (other: ReactTable.Column<R> | ReactTable.ColumnInstance<R>) => boolean {
  const columnId = column?.originalId || column?.id || column?.accessor;
  return (other: ReactTable.Column<R>) => {
    return other.id === columnId || other.accessor === columnId;
  };
}

export function BeeTableInternal<R extends object>({
  tableId,
  additionalRow,
  editColumnLabel,
  isEditableHeader = true,
  onCellUpdates,
  onColumnUpdates,
  onRowAdded,
  onRowDuplicated,
  onRowReset,
  onRowDeleted,
  onColumnAdded,
  onColumnDeleted,
  controllerCell = ROW_INDEX_COLUMN_ACCESOR,
  cellComponentByColumnAccessor,
  rows,
  columns,
  operationConfig,
  allowedOperations,
  headerVisibility = BeeTableHeaderVisibility.AllLevels,
  headerLevelCountForAppendingRowIndexColumn = 0,
  skipLastHeaderGroup = false,
  getRowKey,
  getColumnKey,
  isReadOnly = false,
  enableKeyboardNavigation = true,
  shouldRenderRowIndexColumn,
  shouldShowRowsInlineControls,
  shouldShowColumnsInlineControls,
  resizerStopBehavior,
  lastColumnMinWidth,
  rowWrapper,
  variables,
}: BeeTableProps<R>) {
  const { resetSelectionAt, erase, copy, cut, paste, adaptSelection, mutateSelection, setCurrentDepth } =
    useBeeTableSelectionDispatch();
  const tableComposableRef = useRef<HTMLTableElement>(null);
  const { currentlyOpenContextMenu } = useBoxedExpressionEditor();

  const tableRef = React.useRef<HTMLDivElement>(null);

  const hasAdditionalRow = useMemo(() => {
    return (additionalRow?.length ?? 0) > 0;
  }, [additionalRow?.length]);

  const addRowIndexColumnsRecursively: <R extends object>(
    column: ReactTable.Column<R>,
    headerLevelCount: number
  ) => void = useCallback(
    (column, headerLevelCount) => {
      if (headerLevelCount > 0) {
        _.assign(column, {
          columns: [
            {
              label:
                headerVisibility === BeeTableHeaderVisibility.AllLevels
                  ? ROW_INDEX_SUB_COLUMN_ACCESSOR
                  : (controllerCell as any), // FIXME: https://github.com/kiegroup/kie-issues/issues/169
              accessor: ROW_INDEX_SUB_COLUMN_ACCESSOR as any,
              minWidth: BEE_TABLE_ROW_INDEX_COLUMN_WIDTH,
              width: BEE_TABLE_ROW_INDEX_COLUMN_WIDTH,
              isRowIndexColumn: true,
              dataType: undefined as any,
            } as ReactTable.Column<R>,
          ],
        });

        if (column.columns?.length) {
          addRowIndexColumnsRecursively(column.columns[0], headerLevelCount - 1);
        }
      }
    },
    [controllerCell, headerVisibility]
  );

  const addRowIndexColumns = useCallback<
    (controllerCell: string | JSX.Element, columns: ReactTable.Column<R>[]) => ReactTable.Column<R>[]
  >(
    (currentControllerCell, columns) => {
      const rowIndexColumn: ReactTable.Column<R> = {
        label: currentControllerCell as any, //FIXME: https://github.com/kiegroup/kie-issues/issues/169
        accessor: ROW_INDEX_COLUMN_ACCESOR as any,
        width: BEE_TABLE_ROW_INDEX_COLUMN_WIDTH,
        minWidth: BEE_TABLE_ROW_INDEX_COLUMN_WIDTH,
        isRowIndexColumn: true,
        dataType: undefined as any, // FIXME: https://github.com/kiegroup/kie-issues/issues/169
      };

      addRowIndexColumnsRecursively(rowIndexColumn, headerLevelCountForAppendingRowIndexColumn);

      return [rowIndexColumn, ...columns];
    },
    [addRowIndexColumnsRecursively, headerLevelCountForAppendingRowIndexColumn]
  );

  const columnsWithAddedIndexColumns = useMemo(
    () => addRowIndexColumns(controllerCell, columns),
    [addRowIndexColumns, columns, controllerCell]
  );

  const rowCount = useCallback(
    (normalRowsCount: number) => {
      return normalRowsCount + (hasAdditionalRow ? 1 : 0);
    },
    [hasAdditionalRow]
  );

  const _setEditing = useCallback(
    (rowCount: number, columnCount: (rowIndex: number) => number) => (isEditing: boolean) => {
      mutateSelection({
        part: SelectionPart.ActiveCell,
        columnCount,
        rowCount,
        deltaColumns: 0,
        deltaRows: 0,
        isEditingActiveCell: isEditing,
        keepInsideSelection: true,
      });
    },
    [mutateSelection]
  );

  const _navigateVertically = useCallback(
    (rowCount: number, columnCount: (rowIndex: number) => number) => (args: { isShiftPressed: boolean }) => {
      mutateSelection({
        part: SelectionPart.ActiveCell,
        columnCount,
        rowCount,
        deltaColumns: 0,
        deltaRows: args.isShiftPressed ? -1 : 1,
        isEditingActiveCell: false,
        keepInsideSelection: true,
      });
    },
    [mutateSelection]
  );

  const _navigateHorizontally = useCallback(
    (rowCount: number, columnCount: (rowIndex: number) => number) => (args: { isShiftPressed: boolean }) => {
      mutateSelection({
        part: SelectionPart.ActiveCell,
        columnCount,
        rowCount,
        deltaColumns: args.isShiftPressed ? -1 : 1,
        deltaRows: 0,
        isEditingActiveCell: false,
        keepInsideSelection: true,
      });
    },
    [mutateSelection]
  );

  const defaultColumn = useMemo(
    () => ({
      Cell: (cellProps: ReactTable.CellProps<R>) => {
        const columnIndex = cellProps.allColumns.findIndex((c) => c.id === cellProps.column.id);
        const CellComponentForColumn =
          cellComponentByColumnAccessor?.[cellProps.column.id] ?? cellComponentByColumnAccessor?.["___default"];
        if (CellComponentForColumn) {
          return (
            <CellComponentForColumn
              data={cellProps.data}
              rowIndex={cellProps.row.index}
              columnIndex={columnIndex}
              columnId={cellProps.column.id}
            />
          );
        } else {
          return (
            <BeeTableDefaultCell
              columnIndex={columnIndex}
              cellProps={cellProps}
              onCellUpdates={onCellUpdates}
              isReadOnly={isReadOnly}
              setEditing={_setEditing(cellProps.rows.length, () => cellProps.allColumns.length)}
              navigateHorizontally={_navigateHorizontally(cellProps.rows.length, () => cellProps.allColumns.length)}
              navigateVertically={_navigateVertically(cellProps.rows.length, () => cellProps.allColumns.length)}
              variables={variables}
            />
          );
        }
      },
    }),
    [
      cellComponentByColumnAccessor,
      onCellUpdates,
      isReadOnly,
      _setEditing,
      _navigateHorizontally,
      _navigateVertically,
      variables,
    ]
  );

  const reactTableInstance = ReactTable.useTable<R>(
    {
      columns: columnsWithAddedIndexColumns,
      data: rows,
      defaultColumn,
    },
    ReactTable.useBlockLayout
  );

  const onGetColumnKey = useCallback<(column: ReactTable.ColumnInstance<R>) => string>(
    (column) => {
      return getColumnKey ? getColumnKey(column) : column.originalId || column.id;
    },
    [getColumnKey]
  );

  const onGetRowKey = useCallback(
    (row: ReactTable.Row<R>) => {
      if (getRowKey) {
        return getRowKey(row);
      } else {
        if (row.original) {
          // FIXME: https://github.com/kiegroup/kie-issues/issues/169
          return (row.original as any).id;
        }
        return row.id;
      }
    },
    [getRowKey]
  );

  // For header area (rowIndex < 0), we need to 'getColumnCount' counts just real columns, not placeholders
  // +-----------+----------+-----------+
  // |     A     +          |     C     |
  // +-----+-----+     B    +-----------+
  // |  a  |  a  |          |  c  |  c  |
  // +-----------+----------+-----+-----+
  // | data cells
  // | ....
  //
  // in this example just 'A' and 'C' have rowIndex set to -2
  // So we need 'getColumnCount' returns number 2 for 'rowIndex' -2
  //
  // This is important for boundaries calucalted in 'BeeTableSelectionContext'
  // We do not want to be able navigate horizontally between header cells with different 'rowIndex'
  const getColumnCount = useCallback(
    (rowIndex: number) => {
      if (rowIndex >= 0) {
        return reactTableInstance.allColumns.length;
      } else {
        return _.nth(reactTableInstance.headerGroups, rowIndex)!.headers.reduce(
          (acc, column) => acc + (column.placeholderOf ? 0 : 1),
          0
        );
      }
    },
    [reactTableInstance.allColumns.length, reactTableInstance.headerGroups]
  );

  const onKeyDown = useCallback(
    (e: React.KeyboardEvent) => {
      if (!enableKeyboardNavigation) {
        return;
      }

      if (currentlyOpenContextMenu) {
        return;
      }

      // ENTER
      if (NavigationKeysUtils.isEnter(e.key) && !e.metaKey && !e.altKey && !e.ctrlKey) {
        e.stopPropagation();
        e.preventDefault();
        setCurrentDepth((prev) => {
          const newActiveDepth = Math.min(prev.max, (prev.active ?? SELECTION_MIN_ACTIVE_DEPTH) + 1);
          if ((prev.active ?? SELECTION_MIN_ACTIVE_DEPTH) < prev.max) {
            return {
              max: prev.max,
              active: newActiveDepth,
            };
          }

          mutateSelection({
            part: SelectionPart.ActiveCell,
            columnCount: getColumnCount,
            rowCount: rowCount(reactTableInstance.rows.length),
            deltaColumns: 0,
            deltaRows: 0,
            isEditingActiveCell: true,
            keepInsideSelection: true,
          });
          return prev;
        });
      }

      // TAB
      if (NavigationKeysUtils.isTab(e.key)) {
        e.stopPropagation();
        e.preventDefault();
        if (e.shiftKey) {
          mutateSelection({
            part: SelectionPart.ActiveCell,
            columnCount: getColumnCount,
            rowCount: rowCount(reactTableInstance.rows.length),
            deltaColumns: -1,
            deltaRows: 0,
            isEditingActiveCell: false,
            keepInsideSelection: true,
          });
        } else {
          mutateSelection({
            part: SelectionPart.ActiveCell,
            columnCount: getColumnCount,
            rowCount: rowCount(reactTableInstance.rows.length),
            deltaColumns: 1,
            deltaRows: 0,
            isEditingActiveCell: false,
            keepInsideSelection: true,
          });
        }
      }

      // ARROWS

      const selectionPart = e.shiftKey ? SelectionPart.SelectionEnd : SelectionPart.ActiveCell;

      if (NavigationKeysUtils.isArrowLeft(e.key)) {
        e.stopPropagation();
        e.preventDefault();
        mutateSelection({
          part: selectionPart,
          columnCount: getColumnCount,
          rowCount: rowCount(reactTableInstance.rows.length),
          deltaColumns: -1,
          deltaRows: 0,
          isEditingActiveCell: false,
          keepInsideSelection: false,
        });
      }
      if (NavigationKeysUtils.isArrowRight(e.key)) {
        e.stopPropagation();
        e.preventDefault();
        mutateSelection({
          part: selectionPart,
          columnCount: getColumnCount,
          rowCount: rowCount(reactTableInstance.rows.length),
          deltaColumns: 1,
          deltaRows: 0,
          isEditingActiveCell: false,
          keepInsideSelection: false,
        });
      }
      if (NavigationKeysUtils.isArrowUp(e.key)) {
        e.stopPropagation();
        e.preventDefault();
        mutateSelection({
          part: selectionPart,
          columnCount: getColumnCount,
          rowCount: rowCount(reactTableInstance.rows.length),
          deltaColumns: 0,
          deltaRows: -1,
          isEditingActiveCell: false,
          keepInsideSelection: false,
        });
      }
      if (NavigationKeysUtils.isArrowDown(e.key)) {
        e.stopPropagation();
        e.preventDefault();
        mutateSelection({
          part: selectionPart,
          columnCount: getColumnCount,
          rowCount: rowCount(reactTableInstance.rows.length),
          deltaColumns: 0,
          deltaRows: 1,
          isEditingActiveCell: false,
          keepInsideSelection: false,
        });
      }

      // DELETE

      if (NavigationKeysUtils.isDelete(e.key) || NavigationKeysUtils.isBackspace(e.key)) {
        e.stopPropagation();
        e.preventDefault();
        erase();
      }

      // ESC
      if (NavigationKeysUtils.isEsc(e.key)) {
        e.stopPropagation();
        e.preventDefault();
        resetSelectionAt(undefined);
      }

      const complementaryKey =
        (getOperatingSystem() === OperatingSystem.MACOS && e.metaKey) ||
        (getOperatingSystem() !== OperatingSystem.MACOS && e.ctrlKey);
      if (!e.shiftKey && complementaryKey && e.key.toLowerCase() === "c") {
        e.stopPropagation();
        e.preventDefault();
        copy();
      }
      if (!e.shiftKey && complementaryKey && e.key.toLowerCase() === "x") {
        e.stopPropagation();
        e.preventDefault();
        cut();
      }
      if (!e.shiftKey && complementaryKey && e.key.toLowerCase() === "v") {
        e.stopPropagation();
        e.preventDefault();
        paste();
      }

      // SELECT ALL
      if (!e.shiftKey && complementaryKey && e.key.toLowerCase() === "a") {
        e.stopPropagation();
        e.preventDefault();

        mutateSelection({
          part: SelectionPart.SelectionStart,
          columnCount: getColumnCount,
          rowCount: rowCount(reactTableInstance.rows.length),
          deltaColumns: -(reactTableInstance.allColumns.length - 1),
          deltaRows: -(reactTableInstance.rows.length - 1),
          isEditingActiveCell: false,
          keepInsideSelection: false,
        });
        mutateSelection({
          part: SelectionPart.SelectionEnd,
          columnCount: getColumnCount,
          rowCount: rowCount(reactTableInstance.rows.length),
          deltaColumns: +(reactTableInstance.allColumns.length - 1),
          deltaRows: +(reactTableInstance.rows.length - 1),
          isEditingActiveCell: false,
          keepInsideSelection: false,
        });
      }
    },
    [
      enableKeyboardNavigation,
      currentlyOpenContextMenu,
      setCurrentDepth,
      mutateSelection,
      rowCount,
      reactTableInstance.allColumns.length,
      reactTableInstance.rows.length,
      getColumnCount,
      erase,
      resetSelectionAt,
      copy,
      cut,
      paste,
    ]
  );

  const onRowAdded2 = useCallback(
    (args: { beforeIndex: number }) => {
      if (onRowAdded) {
        onRowAdded(args);
        adaptSelection({
          atRowIndex: args.beforeIndex,
          rowCountDelta: 1,
          atColumnIndex: -1,
          columnCountDelta: 0,
        });
      }
    },
    [adaptSelection, onRowAdded]
  );

  const onColumnAdded2 = useCallback(
    (args: { beforeIndex: number; groupType: string }) => {
      if (onColumnAdded) {
        onColumnAdded(args);
        adaptSelection({
          atRowIndex: -1,
          rowCountDelta: 0,
          // The columnIndex here does not count the rowIndex columns, but the selection does. So + 1.
          atColumnIndex: args.beforeIndex + 1,
          columnCountDelta: 1,
        });
      }
    },
    [adaptSelection, onColumnAdded]
  );

  const onRowDuplicated2 = useCallback(
    (args: { rowIndex: number }) => {
      if (onRowDuplicated) {
        onRowDuplicated(args);
        adaptSelection({
          atRowIndex: args.rowIndex,
          rowCountDelta: 1,
          atColumnIndex: -1,
          columnCountDelta: 0,
        });
      }
    },
    [adaptSelection, onRowDuplicated]
  );

  const onRowDeleted2 = useCallback(
    (args: { rowIndex: number }) => {
      if (onRowDeleted) {
        onRowDeleted(args);
        adaptSelection({
          atRowIndex: args.rowIndex,
          rowCountDelta: -1,
          atColumnIndex: -1,
          columnCountDelta: 0,
        });
      }
    },
    [adaptSelection, onRowDeleted]
  );

  const onColumnDeleted2 = useCallback(
    (args: { columnIndex: number; groupType: string }) => {
      if (onColumnDeleted) {
        onColumnDeleted(args);
        adaptSelection({
          atRowIndex: -1,
          rowCountDelta: 0,
          // The columnIndex here does not count the rowIndex columns, but the selection does. So + 1.
          atColumnIndex: args.columnIndex + 1,
          columnCountDelta: -1,
        });
      }
    },
    [adaptSelection, onColumnDeleted]
  );

  const setEditing = useMemo(() => {
    return _setEditing(reactTableInstance.rows.length, () => reactTableInstance.allColumns.length);
  }, [_setEditing, reactTableInstance.allColumns.length, reactTableInstance.rows.length]);

  return (
    <div className={`table-component ${tableId}`} ref={tableRef} onKeyDown={onKeyDown}>
      <table
        {...reactTableInstance.getTableProps()}
        ref={tableComposableRef}
        data-ouia-component-id={"expression-grid-table"}
      >
        <BeeTableHeader<R>
          resizerStopBehavior={resizerStopBehavior}
          shouldRenderRowIndexColumn={shouldRenderRowIndexColumn}
          shouldShowRowsInlineControls={shouldShowColumnsInlineControls}
          editColumnLabel={editColumnLabel}
          isEditableHeader={isEditableHeader}
          getColumnKey={onGetColumnKey}
          headerVisibility={headerVisibility}
          onColumnUpdates={onColumnUpdates}
          skipLastHeaderGroup={skipLastHeaderGroup}
          tableColumns={columnsWithAddedIndexColumns}
          reactTableInstance={reactTableInstance}
          onColumnAdded={onColumnAdded2}
          lastColumnMinWidth={lastColumnMinWidth}
          setEditing={setEditing}
        />
        <BeeTableBody<R>
          rowWrapper={rowWrapper}
          resizerStopBehavior={resizerStopBehavior}
          shouldRenderRowIndexColumn={shouldRenderRowIndexColumn}
          shouldShowRowsInlineControls={shouldShowRowsInlineControls}
          getColumnKey={onGetColumnKey}
          getRowKey={onGetRowKey}
          headerVisibility={headerVisibility}
          reactTableInstance={reactTableInstance}
          additionalRow={additionalRow}
          onRowAdded={onRowAdded2}
          lastColumnMinWidth={lastColumnMinWidth}
        />
      </table>
      <BeeTableContextMenuHandler
        tableRef={tableRef}
        operationConfig={operationConfig}
        allowedOperations={allowedOperations}
        reactTableInstance={reactTableInstance}
        onRowAdded={onRowAdded2}
        onRowDuplicated={onRowDuplicated2}
        onRowDeleted={onRowDeleted2}
        onColumnAdded={onColumnAdded2}
        onColumnDeleted={onColumnDeleted2}
        onRowReset={onRowReset}
      />
    </div>
  );
}

export type BeeTableRef = BeeTableResizableColumnsDispatchContextType;

export type ForwardRefBeeTableProps<R extends object> = BeeTableProps<R> & { forwardRef?: React.Ref<BeeTableRef> } & {
  onColumnResizingWidthChange?: (args: Map<number, ResizingWidth | undefined>) => void;
};

export const BeeTable = <R extends object>(props: ForwardRefBeeTableProps<R>) => {
  return (
    <BeeTableSelectionContextProvider>
      <BeeTableResizableColumnsContextProvider
        ref={props.forwardRef}
        onChange={props.onColumnResizingWidthChange}
        columns={props.columns}
      >
        <BeeTableCellWidthsToFitDataContextProvider>
          <BeeTableInternal {...props} />
        </BeeTableCellWidthsToFitDataContextProvider>
      </BeeTableResizableColumnsContextProvider>
    </BeeTableSelectionContextProvider>
  );
};
