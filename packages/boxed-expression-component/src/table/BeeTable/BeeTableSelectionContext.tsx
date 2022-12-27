import * as React from "react";
import { useEffect, useMemo, useState } from "react";
import * as ReactTable from "react-table";
import { BoxedExpressionEditorDispatchContextType } from "../../expressions/BoxedExpressionEditor/BoxedExpressionEditorContext";

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
  subscribeToCellStatus(rowIndex: number, columnIndex: number, ref: BeeTableCellRef): BeeTableCellRef;
  unsubscribeToCellStatus(rowIndex: number, columnIndex: number, ref: BeeTableCellRef): void;
}

export const BeeTableSelectionContext = React.createContext<BeeTableSelectionContextType<any>>({} as any);
export const BeeTableSelectionDispatchContext = React.createContext<BeeTableSelectionDispatchContextType<any>>(
  {} as any
);

export interface BeeTableCellStatus {
  isActive: boolean;
  isEditing: boolean;
}

export interface BeeTableCellRef {
  setStatus(args: BeeTableCellStatus): void;
}

export function BeeTableSelectionContextProvider<R extends object>({ children }: React.PropsWithChildren<{}>) {
  const [activeCell, setActiveCell] = useState<BeeTableSelectionActiveCell<R> | undefined>(undefined);

  const refs = React.useRef<Map<number, Map<number, Set<BeeTableCellRef>>>>(new Map());

  const value = useMemo(() => {
    return {
      activeCell,
    };
  }, [activeCell]);

  const dispatch = useMemo<BeeTableSelectionDispatchContextType<R>>(() => {
    return {
      setActiveCell,
      subscribeToCellStatus: (rowIndex, columnIndex, ref) => {
        refs.current?.set(rowIndex, refs.current?.get(rowIndex) ?? new Map());
        const prev = refs.current?.get(rowIndex)?.get(columnIndex) ?? new Set();
        refs.current?.get(rowIndex)?.set(columnIndex, new Set([...prev, ref]));
        return ref;
      },
      unsubscribeToCellStatus: (rowIndex, columnIndex, ref) => {
        ref.setStatus({ isActive: false, isEditing: false });
        refs.current?.get(rowIndex)?.get(columnIndex)?.delete(ref);
      },
    };
  }, []);

  useEffect(() => {
    if (!activeCell) {
      return;
    }

    const ref = refs.current;
    const subscriptions = ref.get(activeCell.rowIndex)?.get(activeCell.columnIndex);
    subscriptions?.forEach((ref) => {
      ref.setStatus({
        isActive: true,
        isEditing: activeCell.isEditing,
      });
    });

    return () => {
      subscriptions?.forEach((ref) => {
        ref.setStatus({
          isActive: false,
          isEditing: false,
        });
      });
    };
  }, [activeCell]);

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

/**
 * This is done like this because if when we have every Th/Td observing { activeCell } from BeeTableSelectionContext,
 * performance suffers. Every component can subscribe to changes on the activeCell, and set their own state with a "copy" from the status.
 */
export function useBeeTableCellStatus(rowIndex: number, columnIndex: number) {
  const { subscribeToCellStatus, unsubscribeToCellStatus } = useBeeTableSelectionDispatch();

  const [status, setStatus] = useState({ isActive: false, isEditing: false });

  useEffect(() => {
    const ref = subscribeToCellStatus(rowIndex, columnIndex, { setStatus });
    return () => {
      unsubscribeToCellStatus(rowIndex, columnIndex, ref);
    };
  }, [columnIndex, rowIndex, subscribeToCellStatus, unsubscribeToCellStatus]);

  return status;
}
