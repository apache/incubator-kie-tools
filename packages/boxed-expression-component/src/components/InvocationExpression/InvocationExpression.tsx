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
import { useCallback, useMemo } from "react";
import {
  ColumnsUpdateArgs,
  ContextEntries,
  ContextEntryRecord,
  DataType,
  DEFAULT_ENTRY_EXPRESSION_MIN_WIDTH,
  DEFAULT_ENTRY_INFO_MIN_WIDTH,
  EntryInfo,
  executeIfExpressionDefinitionChanged,
  generateNextAvailableEntryName,
  generateUuid,
  getEntryKey,
  getHandlerConfiguration,
  InvocationProps,
  LogicType,
  resetEntry,
  RowsUpdateArgs,
  TableHeaderVisibility,
} from "../../api";
import { Table } from "../Table";
import { useBoxedExpressionEditorI18n } from "../../i18n";
import { ColumnInstance, DataRecord } from "react-table";
import { ContextEntryExpressionCell, getContextEntryInfoCell } from "../ContextExpression";
import * as _ from "lodash";
import { useBoxedExpression } from "../../context";
import { hashfy } from "../Resizer";

const DEFAULT_PARAMETER_NAME = "p-1";
const DEFAULT_PARAMETER_DATA_TYPE = DataType.Undefined;

export const InvocationExpression: React.FunctionComponent<InvocationProps> = (invocationProps: InvocationProps) => {
  const { i18n } = useBoxedExpressionEditorI18n();

  const rows = useMemo(() => {
    return (
      invocationProps.bindingEntries ?? [
        {
          entryInfo: {
            id: generateUuid(),
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
  }, [invocationProps.bindingEntries]);

  const { boxedExpressionEditorGWTService, setSupervisorHash, decisionNodeId } = useBoxedExpression();

  const spreadInvocationExpressionDefinition = useCallback(
    (invocationExpressionUpdated?: Partial<InvocationProps>) => {
      const updatedDefinition: InvocationProps = {
        id: invocationProps.id,
        logicType: LogicType.Invocation,
        name: invocationProps.name ?? DEFAULT_PARAMETER_NAME,
        dataType: invocationProps.dataType ?? DEFAULT_PARAMETER_DATA_TYPE,
        bindingEntries: rows as ContextEntries,
        invokedFunction: invocationProps.invokedFunction ?? "",
        entryInfoWidth: invocationProps.entryInfoWidth ?? DEFAULT_ENTRY_INFO_MIN_WIDTH,
        entryExpressionWidth: invocationProps.entryExpressionWidth ?? DEFAULT_ENTRY_EXPRESSION_MIN_WIDTH,
        ...invocationExpressionUpdated,
      };

      if (invocationProps.isHeadless) {
        const headlessDefinition = _.omit(updatedDefinition, ["name", "dataType", "isHeadless"]);
        executeIfExpressionDefinitionChanged(
          invocationProps,
          headlessDefinition,
          () => {
            invocationProps.onUpdatingRecursiveExpression?.(headlessDefinition);
          },
          ["bindingEntries", "invokedFunction", "entryInfoWidth", "entryExpressionWidth"]
        );
      } else {
        executeIfExpressionDefinitionChanged(
          invocationProps,
          updatedDefinition,
          () => {
            setSupervisorHash(hashfy(updatedDefinition));
            boxedExpressionEditorGWTService?.broadcastInvocationExpressionDefinition?.(updatedDefinition);
          },
          ["name", "dataType", "bindingEntries", "invokedFunction", "entryInfoWidth", "entryExpressionWidth"]
        );
      }
    },
    [boxedExpressionEditorGWTService, invocationProps, rows, setSupervisorHash]
  );

  const onBlurCallback = useCallback(
    (event) => {
      if (invocationProps.invokedFunction != event.target.value) {
        boxedExpressionEditorGWTService?.notifyUserAction();
      }
      spreadInvocationExpressionDefinition({ invokedFunction: event.target.value });
    },
    [boxedExpressionEditorGWTService, spreadInvocationExpressionDefinition, invocationProps.invokedFunction]
  );

  const headerCellElement = useMemo(
    () => (
      <div className="function-definition-container">
        <input
          className="function-definition pf-u-text-truncate"
          type="text"
          placeholder={i18n.enterFunction}
          defaultValue={invocationProps.invokedFunction}
          onBlur={onBlurCallback}
        />
      </div>
    ),
    [invocationProps.invokedFunction, onBlurCallback, i18n.enterFunction]
  );

  const setInfoWidth = useCallback(
    (newInfoWidth) => {
      spreadInvocationExpressionDefinition({ entryInfoWidth: newInfoWidth });
    },
    [spreadInvocationExpressionDefinition]
  );

  const setExpressionWidth = useCallback(
    (newEntryExpressionWidth) => {
      spreadInvocationExpressionDefinition({ entryExpressionWidth: newEntryExpressionWidth });
    },
    [spreadInvocationExpressionDefinition]
  );

  const columns = useMemo(
    () => [
      {
        label: invocationProps.name ?? DEFAULT_PARAMETER_NAME,
        accessor: decisionNodeId,
        dataType: invocationProps.dataType ?? DEFAULT_PARAMETER_DATA_TYPE,
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
                width: invocationProps.entryInfoWidth ?? DEFAULT_ENTRY_INFO_MIN_WIDTH,
                setWidth: setInfoWidth,
                minWidth: DEFAULT_ENTRY_INFO_MIN_WIDTH,
              },
              {
                accessor: "entryExpression",
                disableHandlerOnHeader: true,
                width: invocationProps.entryExpressionWidth ?? DEFAULT_ENTRY_EXPRESSION_MIN_WIDTH,
                setWidth: setExpressionWidth,
                minWidth: DEFAULT_ENTRY_EXPRESSION_MIN_WIDTH,
              },
            ],
          },
        ],
      },
    ],
    [
      invocationProps.name,
      invocationProps.dataType,
      invocationProps.entryInfoWidth,
      invocationProps.entryExpressionWidth,
      decisionNodeId,
      headerCellElement,
      setInfoWidth,
      setExpressionWidth,
    ]
  );

  const onColumnsUpdate = useCallback(
    ({ columns: [column] }: ColumnsUpdateArgs<ColumnInstance>) => {
      invocationProps.onUpdatingNameAndDataType?.(column.label as string, column.dataType);

      spreadInvocationExpressionDefinition({
        name: column.label as string,
        dataType: column.dataType,
      });
    },
    [invocationProps, spreadInvocationExpressionDefinition]
  );

  const onRowAdding = useCallback(() => {
    const generatedName = generateNextAvailableEntryName(
      rows.map((row: DataRecord & ContextEntryRecord) => row.entryInfo) as EntryInfo[],
      "p"
    );
    return {
      entryInfo: {
        id: generateUuid(),
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

  const getHeaderVisibility = useMemo(
    () => (invocationProps.isHeadless ? TableHeaderVisibility.SecondToLastLevel : TableHeaderVisibility.Full),
    [invocationProps.isHeadless]
  );

  const onRowsUpdate = useCallback(
    ({ rows }: RowsUpdateArgs<ContextEntryRecord>) => {
      spreadInvocationExpressionDefinition({ bindingEntries: [...rows] });
    },
    [spreadInvocationExpressionDefinition]
  );

  const getRowKeyCallback = useCallback((row) => getEntryKey(row), []);
  const resetEntryCallback = useCallback((row) => resetEntry(row), []);

  const defaultCell = useMemo(
    () => ({
      entryInfo: getContextEntryInfoCell(i18n.editParameter),
      entryExpression: ContextEntryExpressionCell,
    }),
    [i18n.editParameter]
  );

  const handlerConfiguration = useMemo(() => {
    return getHandlerConfiguration(i18n, i18n.parameters);
  }, [i18n]);

  return (
    <div className={`invocation-expression ${invocationProps.id}`}>
      <Table
        tableId={invocationProps.id}
        headerLevels={2}
        headerVisibility={getHeaderVisibility}
        skipLastHeaderGroup={true}
        defaultCell={defaultCell}
        columns={columns}
        rows={(rows ?? []) as DataRecord[]}
        onColumnsUpdate={onColumnsUpdate}
        onRowAdding={onRowAdding}
        onRowsUpdate={onRowsUpdate}
        handlerConfiguration={handlerConfiguration}
        getRowKey={getRowKeyCallback}
        resetRowCustomFunction={resetEntryCallback}
      />
    </div>
  );
};
