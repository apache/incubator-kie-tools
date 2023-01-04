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
  ListExpressionDefinition,
  UndefinedExpressionDefinition,
} from "../../api";
import { useBoxedExpressionEditorI18n } from "../../i18n";
import { BeeTable } from "../../table/BeeTable";
import { useBoxedExpressionEditorDispatch } from "../BoxedExpressionEditor/BoxedExpressionEditorContext";
import "./ListExpression.css";
import { ListItemCell } from "./ListItemCell";

type ROWTYPE = ContextExpressionDefinitionEntry;

export const ListExpression: React.FunctionComponent<ListExpressionDefinition> = (
  listExpression: ListExpressionDefinition
) => {
  const { i18n } = useBoxedExpressionEditorI18n();
  const { setExpression } = useBoxedExpressionEditorDispatch();

  const beeTableOperationConfig = useMemo<BeeTableOperationConfig>(
    () => [
      {
        group: i18n.rows,
        items: [
          { name: i18n.rowOperations.insertAbove, type: BeeTableOperation.RowInsertAbove },
          { name: i18n.rowOperations.insertBelow, type: BeeTableOperation.RowInsertBelow },
          { name: i18n.rowOperations.delete, type: BeeTableOperation.RowDelete },
          { name: i18n.rowOperations.clear, type: BeeTableOperation.RowClear },
        ],
      },
    ],
    [i18n]
  );

  const beeTableRows = useMemo(() => {
    return listExpression.items.map((item) => ({
      entryInfo: undefined as any, // FIXME: Tiago -> Not ideal.
      entryExpression: item,
    }));
  }, [listExpression.items]);

  const beeTableColumns = useMemo<ReactTable.Column<ROWTYPE>[]>(
    () => [
      {
        accessor: "list" as any,
        label: "",
        dataType: undefined as any,
        isRowIndexColumn: false,
        width: undefined,
      },
    ],
    []
  );

  const getRowKey = useCallback((row: ReactTable.Row<ROWTYPE>) => {
    return row.original.entryExpression.id!;
  }, []);

  const cellComponentByColumnId: BeeTableProps<ROWTYPE>["cellComponentByColumnId"] = useMemo(
    () => ({ list: ListItemCell }),
    []
  );

  const onRowAdded = useCallback(
    (args: { beforeIndex: number }) => {
      setExpression((prev: ListExpressionDefinition) => {
        const newLiteralExpression: UndefinedExpressionDefinition = {
          id: generateUuid(),
          logicType: ExpressionDefinitionLogicType.Undefined,
          dataType: DmnBuiltInDataType.Undefined,
          isHeadless: true,
        };
        const newItems = [...prev.items];
        newItems.splice(args.beforeIndex, 0, newLiteralExpression);
        return { ...prev, items: newItems };
      });
    },
    [setExpression]
  );

  return (
    <div className={`${listExpression.id} list-expression`}>
      <BeeTable<ROWTYPE>
        tableId={listExpression.id}
        headerVisibility={BeeTableHeaderVisibility.None}
        cellComponentByColumnId={cellComponentByColumnId}
        columns={beeTableColumns}
        rows={beeTableRows}
        operationConfig={beeTableOperationConfig}
        getRowKey={getRowKey}
        onRowAdded={onRowAdded}
      />
    </div>
  );
};
