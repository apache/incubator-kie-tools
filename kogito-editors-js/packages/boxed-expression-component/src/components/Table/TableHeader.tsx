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

import { Th, Thead, Tr } from "@patternfly/react-table";
import * as _ from "lodash";
import * as React from "react";
import { useCallback, useMemo } from "react";
import { Column, ColumnInstance, DataRecord, HeaderGroup, TableInstance } from "react-table";
import { DataType, TableHeaderVisibility } from "../../api";
import { EditExpressionMenu, EditTextInline } from "../EditExpressionMenu";
import { DEFAULT_MIN_WIDTH, Resizer } from "../Resizer";
import { getColumnsAtLastLevel, getColumnSearchPredicate } from "./Table";

export interface TableHeaderProps {
  /** Table instance */
  tableInstance: TableInstance;
  /** Rows instance */
  tableRows: React.MutableRefObject<DataRecord[]>;
  /** Function to be executed when one or more rows are modified */
  onRowsUpdate: (rows: DataRecord[]) => void;
  /** Optional label, that may depend on column, to be used for the popover that appears when clicking on column header */
  editColumnLabel?: string | { [groupType: string]: string };
  /** The way in which the header will be rendered */
  headerVisibility?: TableHeaderVisibility;
  /** True, for skipping the creation in the DOM of the last defined header group */
  skipLastHeaderGroup: boolean;
  /** Custom function for getting column key prop, and avoid using the column index */
  getColumnKey: (column: Column) => string;
  /** Columns instance */
  tableColumns: React.MutableRefObject<Column[]>;
  /** Function to be executed when columns are modified */
  onColumnsUpdate: (columns: Column[]) => void;
}

export const TableHeader: React.FunctionComponent<TableHeaderProps> = ({
  tableInstance,
  tableRows,
  onRowsUpdate,
  editColumnLabel,
  headerVisibility = TableHeaderVisibility.Full,
  skipLastHeaderGroup,
  getColumnKey,
  tableColumns,
  onColumnsUpdate,
}) => {
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

  const updateColumnNameInRows = useCallback(
    (prevColumnName: string, newColumnName: string) =>
      onRowsUpdate(
        _.map(tableRows.current, (tableCells) => {
          const assignedCellValue = tableCells[prevColumnName]!;
          delete tableCells[prevColumnName];
          tableCells[newColumnName] = assignedCellValue;
          return tableCells;
        })
      ),
    [onRowsUpdate, tableRows]
  );

  /**
   * Currently, column rename/type update is supported only for the first and the second level of the header
   */
  const onColumnNameOrDataTypeUpdate: (
    column: ColumnInstance,
    columnIndex: number
  ) => ({ name, dataType }: { name?: string; dataType?: DataType }) => void = useCallback(
    (column, columnIndex) => {
      return ({ name = "", dataType }) => {
        const prevColumnName = column.label as string;
        let columnToUpdate = tableColumns.current[columnIndex] as ColumnInstance;
        if (column.depth > 0) {
          const parentName = column.parent!.id;
          const parentColumns = (_.find(tableColumns.current, { accessor: parentName }) as ColumnInstance).columns;
          columnToUpdate = _.find(parentColumns, { accessor: prevColumnName })!;
        }
        columnToUpdate.label = name;
        columnToUpdate.accessor = name;
        columnToUpdate.dataType = dataType as DataType;
        onColumnsUpdate([...tableColumns.current]);
        if (name !== prevColumnName) {
          updateColumnNameInRows(prevColumnName, name);
        }
      };
    },
    [onColumnsUpdate, tableColumns, updateColumnNameInRows]
  );

  const computeColumnKey = useCallback(
    (column: ColumnInstance, columnIndex: number) =>
      `${getColumnKey(column)}-${column.parent?.id || ""}-${columnIndex}`,
    [getColumnKey]
  );

  const renderCountColumn = useCallback(
    (column: ColumnInstance, columnIndex: number) => (
      <Th
        {...column.getHeaderProps()}
        className="fixed-column no-clickable-cell"
        key={computeColumnKey(column, columnIndex)}
      >
        <div className="header-cell" data-ouia-component-type="expression-column-header">
          {column.label}
        </div>
      </Th>
    ),
    [computeColumnKey]
  );

  const renderCellInfoLabel = useCallback(
    (column: ColumnInstance, columnIndex: number) => {
      if (column.inlineEditable) {
        return (
          <EditTextInline
            value={column.label as string}
            onTextChange={(value) => onColumnNameOrDataTypeUpdate(column, columnIndex)({ name: value })}
          />
        );
      }
      return <p className="pf-u-text-truncate">{column.label}</p>;
    },
    [onColumnNameOrDataTypeUpdate]
  );

  const renderHeaderCellInfo = useCallback(
    (column, columnIndex) => (
      <div className="header-cell-info" data-ouia-component-type="expression-column-header-cell-info">
        {column.headerCellElement ? column.headerCellElement : renderCellInfoLabel(column, columnIndex)}
        {column.dataType ? <p className="pf-u-text-truncate data-type">({column.dataType})</p> : null}
      </div>
    ),
    [renderCellInfoLabel]
  );

  const onHorizontalResizeStop = useCallback(
    (column, columnWidth) => {
      const columnToBeFound = column.placeholderOf || column;
      let columnToUpdate = _.find(tableColumns.current, getColumnSearchPredicate(columnToBeFound)) as ColumnInstance;
      if (column.parent) {
        columnToUpdate = _.find(
          getColumnsAtLastLevel(tableColumns.current),
          getColumnSearchPredicate(column)
        ) as ColumnInstance;
      }
      columnToUpdate.width = columnWidth;
      onColumnsUpdate(tableColumns.current);
    },
    [onColumnsUpdate, tableColumns]
  );

  const renderResizableHeaderCell = useCallback(
    (column, columnIndex) => {
      const headerProps = {
        ...column.getHeaderProps(),
        style: {},
      };
      const thProps = tableInstance.getThProps(column, columnIndex);
      const width = column.width || DEFAULT_MIN_WIDTH;
      const isColspan = column.columns?.length > 0 || false;

      const getCssClass = () => {
        const cssClasses = [];
        if (!column.dataType) {
          cssClasses.push("no-clickable-cell");
        }
        if (isColspan) {
          cssClasses.push("colspan-header");
        }
        if (column.placeholderOf) {
          cssClasses.push(column.placeholderOf.cssClasses);
          cssClasses.push(column.placeholderOf.groupType);
        }
        cssClasses.push(column.groupType || "");
        cssClasses.push(column.cssClasses || "");
        return cssClasses.join(" ");
      };

      return (
        <Th {...headerProps} {...thProps} className={getCssClass()} key={computeColumnKey(column, columnIndex)}>
          <Resizer width={width} onHorizontalResizeStop={(columnWidth) => onHorizontalResizeStop(column, columnWidth)}>
            <div className="header-cell" data-ouia-component-type="expression-column-header">
              {column.dataType ? (
                <EditExpressionMenu
                  title={getColumnLabel(column.groupType)}
                  selectedExpressionName={column.label}
                  selectedDataType={column.dataType}
                  onExpressionUpdate={(expression) => onColumnNameOrDataTypeUpdate(column, columnIndex)(expression)}
                  key={computeColumnKey(column, columnIndex)}
                >
                  {renderHeaderCellInfo(column, columnIndex)}
                </EditExpressionMenu>
              ) : (
                renderHeaderCellInfo(column, columnIndex)
              )}
            </div>
          </Resizer>
        </Th>
      );
    },
    [
      computeColumnKey,
      getColumnLabel,
      onColumnNameOrDataTypeUpdate,
      onHorizontalResizeStop,
      renderHeaderCellInfo,
      tableInstance,
    ]
  );

  const renderColumn = useCallback(
    (column: ColumnInstance, columnIndex: number) =>
      column.isCountColumn ? renderCountColumn(column, columnIndex) : renderResizableHeaderCell(column, columnIndex),
    [renderCountColumn, renderResizableHeaderCell]
  );

  const getHeaderGroups = useCallback(
    (tableInstance) => {
      return skipLastHeaderGroup ? _.dropRight(tableInstance.headerGroups) : tableInstance.headerGroups;
    },
    [skipLastHeaderGroup]
  );

  const renderHeaderGroups = useMemo(
    () =>
      getHeaderGroups(tableInstance).map((headerGroup: HeaderGroup) => {
        const { key, ...props } = { ...headerGroup.getHeaderGroupProps(), style: {} };
        return (
          <Tr key={key} {...props}>
            {headerGroup.headers.map((column: ColumnInstance, columnIndex: number) =>
              renderColumn(column, columnIndex)
            )}
          </Tr>
        );
      }),
    [getHeaderGroups, renderColumn, tableInstance]
  );

  const renderAtLevelInHeaderGroups = useCallback(
    (level: number) => (
      <Tr>
        {_.nth(tableInstance.headerGroups as HeaderGroup[], level)!.headers.map(
          (column: ColumnInstance, columnIndex: number) => renderColumn(column, columnIndex)
        )}
      </Tr>
    ),
    [renderColumn, tableInstance.headerGroups]
  );

  switch (headerVisibility) {
    case TableHeaderVisibility.Full:
      return <Thead noWrap>{renderHeaderGroups}</Thead>;
    case TableHeaderVisibility.LastLevel:
      return <Thead noWrap>{renderAtLevelInHeaderGroups(-1)}</Thead>;
    case TableHeaderVisibility.SecondToLastLevel:
      return <Thead noWrap>{renderAtLevelInHeaderGroups(-2)}</Thead>;
    default:
      return null;
  }
};
