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

import * as React from "react";
import _ from "lodash";
import { useCallback, useMemo } from "react";
import * as ReactTable from "react-table";
import {
  BeeTableCellProps,
  BeeTableHeaderVisibility,
  BeeTableOperation,
  BeeTableOperationConfig,
  BeeTableProps,
  DmnBuiltInDataType,
  ExpressionDefinition,
  ExpressionDefinitionLogicType,
  FeelFunctionExpressionDefinition,
  FunctionExpressionDefinition,
  FunctionExpressionDefinitionKind,
  generateUuid,
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
  useBoxedExpressionEditorDispatch,
} from "../BoxedExpressionEditor/BoxedExpressionEditorContext";
import { DEFAULT_EXPRESSION_NAME } from "../ExpressionDefinitionHeaderMenu";
import { useFunctionExpressionControllerCell, useFunctionExpressionParametersColumnHeader } from "./FunctionExpression";
import { ExpressionContainer } from "../ExpressionDefinitionRoot/ExpressionContainer";

export type FEEL_ROWTYPE = { functionExpression: FunctionExpressionDefinition };

export function FeelFunctionExpression({
  functionExpression,
}: {
  functionExpression: FeelFunctionExpressionDefinition & { isNested: boolean };
}) {
  const { i18n } = useBoxedExpressionEditorI18n();
  const { setExpression } = useBoxedExpressionEditorDispatch();

  const parametersColumnHeader = useFunctionExpressionParametersColumnHeader(functionExpression.formalParameters);

  const beeTableColumns = useMemo<ReactTable.Column<FEEL_ROWTYPE>[]>(() => {
    return [
      {
        label: functionExpression.name ?? DEFAULT_EXPRESSION_NAME,
        accessor: functionExpression.id as any, // FIXME: Tiago -> ?
        dataType: functionExpression.dataType ?? DmnBuiltInDataType.Undefined,
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
  }, [functionExpression.dataType, functionExpression.name, parametersColumnHeader]);

  const headerVisibility = useMemo(() => {
    return functionExpression.isNested ? BeeTableHeaderVisibility.LastLevel : BeeTableHeaderVisibility.AllLevels;
  }, [functionExpression.isNested]);

  const onColumnUpdates = useCallback(
    ([{ name, dataType }]: BeeTableColumnUpdate<FEEL_ROWTYPE>[]) => {
      setExpression((prev) => ({
        ...prev,
        name,
        dataType,
      }));
    },
    [setExpression]
  );

  const beeTableOperationConfig = useMemo<BeeTableOperationConfig>(() => {
    return [
      {
        group: _.upperCase(i18n.function),
        items: [{ name: i18n.rowOperations.reset, type: BeeTableOperation.RowReset }],
      },
    ];
  }, [i18n]);

  const beeTableRows = useMemo(() => {
    return [{ functionExpression }];
  }, [functionExpression]);

  const controllerCell = useFunctionExpressionControllerCell(FunctionExpressionDefinitionKind.Feel);

  const cellComponentByColumnAccessor: BeeTableProps<FEEL_ROWTYPE>["cellComponentByColumnAccessor"] = useMemo(
    () => ({ parameters: (props) => <FeelFunctionImplementationCell {...props} /> }),
    []
  );

  const getRowKey = useCallback((r: ReactTable.Row<FEEL_ROWTYPE>) => {
    return r.original.functionExpression.id;
  }, []);

  const onRowReset = useCallback(() => {
    setExpression((prev) => {
      return {
        ...prev,
        expression: {
          id: generateUuid(),
          logicType: ExpressionDefinitionLogicType.Undefined,
          dataType: DmnBuiltInDataType.Undefined,
        },
      };
    });
  }, [setExpression]);

  /// //////////////////////////////////////////////////////
  /// ///////////// RESIZING WIDTHS ////////////////////////
  /// //////////////////////////////////////////////////////

  const { nestedExpressionContainerValue, onColumnResizingWidthChange } =
    useNestedExpressionContainerWithNestedExpressions(
      useMemo(() => {
        return {
          nestedExpressions: [functionExpression.expression],
          fixedColumnActualWidth: 0,
          fixedColumnResizingWidth: { value: 0, isPivoting: false },
          fixedColumnMinWidth: 0,
          nestedExpressionMinWidth: CONTEXT_ENTRY_EXPRESSION_MIN_WIDTH,
          extraWidth: FEEL_FUNCTION_EXPRESSION_EXTRA_WIDTH,
          expression: functionExpression,
          flexibleColumnIndex: 1,
        };
      }, [functionExpression])
    );

  /// //////////////////////////////////////////////////////

  return (
    <NestedExpressionContainerContext.Provider value={nestedExpressionContainerValue}>
      <div className={`function-expression ${functionExpression.id}`}>
        <BeeTable
          onColumnResizingWidthChange={onColumnResizingWidthChange}
          resizerStopBehavior={ResizerStopBehavior.SET_WIDTH_WHEN_SMALLER}
          operationConfig={beeTableOperationConfig}
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

export function FeelFunctionImplementationCell({ data, rowIndex, columnIndex }: BeeTableCellProps<FEEL_ROWTYPE>) {
  const functionExpression = data[rowIndex].functionExpression as FeelFunctionExpressionDefinition;

  const { setExpression } = useBoxedExpressionEditorDispatch();

  const onSetExpression = useCallback(
    ({ getNewExpression }: { getNewExpression: (prev: ExpressionDefinition) => ExpressionDefinition }) => {
      setExpression((prev: FeelFunctionExpressionDefinition) => ({
        ...prev,
        expression: getNewExpression(prev.expression),
      }));
    },
    [setExpression]
  );

  return (
    <NestedExpressionDispatchContextProvider onSetExpression={onSetExpression}>
      <ExpressionContainer
        expression={functionExpression.expression}
        isResetSupported={true}
        isNested={true}
        rowIndex={rowIndex}
        columnIndex={columnIndex}
      />
    </NestedExpressionDispatchContextProvider>
  );
}
