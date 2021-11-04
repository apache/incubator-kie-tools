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
import { useCallback, useContext, useMemo } from "react";
import {
  ContextEntryRecord,
  DataType,
  executeIfExpressionDefinitionChanged,
  ExpressionProps,
  ListProps,
  LiteralExpressionProps,
  LogicType,
  TableHandlerConfiguration,
  TableHeaderVisibility,
  TableOperation,
} from "../../api";
import { ContextEntryExpressionCell } from "../ContextExpression";
import { Table } from "../Table";
import { useBoxedExpressionEditorI18n } from "../../i18n";
import { DataRecord, Row } from "react-table";
import { hashfy } from "../Resizer";
import { BoxedExpressionGlobalContext } from "../../context";
import nextId from "react-id-generator";

const LIST_EXPRESSION_MIN_WIDTH = 430;

export const ListExpression: React.FunctionComponent<ListProps> = (listExpression: ListProps) => {
  const { i18n } = useBoxedExpressionEditorI18n();
  const { setSupervisorHash } = useContext(BoxedExpressionGlobalContext);

  const generateLiteralExpression = useMemo(
    () =>
      ({
        uid: nextId(),
        name: "",
        dataType: DataType.Undefined,
        logicType: LogicType.LiteralExpression,
        content: "",
      } as LiteralExpressionProps),
    []
  );

  const handlerConfiguration: TableHandlerConfiguration = useMemo(
    () => [
      {
        group: i18n.rows,
        items: [
          { name: i18n.rowOperations.insertAbove, type: TableOperation.RowInsertAbove },
          { name: i18n.rowOperations.insertBelow, type: TableOperation.RowInsertBelow },
          { name: i18n.rowOperations.delete, type: TableOperation.RowDelete },
          { name: i18n.rowOperations.clear, type: TableOperation.RowClear },
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

  const items = useMemo(() => {
    if (listExpression.items === undefined || listExpression.items?.length === 0) {
      return [{ entryExpression: generateLiteralExpression }];
    } else {
      return listExpression.items.map((item) => ({ entryExpression: item }));
    }
  }, [listExpression.items, generateLiteralExpression]);

  const spreadListExpressionDefinition = useCallback(
    (updatedListExpression?: Partial<ListProps>) => {
      const updatedDefinition: Partial<ListProps> = {
        uid: listExpression.uid,
        name: listExpression.name,
        dataType: listExpression.dataType,
        logicType: LogicType.List,
        width: listExpression.width ?? LIST_EXPRESSION_MIN_WIDTH,
        ...updatedListExpression,
      };

      updatedDefinition.items = (updatedListExpression?.items ? updatedListExpression.items : items).map(
        (listItem: DataRecord) => listItem.entryExpression as ExpressionProps
      );

      if (listExpression.isHeadless) {
        listExpression.onUpdatingRecursiveExpression?.(updatedDefinition);
      } else {
        executeIfExpressionDefinitionChanged(
          listExpression,
          updatedDefinition,
          () => {
            setSupervisorHash(hashfy(updatedDefinition));
            window.beeApi?.broadcastListExpressionDefinition?.(updatedDefinition as ListProps);
          },
          ["width", "items"]
        );
      }
    },
    [listExpression, items, setSupervisorHash]
  );

  const setListWidth = useCallback(
    (newInfoWidth) => {
      spreadListExpressionDefinition({ width: newInfoWidth });
    },
    [spreadListExpressionDefinition]
  );

  const columns = useMemo(
    () => [{ accessor: "list", width: listExpression.width ?? LIST_EXPRESSION_MIN_WIDTH, setWidth: setListWidth }],
    [listExpression.width, setListWidth]
  );

  const resetRowCustomFunction = useCallback((row: DataRecord) => {
    return { entryExpression: { uid: (row.entryExpression as ExpressionProps).uid } };
  }, []);

  const onRowAdding = useCallback(
    () => ({
      entryExpression: generateLiteralExpression,
    }),
    [generateLiteralExpression]
  );

  const onRowsUpdate = useCallback(
    (newItems: any[]) => {
      const newEntryExpressions = newItems.map((newItem) => {
        return { entryExpression: newItem.entryExpression };
      });
      spreadListExpressionDefinition({
        items: newEntryExpressions as ExpressionProps[],
      });
    },
    [spreadListExpressionDefinition]
  );

  const getRowKey = useCallback((row: Row) => {
    return (row.original as ContextEntryRecord).entryExpression.uid!;
  }, []);

  const defaultCell = useMemo(
    () => ({
      list: ContextEntryExpressionCell,
    }),
    []
  );

  return (
    <div className="list-expression">
      <Table
        tableId={listExpression.uid}
        headerVisibility={TableHeaderVisibility.None}
        defaultCell={defaultCell}
        columns={columns}
        rows={items as DataRecord[]}
        onRowsUpdate={onRowsUpdate}
        onRowAdding={onRowAdding}
        handlerConfiguration={handlerConfiguration}
        getRowKey={getRowKey}
        resetRowCustomFunction={resetRowCustomFunction}
      />
    </div>
  );
};
