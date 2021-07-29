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
import { useCallback, useMemo } from "react";
import { Tbody, Td, Tr } from "@patternfly/react-table";
import { TableHeaderVisibility, Column as IColumn } from "../../api";
import { Cell, Column, Row, TableInstance } from "react-table";
import { DEFAULT_MIN_WIDTH, Resizer } from "../Resizer";

export interface TableBodyProps {
  /** Table instance */
  tableInstance: TableInstance;
  /** The way in which the header will be rendered */
  headerVisibility?: TableHeaderVisibility;
  /** Optional children element to be appended below the table content */
  children?: React.ReactElement[];
  /** Custom function for getting row key prop, and avoid using the row index */
  getRowKey: (row: Row) => string;
  /** Custom function for getting column key prop, and avoid using the column index */
  getColumnKey: (column: Column) => string;
  /** Function to be executed when columns are modified */
  onColumnsUpdate?: (columns: Column[]) => void;
}

export const TableBody: React.FunctionComponent<TableBodyProps> = ({
  tableInstance,
  children,
  headerVisibility = TableHeaderVisibility.Full,
  getRowKey,
  getColumnKey,
  onColumnsUpdate,
}) => {
  const renderCell = useCallback(
    (cellIndex: number, cell: Cell, rowIndex: number) => {
      const cellType = cellIndex === 0 ? "counter-cell" : "data-cell";
      const column = tableInstance.allColumns[cellIndex] as unknown as IColumn;

      const getWidth = (): number => {
        if (typeof column?.width === "number") {
          return column?.width;
        }
        return DEFAULT_MIN_WIDTH;
      };
      const setWidth = (width: number) => {
        column?.setWidth?.(width);
        tableInstance.allColumns[cellIndex].width = width;
      };
      const onResize = (width: number) => {
        if (column.setWidth) {
          setWidth(width);
          onColumnsUpdate?.(tableInstance.columns);
        }
      };
      const cellTemplate =
        cellIndex === 0 ? (
          <>{rowIndex + 1}</>
        ) : (
          <Resizer width={getWidth()} onHorizontalResizeStop={onResize}>
            <>{cell.render("Cell")}</>
          </Resizer>
        );

      return (
        <Td
          {...tableInstance.getTdProps(cellIndex, rowIndex)}
          key={`${getColumnKey(cell.column)}-${cellIndex}`}
          data-ouia-component-id={"expression-column-" + cellIndex}
          className={`${cellType}`}
        >
          {cellTemplate}
        </Td>
      );
    },
    [getColumnKey, onColumnsUpdate, tableInstance]
  );

  const renderBodyRow = useCallback(
    (row: Row, rowIndex: number) => {
      const rowProps = { ...row.getRowProps(), style: {} };
      return (
        <Tr
          className="table-row"
          {...rowProps}
          key={`${getRowKey(row)}-${rowIndex}`}
          ouiaId={"expression-row-" + rowIndex}
        >
          {row.cells.map((cell: Cell, cellIndex: number) => renderCell(cellIndex, cell, rowIndex))}
        </Tr>
      );
    },
    [getRowKey, renderCell]
  );

  const renderAdditiveRow = useMemo(
    () => (
      <Tr className="table-row additive-row">
        <Td role="cell" className="empty-cell">
          <br />
        </Td>
        {children?.map((child, childIndex) => {
          return (
            <Td role="cell" key={childIndex} className="row-remainder-content">
              {child}
            </Td>
          );
        })}
      </Tr>
    ),
    [children]
  );

  return (
    <Tbody
      className={`${headerVisibility === TableHeaderVisibility.None ? "missing-header" : ""}`}
      {...tableInstance.getTableBodyProps()}
    >
      {tableInstance.rows.map((row: Row, rowIndex: number) => {
        tableInstance.prepareRow(row);
        return renderBodyRow(row, rowIndex);
      })}
      {children ? renderAdditiveRow : null}
    </Tbody>
  );
};
