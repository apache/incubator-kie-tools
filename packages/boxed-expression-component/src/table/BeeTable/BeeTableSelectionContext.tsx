import * as React from "react";
import { useMemo, useState } from "react";
import * as ReactTable from "react-table";

export interface BeeTableSelectionActiveCell<R extends object> {
  column: ReactTable.ColumnInstance<R> | undefined;
  columnIndex: number;
  row: ReactTable.Row<R> | undefined;
  rowIndex: number;
  isEditing: boolean;
}
export interface BeeTableSelectionContextType<R extends object> {
  activeCell: BeeTableSelectionActiveCell<R> | undefined;
}

export interface BeeTableSelectionDispatchContextType<R extends object> {
  setActiveCell: React.Dispatch<React.SetStateAction<BeeTableSelectionActiveCell<R> | undefined>>;
}

export const BeeTableSelectionContext = React.createContext<BeeTableSelectionContextType<any>>({} as any);
export const BeeTableSelectionDispatchContext = React.createContext<BeeTableSelectionDispatchContextType<any>>(
  {} as any
);

export function BeeTableSelectionContextProvider<R extends object>({ children }: React.PropsWithChildren<{}>) {
  const [activeCell, setActiveCell] = useState<BeeTableSelectionActiveCell<R> | undefined>(undefined);

  const value = useMemo(() => {
    return {
      activeCell,
    };
  }, [activeCell]);

  const dispatch = useMemo(() => {
    return {
      setActiveCell,
    };
  }, []);

  return (
    <BeeTableSelectionContext.Provider value={value}>
      <BeeTableSelectionDispatchContext.Provider value={dispatch}>
        <>{children}</>
      </BeeTableSelectionDispatchContext.Provider>
    </BeeTableSelectionContext.Provider>
  );
}

export function useBeeTableSelection() {
  return React.useContext(BeeTableSelectionContext);
}

export function useBeeTableSelectionDispatch() {
  return React.useContext(BeeTableSelectionDispatchContext);
}
