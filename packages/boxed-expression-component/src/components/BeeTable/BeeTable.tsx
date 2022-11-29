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
import { TableComposable } from "@patternfly/react-table";
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
import { BeeEditableCell } from "./BeeEditableCell";
import "./BeeTable.css";
import { BeeTableBody } from "./BeeTableBody";
import { BeeTableHandler } from "./BeeTableHandler";
import { BeeTableHeader } from "./BEeTableHeader";

export const NO_TABLE_CONTEXT_MENU_CLASS = "no-table-context-menu";

const NUMBER_OF_ROWS_COLUMN = "#";
const NUMBER_OF_ROWS_SUBCOLUMN = "0";

export const DEFAULT_ON_ROW_ADDING = () => ({});

export const getColumnsAtLastLevel: (
  columns: ReactTable.ColumnInstance[] | ReactTable.Column[],
  depth?: number
) => ReactTable.ColumnInstance[] = (columns, depth = 0) =>
  _.flatMap(columns, (column: ReactTable.ColumnInstance) => {
    if (_.has(column, "columns")) {
      return depth > 0 ? getColumnsAtLastLevel(column.columns!, depth - 1) : column.columns;
    }
    return column;
  }) as ReactTable.ColumnInstance[];

export const getColumnSearchPredicate: (
  column: ReactTable.ColumnInstance
) => (columnToCompare: ReactTable.ColumnInstance) => boolean = (column) => {
  const columnId = column.originalId || column.id || column.accessor;
  return (columnToCompare: ReactTable.ColumnInstance) => {
    return columnToCompare.id === columnId || columnToCompare.accessor === columnId;
  };
};

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
    return focusPrevCellByArrowKey(currentTarget, rowSpan);
  }
  if (NavigationKeysUtils.isArrowRight(key)) {
    return focusNextCellByArrowKey(currentTarget, rowSpan);
  }
  if (NavigationKeysUtils.isArrowUp(key)) {
    return focusUpperCell(currentTarget);
  }

  return focusLowerCell(currentTarget);
};

export const BeeTable: React.FunctionComponent<BeeTableProps> = ({
  tableId,
  children,
  getColumnPrefix,
  editColumnLabel,
  editableHeader = true,
  onColumnsUpdate,
  onRowsUpdate,
  onRowAdding,
  controllerCell = NUMBER_OF_ROWS_COLUMN,
  defaultCellByColumnId,
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
  enableKeyboardNavigation = true,
}: BeeTableProps) => {
  const tableComposableRef = useRef<HTMLTableElement>(null);
  const tableEventUUID = useMemo(() => `table-event-${uuid()}`, []);
  const boxedExpressionEditor = useBoxedExpressionEditor();

  const onRowAddingCallback = useCallback(() => {
    return onRowAdding?.() ?? {};
  }, [onRowAdding]);

  const onGetColumnPrefix = useCallback(
    (groupType?: string) => getColumnPrefix?.(groupType) ?? "column-",
    [getColumnPrefix]
  );

  const generateNumberOfRowsSubColumnRecursively: (column: ReactTable.ColumnInstance, headerLevels: number) => void =
    useCallback(
      (column, headerLevels) => {
        if (headerLevels > 0) {
          _.assign(column, {
            columns: [
              {
                label: headerVisibility === BeeTableHeaderVisibility.Full ? NUMBER_OF_ROWS_SUBCOLUMN : controllerCell,
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
    (currentControllerCell: string | JSX.Element, columns: ReactTable.Column[]) => {
      const numberOfRowsColumn = {
        label: currentControllerCell,
        accessor: NUMBER_OF_ROWS_COLUMN,
        width: 60,
        minWidth: 60,
        isCountColumn: true,
      } as ReactTable.ColumnInstance;
      generateNumberOfRowsSubColumnRecursively(numberOfRowsColumn, headerLevels);
      return [numberOfRowsColumn, ...columns];
    },
    [generateNumberOfRowsSubColumnRecursively, headerLevels]
  );

  const evaluateRows = useCallback((rows: ReactTable.DataRecord[]) => {
    return _.map(rows, (row) => {
      if (_.isEmpty(row.id)) {
        row.id = generateUuid();
      }
      return row;
    });
  }, []);

  const tableRows = useRef<ReactTable.DataRecord[]>(evaluateRows(rows));
  const [showTableHandler, setShowTableHandler] = useState(false);
  const [tableHandlerTarget, setTableHandlerTarget] = useState<HTMLElement>(boxedExpressionEditor.editorRef.current!);
  const [tableHandlerAllowedOperations, setTableHandlerAllowedOperations] = useState(
    _.values(BeeTableOperation).map((operation) => parseInt(operation.toString()))
  );
  const [lastSelectedColumn, setLastSelectedColumn] = useState({} as ReactTable.ColumnInstance);
  const [lastSelectedRowIndex, setLastSelectedRowIndex] = useState(-1);

  const reactTableColumns = useMemo(
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
        onRowsUpdate?.({ rows: pastedRows, columns });
      }
    }

    boxedExpressionEditor.editorRef.current?.addEventListener(tableEventUUID, listener);
    return () => {
      boxedExpressionEditor.editorRef.current?.removeEventListener(tableEventUUID, listener);
    };
  }, [
    tableEventUUID,
    tableRows,
    onRowsUpdate,
    onColumnsUpdate,
    onRowAddingCallback,
    columns,
    boxedExpressionEditor.editorRef,
  ]);

  const onColumnsUpdateCallback = useCallback(
    (columns: ReactTable.Column[], operation?: BeeTableOperation, columnIndex?: number) => {
      //Removing "# of rows" column
      onColumnsUpdate?.({ columns: columns.slice(1), operation, columnIndex: (columnIndex ?? 1) - 1 });
    },
    [onColumnsUpdate]
  );

  const onRowsUpdateCallback = useCallback(
    (rows: ReactTable.DataRecord[], operation?: BeeTableOperation, rowIndex?: number) => {
      tableRows.current = rows;
      onRowsUpdate?.({ rows: [...rows], operation, rowIndex, columns });
    },
    [onRowsUpdate, columns]
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
    (rowIndex: number, updatedRow: ReactTable.DataRecord) => {
      const updatedRows = [...tableRows.current];
      updatedRows[rowIndex] = updatedRow;
      onRowsUpdateCallback(updatedRows);
    },
    [onRowsUpdateCallback]
  );

  const contextMenuIsAvailable = useCallback((target: HTMLElement) => {
    const targetIsContainedInCurrentTable = target.closest("table") === tableComposableRef.current;
    const contextMenuAvailableForTarget = !target.classList.contains(NO_TABLE_CONTEXT_MENU_CLASS);
    return targetIsContainedInCurrentTable && contextMenuAvailableForTarget;
  }, []);

  const tableHandlerStateUpdate = useCallback(
    (target: HTMLElement, column: ReactTable.ColumnInstance) => {
      setTableHandlerTarget(target);
      boxedExpressionEditor.currentlyOpenedHandlerCallback?.(false);
      setShowTableHandler(true);
      boxedExpressionEditor.setContextMenuOpen(true);
      boxedExpressionEditor.setCurrentlyOpenedHandlerCallback?.(() => setShowTableHandler);
      setLastSelectedColumn(column);
    },
    [boxedExpressionEditor]
  );

  const getColumnOperations = useCallback(
    (columnIndex: number) => {
      const columnsAtLastLevel = getColumnsAtLastLevel(reactTableColumns);
      const groupTypeForCurrentColumn = (columnsAtLastLevel[columnIndex] as ReactTable.ColumnInstance)?.groupType;
      const columnsByGroupType = _.groupBy(columnsAtLastLevel, (column: ReactTable.ColumnInstance) => column.groupType);
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

  const thProps = useCallback(
    (column: ReactTable.ColumnInstance) => ({
      onContextMenu: (e: ReactTable.ContextMenuEvent) => {
        const columnIndex = _.findIndex(
          getColumnsAtLastLevel(reactTableColumns, column.depth),
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
    [getColumnOperations, tableHandlerStateUpdate, contextMenuIsAvailable, reactTableColumns]
  );

  const defaultColumn: Partial<ReactTable.Column> = useMemo(
    () => ({
      Cell: (cellProps) => {
        if (cellProps.column.isCountColumn) {
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
            <BeeEditableCell
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

  const tdProps = useCallback(
    (columnIndex: number, rowIndex: number) => ({
      onContextMenu: (e: ReactTable.ContextMenuEvent) => {
        const target = e.target as HTMLElement;
        if (contextMenuIsAvailable(target)) {
          e.preventDefault();
          setTableHandlerAllowedOperations([
            ...getColumnOperations(columnIndex),
            BeeTableOperation.RowInsertAbove,
            BeeTableOperation.RowInsertBelow,
            ...(rows.length > 1 ? [BeeTableOperation.RowDelete] : []),
            BeeTableOperation.RowClear,
            BeeTableOperation.RowDuplicate,
          ]);
          tableHandlerStateUpdate(target, getColumnsAtLastLevel(reactTableColumns, headerLevels)[columnIndex]);
          setLastSelectedRowIndex(rowIndex);

          focusCurrentCell(target);
        }
      },
    }),
    [getColumnOperations, tableHandlerStateUpdate, contextMenuIsAvailable, reactTableColumns, rows, headerLevels]
  );

  const reactTableInstance = ReactTable.useTable(
    {
      columns: reactTableColumns,
      data: rows,
      defaultColumn,
      onCellUpdate,
      onRowUpdate,
    },
    ReactTable.useBlockLayout,
    ReactTable.useResizeColumns
  );

  const onGetColumnKey = useCallback(
    (column: ReactTable.ColumnInstance) => {
      const columnId = column.originalId || column.id;
      return getColumnKey ? getColumnKey(column) : columnId;
    },
    [getColumnKey]
  );

  const onGetRowKey = useCallback(
    (row: ReactTable.Row) => {
      if (getRowKey) {
        return getRowKey(row);
      } else {
        if (row.original) {
          return (row.original as ReactTable.Row).id;
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
      <TableComposable
        {...reactTableInstance.getTableProps()}
        variant="compact"
        ref={tableComposableRef}
        ouiaId="expression-grid-table"
      >
        <BeeTableHeader
          editColumnLabel={editColumnLabel}
          editableHeader={editableHeader}
          getColumnKey={onGetColumnKey}
          headerVisibility={headerVisibility}
          onCellKeyDown={onCellKeyDown}
          onColumnsUpdate={onColumnsUpdateCallback}
          skipLastHeaderGroup={skipLastHeaderGroup}
          tableColumns={reactTableColumns}
          reactTableInstance={reactTableInstance}
          thProps={thProps}
        />
        <BeeTableBody
          getColumnKey={onGetColumnKey}
          getRowKey={onGetRowKey}
          headerVisibility={headerVisibility}
          onCellKeyDown={onCellKeyDown}
          onColumnsUpdate={onColumnsUpdateCallback}
          skipLastHeaderGroup={skipLastHeaderGroup}
          tableInstance={reactTableInstance}
          tdProps={tdProps}
        >
          {children}
        </BeeTableBody>
      </TableComposable>
      {showTableHandler && handlerConfiguration && (
        <BeeTableHandler
          tableColumns={reactTableColumns}
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
