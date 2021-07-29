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
import { useCallback, useContext, useEffect, useMemo, useState } from "react";
import {
  ContextEntryRecord,
  DataType,
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
import * as _ from "lodash";
import { hashfy } from "../Resizer";
import { BoxedExpressionGlobalContext } from "../../context";
import nextId from "react-id-generator";

const LIST_EXPRESSION_MIN_WIDTH = 430;

export const ListExpression: React.FunctionComponent<ListProps> = ({
  isHeadless,
  items,
  onUpdatingRecursiveExpression,
  uid,
  width = LIST_EXPRESSION_MIN_WIDTH,
}: ListProps) => {
  const { i18n } = useBoxedExpressionEditorI18n();

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

  const generateLiteralExpression = useCallback(
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

  const initialListOfItems = useCallback(() => {
    if (_.isEmpty(items)) {
      return [{ entryExpression: generateLiteralExpression() } as DataRecord];
    } else {
      return _.map(items, (item) => ({ entryExpression: item } as DataRecord));
    }
  }, [generateLiteralExpression, items]);

  const [listItems, setListItems] = useState(initialListOfItems());
  const [listWidth, setListWidth] = useState(width || LIST_EXPRESSION_MIN_WIDTH);
  const { setSupervisorHash } = useContext(BoxedExpressionGlobalContext);

  const listTableGetRowKey = useCallback((row: Row) => (row.original as ContextEntryRecord).entryExpression.uid!, []);

  const onRowAdding = useCallback(
    () => ({
      entryExpression: generateLiteralExpression(),
    }),
    [generateLiteralExpression]
  );

  useEffect(() => {
    const updatedDefinition: ListProps = {
      uid,
      logicType: LogicType.List,
      width: listWidth,
      items: _.map(listItems, (listItem: DataRecord) => listItem.entryExpression as ExpressionProps),
    };

    if (isHeadless) {
      onUpdatingRecursiveExpression?.(updatedDefinition);
    } else {
      setSupervisorHash(hashfy(updatedDefinition));
      window.beeApi?.broadcastListExpressionDefinition?.(updatedDefinition);
    }
  }, [listWidth, listItems, isHeadless, onUpdatingRecursiveExpression, uid, setSupervisorHash]);

  const resetRowCustomFunction = useCallback((row: DataRecord) => {
    return { entryExpression: { uid: (row.entryExpression as ExpressionProps).uid } };
  }, []);

  const onRowsUpdate = useCallback((rows) => {
    setListItems(rows);
  }, []);

  const columns = useMemo(() => [{ accessor: "list", width: listWidth, setWidth: setListWidth }], [listWidth]);

  return useMemo(
    () => (
      <div className="list-expression">
        <Table
          tableId={uid}
          headerVisibility={TableHeaderVisibility.None}
          defaultCell={{ list: ContextEntryExpressionCell }}
          columns={columns}
          rows={listItems as DataRecord[]}
          onRowsUpdate={onRowsUpdate}
          onRowAdding={onRowAdding}
          handlerConfiguration={handlerConfiguration}
          getRowKey={listTableGetRowKey}
          resetRowCustomFunction={resetRowCustomFunction}
        />
      </div>
    ),
    [
      columns,
      handlerConfiguration,
      listItems,
      listTableGetRowKey,
      onRowAdding,
      onRowsUpdate,
      resetRowCustomFunction,
      uid,
    ]
  );
};
