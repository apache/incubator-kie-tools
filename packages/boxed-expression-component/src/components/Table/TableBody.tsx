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
import { BaseSyntheticEvent, useCallback, useMemo } from "react";
import { Tbody, Tr } from "@patternfly/react-table";
import { TableHeaderVisibility } from "../../api";
import { Cell, Column, HeaderGroup, Row, TableInstance } from "react-table";
import { useBoxedExpression } from "../../context";
import { LOGIC_TYPE_SELECTOR_CLASS } from "../LogicTypeSelector";
import { TdAdditiveCell } from "./TdAdditiveCell";
import { TdCell } from "./TdCell";

export interface TableBodyProps {
  /** Table instance */
  tableInstance: TableInstance;
  /** The way in which the header will be rendered */
  headerVisibility?: TableHeaderVisibility;
  /** True, for skipping the creation in the DOM of the last defined header group */
  skipLastHeaderGroup: boolean;
  /** Optional children element to be appended below the table content */
  children?: React.ReactElement[];
  /** Custom function for getting row key prop, and avoid using the row index */
  getRowKey: (row: Row) => string;
  /** Custom function for getting column key prop, and avoid using the column index */
  getColumnKey: (column: Column) => string;
  /** Function to be executed when columns are modified */
  onColumnsUpdate?: (columns: Column[]) => void;
  /** Function to be executed when a key has been pressed on a cell */
  onCellKeyDown: () => (e: KeyboardEvent) => void;
  /** Td props */
  tdProps: (cellIndex: number, rowIndex: number) => any;
}

export const TableBody: React.FunctionComponent<TableBodyProps> = ({
  tableInstance,
  children,
  headerVisibility = TableHeaderVisibility.Full,
  skipLastHeaderGroup,
  getRowKey,
  getColumnKey,
  onColumnsUpdate,
  onCellKeyDown,
  tdProps,
}) => {
  const { boxedExpressionEditorGWTService } = useBoxedExpression();

  const headerVisibilityMemo = useMemo(() => headerVisibility ?? TableHeaderVisibility.Full, [headerVisibility]);

  const headerRowsLength = useMemo(() => {
    const headerGroupsLength = tableInstance.headerGroups.length - (skipLastHeaderGroup ? 1 : 0);
    switch (headerVisibility) {
      case TableHeaderVisibility.Full:
        return headerGroupsLength;
      case TableHeaderVisibility.LastLevel:
        return headerGroupsLength - 1;
      case TableHeaderVisibility.SecondToLastLevel:
        return headerGroupsLength - 1;
      default:
        return 0;
    }
  }, [headerVisibility, tableInstance]);

  const renderCell = useCallback(
    (cellIndex: number, cell: Cell, rowIndex: number, inAForm: boolean) => {
      return (
        <TdCell
          key={cellIndex}
          cellIndex={cellIndex}
          cell={cell}
          rowIndex={rowIndex}
          inAForm={inAForm}
          onKeyDown={onCellKeyDown}
          tableInstance={tableInstance}
          getColumnKey={getColumnKey}
          onColumnsUpdate={onColumnsUpdate!}
          tdProps={tdProps}
          yPosition={headerRowsLength + rowIndex}
        />
      );
    },
    [getColumnKey, onColumnsUpdate, tableInstance, tdProps, onCellKeyDown, headerRowsLength]
  );

  const eventPathHasNestedExpression = useCallback((event: React.BaseSyntheticEvent, path: EventTarget[]) => {
    let currentPathTarget: EventTarget = event.target;
    let currentIndex = 0;
    while (currentPathTarget !== event.currentTarget && currentIndex < path.length) {
      currentIndex++;
      currentPathTarget = path[currentIndex];
      if ((currentPathTarget as HTMLElement)?.classList?.contains(LOGIC_TYPE_SELECTOR_CLASS)) {
        return true;
      }
    }
    return false;
  }, []);

  const onRowClick = useCallback(
    (rowKey: string) => (event: BaseSyntheticEvent) => {
      const nativeEvent = event.nativeEvent as Event;
      const path: EventTarget[] = nativeEvent?.composedPath?.() || [];
      if (!eventPathHasNestedExpression(event, path)) {
        boxedExpressionEditorGWTService?.selectObject(rowKey);
      }
    },
    [boxedExpressionEditorGWTService, eventPathHasNestedExpression]
  );

  const renderBodyRow = useCallback(
    (row: Row, rowIndex: number) => {
      tableInstance.prepareRow(row);
      const rowProps = { ...row.getRowProps(), style: {} };
      const RowDelegate = (row.original as any).rowDelegate;
      const rowKey = getRowKey(row);
      const rowClassNames = `${rowKey} table-row`;

      const buildTableRow = (inAForm: boolean) => (
        <Tr
          className={rowClassNames}
          {...rowProps}
          ouiaId={"expression-row-" + rowIndex}
          key={rowKey}
          onClick={onRowClick(rowKey)}
        >
          {row.cells.map((cell: Cell, cellIndex: number) => renderCell(cellIndex, cell, rowIndex, inAForm))}
        </Tr>
      );

      return (
        <React.Fragment key={rowKey}>
          {RowDelegate ? <RowDelegate>{buildTableRow(true)}</RowDelegate> : buildTableRow(false)}
        </React.Fragment>
      );
    },
    [getRowKey, onRowClick, renderCell, tableInstance]
  );

  const renderAdditiveRow = useCallback(
    (rowIndex: number) => (
      <Tr className="table-row additive-row">
        <TdAdditiveCell
          isEmptyCell={true}
          rowIndex={rowIndex}
          cellIndex={0}
          onKeyDown={onCellKeyDown}
          xPosition={0}
          yPosition={headerRowsLength + rowIndex}
        />
        {children?.map((child, childIndex) => {
          return (
            <TdAdditiveCell
              key={childIndex}
              cellIndex={childIndex}
              isEmptyCell={false}
              rowIndex={rowIndex}
              onKeyDown={onCellKeyDown}
              xPosition={childIndex + 1}
              yPosition={headerRowsLength + rowIndex}
            >
              {child}
            </TdAdditiveCell>
          );
        })}
      </Tr>
    ),
    [children, onCellKeyDown]
  );

  return (
    <Tbody
      className={`${headerVisibilityMemo === TableHeaderVisibility.None ? "missing-header" : ""}`}
      {...(tableInstance.getTableBodyProps() as any)}
    >
      {tableInstance.rows.map((row: Row, rowIndex: number) => renderBodyRow(row, rowIndex))}
      {children ? renderAdditiveRow(tableInstance.rows.length) : null}
    </Tbody>
  );
};
