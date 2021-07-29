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
import * as React from "react";
import { useCallback, useEffect, useRef } from "react";
import "@patternfly/react-styles/css/utilities/Text/text.css";
import { Column as RelationColumn, DataType, RelationProps, Row, TableOperation } from "../../api";
import { Table } from "../Table";
import { useBoxedExpressionEditorI18n } from "../../i18n";
import * as _ from "lodash";
import { Column, ColumnInstance, DataRecord } from "react-table";

export const RelationExpression: React.FunctionComponent<RelationProps> = (relationProps: RelationProps) => {
  const FIRST_COLUMN_NAME = "column-1";
  const { i18n } = useBoxedExpressionEditorI18n();

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

  const tableColumns = useRef<RelationColumn[]>(
    relationProps.columns === undefined
      ? [{ name: FIRST_COLUMN_NAME, dataType: DataType.Undefined }]
      : relationProps.columns
  );

  const tableRows = useRef<Row[]>(relationProps.rows === undefined ? [[""]] : relationProps.rows);

  const spreadRelationExpressionDefinition = useCallback(() => {
    const expressionDefinition = {
      ...relationProps,
      columns: tableColumns.current,
      rows: tableRows.current,
    };
    relationProps.isHeadless
      ? relationProps.onUpdatingRecursiveExpression?.(expressionDefinition)
      : window.beeApi?.broadcastRelationExpressionDefinition?.(expressionDefinition);
  }, [relationProps]);

  const convertColumnsForTheTable = useCallback(
    () =>
      _.map(
        tableColumns.current,
        (column: RelationColumn) =>
          ({
            label: column.name,
            accessor: column.name,
            dataType: column.dataType,
            ...(column.width ? { width: column.width } : {}),
          } as Column)
      ),
    []
  );

  const convertRowsForTheTable = useCallback(
    () =>
      _.map(tableRows.current, (row) =>
        _.reduce(
          tableColumns.current,
          (tableRow: DataRecord, column, columnIndex) => {
            tableRow[column.name] = row[columnIndex] || "";
            return tableRow;
          },
          {}
        )
      ),
    []
  );

  const onSavingRows = useCallback(
    (rows: DataRecord[]) => {
      tableRows.current = _.map(rows, (tableRow: DataRecord) =>
        _.reduce(
          tableColumns.current,
          (row: string[], column: RelationColumn) => {
            row.push((tableRow[column.name]! as string) || "");
            return row;
          },
          []
        )
      );
      spreadRelationExpressionDefinition();
    },
    [spreadRelationExpressionDefinition]
  );

  const onSavingColumns = useCallback(
    (columns) => {
      tableColumns.current = _.map(columns, (columnInstance: ColumnInstance) => ({
        name: columnInstance.accessor,
        dataType: columnInstance.dataType,
        width: columnInstance.width,
      }));
      spreadRelationExpressionDefinition();
    },
    [spreadRelationExpressionDefinition]
  );

  useEffect(() => {
    /** Function executed only the first time the component is loaded */
    spreadRelationExpressionDefinition();
  }, [spreadRelationExpressionDefinition]);

  return (
    <div className="relation-expression">
      <Table
        editColumnLabel={i18n.editRelation}
        columns={convertColumnsForTheTable()}
        rows={convertRowsForTheTable()}
        onColumnsUpdate={onSavingColumns}
        onRowsUpdate={onSavingRows}
        handlerConfiguration={handlerConfiguration}
      />
    </div>
  );
};
