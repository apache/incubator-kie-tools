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
import { useCallback, useEffect, useMemo, useRef, useState } from "react";
import * as ReactTable from "react-table";
import * as PfReactTable from "@patternfly/react-table";
import { v4 as uuid } from "uuid";
import { generateUuid, BeeTableHeaderVisibility, BeeTableOperation, BeeTableProps } from "../../api";
import { useBoxedExpressionEditor } from "../BoxedExpressionEditor/BoxedExpressionEditorContext";
import {
  focusCurrentCell,
  focusInsideCell,
  focusLowerCell,
  focusNextCellByArrowKey,
  focusNextCellByTabKey,
  focusParentCell,
  focusPrevCellByArrowKey,
  focusPrevCellByTabKey,
  focusUpperCell,
  getParentCell,
  pasteOnTable,
  PASTE_OPERATION,
} from "./common";
import { NavigationKeysUtils } from "../../keysUtils";
import { BeeTableEditableCellContent } from "./BeeTableEditableCellContent";
import "./BeeTable.css";
import { BeeTableBody } from "./BeeTableBody";
import { BeeTableOperationHandler } from "./BeeTableOperationHandler";
import { BeeTableHeader } from "./BeeTableHeader";

export const NO_TABLE_CONTEXT_MENU_CLASS = "no-table-context-menu";

const ROW_INDEX_COLUMN_ACCESOR = "#";
const ROW_INDEX_SUB_COLUMN = "0";

export function getColumnsAtLastLevel<R extends object>(
  columns: ReactTable.ColumnInstance<R>[],
  depth: number = 0
): ReactTable.ColumnInstance<R>[] {
  return _.flatMap(columns, (column) => {
    if (column.columns) {
      return depth > 0 ? getColumnsAtLastLevel(column.columns, depth - 1) : column.columns;
    }
    return column;
  }) as ReactTable.ColumnInstance<R>[];
}

export function getColumnSearchPredicate<R extends object>(
  column: ReactTable.ColumnInstance<R> | undefined
): (columnsToCompare: ReactTable.ColumnInstance<R>) => boolean {
  const columnId = column?.originalId || column?.id || column?.accessor;
  return (columnToCompare: ReactTable.ColumnInstance<R>) => {
    return columnToCompare.id === columnId || columnToCompare.accessor === columnId;
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
  children,
  getNewColumnIdPrefix,
  editColumnLabel,
  editableHeader = true,
  onColumnsUpdate,
  onRowsUpdate,
  onNewRow,
  controllerCell = ROW_INDEX_COLUMN_ACCESOR,
  defaultCellByColumnId,
  rows,
  columns,
  operationHandlerConfig,
  headerVisibility,
  headerLevels = 0,
  skipLastHeaderGroup = false,
  getRowKey,
  getColumnKey,
  resetRowCustomFunction,
  readOnlyCells = false,
  enableKeyboardNavigation = true,
}: BeeTableProps<R>) {
  const tableComposableRef = useRef<HTMLTableElement>(null);
  const tableEventUUID = useMemo(() => `table-event-${uuid()}`, []);
  const boxedExpressionEditor = useBoxedExpressionEditor();

  const onGetColumnPrefix = useCallback(
    (groupType?: string) => getNewColumnIdPrefix?.(groupType) ?? "column-",
    [getNewColumnIdPrefix]
  );

  const generateRowNumberSubColumnRecursively: <R extends object>(
    column: ReactTable.ColumnInstance<R>,
    headerLevels: number
  ) => void = useCallback(
    (column, headerLevels) => {
      if (headerLevels > 0) {
        _.assign(column, {
          columns: [
            {
              label: headerVisibility === BeeTableHeaderVisibility.Full ? ROW_INDEX_SUB_COLUMN : controllerCell,
              accessor: ROW_INDEX_SUB_COLUMN,
              minWidth: 60,
              width: 60,
              disableResizing: true,
              isRowIndexColumn: true,
              hideFilter: true,
            },
          ],
        });

        if (column?.columns?.length) {
          generateRowNumberSubColumnRecursively(column.columns[0], headerLevels - 1);
        }
      }
    },
    [controllerCell, headerVisibility]
  );

  const generateRowNumberColumn = useCallback<
    (
      currentControllerCell: string | JSX.Element,
      columns: ReactTable.ColumnInstance<R>[]
    ) => ReactTable.ColumnInstance<R>[]
  >(
    (currentControllerCell, columns) => {
      const rowNumberColumn = {
        label: currentControllerCell,
        accessor: ROW_INDEX_COLUMN_ACCESOR,
        width: 60,
        minWidth: 60,
        isRowIndexColumn: true,
      } as ReactTable.ColumnInstance<R>;
      generateRowNumberSubColumnRecursively(rowNumberColumn, headerLevels);
      return [rowNumberColumn, ...columns];
    },
    [generateRowNumberSubColumnRecursively, headerLevels]
  );

  // FIXME: TIAGO(NOW) Remove this. If ID is required, there should be a type enforcement.
  const rowsWithId = useCallback<<R extends object>(rows: R[]) => R[]>((rows) => {
    return rows.map((row) => {
      // FIXME: Tiago -> Bad typing.
      if (_.isEmpty((row as any).id)) {
        (row as any).id ||= generateUuid();
      }
      return row;
    });
  }, []);

  const tableRowsRef = useRef<R[]>(rowsWithId(rows));
  const [showTableOperationHandler, setShowTableOperationHandler] = useState(false);
  const [operationHandlerTarget, setOperationHandlerTarget] = useState<HTMLElement>(
    boxedExpressionEditor.editorRef.current!
  );
  const [allowedOperations, setAllowedOperations] = useState(
    _.values(BeeTableOperation).map((operation) => parseInt(operation.toString()))
  );
  const [lastSelectedColumn, setLastSelectedColumn] = useState<ReactTable.ColumnInstance<R>>();
  const [lastSelectedRowIndex, setLastSelectedRowIndex] = useState(-1);

  const reactTableColumns = useMemo(
    () => generateRowNumberColumn(controllerCell, columns),
    [generateRowNumberColumn, columns, controllerCell]
  );

  useEffect(() => {
    tableRowsRef.current = rows;
  }, [rows]);

  useEffect(() => {
    function listener(event: CustomEvent) {
      if (event.detail.type !== PASTE_OPERATION || !tableRowsRef.current || tableRowsRef.current.length === 0) {
        return;
      }

      const { pasteValue, x, y } = event.detail;

      // FIXME: Tiago: Not good, as {} doesn't conform to R.
      const rowFactory = onNewRow ?? ((() => ({})) as any);

      const isLockedTable = _.some(tableRowsRef.current[0], (row) => {
        // FIXME: Tiago -> Logic specific to ExpressionDefinition.
        return (row as any)?.noClearAction;
      });

      if (!isLockedTable) {
        const pastedRows = pasteOnTable(pasteValue, tableRowsRef.current, rowFactory, x, y);
        tableRowsRef.current = pastedRows;
        onRowsUpdate?.({ rows: pastedRows, columns });
      }
    }

    boxedExpressionEditor.editorRef.current?.addEventListener(tableEventUUID, listener);
    return () => {
      boxedExpressionEditor.editorRef.current?.removeEventListener(tableEventUUID, listener);
    };
  }, [tableEventUUID, tableRowsRef, onRowsUpdate, onColumnsUpdate, onNewRow, columns, boxedExpressionEditor.editorRef]);

  const callOnColumnsUpdateWithoutRowIndexColumn = useCallback<
    (columns: ReactTable.ColumnInstance<R>[], operation?: BeeTableOperation, columnIndex?: number) => void
  >(
    (columns, operation, columnIndex) => {
      //Removing "# of rows" column
      onColumnsUpdate?.({ columns: columns.slice(1), operation, columnIndex: (columnIndex ?? 1) - 1 });
    },
    [onColumnsUpdate]
  );

  const callOnRowsUpdate = useCallback(
    (rows: R[], operation?: BeeTableOperation, rowIndex?: number) => {
      tableRowsRef.current = rows;
      onRowsUpdate?.({ rows: [...rows], operation, rowIndex, columns });
    },
    [onRowsUpdate, columns]
  );

  // FIXME: Tiago -> Bad typing
  const onCellUpdate = useCallback(
    (rowIndex: number, columnId: string, value: string) => {
      const cells = [...tableRowsRef.current];
      (cells as any)[rowIndex][columnId] = value;
      callOnRowsUpdate(cells);
    },
    [callOnRowsUpdate]
  );

  const onRowUpdate = useCallback(
    (rowIndex: number, updatedRow: R) => {
      const updatedRows = [...tableRowsRef.current];
      updatedRows[rowIndex] = updatedRow;
      callOnRowsUpdate(updatedRows);
    },
    [callOnRowsUpdate]
  );

  const isContextMenuAvailable = useCallback((target: HTMLElement) => {
    const targetIsContainedInCurrentTable = target.closest("table") === tableComposableRef.current;
    const contextMenuAvailableForTarget = !target.classList.contains(NO_TABLE_CONTEXT_MENU_CLASS);
    return targetIsContainedInCurrentTable && contextMenuAvailableForTarget;
  }, []);

  const operationHandlerStateUpdate = useCallback(
    (target: HTMLElement, column: ReactTable.ColumnInstance<R>) => {
      setOperationHandlerTarget(target);
      boxedExpressionEditor.currentlyOpenedHandlerCallback?.(false);
      setShowTableOperationHandler(true);
      boxedExpressionEditor.setContextMenuOpen(true);
      boxedExpressionEditor.setCurrentlyOpenedHandlerCallback?.(() => setShowTableOperationHandler);
      setLastSelectedColumn(column);
    },
    [boxedExpressionEditor]
  );

  const getColumnOperations = useCallback(
    (columnIndex: number) => {
      const columnsAtLastLevel = getColumnsAtLastLevel(reactTableColumns);
      const groupTypeForCurrentColumn = columnsAtLastLevel[columnIndex]?.groupType;
      const columnsByGroupType = _.groupBy(columnsAtLastLevel, (column) => column.groupType);
      const atLeastTwoColumnsOfTheSameGroupType = groupTypeForCurrentColumn
        ? columnsByGroupType[groupTypeForCurrentColumn].length > 1
        : reactTableColumns.length > 2; // The total number of columns is counting also the # of rows column

      const columnCanBeDeleted = columnIndex > 0 && atLeastTwoColumnsOfTheSameGroupType;

      return columnIndex === 0
        ? []
        : [
            BeeTableOperation.ColumnInsertLeft,
            BeeTableOperation.ColumnInsertRight,
            ...(columnCanBeDeleted ? [BeeTableOperation.ColumnDelete] : []),
          ];
    },
    [reactTableColumns]
  );

  const getThProps = useCallback(
    (column: ReactTable.ColumnInstance<R>) => ({
      onContextMenu: (e: ReactTable.ContextMenuEvent) => {
        const columnIndex = _.findIndex(
          getColumnsAtLastLevel(reactTableColumns, column.depth),
          getColumnSearchPredicate(column)
        );
        const target = e.target as HTMLElement;
        const handlerOnHeaderIsAvailable = !column.disableHandlerOnHeader;
        if (isContextMenuAvailable(target) && handlerOnHeaderIsAvailable) {
          e.preventDefault();
          setAllowedOperations(getColumnOperations(columnIndex));
          operationHandlerStateUpdate(target, column);
        }
      },
    }),
    [getColumnOperations, operationHandlerStateUpdate, isContextMenuAvailable, reactTableColumns]
  );

  const defaultColumn = useMemo(
    () => ({
      Cell: (cellProps: ReactTable.CellProps<R>) => {
        if (cellProps.column.isRowIndexColumn) {
          return cellProps.value;
        } else {
          const DefaultCellComponentForColumn = defaultCellByColumnId?.[cellProps.column.id];
          if (DefaultCellComponentForColumn) {
            return DefaultCellComponentForColumn({
              ...cellProps,
              rowIndex: cellProps.row.index,
              columnId: cellProps.column.id,
            });
          }
          return (
            <BeeTableEditableCellContent
              {...cellProps}
              rowIndex={cellProps.row.index}
              columnId={cellProps.column.id}
              readOnly={readOnlyCells}
            />
          );
        }
      },
    }),
    [defaultCellByColumnId, readOnlyCells]
  );

  const getTdProps = useCallback(
    (columnIndex: number, rowIndex: number) => ({
      onContextMenu: (e: ReactTable.ContextMenuEvent) => {
        const target = e.target as HTMLElement;
        if (isContextMenuAvailable(target)) {
          e.preventDefault();
          setAllowedOperations([
            ...getColumnOperations(columnIndex),
            BeeTableOperation.RowInsertAbove,
            BeeTableOperation.RowInsertBelow,
            ...(rows.length > 1 ? [BeeTableOperation.RowDelete] : []),
            BeeTableOperation.RowClear,
            BeeTableOperation.RowDuplicate,
          ]);
          operationHandlerStateUpdate(target, getColumnsAtLastLevel(reactTableColumns, headerLevels)[columnIndex]);
          setLastSelectedRowIndex(rowIndex);

          focusCurrentCell(target);
        }
      },
    }),
    [getColumnOperations, operationHandlerStateUpdate, isContextMenuAvailable, reactTableColumns, rows, headerLevels]
  );

  const reactTableInstance = ReactTable.useTable<R>(
    {
      columns: reactTableColumns as any, // FIXME: Tiago: ? :)
      data: rows,
      defaultColumn,
      onCellUpdate,
      onRowUpdate,
    },
    ReactTable.useBlockLayout,
    ReactTable.useResizeColumns
  );

  const onGetColumnKey = useCallback<(column: ReactTable.ColumnInstance<R>) => string>(
    (column) => {
      const columnId = column.originalId || column.id;
      return getColumnKey ? getColumnKey(column) : columnId;
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

        if (boxedExpressionEditor.isContextMenuOpen) {
          e.preventDefault();
          if (NavigationKeysUtils.isEscape(key)) {
            //close Select child components if any
            focusCurrentCell(currentTarget);
          }
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

        if (
          !boxedExpressionEditor.isContextMenuOpen &&
          isFiredFromThis &&
          !isModKey &&
          NavigationKeysUtils.isTypingKey(key)
        ) {
          return focusInsideCell(currentTarget, !NavigationKeysUtils.isEnter(key));
        }
      },
    [boxedExpressionEditor.isContextMenuOpen, enableKeyboardNavigation]
  );

  return (
    <div className={`table-component ${tableId} ${tableEventUUID}`}>
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
          tableColumns={reactTableColumns}
          reactTableInstance={reactTableInstance}
          getThProps={getThProps}
        />
        <BeeTableBody<R>
          getColumnKey={onGetColumnKey}
          getRowKey={onGetRowKey}
          headerVisibility={headerVisibility}
          onCellKeyDown={onCellKeyDown}
          onColumnsUpdate={callOnColumnsUpdateWithoutRowIndexColumn}
          skipLastHeaderGroup={skipLastHeaderGroup}
          reactTableInstance={reactTableInstance}
          tdProps={getTdProps}
        >
          {children}
        </BeeTableBody>
      </PfReactTable.TableComposable>
      {showTableOperationHandler && operationHandlerConfig && (
        <BeeTableOperationHandler<R>
          tableColumns={reactTableColumns}
          getNewColumnIdPrefix={onGetColumnPrefix}
          operationHandlerConfig={operationHandlerConfig}
          lastSelectedColumn={lastSelectedColumn}
          lastSelectedRowIndex={lastSelectedRowIndex}
          tableRows={tableRowsRef}
          onRowsUpdate={callOnRowsUpdate}
          onNewRow={onNewRow}
          showTableOperationHandler={showTableOperationHandler}
          setShowTableOperationHandler={setShowTableOperationHandler}
          allowedOperations={allowedOperations}
          operationHandlerTarget={operationHandlerTarget}
          resetRowCustomFunction={resetRowCustomFunction}
          onColumnsUpdate={callOnColumnsUpdateWithoutRowIndexColumn}
        />
      )}
    </div>
  );
}
