import * as React from "react";
import * as ReactTable from "react-table";
import { useFillingResizingWidth } from "../../resizing/FillingColumnResizingWidth";

export function BeeTableThController({
  columnIndex,
  column,
  reactTableInstance,
  shouldRenderRowIndexColumn,
}: {
  columnIndex: number;
  column: ReactTable.ColumnInstance<any>;
  reactTableInstance: ReactTable.TableInstance<any>;
  shouldRenderRowIndexColumn: boolean;
}) {
  useFillingResizingWidth(columnIndex, column, reactTableInstance, shouldRenderRowIndexColumn);
  return <></>;
}
