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
  BeeTableColumn as RelationColumn,
  DmnBuiltInDataType,
  executeIfExpressionDefinitionChanged,
  generateUuid,
  RelationExpressionDefinition,
  BeeTableRow,
  BeeTableRowsUpdateArgs,
  BeeTableOperation,
} from "../../api";
import { BeeTable } from "../BeeTable";
import { useBoxedExpressionEditorI18n } from "../../i18n";
import * as ReactTable from "react-table";
import { DEFAULT_MIN_WIDTH } from "../Resizer";
import { useBoxedExpressionEditor } from "../BoxedExpressionEditor/BoxedExpressionEditorContext";

export const RelationExpression: React.FunctionComponent<RelationExpressionDefinition> = (
  relationProps: RelationExpressionDefinition
) => {
  const FIRST_COLUMN_NAME = "column-1";
  const { i18n } = useBoxedExpressionEditorI18n();
  const { beeGwtService } = useBoxedExpressionEditor();

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

  const columns: RelationColumn[] = useMemo(
    () =>
      relationProps.columns === undefined
        ? [
            {
              id: generateUuid(),
              name: FIRST_COLUMN_NAME,
              dataType: DmnBuiltInDataType.Undefined,
              width: DEFAULT_MIN_WIDTH,
            },
          ]
        : relationProps.columns,
    [relationProps]
  );

  const rows: BeeTableRow[] = useMemo(() => {
    return relationProps.rows === undefined ? [{ id: generateUuid(), cells: [""] }] : relationProps.rows;
  }, [relationProps]);

  const spreadRelationExpressionDefinition = useCallback(
    (newColumns?: RelationColumn[], newRows?: BeeTableRow[]) => {
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
            beeGwtService?.broadcastRelationExpressionDefinition?.(expressionDefinition);
          }
        },
        ["columns", "rows"]
      );
    },
    [beeGwtService, relationProps, columns, rows]
  );

  const beeTableColumns = useMemo<ReactTable.ColumnInstance[]>(
    () =>
      columns.map(
        (column: RelationColumn) =>
          ({
            accessor: column.id,
            label: column.name,
            dataType: column.dataType,
            ...(column.width ? { width: column.width } : {}),
          } as ReactTable.ColumnInstance)
      ),
    [columns]
  );

  const beeTableRows = useMemo(
    () =>
      _.chain(rows)
        .map((row) => {
          const updatedRow = _.chain(columns)
            .reduce((tableRow, column, columnIndex) => {
              tableRow[column.id] = row.cells[columnIndex] || "";
              return tableRow;
            }, {} as ReactTable.DataRecord)
            .value();
          updatedRow.id = row.id;
          return updatedRow;
        })
        .value(),
    [rows, columns]
  );

  const onRowsUpdate = useCallback(
    ({ rows, columns }: BeeTableRowsUpdateArgs) => {
      const newRows = _.chain(rows)
        .map((tableRow: ReactTable.DataRecord) => {
          const cells = _.chain(columns)
            .reduce((row: string[], column: ReactTable.ColumnInstance) => {
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
      const newColumns = columns.map((columnInstance: ReactTable.ColumnInstance) => ({
        id: columnInstance.accessor,
        name: columnInstance.label as string,
        dataType: columnInstance.dataType,
        width: columnInstance.width,
      }));
      const newRows = rows.map((tableRow: BeeTableRow) => {
        switch (operation) {
          case BeeTableOperation.ColumnInsertLeft:
            return {
              ...tableRow,
              cells: [...tableRow.cells.slice(0, columnIndex), "", ...tableRow.cells.slice(columnIndex)],
            };
          case BeeTableOperation.ColumnInsertRight:
            return {
              ...tableRow,
              cells: [...tableRow.cells.slice(0, columnIndex + 1), "", ...tableRow.cells.slice(columnIndex + 1)],
            };
          case BeeTableOperation.ColumnDelete:
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
      <BeeTable
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
