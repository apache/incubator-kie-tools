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

import {
  BeeTableHeaderVisibility,
  BeeTableOperation,
  BeeTableOperationConfig,
  BeeTableProps,
  DmnBuiltInDataType,
  ExpressionDefinitionLogicType,
  FunctionExpressionDefinition,
  FunctionExpressionDefinitionKind,
  generateUuid,
} from "../../api";
import { FeelFunctionImplementationCell } from "./FeelFunctionImplementationCell";
import "./FunctionExpression.css";
import { JavaFunctionBindingCell } from "./JavaFunctionBindingCell";
import { PmmlFunctionBindingCell } from "./PmmlFunctionBindingCell";
import * as React from "react";
import _ from "lodash";
import { PopoverMenu } from "../../contextMenu/PopoverMenu";
import { useBoxedExpressionEditorI18n } from "../../i18n";
import { BeeTableColumnUpdate, BeeTable } from "../../table/BeeTable";
import {
  useBoxedExpressionEditorDispatch,
  useBoxedExpressionEditor,
} from "../BoxedExpressionEditor/BoxedExpressionEditorContext";
import { getDefaultExpressionDefinitionByLogicType } from "../defaultExpression";
import { DEFAULT_EXPRESSION_NAME } from "../ExpressionDefinitionHeaderMenu";
import { FunctionKindSelector } from "./FunctionKindSelector";
import { ParametersPopover } from "./ParametersPopover";
import * as ReactTable from "react-table";
import { JAVA_FUNCTION_EXPRESSION_VALUES_MIN_WIDTH } from "../../resizing/WidthConstants";
import { ResizerStopBehavior } from "../../resizing/ResizingWidthsContext";

export const DEFAULT_FIRST_PARAM_NAME = "p-1";

export type ROWTYPE = { functionExpression: FunctionExpressionDefinition };

export function FunctionExpression(functionExpression: FunctionExpressionDefinition & { isNested: boolean }) {
  const { i18n } = useBoxedExpressionEditorI18n();
  const { setExpression } = useBoxedExpressionEditorDispatch();

  const { editorRef, decisionNodeId } = useBoxedExpressionEditor();

  const parametersColumnHeader = React.useMemo(
    () => (
      <PopoverMenu
        appendTo={() => editorRef.current!}
        className="parameters-editor-popover"
        minWidth="400px"
        body={<ParametersPopover parameters={functionExpression.formalParameters} />}
      >
        <div className={`parameters-list ${_.isEmpty(functionExpression.formalParameters) ? "empty-parameters" : ""}`}>
          <p className="pf-u-text-truncate">
            {_.isEmpty(functionExpression.formalParameters) ? (
              i18n.editParameters
            ) : (
              <>
                <span>{"("}</span>
                {functionExpression.formalParameters.map((parameter, i) => (
                  <React.Fragment key={i}>
                    <span>{parameter.name}</span>
                    <span>{": "}</span>
                    <span className={"expression-info-data-type"}>({parameter.dataType})</span>
                    {i < functionExpression.formalParameters.length - 1 && <span>{", "}</span>}
                  </React.Fragment>
                ))}
                <span>{")"}</span>
              </>
            )}
          </p>
        </div>
      </PopoverMenu>
    ),
    [functionExpression.formalParameters, i18n.editParameters, editorRef]
  );

  const beeTableColumns = React.useMemo<ReactTable.Column<ROWTYPE>[]>(() => {
    return [
      {
        label: functionExpression.name ?? DEFAULT_EXPRESSION_NAME,
        accessor: decisionNodeId as any,
        dataType: functionExpression.dataType ?? DmnBuiltInDataType.Undefined,
        isRowIndexColumn: false,
        width: undefined,
        columns: [
          {
            headerCellElement: parametersColumnHeader,
            accessor: "parameters" as any,
            label: "",
            isRowIndexColumn: false,
            dataType: undefined as any,
            width: undefined,
          },
        ],
      },
    ];
  }, [decisionNodeId, functionExpression.dataType, functionExpression.name, parametersColumnHeader]);

  const headerVisibility = React.useMemo(() => {
    return functionExpression.isNested ? BeeTableHeaderVisibility.LastLevel : BeeTableHeaderVisibility.AllLevels;
  }, [functionExpression.isNested]);

  const onFunctionKindSelect = React.useCallback(
    (kind: string) => {
      setExpression((prev) => {
        if (kind === FunctionExpressionDefinitionKind.Feel) {
          return getDefaultExpressionDefinitionByLogicType(ExpressionDefinitionLogicType.Function, {
            id: prev.id ?? generateUuid(),
            name: prev.name,
            dataType: DmnBuiltInDataType.Undefined,
          });
        } else if (kind === FunctionExpressionDefinitionKind.Java) {
          return {
            name: prev.name,
            id: prev.id ?? generateUuid(),
            logicType: ExpressionDefinitionLogicType.Function,
            functionKind: FunctionExpressionDefinitionKind.Java,
            dataType: DmnBuiltInDataType.Undefined,
            classAndMethodNamesWidth: JAVA_FUNCTION_EXPRESSION_VALUES_MIN_WIDTH,
            formalParameters: [],
          };
        } else if (kind === FunctionExpressionDefinitionKind.Pmml) {
          return {
            name: prev.name,
            id: prev.id ?? generateUuid(),
            logicType: ExpressionDefinitionLogicType.Function,
            functionKind: FunctionExpressionDefinitionKind.Pmml,
            dataType: DmnBuiltInDataType.Undefined,
            formalParameters: [],
          };
        } else {
          throw new Error("Shouldn't ever reach this point.");
        }
      });
    },
    [setExpression]
  );

  const onColumnUpdates = React.useCallback(
    ([{ name, dataType }]: BeeTableColumnUpdate<ROWTYPE>[]) => {
      setExpression((prev) => ({
        ...prev,
        name,
        dataType,
      }));
    },
    [setExpression]
  );

  const beeTableOperationConfig = React.useMemo<BeeTableOperationConfig>(() => {
    return [
      {
        group: _.upperCase(i18n.function),
        items:
          functionExpression.functionKind === FunctionExpressionDefinitionKind.Feel
            ? [
                {
                  name: i18n.rowOperations.reset,
                  type: BeeTableOperation.RowReset,
                },
              ]
            : [],
      },
    ];
  }, [functionExpression.functionKind, i18n]);

  const beeTableRows = React.useMemo(() => {
    return [
      {
        functionExpression: functionExpression,
      },
    ];
  }, [functionExpression]);

  const controllerCell = React.useMemo(
    () => (
      <FunctionKindSelector
        selectedFunctionKind={functionExpression.functionKind}
        onFunctionKindSelect={onFunctionKindSelect}
      />
    ),
    [functionExpression.functionKind, onFunctionKindSelect]
  );

  const cellComponentByColumnId: BeeTableProps<ROWTYPE>["cellComponentByColumnId"] = React.useMemo(
    () => ({
      parameters: (props) => (
        <>
          {functionExpression.functionKind === FunctionExpressionDefinitionKind.Feel && (
            <FeelFunctionImplementationCell {...props} />
          )}
          {functionExpression.functionKind === FunctionExpressionDefinitionKind.Java && (
            <JavaFunctionBindingCell {...props} />
          )}
          {functionExpression.functionKind === FunctionExpressionDefinitionKind.Pmml && (
            <PmmlFunctionBindingCell {...props} />
          )}
        </>
      ),
    }),
    [functionExpression]
  );

  const getRowKey = React.useCallback((r: ReactTable.Row<ROWTYPE>) => {
    return r.original.functionExpression.id;
  }, []);

  const onRowReset = React.useCallback(() => {
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

  return (
    <div className={`function-expression ${functionExpression.id}`}>
      <BeeTable
        resizerStopBehavior={ResizerStopBehavior.SET_WIDTH_WHEN_SMALLER}
        operationConfig={beeTableOperationConfig}
        onColumnUpdates={onColumnUpdates}
        getRowKey={getRowKey}
        onRowReset={onRowReset}
        columns={beeTableColumns}
        rows={beeTableRows}
        headerLevelCount={1}
        headerVisibility={headerVisibility}
        controllerCell={controllerCell}
        cellComponentByColumnId={cellComponentByColumnId}
        shouldRenderRowIndexColumn={true}
        shouldShowRowsInlineControls={false}
        shouldShowColumnsInlineControls={false}
      />
    </div>
  );
}
