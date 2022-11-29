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
import { PropsWithChildren, useCallback, useMemo, useRef } from "react";
import * as ReactTable from "react-table";
import {
  DecisionTableExpressionDefinitionAnnotation,
  DecisionTableExpressionDefinitionBuiltInAggregation,
  DecisionTableExpressionDefinitionCaluse,
  DmnBuiltInDataType,
  DecisionTableExpressionDefinition,
  DecisionTableExpressionDefinitionRule,
  executeIfExpressionDefinitionChanged,
  generateUuid,
  BeeTableOperationGroup,
  DecisionTableExpressionDefinitionHitPolicy,
  ExpressionDefinitionLogicType,
  BeeTableRowsUpdateArgs,
  BeeTableHeaderVisibility,
  BeeTableOperation,
} from "../../api";
import { useBoxedExpressionEditor } from "../BoxedExpressionEditor/BoxedExpressionEditorContext";
import { useBoxedExpressionEditorI18n } from "../../i18n";
import { hashfy } from "../Resizer";
import { getColumnsAtLastLevel, BeeTable } from "../BeeTable";
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

interface SpreadFunction {
  updatedDecisionTable?: Partial<DecisionTableExpressionDefinition>;
  updatedColumns?: ReactTable.ColumnInstance[];
  updatedRows?: Array<object>;
}

export function DecisionTableExpression(decisionTable: PropsWithChildren<DecisionTableExpressionDefinition>) {
  const { setSupervisorHash, decisionNodeId, beeGwtService } = useBoxedExpressionEditor();

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
          { name: i18n.columnOperations.insertLeft, type: BeeTableOperation.ColumnInsertLeft },
          { name: i18n.columnOperations.insertRight, type: BeeTableOperation.ColumnInsertRight },
          { name: i18n.columnOperations.delete, type: BeeTableOperation.ColumnDelete },
        ],
      },
      {
        group: i18n.decisionRule,
        items: [
          { name: i18n.rowOperations.insertAbove, type: BeeTableOperation.RowInsertAbove },
          { name: i18n.rowOperations.insertBelow, type: BeeTableOperation.RowInsertBelow },
          { name: i18n.rowOperations.delete, type: BeeTableOperation.RowDelete },
          { name: i18n.rowOperations.duplicate, type: BeeTableOperation.RowDuplicate },
        ],
      },
    ],
    [i18n]
  );

  const getHandlerConfiguration = useMemo(() => {
    const configuration: { [columnGroupType: string]: BeeTableOperationGroup[] } = {};
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

  const columns = useMemo<ReactTable.ColumnInstance[]>(() => {
    const inputColumns = _.chain(decisionTable.input ?? [{ name: "input-1", dataType: DmnBuiltInDataType.Undefined }])
      .map(
        (inputClause: DecisionTableExpressionDefinitionCaluse) =>
          ({
            accessor: inputClause.id ?? generateUuid(),
            label: inputClause.name,
            dataType: inputClause.dataType,
            width: inputClause.width,
            groupType: DecisionTableColumnType.InputClause,
            cssClasses: "decision-table--input",
          } as ReactTable.ColumnInstance)
      )
      .value();
    const outputColumns = _.chain(
      decisionTable.output ?? [{ name: DECISION_NODE_DEFAULT_NAME, dataType: DmnBuiltInDataType.Undefined }]
    )
      .map(
        (outputClause: DecisionTableExpressionDefinitionCaluse) =>
          ({
            accessor: outputClause.id ?? generateUuid(),
            label: outputClause.name,
            dataType: outputClause.dataType,
            width: outputClause.width,
            groupType: DecisionTableColumnType.OutputClause,
            cssClasses: "decision-table--output",
          } as ReactTable.ColumnInstance)
      )
      .value();
    const annotationColumns = _.chain(decisionTable.annotations ?? [{ name: "annotation-1" }])
      .map(
        (annotation: DecisionTableExpressionDefinitionAnnotation) =>
          ({
            accessor: annotation.id ?? generateUuid(),
            label: annotation.name,
            width: annotation.width,
            inlineEditable: true,
            groupType: DecisionTableColumnType.Annotation,
            cssClasses: "decision-table--annotation",
          } as ReactTable.ColumnInstance)
      )
      .value();

    const inputSection = {
      groupType: DecisionTableColumnType.InputClause,
      accessor: "Input",
      label: "Input",
      cssClasses: "decision-table--input",
      columns: inputColumns,
    };
    const outputSection = {
      groupType: DecisionTableColumnType.OutputClause,
      accessor: decisionTable.isHeadless ? decisionTable.id : decisionNodeId,
      label: decisionTable.name ?? DECISION_NODE_DEFAULT_NAME,
      dataType: decisionTable.dataType ?? DmnBuiltInDataType.Undefined,
      cssClasses: "decision-table--output",
      columns: outputColumns,
      appendColumnsOnChildren: true,
    };
    const annotationSection = {
      groupType: DecisionTableColumnType.Annotation,
      accessor: "Annotations",
      label: "Annotations",
      cssClasses: "decision-table--annotation",
      columns: annotationColumns,
      inlineEditable: true,
    };

    return [inputSection, outputSection, annotationSection] as ReactTable.ColumnInstance[];
  }, [
    decisionNodeId,
    decisionTable.annotations,
    decisionTable.dataType,
    decisionTable.id,
    decisionTable.input,
    decisionTable.isHeadless,
    decisionTable.name,
    decisionTable.output,
  ]);

  const rows = useMemo(
    () =>
      (
        decisionTable.rules ?? [
          {
            id: generateUuid(),
            inputEntries: [DASH_SYMBOL],
            outputEntries: [EMPTY_SYMBOL],
            annotationEntries: [EMPTY_SYMBOL],
          },
        ]
      ).map((rule) => {
        const rowArray = [...rule.inputEntries, ...rule.outputEntries, ...rule.annotationEntries];
        const tableRow = _.chain(getColumnsAtLastLevel(columns))
          .reduce((tableRow: ReactTable.DataRecord, column, columnIndex: number) => {
            tableRow[column.accessor] = rowArray[columnIndex] || EMPTY_SYMBOL;
            return tableRow;
          }, {})
          .value();
        tableRow.id = rule.id;
        return tableRow;
      }),
    [columns, decisionTable.rules]
  );

  const spreadDecisionTableExpressionDefinition = useCallback(
    ({ updatedColumns, updatedDecisionTable, updatedRows }: SpreadFunction) => {
      const groupedColumns = _.groupBy(getColumnsAtLastLevel(updatedColumns ?? columns), (column) => column.groupType);
      const input: DecisionTableExpressionDefinitionCaluse[] = _.chain(
        groupedColumns[DecisionTableColumnType.InputClause]
      )
        .map((inputClause) => ({
          id: inputClause.accessor,
          name: inputClause.label as string,
          dataType: inputClause.dataType,
          width: inputClause.width,
        }))
        .value();
      const output: DecisionTableExpressionDefinitionCaluse[] = _.chain(
        groupedColumns[DecisionTableColumnType.OutputClause]
      )
        .map((outputClause) => ({
          id: outputClause.accessor,
          name: outputClause.label as string,
          dataType: outputClause.dataType,
          width: outputClause.width,
        }))
        .value();
      const annotations: DecisionTableExpressionDefinitionAnnotation[] = _.chain(
        groupedColumns[DecisionTableColumnType.Annotation]
      )
        .map((annotation) => ({
          id: annotation.accessor,
          name: annotation.label as string,
          width: annotation.width,
        }))
        .value();
      const rules: DecisionTableExpressionDefinitionRule[] = _.chain(updatedRows ?? rows)
        .map((row: ReactTable.DataRecord) => ({
          id: row.id as string,
          inputEntries: _.chain(input)
            .map((inputClause) => row[inputClause.id] as string)
            .value(),
          outputEntries: _.chain(output)
            .map((outputClause) => row[outputClause.id] as string)
            .value(),
          annotationEntries: _.chain(annotations)
            .map((annotation) => row[annotation.id] as string)
            .value(),
        }))
        .value();

      const updatedDefinition: Partial<DecisionTableExpressionDefinition> = {
        id: decisionTable.id,
        logicType: ExpressionDefinitionLogicType.DecisionTable,
        name: decisionTable.name ?? DECISION_NODE_DEFAULT_NAME,
        dataType: decisionTable.dataType ?? DmnBuiltInDataType.Undefined,
        hitPolicy: decisionTable.hitPolicy ?? DecisionTableExpressionDefinitionHitPolicy.Unique,
        aggregation: decisionTable.aggregation ?? DecisionTableExpressionDefinitionBuiltInAggregation["<None>"],
        input: input ?? [{ name: "input-1", dataType: DmnBuiltInDataType.Undefined }],
        output: output ?? [{ name: DECISION_NODE_DEFAULT_NAME, dataType: DmnBuiltInDataType.Undefined }],
        annotations: annotations ?? [{ name: "annotation-1" }],
        rules: rules ?? [
          { inputEntries: [DASH_SYMBOL], outputEntries: [EMPTY_SYMBOL], annotationEntries: [EMPTY_SYMBOL] },
        ],
        ...updatedDecisionTable,
      };

      if (decisionTable.isHeadless) {
        const headlessDefinition = _.omit(updatedDefinition, ["isHeadless"]);
        executeIfExpressionDefinitionChanged(
          decisionTable,
          headlessDefinition,
          () => {
            decisionTable.onUpdatingRecursiveExpression?.(headlessDefinition);
          },
          ["name", "dataType", "hitPolicy", "aggregation", "input", "output", "annotations", "rules"]
        );
      } else {
        executeIfExpressionDefinitionChanged(
          decisionTable,
          updatedDefinition,
          () => {
            setSupervisorHash(hashfy(updatedDefinition));
            beeGwtService?.broadcastDecisionTableExpressionDefinition?.(
              updatedDefinition as DecisionTableExpressionDefinition
            );
          },
          ["name", "dataType", "hitPolicy", "aggregation", "input", "output", "annotations", "rules"]
        );
      }
    },
    [beeGwtService, columns, decisionTable, rows, setSupervisorHash]
  );

  const singleOutputChildDataType = useRef(DmnBuiltInDataType.Undefined);

  const synchronizeDecisionNodeDataTypeWithSingleOutputColumnDataType = useCallback(
    (decisionNodeColumn: ReactTable.ColumnInstance) => {
      if (_.size(decisionNodeColumn.columns) === 1) {
        const updatedSingleOutputChildDataType = (_.first(decisionNodeColumn.columns) as ReactTable.ColumnInstance)
          .dataType;

        if (updatedSingleOutputChildDataType !== singleOutputChildDataType.current) {
          singleOutputChildDataType.current = updatedSingleOutputChildDataType;
          decisionNodeColumn.dataType = updatedSingleOutputChildDataType;
        } else if (decisionNodeColumn.dataType !== decisionTable.dataType ?? DmnBuiltInDataType.Undefined) {
          singleOutputChildDataType.current = decisionNodeColumn.dataType;
          (_.first(decisionNodeColumn.columns) as ReactTable.ColumnInstance).dataType = decisionNodeColumn.dataType;
        }
      }
    },
    [decisionTable.dataType]
  );

  const onColumnsUpdate = useCallback(
    ({ columns }) => {
      const decisionNodeColumn = _.find(columns, { groupType: DecisionTableColumnType.OutputClause });
      if (!decisionTable.isHeadless) {
        synchronizeDecisionNodeDataTypeWithSingleOutputColumnDataType(decisionNodeColumn);
      }
      spreadDecisionTableExpressionDefinition({
        updatedDecisionTable: {
          name: decisionNodeColumn.label,
          dataType: decisionNodeColumn.dataType,
        },
        updatedColumns: [...columns],
      });
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
    (updatedRows: ReactTable.DataRecord[]) =>
      updatedRows.map((row) => {
        const updatedRow = getColumnsAtLastLevel(columns).reduce(
          (filledRow: ReactTable.DataRecord, column: ReactTable.ColumnInstance) => {
            if (_.isNil(row[column.accessor])) {
              filledRow[column.accessor] =
                column.groupType === DecisionTableColumnType.InputClause ? DASH_SYMBOL : EMPTY_SYMBOL;
            } else {
              filledRow[column.accessor] = row[column.accessor];
            }
            return filledRow;
          },
          {}
        );
        updatedRow.id = row.id;
        return updatedRow;
      }),
    [columns]
  );

  const onRowsUpdate = useCallback(
    ({ rows }: BeeTableRowsUpdateArgs) => {
      spreadDecisionTableExpressionDefinition({ updatedRows: fillMissingCellValues(rows) });
    },
    [fillMissingCellValues, spreadDecisionTableExpressionDefinition]
  );

  const onRowAdding = useCallback(() => {
    return getColumnsAtLastLevel(columns).reduce(
      (tableRow: ReactTable.DataRecord, column: ReactTable.ColumnInstance) => {
        tableRow[column.accessor] =
          column.groupType === DecisionTableColumnType.InputClause ? DASH_SYMBOL : EMPTY_SYMBOL;
        return tableRow;
      },
      {} as ReactTable.DataRecord
    );
  }, [columns]);

  const onHitPolicySelect = useCallback(
    (itemId: DecisionTableExpressionDefinitionHitPolicy) => {
      spreadDecisionTableExpressionDefinition({
        updatedDecisionTable: {
          hitPolicy: itemId,
        },
      });
    },
    [spreadDecisionTableExpressionDefinition]
  );

  const onBuiltInAggregatorSelect = useCallback(
    (itemId) => {
      spreadDecisionTableExpressionDefinition({
        updatedDecisionTable: {
          aggregation: (DecisionTableExpressionDefinitionBuiltInAggregation as never)[itemId],
        },
      });
    },
    [spreadDecisionTableExpressionDefinition]
  );

  const controllerCell = useMemo(
    () => (
      <HitPolicySelector
        selectedHitPolicy={decisionTable.hitPolicy ?? DecisionTableExpressionDefinitionHitPolicy.Unique}
        selectedBuiltInAggregator={
          decisionTable.aggregation ?? DecisionTableExpressionDefinitionBuiltInAggregation["<None>"]
        }
        onHitPolicySelect={onHitPolicySelect}
        onBuiltInAggregatorSelect={onBuiltInAggregatorSelect}
      />
    ),
    [decisionTable.aggregation, decisionTable.hitPolicy, onBuiltInAggregatorSelect, onHitPolicySelect]
  );

  return (
    <div className={`decision-table-expression ${decisionTable.id}`}>
      <BeeTable
        headerLevels={1}
        headerVisibility={BeeTableHeaderVisibility.Full}
        getColumnPrefix={getColumnPrefix}
        editColumnLabel={getEditColumnLabel}
        handlerConfiguration={getHandlerConfiguration}
        columns={columns}
        rows={rows}
        onColumnsUpdate={onColumnsUpdate}
        onRowsUpdate={onRowsUpdate}
        onRowAdding={onRowAdding}
        controllerCell={controllerCell}
      />
    </div>
  );
}
