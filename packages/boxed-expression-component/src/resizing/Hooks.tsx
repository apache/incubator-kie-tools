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

import { useEffect, useMemo, useState } from "react";
import { BoxedExpression } from "../api";
import { BeeTableRef } from "../table/BeeTable";
import {
  NestedExpressionContainerContextType,
  useNestedExpressionContainer,
  usePivotAwareNestedExpressionContainer,
} from "./NestedExpressionContainerContext";
import { ResizingWidth, useResizingWidths, useResizingWidthsDispatch } from "./ResizingWidthsContext";
import { RELATION_EXPRESSION_COLUMN_MIN_WIDTH } from "./WidthConstants";
import { getExpressionMinWidth, getExpressionResizingWidth } from "./WidthMaths";

export function useNestedExpressionResizingWidthValue(
  isPivoting: boolean,
  nestedExpressions: BoxedExpression[],
  fixedColumnActualWidth: number,
  fixedColumnResizingWidth: ResizingWidth,
  fixedColumnMinWidth: number,
  nestedExpressionMinWidth: number,
  extraWidth: number,
  widthsById: Map<string, number[]>,
  nestedExpressionsExtraWidths?: Map<string, number>
) {
  const { resizingWidths } = useResizingWidths();
  const nestedExpressionContainer = useNestedExpressionContainer();
  const pivotAwareNestedExpressionContainer = usePivotAwareNestedExpressionContainer(isPivoting);

  return useMemo<number>(() => {
    if (nestedExpressionContainer.resizingWidth.isPivoting && !isPivoting) {
      return nestedExpressionContainer.resizingWidth.value - fixedColumnResizingWidth.value - extraWidth;
    }

    const nestedPivotingExpression: BoxedExpression | undefined = nestedExpressions.filter(
      (e) => resizingWidths.get(e?.["@_id"] ?? "")?.isPivoting ?? false
    )[0];

    if (nestedPivotingExpression) {
      return Math.max(
        getExpressionResizingWidth(nestedPivotingExpression, resizingWidths, widthsById) +
          (nestedExpressionsExtraWidths?.get(nestedPivotingExpression["@_id"]!) ?? 0),
        fixedColumnMinWidth
      );
    }

    const nestedExpressionContainerResizingWidthValue =
      fixedColumnResizingWidth.value >= fixedColumnActualWidth
        ? pivotAwareNestedExpressionContainer.resizingWidth.value
        : nestedExpressionContainer.actualWidth;

    return Math.max(
      nestedExpressionContainerResizingWidthValue - fixedColumnResizingWidth.value - extraWidth,
      ...nestedExpressions.map((e) => getExpressionResizingWidth(e, new Map(), widthsById)),
      nestedExpressionMinWidth
    );
  }, [
    nestedExpressionContainer.resizingWidth.isPivoting,
    nestedExpressionContainer.resizingWidth.value,
    nestedExpressionContainer.actualWidth,
    isPivoting,
    nestedExpressions,
    fixedColumnResizingWidth.value,
    fixedColumnActualWidth,
    pivotAwareNestedExpressionContainer.resizingWidth.value,
    extraWidth,
    nestedExpressionMinWidth,
    resizingWidths,
    widthsById,
    fixedColumnMinWidth,
    nestedExpressionsExtraWidths,
  ]);
}

export function useNestedExpressionMinWidth(
  nestedExpressions: BoxedExpression[],
  fixedColumnResizingWidth: ResizingWidth,
  nestedExpressionMinWidth: number,
  extraWidth: number
) {
  const nestedExpressionContainer = useNestedExpressionContainer();
  return useMemo(() => {
    return Math.max(
      nestedExpressionContainer.minWidth - fixedColumnResizingWidth.value - extraWidth,
      ...nestedExpressions.map((e) => getExpressionMinWidth(e)),
      nestedExpressionMinWidth
    );
  }, [
    nestedExpressionMinWidth,
    fixedColumnResizingWidth.value,
    extraWidth,
    nestedExpressionContainer.minWidth,
    nestedExpressions,
  ]);
}

export function useNestedExpressionActualWidth(
  nestedExpressions: BoxedExpression[],
  fixedColumnActualWidth: number,
  extraWidth: number,
  widthsById: Map<string, number[]>
) {
  const nestedExpressionContainer = useNestedExpressionContainer();
  const { resizingWidths } = useResizingWidths();

  return useMemo<number>(() => {
    return Math.max(
      nestedExpressionContainer.actualWidth - fixedColumnActualWidth - extraWidth,
      ...nestedExpressions
        .filter((e) => !(resizingWidths.get(e?.["@_id"] ?? "")?.isPivoting ?? false))
        .map((expression) => getExpressionResizingWidth(expression, new Map(), widthsById))
    );
  }, [
    nestedExpressionContainer.actualWidth,
    fixedColumnActualWidth,
    extraWidth,
    nestedExpressions,
    resizingWidths,
    widthsById,
  ]);
}

export function useNestedExpressionContainerWithNestedExpressions({
  nestedExpressions,
  fixedColumnActualWidth,
  fixedColumnResizingWidth,
  fixedColumnMinWidth,
  nestedExpressionMinWidth,
  extraWidth,
  expression,
  flexibleColumnIndex,
  widthsById,
  nestedExpressionsExtraWidths,
}: {
  nestedExpressions: BoxedExpression[];
  fixedColumnActualWidth: number;
  fixedColumnResizingWidth: ResizingWidth;
  fixedColumnMinWidth: number;
  nestedExpressionMinWidth: number;
  extraWidth: number;
  expression: BoxedExpression;
  flexibleColumnIndex: number;
  widthsById: Map<string, number[]>;
  nestedExpressionsExtraWidths?: Map<string, number>;
}) {
  const nestedExpressionContainer = useNestedExpressionContainer();

  const [flexibleColumnResizingWidth, setFlexibleColumnResizingWidth] = useState({
    isPivoting: false,
    value: 0,
  });

  const onColumnResizingWidthChange = useMemo(() => {
    return (args: Map<number, ResizingWidth | undefined>) => {
      const newResizingWidth = args.get(flexibleColumnIndex);
      if (newResizingWidth) {
        setFlexibleColumnResizingWidth(newResizingWidth);
      }
    };
  }, [flexibleColumnIndex]);

  const { resizingWidths } = useResizingWidths();

  const isPivoting = useMemo<boolean>(() => {
    return (
      fixedColumnResizingWidth.isPivoting ||
      flexibleColumnResizingWidth.isPivoting ||
      nestedExpressions.some((e) => resizingWidths.get(e?.["@_id"] ?? "")?.isPivoting)
    );
  }, [fixedColumnResizingWidth.isPivoting, flexibleColumnResizingWidth.isPivoting, nestedExpressions, resizingWidths]);

  const nestedExpressionResizingWidthValue = useNestedExpressionResizingWidthValue(
    isPivoting,
    nestedExpressions,
    fixedColumnActualWidth,
    fixedColumnResizingWidth,
    fixedColumnMinWidth,
    nestedExpressionMinWidth,
    extraWidth,
    widthsById,
    nestedExpressionsExtraWidths
  );

  const maxNestedExpressionMinWidth = useNestedExpressionMinWidth(
    nestedExpressions,
    fixedColumnResizingWidth,
    nestedExpressionMinWidth,
    extraWidth
  );

  const nestedExpressionActualWidth = useNestedExpressionActualWidth(
    nestedExpressions,
    fixedColumnActualWidth,
    extraWidth,
    widthsById
  );

  const nestedExpressionContainerValue = useMemo<NestedExpressionContainerContextType>(() => {
    return {
      minWidth: maxNestedExpressionMinWidth,
      actualWidth: nestedExpressionActualWidth,
      resizingWidth: {
        value: flexibleColumnResizingWidth.isPivoting
          ? flexibleColumnResizingWidth.value
          : nestedExpressionResizingWidthValue,
        isPivoting: isPivoting || nestedExpressionContainer.resizingWidth.isPivoting,
      },
    };
  }, [
    maxNestedExpressionMinWidth,
    nestedExpressionActualWidth,
    flexibleColumnResizingWidth.isPivoting,
    flexibleColumnResizingWidth.value,
    nestedExpressionResizingWidthValue,
    isPivoting,
    nestedExpressionContainer.resizingWidth.isPivoting,
  ]);

  const { updateResizingWidth } = useResizingWidthsDispatch();

  useEffect(() => {
    updateResizingWidth(expression?.["@_id"] ?? "", (prev) => ({
      value: fixedColumnResizingWidth.value + nestedExpressionContainerValue.resizingWidth.value + extraWidth,
      isPivoting,
    }));
  }, [
    expression,
    nestedExpressionContainerValue.resizingWidth.value,
    isPivoting,
    updateResizingWidth,
    fixedColumnResizingWidth.value,
    extraWidth,
  ]);

  return useMemo(
    () => ({
      nestedExpressionContainerValue,
      onColumnResizingWidthChange,
      isPivoting,
    }),
    [nestedExpressionContainerValue, isPivoting, onColumnResizingWidthChange]
  );
}

export function useApportionedColumnWidthsIfNestedTable(
  beeTableRef: React.RefObject<BeeTableRef>,
  isPivoting: boolean,
  isNested: boolean,
  extraWidth: number,
  columns: Array<{ minWidth: number; width: number | undefined; isFrozen?: boolean }>,
  columnResizingWidths: Map<number, ResizingWidth>,
  rows: any[] // This is a hack to recalculate the positions when the row number changes.
) {
  const nestedExpressionContainer = useNestedExpressionContainer();

  useEffect(() => {
    if (isPivoting || !isNested) {
      return;
    }

    const fixedWidthAmount = columns.reduce(
      (acc, { isFrozen, width, minWidth }) => (isFrozen ? acc + (width ?? minWidth) : acc),
      0
    );

    const nextTotalWidth =
      Math.max(
        nestedExpressionContainer.minWidth - extraWidth,
        nestedExpressionContainer.resizingWidth.value - extraWidth
      ) - fixedWidthAmount;

    const apportionedWidths = apportionColumnWidths(
      nextTotalWidth,
      columns.map(({ minWidth, width, isFrozen }) => ({
        minWidth,
        currentWidth: width ?? minWidth,
        isFrozen: isFrozen ?? false,
      }))
    );

    const newColumnWidths = apportionedWidths.reduce((acc, nextWidth, index) => {
      if (columns[index].isFrozen) {
        return acc;
      }

      const columnIndex = index + 1; // + 1 to compensate for rowIndex column

      acc.set(columnIndex, {
        isPivoting: false,
        value: nextWidth,
      });

      return acc;
    }, new Map());

    beeTableRef.current?.updateColumnResizingWidths(newColumnWidths);
  }, [
    columns,
    isPivoting,
    nestedExpressionContainer.resizingWidth.value,
    isNested,
    beeTableRef,
    rows,
    nestedExpressionContainer.minWidth,
    extraWidth,
  ]);

  const pivotingColumnIndex = useMemo(() => {
    const pivotingColumn = [...columnResizingWidths.entries()].find(([_, { isPivoting }]) => isPivoting);
    if (pivotingColumn) {
      const [pivotingColumnIndex] = pivotingColumn;
      return pivotingColumnIndex - 1;
    } else {
      return 0;
    }
  }, [columnResizingWidths]);

  const spaceToTheRight = useMemo(
    () =>
      nestedExpressionContainer.resizingWidth.value -
      (columnResizingWidths.get(0)?.value ?? 0) -
      columns
        .slice(0, pivotingColumnIndex + 1) //
        .reduce((acc, c, i) => acc + (columnResizingWidths.get(i + 1)?.value ?? 0), 0),
    [columnResizingWidths, columns, nestedExpressionContainer.resizingWidth.value, pivotingColumnIndex]
  );

  const isSmallerThanContainersMinWidth = useMemo(() => {
    return [...columnResizingWidths.values()].reduce((acc, w) => acc + w.value, 0) < nestedExpressionContainer.minWidth;
  }, [columnResizingWidths, nestedExpressionContainer.minWidth]);

  useEffect(() => {
    if (isPivoting && isSmallerThanContainersMinWidth) {
      const apportionedColumnWidths = apportionColumnWidths(
        spaceToTheRight,
        columns.slice(pivotingColumnIndex + 1).map((c) => ({
          minWidth: c.minWidth,
          currentWidth: c.width ?? 0,
          isFrozen: false,
        }))
      );

      const newColumnWidths = apportionedColumnWidths.reduce((acc, apportionedWidth, i) => {
        acc.set(pivotingColumnIndex + 1 + i + 1, {
          isPivoting: false,
          value: apportionedWidth,
        });

        return acc;
      }, new Map());

      beeTableRef.current?.updateColumnResizingWidths(newColumnWidths);
    }
  }, [beeTableRef, columns, isPivoting, isSmallerThanContainersMinWidth, pivotingColumnIndex, spaceToTheRight]);
}

// This code is an implementation of the Jefferson method for solving the Apportion problem.
// See https://en.wikipedia.org/wiki/Mathematics_of_apportionment and https://en.wikipedia.org/wiki/D%27Hondt_method
// This algorithm also allows for columns to be frozen, and not proportional.
export function apportionColumnWidths(
  nextTotalWidth: number, // Analogous to "total seats"
  columns: { currentWidth: number; minWidth: number; isFrozen: boolean }[]
): number[] {
  // Calculate standard divisor (sd)
  const currentTotalWidth = columns.reduce(
    (acc, { currentWidth, isFrozen }) => (isFrozen ? acc : acc + currentWidth),
    0
  ); // Analogous to "total population"
  const sd = currentTotalWidth / nextTotalWidth;

  // Start apportionedWidths array with the minimum width of each column
  const apportionedWidths = columns.map(({ minWidth, isFrozen }) => (isFrozen ? 0 : minWidth)); // Analogous to seats count

  // Distribute widths between columns
  let nextDistributedWidth = apportionedWidths.reduce((acc, n) => acc + n, 0); // Analogous to distributed seats count
  while (nextDistributedWidth !== nextTotalWidth) {
    let maxRemainder = 0;
    let maxRemainderIndex = 0;

    // Find column with the largest remainder
    for (let i = 0; i < columns.length; i++) {
      if (columns[i].isFrozen) {
        continue;
      }
      const quota = columns[i].currentWidth / sd;
      const remainder = quota - apportionedWidths[i];
      if (remainder > maxRemainder) {
        maxRemainder = remainder;
        maxRemainderIndex = i;
      }
    }

    // Adjust apportionedWidth of column at maxReminderIndex
    if (nextDistributedWidth > nextTotalWidth) {
      apportionedWidths[maxRemainderIndex]--;
      nextDistributedWidth--;
    } else {
      apportionedWidths[maxRemainderIndex]++;
      nextDistributedWidth++;
    }
  }

  return apportionedWidths.filter((w) => !isNaN(w)); // Filter out `NaN` values.
}

export function useNestedTableLastColumnMinWidth(columnResizingWidths: Map<number, ResizingWidth>) {
  const nestedExpressionContainer = useNestedExpressionContainer();

  return useMemo(() => {
    // Prevents it from calculating before it's filled.
    if (columnResizingWidths.size <= 1) {
      return;
    }

    const extraWidthOnTable = columnResizingWidths.size;

    const widthOfAllColumnsExceptLastOne = [...columnResizingWidths.entries()].reduce(
      (acc, [columnIndex, { value }]) => {
        return columnIndex === columnResizingWidths.size - 1 ? acc : acc + value;
      },
      0
    );

    return Math.max(
      RELATION_EXPRESSION_COLUMN_MIN_WIDTH,
      nestedExpressionContainer.minWidth - extraWidthOnTable - widthOfAllColumnsExceptLastOne
    );
  }, [columnResizingWidths, nestedExpressionContainer.minWidth]);
}
