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
import "@kogito-tooling/boxed-expression-component";
import { ColumnInstance, DataRecord } from "react-table";
import {
  ExpressionProps,
  GroupOperations,
  TableHeaderVisibility,
  TableOperation,
} from "@kogito-tooling/boxed-expression-component/dist/api";
import { getColumnsAtLastLevel, Table } from "@kogito-tooling/boxed-expression-component/dist/components";
import "./DmnRunnerTable.css";
import { DmnRunnerClause, DmnRunnerRule } from "./DmnRunnerTableTypes";
import { useDmnAutoTableI18n } from "../i18n";

enum DecisionTableColumnType {
  InputClause = "input",
  OutputClause = "output",
  Annotation = "annotation",
}

const DASH_SYMBOL = "-";
const EMPTY_SYMBOL = "";

export interface DmnRunnerTabularProps extends ExpressionProps {
  /** Input columns definition */
  input?: DmnRunnerClause[];
  /** Output columns definition */
  output?: DmnRunnerClause[];
  /** Rules represent rows values */
  rules?: DmnRunnerRule[];
  /** Callback to be called when row number is updated */
  onRowNumberUpdated: (rowNumber: number, operation?: TableOperation, updatedRowIndex?: number) => void;
  onColumnsUpdate: (columns: ColumnInstance[]) => void;
}

export function DmnRunnerTabular(props: DmnRunnerTabularProps) {
  const { i18n } = useDmnAutoTableI18n();

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
        group: i18n.decisionRule,
        items: [
          { name: i18n.rowOperations.insertAbove, type: TableOperation.RowInsertAbove },
          { name: i18n.rowOperations.insertBelow, type: TableOperation.RowInsertBelow },
          { name: i18n.rowOperations.duplicate, type: TableOperation.RowDuplicate },
          { name: i18n.rowOperations.clear, type: TableOperation.RowClear },
          { name: i18n.rowOperations.delete, type: TableOperation.RowDelete },
        ],
      },
    ],
    [i18n]
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
    editColumnLabel[DecisionTableColumnType.InputClause] = i18n.editClause.input;
    editColumnLabel[DecisionTableColumnType.OutputClause] = i18n.editClause.output;
    return editColumnLabel;
  }, [i18n.editClause.input, i18n.editClause.output]);

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

    const outputSection = (props.rules?.[0].outputEntries ?? []).map((outputEntry, outputIndex) => {
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
          const output = props.output?.[outputIndex]?.insideProperties?.find((i) =>
            Object.keys(i).find((keys) => keys === entryKey)
          );
          return {
            groupType: DecisionTableColumnType.OutputClause,
            label: entryKey,
            width: output[entryKey].width,
            accessor: `output-${entryKey}`,
            cssClasses: "decision-table--output",
          } as ColumnInstance;
        });

        return [
          {
            groupType: DecisionTableColumnType.OutputClause,
            label: props.output?.[outputIndex]?.name,
            accessor: `output-${props.output?.[outputIndex]?.name}`,
            cssClasses: "decision-table--output",
            columns: columns,
            width: props.output?.[outputIndex]?.width,
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
    });

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
      const rowArray = [...(rule?.inputEntries ?? []), ...(rule?.outputEntries ?? [])].reduce((acc, entry) => {
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
        tableRow.rowDelegate = rule.rowDelegate;
        return tableRow;
      }, {});
    });
  }, [props.rules, columns]);

  const onRowsUpdate = useCallback(
    (updatedRows, operation?: TableOperation, updatedRowIndex?: number) => {
      const newRows = updatedRows.map((row: any) =>
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
      props.onRowNumberUpdated?.(newRows.length, operation, updatedRowIndex);
    },
    [props.onRowNumberUpdated, columns]
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
    (columns) => {
      props.onColumnsUpdate(columns);
    },
    [props.onColumnsUpdate]
  );

  return (
    <div className="expression-container">
      <div className="expression-name-and-logic-type" />
      <div className="expression-container-box" data-ouia-component-id="expression-container">
        <div className={`decision-table-expression ${props.uid}`}>
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
            />
          </div>
        </div>
      </div>
    </div>
  );
}
