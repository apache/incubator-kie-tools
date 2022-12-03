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

import * as React from "react";
import { useCallback, useEffect, useMemo, useState } from "react";
import { DmnBuiltInDataType, generateUuid, BeeTableOperationHandlerConfig, BeeTableOperation } from "../../api";
import * as _ from "lodash";
import * as ReactTable from "react-table";
import { Popover } from "@patternfly/react-core";
import { BeeTableOperationHandlerMenu } from "./BeeTableOperationHandlerMenu";
import { useBoxedExpressionEditor } from "../BoxedExpressionEditor/BoxedExpressionEditorContext";
import { getColumnsAtLastLevel, areEqualColumns } from "./BeeTable";
import { DEFAULT_MIN_WIDTH } from "../Resizer";

export interface BeeTableOperationHandlerProps<R extends object> {
  /** Gets the prefix to be used for the next column name */
  getNewColumnIdPrefix: (groupType?: string) => string;
  /** Columns instance */
  tableColumns: ReactTable.Column<R>[];
  /** Last selected column */
  lastSelectedColumn: ReactTable.ColumnInstance<R> | undefined;
  /** Last selected row index */
  lastSelectedRowIndex: number;
  /** Rows instance */
  tableRows: React.MutableRefObject<R[]>;
  /** Function to be executed when one or more rows are modified */
  onRowsUpdate: (rows: R[], operation?: BeeTableOperation, rowIndex?: number) => void;
  /** Function to be executed when adding a new row to the table */
  onNewRow: (() => R) | undefined;
  /** Show/hide table handler */
  showTableOperationHandler: boolean;
  /** Function to programmatically show/hide table handler */
  setShowTableOperationHandler: React.Dispatch<React.SetStateAction<boolean>>;
  /** Target for showing the table handler  */
  operationHandlerTarget: HTMLElement;
  /** Custom configuration for the table handler */
  operationHandlerConfig: BeeTableOperationHandlerConfig;
  /** Table handler allowed operations */
  allowedOperations: BeeTableOperation[];
  /** Custom function called for manually resetting a row */
  resetRowCustomFunction?: (row: R) => R;
  /** Function to be executed when columns are modified */
  onColumnsUpdate: (columns: ReactTable.Column<R>[], operation?: BeeTableOperation, columnIndex?: number) => void;
}

export function BeeTableOperationHandler<R extends object>({
  getNewColumnIdPrefix,
  tableColumns,
  lastSelectedColumn,
  lastSelectedRowIndex,
  tableRows,
  onRowsUpdate,
  onNewRow,
  showTableOperationHandler,
  setShowTableOperationHandler,
  operationHandlerTarget,
  operationHandlerConfig,
  allowedOperations,
  resetRowCustomFunction = (r) => r,
  onColumnsUpdate,
}: BeeTableOperationHandlerProps<R>) {
  const boxedExpressionEditor = useBoxedExpressionEditor();

  const [selectedColumn, setSelectedColumn] = useState<ReactTable.ColumnInstance<R> | undefined>(
    lastSelectedColumn?.placeholderOf ?? lastSelectedColumn
  );
  const [selectedRowIndex, setSelectedRowIndex] = useState(lastSelectedRowIndex);

  useEffect(() => {
    setSelectedColumn(lastSelectedColumn?.placeholderOf ?? lastSelectedColumn);
  }, [lastSelectedColumn]);

  useEffect(() => {
    setSelectedRowIndex(lastSelectedRowIndex);
  }, [lastSelectedRowIndex]);

  // FIXME: Tiago -> Bad typing.
  const withDefaultValues = <T extends unknown>(element: T) => ({
    width: DEFAULT_MIN_WIDTH,
    ...(element as any),
  });

  const insertBefore = <T extends unknown>(elements: T[], index: number, element: T) => [
    ...elements.slice(0, index),
    withDefaultValues(element),
    ...elements.slice(index),
  ];

  const insertAfter = <T extends unknown>(elements: T[], index: number, element: T) => [
    ...elements.slice(0, index + 1),
    withDefaultValues(element),
    ...elements.slice(index + 1),
  ];

  const duplicateAfter = <T extends unknown>(elements: T[], index: number) => [
    ...elements.slice(0, index + 1),
    _.cloneDeep(elements[index]),
    ...elements.slice(index + 1),
  ];

  const deleteAt = <T extends unknown>(elements: T[], index: number) => [
    ...elements.slice(0, index),
    ...elements.slice(index + 1),
  ];

  const clearAt = <T extends unknown>(elements: T[], index: number) => [
    ...elements.slice(0, index),
    // FIXME: Tiago -> Bad typing.
    resetRowCustomFunction(elements[index] as unknown as R),
    ...elements.slice(index + 1),
  ];

  const generateNextAvailableColumnName: (lastIndex: number, groupType?: string) => string = useCallback(
    (lastIndex, groupType) => {
      const candidateName = `${getNewColumnIdPrefix(groupType)}${lastIndex}`;
      const columnWithCandidateName = _.find(getColumnsAtLastLevel(tableColumns), { label: candidateName });
      return columnWithCandidateName ? generateNextAvailableColumnName(lastIndex + 1, groupType) : candidateName;
    },
    [getNewColumnIdPrefix, tableColumns]
  );

  const getLengthOfColumnsByGroupType = useCallback((columns: ReactTable.Column<R>[], groupType: string) => {
    const columnsByGroupType = _.groupBy(columns, (column) => column.groupType);
    return columnsByGroupType[groupType]?.length;
  }, []);

  const generateNextAvailableColumn = useCallback((): ReactTable.Column<R> => {
    const groupType = selectedColumn?.groupType;
    const cssClasses = selectedColumn?.cssClasses;
    const columns = getColumnsAtLastLevel(tableColumns);
    const columnsLength = groupType ? getLengthOfColumnsByGroupType(columns, groupType) + 1 : columns.length;
    const nextAvailableColumnName = generateNextAvailableColumnName(columnsLength, groupType);

    return {
      accessor: generateUuid() as any,
      label: nextAvailableColumnName,
      dataType: selectedColumn?.dataType ?? DmnBuiltInDataType.Undefined,
      inlineEditable: selectedColumn?.inlineEditable,
      groupType,
      cssClasses,
      isRowIndexColumn: false,
    };
  }, [generateNextAvailableColumnName, getLengthOfColumnsByGroupType, selectedColumn, tableColumns]);

  /** These column operations have impact also on the collection of cells */
  const updateColumnsThenRows = useCallback(
    (operation?: BeeTableOperation, columnIndex?: number, updatedColumns?: ReactTable.Column<R>[]) => {
      if (updatedColumns) {
        onColumnsUpdate([...updatedColumns], operation, columnIndex);
      } else {
        onColumnsUpdate([...tableColumns], operation, columnIndex);
      }
      onRowsUpdate([...tableRows.current]);
    },
    [onColumnsUpdate, onRowsUpdate, tableColumns, tableRows]
  );

  const appendOnColumnChildren = useCallback(
    (operation: <T extends unknown>(elements: T[], index: number, element: T) => T[]) => {
      const children = tableColumns.find(areEqualColumns(selectedColumn))?.columns;
      if (operation === insertBefore) {
        children?.unshift(generateNextAvailableColumn());
      } else if (operation === insertAfter) {
        children?.push(generateNextAvailableColumn());
      }
    },
    // eslint-disable-next-line react-hooks/exhaustive-deps
    [generateNextAvailableColumn, selectedColumn, tableColumns]
  );

  const updateTargetColumns = useCallback(
    (
      operationCallback: <T extends unknown>(elements: T[], index: number, element: T) => T[],
      operation: BeeTableOperation
    ) => {
      if (selectedColumn?.parent) {
        const parent = _.find(tableColumns, areEqualColumns(selectedColumn.parent)) as ReactTable.Column<R>;
        parent.columns = operationCallback(
          parent.columns!,
          _.findIndex(parent.columns, areEqualColumns(selectedColumn)),
          generateNextAvailableColumn()
        );
      } else {
        if (selectedColumn?.appendColumnsOnChildren && _.isArray(selectedColumn.columns)) {
          appendOnColumnChildren(operationCallback);
        } else {
          let columnIndex = -1;
          for (const column of tableColumns) {
            const foundIndex = column.columns?.findIndex(areEqualColumns(selectedColumn));
            if (column.columns && foundIndex !== undefined && foundIndex !== -1) {
              column.columns = operationCallback(column.columns!, foundIndex, generateNextAvailableColumn());
              columnIndex = foundIndex;
              break;
            }
          }
          if (columnIndex !== -1) {
            updateColumnsThenRows(operation, columnIndex, tableColumns);
          } else {
            const columnIndex = _.findIndex(tableColumns, areEqualColumns(selectedColumn));
            const updatedColumns = operationCallback(tableColumns, columnIndex, generateNextAvailableColumn());
            updateColumnsThenRows(operation, columnIndex, updatedColumns);
          }
          return;
        }
      }
      updateColumnsThenRows();
    },
    [appendOnColumnChildren, generateNextAvailableColumn, selectedColumn, tableColumns, updateColumnsThenRows]
  );

  const generateRow = useCallback(() => {
    // FIXME: Tiago -> Bad typing
    const row: any = onNewRow?.() ?? ({} as R);
    if (_.isEmpty(row.id)) {
      row.id = generateUuid();
    }
    return row;
  }, [onNewRow]);

  const handleOperation = useCallback(
    (operation: BeeTableOperation) => {
      boxedExpressionEditor.beeGwtService?.notifyUserAction();
      switch (operation) {
        case BeeTableOperation.ColumnInsertLeft:
          updateTargetColumns(insertBefore, BeeTableOperation.ColumnInsertLeft);
          break;
        case BeeTableOperation.ColumnInsertRight:
          updateTargetColumns(insertAfter, BeeTableOperation.ColumnInsertRight);
          break;
        case BeeTableOperation.ColumnDelete:
          updateTargetColumns(deleteAt, BeeTableOperation.ColumnDelete);
          break;
        case BeeTableOperation.RowInsertAbove:
          onRowsUpdate(
            insertBefore(tableRows.current, selectedRowIndex, generateRow()),
            BeeTableOperation.RowInsertAbove,
            selectedRowIndex
          );
          break;
        case BeeTableOperation.RowInsertBelow:
          onRowsUpdate(
            insertAfter(tableRows.current, selectedRowIndex, generateRow()),
            BeeTableOperation.RowInsertBelow,
            selectedRowIndex
          );
          break;
        case BeeTableOperation.RowDelete:
          onRowsUpdate(deleteAt(tableRows.current, selectedRowIndex), BeeTableOperation.RowDelete, selectedRowIndex);
          break;
        case BeeTableOperation.RowClear:
          onRowsUpdate(clearAt(tableRows.current, selectedRowIndex), BeeTableOperation.RowClear, selectedRowIndex);
          break;
        case BeeTableOperation.RowDuplicate:
          onRowsUpdate(
            duplicateAfter(tableRows.current, selectedRowIndex),
            BeeTableOperation.RowDuplicate,
            selectedRowIndex
          );
      }
      setShowTableOperationHandler(false);
      boxedExpressionEditor.setContextMenuOpen(false);
    },
    // eslint-disable-next-line react-hooks/exhaustive-deps
    [updateTargetColumns, generateRow, onRowsUpdate, selectedRowIndex, setShowTableOperationHandler, tableRows]
  );

  const filteredOperationHandlerConfig = useMemo(() => {
    if (_.isArray(operationHandlerConfig)) {
      return operationHandlerConfig;
    }
    return operationHandlerConfig[selectedColumn?.groupType || ""];
  }, [operationHandlerConfig, selectedColumn?.groupType]);

  return (
    <Popover
      className="table-handler"
      hasNoPadding
      showClose={false}
      distance={5}
      position={"right"}
      isVisible={showTableOperationHandler}
      shouldClose={() => {
        setShowTableOperationHandler(false);
        boxedExpressionEditor.setContextMenuOpen(false);
      }}
      shouldOpen={(showFunction) => showFunction?.()}
      reference={() => operationHandlerTarget}
      appendTo={boxedExpressionEditor.editorRef?.current ?? undefined}
      bodyContent={
        <BeeTableOperationHandlerMenu
          operationHandlerConfig={filteredOperationHandlerConfig}
          allowedOperations={allowedOperations}
          onOperation={handleOperation}
        />
      }
    />
  );
}
