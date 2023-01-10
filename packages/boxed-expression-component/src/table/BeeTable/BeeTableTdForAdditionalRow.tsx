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

import * as PfReactTable from "@patternfly/react-table";
import * as React from "react";
import { useRef } from "react";
import { BeeTableTdProps } from "../../api";
import { Resizer } from "../../resizing/Resizer";
import { useBeeTableColumnResizingWidth } from "./BeeTableColumnResizingWidthsContextProvider";
import { useBeeTableSelectableCell } from "./BeeTableSelectionContext";

export interface BeeTableTdForAdditionalRowProps<R extends object> extends BeeTableTdProps<R> {
  children?: React.ReactElement;
  isEmptyCell: boolean;
  isLastColumn: boolean;
}

export function BeeTableTdForAdditionalRow<R extends object>({
  children,
  isEmptyCell,
  columnIndex,
  column,
  rowIndex,
  isLastColumn,
}: BeeTableTdForAdditionalRowProps<R>) {
  const tdRef = useRef<HTMLTableCellElement>(null);

  const { resizingWidth, setResizingWidth } = useBeeTableColumnResizingWidth(
    columnIndex,
    // If the column specifies a width, then we should respect its minWidth as well.
    column.width ? Math.max(column.minWidth ?? 0, column.width ?? 0) : undefined
  );

  const { cssClasses, onMouseDown, onDoubleClick } = useBeeTableSelectableCell(tdRef, rowIndex, columnIndex);

  return isEmptyCell ? (
    <PfReactTable.Td
      ref={tdRef}
      role="cell"
      className={`empty-cell ${cssClasses}`}
      onMouseDown={onMouseDown}
      onDoubleClick={onDoubleClick}
    >
      <br />
    </PfReactTable.Td>
  ) : (
    <PfReactTable.Td
      ref={tdRef}
      role={"cell"}
      className={`additional-row-content ${cssClasses}`}
      tabIndex={-1}
      style={{ flexGrow: isLastColumn ? "1" : "0" }}
      onMouseDown={onMouseDown}
      onDoubleClick={onDoubleClick}
    >
      <div style={{ width: resizingWidth?.value }}>{children}</div>

      <Resizer
        minWidth={column.minWidth}
        width={column.width}
        setWidth={column.setWidth}
        resizingWidth={resizingWidth}
        setResizingWidth={setResizingWidth}
      />
    </PfReactTable.Td>
  );
}
