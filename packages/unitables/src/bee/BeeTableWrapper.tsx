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
import { useCallback, useLayoutEffect, useMemo } from "react";
import * as ReactTable from "react-table";
import {
  BeeTableColumnsUpdateArgs,
  BeeTableRowsUpdateArgs,
  BeeTableOperation,
  BeeTableHeaderVisibility,
  BeeTableOperationHandlerGroup,
} from "@kie-tools/boxed-expression-component/dist/api";
import { getColumnsAtLastLevel, BeeTable } from "@kie-tools/boxed-expression-component/dist/components";
import "./BeeTableWrapper.css";
import { UnitablesOutputRows, UnitablesCell, UnitablesInputRows } from "../UnitablesTypes";
import { BoxedExpressionEditorI18n } from "@kie-tools/boxed-expression-component/dist/i18n";

import "@kie-tools/boxed-expression-component/dist/@types/react-table";

export const CELL_MINIMUM_WIDTH = 150;

type BTWROW = any;

enum UnitablesBeeTableWrapperColumnType {
  InputClause = "input",
  OutputClause = "output",
  Annotation = "annotation",
}

const DASH_SYMBOL = "-";
const EMPTY_SYMBOL = "";

export interface BeeTableWrapperProps {
  id: string;
  i18n: BoxedExpressionEditorI18n;
  /** Columns definition */
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
  /** Callback to be called when columns is updated */
  onColumnsUpdate: (columns: ReactTable.ColumnInstance[]) => void;
  /** Callback to be called when row number is updated */
  onRowNumberUpdate?: (rowQtt: number, operation?: BeeTableOperation, updatedRowIndex?: number) => void;
}

export function BeeTableWrapper({ id, i18n, config, onColumnsUpdate, onRowNumberUpdate }: BeeTableWrapperProps) {
  const getColumnPrefix = useCallback((groupType?: string) => {
    switch (groupType) {
      case UnitablesBeeTableWrapperColumnType.InputClause:
        return "input-";
      case UnitablesBeeTableWrapperColumnType.OutputClause:
        return "output-";
      default:
        return "column-";
    }
  }, []);

  const beeTableOperationGroup = useMemo(
    () => [
      {
        group: i18n.decisionRule,
        items: [
          { name: i18n.rowOperations.insertAbove, type: BeeTableOperation.RowInsertAbove },
          { name: i18n.rowOperations.insertBelow, type: BeeTableOperation.RowInsertBelow },
          { name: i18n.rowOperations.duplicate, type: BeeTableOperation.RowDuplicate },
          { name: i18n.rowOperations.clear, type: BeeTableOperation.RowClear },
          { name: i18n.rowOperations.delete, type: BeeTableOperation.RowDelete },
        ],
      },
    ],
    [i18n]
  );

  const beeTableOperationConfig = useMemo(() => {
    const configuration: { [columnGroupType: string]: BeeTableOperationHandlerGroup[] } = {};
    configuration[EMPTY_SYMBOL] = beeTableOperationGroup;
    configuration[UnitablesBeeTableWrapperColumnType.InputClause] = beeTableOperationGroup;
    configuration[UnitablesBeeTableWrapperColumnType.OutputClause] = beeTableOperationGroup;
    return configuration;
  }, [beeTableOperationGroup]);

  const editColumnLabel = useMemo(() => {
    const editColumnLabel: { [columnGroupType: string]: string } = {};
    editColumnLabel[UnitablesBeeTableWrapperColumnType.InputClause] = i18n.editClause.input;
    editColumnLabel[UnitablesBeeTableWrapperColumnType.OutputClause] = i18n.editClause.output;
    return editColumnLabel;
  }, [i18n]);

  const beeTableColumns = useMemo<ReactTable.ColumnInstance[]>(() => {
    if (config.type === "inputs") {
      return config.inputs.map((inputRow) => {
        if (inputRow.insideProperties) {
          const insideProperties = inputRow.insideProperties.map((insideProperty) => {
            return {
              label: insideProperty.name,
              accessor: `input-${insideProperty.name}`,
              dataType: insideProperty.dataType,
              width: insideProperty.width,
              groupType: UnitablesBeeTableWrapperColumnType.InputClause,
              cellDelegate: insideProperty.cellDelegate,
            };
          });
          return {
            groupType: UnitablesBeeTableWrapperColumnType.InputClause,
            label: inputRow.name,
            accessor: `input-${inputRow.name}`,
            dataType: inputRow.dataType,
            width: inputRow.width,
            cssClasses: "decision-table--input",
            columns: insideProperties,
            appendColumnsOnChildren: true,
          };
        }
        return {
          groupType: UnitablesBeeTableWrapperColumnType.InputClause,
          label: inputRow.name,
          accessor: `input-${inputRow.name}`,
          dataType: inputRow.dataType,
          width: inputRow.width,
          cssClasses: "decision-table--input",
          appendColumnsOnChildren: true,
          cellDelegate: inputRow.cellDelegate,
        };
      });
    } else if (config.type === "outputs") {
      const outputColumns = (config.rows?.[0]?.outputEntries ?? []).map((outputEntry, outputIndex) => {
        if (Array.isArray(outputEntry)) {
          return outputEntry.map((entry, entryIndex) => {
            return {
              groupType: UnitablesBeeTableWrapperColumnType.OutputClause,
              label: `${config.outputs?.[outputIndex]?.name}[${entryIndex}]`,
              accessor: `output-${config.outputs?.[outputIndex]?.name}[${entryIndex}]`,
              cssClasses: "decision-table--output",
              columns: Object.keys(entry).map((keys) => {
                return {
                  groupType: UnitablesBeeTableWrapperColumnType.OutputClause,
                  label: `${keys}`,
                  accessor: `output-${keys}-${entryIndex}`,
                  cssClasses: "decision-table--output",
                };
              }),
              appendColumnsOnChildren: true,
              dataType: config.outputs?.[outputIndex]?.dataType,
            };
          });
        }
        if (outputEntry !== null && typeof outputEntry === "object") {
          const columns = Object.keys(outputEntry).map((entryKey) => {
            const filteredOutputs = config.outputs?.[outputIndex]?.insideProperties?.find((property) => {
              return Object.values(property).find((value) => value === entryKey);
            });
            return {
              groupType: UnitablesBeeTableWrapperColumnType.OutputClause,
              label: entryKey,
              width: filteredOutputs?.width ?? CELL_MINIMUM_WIDTH,
              accessor: `output-${entryKey}`,
              cssClasses: "decision-table--output",
            };
          });

          // FIXME: Tiago -> Magic number '2.22'. What does it mean?
          const width = columns.reduce((acc, column) => acc + column.width, 0) + 2.22 * (columns.length - 1);

          return [
            {
              groupType: UnitablesBeeTableWrapperColumnType.OutputClause,
              label: config.outputs?.[outputIndex]?.name,
              accessor: `output-${config.outputs?.[outputIndex]?.name}`,
              cssClasses: "decision-table--output",
              columns,
              width,
              appendColumnsOnChildren: true,
              dataType: config.outputs?.[outputIndex]?.dataType,
            },
          ];
        }
        return [
          {
            groupType: UnitablesBeeTableWrapperColumnType.OutputClause,
            label: config.outputs?.[outputIndex]?.name,
            accessor: `output-${config.outputs?.[outputIndex]?.name}`,
            dataType: config.outputs?.[outputIndex]?.dataType,
            width: config.outputs?.[outputIndex]?.width,
            cssClasses: "decision-table--output",
            appendColumnsOnChildren: true,
          },
        ];
      });
      return outputColumns.reduce((acc, column) => [...acc, ...column], []) as any;
    }
  }, [config]);

  const beeTableRows = useMemo(() => {
    return config.rows.map((row) => {
      const rowArray = [
        ...((row as UnitablesInputRows)?.inputEntries ?? []),
        ...((row as UnitablesOutputRows)?.outputEntries ?? []),
      ].reduce((acc, entry) => {
        if (Array.isArray(entry)) {
          return [
            ...acc,
            ...entry.flatMap((entryElement) =>
              typeof entryElement === "object" ? [...Object.values(entryElement)] : entryElement
            ),
          ];
        }
        if (typeof entry === "object") {
          return [...acc, ...Object.values(entry)];
        }
        return [...acc, entry];
      }, []);
      return getColumnsAtLastLevel(beeTableColumns).reduce((tableRow: any, column, columnIndex: number) => {
        tableRow[column.accessor] = rowArray[columnIndex] || EMPTY_SYMBOL;
        tableRow.rowDelegate = (row as UnitablesInputRows)?.rowDelegate;
        return tableRow;
      }, {});
    });
  }, [config, beeTableColumns]);

  const onRowsUpdate = useCallback(
    ({ rows, operation, rowIndex }: BeeTableRowsUpdateArgs<BTWROW>) => {
      const newRows = rows.map((row) =>
        getColumnsAtLastLevel(beeTableColumns).reduce((filledRow, column) => {
          if (row.rowDelegate) {
            filledRow[column.accessor] = row[column.accessor];
            filledRow.rowDelegate = row.rowDelegate;
          } else if (row[column.accessor] === null || row[column.accessor] === undefined) {
            filledRow[column.accessor] =
              column.groupType === UnitablesBeeTableWrapperColumnType.InputClause ? DASH_SYMBOL : EMPTY_SYMBOL;
          } else {
            filledRow[column.accessor] = row[column.accessor];
          }
          return filledRow;
        }, {} as BTWROW)
      );
      onRowNumberUpdate?.(newRows.length, operation, rowIndex);
    },
    [onRowNumberUpdate, beeTableColumns]
  );

  const onNewRow = useCallback(() => {
    return getColumnsAtLastLevel(beeTableColumns).reduce((tableRow, column) => {
      tableRow[column.accessor] = EMPTY_SYMBOL;
      return tableRow;
    }, {} as BTWROW);
  }, [beeTableColumns]);

  const recusivelyAssignHeight = useCallback((child: any) => {
    if (!child) {
      return;
    }
    if (child.tagName === "svg") {
      return;
    }
    if (child.style) {
      child.style.height = "60px";
    }
    if (!child.childNodes) {
      return;
    }
    child.childNodes.forEach(recusivelyAssignHeight);
  }, []);

  useLayoutEffect(() => {
    const tbody = document.getElementsByTagName("tbody")[0];
    const inputsCells = Array.from(tbody.getElementsByTagName("td"));
    // remove id column
    inputsCells.shift();
    inputsCells.forEach((inputCell) => {
      recusivelyAssignHeight(inputCell.childNodes[0]);
    });
  }, [beeTableColumns, recusivelyAssignHeight]);

  const onBeeTableColumnsUpdate = useCallback(
    ({ columns }: BeeTableColumnsUpdateArgs<BTWROW>) => {
      onColumnsUpdate(columns);
    },
    [onColumnsUpdate]
  );

  return (
    <div className="expression-container">
      <div className="expression-name-and-logic-type" />
      <div className="expression-container-box" data-ouia-component-id="expression-container">
        <div className={`custom-table ${id}`}>
          <div className={`logic-type-selector logic-type-selected`}>
            <BeeTable
              editableHeader={false}
              headerLevels={1}
              headerVisibility={BeeTableHeaderVisibility.Full}
              getColumnPrefix={getColumnPrefix}
              editColumnLabel={editColumnLabel}
              operationHandlerConfig={beeTableOperationConfig}
              columns={beeTableColumns}
              rows={beeTableRows}
              onColumnsUpdate={onBeeTableColumnsUpdate}
              onRowsUpdate={onRowsUpdate}
              onNewRow={onNewRow}
              readOnlyCells={true}
              enableKeyboardNavigation={false}
            />
          </div>
        </div>
      </div>
    </div>
  );
}
