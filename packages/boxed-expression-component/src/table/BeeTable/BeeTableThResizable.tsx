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

import { PopoverPosition } from "@patternfly/react-core/dist/js/components/Popover";
import * as React from "react";
import { useCallback, useEffect, useMemo, useRef, useState } from "react";
import * as ReactTable from "react-table";
import { ExpressionDefinition } from "../../api";
import { ExpressionDefinitionHeaderMenu } from "../../expressions/ExpressionDefinitionHeaderMenu";
import { Resizer } from "../../resizing/Resizer";
import { useBeeTableResizableCell } from "../../resizing/BeeTableResizableColumnsContext";
import { BeeTableTh, getHoverInfo, HoverInfo } from "./BeeTableTh";
import { ResizerStopBehavior } from "../../resizing/ResizingWidthsContext";
import {
  getFlatListOfSubColumns,
  isFlexbileColumn,
  isParentColumn,
  useFillingResizingWidth,
} from "../../resizing/FillingColumnResizingWidth";
import { useBoxedExpressionEditor } from "../../expressions/BoxedExpressionEditor/BoxedExpressionEditorContext";

export interface BeeTableThResizableProps<R extends object> {
  onColumnAdded?: (args: { beforeIndex: number; groupType: string | undefined }) => void;
  column: ReactTable.ColumnInstance<R>;
  columnIndex: number;
  rowIndex: number;
  rowSpan: number;
  editColumnLabel?: string | { [groupType: string]: string };
  isEditableHeader: boolean;
  getColumnKey: (column: ReactTable.ColumnInstance<R>) => string;
  getColumnLabel: (groupType: string | undefined) => string | undefined;
  onExpressionHeaderUpdated: (args: Pick<ExpressionDefinition, "name" | "dataType">) => void;
  onHeaderClick: (columnKey: string) => () => void;
  reactTableInstance: ReactTable.TableInstance<R>;
  headerCellInfo: React.ReactElement;
  shouldShowColumnsInlineControls: boolean;
  resizerStopBehavior: ResizerStopBehavior;
  lastColumnMinWidth?: number;
  onGetWidthToFitData: () => number;
  forwardRef?: React.RefObject<HTMLTableCellElement>;
  shouldRenderRowIndexColumn: boolean;
}

export function BeeTableThResizable<R extends object>({
  column,
  columnIndex,
  rowIndex,
  shouldRenderRowIndexColumn,
  rowSpan,
  isEditableHeader,
  reactTableInstance,
  getColumnKey,
  onExpressionHeaderUpdated,
  onHeaderClick,
  headerCellInfo,
  onColumnAdded,
  resizerStopBehavior,
  shouldShowColumnsInlineControls,
  lastColumnMinWidth,
  onGetWidthToFitData,
  forwardRef,
}: BeeTableThResizableProps<R>) {
  const columnKey = useMemo(() => getColumnKey(column), [column, getColumnKey]);

  const headerCellRef = useRef<HTMLDivElement>(null);

  const cssClasses = useMemo(() => {
    const cssClasses = [columnKey, "data-header-cell"];
    if (!column.dataType) {
      cssClasses.push("no-clickable-cell");
    }

    cssClasses.push(column.groupType ?? "");
    // cssClasses.push(column.cssClasses ?? ""); // FIXME: Breaking Decision tables because of positioning of rowSpan=2 column headers (See https://github.com/kiegroup/kie-issues/issues/162)
    return cssClasses.join(" ");
  }, [columnKey, column.dataType, column.groupType]);

  const onClick = useMemo(() => {
    return onHeaderClick(columnKey);
  }, [columnKey, onHeaderClick]);

  const { resizingWidth, setResizingWidth } = useBeeTableResizableCell(
    columnIndex,
    resizerStopBehavior,
    column.width,
    column.setWidth,
    // If the column specifies a width, then we should respect its minWidth as well.
    column.width ? Math.max(lastColumnMinWidth ?? column.minWidth ?? 0, column.width ?? 0) : undefined
  );

  const getWidthToFitData = useCallback(() => {
    const extraSpace =
      2 + // 2px for compensate for th's borders
      16; // 16px for a nice padding

    return onGetWidthToFitData() + extraSpace;
  }, [onGetWidthToFitData]);

  const {
    // Filling resizing widths are used for header columns that are either parent or flexible.
    fillingResizingWidth,
    setFillingResizingWidth,
    fillingWidth,
    setFillingWidth,
    minFillingWidth,
  } = useFillingResizingWidth(columnIndex, column, reactTableInstance, shouldRenderRowIndexColumn);

  const [hoverInfo, setHoverInfo] = useState<HoverInfo>({ isHovered: false });
  const [isResizing, setResizing] = useState<boolean>(false);

  const { editorRef } = useBoxedExpressionEditor();

  useEffect(() => {
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
      setHoverInfo(() => getHoverInfo(e, th!));
    }

    function onMove(e: MouseEvent) {
      if (hasTextSelectedInBoxedExpressionEditor()) {
        return;
      }
      setHoverInfo(() => getHoverInfo(e, th!));
    }

    function onLeave() {
      setHoverInfo(() => ({ isHovered: false }));
    }

    const th = forwardRef?.current;
    th?.addEventListener("mouseenter", onEnter);
    th?.addEventListener("mousemove", onMove);
    th?.addEventListener("mouseleave", onLeave);
    return () => {
      th?.removeEventListener("mouseleave", onLeave);
      th?.removeEventListener("mousemove", onMove);
      th?.removeEventListener("mouseenter", onEnter);
    };
  }, [columnIndex, rowIndex, forwardRef, editorRef]);

  const getAppendToElement = useCallback(() => {
    return headerCellRef.current!;
  }, [headerCellRef, headerCellRef.current]);

  return (
    <BeeTableTh<R>
      forwardRef={forwardRef}
      className={cssClasses}
      thProps={{
        ...column.getHeaderProps(),
        style: {
          width: column.width ? resizingWidth?.value : "100%",
          minWidth: column.width ? resizingWidth?.value : "100%",
          maxWidth:
            isParentColumn(column) || isFlexbileColumn(column)
              ? fillingWidth
              : column.width
              ? resizingWidth?.value
              : "100%",
        },
      }}
      onClick={onClick}
      columnIndex={columnIndex}
      rowIndex={rowIndex}
      rowSpan={rowSpan}
      onColumnAdded={onColumnAdded}
      groupType={column.groupType}
      isLastLevelColumn={(column.columns?.length ?? 0) <= 0}
      shouldShowColumnsInlineControls={shouldShowColumnsInlineControls}
      column={column}
    >
      <div className="header-cell" data-ouia-component-type="expression-column-header" ref={headerCellRef}>
        {column.dataType && isEditableHeader ? (
          <ExpressionDefinitionHeaderMenu
            position={PopoverPosition.bottom}
            selectedExpressionName={column.label}
            selectedDataType={column.dataType}
            onExpressionHeaderUpdated={onExpressionHeaderUpdated}
            appendTo={getAppendToElement}
          >
            {headerCellInfo}
          </ExpressionDefinitionHeaderMenu>
        ) : (
          headerCellInfo
        )}
      </div>
      {/* resizingWidth. I.e., Exact-sized columns. */}
      {!column.isWidthConstant &&
        column.width &&
        resizingWidth &&
        (hoverInfo.isHovered || (resizingWidth?.isPivoting && isResizing)) && (
          <Resizer
            minWidth={lastColumnMinWidth ?? column.minWidth}
            width={column.width}
            setWidth={column.setWidth}
            resizingWidth={resizingWidth}
            setResizingWidth={setResizingWidth}
            getWidthToFitData={getWidthToFitData}
            setResizing={setResizing}
          />
        )}
      {/* fillingResizingWidth. I.e., Flexible or parent columns. */}
      {getFlatListOfSubColumns(column).some((c) => !(c.isWidthConstant ?? false)) &&
        (isFlexbileColumn(column) || isParentColumn(column)) &&
        (hoverInfo.isHovered || (fillingResizingWidth?.isPivoting && isResizing)) && (
          <Resizer
            minWidth={minFillingWidth}
            width={fillingWidth}
            setWidth={setFillingWidth}
            resizingWidth={fillingResizingWidth}
            setResizingWidth={setFillingResizingWidth}
            getWidthToFitData={getWidthToFitData}
            setResizing={setResizing}
          />
        )}
      {/* //FIXME: Don't know if that's a good idea yet. Please address it as part of https://github.com/kiegroup/kie-issues/issues/181 */}
      {/* {calcWidth && (hoverInfo.isHovered || (calcResizingWidth?.isPivoting && isCalcWidthResizing)) && (
        <Resizer
          minWidth={(column.columns ?? []).reduce((acc, { minWidth }) => acc + (minWidth ?? 0), 0)}
          width={calcWidth}
          setWidth={setCalcWidth}
          resizingWidth={calcResizingWidth}
          setResizingWidth={setCalcResizingWidth}
          getWidthToFitData={getWidthToFitData}
          setResizing={setCalcWidthResizing}
        />
      )} */}
    </BeeTableTh>
  );
}
