import * as React from "react";
import { useMemo, useState } from "react";

export function ResizingWidthsContextProvider({ children }: React.PropsWithChildren<{}>) {
  const [resizingWidths, setResizingWidths] = useState<ResizingWidthsContextType["resizingWidths"]>(new Map());

  const value = useMemo(() => {
    return { resizingWidths };
  }, [resizingWidths]);

  const dispatch = useMemo<ResizingWidthsDispatchContextType>(() => {
    return {
      updateResizingWidth: (id, update) => {
        setResizingWidths((prev) => {
          const prevCopy = new Map(prev);
          const newValue = update(prevCopy.get(id) ?? { value: -2, isPivoting: false });
          prevCopy.set(id, newValue);
          return prevCopy;
        });
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

export type ResizingWidth = { value: number; isPivoting: boolean };

export type ResizingWidthsContextType = {
  resizingWidths: Map<string, ResizingWidth>;
};

export type ResizingWidthsDispatchContextType = {
  updateResizingWidth(id: string, getNewResizingWidth: (prev: ResizingWidth | undefined) => ResizingWidth): void;
};

const ResizingWidthsContext = React.createContext({} as ResizingWidthsContextType);
const ResizingWidthsDispatchContext = React.createContext({} as ResizingWidthsDispatchContextType);

export function useResizingWidths() {
  return React.useContext(ResizingWidthsContext);
}

export function useResizingWidthsDispatch() {
  return React.useContext(ResizingWidthsDispatchContext);
}
