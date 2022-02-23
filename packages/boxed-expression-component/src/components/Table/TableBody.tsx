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
import { useCallback, useEffect, useMemo, useRef } from "react";
import { Tbody, Td, Tr } from "@patternfly/react-table";
import { Column as IColumn, TableHeaderVisibility } from "../../api";
import { Cell, Column, Row, TableInstance } from "react-table";
import { DEFAULT_MIN_WIDTH, Resizer } from "../Resizer";
import {
  focusCurrentCell,
  focusInsideCell,
  focusLowerCell,
  focusNextCell,
  focusNextDataCell,
  focusParentCell,
  focusPrevCell,
  focusPrevDataCell,
  focusUpperCell,
  getParentCell,
} from "./common";
import { useBoxedExpression } from "../../context";

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
  /** Td props */
  tdProps: (cellIndex: number, rowIndex: number) => any;
  /** Enable the  Keyboar Navigation */
  enableKeyboarNavigation?: boolean;
}

export const TableBody: React.FunctionComponent<TableBodyProps> = ({
  tableInstance,
  children,
  headerVisibility,
  getRowKey,
  getColumnKey,
  onColumnsUpdate,
  tdProps,
  enableKeyboarNavigation = true,
}) => {
  const headerVisibilityMemo = useMemo(() => headerVisibility ?? TableHeaderVisibility.Full, [headerVisibility]);

  const { isContextMenuOpen } = useBoxedExpression();

  /**
   * base props for td elements
   */

  const onKeyDown = useCallback(
    (rowIndex: number) => (e: React.KeyboardEvent<HTMLElement>) => {
      const key = e.key;
      const isModKey = e.altKey || e.ctrlKey || e.shiftKey || key === "AltGraph";

      if (!enableKeyboarNavigation) {
        return;
      }

      //prevent the parent cell catch this event if there is a nested table
      if (e.currentTarget !== getParentCell(e.target as HTMLElement)) {
        return;
      }

      if (isContextMenuOpen) {
        e.preventDefault();
        if (key === "Escape") {
          //close Select child components if any
          focusCurrentCell(e.currentTarget);
        }
        return;
      }

      const isFiredFromThis = e.currentTarget === e.target;

      if (key === "Tab") {
        e.preventDefault();
        if (e.shiftKey) {
          focusPrevDataCell(e.currentTarget, rowIndex);
        } else {
          focusNextDataCell(e.currentTarget, rowIndex);
        }
      } else if (key === "ArrowLeft") {
        focusPrevCell(e.currentTarget);
      } else if (key === "ArrowRight") {
        focusNextCell(e.currentTarget);
      } else if (key === "ArrowUp") {
        focusUpperCell(e.currentTarget, rowIndex);
      } else if (key === "ArrowDown") {
        focusLowerCell(e.currentTarget, rowIndex);
      } else if (key === "Escape") {
        focusParentCell(e.currentTarget);
      } else if (!isContextMenuOpen && isFiredFromThis && !isModKey) {
        if (key === "Enter") {
          focusInsideCell(e.currentTarget);
        } else {
          focusInsideCell(e.currentTarget, true);
        }
      }
    },
    [isContextMenuOpen]
  );

  const renderBodyRow = useCallback(
    (row: Row, rowIndex: number) => {
      tableInstance.prepareRow(row);
      const rowProps = { ...row.getRowProps(), style: {} };
      const RowDelegate = (row.original as any).rowDelegate;
      const rowKey = getRowKey(row);
      const rowClassNames = `${rowKey} table-row`;
      return (
        <React.Fragment key={rowKey}>
          {RowDelegate ? (
            <RowDelegate>
              <Tr className={rowClassNames} {...rowProps} ouiaId={"expression-row-" + rowIndex} key={rowKey}>
                {row.cells.map((cell: Cell, cellIndex: number) => (
                  <TdCell
                    key={cellIndex}
                    cellIndex={cellIndex}
                    cell={cell}
                    rowIndex={rowIndex}
                    inAForm={true}
                    onKeyDown={onKeyDown}
                    tableInstance={tableInstance}
                    getColumnKey={getColumnKey}
                    onColumnsUpdate={onColumnsUpdate!}
                    tdProps={tdProps}
                  />
                ))}
              </Tr>
            </RowDelegate>
          ) : (
            <Tr className={rowClassNames} {...rowProps} ouiaId={"expression-row-" + rowIndex} key={rowKey}>
              {row.cells.map((cell: Cell, cellIndex: number) => (
                <TdCell
                  key={cellIndex}
                  cellIndex={cellIndex}
                  cell={cell}
                  rowIndex={rowIndex}
                  inAForm={false}
                  onKeyDown={onKeyDown}
                  tableInstance={tableInstance}
                  getColumnKey={getColumnKey}
                  onColumnsUpdate={onColumnsUpdate!}
                  tdProps={tdProps}
                />
              ))}
            </Tr>
          )}
        </React.Fragment>
      );
    },
    [getColumnKey, getRowKey, onColumnsUpdate, onKeyDown, tableInstance, tdProps]
  );

  const renderAdditiveRow = useCallback(
    (rowIndex: number) => (
      <Tr className="table-row additive-row">
        <Td role="cell" className="empty-cell" tabIndex={-1} onKeyDown={(e) => onKeyDown(rowIndex)(e)}>
          <br />
        </Td>
        {children?.map((child, childIndex) => {
          return (
            <Td
              role="cell"
              key={childIndex}
              className="row-remainder-content"
              tabIndex={-1}
              onKeyDown={(e) => onKeyDown(rowIndex)(e)}
            >
              {child}
            </Td>
          );
        })}
      </Tr>
    ),
    [children, onKeyDown]
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

interface TdCellProps {
  cellIndex: number;
  cell: Cell;
  rowIndex: number;
  inAForm: boolean;
  onKeyDown: (rowIndex: number) => (e: React.KeyboardEvent<HTMLElement>) => void;
  tableInstance: TableInstance;
  getColumnKey: (column: Column) => string;
  onColumnsUpdate: (columns: Column[]) => void;
  tdProps: (cellIndex: number, rowIndex: number) => any;
}

function TdCell({
  cellIndex,
  cell,
  rowIndex,
  inAForm,
  onKeyDown,
  tableInstance,
  getColumnKey,
  onColumnsUpdate,
  tdProps,
}: TdCellProps) {
  let cellType = cellIndex === 0 ? "counter-cell" : "data-cell";
  const column = tableInstance.allColumns[cellIndex] as unknown as IColumn;
  const width = typeof column?.width === "number" ? column?.width : DEFAULT_MIN_WIDTH;
  const tdRef = useRef<HTMLElement>(null);

  useEffect(() => {
    // Typescript don't accept the conversion between DOM event and React event
    const onKeyDownForIndex: any = onKeyDown(rowIndex);
    const cell = tdRef.current;
    cell?.addEventListener("keydown", onKeyDownForIndex);
    return () => {
      cell?.removeEventListener("keydown", onKeyDownForIndex);
    };
  }, [onKeyDown, rowIndex]);

  const onResize = (width: number) => {
    if (column.setWidth) {
      column.setWidth(width);
      tableInstance.allColumns[cellIndex].width = width;
      onColumnsUpdate?.(tableInstance.columns);
    }
  };
  const cellTemplate =
    cellIndex === 0 ? (
      <>{rowIndex + 1}</>
    ) : (
      <Resizer width={width} onHorizontalResizeStop={onResize}>
        <>
          {inAForm && typeof (cell.column as any)?.cellDelegate === "function"
            ? (cell.column as any)?.cellDelegate(`dmn-auto-form-${rowIndex}`)
            : cell.render("Cell")}
        </>
      </Resizer>
    );

  if (typeof (cell.column as any)?.cellDelegate === "function") {
    cellType += " input";
  }

  return (
    <Td
      {...tdProps(cellIndex, rowIndex)}
      ref={tdRef}
      tabIndex={-1}
      key={`${rowIndex}-${getColumnKey(cell.column)}-${cellIndex}`}
      data-ouia-component-id={"expression-column-" + cellIndex}
      className={`${cellType}`}
    >
      {cellTemplate}
    </Td>
  );
}
