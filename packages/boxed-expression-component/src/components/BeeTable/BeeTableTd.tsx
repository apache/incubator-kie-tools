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
import { BeeTableTdsAndThsProps } from "../../api";

export interface BeeTableTdProps<R extends object> extends BeeTableTdsAndThsProps {
  cell: ReactTable.Cell<R>;
  getColumnKey: (column: ReactTable.ColumnInstance<R>) => string;
  shouldUseCellDelegate: boolean;
  onColumnsUpdate: (columns: ReactTable.Column<R>[]) => void;
  reactTableInstance: ReactTable.TableInstance<R>;
  getTdProps: (cellIndex: number, rowIndex: number) => Partial<PfReactTable.TdProps>;
}

export function BeeTableTd<R extends object>({
  index,
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
  let cellType = index === 0 ? "counter-cell" : "data-cell";
  const column = reactTableInstance.allColumns[index];
  const width = column?.width ?? DEFAULT_MIN_WIDTH;
  const tdRef = useRef<HTMLTableCellElement>(null);

  useEffect(() => {
    const onKeyDownForIndex = onKeyDown();
    const td = tdRef.current;
    td?.addEventListener("keydown", onKeyDownForIndex);
    return () => {
      td?.removeEventListener("keydown", onKeyDownForIndex);
    };
  }, [onKeyDown, rowIndex]);

  const onResize = (width: number) => {
    if (column.setWidth) {
      column.setWidth(width);
      // reactTableInstance.allColumns[index].width = width;
      // // FIXME: Tiago -> Not good.
      // onColumnsUpdate?.(reactTableInstance.columns as unknown as ReactTable.Column<R>[]);
    }
  };
  const tdContent =
    index === 0 ? (
      <>{rowIndex + 1}</>
    ) : (
      <Resizer width={width} onHorizontalResizeStop={onResize}>
        <>
          {shouldUseCellDelegate && cell.column?.cellDelegate
            ? cell.column?.cellDelegate(`cell-delegate-${rowIndex}`)
            : cell.render("Cell")}
        </>
      </Resizer>
    );

  // FIXME: Tiago -> DMN Runner-specific logic
  if (cell.column?.cellDelegate) {
    cellType += " input";
  }

  return (
    <PfReactTable.Td
      {...getTdProps(index, rowIndex)}
      ref={tdRef}
      tabIndex={-1}
      key={`${rowIndex}-${getColumnKey(cell.column)}-${index}`}
      data-ouia-component-id={"expression-column-" + index}
      className={`${cellType}`}
      data-xposition={index}
      data-yposition={yPosition ?? rowIndex}
    >
      {tdContent}
    </PfReactTable.Td>
  );
}
