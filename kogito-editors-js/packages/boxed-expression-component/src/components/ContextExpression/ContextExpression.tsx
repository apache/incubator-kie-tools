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
import { useCallback, useEffect, useMemo, useRef, useState } from "react";
import {
  ContextEntries,
  ContextEntryRecord,
  ContextProps,
  DataType,
  DEFAULT_ENTRY_EXPRESSION_MIN_WIDTH,
  DEFAULT_ENTRY_INFO_MIN_WIDTH,
  EntryInfo,
  ExpressionProps,
  generateNextAvailableEntryName,
  getEntryKey,
  getHandlerConfiguration,
  LogicType,
  resetEntry,
  TableHeaderVisibility,
} from "../../api";
import { Table } from "../Table";
import { useBoxedExpressionEditorI18n } from "../../i18n";
import { ColumnInstance, DataRecord } from "react-table";
import { ContextEntryExpressionCell } from "./ContextEntryExpressionCell";
import * as _ from "lodash";
import { ContextEntryExpression } from "./ContextEntryExpression";
import { ContextEntryInfoCell } from "./ContextEntryInfoCell";
import { hashfy, Resizer } from "../Resizer";
import { BoxedExpressionGlobalContext } from "../../context";

const DEFAULT_CONTEXT_ENTRY_NAME = "ContextEntry-1";
const DEFAULT_CONTEXT_ENTRY_DATA_TYPE = DataType.Undefined;

export const ContextExpression: React.FunctionComponent<ContextProps> = ({
  uid,
  name = DEFAULT_CONTEXT_ENTRY_NAME,
  dataType = DEFAULT_CONTEXT_ENTRY_DATA_TYPE,
  onUpdatingNameAndDataType,
  contextEntries,
  result = {} as ExpressionProps,
  renderResult = true,
  entryInfoWidth = DEFAULT_ENTRY_INFO_MIN_WIDTH,
  entryExpressionWidth = DEFAULT_ENTRY_EXPRESSION_MIN_WIDTH,
  isHeadless = false,
  noHandlerMenu = false,
  onUpdatingRecursiveExpression,
}) => {
  const { i18n } = useBoxedExpressionEditorI18n();

  const expressionColumnName = useRef(name);
  const expressionColumnDataType = useRef(dataType);

  const [resultExpression, setResultExpression] = useState(result);
  const [infoWidth, setInfoWidth] = useState(entryInfoWidth);
  const [expressionWidth, setExpressionWidth] = useState(entryExpressionWidth);
  const { setSupervisorHash } = React.useContext(BoxedExpressionGlobalContext);

  const columns = useMemo(
    () => [
      {
        label: expressionColumnName.current,
        accessor: expressionColumnName.current,
        dataType: expressionColumnDataType.current,
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
    [expressionWidth, infoWidth]
  );

  const [rows, setRows] = useState(
    contextEntries || [
      {
        entryInfo: {
          name: DEFAULT_CONTEXT_ENTRY_NAME,
          dataType: DEFAULT_CONTEXT_ENTRY_DATA_TYPE,
        },
        entryExpression: {
          name: DEFAULT_CONTEXT_ENTRY_NAME,
          dataType: DEFAULT_CONTEXT_ENTRY_DATA_TYPE,
        },
        editInfoPopoverLabel: i18n.editContextEntry,
        nameAndDataTypeSynchronized: true,
      } as DataRecord,
    ]
  );

  const spreadContextExpressionDefinition = useCallback(() => {
    const updatedDefinition: ContextProps = {
      uid,
      logicType: LogicType.Context,
      name: expressionColumnName.current,
      dataType: expressionColumnDataType.current,
      contextEntries: rows as ContextEntries,
      result: _.omit(resultExpression, "isHeadless"),
      entryInfoWidth: infoWidth,
      entryExpressionWidth: expressionWidth,
    };

    if (isHeadless) {
      onUpdatingRecursiveExpression?.(_.omit(updatedDefinition, ["name", "dataType"]));
    } else {
      setSupervisorHash(hashfy(updatedDefinition));
      window.beeApi?.broadcastContextExpressionDefinition?.(updatedDefinition);
    }
  }, [
    expressionWidth,
    infoWidth,
    isHeadless,
    onUpdatingRecursiveExpression,
    resultExpression,
    rows,
    setSupervisorHash,
    uid,
  ]);

  const onColumnsUpdate = useCallback(
    ([expressionColumn]: [ColumnInstance]) => {
      expressionColumnName.current = expressionColumn.label as string;
      expressionColumnDataType.current = expressionColumn.dataType;
      onUpdatingNameAndDataType?.(expressionColumnName.current, expressionColumnDataType.current);
      spreadContextExpressionDefinition();
    },
    [onUpdatingNameAndDataType, spreadContextExpressionDefinition]
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

  const getHeaderVisibility = useCallback(() => {
    return isHeadless ? TableHeaderVisibility.None : TableHeaderVisibility.SecondToLastLevel;
  }, [isHeadless]);

  useEffect(() => {
    spreadContextExpressionDefinition();
  }, [
    columns,
    isHeadless,
    onUpdatingRecursiveExpression,
    rows,
    resultExpression,
    infoWidth,
    expressionWidth,
    uid,
    setSupervisorHash,
    spreadContextExpressionDefinition,
  ]);

  return (
    <div className={`context-expression ${uid}`}>
      <Table
        tableId={uid}
        headerLevels={1}
        headerVisibility={getHeaderVisibility()}
        defaultCell={{ entryInfo: ContextEntryInfoCell, entryExpression: ContextEntryExpressionCell }}
        columns={columns}
        rows={rows as DataRecord[]}
        onColumnsUpdate={onColumnsUpdate}
        onRowAdding={onRowAdding}
        onRowsUpdate={setRows}
        handlerConfiguration={noHandlerMenu ? undefined : getHandlerConfiguration(i18n, i18n.contextEntry)}
        getRowKey={useCallback(getEntryKey, [])}
        resetRowCustomFunction={useCallback(resetEntry, [])}
      >
        {renderResult
          ? [
              <Resizer
                key="context-result"
                width={infoWidth}
                minWidth={DEFAULT_ENTRY_INFO_MIN_WIDTH}
                onHorizontalResizeStop={(width) => setInfoWidth(width)}
              >
                <div className="context-result">{`<result>`}</div>
              </Resizer>,
              <Resizer
                key="context-expression"
                width={expressionWidth}
                minWidth={DEFAULT_ENTRY_EXPRESSION_MIN_WIDTH}
                onHorizontalResizeStop={(width) => setExpressionWidth(width)}
              >
                <ContextEntryExpression
                  expression={resultExpression}
                  onUpdatingRecursiveExpression={setResultExpression}
                />
              </Resizer>,
            ]
          : undefined}
      </Table>
    </div>
  );
};
