import { useMemo, useEffect } from "react";
import { ExpressionDefinition } from "../api";
import {
  useNestedExpressionContainer,
  usePivotAwareNestedExpressionContainer,
  NestedExpressionContainerContextType,
} from "./NestedExpressionContainerContext";
import { ResizingWidth, useResizingWidths, useResizingWidthsDispatch } from "./ResizingWidthsContext";
import { getExpressionResizingWidth, getExpressionMinWidth } from "./Widths";

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
    const nestedPivotingExpression: ExpressionDefinition | undefined = nestedExpressions.filter(
      ({ id }) => resizingWidths.get(id!)?.isPivoting ?? false
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
    nestedExpressionMinWidth,
    fixedColumnMinWidth,
    fixedColumnResizingWidth.value,
    fixedColumnActualWidth,
    extraWidth,
    nestedExpressionContainer.actualWidth,
    nestedExpressions,
    pivotAwareNestedExpressionContainer.resizingWidth.value,
    resizingWidths,
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
        .filter(({ id }) => !(resizingWidths.get(id!)?.isPivoting ?? false))
        .map((expression) => getExpressionResizingWidth(expression, new Map()))
    );
  }, [fixedColumnActualWidth, extraWidth, nestedExpressionContainer.actualWidth, nestedExpressions, resizingWidths]);
}

export function useNestedExpressionContainerWidthNestedExpressions({
  nestedExpressions,
  fixedColumnActualWidth,
  fixedColumnResizingWidth,
  fixedColumnMinWidth,
  nestedExpressionMin,
  extraWidth,
  id,
}: {
  nestedExpressions: ExpressionDefinition[];
  fixedColumnActualWidth: number;
  fixedColumnResizingWidth: ResizingWidth;
  fixedColumnMinWidth: number;
  nestedExpressionMin: number;
  extraWidth: number;
  id: string | undefined;
}) {
  const { resizingWidths } = useResizingWidths();
  const isPivoting = useMemo<boolean>(() => {
    return (
      fixedColumnResizingWidth.isPivoting || nestedExpressions.some(({ id }) => resizingWidths.get(id!)?.isPivoting)
    );
  }, [fixedColumnResizingWidth.isPivoting, nestedExpressions, resizingWidths]);

  const nestedExpressionResizingWidthValue = useNestedExpressionResizingWidth(
    isPivoting,
    nestedExpressions,
    fixedColumnActualWidth,
    fixedColumnResizingWidth,
    fixedColumnMinWidth,
    nestedExpressionMin,
    extraWidth
  );

  const maxNestedExpressionMinWidth = useNestedExpressionMinWidth(
    nestedExpressions,
    fixedColumnResizingWidth,
    nestedExpressionMin,
    extraWidth
  );

  const nestedExpressionActualWidth = useNestedExpressionActualWidth(
    nestedExpressions,
    fixedColumnActualWidth,
    extraWidth
  );

  const nestedExpressionContainerValue = useMemo<NestedExpressionContainerContextType>(() => {
    return {
      minWidth: maxNestedExpressionMinWidth,
      actualWidth: nestedExpressionActualWidth,
      resizingWidth: {
        value: nestedExpressionResizingWidthValue,
        isPivoting,
      },
    };
  }, [maxNestedExpressionMinWidth, nestedExpressionActualWidth, nestedExpressionResizingWidthValue, isPivoting]);

  const { updateResizingWidth } = useResizingWidthsDispatch();

  useEffect(() => {
    updateResizingWidth(id!, (prev) => ({
      value: fixedColumnResizingWidth.value + nestedExpressionResizingWidthValue + extraWidth,
      isPivoting,
    }));
  }, [
    id,
    nestedExpressionResizingWidthValue,
    isPivoting,
    updateResizingWidth,
    fixedColumnResizingWidth.value,
    extraWidth,
  ]);

  const ret = useMemo(() => {
    return { nestedExpressionContainerValue };
  }, [nestedExpressionContainerValue]);

  return ret;
}
