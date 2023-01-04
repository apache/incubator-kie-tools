import * as React from "react";
import { useCallback, useEffect, useMemo, useState } from "react";
import { ResizingWidth, useResizingWidthsDispatch } from "../../resizing/ResizingWidthsContext";
import { NESTED_EXPRESSION_RESET_MARGIN } from "../../resizing/WidthValues";

// TYPES

export type BeeTableColumnResizingWidthsContextType<R extends object> = {
  //
};

export interface BeeTableColumnResizingWidthsDispatchContextType<R extends object> {
  updateResizingWidths(
    columnIndex: number,
    getNewResizingWidth: (prev: ResizingWidth | undefined) => ResizingWidth | undefined
  ): void;
  subscribeToColumnResizingWidth(
    columnIndex: number,
    ref: BeeTableColumnResizingWidthRef
  ): BeeTableColumnResizingWidthRef;
  unsubscribeToColumnResizingWidth(columnIndex: number, ref: BeeTableColumnResizingWidthRef): void;
}

export const BeeTableColumnResizingWidthsContext = React.createContext<BeeTableColumnResizingWidthsContextType<any>>(
  {} as any
);
export const BeeTableColumnResizingWidthsDispatchContext = React.createContext<
  BeeTableColumnResizingWidthsDispatchContextType<any>
>({} as any);

export interface BeeTableColumnResizingWidthRef {
  setResizingWidth?: React.Dispatch<React.SetStateAction<ResizingWidth | undefined>>;
}

// PROVIDER

export function BeeTableColumnResizingWidthsContextProvider<R extends object>({
  children,
  onChange,
}: React.PropsWithChildren<{
  onChange?: (args: { columnIndex: number; newResizingWidth: ResizingWidth }) => void;
}>) {
  const refs = React.useRef<Map<number, Set<BeeTableColumnResizingWidthRef>>>(new Map());

  const value = useMemo(() => {
    return {};
  }, []);

  const dispatch = useMemo<BeeTableColumnResizingWidthsDispatchContextType<R>>(() => {
    return {
      updateResizingWidths: (columnIndex, getNewResizingWidth) => {
        const newResizingWidth = getNewResizingWidth(undefined);
        for (const ref of refs.current.get(columnIndex) ?? []) {
          ref.setResizingWidth?.(newResizingWidth);
        }
        if (newResizingWidth) {
          onChange?.({ columnIndex, newResizingWidth });
        }
      },
      subscribeToColumnResizingWidth: (columnIndex, ref) => {
        const prev = refs.current?.get(columnIndex) ?? new Set();
        refs.current?.set(columnIndex, new Set([...prev, ref]));
        return ref;
      },
      unsubscribeToColumnResizingWidth: (columnIndex, ref) => {
        refs.current?.get(columnIndex)?.delete(ref);
      },
    };
  }, [onChange]);

  return (
    <BeeTableColumnResizingWidthsContext.Provider value={value}>
      <BeeTableColumnResizingWidthsDispatchContext.Provider value={dispatch}>
        <>{children}</>
      </BeeTableColumnResizingWidthsDispatchContext.Provider>
    </BeeTableColumnResizingWidthsContext.Provider>
  );
}

export function useBeeTableColumnResizingWidthsDispatch() {
  return React.useContext(BeeTableColumnResizingWidthsDispatchContext);
}

// HOOKS

export function useBeeTableColumnResizingWidth(columnIndex: number, initialResizingWidth?: number) {
  const { subscribeToColumnResizingWidth, unsubscribeToColumnResizingWidth, updateResizingWidths } =
    useBeeTableColumnResizingWidthsDispatch();

  const [resizingWidth, setResizingWidth] = useState<ResizingWidth | undefined>(
    initialResizingWidth
      ? {
          value: initialResizingWidth,
          isPivoting: false,
        }
      : undefined
  );

  useEffect(() => {
    updateResizingWidths(columnIndex, (prev) =>
      initialResizingWidth
        ? {
            value: initialResizingWidth,
            isPivoting: false,
          }
        : undefined
    );
  }, [columnIndex, initialResizingWidth, updateResizingWidths]);

  const updateResizingWidth = useCallback(
    (getNewResizingWidth: (prev: ResizingWidth) => ResizingWidth) => {
      updateResizingWidths(columnIndex, getNewResizingWidth);
    },
    [columnIndex, updateResizingWidths]
  );

  useEffect(() => {
    const ref = subscribeToColumnResizingWidth(columnIndex, { setResizingWidth });
    return () => {
      unsubscribeToColumnResizingWidth(columnIndex, ref);
    };
  }, [columnIndex, subscribeToColumnResizingWidth, unsubscribeToColumnResizingWidth]);

  return { resizingWidth, setResizingWidth: updateResizingWidth };
}

export function usePublishedBeeTableColumnResizingWidths(resizingWidthId: string) {
  const [columnResizingWidths, setColumnResizingWidths] = useState<Map<number, ResizingWidth>>(new Map());
  const onColumnResizingWidthChange = useCallback((args: { columnIndex: number; newResizingWidth: ResizingWidth }) => {
    setColumnResizingWidths((prev) => {
      const n = new Map([...prev.entries()]);
      n.set(args.columnIndex, args.newResizingWidth);
      return n;
    });
  }, []);

  const { updateResizingWidth } = useResizingWidthsDispatch();

  useEffect(() => {
    updateResizingWidth(resizingWidthId, (prev) => {
      const initial: ResizingWidth = {
        value: NESTED_EXPRESSION_RESET_MARGIN + columnResizingWidths.size,
        isPivoting: false,
      };

      return [...columnResizingWidths.values()].reduce(
        (acc, resizingWidth) => ({
          value: acc.value + resizingWidth.value,
          isPivoting: acc.isPivoting || resizingWidth.isPivoting,
        }),
        initial
      );
    });
  }, [columnResizingWidths, resizingWidthId, updateResizingWidth]);

  return { onColumnResizingWidthChange, columnResizingWidths };
}
