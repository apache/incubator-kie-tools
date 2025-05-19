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
import { useCallback, useEffect, useMemo, useState } from "react";
import * as ReactTable from "react-table";
import {
  Action,
  BeeTableContextMenuAllowedOperationsConditions,
  BeeTableHeaderVisibility,
  BeeTableOperation,
  BeeTableOperationConfig,
  BeeTableProps,
  BoxedContext,
  BoxedExpression,
  DmnBuiltInDataType,
  ExpressionChangedArgs,
  generateUuid,
  getNextAvailablePrefixedName,
  Normalized,
} from "../../api";
import { useBoxedExpressionEditorI18n } from "../../i18n";
import { useNestedExpressionContainerWithNestedExpressions } from "../../resizing/Hooks";
import { NestedExpressionContainerContext } from "../../resizing/NestedExpressionContainerContext";
import { ResizerStopBehavior, ResizingWidth } from "../../resizing/ResizingWidthsContext";
import {
  CONTEXT_ENTRY_EXPRESSION_MIN_WIDTH,
  CONTEXT_ENTRY_VARIABLE_COLUMN_WIDTH_INDEX,
  CONTEXT_ENTRY_VARIABLE_MIN_WIDTH,
  CONTEXT_EXPRESSION_EXTRA_WIDTH,
} from "../../resizing/WidthConstants";
import { useBeeTableCoordinates, useBeeTableSelectableCellRef } from "../../selection/BeeTableSelectionContext";
import { BeeTable, BeeTableColumnUpdate } from "../../table/BeeTable";
import { useBoxedExpressionEditor, useBoxedExpressionEditorDispatch } from "../../BoxedExpressionEditorContext";
import { DEFAULT_EXPRESSION_VARIABLE_NAME } from "../../expressionVariable/ExpressionVariableMenu";
import { ContextEntryExpressionCell } from "./ContextEntryExpressionCell";
import { ExpressionVariableCell, ExpressionWithVariable } from "../../expressionVariable/ExpressionVariableCell";
import { ContextResultExpressionCell } from "./ContextResultExpressionCell";
import { getExpressionTotalMinWidth } from "../../resizing/WidthMaths";
import { DMN15__tContextEntry } from "@kie-tools/dmn-marshaller/dist/schemas/dmn-1_5/ts-gen/types";
import { findAllIdsDeep } from "../../ids/ids";
import "./ContextExpression.css";

export type ROWTYPE = ExpressionWithVariable & { index: number };

export function ContextExpression({
  isNested,
  parentElementId,
  expression: contextExpression,
}: {
  expression: Normalized<BoxedContext>;
  isNested: boolean;
  parentElementId: string;
}) {
  const { i18n } = useBoxedExpressionEditorI18n();
  const { setExpression, setWidthsById } = useBoxedExpressionEditorDispatch();
  const { expressionHolderId, widthsById, isReadOnly } = useBoxedExpressionEditor();

  const id = contextExpression["@_id"]!;

  const widths = useMemo(() => widthsById.get(id) ?? [], [id, widthsById]);

  const getEntryVariableWidth = useCallback(
    (widths: number[]) => widths?.[CONTEXT_ENTRY_VARIABLE_COLUMN_WIDTH_INDEX] ?? CONTEXT_ENTRY_VARIABLE_MIN_WIDTH,
    []
  );

  const entryVariableWidth = useMemo(() => getEntryVariableWidth(widths), [getEntryVariableWidth, widths]);

  const setEntryVariableWidth = useCallback(
    (newWidthAction: React.SetStateAction<number | undefined>) => {
      setWidthsById(({ newMap }) => {
        const prev = newMap.get(id) ?? [];
        const newWidth =
          typeof newWidthAction === "function" ? newWidthAction(getEntryVariableWidth(prev)) : newWidthAction;

        if (newWidth) {
          const minSize = CONTEXT_ENTRY_VARIABLE_COLUMN_WIDTH_INDEX + 1;
          const newValues = [...prev];
          newValues.push(
            ...Array<number>(Math.max(0, minSize - newValues.length)).fill(CONTEXT_ENTRY_VARIABLE_MIN_WIDTH)
          );
          newValues.splice(CONTEXT_ENTRY_VARIABLE_COLUMN_WIDTH_INDEX, 1, newWidth);
          newMap.set(id, newValues);
        }
      });
    },
    [getEntryVariableWidth, id, setWidthsById]
  );

  const [entryVariableResizingWidth, setEntryVariableResizingWidth] = useState<ResizingWidth>({
    value: entryVariableWidth,
    isPivoting: false,
  });

  const onColumnResizingWidthChange1 = useCallback((args: Map<number, ResizingWidth | undefined>) => {
    const newResizingWidth = args.get(1);
    if (newResizingWidth) {
      setEntryVariableResizingWidth(newResizingWidth);
    }
  }, []);

  /// //////////////////////////////////////////////////////
  /// ///////////// RESIZING WIDTHS ////////////////////////
  /// //////////////////////////////////////////////////////
  const { nestedExpressionContainerValue, onColumnResizingWidthChange: onColumnResizingWidthChange2 } =
    useNestedExpressionContainerWithNestedExpressions(
      useMemo(() => {
        const nestedExpressions = (contextExpression.contextEntry ?? []).map((e) => e.expression);

        const maxNestedExpressionTotalMinWidth = Math.max(
          ...nestedExpressions.map((e) => getExpressionTotalMinWidth(0, e, widthsById)),
          CONTEXT_ENTRY_EXPRESSION_MIN_WIDTH
        );

        return {
          nestedExpressions: nestedExpressions,
          fixedColumnActualWidth: entryVariableWidth,
          fixedColumnResizingWidth: entryVariableResizingWidth,
          fixedColumnMinWidth: CONTEXT_ENTRY_VARIABLE_MIN_WIDTH,
          nestedExpressionMinWidth: maxNestedExpressionTotalMinWidth,
          extraWidth: CONTEXT_EXPRESSION_EXTRA_WIDTH,
          expression: contextExpression,
          flexibleColumnIndex: 2,
          widthsById: widthsById,
        };
      }, [contextExpression, entryVariableResizingWidth, entryVariableWidth, widthsById])
    );

  /// //////////////////////////////////////////////////////

  const onColumnResizingWidthChange = useCallback(
    (args: Map<number, ResizingWidth | undefined>) => {
      onColumnResizingWidthChange2?.(args);
      onColumnResizingWidthChange1(args);
    },
    [onColumnResizingWidthChange1, onColumnResizingWidthChange2]
  );

  const beeTableColumns = useMemo<ReactTable.Column<ROWTYPE>[]>(() => {
    return [
      {
        accessor: expressionHolderId as any, // FIXME: https://github.com/apache/incubator-kie-issues/issues/169
        label: contextExpression["@_label"] ?? DEFAULT_EXPRESSION_VARIABLE_NAME,
        isRowIndexColumn: false,
        dataType: contextExpression["@_typeRef"] ?? DmnBuiltInDataType.Undefined,
        width: undefined,
        columns: [
          {
            accessor: "variable",
            label: "variable",
            isRowIndexColumn: false,
            dataType: DmnBuiltInDataType.Undefined,
            isWidthPinned: true,
            minWidth: CONTEXT_ENTRY_VARIABLE_MIN_WIDTH,
            width: entryVariableWidth,
            setWidth: setEntryVariableWidth,
          },
          {
            accessor: "expression",
            label: "expression",
            dataType: DmnBuiltInDataType.Undefined,
            isRowIndexColumn: false,
            minWidth: CONTEXT_ENTRY_EXPRESSION_MIN_WIDTH,
            width: undefined,
          },
        ],
      },
    ];
  }, [contextExpression, entryVariableWidth, expressionHolderId, setEntryVariableWidth]);

  const onColumnUpdates = useCallback(
    ([{ name, typeRef }]: BeeTableColumnUpdate<ROWTYPE>[]) => {
      const expressionChangedArgs: ExpressionChangedArgs = {
        action: Action.VariableChanged,
        variableUuid: expressionHolderId,
        typeChange:
          typeRef !== contextExpression["@_typeRef"]
            ? {
                from: contextExpression["@_typeRef"] ?? "",
                to: typeRef,
              }
            : undefined,
        nameChange:
          name !== contextExpression["@_label"]
            ? {
                from: contextExpression["@_label"] ?? "",
                to: name,
              }
            : undefined,
      };

      setExpression({
        setExpressionAction: (prev: Normalized<BoxedContext>) => {
          // Do not inline this variable for type safety. See https://github.com/microsoft/TypeScript/issues/241
          const ret: Normalized<BoxedContext> = {
            ...prev,
            "@_label": name,
            "@_typeRef": typeRef,
          };

          return ret;
        },
        expressionChangedArgs,
      });
    },
    [contextExpression, expressionHolderId, setExpression]
  );

  const headerVisibility = useMemo(() => {
    return isNested ? BeeTableHeaderVisibility.None : BeeTableHeaderVisibility.SecondToLastLevel;
  }, [isNested]);

  const updateVariable = useCallback(
    (index: number, { expression, variable }: ExpressionWithVariable, variableChangedArgs) => {
      setExpression({
        setExpressionAction: (prev: Normalized<BoxedContext>) => {
          const contextEntries = [...(prev.contextEntry ?? [])];
          contextEntries[index] = {
            ...contextEntries[index],
            expression: expression ?? undefined!, // SPEC DISCREPANCY
            variable: variable,
          };

          // Do not inline this variable for type safety. See https://github.com/microsoft/TypeScript/issues/241
          const ret: Normalized<BoxedContext> = {
            ...prev,
            contextEntry: contextEntries,
          };

          return ret;
        },
        expressionChangedArgs: variableChangedArgs,
      });
    },
    [setExpression]
  );

  const cellComponentByColumnAccessor: BeeTableProps<ROWTYPE>["cellComponentByColumnAccessor"] = useMemo(() => {
    return {
      variable: (props) => <ExpressionVariableCell {...props} onExpressionWithVariableUpdated={updateVariable} />,
      expression: (props) => <ContextEntryExpressionCell {...props} />,
    };
  }, [updateVariable]);

  const beeTableOperationConfig = useMemo<BeeTableOperationConfig>(() => {
    return [
      {
        group: i18n.contextEntry,
        items: [
          { name: i18n.rowOperations.reset, type: BeeTableOperation.RowReset },
          { name: i18n.rowOperations.insertAbove, type: BeeTableOperation.RowInsertAbove },
          { name: i18n.rowOperations.insertBelow, type: BeeTableOperation.RowInsertBelow },
          { name: i18n.insert, type: BeeTableOperation.RowInsertN },
          { name: i18n.rowOperations.delete, type: BeeTableOperation.RowDelete },
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
    ];
  }, [i18n]);

  const getRowKey = useCallback((row: ReactTable.Row<ROWTYPE>) => {
    return row.id;
  }, []);

  const beeTableAdditionalRow = useMemo(() => {
    return [
      <ContextResultInfoCell key={"context-result-info"} parentElementId={parentElementId} />,
      <ContextResultExpressionCell
        key={"context-result-expression"}
        contextExpression={contextExpression}
        rowIndex={contextExpression.contextEntry?.length ?? 1}
        columnIndex={2}
      />,
    ];
  }, [contextExpression, parentElementId]);

  const getDefaultContextEntry = useCallback(
    (name?: string): Normalized<DMN15__tContextEntry> => {
      const variableName =
        name ||
        getNextAvailablePrefixedName(
          (contextExpression.contextEntry ?? []).map((e) => e.variable?.["@_name"] ?? ""),
          "ContextEntry"
        );
      return {
        "@_id": generateUuid(),
        expression: undefined!, // SPEC DISCREPANCY: Starting without an expression gives users the ability to select the expression type.
        variable: {
          "@_id": generateUuid(),
          "@_name": variableName,
          "@_typeRef": undefined,
          description: { __$$text: "" },
        },
      };
    },
    [contextExpression]
  );

  const onRowAdded = useCallback(
    (args: { beforeIndex: number; rowsCount: number }) => {
      setExpression({
        setExpressionAction: (prev: Normalized<BoxedContext>) => {
          const newContextEntries = [...(prev.contextEntry ?? [])];

          const newEntries = [];
          const names = newContextEntries.map((e) => e.variable?.["@_name"] ?? "").filter((e) => e !== "");
          for (let i = 0; i < args.rowsCount; i++) {
            const name = getNextAvailablePrefixedName(names, "ContextEntry");
            names.push(name);
            newEntries.push(getDefaultContextEntry(name));
          }

          for (const newEntry of newEntries) {
            newContextEntries.splice(args.beforeIndex, 0, newEntry);
          }

          // Do not inline this variable for type safety. See https://github.com/microsoft/TypeScript/issues/241
          const ret: Normalized<BoxedContext> = {
            ...prev,
            contextEntry: newContextEntries,
          };

          return ret;
        },
        expressionChangedArgs: { action: Action.RowsAdded, rowIndex: args.beforeIndex, rowsCount: args.rowsCount },
      });
    },
    [getDefaultContextEntry, setExpression]
  );

  const onRowDeleted = useCallback(
    (args: { rowIndex: number }) => {
      let oldExpression: Normalized<BoxedExpression> | undefined;

      setExpression({
        setExpressionAction: (prev: Normalized<BoxedContext>) => {
          const newContextEntries = [...(prev.contextEntry ?? [])];

          const { isResultOperation: isDeletingResult, entryIndex } = solveResultAndEntriesIndex({
            contextEntries: newContextEntries,
            rowIndex: args.rowIndex,
          });

          if (isDeletingResult) {
            throw new Error("It's not possible to delete the <result> row");
          } else {
            oldExpression = newContextEntries[entryIndex]?.expression;
            newContextEntries.splice(entryIndex, 1);
          }

          // Do not inline this variable for type safety. See https://github.com/microsoft/TypeScript/issues/241
          const ret: Normalized<BoxedContext> = {
            ...prev,
            contextEntry: newContextEntries,
          };

          return ret;
        },
        expressionChangedArgs: { action: Action.RowRemoved, rowIndex: args.rowIndex },
      });

      setWidthsById(({ newMap }) => {
        for (const id of findAllIdsDeep(oldExpression)) {
          newMap.delete(id);
        }
      });
    },
    [setExpression, setWidthsById]
  );

  const onRowReset = useCallback(
    (args: { rowIndex: number }) => {
      let oldExpression: Normalized<BoxedExpression> | undefined;

      setExpression({
        setExpressionAction: (prev: Normalized<BoxedContext>) => {
          const newContextEntries = [...(prev.contextEntry ?? [])];

          const {
            isResultOperation: isResettingResult,
            hasResultEntry: hasResultExpression,
            resultIndex,
            entryIndex,
          } = solveResultAndEntriesIndex({
            contextEntries: newContextEntries,
            rowIndex: args.rowIndex,
          });

          if (isResettingResult) {
            if (hasResultExpression) {
              oldExpression = newContextEntries[resultIndex]?.expression;
              newContextEntries.splice(resultIndex, 1);
            } else {
              // ignore
            }
          } else {
            oldExpression = newContextEntries[entryIndex]?.expression;
            const defaultContextEntry = getDefaultContextEntry(newContextEntries[entryIndex]?.variable?.["@_name"]);
            newContextEntries.splice(entryIndex, 1, defaultContextEntry);
          }

          // Do not inline this variable for type safety. See https://github.com/microsoft/TypeScript/issues/241
          const ret: Normalized<BoxedContext> = {
            ...prev,
            contextEntry: newContextEntries,
          };

          return ret;
        },
        expressionChangedArgs: { action: Action.RowReset, rowIndex: args.rowIndex },
      });

      setWidthsById(({ newMap }) => {
        for (const id of findAllIdsDeep(oldExpression)) {
          newMap.delete(id);
        }
      });
    },
    [getDefaultContextEntry, setExpression, setWidthsById]
  );

  const allowedOperations = useCallback(
    (conditions: BeeTableContextMenuAllowedOperationsConditions) => {
      if (!conditions.selection.selectionStart || !conditions.selection.selectionEnd) {
        return [];
      }

      const columnIndex = conditions.selection.selectionStart.columnIndex;
      const rowIndex = conditions.selection.selectionStart.rowIndex;

      const contextEntries = contextExpression.contextEntry ?? [];

      const { isResultOperation, hasResultEntry } = solveResultAndEntriesIndex({ contextEntries, rowIndex });

      const canDeleteEntry =
        !isResultOperation && (hasResultEntry ? contextEntries.length > 2 : contextEntries.length > 1);

      return [
        BeeTableOperation.SelectionCopy,
        ...(columnIndex > 1
          ? [BeeTableOperation.SelectionCut, BeeTableOperation.SelectionPaste, BeeTableOperation.SelectionReset]
          : []),
        ...(conditions.selection.selectionStart.rowIndex >= 0
          ? [
              BeeTableOperation.RowInsertAbove,
              ...(!isResultOperation ? [BeeTableOperation.RowInsertBelow] : []),
              ...(!isResultOperation ? [BeeTableOperation.RowInsertN] : []),
              ...(canDeleteEntry ? [BeeTableOperation.RowDelete] : []),
              BeeTableOperation.RowReset,
            ]
          : []),
      ];
    },
    [contextExpression.contextEntry]
  );

  const beeTableRows = useMemo<ROWTYPE[]>(() => {
    return (contextExpression.contextEntry ?? []).flatMap((contextEntry, i) =>
      !contextEntry.variable
        ? []
        : {
            ...contextEntry,
            variable: contextEntry.variable,
            index: i,
          }
    );
  }, [contextExpression.contextEntry]);

  return (
    <NestedExpressionContainerContext.Provider value={nestedExpressionContainerValue}>
      <div className={`context-expression ${id}`}>
        <BeeTable<ROWTYPE>
          isReadOnly={isReadOnly}
          isEditableHeader={!isReadOnly}
          resizerStopBehavior={ResizerStopBehavior.SET_WIDTH_WHEN_SMALLER}
          tableId={id}
          headerLevelCountForAppendingRowIndexColumn={1}
          headerVisibility={headerVisibility}
          cellComponentByColumnAccessor={cellComponentByColumnAccessor}
          columns={beeTableColumns}
          rows={beeTableRows}
          onColumnUpdates={onColumnUpdates}
          operationConfig={beeTableOperationConfig}
          allowedOperations={allowedOperations}
          getRowKey={getRowKey}
          additionalRow={beeTableAdditionalRow}
          onRowAdded={onRowAdded}
          onRowReset={onRowReset}
          onRowDeleted={onRowDeleted}
          onColumnResizingWidthChange={onColumnResizingWidthChange}
          shouldRenderRowIndexColumn={false}
          shouldShowRowsInlineControls={true}
          shouldShowColumnsInlineControls={false}
        />
      </div>
    </NestedExpressionContainerContext.Provider>
  );
}

export function solveResultAndEntriesIndex({
  contextEntries,
  rowIndex,
}: {
  contextEntries: DMN15__tContextEntry[];
  rowIndex: number;
}) {
  const resultIndex = contextEntries.findIndex((e) => !e.variable);

  const hasResultEntry = resultIndex > -1;

  const isResultOperation =
    rowIndex === Math.max(1, hasResultEntry ? contextEntries.length - 1 : contextEntries.length);

  const entryIndex = Math.min(
    contextEntries.length - 1,
    resultIndex === -1
      ? rowIndex //
      : resultIndex < rowIndex
        ? rowIndex + 1
        : rowIndex
  );

  return { isResultOperation, hasResultEntry, resultIndex, entryIndex };
}

export function ContextResultInfoCell(props: { parentElementId: string }) {
  const { containerCellCoordinates } = useBeeTableCoordinates();

  const value = useMemo(() => {
    return `<result>`;
  }, []);

  const getValue = useCallback(() => {
    return value;
  }, [value]);

  const { isActive } = useBeeTableSelectableCellRef(
    containerCellCoordinates?.rowIndex ?? 0,
    containerCellCoordinates?.columnIndex ?? 0,
    undefined,
    getValue
  );

  const { beeGwtService } = useBoxedExpressionEditor();

  // Selecting the context result cell should be the parent data type
  useEffect(() => {
    if (isActive) {
      beeGwtService?.selectObject(props.parentElementId);
    }
  }, [beeGwtService, isActive, props.parentElementId]);

  return <div className="context-result">{value}</div>;
}
