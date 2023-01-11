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
  ExpressionDefinition,
  ExpressionDefinitionLogicType,
  generateUuid,
  ListExpressionDefinition,
} from "../../api";
import { useBoxedExpressionEditorI18n } from "../../i18n";
import { useNestedExpressionContainerWidthNestedExpressions } from "../../resizing/Hooks";
import { NestedExpressionContainerContext } from "../../resizing/NestedExpressionContainerContext";
import { CONTEXT_ENTRY_EXPRESSION_MIN_WIDTH, LIST_EXPRESSION_EXTRA_WIDTH } from "../../resizing/WidthValues";
import { BeeTable, BeeTableColumnUpdate } from "../../table/BeeTable";
import { useBoxedExpressionEditorDispatch } from "../BoxedExpressionEditor/BoxedExpressionEditorContext";
import { ListItemCell } from "./ListItemCell";
import "./ListExpression.css";

type ROWTYPE = ContextExpressionDefinitionEntry;

export function ListExpression(listExpression: ListExpressionDefinition & { isHeadless: boolean }) {
  const { i18n } = useBoxedExpressionEditorI18n();
  const { setExpression } = useBoxedExpressionEditorDispatch();

  /// //////////////////////////////////////////////////////
  /// ///////////// RESIZING WIDTHS ////////////////////////
  /// //////////////////////////////////////////////////////

  const nestedExpressions = useMemo(() => {
    return listExpression.items;
  }, [listExpression.items]);

  const { nestedExpressionContainerValue } = useNestedExpressionContainerWidthNestedExpressions(
    useMemo(() => {
      return {
        nestedExpressions,
        fixedColumnActualWidth: 0,
        fixedColumnResizingWidth: { value: 0, isPivoting: false },
        fixedColumnMinWidth: 0,
        nestedExpressionMin: CONTEXT_ENTRY_EXPRESSION_MIN_WIDTH,
        extraWidth: LIST_EXPRESSION_EXTRA_WIDTH,
        id: listExpression.id,
      };
    }, [nestedExpressions, listExpression.id])
  );

  /// //////////////////////////////////////////////////////

  const beeTableOperationConfig = useMemo<BeeTableOperationConfig>(
    () => [
      {
        group: i18n.rows,
        items: [
          { name: i18n.rowOperations.reset, type: BeeTableOperation.RowReset },
          { name: i18n.rowOperations.insertAbove, type: BeeTableOperation.RowInsertAbove },
          { name: i18n.rowOperations.insertBelow, type: BeeTableOperation.RowInsertBelow },
          { name: i18n.rowOperations.delete, type: BeeTableOperation.RowDelete },
        ],
      },
    ],
    [i18n]
  );

  const beeTableRows = useMemo(() => {
    return listExpression.items.map((item) => ({
      entryInfo: undefined as any,
      entryExpression: item,
    }));
  }, [listExpression.items]);

  const beeTableColumns = useMemo<ReactTable.Column<ROWTYPE>[]>(
    () => [
      {
        accessor: "list" as any,
        label: listExpression.name ?? "Expression Name",
        dataType: listExpression.dataType,
        isRowIndexColumn: false,
        width: undefined,
      },
    ],
    [listExpression.dataType, listExpression.name]
  );

  const getRowKey = useCallback((row: ReactTable.Row<ROWTYPE>) => {
    return row.original.entryExpression.id!;
  }, []);

  const cellComponentByColumnId: BeeTableProps<ROWTYPE>["cellComponentByColumnId"] = useMemo(
    () => ({
      list: ListItemCell,
    }),
    []
  );

  const getDefaultListItem = useCallback((): ExpressionDefinition => {
    return {
      id: generateUuid(),
      logicType: ExpressionDefinitionLogicType.Undefined,
      dataType: DmnBuiltInDataType.Undefined,
    };
  }, []);

  const onRowAdded = useCallback(
    (args: { beforeIndex: number }) => {
      setExpression((prev: ListExpressionDefinition) => {
        const newItems = [...prev.items];
        newItems.splice(args.beforeIndex, 0, getDefaultListItem());
        return { ...prev, items: newItems };
      });
    },
    [getDefaultListItem, setExpression]
  );

  const onRowDeleted = useCallback(
    (args: { rowIndex: number }) => {
      setExpression((prev: ListExpressionDefinition) => {
        const newItems = [...(prev.items ?? [])];
        newItems.splice(args.rowIndex, 1);
        return {
          ...prev,
          items: newItems,
        };
      });
    },
    [setExpression]
  );

  const onRowReset = useCallback(
    (args: { rowIndex: number }) => {
      setExpression((prev: ListExpressionDefinition) => {
        const newItems = [...(prev.items ?? [])];
        newItems.splice(args.rowIndex, 1, getDefaultListItem());
        return {
          ...prev,
          items: newItems,
        };
      });
    },
    [getDefaultListItem, setExpression]
  );

  const beeTableHeaderVisibility = useMemo(() => {
    return listExpression.isHeadless ? BeeTableHeaderVisibility.None : BeeTableHeaderVisibility.AllLevels;
  }, [listExpression.isHeadless]);

  const onColumnUpdates = useCallback(
    ([{ name, dataType }]: BeeTableColumnUpdate<ROWTYPE>[]) => {
      setExpression((prev) => ({
        ...prev,
        name,
        dataType,
      }));
    },
    [setExpression]
  );

  return (
    <NestedExpressionContainerContext.Provider value={nestedExpressionContainerValue}>
      <div className={`${listExpression.id} list-expression`}>
        <BeeTable<ROWTYPE>
          tableId={listExpression.id}
          headerVisibility={beeTableHeaderVisibility}
          cellComponentByColumnId={cellComponentByColumnId}
          columns={beeTableColumns}
          rows={beeTableRows}
          operationConfig={beeTableOperationConfig}
          getRowKey={getRowKey}
          onRowAdded={onRowAdded}
          onRowDeleted={onRowDeleted}
          onRowReset={onRowReset}
          onColumnUpdates={onColumnUpdates}
          shouldRenderRowIndexColumn={true}
          shouldShowRowsInlineControls={true}
          shouldShowColumnsInlineControls={false}
        />
      </div>
    </NestedExpressionContainerContext.Provider>
  );
}
