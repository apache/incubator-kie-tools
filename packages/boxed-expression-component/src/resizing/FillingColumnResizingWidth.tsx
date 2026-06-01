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

import * as React from "react";
import { useCallback, useEffect, useMemo, useState } from "react";
import * as ReactTable from "react-table";
import { useBeeTableResizableColumns, useBeeTableResizableColumnsDispatch } from "./BeeTableResizableColumnsContext";
import { apportionColumnWidths } from "./Hooks";
import { useNestedExpressionContainer } from "./NestedExpressionContainerContext";
import { ResizingWidth } from "./ResizingWidthsContext";
import { BEE_TABLE_ROW_INDEX_COLUMN_WIDTH } from "./WidthConstants";

export function useFillingResizingWidth(
  columnIndex: number,
  column: ReactTable.ColumnInstance<any>,
  reactTableInstance: ReactTable.TableInstance<any>,
  shouldRenderRowIndexColumn: boolean
) {
  const nestedExpressionContainer = useNestedExpressionContainer();

  const minFillingWidth = useMemo(
    () =>
      Math.max(
        nestedExpressionContainer.minWidth - (shouldRenderRowIndexColumn ? BEE_TABLE_ROW_INDEX_COLUMN_WIDTH : 0),
        sumColumnPropertyRecursively(column, "minWidth", nestedExpressionContainer.minWidth)
      ),
    [column, nestedExpressionContainer.minWidth, shouldRenderRowIndexColumn]
  );

  const fillingWidth = useMemo(
    () =>
      Math.max(
        nestedExpressionContainer.minWidth - (shouldRenderRowIndexColumn ? BEE_TABLE_ROW_INDEX_COLUMN_WIDTH : 0),
        sumColumnPropertyRecursively(column, "width", nestedExpressionContainer.actualWidth)
      ),
    [column, nestedExpressionContainer.actualWidth, nestedExpressionContainer.minWidth, shouldRenderRowIndexColumn]
  );

  // That's be be used for:
  // a) Flexible columns -> Terminal columns that don't have a width, and adapt to the size of their cells; or
  // b) Parent columns -> Columns with subColumns.
  const [fillingResizingWidth, setFillingResizingWidth] = useState({
    isPivoting: false,
    value: 0,
  });

  const { columnResizingWidths } = useBeeTableResizableColumns();
  const { updateColumnResizingWidths } = useBeeTableResizableColumnsDispatch();

  const totalColumnResizingWidth = useMemo(() => {
    return getTotalColumnResizingWidth(column, columnResizingWidths, reactTableInstance);
  }, [column, columnResizingWidths, reactTableInstance]);

  // KEEPING FILLING WIDTH IN SYNC
  useEffect(() => {
    setFillingResizingWidth((prev) => {
      if (prev.isPivoting) {
        return prev; // In this case, the resize handle from fillingResizingWidth is in use, therefore, we shouldn't interfere.
      } else if (prev.value === totalColumnResizingWidth.value) {
        return prev; // Skip updating if nothing changed.
      } else if (isFlexbileColumn(column)) {
        return { isPivoting: false, value: totalColumnResizingWidth.value }; // Something changed on a parent column or on the column's cells.
      } else if (isParentColumn(column)) {
        return { isPivoting: false, value: totalColumnResizingWidth.value }; // Something changed on sub columns or on the sub column's cells.
      } else {
        return prev; // Ignore
      }
    });
  }, [column, totalColumnResizingWidth]);

  // FILLING WIDTH HAS STOPPED PIVOTING
  const setFillingWidth = useCallback(
    (newWidth: number) => {
      if (isFlexbileColumn(column)) {
        updateColumnResizingWidths(new Map([[columnIndex, { isPivoting: false, value: newWidth }]]));
      }

      if (isParentColumn(column)) {
        const newColumnResizingWidths = computeNewSubColumnResizingWidths({
          isPivoting: false,
          newTotalWidth: newWidth,
          reactTableInstance,
          column,
        });

        updateColumnResizingWidths(newColumnResizingWidths);
      }
    },
    [column, columnIndex, reactTableInstance, updateColumnResizingWidths]
  );

  // FILLING WIDTH IS NOT PIVOTING
  useEffect(() => {
    if (fillingResizingWidth.isPivoting) {
      return;
    }

    if (isFlexbileColumn(column)) {
      updateColumnResizingWidths(
        new Map([
          [
            columnIndex,
            {
              isPivoting: totalColumnResizingWidth.isPivoting, // Keep whatever this column has.
              value: nestedExpressionContainer.resizingWidth.value,
            },
          ],
        ])
      );
    }
  }, [
    column,
    columnIndex,
    fillingResizingWidth.isPivoting,
    nestedExpressionContainer.resizingWidth.value,
    totalColumnResizingWidth.isPivoting,
    updateColumnResizingWidths,
  ]);

  // FILLING WIDTH IS PIVOTING
  useEffect(() => {
    if (!fillingResizingWidth.isPivoting) {
      return;
    }

    // Flexible column.
    if (isFlexbileColumn(column)) {
      updateColumnResizingWidths(new Map([[columnIndex, fillingResizingWidth]]));
      return;
    }

    // Parent column.
    if (isParentColumn(column)) {
      const newColumnResizingWidths = computeNewSubColumnResizingWidths({
        isPivoting: true,
        newTotalWidth: fillingResizingWidth.value,
        reactTableInstance,
        column,
      });

      updateColumnResizingWidths(newColumnResizingWidths);
    }
  }, [column, columnIndex, fillingResizingWidth, reactTableInstance, updateColumnResizingWidths]);

  return { fillingResizingWidth, setFillingResizingWidth, minFillingWidth, fillingWidth, setFillingWidth };
}

// Supporting functions

export function sumColumnPropertyRecursively(
  column: ReactTable.ColumnInstance<any>,
  property: "width" | "minWidth",
  containerValue: number
): number {
  // Flexible column
  if (isFlexbileColumn(column)) {
    return containerValue ?? column.minWidth ?? 0;
  }

  // Parent column
  if (isParentColumn(column)) {
    return (column.columns ?? []).reduce(
      (acc, c) => acc + sumColumnPropertyRecursively(c, property, containerValue),
      0
    );
  }

  // Exact column
  return column[property] ?? column.minWidth ?? 0;
}

export function findIndexOfColumn(
  column: ReactTable.Column<any> | undefined,
  reactTableInstance: ReactTable.TableInstance<any>
) {
  return reactTableInstance.allColumns.findIndex(({ id }) => id === column?.id);
}

export function getTotalColumnResizingWidth(
  column: ReactTable.ColumnInstance<any>,
  columnResizingWidths: Map<number, ResizingWidth | undefined>,
  reactTableInstance: ReactTable.TableInstance<any>
) {
  const flatListOfSubColumns = getFlatListOfSubColumns(column);
  const indexOfFirstSubColumn = findIndexOfColumn(flatListOfSubColumns[0], reactTableInstance);

  let value = 0;
  let isPivoting = false;
  flatListOfSubColumns.forEach((_, index) => {
    const resizingWidth = columnResizingWidths.get(indexOfFirstSubColumn + index);
    value += resizingWidth?.value ?? 0;
    isPivoting = isPivoting || (resizingWidth?.isPivoting ?? false);
  });

  return { isPivoting, value };
}

export function getFlatListOfSubColumns(column: ReactTable.Column<any>): ReactTable.Column<any>[] {
  if (isParentColumn(column)) {
    return (column.columns ?? []).flatMap((c) => getFlatListOfSubColumns(c));
  }

  return [column];
}

export function isParentColumn(column: ReactTable.Column<any>) {
  return (column.columns?.length ?? 0) > 0;
}

export function isFlexbileColumn(column: ReactTable.Column<any>) {
  return !column.width && !isParentColumn(column);
}

export function computeNewSubColumnResizingWidths(args: {
  reactTableInstance: ReactTable.TableInstance<any>;
  column: ReactTable.ColumnInstance<any>;
  isPivoting: boolean;
  newTotalWidth: number;
}) {
  const flatListOfSubColumns = getFlatListOfSubColumns(args.column);
  const indexOfFirstSubColumn = findIndexOfColumn(flatListOfSubColumns[0], args.reactTableInstance);

  const subColumns = flatListOfSubColumns.map(({ minWidth, width, isWidthPinned }) => ({
    minWidth: minWidth ?? 0,
    currentWidth: width ?? minWidth ?? 0,
    isFrozen: isWidthPinned ?? false,
  }));

  const fixedWidthAmount = subColumns.reduce(
    (acc, { isFrozen, currentWidth, minWidth }) => (isFrozen ? acc + (currentWidth ?? minWidth) : acc),
    0
  );

  const nextTotalWidth = args.newTotalWidth - fixedWidthAmount;
  const apportionedWidths = apportionColumnWidths(nextTotalWidth, subColumns);

  const newColumnResizingWidths = apportionedWidths.reduce((acc, nextWidth, index) => {
    const columnIndex = indexOfFirstSubColumn + index;
    if (subColumns[index]?.isFrozen) {
      return acc; // Skip updating frozen columns.
    }

    acc.set(columnIndex, { isPivoting: args.isPivoting, value: nextWidth });
    return acc;
  }, new Map());

  return newColumnResizingWidths;
}
