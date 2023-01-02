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
import { useCallback, useEffect, useLayoutEffect, useMemo, useRef, useState } from "react";
import * as PfReactTable from "@patternfly/react-table";
import { Resizer } from "../../resizing/Resizer";
import * as ReactTable from "react-table";
import PlusIcon from "@patternfly/react-icons/dist/js/icons/plus-icon";
import { useBeeTableCell, useBeeTableSelectionDispatch } from "./BeeTableSelectionContext";
import { BeeTableTdProps } from "../../api";
import { useBeeTableColumnResizingWidth } from "./BeeTableColumnResizingWidthsContextProvider";

export interface BeeTableTdProps2<R extends object> extends BeeTableTdProps<R> {
  // Individual cells are not immutable referecens, By referencing the row, we avoid multiple re-renders and bugs.
  row: ReactTable.Row<R>;
  column: ReactTable.ColumnInstance<R>;
  shouldUseCellDelegate: boolean;
  onRowAdded?: (args: { beforeIndex: number }) => void;
  isActive: boolean;
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
  onRowAdded,
}: BeeTableTdProps2<R>) {
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

  const { resetSelectionAt, setSelectionEnd, mutateSelection } = useBeeTableSelectionDispatch();
  const { resizingWidth, setResizingWidth } = useBeeTableColumnResizingWidth(columnIndex, column.width);
  const { isActive, isEditing, isSelected, selectedPositions } = useBeeTableCell(rowIndex, columnIndex);

  const cssClasses = useMemo(() => {
    return `
      ${cssClass} 
      ${isActive ? "active" : ""}
      ${isEditing ? "editing" : ""} 
      ${isSelected ? "selected" : ""} 
      ${(selectedPositions?.length ?? 0) <= 0 ? "middle" : selectedPositions?.join(" ")}
    `;
  }, [cssClass, isActive, isEditing, isSelected, selectedPositions]);

  const tdContent = useMemo(() => {
    return shouldUseCellDelegate && column.cellDelegate
      ? column.cellDelegate?.(`cell-delegate-${rowIndex}`)
      : cell.render("Cell");
  }, [cell, rowIndex, shouldUseCellDelegate, column]);

  useEffect(() => {
    function onEnter(e: MouseEvent) {
      e.stopPropagation();

      // User is pressing the left mouse button. Meaning user is dragging.
      // Not a final solution, as user can start dragging from anywhere.
      // Ideally, we want users to change selection only when the dragging originates
      // some other cell within the table.
      if (e.buttons === 1 && e.button === 0) {
        setSelectionEnd({
          columnIndex,
          rowIndex,
          isEditing: false,
        });
      }

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
  }, [columnIndex, rowIndex, resetSelectionAt, setSelectionEnd]);

  const onMouseDown = useCallback(
    (e: React.MouseEvent) => {
      // That's the right-click case to open the Context Menu at the right place.
      if (e.button !== 0 && isSelected) {
        resetSelectionAt({
          columnIndex,
          rowIndex,
          isEditing: false,
          keepSelection: true,
        });
        return;
      }

      if (!isActive && !isEditing) {
        const set = e.shiftKey ? setSelectionEnd : resetSelectionAt;
        set({
          columnIndex,
          rowIndex,
          isEditing: false,
        });
      }
    },
    [columnIndex, isActive, isEditing, isSelected, rowIndex, resetSelectionAt, setSelectionEnd]
  );

  const onDoubleClick = useCallback(
    (e: React.MouseEvent) => {
      e.stopPropagation();
      resetSelectionAt({
        columnIndex,
        rowIndex,
        isEditing: columnIndex > 0, // Not rowIndex column
      });
    },
    [columnIndex, rowIndex, resetSelectionAt]
  );

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
      overflow: "visible",
      position: "relative" as const,
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

  useLayoutEffect(() => {
    if (isActive && !isEditing) {
      tdRef.current?.focus();
    }
  }, [isActive, isEditing]);

  return (
    <PfReactTable.Td
      onMouseDown={onMouseDown}
      onDoubleClick={onDoubleClick}
      ref={tdRef}
      tabIndex={-1}
      className={cssClasses}
      data-ouia-component-id={`expression-column-${columnIndex}`} // FIXME: Tiago -> Bad name
      style={style}
    >
      {column.isRowIndexColumn ? (
        <>{rowIndex + 1}</>
      ) : (
        <>
          <div
            style={{
              width: resizingWidth?.value,
              minWidth: cell.column.minWidth,
            }}
          >
            {tdContent}
          </div>
          {(hoverInfo.isHovered || (resizingWidth?.isPivoting && isResizing)) && (
            <Resizer
              minWidth={cell.column.minWidth}
              width={cell.column.width}
              setWidth={cell.column.setWidth}
              resizingWidth={resizingWidth}
              setResizingWidth={setResizingWidth}
              setResizing={setResizing}
            />
          )}
        </>
      )}

      {hoverInfo.isHovered && column.isRowIndexColumn && onRowAdded && (
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
  );
}

function getHoverInfo(e: MouseEvent, elem: HTMLElement): HoverInfo {
  const rect = elem.getBoundingClientRect();
  const localY = e.clientY - rect.top; // y position within the element.
  const part = localY < rect.height / 3 ? "upper" : "lower"; // upper part is the upper third
  return { isHovered: true, part };
}
