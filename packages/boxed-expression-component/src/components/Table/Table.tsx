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
import { useCallback, useContext, useEffect, useMemo, useRef, useState } from "react";
import {
  Column,
  ColumnInstance,
  ContextMenuEvent,
  DataRecord,
  Row,
  useBlockLayout,
  useResizeColumns,
  useTable,
} from "react-table";
import { TableComposable } from "@patternfly/react-table";
import { v4 as uuid } from "uuid";
import { TableHeaderVisibility, TableOperation, TableProps } from "../../api";
import { BoxedExpressionGlobalContext } from "../../context";
import { PASTE_OPERATION, pasteOnTable } from "./common";
import { EditableCell } from "./EditableCell";
import "./Table.css";
import { TableBody } from "./TableBody";
import { TableHandler } from "./TableHandler";
import { TableHeader } from "./TableHeader";

export const NO_TABLE_CONTEXT_MENU_CLASS = "no-table-context-menu";
const NUMBER_OF_ROWS_COLUMN = "#";
const NUMBER_OF_ROWS_SUBCOLUMN = "0";

export const DEFAULT_ON_ROW_ADDING = () => ({});

export const getColumnsAtLastLevel: (columns: ColumnInstance[] | Column[], depth?: number) => ColumnInstance[] = (
  columns,
  depth = 0
) =>
  _.flatMap(columns, (column: ColumnInstance) => {
    if (_.has(column, "columns")) {
      return depth > 0 ? getColumnsAtLastLevel(column.columns!, depth - 1) : column.columns;
    }
    return column;
  }) as ColumnInstance[];

export const getColumnSearchPredicate: (column: ColumnInstance) => (columnToCompare: ColumnInstance) => boolean = (
  column
) => {
  const columnId = column.originalId || column.id || column.accessor;
  return (columnToCompare: ColumnInstance) => {
    return columnToCompare.id === columnId || columnToCompare.accessor === columnId;
  };
};

export const Table: React.FunctionComponent<TableProps> = ({
  tableId,
  children,
  getColumnPrefix,
  editColumnLabel,
  editableHeader = true,
  onColumnsUpdate,
  onRowsUpdate,
  onRowAdding,
  controllerCell = NUMBER_OF_ROWS_COLUMN,
  defaultCell,
  rows,
  columns,
  handlerConfiguration,
  headerVisibility,
  headerLevels = 0,
  skipLastHeaderGroup = false,
  getRowKey,
  getColumnKey,
  resetRowCustomFunction,
  readOnlyCells = false,
}: TableProps) => {
  const tableRef = useRef<HTMLTableElement>(null);
  const tableEventUUID = useMemo(() => `table-event-${uuid()}`, []);

  const onRowAddingCallback = useCallback(() => {
    return onRowAdding ? onRowAdding() : {};
  }, [onRowAdding]);
  const onGetColumnPrefix = useCallback(() => (getColumnPrefix ? getColumnPrefix() : "column-"), [getColumnPrefix]);

  const globalContext = useContext(BoxedExpressionGlobalContext);

  const generateNumberOfRowsSubColumnRecursively: (column: ColumnInstance, headerLevels: number) => void = useCallback(
    (column, headerLevels) => {
      if (headerLevels > 0) {
        _.assign(column, {
          columns: [
            {
              label: headerVisibility === TableHeaderVisibility.Full ? NUMBER_OF_ROWS_SUBCOLUMN : controllerCell,
              accessor: NUMBER_OF_ROWS_SUBCOLUMN,
              minWidth: 60,
              width: 60,
              disableResizing: true,
              isCountColumn: true,
              hideFilter: true,
            },
          ],
        });

        if (column?.columns?.length) {
          generateNumberOfRowsSubColumnRecursively(column.columns[0], headerLevels - 1);
        }
      }
    },
    [controllerCell, headerVisibility]
  );

  const generateNumberOfRowsColumn = useCallback(
    (currentControllerCell: string | JSX.Element, columns: Column[]) => {
      const numberOfRowsColumn = {
        label: currentControllerCell,
        accessor: NUMBER_OF_ROWS_COLUMN,
        width: 60,
        minWidth: 60,
        isCountColumn: true,
      } as ColumnInstance;
      generateNumberOfRowsSubColumnRecursively(numberOfRowsColumn, headerLevels);
      return [numberOfRowsColumn, ...columns];
    },
    [generateNumberOfRowsSubColumnRecursively, headerLevels]
  );

  const tableRows = useRef<DataRecord[]>(rows);
  const [showTableHandler, setShowTableHandler] = useState(false);
  const [tableHandlerTarget, setTableHandlerTarget] = useState(document.body);
  const [tableHandlerAllowedOperations, setTableHandlerAllowedOperations] = useState(
    _.values(TableOperation).map((operation) => parseInt(operation.toString()))
  );
  const [lastSelectedColumn, setLastSelectedColumn] = useState({} as ColumnInstance);
  const [lastSelectedRowIndex, setLastSelectedRowIndex] = useState(-1);

  const tableColumns = useMemo(
    () => generateNumberOfRowsColumn(controllerCell, columns),
    [generateNumberOfRowsColumn, columns, controllerCell]
  );

  useEffect(() => {
    tableRows.current = rows;
  }, [rows]);

  useEffect(() => {
    function listener(event: CustomEvent) {
      if (event.detail.type !== PASTE_OPERATION || !tableRows.current || tableRows.current.length === 0) {
        return;
      }

      const { pasteValue, x, y } = event.detail;
      const rows = tableRows.current;
      const rowFactory = onRowAddingCallback;

      const isLockedTable = _.some(tableRows.current[0], (col: { noClearAction: boolean }) => {
        return col && col.noClearAction;
      });

      if (DEFAULT_ON_ROW_ADDING !== rowFactory && !isLockedTable) {
        const pastedRows = pasteOnTable(pasteValue, rows, rowFactory, x, y);
        tableRows.current = pastedRows;
        onRowsUpdate?.(pastedRows);
      }
    }

    document.addEventListener(tableEventUUID, listener);
    return () => {
      document.removeEventListener(tableEventUUID, listener);
    };
  }, [tableEventUUID, tableRows, onRowsUpdate, onColumnsUpdate, onRowAddingCallback]);

  const onColumnsUpdateCallback = useCallback(
    (columns: Column[], operation?: TableOperation, columnIndex?: number) => {
      //Removing "# of rows" column
      onColumnsUpdate?.(columns.slice(1), operation, (columnIndex ?? 1) - 1);
    },
    [onColumnsUpdate]
  );

  const onRowsUpdateCallback = useCallback(
    (rows: DataRecord[], operation?: TableOperation, rowIndex?: number) => {
      tableRows.current = rows;
      onRowsUpdate?.([...rows], operation, rowIndex);
    },
    [onRowsUpdate]
  );

  const onCellUpdate = useCallback(
    (rowIndex: number, columnId: string, value: string) => {
      const updatedTableCells = [...tableRows.current];
      updatedTableCells[rowIndex][columnId] = value;
      onRowsUpdateCallback(updatedTableCells);
    },
    [onRowsUpdateCallback]
  );

  const onRowUpdate = useCallback(
    (rowIndex: number, updatedRow: DataRecord) => {
      const updatedRows = [...tableRows.current];
      updatedRows[rowIndex] = updatedRow;
      onRowsUpdateCallback(updatedRows);
    },
    [onRowsUpdateCallback]
  );

  const contextMenuIsAvailable = useCallback((target: HTMLElement) => {
    const targetIsContainedInCurrentTable = target.closest("table") === tableRef.current;
    const contextMenuAvailableForTarget = !target.classList.contains(NO_TABLE_CONTEXT_MENU_CLASS);
    return targetIsContainedInCurrentTable && contextMenuAvailableForTarget;
  }, []);

  const tableHandlerStateUpdate = useCallback(
    (target: HTMLElement, column: ColumnInstance) => {
      setTableHandlerTarget(target);
      globalContext.currentlyOpenedHandlerCallback?.(false);
      setShowTableHandler(true);
      globalContext.setCurrentlyOpenedHandlerCallback?.(() => setShowTableHandler);
      setLastSelectedColumn(column);
    },
    [globalContext]
  );

  const getColumnOperations = useCallback(
    (columnIndex: number) => {
      const columnsAtLastLevel = getColumnsAtLastLevel(tableColumns);
      const groupTypeForCurrentColumn = (columnsAtLastLevel[columnIndex] as ColumnInstance)?.groupType;
      const columnsByGroupType = _.groupBy(columnsAtLastLevel, (column: ColumnInstance) => column.groupType);
      const atLeastTwoColumnsOfTheSameGroupType = groupTypeForCurrentColumn
        ? columnsByGroupType[groupTypeForCurrentColumn].length > 1
        : tableColumns.length > 2; // The total number of columns is counting also the # of rows column

      const columnCanBeDeleted = columnIndex > 0 && atLeastTwoColumnsOfTheSameGroupType;

      return columnIndex === 0
        ? []
        : [
            TableOperation.ColumnInsertLeft,
            TableOperation.ColumnInsertRight,
            ...(columnCanBeDeleted ? [TableOperation.ColumnDelete] : []),
          ];
    },
    [tableColumns]
  );

  const thProps = useCallback(
    (column: ColumnInstance) => ({
      onContextMenu: (e: ContextMenuEvent) => {
        const columnIndex = _.findIndex(
          getColumnsAtLastLevel(tableColumns, column.depth),
          getColumnSearchPredicate(column)
        );
        const target = e.target as HTMLElement;
        const handlerOnHeaderIsAvailable = !column.disableHandlerOnHeader;
        if (contextMenuIsAvailable(target) && handlerOnHeaderIsAvailable) {
          e.preventDefault();
          setTableHandlerAllowedOperations(getColumnOperations(columnIndex));
          tableHandlerStateUpdate(target, column);
        }
      },
    }),
    [getColumnOperations, tableHandlerStateUpdate, contextMenuIsAvailable, tableColumns]
  );

  const defaultColumn = useMemo(
    () => ({
      Cell: (cellRef: any) => {
        const column = cellRef.column as ColumnInstance;
        if (column.isCountColumn) {
          return cellRef.value;
        } else {
          if (defaultCell) {
            return defaultCell[column.id]({ ...cellRef, rowIndex: cellRef.row.index, columnId: cellRef.column.id });
          }
          return (
            <EditableCell
              {...cellRef}
              rowIndex={cellRef.row.index}
              columnId={cellRef.column.id}
              readOnly={readOnlyCells}
            />
          );
        }
      },
    }),
    [defaultCell, readOnlyCells]
  );

  const tdProps = useCallback(
    (columnIndex: number, rowIndex: number) => ({
      onContextMenu: (e: ContextMenuEvent) => {
        const target = e.target as HTMLElement;
        if (contextMenuIsAvailable(target)) {
          e.preventDefault();
          setTableHandlerAllowedOperations([
            ...getColumnOperations(columnIndex),
            TableOperation.RowInsertAbove,
            TableOperation.RowInsertBelow,
            ...(rows.length > 1 ? [TableOperation.RowDelete] : []),
            TableOperation.RowClear,
            TableOperation.RowDuplicate,
          ]);
          tableHandlerStateUpdate(target, getColumnsAtLastLevel(tableColumns, headerLevels)[columnIndex]);
          setLastSelectedRowIndex(rowIndex);
        }
      },
    }),
    [getColumnOperations, tableHandlerStateUpdate, contextMenuIsAvailable, tableColumns, rows, headerLevels]
  );

  const tableInstance = useTable(
    {
      columns: tableColumns,
      data: rows,
      defaultColumn,
      onCellUpdate,
      onRowUpdate,
    },
    useBlockLayout,
    useResizeColumns
  );

  const onGetColumnKey = useCallback(
    (column: Column) => {
      return getColumnKey ? getColumnKey(column) : column.id!;
    },
    [getColumnKey]
  );

  const onGetRowKey = useCallback(
    (row: Row) => {
      return getRowKey ? getRowKey(row) : row.id;
    },
    [getRowKey]
  );

  return (
    <div className={`table-component ${tableId} ${tableEventUUID}`}>
      <TableComposable
        variant="compact"
        {...(tableInstance.getTableProps() as any)}
        ref={tableRef}
        ouiaId="expression-grid-table"
      >
        <TableHeader
          tableInstance={tableInstance}
          editColumnLabel={editColumnLabel}
          headerVisibility={headerVisibility}
          skipLastHeaderGroup={skipLastHeaderGroup}
          tableRows={rows}
          onRowsUpdate={onRowsUpdateCallback}
          tableColumns={tableColumns}
          getColumnKey={onGetColumnKey}
          onColumnsUpdate={onColumnsUpdateCallback}
          thProps={thProps}
          editableHeader={editableHeader}
        />
        <TableBody
          tableInstance={tableInstance}
          getRowKey={onGetRowKey}
          getColumnKey={onGetColumnKey}
          onColumnsUpdate={onColumnsUpdateCallback}
          headerVisibility={headerVisibility}
          tdProps={tdProps}
        >
          {children}
        </TableBody>
      </TableComposable>
      {showTableHandler && handlerConfiguration && (
        <TableHandler
          tableColumns={tableColumns}
          getColumnPrefix={onGetColumnPrefix}
          handlerConfiguration={handlerConfiguration}
          lastSelectedColumn={lastSelectedColumn}
          lastSelectedRowIndex={lastSelectedRowIndex}
          tableRows={tableRows}
          onRowsUpdate={onRowsUpdateCallback}
          onRowAdding={onRowAddingCallback}
          showTableHandler={showTableHandler}
          setShowTableHandler={setShowTableHandler}
          tableHandlerAllowedOperations={tableHandlerAllowedOperations}
          tableHandlerTarget={tableHandlerTarget}
          resetRowCustomFunction={resetRowCustomFunction}
          onColumnsUpdate={onColumnsUpdateCallback}
        />
      )}
    </div>
  );
};
