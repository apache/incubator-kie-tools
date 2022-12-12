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

import "./ContextExpression.css";
import * as React from "react";
import { useCallback, useEffect, useMemo, useState } from "react";
import {
  BeeTableColumnsUpdateArgs,
  ContextExpressionDefinitionEntry,
  ContextExpressionDefinition,
  DmnBuiltInDataType,
  CONTEXT_ENTRY_EXPRESSION_MIN_WIDTH,
  CONTEXT_ENTRY_INFO_MIN_WIDTH,
  getNextAvailableContextExpressionEntryName,
  generateUuid,
  getOperationHandlerConfig,
  ExpressionDefinitionLogicType,
  resetContextExpressionEntry,
  BeeTableHeaderVisibility,
  ExpressionDefinition,
  BeeTableProps,
  BeeTableCellProps,
  ListExpressionDefinition,
  InvocationExpressionDefinition,
  FunctionExpressionDefinitionKind,
  FunctionExpressionDefinition,
  LiteralExpressionDefinition,
} from "../../api";
import { BeeTable } from "../BeeTable";
import { useBoxedExpressionEditorI18n } from "../../i18n";
import * as ReactTable from "react-table";
import { ContextEntryExpressionCell } from "./ContextEntryExpressionCell";
import * as _ from "lodash";
import { ContextEntryExpression } from "./ContextEntryExpression";
import { DEFAULT_MIN_WIDTH, Resizer } from "../Resizer";
import {
  BoxedExpressionEditorDispatchContext,
  useBoxedExpressionEditor,
  useBoxedExpressionEditorDispatch,
} from "../BoxedExpressionEditor/BoxedExpressionEditorContext";
import { ContextEntryInfoCell } from "./ContextEntryInfoCell";
import { LIST_EXPRESSION_MIN_WIDTH } from "../ListExpression";
import { LITERAL_EXPRESSION_MIN_WIDTH, LITERAL_EXPRESSION_EXTRA_WIDTH } from "../LiteralExpression";
import { ResizingWidth, useResizingWidthDispatch, useResizingWidths } from "../ExpressionDefinitionRoot";

const CONTEXT_ENTRY_DEFAULT_NAME = "ContextEntry-1";

const CONTEXT_ENTRY_DEFAULT_DATA_TYPE = DmnBuiltInDataType.Undefined;

export const CONTEXT_ENTRY_EXTRA_WIDTH =
  // 60 = rowIndexColumn,
  // 14 = clear margin,
  // 2 + 2 = info and expression column borders
  60 + 14 + 2 + 2;

type ROWTYPE = ContextExpressionDefinitionEntry;

export function getDefaultExpressionDefinitionByLogicType(
  logicType: ExpressionDefinitionLogicType,
  containerResizingWidth: ResizingWidth,
  prev: ExpressionDefinition
): ExpressionDefinition {
  if (logicType === ExpressionDefinitionLogicType.LiteralExpression) {
    const literalExpression: LiteralExpressionDefinition = {
      ...prev,
      logicType,
      width: LITERAL_EXPRESSION_MIN_WIDTH,
    };
    return literalExpression;
  } else if (logicType === ExpressionDefinitionLogicType.Function) {
    const functionExpression: FunctionExpressionDefinition = {
      ...prev,
      logicType,
      functionKind: FunctionExpressionDefinitionKind.Feel,
      formalParameters: [],
      expression: {
        ...getDefaultExpressionDefinitionByLogicType(
          ExpressionDefinitionLogicType.LiteralExpression,
          containerResizingWidth,
          {
            id: generateUuid(),
            logicType: ExpressionDefinitionLogicType.LiteralExpression,
            isHeadless: true,
          }
        ),
      },
    };
    return functionExpression;
  } else if (logicType === ExpressionDefinitionLogicType.Context) {
    const contextExpression: ContextExpressionDefinition = {
      ...prev,
      logicType,
      entryInfoWidth: CONTEXT_ENTRY_INFO_MIN_WIDTH,
      result: {
        logicType: ExpressionDefinitionLogicType.Undefined,
        id: generateUuid(),
      },
      contextEntries: [
        {
          entryInfo: {
            id: generateUuid(),
            name: "ContextEntry-1",
            dataType: DmnBuiltInDataType.Undefined,
          },
          entryExpression: {
            name: "ContextEntry-1",
            dataType: DmnBuiltInDataType.Undefined,
            logicType: ExpressionDefinitionLogicType.Undefined,
          },
          nameAndDataTypeSynchronized: true,
        },
        {
          entryInfo: {
            id: generateUuid(),
            name: "ContextEntry-2",
            dataType: DmnBuiltInDataType.Undefined,
          },
          entryExpression: {
            name: "ContextEntry-2",
            dataType: DmnBuiltInDataType.Undefined,
            logicType: ExpressionDefinitionLogicType.Undefined,
          },
          nameAndDataTypeSynchronized: true,
        },
      ],
    };
    return contextExpression;
  } else if (logicType === ExpressionDefinitionLogicType.List) {
    const listExpression: ListExpressionDefinition = {
      ...prev,
      logicType,
      isHeadless: true,
      width: containerResizingWidth.value - CONTEXT_ENTRY_EXTRA_WIDTH + 2, // 2px for border
      items: [
        {
          logicType: ExpressionDefinitionLogicType.LiteralExpression,
          isHeadless: true,
          content: "",
          width: LITERAL_EXPRESSION_MIN_WIDTH,
        },
      ],
    };
    return listExpression;
  } else if (logicType === ExpressionDefinitionLogicType.Invocation) {
    const invocationExpression: InvocationExpressionDefinition = {
      ...prev,
      logicType,
      isHeadless: true,
      entryInfoWidth: CONTEXT_ENTRY_INFO_MIN_WIDTH,
    };
    return invocationExpression;
  } else {
    return prev;
  }
}

function getExpressionMinWidth(expression?: ExpressionDefinition): number {
  if (!expression) {
    return DEFAULT_MIN_WIDTH;
  } else if (expression.logicType === ExpressionDefinitionLogicType.Context) {
    return (
      Math.max(
        CONTEXT_ENTRY_EXPRESSION_MIN_WIDTH,
        ...expression.contextEntries.map(({ entryExpression }) => getExpressionMinWidth(entryExpression)),
        getExpressionMinWidth(expression.result)
      ) +
      CONTEXT_ENTRY_INFO_MIN_WIDTH +
      CONTEXT_ENTRY_EXTRA_WIDTH
    );
  } else if (expression.logicType === ExpressionDefinitionLogicType.LiteralExpression) {
    return LITERAL_EXPRESSION_MIN_WIDTH;
  } else if (expression.logicType === ExpressionDefinitionLogicType.Function) {
    if (expression.functionKind === FunctionExpressionDefinitionKind.Feel) {
      return CONTEXT_ENTRY_EXPRESSION_MIN_WIDTH + CONTEXT_ENTRY_EXTRA_WIDTH - 2; // 2px for the missing entry info border
    } else if (expression.functionKind === FunctionExpressionDefinitionKind.Java) {
      return CONTEXT_ENTRY_EXPRESSION_MIN_WIDTH + CONTEXT_ENTRY_INFO_MIN_WIDTH + CONTEXT_ENTRY_EXTRA_WIDTH * 2 - 2;
    } else if (expression.functionKind === FunctionExpressionDefinitionKind.Pmml) {
      return CONTEXT_ENTRY_EXPRESSION_MIN_WIDTH + CONTEXT_ENTRY_INFO_MIN_WIDTH + CONTEXT_ENTRY_EXTRA_WIDTH * 2 - 2;
    } else {
      throw new Error("Should never get here");
    }

    // TODO: Tiago -> Implement those
  } else if (expression.logicType === ExpressionDefinitionLogicType.List) {
    return LIST_EXPRESSION_MIN_WIDTH;
  } else if (expression.logicType === ExpressionDefinitionLogicType.Invocation) {
    return CONTEXT_ENTRY_INFO_MIN_WIDTH + CONTEXT_ENTRY_EXPRESSION_MIN_WIDTH + CONTEXT_ENTRY_EXTRA_WIDTH;
  } else if (expression.logicType === ExpressionDefinitionLogicType.DecisionTable) {
    return 0;
  } else if (expression.logicType === ExpressionDefinitionLogicType.Relation) {
    return 0;
  } else if (expression.logicType === ExpressionDefinitionLogicType.PmmlLiteralExpression) {
    return 0;
  } else if (expression.logicType === ExpressionDefinitionLogicType.Undefined) {
    return 0;
  } else {
    throw new Error("Shouldn't ever reach this point");
  }
}

function getExpressionResizingWidth(
  expression: ExpressionDefinition | undefined,
  resizingWidths: Map<string, ResizingWidth>
): number {
  if (!expression) {
    return getExpressionMinWidth(expression);
  }

  const resizingWidth = resizingWidths.get(expression.id!)?.value;

  if (expression.logicType === ExpressionDefinitionLogicType.Context) {
    return (
      resizingWidth ??
      (expression.entryInfoWidth ?? CONTEXT_ENTRY_INFO_MIN_WIDTH) +
        Math.max(
          ...[...expression.contextEntries.map((e) => e.entryExpression), expression.result].map((e) =>
            getExpressionResizingWidth(e, resizingWidths)
          ),
          CONTEXT_ENTRY_EXPRESSION_MIN_WIDTH
        ) +
        CONTEXT_ENTRY_EXTRA_WIDTH
    );
  } else if (expression.logicType === ExpressionDefinitionLogicType.LiteralExpression) {
    return (resizingWidth ?? expression.width ?? LITERAL_EXPRESSION_MIN_WIDTH) + LITERAL_EXPRESSION_EXTRA_WIDTH;
  } else if (expression.logicType === ExpressionDefinitionLogicType.Function) {
    if (expression.functionKind === FunctionExpressionDefinitionKind.Feel) {
      return getExpressionResizingWidth(expression.expression, resizingWidths) + CONTEXT_ENTRY_EXTRA_WIDTH - 2; // 2px for the missing entry info border
    } else if (expression.functionKind === FunctionExpressionDefinitionKind.Java) {
      return CONTEXT_ENTRY_EXPRESSION_MIN_WIDTH + CONTEXT_ENTRY_INFO_MIN_WIDTH + CONTEXT_ENTRY_EXTRA_WIDTH * 2 - 2;
    } else if (expression.functionKind === FunctionExpressionDefinitionKind.Pmml) {
      return CONTEXT_ENTRY_EXPRESSION_MIN_WIDTH + CONTEXT_ENTRY_INFO_MIN_WIDTH + CONTEXT_ENTRY_EXTRA_WIDTH * 2 - 2;
    } else {
      throw new Error("Should never get here");
    }
  }

  // TODO: Tiago -> Implement those
  else if (expression.logicType === ExpressionDefinitionLogicType.List) {
    return resizingWidth ?? expression.width ?? LIST_EXPRESSION_MIN_WIDTH;
  } else if (expression.logicType === ExpressionDefinitionLogicType.Invocation) {
    return resizingWidth ?? DEFAULT_MIN_WIDTH;
  } else if (expression.logicType === ExpressionDefinitionLogicType.DecisionTable) {
    return resizingWidth ?? DEFAULT_MIN_WIDTH;
  } else if (expression.logicType === ExpressionDefinitionLogicType.Relation) {
    return (
      resizingWidth ??
      expression.columns?.reduce((acc, { width }) => acc + (width ?? DEFAULT_MIN_WIDTH), 0) ??
      DEFAULT_MIN_WIDTH
    );
  } else if (expression.logicType === ExpressionDefinitionLogicType.PmmlLiteralExpression) {
    return resizingWidth ?? DEFAULT_MIN_WIDTH;
  } else if (expression.logicType === ExpressionDefinitionLogicType.Undefined) {
    return resizingWidth ?? DEFAULT_MIN_WIDTH;
  } else {
    throw new Error("Shouldn't ever reach this point");
  }
}

export const ContextExpression: React.FunctionComponent<ContextExpressionDefinition> = (
  contextExpression: ContextExpressionDefinition
) => {
  const { i18n } = useBoxedExpressionEditorI18n();
  const { decisionNodeId } = useBoxedExpressionEditor();
  const { setExpression } = useBoxedExpressionEditorDispatch();
  const nestedExpressionContainer = useNestedExpressionContainer();

  //// RESIZING WIDTHS

  const resizingWidthsDispatch = useResizingWidthDispatch();
  const { resizingWidths } = useResizingWidths();

  const [entryInfoResizingWidth, setEntryInfoResizingWidth] = useState<ResizingWidth>({
    value: contextExpression.entryInfoWidth ?? CONTEXT_ENTRY_INFO_MIN_WIDTH,
    isPivoting: false,
  });

  const nestedExpressions = useMemo<ExpressionDefinition[]>(
    () => [...contextExpression.contextEntries.map((e) => e.entryExpression), contextExpression.result],
    [contextExpression.contextEntries, contextExpression.result]
  );

  const isContextExpressionPivoting = useMemo<boolean>(() => {
    return entryInfoResizingWidth.isPivoting || nestedExpressions.some(({ id }) => resizingWidths.get(id!)?.isPivoting);
  }, [entryInfoResizingWidth.isPivoting, nestedExpressions, resizingWidths]);

  const entryExpressionsResizingWidthValue = useMemo<number>(() => {
    if (entryInfoResizingWidth.isPivoting) {
      return Math.max(
        nestedExpressionContainer.resizingWidth.value - entryInfoResizingWidth.value - CONTEXT_ENTRY_EXTRA_WIDTH,
        ...nestedExpressions.map((e) => getExpressionResizingWidth(e, new Map())),
        CONTEXT_ENTRY_EXPRESSION_MIN_WIDTH
      );
    }

    const nestedPivotingExpressions = nestedExpressions.filter(({ id }) => resizingWidths.get(id!)?.isPivoting);
    if (nestedPivotingExpressions.length === 1) {
      return Math.max(
        getExpressionResizingWidth(nestedPivotingExpressions[0]!, resizingWidths),
        CONTEXT_ENTRY_INFO_MIN_WIDTH
      );
    }

    return Math.max(
      nestedExpressionContainer.resizingWidth.value - entryInfoResizingWidth.value - CONTEXT_ENTRY_EXTRA_WIDTH,
      ...nestedExpressions.map((e) => getExpressionResizingWidth(e, new Map())),
      CONTEXT_ENTRY_EXPRESSION_MIN_WIDTH
    );
  }, [
    entryInfoResizingWidth.isPivoting,
    entryInfoResizingWidth.value,
    nestedExpressions,
    nestedExpressionContainer.resizingWidth.value,
    resizingWidths,
  ]);

  const setResizingWidth = useCallback(
    (getNewResizingWidth: (prev: ResizingWidth) => ResizingWidth) => {
      resizingWidthsDispatch.updateResizingWidth(contextExpression.id!, getNewResizingWidth); // FIXME: Tiago -> id optional
    },
    [contextExpression.id, resizingWidthsDispatch]
  );

  useEffect(() => {
    setResizingWidth((prev) => {
      return {
        value: entryInfoResizingWidth.value + entryExpressionsResizingWidthValue + CONTEXT_ENTRY_EXTRA_WIDTH,
        isPivoting: isContextExpressionPivoting,
      };
    });
  }, [entryExpressionsResizingWidthValue, entryInfoResizingWidth.value, isContextExpressionPivoting, setResizingWidth]);

  const setEntryInfoWidth = useCallback(
    (newEntryInfoWidth: number) => {
      setExpression((prev) => ({ ...prev, entryInfoWidth: newEntryInfoWidth }));
    },
    [setExpression]
  );

  const entryExpressionsMinWidthGlobal = useMemo(() => {
    return Math.max(
      nestedExpressionContainer.minWidthGlobal - entryInfoResizingWidth.value - CONTEXT_ENTRY_EXTRA_WIDTH,
      ...nestedExpressions.map((e) => getExpressionMinWidth(e)),
      CONTEXT_ENTRY_EXPRESSION_MIN_WIDTH
    );
  }, [entryInfoResizingWidth.value, nestedExpressionContainer.minWidthGlobal, nestedExpressions]);

  const entryExpressionsMinWidthLocal = useMemo(() => {
    return Math.max(
      ...nestedExpressions.map((e) => getExpressionMinWidth(e)), //
      CONTEXT_ENTRY_EXPRESSION_MIN_WIDTH
    );
  }, [nestedExpressions]);

  ///

  const beeTableColumns = useMemo<ReactTable.Column<ROWTYPE>[]>(() => {
    return [
      {
        label: contextExpression.name ?? CONTEXT_ENTRY_DEFAULT_NAME,
        accessor: decisionNodeId as any,
        dataType: contextExpression.dataType ?? CONTEXT_ENTRY_DEFAULT_DATA_TYPE,
        disableOperationHandlerOnHeader: true,
        isRowIndexColumn: false,
        columns: [
          {
            accessor: "entryInfo",
            label: "entryInfo",
            disableOperationHandlerOnHeader: true,
            minWidth: CONTEXT_ENTRY_INFO_MIN_WIDTH,
            width: contextExpression.entryInfoWidth ?? CONTEXT_ENTRY_INFO_MIN_WIDTH,
            setWidth: setEntryInfoWidth,
            resizingWidth: entryInfoResizingWidth,
            setResizingWidth: setEntryInfoResizingWidth,
            isRowIndexColumn: false,
            dataType: DmnBuiltInDataType.Undefined,
          },
          {
            accessor: "entryExpression",
            label: "entryExpression",
            disableOperationHandlerOnHeader: true,
            isRowIndexColumn: false,
            dataType: DmnBuiltInDataType.Undefined,
            width: undefined,
            minWidth: CONTEXT_ENTRY_EXPRESSION_MIN_WIDTH,
          },
        ],
      },
    ];
  }, [
    contextExpression.name,
    contextExpression.dataType,
    contextExpression.entryInfoWidth,
    decisionNodeId,
    setEntryInfoWidth,
    entryInfoResizingWidth,
    setEntryInfoResizingWidth,
  ]);

  const onColumnsUpdate = useCallback(
    ({ columns: [column] }: BeeTableColumnsUpdateArgs<ROWTYPE>) => {
      // FIXME: Tiago -> This is not good. We shouldn't need to rely on the table to update those values.
      setExpression((prev) => ({
        ...prev,
        name: column.label,
        dataType: column.dataType,
      }));
    },
    [setExpression]
  );

  const onNewRow = useCallback((): ROWTYPE => {
    const generatedName = getNextAvailableContextExpressionEntryName(
      contextExpression.contextEntries.map((row) => row.entryInfo),
      "ContextEntry"
    );
    return {
      entryInfo: {
        id: generateUuid(),
        name: generatedName,
        dataType: DmnBuiltInDataType.Undefined,
      },
      entryExpression: {
        name: generatedName,
        dataType: DmnBuiltInDataType.Undefined,
        logicType: ExpressionDefinitionLogicType.Undefined,
      },
      nameAndDataTypeSynchronized: true,
    };
  }, [contextExpression.contextEntries]);

  const headerVisibility = useMemo(() => {
    return contextExpression.isHeadless ? BeeTableHeaderVisibility.None : BeeTableHeaderVisibility.SecondToLastLevel;
  }, [contextExpression.isHeadless]);

  const updateEntry = useCallback(
    (rowIndex: number, newEntry: ContextExpressionDefinitionEntry) => {
      setExpression((prev: ContextExpressionDefinition) => {
        const contextEntries = [...prev.contextEntries];
        contextEntries[rowIndex] = newEntry;
        return { ...prev, contextEntries };
      });
    },
    [setExpression]
  );

  const defaultCellByColumnId: BeeTableProps<ROWTYPE>["defaultCellByColumnId"] = useMemo(() => {
    return {
      entryInfo: (props) => {
        return (
          <>
            <ContextEntryInfoCell {...props} editInfoPopoverLabel={i18n.editContextEntry} onEntryUpdate={updateEntry} />
          </>
        );
      },
      entryExpression: (props) => {
        return (
          <>
            <ContextEntryCell {...props} />
          </>
        );
      },
    };
  }, [i18n.editContextEntry, updateEntry]);

  const operationHandlerConfig = useMemo(
    () => (contextExpression.noHandlerMenu ? undefined : getOperationHandlerConfig(i18n, i18n.contextEntry)),
    [i18n, contextExpression.noHandlerMenu]
  );

  const getRowKey = useCallback((row: ReactTable.Row<ROWTYPE>) => {
    return row.original.entryInfo.id;
  }, []);

  const resetRowCustomFunction = useCallback((entry: ContextExpressionDefinitionEntry) => {
    const updatedEntry = resetContextExpressionEntry(entry);
    updatedEntry.entryExpression.name = updatedEntry.entryInfo.name ?? CONTEXT_ENTRY_DEFAULT_NAME;
    updatedEntry.entryExpression.dataType = updatedEntry.entryInfo.dataType ?? CONTEXT_ENTRY_DEFAULT_DATA_TYPE;
    return updatedEntry;
  }, []);

  const beeTableAdditionalRow = useMemo(() => {
    return contextExpression.renderResult ?? true
      ? [
          <Resizer
            key="context-result"
            minWidth={CONTEXT_ENTRY_INFO_MIN_WIDTH}
            width={contextExpression.entryInfoWidth ?? CONTEXT_ENTRY_INFO_MIN_WIDTH}
            setWidth={setEntryInfoWidth}
            resizingWidth={entryInfoResizingWidth}
            setResizingWidth={setEntryInfoResizingWidth}
          >
            <div className="context-result">{`<result> (${contextExpression.id})`}</div>
          </Resizer>,
          <Resizer key="context-expression">
            <ResultExpressionCell contextExpression={contextExpression} />
          </Resizer>,
        ]
      : undefined;
  }, [contextExpression, setEntryInfoWidth, entryInfoResizingWidth, setEntryInfoResizingWidth]);

  const value = useMemo<ContextExpressionContextType>(() => {
    return {
      entryExpressionsResizingWidth: {
        value: entryExpressionsResizingWidthValue,
        isPivoting: isContextExpressionPivoting,
      },
      entryExpressionsMinWidthGlobal: entryExpressionsMinWidthGlobal,
      entryExpressionsMinWidthLocal: entryExpressionsMinWidthLocal,
    };
  }, [
    entryExpressionsMinWidthGlobal,
    entryExpressionsMinWidthLocal,
    entryExpressionsResizingWidthValue,
    isContextExpressionPivoting,
  ]);

  return (
    <ContextExpressionContext.Provider value={value}>
      <div
        className={`context-expression ${contextExpression.id} ${
          isContextExpressionPivoting ? "pivoting" : "not-pivoting"
        }`}
      >
        <BeeTable
          tableId={contextExpression.id}
          headerLevelCount={1}
          headerVisibility={headerVisibility}
          defaultCellByColumnId={defaultCellByColumnId}
          columns={beeTableColumns}
          rows={contextExpression.contextEntries}
          onColumnsUpdate={onColumnsUpdate}
          onNewRow={onNewRow}
          operationHandlerConfig={operationHandlerConfig}
          getRowKey={getRowKey}
          resetRowCustomFunction={resetRowCustomFunction}
          additionalRow={beeTableAdditionalRow}
        />
      </div>
    </ContextExpressionContext.Provider>
  );
};

function ResultExpressionCell(props: { contextExpression: ContextExpressionDefinition }) {
  const { setExpression } = useBoxedExpressionEditorDispatch();

  const onSetExpression = useCallback(
    ({ getNewExpression }) => {
      setExpression((prev: ContextExpressionDefinition) => ({
        ...prev,
        result: getNewExpression(prev.result),
      }));
    },
    [setExpression]
  );

  const contextExpression = useContextExpressionContext();

  const value = useMemo<NestedExpressionContainerContextType>(() => {
    return {
      minWidthGlobal: contextExpression.entryExpressionsMinWidthGlobal,
      minWidthLocal: contextExpression.entryExpressionsMinWidthLocal,
      resizingWidth: contextExpression.entryExpressionsResizingWidth,
    };
  }, [
    contextExpression.entryExpressionsMinWidthGlobal,
    contextExpression.entryExpressionsMinWidthLocal,
    contextExpression.entryExpressionsResizingWidth,
  ]);

  return (
    <NestedExpressionContainerContext.Provider value={value}>
      <NestedExpressionDispatchContextProvider onSetExpression={onSetExpression}>
        <ContextEntryExpression expression={props.contextExpression.result} />
      </NestedExpressionDispatchContextProvider>
    </NestedExpressionContainerContext.Provider>
  );
}

function ContextEntryCell(props: BeeTableCellProps<ROWTYPE>) {
  const { setExpression } = useBoxedExpressionEditorDispatch();

  const onSetExpression = useCallback(
    ({ getNewExpression }) => {
      setExpression((prev: ContextExpressionDefinition) => {
        const contextEntries = [...(prev.contextEntries ?? [])];
        contextEntries[props.rowIndex].entryExpression = getNewExpression(
          contextEntries[props.rowIndex]?.entryExpression ?? { logicType: ExpressionDefinitionLogicType.Undefined }
        );
        return { ...prev, contextEntries };
      });
    },
    [props.rowIndex, setExpression]
  );

  const contextExpression = useContextExpressionContext();

  const value = useMemo<NestedExpressionContainerContextType>(() => {
    return {
      minWidthGlobal: contextExpression.entryExpressionsMinWidthGlobal,
      minWidthLocal: contextExpression.entryExpressionsMinWidthLocal,
      resizingWidth: contextExpression.entryExpressionsResizingWidth,
    };
  }, [
    contextExpression.entryExpressionsMinWidthGlobal,
    contextExpression.entryExpressionsMinWidthLocal,
    contextExpression.entryExpressionsResizingWidth,
  ]);

  return (
    <NestedExpressionContainerContext.Provider value={value}>
      <NestedExpressionDispatchContextProvider onSetExpression={onSetExpression}>
        <ContextEntryExpressionCell {...props} />
      </NestedExpressionDispatchContextProvider>
    </NestedExpressionContainerContext.Provider>
  );
}

export interface ContextExpressionContextType {
  entryExpressionsResizingWidth: ResizingWidth;
  entryExpressionsMinWidthGlobal: number;
  entryExpressionsMinWidthLocal: number;
}

export const ContextExpressionContext = React.createContext<ContextExpressionContextType>({
  entryExpressionsMinWidthLocal: -2,
  entryExpressionsMinWidthGlobal: -2,
  entryExpressionsResizingWidth: {
    value: -2,
    isPivoting: false,
  },
});

export function useContextExpressionContext() {
  return React.useContext(ContextExpressionContext);
}

export function NestedExpressionDispatchContextProvider({
  onSetExpression,
  children,
}: React.PropsWithChildren<{
  onSetExpression: (args: { getNewExpression: (prev: ExpressionDefinition) => ExpressionDefinition }) => void;
}>) {
  const nestedExpressionDispatch = useMemo(() => {
    return {
      setExpression: (newExpressionAction: React.SetStateAction<ExpressionDefinition>) => {
        function getNewExpression(prev: ExpressionDefinition) {
          return typeof newExpressionAction === "function" ? newExpressionAction(prev) : newExpressionAction;
        }

        onSetExpression({ getNewExpression });
      },
    };
  }, [onSetExpression]);

  return (
    <BoxedExpressionEditorDispatchContext.Provider value={nestedExpressionDispatch}>
      {children}
    </BoxedExpressionEditorDispatchContext.Provider>
  );
}

export type NestedExpressionContainerContextType = {
  minWidthLocal: number;
  minWidthGlobal: number;
  resizingWidth: ResizingWidth;
};

export const NestedExpressionContainerContext = React.createContext<NestedExpressionContainerContextType>({
  minWidthLocal: -2,
  minWidthGlobal: -2,
  resizingWidth: {
    value: -2,
    isPivoting: false,
  },
});

export function useNestedExpressionContainer() {
  return React.useContext(NestedExpressionContainerContext);
}
