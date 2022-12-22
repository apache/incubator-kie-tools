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
import { useCallback, useEffect, useMemo, useState } from "react";
import * as ReactTable from "react-table";
import {
  BeeTableCellProps,
  BeeTableHeaderVisibility,
  BeeTableOperation,
  BeeTableOperationHandlerConfig,
  BeeTableProps,
  ContextExpressionDefinition,
  ContextExpressionDefinitionEntry,
  DmnBuiltInDataType,
  ExpressionDefinition,
  ExpressionDefinitionLogicType,
  generateUuid,
  getNextAvailablePrefixedName,
} from "../../api";
import { BoxedExpressionEditorI18n, useBoxedExpressionEditorI18n } from "../../i18n";
import { Resizer } from "../../resizing/Resizer";
import { getExpressionMinWidth, getExpressionResizingWidth } from "../../resizing/Widths";
import { BeeTable, BeeTableColumnUpdate } from "../../table/BeeTable";
import {
  BoxedExpressionEditorDispatchContext,
  useBoxedExpressionEditor,
  useBoxedExpressionEditorDispatch,
} from "../BoxedExpressionEditor/BoxedExpressionEditorContext";
import { ResizingWidth, useResizingWidthsDispatch, useResizingWidths } from "../../resizing/ResizingWidthsContext";
import { ContextEntryExpression } from "./ContextEntryExpression";
import { ContextEntryExpressionCell } from "./ContextEntryExpressionCell";
import { ContextEntryInfoCell } from "./ContextEntryInfoCell";
import "./ContextExpression.css";

const CONTEXT_ENTRY_DEFAULT_NAME = "ContextEntry-1";

const CONTEXT_ENTRY_DEFAULT_DATA_TYPE = DmnBuiltInDataType.Undefined;

export const BEE_TABLE_ROW_INDEX_COLUMN_WIDTH = 60;

export const NESTED_EXPRESSION_CLEAR_MARGIN = 14;

export const CONTEXT_ENTRY_INFO_MIN_WIDTH = 150;
export const CONTEXT_ENTRY_EXPRESSION_MIN_WIDTH = 370;

export const CONTEXT_ENTRY_EXTRA_WIDTH =
  BEE_TABLE_ROW_INDEX_COLUMN_WIDTH +
  NESTED_EXPRESSION_CLEAR_MARGIN +
  // 2 + 2 = info and expression column borders
  (2 + 2);

type ROWTYPE = ContextExpressionDefinitionEntry;

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

  const { updateResizingWidth } = useResizingWidthsDispatch();
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
        disableContextMenuOnHeader: true,
        isRowIndexColumn: false,
        width: undefined,
        columns: [
          {
            accessor: "entryInfo",
            label: "entryInfo",
            disableContextMenuOnHeader: true,
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
            disableContextMenuOnHeader: true,
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

  const cellComponentByColumnId: BeeTableProps<ROWTYPE>["cellComponentByColumnId"] = useMemo(() => {
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
          <ResultExpressionCell key={"context-result-expression"} contextExpression={contextExpression} />,
        ]
      : undefined;
  }, [contextExpression, setEntryInfoWidth, entryInfoResizingWidth, setEntryInfoResizingWidth]);

  const onRowAdded = useCallback(
    (args: { beforeIndex: number }) => {
      setExpression((prev: ContextExpressionDefinition) => {
        const newContextEntries = [...(prev.contextEntries ?? [])];
        newContextEntries.splice(args.beforeIndex, 0, {
          nameAndDataTypeSynchronized: true,
          entryExpression: {
            logicType: ExpressionDefinitionLogicType.Undefined,
            dataType: DmnBuiltInDataType.Undefined,
            id: generateUuid(),
          },
          entryInfo: {
            dataType: DmnBuiltInDataType.Undefined,
            id: generateUuid(),
            name: getNextAvailablePrefixedName(
              prev.contextEntries.map((e) => e.entryInfo.name),
              "ContextEntry"
            ),
          },
        });

        return {
          ...prev,
          contextEntries: newContextEntries,
        };
      });
    },
    [setExpression]
  );

  return (
    <ContextExpressionContext.Provider value={contextExpressionContextValue}>
      <div className={`context-expression ${contextExpression.id}`}>
        <BeeTable
          tableId={contextExpression.id}
          headerLevelCount={1}
          headerVisibility={headerVisibility}
          cellComponentByColumnId={cellComponentByColumnId}
          columns={beeTableColumns}
          rows={contextExpression.contextEntries}
          onColumnUpdates={onColumnUpdates}
          operationHandlerConfig={operationHandlerConfig}
          getRowKey={getRowKey}
          additionalRow={beeTableAdditionalRow}
          onRowAdded={onRowAdded}
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

export const getOperationHandlerConfig = (
  i18n: BoxedExpressionEditorI18n,
  groupName: string
): BeeTableOperationHandlerConfig => [
  {
    group: groupName,
    items: [
      { name: i18n.rowOperations.insertAbove, type: BeeTableOperation.RowInsertAbove },
      { name: i18n.rowOperations.insertBelow, type: BeeTableOperation.RowInsertBelow },
      { name: i18n.rowOperations.delete, type: BeeTableOperation.RowDelete },
      { name: i18n.rowOperations.clear, type: BeeTableOperation.RowClear },
    ],
  },
];
