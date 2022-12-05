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

  const beeTableColumns = useMemo<ReactTable.Column<ROWTYPE>[]>(
    () => [
      {
        label: contextExpression.name ?? DEFAULT_CONTEXT_ENTRY_NAME,
        accessor: decisionNodeId as any, // FIXME: Tiago -> No bueno.
        dataType: contextExpression.dataType ?? DEFAULT_CONTEXT_ENTRY_DATA_TYPE,
        disableOperationHandlerOnHeader: true,
        isRowIndexColumn: false,
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
            width: contextExpression.entryExpressionWidth ?? DEFAULT_ENTRY_EXPRESSION_MIN_WIDTH,
            setWidth: setExpressionWidth,
            minWidth: DEFAULT_ENTRY_EXPRESSION_MIN_WIDTH,
            isRowIndexColumn: false,
            dataType: DmnBuiltInDataType.Undefined,
          },
        ],
      },
    ],
    [
      contextExpression.name,
      contextExpression.dataType,
      contextExpression.entryInfoWidth,
      contextExpression.entryExpressionWidth,
      decisionNodeId,
      setInfoWidth,
      setExpressionWidth,
    ]
  );

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

  const defaultCellByColumnId: BeeTableProps<ROWTYPE>["defaultCellByColumnId"] = useMemo(
    () => ({
      entryInfo: (props) => {
        return (
          <ContextEntryInfoCell
            {...props}
            editInfoPopoverLabel={i18n.editContextEntry}
            onRowUpdate={(rowIndex, newEntry) => {
              setExpression((prev) => {
                const contextEntries = [...((prev as ContextExpressionDefinition).contextEntries ?? [])];
                contextEntries[rowIndex] = newEntry;
                return { ...prev, contextEntries };
              });
            }}
          />
        );
      },
      entryExpression: (props) => {
        return (
          <NestedExpressionDispatchContextProvider
            onSetExpression={({ getNewExpression }) => {
              setExpression((prev) => {
                const contextEntries = [...((prev as ContextExpressionDefinition).contextEntries ?? [])];
                contextEntries[props.rowIndex].entryExpression = getNewExpression(
                  contextEntries[props.rowIndex]?.entryExpression ?? {
                    logicType: ExpressionDefinitionLogicType.Undefined,
                  }
                );
                return { ...prev, contextEntries };
              });
            }}
          >
            <ContextEntryExpressionCell {...props} />
          </NestedExpressionDispatchContextProvider>
        );
      },
    }),
    [i18n, setExpression]
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
        additionalRow={
          contextExpression.renderResult ?? true
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
                  width={contextExpression.entryExpressionWidth ?? DEFAULT_ENTRY_EXPRESSION_MIN_WIDTH}
                  minWidth={DEFAULT_ENTRY_EXPRESSION_MIN_WIDTH}
                  onHorizontalResizeStop={setExpressionWidth}
                >
                  <NestedExpressionDispatchContextProvider
                    onSetExpression={({ getNewExpression }) => {
                      setExpression((prev) => ({
                        ...prev,
                        result: getNewExpression(
                          (prev as ContextExpressionDefinition).result ?? {
                            logicType: ExpressionDefinitionLogicType.Undefined,
                          }
                        ),
                      }));
                    }}
                  >
                    <ContextEntryExpression
                      expression={contextExpression.result ?? { logicType: ExpressionDefinitionLogicType.Undefined }}
                    />
                  </NestedExpressionDispatchContextProvider>
                </Resizer>,
              ]
            : undefined
        }
      />
    </div>
  );
};

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
