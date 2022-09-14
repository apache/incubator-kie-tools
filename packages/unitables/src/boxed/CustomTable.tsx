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
import { Column, ColumnInstance } from "react-table";

import {
  ColumnsUpdateArgs,
  ExpressionProps,
  GroupOperations,
  RowsUpdateArgs,
  TableHeaderVisibility,
  TableOperation,
} from "@kie-tools/boxed-expression-component/dist/api";
import { getColumnsAtLastLevel, Table } from "@kie-tools/boxed-expression-component/dist/components";
import "./CustomTable.css";
import { BoxedExpressionOutputRule, UnitablesClause, UnitablesInputRule } from "../UnitablesBoxedTypes";
import { BoxedExpressionEditorI18n } from "@kie-tools/boxed-expression-component/dist/i18n";

import "@kie-tools/boxed-expression-component/dist/@types/react-table";

export const CELL_MINIMUM_WIDTH = 150;

enum DecisionTableColumnType {
  InputClause = "input",
  OutputClause = "output",
  Annotation = "annotation",
}

const DASH_SYMBOL = "-";
const EMPTY_SYMBOL = "";

type DataRecord = Record<string, unknown>;

export interface CustomTableProps extends ExpressionProps {
  i18n: BoxedExpressionEditorI18n;
  /** Input columns definition */
  input?: UnitablesClause[];
  /** Output columns definition */
  output?: UnitablesClause[];
  /** Rules represent rows values */
  rules?: UnitablesInputRule[] | BoxedExpressionOutputRule[];
  /** Callback to be called when columns is updated */
  onColumnsUpdate: (columns: Column[]) => void;
  /** Callback to be called when row number is updated */
  onRowNumberUpdate?: (rowQtt: number, operation?: TableOperation, updatedRowIndex?: number) => void;
}

export function CustomTable(props: CustomTableProps) {
  const getColumnPrefix = useCallback((groupType?: string) => {
    switch (groupType) {
      case DecisionTableColumnType.InputClause:
        return "input-";
      case DecisionTableColumnType.OutputClause:
        return "output-";
      default:
        return "column-";
    }
  }, []);

  const generateHandlerConfigurationByColumn = useMemo(
    () => [
      {
        group: props.i18n.decisionRule,
        items: [
          { name: props.i18n.rowOperations.insertAbove, type: TableOperation.RowInsertAbove },
          { name: props.i18n.rowOperations.insertBelow, type: TableOperation.RowInsertBelow },
          { name: props.i18n.rowOperations.duplicate, type: TableOperation.RowDuplicate },
          { name: props.i18n.rowOperations.clear, type: TableOperation.RowClear },
          { name: props.i18n.rowOperations.delete, type: TableOperation.RowDelete },
        ],
      },
    ],
    [props.i18n]
  );

  const getHandlerConfiguration = useMemo(() => {
    const configuration: { [columnGroupType: string]: GroupOperations[] } = {};
    configuration[EMPTY_SYMBOL] = generateHandlerConfigurationByColumn;
    configuration[DecisionTableColumnType.InputClause] = generateHandlerConfigurationByColumn;
    configuration[DecisionTableColumnType.OutputClause] = generateHandlerConfigurationByColumn;
    return configuration;
  }, [generateHandlerConfigurationByColumn]);

  const getEditColumnLabel = useMemo(() => {
    const editColumnLabel: { [columnGroupType: string]: string } = {};
    editColumnLabel[DecisionTableColumnType.InputClause] = props.i18n.editClause.input;
    editColumnLabel[DecisionTableColumnType.OutputClause] = props.i18n.editClause.output;
    return editColumnLabel;
  }, [props.i18n.editClause.input, props.i18n.editClause.output]);

  const columns = useMemo(() => {
    const inputSection = (props.input ?? []).map((inputClause) => {
      if (inputClause.insideProperties) {
        const insideProperties = inputClause.insideProperties.map((insideInputClauses) => {
          return {
            label: insideInputClauses.name,
            accessor: `input-${insideInputClauses.name}`,
            dataType: insideInputClauses.dataType,
            width: insideInputClauses.width,
            groupType: DecisionTableColumnType.InputClause,
            cellDelegate: insideInputClauses.cellDelegate,
          };
        });
        return {
          groupType: DecisionTableColumnType.InputClause,
          label: inputClause.name,
          accessor: `input-${inputClause.name}`,
          dataType: inputClause.dataType,
          width: inputClause.width,
          cssClasses: "decision-table--input",
          columns: insideProperties,
          appendColumnsOnChildren: true,
        };
      }
      return {
        groupType: DecisionTableColumnType.InputClause,
        label: inputClause.name,
        accessor: `input-${inputClause.name}`,
        dataType: inputClause.dataType,
        width: inputClause.width,
        cssClasses: "decision-table--input",
        appendColumnsOnChildren: true,
        cellDelegate: inputClause.cellDelegate,
      };
    });

    let outputSection = undefined;
    if (props.output !== undefined) {
      outputSection = ((props.rules as BoxedExpressionOutputRule[])?.[0]?.outputEntries ?? []).map(
        (outputEntry, outputIndex) => {
          if (Array.isArray(outputEntry)) {
            return outputEntry.map((entry, entryIndex) => {
              const columns = Object.keys(entry).map((keys) => {
                return {
                  groupType: DecisionTableColumnType.OutputClause,
                  label: `${keys}`,
                  accessor: `output-${keys}-${entryIndex}`,
                  cssClasses: "decision-table--output",
                } as ColumnInstance;
              });
              return {
                groupType: DecisionTableColumnType.OutputClause,
                label: `${props.output?.[outputIndex]?.name}[${entryIndex}]`,
                accessor: `output-${props.output?.[outputIndex]?.name}[${entryIndex}]`,
                cssClasses: "decision-table--output",
                columns: columns,
                appendColumnsOnChildren: true,
                dataType: props.output?.[outputIndex]?.dataType,
              } as ColumnInstance;
            });
          }
          if (outputEntry !== null && typeof outputEntry === "object") {
            const columns = Object.keys(outputEntry).map((entryKey) => {
              const output = props.output?.[outputIndex]?.insideProperties?.find((property) => {
                return Object.values(property).find((value) => {
                  return value === entryKey;
                });
              });
              return {
                groupType: DecisionTableColumnType.OutputClause,
                label: entryKey,
                width: output?.width ?? CELL_MINIMUM_WIDTH,
                accessor: `output-${entryKey}`,
                cssClasses: "decision-table--output",
              } as ColumnInstance;
            });
            const width =
              columns.reduce((acc, column) => acc + (column.width as number), 0) + 2.22 * (columns.length - 1);
            return [
              {
                groupType: DecisionTableColumnType.OutputClause,
                label: props.output?.[outputIndex]?.name,
                accessor: `output-${props.output?.[outputIndex]?.name}`,
                cssClasses: "decision-table--output",
                columns: columns,
                width,
                appendColumnsOnChildren: true,
                dataType: props.output?.[outputIndex]?.dataType,
              } as ColumnInstance,
            ];
          }
          return [
            {
              groupType: DecisionTableColumnType.OutputClause,
              label: props.output?.[outputIndex]?.name,
              accessor: `output-${props.output?.[outputIndex]?.name}`,
              dataType: props.output?.[outputIndex]?.dataType,
              width: props.output?.[outputIndex]?.width,
              cssClasses: "decision-table--output",
              appendColumnsOnChildren: true,
            },
          ];
        }
      );
    }

    const updatedColumns: ColumnInstance[] = [];
    if (inputSection) {
      updatedColumns.push(...(inputSection as any));
    }
    if (outputSection) {
      const flattenOutput = outputSection.reduce((acc, outp) => [...acc, ...outp], []);
      updatedColumns.push(...(flattenOutput as any));
    }
    return updatedColumns;
  }, [props.input, props.output, props.rules]);

  const rows = useMemo(() => {
    return (props.rules ?? []).map((rule) => {
      const rowArray = [
        ...((rule as UnitablesInputRule)?.inputEntries ?? []),
        ...((rule as BoxedExpressionOutputRule)?.outputEntries ?? []),
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
      return getColumnsAtLastLevel(columns).reduce((tableRow: any, column, columnIndex: number) => {
        tableRow[column.accessor] = rowArray[columnIndex] || EMPTY_SYMBOL;
        tableRow.rowDelegate = (rule as UnitablesInputRule)?.rowDelegate;
        return tableRow;
      }, {});
    });
  }, [props.rules, columns]);

  const onRowsUpdate = useCallback(
    ({ rows, operation, rowIndex }: RowsUpdateArgs) => {
      const newRows = rows.map((row: any) =>
        getColumnsAtLastLevel(columns).reduce((filledRow: DataRecord, column) => {
          if (row.rowDelegate) {
            filledRow[column.accessor] = row[column.accessor];
            filledRow.rowDelegate = row.rowDelegate;
          } else if (row[column.accessor] === null || row[column.accessor] === undefined) {
            filledRow[column.accessor] =
              column.groupType === DecisionTableColumnType.InputClause ? DASH_SYMBOL : EMPTY_SYMBOL;
          } else {
            filledRow[column.accessor] = row[column.accessor];
          }
          return filledRow;
        }, {})
      );
      props.onRowNumberUpdate?.(newRows.length, operation, rowIndex);
    },
    [props.onRowNumberUpdate, columns]
  );

  const onRowAdding = useCallback(() => {
    return getColumnsAtLastLevel(columns).reduce((tableRow: DataRecord, column) => {
      tableRow[column.accessor] = EMPTY_SYMBOL;
      return tableRow;
    }, {} as DataRecord);
  }, [columns]);

  const searchRecursively = useCallback((child: any) => {
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
    child.childNodes.forEach(searchRecursively);
  }, []);

  useLayoutEffect(() => {
    const tbody = document.getElementsByTagName("tbody")[0];
    const inputsCells = Array.from(tbody.getElementsByTagName("td"));
    // remove id column
    inputsCells.shift();
    inputsCells.forEach((inputCell) => {
      searchRecursively(inputCell.childNodes[0]);
    });
  }, [columns, searchRecursively]);

  const onColumnsUpdate = useCallback(
    ({ columns }: ColumnsUpdateArgs) => {
      props.onColumnsUpdate(columns);
    },
    [props.onColumnsUpdate]
  );

  return (
    <div className="expression-container">
      <div className="expression-name-and-logic-type" />
      <div className="expression-container-box" data-ouia-component-id="expression-container">
        <div className={`custom-table ${props.id}`}>
          <div className={`logic-type-selector logic-type-selected`}>
            <Table
              editableHeader={false}
              headerLevels={1}
              headerVisibility={TableHeaderVisibility.Full}
              getColumnPrefix={getColumnPrefix}
              editColumnLabel={getEditColumnLabel}
              handlerConfiguration={getHandlerConfiguration}
              columns={columns}
              rows={rows}
              onColumnsUpdate={onColumnsUpdate}
              onRowsUpdate={onRowsUpdate}
              onRowAdding={onRowAdding}
              readOnlyCells={true}
              enableKeyboarNavigation={false}
            />
          </div>
        </div>
      </div>
    </div>
  );
}
