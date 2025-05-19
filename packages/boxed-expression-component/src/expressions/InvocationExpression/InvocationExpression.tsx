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
import { useCallback, useEffect, useMemo } from "react";
import * as ReactTable from "react-table";
import {
  Action,
  BeeTableContextMenuAllowedOperationsConditions,
  BeeTableHeaderVisibility,
  BeeTableOperation,
  BeeTableOperationConfig,
  BeeTableProps,
  BoxedExpression,
  BoxedInvocation,
  DmnBuiltInDataType,
  generateUuid,
  getNextAvailablePrefixedName,
  Normalized,
} from "../../api";
import { useBoxedExpressionEditorI18n } from "../../i18n";
import { NestedExpressionContainerContext } from "../../resizing/NestedExpressionContainerContext";
import { ResizerStopBehavior, ResizingWidth } from "../../resizing/ResizingWidthsContext";
import {
  CONTEXT_ENTRY_VARIABLE_MIN_WIDTH,
  INVOCATION_ARGUMENT_EXPRESSION_MIN_WIDTH,
  INVOCATION_EXTRA_WIDTH,
  INVOCATION_PARAMETER_INFO_COLUMN_WIDTH_INDEX,
  INVOCATION_PARAMETER_MIN_WIDTH,
} from "../../resizing/WidthConstants";
import { BeeTable, BeeTableColumnUpdate } from "../../table/BeeTable";
import { useBoxedExpressionEditor, useBoxedExpressionEditorDispatch } from "../../BoxedExpressionEditorContext";
import { useNestedExpressionContainerWithNestedExpressions } from "../../resizing/Hooks";
import { ArgumentEntryExpressionCell } from "./ArgumentEntryExpressionCell";
import { ExpressionVariableCell, ExpressionWithVariable } from "../../expressionVariable/ExpressionVariableCell";
import { DEFAULT_EXPRESSION_VARIABLE_NAME } from "../../expressionVariable/ExpressionVariableMenu";
import { getExpressionTotalMinWidth } from "../../resizing/WidthMaths";
import { useBeeTableCoordinates, useBeeTableSelectableCellRef } from "../../selection/BeeTableSelectionContext";
import { DMN15__tBinding } from "@kie-tools/dmn-marshaller/dist/schemas/dmn-1_5/ts-gen/types";
import { findAllIdsDeep } from "../../ids/ids";
import "./InvocationExpression.css";

export type ROWTYPE = ExpressionWithVariable & { index: number };

export const INVOCATION_EXPRESSION_DEFAULT_PARAMETER_NAME = "p-1";

export function InvocationExpression({
  isNested,
  parentElementId,
  expression: invocationExpression,
}: {
  expression: Normalized<BoxedInvocation>;
  isNested: boolean;
  parentElementId: string;
}) {
  const { i18n } = useBoxedExpressionEditorI18n();
  const { expressionHolderId, widthsById, isReadOnly } = useBoxedExpressionEditor();
  const { setExpression, setWidthsById } = useBoxedExpressionEditorDispatch();

  const id = invocationExpression["@_id"]!;

  const widths = useMemo(() => widthsById.get(id) ?? [], [id, widthsById]);

  const getParametersWidth = useCallback((widths: number[]) => {
    return widths?.[INVOCATION_PARAMETER_INFO_COLUMN_WIDTH_INDEX] ?? INVOCATION_PARAMETER_MIN_WIDTH;
  }, []);

  const parametersWidth = useMemo(() => getParametersWidth(widths), [getParametersWidth, widths]);

  const setParametersWidth = useCallback(
    (newWidthAction: React.SetStateAction<number | undefined>) => {
      setWidthsById(({ newMap }) => {
        const prev = newMap.get(id) ?? [];
        const newWidth =
          typeof newWidthAction === "function" ? newWidthAction(getParametersWidth(prev)) : newWidthAction;

        if (newWidth) {
          const minSize = INVOCATION_PARAMETER_INFO_COLUMN_WIDTH_INDEX + 1;
          const newValues = [...prev];
          newValues.push(
            ...Array<number>(Math.max(0, minSize - newValues.length)).fill(INVOCATION_PARAMETER_MIN_WIDTH)
          );
          newValues.splice(INVOCATION_PARAMETER_INFO_COLUMN_WIDTH_INDEX, 1, newWidth);
          newMap.set(id, newValues);
        }
      });
    },
    [getParametersWidth, id, setWidthsById]
  );

  const [parametersResizingWidth, setParametersResizingWidth] = React.useState<ResizingWidth>({
    value: parametersWidth,
    isPivoting: false,
  });

  const onColumnResizingWidthChange1 = useCallback((args: Map<number, ResizingWidth | undefined>) => {
    const newResizingWidth = args.get(1);
    if (newResizingWidth) {
      setParametersResizingWidth(newResizingWidth);
    }
  }, []);

  const { containerCellCoordinates } = useBeeTableCoordinates();
  const { isActive } = useBeeTableSelectableCellRef(
    containerCellCoordinates?.rowIndex ?? 0,
    containerCellCoordinates?.columnIndex ?? 0,
    undefined
  );

  const { beeGwtService } = useBoxedExpressionEditor();
  useEffect(() => {
    if (isActive) {
      beeGwtService?.selectObject();
    }
  }, [beeGwtService, isActive]);

  /// //////////////////////////////////////////////////////
  /// ///////////// RESIZING WIDTHS ////////////////////////
  /// //////////////////////////////////////////////////////

  const { nestedExpressionContainerValue, onColumnResizingWidthChange: onColumnResizingWidthChange2 } =
    useNestedExpressionContainerWithNestedExpressions(
      useMemo(() => {
        const nestedExpressions = (invocationExpression.binding ?? []).map((b) => b.expression ?? undefined!);

        const maxNestedExpressionTotalMinWidth = Math.max(
          ...nestedExpressions.map((e) => getExpressionTotalMinWidth(0, e, widthsById)),
          INVOCATION_ARGUMENT_EXPRESSION_MIN_WIDTH
        );

        return {
          nestedExpressions: nestedExpressions,
          fixedColumnActualWidth: parametersWidth,
          fixedColumnResizingWidth: parametersResizingWidth,
          fixedColumnMinWidth: INVOCATION_PARAMETER_MIN_WIDTH,
          nestedExpressionMinWidth: maxNestedExpressionTotalMinWidth,
          extraWidth: INVOCATION_EXTRA_WIDTH,
          expression: invocationExpression,
          flexibleColumnIndex: 2,
          widthsById: widthsById,
        };
      }, [invocationExpression, parametersWidth, parametersResizingWidth, widthsById])
    );

  /// //////////////////////////////////////////////////////

  const onColumnResizingWidthChange = useCallback(
    (args: Map<number, ResizingWidth | undefined>) => {
      onColumnResizingWidthChange2?.(args);
      onColumnResizingWidthChange1(args);
    },
    [onColumnResizingWidthChange1, onColumnResizingWidthChange2]
  );

  const beeTableRows = useMemo<ROWTYPE[]>(() => {
    return (invocationExpression.binding ?? []).map((b, i) => ({
      variable: b.parameter,
      expression: b.expression,
      index: i,
    }));
  }, [invocationExpression.binding]);

  const invocationId = useMemo(
    () => invocationExpression.expression?.["@_id"] ?? "functionName",
    [invocationExpression.expression]
  );

  const beeTableColumns = useMemo<ReactTable.Column<ROWTYPE>[]>(
    () => [
      {
        accessor: expressionHolderId as any, // FIXME: https://github.com/apache/incubator-kie-issues/issues/169
        label: invocationExpression["@_label"] ?? DEFAULT_EXPRESSION_VARIABLE_NAME,
        dataType: invocationExpression["@_typeRef"] ?? DmnBuiltInDataType.Undefined,
        isRowIndexColumn: false,
        width: undefined,
        columns: [
          {
            accessor: invocationId as keyof ROWTYPE,
            label:
              invocationExpression.expression?.__$$element === "literalExpression"
                ? invocationExpression.expression.text?.__$$text ?? "Function name"
                : "Function name",
            isRowIndexColumn: false,
            isInlineEditable: true,
            dataType: undefined as any,
            width: undefined,
            groupType: "invokedFunctionName",
            columns: [
              {
                accessor: "parameter" as any,
                label: "parameter",
                isRowIndexColumn: false,
                dataType: DmnBuiltInDataType.Undefined,
                isWidthPinned: true,
                minWidth: CONTEXT_ENTRY_VARIABLE_MIN_WIDTH,
                width: parametersWidth,
                setWidth: setParametersWidth,
              },
              {
                accessor: "expression" as any,
                label: "expression",
                isRowIndexColumn: false,
                dataType: DmnBuiltInDataType.Undefined,
                minWidth: INVOCATION_ARGUMENT_EXPRESSION_MIN_WIDTH,
                width: undefined,
              },
            ],
          },
        ],
      },
    ],
    [expressionHolderId, invocationExpression, parametersWidth, invocationId, setParametersWidth]
  );

  const onColumnUpdates = useCallback(
    (columnUpdates: BeeTableColumnUpdate<ROWTYPE>[]) => {
      for (const u of columnUpdates) {
        if (u.column.originalId === id) {
          setExpression({
            setExpressionAction: (prev: Normalized<BoxedInvocation>) => ({
              ...prev,
              "@_id": prev["@_id"],
              name: u.name,
            }),
            expressionChangedArgs: {
              action: Action.VariableChanged,
              variableUuid: expressionHolderId,
              nameChange: { from: invocationExpression["@_label"] ?? "", to: u.name },
            },
          });
        } else if (u.column.originalId === invocationId) {
          setExpression({
            setExpressionAction: (prev: Normalized<BoxedInvocation>) => {
              // Do not inline this variable for type safety. See https://github.com/microsoft/TypeScript/issues/241
              const ret: Normalized<BoxedInvocation> = {
                ...prev,
                expression: {
                  ...prev.expression,
                  "@_id": prev.expression?.["@_id"] ?? generateUuid(),
                  __$$element: "literalExpression",
                  text: {
                    __$$text: u.name,
                  },
                },
              };
              return ret;
            },
            expressionChangedArgs: {
              action: Action.VariableChanged,
              variableUuid: expressionHolderId,
              nameChange: { from: invocationExpression["@_label"] ?? "", to: u.name },
            },
          });
        } else {
          setExpression({
            setExpressionAction: (prev: Normalized<BoxedInvocation>) => {
              // Do not inline this variable for type safety. See https://github.com/microsoft/TypeScript/issues/241
              const ret: Normalized<BoxedInvocation> = {
                ...prev,
                "@_id": prev["@_id"] ?? generateUuid(),
                "@_typeRef": u.typeRef,
                "@_label": u.name,
              };

              return ret;
            },
            expressionChangedArgs: { action: Action.ExpressionCreated },
          });
        }
      }
    },
    [id, invocationId, setExpression, expressionHolderId, invocationExpression]
  );

  const headerVisibility = useMemo(
    () => (isNested ? BeeTableHeaderVisibility.SecondToLastLevel : BeeTableHeaderVisibility.AllLevels),
    [isNested]
  );

  const getRowKey = useCallback((row: ReactTable.Row<ROWTYPE>) => {
    return row.id;
  }, []);

  const updateParameter = useCallback(
    (index: number, { expression, variable }: ExpressionWithVariable) => {
      setExpression({
        setExpressionAction: (prev: Normalized<BoxedInvocation>) => {
          const newArgumentEntries = [...(prev.binding ?? [])];
          newArgumentEntries[index] = {
            parameter: variable,
            expression: expression,
          };
          // Do not inline this variable for type safety. See https://github.com/microsoft/TypeScript/issues/241
          const ret: Normalized<BoxedInvocation> = {
            ...prev,
            binding: newArgumentEntries,
          };

          return ret;
        },
        expressionChangedArgs: { action: Action.InvocationParametersChanged },
      });
    },
    [setExpression]
  );

  const cellComponentByColumnAccessor: BeeTableProps<ROWTYPE>["cellComponentByColumnAccessor"] = useMemo(
    () => ({
      parameter: (props) => <ExpressionVariableCell {...props} onExpressionWithVariableUpdated={updateParameter} />,
      expression: (props) => <ArgumentEntryExpressionCell {...props} parentElementId={parentElementId} />,
    }),
    [parentElementId, updateParameter]
  );

  const beeTableOperationConfig = useMemo<BeeTableOperationConfig>(() => {
    return [
      {
        group: i18n.parameters,
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

  const getDefaultArgumentEntry = useCallback(
    (name?: string): Normalized<DMN15__tBinding> => {
      return {
        parameter: {
          "@_id": generateUuid(),
          "@_typeRef": undefined,
          "@_name":
            name ||
            getNextAvailablePrefixedName(
              (invocationExpression.binding ?? []).map((e) => e.parameter["@_name"]),
              "p"
            ),
        },
        expression: undefined!, // SPEC DISCREPANCY: Starting without an expression gives users the ability to select the expression type.,
      };
    },
    [invocationExpression.binding]
  );

  const onRowAdded = useCallback(
    (args: { beforeIndex: number; rowsCount: number }) => {
      const newEntries: Normalized<DMN15__tBinding>[] = [];
      const names = (invocationExpression.binding ?? []).map((e) => e.parameter["@_name"]);
      for (let i = 0; i < args.rowsCount; i++) {
        const name = getNextAvailablePrefixedName(names, "p");
        names.push(name);
        newEntries.push(getDefaultArgumentEntry(name));
      }
      setExpression({
        setExpressionAction: (prev: Normalized<BoxedInvocation>) => {
          const newArgumentEntries = [...(prev.binding ?? [])];

          for (const newEntry of newEntries) {
            newArgumentEntries.splice(args.beforeIndex, 0, newEntry);
          }

          // Do not inline this variable for type safety. See https://github.com/microsoft/TypeScript/issues/241
          const ret: Normalized<BoxedInvocation> = {
            ...prev,
            binding: newArgumentEntries,
          };

          return ret;
        },
        expressionChangedArgs: { action: Action.RowsAdded, rowIndex: args.beforeIndex, rowsCount: args.rowsCount },
      });
    },
    [getDefaultArgumentEntry, invocationExpression.binding, setExpression]
  );

  const onRowDeleted = useCallback(
    (args: { rowIndex: number }) => {
      let oldExpression: Normalized<BoxedExpression> | undefined;
      setExpression({
        setExpressionAction: (prev: Normalized<BoxedInvocation>) => {
          const newArgumentEntries = [...(prev.binding ?? [])];
          oldExpression = newArgumentEntries[args.rowIndex].expression;
          newArgumentEntries.splice(args.rowIndex, 1);
          // Do not inline this variable for type safety. See https://github.com/microsoft/TypeScript/issues/241
          const ret: Normalized<BoxedInvocation> = {
            ...prev,
            binding: newArgumentEntries,
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
        setExpressionAction: (prev: Normalized<BoxedInvocation>) => {
          const newArgumentEntries = [...(prev.binding ?? [])];
          oldExpression = newArgumentEntries[args.rowIndex].expression;
          const defaultArgumentEntry = getDefaultArgumentEntry(newArgumentEntries[args.rowIndex].parameter["@_name"]);
          newArgumentEntries.splice(args.rowIndex, 1, defaultArgumentEntry);
          // Do not inline this variable for type safety. See https://github.com/microsoft/TypeScript/issues/241
          const ret: Normalized<BoxedInvocation> = {
            ...prev,
            binding: newArgumentEntries,
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
    [getDefaultArgumentEntry, setExpression, setWidthsById]
  );

  const allowedOperations = useCallback(
    (conditions: BeeTableContextMenuAllowedOperationsConditions) => {
      if (!conditions.selection.selectionStart || !conditions.selection.selectionEnd) {
        return [];
      }

      return [
        BeeTableOperation.SelectionCopy,
        ...(conditions.selection.selectionStart.rowIndex >= 0
          ? [
              BeeTableOperation.RowInsertAbove,
              BeeTableOperation.RowInsertBelow,
              BeeTableOperation.RowInsertN,
              ...(beeTableRows.length > 1 ? [BeeTableOperation.RowDelete] : []),
              BeeTableOperation.RowReset,
            ]
          : []),
      ];
    },
    [beeTableRows.length]
  );

  return (
    <NestedExpressionContainerContext.Provider value={nestedExpressionContainerValue}>
      <div className={`invocation-expression ${id}`}>
        <BeeTable<ROWTYPE>
          isReadOnly={isReadOnly}
          isEditableHeader={!isReadOnly}
          resizerStopBehavior={ResizerStopBehavior.SET_WIDTH_WHEN_SMALLER}
          tableId={id}
          headerLevelCountForAppendingRowIndexColumn={2}
          headerVisibility={headerVisibility}
          skipLastHeaderGroup={true}
          cellComponentByColumnAccessor={cellComponentByColumnAccessor}
          columns={beeTableColumns}
          rows={beeTableRows}
          onColumnUpdates={onColumnUpdates}
          onColumnResizingWidthChange={onColumnResizingWidthChange}
          operationConfig={beeTableOperationConfig}
          allowedOperations={allowedOperations}
          getRowKey={getRowKey}
          onRowAdded={onRowAdded}
          onRowReset={onRowReset}
          onRowDeleted={onRowDeleted}
          shouldRenderRowIndexColumn={false}
          shouldShowRowsInlineControls={true}
          shouldShowColumnsInlineControls={false}
        />
      </div>
    </NestedExpressionContainerContext.Provider>
  );
}
