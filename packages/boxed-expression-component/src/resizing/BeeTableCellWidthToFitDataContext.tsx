import * as React from "react";
import { useEffect, useMemo, useRef } from "react";

type BeeTableCellWidthsToFitDataContextType = {
  getRefs: () => Map<number, Map<number, BeeTableCellWidthToFitDataRef>>;
};
type BeeTableCellWidthsToFitDataDispatchContextType = {
  registerCellWidthToFitDataRef(
    rowIndex: number,
    columnIndex: number,
    ref: BeeTableCellWidthToFitDataRef
  ): BeeTableCellWidthToFitDataRef;
  deregisterCellWidthToFitDataRef(rowIndex: number, olumnIndex: number, ref: BeeTableCellWidthToFitDataRef): void;
};

const BeeTableCellWidthsToFitDataContext = React.createContext<BeeTableCellWidthsToFitDataContextType>({} as any);

const BeeTableCellWidthsToFitDataDispatchContext = React.createContext<BeeTableCellWidthsToFitDataDispatchContextType>(
  {} as any
);

export interface BeeTableCellWidthToFitDataRef {
  getWidthToFitData: () => number | undefined;
}

export function BeeTableCellWidthsToFitDataContextProvider({ children }: React.PropsWithChildren<{}>) {
  const refs = useRef<Map<number, Map<number, BeeTableCellWidthToFitDataRef>>>(new Map());

  const value = useMemo<BeeTableCellWidthsToFitDataContextType>(
    () => ({
      getRefs: () => refs.current,
    }),
    []
  );

  const dispatch = useMemo<BeeTableCellWidthsToFitDataDispatchContextType>(() => {
    return {
      registerCellWidthToFitDataRef: (rowIndex, columnIndex, ref) => {
        const columnsMap = new Map(refs.current.get(rowIndex) ?? new Map());
        if (columnsMap?.get(columnIndex)) {
          throw new Error(`Can't have two cells registered for the same coordinates (${rowIndex}, ${columnIndex})`);
        } else {
          columnsMap?.set(columnIndex, ref);
        }

        refs.current.set(rowIndex, columnsMap);
        return ref;
      },
      deregisterCellWidthToFitDataRef: (rowIndex, columnIndex, ref) => {
        const columnsMap = new Map(refs.current.get(rowIndex) ?? new Map());
        if (columnsMap?.get(columnIndex)) {
          columnsMap?.delete(columnIndex);
        } else {
          throw new Error(`Can't delete unregistered cell for coordinates (${rowIndex}, ${columnIndex})`);
        }

        refs.current.set(rowIndex, columnsMap);
      },
    };
  }, []);

  return (
    <BeeTableCellWidthsToFitDataContext.Provider value={value}>
      <BeeTableCellWidthsToFitDataDispatchContext.Provider value={dispatch}>
        {children}
      </BeeTableCellWidthsToFitDataDispatchContext.Provider>
    </BeeTableCellWidthsToFitDataContext.Provider>
  );
}

export function useCellWidthsToFitData() {
  return React.useContext(BeeTableCellWidthsToFitDataContext);
}

export function useCellWidthsToFitDataDispatch() {
  return React.useContext(BeeTableCellWidthsToFitDataDispatchContext);
}

export function useCellWidthToFitData(
  rowIndex: number,
  columnIndex: number
): BeeTableCellWidthToFitDataRef | undefined {
  const { getRefs } = useCellWidthsToFitData();

  return useMemo<BeeTableCellWidthToFitDataRef>(
    () => ({
      getWidthToFitData: () => getRefs().get(rowIndex)?.get(columnIndex)?.getWidthToFitData(),
    }),
    [columnIndex, getRefs, rowIndex]
  );
}

export function useCellWidthToFitDataRef(rowIndex: number, columnIndex: number, ref: BeeTableCellWidthToFitDataRef) {
  const { registerCellWidthToFitDataRef, deregisterCellWidthToFitDataRef } = useCellWidthsToFitDataDispatch();

  useEffect(() => {
    const r = registerCellWidthToFitDataRef(rowIndex, columnIndex, ref);
    return () => {
      deregisterCellWidthToFitDataRef(rowIndex, columnIndex, r);
    };
  }, [columnIndex, deregisterCellWidthToFitDataRef, ref, registerCellWidthToFitDataRef, rowIndex]);
}
