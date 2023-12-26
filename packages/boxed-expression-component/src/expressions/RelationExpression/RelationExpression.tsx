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

import "@patternfly/react-styles/css/utilities/Text/text.css";
import * as React from "react";
import { useCallback, useMemo, useRef } from "react";
import * as ReactTable from "react-table";
import {
  BeeTableContextMenuAllowedOperationsConditions,
  BeeTableHeaderVisibility,
  BeeTableOperation,
  BeeTableOperationConfig,
  DmnBuiltInDataType,
  generateUuid,
  getNextAvailablePrefixedName,
  InsertRowColumnsDirection,
  RelationExpressionDefinition,
  RelationExpressionDefinitionRow,
} from "../../api";
import { useBoxedExpressionEditorI18n } from "../../i18n";
import { usePublishedBeeTableResizableColumns } from "../../resizing/BeeTableResizableColumnsContext";
import { useApportionedColumnWidthsIfNestedTable, useNestedTableLastColumnMinWidth } from "../../resizing/Hooks";
import { ResizerStopBehavior } from "../../resizing/ResizingWidthsContext";
import {
  BEE_TABLE_ROW_INDEX_COLUMN_WIDTH,
  RELATION_EXPRESSION_COLUMN_DEFAULT_WIDTH,
  RELATION_EXPRESSION_COLUMN_MIN_WIDTH,
} from "../../resizing/WidthConstants";
import { BeeTable, BeeTableCellUpdate, BeeTableColumnUpdate, BeeTableRef } from "../../table/BeeTable";
import {
  useBoxedExpressionEditor,
  useBoxedExpressionEditorDispatch,
} from "../BoxedExpressionEditor/BoxedExpressionEditorContext";
import { DEFAULT_EXPRESSION_NAME } from "../ExpressionDefinitionHeaderMenu";
import "./RelationExpression.css";

type ROWTYPE = RelationExpressionDefinitionRow;

export const RELATION_EXPRESSION_DEFAULT_VALUE = "";

export function RelationExpression(
  relationExpression: RelationExpressionDefinition & { isNested: boolean; parentElementId: string }
) {
  const { i18n } = useBoxedExpressionEditorI18n();
  const { decisionNodeId } = useBoxedExpressionEditor();
  const { setExpression } = useBoxedExpressionEditorDispatch();
  const { variables } = useBoxedExpressionEditor();

  const beeTableOperationConfig = useMemo<BeeTableOperationConfig>(
    () => [
      {
        group: i18n.columns,
        items: [
          { name: i18n.columnOperations.insertLeft, type: BeeTableOperation.ColumnInsertLeft },
          { name: i18n.columnOperations.insertRight, type: BeeTableOperation.ColumnInsertRight },
          { name: i18n.insert, type: BeeTableOperation.ColumnInsertN },
          { name: i18n.columnOperations.delete, type: BeeTableOperation.ColumnDelete },
        ],
      },
      {
        group: i18n.rows,
        items: [
          { name: i18n.rowOperations.insertAbove, type: BeeTableOperation.RowInsertAbove },
          { name: i18n.rowOperations.insertBelow, type: BeeTableOperation.RowInsertBelow },
          { name: i18n.insert, type: BeeTableOperation.RowInsertN },
          { name: i18n.rowOperations.delete, type: BeeTableOperation.RowDelete },
          { name: i18n.rowOperations.duplicate, type: BeeTableOperation.RowDuplicate },
        ],
      },
      {
        group: i18n.terms.selection.toUpperCase(),
        items: [
          { name: i18n.terms.copy, type: BeeTableOperation.SelectionCopy },
          { name: i18n.terms.cut, type: BeeTableOperation.SelectionCut },
          { name: i18n.terms.paste, type: BeeTableOperation.SelectionPaste },
          { name: i18n.terms.reset, type: BeeTableOperation.SelectionReset },
        ],
      },
    ],
    [i18n]
  );
  const columns = useMemo(() => {
    return (relationExpression.columns ?? []).map((c) => ({ ...c, minWidth: RELATION_EXPRESSION_COLUMN_MIN_WIDTH }));
  }, [relationExpression.columns]);

  const rows = useMemo<RelationExpressionDefinitionRow[]>(() => {
    return relationExpression.rows ?? [];
  }, [relationExpression]);

  const setColumnWidth = useCallback(
    (columnIndex: number) => (newWidthAction: React.SetStateAction<number | undefined>) => {
      setExpression((prev: RelationExpressionDefinition) => {
        const newColumns = [...(prev.columns ?? [])];
        const newWidth =
          typeof newWidthAction === "function" ? newWidthAction(newColumns[columnIndex].width) : newWidthAction;
        newColumns[columnIndex].width = newWidth;
        return { ...prev, columns: newColumns };
      });
    },
    [setExpression]
  );

  /// //////////////////////////////////////////////////////
  /// ///////////// RESIZING WIDTHS ////////////////////////
  /// //////////////////////////////////////////////////////

  const beeTableRef = useRef<BeeTableRef>(null);
  const { onColumnResizingWidthChange, columnResizingWidths, isPivoting } = usePublishedBeeTableResizableColumns(
    relationExpression.id,
    columns.length,
    true
  );

  const lastColumnMinWidth = useNestedTableLastColumnMinWidth(columnResizingWidths);

  useApportionedColumnWidthsIfNestedTable(
    beeTableRef,
    isPivoting,
    relationExpression.isNested,
    BEE_TABLE_ROW_INDEX_COLUMN_WIDTH,
    columns,
    columnResizingWidths,
    rows
  );

  /// //////////////////////////////////////////////////////

  const beeTableColumns = useMemo<ReactTable.Column<ROWTYPE>[]>(() => {
    return [
      {
        accessor: decisionNodeId as any, // FIXME: https://github.com/kiegroup/kie-issues/issues/169
        label: relationExpression.name ?? DEFAULT_EXPRESSION_NAME,
        dataType: relationExpression.dataType,
        isRowIndexColumn: false,
        width: undefined,
        columns: columns.map((column, columnIndex) => ({
          accessor: column.id as any,
          label: column.name,
          dataType: column.dataType,
          isRowIndexColumn: false,
          minWidth: RELATION_EXPRESSION_COLUMN_MIN_WIDTH,
          setWidth: setColumnWidth(columnIndex),
          width: column.width ?? RELATION_EXPRESSION_COLUMN_MIN_WIDTH,
        })),
      },
    ];
  }, [columns, decisionNodeId, relationExpression.dataType, relationExpression.name, setColumnWidth]);

  const beeTableRows = useMemo<ROWTYPE[]>(
    () =>
      rows.map((row) => {
        const beeTableRow = columns.reduce(
          (tableRow, column, columnIndex) => {
            (tableRow as any)[column.id] = row.cells[columnIndex] || { id: generateUuid(), content: "" };
            return tableRow;
          },
          { id: row.id } as ROWTYPE
        );
        return beeTableRow;
      }),
    [rows, columns]
  );

  const onCellUpdates = useCallback(
    (cellUpdates: BeeTableCellUpdate<ROWTYPE>[]) => {
      setExpression((prev: RelationExpressionDefinition) => {
        const n = { ...prev };
        cellUpdates.forEach((u) => {
          const newRows = [...(n.rows ?? [])];

          const newCells = [...newRows[u.rowIndex].cells];
          newCells[u.columnIndex].content = u.value;

          newRows[u.rowIndex] = {
            ...newRows[u.rowIndex],
            cells: newCells,
          };

          n.rows = newRows;
        });

        return n;
      });
    },
    [setExpression]
  );

  const onColumnUpdates = useCallback(
    (columnUpdates: BeeTableColumnUpdate<ROWTYPE>[]) => {
      setExpression((prev: RelationExpressionDefinition) => {
        const n = {
          ...prev,
        };
        const newColumns = [...(prev.columns ?? [])];

        for (const u of columnUpdates) {
          if (u.column.depth === 0) {
            n.dataType = u.dataType;
            n.name = u.name;
          } else {
            newColumns[u.columnIndex] = {
              ...newColumns[u.columnIndex],
              name: u.name,
              dataType: u.dataType,
            };
          }
        }

        return {
          ...n,
          columns: newColumns,
        };
      });
    },
    [setExpression]
  );

  const createCell = useCallback(() => {
    const cell = { id: generateUuid(), content: RELATION_EXPRESSION_DEFAULT_VALUE };
    variables?.repository.addVariableToContext(cell.id, cell.id, relationExpression.parentElementId);
    return cell;
  }, [relationExpression.parentElementId, variables?.repository]);

  const onRowAdded = useCallback(
    (args: { beforeIndex: number; rowsCount: number; insertDirection: InsertRowColumnsDirection }) => {
      setExpression((prev: RelationExpressionDefinition) => {
        const newRows = [...(prev.rows ?? [])];
        const newItems = [];

        for (let i = 0; i < args.rowsCount; i++) {
          newItems.push({
            id: generateUuid(),
            cells: Array.from(new Array(prev.columns?.length ?? 0)).map(() => {
              return createCell();
            }),
          });
        }

        for (const newEntry of newItems) {
          let index = args.beforeIndex;
          newRows.splice(index, 0, newEntry);
          if (args.insertDirection === InsertRowColumnsDirection.AboveOrRight) {
            index++;
          }
        }

        return {
          ...prev,
          rows: newRows,
        };
      });
    },
    [createCell, setExpression]
  );

  const onColumnAdded = useCallback(
    (args: { beforeIndex: number; columnsCount: number; insertDirection: InsertRowColumnsDirection }) => {
      setExpression((prev: RelationExpressionDefinition) => {
        const newColumns = [...(prev.columns ?? [])];

        const newItems = [];
        const availableNames = prev.columns?.map((c) => c.name) ?? [];

        for (let i = 0; i < args.columnsCount; i++) {
          const name = getNextAvailablePrefixedName(availableNames, "column");
          availableNames.push(name);

          newItems.push({
            id: generateUuid(),
            name: name,
            dataType: DmnBuiltInDataType.Undefined,
            width: RELATION_EXPRESSION_COLUMN_DEFAULT_WIDTH,
          });
        }

        for (const newEntry of newItems) {
          let index = args.beforeIndex;
          newColumns.splice(index, 0, newEntry);
          if (args.insertDirection === InsertRowColumnsDirection.BelowOrLeft) {
            index++;
          }
        }

        const newRows = [...(prev.rows ?? [])].map((row) => {
          const newCells = [...row.cells];
          newCells.splice(args.beforeIndex, 0, createCell());
          return {
            ...row,
            cells: newCells,
          };
        });

        return {
          ...prev,
          columns: newColumns,
          rows: newRows,
        };
      });
    },
    [createCell, setExpression]
  );

  const onColumnDeleted = useCallback(
    (args: { columnIndex: number }) => {
      setExpression((prev: RelationExpressionDefinition) => {
        const newColumns = [...(prev.columns ?? [])];
        newColumns.splice(args.columnIndex, 1);

        const newRows = [...(prev.rows ?? [])].map((row) => {
          const newCells = [...row.cells];
          newCells.splice(args.columnIndex, 1);
          return {
            ...row,
            cells: newCells,
          };
        });

        return {
          ...prev,
          columns: newColumns,
          rows: newRows,
        };
      });
    },
    [setExpression]
  );

  const onRowDeleted = useCallback(
    (args: { rowIndex: number }) => {
      setExpression((prev: RelationExpressionDefinition) => {
        const newRows = [...(prev.rows ?? [])];
        newRows.splice(args.rowIndex, 1);
        return {
          ...prev,
          rows: newRows,
        };
      });
    },
    [setExpression]
  );

  const onRowDuplicated = useCallback(
    (args: { rowIndex: number }) => {
      setExpression((prev: RelationExpressionDefinition) => {
        const duplicatedRow = {
          id: generateUuid(),
          cells: prev.rows![args.rowIndex].cells.map((cell) => ({
            ...cell,
            id: generateUuid(),
          })),
        };

        const newRows = [...(prev.rows ?? [])];
        newRows.splice(args.rowIndex, 0, duplicatedRow);
        return {
          ...prev,
          rows: newRows,
        };
      });
    },
    [setExpression]
  );

  const beeTableHeaderVisibility = useMemo(() => {
    return relationExpression.isNested ? BeeTableHeaderVisibility.LastLevel : BeeTableHeaderVisibility.AllLevels;
  }, [relationExpression.isNested]);

  const allowedOperations = useCallback(
    (conditions: BeeTableContextMenuAllowedOperationsConditions) => {
      if (!conditions.selection.selectionStart || !conditions.selection.selectionEnd) {
        return [];
      }

      const columnIndex = conditions.selection.selectionStart.columnIndex;

      const columnCanBeDeleted = (conditions.columns?.length ?? 0) > 2; // That's a regular column and the rowIndex column

      const columnOperations =
        columnIndex === 0 // This is the rowIndex column
          ? []
          : [
              BeeTableOperation.ColumnInsertLeft,
              BeeTableOperation.ColumnInsertRight,
              BeeTableOperation.ColumnInsertN,
              ...(columnCanBeDeleted ? [BeeTableOperation.ColumnDelete] : []),
            ];

      return [
        ...columnOperations,
        BeeTableOperation.SelectionCopy,
        ...(columnIndex > 0 && conditions.selection.selectionStart.rowIndex >= 0
          ? [BeeTableOperation.SelectionCut, BeeTableOperation.SelectionPaste, BeeTableOperation.SelectionReset]
          : []),
        ...(conditions.selection.selectionStart.rowIndex >= 0
          ? [
              BeeTableOperation.RowInsertAbove,
              BeeTableOperation.RowInsertBelow,
              BeeTableOperation.RowInsertN,
              ...(beeTableRows.length > 1 ? [BeeTableOperation.RowDelete] : []),
              BeeTableOperation.RowReset,
              BeeTableOperation.RowDuplicate,
            ]
          : []),
      ];
    },
    [beeTableRows.length]
  );

  return (
    <div className={`relation-expression`}>
      <BeeTable
        resizerStopBehavior={
          isPivoting ? ResizerStopBehavior.SET_WIDTH_ALWAYS : ResizerStopBehavior.SET_WIDTH_WHEN_SMALLER
        }
        forwardRef={beeTableRef}
        headerLevelCountForAppendingRowIndexColumn={1}
        editColumnLabel={i18n.editRelation}
        columns={beeTableColumns}
        headerVisibility={beeTableHeaderVisibility}
        rows={beeTableRows}
        onCellUpdates={onCellUpdates}
        onColumnUpdates={onColumnUpdates}
        operationConfig={beeTableOperationConfig}
        allowedOperations={allowedOperations}
        onRowAdded={onRowAdded}
        onRowDuplicated={onRowDuplicated}
        onRowDeleted={onRowDeleted}
        onColumnAdded={onColumnAdded}
        onColumnDeleted={onColumnDeleted}
        onColumnResizingWidthChange={onColumnResizingWidthChange}
        shouldRenderRowIndexColumn={true}
        shouldShowRowsInlineControls={true}
        shouldShowColumnsInlineControls={true}
        variables={variables}
        // lastColumnMinWidth={lastColumnMinWidth} // FIXME: Check if this is a good strategy or not when doing https://github.com/kiegroup/kie-issues/issues/181
      />
    </div>
  );
}
