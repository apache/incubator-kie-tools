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
  Action,
  BeeTableCellProps,
  BeeTableContextMenuAllowedOperationsConditions,
  BeeTableHeaderVisibility,
  BeeTableOperation,
  BeeTableOperationConfig,
  BeeTableProps,
  BoxedExpression,
  BoxedFunction,
  BoxedFunctionKind,
  DmnBuiltInDataType,
  ExpressionChangedArgs,
  Normalized,
} from "../../api";
import { useBoxedExpressionEditorI18n } from "../../i18n";
import { useNestedExpressionContainerWithNestedExpressions } from "../../resizing/Hooks";
import { NestedExpressionContainerContext } from "../../resizing/NestedExpressionContainerContext";
import { ResizerStopBehavior } from "../../resizing/ResizingWidthsContext";
import {
  FEEL_FUNCTION_EXPRESSION_EXTRA_WIDTH,
  FEEL_FUNCTION_EXPRESSION_MIN_WIDTH,
} from "../../resizing/WidthConstants";
import { BeeTable, BeeTableColumnUpdate } from "../../table/BeeTable";
import {
  NestedExpressionDispatchContextProvider,
  OnSetExpression,
  useBoxedExpressionEditor,
  useBoxedExpressionEditorDispatch,
} from "../../BoxedExpressionEditorContext";
import { DEFAULT_EXPRESSION_VARIABLE_NAME } from "../../expressionVariable/ExpressionVariableMenu";
import { useFunctionExpressionControllerCell, useFunctionExpressionParametersColumnHeader } from "./FunctionExpression";
import { ExpressionContainer } from "../ExpressionDefinitionRoot/ExpressionContainer";
import { DMN15__tFunctionDefinition } from "@kie-tools/dmn-marshaller/dist/schemas/dmn-1_5/ts-gen/types";
import { findAllIdsDeep } from "../../ids/ids";

export type FEEL_ROWTYPE = { functionExpression: Normalized<BoxedFunction> };

export type BoxedFunctionFeel = DMN15__tFunctionDefinition & {
  "@_kind": "FEEL";
  __$$element: "functionDefinition";
};

export function FeelFunctionExpression({
  isNested,
  parentElementId,
  functionExpression,
}: {
  functionExpression: Normalized<BoxedFunctionFeel>;
  isNested: boolean;
  parentElementId: string;
}) {
  const { i18n } = useBoxedExpressionEditorI18n();
  const { expressionHolderId, widthsById, isReadOnly } = useBoxedExpressionEditor();
  const { setExpression, setWidthsById } = useBoxedExpressionEditorDispatch();

  const parametersColumnHeader = useFunctionExpressionParametersColumnHeader(
    functionExpression.formalParameter,
    isReadOnly ?? false
  );
  const parametersId = useMemo(
    () => (functionExpression["@_id"] ? `${functionExpression["@_id"]}-parameters` : "parameters"),
    [functionExpression]
  );

  const beeTableColumns = useMemo<ReactTable.Column<FEEL_ROWTYPE>[]>(() => {
    return [
      {
        accessor: expressionHolderId as any, // FIXME: https://github.com/apache/incubator-kie-issues/issues/169
        label: functionExpression["@_label"] ?? DEFAULT_EXPRESSION_VARIABLE_NAME,
        dataType: functionExpression["@_typeRef"] ?? DmnBuiltInDataType.Undefined,
        isRowIndexColumn: false,
        width: undefined,
        columns: [
          {
            headerCellElement: parametersColumnHeader,
            accessor: parametersId as any,
            label: "parameters",
            isRowIndexColumn: false,
            dataType: undefined as any,
            width: undefined,
          },
        ],
      },
    ];
  }, [expressionHolderId, functionExpression, parametersColumnHeader, parametersId]);

  const headerVisibility = useMemo(() => {
    return isNested ? BeeTableHeaderVisibility.LastLevel : BeeTableHeaderVisibility.AllLevels;
  }, [isNested]);

  const onColumnUpdates = useCallback(
    ([{ name, typeRef }]: BeeTableColumnUpdate<FEEL_ROWTYPE>[]) => {
      const expressionChangedArgs: ExpressionChangedArgs = {
        action: Action.VariableChanged,
        variableUuid: expressionHolderId,
        typeChange:
          typeRef !== functionExpression["@_typeRef"]
            ? {
                from: functionExpression["@_typeRef"] ?? "",
                to: typeRef,
              }
            : undefined,
        nameChange:
          name !== functionExpression["@_label"]
            ? {
                from: functionExpression["@_label"] ?? "",
                to: name,
              }
            : undefined,
      };
      setExpression({
        setExpressionAction: (prev: Normalized<BoxedFunctionFeel>) => {
          // Do not inline this variable for type safety. See https://github.com/microsoft/TypeScript/issues/241
          const ret: Normalized<BoxedFunctionFeel> = {
            ...prev,
            "@_label": name,
            "@_typeRef": typeRef,
          };

          return ret;
        },
        expressionChangedArgs,
      });
    },
    [expressionHolderId, functionExpression, setExpression]
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

  const controllerCell = useFunctionExpressionControllerCell(BoxedFunctionKind.Feel);

  const cellComponentByColumnAccessor: BeeTableProps<FEEL_ROWTYPE>["cellComponentByColumnAccessor"] = useMemo(() => {
    return {
      [`${parametersId}`]: (props) => <FeelFunctionImplementationCell {...props} parentElementId={parentElementId} />,
    };
  }, [parentElementId, parametersId]);

  const getRowKey = useCallback((r: ReactTable.Row<FEEL_ROWTYPE>) => {
    return r.id;
  }, []);

  const onRowReset = useCallback(() => {
    let oldExpression: Normalized<BoxedExpression> | undefined;
    setExpression({
      setExpressionAction: (prev: Normalized<BoxedFunctionFeel>) => {
        oldExpression = prev.expression;
        return undefined!; // SPEC DISCREPANCY
      },
      expressionChangedArgs: { action: Action.RowReset, rowIndex: 0 },
    });
    setWidthsById(({ newMap }) => {
      for (const id of findAllIdsDeep(oldExpression)) {
        newMap.delete(id);
      }
    });
  }, [setExpression, setWidthsById]);

  /// //////////////////////////////////////////////////////
  /// ///////////// RESIZING WIDTHS ////////////////////////
  /// //////////////////////////////////////////////////////

  const { nestedExpressionContainerValue, onColumnResizingWidthChange } =
    useNestedExpressionContainerWithNestedExpressions(
      useMemo(() => {
        const nestedExpressions = [functionExpression.expression ?? undefined!];

        return {
          nestedExpressions: nestedExpressions,
          fixedColumnActualWidth: 0,
          fixedColumnResizingWidth: { value: 0, isPivoting: false },
          fixedColumnMinWidth: 0,
          nestedExpressionMinWidth: FEEL_FUNCTION_EXPRESSION_MIN_WIDTH,
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
          isReadOnly={isReadOnly}
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

  const onSetExpression = useCallback<OnSetExpression>(
    ({
      getNewExpression,
      expressionChangedArgs,
    }: {
      getNewExpression: (prev: Normalized<BoxedExpression>) => Normalized<BoxedExpression>;
      expressionChangedArgs: ExpressionChangedArgs;
    }) => {
      setExpression({
        setExpressionAction: (prev: Normalized<BoxedFunctionFeel>) => {
          // Do not inline this variable for type safety. See https://github.com/microsoft/TypeScript/issues/241
          const ret: Normalized<BoxedFunctionFeel> = {
            ...prev,
            expression: getNewExpression(prev.expression ?? undefined!),
          };

          return ret;
        },
        expressionChangedArgs,
      });
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
        parentElementTypeRef={functionExpression["@_typeRef"]}
        parentElementName={"Return"}
      />
    </NestedExpressionDispatchContextProvider>
  );
}
