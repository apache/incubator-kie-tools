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
import { useCallback, useEffect, useMemo, useRef } from "react";
import * as PfReactTable from "@patternfly/react-table";
import { DEFAULT_MIN_WIDTH, Resizer } from "../Resizer";
import * as ReactTable from "react-table";
import { BeeTableCellComponent } from "../../api";

export interface BeeTableTdProps<R extends object> extends BeeTableCellComponent {
  cell: ReactTable.Cell<R>;
  getColumnKey: (column: ReactTable.ColumnInstance<R>) => string;
  shouldUseCellDelegate: boolean;
  onColumnsUpdate: (columns: ReactTable.ColumnInstance<R>[]) => void;
  reactTableInstance: ReactTable.TableInstance<R>;
  getTdProps: (cellIndex: number, rowIndex: number) => Partial<PfReactTable.TdProps>;
}

export function BeeTableTd<R extends object>({
  cellIndex,
  cell,
  rowIndex,
  shouldUseCellDelegate,
  onKeyDown,
  reactTableInstance,
  getColumnKey,
  onColumnsUpdate,
  getTdProps,
  yPosition,
}: BeeTableTdProps<R>) {
  let cellType = cellIndex === 0 ? "counter-cell" : "data-cell";
  const column = reactTableInstance.allColumns[cellIndex];
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
          {shouldUseCellDelegate && cell.column?.cellDelegate
            ? cell.column?.cellDelegate(`dmn-auto-form-${rowIndex}`)
            : cell.render("Cell")}
        </>
      </Resizer>
    );

  if (cell.column?.cellDelegate) {
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
