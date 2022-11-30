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
  BeeTableColumnsUpdateArgs,
  ContextExpressionDefinitionEntry,
  DmnBuiltInDataType,
  DEFAULT_ENTRY_EXPRESSION_MIN_WIDTH,
  DEFAULT_ENTRY_INFO_MIN_WIDTH,
  executeIfExpressionDefinitionChanged,
  generateNextAvailableEntryName,
  generateUuid,
  getEntryKey,
  operationHandlerConfig as getOperationHandlerConfig,
  InvocationExpressionDefinition,
  ExpressionDefinitionLogicType,
  resetEntry,
  BeeTableRowsUpdateArgs,
  BeeTableHeaderVisibility,
} from "../../api";
import { BeeTable } from "../BeeTable";
import { useBoxedExpressionEditorI18n } from "../../i18n";
import * as ReactTable from "react-table";
import { ContextEntryExpressionCell, getContextEntryInfoCell } from "../ContextExpression";
import * as _ from "lodash";
import { useBoxedExpressionEditor } from "../BoxedExpressionEditor/BoxedExpressionEditorContext";
import { hashfy } from "../Resizer";

const DEFAULT_PARAMETER_NAME = "p-1";
const DEFAULT_PARAMETER_DATA_TYPE = DmnBuiltInDataType.Undefined;

export const InvocationExpression: React.FunctionComponent<InvocationExpressionDefinition> = (
  invocation: InvocationExpressionDefinition
) => {
  const { i18n } = useBoxedExpressionEditorI18n();

  const rows = useMemo(() => {
    return (
      invocation.bindingEntries ?? [
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
        } as ReactTable.DataRecord,
      ]
    );
  }, [invocation.bindingEntries]);

  const { beeGwtService, setSupervisorHash, decisionNodeId } = useBoxedExpressionEditor();

  const spreadInvocationExpressionDefinition = useCallback(
    (invocationExpressionUpdated?: Partial<InvocationExpressionDefinition>) => {
      const updatedDefinition: InvocationExpressionDefinition = {
        id: invocation.id,
        logicType: ExpressionDefinitionLogicType.Invocation,
        name: invocation.name ?? DEFAULT_PARAMETER_NAME,
        dataType: invocation.dataType ?? DEFAULT_PARAMETER_DATA_TYPE,
        bindingEntries: rows as ContextExpressionDefinitionEntry[],
        invokedFunction: invocation.invokedFunction ?? "",
        entryInfoWidth: invocation.entryInfoWidth ?? DEFAULT_ENTRY_INFO_MIN_WIDTH,
        entryExpressionWidth: invocation.entryExpressionWidth ?? DEFAULT_ENTRY_EXPRESSION_MIN_WIDTH,
        ...invocationExpressionUpdated,
      };

      if (invocation.isHeadless) {
        const headlessDefinition = _.omit(updatedDefinition, ["name", "dataType", "isHeadless"]);
        executeIfExpressionDefinitionChanged(
          invocation,
          headlessDefinition,
          () => {
            invocation.onUpdatingRecursiveExpression?.(headlessDefinition);
          },
          ["bindingEntries", "invokedFunction", "entryInfoWidth", "entryExpressionWidth"]
        );
      } else {
        executeIfExpressionDefinitionChanged(
          invocation,
          updatedDefinition,
          () => {
            setSupervisorHash(hashfy(updatedDefinition));
            beeGwtService?.broadcastInvocationExpressionDefinition?.(updatedDefinition);
          },
          ["name", "dataType", "bindingEntries", "invokedFunction", "entryInfoWidth", "entryExpressionWidth"]
        );
      }
    },
    [beeGwtService, invocation, rows, setSupervisorHash]
  );

  const onBlurCallback = useCallback(
    (event) => {
      if (invocation.invokedFunction != event.target.value) {
        beeGwtService?.notifyUserAction();
      }
      spreadInvocationExpressionDefinition({ invokedFunction: event.target.value });
    },
    [beeGwtService, spreadInvocationExpressionDefinition, invocation.invokedFunction]
  );

  const headerCellElement = useMemo(
    () => (
      <div className="function-definition-container">
        <input
          className="function-definition pf-u-text-truncate"
          type="text"
          placeholder={i18n.enterFunction}
          defaultValue={invocation.invokedFunction}
          onBlur={onBlurCallback}
        />
      </div>
    ),
    [invocation.invokedFunction, onBlurCallback, i18n.enterFunction]
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
        label: invocation.name ?? DEFAULT_PARAMETER_NAME,
        accessor: decisionNodeId,
        dataType: invocation.dataType ?? DEFAULT_PARAMETER_DATA_TYPE,
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
                width: invocation.entryInfoWidth ?? DEFAULT_ENTRY_INFO_MIN_WIDTH,
                setWidth: setInfoWidth,
                minWidth: DEFAULT_ENTRY_INFO_MIN_WIDTH,
              },
              {
                accessor: "entryExpression",
                disableHandlerOnHeader: true,
                width: invocation.entryExpressionWidth ?? DEFAULT_ENTRY_EXPRESSION_MIN_WIDTH,
                setWidth: setExpressionWidth,
                minWidth: DEFAULT_ENTRY_EXPRESSION_MIN_WIDTH,
              },
            ],
          },
        ],
      },
    ],
    [
      invocation.name,
      invocation.dataType,
      invocation.entryInfoWidth,
      invocation.entryExpressionWidth,
      decisionNodeId,
      headerCellElement,
      setInfoWidth,
      setExpressionWidth,
    ]
  );

  const onColumnsUpdate = useCallback(
    ({ columns: [column] }: BeeTableColumnsUpdateArgs<ReactTable.ColumnInstance>) => {
      invocation.onUpdatingNameAndDataType?.(column.label as string, column.dataType);

      spreadInvocationExpressionDefinition({
        name: column.label as string,
        dataType: column.dataType,
      });
    },
    [invocation, spreadInvocationExpressionDefinition]
  );

  const onRowAdding = useCallback(() => {
    const generatedName = generateNextAvailableEntryName(
      rows.map((row: ReactTable.DataRecord & ContextExpressionDefinitionEntry) => row.entryInfo),
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
    () => (invocation.isHeadless ? BeeTableHeaderVisibility.SecondToLastLevel : BeeTableHeaderVisibility.Full),
    [invocation.isHeadless]
  );

  const onRowsUpdate = useCallback(
    ({ rows }: BeeTableRowsUpdateArgs<ContextExpressionDefinitionEntry>) => {
      spreadInvocationExpressionDefinition({ bindingEntries: [...rows] });
    },
    [spreadInvocationExpressionDefinition]
  );

  const getRowKeyCallback = useCallback((row) => getEntryKey(row), []);
  const resetEntryCallback = useCallback((row) => resetEntry(row), []);

  const defaultCellByColumnName = useMemo(
    () => ({
      entryInfo: getContextEntryInfoCell(i18n.editParameter),
      entryExpression: ContextEntryExpressionCell,
    }),
    [i18n.editParameter]
  );

  const operationHandlerConfig = useMemo(() => {
    return getOperationHandlerConfig(i18n, i18n.parameters);
  }, [i18n]);

  return (
    <div className={`invocation-expression ${invocation.id}`}>
      <BeeTable
        tableId={invocation.id}
        headerLevels={2}
        headerVisibility={getHeaderVisibility}
        skipLastHeaderGroup={true}
        defaultCellByColumnId={defaultCellByColumnName}
        columns={columns}
        rows={rows ?? []}
        onColumnsUpdate={onColumnsUpdate}
        onRowAdding={onRowAdding}
        onRowsUpdate={onRowsUpdate}
        operationHandlerConfig={operationHandlerConfig}
        getRowKey={getRowKeyCallback}
        resetRowCustomFunction={resetEntryCallback}
      />
    </div>
  );
};
