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
import { useResizingWidthDispatch, useResizingWidths } from "../ExpressionDefinitionRoot";

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
  containerWidth: number,
  prev: ExpressionDefinition
): ExpressionDefinition {
  if (logicType === ExpressionDefinitionLogicType.LiteralExpression) {
    const literalExpression: LiteralExpressionDefinition = {
      ...prev,
      logicType,
      width: CONTEXT_ENTRY_EXPRESSION_MIN_WIDTH - LITERAL_EXPRESSION_EXTRA_WIDTH, // Change to container's minWdith
    };
    return literalExpression;
  } else if (logicType === ExpressionDefinitionLogicType.Function) {
    const functionExpression: FunctionExpressionDefinition = {
      ...prev,
      logicType,
      functionKind: FunctionExpressionDefinitionKind.Feel,
      formalParameters: [],
      expression: {
        ...getDefaultExpressionDefinitionByLogicType(ExpressionDefinitionLogicType.LiteralExpression, containerWidth, {
          logicType: ExpressionDefinitionLogicType.LiteralExpression,
          isHeadless: true,
        }),
      },
    };
    return functionExpression;
  } else if (logicType === ExpressionDefinitionLogicType.Context) {
    const contextExpression: ContextExpressionDefinition = {
      ...prev,
      logicType,
      entryInfoWidth: CONTEXT_ENTRY_INFO_MIN_WIDTH,
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
      width: containerWidth - CONTEXT_ENTRY_EXTRA_WIDTH + 2, // 2px for border
      items: [
        {
          logicType: ExpressionDefinitionLogicType.LiteralExpression,
          isHeadless: true,
          content: "",
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
        getExpressionMinWidth(expression.result) + CONTEXT_ENTRY_INFO_MIN_WIDTH + CONTEXT_ENTRY_EXTRA_WIDTH
      ) +
      CONTEXT_ENTRY_INFO_MIN_WIDTH +
      CONTEXT_ENTRY_EXTRA_WIDTH
    );
  } else if (expression.logicType === ExpressionDefinitionLogicType.LiteralExpression) {
    return LITERAL_EXPRESSION_MIN_WIDTH;
  } else if (expression.logicType === ExpressionDefinitionLogicType.List) {
    return LIST_EXPRESSION_MIN_WIDTH;
  } else if (expression.logicType === ExpressionDefinitionLogicType.Function) {
    if (expression.functionKind === FunctionExpressionDefinitionKind.Feel) {
      return CONTEXT_ENTRY_EXPRESSION_MIN_WIDTH + CONTEXT_ENTRY_EXTRA_WIDTH - 2; // 2px for the missing entry info border
    } else if (expression.functionKind === FunctionExpressionDefinitionKind.Java) {
      return CONTEXT_ENTRY_EXPRESSION_MIN_WIDTH + CONTEXT_ENTRY_EXTRA_WIDTH;
    } else if (expression.functionKind === FunctionExpressionDefinitionKind.Pmml) {
      return CONTEXT_ENTRY_EXPRESSION_MIN_WIDTH + CONTEXT_ENTRY_EXTRA_WIDTH;
    } else {
      throw new Error("Should never get here");
    }
  } else if (expression.logicType === ExpressionDefinitionLogicType.Invocation) {
    return CONTEXT_ENTRY_INFO_MIN_WIDTH + CONTEXT_ENTRY_EXPRESSION_MIN_WIDTH + CONTEXT_ENTRY_EXTRA_WIDTH;
  } else if (expression.logicType === ExpressionDefinitionLogicType.DecisionTable) {
    return 0; //FIXME: Tiago -> TODO
  } else if (expression.logicType === ExpressionDefinitionLogicType.Relation) {
    return 0; //FIXME: Tiago -> TODO
  } else if (expression.logicType === ExpressionDefinitionLogicType.PmmlLiteralExpression) {
    return 0; //FIXME: Tiago -> TODO
  } else if (expression.logicType === ExpressionDefinitionLogicType.Undefined) {
    return 0;
  } else {
    throw new Error("Shouldn't ever reach this point");
  }
}

function getExpressionResizingWidth(
  expression: ExpressionDefinition | undefined,
  resizingWidths: Map<string, { resizingWidth: number }>
): number {
  if (!expression) {
    return getExpressionMinWidth(expression);
  }

  const resizingWidth = resizingWidths.get(expression.id!)?.resizingWidth;

  if (expression.logicType === ExpressionDefinitionLogicType.Context) {
    return (
      resizingWidth ??
      (expression.entryInfoWidth ?? CONTEXT_ENTRY_INFO_MIN_WIDTH) +
        Math.max(
          ...expression.contextEntries.map(({ entryExpression }) =>
            getExpressionResizingWidth(entryExpression, resizingWidths)
          ),
          getExpressionResizingWidth(expression.result, resizingWidths),
          CONTEXT_ENTRY_EXPRESSION_MIN_WIDTH
        ) +
        CONTEXT_ENTRY_EXTRA_WIDTH
    );
  } else if (expression.logicType === ExpressionDefinitionLogicType.LiteralExpression) {
    return (resizingWidth ?? expression.width ?? LITERAL_EXPRESSION_MIN_WIDTH) + LITERAL_EXPRESSION_EXTRA_WIDTH;
  } else if (expression.logicType === ExpressionDefinitionLogicType.List) {
    return resizingWidth ?? expression.width ?? LIST_EXPRESSION_MIN_WIDTH;
  } else if (expression.logicType === ExpressionDefinitionLogicType.Function) {
    if (expression.functionKind === FunctionExpressionDefinitionKind.Feel) {
      return getExpressionResizingWidth(expression.expression, resizingWidths) + CONTEXT_ENTRY_EXTRA_WIDTH - 2; // 2px for the missing entry info border
    } else if (expression.functionKind === FunctionExpressionDefinitionKind.Java) {
      return CONTEXT_ENTRY_EXPRESSION_MIN_WIDTH + CONTEXT_ENTRY_EXTRA_WIDTH;
    } else if (expression.functionKind === FunctionExpressionDefinitionKind.Pmml) {
      return CONTEXT_ENTRY_EXPRESSION_MIN_WIDTH + CONTEXT_ENTRY_EXTRA_WIDTH;
    } else {
      throw new Error("Should never get here");
    }
  } else if (expression.logicType === ExpressionDefinitionLogicType.Invocation) {
    return DEFAULT_MIN_WIDTH;
    // Math.max(
    //   CONTEXT_ENTRY_EXPRESSION_MIN_WIDTH,
    //   ...(expression.bindingEntries ?? []).map(({ entryExpression }) =>
    //     getExpressionResizingWidth(entryExpression, resizingWidths)
    //   )
    // ) +
    // (resizingWidth ?? expression.entryInfoWidth ?? CONTEXT_ENTRY_INFO_MIN_WIDTH) +
    // CONTEXT_ENTRY_EXTRA_WIDTH
    //FIXME: Tiago -> TODO
  } else if (expression.logicType === ExpressionDefinitionLogicType.DecisionTable) {
    return resizingWidth ?? DEFAULT_MIN_WIDTH; //FIXME: Tiago -> TODO
  } else if (expression.logicType === ExpressionDefinitionLogicType.Relation) {
    return (
      resizingWidth ??
      expression.columns?.reduce((acc, { width }) => acc + (width ?? DEFAULT_MIN_WIDTH), 0) ??
      DEFAULT_MIN_WIDTH
    );
  } else if (expression.logicType === ExpressionDefinitionLogicType.PmmlLiteralExpression) {
    return resizingWidth ?? DEFAULT_MIN_WIDTH; //FIXME: Tiago -> TODO
  } else if (expression.logicType === ExpressionDefinitionLogicType.Undefined) {
    return resizingWidth ?? DEFAULT_MIN_WIDTH; //FIXME: Tiago -> TODO
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

  const [entryInfoResizingWidthPivot, setEntryInfoResizingWidthPivot] = useState<boolean>(false);
  const [entryInfoResizingWidth, setEntryInfoResizingWidthA] = useState<number | undefined>(
    contextExpression.entryInfoWidth ?? CONTEXT_ENTRY_INFO_MIN_WIDTH
  );

  const setEntryInfoResizingWidth = useCallback((width: number, pivotArgs: { isPivot: boolean }) => {
    setEntryInfoResizingWidthPivot(pivotArgs.isPivot);
    setEntryInfoResizingWidthA(width);
  }, []);

  //// RESIZING WIDTH

  const resizingWidthsDispatch = useResizingWidthDispatch();
  const { resizingWidths } = useResizingWidths(); // ?

  const entryExpressionsResizingWidth = useMemo(() => {
    return Math.max(
      ...contextExpression.contextEntries.map(({ entryExpression }) =>
        getExpressionResizingWidth(entryExpression, new Map())
      ),
      getExpressionResizingWidth(contextExpression.result, new Map()),
      nestedExpressionContainer.resizingWidth -
        (entryInfoResizingWidth ?? contextExpression.entryInfoWidth ?? CONTEXT_ENTRY_INFO_MIN_WIDTH) -
        CONTEXT_ENTRY_EXTRA_WIDTH,
      CONTEXT_ENTRY_EXPRESSION_MIN_WIDTH
    );
  }, [contextExpression, entryInfoResizingWidth, nestedExpressionContainer.resizingWidth]);

  const setResizingWidth = useCallback(
    (newResizingWidth: number, pivotArgs: { isPivot: boolean }) => {
      resizingWidthsDispatch.updateResizingWidth(contextExpression.id!, newResizingWidth, pivotArgs); // FIXME: Tiago -> id optional
    },
    [contextExpression.id, resizingWidthsDispatch]
  );

  useEffect(() => {
    setResizingWidth(
      (entryInfoResizingWidth ?? contextExpression.entryInfoWidth ?? CONTEXT_ENTRY_INFO_MIN_WIDTH) +
        entryExpressionsResizingWidth +
        CONTEXT_ENTRY_EXTRA_WIDTH,
      { isPivot: entryInfoResizingWidthPivot }
    );
  }, [
    contextExpression.entryInfoWidth,
    contextExpression.id,
    entryExpressionsResizingWidth,
    entryInfoResizingWidth,
    entryInfoResizingWidthPivot,
    setResizingWidth,
  ]);

  ///

  const setEntryInfoWidth = useCallback(
    (newEntryInfoWidth: number) => {
      setExpression((prev) => ({ ...prev, entryInfoWidth: newEntryInfoWidth }));
    },
    [setExpression]
  );

  // const entryExpressionMinWidthGlobal = useMemo(() => {
  //   return Math.max(
  //     ...contextExpression.contextEntries.map(({ entryExpression }) => getExpressionMinWidth(entryExpression)),
  //     getExpressionMinWidth(contextExpression.result),
  //     CONTEXT_ENTRY_EXPRESSION_MIN_WIDTH,
  //     nestedExpressionContainer.minWidthLocal -
  //       (entryInfoResizingWidth ?? contextExpression.entryInfoWidth ?? CONTEXT_ENTRY_INFO_MIN_WIDTH) -
  //       CONTEXT_ENTRY_EXTRA_WIDTH
  //   );
  // }, [contextExpression, entryInfoResizingWidth, nestedExpressionContainer.minWidthLocal]);

  // const entryExpressionMinWidthLocal = useMemo(() => {
  //   return Math.max(
  //     ...contextExpression.contextEntries.map(({ entryExpression }) => getExpressionMinWidth(entryExpression)),
  //     getExpressionMinWidth(contextExpression.result),
  //     CONTEXT_ENTRY_EXPRESSION_MIN_WIDTH
  //   );
  // }, [contextExpression]);

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
            <div className="context-result">{`<result>`}</div>
          </Resizer>,
          <Resizer key="context-expression">
            <ResultExpressionCell contextExpression={contextExpression} />
          </Resizer>,
        ]
      : undefined;
  }, [contextExpression, setEntryInfoWidth, entryInfoResizingWidth, setEntryInfoResizingWidth]);

  const value = useMemo(
    () => ({
      entryExpressionsResizingWidth,
    }),
    [entryExpressionsResizingWidth]
  );

  return (
    <ContextExpressionContext.Provider value={value}>
      <div className={`context-expression ${contextExpression.id}`}>
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
        result: getNewExpression(prev.result ?? { logicType: ExpressionDefinitionLogicType.Undefined }),
      }));
    },
    [setExpression]
  );

  const expression = useMemo<ExpressionDefinition>(() => {
    return props.contextExpression.result ?? { logicType: ExpressionDefinitionLogicType.Undefined };
  }, [props.contextExpression.result]);

  const contextExpression = useContextExpressionContext();

  const value = useMemo<NestedExpressionContainerContextType>(() => {
    return {
      minWidthLocal: CONTEXT_ENTRY_EXPRESSION_MIN_WIDTH,
      minWidthGlobal: CONTEXT_ENTRY_EXPRESSION_MIN_WIDTH,
      resizingWidth: contextExpression.entryExpressionsResizingWidth,
    };
  }, [contextExpression.entryExpressionsResizingWidth]);

  return (
    <NestedExpressionContainerContext.Provider value={value}>
      <NestedExpressionDispatchContextProvider onSetExpression={onSetExpression}>
        <ContextEntryExpression expression={expression} />
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
      minWidthLocal: CONTEXT_ENTRY_EXPRESSION_MIN_WIDTH,
      minWidthGlobal: CONTEXT_ENTRY_EXPRESSION_MIN_WIDTH,
      resizingWidth: contextExpression.entryExpressionsResizingWidth,
    };
  }, [contextExpression.entryExpressionsResizingWidth]);

  return (
    <NestedExpressionContainerContext.Provider value={value}>
      <NestedExpressionDispatchContextProvider onSetExpression={onSetExpression}>
        <ContextEntryExpressionCell {...props} />
      </NestedExpressionDispatchContextProvider>
    </NestedExpressionContainerContext.Provider>
  );
}

interface ContextExpressionContextType {
  entryExpressionsResizingWidth: number;
}

const ContextExpressionContext = React.createContext({} as ContextExpressionContextType);

function useContextExpressionContext() {
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
  resizingWidth: number;
};

export const NestedExpressionContainerContext = React.createContext<NestedExpressionContainerContextType>({
  minWidthLocal: -2,
  minWidthGlobal: -2,
  resizingWidth: -2,
});

export function useNestedExpressionContainer() {
  return React.useContext(NestedExpressionContainerContext);
}
