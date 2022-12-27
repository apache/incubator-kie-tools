/*
 * Copyright 2020 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import * as React from "react";
import * as ReactTable from "react-table";
import { BeeTableCellUpdate, BeeTableColumnUpdate } from "../table/BeeTable/BeeTableHeader";

export interface BeeTableRowsUpdateArgs<R extends object> {
  rows: R[];
  operation?: BeeTableOperation;
  rowIndex?: number;
  columns?: ReactTable.Column<R>[];
}

export interface BeeTableCellProps<R extends object> {
  data: readonly R[];
  rowIndex: number;
  columnIndex: number;
  columnId: string;
}

export interface BeeTableProps<R extends object> {
  /** Table identifier, useful for nested structures */
  tableId?: string;
  /** Optional children element to be appended below the table content */
  additionalRow?: React.ReactElement[];
  /** Optional label, that may depend on column, to be used for the popover that appears when clicking on column header */
  editColumnLabel?: string | { [groupType: string]: string };
  /** Option to enable or disable header edits */
  editableHeader?: boolean;
  /** Top-left cell custom content */
  controllerCell?: string | JSX.Element;
  /** For each column there is a default component to be used to render the related cell */
  cellComponentByColumnId?: { [columnId: string]: React.FunctionComponent<BeeTableCellProps<R>> };
  /** Table's columns */
  columns: ReactTable.Column<R>[];
  /** Table's cells */
  rows: R[];
  /** Function to be executed when cells are modified */
  onCellUpdates?: (cellUpdates: BeeTableCellUpdate<R>[]) => void;
  /** Function to be executed when columns are modified */
  onColumnUpdates?: (columnUpdates: BeeTableColumnUpdate<R>[]) => void;
  /** Function to be executed when rows are modified */
  onRowUpdates?: (args: BeeTableRowsUpdateArgs<R>) => void;
  /** Custom configuration for the table context menu */
  operationConfig?: BeeTableOperationConfig;
  /** The way in which the header will be rendered */
  headerVisibility?: BeeTableHeaderVisibility;
  /** Number of levels in the header, 0-based */
  headerLevelCount?: number;
  /** True, for skipping the creation in the DOM of the last defined header group */
  skipLastHeaderGroup?: boolean;
  /** Custom function for getting row key prop, and avoid using the row index */
  getRowKey?: (row: ReactTable.Row<R>) => string;
  /** Custom function for getting column key prop, and avoid using the column index */
  getColumnKey?: (column: ReactTable.ColumnInstance<R>) => string;
  /** Disable/Enable cell edits. Enabled by default */
  isReadOnly?: boolean;
  /** Enable keyboard navigation */
  enableKeyboardNavigation?: boolean;
  /** */
  onRowAdded?: (args: { beforeIndex: number }) => void;
  onRowDuplicated?: (args: { rowIndex: number }) => void;
  onRowDeleted?: (args: { rowIndex: number }) => void;
  onColumnAdded?: (args: { beforeIndex: number; groupType: string | undefined }) => void;
  onColumnDeleted?: (args: { columnIndex: number; groupType: string | undefined }) => void;
}

/** Possible status for the visibility of the Table's Header */
export enum BeeTableHeaderVisibility {
  Full,
  LastLevel,
  SecondToLastLevel,
  None,
}

/** Table allowed operations */
export enum BeeTableOperation {
  ColumnInsertLeft,
  ColumnInsertRight,
  ColumnDelete,
  RowInsertAbove,
  RowInsertBelow,
  RowDelete,
  RowClear,
  RowDuplicate,
}

export interface BeeTableOperationGroup {
  /** Name of the group (localized) */
  group: string;
  /** Collection of operations belonging to this group */
  items: {
    /** Name of the operation (localized) */
    name: string;
    /** Type of the operation */
    type: BeeTableOperation;
  }[];
}

export type BeeTableOperationConfig =
  | BeeTableOperationGroup[]
  | { [columnGroupType: string]: BeeTableOperationGroup[] };

/**
 * Interface to be inherited from the table cell components (td, th)
 */
export interface BeeTableThProps<R extends object> {
  /** the x position of the cell. Colspan are counted */
  xPosition?: number;
  columnIndex: number;
  column: ReactTable.ColumnInstance<R>;
}

export interface BeeTableTdProps<R extends object> {
  /** the x position of the cell. Colspan are counted */
  xPosition?: number;
  /** the y position of the cell */
  yPosition?: number;
  rowIndex: number;
  row: ReactTable.Row<R>;
  columnIndex: number;
  column: ReactTable.ColumnInstance<R>;
}
