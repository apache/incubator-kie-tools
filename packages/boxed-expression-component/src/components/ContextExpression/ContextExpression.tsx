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
import { useCallback, useMemo } from "react";
import {
  BeeTableColumnsUpdateArgs,
  ContextExpressionDefinitionEntry,
  ContextExpressionDefinition,
  DmnBuiltInDataType,
  DEFAULT_ENTRY_EXPRESSION_MIN_WIDTH,
  DEFAULT_ENTRY_INFO_MIN_WIDTH,
  getNextAvailableContextExpressionEntryName,
  generateUuid,
  getOperationHandlerConfig,
  ExpressionDefinitionLogicType,
  resetContextExpressionEntry,
  BeeTableHeaderVisibility,
  ExpressionDefinition,
  BeeTableProps,
  BeeTableCellProps,
} from "../../api";
import { BeeTable } from "../BeeTable";
import { useBoxedExpressionEditorI18n } from "../../i18n";
import * as ReactTable from "react-table";
import { ContextEntryExpressionCell } from "./ContextEntryExpressionCell";
import * as _ from "lodash";
import { ContextEntryExpression } from "./ContextEntryExpression";
import { Resizer } from "../Resizer";
import {
  BoxedExpressionEditorDispatchContext,
  useBoxedExpressionEditor,
  useBoxedExpressionEditorDispatch,
} from "../BoxedExpressionEditor/BoxedExpressionEditorContext";
import { ContextEntryInfoCell } from "./ContextEntryInfoCell";

const DEFAULT_CONTEXT_ENTRY_NAME = "ContextEntry-1";

const DEFAULT_CONTEXT_ENTRY_DATA_TYPE = DmnBuiltInDataType.Undefined;

export const EXTRA_WIDTH_FOR_NESTED_CONTEXT_EXPRESSION =
  // 60 = rowIndexColumn,
  // 14 = clear margin,
  // 2 + 2 = entry and expression column borders
  60 + 14 + 2 + 2;

type ROWTYPE = ContextExpressionDefinitionEntry;

export const ContextExpression: React.FunctionComponent<ContextExpressionDefinition> = (
  contextExpression: ContextExpressionDefinition
) => {
  const { i18n } = useBoxedExpressionEditorI18n();
  const { decisionNodeId } = useBoxedExpressionEditor();
  const { setExpression } = useBoxedExpressionEditorDispatch();

  const setInfoWidth = useCallback(
    (newInfoWidth: number) => {
      setExpression((prev) => ({ ...prev, entryInfoWidth: newInfoWidth }));
    },
    [setExpression]
  );

  const setExpressionWidth = useCallback(
    (newEntryExpressionWidth: number) => {
      setExpression((prev) => ({ ...prev, entryExpressionWidth: newEntryExpressionWidth }));
    },
    [setExpression]
  );

  const getEntryExpressionColumnWidthDeep = useCallback(
    (contextExpression: ContextExpressionDefinition, current: number): number => {
      const entriesWidth = contextExpression.contextEntries
        .map(({ entryExpression: nestedExpression }) => {
          if (nestedExpression.logicType === ExpressionDefinitionLogicType.Context) {
            return (
              getEntryExpressionColumnWidthDeep(nestedExpression, EXTRA_WIDTH_FOR_NESTED_CONTEXT_EXPRESSION) +
              (nestedExpression.entryInfoWidth ?? DEFAULT_ENTRY_INFO_MIN_WIDTH)
            );
          } else if (nestedExpression.logicType === ExpressionDefinitionLogicType.LiteralExpression) {
            return nestedExpression.width ?? 0;
          } else if (nestedExpression.logicType === ExpressionDefinitionLogicType.List) {
            return EXTRA_WIDTH_FOR_NESTED_CONTEXT_EXPRESSION; // FIXME: Tiago -> ??
          } else if (nestedExpression.logicType === ExpressionDefinitionLogicType.Function) {
            return EXTRA_WIDTH_FOR_NESTED_CONTEXT_EXPRESSION + 60; // FIXME: Tiago -> ??
          } else if (nestedExpression.logicType === ExpressionDefinitionLogicType.Invocation) {
            return EXTRA_WIDTH_FOR_NESTED_CONTEXT_EXPRESSION + DEFAULT_ENTRY_INFO_MIN_WIDTH; // FIXME: Tiago -> ??
          } else {
            return 0;
          }
        })
        .reduce((acc, w) => acc + w, current);

      const resultWidth =
        contextExpression.result?.logicType === ExpressionDefinitionLogicType.Context
          ? getEntryExpressionColumnWidthDeep(contextExpression.result, current) +
            (contextExpression.result.entryInfoWidth ?? DEFAULT_ENTRY_INFO_MIN_WIDTH) +
            EXTRA_WIDTH_FOR_NESTED_CONTEXT_EXPRESSION
          : 0;

      return Math.max(entriesWidth, resultWidth);
    },
    []
  );

  const entryExpressionColumnWidthDeep = useMemo(
    () =>
      getEntryExpressionColumnWidthDeep(
        contextExpression,
        contextExpression.entryExpressionWidth ?? DEFAULT_ENTRY_EXPRESSION_MIN_WIDTH
      ),
    [contextExpression, getEntryExpressionColumnWidthDeep]
  );

  const headerColumnWidth = useMemo(() => {
    return (contextExpression.entryInfoWidth ?? DEFAULT_ENTRY_INFO_MIN_WIDTH) + entryExpressionColumnWidthDeep + 2; // 2px for border
  }, [contextExpression.entryInfoWidth, entryExpressionColumnWidthDeep]);

  const beeTableColumns = useMemo<ReactTable.Column<ROWTYPE>[]>(() => {
    return [
      {
        label: contextExpression.name ?? DEFAULT_CONTEXT_ENTRY_NAME,
        accessor: decisionNodeId as any,
        dataType: contextExpression.dataType ?? DEFAULT_CONTEXT_ENTRY_DATA_TYPE,
        disableOperationHandlerOnHeader: true,
        isRowIndexColumn: false,
        width: headerColumnWidth,
        columns: [
          {
            accessor: "entryInfo",
            label: "entryInfo",
            disableOperationHandlerOnHeader: true,
            width: contextExpression.entryInfoWidth ?? DEFAULT_ENTRY_INFO_MIN_WIDTH,
            setWidth: setInfoWidth,
            minWidth: DEFAULT_ENTRY_INFO_MIN_WIDTH,
            isRowIndexColumn: false,
            dataType: DmnBuiltInDataType.Undefined,
          },
          {
            accessor: "entryExpression",
            label: "entryExpression",
            disableOperationHandlerOnHeader: true,
            width: entryExpressionColumnWidthDeep,
            setWidth: setExpressionWidth,
            minWidth: DEFAULT_ENTRY_EXPRESSION_MIN_WIDTH,
            isRowIndexColumn: false,
            dataType: DmnBuiltInDataType.Undefined,
          },
        ],
      },
    ];
  }, [
    contextExpression,
    decisionNodeId,
    headerColumnWidth,
    entryExpressionColumnWidthDeep,
    setInfoWidth,
    setExpressionWidth,
  ]);

  const onColumnsUpdate = useCallback(
    ({ columns: [column] }: BeeTableColumnsUpdateArgs<ROWTYPE>) => {
      // FIXME: Tiago -> This is not good. We shouldn't need to rely on the table to update those values.
      setExpression((prev) => ({
        ...prev,
        name: column.label,
        dataType: column.dataType,
        entryInfoWidth: column.columns?.[0].width,
        entryExpressionWidth: column.columns?.[1].width,
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
      setExpression((prev) => {
        const contextEntries = [...((prev as ContextExpressionDefinition).contextEntries ?? [])];
        contextEntries[rowIndex] = newEntry;
        return { ...prev, contextEntries };
      });
    },
    [setExpression]
  );

  const defaultCellByColumnId: BeeTableProps<ROWTYPE>["defaultCellByColumnId"] = useMemo(
    () => ({
      entryInfo: (props) => {
        return (
          <ContextEntryInfoCell {...props} editInfoPopoverLabel={i18n.editContextEntry} onEntryUpdate={updateEntry} />
        );
      },
      entryExpression: (props) => {
        return <NestedContextEntryCell {...props} containerWidth={entryExpressionColumnWidthDeep} />;
      },
    }),
    [entryExpressionColumnWidthDeep, i18n.editContextEntry, updateEntry]
  );

  const operationHandlerConfig = useMemo(
    () => (contextExpression.noHandlerMenu ? undefined : getOperationHandlerConfig(i18n, i18n.contextEntry)),
    [i18n, contextExpression.noHandlerMenu]
  );

  const getRowKey = useCallback((row: ReactTable.Row<ROWTYPE>) => {
    return row.original.entryInfo.id;
  }, []);

  const resetRowCustomFunction = useCallback((entry: ContextExpressionDefinitionEntry) => {
    const updatedEntry = resetContextExpressionEntry(entry);
    updatedEntry.entryExpression.name = updatedEntry.entryInfo.name ?? DEFAULT_CONTEXT_ENTRY_NAME;
    updatedEntry.entryExpression.dataType = updatedEntry.entryInfo.dataType ?? DEFAULT_CONTEXT_ENTRY_DATA_TYPE;
    return updatedEntry;
  }, []);

  const beeTableAdditionalRow = useMemo(() => {
    return contextExpression.renderResult ?? true
      ? [
          <Resizer
            key="context-result"
            width={contextExpression.entryInfoWidth ?? DEFAULT_ENTRY_INFO_MIN_WIDTH}
            minWidth={DEFAULT_ENTRY_INFO_MIN_WIDTH}
            onHorizontalResizeStop={setExpressionWidth}
          >
            <div className="context-result">{`<result>`}</div>
          </Resizer>,
          <Resizer
            key="context-expression"
            width={getEntryExpressionColumnWidthDeep(contextExpression, contextExpression.entryExpressionWidth ?? 0)}
            minWidth={DEFAULT_ENTRY_EXPRESSION_MIN_WIDTH}
            onHorizontalResizeStop={setExpressionWidth}
          >
            <ResultExpression contextExpression={contextExpression} containerWidth={entryExpressionColumnWidthDeep} />
          </Resizer>,
        ]
      : undefined;
  }, [contextExpression, entryExpressionColumnWidthDeep, getEntryExpressionColumnWidthDeep, setExpressionWidth]);

  return (
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
  );
};

function ResultExpression(props: { contextExpression: ContextExpressionDefinition } & { containerWidth: number }) {
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

  return (
    <NestedExpressionContainerWidthContext.Provider value={props.containerWidth}>
      <NestedExpressionDispatchContextProvider onSetExpression={onSetExpression}>
        <ContextEntryExpression expression={expression} />
      </NestedExpressionDispatchContextProvider>
    </NestedExpressionContainerWidthContext.Provider>
  );
}

function NestedContextEntryCell(props: BeeTableCellProps<ROWTYPE> & { containerWidth: number }) {
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

  return (
    <NestedExpressionContainerWidthContext.Provider value={props.containerWidth}>
      <NestedExpressionDispatchContextProvider onSetExpression={onSetExpression}>
        <ContextEntryExpressionCell {...props} />
      </NestedExpressionDispatchContextProvider>
    </NestedExpressionContainerWidthContext.Provider>
  );
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

const NestedExpressionContainerWidthContext = React.createContext<number>(-1);

export function useNestedExpressionContainerWidth() {
  return React.useContext(NestedExpressionContainerWidthContext);
}
