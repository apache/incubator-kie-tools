/*
 * Copyright 2021 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import "./InvocationExpression.css";
import * as React from "react";
import { useCallback, useContext, useEffect, useMemo, useRef, useState } from "react";
import {
  ContextEntries,
  ContextEntryRecord,
  DataType,
  DEFAULT_ENTRY_EXPRESSION_MIN_WIDTH,
  DEFAULT_ENTRY_INFO_MIN_WIDTH,
  EntryInfo,
  executeIfExpressionDefinitionChanged,
  generateNextAvailableEntryName,
  getEntryKey,
  getHandlerConfiguration,
  InvocationProps,
  resetEntry,
  TableHeaderVisibility,
} from "../../api";
import { Table } from "../Table";
import { useBoxedExpressionEditorI18n } from "../../i18n";
import { ColumnInstance, DataRecord } from "react-table";
import { ContextEntryExpressionCell, getContextEntryInfoCell } from "../ContextExpression";
import * as _ from "lodash";
import { BoxedExpressionGlobalContext } from "../../context";
import { hashfy } from "../Resizer";

const DEFAULT_PARAMETER_NAME = "p-1";
const DEFAULT_PARAMETER_DATA_TYPE = DataType.Undefined;

export const InvocationExpression: React.FunctionComponent<InvocationProps> = ({
  bindingEntries,
  dataType = DEFAULT_PARAMETER_DATA_TYPE,
  entryInfoWidth = DEFAULT_ENTRY_INFO_MIN_WIDTH,
  entryExpressionWidth = DEFAULT_ENTRY_EXPRESSION_MIN_WIDTH,
  invokedFunction = "",
  isHeadless,
  logicType,
  name = DEFAULT_PARAMETER_NAME,
  onUpdatingNameAndDataType,
  onUpdatingRecursiveExpression,
  uid,
}: InvocationProps) => {
  const { i18n } = useBoxedExpressionEditorI18n();

  const storedExpressionDefinition = useRef({} as InvocationProps);

  const [rows, setRows] = useState(
    bindingEntries || [
      {
        entryInfo: {
          name: DEFAULT_PARAMETER_NAME,
          dataType: DEFAULT_PARAMETER_DATA_TYPE,
        },
        entryExpression: {
          name: DEFAULT_PARAMETER_NAME,
          dataType: DEFAULT_PARAMETER_DATA_TYPE,
        },
        nameAndDataTypeSynchronized: true,
      } as DataRecord,
    ]
  );

  const [infoWidth, setInfoWidth] = useState(entryInfoWidth);
  const [expressionWidth, setExpressionWidth] = useState(entryExpressionWidth);
  const [functionName, setFunctionName] = useState(invokedFunction);
  const { setSupervisorHash } = useContext(BoxedExpressionGlobalContext);

  useEffect(() => {
    const [expressionColumn] = columns.current;

    const updatedDefinition: InvocationProps = {
      uid,
      logicType,
      name: expressionColumn.label,
      dataType: expressionColumn.dataType,
      bindingEntries: rows as ContextEntries,
      invokedFunction: functionName,
      entryInfoWidth: infoWidth,
      entryExpressionWidth: expressionWidth,
    };

    const expression = _.omit(updatedDefinition, ["name", "dataType"]);

    if (isHeadless) {
      onUpdatingRecursiveExpression?.(expression);
    } else {
      executeIfExpressionDefinitionChanged(
        storedExpressionDefinition.current,
        updatedDefinition,
        () => {
          setSupervisorHash(hashfy(expression));
          window.beeApi?.broadcastInvocationExpressionDefinition?.(updatedDefinition);
          storedExpressionDefinition.current = updatedDefinition;
        },
        ["name", "dataType", "bindingEntries", "invokedFunction", "entryInfoWidth", "entryExpressionWidth"]
      );
    }
  }, [
    expressionWidth,
    functionName,
    infoWidth,
    isHeadless,
    logicType,
    onUpdatingRecursiveExpression,
    rows,
    setSupervisorHash,
    uid,
  ]);

  const onBlurCallback = useCallback((event) => {
    setFunctionName(event.target.value);
  }, []);

  const headerCellElement = (
    <div className="function-definition-container">
      <input
        className="function-definition pf-u-text-truncate"
        type="text"
        placeholder={i18n.enterFunction}
        defaultValue={functionName}
        onBlur={onBlurCallback}
      />
    </div>
  );

  const columns = useRef([
    {
      label: name,
      accessor: name,
      dataType,
      disableHandlerOnHeader: true,
      columns: [
        {
          headerCellElement,
          accessor: "functionDefinition",
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
    },
  ]);

  const onColumnsUpdate = useCallback(
    ([expressionColumn]: [ColumnInstance]) => {
      onUpdatingNameAndDataType?.(expressionColumn.label as string, expressionColumn.dataType);

      const [updatedExpressionColumn] = columns.current;
      updatedExpressionColumn.label = expressionColumn.label as string;
      updatedExpressionColumn.accessor = expressionColumn.accessor;
      updatedExpressionColumn.dataType = expressionColumn.dataType;
    },
    [onUpdatingNameAndDataType]
  );

  const onRowAdding = useCallback(() => {
    const generatedName = generateNextAvailableEntryName(
      _.map(rows, (row: ContextEntryRecord) => row.entryInfo) as EntryInfo[],
      "p"
    );
    return {
      entryInfo: {
        name: generatedName,
        dataType: DEFAULT_PARAMETER_DATA_TYPE,
      },
      entryExpression: {
        name: generatedName,
        dataType: DEFAULT_PARAMETER_DATA_TYPE,
      },
      nameAndDataTypeSynchronized: true,
    };
  }, [rows]);

  const getHeaderVisibility = useCallback(() => {
    return isHeadless ? TableHeaderVisibility.SecondToLastLevel : TableHeaderVisibility.Full;
  }, [isHeadless]);

  const setRowsCallback = useCallback((entries) => setRows(entries), []);
  const getRowKeyCallback = useCallback((row) => getEntryKey(row), []);
  const resetEntryCallback = useCallback((row) => resetEntry(row), []);

  return useMemo(
    () => (
      <div className={`invocation-expression ${uid}`}>
        <Table
          tableId={uid}
          headerLevels={2}
          headerVisibility={getHeaderVisibility()}
          skipLastHeaderGroup
          defaultCell={{
            entryInfo: getContextEntryInfoCell(i18n.editParameter),
            entryExpression: ContextEntryExpressionCell,
          }}
          columns={columns.current}
          rows={rows as DataRecord[]}
          onColumnsUpdate={onColumnsUpdate}
          onRowAdding={onRowAdding}
          onRowsUpdate={setRowsCallback}
          handlerConfiguration={getHandlerConfiguration(i18n, i18n.parameters)}
          getRowKey={getRowKeyCallback}
          resetRowCustomFunction={resetEntryCallback}
        />
      </div>
    ),
    [
      getHeaderVisibility,
      getRowKeyCallback,
      i18n,
      onColumnsUpdate,
      onRowAdding,
      resetEntryCallback,
      rows,
      setRowsCallback,
      uid,
    ]
  );
};
