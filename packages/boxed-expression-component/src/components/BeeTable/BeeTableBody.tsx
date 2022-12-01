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
import { BeeTableTdIndex } from "./BeeTableTdIndex";
import { BeeTableTd } from "./BeeTableTd";

export interface BeeTableBodyProps {
  /** Table instance */
  reactTableInstance: ReactTable.TableInstance;
  /** The way in which the header will be rendered */
  headerVisibility?: BeeTableHeaderVisibility;
  /** True, for skipping the creation in the DOM of the last defined header group */
  skipLastHeaderGroup: boolean;
  /** Optional children element to be appended below the table content */
  children?: React.ReactElement[];
  /** Custom function for getting row key prop, and avoid using the row index */
  getRowKey: (row: ReactTable.Row) => string;
  /** Custom function for getting column key prop, and avoid using the column index */
  getColumnKey: (column: ReactTable.ColumnInstance) => string;
  /** Function to be executed when columns are modified */
  onColumnsUpdate?: (columns: ReactTable.ColumnInstance[]) => void;
  /** Function to be executed when a key has been pressed on a cell */
  onCellKeyDown: () => (e: KeyboardEvent) => void;
  /** Td props */
  tdProps: (cellIndex: number, rowIndex: number) => Partial<PfReactTable.TdProps>;
}

export const BeeTableBody: React.FunctionComponent<BeeTableBodyProps> = ({
  reactTableInstance,
  children,
  headerVisibility = BeeTableHeaderVisibility.Full,
  skipLastHeaderGroup,
  getRowKey,
  getColumnKey,
  onColumnsUpdate,
  onCellKeyDown,
  tdProps,
}) => {
  const { beeGwtService } = useBoxedExpressionEditor();

  const headerVisibilityMemo = useMemo(() => headerVisibility ?? BeeTableHeaderVisibility.Full, [headerVisibility]);

  const headerRowsLength = useMemo(() => {
    const headerGroupsLength = reactTableInstance.headerGroups.length - (skipLastHeaderGroup ? 1 : 0);
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
  }, [headerVisibility, reactTableInstance]);

  const renderCell = useCallback(
    (cellIndex: number, cell: ReactTable.Cell, rowIndex: number, isInForm: boolean) => {
      return (
        <BeeTableTd
          key={cellIndex}
          cellIndex={cellIndex}
          cell={cell}
          rowIndex={rowIndex}
          isInForm={isInForm}
          onKeyDown={onCellKeyDown}
          reactTableInstance={reactTableInstance}
          getColumnKey={getColumnKey}
          onColumnsUpdate={onColumnsUpdate!}
          getTdProps={tdProps}
          yPosition={headerRowsLength + rowIndex}
        />
      );
    },
    [getColumnKey, onColumnsUpdate, reactTableInstance, tdProps, onCellKeyDown, headerRowsLength]
  );

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
    (row: ReactTable.Row, rowIndex: number) => {
      reactTableInstance.prepareRow(row);
      const rowProps = { ...row.getRowProps(), style: {} };
      const RowDelegate = (row.original as any).rowDelegate; // FIXME: Tiago -> Bad typing.
      const rowKey = getRowKey(row);
      const rowClassNames = `${rowKey} table-row`;

      const renderTr = (isInForm: boolean) => (
        <PfReactTable.Tr
          className={rowClassNames}
          {...rowProps}
          ouiaId={"expression-row-" + rowIndex}
          key={rowKey}
          onClick={onRowClick(rowKey)}
        >
          {row.cells.map((cell: ReactTable.Cell, cellIndex: number) => {
            return renderCell(cellIndex, cell, rowIndex, isInForm);
          })}
        </PfReactTable.Tr>
      );

      return (
        <React.Fragment key={rowKey}>
          {RowDelegate ? <RowDelegate>{renderTr(true)}</RowDelegate> : renderTr(false)}
        </React.Fragment>
      );
    },
    [getRowKey, onRowClick, renderCell, reactTableInstance]
  );

  const renderAdditiveRow = useCallback(
    (rowIndex: number) => (
      <PfReactTable.Tr className="table-row additive-row">
        <BeeTableTdIndex
          isEmptyCell={true}
          rowIndex={rowIndex}
          cellIndex={0}
          onKeyDown={onCellKeyDown}
          xPosition={0}
          yPosition={headerRowsLength + rowIndex}
        />
        {children?.map((child, childIndex) => {
          return (
            <BeeTableTdIndex
              key={childIndex}
              cellIndex={childIndex}
              isEmptyCell={false}
              rowIndex={rowIndex}
              onKeyDown={onCellKeyDown}
              xPosition={childIndex + 1}
              yPosition={headerRowsLength + rowIndex}
            >
              {child}
            </BeeTableTdIndex>
          );
        })}
      </PfReactTable.Tr>
    ),
    [children, onCellKeyDown]
  );

  return (
    <PfReactTable.Tbody
      className={`${headerVisibilityMemo === BeeTableHeaderVisibility.None ? "missing-header" : ""}`}
      {...reactTableInstance.getTableBodyProps()}
    >
      {reactTableInstance.rows.map((row, rowIndex) => {
        return renderRow(row, rowIndex);
      })}
      {children ? renderAdditiveRow(reactTableInstance.rows.length) : null}
    </PfReactTable.Tbody>
  );
};
