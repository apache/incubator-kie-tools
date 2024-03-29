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
  BeeTableContextMenuAllowedOperationsConditions,
  BeeTableHeaderVisibility,
  BeeTableOperation,
  BeeTableOperationConfig,
  BeeTableProps,
  BoxedContext,
  BoxedExpression,
  DmnBuiltInDataType,
  generateUuid,
  getNextAvailablePrefixedName,
} from "../../api";
import { useBoxedExpressionEditorI18n } from "../../i18n";
import { useNestedExpressionContainerWithNestedExpressions } from "../../resizing/Hooks";
import { NestedExpressionContainerContext } from "../../resizing/NestedExpressionContainerContext";
import { ResizerStopBehavior, ResizingWidth } from "../../resizing/ResizingWidthsContext";
import {
  CONTEXT_ENTRY_EXPRESSION_MIN_WIDTH,
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

const CONTEXT_ENTRY_DEFAULT_DATA_TYPE = DmnBuiltInDataType.Undefined;
const CONTEXT_ENTRY_VARIABLE_WIDTH_INDEX = 0;

export type ROWTYPE = ExpressionWithVariable & { index: number };

export function ContextExpression(
  contextExpression: BoxedContext & {
    isNested: boolean;
    parentElementId: string;
  }
) {
  const { i18n } = useBoxedExpressionEditorI18n();
  const { setExpression, setWidthsById } = useBoxedExpressionEditorDispatch();
  const { variables, widthsById } = useBoxedExpressionEditor();

  const id = contextExpression["@_id"]!;

  const widths = useMemo(() => widthsById.get(id) ?? [], [id, widthsById]);

  const getEntryVariableWidth = useCallback(
    (widths: number[]) => widths?.[CONTEXT_ENTRY_VARIABLE_WIDTH_INDEX] ?? CONTEXT_ENTRY_VARIABLE_MIN_WIDTH,
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
          const minSize = CONTEXT_ENTRY_VARIABLE_WIDTH_INDEX + 1;
          const newValues = [...prev];
          newValues.push(...Array(Math.max(0, minSize - newValues.length)));
          newValues.splice(CONTEXT_ENTRY_VARIABLE_WIDTH_INDEX, 1, newWidth);
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

  const resultExpression = useMemo(
    () => contextExpression.contextEntry?.find((e) => !e.variable)?.expression,
    [contextExpression.contextEntry]
  );

  const { nestedExpressionContainerValue, onColumnResizingWidthChange: onColumnResizingWidthChange2 } =
    useNestedExpressionContainerWithNestedExpressions(
      useMemo(() => {
        const entriesWidths = (contextExpression.contextEntry ?? []).map((e) =>
          getExpressionTotalMinWidth(0, e.expression, widthsById)
        );

        const resultWidth = getExpressionTotalMinWidth(0, resultExpression, widthsById);
        const maxNestedExpressionMinWidth = Math.max(...entriesWidths, resultWidth, CONTEXT_ENTRY_EXPRESSION_MIN_WIDTH);

        return {
          nestedExpressions: (contextExpression.contextEntry ?? []).map((e) => e.expression),
          fixedColumnActualWidth: entryVariableWidth,
          fixedColumnResizingWidth: entryVariableResizingWidth,
          fixedColumnMinWidth: CONTEXT_ENTRY_VARIABLE_MIN_WIDTH,
          nestedExpressionMinWidth: maxNestedExpressionMinWidth,
          extraWidth: CONTEXT_EXPRESSION_EXTRA_WIDTH,
          expression: contextExpression,
          flexibleColumnIndex: 2,
          widthsById: widthsById,
        };
      }, [contextExpression, entryVariableResizingWidth, entryVariableWidth, resultExpression, widthsById])
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
        accessor: id as any, // FIXME: https://github.com/kiegroup/kie-issues/issues/169
        label: contextExpression["@_label"] ?? DEFAULT_EXPRESSION_VARIABLE_NAME,
        isRowIndexColumn: false,
        dataType: contextExpression["@_typeRef"] ?? CONTEXT_ENTRY_DEFAULT_DATA_TYPE,
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
  }, [contextExpression, entryVariableWidth, id, setEntryVariableWidth]);

  const onColumnUpdates = useCallback(
    ([{ name, typeRef }]: BeeTableColumnUpdate<ROWTYPE>[]) => {
      setExpression((prev) => ({
        ...prev,
        "@_label": name,
        "@_typeRef": typeRef,
        // FIXME: Tiago --> Update <result> expression @_label and @_typeRef here too.
      }));
    },
    [setExpression]
  );

  const headerVisibility = useMemo(() => {
    return contextExpression.isNested ? BeeTableHeaderVisibility.None : BeeTableHeaderVisibility.SecondToLastLevel;
  }, [contextExpression.isNested]);

  const updateVariable = useCallback(
    (index: number, { expression, variable }: ExpressionWithVariable) => {
      setExpression((prev: BoxedContext) => {
        const contextEntries = [...(prev.contextEntry ?? [])];

        variables?.repository.updateVariableType(
          variable?.["@_id"] ?? "",
          variable?.["@_typeRef"] ?? DmnBuiltInDataType.Undefined
        );
        variables?.repository.renameVariable(
          variable?.["@_id"] ?? "",
          variable?.["@_name"] ?? DmnBuiltInDataType.Undefined
        );

        contextEntries[index] = {
          ...contextEntries[index],
          expression: expression ?? undefined!, // SPEC DISCREPANCY
          variable: variable,
        };

        return {
          ...prev,
          contextEntry: contextEntries,
        };
      });
    },
    [setExpression, variables?.repository]
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
      <ContextResultInfoCell key={"context-result-info"} />,
      <ContextResultExpressionCell
        key={"context-result-expression"}
        contextExpression={contextExpression}
        rowIndex={contextExpression.contextEntry?.length ?? 1}
        columnIndex={2}
      />,
    ];
  }, [contextExpression]);

  const getDefaultContextEntry = useCallback(
    (name?: string): DMN15__tContextEntry => {
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
          "@_name": variableName,
          "@_typeRef": DmnBuiltInDataType.Undefined,
          description: { __$$text: "" },
        },
      };
    },
    [contextExpression]
  );

  const addVariable = useCallback(
    (
      args: {
        beforeIndex: number;
      },
      newContextEntries: DMN15__tContextEntry[],
      prev: BoxedContext,
      newVariable: DMN15__tContextEntry
    ) => {
      const parentIndex = args.beforeIndex - 1;
      let parentId = contextExpression.parentElementId;
      if (parentIndex >= 0 && parentIndex < newContextEntries.length) {
        parentId = newContextEntries[parentIndex].variable?.["@_id"] ?? "";
      }

      let childId: undefined | string;
      if (args.beforeIndex < newContextEntries.length) {
        childId = newContextEntries[args.beforeIndex].variable?.["@_id"];
      } else {
        childId = prev.contextEntry?.find((e) => !e.variable)?.["@_id"] ?? "";
      }

      variables?.repository.addVariableToContext(
        newVariable.variable?.["@_id"] ?? "",
        newVariable.variable?.["@_name"] ?? "",
        parentId,
        childId
      );
    },
    [contextExpression.parentElementId, variables?.repository]
  );

  const onRowAdded = useCallback(
    (args: { beforeIndex: number; rowsCount: number }) => {
      setExpression((prev: BoxedContext) => {
        const newContextEntries = [...(prev.contextEntry ?? [])];

        const newEntries = [];
        const names = newContextEntries.map((e) => e.variable?.["@_name"] ?? "");
        for (let i = 0; i < args.rowsCount; i++) {
          const name = getNextAvailablePrefixedName(names, "ContextEntry");
          names.push(name);

          const defaultContextEntry = getDefaultContextEntry(name);
          addVariable(args, newContextEntries, prev, defaultContextEntry);
          newEntries.push(defaultContextEntry);
        }

        for (const newEntry of newEntries) {
          newContextEntries.splice(args.beforeIndex, 0, newEntry);
        }

        return {
          ...prev,
          contextEntry: newContextEntries,
        };
      });
    },
    [addVariable, getDefaultContextEntry, setExpression]
  );

  const onRowDeleted = useCallback(
    (args: { rowIndex: number }) => {
      let oldExpression: BoxedExpression | undefined;

      setExpression((prev: BoxedContext) => {
        const newContextEntries = [...(prev.contextEntry ?? [])];

        const { isResultOperation: isDeletingResult, entryIndex } = solveResultAndEntriesIndex({
          contextEntries: newContextEntries,
          rowIndex: args.rowIndex,
        });

        if (isDeletingResult) {
          throw new Error("It's not possible to delete the <result> row");
        } else {
          if (prev.contextEntry) {
            variables?.repository.removeVariable(prev.contextEntry[entryIndex]["@_id"]!);
          }

          oldExpression = newContextEntries[entryIndex]?.expression;
          newContextEntries.splice(entryIndex, 1);
        }

        return {
          ...prev,
          contextEntry: newContextEntries,
        };
      });

      setWidthsById(({ newMap }) => {
        for (const id of findAllIdsDeep(oldExpression)) {
          newMap.delete(id);
        }
      });
    },
    [setExpression, setWidthsById, variables?.repository]
  );

  const onRowReset = useCallback(
    (args: { rowIndex: number }) => {
      let oldExpression: BoxedExpression | undefined;

      setExpression((prev: BoxedContext) => {
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

        return {
          ...prev,
          contextEntry: newContextEntries,
        };
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

      const isResultEntry = rowIndex === contextExpression.contextEntry?.length;

      return [
        BeeTableOperation.SelectionCopy,
        ...(columnIndex > 1
          ? [BeeTableOperation.SelectionCut, BeeTableOperation.SelectionPaste, BeeTableOperation.SelectionReset]
          : []),
        ...(conditions.selection.selectionStart.rowIndex >= 0
          ? [
              BeeTableOperation.RowInsertAbove,
              ...(!isResultEntry ? [BeeTableOperation.RowInsertBelow] : []), // do not insert below <result>
              ...(!isResultEntry ? [BeeTableOperation.RowInsertN] : []), // Because we can't insert multiple lines below <result>
              ...((contextExpression.contextEntry?.length ?? 0) > 1 && !isResultEntry
                ? [BeeTableOperation.RowDelete]
                : []), // do not delete <result>
              BeeTableOperation.RowReset,
            ]
          : []),
      ];
    },
    [contextExpression.contextEntry?.length]
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
          variables={variables}
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

export function ContextResultInfoCell() {
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

  useEffect(() => {
    if (isActive) {
      beeGwtService?.selectObject("");
    }
  }, [beeGwtService, isActive]);

  return <div className="context-result">{value}</div>;
}
