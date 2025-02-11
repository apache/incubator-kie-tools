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
  Action,
  BeeTableContextMenuAllowedOperationsConditions,
  BeeTableHeaderVisibility,
  BeeTableOperation,
  BeeTableOperationConfig,
  BoxedRelation,
  DmnBuiltInDataType,
  ExpressionChangedArgs,
  generateUuid,
  getNextAvailablePrefixedName,
  Normalized,
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
import { useBoxedExpressionEditor, useBoxedExpressionEditorDispatch } from "../../BoxedExpressionEditorContext";
import { DEFAULT_EXPRESSION_VARIABLE_NAME } from "../../expressionVariable/ExpressionVariableMenu";
import { DMN15__tList } from "@kie-tools/dmn-marshaller/dist/schemas/dmn-1_5/ts-gen/types";
import "./RelationExpression.css";

type ROWTYPE = any; // FIXME: https://github.com/kiegroup/kie-issues/issues/169

export const RELATION_EXPRESSION_DEFAULT_VALUE = "";

export function RelationExpression({
  isNested,
  expression: relationExpression,
}: {
  expression: BoxedRelation;
  isNested: boolean;
  parentElementId: string;
}) {
  const { i18n } = useBoxedExpressionEditorI18n();
  const { widthsById, expressionHolderId, isReadOnly } = useBoxedExpressionEditor();
  const { setExpression, setWidthsById } = useBoxedExpressionEditorDispatch();

  const id = relationExpression["@_id"]!;

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

  const widths = useMemo(() => widthsById.get(id) ?? [], [id, widthsById]);

  const getColumnWidth = useCallback((columnIndex: number, widths: number[]) => {
    return widths[columnIndex] ?? RELATION_EXPRESSION_COLUMN_DEFAULT_WIDTH;
  }, []);

  const columns = useMemo(() => {
    return (relationExpression.column ?? []).map((c, index) => ({
      ...c,
      minWidth: RELATION_EXPRESSION_COLUMN_MIN_WIDTH,
      width: getColumnWidth(index + 1, widths),
    }));
  }, [getColumnWidth, relationExpression.column, widths]);

  const rows = useMemo<DMN15__tList[]>(() => {
    return relationExpression.row ?? [];
  }, [relationExpression]);

  const setColumnWidth = useCallback(
    (columnIndex: number) => (newWidthAction: React.SetStateAction<number | undefined>) => {
      setWidthsById(({ newMap }) => {
        const prev = newMap.get(id) ?? [];
        const prevColumnWidth = getColumnWidth(columnIndex, prev);
        const newWidth = typeof newWidthAction === "function" ? newWidthAction(prevColumnWidth) : newWidthAction;

        if (newWidth && prevColumnWidth) {
          const minSize = columnIndex + 1;
          const newValues = [...prev];
          newValues.push(
            ...Array<number>(Math.max(0, minSize - newValues.length)).fill(RELATION_EXPRESSION_COLUMN_MIN_WIDTH)
          );
          newValues.splice(columnIndex, 1, newWidth);
          newMap.set(id, newValues);
        }
      });
    },
    [getColumnWidth, setWidthsById, id]
  );
  /// //////////////////////////////////////////////////////
  /// ///////////// RESIZING WIDTHS ////////////////////////
  /// //////////////////////////////////////////////////////

  const beeTableRef = useRef<BeeTableRef>(null);
  const { onColumnResizingWidthChange, columnResizingWidths, isPivoting } = usePublishedBeeTableResizableColumns(
    relationExpression["@_id"]!,
    columns.length,
    true
  );

  useNestedTableLastColumnMinWidth(columnResizingWidths);

  useApportionedColumnWidthsIfNestedTable(
    beeTableRef,
    isPivoting,
    isNested,
    BEE_TABLE_ROW_INDEX_COLUMN_WIDTH,
    columns,
    columnResizingWidths,
    rows
  );

  /// //////////////////////////////////////////////////////

  const beeTableColumns = useMemo<ReactTable.Column<ROWTYPE>[]>(() => {
    return [
      {
        accessor: expressionHolderId as any, // FIXME: https://github.com/apache/incubator-kie-issues/issues/169
        label: relationExpression["@_label"] ?? DEFAULT_EXPRESSION_VARIABLE_NAME,
        dataType: relationExpression["@_typeRef"] ?? DmnBuiltInDataType.Undefined,
        isRowIndexColumn: false,
        width: undefined,
        columns: columns.map((column, columnIndex) => ({
          accessor: column["@_id"] as any,
          label: column["@_name"],
          dataType: column["@_typeRef"] ?? DmnBuiltInDataType.Undefined,
          isRowIndexColumn: false,
          minWidth: RELATION_EXPRESSION_COLUMN_MIN_WIDTH,
          setWidth: setColumnWidth(columnIndex + 1),
          width: getColumnWidth(columnIndex + 1, widths),
        })),
      },
    ];
  }, [columns, expressionHolderId, getColumnWidth, relationExpression, setColumnWidth, widths]);

  const beeTableRows = useMemo<ROWTYPE[]>(
    () =>
      rows.map((row) => {
        return columns.reduce(
          (tableRow: ROWTYPE, column, columnIndex) => {
            const cellExpression = row.expression?.[columnIndex];
            if (cellExpression?.__$$element === "literalExpression") {
              tableRow[column["@_id"]!] = {
                id: cellExpression["@_id"] ?? generateUuid(),
                content: cellExpression.text?.__$$text ?? "",
              };
            }
            return tableRow;
          },
          { id: row["@_id"] }
        );
      }),
    [rows, columns]
  );

  const onCellUpdates = useCallback(
    (cellUpdates: BeeTableCellUpdate<ROWTYPE>[]) => {
      setExpression({
        setExpressionAction: (prev: Normalized<BoxedRelation>) => {
          let previousExpression: Normalized<BoxedRelation> = { ...prev };

          cellUpdates.forEach((cellUpdate) => {
            const newRows = [...(previousExpression.row ?? [])];
            const newExpressions = [...(newRows[cellUpdate.rowIndex].expression ?? [])];
            newExpressions[cellUpdate.columnIndex] = {
              ...newExpressions[cellUpdate.columnIndex],
              __$$element: "literalExpression",
              text: {
                __$$text: cellUpdate.value,
              },
            };
            newRows[cellUpdate.rowIndex] = {
              ...newRows[cellUpdate.rowIndex],
              expression: newExpressions,
            };

            previousExpression = {
              ...previousExpression,
              row: newRows,
            };
          });

          return previousExpression;
        },
        expressionChangedArgs: { action: Action.RelationCellsUpdated },
      });
    },
    [setExpression]
  );

  const getExpressionChangedArgsFromColumnUpdates = useCallback(
    (columnUpdates: BeeTableColumnUpdate<ROWTYPE>[]) => {
      // column.depth === 0 changes the Decision name and/or type which is a variable
      const updateNodeNameOrType = columnUpdates.filter(
        (update) =>
          update.column.depth === 0 &&
          (relationExpression["@_label"] !== update.name || relationExpression["@_typeRef"] !== update.typeRef)
      );

      if (updateNodeNameOrType.length > 1) {
        throw new Error("Unexpected multiple name and/or type changed simultaneously in a Relation Expression.");
      }

      if (updateNodeNameOrType.length === 1) {
        const expressionChangedArgs: ExpressionChangedArgs = {
          action: Action.VariableChanged,
          variableUuid: expressionHolderId,
          typeChange:
            relationExpression["@_typeRef"] !== updateNodeNameOrType[0].typeRef
              ? {
                  from: relationExpression["@_typeRef"],
                  to: updateNodeNameOrType[0].typeRef,
                }
              : undefined,
          nameChange:
            relationExpression["@_label"] !== updateNodeNameOrType[0].name
              ? {
                  from: relationExpression["@_label"],
                  to: updateNodeNameOrType[0].name,
                }
              : undefined,
        };

        return expressionChangedArgs;
      } else {
        //  Changes in other columns does not reflect in changes in variables
        // Do not inline this variable for type safety. See https://github.com/microsoft/TypeScript/issues/241
        const expressionChangedArgs: ExpressionChangedArgs = { action: Action.ColumnChanged };
        return expressionChangedArgs;
      }
    },
    [expressionHolderId, relationExpression]
  );

  const onColumnUpdates = useCallback(
    (columnUpdates: BeeTableColumnUpdate<ROWTYPE>[]) => {
      const expressionChangedArgs = getExpressionChangedArgsFromColumnUpdates(columnUpdates);
      setExpression({
        setExpressionAction: (prev: Normalized<BoxedRelation>) => {
          const n = { ...prev };
          const newColumns = [...(prev.column ?? [])];

          for (const u of columnUpdates) {
            if (u.column.depth === 0) {
              n["@_typeRef"] = u.typeRef;
              n["@_label"] = u.name;
            } else {
              newColumns[u.columnIndex] = {
                ...newColumns[u.columnIndex],
                "@_name": u.name,
                "@_typeRef": u.typeRef,
              };
            }
          }

          // Do not inline this variable for type safety. See https://github.com/microsoft/TypeScript/issues/241
          const ret: Normalized<BoxedRelation> = {
            ...n,
            column: newColumns,
          };

          return ret;
        },
        expressionChangedArgs,
      });
    },
    [getExpressionChangedArgsFromColumnUpdates, setExpression]
  );

  const createCell = useCallback(() => {
    const cell: {
      __$$element: "literalExpression";
      "@_id": string;
      text: { __$$text: string };
    } = {
      __$$element: "literalExpression",
      "@_id": generateUuid(),
      text: {
        __$$text: RELATION_EXPRESSION_DEFAULT_VALUE,
      },
    };
    return cell;
  }, []);

  const onRowAdded = useCallback(
    (args: { beforeIndex: number; rowsCount: number }) => {
      setExpression({
        setExpressionAction: (prev: Normalized<BoxedRelation>) => {
          const newRows = [...(prev.row ?? [])];
          const newItems = [];

          for (let i = 0; i < args.rowsCount; i++) {
            newItems.push({
              "@_id": generateUuid(),
              expression: Array.from(new Array(prev.column?.length ?? 0)).map(() => {
                return createCell();
              }),
            });
          }

          for (const newEntry of newItems) {
            newRows.splice(args.beforeIndex, 0, newEntry);
          }

          // Do not inline this variable for type safety. See https://github.com/microsoft/TypeScript/issues/241
          const ret: Normalized<BoxedRelation> = {
            ...prev,
            row: newRows,
          };

          return ret;
        },
        expressionChangedArgs: { action: Action.RowsAdded, rowsCount: args.rowsCount, rowIndex: args.beforeIndex },
      });
    },
    [createCell, setExpression]
  );

  const onColumnAdded = useCallback(
    (args: { beforeIndex: number; columnsCount: number }) => {
      setExpression({
        setExpressionAction: (prev: Normalized<BoxedRelation>) => {
          const newColumns = [...(prev.column ?? [])];

          const newItems = [];
          const availableNames = prev.column?.map((c) => c["@_name"]) ?? [];

          for (let i = 0; i < args.columnsCount; i++) {
            const name = getNextAvailablePrefixedName(availableNames, "column");
            availableNames.push(name);

            newItems.push({
              "@_id": generateUuid(),
              "@_name": name,
              "@_typeRef": undefined,
            });
          }

          for (const newEntry of newItems) {
            newColumns.splice(args.beforeIndex, 0, newEntry);
          }

          const newRows = [...(prev.row ?? [])].map((row) => {
            const newCells = [...(row.expression ?? [])];
            newCells.splice(args.beforeIndex, 0, createCell());
            return {
              ...row,
              expression: newCells,
            };
          });

          // Do not inline this variable for type safety. See https://github.com/microsoft/TypeScript/issues/241
          const ret: Normalized<BoxedRelation> = {
            ...prev,
            column: newColumns,
            row: newRows,
          };

          return ret;
        },
        expressionChangedArgs: {
          action: Action.ColumnAdded,
          columnCount: args.columnsCount,
          columnIndex: args.beforeIndex,
        },
      });

      setWidthsById(({ newMap }) => {
        const prev = newMap.get(id) ?? [];
        const defaultWidth = RELATION_EXPRESSION_COLUMN_DEFAULT_WIDTH;

        const newValues = [...prev];
        for (let i = 0; i < args.columnsCount; i++) {
          newValues.splice(args.beforeIndex + 1, 0, defaultWidth); // + 1 to account for rowIndex column
        }
        newMap.set(id, newValues);
      });
    },
    [createCell, id, setExpression, setWidthsById]
  );

  const onColumnDeleted = useCallback(
    (args: { columnIndex: number }) => {
      setExpression({
        setExpressionAction: (prev: Normalized<BoxedRelation>) => {
          const newColumns = [...(prev.column ?? [])];
          newColumns.splice(args.columnIndex, 1);

          const newRows = [...(prev.row ?? [])].map((row) => {
            const newCells = [...(row.expression ?? [])];
            newCells.splice(args.columnIndex, 1);
            return {
              ...row,
              expression: newCells,
            };
          });

          // Do not inline this variable for type safety. See https://github.com/microsoft/TypeScript/issues/241
          const ret: Normalized<BoxedRelation> = {
            ...prev,
            column: newColumns,
            row: newRows,
          };

          return ret;
        },
        expressionChangedArgs: { action: Action.ColumnRemoved, columnIndex: args.columnIndex },
      });

      setWidthsById(({ newMap }) => {
        const prev = newMap.get(id) ?? [];
        const newValues = [...prev];
        newValues.splice(args.columnIndex + 1, 1); // + 1 to account for the rowIndex column
        newMap.set(id, newValues);
      });
    },
    [id, setExpression, setWidthsById]
  );

  const onRowDeleted = useCallback(
    (args: { rowIndex: number }) => {
      setExpression({
        setExpressionAction: (prev: Normalized<BoxedRelation>) => {
          const newRows = [...(prev.row ?? [])];
          newRows.splice(args.rowIndex, 1);

          // Do not inline this variable for type safety. See https://github.com/microsoft/TypeScript/issues/241
          const ret: Normalized<BoxedRelation> = {
            ...prev,
            row: newRows,
          };

          return ret;
        },
        expressionChangedArgs: { action: Action.RowRemoved, rowIndex: args.rowIndex },
      });
    },
    [setExpression]
  );

  const onRowDuplicated = useCallback(
    (args: { rowIndex: number }) => {
      setExpression({
        setExpressionAction: (prev: Normalized<BoxedRelation>) => {
          const duplicatedRow = {
            "@_id": generateUuid(),
            expression: prev.row![args.rowIndex].expression?.map((cell) => ({
              ...cell,
              "@_id": generateUuid(),
            })),
          };

          const newRows = [...(prev.row ?? [])];
          newRows.splice(args.rowIndex, 0, duplicatedRow);

          // Do not inline this variable for type safety. See https://github.com/microsoft/TypeScript/issues/241
          const ret: Normalized<BoxedRelation> = {
            ...prev,
            row: newRows,
          };

          return ret;
        },
        expressionChangedArgs: { action: Action.RowDuplicated, rowIndex: args.rowIndex },
      });
    },
    [setExpression]
  );

  const beeTableHeaderVisibility = useMemo(() => {
    return isNested ? BeeTableHeaderVisibility.LastLevel : BeeTableHeaderVisibility.AllLevels;
  }, [isNested]);

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
      <BeeTable<ROWTYPE>
        isReadOnly={isReadOnly}
        isEditableHeader={!isReadOnly}
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
        // lastColumnMinWidth={lastColumnMinWidth} // FIXME: Check if this is a good strategy or not when doing https://github.com/apache/incubator-kie-issues/issues/181
      />
    </div>
  );
}
