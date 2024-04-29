/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

import PlusIcon from "@patternfly/react-icons/dist/js/icons/plus-icon";
import * as React from "react";
import { useCallback, useEffect, useMemo, useRef, useState } from "react";
import * as ReactTable from "react-table";
import {
  BeeTableCellCoordinates,
  BeeTableCoordinatesContextProvider,
  useBeeTableSelectableCell,
  useBeeTableSelectableCellRef,
} from "../../selection/BeeTableSelectionContext";
import { useBoxedExpressionEditor } from "../../BoxedExpressionEditorContext";
import { InsertRowColumnsDirection } from "../../api";

export interface BeeTableThProps<R extends object> {
  groupType: string | undefined;
  onColumnAdded?: (args: {
    beforeIndex: number;
    currentIndex: number;
    groupType: string | undefined;
    columnsCount: number;
    insertDirection: InsertRowColumnsDirection;
  }) => void;
  className: string;
  thProps: Partial<ReactTable.TableHeaderProps>;
  onClick?: React.MouseEventHandler;
  onHeaderKeyUp?: React.KeyboardEventHandler;
  isLastLevelColumn: boolean;
  rowIndex: number;
  rowSpan: number;
  columnKey: string;
  columnIndex: number;
  column: ReactTable.ColumnInstance<R>;
  shouldShowColumnsInlineControls: boolean;
  forwardRef?: React.RefObject<HTMLTableCellElement>;
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
  forwardRef,
  onColumnAdded,
  children,
  className,
  thProps,
  onClick,
  onHeaderKeyUp,
  columnIndex,
  columnKey,
  rowIndex,
  rowSpan,
  groupType,
  column,
  isLastLevelColumn,
  shouldShowColumnsInlineControls: shouldShowRowsInlineControls,
}: React.PropsWithChildren<BeeTableThProps<R>>) {
  const [hoverInfo, setHoverInfo] = useState<HoverInfo>({ isHovered: false });

  const onAddColumnButtonClick = useCallback(
    (e: React.MouseEvent) => {
      e.stopPropagation();

      if (!hoverInfo.isHovered) {
        return;
      }

      // This index doesn't take into account the rowIndex column, so we actually need to subtract 1.
      onColumnAdded?.({
        beforeIndex: hoverInfo.part === "left" ? columnIndex - 1 : columnIndex,
        groupType: groupType,
        columnsCount: 1,
        insertDirection:
          hoverInfo.part === "left" ? InsertRowColumnsDirection.BelowOrLeft : InsertRowColumnsDirection.AboveOrRight,
        currentIndex: columnIndex,
      });

      if (hoverInfo.part === "left") {
        setHoverInfo({ isHovered: false });
      }
    },
    [columnIndex, groupType, hoverInfo, onColumnAdded]
  );

  const { isActive } = useBeeTableSelectableCellRef(rowIndex, columnIndex, undefined);

  // FIXME: The BeeTable shouldn't know about DMN or GWT
  // The following useEffect shouldn't be placed here.
  const { beeGwtService } = useBoxedExpressionEditor();
  useEffect(() => {
    if (isActive) {
      if (column.isRowIndexColumn || groupType === "annotation") {
        beeGwtService?.selectObject("");
      } else {
        beeGwtService?.selectObject(columnKey);
      }
    }
  }, [beeGwtService, column.isRowIndexColumn, columnKey, groupType, isActive]);

  const _thRef = useRef<HTMLTableCellElement>(null);
  const thRef = forwardRef ?? _thRef;

  useEffect(() => {
    function onEnter(e: MouseEvent) {
      e.stopPropagation();
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
  }, [thRef]);

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

  const { cssClasses, onMouseDown, onDoubleClick } = useBeeTableSelectableCell(
    thRef,
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

  const coordinates = useMemo<BeeTableCellCoordinates>(
    () => ({
      rowIndex,
      columnIndex,
    }),
    [columnIndex, rowIndex]
  );

  return (
    <BeeTableCoordinatesContextProvider coordinates={coordinates}>
      <th
        rowSpan={rowSpan}
        {...thProps}
        style={{ ...thProps.style, display: "table-cell" }}
        ref={thRef}
        onMouseDown={onMouseDown}
        onDoubleClick={onDoubleClick}
        onClick={onClick}
        onKeyUp={onHeaderKeyUp}
        className={`${className} ${cssClasses}`}
        tabIndex={-1}
      >
        {children}
        {hoverInfo.isHovered && onColumnAdded && isLastLevelColumn && shouldShowRowsInlineControls && (
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
      </th>
    </BeeTableCoordinatesContextProvider>
  );
}

export function getHoverInfo(e: MouseEvent, elem: HTMLElement): HoverInfo {
  const rect = elem.getBoundingClientRect();
  const localX = e.clientX - rect.left; // x position within the element.
  const part = localX < rect.width / 2 ? "left" : "right"; // upper part is the upper half
  return { isHovered: true, part };
}
