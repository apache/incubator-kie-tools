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
  RelationExpressionDefinition,
  DecisionTableExpressionDefinition,
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
import { RELATION_EXPRESSION_COLUMN_MIN_WIDTH } from "../RelationExpression";

const CONTEXT_ENTRY_DEFAULT_NAME = "ContextEntry-1";

const CONTEXT_ENTRY_DEFAULT_DATA_TYPE = DmnBuiltInDataType.Undefined;

export const BEE_TABLE_ROW_INDEX_COLUMN_WIDTH = 60;

export const NESTED_EXPRESSION_CLEAR_MARGIN = 14;

export const CONTEXT_ENTRY_EXTRA_WIDTH =
  BEE_TABLE_ROW_INDEX_COLUMN_WIDTH +
  NESTED_EXPRESSION_CLEAR_MARGIN +
  // 2 + 2 = info and expression column borders
  (2 + 2);

type ROWTYPE = ContextExpressionDefinitionEntry;

export function getDefaultExpressionDefinitionByLogicType(
  logicType: ExpressionDefinitionLogicType,
  prev: Partial<ExpressionDefinition>
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
        ...getDefaultExpressionDefinitionByLogicType(ExpressionDefinitionLogicType.LiteralExpression, {
          id: generateUuid(),
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
  } else if (logicType === ExpressionDefinitionLogicType.Relation) {
    const relationExpression: RelationExpressionDefinition = {
      ...prev,
      logicType,
      columns: [
        {
          id: generateUuid(),
          name: "column-1",
          dataType: DmnBuiltInDataType.Undefined,
          width: RELATION_EXPRESSION_COLUMN_MIN_WIDTH,
        },
        {
          id: generateUuid(),
          name: "column-2",
          dataType: DmnBuiltInDataType.Undefined,
          width: RELATION_EXPRESSION_COLUMN_MIN_WIDTH,
        },
        {
          id: generateUuid(),
          name: "column-3",
          dataType: DmnBuiltInDataType.Undefined,
          width: RELATION_EXPRESSION_COLUMN_MIN_WIDTH,
        },
        {
          id: generateUuid(),
          name: "column-4",
          dataType: DmnBuiltInDataType.Undefined,
          width: RELATION_EXPRESSION_COLUMN_MIN_WIDTH,
        },
      ],
      rows: [
        {
          id: generateUuid(),
          cells: ["a", "b", "c", "d"],
        },
        {
          id: generateUuid(),
          cells: ["e", "f", "g", "h"],
        },
        {
          id: generateUuid(),
          cells: ["i", "j", "k", "l"],
        },
      ],
    };
    return relationExpression;
  } else if (logicType === ExpressionDefinitionLogicType.DecisionTable) {
    const decisionTableExpression: DecisionTableExpressionDefinition = {
      ...prev,
      logicType,
      input: [
        {
          id: generateUuid(),
          name: "input-1",
          dataType: DmnBuiltInDataType.Undefined,
          width: RELATION_EXPRESSION_COLUMN_MIN_WIDTH,
        },
        {
          id: generateUuid(),
          name: "input-2",
          dataType: DmnBuiltInDataType.Undefined,
          width: RELATION_EXPRESSION_COLUMN_MIN_WIDTH,
        },
      ],
      output: [
        {
          id: generateUuid(),
          name: "output-1",
          dataType: DmnBuiltInDataType.Undefined,
          width: RELATION_EXPRESSION_COLUMN_MIN_WIDTH,
        },
        {
          id: generateUuid(),
          name: "output-2",
          dataType: DmnBuiltInDataType.Undefined,
          width: RELATION_EXPRESSION_COLUMN_MIN_WIDTH,
        },
        {
          id: generateUuid(),
          name: "output-3",
          dataType: DmnBuiltInDataType.Undefined,
          width: RELATION_EXPRESSION_COLUMN_MIN_WIDTH,
        },
      ],
      annotations: [
        {
          id: generateUuid(),
          name: "annotation-1",
          width: RELATION_EXPRESSION_COLUMN_MIN_WIDTH,
        },
      ],
      rules: [
        {
          id: generateUuid(),
          inputEntries: ["a", "b"],
          outputEntries: ["c", "d", "e"],
          annotationEntries: [""],
        },
        {
          id: generateUuid(),
          inputEntries: ["a", "b"],
          outputEntries: ["c", "d", "e"],
          annotationEntries: [""],
        },
        {
          id: generateUuid(),
          inputEntries: ["a", "b"],
          outputEntries: ["c", "d", "e"],
          annotationEntries: [""],
        },
      ],
    };
    return decisionTableExpression;
  } else {
    throw new Error(`No default expression available for ${logicType}`);
  }
}

export function getExpressionMinWidth(expression?: ExpressionDefinition): number {
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
  } else if (expression.logicType === ExpressionDefinitionLogicType.Relation) {
    return (
      (expression.columns?.length ?? 0) * RELATION_EXPRESSION_COLUMN_MIN_WIDTH +
      BEE_TABLE_ROW_INDEX_COLUMN_WIDTH +
      NESTED_EXPRESSION_CLEAR_MARGIN +
      (expression.columns?.length ?? 0) * 2 // 2px for border of each column
    );
  }

  // TODO: Tiago -> Implement those
  else if (expression.logicType === ExpressionDefinitionLogicType.List) {
    return LIST_EXPRESSION_MIN_WIDTH;
  } else if (expression.logicType === ExpressionDefinitionLogicType.Invocation) {
    return CONTEXT_ENTRY_INFO_MIN_WIDTH + CONTEXT_ENTRY_EXPRESSION_MIN_WIDTH + CONTEXT_ENTRY_EXTRA_WIDTH;
  } else if (expression.logicType === ExpressionDefinitionLogicType.DecisionTable) {
    return 0;
  } else if (expression.logicType === ExpressionDefinitionLogicType.PmmlLiteralExpression) {
    return 0;
  } else if (expression.logicType === ExpressionDefinitionLogicType.Undefined) {
    return 0;
  } else {
    throw new Error("Shouldn't ever reach this point");
  }
}

export function getExpressionResizingWidth(
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
  } else if (expression.logicType === ExpressionDefinitionLogicType.Relation) {
    return (
      resizingWidth ??
      (expression.columns ?? []).reduce(
        (acc, { width }) => acc + (width ?? RELATION_EXPRESSION_COLUMN_MIN_WIDTH),
        CONTEXT_ENTRY_EXTRA_WIDTH + (expression.columns?.length ?? 0)
      )
    );
  } else if (expression.logicType === ExpressionDefinitionLogicType.DecisionTable) {
    const columns = [...(expression.input ?? []), ...(expression.output ?? []), ...(expression.annotations ?? [])];
    return (
      resizingWidth ??
      columns.reduce(
        (acc, c) => acc + (c.width ?? RELATION_EXPRESSION_COLUMN_MIN_WIDTH),
        BEE_TABLE_ROW_INDEX_COLUMN_WIDTH + NESTED_EXPRESSION_CLEAR_MARGIN + columns.length * 2
      )
    );
  }

  // TODO: Tiago -> Implement those
  else if (expression.logicType === ExpressionDefinitionLogicType.List) {
    return resizingWidth ?? expression.width ?? LIST_EXPRESSION_MIN_WIDTH;
  } else if (expression.logicType === ExpressionDefinitionLogicType.Invocation) {
    return resizingWidth ?? DEFAULT_MIN_WIDTH;
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

  const nestedExpressions = useMemo<ExpressionDefinition[]>(
    () => [...contextExpression.contextEntries.map((e) => e.entryExpression), contextExpression.result],
    [contextExpression.contextEntries, contextExpression.result]
  );

  //// RESIZING WIDTHS

  const { updateResizingWidth } = useResizingWidthDispatch();
  const { resizingWidths } = useResizingWidths();

  const [entryInfoResizingWidth, setEntryInfoResizingWidth] = useState<ResizingWidth>({
    value: contextExpression.entryInfoWidth ?? CONTEXT_ENTRY_INFO_MIN_WIDTH,
    isPivoting: false,
  });

  const isContextExpressionPivoting = useMemo<boolean>(() => {
    return entryInfoResizingWidth.isPivoting || nestedExpressions.some(({ id }) => resizingWidths.get(id!)?.isPivoting);
  }, [entryInfoResizingWidth.isPivoting, nestedExpressions, resizingWidths]);

  const nonPivotingEntryExpressionsMaxActualWidth = useMemo<number>(() => {
    return Math.max(
      nestedExpressionContainer.actualWidth -
        (contextExpression.entryInfoWidth ?? CONTEXT_ENTRY_INFO_MIN_WIDTH) -
        CONTEXT_ENTRY_EXTRA_WIDTH,
      ...nestedExpressions
        .filter(({ id }) => !(resizingWidths.get(id!)?.isPivoting ?? false))
        .map((expression) => getExpressionResizingWidth(expression, new Map()))
    );
  }, [contextExpression.entryInfoWidth, nestedExpressionContainer.actualWidth, nestedExpressions, resizingWidths]);

  const [pivotAwareExpressionContainer, setPivotAwareNestedExpressionContainer] = useState(nestedExpressionContainer);
  useEffect(() => {
    setPivotAwareNestedExpressionContainer((prev) => {
      return isContextExpressionPivoting ? prev : nestedExpressionContainer;
    });
  }, [isContextExpressionPivoting, nestedExpressionContainer, nestedExpressionContainer.resizingWidth.value]);

  const entryExpressionsResizingWidthValue = useMemo<number>(() => {
    const nestedPivotingExpressions = nestedExpressions.filter(({ id }) => resizingWidths.get(id!)?.isPivoting);
    if (nestedPivotingExpressions.length === 1) {
      return Math.max(
        getExpressionResizingWidth(nestedPivotingExpressions[0]!, resizingWidths),
        CONTEXT_ENTRY_INFO_MIN_WIDTH
      );
    }

    return Math.max(
      entryInfoResizingWidth.value >= (contextExpression.entryInfoWidth ?? CONTEXT_ENTRY_INFO_MIN_WIDTH)
        ? pivotAwareExpressionContainer.resizingWidth.value - entryInfoResizingWidth.value - CONTEXT_ENTRY_EXTRA_WIDTH
        : nestedExpressionContainer.actualWidth - entryInfoResizingWidth.value - CONTEXT_ENTRY_EXTRA_WIDTH,
      ...nestedExpressions.map((e) => getExpressionResizingWidth(e, new Map())),
      CONTEXT_ENTRY_EXPRESSION_MIN_WIDTH
    );
  }, [
    contextExpression.entryInfoWidth,
    entryInfoResizingWidth.value,
    nestedExpressionContainer.actualWidth,
    nestedExpressions,
    pivotAwareExpressionContainer.resizingWidth.value,
    resizingWidths,
  ]);

  useEffect(() => {
    updateResizingWidth(contextExpression.id!, (prev) => {
      return {
        value: entryInfoResizingWidth.value + entryExpressionsResizingWidthValue + CONTEXT_ENTRY_EXTRA_WIDTH,
        isPivoting: isContextExpressionPivoting,
      };
    });
  }, [
    contextExpression.id,
    entryExpressionsResizingWidthValue,
    entryInfoResizingWidth.value,
    isContextExpressionPivoting,
    updateResizingWidth,
  ]);

  const entryExpressionsMinWidthLocal = useMemo(() => {
    return Math.max(
      ...nestedExpressions.map((e) => getExpressionMinWidth(e)), //
      CONTEXT_ENTRY_EXPRESSION_MIN_WIDTH
    );
  }, [nestedExpressions]);

  const entryExpressionsMinWidthGlobal = useMemo(() => {
    return Math.max(
      nestedExpressionContainer.minWidthGlobal - entryInfoResizingWidth.value - CONTEXT_ENTRY_EXTRA_WIDTH,
      ...nestedExpressions.map((e) => getExpressionMinWidth(e)),
      CONTEXT_ENTRY_EXPRESSION_MIN_WIDTH
    );
  }, [entryInfoResizingWidth.value, nestedExpressionContainer.minWidthGlobal, nestedExpressions]);

  const contextExpressionContextValue = useMemo<ContextExpressionContextType>(() => {
    return {
      entryExpressionsMinWidthLocal: entryExpressionsMinWidthLocal,
      entryExpressionsMinWidthGlobal: entryExpressionsMinWidthGlobal,
      entryExpressionsActualWidth: nonPivotingEntryExpressionsMaxActualWidth,
      entryExpressionsResizingWidth: {
        value: entryExpressionsResizingWidthValue,
        isPivoting: isContextExpressionPivoting,
      },
    };
  }, [
    entryExpressionsMinWidthLocal,
    entryExpressionsMinWidthGlobal,
    nonPivotingEntryExpressionsMaxActualWidth,
    entryExpressionsResizingWidthValue,
    isContextExpressionPivoting,
  ]);

  const setEntryInfoWidth = useCallback(
    (newEntryInfoWidth: number) => {
      setExpression((prev) => ({ ...prev, entryInfoWidth: newEntryInfoWidth }));
    },
    [setExpression]
  );

  ///

  const beeTableColumns = useMemo<ReactTable.Column<ROWTYPE>[]>(() => {
    return [
      {
        label: contextExpression.name ?? CONTEXT_ENTRY_DEFAULT_NAME,
        accessor: decisionNodeId as any,
        dataType: contextExpression.dataType ?? CONTEXT_ENTRY_DEFAULT_DATA_TYPE,
        disableOperationHandlerOnHeader: true,
        isRowIndexColumn: false,
        width: undefined,
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
    entryInfoResizingWidth,
    setEntryInfoWidth,
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
            actualWidth={contextExpression.entryInfoWidth}
          >
            <div className="context-result">{`<result>`}</div>
          </Resizer>,
          <Resizer key="context-expression">
            <ResultExpressionCell contextExpression={contextExpression} />
          </Resizer>,
        ]
      : undefined;
  }, [contextExpression, setEntryInfoWidth, entryInfoResizingWidth, setEntryInfoResizingWidth]);

  return (
    <ContextExpressionContext.Provider value={contextExpressionContextValue}>
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

export interface ContextExpressionContextType {
  entryExpressionsMinWidthLocal: number;
  entryExpressionsMinWidthGlobal: number;
  entryExpressionsActualWidth: number;
  entryExpressionsResizingWidth: ResizingWidth;
}

export const ContextExpressionContext = React.createContext<ContextExpressionContextType>({
  entryExpressionsMinWidthLocal: -2,
  entryExpressionsMinWidthGlobal: -2,
  entryExpressionsActualWidth: -2,
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
  actualWidth: number;
  resizingWidth: ResizingWidth;
};

export const NestedExpressionContainerContext = React.createContext<NestedExpressionContainerContextType>({
  minWidthLocal: -2,
  minWidthGlobal: -2,
  actualWidth: -2,
  resizingWidth: {
    value: -2,
    isPivoting: false,
  },
});

export function useNestedExpressionContainer() {
  return React.useContext(NestedExpressionContainerContext);
}
