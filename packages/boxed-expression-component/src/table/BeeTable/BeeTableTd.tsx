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
import { useCallback, useEffect, useMemo, useRef, useState } from "react";
import * as PfReactTable from "@patternfly/react-table";
import { Resizer } from "../../resizing/Resizer";
import * as ReactTable from "react-table";
import PlusIcon from "@patternfly/react-icons/dist/js/icons/plus-icon";
import { useBeeTableCellStatus, useBeeTableSelectionDispatch } from "./BeeTableSelectionContext";
import { BeeTableTdProps } from "../../api";
import { NavigationKeysUtils } from "../../keysUtils";
import { BeeTableCellUpdate } from ".";

export interface BeeTableTdProps2<R extends object> extends BeeTableTdProps<R> {
  // Individual cells are not immutable referecens, By referencing the row, we avoid multiple re-renders and bugs.
  row: ReactTable.Row<R>;
  column: ReactTable.ColumnInstance<R>;
  shouldUseCellDelegate: boolean;
  onRowAdded?: (args: { beforeIndex: number }) => void;
  onCellUpdates?: (cellUpdates: BeeTableCellUpdate<R>[]) => void;
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
  yPosition,
  onCellUpdates,
}: BeeTableTdProps2<R>) {
  const { setActiveCell, setSelectionEnd } = useBeeTableSelectionDispatch();

  const { isActive, isEditing, isSelected, selectedPositions } = useBeeTableCellStatus(rowIndex, columnIndex);

  const [hoverInfo, setHoverInfo] = useState<HoverInfo>({ isHovered: false });

  const tdRef = useRef<HTMLTableCellElement>(null);

  let cssClass = column.isRowIndexColumn ? "row-index-column-cell" : "data-cell";
  if (column.cellDelegate) {
    cssClass += " input"; // FIXME: Tiago -> DMN Runner/DecisionTable-specific logic
  }

  const cssClasses = useMemo(() => {
    return `
      ${cssClass} 
      ${isActive ? "active" : ""}
      ${isEditing ? "editing" : ""} 
      ${isSelected ? "selected" : ""} 
      ${selectedPositions?.join(" ")}
    `;
  }, [cssClass, isActive, isEditing, isSelected, selectedPositions]);

  const cell = useMemo(() => {
    return row.cells[columnIndex];
  }, [columnIndex, row]);

  const tdContent = useMemo(() => {
    return shouldUseCellDelegate && column.cellDelegate
      ? column.cellDelegate?.(`cell-delegate-${rowIndex}`)
      : cell.render("Cell");
  }, [cell, rowIndex, shouldUseCellDelegate, column]);

  useEffect(() => {
    function onEnter(e: MouseEvent) {
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
  }, [column, columnIndex, row, rowIndex, setActiveCell]);

  useEffect(() => {
    function onDown(e: KeyboardEvent) {
      const set = e.shiftKey ? setSelectionEnd : setActiveCell;
      set({
        columnIndex,
        rowIndex,
        isEditing: false,
      });
    }

    function onDoubleClick() {
      setActiveCell({
        columnIndex,
        rowIndex,
        isEditing: true,
      });
    }

    const td = tdRef.current;
    td?.addEventListener("mousedown", onDown);
    td?.addEventListener("dblclick", onDoubleClick);
    return () => {
      td?.removeEventListener("dblclick", onDoubleClick);
      td?.removeEventListener("mousedown", onDown);
    };
  }, [column, columnIndex, row, rowIndex, setActiveCell, setSelectionEnd]);

  const onAddRowButtonClick = useCallback(
    (e: React.MouseEvent) => {
      if (!hoverInfo.isHovered) {
        return;
      }
      e.stopPropagation();

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
            bottom: "-10px",
          }
        : {
            top: "-10px",
          },
    [hoverInfo]
  );

  useEffect(() => {
    if (isActive && !isEditing) {
      tdRef.current?.focus();
    }
  }, [isActive, isEditing]);

  const onKeyDown = useCallback(
    (e: React.KeyboardEvent) => {
      if (NavigationKeysUtils.isDelete(e.key) || NavigationKeysUtils.isBackspace(e.key)) {
        e.preventDefault();
        if (isActive) {
          onCellUpdates?.([
            {
              columnIndex: columnIndex - 1,
              rowIndex: rowIndex,
              row: row.original,
              column: column,
              value: "", //FIXME: Tiago -> Need to parameterize this? e.g. Context expressions.
            },
          ]);
        }
      }
    },
    [column, columnIndex, isActive, onCellUpdates, row.original, rowIndex]
  );

  return (
    <PfReactTable.Td
      ref={tdRef}
      tabIndex={-1}
      className={cssClasses}
      data-ouia-component-id={`expression-column-${columnIndex}`} // FIXME: Tiago -> Bad name
      data-xposition={columnIndex}
      data-yposition={yPosition ?? rowIndex}
      style={style}
      onKeyDown={onKeyDown}
    >
      {column.isRowIndexColumn ? (
        <>{rowIndex + 1}</>
      ) : (
        <>
          <div
            style={{
              width: column.resizingWidth?.value,
              minWidth: cell.column.minWidth,
            }}
          >
            {tdContent}
          </div>
          {(hoverInfo.isHovered || cell.column.resizingWidth?.isPivoting) && (
            <Resizer
              minWidth={cell.column.minWidth}
              width={cell.column.width}
              setWidth={cell.column.setWidth}
              resizingWidth={cell.column.resizingWidth}
              setResizingWidth={cell.column.setResizingWidth}
            />
          )}
        </>
      )}

      {hoverInfo.isHovered && column.isRowIndexColumn && onRowAdded && (
        <div onClick={onAddRowButtonClick} className={"add-row-button"} style={addRowButtonStyle}>
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
