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
import { useBoxedExpressionEditor } from "../BoxedExpressionEditor/BoxedExpressionEditorContext";
import { LOGIC_TYPE_SELECTOR_CLASS } from "../ExpressionDefinitionLogicTypeSelector";
import { BeeTableTdForAdditionalRow } from "./BeeTableTdForAdditionalRow";
import { BeeTableTd } from "./BeeTableTd";

export interface BeeTableBodyProps<R extends object> {
  /** Table instance */
  reactTableInstance: ReactTable.TableInstance<R>;
  /** The way in which the header will be rendered */
  headerVisibility?: BeeTableHeaderVisibility;
  /** True, for skipping the creation in the DOM of the last defined header group */
  skipLastHeaderGroup: boolean;
  /** Optional children element to be appended below the table content */
  additionalRow?: React.ReactElement[];
  /** Custom function for getting row key prop, and avoid using the row index */
  getRowKey: (row: ReactTable.Row<R>) => string;
  /** Custom function for getting column key prop, and avoid using the column index */
  getColumnKey: (column: ReactTable.ColumnInstance<R>) => string;
  /** Function to be executed when a key has been pressed on a cell */
  onCellKeyDown: () => (e: KeyboardEvent) => void;
  /** Td props */
  tdProps: (cellIndex: number, rowIndex: number) => Partial<PfReactTable.TdProps>;
}

export function BeeTableBody<R extends object>({
  reactTableInstance,
  additionalRow,
  headerVisibility = BeeTableHeaderVisibility.Full,
  skipLastHeaderGroup,
  getRowKey,
  getColumnKey,
  onCellKeyDown,
  tdProps,
}: BeeTableBodyProps<R>) {
  const { beeGwtService } = useBoxedExpressionEditor();

  const headerVisibilityMemo = useMemo(() => {
    return headerVisibility ?? BeeTableHeaderVisibility.Full;
  }, [headerVisibility]);

  const headerRowsLength = useMemo(() => {
    const headerGroupsLength = skipLastHeaderGroup
      ? reactTableInstance.headerGroups.length - 1
      : reactTableInstance.headerGroups.length;

    switch (headerVisibility) {
      case BeeTableHeaderVisibility.Full:
        return headerGroupsLength;
      case BeeTableHeaderVisibility.LastLevel:
        return headerGroupsLength - 1;
      case BeeTableHeaderVisibility.SecondToLastLevel:
        return headerGroupsLength - 1;
      default:
        return 0;
    }
  }, [headerVisibility, reactTableInstance.headerGroups.length, skipLastHeaderGroup]);

  const eventPathHasNestedExpression = useCallback((event: React.BaseSyntheticEvent, path: EventTarget[]) => {
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
  }, []);

  const onRowClick = useCallback(
    (rowKey: string) => (event: BaseSyntheticEvent) => {
      const nativeEvent = event.nativeEvent as Event;
      const path: EventTarget[] = nativeEvent?.composedPath?.() || [];
      if (!eventPathHasNestedExpression(event, path)) {
        beeGwtService?.selectObject(rowKey);
      }
    },
    [beeGwtService, eventPathHasNestedExpression]
  );

  const renderRow = useCallback(
    (row: ReactTable.Row<R>, rowIndex: number) => {
      reactTableInstance.prepareRow(row);
      const rowProps = { ...row.getRowProps(), style: {} };
      const RowDelegate = (row.original as any).rowDelegate; // FIXME: Tiago -> Bad typing.
      const rowKey = getRowKey(row);
      const rowClassNames = `${rowKey} table-row`;

      const renderTr = (args: { shouldUseCellDelegate: boolean }) => (
        <PfReactTable.Tr
          className={rowClassNames}
          {...rowProps}
          ouiaId={"expression-row-" + rowIndex}
          key={rowKey}
          onClick={onRowClick(rowKey)}
          style={{ display: "flex", backgroundColor: "#f9e9d6" }}
        >
          {row.cells.map((_, cellIndex) => {
            return (
              <BeeTableTd<R>
                key={cellIndex}
                index={cellIndex}
                row={row}
                rowIndex={rowIndex}
                shouldUseCellDelegate={args.shouldUseCellDelegate}
                onKeyDown={onCellKeyDown}
                column={reactTableInstance.allColumns[cellIndex]}
                getColumnKey={getColumnKey}
                getTdProps={tdProps}
                yPosition={headerRowsLength + rowIndex}
              />
            );
          })}
        </PfReactTable.Tr>
      );

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
    [getColumnKey, getRowKey, headerRowsLength, onCellKeyDown, onRowClick, reactTableInstance, tdProps]
  );

  const additionalRowIndex = useMemo(() => {
    return reactTableInstance.rows.length;
  }, [reactTableInstance.rows.length]);

  return (
    <PfReactTable.Tbody
      className={`${headerVisibilityMemo === BeeTableHeaderVisibility.None ? "missing-header" : ""}`}
      {...reactTableInstance.getTableBodyProps()}
    >
      {reactTableInstance.rows.map((row, rowIndex) => {
        return renderRow(row, rowIndex);
      })}
      {additionalRow && (
        <PfReactTable.Tr className="table-row additive-row">
          <BeeTableTdForAdditionalRow
            isLastColumn={false}
            isEmptyCell={true}
            rowIndex={additionalRowIndex}
            index={0}
            onKeyDown={onCellKeyDown}
            xPosition={0}
            yPosition={headerRowsLength + additionalRowIndex}
          />
          {additionalRow.map((elem, elemIndex) => (
            <BeeTableTdForAdditionalRow
              isLastColumn={elemIndex === additionalRow.length - 1}
              key={elemIndex}
              index={elemIndex}
              isEmptyCell={false}
              rowIndex={additionalRowIndex}
              onKeyDown={onCellKeyDown}
              xPosition={elemIndex + 1}
              yPosition={headerRowsLength + additionalRowIndex}
            >
              {elem}
            </BeeTableTdForAdditionalRow>
          ))}
        </PfReactTable.Tr>
      )}
    </PfReactTable.Tbody>
  );
}
