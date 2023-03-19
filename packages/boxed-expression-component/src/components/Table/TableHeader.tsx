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
import { Column, ColumnInstance, HeaderGroup, TableInstance } from "react-table";
import { DataType, TableHeaderVisibility } from "../../api";
import { getColumnsAtLastLevel, getColumnSearchPredicate } from "./Table";
import { useBoxedExpression } from "../../context";
import { focusCurrentCell, getParentCell } from "./common";
import { ThCell } from "./ThCell";
import { ResizableHeaderCell } from "./ResizableHeaderCell";
import { EditTextInline } from "../EditExpressionMenu";

export interface TableHeaderProps {
  /** Table instance */
  tableInstance: TableInstance;
  /** Optional label, that may depend on column, to be used for the popover that appears when clicking on column header */
  editColumnLabel?: string | { [groupType: string]: string };
  /** The way in which the header will be rendered */
  headerVisibility?: TableHeaderVisibility;
  /** True, for skipping the creation in the DOM of the last defined header group */
  skipLastHeaderGroup: boolean;
  /** Custom function for getting column key prop, and avoid using the column index */
  getColumnKey: (column: Column) => string;
  /** Columns instance */
  tableColumns: Column[];
  /** Function to be executed when columns are modified */
  onColumnsUpdate: (columns: Column[]) => void;
  /** Function to be executed when a key has been pressed on a cell */
  onCellKeyDown: () => (e: KeyboardEvent) => void;
  /** Th props */
  thProps: (column: ColumnInstance) => any;
  /** Option to enable or disable header edits */
  editableHeader: boolean;
}

export const TableHeader: React.FunctionComponent<TableHeaderProps> = ({
  tableInstance,
  editColumnLabel,
  headerVisibility = TableHeaderVisibility.Full,
  skipLastHeaderGroup,
  getColumnKey,
  tableColumns,
  onColumnsUpdate,
  onCellKeyDown,
  thProps,
  editableHeader,
}) => {
  const { boxedExpressionEditorGWTService } = useBoxedExpression();

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
  const onColumnNameOrDataTypeUpdate: (
    column: ColumnInstance,
    columnIndex: number
  ) => ({ name, dataType }: { name?: string; dataType?: DataType }) => void = useCallback(
    (column, columnIndex) => {
      return ({ name = "", dataType }) => {
        let columnToUpdate = tableColumns[columnIndex] as ColumnInstance;
        if (column.depth > 0) {
          const columnsBelongingToParent = (_.find(tableColumns, { accessor: column.parent!.id }) as ColumnInstance)
            .columns;
          columnToUpdate = _.find(columnsBelongingToParent, { accessor: column.id })!;
        }
        columnToUpdate.label = name;
        columnToUpdate.dataType = dataType as DataType;
        onColumnsUpdate([...tableColumns]);
      };
    },
    [onColumnsUpdate, tableColumns]
  );

  const renderCountColumn = useCallback(
    (column: ColumnInstance, rowIndex: number) => {
      const columnKey = getColumnKey(column);
      const classNames = `${columnKey} fixed-column no-clickable-cell counter-header-cell`;

      return (
        <ThCell
          rowIndex={rowIndex}
          cellIndex={0}
          rowSpan={1}
          headerProps={column.getHeaderProps()}
          className={classNames}
          key={columnKey}
          isFocusable={true}
          onKeyDown={onCellKeyDown}
          xPosition={0}
          yPosition={rowIndex}
        >
          <div className="header-cell" data-ouia-component-type="expression-column-header">
            {column.label}
          </div>
        </ThCell>
      );
    },
    [getColumnKey, onCellKeyDown]
  );

  const renderCellInfoLabel = useCallback(
    (column: ColumnInstance, columnIndex: number, onAnnotationCellToggle?: (isReadMode: boolean) => void) => {
      if (column.inlineEditable) {
        return (
          <EditTextInline
            value={column.label as string}
            onTextChange={(value) => {
              if (column.label != value) {
                boxedExpressionEditorGWTService?.notifyUserAction();
              }
              onColumnNameOrDataTypeUpdate(column, columnIndex)({ name: value });
            }}
            onKeyDown={(event) => {
              const parentCell = getParentCell(event.target as HTMLElement);
              //this timeout prevent the cell focus to call the input's blur and the onValueBlur
              setTimeout(() => {
                if (/(enter)/i.test(event.key)) {
                  focusCurrentCell(parentCell);
                }
              }, 0);
            }}
            onCancel={(event) => {
              const parentCell = getParentCell(event.target as HTMLElement);
              //this timeout prevent the cell focus to call the input's blur and the onValueBlur
              setTimeout(() => {
                focusCurrentCell(parentCell);
              }, 0);
            }}
            onToggle={onAnnotationCellToggle}
          />
        );
      }
      return <p className="pf-u-text-truncate label">{column.label}</p>;
    },
    [onColumnNameOrDataTypeUpdate]
  );

  const renderHeaderCellInfo = useCallback(
    (column, columnIndex, onAnnotationCellToggle?: (isReadMode: boolean) => void) => (
      <div className="header-cell-info" data-ouia-component-type="expression-column-header-cell-info">
        {column.headerCellElement
          ? column.headerCellElement
          : renderCellInfoLabel(column, columnIndex, onAnnotationCellToggle)}
        {column.dataType ? <p className="pf-u-text-truncate data-type">({column.dataType})</p> : null}
      </div>
    ),
    [renderCellInfoLabel]
  );

  const onHorizontalResizeStop = useCallback(
    (column, columnWidth) => {
      const columnToBeFound = column.placeholderOf || column;
      let columnToUpdate = _.find(tableColumns, getColumnSearchPredicate(columnToBeFound)) as ColumnInstance;
      if (column.parent) {
        columnToUpdate = _.find(
          getColumnsAtLastLevel(tableColumns),
          getColumnSearchPredicate(column)
        ) as ColumnInstance;
      }
      if (columnToUpdate) {
        columnToUpdate.width = columnWidth;
      }
      tableColumns.forEach((tableColumn) => {
        if (tableColumn.width === undefined) {
          tableColumn.width = (tableColumn as any).columns.reduce((acc: number, column: any) => acc + column.width, 0);
        }
      });
      onColumnsUpdate(tableColumns);
    },
    [onColumnsUpdate, tableColumns]
  );

  const onHeaderClick = useCallback(
    (columnKey: string) => () => {
      boxedExpressionEditorGWTService?.selectObject(columnKey);
    },
    [boxedExpressionEditorGWTService]
  );

  const renderColumn = useCallback(
    (column: ColumnInstance, rowIndex: number, columnIndex: number, xPosition?: number) =>
      column.isCountColumn ? (
        renderCountColumn(column, rowIndex)
      ) : (
        <ResizableHeaderCell
          key={`${rowIndex}_${columnIndex}`}
          editableHeader={editableHeader}
          getColumnKey={getColumnKey}
          getColumnLabel={getColumnLabel}
          onCellKeyDown={onCellKeyDown}
          onColumnNameOrDataTypeUpdate={onColumnNameOrDataTypeUpdate}
          onHeaderClick={onHeaderClick}
          onHorizontalResizeStop={onHorizontalResizeStop}
          renderHeaderCellInfo={renderHeaderCellInfo}
          thProps={thProps}
          tableInstance={tableInstance}
          column={column}
          rowIndex={rowIndex}
          columnIndex={columnIndex}
          xPosition={xPosition ?? columnIndex}
          yPosition={rowIndex}
        />
      ),
    [
      renderCountColumn,
      editableHeader,
      getColumnKey,
      getColumnLabel,
      onCellKeyDown,
      onColumnNameOrDataTypeUpdate,
      onHeaderClick,
      onHorizontalResizeStop,
      renderHeaderCellInfo,
      tableInstance,
      thProps,
    ]
  );

  const getHeaderGroups = useCallback(
    (tableInstance) => {
      return skipLastHeaderGroup ? _.dropRight(tableInstance.headerGroups) : tableInstance.headerGroups;
    },
    [skipLastHeaderGroup]
  );

  const renderHeaderGroups = useMemo(
    () =>
      getHeaderGroups(tableInstance).map((headerGroup: HeaderGroup, rowIndex: number) => {
        const { key, ...props } = { ...headerGroup.getHeaderGroupProps(), style: {} };
        let xPosition = 0;
        return (
          <Tr key={key} {...props}>
            {headerGroup.headers.map((column: ColumnInstance, columnIndex: number) => {
              const currentXPosition = xPosition;
              xPosition += column.columns?.length ?? 1;
              return renderColumn(column, rowIndex, columnIndex, currentXPosition);
            })}
          </Tr>
        );
      }),
    [getHeaderGroups, renderColumn, tableInstance]
  );

  const renderAtLevelInHeaderGroups = useCallback(
    (level: number) => (
      <Tr>
        {_.nth(tableInstance.headerGroups as HeaderGroup[], level)!.headers.map(
          (column: ColumnInstance, columnIndex: number) => renderColumn(column, 0, columnIndex)
        )}
      </Tr>
    ),
    [renderColumn, tableInstance.headerGroups]
  );

  const header = useMemo(() => {
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
  }, [headerVisibility, renderHeaderGroups, renderAtLevelInHeaderGroups]);

  return <>{header}</>;
};
