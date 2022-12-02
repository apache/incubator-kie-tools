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

import "./ListExpression.css";
import * as React from "react";
import { useCallback, useMemo } from "react";
import {
  ContextExpressionDefinitionEntry,
  DmnBuiltInDataType,
  executeIfExpressionDefinitionChanged,
  generateUuid,
  ListExpressionDefinition,
  LiteralExpressionDefinition,
  ExpressionDefinitionLogicType,
  BeeTableRowsUpdateArgs,
  BeeTableOperationHandlerConfig,
  BeeTableHeaderVisibility,
  BeeTableOperation,
  ROWGENERICTYPE,
} from "../../api";
import { ContextEntryExpressionCell } from "../ContextExpression";
import { BeeTable } from "../BeeTable";
import { useBoxedExpressionEditorI18n } from "../../i18n";
import * as ReactTable from "react-table";
import { hashfy } from "../Resizer";
import { useBoxedExpressionEditor } from "../BoxedExpressionEditor/BoxedExpressionEditorContext";

const LIST_EXPRESSION_MIN_WIDTH = 430;

export const ListExpression: React.FunctionComponent<ListExpressionDefinition> = (
  listExpression: ListExpressionDefinition
) => {
  const { i18n } = useBoxedExpressionEditorI18n();
  const { beeGwtService, setSupervisorHash } = useBoxedExpressionEditor();

  const generateLiteralExpression: () => LiteralExpressionDefinition = useCallback(
    () => ({
      id: generateUuid(),
      name: "",
      dataType: DmnBuiltInDataType.Undefined,
      logicType: ExpressionDefinitionLogicType.LiteralExpression,
      content: "",
    }),
    []
  );

  const operationHandlerConfig: BeeTableOperationHandlerConfig = useMemo(
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
    [
      i18n.rowOperations.clear,
      i18n.rowOperations.delete,
      i18n.rowOperations.insertAbove,
      i18n.rowOperations.insertBelow,
      i18n.rows,
    ]
  );

  const beeTableRows = useMemo(() => {
    if (listExpression.items === undefined || listExpression.items?.length === 0) {
      return [{ entryExpression: generateLiteralExpression() }];
    } else {
      return listExpression.items.map((item) => ({ entryExpression: item }));
    }
  }, [listExpression.items, generateLiteralExpression]);

  const spreadListExpressionDefinition = useCallback(
    (updatedListExpression?: Partial<ListExpressionDefinition>) => {
      const updatedDefinition: ListExpressionDefinition = {
        id: listExpression.id,
        name: listExpression.name,
        dataType: listExpression.dataType,
        logicType: ExpressionDefinitionLogicType.List,
        width: listExpression.width ?? LIST_EXPRESSION_MIN_WIDTH,
        ...updatedListExpression,
      };

      updatedDefinition.items = (updatedListExpression?.items ? updatedListExpression.items : beeTableRows).map(
        (listItem: ROWGENERICTYPE) => listItem.entryExpression
      );

      executeIfExpressionDefinitionChanged(
        listExpression,
        updatedDefinition,
        () => {
          if (listExpression.isHeadless) {
            listExpression.onUpdatingRecursiveExpression?.(updatedDefinition);
          } else {
            setSupervisorHash(hashfy(updatedDefinition));
            beeGwtService?.broadcastListExpressionDefinition?.(updatedDefinition);
          }
        },
        ["width", "items"]
      );
    },
    [beeGwtService, listExpression, beeTableRows, setSupervisorHash]
  );

  const setListWidth = useCallback(
    (newInfoWidth) => {
      spreadListExpressionDefinition({ width: newInfoWidth });
    },
    [spreadListExpressionDefinition]
  );

  const beeTableColumns = useMemo<ReactTable.ColumnInstance<ROWGENERICTYPE>[]>(
    () => [
      {
        accessor: "list",
        width: listExpression.width ?? LIST_EXPRESSION_MIN_WIDTH,
        setWidth: setListWidth,
      } as any, // FIXME: Tiago -> Remove this!!,
    ],
    [listExpression.width, setListWidth]
  );

  const resetRowCustomFunction = useCallback((row: ROWGENERICTYPE) => {
    return { entryExpression: { id: row.entryExpression.id } };
  }, []);

  const onNewRow = useCallback(
    () => ({
      entryExpression: generateLiteralExpression(),
    }),
    [generateLiteralExpression]
  );

  const onRowsUpdate = useCallback(
    ({ rows }: BeeTableRowsUpdateArgs<ROWGENERICTYPE>) => {
      const newEntryExpressions = rows.map((row) => {
        return { entryExpression: row.entryExpression };
      });
      spreadListExpressionDefinition({
        items: newEntryExpressions as any,
      });
    },
    [spreadListExpressionDefinition]
  );

  const getRowKey = useCallback((row: ReactTable.Row<ROWGENERICTYPE>) => {
    return (row.original as ContextExpressionDefinitionEntry).entryExpression.id!;
  }, []);

  const defaultCellByColumnId = useMemo(
    () => ({
      list: ContextEntryExpressionCell,
    }),
    []
  );

  return (
    <div className={`${listExpression.id} list-expression`}>
      <BeeTable
        tableId={listExpression.id}
        headerVisibility={BeeTableHeaderVisibility.None}
        defaultCellByColumnId={defaultCellByColumnId}
        columns={beeTableColumns}
        rows={beeTableRows}
        onRowsUpdate={onRowsUpdate}
        onNewRow={onNewRow}
        operationHandlerConfig={operationHandlerConfig}
        getRowKey={getRowKey}
        resetRowCustomFunction={resetRowCustomFunction}
      />
    </div>
  );
};
