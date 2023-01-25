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
  DmnBuiltInDataType,
} from "@kie-tools/boxed-expression-component/dist/api";
import { getColumnsAtLastLevel } from "@kie-tools/boxed-expression-component/dist/table/BeeTable";
import { StandaloneBeeTable } from "@kie-tools/boxed-expression-component/dist/table/BeeTable/StandaloneBeeTable";
import "./BeeTableWrapper.css";
import { UnitablesOutputRows, UnitablesCell, UnitablesInputRows } from "../UnitablesTypes";
import { BoxedExpressionEditorI18n } from "@kie-tools/boxed-expression-component/dist/i18n";

import "@kie-tools/boxed-expression-component/dist/@types/react-table";
import { ResizerStopBehavior } from "@kie-tools/boxed-expression-component/dist/resizing/ResizingWidthsContext";

export const CELL_MINIMUM_WIDTH = 150;

type BTWROW = any; // FIXME: Tiago

const DASH_SYMBOL = "-";
const EMPTY_SYMBOL = "";

export interface BeeTableWrapperProps {
  id: string;
  i18n: BoxedExpressionEditorI18n;
  config:
    | {
        type: "inputs";
        rows: UnitablesInputRows[];
        inputs: UnitablesCell[];
      }
    | {
        type: "outputs";
        rows: UnitablesOutputRows[];
        outputs?: UnitablesCell[];
      };
}

export function BeeTableWrapper({ id, i18n, config }: BeeTableWrapperProps) {
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
    if (config.type === "inputs") {
      return config.inputs.map((inputRow) => {
        if (inputRow.insideProperties) {
          return {
            label: inputRow.name,
            accessor: `input-${inputRow.name}` as any,
            dataType: inputRow.dataType,
            width: inputRow.width as number,
            isRowIndexColumn: false,
            columns: inputRow.insideProperties.map((insideProperty) => {
              return {
                label: insideProperty.name,
                isRowIndexColumn: false,
                accessor: `input-${insideProperty.name}` as any,
                dataType: insideProperty.dataType,
                width: insideProperty.width,
                cellDelegate: insideProperty.cellDelegate,
              };
            }),
          };
        } else {
          return {
            label: inputRow.name,
            isRowIndexColumn: false,
            accessor: `input-${inputRow.name}` as any,
            dataType: inputRow.dataType,
            width: inputRow.width as number,
            cellDelegate: inputRow.cellDelegate,
          };
        }
      });
    }

    // Outputs
    else if (config.type === "outputs") {
      return (config.rows?.[0]?.outputEntries ?? []).flatMap((outputEntry, outputIndex) => {
        // Lists
        const output = config.outputs?.[outputIndex];
        if (Array.isArray(outputEntry)) {
          console.info("TIAGO-A:" + outputEntry);
          return [
            {
              label: `${output?.name}`,
              accessor: `output-array-parent-${output?.name}` as any,
              dataType: output?.dataType ?? DmnBuiltInDataType.Undefined,
              isRowIndexColumn: false,
              width: undefined,
              columns: outputEntry.map((entry, entryIndex) => ({
                label: `[${entryIndex}]`,
                accessor: `output-array-${entryIndex}` as any,
                dataType: undefined as any,
                isRowIndexColumn: false,
                width: CELL_MINIMUM_WIDTH,
                minWidth: CELL_MINIMUM_WIDTH,
              })),
            },
          ];
        }
        // Contexts/Structures
        else if (typeof outputEntry === "object") {
          console.info("TIAGO-B:" + outputEntry);
          return [
            {
              label: output?.name ?? "",
              accessor: `output-object-parent-${output?.name}` as any,
              dataType: output?.dataType ?? DmnBuiltInDataType.Undefined,
              isRowIndexColumn: false,
              width: undefined,
              minWidth: CELL_MINIMUM_WIDTH,
              columns: Object.keys(outputEntry).map((entryKey) => {
                const filteredOutputs = output?.insideProperties?.find((property) =>
                  Object.values(property).find((value) => value === entryKey)
                );
                return {
                  label: entryKey,
                  accessor: `output-object-${entryKey}` as any,
                  dataType: filteredOutputs?.dataType ?? DmnBuiltInDataType.Undefined,
                  isRowIndexColumn: false,
                  width: CELL_MINIMUM_WIDTH,
                  minWidth: CELL_MINIMUM_WIDTH,
                };
              }),
            },
          ];
        }
        // Primitives
        else {
          console.info("TIAGO-C:" + outputEntry);
          return [
            {
              label: "",
              accessor: `output-parent-${output?.name}` as any,
              dataType: undefined as any,
              isRowIndexColumn: false,
              width: undefined,
              minWidth: CELL_MINIMUM_WIDTH,
              columns: [
                {
                  label: output?.name ?? "",
                  accessor: `output-${output?.name}` as any,
                  dataType: output?.dataType ?? DmnBuiltInDataType.Undefined,
                  isRowIndexColumn: false,
                  width: CELL_MINIMUM_WIDTH,
                  minWidth: CELL_MINIMUM_WIDTH,
                },
              ],
            },
          ];
        }
      });
    } else {
      console.info("TIAGO-D:");
      return [];
    }
  }, [config]);

  const beeTableRows = useMemo<BTWROW[]>(() => {
    return config.rows.map((row) => {
      const rowArray = [
        ...((row as UnitablesInputRows)?.inputEntries ?? []),
        ...((row as UnitablesOutputRows)?.outputEntries ?? []),
      ].reduce((acc, entry) => {
        if (Array.isArray(entry)) {
          return [...acc, ...entry.map((element) => JSON.stringify(element, null, 2).replace(/"([^"]+)":/g, "$1:"))];
        }
        if (typeof entry === "object") {
          return [
            ...acc,
            ...Object.values(entry).map((element) => JSON.stringify(element, null, 2).replace(/"([^"]+)":/g, "$1:")),
          ];
        }
        return [...acc, JSON.stringify(entry)];
      }, []);

      return getColumnsAtLastLevel(beeTableColumns).reduce((tableRow: any, column, columnIndex) => {
        tableRow[column.accessor] = rowArray[columnIndex] || EMPTY_SYMBOL;
        tableRow.rowDelegate = (row as UnitablesInputRows)?.rowDelegate;
        return tableRow;
      }, {});
    });
  }, [config, beeTableColumns]);

  return (
    <div className="expression-container">
      <div className="expression-name-and-logic-type" />
      <div className="expression-container-box" data-ouia-component-id="expression-container">
        <div className={`custom-table ${id}`}>
          <div className={`logic-type-selector logic-type-selected`}>
            <StandaloneBeeTable
              isEditableHeader={false}
              headerLevelCount={1}
              headerVisibility={BeeTableHeaderVisibility.AllLevels}
              editColumnLabel={editColumnLabel}
              operationConfig={beeTableOperationConfig}
              columns={beeTableColumns}
              rows={beeTableRows}
              enableKeyboardNavigation={false}
              shouldRenderRowIndexColumn={false}
              shouldShowRowsInlineControls={true}
              shouldShowColumnsInlineControls={false}
              resizerStopBehavior={ResizerStopBehavior.SET_WIDTH_ALWAYS}
            />
          </div>
        </div>
      </div>
    </div>
  );
}
