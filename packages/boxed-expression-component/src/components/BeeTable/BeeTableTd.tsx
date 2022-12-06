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
import { BeeTableTdsAndThsProps } from "../../api";

export interface BeeTableTdProps<R extends object> extends BeeTableTdsAndThsProps {
  // Individual cells are not immutable referecens, By referencing the row, we avoid multiple re-renders and bugs.
  row: ReactTable.Row<R>;
  column: ReactTable.ColumnInstance<R>;
  shouldUseCellDelegate: boolean;
  getColumnKey: (column: ReactTable.ColumnInstance<R>) => string;
  getTdProps: (cellIndex: number, rowIndex: number) => Partial<PfReactTable.TdProps>;
}

export function BeeTableTd<R extends object>({
  index,
  row,
  column,
  rowIndex,
  shouldUseCellDelegate,
  onKeyDown,
  getColumnKey,
  getTdProps,
  yPosition,
}: BeeTableTdProps<R>) {
  let cellType = index === 0 ? "counter-cell" : "data-cell";
  const tdRef = useRef<HTMLTableCellElement>(null);

  useEffect(() => {
    const handler = onKeyDown();
    const td = tdRef.current;
    td?.addEventListener("keydown", handler);
    return () => {
      td?.removeEventListener("keydown", handler);
    };
  }, [onKeyDown, rowIndex]);

  // FIXME: Tiago -> DMN Runner-specific logic
  if (column.cellDelegate) {
    cellType += " input";
  }

  const cell = useMemo(() => {
    return row.cells[index];
  }, [index, row]);

  const tdContent = useMemo(() => {
    return shouldUseCellDelegate && column.cellDelegate
      ? column.cellDelegate?.(`cell-delegate-${rowIndex}`)
      : cell.render("Cell");
  }, [cell, rowIndex, shouldUseCellDelegate, column]);

  return (
    <PfReactTable.Td
      {...getTdProps(index, rowIndex)}
      ref={tdRef}
      tabIndex={-1}
      key={`${rowIndex}-${getColumnKey(row.cells[index].column)}-${index}`}
      data-ouia-component-id={"expression-column-" + index}
      className={`${cellType}`}
      data-xposition={index}
      data-yposition={yPosition ?? rowIndex}
      style={{ flexGrow: index === row.cells.length - 1 ? "1" : "0" }}
    >
      {index === 0 ? (
        <>{rowIndex + 1}</>
      ) : (
        <>
          <Resizer width={column.width} setWidth={column.setWidth} minWidth={column.minWidth}>
            <>{tdContent}</>
          </Resizer>
        </>
      )}
    </PfReactTable.Td>
  );
}
