import * as React from "react";
import { useCallback, useEffect, useMemo, useState } from "react";
import { ResizingWidth } from "../../resizing/ResizingWidthsContext";

export const PLACEHOLDER_WIDTH_FOR_DETECTING_ERRORS = -5;

// TYPES

export type BeeTableColumnResizingWidthsContextType<R extends object> = {
  //
};

export interface BeeTableColumnResizingWidthsDispatchContextType<R extends object> {
  updateResizingWidths(
    columnIndex: number,
    getNewResizingWidth: (prev: ResizingWidth | undefined) => ResizingWidth
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
  setResizingWidth?: React.Dispatch<React.SetStateAction<ResizingWidth>>;
}

// PROVIDER

export function BeeTableColumnResizingWidthsContextProvider<R extends object>({
  children,
}: React.PropsWithChildren<{}>) {
  const refs = React.useRef<Map<number, Set<BeeTableColumnResizingWidthRef>>>(new Map());

  const value = useMemo(() => {
    return {};
  }, []);

  const dispatch = useMemo<BeeTableColumnResizingWidthsDispatchContextType<R>>(() => {
    return {
      updateResizingWidths: (columnIndex, getNewResizingWidth) => {
        for (const ref of refs.current.get(columnIndex) ?? []) {
          ref.setResizingWidth?.(getNewResizingWidth(undefined));
        }
      },
      subscribeToColumnResizingWidth: (columnIndex, ref) => {
        const prev = refs.current?.get(columnIndex) ?? new Set();
        refs.current?.set(columnIndex, new Set([...prev, ref]));
        return ref;
      },
      unsubscribeToColumnResizingWidth: (columnIndex, ref) => {
        ref.setResizingWidth?.({ value: PLACEHOLDER_WIDTH_FOR_DETECTING_ERRORS, isPivoting: false });
        refs.current?.get(columnIndex)?.delete(ref);
      },
    };
  }, []);

  return (
    <BeeTableColumnResizingWidthsContext.Provider value={value}>
      <BeeTableColumnResizingWidthsDispatchContext.Provider value={dispatch}>
        <>{children}</>
      </BeeTableColumnResizingWidthsDispatchContext.Provider>
    </BeeTableColumnResizingWidthsContext.Provider>
  );
}

export function useBeeTableColumnResizingWidths() {
  return React.useContext(BeeTableColumnResizingWidthsContext);
}

export function useBeeTableColumnResizingWidthsDispatch() {
  return React.useContext(BeeTableColumnResizingWidthsDispatchContext);
}

// HOOKS

export function useBeeTableColumnWidth(columnIndex: number, initialResizingWidth?: number) {
  const { subscribeToColumnResizingWidth, unsubscribeToColumnResizingWidth, updateResizingWidths } =
    useBeeTableColumnResizingWidthsDispatch();

  const [resizingWidth, setResizingWidth] = useState<ResizingWidth>({
    value: initialResizingWidth ?? PLACEHOLDER_WIDTH_FOR_DETECTING_ERRORS,
    isPivoting: false,
  });

  useEffect(() => {
    updateResizingWidths(columnIndex, (prev) => ({
      value: initialResizingWidth ?? PLACEHOLDER_WIDTH_FOR_DETECTING_ERRORS,
      isPivoting: false,
    }));
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
