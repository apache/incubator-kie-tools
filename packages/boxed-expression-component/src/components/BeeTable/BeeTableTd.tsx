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
import * as PfReactTable from "@patternfly/react-table";
import { DEFAULT_MIN_WIDTH, Resizer } from "../Resizer";
import * as ReactTable from "react-table";
import { BeeTableColumn, BeeTableCellComponent } from "../../api";

export interface BeeTableTdProps extends BeeTableCellComponent {
  cell: ReactTable.Cell;
  getColumnKey: (column: ReactTable.ColumnInstance) => string;
  isInForm: boolean;
  onColumnsUpdate: (columns: ReactTable.ColumnInstance[]) => void;
  reactTableInstance: ReactTable.TableInstance;
  getTdProps: (cellIndex: number, rowIndex: number) => Partial<PfReactTable.TdProps>;
}

export function BeeTableTd({
  cellIndex,
  cell,
  rowIndex,
  isInForm,
  onKeyDown,
  reactTableInstance,
  getColumnKey,
  onColumnsUpdate,
  getTdProps,
  yPosition,
}: BeeTableTdProps) {
  let cellType = cellIndex === 0 ? "counter-cell" : "data-cell";
  // FIXME: Tiago -> Bad typing.
  const column = reactTableInstance.allColumns[cellIndex] as unknown as BeeTableColumn;
  const width = typeof column?.width === "number" ? column?.width : DEFAULT_MIN_WIDTH;
  const tdRef = useRef<HTMLTableCellElement>(null);

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
      reactTableInstance.allColumns[cellIndex].width = width;
      onColumnsUpdate?.(reactTableInstance.columns);
    }
  };
  const cellContent =
    cellIndex === 0 ? (
      <>{rowIndex + 1}</>
    ) : (
      <Resizer width={width} onHorizontalResizeStop={onResize}>
        <>
          {/* FIXME: Tiago -> Bad typing. */}
          {isInForm && typeof (cell.column as any)?.cellDelegate === "function"
            ? (cell.column as any)?.cellDelegate(`dmn-auto-form-${rowIndex}`)
            : cell.render("Cell")}
        </>
      </Resizer>
    );

  if (typeof (cell.column as any)?.cellDelegate === "function") {
    cellType += " input";
  }

  return (
    <PfReactTable.Td
      {...getTdProps(cellIndex, rowIndex)}
      ref={tdRef}
      tabIndex={-1}
      key={`${rowIndex}-${getColumnKey(cell.column)}-${cellIndex}`}
      data-ouia-component-id={"expression-column-" + cellIndex}
      className={`${cellType}`}
      data-xposition={cellIndex}
      data-yposition={yPosition ?? rowIndex}
    >
      {cellContent}
    </PfReactTable.Td>
  );
}
