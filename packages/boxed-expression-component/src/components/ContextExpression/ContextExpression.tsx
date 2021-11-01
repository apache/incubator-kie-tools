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
import { useCallback, useMemo, useState } from "react";
import {
  ContextEntries,
  ContextEntryRecord,
  ContextProps,
  DataType,
  DEFAULT_ENTRY_EXPRESSION_MIN_WIDTH,
  DEFAULT_ENTRY_INFO_MIN_WIDTH,
  EntryInfo,
  executeIfExpressionDefinitionChanged,
  generateNextAvailableEntryName,
  getEntryKey,
  getHandlerConfiguration,
  LogicType,
  resetEntry,
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
import { BoxedExpressionGlobalContext } from "../../context";

const DEFAULT_CONTEXT_ENTRY_NAME = "ContextEntry-1";
const DEFAULT_CONTEXT_ENTRY_DATA_TYPE = DataType.Undefined;

export const ContextExpression: React.FunctionComponent<ContextProps> = (contextExpression: ContextProps) => {
  const { i18n } = useBoxedExpressionEditorI18n();
  const [infoWidth, setInfoWidth] = useState(contextExpression.entryInfoWidth ?? DEFAULT_ENTRY_INFO_MIN_WIDTH);
  const [expressionWidth, setExpressionWidth] = useState(
    contextExpression.entryExpressionWidth ?? DEFAULT_ENTRY_EXPRESSION_MIN_WIDTH
  );
  const { setSupervisorHash } = React.useContext(BoxedExpressionGlobalContext);

  const columns = useMemo(
    () => [
      {
        label: contextExpression.name ?? DEFAULT_CONTEXT_ENTRY_NAME,
        accessor: contextExpression.name ?? DEFAULT_CONTEXT_ENTRY_NAME,
        dataType: contextExpression.dataType ?? DEFAULT_CONTEXT_ENTRY_DATA_TYPE,
        disableHandlerOnHeader: true,
        columns: [
          {
            accessor: "entryInfo",
            disableHandlerOnHeader: true,
            width: infoWidth,
            setWidth: setInfoWidth,
            minWidth: DEFAULT_ENTRY_INFO_MIN_WIDTH,
          },
          {
            accessor: "entryExpression",
            disableHandlerOnHeader: true,
            width: expressionWidth,
            setWidth: setExpressionWidth,
            minWidth: DEFAULT_ENTRY_EXPRESSION_MIN_WIDTH,
          },
        ],
      },
    ],
    [expressionWidth, infoWidth, contextExpression.name, contextExpression.dataType]
  );

  const rows = useMemo(
    () =>
      contextExpression.contextEntries || [
        {
          entryInfo: {
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
    [contextExpression.contextEntries, i18n.editContextEntry]
  );

  const spreadContextExpressionDefinition = useCallback(
    (contextExpressionUpdated: Partial<ContextProps>) => {
      const updatedDefinition: ContextProps = {
        uid: contextExpression.uid,
        logicType: LogicType.Context,
        name: contextExpression.name ?? DEFAULT_CONTEXT_ENTRY_NAME,
        dataType: contextExpression.dataType ?? DEFAULT_CONTEXT_ENTRY_DATA_TYPE,
        contextEntries: rows as ContextEntries,
        result: contextExpression.result,
        entryInfoWidth: infoWidth,
        entryExpressionWidth: expressionWidth,
        noClearAction: contextExpression.noClearAction,
        renderResult: contextExpression.renderResult,
        noHandlerMenu: contextExpression.noHandlerMenu,
        ...contextExpressionUpdated,
      };

      executeIfExpressionDefinitionChanged(
        contextExpression,
        updatedDefinition,
        () => {
          if (contextExpression.isHeadless) {
            contextExpression.onUpdatingRecursiveExpression?.(_.omit(updatedDefinition, ["name", "dataType"]));
          } else {
            setSupervisorHash(hashfy(updatedDefinition));
            window.beeApi?.broadcastContextExpressionDefinition?.(updatedDefinition);
          }
        },
        ["name", "dataType", "contextEntries", "result", "entryInfoWidth", "entryExpressionWidth"]
      );
    },
    [expressionWidth, infoWidth, contextExpression, setSupervisorHash, rows]
  );

  const onColumnsUpdate = useCallback(
    ([expressionColumn]: [ColumnInstance]) => {
      contextExpression.onUpdatingNameAndDataType?.(expressionColumn.label as string, expressionColumn.dataType);
      const updatedWidth = expressionColumn.columns?.reduce((acc, e) => {
        if (e.id === "entryInfo") {
          acc["entryInfoWidth"] = e.width;
        }
        if (e.id === "entryExpression") {
          acc["entryExpressionWidth"] = e.width;
        }
        return acc;
      }, {} as any);
      spreadContextExpressionDefinition({
        name: expressionColumn.label as string,
        dataType: expressionColumn.dataType,
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
    (entries) => {
      spreadContextExpressionDefinition({ contextEntries: [...entries] });
    },
    [spreadContextExpressionDefinition]
  );

  const onUpdatingRecursiveExpression = useCallback(
    (expression: any) => {
      const filteredExpression = _.omit(expression, "onUpdatingRecursiveExpression");
      spreadContextExpressionDefinition({ result: { ...filteredExpression } });
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

  const resetRowCustomFunction = useCallback((row: DataRecord) => {
    return resetEntry(row);
  }, []);

  const onHorizontalResizeStop = useCallback((width: number) => {
    setExpressionWidth(width);
  }, []);

  const shouldRenderResult = useMemo(() => {
    if (contextExpression.renderResult === undefined) {
      return true;
    }
    return contextExpression.renderResult;
  }, [contextExpression.renderResult]);

  return (
    <div className={`context-expression ${contextExpression.uid}`}>
      <Table
        tableId={contextExpression.uid}
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
                width={infoWidth}
                minWidth={DEFAULT_ENTRY_INFO_MIN_WIDTH}
                onHorizontalResizeStop={onHorizontalResizeStop}
              >
                <div className="context-result">{`<result>`}</div>
              </Resizer>,
              <Resizer
                key="context-expression"
                width={expressionWidth}
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
