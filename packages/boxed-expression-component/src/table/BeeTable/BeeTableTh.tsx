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
import PlusIcon from "@patternfly/react-icons/dist/js/icons/plus-icon";
import { useBeeTableCell, useBeeTableSelectionDispatch } from "./BeeTableSelectionContext";
import { BeeTableThProps } from "../../api";

export interface BeeTableThProps2<R extends object> extends BeeTableThProps<R> {
  groupType: string | undefined;
  onColumnAdded?: (args: { beforeIndex: number; groupType: string | undefined }) => void;
  className: string;
  thProps: Partial<PfReactTable.ThProps>;
  onClick?: React.MouseEventHandler;
  isLastLevelColumn: boolean;
  rowIndex: number;
}

export type HoverInfo =
  | {
      isHovered: false;
    }
  | {
      isHovered: true;
      part: "left" | "right";
    };

export function BeeTableTh<R extends object>({
  onColumnAdded,
  children,
  className,
  thProps,
  onClick,
  columnIndex,
  rowIndex,
  groupType,
  column,
  isLastLevelColumn,
}: React.PropsWithChildren<BeeTableThProps2<R>>) {
  const { resetSelectionAt, setSelectionEnd } = useBeeTableSelectionDispatch();
  const thRef = useRef<HTMLTableCellElement>(null);

  const [hoverInfo, setHoverInfo] = useState<HoverInfo>({ isHovered: false });

  const onAddColumnButtonClick = useCallback(
    (e: React.MouseEvent) => {
      e.stopPropagation();

      if (!hoverInfo.isHovered) {
        return;
      }

      // This index doesn't take into account the rowIndex column, so we actually need to subtract 1.
      onColumnAdded?.({ beforeIndex: hoverInfo.part === "left" ? columnIndex - 1 : columnIndex, groupType: groupType });

      if (hoverInfo.part === "left") {
        setHoverInfo({ isHovered: false });
      }
    },
    [columnIndex, groupType, hoverInfo, onColumnAdded]
  );

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

      setHoverInfo((prev) => getHoverInfo(e, th!));
    }

    function onMove(e: MouseEvent) {
      setHoverInfo((prev) => getHoverInfo(e, th!));
    }

    function onLeave() {
      setHoverInfo((prev) => ({ isHovered: false }));
    }

    const th = thRef.current;
    th?.addEventListener("mouseenter", onEnter);
    th?.addEventListener("mousemove", onMove);
    th?.addEventListener("mouseleave", onLeave);
    return () => {
      th?.removeEventListener("mouseleave", onLeave);
      th?.removeEventListener("mousemove", onMove);
      th?.removeEventListener("mouseenter", onEnter);
    };
  }, [columnIndex, rowIndex, resetSelectionAt, setSelectionEnd]);

  const addColumButtonStyle = useMemo(
    () =>
      hoverInfo.isHovered && hoverInfo.part === "left"
        ? {
            left: "-9px",
          }
        : {
            right: "-10px",
          },
    [hoverInfo]
  );

  const { isActive, isEditing, isSelected, selectedPositions } = useBeeTableCell(
    rowIndex,
    columnIndex,
    undefined,
    useCallback(() => {
      if (column.dataType) {
        return `${column.label} (${column.dataType})`;
      } else {
        return column.label;
      }
    }, [column.dataType, column.label])
  );

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

  useLayoutEffect(() => {
    if (isActive && !isEditing) {
      thRef.current?.focus();
    }
  }, [isActive, isEditing]);

  const cssClasses = useMemo(() => {
    return `
        ${className}
        ${isActive ? "active" : ""}
        ${isEditing ? "editing" : ""}
        ${isSelected ? "selected" : ""}
        ${selectedPositions?.join(" ") ?? ""}
      `;
  }, [className, isActive, isEditing, isSelected, selectedPositions]);

  return (
    <PfReactTable.Th
      {...thProps}
      style={{ ...thProps.style, display: "table-cell" }}
      ref={thRef}
      onMouseDown={onMouseDown}
      onDoubleClick={onDoubleClick}
      onClick={onClick}
      className={cssClasses}
      tabIndex={-1}
    >
      {children}
      {hoverInfo.isHovered && onColumnAdded && isLastLevelColumn && (
        <div
          onMouseDown={(e) => e.stopPropagation()}
          onDoubleClick={(e) => e.stopPropagation()}
          onClick={onAddColumnButtonClick}
          className={"add-column-button"}
          style={addColumButtonStyle}
        >
          <PlusIcon size="sm" />
        </div>
      )}
    </PfReactTable.Th>
  );
}

function getHoverInfo(e: MouseEvent, elem: HTMLElement): HoverInfo {
  const rect = elem.getBoundingClientRect();
  const localX = e.clientX - rect.left; // x position within the element.
  const part = localX < rect.width / 2 ? "left" : "right"; // upper part is the upper half
  return { isHovered: true, part };
}
