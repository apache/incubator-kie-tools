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
import { BeeTableHeaderVisibility, InsertRowColumnsDirection } from "../../api";
import * as ReactTable from "react-table";
import { BeeTableTdForAdditionalRow } from "./BeeTableTdForAdditionalRow";
import { BeeTableTd } from "./BeeTableTd";
import { BeeTableCoordinatesContextProvider } from "../../selection/BeeTableSelectionContext";
import { ResizerStopBehavior } from "../../resizing/ResizingWidthsContext";
import { useBoxedExpressionEditor } from "../../BoxedExpressionEditorContext";

export interface BeeTableBodyProps<R extends object> {
  /** Table instance */
  reactTableInstance: ReactTable.TableInstance<R>;
  /** The way in which the header will be rendered */
  headerVisibility: BeeTableHeaderVisibility;
  /** Optional children element to be appended below the table content */
  additionalRow?: React.ReactElement[];
  /** Custom function for getting row key prop, and avoid using the row index */
  getRowKey: (row: ReactTable.Row<R>) => string;
  /** Custom function for getting column key prop, and avoid using the column index */
  getColumnKey: (column: ReactTable.ColumnInstance<R>) => string;
  /** Function to be executed when a column's data cell is clicked */
  onDataCellClick?: (columnID: string) => void;
  /** Function to be executed when a key up event occurs in a column's data cell */
  onDataCellKeyUp?: (columnID: string) => void;
  /** */
  onRowAdded?: (args: { beforeIndex: number; rowsCount: number; insertDirection: InsertRowColumnsDirection }) => void;

  shouldRenderRowIndexColumn: boolean;

  shouldShowRowsInlineControls: boolean;

  resizerStopBehavior: ResizerStopBehavior;

  lastColumnMinWidth?: number;

  rowWrapper?: React.FunctionComponent<React.PropsWithChildren<{ row: R; rowIndex: number }>>;

  isReadOnly: boolean;
  /** See BeeTable.ts */
  supportsEvaluationHitsCount?: (row: ReactTable.Row<R>) => boolean;
}

export function BeeTableBody<R extends object>({
  reactTableInstance,
  additionalRow,
  headerVisibility,
  getRowKey,
  getColumnKey,
  onRowAdded,
  onDataCellClick,
  onDataCellKeyUp,
  shouldRenderRowIndexColumn,
  shouldShowRowsInlineControls,
  resizerStopBehavior,
  lastColumnMinWidth,
  rowWrapper,
  isReadOnly,
  supportsEvaluationHitsCount,
}: BeeTableBodyProps<R>) {
  const { evaluationHitsCountById } = useBoxedExpressionEditor();

  const renderRow = useCallback(
    (row: ReactTable.Row<R>, rowIndex: number) => {
      reactTableInstance.prepareRow(row);

      const rowKey = getRowKey(row);
      const rowEvaluationHitsCount = evaluationHitsCountById ? evaluationHitsCountById?.get(rowKey) ?? 0 : undefined;
      const canDisplayEvaluationHitsCountRowOverlay =
        rowEvaluationHitsCount !== undefined && (supportsEvaluationHitsCount?.(row) ?? false);
      const rowClassName = `${rowKey}${canDisplayEvaluationHitsCountRowOverlay && rowEvaluationHitsCount > 0 ? " evaluation-hits-count-row-overlay" : ""}`;

      let evaluationHitsCountBadgeColumnIndex = -1;
      const renderTr = () => (
        <tr className={rowClassName} key={rowKey} data-testid={`kie-tools--bee--expression-row-${rowIndex}`}>
          {row.cells.map((cell, cellIndex) => {
            const columnKey = getColumnKey(reactTableInstance.allColumns[cellIndex]);
            const isColumnToRender =
              (cell.column.isRowIndexColumn && shouldRenderRowIndexColumn) || !cell.column.isRowIndexColumn;
            if (evaluationHitsCountBadgeColumnIndex === -1 && isColumnToRender) {
              // We store the index of the first column in the row
              // We show evaluation hits count badge in this column
              evaluationHitsCountBadgeColumnIndex = cellIndex;
            }
            const canDisplayEvaluationHitsCountBadge =
              canDisplayEvaluationHitsCountRowOverlay && cellIndex === evaluationHitsCountBadgeColumnIndex;
            return (
              <React.Fragment key={columnKey}>
                {isColumnToRender && (
                  <BeeTableTd<R>
                    resizerStopBehavior={resizerStopBehavior}
                    shouldShowRowsInlineControls={shouldShowRowsInlineControls}
                    columnIndex={cellIndex}
                    row={row}
                    rowIndex={rowIndex}
                    column={reactTableInstance.allColumns[cellIndex]}
                    onDataCellClick={onDataCellClick}
                    onDataCellKeyUp={onDataCellKeyUp}
                    onRowAdded={onRowAdded}
                    isActive={false}
                    shouldRenderInlineButtons={
                      shouldRenderRowIndexColumn
                        ? reactTableInstance.allColumns[cellIndex].isRowIndexColumn
                        : cellIndex === 1
                    }
                    lastColumnMinWidth={
                      cellIndex === reactTableInstance.allColumns.length - 1 ? lastColumnMinWidth : undefined
                    }
                    isReadOnly={isReadOnly}
                    canDisplayEvaluationHitsCountBadge={canDisplayEvaluationHitsCountBadge}
                    evaluationHitsCount={rowEvaluationHitsCount}
                  />
                )}
              </React.Fragment>
            );
          })}
        </tr>
      );

      const RowWrapper = rowWrapper;

      return (
        <React.Fragment key={rowKey}>
          {RowWrapper ? (
            <RowWrapper rowIndex={rowIndex} row={row.original}>
              {renderTr()}
            </RowWrapper>
          ) : (
            <>{renderTr()}</>
          )}
        </React.Fragment>
      );
    },
    [
      evaluationHitsCountById,
      supportsEvaluationHitsCount,
      reactTableInstance,
      rowWrapper,
      getRowKey,
      getColumnKey,
      shouldRenderRowIndexColumn,
      resizerStopBehavior,
      shouldShowRowsInlineControls,
      onDataCellClick,
      onDataCellKeyUp,
      onRowAdded,
      lastColumnMinWidth,
      isReadOnly,
    ]
  );

  const additionalRowIndex = useMemo(() => {
    return reactTableInstance.rows.length;
  }, [reactTableInstance.rows.length]);

  return (
    <tbody
      className={`${headerVisibility === BeeTableHeaderVisibility.None ? "missing-header" : ""}`}
      {...reactTableInstance.getTableBodyProps()}
    >
      {reactTableInstance.rows.map((row, rowIndex) => {
        return renderRow(row, rowIndex);
      })}

      {additionalRow && (
        <tr
          className={"additional-row"}
          data-ouia-component-id={"additional-row"}
          data-testid={"kie-tools--bee--additional-row"}
        >
          {shouldRenderRowIndexColumn && (
            <BeeTableCoordinatesContextProvider coordinates={{ rowIndex: additionalRowIndex, columnIndex: 0 }}>
              <BeeTableTdForAdditionalRow
                row={undefined as any}
                rowIndex={additionalRowIndex}
                columnIndex={0}
                column={reactTableInstance.allColumns[0]}
                isLastColumn={false}
                isEmptyCell={true}
                resizerStopBehavior={resizerStopBehavior}
                isReadOnly={isReadOnly}
              />
            </BeeTableCoordinatesContextProvider>
          )}
          {additionalRow.map((elem, elemIndex) => {
            const columnIndex = elemIndex + 1;
            return (
              <BeeTableCoordinatesContextProvider
                key={columnIndex}
                coordinates={{ rowIndex: additionalRowIndex, columnIndex }}
              >
                <BeeTableTdForAdditionalRow
                  key={columnIndex}
                  row={undefined as any}
                  rowIndex={additionalRowIndex}
                  column={reactTableInstance.allColumns[columnIndex]}
                  columnIndex={columnIndex}
                  isLastColumn={elemIndex === additionalRow.length - 1}
                  isEmptyCell={false}
                  resizerStopBehavior={resizerStopBehavior}
                  lastColumnMinWidth={columnIndex === additionalRow.length - 1 ? lastColumnMinWidth : undefined}
                  isReadOnly={isReadOnly}
                >
                  {elem}
                </BeeTableTdForAdditionalRow>
              </BeeTableCoordinatesContextProvider>
            );
          })}
        </tr>
      )}
    </tbody>
  );
}
