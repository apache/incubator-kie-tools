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
import { PropsWithChildren, useCallback, useEffect, useMemo, useRef, useState } from "react";
import * as ReactTable from "react-table";
import {
  DecisionTableExpressionDefinitionBuiltInAggregation,
  DmnBuiltInDataType,
  DecisionTableExpressionDefinition,
  generateUuid,
  BeeTableOperationGroup,
  DecisionTableExpressionDefinitionHitPolicy,
  BeeTableRowsUpdateArgs,
  BeeTableHeaderVisibility,
  BeeTableOperation,
  getNextAvailablePrefixedName,
} from "../../api";
import {
  useBoxedExpressionEditor,
  useBoxedExpressionEditorDispatch,
} from "../BoxedExpressionEditor/BoxedExpressionEditorContext";
import { useBoxedExpressionEditorI18n } from "../../i18n";
import { getColumnsAtLastLevel, BeeTable } from "../../table/BeeTable";
import "./DecisionTableExpression.css";
import { HitPolicySelector, HIT_POLICIES_THAT_SUPPORT_AGGREGATION } from "./HitPolicySelector";
import { ResizingWidth, useResizingWidthDispatch } from "../ExpressionDefinitionRoot";
import { BEE_TABLE_ROW_INDEX_COLUMN_WIDTH, NESTED_EXPRESSION_CLEAR_MARGIN } from "../ContextExpression";
import { assertUnreachable } from "../ExpressionDefinitionLogicTypeSelector";

type ROWTYPE = any;

enum DecisionTableColumnType {
  InputClause = "input",
  OutputClause = "output",
  Annotation = "annotation",
}

const DECISION_NODE_DEFAULT_NAME = "output-1";

const INPUT_DEFAULT_VALUE = "-";
const OUTPUT_DEFAULT_VALUE = "";
const ANNOTATION_DEFAULT_VALUE = "";

export const DECISION_TABLE_INPUT_MIN_WIDTH = 100;
export const DECISION_TABLE_INPUT_DEFAULT_WIDTH = 100;
export const DECISION_TABLE_OUTPUT_MIN_WIDTH = 100;
export const DECISION_TABLE_OUTPUT_DEFAULT_WIDTH = 150;
export const DECISION_TABLE_ANNOTATION_MIN_WIDTH = 100;
export const DECISION_TABLE_ANNOTATION_DEFAULT_WIDTH = 250;

export function DecisionTableExpression(decisionTable: PropsWithChildren<DecisionTableExpressionDefinition>) {
  const { i18n } = useBoxedExpressionEditorI18n();
  const { decisionNodeId } = useBoxedExpressionEditor();
  const { setExpression } = useBoxedExpressionEditorDispatch();
  const { updateResizingWidth } = useResizingWidthDispatch();

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
    const configuration: { [columnGroupType: string]: BeeTableOperationGroup[] } = {};
    configuration[""] = generateOperationHandlerConfig(i18n.ruleAnnotation);
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

  const setInputColumnWidth = useCallback(
    (inputIndex: number) => (newWidth: number) => {
      setExpression((prev: DecisionTableExpressionDefinition) => {
        const newInputs = [...(prev.input ?? [])];
        newInputs[inputIndex].width = newWidth;
        return { ...prev, input: newInputs };
      });
    },
    [setExpression]
  );

  const setOutputColumnWidth = useCallback(
    (outputIndex: number) => (newWidth: number) => {
      setExpression((prev: DecisionTableExpressionDefinition) => {
        const newOutputs = [...(prev.output ?? [])];
        newOutputs[outputIndex].width = newWidth;
        return { ...prev, output: newOutputs };
      });
    },
    [setExpression]
  );

  const setAnnotationColumnWidth = useCallback(
    (annotationIndex: number) => (newWidth: number) => {
      setExpression((prev: DecisionTableExpressionDefinition) => {
        const newAnnotations = [...(prev.annotations ?? [])];
        newAnnotations[annotationIndex].width = newWidth;
        return { ...prev, annotations: newAnnotations };
      });
    },
    [setExpression]
  );

  const [inputsResizingWidths, setInputsResizingWidths] = useState<ResizingWidth[]>(
    [...(decisionTable.input ?? [])].map((c) => ({
      value: c.width ?? DECISION_TABLE_INPUT_DEFAULT_WIDTH,
      isPivoting: false,
    }))
  );

  const [outputsResizingWidths, setOutputsResizingWidths] = useState<ResizingWidth[]>(
    [...(decisionTable.output ?? [])].map((c) => ({
      value: c.width ?? DECISION_TABLE_OUTPUT_DEFAULT_WIDTH,
      isPivoting: false,
    }))
  );

  const [annotationsResizingWidths, setAnnotationsResizingWidths] = useState<ResizingWidth[]>(
    [...(decisionTable.annotations ?? [])].map((c) => ({
      value: c.width ?? DECISION_TABLE_ANNOTATION_DEFAULT_WIDTH,
      isPivoting: false,
    }))
  );

  const setInputResizingWidth = useCallback(
    (inputIndex: number) => (getNewResizingWidth: (prev: ResizingWidth) => ResizingWidth) => {
      setInputsResizingWidths((prev: ResizingWidth[]) => {
        const newResizingWidth = getNewResizingWidth(prev[inputIndex]);
        const n = [...prev];
        n[inputIndex] = newResizingWidth;
        return n;
      });
    },
    []
  );

  const setOutputResizingWidth = useCallback(
    (outputIndex: number) => (getNewResizingWidth: (prev: ResizingWidth) => ResizingWidth) => {
      setOutputsResizingWidths((prev: ResizingWidth[]) => {
        const newResizingWidth = getNewResizingWidth(prev[outputIndex]);
        const n = [...prev];
        n[outputIndex] = newResizingWidth;
        return n;
      });
    },
    []
  );

  const setAnnotationResizingWidth = useCallback(
    (annotationIndex: number) => (getNewResizingWidth: (prev: ResizingWidth) => ResizingWidth) => {
      setAnnotationsResizingWidths((prev: ResizingWidth[]) => {
        const newResizingWidth = getNewResizingWidth(prev[annotationIndex]);
        const n = [...prev];
        n[annotationIndex] = newResizingWidth;
        return n;
      });
    },
    []
  );

  useEffect(() => {
    updateResizingWidth(decisionTable.id!, (prev) => {
      const columns = [...inputsResizingWidths, ...outputsResizingWidths, ...annotationsResizingWidths];
      return columns.reduce(
        (acc, { value, isPivoting }) => ({ value: acc.value + value, isPivoting: acc.isPivoting || isPivoting }),
        {
          value: BEE_TABLE_ROW_INDEX_COLUMN_WIDTH + NESTED_EXPRESSION_CLEAR_MARGIN + columns.length * 2,
          isPivoting: false,
        }
      );
    });
  }, [annotationsResizingWidths, decisionTable.id, inputsResizingWidths, outputsResizingWidths, updateResizingWidth]);

  const beeTableColumns = useMemo<ReactTable.Column<ROWTYPE>[]>(() => {
    const inputColumns: ReactTable.Column<ROWTYPE>[] = (decisionTable.input ?? []).map((inputClause, inputIndex) => ({
      accessor: inputClause.id ?? generateUuid(),
      label: inputClause.name,
      id: inputClause.id,
      dataType: inputClause.dataType,
      width: inputClause.width,
      setWidth: setInputColumnWidth(inputIndex),
      resizingWidth: inputsResizingWidths[inputIndex],
      setResizingWidth: setInputResizingWidth(inputIndex),
      minWidth: DECISION_TABLE_INPUT_MIN_WIDTH,
      groupType: DecisionTableColumnType.InputClause,
      cssClasses: "decision-table--input",
      isRowIndexColumn: false,
    }));

    const outputColumns: ReactTable.Column<ROWTYPE>[] = (decisionTable.output ?? []).map(
      (outputClause, outputIndex) => ({
        accessor: outputClause.id ?? generateUuid(),
        id: outputClause.id,
        label: outputClause.name,
        dataType: outputClause.dataType,
        width: outputClause.width,
        setWidth: setOutputColumnWidth(outputIndex),
        resizingWidth: outputsResizingWidths[outputIndex],
        setResizingWidth: setOutputResizingWidth(outputIndex),
        minWidth: DECISION_TABLE_OUTPUT_MIN_WIDTH,
        groupType: DecisionTableColumnType.OutputClause,
        cssClasses: "decision-table--output",
        isRowIndexColumn: false,
      })
    );

    const annotationColumns: ReactTable.Column<ROWTYPE>[] = (decisionTable.annotations ?? []).map(
      (annotation, annotationIndex) => ({
        accessor: annotation.id ?? (generateUuid() as any),
        id: annotation.id,
        label: annotation.name,
        width: annotation.width,
        setWidth: setAnnotationColumnWidth(annotationIndex),
        resizingWidth: annotationsResizingWidths[annotationIndex],
        setResizingWidth: setAnnotationResizingWidth(annotationIndex),
        minWidth: DECISION_TABLE_ANNOTATION_MIN_WIDTH,
        inlineEditable: true,
        groupType: DecisionTableColumnType.Annotation,
        cssClasses: "decision-table--annotation",
        isRowIndexColumn: false,
        dataType: undefined as any,
      })
    );

    const inputSectionWidth = inputsResizingWidths.reduce((acc, { value }) => acc + value + 2, 0) - 2; // 2px for left/right borders of 1px
    const inputSection = {
      groupType: DecisionTableColumnType.InputClause,
      id: "Inputs",
      accessor: "Input" as any,
      label: "Input",
      dataType: undefined as any,
      cssClasses: "decision-table--input",
      isRowIndexColumn: false,
      columns: inputColumns,
      width: inputSectionWidth,
      resizingWidth: {
        value: inputSectionWidth,
        isPivoting: false,
      },
    };

    const outputSectionWidth = outputsResizingWidths.reduce((acc, { value }) => acc + value + 2, 0) - 2; // 2px for left/right borders of 1px
    const outputSection = {
      groupType: DecisionTableColumnType.OutputClause,
      id: "Outputs",
      accessor: decisionTable.isHeadless ? decisionTable.id : (decisionNodeId as any),
      label: decisionTable.name ?? DECISION_NODE_DEFAULT_NAME,
      dataType: decisionTable.dataType ?? DmnBuiltInDataType.Undefined,
      cssClasses: "decision-table--output",
      isRowIndexColumn: false,
      columns: outputColumns,
      appendColumnsOnChildren: true,
      width: outputSectionWidth,
      resizingWidth: {
        value: outputSectionWidth,
        isPivoting: false,
      },
    };

    const annotationSectionWidth = annotationsResizingWidths.reduce((acc, { value }) => acc + value + 2, 0) - 2; // 2px for left/right borders of 1px
    const annotationSection = {
      groupType: DecisionTableColumnType.Annotation,
      id: "Annotations",
      accessor: "Annotations" as any,
      label: "Annotations",
      cssClasses: "decision-table--annotation",
      columns: annotationColumns,
      inlineEditable: true,
      isRowIndexColumn: false,
      dataType: undefined as any,
      width: annotationSectionWidth,
      resizingWidth: {
        value: annotationSectionWidth,
        isPivoting: false,
      },
    };

    return [inputSection, outputSection, annotationSection];
  }, [
    annotationsResizingWidths,
    decisionNodeId,
    decisionTable.annotations,
    decisionTable.dataType,
    decisionTable.id,
    decisionTable.input,
    decisionTable.isHeadless,
    decisionTable.name,
    decisionTable.output,
    inputsResizingWidths,
    outputsResizingWidths,
    setAnnotationColumnWidth,
    setAnnotationResizingWidth,
    setInputColumnWidth,
    setInputResizingWidth,
    setOutputColumnWidth,
    setOutputResizingWidth,
  ]);

  const beeTableRows = useMemo(
    () =>
      (decisionTable.rules ?? []).map((rule) => {
        const ruleRow = [...rule.inputEntries, ...rule.outputEntries, ...rule.annotationEntries];
        const tableRow = getColumnsAtLastLevel(beeTableColumns).reduce(
          (tableRow: ROWTYPE, column, columnIndex) => {
            tableRow[column.accessor] = ruleRow[columnIndex] ?? "";
            return tableRow;
          },
          { id: rule.id }
        );
        return tableRow;
      }),
    [beeTableColumns, decisionTable.rules]
  );

  const singleOutputChildDataType = useRef(DmnBuiltInDataType.Undefined);

  const synchronizeDecisionNodeDataTypeWithSingleOutputColumnDataType = useCallback(
    (decisionNodeColumn: ReactTable.Column<ROWTYPE>) => {
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
    },
    // eslint-disable-next-line react-hooks/exhaustive-deps
    [synchronizeDecisionNodeDataTypeWithSingleOutputColumnDataType]
  );

  const onRowsUpdate = useCallback(
    ({ rows }: BeeTableRowsUpdateArgs<ROWTYPE>) => {},
    [
      /** */
    ]
  );

  const onHitPolicySelect = useCallback(
    (hitPolicy: DecisionTableExpressionDefinitionHitPolicy) => {
      setExpression((prev: DecisionTableExpressionDefinition) => {
        return {
          ...prev,
          hitPolicy,
          aggregation: HIT_POLICIES_THAT_SUPPORT_AGGREGATION.includes(hitPolicy)
            ? prev.aggregation
            : DecisionTableExpressionDefinitionBuiltInAggregation["<None>"],
        };
      });
    },
    [setExpression]
  );

  const onBuiltInAggregatorSelect = useCallback(
    (aggregation: DecisionTableExpressionDefinitionBuiltInAggregation) => {
      setExpression((prev) => {
        return {
          ...prev,
          aggregation,
        };
      });
    },
    [setExpression]
  );

  const controllerCell = useMemo(
    () => (
      <HitPolicySelector
        selectedHitPolicy={decisionTable.hitPolicy}
        selectedBuiltInAggregator={decisionTable.aggregation}
        onHitPolicySelected={onHitPolicySelect}
        onBuiltInAggregatorSelected={onBuiltInAggregatorSelect}
      />
    ),
    [decisionTable.aggregation, decisionTable.hitPolicy, onBuiltInAggregatorSelect, onHitPolicySelect]
  );

  const onRowAdded = useCallback(
    (args: { beforeIndex: number }) => {
      setExpression((prev: DecisionTableExpressionDefinition) => {
        const newRules = [...(prev.rules ?? [])];
        newRules.splice(args.beforeIndex, 0, {
          id: generateUuid(),
          inputEntries: Array.from(new Array(prev.input?.length ?? 0)).map(() => INPUT_DEFAULT_VALUE),
          outputEntries: Array.from(new Array(prev.output?.length ?? 0)).map(() => OUTPUT_DEFAULT_VALUE),
          annotationEntries: Array.from(new Array(prev.annotations?.length ?? 0)).map(() => ANNOTATION_DEFAULT_VALUE),
        });

        return {
          ...prev,
          rules: newRules,
        };
      });
    },
    [setExpression]
  );

  const onColumnAdded = useCallback(
    (args: { beforeIndex: number; groupType: DecisionTableColumnType | undefined }) => {
      const groupType = args.groupType;
      if (!groupType) {
        throw new Error("Column without groupType for Decision Table.");
      }

      // Index used to mutate individual sections locally. Inputs, Outputs, and Annotations.
      let sectionIndex: number;

      switch (groupType) {
        case DecisionTableColumnType.InputClause:
          sectionIndex = args.beforeIndex;
          setInputsResizingWidths((prev) => {
            const newResizingWidths = [...(prev ?? [])];
            newResizingWidths.splice(args.beforeIndex, 0, {
              value: DECISION_TABLE_INPUT_DEFAULT_WIDTH,
              isPivoting: false,
            });
            return newResizingWidths;
          });
          break;
        case DecisionTableColumnType.OutputClause:
          sectionIndex = args.beforeIndex - (decisionTable.input?.length ?? 0);
          setOutputsResizingWidths((prev) => {
            const newResizingWidths = [...(prev ?? [])];
            newResizingWidths.splice(sectionIndex, 0, {
              value: DECISION_TABLE_OUTPUT_DEFAULT_WIDTH,
              isPivoting: false,
            });
            return newResizingWidths;
          });
          break;
        case DecisionTableColumnType.Annotation:
          sectionIndex = args.beforeIndex - (decisionTable.input?.length ?? 0) - (decisionTable.output?.length ?? 0);
          setAnnotationsResizingWidths((prev) => {
            const newResizingWidths = [...(prev ?? [])];
            newResizingWidths.splice(sectionIndex, 0, {
              value: DECISION_TABLE_ANNOTATION_DEFAULT_WIDTH,
              isPivoting: false,
            });
            return newResizingWidths;
          });
          break;
      }

      setExpression((prev: DecisionTableExpressionDefinition) => {
        const newRules = [...(prev.rules ?? [])];

        switch (groupType) {
          case DecisionTableColumnType.InputClause:
            const newInputs = [...(prev.input ?? [])];
            newInputs.splice(sectionIndex, 0, {
              id: generateUuid(),
              name: getNextAvailablePrefixedName(prev.input?.map((c) => c.name) ?? [], "input"),
              dataType: DmnBuiltInDataType.Undefined,
              width: DECISION_TABLE_INPUT_DEFAULT_WIDTH,
            });

            newRules.forEach((r) => r.inputEntries.splice(sectionIndex, 0, INPUT_DEFAULT_VALUE));

            return {
              ...prev,
              input: newInputs,
              rules: newRules,
            };
          case DecisionTableColumnType.OutputClause:
            const newOutputs = [...(prev.output ?? [])];
            newOutputs.splice(sectionIndex, 0, {
              id: generateUuid(),
              name: getNextAvailablePrefixedName(prev.output?.map((c) => c.name) ?? [], "output"),
              dataType: DmnBuiltInDataType.Undefined,
              width: DECISION_TABLE_OUTPUT_DEFAULT_WIDTH,
            });

            newRules.forEach((r) => r.outputEntries.splice(sectionIndex, 0, OUTPUT_DEFAULT_VALUE));

            return {
              ...prev,
              output: newOutputs,
              rules: newRules,
            };
          case DecisionTableColumnType.Annotation:
            const newAnnotations = [...(prev.annotations ?? [])];
            newAnnotations.splice(sectionIndex, 0, {
              id: generateUuid(),
              name: getNextAvailablePrefixedName(prev.annotations?.map((c) => c.name) ?? [], "annotation"),
              width: DECISION_TABLE_ANNOTATION_DEFAULT_WIDTH,
            });

            newRules.forEach((r) => r.annotationEntries.splice(sectionIndex, 0, ANNOTATION_DEFAULT_VALUE));

            return {
              ...prev,
              annotations: newAnnotations,
              rules: newRules,
            };
          default:
            assertUnreachable(groupType);
        }
      });
    },
    [setExpression, decisionTable]
  );

  return (
    <div className={`decision-table-expression ${decisionTable.id}`}>
      <BeeTable
        headerLevelCount={1}
        headerVisibility={BeeTableHeaderVisibility.Full}
        editColumnLabel={getEditColumnLabel}
        operationHandlerConfig={operationHandlerConfig}
        columns={beeTableColumns}
        rows={beeTableRows}
        onColumnsUpdate={onColumnsUpdate}
        onRowsUpdate={onRowsUpdate}
        controllerCell={controllerCell}
        onRowAdded={onRowAdded}
        onColumnAdded={onColumnAdded}
      />
    </div>
  );
}
