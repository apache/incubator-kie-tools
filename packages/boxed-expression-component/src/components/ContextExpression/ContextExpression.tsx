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
  ContextExpressionDefinitionEntryInfo,
  executeIfExpressionDefinitionChanged,
  generateNextAvailableEntryName,
  generateUuid,
  getEntryKey,
  operationHandlerConfig as getOperationHandlerConfig,
  ExpressionDefinitionLogicType,
  resetEntry,
  BeeTableRowsUpdateArgs,
  BeeTableHeaderVisibility,
  ExpressionDefinition,
  ROWGENERICTYPE,
} from "../../api";
import { BeeTable } from "../BeeTable";
import { useBoxedExpressionEditorI18n } from "../../i18n";
import * as ReactTable from "react-table";
import { ContextEntryExpressionCell } from "./ContextEntryExpressionCell";
import * as _ from "lodash";
import { ContextEntryExpression } from "./ContextEntryExpression";
import { getContextEntryInfoCell } from "./ContextEntryInfoCell";
import { hashfy, Resizer } from "../Resizer";
import { useBoxedExpressionEditor } from "../BoxedExpressionEditor/BoxedExpressionEditorContext";

const DEFAULT_CONTEXT_ENTRY_NAME = "ContextEntry-1";
const DEFAULT_CONTEXT_ENTRY_DATA_TYPE = DmnBuiltInDataType.Undefined;

export const ContextExpression: React.FunctionComponent<ContextExpressionDefinition> = (
  contextExpression: ContextExpressionDefinition
) => {
  const { i18n } = useBoxedExpressionEditorI18n();
  const { setSupervisorHash, beeGwtService, decisionNodeId } = useBoxedExpressionEditor();

  const beeTableRows = useMemo(
    () =>
      contextExpression.contextEntries ?? [
        {
          entryInfo: {
            id: generateUuid(),
            name: DEFAULT_CONTEXT_ENTRY_NAME,
            dataType: DEFAULT_CONTEXT_ENTRY_DATA_TYPE,
          },
          entryExpression: {
            name: DEFAULT_CONTEXT_ENTRY_NAME,
            dataType: DEFAULT_CONTEXT_ENTRY_DATA_TYPE,
          },
          nameAndDataTypeSynchronized: true,
        } as ROWGENERICTYPE,
      ],
    [contextExpression.contextEntries]
  );

  const spreadContextExpressionDefinition = useCallback(
    (contextExpressionUpdated: Partial<ContextExpressionDefinition>) => {
      const updatedDefinition: Partial<ContextExpressionDefinition> = {
        id: contextExpression.id,
        logicType: ExpressionDefinitionLogicType.Context,
        name: contextExpression.name ?? DEFAULT_CONTEXT_ENTRY_NAME,
        dataType: contextExpression.dataType ?? DEFAULT_CONTEXT_ENTRY_DATA_TYPE,
        contextEntries: beeTableRows as ContextExpressionDefinitionEntry[],
        result: contextExpression.result,
        entryInfoWidth: contextExpression.entryInfoWidth ?? DEFAULT_ENTRY_INFO_MIN_WIDTH,
        entryExpressionWidth: contextExpression.entryExpressionWidth ?? DEFAULT_ENTRY_EXPRESSION_MIN_WIDTH,
        noClearAction: contextExpression.noClearAction,
        renderResult: contextExpression.renderResult,
        noHandlerMenu: contextExpression.noHandlerMenu,
        ...contextExpressionUpdated,
      };

      const expression = _.omit(updatedDefinition, ["name", "dataType", "isHeadless"]);
      executeIfExpressionDefinitionChanged(
        contextExpression,
        updatedDefinition,
        () => {
          if (contextExpression.isHeadless) {
            contextExpression.onUpdatingRecursiveExpression?.(expression);
          } else {
            setSupervisorHash(hashfy(expression));
            beeGwtService?.broadcastContextExpressionDefinition?.(updatedDefinition as ContextExpressionDefinition);
          }
        },
        ["name", "dataType", "contextEntries", "result", "entryInfoWidth", "entryExpressionWidth"]
      );
    },
    [beeGwtService, contextExpression, setSupervisorHash, beeTableRows]
  );

  const setInfoWidth = useCallback(
    (newInfoWidth) => {
      spreadContextExpressionDefinition({ entryInfoWidth: newInfoWidth });
    },
    [spreadContextExpressionDefinition]
  );

  const setExpressionWidth = useCallback(
    (newEntryExpressionWidth) => {
      spreadContextExpressionDefinition({ entryExpressionWidth: newEntryExpressionWidth });
    },
    [spreadContextExpressionDefinition]
  );

  const beeTableColumns = useMemo<ReactTable.ColumnInstance[]>(
    () => [
      {
        label: contextExpression.name ?? DEFAULT_CONTEXT_ENTRY_NAME,
        accessor: decisionNodeId,
        dataType: contextExpression.dataType ?? DEFAULT_CONTEXT_ENTRY_DATA_TYPE,
        disableHandlerOnHeader: true,
        columns: [
          {
            accessor: "entryInfo",
            disableHandlerOnHeader: true,
            width: contextExpression.entryInfoWidth ?? DEFAULT_ENTRY_INFO_MIN_WIDTH,
            setWidth: setInfoWidth,
            minWidth: DEFAULT_ENTRY_INFO_MIN_WIDTH,
          },
          {
            accessor: "entryExpression",
            disableHandlerOnHeader: true,
            width: contextExpression.entryExpressionWidth ?? DEFAULT_ENTRY_EXPRESSION_MIN_WIDTH,
            setWidth: setExpressionWidth,
            minWidth: DEFAULT_ENTRY_EXPRESSION_MIN_WIDTH,
          },
        ],
      } as any, // FIXME: Tiago -> Remove this!!,
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
    ({ columns: [column] }: BeeTableColumnsUpdateArgs<ReactTable.ColumnInstance<ROWGENERICTYPE>>) => {
      contextExpression.onUpdatingNameAndDataType?.(column.label as string, column.dataType);
      const updatedWidth = column.columns?.reduce((acc, column) => {
        if (column.id === "entryInfo") {
          acc["entryInfoWidth"] = column.width;
        }
        if (column.id === "entryExpression") {
          acc["entryExpressionWidth"] = column.width;
        }
        return acc;
      }, {} as ContextExpressionDefinition);
      spreadContextExpressionDefinition({
        name: column.label as string,
        dataType: column.dataType,
        ...updatedWidth,
      });
    },
    [contextExpression, spreadContextExpressionDefinition]
  );

  const onNewRow = useCallback(() => {
    const generatedName = generateNextAvailableEntryName(
      _.map(beeTableRows, (row: ContextExpressionDefinitionEntry) => row.entryInfo),
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
      },
      editInfoPopoverLabel: i18n.editContextEntry,
      nameAndDataTypeSynchronized: true,
    };
  }, [i18n, beeTableRows]);

  const onRowsUpdate = useCallback(
    ({ rows }: BeeTableRowsUpdateArgs<ContextExpressionDefinitionEntry>) => {
      spreadContextExpressionDefinition({ contextEntries: [...rows] });
    },
    [spreadContextExpressionDefinition]
  );

  // FIXME: Tiago
  const onUpdatingRecursiveExpression = useCallback(
    (expression: ExpressionDefinition) => {
      if (expression.logicType === ExpressionDefinitionLogicType.Undefined) {
        spreadContextExpressionDefinition({ result: { logicType: ExpressionDefinitionLogicType.Undefined } });
      } else {
        const filteredExpression = _.omit(expression, "onUpdatingRecursiveExpression");
        spreadContextExpressionDefinition({ result: { ...filteredExpression } });
      }
    },
    [spreadContextExpressionDefinition]
  );

  const getHeaderVisibility = useMemo(() => {
    return contextExpression.isHeadless ? BeeTableHeaderVisibility.None : BeeTableHeaderVisibility.SecondToLastLevel;
  }, [contextExpression.isHeadless]);

  const defaultByColumnNameCell = useMemo(
    () => ({
      entryInfo: getContextEntryInfoCell(i18n.editContextEntry),
      entryExpression: ContextEntryExpressionCell,
    }),
    [i18n]
  );

  const operationHandlerConfig = useMemo(
    () => (contextExpression.noHandlerMenu ? undefined : getOperationHandlerConfig(i18n, i18n.contextEntry)),
    [i18n, contextExpression.noHandlerMenu]
  );

  const getRowKey = useCallback((row: ReactTable.Row) => {
    return getEntryKey(row);
  }, []);

  const resetRowCustomFunction = useCallback((entry: ContextExpressionDefinitionEntry) => {
    const updatedEntry = resetEntry(entry);
    updatedEntry.entryExpression.name = updatedEntry.entryInfo.name ?? DEFAULT_CONTEXT_ENTRY_NAME;
    updatedEntry.entryExpression.dataType = updatedEntry.entryInfo.dataType ?? DEFAULT_CONTEXT_ENTRY_DATA_TYPE;
    return updatedEntry;
  }, []);

  const onHorizontalResizeStop = useCallback(
    (width: number) => {
      setExpressionWidth(width);
    },
    [setExpressionWidth]
  );

  return (
    <div className={`context-expression ${contextExpression.id}`}>
      <BeeTable
        tableId={contextExpression.id}
        headerLevels={1}
        headerVisibility={getHeaderVisibility}
        defaultCellByColumnId={defaultByColumnNameCell}
        columns={beeTableColumns}
        rows={beeTableRows}
        onColumnsUpdate={onColumnsUpdate}
        onNewRow={onNewRow}
        onRowsUpdate={onRowsUpdate}
        operationHandlerConfig={operationHandlerConfig}
        getRowKey={getRowKey}
        resetRowCustomFunction={resetRowCustomFunction}
      >
        {contextExpression.renderResult ?? true
          ? [
              <Resizer
                key="context-result"
                width={contextExpression.entryInfoWidth ?? DEFAULT_ENTRY_INFO_MIN_WIDTH}
                minWidth={DEFAULT_ENTRY_INFO_MIN_WIDTH}
                onHorizontalResizeStop={onHorizontalResizeStop}
              >
                <div className="context-result">{`<result>`}</div>
              </Resizer>,
              <Resizer
                key="context-expression"
                width={contextExpression.entryExpressionWidth ?? DEFAULT_ENTRY_EXPRESSION_MIN_WIDTH}
                minWidth={DEFAULT_ENTRY_EXPRESSION_MIN_WIDTH}
                onHorizontalResizeStop={onHorizontalResizeStop}
              >
                <ContextEntryExpression
                  expression={contextExpression.result ?? {}}
                  onUpdatingRecursiveExpression={onUpdatingRecursiveExpression}
                />
              </Resizer>,
            ]
          : undefined}
      </BeeTable>
    </div>
  );
};
