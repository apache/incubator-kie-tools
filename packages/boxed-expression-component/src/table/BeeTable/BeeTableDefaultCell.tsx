import * as React from "react";
import { useMemo, useCallback } from "react";
import { BeeTableCellUpdate } from ".";
import { BeeTableEditableCellContent } from "./BeeTableEditableCellContent";
import {
  useBeeTableSelectionDispatch,
  SelectionPart,
  useBeeTableSelectableCellRef,
} from "../../selection/BeeTableSelectionContext";
import * as ReactTable from "react-table";

export function BeeTableDefaultCell<R extends object>({
  cellProps,
  onCellUpdates,
  isReadOnly,
  hasAdditionalRow,
}: {
  isReadOnly: boolean;
  cellProps: ReactTable.CellProps<R>;
  onCellUpdates?: (cellUpdates: BeeTableCellUpdate<R>[]) => void;
  hasAdditionalRow: boolean;
}) {
  const { mutateSelection } = useBeeTableSelectionDispatch();

  const columnIndex = useMemo(() => {
    return cellProps.allColumns.findIndex((c) => c.id === cellProps.column.id);
  }, [cellProps.allColumns, cellProps.column.id]);

  const rowCount = useMemo(() => {
    return cellProps.rows.length + (hasAdditionalRow ? 1 : 0);
  }, [hasAdditionalRow, cellProps.rows.length]);

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

  const setEditing = useCallback(
    (isEditing: boolean) => {
      mutateSelection({
        part: SelectionPart.ActiveCell,
        columnCount: cellProps.allColumns.length,
        rowCount,
        deltaColumns: 0,
        deltaRows: 0,
        isEditingActiveCell: isEditing,
        keepInsideSelection: true,
      });
    },
    [cellProps.allColumns.length, rowCount, mutateSelection]
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

  const navigateVertically = useCallback(
    (args: { isShiftPressed: boolean }) => {
      mutateSelection({
        part: SelectionPart.ActiveCell,
        columnCount: cellProps.allColumns.length,
        rowCount,
        deltaColumns: 0,
        deltaRows: args.isShiftPressed ? -1 : 1,
        isEditingActiveCell: false,
        keepInsideSelection: true,
      });
    },
    [cellProps.allColumns.length, rowCount, mutateSelection]
  );

  const navigateHorizontally = useCallback(
    (args: { isShiftPressed: boolean }) => {
      mutateSelection({
        part: SelectionPart.ActiveCell,
        columnCount: cellProps.allColumns.length,
        rowCount,
        deltaColumns: args.isShiftPressed ? -1 : 1,
        deltaRows: 0,
        isEditingActiveCell: false,
        keepInsideSelection: true,
      });
    },
    [cellProps.allColumns.length, rowCount, mutateSelection]
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
