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
import { useCallback, useMemo } from "react";
import * as ReactTable from "react-table";
import {
  BeeTableContextMenuAllowedOperationsConditions,
  BeeTableHeaderVisibility,
  BeeTableOperation,
  BeeTableOperationConfig,
  BeeTableProps,
  DmnBuiltInDataType,
  ExpressionDefinition,
  generateUuid,
  getNextAvailablePrefixedName,
  InsertRowColumnsDirection,
  InvocationExpressionDefinition,
} from "../../api";
import { useBoxedExpressionEditorI18n } from "../../i18n";
import { NestedExpressionContainerContext } from "../../resizing/NestedExpressionContainerContext";
import { ResizerStopBehavior, ResizingWidth } from "../../resizing/ResizingWidthsContext";
import {
  CONTEXT_ENTRY_INFO_MIN_WIDTH,
  INVOCATION_PARAMETER_MIN_WIDTH,
  INVOCATION_ARGUMENT_EXPRESSION_MIN_WIDTH,
  INVOCATION_EXTRA_WIDTH,
} from "../../resizing/WidthConstants";
import { BeeTable, BeeTableColumnUpdate } from "../../table/BeeTable";
import {
  useBoxedExpressionEditor,
  useBoxedExpressionEditorDispatch,
} from "../BoxedExpressionEditor/BoxedExpressionEditorContext";
import { useNestedExpressionContainerWithNestedExpressions } from "../../resizing/Hooks";
import { ArgumentEntryExpressionCell } from "./ArgumentEntryExpressionCell";
import { ContextEntryInfoCell, Entry } from "../ContextExpression";
import "./InvocationExpression.css";
import { DEFAULT_EXPRESSION_NAME } from "../ExpressionDefinitionHeaderMenu";
import { getExpressionTotalMinWidth } from "../../resizing/WidthMaths";
import { DMN15__tBinding } from "@kie-tools/dmn-marshaller/dist/schemas/dmn-1_5/ts-gen/types";

type ROWTYPE = DMN15__tBinding;

export const INVOCATION_EXPRESSION_DEFAULT_PARAMETER_NAME = "p-1";
export const INVOCATION_EXPRESSION_DEFAULT_PARAMETER_DATA_TYPE = DmnBuiltInDataType.Undefined;

export const INVOCATION_PARAMETER_INFO_WIDTH_INDEX = 0;

export function InvocationExpression(
  invocationExpression: InvocationExpressionDefinition & {
    isNested: boolean;
    parentElementId: string;
  }
) {
  const { i18n } = useBoxedExpressionEditorI18n();
  const { expressionHolderId, variables, widthsById } = useBoxedExpressionEditor();
  const { setExpression, setWidth } = useBoxedExpressionEditorDispatch();

  const id = invocationExpression["@_id"]!;

  const widths = useMemo(() => {
    const expressionWidths = widthsById.get(id) ?? [];
    if (expressionWidths.length === 0) {
      expressionWidths.push(INVOCATION_PARAMETER_MIN_WIDTH, INVOCATION_ARGUMENT_EXPRESSION_MIN_WIDTH);
    }
    return expressionWidths;
  }, [id, widthsById]);

  const parametersWidth = useMemo(
    () => widths?.[INVOCATION_PARAMETER_INFO_WIDTH_INDEX] ?? INVOCATION_PARAMETER_MIN_WIDTH,
    [widths]
  );

  const setParametersWidth = useCallback(
    (newWidthAction: React.SetStateAction<number | undefined>) => {
      const newWidth = typeof newWidthAction === "function" ? newWidthAction(parametersWidth) : newWidthAction;

      if (newWidth) {
        const values = [...widths];
        values.splice(INVOCATION_PARAMETER_INFO_WIDTH_INDEX, 1, newWidth);
        setWidth({ id, values });
      }
    },
    [id, parametersWidth, setWidth, widths]
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

  /// //////////////////////////////////////////////////////
  /// ///////////// RESIZING WIDTHS ////////////////////////
  /// //////////////////////////////////////////////////////

  const { nestedExpressionContainerValue, onColumnResizingWidthChange: onColumnResizingWidthChange2 } =
    useNestedExpressionContainerWithNestedExpressions(
      useMemo(() => {
        const entriesWidths =
          invocationExpression.binding?.map((e) =>
            getExpressionTotalMinWidth(0, e.expression as ExpressionDefinition, widthsById)
          ) ?? [];

        const maxNestedExpressionWidth = Math.max(...entriesWidths, INVOCATION_ARGUMENT_EXPRESSION_MIN_WIDTH);

        const nestedExpressions = invocationExpression.binding?.map((b) => b.expression as ExpressionDefinition);

        return {
          nestedExpressions: nestedExpressions ?? [],
          fixedColumnActualWidth: parametersWidth,
          fixedColumnResizingWidth: parametersResizingWidth,
          fixedColumnMinWidth: INVOCATION_PARAMETER_MIN_WIDTH,
          nestedExpressionMinWidth: maxNestedExpressionWidth,
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

  const beeTableRows: ROWTYPE[] = useMemo(() => {
    return invocationExpression.binding ?? [];
  }, [invocationExpression.binding]);

  const beeTableColumns = useMemo<ReactTable.Column<ROWTYPE>[]>(
    () => [
      {
        accessor: expressionHolderId as any, // FIXME: https://github.com/kiegroup/kie-issues/issues/169,
        label: invocationExpression["@_label"] ?? DEFAULT_EXPRESSION_NAME,
        dataType: invocationExpression["@_typeRef"] ?? "<Undefined>",
        isRowIndexColumn: false,
        width: undefined,
        columns: [
          {
            accessor: "functionName" as keyof ROWTYPE,
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
                dataType: INVOCATION_EXPRESSION_DEFAULT_PARAMETER_DATA_TYPE,
                isWidthPinned: true,
                minWidth: CONTEXT_ENTRY_INFO_MIN_WIDTH,
                width: parametersWidth,
                setWidth: setParametersWidth,
              },
              {
                accessor: "expression" as any,
                label: "expression",
                isRowIndexColumn: false,
                dataType: INVOCATION_EXPRESSION_DEFAULT_PARAMETER_DATA_TYPE,
                minWidth: INVOCATION_ARGUMENT_EXPRESSION_MIN_WIDTH,
                width: undefined,
              },
            ],
          },
        ],
      },
    ],
    [expressionHolderId, invocationExpression, parametersWidth, setParametersWidth]
  );

  const onColumnUpdates = useCallback(
    (columnUpdates: BeeTableColumnUpdate<ROWTYPE>[]) => {
      for (const u of columnUpdates) {
        if (u.column.originalId === "functionName") {
          setExpression((prev: InvocationExpressionDefinition) => ({
            ...prev,
            expression: {
              __$$element: "literalExpression",
              text: {
                __$$text: u.name,
              },
            },
          }));
        } else {
          setExpression((prev: InvocationExpressionDefinition) => ({
            ...prev,
            "@_typeRef": u.dataType,
            "@_label": u.name,
          }));
        }
      }
    },
    [setExpression]
  );

  const headerVisibility = useMemo(
    () =>
      invocationExpression.isNested ? BeeTableHeaderVisibility.SecondToLastLevel : BeeTableHeaderVisibility.AllLevels,
    [invocationExpression.isNested]
  );

  const getRowKey = useCallback((row: ReactTable.Row<ROWTYPE>) => {
    return row.id;
  }, []);

  const updateEntry = useCallback(
    (rowIndex: number, newArgumentEntry: Entry) => {
      setExpression((prev: InvocationExpressionDefinition) => {
        const newArgumentEntries = [...(prev.binding ?? [])];
        newArgumentEntries[rowIndex] = {
          parameter: newArgumentEntry.variable,
          expression: newArgumentEntry.expression,
        };
        return { ...prev, binding: newArgumentEntries };
      });
    },
    [setExpression]
  );

  const onDataUpdate = useCallback(
    (data: DMN15__tBinding[]) => {
      setExpression((prev: InvocationExpressionDefinition) => {
        return { ...prev, binding: data };
      });
    },
    [setExpression]
  );

  const cellComponentByColumnAccessor: BeeTableProps<ROWTYPE>["cellComponentByColumnAccessor"] = useMemo(
    () => ({
      parameter: (props) => (
        <ContextEntryInfoCell
          {...props}
          data={props.data.map((e) => {
            return { variable: e.parameter, expression: e.expression };
          })}
          onEntryUpdate={updateEntry}
        />
      ),
      expression: (props) => (
        <ArgumentEntryExpressionCell
          {...props}
          data={props.data}
          parentElementId={invocationExpression.parentElementId}
          onDataUpdate={onDataUpdate}
        />
      ),
    }),
    [invocationExpression.parentElementId, onDataUpdate, updateEntry]
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
    (name?: string): DMN15__tBinding => {
      return {
        parameter: {
          "@_id": generateUuid(),
          "@_typeRef": DmnBuiltInDataType.Undefined,
          "@_name":
            name ||
            getNextAvailablePrefixedName(
              (invocationExpression.binding ?? []).map((e) => e.parameter["@_name"]),
              "p"
            ),
        },
        expression: undefined as any, // SPEC DISCREPANCY: Starting without an expression gives users the ability to select the expression type.,
      };
    },
    [invocationExpression.binding]
  );

  const onRowAdded = useCallback(
    (args: { beforeIndex: number; rowsCount: number; insertDirection: InsertRowColumnsDirection }) => {
      const newEntries: DMN15__tBinding[] = [];
      const names = (invocationExpression.binding ?? []).map((e) => e.parameter["@_name"]);
      for (let i = 0; i < args.rowsCount; i++) {
        const name = getNextAvailablePrefixedName(names, "p");
        names.push(name);
        newEntries.push(getDefaultArgumentEntry(name));
      }
      setExpression((prev: InvocationExpressionDefinition) => {
        const newArgumentEntries = [...(prev.binding ?? [])];

        for (const newEntry of newEntries) {
          let index = args.beforeIndex;
          newArgumentEntries.splice(index, 0, newEntry);
          if (args.insertDirection === InsertRowColumnsDirection.AboveOrRight) {
            index++;
          }
        }

        return {
          ...prev,
          binding: newArgumentEntries,
        };
      });
    },
    [getDefaultArgumentEntry, invocationExpression.binding, setExpression]
  );

  const onRowDeleted = useCallback(
    (args: { rowIndex: number }) => {
      setExpression((prev: InvocationExpressionDefinition) => {
        const newArgumentEntries = [...(prev.binding ?? [])];
        newArgumentEntries.splice(args.rowIndex, 1);
        return {
          ...prev,
          binding: newArgumentEntries,
        };
      });
    },
    [setExpression]
  );

  const onRowReset = useCallback(
    (args: { rowIndex: number }) => {
      setExpression((prev: InvocationExpressionDefinition) => {
        const newArgumentEntries = [...(prev.binding ?? [])];
        newArgumentEntries.splice(
          args.rowIndex,
          1,
          getDefaultArgumentEntry(newArgumentEntries[args.rowIndex].parameter["@_name"])
        );
        return {
          ...prev,
          binding: newArgumentEntries,
        };
      });
    },
    [getDefaultArgumentEntry, setExpression]
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
          variables={variables}
          widthsById={widthsById}
        />
      </div>
    </NestedExpressionContainerContext.Provider>
  );
}
