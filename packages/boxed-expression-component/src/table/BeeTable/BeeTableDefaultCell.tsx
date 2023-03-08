import * as React from "react";
import { useCallback } from "react";
import { BeeTableCellUpdate } from ".";
import { BeeTableEditableCellContent } from "./BeeTableEditableCellContent";
import { useBeeTableSelectableCellRef } from "../../selection/BeeTableSelectionContext";
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
  cellProps: ReactTable.CellProps<R>;
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
    return cellProps.value;
  }, [cellProps.value]);

  const { isActive, isEditing } = useBeeTableSelectableCellRef(
    cellProps.row.index,
    columnIndex,
    onCellChanged,
    getValue
  );

  return (
    <BeeTableEditableCellContent
      isEditing={isEditing}
      isActive={isActive}
      setEditing={setEditing}
      onChange={onCellChanged}
      value={cellProps.value}
      isReadOnly={isReadOnly}
      onFeelEnterKeyDown={navigateVertically}
      onFeelTabKeyDown={navigateHorizontally}
    />
  );
}
