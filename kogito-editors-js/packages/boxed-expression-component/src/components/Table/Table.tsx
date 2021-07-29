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

import "./Table.css";
import {
  Column,
  ColumnInstance,
  ContextMenuEvent,
  DataRecord,
  useBlockLayout,
  useResizeColumns,
  useTable,
} from "react-table";
import { TableComposable } from "@patternfly/react-table";
import * as React from "react";
import { useCallback, useContext, useEffect, useRef, useState } from "react";
import { EditableCell } from "./EditableCell";
import { TableHeaderVisibility, TableOperation, TableProps } from "../../api";
import * as _ from "lodash";
import { TableBody } from "./TableBody";
import { TableHandler } from "./TableHandler";
import { TableHeader } from "./TableHeader";
import { BoxedExpressionGlobalContext } from "../../context";

export const NO_TABLE_CONTEXT_MENU_CLASS = "no-table-context-menu";
const NUMBER_OF_ROWS_COLUMN = "#";
const NUMBER_OF_ROWS_SUBCOLUMN = "0";

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
  getColumnPrefix = () => "column-",
  editColumnLabel,
  onColumnsUpdate,
  onRowsUpdate,
  onRowAdding = () => ({}),
  controllerCell = NUMBER_OF_ROWS_COLUMN,
  defaultCell,
  rows,
  columns,
  handlerConfiguration,
  headerVisibility,
  headerLevels = 0,
  skipLastHeaderGroup = false,
  getRowKey = (row) => row.id as string,
  getColumnKey = (column) => column.id as string,
  resetRowCustomFunction,
}: TableProps) => {
  const tableRef = useRef<HTMLTableElement>(null);

  const globalContext = useContext(BoxedExpressionGlobalContext);

  const [currentControllerCell, setCurrentControllerCell] = useState(controllerCell);

  const generateNumberOfRowsSubColumnRecursively: (column: ColumnInstance, headerLevels: number) => void = useCallback(
    (column, headerLevels) => {
      if (headerLevels > 0) {
        _.assign(column, {
          columns: [
            {
              label: headerVisibility === TableHeaderVisibility.Full ? NUMBER_OF_ROWS_SUBCOLUMN : currentControllerCell,
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
    [currentControllerCell, headerVisibility]
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

  const tableColumns = useRef<Column[]>(generateNumberOfRowsColumn(currentControllerCell, columns));
  const tableRows = useRef<DataRecord[]>(rows);
  const [showTableHandler, setShowTableHandler] = useState(false);
  const [tableHandlerTarget, setTableHandlerTarget] = useState(document.body);
  const [tableHandlerAllowedOperations, setTableHandlerAllowedOperations] = useState(
    _.values(TableOperation).map((operation) => parseInt(operation.toString()))
  );
  const [lastSelectedColumn, setLastSelectedColumn] = useState({} as ColumnInstance);
  const [lastSelectedRowIndex, setLastSelectedRowIndex] = useState(-1);

  useEffect(() => {
    tableColumns.current = generateNumberOfRowsColumn(controllerCell, tableColumns.current.slice(1));
    setCurrentControllerCell(controllerCell);
  }, [controllerCell, generateNumberOfRowsColumn]);

  useEffect(() => {
    tableColumns.current = generateNumberOfRowsColumn(currentControllerCell, columns);
    // Watching for external changes of the columns
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [columns]);

  useEffect(() => {
    tableRows.current = rows;
    // Watching for external changes of the rows
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [rows]);

  const onColumnsUpdateCallback = useCallback(
    (columns: Column[]) => {
      tableColumns.current = columns;
      onColumnsUpdate?.(columns.slice(1)); //Removing "# of rows" column
    },
    [onColumnsUpdate]
  );

  const onRowsUpdateCallback = useCallback(
    (rows: DataRecord[]) => {
      tableRows.current = rows;
      onRowsUpdate?.(rows);
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

  const defaultColumn = {
    Cell: useCallback((cellRef) => {
      const column = cellRef.column as ColumnInstance;
      if (column.isCountColumn) {
        return cellRef.value;
      } else {
        return defaultCell ? defaultCell[column.id](cellRef) : EditableCell(cellRef);
      }
      // Table performance optimization: no need to re-render cells, since nested component themselves will re-render
      // eslint-disable-next-line react-hooks/exhaustive-deps
    }, []),
  };

  const contextMenuIsAvailable = (target: HTMLElement) => {
    const targetIsContainedInCurrentTable = target.closest("table") === tableRef.current;
    const contextMenuAvailableForTarget = !target.classList.contains(NO_TABLE_CONTEXT_MENU_CLASS);
    return targetIsContainedInCurrentTable && contextMenuAvailableForTarget;
  };

  const tableHandlerStateUpdate = (target: HTMLElement, column: ColumnInstance) => {
    setTableHandlerTarget(target);
    globalContext.currentlyOpenedHandlerCallback?.(false);
    setShowTableHandler(true);
    globalContext.setCurrentlyOpenedHandlerCallback?.(() => setShowTableHandler);
    setLastSelectedColumn(column);
  };

  const atLeastTwoColumnsOfTheSameGroupType = (columnIndex: number) => {
    const columnsAtLastLevel = getColumnsAtLastLevel(tableColumns.current);
    const groupTypeForCurrentColumn = (columnsAtLastLevel[columnIndex] as ColumnInstance)?.groupType;
    const columnsByGroupType = _.groupBy(columnsAtLastLevel, (column: ColumnInstance) => column.groupType);
    return groupTypeForCurrentColumn
      ? columnsByGroupType[groupTypeForCurrentColumn].length > 1
      : tableColumns.current.length > 2; // The total number of columns is counting also the # of rows column
  };

  const columnCanBeDeleted = (columnIndex: number) => {
    return columnIndex > 0 && atLeastTwoColumnsOfTheSameGroupType(columnIndex);
  };

  const getColumnOperations = (columnIndex: number) =>
    columnIndex === 0
      ? []
      : [
          TableOperation.ColumnInsertLeft,
          TableOperation.ColumnInsertRight,
          ...(columnCanBeDeleted(columnIndex) ? [TableOperation.ColumnDelete] : []),
        ];

  const getThProps = (column: ColumnInstance) => ({
    onContextMenu: (e: ContextMenuEvent) => {
      const columnIndex = _.findIndex(
        getColumnsAtLastLevel(tableColumns.current, column.depth),
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
  });

  const getTdProps = (columnIndex: number, rowIndex: number) => ({
    onContextMenu: (e: ContextMenuEvent) => {
      const target = e.target as HTMLElement;
      if (contextMenuIsAvailable(target)) {
        e.preventDefault();
        setTableHandlerAllowedOperations([
          ...getColumnOperations(columnIndex),
          TableOperation.RowInsertAbove,
          TableOperation.RowInsertBelow,
          ...(tableRows.current.length > 1 ? [TableOperation.RowDelete] : []),
          TableOperation.RowClear,
          TableOperation.RowDuplicate,
        ]);
        tableHandlerStateUpdate(target, getColumnsAtLastLevel(tableInstance.columns, headerLevels)[columnIndex]);
        setLastSelectedRowIndex(rowIndex);
      }
    },
  });

  const tableInstance = useTable(
    {
      columns: tableColumns.current,
      data: tableRows.current,
      defaultColumn,
      onCellUpdate,
      onRowUpdate,
      getThProps,
      getTdProps,
    },
    useBlockLayout,
    useResizeColumns
  );

  return (
    <div className={`table-component ${tableId}`}>
      <TableComposable variant="compact" {...tableInstance.getTableProps()} ref={tableRef}>
        <TableHeader
          tableInstance={tableInstance}
          editColumnLabel={editColumnLabel}
          headerVisibility={headerVisibility}
          skipLastHeaderGroup={skipLastHeaderGroup}
          tableRows={tableRows}
          onRowsUpdate={onRowsUpdateCallback}
          tableColumns={tableColumns}
          getColumnKey={getColumnKey}
          onColumnsUpdate={onColumnsUpdateCallback}
        />
        <TableBody
          tableInstance={tableInstance}
          getRowKey={getRowKey}
          getColumnKey={getColumnKey}
          onColumnsUpdate={onColumnsUpdateCallback}
          headerVisibility={headerVisibility}
        >
          {children}
        </TableBody>
      </TableComposable>
      {showTableHandler && handlerConfiguration ? (
        <TableHandler
          tableColumns={tableColumns}
          getColumnPrefix={getColumnPrefix}
          handlerConfiguration={handlerConfiguration}
          lastSelectedColumn={lastSelectedColumn}
          lastSelectedRowIndex={lastSelectedRowIndex}
          tableRows={tableRows}
          onRowsUpdate={onRowsUpdateCallback}
          onRowAdding={onRowAdding}
          showTableHandler={showTableHandler}
          setShowTableHandler={setShowTableHandler}
          tableHandlerAllowedOperations={tableHandlerAllowedOperations}
          tableHandlerTarget={tableHandlerTarget}
          resetRowCustomFunction={resetRowCustomFunction}
          onColumnsUpdate={onColumnsUpdateCallback}
        />
      ) : null}
    </div>
  );
};
