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
  BeeTableCellProps,
  BeeTableContextMenuAllowedOperationsConditions,
  BeeTableHeaderVisibility,
  BeeTableOperation,
  BeeTableOperationConfig,
  BeeTableProps,
  ExpressionDefinition,
  FunctionExpressionDefinition,
  FunctionExpressionDefinitionKind,
} from "../../api";
import { useBoxedExpressionEditorI18n } from "../../i18n";
import { useNestedExpressionContainerWithNestedExpressions } from "../../resizing/Hooks";
import { NestedExpressionContainerContext } from "../../resizing/NestedExpressionContainerContext";
import { ResizerStopBehavior } from "../../resizing/ResizingWidthsContext";
import {
  CONTEXT_ENTRY_EXPRESSION_MIN_WIDTH,
  FEEL_FUNCTION_EXPRESSION_EXTRA_WIDTH,
} from "../../resizing/WidthConstants";
import { BeeTable, BeeTableColumnUpdate } from "../../table/BeeTable";
import {
  NestedExpressionDispatchContextProvider,
  useBoxedExpressionEditor,
  useBoxedExpressionEditorDispatch,
} from "../BoxedExpressionEditor/BoxedExpressionEditorContext";
import { DEFAULT_EXPRESSION_NAME } from "../ExpressionDefinitionHeaderMenu";
import { useFunctionExpressionControllerCell, useFunctionExpressionParametersColumnHeader } from "./FunctionExpression";
import { ExpressionContainer } from "../ExpressionDefinitionRoot/ExpressionContainer";
import { DMN15__tFunctionDefinition } from "@kie-tools/dmn-marshaller/dist/schemas/dmn-1_5/ts-gen/types";

export type FEEL_ROWTYPE = { functionExpression: FunctionExpressionDefinition };

export type FeelFunctionExpressionDefinition = DMN15__tFunctionDefinition & {
  "@_kind": "FEEL";
  __$$element: "functionDefinition";
  isNested: boolean;
  parentElementId: string;
};

export function FeelFunctionExpression({
  functionExpression,
}: {
  functionExpression: FeelFunctionExpressionDefinition;
}) {
  const { i18n } = useBoxedExpressionEditorI18n();
  const { expressionHolderId, widthsById } = useBoxedExpressionEditor();
  const { setExpression } = useBoxedExpressionEditorDispatch();

  const parametersColumnHeader = useFunctionExpressionParametersColumnHeader(functionExpression.formalParameter);

  const beeTableColumns = useMemo<ReactTable.Column<FEEL_ROWTYPE>[]>(() => {
    return [
      {
        label: functionExpression["@_label"] ?? DEFAULT_EXPRESSION_NAME,
        accessor: expressionHolderId as any, // FIXME: https://github.com/kiegroup/kie-issues/issues/169
        dataType: functionExpression["@_typeRef"] ?? "<Undefined>",
        isRowIndexColumn: false,
        width: undefined,
        columns: [
          {
            headerCellElement: parametersColumnHeader,
            accessor: "parameters" as any,
            label: "parameters",
            isRowIndexColumn: false,
            dataType: undefined as any,
            width: undefined,
          },
        ],
      },
    ];
  }, [expressionHolderId, functionExpression, parametersColumnHeader]);

  const headerVisibility = useMemo(() => {
    return functionExpression.isNested ? BeeTableHeaderVisibility.LastLevel : BeeTableHeaderVisibility.AllLevels;
  }, [functionExpression.isNested]);

  const onColumnUpdates = useCallback(
    ([{ name, dataType }]: BeeTableColumnUpdate<FEEL_ROWTYPE>[]) => {
      setExpression((prev) => ({
        ...prev,
        "@_label": name,
        "@_typeRef": dataType,
      }));
    },
    [setExpression]
  );

  const beeTableOperationConfig = useMemo<BeeTableOperationConfig>(() => {
    return [
      {
        group: i18n.terms.selection.toUpperCase(),
        items: [{ name: i18n.terms.copy, type: BeeTableOperation.SelectionCopy }],
      },
      {
        group: i18n.function.toUpperCase(),
        items: [{ name: i18n.rowOperations.reset, type: BeeTableOperation.RowReset }],
      },
    ];
  }, [i18n]);

  const beeTableRows = useMemo(() => {
    return [{ functionExpression }];
  }, [functionExpression]);

  const controllerCell = useFunctionExpressionControllerCell(FunctionExpressionDefinitionKind.Feel);

  const cellComponentByColumnAccessor: BeeTableProps<FEEL_ROWTYPE>["cellComponentByColumnAccessor"] = useMemo(() => {
    return {
      parameters: (props) => (
        <FeelFunctionImplementationCell {...props} parentElementId={functionExpression.parentElementId} />
      ),
    };
  }, [functionExpression.parentElementId]);

  const getRowKey = useCallback((r: ReactTable.Row<FEEL_ROWTYPE>) => {
    return r.id;
  }, []);

  const onRowReset = useCallback(() => {
    setExpression(() => {
      return undefined as any;
    });
  }, [setExpression]);

  /// //////////////////////////////////////////////////////
  /// ///////////// RESIZING WIDTHS ////////////////////////
  /// //////////////////////////////////////////////////////

  const { nestedExpressionContainerValue, onColumnResizingWidthChange } =
    useNestedExpressionContainerWithNestedExpressions(
      useMemo(() => {
        return {
          nestedExpressions: [functionExpression.expression ?? undefined!],
          fixedColumnActualWidth: 0,
          fixedColumnResizingWidth: { value: 0, isPivoting: false },
          fixedColumnMinWidth: 0,
          nestedExpressionMinWidth: CONTEXT_ENTRY_EXPRESSION_MIN_WIDTH,
          extraWidth: FEEL_FUNCTION_EXPRESSION_EXTRA_WIDTH,
          expression: functionExpression,
          flexibleColumnIndex: 1,
          widthsById: widthsById,
        };
      }, [functionExpression, widthsById])
    );

  const allowedOperations = useCallback((conditions: BeeTableContextMenuAllowedOperationsConditions) => {
    if (!conditions.selection.selectionStart || !conditions.selection.selectionEnd) {
      return [];
    }

    return [
      BeeTableOperation.SelectionCopy,
      ...(conditions.selection.selectionStart.rowIndex >= 0 ? [BeeTableOperation.RowReset] : []),
    ];
  }, []);

  /// //////////////////////////////////////////////////////

  return (
    <NestedExpressionContainerContext.Provider value={nestedExpressionContainerValue}>
      <div className={`function-expression ${functionExpression["@_id"]}`}>
        <BeeTable<FEEL_ROWTYPE>
          onColumnResizingWidthChange={onColumnResizingWidthChange}
          resizerStopBehavior={ResizerStopBehavior.SET_WIDTH_WHEN_SMALLER}
          operationConfig={beeTableOperationConfig}
          allowedOperations={allowedOperations}
          onColumnUpdates={onColumnUpdates}
          getRowKey={getRowKey}
          onRowReset={onRowReset}
          columns={beeTableColumns}
          rows={beeTableRows}
          headerLevelCountForAppendingRowIndexColumn={1}
          headerVisibility={headerVisibility}
          controllerCell={controllerCell}
          cellComponentByColumnAccessor={cellComponentByColumnAccessor}
          shouldRenderRowIndexColumn={true}
          shouldShowRowsInlineControls={false}
          shouldShowColumnsInlineControls={false}
        />
      </div>
    </NestedExpressionContainerContext.Provider>
  );
}

export function FeelFunctionImplementationCell({
  data,
  rowIndex,
  columnIndex,
  parentElementId,
}: BeeTableCellProps<FEEL_ROWTYPE> & { parentElementId: string }) {
  const functionExpression = data[rowIndex].functionExpression;

  const { setExpression } = useBoxedExpressionEditorDispatch();

  const onSetExpression = useCallback(
    ({ getNewExpression }: { getNewExpression: (prev: ExpressionDefinition) => ExpressionDefinition }) => {
      setExpression((prev: FeelFunctionExpressionDefinition) => ({
        ...prev,
        expression: getNewExpression(prev.expression ?? undefined!),
      }));
    },
    [setExpression]
  );

  return (
    <NestedExpressionDispatchContextProvider onSetExpression={onSetExpression}>
      <ExpressionContainer
        expression={functionExpression.expression ?? undefined!}
        isResetSupported={true}
        isNested={true}
        rowIndex={rowIndex}
        columnIndex={columnIndex}
        parentElementId={parentElementId}
      />
    </NestedExpressionDispatchContextProvider>
  );
}
