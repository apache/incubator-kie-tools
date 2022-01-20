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
  Column as RelationColumn,
  DataType,
  executeIfExpressionDefinitionChanged,
  generateUuid,
  RelationProps,
  Row,
  RowsUpdateArgs,
  TableOperation,
} from "../../api";
import { Table } from "../Table";
import { useBoxedExpressionEditorI18n } from "../../i18n";
import { Column, ColumnInstance, DataRecord } from "react-table";
import { DEFAULT_MIN_WIDTH } from "../Resizer";
import { useBoxedExpression } from "../../context";

export const RelationExpression: React.FunctionComponent<RelationProps> = (relationProps: RelationProps) => {
  const FIRST_COLUMN_NAME = "column-1";
  const { i18n } = useBoxedExpressionEditorI18n();
  const { boxedExpressionEditorGWTService } = useBoxedExpression();

  const handlerConfiguration = [
    {
      group: i18n.columns,
      items: [
        { name: i18n.columnOperations.insertLeft, type: TableOperation.ColumnInsertLeft },
        { name: i18n.columnOperations.insertRight, type: TableOperation.ColumnInsertRight },
        { name: i18n.columnOperations.delete, type: TableOperation.ColumnDelete },
      ],
    },
    {
      group: i18n.rows,
      items: [
        { name: i18n.rowOperations.insertAbove, type: TableOperation.RowInsertAbove },
        { name: i18n.rowOperations.insertBelow, type: TableOperation.RowInsertBelow },
        { name: i18n.rowOperations.delete, type: TableOperation.RowDelete },
      ],
    },
  ];

  const columns: RelationColumn[] = useMemo(
    () =>
      relationProps.columns === undefined
        ? [{ id: generateUuid(), name: FIRST_COLUMN_NAME, dataType: DataType.Undefined, width: DEFAULT_MIN_WIDTH }]
        : relationProps.columns,
    [relationProps]
  );

  const rows: Row[] = useMemo(() => {
    return relationProps.rows === undefined ? [{ id: generateUuid(), cells: [""] }] : relationProps.rows;
  }, [relationProps]);

  const spreadRelationExpressionDefinition = useCallback(
    (newColumns?: RelationColumn[], newRows?: Row[]) => {
      const expressionDefinition = {
        ...relationProps,
        columns: newColumns ?? columns,
        rows: newRows ?? rows,
      };

      executeIfExpressionDefinitionChanged(
        relationProps,
        expressionDefinition,
        () => {
          if (relationProps.isHeadless) {
            relationProps.onUpdatingRecursiveExpression?.(expressionDefinition);
          } else {
            boxedExpressionEditorGWTService?.broadcastRelationExpressionDefinition?.(expressionDefinition);
          }
        },
        ["columns", "rows"]
      );
    },
    [boxedExpressionEditorGWTService, relationProps, columns, rows]
  );

  const convertColumnsForTheTable = useMemo(
    () =>
      columns.map(
        (column: RelationColumn) =>
          ({
            accessor: column.id,
            label: column.name,
            dataType: column.dataType,
            ...(column.width ? { width: column.width } : {}),
          } as Column)
      ),
    [columns]
  );

  const convertRowsForTheTable = useMemo(
    () =>
      _.chain(rows)
        .map((row) => {
          const updatedRow = _.chain(columns)
            .reduce((tableRow: DataRecord, column, columnIndex) => {
              tableRow[column.id] = row.cells[columnIndex] || "";
              return tableRow;
            }, {})
            .value();
          updatedRow.id = row.id;
          return updatedRow;
        })
        .value(),
    [rows, columns]
  );

  const onRowsUpdate = useCallback(
    ({ rows, columns }: RowsUpdateArgs) => {
      const newRows = _.chain(rows)
        .map((tableRow: DataRecord) => {
          const cells = _.chain(columns)
            .reduce((row: string[], column: ColumnInstance) => {
              row.push((tableRow[column.accessor] as string) ?? "");
              return row;
            }, [])
            .value();
          return { id: tableRow.id as string, cells };
        })
        .value();
      spreadRelationExpressionDefinition(undefined, newRows);
    },
    [spreadRelationExpressionDefinition]
  );

  const onColumnsUpdate = useCallback(
    ({ columns, operation, columnIndex }) => {
      const newColumns = columns.map((columnInstance: ColumnInstance) => ({
        id: columnInstance.accessor,
        name: columnInstance.label as string,
        dataType: columnInstance.dataType,
        width: columnInstance.width,
      }));
      const newRows = rows.map((tableRow: Row) => {
        switch (operation) {
          case TableOperation.ColumnInsertLeft:
            return {
              ...tableRow,
              cells: [...tableRow.cells.slice(0, columnIndex), "", ...tableRow.cells.slice(columnIndex)],
            };
          case TableOperation.ColumnInsertRight:
            return {
              ...tableRow,
              cells: [...tableRow.cells.slice(0, columnIndex + 1), "", ...tableRow.cells.slice(columnIndex + 1)],
            };
          case TableOperation.ColumnDelete:
            return {
              ...tableRow,
              cells: [...tableRow.cells.slice(0, columnIndex), ...tableRow.cells.slice(columnIndex + 1)],
            };
        }
        return { ...tableRow };
      });
      spreadRelationExpressionDefinition(newColumns, newRows);
    },
    [spreadRelationExpressionDefinition, rows]
  );

  return (
    <div className="relation-expression">
      <Table
        editColumnLabel={i18n.editRelation}
        columns={convertColumnsForTheTable}
        rows={convertRowsForTheTable}
        onColumnsUpdate={onColumnsUpdate}
        onRowsUpdate={onRowsUpdate}
        handlerConfiguration={handlerConfiguration}
      />
    </div>
  );
};
