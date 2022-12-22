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
  BeeTableCellProps,
  BeeTableHeaderVisibility,
  BeeTableOperation,
  BeeTableProps,
  ContextExpressionDefinition,
  ContextExpressionDefinitionEntry,
  ContextExpressionDefinitionEntryInfo,
  DmnBuiltInDataType,
  ExpressionDefinition,
  ExpressionDefinitionLogicType,
  FunctionExpressionDefinition,
  FunctionExpressionDefinitionKind,
  generateUuid,
  LiteralExpressionDefinition,
  PmmlLiteralExpressionDefinition,
  PmmlLiteralExpressionDefinitionKind,
} from "../../api";
import { BoxedExpressionEditorI18n, useBoxedExpressionEditorI18n } from "../../i18n";
import { BeeTable, BeeTableColumnUpdate } from "../../table/BeeTable";
import {
  useBoxedExpressionEditor,
  useBoxedExpressionEditorDispatch,
} from "../BoxedExpressionEditor/BoxedExpressionEditorContext";
import {
  ContextEntryExpressionCell,
  ContextExpressionContext,
  ContextExpressionContextType,
  CONTEXT_ENTRY_EXTRA_WIDTH,
  NestedExpressionContainerContext,
  NestedExpressionContainerContextType,
  NestedExpressionDispatchContextProvider,
  useContextExpressionContext,
  useNestedExpressionContainer,
} from "../ContextExpression";
import { useResizingWidthsDispatch } from "../../resizing/ResizingWidthsContext";
import { LITERAL_EXPRESSION_MIN_WIDTH } from "../LiteralExpression";
import { PopoverMenu } from "../../contextMenu/PopoverMenu";
import { ParametersPopover } from "./ParametersPopover";
import "./FunctionExpression.css";
import { FunctionKindSelector } from "./FunctionKindSelector";
import { getDefaultExpressionDefinitionByLogicType } from "../defaultExpression";

export const DEFAULT_FIRST_PARAM_NAME = "p-1";

type ROWTYPE = ContextExpressionDefinitionEntry;

const javaContextExpression = (prev: ExpressionDefinition, i18n: BoxedExpressionEditorI18n): ExpressionDefinition => {
  const id = generateUuid();
  if (
    !(
      prev.logicType === ExpressionDefinitionLogicType.Function &&
      prev.functionKind === FunctionExpressionDefinitionKind.Java
    )
  ) {
    return { logicType: ExpressionDefinitionLogicType.Undefined, id };
  }

  return {
    id: id,
    logicType: ExpressionDefinitionLogicType.Context,
    renderResult: false,
    result: {
      id: `${id}-result`,
      logicType: ExpressionDefinitionLogicType.Undefined,
    },
    noHandlerMenu: true,
    contextEntries: [
      {
        entryInfo: {
          id: prev.classFieldId ?? `${id}-classFieldId`,
          name: i18n.class,
          dataType: DmnBuiltInDataType.String,
        },
        entryExpression: {
          id: prev.classFieldId ?? `${id}-classFieldId`,
          logicType: ExpressionDefinitionLogicType.LiteralExpression,
          width: LITERAL_EXPRESSION_MIN_WIDTH,
          content: prev.className ?? "",
          isHeadless: true,
        },
      },
      {
        entryInfo: {
          id: prev.methodFieldId ?? `${id}-methodFieldId`,
          name: i18n.methodSignature,
          dataType: DmnBuiltInDataType.String,
        },
        entryExpression: {
          id: prev.methodFieldId ?? `${id}-methodFieldId`,
          logicType: ExpressionDefinitionLogicType.LiteralExpression,
          content: prev.methodName ?? "",
          width: LITERAL_EXPRESSION_MIN_WIDTH,
          isHeadless: true,
        },
      },
    ],
    isHeadless: true,
  };
};

const pmmlContextExpression = (prev: ExpressionDefinition, i18n: BoxedExpressionEditorI18n): ExpressionDefinition => {
  const id = generateUuid();

  if (
    !(
      prev.logicType === ExpressionDefinitionLogicType.Function &&
      prev.functionKind === FunctionExpressionDefinitionKind.Pmml
    )
  ) {
    return { logicType: ExpressionDefinitionLogicType.Undefined, id };
  }

  return {
    id,
    logicType: ExpressionDefinitionLogicType.Context,
    renderResult: false,
    noHandlerMenu: true,
    result: {
      id: `${id}-result`,
      logicType: ExpressionDefinitionLogicType.Undefined,
    },
    contextEntries: [
      {
        entryInfo: {
          id: prev.documentFieldId ?? `${id}-document`,
          name: i18n.document,
          dataType: DmnBuiltInDataType.String,
        },
        entryExpression: {
          id: prev.documentFieldId ?? `${id}-document`,
          logicType: ExpressionDefinitionLogicType.PmmlLiteralExpression,
          testId: "pmml-selector-document",
          noOptionsLabel: i18n.pmml.firstSelection,
          kind: PmmlLiteralExpressionDefinitionKind.Document,
          selected: prev.document ?? "",
          isHeadless: true,
        },
      },
      {
        entryInfo: {
          id: prev.modelFieldId ?? `${id}-model`,
          name: i18n.model,
          dataType: DmnBuiltInDataType.String,
        },
        entryExpression: {
          id: prev.modelFieldId ?? `${id}-model`,
          logicType: ExpressionDefinitionLogicType.PmmlLiteralExpression,
          noOptionsLabel: i18n.pmml.secondSelection,
          testId: "pmml-selector-model",
          kind: PmmlLiteralExpressionDefinitionKind.Model,
          selected: prev.model ?? "",
          isHeadless: true,
        },
      },
    ],
    isHeadless: true,
  };
};

export const FunctionExpression: React.FunctionComponent<FunctionExpressionDefinition> = (
  functionExpression: PropsWithChildren<FunctionExpressionDefinition>
) => {
  const { i18n } = useBoxedExpressionEditorI18n();
  const { setExpression } = useBoxedExpressionEditorDispatch();
  const nestedExpressionContainer = useNestedExpressionContainer();

  const { editorRef, pmmlParams, decisionNodeId } = useBoxedExpressionEditor();

  const editParametersPopoverAppendTo = useCallback(() => {
    return () => editorRef.current!;
  }, [editorRef]);

  const parametersColumnHeader = useMemo(
    () => (
      <PopoverMenu
        title={i18n.editParameters}
        appendTo={editParametersPopoverAppendTo()}
        className="parameters-editor-popover"
        minWidth="400px"
        body={<ParametersPopover parameters={functionExpression.formalParameters} />}
      >
        <div className={`parameters-list ${_.isEmpty(functionExpression.formalParameters) ? "empty-parameters" : ""}`}>
          <p className="pf-u-text-truncate">
            {_.isEmpty(functionExpression.formalParameters)
              ? i18n.editParameters
              : `(${functionExpression.formalParameters.map((parameter) => parameter.name).join(", ")})`}
          </p>
        </div>
      </PopoverMenu>
    ),
    [editParametersPopoverAppendTo, i18n, functionExpression.formalParameters]
  );

  const beeTableColumns = useMemo<ReactTable.Column<ROWTYPE>[]>(() => {
    return [
      {
        label: functionExpression.name ?? DEFAULT_FIRST_PARAM_NAME,
        accessor: decisionNodeId as any, // FIXME: Tiago -> No bueno.
        dataType: functionExpression.dataType ?? DmnBuiltInDataType.Undefined,
        disableContextMenuOnHeader: true,
        isRowIndexColumn: false,
        width: undefined,
        columns: [
          {
            headerCellElement: parametersColumnHeader,
            accessor: "parameters" as any, // FIXME: Tiago -> No bueno.
            disableContextMenuOnHeader: true,
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
    return functionExpression.isHeadless ? BeeTableHeaderVisibility.LastLevel : BeeTableHeaderVisibility.Full;
  }, [functionExpression.isHeadless]);

  const onFunctionKindSelect = useCallback(
    (kind: string) => {
      setExpression((prev) => {
        if (kind === FunctionExpressionDefinitionKind.Feel) {
          return getDefaultExpressionDefinitionByLogicType(ExpressionDefinitionLogicType.Function, {
            id: prev.id ?? generateUuid(),
            name: prev.name,
          });
        } else if (kind === FunctionExpressionDefinitionKind.Java) {
          return {
            name: prev.name,
            id: prev.id ?? generateUuid(),
            logicType: ExpressionDefinitionLogicType.Function,
            functionKind: FunctionExpressionDefinitionKind.Java,
            formalParameters: [],
          };
        } else if (kind === FunctionExpressionDefinitionKind.Pmml) {
          return {
            name: prev.name,
            id: prev.id ?? generateUuid(),
            logicType: ExpressionDefinitionLogicType.Function,
            functionKind: FunctionExpressionDefinitionKind.Pmml,
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

  const operationHandlerConfig = useMemo(() => {
    return [
      {
        group: _.upperCase(i18n.function),
        items: [
          {
            name: i18n.rowOperations.clear,
            type: BeeTableOperation.RowClear,
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
      parameters: (props) => (
        <>
          <ParametersCell {...props} />
        </>
      ),
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
          operationHandlerConfig={operationHandlerConfig}
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

function ParametersCell(props: BeeTableCellProps<ROWTYPE>) {
  const { i18n } = useBoxedExpressionEditorI18n();
  const contextExpression = useContextExpressionContext();

  const { setExpression } = useBoxedExpressionEditorDispatch();

  const onSetExpression = useCallback(
    ({ getNewExpression }) => {
      setExpression((prev) => {
        if (prev.logicType !== ExpressionDefinitionLogicType.Function) {
          return prev;
        }

        // FEEL
        if (prev.functionKind === FunctionExpressionDefinitionKind.Feel) {
          return { ...prev, expression: getNewExpression(prev.expression) };
        }

        // Java
        else if (prev.functionKind === FunctionExpressionDefinitionKind.Java) {
          const newExpression = getNewExpression(javaContextExpression(prev, i18n)) as ContextExpressionDefinition;
          return {
            ...prev,
            className: (newExpression.contextEntries![0].entryExpression as LiteralExpressionDefinition).content,
            classFieldId: (newExpression.contextEntries![0].entryExpression as LiteralExpressionDefinition).content,
            methodName: (newExpression.contextEntries![1].entryExpression as LiteralExpressionDefinition).content,
            methodFieldId: (newExpression.contextEntries![1].entryExpression as LiteralExpressionDefinition).content,
          };
        }

        // PMML
        else if (prev.functionKind === FunctionExpressionDefinitionKind.Pmml) {
          const newExpression = getNewExpression(pmmlContextExpression(prev, i18n)) as ContextExpressionDefinition;
          // FIXME: Tiago -> STATE GAP
          return {
            ...prev,
            document: (newExpression.contextEntries[0].entryExpression as PmmlLiteralExpressionDefinition).selected,
            documentFieldId: (newExpression.contextEntries[0].entryExpression as PmmlLiteralExpressionDefinition)
              .selected,
            model: (newExpression.contextEntries[1].entryExpression as PmmlLiteralExpressionDefinition).selected,
            modelFieldId: (newExpression.contextEntries[1].entryExpression as PmmlLiteralExpressionDefinition).selected,
          };
        }

        // default
        else {
          throw new Error("Shouldn't ever reach this point.");
        }
      });
    },
    [i18n, setExpression]
  );

  const nestedExpressionContainer = useMemo<NestedExpressionContainerContextType>(() => {
    return {
      minWidthLocal: contextExpression.entryExpressionsMinWidthLocal,
      minWidthGlobal: contextExpression.entryExpressionsMinWidthGlobal,
      actualWidth: contextExpression.entryExpressionsActualWidth,
      resizingWidth: contextExpression.entryExpressionsResizingWidth,
    };
  }, [contextExpression]);

  return (
    <NestedExpressionContainerContext.Provider value={nestedExpressionContainer}>
      <NestedExpressionDispatchContextProvider onSetExpression={onSetExpression}>
        <ContextEntryExpressionCell {...props} />
      </NestedExpressionDispatchContextProvider>
    </NestedExpressionContainerContext.Provider>
  );
}
