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

import * as _ from "lodash";
import * as React from "react";
import { PropsWithChildren, useCallback, useMemo } from "react";
import * as ReactTable from "react-table";
import {
  BeeTableHeaderVisibility,
  BeeTableOperation,
  BeeTableOperationConfig,
  BeeTableProps,
  ContextExpressionDefinitionEntry,
  DmnBuiltInDataType,
  ExpressionDefinitionLogicType,
  FunctionExpressionDefinition,
  FunctionExpressionDefinitionKind,
  generateUuid,
} from "../../api";
import { PopoverMenu } from "../../contextMenu/PopoverMenu";
import { useBoxedExpressionEditorI18n } from "../../i18n";
import { useNestedExpressionContainer } from "../../resizing/NestedExpressionContainerContext";
import { useResizingWidthsDispatch } from "../../resizing/ResizingWidthsContext";
import { CONTEXT_ENTRY_EXTRA_WIDTH } from "../../resizing/WidthValues";
import { BeeTable, BeeTableColumnUpdate } from "../../table/BeeTable";
import {
  useBoxedExpressionEditor,
  useBoxedExpressionEditorDispatch,
} from "../BoxedExpressionEditor/BoxedExpressionEditorContext";
import { ContextExpressionContext, ContextExpressionContextType } from "../ContextExpression";
import { getDefaultExpressionDefinitionByLogicType } from "../defaultExpression";
import { FunctionDefinitionCell } from "./FunctionDefinitionCell";
import "./FunctionExpression.css";
import { FunctionKindSelector } from "./FunctionKindSelector";
import { javaContextExpression } from "./JavaFunctionExpression";
import { ParametersPopover } from "./ParametersPopover";
import { pmmlContextExpression } from "./PmmlFunctionExpression";

export const DEFAULT_FIRST_PARAM_NAME = "p-1";

export type ROWTYPE = ContextExpressionDefinitionEntry;

export const FunctionExpression: React.FunctionComponent<FunctionExpressionDefinition> = (
  functionExpression: PropsWithChildren<FunctionExpressionDefinition>
) => {
  const { i18n } = useBoxedExpressionEditorI18n();
  const { setExpression } = useBoxedExpressionEditorDispatch();
  const nestedExpressionContainer = useNestedExpressionContainer();

  const { editorRef, pmmlParams, decisionNodeId } = useBoxedExpressionEditor();

  const parametersColumnHeader = useMemo(
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
                  <>
                    <span>{parameter.name}</span>
                    <span>{": "}</span>
                    <span className={"expression-info-data-type"}>({parameter.dataType})</span>
                    {i < functionExpression.formalParameters.length - 1 && <span>{", "}</span>}
                  </>
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

  const beeTableColumns = useMemo<ReactTable.Column<ROWTYPE>[]>(() => {
    return [
      {
        label: functionExpression.name ?? DEFAULT_FIRST_PARAM_NAME,
        accessor: decisionNodeId as any, // FIXME: Tiago -> No bueno.
        dataType: functionExpression.dataType ?? DmnBuiltInDataType.Undefined,
        isRowIndexColumn: false,
        width: undefined,
        columns: [
          {
            headerCellElement: parametersColumnHeader,
            accessor: "parameters" as any, // FIXME: Tiago -> No bueno.
            label: "",
            isRowIndexColumn: false,
            dataType: undefined as any, // FIXME: Tiago -> No bueno.
            width: undefined,
          },
        ],
      },
    ];
  }, [decisionNodeId, functionExpression.dataType, functionExpression.name, parametersColumnHeader]);

  const headerVisibility = useMemo(() => {
    return functionExpression.isHeadless ? BeeTableHeaderVisibility.LastLevel : BeeTableHeaderVisibility.AllLevels;
  }, [functionExpression.isHeadless]);

  const onFunctionKindSelect = useCallback(
    (kind: string) => {
      setExpression((prev) => {
        if (kind === FunctionExpressionDefinitionKind.Feel) {
          return getDefaultExpressionDefinitionByLogicType(ExpressionDefinitionLogicType.Function, {
            isHeadless: prev.isHeadless,
            id: prev.id ?? generateUuid(),
            name: prev.name,
            dataType: DmnBuiltInDataType.Undefined,
          });
        } else if (kind === FunctionExpressionDefinitionKind.Java) {
          return {
            isHeadless: prev.isHeadless,
            name: prev.name,
            id: prev.id ?? generateUuid(),
            logicType: ExpressionDefinitionLogicType.Function,
            functionKind: FunctionExpressionDefinitionKind.Java,
            dataType: DmnBuiltInDataType.Undefined,
            formalParameters: [],
          };
        } else if (kind === FunctionExpressionDefinitionKind.Pmml) {
          return {
            isHeadless: prev.isHeadless,
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

  const onColumnUpdates = useCallback(
    ([{ name, dataType }]: BeeTableColumnUpdate<ROWTYPE>[]) => {
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
        items: [
          {
            name: i18n.rowOperations.reset,
            type: BeeTableOperation.RowReset,
          },
        ],
      },
    ];
  }, [i18n]);

  const { updateResizingWidth } = useResizingWidthsDispatch();

  // FIXME: Tiago -> Fix this.
  // useEffect(() => {
  //   if (functionExpression.functionKind === FunctionExpressionDefinitionKind.Feel) {
  //     updateResizingWidth(functionExpression.id!, (prev) => {
  //       const nestedLiteralExpressionResizingWidth = prev
  //         ? {
  //             value: prev.value - BEE_TABLE_ROW_INDEX_COLUMN_WIDTH - 2,
  //             isPivoting: prev?.isPivoting ?? false,
  //           }
  //         : {
  //             value: getExpressionResizingWidth(functionExpression.expression, new Map()),
  //             isPivoting: false,
  //           };
  //       return {
  //         value: nestedLiteralExpressionResizingWidth.value + BEE_TABLE_ROW_INDEX_COLUMN_WIDTH + 2,
  //         isPivoting: nestedLiteralExpressionResizingWidth.isPivoting,
  //       };
  //     });
  //   } else {
  //     // FIXME: Tiago -> Implement the logic for the others.
  //   }
  // }, [functionExpression, updateResizingWidth]);

  const beeTableRows = useMemo(() => {
    function rows(): ContextExpressionDefinitionEntry {
      switch (functionExpression.functionKind) {
        case FunctionExpressionDefinitionKind.Java: {
          const javaEntryExpression = javaContextExpression(functionExpression, i18n);
          return {
            entryInfo: {
              id: javaEntryExpression.id!,
              name: javaEntryExpression.id!,
              dataType: undefined as any, // FIXME: Tiago -> Not good.
            },
            entryExpression: javaEntryExpression,
          };
        }
        case FunctionExpressionDefinitionKind.Pmml: {
          const pmmlEntryExpression = pmmlContextExpression(functionExpression, i18n);
          return {
            entryInfo: {
              id: pmmlEntryExpression.id!,
              name: pmmlEntryExpression.id!,
              dataType: undefined as any, // FIXME: Tiago -> Not good.
            },
            entryExpression: pmmlEntryExpression,
          };
        }
        case FunctionExpressionDefinitionKind.Feel:
        default: {
          return {
            entryInfo: {
              id: functionExpression.expression.id!,
              name: functionExpression.expression.id!,
              dataType: undefined as any, // FIXME: Tiago -> Not good.
            },
            entryExpression: functionExpression.expression,
          };
        }
      }
    }
    return [rows()];
  }, [functionExpression, i18n]);

  const controllerCell = useMemo(
    () => (
      <FunctionKindSelector
        selectedFunctionKind={functionExpression.functionKind ?? FunctionExpressionDefinitionKind.Feel}
        onFunctionKindSelect={onFunctionKindSelect}
      />
    ),
    [functionExpression.functionKind, onFunctionKindSelect]
  );

  const cellComponentByColumnId: BeeTableProps<ROWTYPE>["cellComponentByColumnId"] = useMemo(
    () => ({
      parameters: (props) => <FunctionDefinitionCell {...props} />,
    }),
    []
  );

  const getRowKey = useCallback((r: ReactTable.Row<ROWTYPE>) => {
    return r.original.entryInfo.id;
  }, []);

  const contextExpressionContextValue = useMemo<ContextExpressionContextType>(() => {
    // TODO: Tiago -> Make this depend on Function type
    return {
      entryExpressionsMinWidthGlobal: nestedExpressionContainer.minWidthGlobal - CONTEXT_ENTRY_EXTRA_WIDTH + 2, // 2px for the border
      entryExpressionsMinWidthLocal: nestedExpressionContainer.minWidthLocal - CONTEXT_ENTRY_EXTRA_WIDTH + 2, // 2px for the border
      entryExpressionsActualWidth: nestedExpressionContainer.actualWidth - CONTEXT_ENTRY_EXTRA_WIDTH + 2, // 2px for the border
      entryExpressionsResizingWidth: {
        value: nestedExpressionContainer.resizingWidth.value - CONTEXT_ENTRY_EXTRA_WIDTH + 2, // 2px for the border
        isPivoting: false,
      },
    };
  }, [nestedExpressionContainer]);

  return (
    <ContextExpressionContext.Provider value={contextExpressionContextValue}>
      <div className={`function-expression ${functionExpression.id}`}>
        <BeeTable<ROWTYPE>
          operationConfig={beeTableOperationConfig}
          onColumnUpdates={onColumnUpdates}
          getRowKey={getRowKey}
          columns={beeTableColumns}
          rows={beeTableRows}
          headerLevelCount={1}
          headerVisibility={headerVisibility}
          controllerCell={controllerCell}
          cellComponentByColumnId={cellComponentByColumnId}
        />
      </div>
    </ContextExpressionContext.Provider>
  );
};
