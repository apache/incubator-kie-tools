import * as React from "react";
import { useCallback, useEffect, useImperativeHandle, useMemo, useState } from "react";
import { ResizerStopBehavior, ResizingWidth, useResizerRef, useResizingWidthsDispatch } from "./ResizingWidthsContext";
import { BEE_TABLE_ROW_INDEX_COLUMN_WIDTH } from "./WidthConstants";

// TYPES

export type BeeTableResizableColumnsContextType = {
  //
};

export interface BeeTableResizableColumnsDispatchContextType {
  updateColumnResizingWidth(
    columnIndex: number,
    getNewResizingWidth: (prev: ResizingWidth | undefined) => ResizingWidth | undefined
  ): void;
  registerResizableCellRef(columnIndex: number, ref: BeeTableResizableCellRef): BeeTableResizableCellRef;
  deregisterResizableCellRef(columnIndex: number, ref: BeeTableResizableCellRef): void;
}

export const BeeTableResizableColumnsContext = React.createContext<BeeTableResizableColumnsContextType>({} as any);
export const BeeTableResizableColumnsDispatchContext = React.createContext<BeeTableResizableColumnsDispatchContextType>(
  {} as any
);

export interface BeeTableResizableCellRef {
  setResizingWidth?: React.Dispatch<React.SetStateAction<ResizingWidth | undefined>>;
}

// PROVIDER

type Props = React.PropsWithChildren<{
  onChange?: (args: { columnIndex: number; newResizingWidth: ResizingWidth }) => void;
}>;

type MyRef = BeeTableResizableColumnsDispatchContextType;

export const BeeTableResizableColumnsContextProvider = React.forwardRef<MyRef, Props>(
  ({ children, onChange }, forwardRef) => {
    const refs = React.useRef<Map<number, Set<BeeTableResizableCellRef>>>(new Map());

    const value = useMemo(() => {
      return {};
    }, []);

    const dispatch = useMemo<BeeTableResizableColumnsDispatchContextType>(() => {
      return {
        updateColumnResizingWidth: (columnIndex, getNewResizingWidth) => {
          const newResizingWidth = getNewResizingWidth(undefined); // FIXME: Tiago -> Not good!
          for (const ref of refs.current.get(columnIndex) ?? []) {
            ref.setResizingWidth?.(newResizingWidth);
          }
          if (newResizingWidth) {
            onChange?.({ columnIndex, newResizingWidth });
          }
        },
        registerResizableCellRef: (columnIndex, ref) => {
          const prev = refs.current?.get(columnIndex) ?? new Set();
          refs.current?.set(columnIndex, new Set([...prev, ref]));
          return ref;
        },
        deregisterResizableCellRef: (columnIndex, ref) => {
          refs.current?.get(columnIndex)?.delete(ref);
        },
      };
    }, [onChange]);

    useImperativeHandle(forwardRef, () => dispatch, [dispatch]);

    return (
      <BeeTableResizableColumnsContext.Provider value={value}>
        <BeeTableResizableColumnsDispatchContext.Provider value={dispatch}>
          <>{children}</>
        </BeeTableResizableColumnsDispatchContext.Provider>
      </BeeTableResizableColumnsContext.Provider>
    );
  }
);

export function useBeeTableResizableColumnsDispatch() {
  return React.useContext(BeeTableResizableColumnsDispatchContext);
}

// HOOKS

export function useBeeTableResizableCell(
  columnIndex: number,
  resizerStopBehavior: ResizerStopBehavior,
  setWidth?: React.Dispatch<React.SetStateAction<number | undefined>>,
  initialResizingWidthValue?: number
) {
  const { registerResizableCellRef, deregisterResizableCellRef, updateColumnResizingWidth } =
    useBeeTableResizableColumnsDispatch();

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
    const ref = registerResizableCellRef(columnIndex, { setResizingWidth });
    return () => {
      deregisterResizableCellRef(columnIndex, ref);
    };
  }, [columnIndex, setWidth, registerResizableCellRef, deregisterResizableCellRef]);

  return { resizingWidth, setResizingWidth: _updateResizingWidth };
}

export function usePublishedBeeTableResizableColumns(id: string, columnCount: number, hasRowIndexColumn: boolean) {
  const [columnResizingWidths, setColumnResizingWidths] = useState<Map<number, ResizingWidth>>(new Map());

  const isPivoting = useMemo(() => {
    return [...columnResizingWidths.values()].some(({ isPivoting }) => isPivoting);
  }, [columnResizingWidths]);

  // Reset `columnResizingWidths` when the column count changes. This fixes the case of deleting/inserting columns.
  useEffect(() => {
    setColumnResizingWidths(
      hasRowIndexColumn //
        ? new Map([[0, { isPivoting: false, value: BEE_TABLE_ROW_INDEX_COLUMN_WIDTH }]])
        : new Map()
    );
  }, [columnCount, hasRowIndexColumn]);

  const onColumnResizingWidthChange = useCallback((args: { columnIndex: number; newResizingWidth: ResizingWidth }) => {
    setColumnResizingWidths((prev) => {
      const n = new Map(prev);
      n.set(args.columnIndex, args.newResizingWidth);
      return n;
    });
  }, []);

  const { updateResizingWidth } = useResizingWidthsDispatch();

  useEffect(() => {
    updateResizingWidth(id, (prev) => {
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
  }, [columnResizingWidths, id, updateResizingWidth]);

  return { onColumnResizingWidthChange, columnResizingWidths, isPivoting };
}
