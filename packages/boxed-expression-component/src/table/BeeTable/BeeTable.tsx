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

import * as PfReactTable from "@patternfly/react-table";
import * as _ from "lodash";
import * as React from "react";
import { useCallback, useMemo, useRef } from "react";
import * as ReactTable from "react-table";
import { BeeTableHeaderVisibility, BeeTableProps } from "../../api";
import { useBoxedExpressionEditor } from "../../expressions/BoxedExpressionEditor/BoxedExpressionEditorContext";
import { NavigationKeysUtils } from "../../keysUtils";
import { ResizingWidth } from "../../resizing/ResizingWidthsContext";
import { BEE_TABLE_ROW_INDEX_COLUMN_WIDTH } from "../../resizing/WidthValues";
import "./BeeTable.css";
import { BeeTableBody } from "./BeeTableBody";
import { BeeTableColumnResizingWidthsContextProvider } from "./BeeTableColumnResizingWidthsContextProvider";
import { BeeTableContextMenuHandler } from "./BeeTableContextMenuHandler";
import { BeeTableDefaultCell } from "./BeeTableDefaultCell";
import { BeeTableHeader } from "./BeeTableHeader";
import {
  BeeTableSelectionContextProvider,
  SelectionPart,
  SELECTION_MIN_ACTIVE_DEPTH,
  useBeeTableSelectionDispatch,
} from "./BeeTableSelectionContext";

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

export function BeeTable2<R extends object>({
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
  cellComponentByColumnId,
  rows,
  columns,
  operationConfig,
  headerVisibility = BeeTableHeaderVisibility.AllLevels,
  headerLevelCount = 0,
  skipLastHeaderGroup = false,
  getRowKey,
  getColumnKey,
  isReadOnly = false,
  enableKeyboardNavigation = true,
  shouldRenderRowIndexColumn,
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
                  : (controllerCell as any), // FIXME: Tiago -> Not good.
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
        label: currentControllerCell as any, //FIXME: Tiago -> No bueno.
        accessor: ROW_INDEX_COLUMN_ACCESOR as any,
        width: BEE_TABLE_ROW_INDEX_COLUMN_WIDTH,
        minWidth: BEE_TABLE_ROW_INDEX_COLUMN_WIDTH,
        isRowIndexColumn: true,
        dataType: undefined as any, // FIXME: Tiago -> No bueno.
      };

      addRowIndexColumnsRecursively(rowIndexColumn, headerLevelCount);

      return [rowIndexColumn, ...columns];
    },
    [addRowIndexColumnsRecursively, headerLevelCount]
  );

  const columnsWithAddedIndexColumns = useMemo(
    () => addRowIndexColumns(controllerCell, columns),
    [addRowIndexColumns, columns, controllerCell]
  );

  const defaultColumn = useMemo(
    () => ({
      Cell: (cellProps: ReactTable.CellProps<R>) => {
        const CellComponentForColumn = cellComponentByColumnId?.[cellProps.column.id];
        if (CellComponentForColumn) {
          return (
            <CellComponentForColumn
              data={cellProps.data}
              rowIndex={cellProps.row.index}
              columnIndex={cellProps.allColumns.findIndex((c) => c.id === cellProps.column.id)}
              columnId={cellProps.column.id}
            />
          );
        } else {
          return (
            <BeeTableDefaultCell
              hasAdditionalRow={hasAdditionalRow}
              cellProps={cellProps}
              onCellUpdates={onCellUpdates}
              isReadOnly={isReadOnly}
            />
          );
        }
      },
    }),
    [hasAdditionalRow, cellComponentByColumnId, isReadOnly, onCellUpdates]
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
          // FIXME: Tiago -> Bad typing.
          return (row.original as any).id;
        }
        return row.id;
      }
    },
    [getRowKey]
  );

  const rowCount = useMemo(() => {
    return reactTableInstance.rows.length + (hasAdditionalRow ? 1 : 0);
  }, [hasAdditionalRow, reactTableInstance.rows.length]);

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
        mutateSelection({
          part: SelectionPart.ActiveCell,
          columnCount: reactTableInstance.allColumns.length,
          rowCount,
          deltaColumns: 0,
          deltaRows: 0,
          isEditingActiveCell: true,
          keepInsideSelection: true,
        });
      }

      // TAB
      if (NavigationKeysUtils.isTab(e.key)) {
        e.stopPropagation();
        e.preventDefault();
        if (e.shiftKey) {
          mutateSelection({
            part: SelectionPart.ActiveCell,
            columnCount: reactTableInstance.allColumns.length,
            rowCount,
            deltaColumns: -1,
            deltaRows: 0,
            isEditingActiveCell: false,
            keepInsideSelection: true,
          });
        } else {
          mutateSelection({
            part: SelectionPart.ActiveCell,
            columnCount: reactTableInstance.allColumns.length,
            rowCount,
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
          columnCount: reactTableInstance.allColumns.length,
          rowCount,
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
          columnCount: reactTableInstance.allColumns.length,
          rowCount,
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
          columnCount: reactTableInstance.allColumns.length,
          rowCount,
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
          columnCount: reactTableInstance.allColumns.length,
          rowCount,
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

      // SPACE
      if (NavigationKeysUtils.isSpace(e.key) && !e.metaKey && !e.altKey && !e.ctrlKey && !e.shiftKey) {
        e.stopPropagation();
        setCurrentDepth((prev) => ({
          max: prev.max,
          active: Math.min(prev.max, (prev.active ?? SELECTION_MIN_ACTIVE_DEPTH) + 1),
        }));
      }

      // FIXME: Tiago -> This won't work well on non-macOS
      // COPY/CUT/PASTE
      if (!e.shiftKey && e.metaKey && e.key === "c") {
        e.stopPropagation();
        e.preventDefault();
        copy();
      }
      if (!e.shiftKey && e.metaKey && e.key === "x") {
        e.stopPropagation();
        e.preventDefault();
        cut();
      }
      if (!e.shiftKey && e.metaKey && e.key === "v") {
        e.stopPropagation();
        e.preventDefault();
        paste();
      }

      // FIXME: Tiago -> This won't work well on non-macOS
      // SELECT ALL
      if (!e.shiftKey && e.metaKey && e.key === "a") {
        e.stopPropagation();
        e.preventDefault();

        mutateSelection({
          part: SelectionPart.SelectionStart,
          columnCount: reactTableInstance.allColumns.length,
          rowCount,
          deltaColumns: -(reactTableInstance.allColumns.length - 1),
          deltaRows: -(reactTableInstance.rows.length - 1),
          isEditingActiveCell: false,
          keepInsideSelection: false,
        });
        mutateSelection({
          part: SelectionPart.SelectionEnd,
          columnCount: reactTableInstance.allColumns.length,
          rowCount,
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
      reactTableInstance.allColumns.length,
      reactTableInstance.rows.length,
      rowCount,
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

  return (
    <div className={`table-component ${tableId}`} ref={tableRef} onKeyDown={onKeyDown}>
      <PfReactTable.TableComposable
        {...reactTableInstance.getTableProps()}
        variant="compact"
        ref={tableComposableRef}
        ouiaId="expression-grid-table"
      >
        <BeeTableHeader<R>
          shouldRenderRowIndexColumn={shouldRenderRowIndexColumn}
          editColumnLabel={editColumnLabel}
          isEditableHeader={isEditableHeader}
          getColumnKey={onGetColumnKey}
          headerVisibility={headerVisibility}
          onColumnUpdates={onColumnUpdates}
          skipLastHeaderGroup={skipLastHeaderGroup}
          tableColumns={columnsWithAddedIndexColumns}
          reactTableInstance={reactTableInstance}
          onColumnAdded={onColumnAdded2}
        />
        <BeeTableBody<R>
          shouldRenderRowIndexColumn={shouldRenderRowIndexColumn}
          getColumnKey={onGetColumnKey}
          getRowKey={onGetRowKey}
          headerVisibility={headerVisibility}
          reactTableInstance={reactTableInstance}
          additionalRow={additionalRow}
          onRowAdded={onRowAdded2}
        />
      </PfReactTable.TableComposable>
      <BeeTableContextMenuHandler
        tableRef={tableRef}
        operationConfig={operationConfig}
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

export function BeeTable<R extends object>(
  props: BeeTableProps<R> & {
    onColumnResizingWidthChange?: (args: { columnIndex: number; newResizingWidth: ResizingWidth }) => void;
  }
) {
  return (
    <BeeTableSelectionContextProvider>
      <BeeTableColumnResizingWidthsContextProvider onChange={props.onColumnResizingWidthChange}>
        <BeeTable2 {...props} />
      </BeeTableColumnResizingWidthsContextProvider>
    </BeeTableSelectionContextProvider>
  );
}
