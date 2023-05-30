import * as React from "react";
import { useCallback, useEffect, useLayoutEffect, useMemo, useState } from "react";
import { assertUnreachable } from "../expressions/ExpressionDefinitionRoot/ExpressionDefinitionLogicTypeSelector";
import { ResizingWidth, useResizingWidthsDispatch } from "../resizing/ResizingWidthsContext";

export const SELECTION_MIN_ACTIVE_DEPTH = -1;
export const SELECTION_MIN_MAX_DEPTH = 0;

export const INITIAL_CURRENT_DEPTH = {
  active: undefined,
  max: SELECTION_MIN_MAX_DEPTH,
};

export interface BeeTableCellCoordinates {
  columnIndex: number;
  rowIndex: number;
}

export interface BeeTableSelectionActiveCell {
  columnIndex: number;
  rowIndex: number;
  isEditing: boolean;
}

export enum SelectionPart {
  ActiveCell,
  SelectionEnd,
  SelectionStart,
}

export interface BeeTableSelectionContextType {
  activeCell: BeeTableSelectionActiveCell | undefined;
  activeCellForNestedTables: BeeTableSelectionActiveCell | undefined;
  selectionEnd: BeeTableSelectionActiveCell | undefined;
  selectionStart: BeeTableSelectionActiveCell | undefined;
  currentDepth: { active: number | undefined; max: number };
  depth: number;
  isSelectionHere: boolean;
}

export interface BeeTableCoordinatesDispatchContextType {
  setMaxDepth: React.Dispatch<React.SetStateAction<number>>;
}

export interface BeeTableSelectionDispatchContextType {
  setCurrentDepth: React.Dispatch<React.SetStateAction<{ active: number | undefined; max: number }>>;
  erase(): void;
  copy(): void;
  cut(): void;
  paste(): void;
  adaptSelection(args: {
    atRowIndex: number;
    rowCountDelta: number;
    atColumnIndex: number;
    columnCountDelta: number;
  }): void;
  mutateSelection: (args: {
    part: SelectionPart;
    columnCount: (rowIndex: number) => number;
    rowCount: number;
    deltaColumns: number;
    deltaRows: number;
    isEditingActiveCell: boolean;
    keepInsideSelection: boolean;
  }) => void;
  resetSelectionAt: React.Dispatch<
    React.SetStateAction<(BeeTableSelectionActiveCell & { keepSelection?: boolean }) | undefined>
  >;
  setSelectionEnd: React.Dispatch<React.SetStateAction<BeeTableSelectionActiveCell | undefined>>;
  registerSelectableCellRef(rowIndex: number, columnIndex: number, ref: BeeTableCellRef): BeeTableCellRef;
  deregisterSelectableCellRef(rowIndex: number, columnIndex: number, ref: BeeTableCellRef): void;
}

export interface BeeTableCoordinatesContextType {
  containerCellCoordinates: BeeTableCellCoordinates | undefined;
}

export const BeeTableSelectionContext = React.createContext<BeeTableSelectionContextType>({
  activeCell: undefined,
  activeCellForNestedTables: undefined,
  selectionEnd: undefined,
  selectionStart: undefined,
  currentDepth: INITIAL_CURRENT_DEPTH,
  depth: SELECTION_MIN_ACTIVE_DEPTH,
  isSelectionHere: true,
});

export const BeeTableCoordinatesContext = React.createContext<BeeTableCoordinatesContextType>({
  containerCellCoordinates: undefined,
});

export const BeeTableCoordinatesDispatchContext = React.createContext<BeeTableCoordinatesDispatchContextType>(
  {} as any
);

export const BeeTableSelectionDispatchContext = React.createContext<BeeTableSelectionDispatchContextType>({} as any);

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

export interface BeeTableSelection {
  active: BeeTableSelectionActiveCell | undefined;
  selectionEnd: BeeTableSelectionActiveCell | undefined;
  selectionStart: BeeTableSelectionActiveCell | undefined;
}

export enum BeeTableSelectionPosition {
  Top = "top",
  Bottom = "bottom",
  Left = "left",
  Right = "right",
}

const CLIPBOARD_ROW_SEPARATOR = "\n";
const CLIPBOARD_COLUMN_SEPARATOR = "\t";

const NEUTRAL_SELECTION = {
  active: undefined,
  selectionEnd: undefined,
  selectionStart: undefined,
};

const NEUTRAL_CELL_STATUS = {
  isActive: false,
  isEditing: false,
  isSelected: false,
};

const CELL_EMPTY_VALUE = ""; // This value needs to be parameterized, perhaps. Not all values are strings. See https://github.com/kiegroup/kie-issues/issues/170.

export function BeeTableCoordinatesContextProvider({
  children,
  coordinates,
}: React.PropsWithChildren<{ coordinates: BeeTableCellCoordinates }>) {
  const { activeCell, depth } = useBeeTableSelection();

  //

  const { setMaxDepth: setParentMaxDepth } = useBeeTableCoordinatesDispatch();
  const [_maxDepth, _setMaxDepth] = useState<number>(depth);

  const { setCurrentDepth } = useBeeTableSelectionDispatch();

  const setMaxDepth: React.Dispatch<React.SetStateAction<number>> = useCallback(
    (newMaxDepthAction) => {
      setParentMaxDepth?.(newMaxDepthAction);
      _setMaxDepth?.(newMaxDepthAction);
    },
    [setParentMaxDepth]
  );

  useEffect(() => {
    setMaxDepth((prev) => Math.max(prev, depth));
  }, [coordinates.columnIndex, coordinates.rowIndex, depth, setMaxDepth]);

  //

  useEffect(() => {
    if (coincides(activeCell, coordinates)) {
      setCurrentDepth((prev) => ({
        active: prev.active,
        max: _maxDepth,
      }));
    }
  }, [_maxDepth, activeCell, coordinates, depth, setCurrentDepth]);

  //

  const value = useMemo<BeeTableCoordinatesContextType>(() => {
    return {
      containerCellCoordinates: coordinates,
    };
  }, [coordinates]);

  const dispatch = useMemo(() => {
    return {
      setMaxDepth,
    };
  }, [setMaxDepth]);

  return (
    <BeeTableCoordinatesContext.Provider value={value}>
      <BeeTableCoordinatesDispatchContext.Provider value={dispatch}>
        {children}
      </BeeTableCoordinatesDispatchContext.Provider>
    </BeeTableCoordinatesContext.Provider>
  );
}

export function BeeTableSelectionContextProvider({ children }: React.PropsWithChildren<{}>) {
  const refs = React.useRef<Map<number, Map<number, Set<BeeTableCellRef>>>>(new Map());

  const [_selection, _setSelection] = useState<BeeTableSelection>(NEUTRAL_SELECTION);
  const [_currentDepth, _setCurrentDepth] =
    useState<{ active: number | undefined; max: number }>(INITIAL_CURRENT_DEPTH);

  const {
    isSelectionHere: isParentSelectionThere,
    activeCellForNestedTables: parentActiveCell,
    currentDepth: parentCurrentDepth,
    depth: parentDepth,
  } = useBeeTableSelection();

  const { setCurrentDepth: setParentCurrentDepth, resetSelectionAt: resetParentSelectionAt } =
    useBeeTableSelectionDispatch();
  const { containerCellCoordinates } = useBeeTableCoordinates();

  //

  const depth = parentDepth + 1;
  const activeDepth = parentCurrentDepth.active ?? _currentDepth.active;
  const activeMaxDepth = Math.max(parentCurrentDepth.max, _currentDepth.max);
  const setCurrentDepth = setParentCurrentDepth ?? _setCurrentDepth;

  const isSelectionHere = useMemo(() => {
    return coincides(parentActiveCell, containerCellCoordinates) && isParentSelectionThere;
  }, [containerCellCoordinates, isParentSelectionThere, parentActiveCell]);

  const selection = useMemo(() => {
    if (depth === activeDepth && isSelectionHere) {
      return _selection;
    }

    return NEUTRAL_SELECTION;
  }, [_selection, activeDepth, depth, isSelectionHere]);

  const selectionRef = React.useRef<BeeTableSelection>(selection);
  useEffect(() => {
    selectionRef.current = selection;
  }, [selection]);

  //
  // paste batching strategy (begin)
  //
  // This is a hack to make React batch the multiple state updates we're doing here with the calls to `setValue`.
  // Every call to `setValue` mutates the expression, so batching is essential for performance reasons.
  // This effect runs once when pasteData is truthy. Then, after running, it sets pasteData to a falsy value, which short-circuits it.
  //
  // This can be refactored to be simpler when upgrading to React 18, as batching is automatic, even outside event handlers and hooks.
  const [pasteData, setPasteData] = useState("");
  useEffect(() => {
    if (!pasteData) {
      return;
    }

    const clipboardValue = pasteData;

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

    _setSelection({
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

    setPasteData("");
  }, [pasteData]);

  // paste batching strategy (end)

  const value = useMemo(() => {
    return {
      activeCell: selection.active,
      selectionStart: selection.selectionStart,
      selectionEnd: selection.selectionEnd,
      activeCellForNestedTables: _selection.active,
      currentDepth: {
        active: activeDepth,
        max: activeMaxDepth,
      },
      depth,
      isSelectionHere,
    };
  }, [
    _selection.active,
    activeDepth,
    activeMaxDepth,
    depth,
    isSelectionHere,
    selection.active,
    selection.selectionEnd,
    selection.selectionStart,
  ]);

  const dispatch = useMemo<BeeTableSelectionDispatchContextType>(() => {
    return {
      setCurrentDepth: (newCurrentDepthAction) => {
        setCurrentDepth((prev) => {
          const newCurrentDepth =
            typeof newCurrentDepthAction === "function"
              ? newCurrentDepthAction(prev ?? SELECTION_MIN_ACTIVE_DEPTH)
              : newCurrentDepthAction;

          return {
            max: Math.max(SELECTION_MIN_MAX_DEPTH, newCurrentDepth.max),
            active: newCurrentDepth.active ?? SELECTION_MIN_ACTIVE_DEPTH,
          };
        });
      },
      mutateSelection: ({
        part,
        columnCount,
        rowCount,
        deltaColumns,
        deltaRows,
        isEditingActiveCell,
        keepInsideSelection,
      }) => {
        _setSelection((prev) => {
          if (!prev.active) {
            return prev;
          }

          const isExpanded = isSelectionExpanded(prev);
          const { startRow, startColumn, endRow, endColumn } = getSelectionIterationBoundaries(prev);

          const boundaries =
            isExpanded && keepInsideSelection
              ? {
                  rows: { min: startRow, max: endRow },
                  columns: { min: startColumn, max: endColumn },
                }
              : {
                  rows: { min: 0, max: rowCount - 1 },
                  columns: { min: 1, max: columnCount(prev.active.rowIndex) - 1 },
                };

          const prevCoords =
            part === SelectionPart.ActiveCell
              ? {
                  rowIndex: prev.active.rowIndex,
                  columnIndex: prev.active.columnIndex,
                }
              : part === SelectionPart.SelectionEnd
              ? {
                  rowIndex: prev.selectionEnd?.rowIndex,
                  columnIndex: prev.selectionEnd?.columnIndex,
                }
              : part === SelectionPart.SelectionStart
              ? {
                  rowIndex: prev.selectionStart?.rowIndex,
                  columnIndex: prev.selectionStart?.columnIndex,
                }
              : (() => {
                  throw new Error("Impossible case for SelectionPart");
                })();

          const newRowIndex =
            (prevCoords.rowIndex ?? 0) < 0
              ? prevCoords.rowIndex ?? 0 // Don't move away from header cells
              : Math.min(boundaries.rows.max, Math.max(boundaries.rows.min, (prevCoords.rowIndex ?? 0) + deltaRows));

          const newColumnIndex =
            prevCoords.columnIndex === 0
              ? prevCoords.columnIndex // Don't move away from rowIndex cells
              : Math.min(
                  boundaries.columns.max,
                  Math.max(boundaries.columns.min, (prevCoords.columnIndex ?? 0) + deltaColumns)
                );

          switch (part) {
            case SelectionPart.SelectionEnd:
              return {
                ...prev,
                selectionEnd: {
                  rowIndex: newRowIndex,
                  columnIndex: newColumnIndex,
                  isEditing: prev.selectionEnd?.isEditing ?? false,
                },
              };
            case SelectionPart.SelectionStart:
              return {
                ...prev,
                selectionStart: {
                  rowIndex: newRowIndex,
                  columnIndex: newColumnIndex,
                  isEditing: prev.selectionStart?.isEditing ?? false,
                },
              };
            case SelectionPart.ActiveCell:
              if (!isExpanded || !keepInsideSelection) {
                return {
                  active: {
                    rowIndex: newRowIndex,
                    columnIndex: newColumnIndex,
                    isEditing: isEditingActiveCell,
                  },
                  selectionEnd: {
                    rowIndex: newRowIndex,
                    columnIndex: newColumnIndex,
                    isEditing: false,
                  },
                  selectionStart: {
                    rowIndex: newRowIndex,
                    columnIndex: newColumnIndex,
                    isEditing: false,
                  },
                };
              }

              // Wrap-around inside selection
              //
              // Direction: left-to-right, top-to-bottom
              //
              // ===============================================
              // Enter         --> Top-Down, LTR
              // Shift + Enter --> Bottom-Up, RTL
              // Tab           --> LTR, Top-Down
              // Shift + Tab   --> RTL, Bottom-Up
              // ===============================================

              const targetRow = prev.active.rowIndex + deltaRows;
              const targetColumn = prev.active.columnIndex + deltaColumns;

              if (targetRow > boundaries.rows.max) {
                const nextColumn = prev.active.columnIndex + 1;
                return {
                  ...prev,
                  active: {
                    rowIndex: boundaries.rows.min,
                    columnIndex: nextColumn > boundaries.columns.max ? boundaries.columns.min : nextColumn,
                    isEditing: isEditingActiveCell,
                  },
                };
              } else if (targetColumn < boundaries.columns.min) {
                const previousRow = prev.active.rowIndex - 1;
                return {
                  ...prev,
                  active: {
                    rowIndex: previousRow < boundaries.rows.min ? boundaries.rows.max : previousRow,
                    columnIndex: boundaries.columns.max,
                    isEditing: isEditingActiveCell,
                  },
                };
              } else if (targetColumn > boundaries.columns.max) {
                const nextRow = prev.active.rowIndex + 1;
                return {
                  ...prev,
                  active: {
                    rowIndex: nextRow > boundaries.rows.max ? boundaries.rows.min : nextRow,
                    columnIndex: boundaries.columns.min,
                    isEditing: isEditingActiveCell,
                  },
                };
              } else if (targetRow < boundaries.rows.min) {
                const previousColumn = prev.active.columnIndex - 1;
                return {
                  ...prev,
                  active: {
                    rowIndex: boundaries.rows.max,
                    columnIndex: previousColumn < boundaries.columns.min ? boundaries.columns.max : previousColumn,
                    isEditing: isEditingActiveCell,
                  },
                };
              } else {
                return {
                  ...prev,
                  active: {
                    rowIndex: newRowIndex,
                    columnIndex: newColumnIndex,
                    isEditing: isEditingActiveCell,
                  },
                };
              }
            default:
              assertUnreachable(part);
          }
        });
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
        _setSelection((prev) => {
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
              .join(""); // FIXME: What to do? Only one ref should be yielding the content. See https://github.com/kiegroup/kie-issues/issues/170
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
              .join(""); // What to do? Only one ref should be yielding the content. See https://github.com/kiegroup/kie-issues/issues/170
          }
        }

        const clipboardValue = clipboardMatrix
          .map((row) => row.join(CLIPBOARD_COLUMN_SEPARATOR))
          .join(CLIPBOARD_ROW_SEPARATOR);

        navigator.clipboard.writeText(clipboardValue);
      },
      paste: () => {
        navigator.clipboard.readText().then((clipboardValue) => {
          setPasteData(clipboardValue);
        });
      },
      erase: () => {
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
      resetSelectionAt: (newSelectionAction) => {
        resetParentSelectionAt?.({
          columnIndex: containerCellCoordinates?.columnIndex ?? 1,
          rowIndex: containerCellCoordinates?.rowIndex ?? 0,
          isEditing: false,
        });

        if (!newSelectionAction) {
          setCurrentDepth((prev) => ({
            max: prev.max,
            active: Math.max(SELECTION_MIN_ACTIVE_DEPTH, depth - 1),
          }));
          return;
        }

        setCurrentDepth((prev) => ({
          max: prev.max,
          active: depth,
        }));

        _setSelection((prev) => {
          const newActiveCell =
            typeof newSelectionAction === "function" //
              ? newSelectionAction(prev.active)
              : newSelectionAction;

          return {
            active: newActiveCell,
            selectionStart: newActiveCell?.keepSelection ? prev.selectionStart : newActiveCell,
            selectionEnd: newActiveCell?.keepSelection ? prev.selectionEnd : newActiveCell,
          };
        });
      },
      setSelectionEnd: (newSelectionEndAction) => {
        _setSelection((prev) => {
          const newSelectionEnd =
            typeof newSelectionEndAction === "function"
              ? newSelectionEndAction(prev.selectionEnd)
              : newSelectionEndAction;

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
      registerSelectableCellRef: (rowIndex, columnIndex, ref) => {
        refs.current?.set(rowIndex, refs.current?.get(rowIndex) ?? new Map());
        const prev = refs.current?.get(rowIndex)?.get(columnIndex) ?? new Set();
        refs.current?.get(rowIndex)?.set(columnIndex, new Set([...prev, ref]));
        const isActive = coincides(selectionRef.current?.active, { rowIndex, columnIndex });
        ref.setStatus?.({
          isActive,
          isEditing: isActive && (selectionRef.current?.active?.isEditing ?? false),
          isSelected: !isActive && isCellSelected(rowIndex, columnIndex, selectionRef.current),
        });
        return ref;
      },
      deregisterSelectableCellRef: (rowIndex, columnIndex, ref) => {
        ref.setStatus?.(NEUTRAL_CELL_STATUS);
        refs.current?.get(rowIndex)?.get(columnIndex)?.delete(ref);
      },
    };
  }, [
    containerCellCoordinates?.columnIndex,
    containerCellCoordinates?.rowIndex,
    depth,
    resetParentSelectionAt,
    setCurrentDepth,
  ]);

  // If there's no selection on the table that is coming into focus, we focus at the top-left cell.
  useEffect(() => {
    if (!selection.active && depth === activeDepth && isSelectionHere) {
      dispatch.resetSelectionAt({
        rowIndex: 0,
        columnIndex: 1,
        isEditing: false,
      });
    }
  }, [activeDepth, containerCellCoordinates, depth, dispatch, isSelectionHere, parentActiveCell, selection]);

  // Paint the selection
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
        if (startRow >= 0) {
          currentRefs
            .get(-1)
            ?.get(c)
            ?.forEach((e) => e.setStatus?.({ isActive: false, isEditing: false, isSelected: true }));
        }

        // Select normal cells
        const refs = currentRefs.get(r)?.get(c);
        refs?.forEach((ref) =>
          ref.setStatus?.({
            isActive: false,
            isEditing: false,
            isSelected: true,
            selectedPositions: getSelectedPositions(selection, { rowIndex: r, columnIndex: c }),
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
          isSelected: !coincides(selectionRef.current?.selectionStart, selectionRef.current?.selectionEnd),
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
  }, [selection, selectionRef.current?.selectionStart, selectionRef.current?.selectionEnd]);

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

export function useBeeTableCoordinates() {
  return React.useContext(BeeTableCoordinatesContext);
}

export function useBeeTableCoordinatesDispatch() {
  return React.useContext(BeeTableCoordinatesDispatchContext);
}

/**
 * This is done like this because if when we have every Th/Td observing { activeCell } from BeeTableSelectionContext,
 * performance suffers. Every component can register a BeeTableCellRef and observe changes to it, then set their own state with a "copy" from the status.
 */
export function useBeeTableSelectableCellRef(
  rowIndex: number,
  columnIndex: number,
  setValue?: BeeTableCellRef["setValue"],
  getValue?: BeeTableCellRef["getValue"]
) {
  const { registerSelectableCellRef, deregisterSelectableCellRef } = useBeeTableSelectionDispatch();

  const [status, setStatus] = useState<BeeTableCellStatus>(NEUTRAL_CELL_STATUS);

  useLayoutEffect(() => {
    const ref = registerSelectableCellRef?.(rowIndex, columnIndex, {
      setStatus,
      setValue,
      getValue,
    });
    return () => {
      deregisterSelectableCellRef?.(rowIndex, columnIndex, ref);
    };
  }, [columnIndex, rowIndex, getValue, setValue, registerSelectableCellRef, deregisterSelectableCellRef]);

  return status;
}

function getSelectionIterationBoundaries(selection: BeeTableSelection) {
  // Let's always go smaller to bigger, no matter the direction of the selection.
  return {
    startColumn: Math.min(selection.selectionStart?.columnIndex ?? 0, selection.selectionEnd?.columnIndex ?? 0),
    endColumn: Math.max(selection.selectionStart?.columnIndex ?? 0, selection.selectionEnd?.columnIndex ?? 0),
    startRow: Math.min(selection.selectionStart?.rowIndex ?? 0, selection.selectionEnd?.rowIndex ?? 0),
    endRow: Math.max(selection.selectionStart?.rowIndex ?? 0, selection.selectionEnd?.rowIndex ?? 0),
  };
}

function getSelectedPositions(selection: BeeTableSelection, cell: BeeTableCellCoordinates) {
  const { startRow, endRow, startColumn, endColumn } = getSelectionIterationBoundaries(selection);
  return [
    ...(cell.rowIndex === startRow ? [BeeTableSelectionPosition.Top] : []),
    ...(cell.rowIndex === endRow ? [BeeTableSelectionPosition.Bottom] : []),
    ...(cell.columnIndex === startColumn ? [BeeTableSelectionPosition.Left] : []),
    ...(cell.columnIndex === endColumn ? [BeeTableSelectionPosition.Right] : []),
  ];
}

function coincides(a: BeeTableCellCoordinates | undefined, b: BeeTableCellCoordinates | undefined) {
  return a?.columnIndex === b?.columnIndex && a?.rowIndex === b?.rowIndex;
}

function isCellSelected(row: number, column: number, current: BeeTableSelection) {
  if (!current.selectionEnd && !current.selectionStart) {
    return false;
  }

  const bounds = getSelectionIterationBoundaries(current);

  return row >= bounds.startRow && row <= bounds.endRow && column >= bounds.startColumn && column <= bounds.endColumn;
}

function isSelectionExpanded(selection: BeeTableSelection) {
  return !coincides(selection.active, selection.selectionEnd) || !coincides(selection.active, selection.selectionStart);
}

export function useBeeTableSelectableCell(
  cellRef: React.RefObject<HTMLTableCellElement>,
  rowIndex: number,
  columnIndex: number,
  setValue?: BeeTableCellRef["setValue"],
  getValue?: BeeTableCellRef["getValue"]
) {
  const { isResizing } = useResizingWidthsDispatch();
  const { isActive, isEditing, isSelected, selectedPositions } = useBeeTableSelectableCellRef(
    rowIndex,
    columnIndex,
    setValue,
    getValue
  );

  const cssClasses = useMemo(() => {
    return `
      ${isActive ? "active" : ""}
      ${isEditing ? "editing" : ""} 
      ${isSelected ? "selected" : ""}
      ${(selectedPositions?.length ?? 0) <= 0 ? "middle" : selectedPositions?.join(" ")}
    `;
  }, [isActive, isEditing, isSelected, selectedPositions]);

  const { resetSelectionAt, setSelectionEnd } = useBeeTableSelectionDispatch();

  const onMouseDown = useCallback(
    (e: React.MouseEvent) => {
      e.stopPropagation();

      // That's the right-click case to open the Context Menu at the right place.
      if (e.button !== 0 && isSelected) {
        resetSelectionAt({
          columnIndex,
          rowIndex,
          isEditing: false,
          keepSelection: true,
        });
        return;
      }

      if (!isActive && !isEditing) {
        const set = e.shiftKey ? setSelectionEnd : resetSelectionAt;
        set({
          columnIndex,
          rowIndex,
          isEditing: false,
        });
      }
    },
    [columnIndex, isActive, isEditing, isSelected, rowIndex, resetSelectionAt, setSelectionEnd]
  );

  const onDoubleClick = useCallback(
    (e: React.MouseEvent) => {
      e.stopPropagation();

      resetSelectionAt({
        columnIndex,
        rowIndex,
        isEditing: columnIndex > 0, // Not rowIndex column
      });
    },
    [columnIndex, rowIndex, resetSelectionAt]
  );

  useEffect(() => {
    function onEnter(e: MouseEvent) {
      e.stopPropagation();

      // User is pressing the left mouse button. Meaning user is dragging.
      // Not a final solution, as user can start dragging from anywhere.
      // Ideally, we want users to change selection only when the dragging originates
      // some other cell within the table.
      if (e.buttons === 1 && e.button === 0 && !isResizing()) {
        setSelectionEnd({
          columnIndex,
          rowIndex,
          isEditing: false,
        });
      }
    }

    const cell = cellRef.current;
    cell?.addEventListener("mouseenter", onEnter);
    return () => {
      cell?.removeEventListener("mouseenter", onEnter);
    };
  }, [columnIndex, rowIndex, resetSelectionAt, setSelectionEnd, cellRef, isResizing]);

  useLayoutEffect(() => {
    if (isActive && !isEditing) {
      cellRef.current?.focus();
    }
  }, [columnIndex, isActive, isEditing, rowIndex, cellRef]);

  return {
    cssClasses,
    onMouseDown,
    onDoubleClick,
    isActive,
  };
}
