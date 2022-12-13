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

import "./RelationExpression.css";
import _ from "lodash";
import * as React from "react";
import { useCallback, useMemo } from "react";
import "@patternfly/react-styles/css/utilities/Text/text.css";
import {
  RelationExpressionDefinitionColumn,
  RelationExpressionDefinition,
  RelationExpressionDefinitionRow,
  BeeTableRowsUpdateArgs,
  BeeTableOperation,
  BeeTableColumnsUpdateArgs,
} from "../../api";
import { BeeTable } from "../BeeTable";
import { useBoxedExpressionEditorI18n } from "../../i18n";
import * as ReactTable from "react-table";
import { useBoxedExpressionEditorDispatch } from "../BoxedExpressionEditor/BoxedExpressionEditorContext";
import { BEE_TABLE_ROW_INDEX_COLUMN_WIDTH, useNestedExpressionContainer } from "../ContextExpression";
import { useEffect } from "react";
import { useResizingWidthDispatch } from "../ExpressionDefinitionRoot";
import { NESTED_EXPRESSION_CLEAR_MARGIN } from "../ContextExpression";

type ROWTYPE = RelationExpressionDefinitionRow;

export const RELATION_EXPRESSION_COLUMN_MIN_WIDTH = 100;

export const RelationExpression: React.FunctionComponent<RelationExpressionDefinition> = (
  relationExpression: RelationExpressionDefinition
) => {
  const { i18n } = useBoxedExpressionEditorI18n();
  const { setExpression } = useBoxedExpressionEditorDispatch();
  const { updateResizingWidth } = useResizingWidthDispatch();

  const nestedExpressionContainer = useNestedExpressionContainer();

  const operationHandlerConfig = [
    {
      group: i18n.columns,
      items: [
        { name: i18n.columnOperations.insertLeft, type: BeeTableOperation.ColumnInsertLeft },
        { name: i18n.columnOperations.insertRight, type: BeeTableOperation.ColumnInsertRight },
        { name: i18n.columnOperations.delete, type: BeeTableOperation.ColumnDelete },
      ],
    },
    {
      group: i18n.rows,
      items: [
        { name: i18n.rowOperations.insertAbove, type: BeeTableOperation.RowInsertAbove },
        { name: i18n.rowOperations.insertBelow, type: BeeTableOperation.RowInsertBelow },
        { name: i18n.rowOperations.delete, type: BeeTableOperation.RowDelete },
      ],
    },
  ];

  const columns = useMemo<RelationExpressionDefinitionColumn[]>(() => {
    return relationExpression.columns ?? [];
  }, [relationExpression.columns]);

  const rows = useMemo<RelationExpressionDefinitionRow[]>(() => {
    return relationExpression.rows ?? [];
  }, [relationExpression]);

  const beeTableColumns = useMemo<ReactTable.Column<ROWTYPE>[]>(() => {
    const columnsWidth =
      nestedExpressionContainer.resizingWidth.value -
      BEE_TABLE_ROW_INDEX_COLUMN_WIDTH -
      NESTED_EXPRESSION_CLEAR_MARGIN -
      columns.length * 2; // 2px for border of each column

    const columnIntegerWidth = Math.max(
      Math.floor(columnsWidth / columns.length),
      RELATION_EXPRESSION_COLUMN_MIN_WIDTH
    );
    const remainder = Math.max(columnsWidth % columns.length, 0);

    return columns.map((column, columnIndex) => ({
      accessor: column.id as any, // FIXME: Tiago -> Not good.
      label: column.name,
      dataType: column.dataType,
      isRowIndexColumn: false,
      minWidth: RELATION_EXPRESSION_COLUMN_MIN_WIDTH,
      resizingWidth: {
        value: columnIndex === columns.length - 1 ? columnIntegerWidth + remainder : columnIntegerWidth,
        isPivoting: false,
      },
      width: column.width,
    }));
  }, [columns, nestedExpressionContainer.resizingWidth.value]);

  useEffect(() => {
    updateResizingWidth(relationExpression.id!, (prev) => ({
      value: beeTableColumns.reduce(
        (acc, { resizingWidth }) => acc + (resizingWidth?.value ?? RELATION_EXPRESSION_COLUMN_MIN_WIDTH),
        BEE_TABLE_ROW_INDEX_COLUMN_WIDTH - NESTED_EXPRESSION_CLEAR_MARGIN + (beeTableColumns?.length ?? 0) * 2 // 2px for border of each column
      ),
      isPivoting: false,
    }));
  }, [beeTableColumns, relationExpression.id, updateResizingWidth]);

  const beeTableRows = useMemo<ROWTYPE[]>(
    () =>
      rows.map((row) => {
        const beeTableRow = columns.reduce(
          (tableRow, column, columnIndex) => {
            (tableRow as any)[column.id] = row.cells[columnIndex] || "";
            return tableRow;
          },
          { id: row.id } as ROWTYPE
        );
        return beeTableRow;
      }),
    [rows, columns]
  );

  const onRowsUpdate = useCallback(({ rows, columns }: BeeTableRowsUpdateArgs<ROWTYPE>) => {
    // Do nothing for now
  }, []);

  const onColumnsUpdate = useCallback((args: BeeTableColumnsUpdateArgs<ROWTYPE>) => {
    // Do nothing for now
  }, []);

  return (
    <div className="relation-expression">
      <BeeTable<ROWTYPE>
        editColumnLabel={i18n.editRelation}
        columns={beeTableColumns}
        rows={beeTableRows}
        onColumnsUpdate={onColumnsUpdate}
        onRowsUpdate={onRowsUpdate}
        operationHandlerConfig={operationHandlerConfig}
      />
    </div>
  );
};
