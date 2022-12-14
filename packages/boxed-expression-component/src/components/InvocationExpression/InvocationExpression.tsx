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
  CONTEXT_ENTRY_INFO_MIN_WIDTH,
  getNextAvailableContextExpressionEntryName,
  generateUuid,
  getOperationHandlerConfig,
  InvocationExpressionDefinition,
  ExpressionDefinitionLogicType,
  resetContextExpressionEntry,
  BeeTableRowsUpdateArgs,
  BeeTableHeaderVisibility,
  BeeTableProps,
} from "../../api";
import { BeeTable } from "../BeeTable";
import { useBoxedExpressionEditorI18n } from "../../i18n";
import * as ReactTable from "react-table";
import { ContextEntryExpressionCell, ContextEntryInfoCell, ContextEntryInfoCellProps } from "../ContextExpression";
import * as _ from "lodash";
import { useBoxedExpressionEditor } from "../BoxedExpressionEditor/BoxedExpressionEditorContext";

type ROWTYPE = ContextExpressionDefinitionEntry;

const DEFAULT_PARAMETER_NAME = "p-1";
const DEFAULT_PARAMETER_DATA_TYPE = DmnBuiltInDataType.Undefined;
const DEFAULT_PARAMETER_LOGIC_TYPE = ExpressionDefinitionLogicType.Undefined;

export const InvocationExpression: React.FunctionComponent<InvocationExpressionDefinition> = (
  invocation: InvocationExpressionDefinition
) => {
  const { i18n } = useBoxedExpressionEditorI18n();

  const beeTableRows: ROWTYPE[] = useMemo(() => {
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
            logicType: DEFAULT_PARAMETER_LOGIC_TYPE,
            isHeadless: true,
          },
          nameAndDataTypeSynchronized: true,
        },
      ]
    );
  }, [invocation.bindingEntries]);

  const { decisionNodeId } = useBoxedExpressionEditor();

  const onBlurCallback = useCallback((event) => {}, []);

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

  const setInfoWidth = useCallback((newInfoWidth) => {}, []);

  const setExpressionWidth = useCallback((newEntryExpressionWidth) => {}, []);

  const beeTableColumns = useMemo<ReactTable.Column<ROWTYPE>[]>(
    () => [
      {
        label: invocation.name ?? DEFAULT_PARAMETER_NAME,
        accessor: decisionNodeId as keyof ROWTYPE,
        dataType: invocation.dataType ?? DEFAULT_PARAMETER_DATA_TYPE,
        disableOperationHandlerOnHeader: true,
        isRowIndexColumn: false,
        width: undefined,
        columns: [
          {
            headerCellElement,
            accessor: "functionDefinition" as keyof ROWTYPE,
            disableOperationHandlerOnHeader: true,
            isRowIndexColumn: false,
            label: "functionDefinition",
            dataType: undefined as any, // FIXME: Tiago -> This column shouldn't have a datatype, however, the type system asks for it.
            width: undefined,
            columns: [
              {
                accessor: "entryInfo",
                disableOperationHandlerOnHeader: true,
                width: invocation.entryInfoWidth ?? CONTEXT_ENTRY_INFO_MIN_WIDTH,
                setWidth: setInfoWidth,
                minWidth: CONTEXT_ENTRY_INFO_MIN_WIDTH,
                isRowIndexColumn: false,
                label: "entryInfo",
                dataType: DEFAULT_PARAMETER_DATA_TYPE,
              },
              {
                accessor: "entryExpression",
                disableOperationHandlerOnHeader: true,
                width: undefined,
                isRowIndexColumn: false,
                label: "entryExpression",
                dataType: DEFAULT_PARAMETER_DATA_TYPE,
              },
            ],
          },
        ],
      },
    ],
    [invocation.name, invocation.dataType, invocation.entryInfoWidth, decisionNodeId, headerCellElement, setInfoWidth]
  );

  const onColumnsUpdate = useCallback(({ columns: [column] }: BeeTableColumnsUpdateArgs<ROWTYPE>) => {}, []);

  const onNewRow = useCallback<() => ROWTYPE>(() => {
    const generatedName = getNextAvailableContextExpressionEntryName(
      beeTableRows.map((row) => row.entryInfo),
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
        logicType: DEFAULT_PARAMETER_LOGIC_TYPE,
      },
      nameAndDataTypeSynchronized: true,
    };
  }, [beeTableRows]);

  const headerVisibility = useMemo(
    () => (invocation.isHeadless ? BeeTableHeaderVisibility.SecondToLastLevel : BeeTableHeaderVisibility.Full),
    [invocation.isHeadless]
  );

  const onRowsUpdate = useCallback(({ rows }: BeeTableRowsUpdateArgs<ROWTYPE>) => {}, []);

  const getRowKey = useCallback((row: ReactTable.Row<ROWTYPE>) => {
    return row.original.entryInfo.id;
  }, []);

  const defaultCellByColumnId: BeeTableProps<ROWTYPE>["defaultCellByColumnId"] = useMemo(
    () => ({
      entryInfo: (props: ContextEntryInfoCellProps) =>
        ContextEntryInfoCell({ ...props, editInfoPopoverLabel: i18n.editParameter }),
      entryExpression: ContextEntryExpressionCell,
    }),
    [i18n]
  );

  const operationHandlerConfig = useMemo(() => {
    return getOperationHandlerConfig(i18n, i18n.parameters);
  }, [i18n]);

  return (
    <div className={`invocation-expression ${invocation.id}`}>
      <BeeTable<ROWTYPE>
        tableId={invocation.id}
        headerLevelCount={2}
        headerVisibility={headerVisibility}
        skipLastHeaderGroup={true}
        defaultCellByColumnId={defaultCellByColumnId}
        columns={beeTableColumns}
        rows={beeTableRows}
        onColumnsUpdate={onColumnsUpdate}
        onNewRow={onNewRow}
        onRowsUpdate={onRowsUpdate}
        operationHandlerConfig={operationHandlerConfig}
        getRowKey={getRowKey}
        resetRowCustomFunction={resetContextExpressionEntry}
      />
    </div>
  );
};
