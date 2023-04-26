import * as React from "react";
import { useEffect, useMemo, useRef, useState } from "react";

export enum ResizerStopBehavior {
  SET_WIDTH_WHEN_SMALLER,
  SET_WIDTH_ALWAYS,
}

export type ResizerRef = {
  width: number | undefined;
  setWidth?: React.Dispatch<React.SetStateAction<number | undefined>>;
  resizingWidth: ResizingWidth | undefined;
  resizerStopBehavior: ResizerStopBehavior;
};

export const DEFAULT_RESIZING_WIDTH: ResizingWidth = {
  value: -2,
  isPivoting: false,
};

export function ResizingWidthsContextProvider({ children }: React.PropsWithChildren<{}>) {
  const [resizingWidths, setResizingWidths] = useState<ResizingWidthsContextType["resizingWidths"]>(new Map());

  const value = useMemo(() => {
    return { resizingWidths };
  }, [resizingWidths]);

  const refs = useRef<Set<ResizerRef>>(new Set());
  const isResizingRef = useRef<boolean>(false);

  const dispatch = useMemo<ResizingWidthsDispatchContextType>(() => {
    return {
      setResizing: (isResizing) => {
        isResizingRef.current = isResizing;
      },
      isResizing: () => {
        return isResizingRef.current;
      },
      updateResizingWidth: (id, getNewResizingWidth) => {
        setResizingWidths((prev) => {
          const n = new Map(prev);
          n.set(id, getNewResizingWidth(n.get(id) ?? DEFAULT_RESIZING_WIDTH));
          return n;
        });
      },
      registerResizerRef: (ref) => {
        refs.current.add(ref);
        return ref;
      },
      deregisterResizerRef: (ref) => {
        refs.current.delete(ref);
      },
      getResizerRefs: () => {
        return refs.current;
      },
    };
  }, []);

  return (
    <ResizingWidthsContext.Provider value={value}>
      <ResizingWidthsDispatchContext.Provider value={dispatch}>
        <>{children}</>
      </ResizingWidthsDispatchContext.Provider>
    </ResizingWidthsContext.Provider>
  );
}

export type ResizingWidth = {
  value: number;
  isPivoting: boolean;
};

export type ResizingWidthsContextType = {
  resizingWidths: Map<string, ResizingWidth>;
};

export type ResizingWidthsDispatchContextType = {
  setResizing: (isResizing: boolean) => void;
  /**
   * This is exactly the same as searching for a pivoting resizingWidth, but more performatic.
   */
  isResizing: () => boolean;
  updateResizingWidth(id: string, getNewResizingWidth: (prev: ResizingWidth | undefined) => ResizingWidth): void;
  registerResizerRef(ref: ResizerRef): ResizerRef;
  deregisterResizerRef(ref: ResizerRef): void;
  getResizerRefs(): Set<ResizerRef>;
};

const ResizingWidthsContext = React.createContext({} as ResizingWidthsContextType);
const ResizingWidthsDispatchContext = React.createContext({} as ResizingWidthsDispatchContextType);

export function useResizingWidths() {
  return React.useContext(ResizingWidthsContext);
}

export function useResizingWidthsDispatch() {
  return React.useContext(ResizingWidthsDispatchContext);
}

export function useResizerRef(resizerRef: ResizerRef) {
  const { registerResizerRef, deregisterResizerRef } = useResizingWidthsDispatch();

  useEffect(() => {
    const ref = registerResizerRef(resizerRef);
    return () => {
      deregisterResizerRef(ref);
    };
  }, [deregisterResizerRef, registerResizerRef, resizerRef]);
}
