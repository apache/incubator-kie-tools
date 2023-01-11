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
import { useCallback, useEffect, useMemo } from "react";
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
import {
  NestedExpressionContainerContext,
  NestedExpressionContainerContextType,
} from "../../resizing/NestedExpressionContainerContext";
import { useResizingWidths, useResizingWidthsDispatch } from "../../resizing/ResizingWidthsContext";
import { CONTEXT_ENTRY_EXPRESSION_MIN_WIDTH, LIST_EXPRESSION_EXTRA_WIDTH } from "../../resizing/WidthValues";
import { BeeTable, BeeTableColumnUpdate } from "../../table/BeeTable";
import {
  useBoxedExpressionEditor,
  useBoxedExpressionEditorDispatch,
} from "../BoxedExpressionEditor/BoxedExpressionEditorContext";
import {
  useNestedExpressionActualWidth,
  useNestedExpressionMinWidth,
  useNestedExpressionResizingWidth,
} from "../ContextExpression";
import { getDefaultExpressionDefinitionByLogicType } from "../defaultExpression";
import { FunctionDefinitionCell } from "./FunctionDefinitionCell";
import "./FunctionExpression.css";
import { FunctionKindSelector } from "./FunctionKindSelector";
import { javaContextExpression } from "./JavaFunctionExpression";
import { ParametersPopover } from "./ParametersPopover";
import { pmmlContextExpression } from "./PmmlFunctionExpression";

export const DEFAULT_FIRST_PARAM_NAME = "p-1";

export type ROWTYPE = ContextExpressionDefinitionEntry;

export function FunctionExpression(functionExpression: FunctionExpressionDefinition & { isHeadless: boolean }) {
  const { i18n } = useBoxedExpressionEditorI18n();
  const { setExpression } = useBoxedExpressionEditorDispatch();

  const nestedFeelExpression = useMemo(() => {
    return functionExpression.functionKind === FunctionExpressionDefinitionKind.Feel
      ? functionExpression.expression
      : undefined;
  }, [functionExpression]);

  const nestedExpressions = useMemo(() => {
    return nestedFeelExpression ? [nestedFeelExpression] : [];
  }, [nestedFeelExpression]);

  const nonExistingInfoColumnWidth = 0;
  const nonExistingInfoColumnMinWidth = 0;

  const nonExistingInfoColumnResizingWidth = useMemo(
    () => ({
      value: nonExistingInfoColumnWidth,
      isPivoting: false,
    }),
    []
  );

  /// //////////////////////////////////////////////////////
  /// //////////////////////////////////////////////////////
  /// //////////////////////////////////////////////////////
  /// //////// COPIED FROM ContextExpression.tsx ///////////
  /// //////////////////////////////////////////////////////
  /// //////////////////////////////////////////////////////
  /// //////////////////////////////////////////////////////
  /// //////////////////////////////////////////////////////

  //// RESIZING WIDTHS (begin)

  const { resizingWidths } = useResizingWidths();
  const isPivoting = useMemo<boolean>(() => {
    return (
      nonExistingInfoColumnResizingWidth.isPivoting ||
      nestedExpressions.some(({ id }) => resizingWidths.get(id!)?.isPivoting)
    );
  }, [nonExistingInfoColumnResizingWidth.isPivoting, nestedExpressions, resizingWidths]);

  const itemExpressionsResizingWidthValue = useNestedExpressionResizingWidth(
    isPivoting,
    nestedExpressions,
    nonExistingInfoColumnWidth,
    nonExistingInfoColumnResizingWidth,
    nonExistingInfoColumnMinWidth,
    CONTEXT_ENTRY_EXPRESSION_MIN_WIDTH,
    LIST_EXPRESSION_EXTRA_WIDTH
  );

  const itemExpressionsMinWidth = useNestedExpressionMinWidth(
    nestedExpressions,
    nonExistingInfoColumnResizingWidth,
    CONTEXT_ENTRY_EXPRESSION_MIN_WIDTH,
    LIST_EXPRESSION_EXTRA_WIDTH
  );

  const itemExpressionsActualWidth = useNestedExpressionActualWidth(
    nestedExpressions,
    nonExistingInfoColumnWidth,
    LIST_EXPRESSION_EXTRA_WIDTH
  );

  const nestedExpressionContainerValue = useMemo<NestedExpressionContainerContextType>(() => {
    return {
      minWidth: itemExpressionsMinWidth,
      actualWidth: itemExpressionsActualWidth,
      resizingWidth: {
        value: itemExpressionsResizingWidthValue,
        isPivoting,
      },
    };
  }, [itemExpressionsMinWidth, itemExpressionsActualWidth, itemExpressionsResizingWidthValue, isPivoting]);

  console.info(nestedExpressionContainerValue);

  const { updateResizingWidth } = useResizingWidthsDispatch();

  useEffect(() => {
    updateResizingWidth(functionExpression.id!, (prev) => ({
      value: nonExistingInfoColumnResizingWidth.value + itemExpressionsResizingWidthValue + LIST_EXPRESSION_EXTRA_WIDTH,
      isPivoting,
    }));
  }, [
    functionExpression.id,
    itemExpressionsResizingWidthValue,
    nonExistingInfoColumnResizingWidth.value,
    isPivoting,
    updateResizingWidth,
  ]);

  /// //////////////////////////////////////////////////////
  /// //////////////////////////////////////////////////////
  /// //////////////////////////////////////////////////////
  /// //////////////////////////////////////////////////////
  /// //////////////////////////////////////////////////////
  /// //////////////////////////////////////////////////////
  /// //////////////////////////////////////////////////////
  /// //////////////////////////////////////////////////////

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
        label: functionExpression.name ?? "Expression Name",
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

  const headerVisibility = useMemo(() => {
    return functionExpression.isHeadless ? BeeTableHeaderVisibility.LastLevel : BeeTableHeaderVisibility.AllLevels;
  }, [functionExpression.isHeadless]);

  const onFunctionKindSelect = useCallback(
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

  const onRowReset = useCallback(() => {
    setExpression((prev) => {
      if (functionExpression.functionKind === FunctionExpressionDefinitionKind.Feel) {
        return {
          ...prev,
          expression: {
            id: generateUuid(),
            logicType: ExpressionDefinitionLogicType.Undefined,
            dataType: DmnBuiltInDataType.Undefined,
          },
        };
      }

      return prev;
    });
  }, [functionExpression.functionKind, setExpression]);

  return (
    <NestedExpressionContainerContext.Provider value={nestedExpressionContainerValue}>
      <div className={`function-expression ${functionExpression.id}`}>
        <BeeTable
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
    </NestedExpressionContainerContext.Provider>
  );
}
