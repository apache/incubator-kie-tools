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

import * as _ from "lodash";
import * as React from "react";
import { PropsWithChildren, useCallback, useContext, useEffect, useMemo, useRef, useState } from "react";
import { ColumnInstance, DataRecord } from "react-table";
import {
  Annotation,
  BuiltinAggregation,
  Clause,
  DataType,
  DecisionTableProps,
  DecisionTableRule,
  executeIfExpressionDefinitionChanged,
  GroupOperations,
  HitPolicy,
  LogicType,
  TableHeaderVisibility,
  TableOperation,
} from "../../api";
import { BoxedExpressionGlobalContext } from "../../context";
import { useBoxedExpressionEditorI18n } from "../../i18n";
import { hashfy } from "../Resizer";
import { getColumnsAtLastLevel, Table } from "../Table";
import "./DecisionTableExpression.css";
import { HitPolicySelector } from "./HitPolicySelector";

enum DecisionTableColumnType {
  InputClause = "input",
  OutputClause = "output",
  Annotation = "annotation",
}

const DASH_SYMBOL = "-";
const EMPTY_SYMBOL = "";
const DECISION_NODE_DEFAULT_NAME = "output-1";

export function DecisionTableExpression(decisionTable: PropsWithChildren<DecisionTableProps>) {
  const { i18n } = useBoxedExpressionEditorI18n();

  const getColumnPrefix = useCallback((groupType?: string) => {
    switch (groupType) {
      case DecisionTableColumnType.InputClause:
        return "input-";
      case DecisionTableColumnType.OutputClause:
        return "output-";
      case DecisionTableColumnType.Annotation:
        return "annotation-";
      default:
        return "column-";
    }
  }, []);

  const generateHandlerConfigurationByColumn = useCallback(
    (groupName: string) => [
      {
        group: groupName,
        items: [
          { name: i18n.columnOperations.insertLeft, type: TableOperation.ColumnInsertLeft },
          { name: i18n.columnOperations.insertRight, type: TableOperation.ColumnInsertRight },
          { name: i18n.columnOperations.delete, type: TableOperation.ColumnDelete },
        ],
      },
      {
        group: i18n.decisionRule,
        items: [
          { name: i18n.rowOperations.insertAbove, type: TableOperation.RowInsertAbove },
          { name: i18n.rowOperations.insertBelow, type: TableOperation.RowInsertBelow },
          { name: i18n.rowOperations.delete, type: TableOperation.RowDelete },
          { name: i18n.rowOperations.duplicate, type: TableOperation.RowDuplicate },
        ],
      },
    ],
    [i18n]
  );

  const { setSupervisorHash } = useContext(BoxedExpressionGlobalContext);

  const getHandlerConfiguration = useMemo(() => {
    const configuration: { [columnGroupType: string]: GroupOperations[] } = {};
    configuration[EMPTY_SYMBOL] = generateHandlerConfigurationByColumn(i18n.ruleAnnotation);
    configuration[DecisionTableColumnType.InputClause] = generateHandlerConfigurationByColumn(i18n.inputClause);
    configuration[DecisionTableColumnType.OutputClause] = generateHandlerConfigurationByColumn(i18n.outputClause);
    configuration[DecisionTableColumnType.Annotation] = generateHandlerConfigurationByColumn(i18n.ruleAnnotation);
    return configuration;
  }, [generateHandlerConfigurationByColumn, i18n.inputClause, i18n.outputClause, i18n.ruleAnnotation]);

  const getEditColumnLabel = useMemo(() => {
    const editColumnLabel: { [columnGroupType: string]: string } = {};
    editColumnLabel[DecisionTableColumnType.InputClause] = i18n.editClause.input;
    editColumnLabel[DecisionTableColumnType.OutputClause] = i18n.editClause.output;
    return editColumnLabel;
  }, [i18n.editClause.input, i18n.editClause.output]);

  const columns = useMemo(() => {
    const inputColumns = (decisionTable.input ?? [{ name: "input-1", dataType: DataType.Undefined }])?.map(
      (inputClause) =>
        ({
          label: inputClause.name,
          accessor: inputClause.name,
          dataType: inputClause.dataType,
          width: inputClause.width,
          groupType: DecisionTableColumnType.InputClause,
          cssClasses: "decision-table--input",
        } as ColumnInstance)
    );
    const outputColumns = (
      decisionTable.output ?? [{ name: DECISION_NODE_DEFAULT_NAME, dataType: DataType.Undefined }]
    )?.map(
      (outputClause) =>
        ({
          label: outputClause.name,
          accessor: outputClause.name,
          dataType: outputClause.dataType,
          width: outputClause.width,
          groupType: DecisionTableColumnType.OutputClause,
          cssClasses: "decision-table--output",
        } as ColumnInstance)
    );
    const annotationColumns = (decisionTable.annotations ?? [{ name: "annotation-1" }])?.map(
      (annotation) =>
        ({
          label: annotation.name,
          accessor: annotation.name,
          width: annotation.width,
          inlineEditable: true,
          groupType: DecisionTableColumnType.Annotation,
          cssClasses: "decision-table--annotation",
        } as ColumnInstance)
    );

    const inputSection = {
      groupType: DecisionTableColumnType.InputClause,
      label: "Input",
      accessor: "Input",
      cssClasses: "decision-table--input",
      columns: inputColumns,
    };
    const outputSection = {
      groupType: DecisionTableColumnType.OutputClause,
      label: decisionTable.name ?? DECISION_NODE_DEFAULT_NAME,
      accessor: decisionTable.name ?? DECISION_NODE_DEFAULT_NAME,
      dataType: decisionTable.dataType ?? DataType.Undefined,
      cssClasses: "decision-table--output",
      columns: outputColumns,
      appendColumnsOnChildren: true,
    };
    const annotationSection = {
      groupType: DecisionTableColumnType.Annotation,
      label: "Annotations",
      accessor: "Annotations",
      cssClasses: "decision-table--annotation",
      columns: annotationColumns,
      inlineEditable: true,
    };

    return [inputSection, outputSection, annotationSection];
  }, [
    decisionTable.annotations,
    decisionTable.dataType,
    decisionTable.input,
    decisionTable.name,
    decisionTable.output,
  ]);

  const rows = useMemo(
    () =>
      (
        decisionTable.rules ?? [
          { inputEntries: [DASH_SYMBOL], outputEntries: [EMPTY_SYMBOL], annotationEntries: [EMPTY_SYMBOL] },
        ]
      ).map((rule) => {
        const rowArray = [...rule.inputEntries, ...rule.outputEntries, ...rule.annotationEntries];
        return getColumnsAtLastLevel(columns).reduce((tableRow: DataRecord, column, columnIndex: number) => {
          tableRow[column.accessor] = rowArray[columnIndex] || EMPTY_SYMBOL;
          return tableRow;
        }, {});
      }),
    [columns, decisionTable.rules]
  );

  const spreadDecisionTableExpressionDefinition = useCallback(
    (updatedDecisionTable?: Partial<DecisionTableProps>, updatedColumns?: any, updatedRows?: Array<object>) => {
      const groupedColumns = _.groupBy(getColumnsAtLastLevel(updatedColumns ?? columns), (column) => column.groupType);
      const input: Clause[] = _.map(groupedColumns[DecisionTableColumnType.InputClause], (inputClause) => ({
        name: inputClause.accessor,
        dataType: inputClause.dataType,
        width: inputClause.width,
      }));
      const output: Clause[] = _.map(groupedColumns[DecisionTableColumnType.OutputClause], (outputClause) => ({
        name: outputClause.accessor,
        dataType: outputClause.dataType,
        width: outputClause.width,
      }));
      const annotations: Annotation[] = _.map(groupedColumns[DecisionTableColumnType.Annotation], (annotation) => ({
        name: annotation.accessor,
        width: annotation.width,
      }));
      const rules: DecisionTableRule[] = _.map(updatedRows ?? rows, (row: DataRecord) => ({
        inputEntries: _.map(input, (inputClause) => row[inputClause.name] as string),
        outputEntries: _.map(output, (outputClause) => row[outputClause.name] as string),
        annotationEntries: _.map(annotations, (annotation) => row[annotation.name] as string),
      }));

      const updatedDefinition: Partial<DecisionTableProps> = {
        uid: decisionTable.uid,
        logicType: LogicType.DecisionTable,
        name: decisionTable.name ?? DECISION_NODE_DEFAULT_NAME,
        dataType: decisionTable.dataType ?? DataType.Undefined,
        hitPolicy: decisionTable.hitPolicy ?? HitPolicy.Unique,
        aggregation: decisionTable.aggregation ?? BuiltinAggregation["<None>"],
        input: input ?? [{ name: "input-1", dataType: DataType.Undefined }],
        output: output ?? [{ name: DECISION_NODE_DEFAULT_NAME, dataType: DataType.Undefined }],
        annotations: annotations ?? [{ name: "annotation-1" }],
        rules: rules ?? [
          { inputEntries: [DASH_SYMBOL], outputEntries: [EMPTY_SYMBOL], annotationEntries: [EMPTY_SYMBOL] },
        ],
        ...updatedDecisionTable,
      };

      if (decisionTable.isHeadless) {
        const headlessDefinition = _.omit(updatedDefinition, ["name", "dataType", "isHeadless"]);
        executeIfExpressionDefinitionChanged(
          decisionTable,
          headlessDefinition,
          () => {
            decisionTable.onUpdatingRecursiveExpression?.(headlessDefinition);
          },
          ["hitPolicy", "aggregation", "input", "output", "annotations", "rules"]
        );
      } else {
        executeIfExpressionDefinitionChanged(
          decisionTable,
          updatedDefinition,
          () => {
            setSupervisorHash(hashfy(updatedDefinition));
            window.beeApi?.broadcastDecisionTableExpressionDefinition?.(updatedDefinition as DecisionTableProps);
          },
          ["name", "dataType", "hitPolicy", "aggregation", "input", "output", "annotations", "rules"]
        );
      }
    },
    [columns, decisionTable, rows, setSupervisorHash]
  );

  const singleOutputChildDataType = useRef(DataType.Undefined);

  const synchronizeDecisionNodeDataTypeWithSingleOutputColumnDataType = useCallback(
    (decisionNodeColumn: ColumnInstance) => {
      if (_.size(decisionNodeColumn.columns) === 1) {
        const updatedSingleOutputChildDataType = (_.first(decisionNodeColumn.columns) as ColumnInstance).dataType;

        if (updatedSingleOutputChildDataType !== singleOutputChildDataType.current) {
          singleOutputChildDataType.current = updatedSingleOutputChildDataType;
          decisionNodeColumn.dataType = updatedSingleOutputChildDataType;
        } else if (decisionNodeColumn.dataType !== decisionTable.dataType ?? DataType.Undefined) {
          singleOutputChildDataType.current = decisionNodeColumn.dataType;
          (_.first(decisionNodeColumn.columns) as ColumnInstance).dataType = decisionNodeColumn.dataType;
        }
      }
    },
    [decisionTable.dataType]
  );

  const onColumnsUpdate = useCallback(
    (updatedColumns) => {
      const decisionNodeColumn = _.find(updatedColumns, { groupType: DecisionTableColumnType.OutputClause });
      synchronizeDecisionNodeDataTypeWithSingleOutputColumnDataType(decisionNodeColumn);
      spreadDecisionTableExpressionDefinition(
        {
          name: decisionNodeColumn.label,
          dataType: decisionNodeColumn.dataType,
        },
        [...updatedColumns]
      );
      decisionTable.onUpdatingNameAndDataType?.(decisionNodeColumn.label, decisionNodeColumn.dataType);
    },
    // eslint-disable-next-line react-hooks/exhaustive-deps
    [
      decisionTable.onUpdatingNameAndDataType,
      spreadDecisionTableExpressionDefinition,
      synchronizeDecisionNodeDataTypeWithSingleOutputColumnDataType,
    ]
  );

  const fillMissingCellValues = useCallback(
    (updatedRows: DataRecord[]) =>
      updatedRows.map((row) =>
        getColumnsAtLastLevel(columns).reduce((filledRow: DataRecord, column: ColumnInstance) => {
          if (_.isNil(row[column.accessor])) {
            filledRow[column.accessor] =
              column.groupType === DecisionTableColumnType.InputClause ? DASH_SYMBOL : EMPTY_SYMBOL;
          } else {
            filledRow[column.accessor] = row[column.accessor];
          }
          return filledRow;
        }, {})
      ),
    [columns]
  );

  const onRowsUpdate = useCallback(
    (updatedRows) => {
      spreadDecisionTableExpressionDefinition(undefined, undefined, fillMissingCellValues(updatedRows));
    },
    [fillMissingCellValues, spreadDecisionTableExpressionDefinition]
  );

  const onRowAdding = useCallback(() => {
    return getColumnsAtLastLevel(columns).reduce((tableRow: DataRecord, column: ColumnInstance) => {
      tableRow[column.accessor] = column.groupType === DecisionTableColumnType.InputClause ? DASH_SYMBOL : EMPTY_SYMBOL;
      return tableRow;
    }, {} as DataRecord);
  }, [columns]);

  const onHitPolicySelect = useCallback(
    (itemId: HitPolicy) => {
      spreadDecisionTableExpressionDefinition({ hitPolicy: itemId });
    },
    [spreadDecisionTableExpressionDefinition]
  );

  const onBuiltInAggregatorSelect = useCallback(
    (itemId) => {
      spreadDecisionTableExpressionDefinition({ aggregation: (BuiltinAggregation as never)[itemId] });
    },
    [spreadDecisionTableExpressionDefinition]
  );

  const controllerCell = useMemo(
    () => (
      <HitPolicySelector
        selectedHitPolicy={decisionTable.hitPolicy ?? HitPolicy.Unique}
        selectedBuiltInAggregator={decisionTable.aggregation ?? BuiltinAggregation["<None>"]}
        onHitPolicySelect={onHitPolicySelect}
        onBuiltInAggregatorSelect={onBuiltInAggregatorSelect}
      />
    ),
    [decisionTable.aggregation, decisionTable.hitPolicy, onBuiltInAggregatorSelect, onHitPolicySelect]
  );

  return (
    <div className={`decision-table-expression ${decisionTable.uid}`}>
      <Table
        headerLevels={1}
        headerVisibility={TableHeaderVisibility.Full}
        getColumnPrefix={getColumnPrefix}
        editColumnLabel={getEditColumnLabel}
        handlerConfiguration={getHandlerConfiguration}
        columns={columns}
        rows={(rows ?? []) as DataRecord[]}
        onColumnsUpdate={onColumnsUpdate}
        onRowsUpdate={onRowsUpdate}
        onRowAdding={onRowAdding}
        controllerCell={controllerCell}
      />
    </div>
  );
}
