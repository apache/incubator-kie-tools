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
import { useCellWidthToFitData } from "../../resizing/BeeTableCellWidthToFitDataContext";
import { useBeeTableResizableCell } from "../../resizing/BeeTableResizableColumnsContext";
import { Resizer } from "../../resizing/Resizer";
import { ResizerStopBehavior } from "../../resizing/ResizingWidthsContext";
import {
  BeeTableCellCoordinates,
  BeeTableCoordinatesContextProvider,
  useBeeTableSelectableCell,
  useBeeTableSelectableCellRef,
} from "../../selection/BeeTableSelectionContext";
import { useBoxedExpressionEditor } from "../../BoxedExpressionEditorContext";
import { InsertRowColumnsDirection } from "../../api";
import { Icon } from "@patternfly/react-core/dist/js/components/Icon";

export interface BeeTableTdProps<R extends object> {
  // Individual cells are not immutable references, By referencing the row, we avoid multiple re-renders and bugs.
  onRowAdded?: (args: { beforeIndex: number; rowsCount: number; insertDirection: InsertRowColumnsDirection }) => void;
  isActive: boolean;
  shouldRenderInlineButtons: boolean;
  shouldShowRowsInlineControls: boolean;
  rowIndex: number;
  row: ReactTable.Row<R>;
  columnIndex: number;
  column: ReactTable.ColumnInstance<R>;
  resizerStopBehavior: ResizerStopBehavior;
  lastColumnMinWidth?: number;
  onDataCellClick?: (columnID: string) => void;
  onDataCellKeyUp?: (columnID: string) => void;
  isReadOnly: boolean;
  /** True means the table cell can display evaluation hits count. False means evaluation hits count is not displayed in the table cell. */
  canDisplayEvaluationHitsCountBadge?: boolean;
  /** Actuall evaluation hits count number that will be displayed in the table cell if 'canDisplayEvaluationHitsCountBadge' is set to true. */
  evaluationHitsCount?: number;
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
  shouldRenderInlineButtons,
  shouldShowRowsInlineControls,
  resizerStopBehavior,
  onRowAdded,
  lastColumnMinWidth,
  onDataCellClick,
  onDataCellKeyUp,
  isReadOnly,
  canDisplayEvaluationHitsCountBadge,
  evaluationHitsCount,
}: BeeTableTdProps<R>) {
  const [isResizing, setResizing] = useState(false);
  const [hoverInfo, setHoverInfo] = useState<HoverInfo>({ isHovered: false });

  const tdRef = useRef<HTMLTableCellElement>(null);

  const cssClass = column.isRowIndexColumn ? "row-index-column-cell" : "data-cell";

  const cell = useMemo(() => {
    return row.cells[columnIndex];
  }, [columnIndex, row]);

  const cellWidthToFitDataRef = useCellWidthToFitData(rowIndex, columnIndex);

  const { resizingWidth, setResizingWidth } = useBeeTableResizableCell(
    columnIndex,
    resizerStopBehavior,
    column.width,
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

  const { isActive } = useBeeTableSelectableCellRef(rowIndex, columnIndex, undefined);

  // FIXME: The BeeTable shouldn't know about DMN or GWT
  // The following useEffect shouldn't be placed here.
  const { beeGwtService, editorRef } = useBoxedExpressionEditor();
  useEffect(() => {
    if (isActive) {
      if (column.isRowIndexColumn) {
        beeGwtService?.selectObject("");
      }
    }
  }, [beeGwtService, isActive, column.isRowIndexColumn, cell.value]);

  useEffect(() => {
    const td = tdRef.current;

    function hasTextSelectedInBoxedExpressionEditor() {
      const selection = window.getSelection();
      if (selection) {
        return selection?.toString() && editorRef.current?.contains(selection.focusNode);
      }
      return false;
    }

    function onEnter(e: MouseEvent) {
      e.stopPropagation();
      if (hasTextSelectedInBoxedExpressionEditor()) {
        return;
      }
      setHoverInfo((prev) => getHoverInfo(e, td!));
    }

    function onMove(e: MouseEvent) {
      if (hasTextSelectedInBoxedExpressionEditor()) {
        return;
      }
      setHoverInfo((prev) => getHoverInfo(e, td!));
    }

    function onLeave() {
      setHoverInfo((prev) => ({ isHovered: false }));
    }
    td?.addEventListener("mouseenter", onEnter);
    td?.addEventListener("mousemove", onMove);
    td?.addEventListener("mouseleave", onLeave);
    return () => {
      td?.removeEventListener("mouseleave", onLeave);
      td?.removeEventListener("mousemove", onMove);
      td?.removeEventListener("mouseenter", onEnter);
    };
  }, [editorRef]);

  const onAddRowButtonClick = useCallback(
    (e: React.MouseEvent) => {
      e.stopPropagation();

      if (!hoverInfo.isHovered) {
        return;
      }

      onRowAdded?.({
        beforeIndex: hoverInfo.part === "upper" ? rowIndex : rowIndex + 1,
        rowsCount: 1,
        insertDirection: InsertRowColumnsDirection.BelowOrLeft,
      });

      if (hoverInfo.part === "upper") {
        setHoverInfo({ isHovered: false });
      }
    },
    [hoverInfo, onRowAdded, rowIndex]
  );

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

  const { cssClasses, onMouseDown, onDoubleClick } = useBeeTableSelectableCell(
    tdRef,
    rowIndex,
    columnIndex,
    undefined,
    getValue
  );

  const tdContent = useMemo(() => {
    return cell.render("Cell");
  }, [cell]);

  const shouldRenderResizer = useMemo(
    () => !column.isWidthConstant && (hoverInfo.isHovered || isActive || (resizingWidth?.isPivoting && isResizing)),
    [column.isWidthConstant, hoverInfo.isHovered, isActive, isResizing, resizingWidth?.isPivoting]
  );

  const onClick = useCallback(() => {
    return onDataCellClick?.(column.id);
  }, [column.id, onDataCellClick]);

  const onKeyUp = useCallback(() => {
    return onDataCellKeyUp?.(column.id);
  }, [column.id, onDataCellKeyUp]);

  const evaluationHitsCountBadgeClassName = useMemo(() => {
    return canDisplayEvaluationHitsCountBadge
      ? (evaluationHitsCount ?? 0) > 0
        ? "evaluation-hits-count-badge-colored"
        : "evaluation-hits-count-badge-non-colored"
      : "";
  }, [canDisplayEvaluationHitsCountBadge, evaluationHitsCount]);

  return (
    <BeeTableCoordinatesContextProvider coordinates={coordinates}>
      <td
        onMouseDown={onMouseDown}
        onDoubleClick={isReadOnly ? undefined : onDoubleClick}
        onClick={onClick}
        onKeyUp={isReadOnly ? undefined : onKeyUp}
        ref={tdRef}
        tabIndex={-1}
        className={`${cssClass} ${cssClasses} ${column.cssClasses}`}
        data-testid={`kie-tools--bee--expression-column-${columnIndex}`}
        data-ouia-component-id={`expression-column-${columnIndex}`}
        style={{
          outline: "none",
          minHeight: `60px`,
          width: column.width ? resizingWidth?.value : "100%",
          minWidth: column.width ? resizingWidth?.value : "100%",
          maxWidth: column.width ? resizingWidth?.value : "100%",
        }}
      >
        {column.isRowIndexColumn ? (
          <div className={evaluationHitsCountBadgeClassName} data-evaluation-hits-count={evaluationHitsCount}>
            {rowIndexLabel}
          </div>
        ) : (
          <div className={evaluationHitsCountBadgeClassName} data-evaluation-hits-count={evaluationHitsCount}>
            {tdContent}

            {!isReadOnly && shouldRenderResizer && (
              <Resizer
                getWidthToFitData={cellWidthToFitDataRef?.getWidthToFitData}
                minWidth={lastColumnMinWidth ?? cell.column.minWidth}
                width={cell.column.width}
                setWidth={cell.column.setWidth}
                resizingWidth={resizingWidth}
                setResizingWidth={setResizingWidth}
                setResizing={setResizing}
              />
            )}
          </div>
        )}

        {!isReadOnly &&
          hoverInfo.isHovered &&
          shouldRenderInlineButtons &&
          onRowAdded &&
          shouldShowRowsInlineControls && (
            <div
              style={{
                display: "flex",
                justifyContent: "center",
              }}
            >
              <div
                onMouseDown={(e) => e.stopPropagation()}
                onDoubleClick={(e) => e.stopPropagation()}
                onClick={onAddRowButtonClick}
                className={"add-row-button"}
                style={addRowButtonStyle}
              >
                <Icon size="sm">
                  <PlusIcon />
                </Icon>
              </div>
            </div>
          )}
      </td>
    </BeeTableCoordinatesContextProvider>
  );
}

function getHoverInfo(e: MouseEvent, elem: HTMLElement): HoverInfo {
  const rect = elem.getBoundingClientRect();
  const localY = e.clientY - rect.top; // y position within the element.
  const part = localY < rect.height / 3 ? "upper" : "lower"; // upper part is the upper third
  return { isHovered: true, part };
}
