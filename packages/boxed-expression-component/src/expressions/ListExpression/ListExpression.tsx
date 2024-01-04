/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

import * as React from "react";
import * as ReactTable from "react-table";
import { useCallback, useMemo } from "react";
import {
  BeeTableCellProps,
  BeeTableContextMenuAllowedOperationsConditions,
  BeeTableHeaderVisibility,
  BeeTableOperation,
  BeeTableOperationConfig,
  BeeTableProps,
  ContextExpressionDefinitionEntry,
  DmnBuiltInDataType,
  ExpressionDefinition,
  ExpressionDefinitionLogicType,
  generateUuid,
  InsertRowColumnsDirection,
  ListExpressionDefinition,
} from "../../api";
import { useBoxedExpressionEditorI18n } from "../../i18n";
import { useNestedExpressionContainerWithNestedExpressions } from "../../resizing/Hooks";
import { NestedExpressionContainerContext } from "../../resizing/NestedExpressionContainerContext";
import { LIST_EXPRESSION_EXTRA_WIDTH, LIST_EXPRESSION_ITEM_MIN_WIDTH } from "../../resizing/WidthConstants";
import { BeeTable, BeeTableColumnUpdate } from "../../table/BeeTable";
import {
  useBoxedExpressionEditor,
  useBoxedExpressionEditorDispatch,
} from "../BoxedExpressionEditor/BoxedExpressionEditorContext";
import { DEFAULT_EXPRESSION_NAME } from "../ExpressionDefinitionHeaderMenu";
import "./ListExpression.css";
import { ListItemCell } from "./ListItemCell";
import { ResizerStopBehavior } from "../../resizing/ResizingWidthsContext";

export type ROWTYPE = ContextExpressionDefinitionEntry;

export function ListExpression(
  listExpression: ListExpressionDefinition & {
    isNested: boolean;
    parentElementId: string;
  }
) {
  const { i18n } = useBoxedExpressionEditorI18n();
  const { setExpression } = useBoxedExpressionEditorDispatch();
  const { decisionNodeId, variables } = useBoxedExpressionEditor();

  /// //////////////////////////////////////////////////////
  /// ///////////// RESIZING WIDTHS ////////////////////////
  /// //////////////////////////////////////////////////////

  const { nestedExpressionContainerValue, onColumnResizingWidthChange } =
    useNestedExpressionContainerWithNestedExpressions(
      useMemo(() => {
        return {
          nestedExpressions: listExpression.items,
          fixedColumnActualWidth: 0,
          fixedColumnResizingWidth: { value: 0, isPivoting: false },
          fixedColumnMinWidth: 0,
          nestedExpressionMinWidth: LIST_EXPRESSION_ITEM_MIN_WIDTH,
          extraWidth: LIST_EXPRESSION_EXTRA_WIDTH,
          expression: listExpression,
          flexibleColumnIndex: 1,
        };
      }, [listExpression])
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
          { name: i18n.insert, type: BeeTableOperation.RowInsertN },
          { name: i18n.rowOperations.delete, type: BeeTableOperation.RowDelete },
        ],
      },
      {
        group: i18n.terms.selection.toUpperCase(),
        items: [
          { name: i18n.terms.copy, type: BeeTableOperation.SelectionCopy },
          { name: i18n.terms.cut, type: BeeTableOperation.SelectionCut },
          { name: i18n.terms.paste, type: BeeTableOperation.SelectionPaste },
          { name: i18n.terms.reset, type: BeeTableOperation.SelectionReset },
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
        accessor: decisionNodeId as any, // FIXME: https://github.com/kiegroup/kie-issues/issues/169
        label: listExpression.name ?? DEFAULT_EXPRESSION_NAME,
        dataType: listExpression.dataType,
        isRowIndexColumn: false,
        minWidth: LIST_EXPRESSION_ITEM_MIN_WIDTH,
        width: undefined,
      },
    ],
    [decisionNodeId, listExpression.dataType, listExpression.name]
  );

  const getRowKey = useCallback((row: ReactTable.Row<ROWTYPE>) => {
    return row.original.entryExpression.id;
  }, []);

  const cellComponentByColumnAccessor: BeeTableProps<ROWTYPE>["cellComponentByColumnAccessor"] = useMemo(
    (): { [p: string]: ({ rowIndex, data, columnIndex }: BeeTableCellProps<ROWTYPE>) => JSX.Element } => ({
      [decisionNodeId]: (props) => <ListItemCell parentElementId={listExpression.parentElementId} {...props} />,
    }),
    [decisionNodeId, listExpression.parentElementId]
  );

  const getDefaultListItem = useCallback((dataType: DmnBuiltInDataType): ExpressionDefinition => {
    return {
      id: generateUuid(),
      logicType: ExpressionDefinitionLogicType.Undefined,
      dataType: dataType,
    };
  }, []);

  const onRowAdded = useCallback(
    (args: { beforeIndex: number; rowsCount: number; insertDirection: InsertRowColumnsDirection }) => {
      setExpression((prev: ListExpressionDefinition) => {
        const newItems = [...prev.items];
        const newListItems = [];

        for (let i = 0; i < args.rowsCount; i++) {
          const newItem = getDefaultListItem(prev.dataType);
          newListItems.push(newItem);
          variables?.repository.addVariableToContext(newItem.id, newItem.id, listExpression.parentElementId);
        }

        for (const newEntry of newListItems) {
          let index = args.beforeIndex;
          newItems.splice(index, 0, newEntry);
          if (args.insertDirection === InsertRowColumnsDirection.AboveOrRight) {
            index++;
          }
        }

        return { ...prev, items: newItems };
      });
    },
    [getDefaultListItem, listExpression.parentElementId, setExpression, variables?.repository]
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
        newItems.splice(args.rowIndex, 1, getDefaultListItem(prev.dataType));
        return {
          ...prev,
          items: newItems,
        };
      });
    },
    [getDefaultListItem, setExpression]
  );

  const beeTableHeaderVisibility = useMemo(() => {
    return listExpression.isNested ? BeeTableHeaderVisibility.None : BeeTableHeaderVisibility.AllLevels;
  }, [listExpression.isNested]);

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

  const allowedOperations = useCallback(
    (conditions: BeeTableContextMenuAllowedOperationsConditions) => {
      if (!conditions.selection.selectionStart || !conditions.selection.selectionEnd) {
        return [];
      }

      return [
        BeeTableOperation.SelectionCopy,
        ...(conditions.selection.selectionStart.rowIndex >= 0
          ? [
              BeeTableOperation.RowInsertAbove,
              BeeTableOperation.RowInsertBelow,
              BeeTableOperation.RowInsertN,
              ...(beeTableRows.length > 1 ? [BeeTableOperation.RowDelete] : []),
              BeeTableOperation.RowReset,
            ]
          : []),
      ];
    },
    [beeTableRows.length]
  );

  return (
    <NestedExpressionContainerContext.Provider value={nestedExpressionContainerValue}>
      <div className={`${listExpression.id} list-expression`}>
        <BeeTable<ROWTYPE>
          onColumnResizingWidthChange={onColumnResizingWidthChange}
          resizerStopBehavior={ResizerStopBehavior.SET_WIDTH_WHEN_SMALLER}
          tableId={listExpression.id}
          headerVisibility={beeTableHeaderVisibility}
          cellComponentByColumnAccessor={cellComponentByColumnAccessor}
          columns={beeTableColumns}
          rows={beeTableRows}
          operationConfig={beeTableOperationConfig}
          allowedOperations={allowedOperations}
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
