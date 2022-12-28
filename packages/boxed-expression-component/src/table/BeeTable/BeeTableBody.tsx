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
import { BaseSyntheticEvent, useCallback, useMemo } from "react";
import * as PfReactTable from "@patternfly/react-table";
import { BeeTableHeaderVisibility } from "../../api";
import * as ReactTable from "react-table";
import { useBoxedExpressionEditor } from "../../expressions/BoxedExpressionEditor/BoxedExpressionEditorContext";
import { LOGIC_TYPE_SELECTOR_CLASS } from "../../expressions/ExpressionDefinitionLogicTypeSelector";
import { BeeTableTdForAdditionalRow } from "./BeeTableTdForAdditionalRow";
import { BeeTableTd } from "./BeeTableTd";
import { BeeTableCellUpdate } from ".";

export interface BeeTableBodyProps<R extends object> {
  /** Table instance */
  reactTableInstance: ReactTable.TableInstance<R>;
  /** The way in which the header will be rendered */
  headerVisibility?: BeeTableHeaderVisibility;
  /** True, for skipping the creation in the DOM of the last defined header group */
  headerRowsCount: number;
  /** Optional children element to be appended below the table content */
  additionalRow?: React.ReactElement[];
  /** Custom function for getting row key prop, and avoid using the row index */
  getRowKey: (row: ReactTable.Row<R>) => string;
  /** Custom function for getting column key prop, and avoid using the column index */
  getColumnKey: (column: ReactTable.ColumnInstance<R>) => string;
  /** */
  onRowAdded?: (args: { beforeIndex: number }) => void;
}

export function BeeTableBody<R extends object>({
  reactTableInstance,
  additionalRow,
  headerVisibility = BeeTableHeaderVisibility.Full,
  headerRowsCount,
  getRowKey,
  getColumnKey,
  onRowAdded,
}: BeeTableBodyProps<R>) {
  const { beeGwtService } = useBoxedExpressionEditor();

  const onTrClick = useCallback(
    (rowKey: string) => (event: BaseSyntheticEvent) => {
      function eventPathHasNestedExpression(event: React.BaseSyntheticEvent, path: EventTarget[]) {
        let currentPathTarget: EventTarget = event.target;
        let currentIndex = 0;
        while (currentPathTarget !== event.currentTarget && currentIndex < path.length) {
          currentIndex++;
          currentPathTarget = path[currentIndex];
          if ((currentPathTarget as HTMLElement)?.classList?.contains(LOGIC_TYPE_SELECTOR_CLASS)) {
            return true;
          }
        }
        return false;
      }

      const nativeEvent = event.nativeEvent as Event;
      const path: EventTarget[] = nativeEvent?.composedPath?.() || [];
      if (!eventPathHasNestedExpression(event, path)) {
        beeGwtService?.selectObject(rowKey);
      }
    },
    [beeGwtService]
  );

  const renderRow = useCallback(
    (row: ReactTable.Row<R>, rowIndex: number) => {
      reactTableInstance.prepareRow(row);
      const rowKey = getRowKey(row);

      const renderTr = (args: { shouldUseCellDelegate: boolean }) => (
        <PfReactTable.Tr
          className={`${rowKey} table-row`}
          {...row.getRowProps()}
          ouiaId={"expression-row-" + rowIndex} // FIXME: Tiago -> Bad name.
          key={rowKey}
          onClick={onTrClick(rowKey)}
          style={{ display: "flex" }}
        >
          {row.cells.map((_, columnIndex) => {
            return (
              // <div
              //   key={getColumnKey(reactTableInstance.allColumns[columnIndex])}
              //   style={{ width: _.column.resizingWidth?.value ?? "60px" }}
              // >
              //   oi
              // </div>
              <BeeTableTd<R>
                key={getColumnKey(reactTableInstance.allColumns[columnIndex])}
                columnIndex={columnIndex}
                row={row}
                rowIndex={rowIndex}
                shouldUseCellDelegate={args.shouldUseCellDelegate}
                column={reactTableInstance.allColumns[columnIndex]}
                yPosition={headerRowsCount + rowIndex}
                onRowAdded={onRowAdded}
                isActive={false}
              />
            );
          })}
        </PfReactTable.Tr>
      );

      const RowDelegate = (row.original as any).rowDelegate; // FIXME: Tiago -> Bad typing.
      return (
        <React.Fragment key={rowKey}>
          {RowDelegate ? (
            <RowDelegate>{renderTr({ shouldUseCellDelegate: true })}</RowDelegate> //
          ) : (
            renderTr({ shouldUseCellDelegate: false })
          )}
        </React.Fragment>
      );
    },
    [reactTableInstance, getRowKey, onTrClick, getColumnKey, headerRowsCount, onRowAdded]
  );

  const additionalRowIndex = useMemo(() => {
    return reactTableInstance.rows.length;
  }, [reactTableInstance.rows.length]);

  return (
    <PfReactTable.Tbody
      className={`${headerVisibility === BeeTableHeaderVisibility.None ? "missing-header" : ""}`}
      {...reactTableInstance.getTableBodyProps()}
    >
      {reactTableInstance.rows.map((row, rowIndex) => {
        return renderRow(row, rowIndex);
      })}

      {additionalRow && (
        <PfReactTable.Tr className="table-row additive-row">
          <BeeTableTdForAdditionalRow
            row={undefined as any}
            column={reactTableInstance.allColumns[0]}
            isLastColumn={false}
            isEmptyCell={true}
            rowIndex={additionalRowIndex}
            columnIndex={0}
            xPosition={0}
            yPosition={headerRowsCount + additionalRowIndex}
          />
          {additionalRow.map((elem, elemIndex) => (
            <BeeTableTdForAdditionalRow
              row={undefined as any}
              column={reactTableInstance.allColumns[0]}
              isLastColumn={elemIndex === additionalRow.length - 1}
              key={elemIndex}
              columnIndex={elemIndex}
              isEmptyCell={false}
              rowIndex={additionalRowIndex}
              xPosition={elemIndex + 1}
              yPosition={headerRowsCount + additionalRowIndex}
            >
              {elem}
            </BeeTableTdForAdditionalRow>
          ))}
        </PfReactTable.Tr>
      )}
    </PfReactTable.Tbody>
  );
}
