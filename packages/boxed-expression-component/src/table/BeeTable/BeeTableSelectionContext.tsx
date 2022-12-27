import * as React from "react";
import { useEffect, useMemo, useState } from "react";

export interface BeeTableSelectionActiveCell<R extends object> {
  columnIndex: number;
  rowIndex: number;
  isEditing: boolean;
}
export interface BeeTableSelectionContextType<R extends object> {
  activeCell: BeeTableSelectionActiveCell<R> | undefined;
}

export interface BeeTableSelectionDispatchContextType<R extends object> {
  setActiveCell: React.Dispatch<React.SetStateAction<BeeTableSelectionActiveCell<R> | undefined>>;
  setSelectionEnd: React.Dispatch<React.SetStateAction<BeeTableSelectionActiveCell<R> | undefined>>;
  subscribeToCellStatus(rowIndex: number, columnIndex: number, ref: BeeTableCellRef): BeeTableCellRef;
  unsubscribeToCellStatus(rowIndex: number, columnIndex: number, ref: BeeTableCellRef): void;
}

export const BeeTableSelectionContext = React.createContext<BeeTableSelectionContextType<any>>({} as any);
export const BeeTableSelectionDispatchContext = React.createContext<BeeTableSelectionDispatchContextType<any>>(
  {} as any
);

export type BeeTableCellStatus = {
  isActive: boolean;
  isEditing: boolean;
  isSelected: boolean;
  selectedPositions?: BeeTableSelectionPosition[];
};

export interface BeeTableCellRef {
  setStatus(args: BeeTableCellStatus): void;
}

export interface BeeTableSelection<R extends object> {
  active: BeeTableSelectionActiveCell<R> | undefined;
  selectionEnd: BeeTableSelectionActiveCell<R> | undefined;
}

export enum BeeTableSelectionPosition {
  Top = "top",
  Bottom = "bottom",
  Left = "left",
  Right = "right",
}

const neutralCellStatus = { isActive: false, isEditing: false, isSelected: false };

export function BeeTableSelectionContextProvider<R extends object>({ children }: React.PropsWithChildren<{}>) {
  const [selection, setSelection] = useState<BeeTableSelection<R>>({
    active: undefined,
    selectionEnd: undefined,
  });

  const refs = React.useRef<Map<number, Map<number, Set<BeeTableCellRef>>>>(new Map());

  const value = useMemo(() => {
    return {
      activeCell: selection.active,
    };
  }, [selection]);

  const dispatch = useMemo<BeeTableSelectionDispatchContextType<R>>(() => {
    return {
      setActiveCell: (activeCell) => {
        setSelection((prev) => {
          const newActiveCell = typeof activeCell === "function" ? activeCell(prev.active) : activeCell;
          const newSelectionEnd = newActiveCell;
          return { active: newActiveCell, selectionEnd: newSelectionEnd };
        });
      },
      setSelectionEnd: (selectionEnd) => {
        setSelection((prev) => {
          const newSelectionEnd = typeof selectionEnd === "function" ? selectionEnd(prev.selectionEnd) : selectionEnd;

          // Selecting the activeCell
          if (
            newSelectionEnd?.columnIndex === prev.active?.columnIndex &&
            newSelectionEnd?.rowIndex === prev.active?.rowIndex
          ) {
            return { ...prev, selectionEnd: prev.active };
          }
          // Selecting a normall cell from a rowIndex cell
          else if (prev.selectionEnd?.columnIndex === 0) {
            return {
              ...prev,
              selectionEnd: {
                columnIndex: 0,
                rowIndex: newSelectionEnd?.rowIndex ?? prev.selectionEnd.rowIndex,
                isEditing: false,
              },
            };
          }
          // Selecting a normall cell from a header cell
          else if ((prev.selectionEnd?.rowIndex ?? 0) < 0) {
            return {
              ...prev,
              selectionEnd: {
                columnIndex: newSelectionEnd?.columnIndex ?? 0,
                rowIndex: prev.selectionEnd?.rowIndex ?? 0,
                isEditing: false,
              },
            };
          } // Selecting a rowIndex cell from a normal cell
          else if (newSelectionEnd?.columnIndex === 0) {
            return {
              ...prev,
              selectionEnd: {
                columnIndex: 1,
                rowIndex: newSelectionEnd?.rowIndex ?? 0,
                isEditing: false,
              },
            };
          }
          // Selecting a header cell from a normal cell
          else if ((newSelectionEnd?.rowIndex ?? 0) < 0) {
            return {
              ...prev,
              selectionEnd: {
                columnIndex: newSelectionEnd?.columnIndex ?? 0,
                rowIndex: 0,
                isEditing: false,
              },
            };
          } else {
            return { ...prev, selectionEnd: newSelectionEnd };
          }
        });
      },
      subscribeToCellStatus: (rowIndex, columnIndex, ref) => {
        refs.current?.set(rowIndex, refs.current?.get(rowIndex) ?? new Map());
        const prev = refs.current?.get(rowIndex)?.get(columnIndex) ?? new Set();
        refs.current?.get(rowIndex)?.set(columnIndex, new Set([...prev, ref]));
        return ref;
      },
      unsubscribeToCellStatus: (rowIndex, columnIndex, ref) => {
        ref.setStatus(neutralCellStatus);
        refs.current?.get(rowIndex)?.get(columnIndex)?.delete(ref);
      },
    };
  }, []);

  useEffect(() => {
    if (!selection.active || !selection.selectionEnd) {
      return;
    }

    const currentRefs = refs.current;

    // Let's always go smaller to bigger, no matter the direction of the selection.
    const cStart = Math.min(selection.active.columnIndex, selection.selectionEnd.columnIndex);
    const cEnd = Math.max(selection.active.columnIndex, selection.selectionEnd.columnIndex);
    const rStart = Math.min(selection.active.rowIndex, selection.selectionEnd.rowIndex);
    const rEnd = Math.max(selection.active.rowIndex, selection.selectionEnd.rowIndex);

    for (let r = rStart; r <= rEnd; r++) {
      // Select rowIndex cells
      currentRefs
        .get(r)
        ?.get(0)
        ?.forEach((e) => e.setStatus({ isActive: false, isEditing: false, isSelected: true }));

      for (let c = cStart; c <= cEnd; c++) {
        const selectedPositions = [
          ...(r === rStart ? [BeeTableSelectionPosition.Top] : []),
          ...(r === rEnd ? [BeeTableSelectionPosition.Bottom] : []),
          ...(c === cStart ? [BeeTableSelectionPosition.Left] : []),
          ...(c === cEnd ? [BeeTableSelectionPosition.Right] : []),
        ];

        // Select header cells
        currentRefs
          .get(-1)
          ?.get(c)
          ?.forEach((e) => e.setStatus({ isActive: false, isEditing: false, isSelected: true }));

        const refs = currentRefs.get(r)?.get(c);
        refs?.forEach((ref) => {
          ref.setStatus({
            isActive: c === selection.active?.columnIndex && r === selection.active?.rowIndex,
            isEditing: selection.active?.isEditing ?? false,
            isSelected: selection.active !== selection.selectionEnd,
            selectedPositions,
          });
        });
      }
    }

    return () => {
      for (let r = rStart; r <= rEnd; r++) {
        currentRefs
          .get(r)
          ?.get(0)
          ?.forEach((e) => e.setStatus(neutralCellStatus));

        for (let c = cStart; c <= cEnd; c++) {
          currentRefs
            .get(-1)
            ?.get(c)
            ?.forEach((e) => e.setStatus(neutralCellStatus));

          const refs = currentRefs.get(r)?.get(c);
          refs?.forEach((ref) => {
            ref.setStatus(neutralCellStatus);
          });
        }
      }
    };
  }, [selection]);

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

  const [status, setStatus] = useState<BeeTableCellStatus>(neutralCellStatus);

  useEffect(() => {
    const ref = subscribeToCellStatus(rowIndex, columnIndex, { setStatus });
    return () => {
      unsubscribeToCellStatus(rowIndex, columnIndex, ref);
    };
  }, [columnIndex, rowIndex, subscribeToCellStatus, unsubscribeToCellStatus]);

  return status;
}
