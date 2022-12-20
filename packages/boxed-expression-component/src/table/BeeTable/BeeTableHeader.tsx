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
import { focusCurrentCell, getParentCell } from "./common";
import { BeeTableTh } from "./BeeTableTh";
import { BeeTableThResizable } from "./BeeTableThResizable";
import { InlineEditableTextInput } from "../../expressions/ExpressionDefinitionHeaderMenu";
import { NavigationKeysUtils } from "../../keysUtils";

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
  onColumnsUpdate: (columns: ReactTable.Column<R>[]) => void;
  /** Function to be executed when a key has been pressed on a cell */
  onCellKeyDown: () => (e: KeyboardEvent) => void;
  /** Th props */
  getMouseDownThProps: (columnIndex: number) => Pick<PfReactTable.ThProps, "onMouseDown">;
  /** Option to enable or disable header edits */
  editableHeader: boolean;
  /** */
  onColumnAdded?: (args: { beforeIndex: number }) => void;
}

export function BeeTableHeader<R extends object>({
  reactTableInstance,
  editColumnLabel,
  headerVisibility = BeeTableHeaderVisibility.Full,
  skipLastHeaderGroup,
  getColumnKey,
  tableColumns,
  onColumnsUpdate,
  onCellKeyDown,
  getMouseDownThProps,
  editableHeader,
  onColumnAdded,
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
        let columnToUpdate: ReactTable.Column<R> | undefined = tableColumns[columnIndex];
        if (column.depth > 0) {
          const columnsBelongingToParent = tableColumns.find((c) => c.accessor === column.parent?.id)?.columns;
          columnToUpdate = (columnsBelongingToParent ?? []).find((c) => c.accessor === column.id);
        }

        if (columnToUpdate) {
          columnToUpdate.label = name;
          columnToUpdate.dataType = dataType;
        }
        onColumnsUpdate([...tableColumns]);
      };
    },
    [onColumnsUpdate, tableColumns]
  );

  const renderRowIndexColumn = useCallback<(column: ReactTable.ColumnInstance<R>, rowIndex: number) => JSX.Element>(
    (column, rowIndex) => {
      const columnKey = getColumnKey(column);
      const classNames = `${columnKey} fixed-column no-clickable-cell counter-header-cell`;

      return (
        <BeeTableTh
          rowIndex={rowIndex}
          columnIndex={0}
          rowSpan={1}
          thProps={column.getHeaderProps()}
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
        </BeeTableTh>
      );
    },
    [getColumnKey, onCellKeyDown]
  );

  const renderCellInfoLabel = useCallback<
    (
      column: ReactTable.ColumnInstance<R>,
      columnIndex: number,
      onAnnotationCellToggle?: (isReadMode: boolean) => void
    ) => JSX.Element
  >(
    (column, columnIndex, onAnnotationCellToggle) => {
      if (column.inlineEditable) {
        return (
          <InlineEditableTextInput
            value={column.label}
            onTextChange={(value) => {
              if (column.label != value) {
                beeGwtService?.notifyUserAction();
              }
              onColumnNameOrDataTypeUpdate(column, columnIndex)({ name: value });
            }}
            onKeyDown={(event) => {
              const parentCell = getParentCell(event.target as HTMLElement);
              //this timeout prevent the cell focus to call the input's blur and the onValueBlur
              setTimeout(() => {
                if (NavigationKeysUtils.isEnter(event.key)) {
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
    [beeGwtService, onColumnNameOrDataTypeUpdate]
  );

  const renderHeaderCellInfo = useCallback(
    (
      column: ReactTable.ColumnInstance<R>,
      columnIndex: number,
      onAnnotationCellToggle?: (isReadMode: boolean) => void
    ) => (
      <div className="header-cell-info" data-ouia-component-type="expression-column-header-cell-info">
        {column.headerCellElement
          ? column.headerCellElement
          : renderCellInfoLabel(column, columnIndex, onAnnotationCellToggle)}
        {column.dataType ? <p className="pf-u-text-truncate data-type">({column.dataType})</p> : null}
      </div>
    ),
    [renderCellInfoLabel]
  );

  const onHeaderClick = useCallback(
    (columnKey: string) => () => {
      beeGwtService?.selectObject(columnKey);
    },
    [beeGwtService]
  );

  const renderColumn = useCallback<
    (column: ReactTable.ColumnInstance<R>, rowIndex: number, columnIndex: number, xPosition?: number) => JSX.Element
  >(
    (column, rowIndex, columnIndex, xPosition) =>
      column.isRowIndexColumn ? (
        renderRowIndexColumn(column, rowIndex)
      ) : (
        <BeeTableThResizable
          key={getColumnKey(column)}
          editableHeader={editableHeader}
          getColumnKey={getColumnKey}
          getColumnLabel={getColumnLabel}
          onCellKeyDown={onCellKeyDown}
          onExpressionHeaderUpdated={(expression) => onColumnNameOrDataTypeUpdate(column, columnIndex)(expression)}
          onHeaderClick={onHeaderClick}
          renderHeaderCellInfo={renderHeaderCellInfo}
          getMouseDownThProps={getMouseDownThProps}
          reactTableInstance={reactTableInstance}
          column={column}
          rowIndex={rowIndex}
          columnIndex={columnIndex}
          xPosition={xPosition ?? columnIndex}
          yPosition={rowIndex}
          onColumnAdded={onColumnAdded}
        />
      ),
    [
      renderRowIndexColumn,
      getColumnKey,
      editableHeader,
      getColumnLabel,
      onCellKeyDown,
      onHeaderClick,
      renderHeaderCellInfo,
      getMouseDownThProps,
      reactTableInstance,
      onColumnAdded,
      onColumnNameOrDataTypeUpdate,
    ]
  );

  const renderHeaderGroups = useMemo(
    () =>
      (skipLastHeaderGroup ? _.dropRight(reactTableInstance.headerGroups) : reactTableInstance.headerGroups).map(
        (headerGroup, rowIndex) => {
          const { key, ...props } = { ...headerGroup.getHeaderGroupProps(), style: {} };
          let xPosition = 0;
          return (
            <PfReactTable.Tr key={key} {...props}>
              {headerGroup.headers.map((column, columnIndex) => {
                const currentXPosition = xPosition;
                xPosition += column.columns?.length ?? 1;
                return renderColumn(column, rowIndex, columnIndex, currentXPosition);
              })}
            </PfReactTable.Tr>
          );
        }
      ),
    [skipLastHeaderGroup, reactTableInstance.headerGroups, renderColumn]
  );

  const renderAtLevelInHeaderGroups = useCallback(
    (level: number) => (
      <PfReactTable.Tr style={{ display: "flex" }}>
        {_.nth(reactTableInstance.headerGroups, level)!.headers.map((column, columnIndex) =>
          renderColumn(column, 0, columnIndex)
        )}
      </PfReactTable.Tr>
    ),
    [renderColumn, reactTableInstance.headerGroups]
  );

  const header = useMemo(() => {
    switch (headerVisibility) {
      case BeeTableHeaderVisibility.Full:
        return <PfReactTable.Thead noWrap>{renderHeaderGroups}</PfReactTable.Thead>;
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
