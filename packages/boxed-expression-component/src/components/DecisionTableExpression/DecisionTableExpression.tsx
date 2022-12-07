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
  DecisionTableExpressionDefinitionClause,
  DmnBuiltInDataType,
  DecisionTableExpressionDefinition,
  DecisionTableExpressionDefinitionRule,
  executeIfExpressionDefinitionChanged,
  generateUuid,
  BeeTableOperationHandlerGroup,
  DecisionTableExpressionDefinitionHitPolicy,
  ExpressionDefinitionLogicType,
  BeeTableRowsUpdateArgs,
  BeeTableHeaderVisibility,
  BeeTableOperation,
  ROWGENERICTYPE,
} from "../../api";
import { useBoxedExpressionEditor } from "../BoxedExpressionEditor/BoxedExpressionEditorContext";
import { useBoxedExpressionEditorI18n } from "../../i18n";
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
  updatedColumns?: ReactTable.ColumnInstance<ROWGENERICTYPE>[];
  updatedRows?: Array<object>;
}

export function DecisionTableExpression(decisionTable: PropsWithChildren<DecisionTableExpressionDefinition>) {
  const { decisionNodeId, beeGwtService } = useBoxedExpressionEditor();

  const { i18n } = useBoxedExpressionEditorI18n();

  const getNewColumnIdPrefix = useCallback((groupType?: string) => {
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

  const generateOperationHandlerConfig = useCallback(
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

  const operationHandlerConfig = useMemo(() => {
    const configuration: { [columnGroupType: string]: BeeTableOperationHandlerGroup[] } = {};
    configuration[EMPTY_SYMBOL] = generateOperationHandlerConfig(i18n.ruleAnnotation);
    configuration[DecisionTableColumnType.InputClause] = generateOperationHandlerConfig(i18n.inputClause);
    configuration[DecisionTableColumnType.OutputClause] = generateOperationHandlerConfig(i18n.outputClause);
    configuration[DecisionTableColumnType.Annotation] = generateOperationHandlerConfig(i18n.ruleAnnotation);
    return configuration;
  }, [generateOperationHandlerConfig, i18n.inputClause, i18n.outputClause, i18n.ruleAnnotation]);

  const getEditColumnLabel = useMemo(() => {
    const editColumnLabel: { [columnGroupType: string]: string } = {};
    editColumnLabel[DecisionTableColumnType.InputClause] = i18n.editClause.input;
    editColumnLabel[DecisionTableColumnType.OutputClause] = i18n.editClause.output;
    return editColumnLabel;
  }, [i18n]);

  const beeTableColumns = useMemo<ReactTable.ColumnInstance<ROWGENERICTYPE>[]>(() => {
    const inputColumns = _.chain(decisionTable.input ?? [{ name: "input-1", dataType: DmnBuiltInDataType.Undefined }])
      .map((inputClause: DecisionTableExpressionDefinitionClause) => ({
        accessor: inputClause.id ?? generateUuid(),
        label: inputClause.name,
        dataType: inputClause.dataType,
        width: inputClause.width,
        groupType: DecisionTableColumnType.InputClause,
        cssClasses: "decision-table--input",
      }))
      .value();
    const outputColumns = _.chain(
      decisionTable.output ?? [{ name: DECISION_NODE_DEFAULT_NAME, dataType: DmnBuiltInDataType.Undefined }]
    )
      .map((outputClause: DecisionTableExpressionDefinitionClause) => ({
        accessor: outputClause.id ?? generateUuid(),
        label: outputClause.name,
        dataType: outputClause.dataType,
        width: outputClause.width,
        groupType: DecisionTableColumnType.OutputClause,
        cssClasses: "decision-table--output",
      }))
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
          } as ReactTable.ColumnInstance<ROWGENERICTYPE>)
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

    return [inputSection, outputSection, annotationSection] as ReactTable.ColumnInstance<ROWGENERICTYPE>[];
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

  const beeTableRows = useMemo(
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
        const tableRow = _.chain(getColumnsAtLastLevel(beeTableColumns))
          .reduce((tableRow: ROWGENERICTYPE, column, columnIndex: number) => {
            tableRow[column.accessor] = rowArray[columnIndex] || EMPTY_SYMBOL;
            return tableRow;
          }, {})
          .value();
        tableRow.id = rule.id;
        return tableRow;
      }),
    [beeTableColumns, decisionTable.rules]
  );

  const spreadDecisionTableExpressionDefinition = useCallback(
    ({ updatedColumns, updatedDecisionTable, updatedRows }: SpreadFunction) => {
      const groupedColumns = _.groupBy(
        getColumnsAtLastLevel(updatedColumns ?? beeTableColumns),
        (column) => column.groupType
      );
      const input: DecisionTableExpressionDefinitionClause[] = _.chain(
        groupedColumns[DecisionTableColumnType.InputClause]
      )
        .map((inputClause) => ({
          id: inputClause.accessor,
          name: inputClause.label,
          dataType: inputClause.dataType,
          width: inputClause.width,
        }))
        .value();
      const output: DecisionTableExpressionDefinitionClause[] = _.chain(
        groupedColumns[DecisionTableColumnType.OutputClause]
      )
        .map((outputClause) => ({
          id: outputClause.accessor,
          name: outputClause.label,
          dataType: outputClause.dataType,
          width: outputClause.width,
        }))
        .value();
      const annotations: DecisionTableExpressionDefinitionAnnotation[] = _.chain(
        groupedColumns[DecisionTableColumnType.Annotation]
      )
        .map((annotation) => ({
          id: annotation.accessor,
          name: annotation.label,
          width: annotation.width,
        }))
        .value();
      const rules: DecisionTableExpressionDefinitionRule[] = _.chain(updatedRows ?? beeTableRows)
        .map((row: ROWGENERICTYPE) => ({
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

      const updatedDefinition: DecisionTableExpressionDefinition = {
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
            beeGwtService?.broadcastDecisionTableExpressionDefinition?.(updatedDefinition);
          },
          ["name", "dataType", "hitPolicy", "aggregation", "input", "output", "annotations", "rules"]
        );
      }
    },
    [beeGwtService, beeTableColumns, decisionTable, beeTableRows]
  );

  const singleOutputChildDataType = useRef(DmnBuiltInDataType.Undefined);

  const synchronizeDecisionNodeDataTypeWithSingleOutputColumnDataType = useCallback(
    (decisionNodeColumn: ReactTable.ColumnInstance<ROWGENERICTYPE>) => {
      if (_.size(decisionNodeColumn.columns) === 1) {
        const updatedSingleOutputChildDataType = _.first(decisionNodeColumn.columns)!.dataType;

        if (updatedSingleOutputChildDataType !== singleOutputChildDataType.current) {
          singleOutputChildDataType.current = updatedSingleOutputChildDataType;
          decisionNodeColumn.dataType = updatedSingleOutputChildDataType;
        } else if (decisionNodeColumn.dataType !== decisionTable.dataType ?? DmnBuiltInDataType.Undefined) {
          singleOutputChildDataType.current = decisionNodeColumn.dataType;
          _.first(decisionNodeColumn.columns)!.dataType = decisionNodeColumn.dataType;
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

      // FIXME: Tiago -> Apparently this is not necessary
      // decisionTable.onExpressionHeaderUpdated?.({
      //   name: decisionNodeColumn.label,
      //   dataType: decisionNodeColumn.dataType,
      // });
    },
    // eslint-disable-next-line react-hooks/exhaustive-deps
    [spreadDecisionTableExpressionDefinition, synchronizeDecisionNodeDataTypeWithSingleOutputColumnDataType]
  );

  const fillMissingCellValues = useCallback(
    (updatedRows: ROWGENERICTYPE[]) =>
      updatedRows.map((row) => {
        const updatedRow = getColumnsAtLastLevel(beeTableColumns).reduce(
          (filledRow: ROWGENERICTYPE, column: ReactTable.ColumnInstance<ROWGENERICTYPE>) => {
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
    [beeTableColumns]
  );

  const onRowsUpdate = useCallback(
    ({ rows }: BeeTableRowsUpdateArgs<ROWGENERICTYPE>) => {
      spreadDecisionTableExpressionDefinition({ updatedRows: fillMissingCellValues(rows) });
    },
    [fillMissingCellValues, spreadDecisionTableExpressionDefinition]
  );

  const onNewRow = useCallback(() => {
    return getColumnsAtLastLevel(beeTableColumns).reduce(
      (tableRow: ROWGENERICTYPE, column: ReactTable.ColumnInstance<ROWGENERICTYPE>) => {
        tableRow[column.accessor] =
          column.groupType === DecisionTableColumnType.InputClause ? DASH_SYMBOL : EMPTY_SYMBOL;
        return tableRow;
      },
      {} as ROWGENERICTYPE
    );
  }, [beeTableColumns]);

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
        headerLevelCount={1}
        headerVisibility={BeeTableHeaderVisibility.Full}
        getNewColumnIdPrefix={getNewColumnIdPrefix}
        editColumnLabel={getEditColumnLabel}
        operationHandlerConfig={operationHandlerConfig}
        columns={beeTableColumns}
        rows={beeTableRows}
        onColumnsUpdate={onColumnsUpdate}
        onRowsUpdate={onRowsUpdate}
        onNewRow={onNewRow}
        controllerCell={controllerCell}
      />
    </div>
  );
}
