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
import { useCallback, useMemo } from "react";
import * as ReactTable from "react-table";
import {
  BeeTableCellProps,
  BeeTableContextMenuAllowedOperationsConditions,
  BeeTableHeaderVisibility,
  BeeTableOperation,
  BeeTableOperationConfig,
  BeeTableProps,
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
import { DMN15__tContextEntry } from "@kie-tools/dmn-marshaller/dist/schemas/dmn-1_5/ts-gen/types";

export type ROWTYPE = DMN15__tContextEntry;

export function ListExpression(
  listExpression: ListExpressionDefinition & {
    isNested: boolean;
    parentElementId: string;
  }
) {
  const { i18n } = useBoxedExpressionEditorI18n();
  const { setExpression } = useBoxedExpressionEditorDispatch();
  const { expressionHolderId, variables, widthsById } = useBoxedExpressionEditor();

  /// //////////////////////////////////////////////////////
  /// ///////////// RESIZING WIDTHS ////////////////////////
  /// //////////////////////////////////////////////////////

  const { nestedExpressionContainerValue, onColumnResizingWidthChange } =
    useNestedExpressionContainerWithNestedExpressions(
      useMemo(() => {
        return {
          nestedExpressions: listExpression.expression ?? [],
          fixedColumnActualWidth: 0,
          fixedColumnResizingWidth: { value: 0, isPivoting: false },
          fixedColumnMinWidth: 0,
          nestedExpressionMinWidth: LIST_EXPRESSION_ITEM_MIN_WIDTH,
          extraWidth: LIST_EXPRESSION_EXTRA_WIDTH,
          expression: listExpression,
          flexibleColumnIndex: 1,
          widthsById: widthsById,
        };
      }, [listExpression, widthsById])
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
    const rows = (listExpression.expression ?? []).map((item) => ({
      variable: undefined as any,
      expression: item,
    }));

    if (rows.length === 0) {
      rows.push({
        variable: undefined as any,
        expression: undefined as any,
      });
    }

    return rows;
  }, [listExpression.expression]);

  const beeTableColumns = useMemo<ReactTable.Column<ROWTYPE>[]>(
    () => [
      {
        accessor: expressionHolderId as any,
        label: listExpression["@_label"] ?? DEFAULT_EXPRESSION_NAME,
        dataType: listExpression["@_typeRef"] ?? "<Undefined>",
        isRowIndexColumn: false,
        minWidth: LIST_EXPRESSION_ITEM_MIN_WIDTH,
        width: undefined,
      },
    ],
    [expressionHolderId, listExpression]
  );

  const getRowKey = useCallback((row: ReactTable.Row<ROWTYPE>) => {
    return row.id;
  }, []);

  const cellComponentByColumnAccessor: BeeTableProps<ROWTYPE>["cellComponentByColumnAccessor"] = useMemo(
    (): { [p: string]: ({ rowIndex, data, columnIndex }: BeeTableCellProps<ROWTYPE>) => JSX.Element } => ({
      [expressionHolderId]: (props) => <ListItemCell parentElementId={listExpression.parentElementId} {...props} />,
    }),
    [expressionHolderId, listExpression.parentElementId]
  );

  const onRowAdded = useCallback(
    (args: { beforeIndex: number; rowsCount: number; insertDirection: InsertRowColumnsDirection }) => {
      setExpression((prev: ListExpressionDefinition) => {
        const newItems = [...(prev.expression ?? [])];
        const newListItems = [];

        for (let i = 0; i < args.rowsCount; i++) {
          const newItem = undefined as any; // SPEC DISCREPANCY: Starting without an expression gives users the ability to select the expression type.
          newListItems.push(newItem);
        }

        for (const newEntry of newListItems) {
          let index = args.beforeIndex;
          newItems.splice(index, 0, newEntry);
          if (args.insertDirection === InsertRowColumnsDirection.AboveOrRight) {
            index++;
          }
        }

        return { ...prev, expression: newItems };
      });
    },
    [setExpression]
  );

  const onRowDeleted = useCallback(
    (args: { rowIndex: number }) => {
      setExpression((prev: ListExpressionDefinition) => {
        const newItems = [...(prev.expression ?? [])];
        newItems.splice(args.rowIndex, 1);
        return {
          ...prev,
          expression: newItems,
        };
      });
    },
    [setExpression]
  );

  const onRowReset = useCallback(
    (args: { rowIndex: number }) => {
      setExpression((prev: ListExpressionDefinition) => {
        const newItems = [...(prev.expression ?? [])];
        newItems.splice(args.rowIndex, 1, undefined as any); // SPEC DISCREPANCY: Starting without an expression gives users the ability to select the expression type.
        return {
          ...prev,
          expression: newItems,
        };
      });
    },
    [setExpression]
  );

  const beeTableHeaderVisibility = useMemo(() => {
    return listExpression.isNested ? BeeTableHeaderVisibility.None : BeeTableHeaderVisibility.AllLevels;
  }, [listExpression.isNested]);

  const onColumnUpdates = useCallback(
    ([{ name, dataType }]: BeeTableColumnUpdate<ROWTYPE>[]) => {
      setExpression((prev) => ({
        ...prev,
        "@_label": name,
        "@_typeRef": dataType,
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
      <div className={`${listExpression["@_id"]} list-expression`}>
        <BeeTable<ROWTYPE>
          onColumnResizingWidthChange={onColumnResizingWidthChange}
          resizerStopBehavior={ResizerStopBehavior.SET_WIDTH_WHEN_SMALLER}
          tableId={listExpression["@_id"]}
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
          widthsById={widthsById}
        />
      </div>
    </NestedExpressionContainerContext.Provider>
  );
}
