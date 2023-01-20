import { useMemo, useEffect } from "react";
import { ExpressionDefinition } from "../api";
import { BeeTableRef } from "../expressions";
import {
  useNestedExpressionContainer,
  usePivotAwareNestedExpressionContainer,
  NestedExpressionContainerContextType,
} from "./NestedExpressionContainerContext";
import { ResizingWidth, useResizingWidths, useResizingWidthsDispatch } from "./ResizingWidthsContext";
import { BEE_TABLE_ROW_INDEX_COLUMN_WIDTH, RELATION_EXPRESSION_COLUMN_MIN_WIDTH } from "./WidthConstants";
import { getExpressionResizingWidth, getExpressionMinWidth } from "./WidthMaths";

export function useNestedExpressionResizingWidth(
  isPivoting: boolean,
  nestedExpressions: ExpressionDefinition[],
  fixedColumnActualWidth: number,
  fixedColumnResizingWidth: ResizingWidth,
  fixedColumnMinWidth: number,
  nestedExpressionMinWidth: number,
  extraWidth: number
) {
  const { resizingWidths } = useResizingWidths();
  const nestedExpressionContainer = useNestedExpressionContainer();
  const pivotAwareNestedExpressionContainer = usePivotAwareNestedExpressionContainer(isPivoting);

  const nestedExpressionResizingWidthValue = useMemo<number>(() => {
    if (nestedExpressionContainer.resizingWidth.isPivoting && !isPivoting) {
      return nestedExpressionContainer.resizingWidth.value - fixedColumnResizingWidth.value - extraWidth;
    }

    const nestedPivotingExpression: ExpressionDefinition | undefined = nestedExpressions.filter(
      ({ id }) => resizingWidths.get(id)?.isPivoting ?? false
    )[0];

    if (nestedPivotingExpression) {
      return Math.max(getExpressionResizingWidth(nestedPivotingExpression, resizingWidths), fixedColumnMinWidth);
    }

    const nestedExpressionContainerResizingWidthValue =
      fixedColumnResizingWidth.value >= fixedColumnActualWidth
        ? pivotAwareNestedExpressionContainer.resizingWidth.value
        : nestedExpressionContainer.actualWidth;

    return Math.max(
      nestedExpressionContainerResizingWidthValue - fixedColumnResizingWidth.value - extraWidth,
      ...nestedExpressions.map((e) => getExpressionResizingWidth(e, new Map())),
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
    fixedColumnMinWidth,
  ]);

  return nestedExpressionResizingWidthValue;
}

export function useNestedExpressionMinWidth(
  nestedExpressions: ExpressionDefinition[],
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
  nestedExpressions: ExpressionDefinition[],
  fixedColumnActualWidth: number,
  extraWidth: number
) {
  const nestedExpressionContainer = useNestedExpressionContainer();
  const { resizingWidths } = useResizingWidths();

  return useMemo<number>(() => {
    return Math.max(
      nestedExpressionContainer.actualWidth - fixedColumnActualWidth - extraWidth,
      ...nestedExpressions
        .filter(({ id }) => !(resizingWidths.get(id)?.isPivoting ?? false))
        .map((expression) => getExpressionResizingWidth(expression, new Map()))
    );
  }, [fixedColumnActualWidth, extraWidth, nestedExpressionContainer.actualWidth, nestedExpressions, resizingWidths]);
}

export function useNestedExpressionContainerWithNestedExpressions({
  nestedExpressions,
  fixedColumnActualWidth,
  fixedColumnResizingWidth,
  fixedColumnMinWidth,
  nestedExpressionMinWidth,
  extraWidth,
  expression,
}: {
  nestedExpressions: ExpressionDefinition[];
  fixedColumnActualWidth: number;
  fixedColumnResizingWidth: ResizingWidth;
  fixedColumnMinWidth: number;
  nestedExpressionMinWidth: number;
  extraWidth: number;
  expression: ExpressionDefinition;
}) {
  const { resizingWidths } = useResizingWidths();
  const isPivoting = useMemo<boolean>(() => {
    return (
      fixedColumnResizingWidth.isPivoting || nestedExpressions.some(({ id }) => resizingWidths.get(id)?.isPivoting)
    );
  }, [fixedColumnResizingWidth.isPivoting, nestedExpressions, resizingWidths]);

  const nestedExpressionResizingWidthValue = useNestedExpressionResizingWidth(
    isPivoting,
    nestedExpressions,
    fixedColumnActualWidth,
    fixedColumnResizingWidth,
    fixedColumnMinWidth,
    nestedExpressionMinWidth,
    extraWidth
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
    extraWidth
  );

  const nestedExpressionContainer = useNestedExpressionContainer();

  const nestedExpressionContainerValue = useMemo<NestedExpressionContainerContextType>(() => {
    return {
      minWidth: maxNestedExpressionMinWidth,
      actualWidth: nestedExpressionActualWidth,
      resizingWidth: {
        value: nestedExpressionResizingWidthValue,
        isPivoting: isPivoting || nestedExpressionContainer.resizingWidth.isPivoting,
      },
    };
  }, [
    maxNestedExpressionMinWidth,
    nestedExpressionActualWidth,
    nestedExpressionResizingWidthValue,
    isPivoting,
    nestedExpressionContainer.resizingWidth.isPivoting,
  ]);

  const { updateResizingWidth } = useResizingWidthsDispatch();

  useEffect(() => {
    updateResizingWidth(expression.id, (prev) => ({
      value: fixedColumnResizingWidth.value + nestedExpressionResizingWidthValue + extraWidth,
      isPivoting,
    }));
  }, [
    expression.id,
    nestedExpressionResizingWidthValue,
    isPivoting,
    updateResizingWidth,
    fixedColumnResizingWidth.value,
    extraWidth,
  ]);

  return useMemo(() => {
    return { nestedExpressionContainerValue };
  }, [nestedExpressionContainerValue]);
}

export function useApportionedColumnWidthsIfNestedTable(
  beeTableRef: React.RefObject<BeeTableRef>,
  isPivoting: boolean,
  isNested: boolean,
  extraWidth: number,
  columns: Array<{ width: number | undefined; isFrozen?: boolean }>,
  rows: any[] //FIXME: Tiago -> This is a hack to recalculate the positions when the row number changes.
) {
  const minWidth = RELATION_EXPRESSION_COLUMN_MIN_WIDTH; // FIXME: Tiago -> Maybe this should be a parameter?
  const nestedExpressionContainer = useNestedExpressionContainer();

  useEffect(() => {
    if (isPivoting || !isNested) {
      return;
    }

    const fixedWidthAmount = columns.reduce(
      (acc, { isFrozen, width }) => (isFrozen ? acc + (width ?? minWidth) : acc),
      0
    );
    const nextTotalWidth =
      Math.max(
        nestedExpressionContainer.minWidth - extraWidth,
        nestedExpressionContainer.resizingWidth.value - extraWidth
      ) - fixedWidthAmount;

    const apportionedWidths = apportionColumnWidths(
      nextTotalWidth,
      columns.map(({ width, isFrozen }) => ({
        minWidth,
        currentWidth: width ?? minWidth,
        isFrozen: isFrozen ?? false,
      }))
    );

    apportionedWidths.forEach((nextWidth, index) => {
      if (columns[index].isFrozen) {
        console.info(nextWidth);
        return;
      }

      const columnIndex = index + 1; // + 1 to compensate for rowIndex column
      beeTableRef.current?.updateColumnResizingWidth(columnIndex, (prev) => ({
        isPivoting: prev?.isPivoting ?? false,
        value: nextWidth,
      }));
    });
  }, [
    columns,
    isPivoting,
    nestedExpressionContainer.resizingWidth.value,
    isNested,
    beeTableRef,
    rows,
    nestedExpressionContainer.minWidth,
    minWidth,
    extraWidth,
  ]);
}

// This code is an implementation of the Jefferson method for solving the Apportion problem.
// See https://en.wikipedia.org/wiki/Mathematics_of_apportionment and https://en.wikipedia.org/wiki/D%27Hondt_method
function apportionColumnWidths(
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

  return apportionedWidths;
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
