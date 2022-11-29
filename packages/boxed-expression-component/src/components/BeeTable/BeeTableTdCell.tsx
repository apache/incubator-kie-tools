/*
 * Copyright 2022 Red Hat, Inc. and/or its affiliates.
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
import { useEffect, useRef } from "react";
import { Td } from "@patternfly/react-table";
import { DEFAULT_MIN_WIDTH, Resizer } from "../Resizer";
import * as ReactTable from "react-table";
import { BeeTableColumn as IColumn, BeeTableCellComponent } from "../../api";

export interface BeeTableTdCellProps extends BeeTableCellComponent {
  cell: ReactTable.Cell;
  getColumnKey: (column: ReactTable.Column) => string;
  inAForm: boolean;
  onColumnsUpdate: (columns: ReactTable.Column[]) => void;
  reactTableInstance: ReactTable.TableInstance;
  tdProps: (cellIndex: number, rowIndex: number) => any;
}

export function BeeTableTdCell({
  cellIndex,
  cell,
  rowIndex,
  inAForm,
  onKeyDown,
  reactTableInstance: tableInstance,
  getColumnKey,
  onColumnsUpdate,
  tdProps,
  yPosition,
}: BeeTableTdCellProps) {
  let cellType = cellIndex === 0 ? "counter-cell" : "data-cell";
  const column = tableInstance.allColumns[cellIndex] as unknown as IColumn;
  const width = typeof column?.width === "number" ? column?.width : DEFAULT_MIN_WIDTH;
  const tdRef = useRef<HTMLElement>(null);

  useEffect(() => {
    const onKeyDownForIndex = onKeyDown();
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
      data-xposition={cellIndex}
      data-yposition={yPosition ?? rowIndex}
    >
      {cellTemplate}
    </Td>
  );
}
