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
import { useCallback, useContext, useEffect, useMemo, useState } from "react";
import { DataType, TableHandlerConfiguration, TableOperation } from "../../api";
import * as _ from "lodash";
import { Column, ColumnInstance, DataRecord } from "react-table";
import { Popover } from "@patternfly/react-core";
import { TableHandlerMenu } from "./TableHandlerMenu";
import { BoxedExpressionGlobalContext } from "../../context";
import { getColumnsAtLastLevel, getColumnSearchPredicate } from "./Table";
import { DEFAULT_MIN_WIDTH } from "../Resizer";

export interface TableHandlerProps {
  /** Gets the prefix to be used for the next column name */
  getColumnPrefix: (groupType?: string) => string;
  /** Columns instance */
  tableColumns: Column[];
  /** Last selected column */
  lastSelectedColumn: ColumnInstance;
  /** Last selected row index */
  lastSelectedRowIndex: number;
  /** Rows instance */
  tableRows: React.MutableRefObject<DataRecord[]>;
  /** Function to be executed when one or more rows are modified */
  onRowsUpdate: (rows: DataRecord[], operation?: TableOperation, rowIndex?: number) => void;
  /** Function to be executed when adding a new row to the table */
  onRowAdding: () => DataRecord;
  /** Show/hide table handler */
  showTableHandler: boolean;
  /** Function to programmatically show/hide table handler */
  setShowTableHandler: React.Dispatch<React.SetStateAction<boolean>>;
  /** Target for showing the table handler  */
  tableHandlerTarget: HTMLElement;
  /** Custom configuration for the table handler */
  handlerConfiguration: TableHandlerConfiguration;
  /** Table handler allowed operations */
  tableHandlerAllowedOperations: TableOperation[];
  /** Custom function called for manually resetting a row */
  resetRowCustomFunction?: (row: DataRecord) => DataRecord;
  /** Function to be executed when columns are modified */
  onColumnsUpdate: (columns: Column[], operation?: TableOperation, columnIndex?: number) => void;
}

export const TableHandler: React.FunctionComponent<TableHandlerProps> = ({
  getColumnPrefix,
  tableColumns,
  lastSelectedColumn,
  lastSelectedRowIndex,
  tableRows,
  onRowsUpdate,
  onRowAdding,
  showTableHandler,
  setShowTableHandler,
  tableHandlerTarget,
  handlerConfiguration,
  tableHandlerAllowedOperations,
  resetRowCustomFunction = () => ({}),
  onColumnsUpdate,
}) => {
  const globalContext = useContext(BoxedExpressionGlobalContext);

  const [selectedColumn, setSelectedColumn] = useState(lastSelectedColumn.placeholderOf || lastSelectedColumn);
  const [selectedRowIndex, setSelectedRowIndex] = useState(lastSelectedRowIndex);

  useEffect(() => {
    setSelectedColumn(lastSelectedColumn.placeholderOf || lastSelectedColumn);
  }, [lastSelectedColumn]);

  useEffect(() => {
    setSelectedRowIndex(lastSelectedRowIndex);
  }, [lastSelectedRowIndex]);

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
    resetRowCustomFunction(elements[index] as DataRecord),
    ...elements.slice(index + 1),
  ];

  const generateNextAvailableColumnName: (lastIndex: number, groupType?: string) => string = useCallback(
    (lastIndex, groupType) => {
      const candidateName = `${getColumnPrefix(groupType)}${lastIndex}`;
      const columnWithCandidateName = _.find(getColumnsAtLastLevel(tableColumns), { accessor: candidateName });
      return columnWithCandidateName ? generateNextAvailableColumnName(lastIndex + 1, groupType) : candidateName;
    },
    [getColumnPrefix, tableColumns]
  );

  const getLengthOfColumnsByGroupType = useCallback((columns: Column[], groupType: string) => {
    const columnsByGroupType = _.groupBy(columns, (column: ColumnInstance) => column.groupType);
    return columnsByGroupType[groupType]?.length;
  }, []);

  const generateNextAvailableColumn = useCallback(() => {
    const groupType = selectedColumn.groupType;
    const cssClasses = selectedColumn.cssClasses;
    const columns = getColumnsAtLastLevel(tableColumns);
    const columnsLength = groupType ? getLengthOfColumnsByGroupType(columns, groupType) + 1 : columns.length;
    const nextAvailableColumnName = generateNextAvailableColumnName(columnsLength, groupType);

    return {
      accessor: nextAvailableColumnName,
      label: nextAvailableColumnName,
      ...(selectedColumn.dataType ? { dataType: DataType.Undefined } : {}),
      inlineEditable: selectedColumn.inlineEditable,
      groupType,
      cssClasses,
    } as ColumnInstance;
  }, [generateNextAvailableColumnName, getLengthOfColumnsByGroupType, selectedColumn, tableColumns]);

  /** These column operations have impact also on the collection of cells */
  const updateColumnsThenRows = useCallback(
    (operation?: TableOperation, columnIndex?: number, updatedColumns?: any) => {
      updatedColumns
        ? onColumnsUpdate([...updatedColumns], operation, columnIndex)
        : onColumnsUpdate([...tableColumns], operation, columnIndex);

      onRowsUpdate([...tableRows.current]);
    },
    [onColumnsUpdate, onRowsUpdate, tableColumns, tableRows]
  );

  const appendOnColumnChildren = useCallback(
    (operation: <T extends unknown>(elements: T[], index: number, element: T) => T[]) => {
      const children = (_.find(tableColumns, getColumnSearchPredicate(selectedColumn)) as ColumnInstance).columns;
      if (operation === insertBefore) {
        children!.unshift(generateNextAvailableColumn());
      } else if (operation === insertAfter) {
        children!.push(generateNextAvailableColumn());
      }
    },
    // eslint-disable-next-line react-hooks/exhaustive-deps
    [generateNextAvailableColumn, selectedColumn, tableColumns]
  );

  const updateTargetColumns = useCallback(
    (
      operationCallback: <T extends unknown>(elements: T[], index: number, element: T) => T[],
      operation: TableOperation
    ) => {
      if (selectedColumn.parent) {
        const parent = _.find(tableColumns, getColumnSearchPredicate(selectedColumn.parent)) as ColumnInstance;
        parent.columns = operationCallback(
          parent.columns!,
          _.findIndex(parent.columns, getColumnSearchPredicate(selectedColumn)),
          generateNextAvailableColumn()
        );
      } else {
        if (selectedColumn.appendColumnsOnChildren && _.isArray(selectedColumn.columns)) {
          appendOnColumnChildren(operationCallback);
        } else {
          const columnIndex = _.findIndex(tableColumns, getColumnSearchPredicate(selectedColumn));
          const updatedColumns = operationCallback(tableColumns, columnIndex, generateNextAvailableColumn());
          updateColumnsThenRows(operation, columnIndex, updatedColumns);
          return;
        }
      }
      updateColumnsThenRows();
    },
    [appendOnColumnChildren, generateNextAvailableColumn, selectedColumn, tableColumns, updateColumnsThenRows]
  );

  const handlingOperation = useCallback(
    (tableOperation: TableOperation) => {
      switch (tableOperation) {
        case TableOperation.ColumnInsertLeft:
          updateTargetColumns(insertBefore, TableOperation.ColumnInsertLeft);
          break;
        case TableOperation.ColumnInsertRight:
          updateTargetColumns(insertAfter, TableOperation.ColumnInsertRight);
          break;
        case TableOperation.ColumnDelete:
          updateTargetColumns(deleteAt, TableOperation.ColumnDelete);
          break;
        case TableOperation.RowInsertAbove:
          onRowsUpdate(
            insertBefore(tableRows.current, selectedRowIndex, onRowAdding()),
            TableOperation.RowInsertAbove,
            selectedRowIndex
          );
          break;
        case TableOperation.RowInsertBelow:
          onRowsUpdate(
            insertAfter(tableRows.current, selectedRowIndex, onRowAdding()),
            TableOperation.RowInsertBelow,
            selectedRowIndex
          );
          break;
        case TableOperation.RowDelete:
          onRowsUpdate(deleteAt(tableRows.current, selectedRowIndex), TableOperation.RowDelete, selectedRowIndex);
          break;
        case TableOperation.RowClear:
          onRowsUpdate(clearAt(tableRows.current, selectedRowIndex), TableOperation.RowClear, selectedRowIndex);
          break;
        case TableOperation.RowDuplicate:
          onRowsUpdate(
            duplicateAfter(tableRows.current, selectedRowIndex),
            TableOperation.RowDuplicate,
            selectedRowIndex
          );
      }
      setShowTableHandler(false);
    },
    // eslint-disable-next-line react-hooks/exhaustive-deps
    [updateTargetColumns, onRowAdding, onRowsUpdate, selectedRowIndex, setShowTableHandler, tableRows]
  );

  const getHandlerConfiguration = useMemo(() => {
    if (_.isArray(handlerConfiguration)) {
      return handlerConfiguration;
    }
    return handlerConfiguration[selectedColumn?.groupType || ""];
  }, [handlerConfiguration, selectedColumn?.groupType]);

  return (
    <Popover
      className="table-handler"
      hasNoPadding
      showClose={false}
      distance={5}
      position={"right"}
      isVisible={showTableHandler}
      shouldClose={() => setShowTableHandler(false)}
      shouldOpen={(showFunction) => showFunction?.()}
      reference={() => tableHandlerTarget}
      appendTo={globalContext.boxedExpressionEditorRef?.current ?? undefined}
      bodyContent={
        <TableHandlerMenu
          handlerConfiguration={getHandlerConfiguration}
          allowedOperations={tableHandlerAllowedOperations}
          onOperation={handlingOperation}
        />
      }
    />
  );
};
