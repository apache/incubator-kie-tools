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
  ContextExpressionDefinitionEntry,
  DmnBuiltInDataType,
  generateUuid,
  InvocationExpressionDefinition,
  ExpressionDefinitionLogicType,
  BeeTableRowsUpdateArgs,
  BeeTableHeaderVisibility,
  BeeTableProps,
  getNextAvailablePrefixedName,
  BeeTableOperation,
  BeeTableOperationConfig,
} from "../../api";
import { BeeTable, BeeTableColumnUpdate } from "../../table/BeeTable";
import { useBoxedExpressionEditorI18n } from "../../i18n";
import * as ReactTable from "react-table";
import {
  ContextEntryExpressionCell,
  ContextEntryInfoCell,
  ContextEntryInfoCellProps,
  CONTEXT_ENTRY_INFO_MIN_WIDTH,
} from "../ContextExpression";
import * as _ from "lodash";
import {
  useBoxedExpressionEditor,
  useBoxedExpressionEditorDispatch,
} from "../BoxedExpressionEditor/BoxedExpressionEditorContext";

type ROWTYPE = ContextExpressionDefinitionEntry;

export const INVOCATION_EXPRESSION_DEFAULT_PARAMETER_NAME = "p-1";
export const INVOCATION_EXPRESSION_DEFAULT_PARAMETER_DATA_TYPE = DmnBuiltInDataType.Undefined;
export const INVOCATION_EXPRESSION_DEFAULT_PARAMETER_LOGIC_TYPE = ExpressionDefinitionLogicType.Undefined;

export const InvocationExpression: React.FunctionComponent<InvocationExpressionDefinition> = (
  invocation: InvocationExpressionDefinition
) => {
  const { i18n } = useBoxedExpressionEditorI18n();

  const beeTableRows: ROWTYPE[] = useMemo(() => {
    return invocation.bindingEntries ?? [];
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

  const beeTableColumns = useMemo<ReactTable.Column<ROWTYPE>[]>(
    () => [
      {
        label: invocation.name ?? INVOCATION_EXPRESSION_DEFAULT_PARAMETER_NAME,
        accessor: decisionNodeId as keyof ROWTYPE,
        dataType: invocation.dataType ?? INVOCATION_EXPRESSION_DEFAULT_PARAMETER_DATA_TYPE,
        disableContextMenuOnHeader: true,
        isRowIndexColumn: false,
        width: undefined,
        columns: [
          {
            headerCellElement,
            accessor: "functionDefinition" as keyof ROWTYPE,
            disableContextMenuOnHeader: true,
            isRowIndexColumn: false,
            label: "functionDefinition",
            dataType: undefined as any, // FIXME: Tiago -> This column shouldn't have a datatype, however, the type system asks for it.
            width: undefined,
            columns: [
              {
                accessor: "entryInfo",
                disableContextMenuOnHeader: true,
                width: invocation.entryInfoWidth ?? CONTEXT_ENTRY_INFO_MIN_WIDTH,
                setWidth: setInfoWidth,
                minWidth: CONTEXT_ENTRY_INFO_MIN_WIDTH,
                isRowIndexColumn: false,
                label: "entryInfo",
                dataType: INVOCATION_EXPRESSION_DEFAULT_PARAMETER_DATA_TYPE,
              },
              {
                accessor: "entryExpression",
                disableContextMenuOnHeader: true,
                width: undefined,
                isRowIndexColumn: false,
                label: "entryExpression",
                dataType: INVOCATION_EXPRESSION_DEFAULT_PARAMETER_DATA_TYPE,
              },
            ],
          },
        ],
      },
    ],
    [invocation.name, invocation.dataType, invocation.entryInfoWidth, decisionNodeId, headerCellElement, setInfoWidth]
  );

  const onColumnUpdates = useCallback((columnUpdates: BeeTableColumnUpdate<ROWTYPE>[]) => {
    /** */
  }, []);

  const headerVisibility = useMemo(
    () => (invocation.isHeadless ? BeeTableHeaderVisibility.SecondToLastLevel : BeeTableHeaderVisibility.Full),
    [invocation.isHeadless]
  );

  const onRowUpdates = useCallback(({ rows }: BeeTableRowsUpdateArgs<ROWTYPE>) => {
    //
  }, []);

  const getRowKey = useCallback((row: ReactTable.Row<ROWTYPE>) => {
    return row.original.entryInfo.id;
  }, []);

  const cellComponentByColumnId: BeeTableProps<ROWTYPE>["cellComponentByColumnId"] = useMemo(
    () => ({
      entryInfo: (props: ContextEntryInfoCellProps) =>
        ContextEntryInfoCell({ ...props, editInfoPopoverLabel: i18n.editParameter }),
      entryExpression: ContextEntryExpressionCell,
    }),
    [i18n]
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

  const { setExpression } = useBoxedExpressionEditorDispatch();

  const onRowAdded = useCallback(
    (args: { beforeIndex: number }) => {
      setExpression((prev: InvocationExpressionDefinition) => {
        const newBindingEntries = [...(prev.bindingEntries ?? [])];
        newBindingEntries.splice(args.beforeIndex, 0, {
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
          bindingEntries: newBindingEntries,
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
        onRowUpdates={onRowUpdates}
        operationConfig={beeTableOperationConfig}
        getRowKey={getRowKey}
        onRowAdded={onRowAdded}
      />
    </div>
  );
};
