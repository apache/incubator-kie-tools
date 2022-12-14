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
import { useCallback, useMemo, useState } from "react";
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
import { ResizingWidth, useResizingWidthDispatch, useResizingWidths } from "../ExpressionDefinitionRoot";
import { NESTED_EXPRESSION_CLEAR_MARGIN } from "../ContextExpression";

type ROWTYPE = RelationExpressionDefinitionRow;

export const RELATION_EXPRESSION_COLUMN_MIN_WIDTH = 100;

export const RelationExpression: React.FunctionComponent<RelationExpressionDefinition> = (
  relationExpression: RelationExpressionDefinition
) => {
  const { i18n } = useBoxedExpressionEditorI18n();
  const { setExpression } = useBoxedExpressionEditorDispatch();

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

  // RESIZING WIDTHS

  const { updateResizingWidth } = useResizingWidthDispatch();

  const nestedExpressionContainer = useNestedExpressionContainer();

  const [columnResizingWidths, setColumnResizingWidths] = useState<ResizingWidth[]>(
    columns.map((c) => ({ value: c.width ?? RELATION_EXPRESSION_COLUMN_MIN_WIDTH, isPivoting: false }))
  );

  const setColumnWidth = useCallback(
    (columnIndex: number) => (newWidth: number) => {
      setExpression((prev: RelationExpressionDefinition) => {
        const newColumns = [...(prev.columns ?? [])];
        newColumns[columnIndex].width = newWidth;
        return { ...prev, columns: newColumns };
      });
    },
    [setExpression]
  );

  useEffect(() => {
    setColumnResizingWidths((prev) => {
      return columns.map((column, columnIndex) => ({
        value: Math.max(column.width ?? RELATION_EXPRESSION_COLUMN_MIN_WIDTH, prev[columnIndex].value),
        isPivoting: false,
      }));
    });
  }, [columns]);

  const isRelationExpressionPivoting = useMemo(() => {
    return columnResizingWidths.some(({ isPivoting }) => isPivoting);
  }, [columnResizingWidths]);

  const [pivotAwareNestedExpressionContainer, setPivotAwareNestedExpressionContainer] =
    useState(nestedExpressionContainer);
  useEffect(() => {
    setPivotAwareNestedExpressionContainer((prev) => {
      return isRelationExpressionPivoting ? prev : nestedExpressionContainer;
    });
  }, [isRelationExpressionPivoting, nestedExpressionContainer]);

  const setColumnResizingWidth = useCallback(
    (columnIndex: number) => (getNewResizingWidth: (prev: ResizingWidth) => ResizingWidth) => {
      setColumnResizingWidths((prev: ResizingWidth[]) => {
        const newResizingWidth = getNewResizingWidth(prev[columnIndex]).value;
        const totalAvailableSpaceForColumns =
          pivotAwareNestedExpressionContainer.resizingWidth.value -
          BEE_TABLE_ROW_INDEX_COLUMN_WIDTH -
          NESTED_EXPRESSION_CLEAR_MARGIN -
          columns.length * 2; // 2px for border of each column

        const equallyProportionalWidth = Math.max(
          Math.floor(totalAvailableSpaceForColumns / columns.length),
          RELATION_EXPRESSION_COLUMN_MIN_WIDTH
        );

        const proportionalColumns = columns.reduce(
          (acc, column, i) => {
            const columnWidth =
              i === columnIndex ? newResizingWidth : column.width ?? RELATION_EXPRESSION_COLUMN_MIN_WIDTH;
            if (columnWidth > equallyProportionalWidth) {
              return {
                totalSpaceLeft: acc.totalSpaceLeft - columnWidth,
                count: acc.count - 1,
              };
            } else {
              return acc;
            }
          },
          {
            totalSpaceLeft: totalAvailableSpaceForColumns,
            count: columns.length,
          }
        );

        const proportionalColumnWidth = Math.max(
          Math.floor(proportionalColumns.totalSpaceLeft / proportionalColumns.count),
          RELATION_EXPRESSION_COLUMN_MIN_WIDTH
        );

        let currentSpaceLeft = totalAvailableSpaceForColumns;
        const a = columns.map((column, i) => {
          const columnWidth = column.width ?? RELATION_EXPRESSION_COLUMN_MIN_WIDTH;
          if (i === columnIndex) {
            currentSpaceLeft -= newResizingWidth;
            return {
              value: newResizingWidth,
              isPivoting: false,
            };
          } else if (columnWidth > equallyProportionalWidth) {
            currentSpaceLeft -= columnWidth;
            return {
              value: columnWidth,
              isPivoting: false,
            };
          } else if (i === columns.length - 1) {
            return {
              value: currentSpaceLeft,
              isPivoting: false,
            };
          } else {
            currentSpaceLeft -= proportionalColumnWidth;
            return {
              value: proportionalColumnWidth,
              isPivoting: false,
            };
          }
        });

        return a;
      });
    },
    [columns, pivotAwareNestedExpressionContainer]
  );

  useEffect(() => {
    setColumnResizingWidths((prev) => {
      const totalAvailableSpaceForColumns =
        pivotAwareNestedExpressionContainer.resizingWidth.value -
        BEE_TABLE_ROW_INDEX_COLUMN_WIDTH -
        NESTED_EXPRESSION_CLEAR_MARGIN -
        columns.length * 2; // 2px for border of each column

      const equallyProportionalWidth = Math.max(
        Math.floor(totalAvailableSpaceForColumns / columns.length),
        RELATION_EXPRESSION_COLUMN_MIN_WIDTH
      );

      const proportionalColumns = columns.reduce(
        (acc, column) => {
          const columnWidth = column.width ?? RELATION_EXPRESSION_COLUMN_MIN_WIDTH;
          if (columnWidth > equallyProportionalWidth) {
            return {
              totalSpaceLeft: acc.totalSpaceLeft - columnWidth,
              count: acc.count - 1,
            };
          } else {
            return acc;
          }
        },
        {
          totalSpaceLeft: totalAvailableSpaceForColumns,
          count: columns.length,
        }
      );

      const proportionalColumnWidth = Math.max(
        Math.floor(proportionalColumns.totalSpaceLeft / proportionalColumns.count),
        RELATION_EXPRESSION_COLUMN_MIN_WIDTH
      );

      let currentSpaceLeft = totalAvailableSpaceForColumns;
      return columns.map((column, columnIndex) => {
        const columnWidth = column.width ?? RELATION_EXPRESSION_COLUMN_MIN_WIDTH;
        if (columnWidth > equallyProportionalWidth) {
          currentSpaceLeft -= columnWidth;
          return {
            value: columnWidth,
            isPivoting: false,
          };
        } else if (columnIndex === columns.length - 1) {
          return {
            value: currentSpaceLeft,
            isPivoting: false,
          };
        } else {
          currentSpaceLeft -= proportionalColumnWidth;
          return {
            value: proportionalColumnWidth,
            isPivoting: false,
          };
        }
      });
    });
  }, [columns, pivotAwareNestedExpressionContainer]);

  useEffect(() => {
    updateResizingWidth(relationExpression.id!, (prev) =>
      columnResizingWidths.reduce(
        (acc, { value, isPivoting }) => ({ value: acc.value + value, isPivoting: acc.isPivoting || isPivoting }),
        {
          value: BEE_TABLE_ROW_INDEX_COLUMN_WIDTH + NESTED_EXPRESSION_CLEAR_MARGIN + columnResizingWidths.length * 2, // 2px for border of each column
          isPivoting: false,
        }
      )
    );
  }, [columnResizingWidths, relationExpression.id, updateResizingWidth]);

  const beeTableColumns = useMemo<ReactTable.Column<ROWTYPE>[]>(() => {
    return columns.map((column, columnIndex) => ({
      accessor: column.id as any, // FIXME: Tiago -> Not good.
      label: column.name,
      dataType: column.dataType,
      isRowIndexColumn: false,
      minWidth: RELATION_EXPRESSION_COLUMN_MIN_WIDTH,
      setWidth: setColumnWidth(columnIndex),
      setResizingWidth: setColumnResizingWidth(columnIndex),
      resizingWidth: {
        value: columnResizingWidths[columnIndex]?.value ?? RELATION_EXPRESSION_COLUMN_MIN_WIDTH,
        isPivoting: columnResizingWidths[columnIndex]?.isPivoting ?? false,
      },
      width: column.width,
    }));
  }, [columnResizingWidths, columns, setColumnResizingWidth, setColumnWidth]);

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
    <div
      className={`relation-expression ${
        useResizingWidths().resizingWidths.get(relationExpression.id!)?.isPivoting ? "pivoting" : "not-pivoting"
      }`}
    >
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
