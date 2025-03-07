/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

import * as React from "react";
import * as ReactTable from "react-table";
import { ResizerStopBehavior } from "../resizing/ResizingWidthsContext";
import { BeeTableCellUpdate, BeeTableColumnUpdate } from "../table/BeeTable";
import { BeeTableSelection } from "../selection/BeeTableSelectionContext";

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
  isEditableHeader?: boolean;
  /** Top-left cell custom content */
  controllerCell?: string | JSX.Element;
  //
  rowWrapper?: React.FunctionComponent<React.PropsWithChildren<{ row: R; rowIndex: number }>>;
  /** For each column there is a default component to be used to render the related cell */
  cellComponentByColumnAccessor?: { [columnId: string]: React.FunctionComponent<BeeTableCellProps<R>> };
  /** Table's columns */
  columns: ReactTable.Column<R>[];
  /** Table's cells */
  rows: R[];
  /** Function to be executed when cells are modified */
  onCellUpdates?: (cellUpdates: BeeTableCellUpdate<R>[]) => void;
  /** Function to be executed when columns are modified */
  onColumnUpdates?: (columnUpdates: BeeTableColumnUpdate<R>[]) => void;
  /** Custom configuration for the table context menu */
  operationConfig?: BeeTableOperationConfig;
  /** Collection of allowed operations that are allowed for current table 'conditions' */
  allowedOperations: (conditions: BeeTableContextMenuAllowedOperationsConditions) => BeeTableOperation[];
  /** The way in which the header will be rendered */
  headerVisibility?: BeeTableHeaderVisibility;
  /** Number of levels in the header, 0-based */
  headerLevelCountForAppendingRowIndexColumn?: number;
  /** True, for skipping the creation in the DOM of the last defined header group */
  skipLastHeaderGroup?: boolean;
  /** Custom function for getting row key prop, and avoid using the row index */
  getRowKey?: (row: ReactTable.Row<R>) => string;
  /** Custom function for getting column key prop, and avoid using the column index */
  getColumnKey?: (column: ReactTable.ColumnInstance<R>) => string;
  /** Function to be executed when a column's header is clicked */
  onHeaderClick?: (columnKey: string) => void;
  /** Function to be executed when a key up event occurs in a column's header */
  onHeaderKeyUp?: (columnKey: string) => void;
  /** Function to be executed when a column's data cell is clicked */
  onDataCellClick?: (columnID: string) => void;
  /** Function to be executed when a column's data cell is clicked */
  onDataCellKeyUp?: (columnID: string) => void;
  /** Disable/Enable cell edits. Enabled by default */
  isReadOnly?: boolean;
  /** Enable keyboard navigation */
  enableKeyboardNavigation?: boolean;
  /** */
  onRowAdded?: (args: { beforeIndex: number; insertDirection: InsertRowColumnsDirection; rowsCount: number }) => void;
  onRowDuplicated?: (args: { rowIndex: number }) => void;
  onRowReset?: (args: { rowIndex: number }) => void;
  onRowDeleted?: (args: { rowIndex: number }) => void;
  onColumnAdded?: (args: {
    beforeIndex: number;
    currentIndex?: number;
    groupType: string | undefined;
    columnsCount: number;
    insertDirection: InsertRowColumnsDirection;
  }) => void;
  onColumnDeleted?: (args: { columnIndex: number; groupType: string | undefined }) => void;
  shouldRenderRowIndexColumn: boolean;
  shouldShowRowsInlineControls: boolean;
  shouldShowColumnsInlineControls: boolean;
  resizerStopBehavior: ResizerStopBehavior;
  lastColumnMinWidth?: number;
  /** Method should return true for table rows, that can display evaluation hits count, false otherwise. If not set, BeeTableBody defaults to false. */
  supportsEvaluationHitsCount?: (row: ReactTable.Row<R>) => boolean;
}

/** Possible status for the visibility of the Table's Header */
export enum BeeTableHeaderVisibility {
  AllLevels,
  LastLevel,
  SecondToLastLevel,
  None,
}

/** Table allowed operations */
export enum BeeTableOperation {
  ColumnInsertLeft,
  ColumnInsertRight,
  ColumnInsertN,
  ColumnDelete,
  RowInsertAbove,
  RowInsertBelow,
  RowInsertN,
  RowDelete,
  RowReset,
  RowDuplicate,
  SelectionCopy,
  SelectionCut,
  SelectionPaste,
  SelectionReset,
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

export type BeeTableContextMenuAllowedOperationsConditions = {
  selection: BeeTableSelection;
  column: ReactTable.ColumnInstance<any> | undefined;
  columns: ReactTable.ColumnInstance<any>[] | undefined;
};

export enum InsertRowColumnsDirection {
  AboveOrRight,
  BelowOrLeft,
}
