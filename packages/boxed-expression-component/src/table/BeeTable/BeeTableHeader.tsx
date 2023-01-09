/*
 * Copyright 2021 Red Hat, Inc. and/or its affiliates.
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

import * as PfReactTable from "@patternfly/react-table";
import * as _ from "lodash";
import * as React from "react";
import { useCallback, useMemo } from "react";
import * as ReactTable from "react-table";
import { DmnBuiltInDataType, BeeTableHeaderVisibility, ExpressionDefinition } from "../../api";
import { useBoxedExpressionEditor } from "../../expressions/BoxedExpressionEditor/BoxedExpressionEditorContext";
import { BeeTableTh } from "./BeeTableTh";
import { BeeTableThResizable } from "./BeeTableThResizable";
import { InlineEditableTextInput } from "../../expressions/ExpressionDefinitionHeaderMenu";

export interface BeeTableColumnUpdate<R extends object> {
  dataType: DmnBuiltInDataType;
  name: string;
  column: ReactTable.ColumnInstance<R>;
  columnIndex: number;
}

export interface BeeTableCellUpdate<R extends object> {
  value: string;
  column: ReactTable.ColumnInstance<R>;
  columnIndex: number;
  row: R;
  rowIndex: number;
}

export interface BeeTableHeaderProps<R extends object> {
  /** Table instance */
  reactTableInstance: ReactTable.TableInstance<R>;
  /** Optional label, that may depend on column, to be used for the popover that appears when clicking on column header */
  editColumnLabel?: string | { [groupType: string]: string };
  /** The way in which the header will be rendered */
  headerVisibility?: BeeTableHeaderVisibility;
  /** True, for skipping the creation in the DOM of the last defined header group */
  skipLastHeaderGroup: boolean;
  /** Custom function for getting column key prop, and avoid using the column index */
  getColumnKey: (column: ReactTable.ColumnInstance<R>) => string;
  /** Columns instance */
  tableColumns: ReactTable.Column<R>[];
  /** Function to be executed when columns are modified */
  onColumnUpdates?: (columnUpdates: BeeTableColumnUpdate<R>[]) => void;
  /** Option to enable or disable header edits */
  isEditableHeader: boolean;
  /** */
  onColumnAdded?: (args: { beforeIndex: number; groupType: string | undefined }) => void;

  shouldRenderRowIndexColumn: boolean;

  showInlineControls: boolean;
}

export function BeeTableHeader<R extends object>({
  reactTableInstance,
  editColumnLabel,
  headerVisibility = BeeTableHeaderVisibility.AllLevels,
  skipLastHeaderGroup,
  getColumnKey,
  onColumnUpdates,
  isEditableHeader,
  onColumnAdded,
  shouldRenderRowIndexColumn,
  showInlineControls,
}: BeeTableHeaderProps<R>) {
  const { beeGwtService } = useBoxedExpressionEditor();

  const getColumnLabel: (groupType: string) => string | undefined = useCallback(
    (groupType) => {
      if (_.isObject(editColumnLabel) && _.has(editColumnLabel, groupType)) {
        return editColumnLabel[groupType];
      }
      if (typeof editColumnLabel === "string") {
        return editColumnLabel;
      }
    },
    [editColumnLabel]
  );

  /**
   * Currently, column rename/type update is supported only for the first and the second level of the header
   */
  const onColumnNameOrDataTypeUpdate = useCallback<
    (
      column: ReactTable.ColumnInstance<R>,
      columnIndex: number
    ) => (args: Pick<ExpressionDefinition, "name" | "dataType">) => void
  >(
    (column, columnIndex) => {
      return ({ name = "", dataType = DmnBuiltInDataType.Undefined }) => {
        onColumnUpdates?.([
          {
            // Subtract one because of the rowIndex column.
            columnIndex: columnIndex - 1,
            dataType,
            name,
            column,
          },
        ]);
      };
    },
    [onColumnUpdates]
  );

  const renderRowIndexColumn = useCallback<(column: ReactTable.ColumnInstance<R>, rowIndex: number) => JSX.Element>(
    (column, rowIndex) => {
      const columnKey = getColumnKey(column);
      const classNames = `${columnKey} fixed-column no-clickable-cell counter-header-cell`;

      return (
        <BeeTableTh
          rowSpan={1}
          key={columnKey}
          column={column}
          columnIndex={0}
          rowIndex={rowIndex}
          thProps={column.getHeaderProps()}
          className={classNames}
          groupType={column.groupType}
          isLastLevelColumn={(column.columns?.length ?? 0) <= 0}
          showInlineControls={showInlineControls}
        >
          <div className="header-cell" data-ouia-component-type="expression-column-header">
            {column.label}
          </div>
        </BeeTableTh>
      );
    },
    [getColumnKey, showInlineControls]
  );

  const onHeaderClick = useCallback(
    (columnKey: string) => () => {
      beeGwtService?.selectObject(columnKey);
    },
    [beeGwtService]
  );

  const renderColumn = useCallback<
    (
      rowIndex: number,
      column: ReactTable.ColumnInstance<R>,
      columnIndex: number,
      done: Set<ReactTable.ColumnInstance<R>>
    ) => JSX.Element
  >(
    (rowIndex, _column, columnIndex, done) => {
      const column = _column;
      const rowSpan = 1;
      const ret = column.isRowIndexColumn ? (
        <React.Fragment key={"row-index-column"}>
          {shouldRenderRowIndexColumn && renderRowIndexColumn(column, rowIndex)}
        </React.Fragment>
      ) : (
        <React.Fragment key={getColumnKey(column)}>
          {!done.has(column) && (
            <BeeTableThResizable
              rowSpan={rowSpan}
              isEditableHeader={isEditableHeader}
              showInlineControls={showInlineControls}
              getColumnKey={getColumnKey}
              getColumnLabel={getColumnLabel}
              onHeaderClick={onHeaderClick}
              reactTableInstance={reactTableInstance}
              column={column}
              columnIndex={columnIndex}
              rowIndex={rowIndex}
              onColumnAdded={onColumnAdded}
              onExpressionHeaderUpdated={({ name, dataType }) =>
                onColumnNameOrDataTypeUpdate(column, columnIndex)({ name, dataType })
              }
              headerCellInfo={
                <div
                  className="expression-info header-cell-info"
                  data-ouia-component-type="expression-column-header-cell-info"
                >
                  {column.headerCellElement ? (
                    column.headerCellElement
                  ) : column.isInlineEditable ? (
                    <InlineEditableTextInput
                      value={column.label}
                      onChange={(value) => {
                        onColumnNameOrDataTypeUpdate(column, columnIndex)({ name: value, dataType: column.dataType });
                      }}
                    />
                  ) : (
                    <p className="expression-info-name pf-u-text-truncate label">{column.label}</p>
                  )}
                  {column.dataType ? (
                    <p className="expression-info-data-type pf-u-text-truncate data-type">({column.dataType})</p>
                  ) : null}
                </div>
              }
            />
          )}
        </React.Fragment>
      );

      done.add(column);
      return ret;
    },
    [
      shouldRenderRowIndexColumn,
      renderRowIndexColumn,
      getColumnKey,
      isEditableHeader,
      showInlineControls,
      getColumnLabel,
      onHeaderClick,
      reactTableInstance,
      onColumnAdded,
      onColumnNameOrDataTypeUpdate,
    ]
  );

  const renderHeaderGroups = useCallback(() => {
    const headerGroupsToRender = skipLastHeaderGroup
      ? _.dropRight(reactTableInstance.headerGroups)
      : reactTableInstance.headerGroups;

    const done = new Set<ReactTable.ColumnInstance<R>>();
    return headerGroupsToRender.map((headerGroup, headerGroupLevel) => {
      // rowIndex === -1 --> Last headerGroup
      // rowIndex === -2 --> Second to last headerGroup
      // ... and so on
      const rowIndex = -(headerGroupsToRender.length - 1 - headerGroupLevel + 1);

      const { key, ...props } = { ...headerGroup.getHeaderGroupProps(), style: {} };
      return (
        <PfReactTable.Tr key={key} {...props}>
          {headerGroup.headers.map((column, columnIndex) => renderColumn(rowIndex, column, columnIndex, done))}
        </PfReactTable.Tr>
      );
    });
  }, [skipLastHeaderGroup, reactTableInstance.headerGroups, renderColumn]);

  const renderAtLevelInHeaderGroups = useCallback(
    (headerGroupLevel: number) => {
      const done = new Set<ReactTable.ColumnInstance<R>>();
      return (
        <PfReactTable.Tr>
          {_.nth(reactTableInstance.headerGroups, headerGroupLevel)?.headers.map((column, columnIndex) =>
            renderColumn(headerGroupLevel, column, columnIndex, done)
          )}
        </PfReactTable.Tr>
      );
    },
    [renderColumn, reactTableInstance.headerGroups]
  );

  const header = useMemo(() => {
    switch (headerVisibility) {
      case BeeTableHeaderVisibility.AllLevels:
        return <PfReactTable.Thead noWrap>{renderHeaderGroups()}</PfReactTable.Thead>;
      case BeeTableHeaderVisibility.LastLevel:
        return <PfReactTable.Thead noWrap>{renderAtLevelInHeaderGroups(-1)}</PfReactTable.Thead>;
      case BeeTableHeaderVisibility.SecondToLastLevel:
        return <PfReactTable.Thead noWrap>{renderAtLevelInHeaderGroups(-2)}</PfReactTable.Thead>;
      default:
        return null;
    }
  }, [headerVisibility, renderHeaderGroups, renderAtLevelInHeaderGroups]);

  return <>{header}</>;
}
