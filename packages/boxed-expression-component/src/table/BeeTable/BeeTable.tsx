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
import { useCallback, useMemo, useRef, useState } from "react";
import * as ReactTable from "react-table";
import { v4 as uuid } from "uuid";
import { BeeTableHeaderVisibility, BeeTableOperation, BeeTableProps } from "../../api";
import { NavigationKeysUtils } from "../../keysUtils";
import { useBoxedExpressionEditor } from "../../expressions/BoxedExpressionEditor/BoxedExpressionEditorContext";
import "./BeeTable.css";
import { BeeTableBody } from "./BeeTableBody";
import { BeeTableContextMenuHandler } from "./BeeTableContextMenuHandler";
import { BeeTableEditableCellContent } from "./BeeTableEditableCellContent";
import { BeeTableHeader } from "./BeeTableHeader";
import {
  focusInsideCell,
  focusLowerCell,
  focusNextCellByArrowKey,
  focusNextCellByTabKey,
  focusParentCell,
  focusPrevCellByArrowKey,
  focusPrevCellByTabKey,
  focusUpperCell,
  getParentCell,
} from "./common";
import { BEE_TABLE_ROW_INDEX_COLUMN_WIDTH } from "../../expressions/ContextExpression";

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

/**
 * Callback fired during arrow navigation.
 *
 * @param e the event object
 * @param rowSpan the cell rowSpan, default is 1
 * @returns
 */
const onCellTabNavigation = (e: KeyboardEvent, rowSpan = 1) => {
  const currentTarget = e.currentTarget as HTMLElement;
  e.preventDefault();

  if (e.shiftKey) {
    return focusPrevCellByTabKey(currentTarget, rowSpan);
  } else {
    return focusNextCellByTabKey(currentTarget, rowSpan);
  }
};

/**
 * Callback fired during arrow navigation.
 *
 * @param e the event object
 * @param rowSpan the cell rowSpan, default is 1
 * @returns
 */
const onCellArrowNavigation = (e: KeyboardEvent, rowSpan = 1): void => {
  const key = e.key;
  const currentTarget = e.currentTarget as HTMLElement;

  if (NavigationKeysUtils.isArrowLeft(key)) {
    e.preventDefault();
    return focusPrevCellByArrowKey(currentTarget, rowSpan);
  }
  if (NavigationKeysUtils.isArrowRight(key)) {
    e.preventDefault();
    return focusNextCellByArrowKey(currentTarget, rowSpan);
  }
  if (NavigationKeysUtils.isArrowUp(key)) {
    e.preventDefault();
    return focusUpperCell(currentTarget);
  }
  if (NavigationKeysUtils.isArrowDown(key)) {
    e.preventDefault();
    return focusLowerCell(currentTarget);
  }
};

export function BeeTable<R extends object>({
  tableId,
  additionalRow,
  editColumnLabel,
  editableHeader = true,
  onColumnsUpdate,
  onRowAdded,
  onColumnAdded,
  controllerCell = ROW_INDEX_COLUMN_ACCESOR,
  cellComponentByColumnId,
  rows,
  columns,
  operationHandlerConfig,
  headerVisibility,
  headerLevelCount = 0,
  skipLastHeaderGroup = false,
  getRowKey,
  getColumnKey,
  isReadOnly = false,
  enableKeyboardNavigation = true,
}: BeeTableProps<R>) {
  const tableComposableRef = useRef<HTMLTableElement>(null);
  const tableEventUUID = useMemo(() => `table-event-${uuid()}`, []);
  const { currentlyOpenContextMenu, setCurrentlyOpenContextMenu } = useBoxedExpressionEditor();

  const tableRef = React.useRef<HTMLDivElement>(null);

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
                headerVisibility === BeeTableHeaderVisibility.Full
                  ? ROW_INDEX_SUB_COLUMN_ACCESSOR
                  : (controllerCell as any),
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

      // FIXME: Tiago -> This is a special case because the controller cell doesn't have a dataType, but...
      return [rowIndexColumn, ...columns];
    },
    [addRowIndexColumnsRecursively, headerLevelCount]
  );

  const [lastSelectedColumnIndex, setLastSelectedColumnIndex] = useState(-1);
  const [lastSelectedRowIndex, setLastSelectedRowIndex] = useState(-1);

  const columnsWithAddedIndexColumns = useMemo(
    () => addRowIndexColumns(controllerCell, columns),
    [addRowIndexColumns, columns, controllerCell]
  );

  const callOnColumnsUpdateWithoutRowIndexColumn = useCallback<
    (columns: ReactTable.Column<R>[], operation?: BeeTableOperation, columnIndex?: number) => void
  >(
    (columns, operation, columnIndex) => {
      const originalColumns = columns.slice(1); //Removing row index column
      onColumnsUpdate?.({ columns: originalColumns, operation, columnIndex: (columnIndex ?? 1) - 1 });
    },
    [onColumnsUpdate]
  );

  const defaultColumn = useMemo(
    () => ({
      Cell: (cellProps: ReactTable.CellProps<R>) => {
        if (cellProps.column.isRowIndexColumn) {
          return cellProps.value;
        } else {
          const CellComponentForColumn = cellComponentByColumnId?.[cellProps.column.id];
          if (CellComponentForColumn) {
            return (
              <CellComponentForColumn
                data={cellProps.data}
                rowIndex={cellProps.row.index}
                columnId={cellProps.column.id}
              />
            );
          }
          return (
            <BeeTableEditableCellContent
              onCellUpdate={() => {}} // FIXME: Tiago -> STATE GAP
              value={cellProps.value}
              rowIndex={cellProps.row.index}
              columnId={cellProps.column.id}
              isReadOnly={isReadOnly}
            />
          );
        }
      },
    }),
    [cellComponentByColumnId, isReadOnly]
  );

  const reactTableInstance = ReactTable.useTable<R>(
    {
      columns: columnsWithAddedIndexColumns,
      data: rows,
      defaultColumn,
    },
    ReactTable.useBlockLayout
  );

  const getColumnOperations = useCallback(
    (columnIndex: number) => {
      const groupTypeForCurrentColumn = reactTableInstance.allColumns[columnIndex]?.groupType;
      const columnsByGroupType = _.groupBy(reactTableInstance.allColumns, (column) => column.groupType);
      const atLeastTwoColumnsOfTheSameGroupType = groupTypeForCurrentColumn
        ? columnsByGroupType[groupTypeForCurrentColumn].length > 1
        : columnsWithAddedIndexColumns.length > 2; // The total number of columns is counting also the # of rows column

      const columnCanBeDeleted = columnIndex > 0 && atLeastTwoColumnsOfTheSameGroupType;

      return columnIndex === 0 // This is the "row index" column
        ? []
        : [
            BeeTableOperation.ColumnInsertLeft,
            BeeTableOperation.ColumnInsertRight,
            ...(columnCanBeDeleted ? [BeeTableOperation.ColumnDelete] : []),
          ];
    },
    [columnsWithAddedIndexColumns.length, reactTableInstance.allColumns]
  );

  const getMouseDownThProps = useCallback(
    (columnIndex: number): Pick<PfReactTable.ThProps, "onMouseDown"> => ({
      onMouseDown: (e) => {
        e.preventDefault();
        setLastSelectedColumnIndex(columnIndex);
        setLastSelectedRowIndex(-1);
      },
    }),
    []
  );

  const getMouseDownTdProps = useCallback(
    (columnIndex: number, rowIndex: number): Pick<PfReactTable.TdProps, "onMouseDown"> => ({
      onMouseDown: (e) => {
        e.preventDefault();

        setLastSelectedColumnIndex(columnIndex);
        setLastSelectedRowIndex(rowIndex);
      },
    }),
    []
  );

  // FIXME: Tiago -> Pasting
  //
  // useEffect(() => {
  //   function listener(event: CustomEvent) {
  //     if (event.detail.type !== PASTE_OPERATION || !tableRowsRef.current || tableRowsRef.current.length === 0) {
  //       return;
  //     }

  //     const { pasteValue, x, y } = event.detail;

  //     // FIXME: Tiago: Not good, as {} doesn't conform to R.
  //     const rowFactory = onNewRow ?? ((() => ({})) as any);

  //     const isLockedTable = _.some(tableRowsRef.current[0], (row) => {
  //       // FIXME: Tiago -> Logic specific to ExpressionDefinition.
  //       return (row as any)?.noClearAction;
  //     });

  //     if (!isLockedTable) {
  //       const pastedRows = pasteOnTable(pasteValue, tableRowsRef.current, rowFactory, x, y);
  //       tableRowsRef.current = pastedRows;
  //       onRowsUpdate?.({ rows: pastedRows, columns });
  //     }
  //   }

  //   boxedExpressionEditor.editorRef.current?.addEventListener(tableEventUUID, listener);
  //   return () => {
  //     boxedExpressionEditor.editorRef.current?.removeEventListener(tableEventUUID, listener);
  //   };
  // }, [tableEventUUID, tableRowsRef, onRowsUpdate, onColumnsUpdate, onNewRow, boxedExpressionEditor.editorRef, columns]);

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

  /**
   * Function to be executed when a key has been pressed on a cell
   * @param rowIndex the index of the row
   */
  const onCellKeyDown = useCallback(
    (rowSpan = 1) =>
      (e: KeyboardEvent) => {
        const key = e.key;
        const isModKey = e.altKey || e.ctrlKey || e.shiftKey || NavigationKeysUtils.isAltGraph(key);
        const currentTarget = e.currentTarget as HTMLElement;
        const isFiredFromThis = e.currentTarget === e.target;

        if (!enableKeyboardNavigation) {
          return;
        }

        //prevent the parent cell catch this event if there is a nested table
        if (e.currentTarget !== getParentCell(e.target as HTMLElement)) {
          return;
        }

        if (NavigationKeysUtils.isTab(key)) {
          return onCellTabNavigation(e, rowSpan);
        }

        if (NavigationKeysUtils.isAnyArrow(key)) {
          return onCellArrowNavigation(e, rowSpan);
        }

        if (NavigationKeysUtils.isEscape(key)) {
          return focusParentCell(currentTarget);
        }

        if (!currentlyOpenContextMenu && isFiredFromThis && !isModKey && NavigationKeysUtils.isTypingKey(key)) {
          return focusInsideCell(currentTarget, !NavigationKeysUtils.isEnter(key));
        }
      },
    [currentlyOpenContextMenu, enableKeyboardNavigation]
  );

  const headerRowsCount = useMemo(() => {
    const headerGroupsLength = skipLastHeaderGroup
      ? reactTableInstance.headerGroups.length - 1
      : reactTableInstance.headerGroups.length;

    switch (headerVisibility) {
      case BeeTableHeaderVisibility.Full:
        return headerGroupsLength;
      case BeeTableHeaderVisibility.LastLevel:
        return headerGroupsLength - 1;
      case BeeTableHeaderVisibility.SecondToLastLevel:
        return headerGroupsLength - 1;
      default:
        return 0;
    }
  }, [headerVisibility, reactTableInstance.headerGroups.length, skipLastHeaderGroup]);

  const operationGroups = useMemo(() => {
    if (_.isArray(operationHandlerConfig)) {
      return operationHandlerConfig;
    }
    const column = reactTableInstance.allColumns[lastSelectedColumnIndex];
    return (operationHandlerConfig ?? {})[column?.groupType || ""];
  }, [lastSelectedColumnIndex, operationHandlerConfig, reactTableInstance.allColumns]);

  const allowedOperations = useMemo(() => {
    return [
      ...getColumnOperations(lastSelectedColumnIndex),
      ...(lastSelectedRowIndex >= 0
        ? [
            BeeTableOperation.RowInsertAbove,
            BeeTableOperation.RowInsertBelow,
            ...(rows.length > 1 ? [BeeTableOperation.RowDelete] : []),
            BeeTableOperation.RowClear,
            BeeTableOperation.RowDuplicate,
          ]
        : []),
    ];
  }, [getColumnOperations, lastSelectedColumnIndex, lastSelectedRowIndex, rows.length]);

  return (
    <div className={`table-component ${tableId} ${tableEventUUID}`} ref={tableRef}>
      <PfReactTable.TableComposable
        {...reactTableInstance.getTableProps()}
        variant="compact"
        ref={tableComposableRef}
        ouiaId="expression-grid-table"
      >
        <BeeTableHeader<R>
          editColumnLabel={editColumnLabel}
          editableHeader={editableHeader}
          getColumnKey={onGetColumnKey}
          headerVisibility={headerVisibility}
          onCellKeyDown={onCellKeyDown}
          onColumnsUpdate={callOnColumnsUpdateWithoutRowIndexColumn}
          skipLastHeaderGroup={skipLastHeaderGroup}
          tableColumns={columnsWithAddedIndexColumns}
          reactTableInstance={reactTableInstance}
          getMouseDownThProps={getMouseDownThProps}
          onColumnAdded={onColumnAdded}
        />
        <BeeTableBody<R>
          getColumnKey={onGetColumnKey}
          getRowKey={onGetRowKey}
          headerVisibility={headerVisibility}
          onCellKeyDown={onCellKeyDown}
          headerRowsCount={headerRowsCount}
          reactTableInstance={reactTableInstance}
          getMouseDownTdProps={getMouseDownTdProps}
          additionalRow={additionalRow}
          onRowAdded={onRowAdded}
        />
      </PfReactTable.TableComposable>
      <BeeTableContextMenuHandler
        tableRef={tableRef}
        operationGroups={operationGroups}
        allowedOperations={allowedOperations}
        lastSelectedColumnIndex={lastSelectedColumnIndex}
        lastSelectedRowIndex={lastSelectedRowIndex}
      />
    </div>
  );
}
