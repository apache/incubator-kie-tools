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
import { useCallback, useEffect, useMemo, useState } from "react";
import * as ReactTable from "react-table";
import { ExpressionDefinition } from "../../api";
import { ExpressionDefinitionHeaderMenu } from "../../expressions/ExpressionDefinitionHeaderMenu";
import { Resizer } from "../../resizing/Resizer";
import { useBeeTableResizableCell } from "../../resizing/BeeTableResizableColumnsContext";
import { BeeTableTh, getHoverInfo, HoverInfo } from "./BeeTableTh";
import { ResizerStopBehavior } from "../../resizing/ResizingWidthsContext";
import { isFlexbileColumn, isParentColumn, useFillingResizingWidth } from "../../resizing/FillingColumnResizingWidth";

export interface BeeTableThResizableProps<R extends object> {
  onColumnAdded?: (args: { beforeIndex: number; groupType: string | undefined }) => void;
  column: ReactTable.ColumnInstance<R>;
  columnIndex: number;
  rowIndex: number;
  firstColumnIndexOfGroup: number;
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
}

export function BeeTableThResizable<R extends object>({
  column,
  columnIndex,
  rowIndex,
  firstColumnIndexOfGroup,
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

  const cssClasses = useMemo(() => {
    const cssClasses = [columnKey, "data-header-cell"];
    if (!column.dataType) {
      cssClasses.push("no-clickable-cell");
    }

    cssClasses.push(column.groupType ?? "");
    // cssClasses.push(column.cssClasses ?? ""); // FIXME: Tiago -> Breaking Decision tables because of positioning of rowSpan=2 column headers
    return cssClasses.join(" ");
  }, [column, columnKey]);

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

  // const { updateColumnResizingWidths } = useBeeTableResizableColumnsDispatch();
  // const [isCalcWidthResizing, setCalcWidthResizing] = useState<boolean>(false);
  // const [calcWidth, setCalcWidth] = useState<number | undefined>(undefined);
  // const [calcResizingWidth, setCalcResizingWidth] = useState<ResizingWidth | undefined>(undefined);

  // useLayoutEffect(() => {
  //   if (column.width) {
  //     return;
  //   }

  //   const calcWidth = forwardRef?.current?.getBoundingClientRect().width;
  //   if (!calcWidth) {
  //     return;
  //   }

  //   setCalcWidth(calcWidth);
  //   setCalcResizingWidth({ isPivoting: false, value: calcWidth ?? 0 });
  // }, [column.width, forwardRef]);

  // useEffect(() => {
  //   if (!calcResizingWidth?.isPivoting) {
  //     return;
  //   }

  //   const apportionedColumnWidths = apportionColumnWidths(
  //     calcResizingWidth.value,
  //     (column.columns ?? []).map((c) => ({
  //       minWidth: c.minWidth ?? 0,
  //       currentWidth: c.width ?? 0,
  //       isFrozen: c.isWidthPinned ?? false,
  //     }))
  //   );

  //   (column.columns ?? []).forEach((c, i) => {
  //     updateColumnResizingWidths(firstColumnIndexOfGroup + i, (prev) => {
  //       return { isPivoting: true, value: apportionedColumnWidths[i] };
  //     });
  //   });
  // }, [
  //   calcResizingWidth?.isPivoting,
  //   calcResizingWidth?.value,
  //   column.columns,
  //   columnIndex,
  //   firstColumnIndexOfGroup,
  //   updateColumnResizingWidths,
  // ]);

  const {
    // Filling resizing widths are used for header columns that are either parent or flexible.
    fillingResizingWidth,
    setFillingResizingWidth,
    fillingWidth,
    setFillingWidth,
    minFillingWidth,
  } = useFillingResizingWidth(columnIndex, column, reactTableInstance);

  const [hoverInfo, setHoverInfo] = useState<HoverInfo>({ isHovered: false });
  const [isResizing, setResizing] = useState<boolean>(false);

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

    const th = forwardRef?.current;
    th?.addEventListener("mouseenter", onEnter);
    th?.addEventListener("mousemove", onMove);
    th?.addEventListener("mouseleave", onLeave);
    return () => {
      th?.removeEventListener("mouseleave", onLeave);
      th?.removeEventListener("mousemove", onMove);
      th?.removeEventListener("mouseenter", onEnter);
    };
  }, [columnIndex, rowIndex, forwardRef]);

  return (
    <BeeTableTh<R>
      forwardRef={forwardRef}
      className={cssClasses}
      thProps={{
        ...column.getHeaderProps(),
        style: {
          width: column.width ? resizingWidth?.value : "100%",
          minWidth: column.width ? resizingWidth?.value : "100%",
          maxWidth: column.width ? resizingWidth?.value : "100%",
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
      <div className="header-cell" data-ouia-component-type="expression-column-header">
        {column.dataType && isEditableHeader ? (
          <ExpressionDefinitionHeaderMenu
            position={PopoverPosition.bottom}
            selectedExpressionName={column.label}
            selectedDataType={column.dataType}
            onExpressionHeaderUpdated={onExpressionHeaderUpdated}
          >
            {headerCellInfo}
          </ExpressionDefinitionHeaderMenu>
        ) : (
          headerCellInfo
        )}
      </div>
      {/* resizingWidth. I.e., Exact-sized columns. */}
      {column.width && resizingWidth && (hoverInfo.isHovered || (resizingWidth?.isPivoting && isResizing)) && (
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
      {(isFlexbileColumn(column) || isParentColumn(column)) &&
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
      {/* //FIXME: Tiago -> Don't know if that's a good idea yet. */}
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
