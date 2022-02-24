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
  ColumnsUpdateArgs,
  ContextEntries,
  ContextEntryRecord,
  ContextProps,
  DataType,
  DEFAULT_ENTRY_EXPRESSION_MIN_WIDTH,
  DEFAULT_ENTRY_INFO_MIN_WIDTH,
  EntryInfo,
  executeIfExpressionDefinitionChanged,
  generateNextAvailableEntryName,
  generateUuid,
  getEntryKey,
  getHandlerConfiguration,
  LogicType,
  resetEntry,
  RowsUpdateArgs,
  TableHeaderVisibility,
} from "../../api";
import { Table } from "../Table";
import { useBoxedExpressionEditorI18n } from "../../i18n";
import { ColumnInstance, DataRecord, Row } from "react-table";
import { ContextEntryExpressionCell } from "./ContextEntryExpressionCell";
import * as _ from "lodash";
import { ContextEntryExpression } from "./ContextEntryExpression";
import { getContextEntryInfoCell } from "./ContextEntryInfoCell";
import { hashfy, Resizer } from "../Resizer";
import { useBoxedExpression } from "../../context";

const DEFAULT_CONTEXT_ENTRY_NAME = "ContextEntry-1";
const DEFAULT_CONTEXT_ENTRY_DATA_TYPE = DataType.Undefined;

export const ContextExpression: React.FunctionComponent<ContextProps> = (contextExpression: ContextProps) => {
  const { i18n } = useBoxedExpressionEditorI18n();
  const { setSupervisorHash, boxedExpressionEditorGWTService, decisionNodeId } = useBoxedExpression();

  const rows = useMemo(
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
        } as DataRecord,
      ],
    [contextExpression.contextEntries]
  );

  const spreadContextExpressionDefinition = useCallback(
    (contextExpressionUpdated: Partial<ContextProps>) => {
      const updatedDefinition: Partial<ContextProps> = {
        id: contextExpression.id,
        logicType: LogicType.Context,
        name: contextExpression.name ?? DEFAULT_CONTEXT_ENTRY_NAME,
        dataType: contextExpression.dataType ?? DEFAULT_CONTEXT_ENTRY_DATA_TYPE,
        contextEntries: rows as ContextEntries,
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
            boxedExpressionEditorGWTService?.broadcastContextExpressionDefinition?.(updatedDefinition as ContextProps);
          }
        },
        ["name", "dataType", "contextEntries", "result", "entryInfoWidth", "entryExpressionWidth"]
      );
    },
    [boxedExpressionEditorGWTService, contextExpression, setSupervisorHash, rows]
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

  const columns = useMemo(
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
    ({ columns: [column] }: ColumnsUpdateArgs<ColumnInstance>) => {
      contextExpression.onUpdatingNameAndDataType?.(column.label as string, column.dataType);
      const updatedWidth = column.columns?.reduce((acc, e) => {
        if (e.id === "entryInfo") {
          acc["entryInfoWidth"] = e.width;
        }
        if (e.id === "entryExpression") {
          acc["entryExpressionWidth"] = e.width;
        }
        return acc;
      }, {} as any);
      spreadContextExpressionDefinition({
        name: column.label as string,
        dataType: column.dataType,
        ...updatedWidth,
      });
    },
    [contextExpression, spreadContextExpressionDefinition]
  );

  const onRowAdding = useCallback(() => {
    const generatedName = generateNextAvailableEntryName(
      _.map(rows, (row: ContextEntryRecord) => row.entryInfo) as EntryInfo[],
      "ContextEntry"
    );
    return {
      entryInfo: {
        id: generateUuid(),
        name: generatedName,
        dataType: DataType.Undefined,
      },
      entryExpression: {
        name: generatedName,
        dataType: DataType.Undefined,
      },
      editInfoPopoverLabel: i18n.editContextEntry,
      nameAndDataTypeSynchronized: true,
    };
  }, [i18n.editContextEntry, rows]);

  const onRowsUpdate = useCallback(
    ({ rows }: RowsUpdateArgs<ContextEntryRecord>) => {
      spreadContextExpressionDefinition({ contextEntries: [...rows] });
    },
    [spreadContextExpressionDefinition]
  );

  const onUpdatingRecursiveExpression = useCallback(
    (expression: any) => {
      if (expression.logicType === LogicType.Undefined) {
        spreadContextExpressionDefinition({ result: { logicType: LogicType.Undefined } });
      } else {
        const filteredExpression = _.omit(expression, "onUpdatingRecursiveExpression");
        spreadContextExpressionDefinition({ result: { ...filteredExpression } });
      }
    },
    [spreadContextExpressionDefinition]
  );

  const getHeaderVisibility = useMemo(() => {
    return contextExpression.isHeadless ? TableHeaderVisibility.None : TableHeaderVisibility.SecondToLastLevel;
  }, [contextExpression.isHeadless]);

  const defaultCell = useMemo(
    () => ({ entryInfo: getContextEntryInfoCell(i18n.editContextEntry), entryExpression: ContextEntryExpressionCell }),
    [i18n.editContextEntry]
  );

  const handlerConfiguration = useMemo(
    () => (contextExpression.noHandlerMenu ? undefined : getHandlerConfiguration(i18n, i18n.contextEntry)),
    [i18n, contextExpression.noHandlerMenu]
  );

  const getRowKey = useCallback((row: Row) => {
    return getEntryKey(row);
  }, []);

  const resetRowCustomFunction = useCallback((row: ContextEntryRecord) => {
    const updatedRow = resetEntry(row);
    updatedRow.entryExpression.name = updatedRow.entryInfo.name ?? DEFAULT_CONTEXT_ENTRY_NAME;
    updatedRow.entryExpression.dataType = updatedRow.entryInfo.dataType ?? DEFAULT_CONTEXT_ENTRY_DATA_TYPE;
    return updatedRow;
  }, []);

  const onHorizontalResizeStop = useCallback(
    (width: number) => {
      setExpressionWidth(width);
    },
    [setExpressionWidth]
  );

  const shouldRenderResult = useMemo(() => {
    if (contextExpression.renderResult === undefined) {
      return true;
    }
    return contextExpression.renderResult;
  }, [contextExpression.renderResult]);

  return (
    <div className={`context-expression ${contextExpression.id}`}>
      <Table
        tableId={contextExpression.id}
        headerLevels={1}
        headerVisibility={getHeaderVisibility}
        defaultCell={defaultCell}
        columns={columns}
        rows={rows as DataRecord[]}
        onColumnsUpdate={onColumnsUpdate}
        onRowAdding={onRowAdding}
        onRowsUpdate={onRowsUpdate}
        handlerConfiguration={handlerConfiguration}
        getRowKey={getRowKey}
        resetRowCustomFunction={resetRowCustomFunction}
      >
        {shouldRenderResult
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
      </Table>
    </div>
  );
};
