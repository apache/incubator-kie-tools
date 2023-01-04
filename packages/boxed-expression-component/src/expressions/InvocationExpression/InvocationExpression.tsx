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

import * as React from "react";
import { useCallback, useMemo } from "react";
import * as ReactTable from "react-table";
import {
  BeeTableHeaderVisibility,
  BeeTableOperation,
  BeeTableOperationConfig,
  BeeTableProps,
  ContextExpressionDefinitionEntry,
  DmnBuiltInDataType,
  ExpressionDefinitionLogicType,
  generateUuid,
  getNextAvailablePrefixedName,
  InvocationExpressionDefinition,
} from "../../api";
import { useBoxedExpressionEditorI18n } from "../../i18n";
import { BeeTable, BeeTableColumnUpdate } from "../../table/BeeTable";
import {
  useBoxedExpressionEditor,
  useBoxedExpressionEditorDispatch,
} from "../BoxedExpressionEditor/BoxedExpressionEditorContext";
import { ContextEntryInfoCell, CONTEXT_ENTRY_INFO_MIN_WIDTH } from "../ContextExpression";
import { ArgumentEntryExpressionCell } from "./ArgumentEntryExpressionCell";
import "./InvocationExpression.css";

type ROWTYPE = ContextExpressionDefinitionEntry;

export const INVOCATION_EXPRESSION_DEFAULT_PARAMETER_NAME = "p-1";
export const INVOCATION_EXPRESSION_DEFAULT_PARAMETER_DATA_TYPE = DmnBuiltInDataType.Undefined;
export const INVOCATION_EXPRESSION_DEFAULT_PARAMETER_LOGIC_TYPE = ExpressionDefinitionLogicType.Undefined;

export const InvocationExpression: React.FunctionComponent<InvocationExpressionDefinition> = (
  invocation: InvocationExpressionDefinition
) => {
  const { i18n } = useBoxedExpressionEditorI18n();

  const { setExpression } = useBoxedExpressionEditorDispatch();

  const beeTableRows: ROWTYPE[] = useMemo(() => {
    return invocation.bindingEntries ?? [];
  }, [invocation.bindingEntries]);

  const { decisionNodeId } = useBoxedExpressionEditor();

  const setParametersInfoColumnWidth = useCallback(
    (newParametersInfoColumnWidth) => {
      setExpression((prev) => ({
        ...prev,
        entryInfoWidth: newParametersInfoColumnWidth,
      }));
    },
    [setExpression]
  );

  const beeTableColumns = useMemo<ReactTable.Column<ROWTYPE>[]>(
    () => [
      {
        label: invocation.name ?? INVOCATION_EXPRESSION_DEFAULT_PARAMETER_NAME,
        accessor: decisionNodeId as keyof ROWTYPE,
        dataType: invocation.dataType ?? INVOCATION_EXPRESSION_DEFAULT_PARAMETER_DATA_TYPE,
        isRowIndexColumn: false,
        width: undefined,
        columns: [
          {
            accessor: "functionName" as keyof ROWTYPE,
            label: invocation.invokedFunction ?? "Function name",
            isRowIndexColumn: false,
            isInlineEditable: true,
            dataType: undefined as any, // FIXME: Tiago -> This column shouldn't have a datatype, however, the type system asks for it.
            width: undefined,
            groupType: "invokedFunctionName",
            columns: [
              {
                accessor: "parametersInfo" as any,
                label: "parametersInfo",
                isRowIndexColumn: false,
                dataType: INVOCATION_EXPRESSION_DEFAULT_PARAMETER_DATA_TYPE,
                minWidth: CONTEXT_ENTRY_INFO_MIN_WIDTH,
                width: invocation.entryInfoWidth ?? CONTEXT_ENTRY_INFO_MIN_WIDTH,
                setWidth: setParametersInfoColumnWidth,
              },
              {
                accessor: "argumentExpression" as any,
                label: "argumentExpression",
                isRowIndexColumn: false,
                dataType: INVOCATION_EXPRESSION_DEFAULT_PARAMETER_DATA_TYPE,
                width: undefined,
              },
            ],
          },
        ],
      },
    ],
    [
      invocation.name,
      invocation.dataType,
      invocation.invokedFunction,
      invocation.entryInfoWidth,
      decisionNodeId,
      setParametersInfoColumnWidth,
    ]
  );

  const onColumnUpdates = useCallback(
    (columnUpdates: BeeTableColumnUpdate<ROWTYPE>[]) => {
      for (const u of columnUpdates) {
        if (u.column.originalId === "functionName") {
          setExpression((prev) => ({
            ...prev,
            invokedFunction: u.name,
          }));
        }
      }
    },
    [setExpression]
  );

  const headerVisibility = useMemo(
    () => (invocation.isHeadless ? BeeTableHeaderVisibility.SecondToLastLevel : BeeTableHeaderVisibility.AllLevels),
    [invocation.isHeadless]
  );

  const getRowKey = useCallback((row: ReactTable.Row<ROWTYPE>) => {
    return row.original.entryInfo.id;
  }, []);

  const updateParameterInfo = useCallback(
    (rowIndex: number, newEntry: ContextExpressionDefinitionEntry) => {
      setExpression((prev: InvocationExpressionDefinition) => {
        const newArgumentEntries = [...(prev.bindingEntries ?? [])];
        newArgumentEntries[rowIndex] = newEntry;
        return { ...prev, bindingEntries: newArgumentEntries };
      });
    },
    [setExpression]
  );

  const cellComponentByColumnId: BeeTableProps<ROWTYPE>["cellComponentByColumnId"] = useMemo(
    () => ({
      parametersInfo: (props) => <ContextEntryInfoCell {...props} onEntryUpdate={updateParameterInfo} />,
      argumentExpression: (props) => <ArgumentEntryExpressionCell {...props} />,
    }),
    [updateParameterInfo]
  );

  const beeTableOperationConfig = useMemo<BeeTableOperationConfig>(() => {
    return [
      {
        group: i18n.parameters,
        items: [
          { name: i18n.rowOperations.insertAbove, type: BeeTableOperation.RowInsertAbove },
          { name: i18n.rowOperations.insertBelow, type: BeeTableOperation.RowInsertBelow },
          { name: i18n.rowOperations.delete, type: BeeTableOperation.RowDelete },
          { name: i18n.rowOperations.clear, type: BeeTableOperation.RowClear },
        ],
      },
    ];
  }, [i18n]);

  const onRowAdded = useCallback(
    (args: { beforeIndex: number }) => {
      setExpression((prev: InvocationExpressionDefinition) => {
        const newArgumentEntries = [...(prev.bindingEntries ?? [])];
        newArgumentEntries.splice(args.beforeIndex, 0, {
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
              (prev.bindingEntries ?? []).map((e) => e.entryInfo.name),
              "p"
            ),
          },
        });

        return {
          ...prev,
          bindingEntries: newArgumentEntries,
        };
      });
    },
    [setExpression]
  );

  return (
    <div className={`invocation-expression ${invocation.id}`}>
      <BeeTable<ROWTYPE>
        tableId={invocation.id}
        headerLevelCount={2}
        headerVisibility={headerVisibility}
        skipLastHeaderGroup={true}
        cellComponentByColumnId={cellComponentByColumnId}
        columns={beeTableColumns}
        rows={beeTableRows}
        onColumnUpdates={onColumnUpdates}
        operationConfig={beeTableOperationConfig}
        getRowKey={getRowKey}
        onRowAdded={onRowAdded}
      />
    </div>
  );
};
