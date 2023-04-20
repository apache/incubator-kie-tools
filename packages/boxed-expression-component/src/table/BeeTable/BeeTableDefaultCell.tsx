import * as React from "react";
import { useCallback, useEffect } from "react";
import { BeeTableCellUpdate } from ".";
import { BeeTableEditableCellContent } from "./BeeTableEditableCellContent";
import { useBeeTableSelectableCellRef } from "../../selection/BeeTableSelectionContext";
import { useBoxedExpressionEditor } from "../../expressions/BoxedExpressionEditor/BoxedExpressionEditorContext";
import * as ReactTable from "react-table";

export function BeeTableDefaultCell<R extends object>({
  cellProps,
  onCellUpdates,
  isReadOnly,
  columnIndex,
  setEditing,
  navigateHorizontally,
  navigateVertically,
}: {
  isReadOnly: boolean;
  cellProps: ReactTable.CellProps<R, string | { content: string; id: string }>;
  onCellUpdates?: (cellUpdates: BeeTableCellUpdate<R>[]) => void;
  columnIndex: number;
  setEditing: React.Dispatch<React.SetStateAction<boolean>>;
  navigateVertically: (args: { isShiftPressed: boolean }) => void;
  navigateHorizontally: (args: { isShiftPressed: boolean }) => void;
}) {
  const onCellChanged = useCallback(
    (value: string) => {
      onCellUpdates?.([
        {
          value,
          row: cellProps.row.original,
          rowIndex: cellProps.row.index,
          column: cellProps.column,
          columnIndex: columnIndex - 1, // Subtract one because of the rowIndex column.
        },
      ]);
    },
    [cellProps.column, cellProps.row.index, cellProps.row.original, columnIndex, onCellUpdates]
  );

  const getValue = useCallback(() => {
    return typeof cellProps.value === "string" ? cellProps.value : cellProps.value.content;
  }, [cellProps.value]);

  const { isActive, isEditing } = useBeeTableSelectableCellRef(
    cellProps.row.index,
    columnIndex,
    onCellChanged,
    getValue
  );

  const { beeGwtService } = useBoxedExpressionEditor();

  useEffect(() => {
    if (isActive) {
      beeGwtService?.selectObject(typeof cellProps.value === "string" ? "" : cellProps.value?.id);
    }
  }, [isActive, beeGwtService, cellProps]);

  return (
    <BeeTableEditableCellContent
      isEditing={isEditing}
      isActive={isActive}
      setEditing={setEditing}
      onChange={onCellChanged}
      value={typeof cellProps.value === "string" ? cellProps.value : cellProps.value?.content}
      isReadOnly={isReadOnly}
      onFeelEnterKeyDown={navigateVertically}
      onFeelTabKeyDown={navigateHorizontally}
    />
  );
}
