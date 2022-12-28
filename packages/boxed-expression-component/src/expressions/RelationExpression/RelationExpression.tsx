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
  generateUuid,
  DmnBuiltInDataType,
  getNextAvailablePrefixedName,
  BeeTableOperationConfig,
} from "../../api";
import { BeeTable, BeeTableCellUpdate, BeeTableColumnUpdate } from "../../table/BeeTable";
import { useBoxedExpressionEditorI18n } from "../../i18n";
import * as ReactTable from "react-table";
import { useBoxedExpressionEditorDispatch } from "../BoxedExpressionEditor/BoxedExpressionEditorContext";
import { BEE_TABLE_ROW_INDEX_COLUMN_WIDTH, useNestedExpressionContainer } from "../ContextExpression";
import { useEffect } from "react";
import { ResizingWidth, useResizingWidthsDispatch } from "../../resizing/ResizingWidthsContext";
import { NESTED_EXPRESSION_CLEAR_MARGIN } from "../ContextExpression";

type ROWTYPE = RelationExpressionDefinitionRow;

export const RELATION_EXPRESSION_COLUMN_MIN_WIDTH = 100;
export const RELATION_EXPRESSION_COLUMN_DEFAULT_WIDTH = 150;
export const RELATION_EXPRESSION_DEFAULT_VALUE = "";

export const RelationExpression: React.FunctionComponent<RelationExpressionDefinition> = (
  relationExpression: RelationExpressionDefinition
) => {
  const { i18n } = useBoxedExpressionEditorI18n();
  const { setExpression } = useBoxedExpressionEditorDispatch();

  const beeTableOperationConfig = useMemo<BeeTableOperationConfig>(
    () => [
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
    ],
    [i18n]
  );
  const columns = useMemo<RelationExpressionDefinitionColumn[]>(() => {
    return relationExpression.columns ?? [];
  }, [relationExpression.columns]);

  const rows = useMemo<RelationExpressionDefinitionRow[]>(() => {
    return relationExpression.rows ?? [];
  }, [relationExpression]);

  // RESIZING WIDTHS

  const { updateResizingWidth } = useResizingWidthsDispatch();

  const nestedExpressionContainer = useNestedExpressionContainer();

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

  // const isRelationExpressionPivoting = useMemo(() => {
  //   return columnResizingWidths.some(({ isPivoting }) => isPivoting);
  // }, [columnResizingWidths]);

  // const [pivotAwareNestedExpressionContainer, setPivotAwareNestedExpressionContainer] =
  //   useState(nestedExpressionContainer);
  // useEffect(() => {
  //   setPivotAwareNestedExpressionContainer((prev) => {
  //     return isRelationExpressionPivoting ? prev : nestedExpressionContainer;
  //   });
  // }, [isRelationExpressionPivoting, nestedExpressionContainer]);

  // useEffect(() => {
  //   setColumnResizingWidths((prev) => {
  //     const totalAvailableSpaceForColumns =
  //       pivotAwareNestedExpressionContainer.resizingWidth.value -
  //       BEE_TABLE_ROW_INDEX_COLUMN_WIDTH -
  //       NESTED_EXPRESSION_CLEAR_MARGIN -
  //       columns.length * 2; // 2px for border of each column

  //     const totalColumnsWidths = columns.reduce(
  //       (acc, { width }) => acc + (width ?? RELATION_EXPRESSION_COLUMN_MIN_WIDTH),
  //       0
  //     );

  //     let currentSpaceLeftToDistribute = totalAvailableSpaceForColumns;

  //     return columns.map((column, columnIndex) => {
  //       const proportion = (column.width ?? RELATION_EXPRESSION_COLUMN_MIN_WIDTH) / totalColumnsWidths;
  //       const proportionalColumnWidth = Math.max(
  //         column.width ?? RELATION_EXPRESSION_COLUMN_MIN_WIDTH,
  //         Math.floor(proportion * totalAvailableSpaceForColumns),
  //         RELATION_EXPRESSION_COLUMN_MIN_WIDTH
  //       );

  //       // If last column, take up all space left to distribute. That will make sure no pixel remains unused, due to the Math.floor rounding.
  //       if (columnIndex === columns.length - 1) {
  //         return {
  //           value: Math.max(currentSpaceLeftToDistribute, RELATION_EXPRESSION_COLUMN_MIN_WIDTH),
  //           isPivoting: false,
  //         };
  //       } else {
  //         currentSpaceLeftToDistribute -= proportionalColumnWidth;
  //         return {
  //           value: proportionalColumnWidth,
  //           isPivoting: false,
  //         };
  //       }
  //     });
  //   });
  // }, [columns, pivotAwareNestedExpressionContainer]);

  // FIXME: Tiago -> OMG
  // ***************************************************************************************
  // THIS STATE IS AMBIGUOUS. WE SHOULD RELY ON THE RESIZING WIDTHS TO GET THE COLUMN WIDTHS
  // ***************************************************************************************
  //
  // useEffect(() => {
  //   updateResizingWidth(relationExpression.id!, (prev) =>
  //     columnResizingWidths.reduce(
  //       (acc, { value, isPivoting }) => ({ value: acc.value + value, isPivoting: acc.isPivoting || isPivoting }),
  //       {
  //         value: BEE_TABLE_ROW_INDEX_COLUMN_WIDTH + NESTED_EXPRESSION_CLEAR_MARGIN + columnResizingWidths.length + 1, // 1px for border-right of last column
  //         isPivoting: false,
  //       }
  //     )
  //   );
  // }, [columnResizingWidths, relationExpression.id, updateResizingWidth]);

  const beeTableColumns = useMemo<ReactTable.Column<ROWTYPE>[]>(() => {
    return columns.map((column, columnIndex) => ({
      accessor: column.id as any, // FIXME: Tiago -> Not good.
      label: column.name,
      dataType: column.dataType,
      isRowIndexColumn: false,
      minWidth: RELATION_EXPRESSION_COLUMN_MIN_WIDTH,
      setWidth: setColumnWidth(columnIndex),
      width: column.width,
    }));
  }, [columns, setColumnWidth]);

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

  const onRowUpdates = useCallback(({ rows, columns }: BeeTableRowsUpdateArgs<ROWTYPE>) => {
    // Do nothing for now
  }, []);

  const onCellUpdates = useCallback(
    (cellUpdates: BeeTableCellUpdate<ROWTYPE>[]) => {
      setExpression((prev: RelationExpressionDefinition) => {
        const n = { ...prev };
        cellUpdates.forEach((u) => {
          const newRows = [...(n.rows ?? [])];

          const newCells = [...newRows[u.rowIndex].cells];
          newCells[u.columnIndex] = u.value;

          newRows[u.rowIndex] = {
            ...newRows[u.rowIndex],
            cells: newCells,
          };

          n.rows = newRows;
        });

        return n;
      });
    },
    [setExpression]
  );

  const onColumnUpdates = useCallback(
    (columnUpdates: BeeTableColumnUpdate<ROWTYPE>[]) => {
      setExpression((prev: RelationExpressionDefinition) => {
        const newColumns = [...(prev.columns ?? [])];

        for (const u of columnUpdates) {
          newColumns[u.columnIndex] = {
            ...newColumns[u.columnIndex],
            name: u.name,
            dataType: u.dataType,
          };
        }

        return {
          ...prev,
          columns: newColumns,
        };
      });
    },
    [setExpression]
  );

  const onRowAdded = useCallback(
    (args: { beforeIndex: number }) => {
      setExpression((prev: RelationExpressionDefinition) => {
        const newRows = [...(prev.rows ?? [])];
        newRows.splice(args.beforeIndex, 0, {
          id: generateUuid(),
          cells: Array.from(new Array(prev.columns?.length ?? 0)).map(() => ""),
        });

        return {
          ...prev,
          rows: newRows,
        };
      });
    },
    [setExpression]
  );

  const onColumnAdded = useCallback(
    (args: { beforeIndex: number }) => {
      setExpression((prev: RelationExpressionDefinition) => {
        const newColumns = [...(prev.columns ?? [])];
        newColumns.splice(args.beforeIndex, 0, {
          id: generateUuid(),
          name: getNextAvailablePrefixedName(prev.columns?.map((c) => c.name) ?? [], "column"),
          dataType: DmnBuiltInDataType.Undefined,
          width: RELATION_EXPRESSION_COLUMN_DEFAULT_WIDTH,
        });

        const newRows = [...(prev.rows ?? [])].map((row) => {
          const newCells = [...row.cells];
          newCells.splice(args.beforeIndex, 0, RELATION_EXPRESSION_DEFAULT_VALUE);
          return {
            ...row,
            cells: newCells,
          };
        });

        return {
          ...prev,
          columns: newColumns,
          rows: newRows,
        };
      });
    },
    [setExpression]
  );

  return (
    <div className={`relation-expression`}>
      <BeeTable<ROWTYPE>
        editColumnLabel={i18n.editRelation}
        columns={beeTableColumns}
        rows={beeTableRows}
        onCellUpdates={onCellUpdates}
        onColumnUpdates={onColumnUpdates}
        onRowUpdates={onRowUpdates}
        operationConfig={beeTableOperationConfig}
        onRowAdded={onRowAdded}
        onColumnAdded={onColumnAdded}
      />
    </div>
  );
};
