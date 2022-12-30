import * as React from "react";
import { useEffect, useMemo, useState } from "react";
import { ResizingWidth } from "../../resizing/ResizingWidthsContext";

export interface BeeTableSelectionActiveCell<R extends object> {
  columnIndex: number;
  rowIndex: number;
  isEditing: boolean;
}
export interface BeeTableSelectionContextType<R extends object> {
  activeCell: BeeTableSelectionActiveCell<R> | undefined;
  selectionEnd: BeeTableSelectionActiveCell<R> | undefined;
  selectionStart: BeeTableSelectionActiveCell<R> | undefined;
}

export interface BeeTableSelectionDispatchContextType<R extends object> {
  erase(): void;
  copy(): void;
  cut(): void;
  paste(): void;
  updateResizingWidths(
    columnIndex: number,
    getNewResizingWidth: (prev: ResizingWidth | undefined) => ResizingWidth
  ): void;
  adaptSelection(args: {
    atRowIndex: number;
    rowCountDelta: number;
    atColumnIndex: number;
    columnCountDelta: number;
  }): void;
  setActiveCell: React.Dispatch<
    React.SetStateAction<(BeeTableSelectionActiveCell<R> & { keepSelection?: boolean }) | undefined>
  >;
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
  setStatus?(args: BeeTableCellStatus): void;
  setValue?(value: string): void;
  getValue?(): string;
  setResizingWidth?: React.Dispatch<React.SetStateAction<ResizingWidth>>;
}

export interface BeeTableSelection<R extends object> {
  active: BeeTableSelectionActiveCell<R> | undefined;
  selectionEnd: BeeTableSelectionActiveCell<R> | undefined;
  selectionStart: BeeTableSelectionActiveCell<R> | undefined;
}

export enum BeeTableSelectionPosition {
  Top = "top",
  Bottom = "bottom",
  Left = "left",
  Right = "right",
}

const CLIPBOARD_ROW_SEPARATOR = "\n";
const CLIPBOARD_COLUMN_SEPARATOR = "\t";

const NEUTRAL_CELL_STATUS = {
  isActive: false,
  isEditing: false,
  isSelected: false,
};

const CELL_EMPTY_VALUE = ""; // FIXME: Tiago -> This value needs to be parameterized, perhaps. Not all values are strings.

export function BeeTableSelectionContextProvider<R extends object>({ children }: React.PropsWithChildren<{}>) {
  const [selection, setSelection] = useState<BeeTableSelection<R>>({
    active: undefined,
    selectionEnd: undefined,
    selectionStart: undefined,
  });

  const refs = React.useRef<Map<number, Map<number, Set<BeeTableCellRef>>>>(new Map());

  const selectionRef = React.useRef<BeeTableSelection<R>>(selection);

  useEffect(() => {
    selectionRef.current = selection;
  }, [selection]);

  const value = useMemo(() => {
    return {
      activeCell: selection.active,
      selectionStart: selection.selectionStart,
      selectionEnd: selection.selectionEnd,
    };
  }, [selection]);

  const dispatch = useMemo<BeeTableSelectionDispatchContextType<R>>(() => {
    return {
      updateResizingWidths: (
        columnIndex: number,
        getNewResizingWidth: (prev: ResizingWidth | undefined) => ResizingWidth
      ) => {
        for (const c of refs.current?.values()) {
          for (const ref of c.get(columnIndex) ?? []) {
            ref.setResizingWidth?.(getNewResizingWidth(undefined));
          }
        }
      },
      adaptSelection: ({
        atRowIndex,
        rowCountDelta,
        atColumnIndex,
        columnCountDelta,
      }: {
        atRowIndex: number;
        rowCountDelta: number;
        atColumnIndex: number;
        columnCountDelta: number;
      }) => {
        setSelection((prev) => {
          if (!prev || !prev.active || !prev.selectionStart || !prev.selectionEnd) {
            return prev;
          }

          let moveRows = 0;
          let growRows = 0;
          let activeMoveRows = 0;

          if (atRowIndex >= 0) {
            if (atRowIndex <= prev.selectionStart.rowIndex) {
              moveRows = rowCountDelta;
            } else if (atRowIndex <= prev.selectionEnd.rowIndex) {
              growRows = rowCountDelta;
            }

            if (atRowIndex <= prev.active.rowIndex) {
              activeMoveRows = rowCountDelta;
            }
          }

          let moveColumns = 0;
          let growColumns = 0;
          let activeMoveColumns = 0;

          if (atColumnIndex >= 0) {
            if (atColumnIndex <= prev.selectionStart.columnIndex) {
              moveColumns = columnCountDelta;
            } else if (atColumnIndex <= prev.selectionEnd.columnIndex) {
              growColumns = columnCountDelta;
            }

            if (atColumnIndex <= prev.active.columnIndex) {
              activeMoveColumns = columnCountDelta;
            }
          }

          return {
            active: {
              rowIndex: prev.active.rowIndex + activeMoveRows,
              columnIndex: prev.active.columnIndex + activeMoveColumns,
              isEditing: prev.active.isEditing,
            },
            selectionStart: {
              rowIndex: prev.selectionStart.rowIndex + moveRows,
              columnIndex: prev.selectionStart.columnIndex + moveColumns,
              isEditing: prev.selectionStart.isEditing,
            },
            selectionEnd: {
              rowIndex: prev.selectionEnd.rowIndex + moveRows + growRows,
              columnIndex: prev.selectionEnd.columnIndex + moveColumns + growColumns,
              isEditing: prev.selectionEnd.isEditing,
            },
          };
        });
      },
      copy: () => {
        if (!selectionRef.current?.selectionStart || !selectionRef.current?.selectionEnd) {
          return;
        }

        const clipboardMatrix: string[][] = [];

        const { startRow, endRow, startColumn, endColumn } = getSelectionIterationBoundaries(selectionRef.current);
        for (let r = startRow; r <= endRow; r++) {
          clipboardMatrix[r - startRow] ??= [];
          for (let c = startColumn; c <= endColumn; c++) {
            clipboardMatrix[r - startRow] ??= [];
            clipboardMatrix[r - startRow][c - startColumn] = [...(refs.current?.get(r)?.get(c) ?? [])]
              ?.flatMap((ref) => (ref.getValue ? [ref.getValue()] : []))
              .join(""); // FIXME: Tiago -> What to do? Only one ref should be yielding the content
          }
        }

        const clipboardValue = clipboardMatrix
          .map((r) => r.join(CLIPBOARD_COLUMN_SEPARATOR))
          .join(CLIPBOARD_ROW_SEPARATOR);
        navigator.clipboard.writeText(clipboardValue);
      },
      cut: () => {
        if (!selectionRef.current?.selectionStart || !selectionRef.current?.selectionEnd) {
          return;
        }

        const clipboardMatrix: string[][] = [];

        const { startRow, endRow, startColumn, endColumn } = getSelectionIterationBoundaries(selectionRef.current);
        for (let r = startRow; r <= endRow; r++) {
          clipboardMatrix[r - startRow] ??= [];
          for (let c = startColumn; c <= endColumn; c++) {
            clipboardMatrix[r - startRow] ??= [];
            clipboardMatrix[r - startRow][c - startColumn] = [...(refs.current?.get(r)?.get(c) ?? [])]
              ?.flatMap((ref) => {
                ref.setValue?.(CELL_EMPTY_VALUE);
                return ref.getValue ? [ref.getValue()] : [];
              })
              .join(""); // FIXME: Tiago -> What to do? Only one ref should be yielding the content
          }
        }

        const clipboardValue = clipboardMatrix
          .map((row) => row.join(CLIPBOARD_COLUMN_SEPARATOR))
          .join(CLIPBOARD_ROW_SEPARATOR);

        navigator.clipboard.writeText(clipboardValue);
      },
      paste: () => {
        // FIXME: Tiago -> This is currenty very slow, as React 17 state updates are not
        //                 batched, causing every pasted cell to trigger an isolated setState.
        //                 Upgrading to React 18 should fix this slowness.

        // FIXME: Tiago -> Add new columns and rows, based on the clipboard's size.

        navigator.clipboard.readText().then((clipboardValue) => {
          if (!selectionRef.current?.selectionStart || !selectionRef.current?.selectionEnd) {
            return;
          }

          const clipboardMatrix = clipboardValue
            .split(CLIPBOARD_ROW_SEPARATOR)
            .map((r) => r.split(CLIPBOARD_COLUMN_SEPARATOR));

          const { startRow, endRow, startColumn, endColumn } = getSelectionIterationBoundaries(selectionRef.current);

          const pasteEndRow = Math.max(endRow, startRow + clipboardMatrix.length - 1);
          const pasteEndColumn = Math.max(endColumn, startColumn + clipboardMatrix[0].length - 1);

          for (let r = startRow; r <= pasteEndRow; r++) {
            for (let c = startColumn; c <= pasteEndColumn; c++) {
              refs.current
                ?.get(r)
                ?.get(c)
                ?.forEach((e) => e.setValue?.(clipboardMatrix[r - startRow]?.[c - startColumn]));
            }
          }

          setSelection({
            active: {
              rowIndex: startRow,
              columnIndex: startColumn,
              isEditing: false,
            },
            selectionStart: {
              rowIndex: startRow,
              columnIndex: startColumn,
              isEditing: false,
            },
            selectionEnd: {
              rowIndex: pasteEndRow,
              columnIndex: pasteEndColumn,
              isEditing: false,
            },
          });
        });
      },
      erase: () => {
        // FIXME: Tiago -> This is not good. We shouldn't be setting a state just to read it.
        if (!selectionRef.current?.selectionStart || !selectionRef.current?.selectionEnd) {
          return;
        }

        const { startRow, endRow, startColumn, endColumn } = getSelectionIterationBoundaries(selectionRef.current);
        for (let r = startRow; r <= endRow; r++) {
          for (let c = startColumn; c <= endColumn; c++) {
            refs.current
              ?.get(r)
              ?.get(c)
              ?.forEach((ref) => {
                ref.setValue?.(CELL_EMPTY_VALUE);
              });
          }
        }
      },
      setActiveCell: (activeCell) => {
        setSelection((prev) => {
          const newActiveCell = typeof activeCell === "function" ? activeCell(prev.active) : activeCell;
          return {
            active: newActiveCell,
            selectionStart: newActiveCell?.keepSelection ? prev.selectionStart : newActiveCell,
            selectionEnd: newActiveCell?.keepSelection ? prev.selectionEnd : newActiveCell,
          };
        });
      },
      setSelectionEnd: (selectionEnd) => {
        setSelection((prev) => {
          const newSelectionEnd = typeof selectionEnd === "function" ? selectionEnd(prev.selectionEnd) : selectionEnd;

          // Selecting a header cell from another header cell
          // Do not allow selecting multi-line header cells
          if (
            (prev.selectionEnd?.rowIndex ?? 0) < 0 &&
            (newSelectionEnd?.rowIndex ?? 0) < 0 &&
            prev.selectionEnd?.rowIndex !== newSelectionEnd?.rowIndex
          ) {
            return prev;
          }
          // Selecting a rowIndex cell from a header cell.
          // Do not allow selecting rowIndex cells from header cells
          else if ((prev.selectionEnd?.rowIndex ?? 0) < 0 && newSelectionEnd?.columnIndex === 0) {
            return prev;
          }
          // Selecting a normal cell from a rowIndex cell
          // Do not allow leaving the rowIndex cells
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
          // Selecting a normal cell from a header cell
          // Do not allow selecting header and normal cells simultaneously
          else if ((prev.selectionEnd?.rowIndex ?? 0) < 0) {
            return {
              ...prev,
              selectionEnd: {
                columnIndex: newSelectionEnd?.columnIndex ?? 0,
                rowIndex: prev.selectionEnd?.rowIndex ?? 0,
                isEditing: false,
              },
            };
          }
          // Selecting a rowIndex cell from a normal cell
          // Do not allow selecting rowIndex and normal cells simultaneously
          else if (newSelectionEnd?.columnIndex === 0) {
            return {
              ...prev,
              selectionEnd: {
                columnIndex: 1,
                rowIndex: Math.max(0, newSelectionEnd?.rowIndex ?? 0),
                isEditing: false,
              },
            };
          }
          // Selecting a header cell from a normal cell
          // Do not allow selecting rowIndex and normal cells simultaneously
          else if ((newSelectionEnd?.rowIndex ?? 0) < 0) {
            return {
              ...prev,
              selectionEnd: {
                columnIndex: newSelectionEnd?.columnIndex ?? 0,
                rowIndex: 0,
                isEditing: false,
              },
            };
          }
          // Selecting a normal cell from another normal cell
          else {
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
        ref.setStatus?.(NEUTRAL_CELL_STATUS);
        refs.current?.get(rowIndex)?.get(columnIndex)?.delete(ref);
      },
    };
  }, []);

  useEffect(() => {
    if (!selection.active || !selection.selectionStart || !selection.selectionEnd) {
      return;
    }

    const currentRefs = refs.current;
    const active = selection.active;

    const { startRow, endRow, startColumn, endColumn } = getSelectionIterationBoundaries(selection);
    for (let r = startRow; r <= endRow; r++) {
      // Select rowIndex cells
      currentRefs
        .get(r)
        ?.get(0)
        ?.forEach((e) => e.setStatus?.({ isActive: false, isEditing: false, isSelected: true }));

      for (let c = startColumn; c <= endColumn; c++) {
        // Select header cells
        currentRefs
          .get(-1)
          ?.get(c)
          ?.forEach((e) => e.setStatus?.({ isActive: false, isEditing: false, isSelected: true }));

        // Select normal cells
        const refs = currentRefs.get(r)?.get(c);
        refs?.forEach((ref) =>
          ref.setStatus?.({
            isActive: false,
            isEditing: false,
            isSelected: true,
            selectedPositions: [
              ...(r === startRow ? [BeeTableSelectionPosition.Top] : []),
              ...(r === endRow ? [BeeTableSelectionPosition.Bottom] : []),
              ...(c === startColumn ? [BeeTableSelectionPosition.Left] : []),
              ...(c === endColumn ? [BeeTableSelectionPosition.Right] : []),
            ],
          })
        );
      }
    }

    // Active cell. Sometimes it is not inside the selection.
    currentRefs
      .get(active.rowIndex)
      ?.get(active.columnIndex)
      ?.forEach((r) =>
        r.setStatus?.({
          isActive: true,
          isEditing: active?.isEditing ?? false,
          isSelected: !(
            selection.selectionStart?.rowIndex === selection.selectionEnd?.rowIndex &&
            selection.selectionStart?.columnIndex === selection.selectionEnd?.columnIndex
          ),
        })
      );

    // Cleanup
    return () => {
      for (let r = startRow; r <= endRow; r++) {
        currentRefs
          .get(r)
          ?.get(0)
          ?.forEach((e) => e.setStatus?.(NEUTRAL_CELL_STATUS));

        for (let c = startColumn; c <= endColumn; c++) {
          currentRefs
            .get(-1)
            ?.get(c)
            ?.forEach((e) => e.setStatus?.(NEUTRAL_CELL_STATUS));

          const refs = currentRefs.get(r)?.get(c);
          refs?.forEach((ref) => ref.setStatus?.(NEUTRAL_CELL_STATUS));
        }
      }

      currentRefs
        .get(active.rowIndex)
        ?.get(active.columnIndex)
        ?.forEach((r) => r.setStatus?.(NEUTRAL_CELL_STATUS));
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
export function useBeeTableCell(
  rowIndex: number,
  columnIndex: number,
  setValue?: BeeTableCellRef["setValue"],
  getValue?: BeeTableCellRef["getValue"]
) {
  const { subscribeToCellStatus, unsubscribeToCellStatus } = useBeeTableSelectionDispatch();

  const [status, setStatus] = useState<BeeTableCellStatus>(NEUTRAL_CELL_STATUS);

  useEffect(() => {
    const ref = subscribeToCellStatus(rowIndex, columnIndex, {
      setStatus,
      setValue,
      getValue,
    });
    return () => {
      unsubscribeToCellStatus(rowIndex, columnIndex, ref);
    };
  }, [columnIndex, rowIndex, getValue, setValue, subscribeToCellStatus, unsubscribeToCellStatus]);

  return status;
}

function getSelectionIterationBoundaries(selection: BeeTableSelection<any>) {
  // Let's always go smaller to bigger, no matter the direction of the selection.
  return {
    startColumn: Math.min(selection.selectionStart?.columnIndex ?? 0, selection.selectionEnd?.columnIndex ?? 0),
    endColumn: Math.max(selection.selectionStart?.columnIndex ?? 0, selection.selectionEnd?.columnIndex ?? 0),
    startRow: Math.min(selection.selectionStart?.rowIndex ?? 0, selection.selectionEnd?.rowIndex ?? 0),
    endRow: Math.max(selection.selectionStart?.rowIndex ?? 0, selection.selectionEnd?.rowIndex ?? 0),
  };
}
