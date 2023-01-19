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

import PlusIcon from "@patternfly/react-icons/dist/js/icons/plus-icon";
import * as PfReactTable from "@patternfly/react-table";
import * as React from "react";
import { useCallback, useEffect, useMemo, useRef, useState } from "react";
import * as ReactTable from "react-table";
import { Resizer } from "../../resizing/Resizer";
import { useBeeTableResizableCell } from "../../resizing/BeeTableResizableColumnsContextProvider";
import {
  BeeTableCellCoordinates,
  BeeTableCoordinatesContextProvider,
  useBeeTableSelectableCellRef,
} from "../../selection/BeeTableSelectionContext";
import { useBeeTableSelectableCell } from "../../selection/BeeTableSelectionContext";
import { ResizerStopBehavior } from "../../resizing/ResizingWidthsContext";

export interface BeeTableTdProps<R extends object> {
  // Individual cells are not immutable referecens, By referencing the row, we avoid multiple re-renders and bugs.
  shouldUseCellDelegate: boolean;
  onRowAdded?: (args: { beforeIndex: number }) => void;
  isActive: boolean;
  shouldRenderInlineButtons: boolean;
  shouldShowRowsInlineControls: boolean;
  rowIndex: number;
  row: ReactTable.Row<R>;
  columnIndex: number;
  column: ReactTable.ColumnInstance<R>;
  resizerStopBehavior: ResizerStopBehavior;
  lastColumnMinWidth?: number;
}

export type HoverInfo =
  | {
      isHovered: false;
    }
  | {
      isHovered: true;
      part: "upper" | "lower";
    };

export function BeeTableTd<R extends object>({
  columnIndex,
  row,
  column,
  rowIndex,
  shouldUseCellDelegate,
  shouldRenderInlineButtons,
  shouldShowRowsInlineControls,
  resizerStopBehavior,
  onRowAdded,
  lastColumnMinWidth,
}: BeeTableTdProps<R>) {
  const [isResizing, setResizing] = useState(false);
  const [hoverInfo, setHoverInfo] = useState<HoverInfo>({ isHovered: false });

  const tdRef = useRef<HTMLTableCellElement>(null);

  let cssClass = column.isRowIndexColumn ? "row-index-column-cell" : "data-cell";
  if (column.cellDelegate) {
    cssClass += " input"; // FIXME: Tiago -> DMN Runner/DecisionTable-specific logic
  }
  const cell = useMemo(() => {
    return row.cells[columnIndex];
  }, [columnIndex, row]);

  const { resizingWidth, setResizingWidth } = useBeeTableResizableCell(
    columnIndex,
    resizerStopBehavior,
    column.setWidth,
    // If the column specifies a width, then we should respect its minWidth as well.
    column.width ? Math.max(lastColumnMinWidth ?? column.minWidth ?? 0, column.width ?? 0) : undefined
  );

  const rowIndexLabel = useMemo(() => {
    return `${rowIndex + 1}`;
  }, [rowIndex]);

  const getValue = useMemo(() => {
    if (column.isRowIndexColumn) {
      return () => rowIndexLabel;
    }
    return undefined;
  }, [column.isRowIndexColumn, rowIndexLabel]);

  useBeeTableSelectableCellRef(rowIndex, columnIndex, undefined, getValue);

  const tdContent = useMemo(() => {
    return shouldUseCellDelegate && column.cellDelegate
      ? column.cellDelegate?.(`cell-delegate-${rowIndex}`)
      : cell.render("Cell");
  }, [cell, rowIndex, shouldUseCellDelegate, column]);

  useEffect(() => {
    function onEnter(e: MouseEvent) {
      e.stopPropagation();

      setHoverInfo((prev) => getHoverInfo(e, td!));
    }

    function onMove(e: MouseEvent) {
      setHoverInfo((prev) => getHoverInfo(e, td!));
    }

    function onLeave() {
      setHoverInfo((prev) => ({ isHovered: false }));
    }

    const td = tdRef.current;
    td?.addEventListener("mouseenter", onEnter);
    td?.addEventListener("mousemove", onMove);
    td?.addEventListener("mouseleave", onLeave);
    return () => {
      td?.removeEventListener("mouseleave", onLeave);
      td?.removeEventListener("mousemove", onMove);
      td?.removeEventListener("mouseenter", onEnter);
    };
  }, [columnIndex, rowIndex]);

  const onAddRowButtonClick = useCallback(
    (e: React.MouseEvent) => {
      e.stopPropagation();

      if (!hoverInfo.isHovered) {
        return;
      }

      onRowAdded?.({ beforeIndex: hoverInfo.part === "upper" ? rowIndex : rowIndex + 1 });

      if (hoverInfo.part === "upper") {
        setHoverInfo({ isHovered: false });
      }
    },
    [hoverInfo, onRowAdded, rowIndex]
  );

  const style = useMemo(() => {
    return {
      flexGrow: columnIndex === row.cells.length - 1 ? "1" : "0",
    };
  }, [columnIndex, row.cells.length]);

  const addRowButtonStyle = useMemo(
    () =>
      hoverInfo.isHovered && hoverInfo.part === "lower"
        ? {
            bottom: "-9px",
          }
        : {
            top: "-10px",
          },
    [hoverInfo]
  );

  const coordinates = useMemo<BeeTableCellCoordinates>(
    () => ({
      rowIndex,
      columnIndex,
    }),
    [columnIndex, rowIndex]
  );

  const { cssClasses, onMouseDown, onDoubleClick } = useBeeTableSelectableCell(tdRef, rowIndex, columnIndex);

  return (
    <BeeTableCoordinatesContextProvider coordinates={coordinates}>
      <PfReactTable.Td
        onMouseDown={onMouseDown}
        onDoubleClick={onDoubleClick}
        ref={tdRef}
        tabIndex={-1}
        className={`${cssClass} ${cssClasses}`}
        data-ouia-component-id={`expression-column-${columnIndex}`} // FIXME: Tiago -> Bad name
        style={style}
      >
        {column.isRowIndexColumn ? (
          <>{rowIndexLabel}</>
        ) : (
          <>
            <div
              style={{
                width: resizingWidth?.value,
                minWidth: lastColumnMinWidth ?? cell.column.minWidth,
              }}
            >
              {tdContent}
            </div>
            {(hoverInfo.isHovered || (resizingWidth?.isPivoting && isResizing)) && (
              <Resizer
                minWidth={lastColumnMinWidth ?? cell.column.minWidth}
                width={cell.column.width}
                setWidth={cell.column.setWidth}
                resizingWidth={resizingWidth}
                setResizingWidth={setResizingWidth}
                setResizing={setResizing}
              />
            )}
          </>
        )}

        {hoverInfo.isHovered && shouldRenderInlineButtons && onRowAdded && shouldShowRowsInlineControls && (
          <div
            onMouseDown={(e) => e.stopPropagation()}
            onDoubleClick={(e) => e.stopPropagation()}
            onClick={onAddRowButtonClick}
            className={"add-row-button"}
            style={addRowButtonStyle}
          >
            <PlusIcon size="sm" />
          </div>
        )}
      </PfReactTable.Td>
    </BeeTableCoordinatesContextProvider>
  );
}

function getHoverInfo(e: MouseEvent, elem: HTMLElement): HoverInfo {
  const rect = elem.getBoundingClientRect();
  const localY = e.clientY - rect.top; // y position within the element.
  const part = localY < rect.height / 3 ? "upper" : "lower"; // upper part is the upper third
  return { isHovered: true, part };
}
