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

import * as React from "react";
import * as ReactTable from "react-table";
import { useMemo } from "react";
import {
  BeeTableOperation,
  BeeTableHeaderVisibility,
  BeeTableOperationConfig,
} from "@kie-tools/boxed-expression-component/dist/api";
import { getColumnsAtLastLevel } from "@kie-tools/boxed-expression-component/dist/table/BeeTable";
import { StandaloneBeeTable } from "@kie-tools/boxed-expression-component/dist/table/BeeTable/StandaloneBeeTable";
import { UnitablesCell, UnitablesInputRows } from "../UnitablesTypes";
import { BoxedExpressionEditorI18n } from "@kie-tools/boxed-expression-component/dist/i18n";

import "@kie-tools/boxed-expression-component/dist/@types/react-table";
import { ResizerStopBehavior } from "@kie-tools/boxed-expression-component/dist/resizing/ResizingWidthsContext";

export const CELL_MINIMUM_WIDTH = 150;

type BTWROW = any; // FIXME: Tiago

const EMPTY_SYMBOL = "";

export interface UnitablesBeeTable {
  id: string;
  i18n: BoxedExpressionEditorI18n;
  config: {
    rows: UnitablesInputRows[];
    inputs: UnitablesCell[];
  };
}

export function UnitablesBeeTable({ id, i18n, config }: UnitablesBeeTable) {
  const beeTableOperationConfig = useMemo<BeeTableOperationConfig>(
    () => [
      {
        group: i18n.rows,
        items: [
          { name: i18n.rowOperations.insertAbove, type: BeeTableOperation.RowInsertAbove },
          { name: i18n.rowOperations.insertBelow, type: BeeTableOperation.RowInsertBelow },
          { name: i18n.rowOperations.duplicate, type: BeeTableOperation.RowDuplicate },
          { name: i18n.rowOperations.reset, type: BeeTableOperation.RowReset },
          { name: i18n.rowOperations.delete, type: BeeTableOperation.RowDelete },
        ],
      },
    ],
    [i18n]
  );

  const editColumnLabel = useMemo(() => {
    const editColumnLabel: { [columnGroupType: string]: string } = {};
    return editColumnLabel;
  }, []);

  const beeTableColumns = useMemo<ReactTable.Column<BTWROW>[]>(() => {
    // Inputs
    return config.inputs.map((inputRow) => {
      if (inputRow.insideProperties) {
        return {
          label: inputRow.name,
          accessor: `input-${inputRow.name}` as any,
          dataType: inputRow.dataType,
          isRowIndexColumn: false,
          width: undefined,
          minWidth: CELL_MINIMUM_WIDTH,
          columns: inputRow.insideProperties.map((insideProperty) => {
            return {
              label: insideProperty.name,
              accessor: `input-${insideProperty.name}` as any,
              dataType: insideProperty.dataType,
              isRowIndexColumn: false,
              width: insideProperty.width,
              minWidth: CELL_MINIMUM_WIDTH,
              cellDelegate: insideProperty.cellDelegate,
            };
          }),
        };
      } else {
        return {
          label: inputRow.name,
          accessor: `input-${inputRow.name}` as any,
          dataType: inputRow.dataType,
          isRowIndexColumn: false,
          width: inputRow.width as number,
          minWidth: CELL_MINIMUM_WIDTH,
          cellDelegate: inputRow.cellDelegate,
        };
      }
    });
  }, [config]);

  const beeTableRows = useMemo<BTWROW[]>(() => {
    return config.rows.map((row) => {
      const rowArray = row.inputEntries.reduce((acc, entry) => {
        if (Array.isArray(entry)) {
          return [...acc, ...entry.map((element) => JSON.stringify(element, null, 2).replace(/"([^"]+)":/g, "$1:"))];
        } else if (typeof entry === "object") {
          return [
            ...acc,
            ...Object.values(entry).map((element) => JSON.stringify(element, null, 2).replace(/"([^"]+)":/g, "$1:")),
          ];
        } else {
          return [...acc, JSON.stringify(entry)];
        }
      }, []);

      return getColumnsAtLastLevel(beeTableColumns).reduce((tableRow: any, column, columnIndex) => {
        tableRow[column.accessor] = rowArray[columnIndex] || EMPTY_SYMBOL;
        tableRow.rowDelegate = row.rowDelegate;
        return tableRow;
      }, {});
    });
  }, [config, beeTableColumns]);

  return (
    <StandaloneBeeTable
      tableId={id}
      isEditableHeader={false}
      headerLevelCount={1}
      headerVisibility={BeeTableHeaderVisibility.AllLevels}
      editColumnLabel={editColumnLabel}
      operationConfig={beeTableOperationConfig}
      columns={beeTableColumns}
      rows={beeTableRows}
      enableKeyboardNavigation={true}
      shouldRenderRowIndexColumn={true}
      shouldShowRowsInlineControls={true}
      shouldShowColumnsInlineControls={false}
      resizerStopBehavior={ResizerStopBehavior.SET_WIDTH_ALWAYS}
    />
  );
}
