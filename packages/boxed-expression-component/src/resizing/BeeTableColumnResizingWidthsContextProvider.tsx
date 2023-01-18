import * as React from "react";
import { useCallback, useEffect, useImperativeHandle, useMemo, useRef, useState } from "react";
import { ResizerStopBehavior, ResizingWidth, useResizerRef, useResizingWidthsDispatch } from "./ResizingWidthsContext";

// TYPES

export type BeeTableColumnResizingWidthsContextType = {
  //
};

export interface BeeTableColumnResizingWidthsDispatchContextType {
  updateColumnResizingWidth(
    columnIndex: number,
    getNewResizingWidth: (prev: ResizingWidth | undefined) => ResizingWidth | undefined
  ): void;
  registerResizableColumnRef(columnIndex: number, ref: BeeTableColumnResizingWidthRef): BeeTableColumnResizingWidthRef;
  deregisterResizableColumnRef(columnIndex: number, ref: BeeTableColumnResizingWidthRef): void;
}

export const BeeTableColumnResizingWidthsContext = React.createContext<BeeTableColumnResizingWidthsContextType>(
  {} as any
);
export const BeeTableColumnResizingWidthsDispatchContext =
  React.createContext<BeeTableColumnResizingWidthsDispatchContextType>({} as any);

export interface BeeTableColumnResizingWidthRef {
  setResizingWidth?: React.Dispatch<React.SetStateAction<ResizingWidth | undefined>>;
  setWidth?: React.Dispatch<React.SetStateAction<number | undefined>>;
}

// PROVIDER

type Props = React.PropsWithChildren<{
  onChange?: (args: { columnIndex: number; newResizingWidth: ResizingWidth }) => void;
}>;

type MyRef = BeeTableColumnResizingWidthsDispatchContextType;

export const BeeTableColumnResizingWidthsContextProvider = React.forwardRef<MyRef, Props>(
  ({ children, onChange }, forwardRef) => {
    const refs = React.useRef<Map<number, Set<BeeTableColumnResizingWidthRef>>>(new Map());

    const value = useMemo(() => {
      return {};
    }, []);

    const dispatch = useMemo<BeeTableColumnResizingWidthsDispatchContextType>(() => {
      return {
        updateColumnResizingWidth: (columnIndex, getNewResizingWidth) => {
          const newResizingWidth = getNewResizingWidth(undefined);
          for (const ref of refs.current.get(columnIndex) ?? []) {
            ref.setResizingWidth?.(newResizingWidth);
          }
          if (newResizingWidth) {
            onChange?.({ columnIndex, newResizingWidth });
          }
        },
        registerResizableColumnRef: (columnIndex, ref) => {
          const prev = refs.current?.get(columnIndex) ?? new Set();
          refs.current?.set(columnIndex, new Set([...prev, ref]));
          return ref;
        },
        deregisterResizableColumnRef: (columnIndex, ref) => {
          refs.current?.get(columnIndex)?.delete(ref);
        },
      };
    }, [onChange]);

    useImperativeHandle(forwardRef, () => dispatch, [dispatch]);

    return (
      <BeeTableColumnResizingWidthsContext.Provider value={value}>
        <BeeTableColumnResizingWidthsDispatchContext.Provider value={dispatch}>
          <>{children}</>
        </BeeTableColumnResizingWidthsDispatchContext.Provider>
      </BeeTableColumnResizingWidthsContext.Provider>
    );
  }
);

export function useBeeTableColumnResizingWidthsDispatch() {
  return React.useContext(BeeTableColumnResizingWidthsDispatchContext);
}

// HOOKS

export function useBeeTableColumnResizingWidth(
  columnIndex: number,
  resizerStopBehavior: ResizerStopBehavior,
  setWidth?: React.Dispatch<React.SetStateAction<number | undefined>>,
  initialResizingWidthValue?: number
) {
  const { registerResizableColumnRef, deregisterResizableColumnRef, updateColumnResizingWidth } =
    useBeeTableColumnResizingWidthsDispatch();

  const initialResizingWidth: ResizingWidth | undefined = useMemo(() => {
    if (!initialResizingWidthValue) {
      return undefined;
    }

    return {
      value: initialResizingWidthValue,
      isPivoting: false,
    };
  }, [initialResizingWidthValue]);

  const [resizingWidth, setResizingWidth] = useState<ResizingWidth | undefined>(initialResizingWidth);

  useResizerRef(
    useMemo(
      () => ({
        setWidth,
        resizingWidth,
        resizerStopBehavior,
      }),
      [resizerStopBehavior, resizingWidth, setWidth]
    )
  );

  useEffect(() => {
    updateColumnResizingWidth(columnIndex, (prev) => initialResizingWidth);
  }, [initialResizingWidth, columnIndex, initialResizingWidthValue, updateColumnResizingWidth]);

  const _updateResizingWidth = useCallback(
    (getNewResizingWidth: (prev: ResizingWidth) => ResizingWidth) => {
      updateColumnResizingWidth(columnIndex, getNewResizingWidth);
    },
    [columnIndex, updateColumnResizingWidth]
  );

  useEffect(() => {
    const ref = registerResizableColumnRef(columnIndex, { setResizingWidth, setWidth });
    return () => {
      deregisterResizableColumnRef(columnIndex, ref);
    };
  }, [columnIndex, setWidth, registerResizableColumnRef, deregisterResizableColumnRef]);

  return { resizingWidth, setResizingWidth: _updateResizingWidth };
}

export function usePublishedBeeTableColumnResizingWidths(resizingWidthId: string) {
  const [columnResizingWidths, setColumnResizingWidths] = useState<Map<number, ResizingWidth>>(new Map());

  const isPivoting = useMemo(() => {
    return [...columnResizingWidths.values()].some((s) => s.isPivoting);
  }, [columnResizingWidths]);

  const onColumnResizingWidthChange = useCallback((args: { columnIndex: number; newResizingWidth: ResizingWidth }) => {
    setColumnResizingWidths((prev) => {
      const n = new Map(prev);
      n.set(args.columnIndex, args.newResizingWidth);
      return n;
    });
  }, []);

  const { updateResizingWidth } = useResizingWidthsDispatch();

  useEffect(() => {
    updateResizingWidth(resizingWidthId, (prev) => {
      return [...columnResizingWidths.values()].reduce(
        (acc, resizingWidth) => ({
          value: acc.value + resizingWidth.value,
          isPivoting: acc.isPivoting || resizingWidth.isPivoting,
        }),
        {
          value: 0,
          isPivoting: false,
        }
      );
    });
  }, [columnResizingWidths, resizingWidthId, updateResizingWidth]);

  return { onColumnResizingWidthChange, columnResizingWidths, isPivoting };
}
