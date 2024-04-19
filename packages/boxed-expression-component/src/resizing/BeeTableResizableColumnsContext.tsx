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
import { useCallback, useEffect, useImperativeHandle, useMemo, useState } from "react";
import { ResizerStopBehavior, ResizingWidth, useResizerRef, useResizingWidthsDispatch } from "./ResizingWidthsContext";
import { BEE_TABLE_ROW_INDEX_COLUMN_WIDTH } from "./WidthConstants";

// TYPES

export type BeeTableResizableColumnsContextType = {
  columnResizingWidths: Map<number, ResizingWidth>;
};

export interface BeeTableResizableColumnsDispatchContextType {
  updateColumnResizingWidths(newColumnResizingWidths: Map<number, ResizingWidth | undefined>): void;

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

export type BeeTableResizingRef = BeeTableResizableColumnsDispatchContextType;

export const BeeTableResizableColumnsContextProvider = ({
  children,
  onChange,
  resizingRef,
}: React.PropsWithChildren<{
  onChange?: (args: Map<number, ResizingWidth | undefined>) => void;
  resizingRef: React.RefObject<BeeTableResizingRef>;
}>) => {
  const refs = React.useRef<Map<number, Set<BeeTableResizableCellRef>>>(new Map());

  const [columnResizingWidths, setColumnResizingWidths] = useState<Map<number, ResizingWidth>>(new Map());

  const onColumnResizingWidthChange = useCallback((args: Map<number, ResizingWidth | undefined>) => {
    setColumnResizingWidths((prev) => {
      const n = new Map(prev);
      for (const [columnIndex, newResizingWidth] of args.entries()) {
        if (newResizingWidth) {
          n.set(columnIndex, newResizingWidth);
        }
      }
      return n;
    });
  }, []);

  const value = useMemo(() => {
    return { columnResizingWidths };
  }, [columnResizingWidths]);

  const dispatch = useMemo<BeeTableResizableColumnsDispatchContextType>(() => {
    return {
      updateColumnResizingWidths: (newColumnResizingWidths) => {
        for (const [columnIndex, newResizingWidth] of newColumnResizingWidths.entries()) {
          for (const ref of refs.current.get(columnIndex) ?? []) {
            ref.setResizingWidth?.(newResizingWidth);
          }
        }

        onColumnResizingWidthChange(newColumnResizingWidths);
        onChange?.(newColumnResizingWidths);
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
  }, [onChange, onColumnResizingWidthChange]);

  useImperativeHandle(resizingRef, () => dispatch, [dispatch]);

  return (
    <BeeTableResizableColumnsContext.Provider value={value}>
      <BeeTableResizableColumnsDispatchContext.Provider value={dispatch}>
        <>{children}</>
      </BeeTableResizableColumnsDispatchContext.Provider>
    </BeeTableResizableColumnsContext.Provider>
  );
};

export function useBeeTableResizableColumnsDispatch() {
  return React.useContext(BeeTableResizableColumnsDispatchContext);
}

export function useBeeTableResizableColumns() {
  return React.useContext(BeeTableResizableColumnsContext);
}

// HOOKS

export function useBeeTableResizableCell(
  columnIndex: number,
  resizerStopBehavior: ResizerStopBehavior,
  width: number | undefined,
  setWidth?: React.Dispatch<React.SetStateAction<number | undefined>>,
  initialResizingWidthValue?: number
) {
  const { registerResizableCellRef, deregisterResizableCellRef, updateColumnResizingWidths } =
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
        width,
        setWidth,
        resizingWidth,
        resizerStopBehavior,
      }),
      [resizerStopBehavior, resizingWidth, setWidth, width]
    )
  );

  const _updateResizingWidth = useCallback(
    (newResizingWidth: ResizingWidth) => {
      updateColumnResizingWidths(new Map([[columnIndex, newResizingWidth]]));
    },
    [columnIndex, updateColumnResizingWidths]
  );

  useEffect(() => {
    const ref = registerResizableCellRef(columnIndex, { setResizingWidth });
    return () => {
      deregisterResizableCellRef(columnIndex, ref);
    };
  }, [columnIndex, setWidth, registerResizableCellRef, deregisterResizableCellRef]);

  // This needs to run AFTER the resizable cell is registered, otherwise its ref might not be there yet, thus missing the last row.
  useEffect(() => {
    updateColumnResizingWidths(new Map([[columnIndex, initialResizingWidth]]));
  }, [initialResizingWidth, columnIndex, initialResizingWidthValue, updateColumnResizingWidths]);

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

  const onColumnResizingWidthChange = useCallback((args: Map<number, ResizingWidth | undefined>) => {
    setColumnResizingWidths((prev) => {
      const n = new Map(prev);
      for (const [columnIndex, newResizingWidth] of args.entries()) {
        if (newResizingWidth) {
          n.set(columnIndex, newResizingWidth);
        }
      }
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
