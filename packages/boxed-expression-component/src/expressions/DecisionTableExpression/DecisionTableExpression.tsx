/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

import * as React from "react";
import { useCallback, useMemo, useRef } from "react";
import * as ReactTable from "react-table";
import {
  Action,
  BeeTableContextMenuAllowedOperationsConditions,
  BeeTableHeaderVisibility,
  BeeTableOperation,
  BeeTableOperationConfig,
  BoxedDecisionTable,
  DmnBuiltInDataType,
  ExpressionChangedArgs,
  generateUuid,
  getNextAvailablePrefixedName,
  Normalized,
} from "../../api";
import { useBoxedExpressionEditorI18n } from "../../i18n";
import { usePublishedBeeTableResizableColumns } from "../../resizing/BeeTableResizableColumnsContext";
import { useApportionedColumnWidthsIfNestedTable, useNestedTableLastColumnMinWidth } from "../../resizing/Hooks";
import { ResizerStopBehavior } from "../../resizing/ResizingWidthsContext";
import {
  BEE_TABLE_ROW_INDEX_COLUMN_WIDTH,
  DECISION_TABLE_ANNOTATION_DEFAULT_WIDTH,
  DECISION_TABLE_ANNOTATION_MIN_WIDTH,
  DECISION_TABLE_INPUT_DEFAULT_WIDTH,
  DECISION_TABLE_INPUT_MIN_WIDTH,
  DECISION_TABLE_OUTPUT_DEFAULT_WIDTH,
  DECISION_TABLE_OUTPUT_MIN_WIDTH,
} from "../../resizing/WidthConstants";
import {
  BeeTable,
  BeeTableCellUpdate,
  BeeTableColumnUpdate,
  BeeTableRef,
  getColumnsAtLastLevel,
} from "../../table/BeeTable";
import { useBoxedExpressionEditor, useBoxedExpressionEditorDispatch } from "../../BoxedExpressionEditorContext";
import { DEFAULT_EXPRESSION_VARIABLE_NAME } from "../../expressionVariable/ExpressionVariableMenu";
import { assertUnreachable } from "../ExpressionDefinitionRoot/ExpressionDefinitionLogicTypeSelector";
import { HIT_POLICIES_THAT_SUPPORT_AGGREGATION, HitPolicySelector } from "./HitPolicySelector";
import _ from "lodash";
import {
  DMN15__tBuiltinAggregator,
  DMN15__tDecisionRule,
  DMN15__tHitPolicy,
  DMN15__tInputClause,
  DMN15__tLiteralExpression,
  DMN15__tOutputClause,
  DMN15__tRuleAnnotationClause,
  DMN15__tUnaryTests,
} from "@kie-tools/dmn-marshaller/dist/schemas/dmn-1_5/ts-gen/types";
import "./DecisionTableExpression.css";
import { Unpacked } from "../../tsExt/tsExt";

type ROWTYPE = any; // FIXME: https://github.com/apache/incubator-kie-issues/issues/169

enum DecisionTableColumnType {
  InputClause = "input",
  OutputClause = "output",
  Annotation = "annotation",
}
export const DECISION_TABLE_INPUT_DEFAULT_VALUE = "-";
export const DECISION_TABLE_OUTPUT_DEFAULT_VALUE = "";
export const DECISION_TABLE_ANNOTATION_DEFAULT_VALUE = "";

function createInputEntry(): Unpacked<Normalized<DMN15__tDecisionRule["inputEntry"]>> {
  return {
    "@_id": generateUuid(),
    text: { __$$text: DECISION_TABLE_INPUT_DEFAULT_VALUE },
  };
}

function createOutputEntry(): Unpacked<Normalized<DMN15__tDecisionRule["outputEntry"]>> {
  return {
    "@_id": generateUuid(),
    text: { __$$text: DECISION_TABLE_OUTPUT_DEFAULT_VALUE },
  };
}

function createAnnotationEntry(): Unpacked<Normalized<DMN15__tDecisionRule["annotationEntry"]>> {
  return {
    text: { __$$text: DECISION_TABLE_ANNOTATION_DEFAULT_VALUE },
  };
}

const createDefaultRule = (): Normalized<DMN15__tDecisionRule> => {
  const defaultRowToAdd: Normalized<DMN15__tDecisionRule> = {
    "@_id": generateUuid(),
    inputEntry: [
      {
        "@_id": generateUuid(),
        text: { __$$text: "-" },
      },
    ],
    outputEntry: [
      {
        "@_id": generateUuid(),
        text: { __$$text: "" },
      },
    ],
    annotationEntry: [{ text: { __$$text: "// Your annotations here" } }],
  };
  return defaultRowToAdd;
};

export function DecisionTableExpression({
  isNested,
  expression: decisionTableExpression,
}: {
  expression: BoxedDecisionTable;
  isNested: boolean;
}) {
  const { i18n } = useBoxedExpressionEditorI18n();
  const { expressionHolderId, widthsById, isReadOnly } = useBoxedExpressionEditor();
  const { setExpression, setWidthsById } = useBoxedExpressionEditorDispatch();

  const id = decisionTableExpression["@_id"]!;

  const widths = useMemo(() => widthsById.get(id) ?? [], [id, widthsById]);

  const getInputIndexInTable = useCallback((localIndex: number) => {
    return 1 + localIndex;
  }, []);

  const getOutputIndexInTable = useCallback(
    (localIndex: number) => {
      return 1 + (decisionTableExpression.input?.length ?? 0) + localIndex;
    },
    [decisionTableExpression.input?.length]
  );

  const getAnnotationIndexInTable = useCallback(
    (localIndex: number) => {
      return (
        1 + (decisionTableExpression.input?.length ?? 0) + (decisionTableExpression.output?.length ?? 0) + localIndex
      );
    },
    [decisionTableExpression.input?.length, decisionTableExpression.output?.length]
  );

  const getInputWidth = useCallback(
    (inputIndex: number, widths: number[]) => {
      const index = getInputIndexInTable(inputIndex);
      return { index, width: widths[index] };
    },
    [getInputIndexInTable]
  );

  const getOutputWidth = useCallback(
    (outputIndex: number, widths: number[]) => {
      const index = getOutputIndexInTable(outputIndex);
      return { index, width: widths[index] };
    },
    [getOutputIndexInTable]
  );

  const getAnnotationWidth = useCallback(
    (annotationIndex: number, widths: number[]) => {
      const index = getAnnotationIndexInTable(annotationIndex);
      return { index, width: widths[index] };
    },
    [getAnnotationIndexInTable]
  );

  const generateOperationConfig = useCallback(
    (groupName: string) => [
      {
        group: groupName,
        items: [
          { name: i18n.columnOperations.insertLeft, type: BeeTableOperation.ColumnInsertLeft },
          { name: i18n.columnOperations.insertRight, type: BeeTableOperation.ColumnInsertRight },
          { name: i18n.insert, type: BeeTableOperation.ColumnInsertN },
          { name: i18n.columnOperations.delete, type: BeeTableOperation.ColumnDelete },
        ],
      },
      {
        group: i18n.decisionRule,
        items: [
          { name: i18n.rowOperations.insertAbove, type: BeeTableOperation.RowInsertAbove },
          { name: i18n.rowOperations.insertBelow, type: BeeTableOperation.RowInsertBelow },
          { name: i18n.insert, type: BeeTableOperation.RowInsertN },
          { name: i18n.rowOperations.delete, type: BeeTableOperation.RowDelete },
          { name: i18n.rowOperations.duplicate, type: BeeTableOperation.RowDuplicate },
        ],
      },
      {
        group: i18n.terms.selection.toUpperCase(),
        items: [
          { name: i18n.terms.copy, type: BeeTableOperation.SelectionCopy },
          { name: i18n.terms.cut, type: BeeTableOperation.SelectionCut },
          { name: i18n.terms.paste, type: BeeTableOperation.SelectionPaste },
          { name: i18n.terms.reset, type: BeeTableOperation.SelectionReset },
        ],
      },
    ],
    [i18n]
  );

  const beeTableOperationConfig = useMemo<BeeTableOperationConfig>(() => {
    const config: BeeTableOperationConfig = {};
    config[""] = generateOperationConfig(i18n.outputClause);
    config[DecisionTableColumnType.InputClause] = generateOperationConfig(i18n.inputClause);
    config[DecisionTableColumnType.OutputClause] = generateOperationConfig(i18n.outputClause);
    config[DecisionTableColumnType.Annotation] = generateOperationConfig(i18n.ruleAnnotation);
    return config;
  }, [generateOperationConfig, i18n.inputClause, i18n.outputClause, i18n.ruleAnnotation]);

  const getEditColumnLabel = useMemo(() => {
    const editColumnLabel: { [columnGroupType: string]: string } = {};
    editColumnLabel[DecisionTableColumnType.InputClause] = i18n.editClause.input;
    editColumnLabel[DecisionTableColumnType.OutputClause] = i18n.editClause.output;
    return editColumnLabel;
  }, [i18n]);

  const setInputColumnWidth = useCallback(
    (inputIndex: number) => (newWidthAction: React.SetStateAction<number | undefined>) => {
      setWidthsById(({ newMap }) => {
        const prev = newMap.get(id) ?? [];
        const inputWidth = getInputWidth(inputIndex, prev);
        const newWidth = typeof newWidthAction === "function" ? newWidthAction(inputWidth?.width) : newWidthAction;

        if (newWidth && inputWidth) {
          const minSize = inputWidth.index + 1;
          const newValues = [...prev];
          newValues.push(
            ...Array<number>(Math.max(0, minSize - newValues.length)).fill(DECISION_TABLE_INPUT_MIN_WIDTH)
          );
          newValues.splice(inputWidth.index, 1, newWidth);
          newMap.set(id, newValues);
        }
      });
    },
    [id, getInputWidth, setWidthsById]
  );

  const setOutputColumnWidth = useCallback(
    (outputIndex: number) => (newWidthAction: React.SetStateAction<number | undefined>) => {
      setWidthsById(({ newMap }) => {
        const prev = newMap.get(id) ?? [];
        const outputWidth = getOutputWidth(outputIndex, prev);
        const newWidth = typeof newWidthAction === "function" ? newWidthAction(outputWidth?.width) : newWidthAction;

        if (newWidth && outputWidth) {
          const minSize = outputWidth.index + 1;
          const newValues = [...prev];
          newValues.push(
            ...Array<number>(Math.max(0, minSize - newValues.length)).fill(DECISION_TABLE_OUTPUT_MIN_WIDTH)
          );
          newValues.splice(outputWidth.index, 1, newWidth);
          newMap.set(id, newValues);
        }
      });
    },
    [id, getOutputWidth, setWidthsById]
  );

  const setAnnotationColumnWidth = useCallback(
    (annotationIndex: number) => (newWidthAction: React.SetStateAction<number | undefined>) => {
      setWidthsById(({ newMap }) => {
        const prev = newMap.get(id) ?? [];
        const annotationWidth = getAnnotationWidth(annotationIndex, prev);
        const newWidth = typeof newWidthAction === "function" ? newWidthAction(annotationWidth?.width) : newWidthAction;

        if (newWidth && annotationWidth) {
          const minSize = annotationWidth.index + 1;
          const newValues = [...prev];
          newValues.push(
            ...Array<number>(Math.max(0, minSize - newValues.length)).fill(DECISION_TABLE_ANNOTATION_MIN_WIDTH)
          );
          newValues.splice(annotationWidth.index, 1, newWidth);
          newMap.set(id, newValues);
        }
      });
    },
    [id, getAnnotationWidth, setWidthsById]
  );

  /// //////////////////////////////////////////////////////
  /// ///////////// RESIZING WIDTHS ////////////////////////
  /// //////////////////////////////////////////////////////

  const columns = useMemo(
    () => [
      ...(decisionTableExpression.input ?? []).map((value, index) => ({
        ...value,
        minWidth: DECISION_TABLE_INPUT_MIN_WIDTH,
        width: getInputWidth(index, widths)?.width,
        label: value.inputExpression.text?.__$$text,
      })),
      ...(decisionTableExpression.output ?? []).map((value, index) => ({
        ...value,
        minWidth: DECISION_TABLE_OUTPUT_MIN_WIDTH,
        width: getOutputWidth(index, widths)?.width,
        label: value["@_name"],
      })),
      ...(decisionTableExpression.annotation ?? []).map((value, index) => ({
        ...value,
        minWidth: DECISION_TABLE_ANNOTATION_MIN_WIDTH,
        width: getAnnotationWidth(index, widths)?.width,
        label: value["@_name"],
      })),
    ],
    [
      decisionTableExpression.annotation,
      decisionTableExpression.input,
      decisionTableExpression.output,
      getAnnotationWidth,
      getInputWidth,
      getOutputWidth,
      widths,
    ]
  );

  const rules = useMemo<DMN15__tDecisionRule[]>(() => {
    return decisionTableExpression.rule ?? [];
  }, [decisionTableExpression]);

  const beeTableRef = useRef<BeeTableRef>(null);
  const { onColumnResizingWidthChange, columnResizingWidths, isPivoting } = usePublishedBeeTableResizableColumns(
    decisionTableExpression["@_id"]!,
    columns.length,
    true
  );

  const lastColumnMinWidth = useNestedTableLastColumnMinWidth(columnResizingWidths);

  useApportionedColumnWidthsIfNestedTable(
    beeTableRef,
    isPivoting,
    isNested,
    BEE_TABLE_ROW_INDEX_COLUMN_WIDTH,
    columns,
    columnResizingWidths,
    rules
  );

  /// //////////////////////////////////////////////////////

  const beeTableColumns = useMemo<ReactTable.Column<ROWTYPE>[]>(() => {
    const inputColumns: ReactTable.Column<ROWTYPE>[] = (decisionTableExpression.input ?? []).map(
      (inputClause, inputIndex) => ({
        accessor: inputClause["@_id"] ?? generateUuid(),
        label: inputClause.inputExpression.text?.__$$text ?? "",
        id: inputClause["@_id"]!,
        dataType: inputClause.inputExpression["@_typeRef"] ?? DmnBuiltInDataType.Undefined,
        width: getInputWidth(inputIndex, widths)?.width ?? DECISION_TABLE_INPUT_MIN_WIDTH,
        setWidth: setInputColumnWidth(inputIndex),
        minWidth: DECISION_TABLE_INPUT_MIN_WIDTH,
        groupType: DecisionTableColumnType.InputClause,
        isRowIndexColumn: false,
        isHeaderAFeelExpression: true,
      })
    );

    const outputColumns: ReactTable.Column<ROWTYPE>[] = (decisionTableExpression.output ?? []).map(
      (outputClause, outputIndex) => ({
        accessor: outputClause["@_id"] ?? generateUuid(),
        id: outputClause["@_id"],
        label:
          decisionTableExpression.output?.length == 1
            ? decisionTableExpression["@_label"] ?? DEFAULT_EXPRESSION_VARIABLE_NAME
            : outputClause["@_name"] ?? outputClause["@_label"] ?? DEFAULT_EXPRESSION_VARIABLE_NAME,
        dataType:
          decisionTableExpression.output?.length == 1
            ? decisionTableExpression["@_typeRef"] ?? DmnBuiltInDataType.Undefined
            : outputClause["@_typeRef"] ?? DmnBuiltInDataType.Undefined,
        width: getOutputWidth(outputIndex, widths)?.width ?? DECISION_TABLE_OUTPUT_MIN_WIDTH,
        setWidth: setOutputColumnWidth(outputIndex),
        minWidth: DECISION_TABLE_OUTPUT_MIN_WIDTH,
        groupType: DecisionTableColumnType.OutputClause,
        isRowIndexColumn: false,
      })
    );

    const outputGroup = {
      groupType: DecisionTableColumnType.OutputClause,
      id: expressionHolderId as any, // FIXME: https://github.com/apache/incubator-kie-issues/issues/169
      accessor: "decision-table-expression" as any, // FIXME: https://github.com/apache/incubator-kie-issues/issues/169
      label: decisionTableExpression["@_label"] ?? DEFAULT_EXPRESSION_VARIABLE_NAME,
      dataType: decisionTableExpression["@_typeRef"] ?? DmnBuiltInDataType.Undefined,
      isRowIndexColumn: false,
      width: undefined,
      columns: outputColumns,
    };

    const annotationColumns: ReactTable.Column<ROWTYPE>[] = (decisionTableExpression.annotation ?? []).map(
      (annotation, annotationIndex) => {
        const annotationId = generateUuid();
        return {
          accessor: annotationId,
          id: annotationId,
          label: annotation["@_name"] ?? "",
          width: getAnnotationWidth(annotationIndex, widths)?.width ?? DECISION_TABLE_ANNOTATION_MIN_WIDTH,
          setWidth: setAnnotationColumnWidth(annotationIndex),
          minWidth: DECISION_TABLE_ANNOTATION_MIN_WIDTH,
          isInlineEditable: true,
          groupType: DecisionTableColumnType.Annotation,
          isRowIndexColumn: false,
          dataType: undefined!,
        };
      }
    );

    if (outputColumns.length == 1) {
      return [...inputColumns, ...outputColumns, ...annotationColumns];
    } else {
      return [...inputColumns, outputGroup, ...annotationColumns];
    }
  }, [
    expressionHolderId,
    decisionTableExpression,
    getAnnotationWidth,
    getInputWidth,
    getOutputWidth,
    setAnnotationColumnWidth,
    setInputColumnWidth,
    setOutputColumnWidth,
    widths,
  ]);

  const beeTableRows = useMemo(() => {
    const mapRuleToRow = (rule: Normalized<DMN15__tDecisionRule>) => {
      const ruleRow = [
        ...(rule.inputEntry ?? []),
        ...(rule.outputEntry ?? new Array(decisionTableExpression.output.length)),
        ...(rule.annotationEntry ?? []),
      ];

      return getColumnsAtLastLevel(beeTableColumns).reduce(
        (tableRow: ROWTYPE, column, columnIndex) => {
          tableRow[column.accessor] = {
            id: (ruleRow[columnIndex] as DMN15__tUnaryTests & DMN15__tLiteralExpression)?.["@_id"] ?? "",
            content: ruleRow[columnIndex]?.text?.__$$text ?? "",
          };
          return tableRow;
        },
        { id: rule["@_id"] }
      );
    };
    if (!decisionTableExpression.rule || decisionTableExpression.rule.length === 0) {
      return [mapRuleToRow(createDefaultRule())];
    }
    return decisionTableExpression.rule.map(mapRuleToRow);
  }, [decisionTableExpression.rule, decisionTableExpression.output.length, beeTableColumns]);

  const onCellUpdates = useCallback(
    (cellUpdates: BeeTableCellUpdate<ROWTYPE>[]) => {
      setExpression({
        setExpressionAction: (prev: Normalized<BoxedDecisionTable>) => {
          let previousExpression: Normalized<BoxedDecisionTable> = { ...prev };
          if (!previousExpression.rule || previousExpression.rule.length === 0) {
            previousExpression.rule = [createDefaultRule()];
          }
          cellUpdates.forEach((cellUpdate) => {
            const newRules = [...(previousExpression.rule ?? [])];
            const groupType = cellUpdate.column.groupType as DecisionTableColumnType;
            switch (groupType) {
              case DecisionTableColumnType.InputClause:
                const newInputEntries = [...(newRules[cellUpdate.rowIndex].inputEntry ?? [])];
                newInputEntries[cellUpdate.columnIndex] = {
                  ...newInputEntries[cellUpdate.columnIndex],
                  text: {
                    __$$text: cellUpdate.value,
                  },
                };
                newRules[cellUpdate.rowIndex] = {
                  ...newRules[cellUpdate.rowIndex],
                  inputEntry: newInputEntries,
                };
                break;
              case DecisionTableColumnType.OutputClause:
                const newOutputEntries = [...newRules[cellUpdate.rowIndex].outputEntry];
                const entryIndex = cellUpdate.columnIndex - (prev.input?.length ?? 0);
                newOutputEntries[entryIndex] = {
                  ...newOutputEntries[entryIndex],
                  text: {
                    __$$text: cellUpdate.value,
                  },
                };
                newRules[cellUpdate.rowIndex] = {
                  ...newRules[cellUpdate.rowIndex],
                  outputEntry: newOutputEntries,
                };
                break;
              case DecisionTableColumnType.Annotation:
                const newAnnotationEntries = [...(newRules[cellUpdate.rowIndex].annotationEntry ?? [])];
                const annotationIndex = cellUpdate.columnIndex - (prev.input?.length ?? 0) - (prev.output?.length ?? 0);
                newAnnotationEntries[annotationIndex] = {
                  ...newAnnotationEntries[annotationIndex],
                  text: { __$$text: cellUpdate.value },
                };
                newRules[cellUpdate.rowIndex] = {
                  ...newRules[cellUpdate.rowIndex],
                  annotationEntry: newAnnotationEntries,
                };
                break;
              default:
                assertUnreachable(groupType);
            }

            previousExpression = {
              ...previousExpression,
              rule: newRules,
            };
          });

          return previousExpression;
        },
        expressionChangedArgs: { action: Action.DecisionTableCellsUpdated },
      });
    },
    [setExpression]
  );

  const getExpressionChangedArgsFromColumnUpdates = useCallback(
    (columnUpdates: BeeTableColumnUpdate<ROWTYPE>[]) => {
      const updateNodeNameOrType = columnUpdates.filter(
        (columnUpdate) =>
          columnUpdate.column.depth === 0 &&
          columnUpdate.column.groupType === DecisionTableColumnType.OutputClause &&
          (decisionTableExpression["@_label"] !== columnUpdate.name ||
            decisionTableExpression["@_typeRef"] !== columnUpdate.typeRef)
      );

      if (updateNodeNameOrType.length > 1) {
        throw new Error("Unexpected multiple name and/or type changed simultaneously in a Decision Table.");
      }

      // This is the Output column aggregator column, which represents the entire expression name and typeRef
      if (updateNodeNameOrType.length === 1) {
        const expressionChangedArgs: ExpressionChangedArgs = {
          action: Action.VariableChanged,
          variableUuid: isNested ? decisionTableExpression["@_id"]! : expressionHolderId,
          typeChange:
            decisionTableExpression["@_typeRef"] !== updateNodeNameOrType[0].typeRef
              ? {
                  from: decisionTableExpression["@_typeRef"],
                  to: updateNodeNameOrType[0].typeRef,
                }
              : undefined,
          nameChange:
            decisionTableExpression["@_label"] !== updateNodeNameOrType[0].name
              ? {
                  from: decisionTableExpression["@_label"],
                  to: updateNodeNameOrType[0].name,
                }
              : undefined,
        };

        return expressionChangedArgs;
      } else {
        //  Changes in other columns does not reflect in changes in variables
        // Do not inline this variable for type safety. See https://github.com/microsoft/TypeScript/issues/241
        const expressionChangedArgs: ExpressionChangedArgs = { action: Action.ColumnChanged };
        return expressionChangedArgs;
      }
    },
    [decisionTableExpression, expressionHolderId, isNested]
  );

  const onColumnUpdates = useCallback(
    (columnUpdates: BeeTableColumnUpdate<ROWTYPE>[]) => {
      const expressionChangedArgs = getExpressionChangedArgsFromColumnUpdates(columnUpdates);

      setExpression({
        setExpressionAction: (prev: Normalized<BoxedDecisionTable>) => {
          // Do not inline this variable for type safety. See https://github.com/microsoft/TypeScript/issues/241
          const ret: Normalized<BoxedDecisionTable> = { ...prev };
          for (const columnUpdate of columnUpdates) {
            // This is the Output column aggregator column, which represents the entire expression name and typeRef
            if (
              columnUpdate.column.depth === 0 &&
              columnUpdate.column.groupType === DecisionTableColumnType.OutputClause
            ) {
              ret["@_label"] = columnUpdate.name;
              ret["@_typeRef"] = columnUpdate.typeRef;

              // Single output column is merged with the aggregator column and should have the same typeRef
              if (ret.output?.length === 1) {
                const newOutputs = [...(ret.output ?? [])];
                newOutputs[0] = {
                  ...newOutputs[0],
                  "@_typeRef": columnUpdate.typeRef,
                  "@_name": columnUpdate.name,
                };
              }
              continue;
            }

            // These are the other columns.
            const groupType = columnUpdate.column.groupType as DecisionTableColumnType;
            switch (groupType) {
              case DecisionTableColumnType.InputClause:
                const newInputs = [...(ret.input ?? [])];
                newInputs[columnUpdate.columnIndex] = {
                  ...newInputs[columnUpdate.columnIndex],
                  inputExpression: {
                    ...newInputs[columnUpdate.columnIndex].inputExpression,
                    "@_typeRef": columnUpdate.typeRef,
                    text: { __$$text: columnUpdate.name },
                  },
                };
                ret.input = newInputs;
                break;
              case DecisionTableColumnType.OutputClause:
                const newOutputs = [...(ret.output ?? [])];
                const outputIndex = columnUpdate.columnIndex - (prev.input?.length ?? 0);
                newOutputs[outputIndex] = {
                  ...newOutputs[outputIndex],
                  "@_typeRef": columnUpdate.typeRef,
                  "@_name": columnUpdate.name,
                };

                ret.output = newOutputs;
                break;
              case DecisionTableColumnType.Annotation:
                const newAnnotations = [...(ret.annotation ?? [])];
                const annotationIndex =
                  columnUpdate.columnIndex - (prev.input?.length ?? 0) - (prev.output?.length ?? 0);
                newAnnotations[annotationIndex] = {
                  ...newAnnotations[annotationIndex],
                  "@_name": columnUpdate.name,
                };
                ret.annotation = newAnnotations;
                break;
              default:
                assertUnreachable(groupType);
            }
          }

          return ret;
        },
        expressionChangedArgs,
      });
    },
    [getExpressionChangedArgsFromColumnUpdates, setExpression]
  );

  const onHitPolicySelect = useCallback(
    (hitPolicy: string) => {
      setExpression({
        setExpressionAction: (prev: Normalized<BoxedDecisionTable>) => {
          // Do not inline this variable for type safety. See https://github.com/microsoft/TypeScript/issues/241
          const ret: Normalized<BoxedDecisionTable> = {
            ...prev,
            "@_hitPolicy": hitPolicy as DMN15__tHitPolicy,
            "@_aggregation": HIT_POLICIES_THAT_SUPPORT_AGGREGATION.includes(hitPolicy)
              ? (prev as BoxedDecisionTable)["@_aggregation"]
              : undefined!,
          };

          return ret;
        },
        expressionChangedArgs: { action: Action.DecisionTableHitPolicyChanged },
      });
    },
    [setExpression]
  );

  const getAggregation = useCallback((aggKey: string) => {
    switch (aggKey) {
      case "<":
        return "MIN";
      case ">":
        return "MAX";
      case "#":
        return "COUNT";
      case "+":
        return "SUM";
      case "?":
        return undefined;
    }
  }, []);

  const getAggregationKey = useCallback((aggKey: string | undefined) => {
    if (!aggKey) {
      return "?";
    }
    switch (aggKey) {
      case "MIN":
        return "<";
      case "MAX":
        return ">";
      case "COUNT":
        return "#";
      case "SUM":
        return "+";
      default:
        return "?";
    }
  }, []);

  const onBuiltInAggregatorSelect = useCallback(
    (aggregation: DMN15__tBuiltinAggregator) => {
      setExpression({
        setExpressionAction: (prev: Normalized<BoxedDecisionTable>) => {
          // Do not inline this variable for type safety. See https://github.com/microsoft/TypeScript/issues/241
          const ret: Normalized<BoxedDecisionTable> = {
            ...prev,
            "@_aggregation": getAggregation(aggregation),
          };

          return ret;
        },
        expressionChangedArgs: { action: Action.DecisionTableBuiltInAggregatorChanged },
      });
    },
    [getAggregation, setExpression]
  );

  const controllerCell = useMemo(
    () => (
      <HitPolicySelector
        selectedHitPolicy={decisionTableExpression["@_hitPolicy"] ?? "UNIQUE"}
        selectedBuiltInAggregator={getAggregationKey(decisionTableExpression["@_aggregation"])}
        onHitPolicySelected={onHitPolicySelect}
        onBuiltInAggregatorSelected={onBuiltInAggregatorSelect}
        isReadOnly={isReadOnly ?? false}
      />
    ),
    [decisionTableExpression, getAggregationKey, isReadOnly, onBuiltInAggregatorSelect, onHitPolicySelect]
  );

  const onRowAdded = useCallback(
    (args: { beforeIndex: number; rowsCount: number }) => {
      setExpression({
        setExpressionAction: (prev: Normalized<BoxedDecisionTable>) => {
          let newRules = [...(prev.rule ?? [])];
          if (newRules.length === 0) {
            newRules = [createDefaultRule()];
          }

          const newItems: Normalized<DMN15__tDecisionRule>[] = [];
          for (let i = 0; i < args.rowsCount; i++) {
            newItems.push({
              "@_id": generateUuid(),
              inputEntry: Array.from(new Array(prev.input?.length ?? 0)).map(() => {
                return createInputEntry();
              }),
              outputEntry: Array.from(new Array(prev.output?.length ?? 0)).map(() => {
                return createOutputEntry();
              }),
              annotationEntry: Array.from(new Array(prev.annotation?.length ?? 0)).map(() => {
                return { text: { __$$text: DECISION_TABLE_ANNOTATION_DEFAULT_VALUE } };
              }),
            });
          }

          for (const newEntry of newItems) {
            newRules.splice(args.beforeIndex, 0, newEntry);
          }

          // Do not inline this variable for type safety. See https://github.com/microsoft/TypeScript/issues/241
          const ret: Normalized<BoxedDecisionTable> = {
            ...prev,
            rule: newRules,
          };

          return ret;
        },
        expressionChangedArgs: { action: Action.RowsAdded, rowIndex: args.beforeIndex, rowsCount: args.rowsCount },
      });
    },
    [setExpression]
  );

  const getLocalIndexInsideGroupType = useCallback(
    (columnIndex: number, groupType: DecisionTableColumnType) => {
      switch (groupType) {
        case DecisionTableColumnType.InputClause:
          return columnIndex;
        case DecisionTableColumnType.OutputClause:
          return columnIndex - (decisionTableExpression.input?.length ?? 0);
        case DecisionTableColumnType.Annotation:
          return (
            columnIndex - (decisionTableExpression.input?.length ?? 0) - (decisionTableExpression.output?.length ?? 0)
          );
        default:
          assertUnreachable(groupType);
      }
    },
    [decisionTableExpression.input?.length, decisionTableExpression.output?.length]
  );

  const onColumnAdded = useCallback(
    (args: { beforeIndex: number; groupType: string | undefined; columnsCount: number }) => {
      const groupType = args.groupType as DecisionTableColumnType;
      if (!groupType) {
        throw new Error("Column without groupType for Decision table.");
      }

      const localIndexInsideGroup = getLocalIndexInsideGroupType(args.beforeIndex, groupType);

      setExpression({
        setExpressionAction: (prev: Normalized<BoxedDecisionTable>) => {
          const nextRows = [...(prev.rule ?? [])];

          switch (groupType) {
            case DecisionTableColumnType.InputClause:
              const inputColumnsToAdd: Normalized<DMN15__tInputClause>[] = [];

              const currentInputNames = prev.input?.map((c) => c.inputExpression.text?.__$$text ?? "") ?? [];
              for (let i = 0; i < args.columnsCount; i++) {
                const newName = getNextAvailablePrefixedName(currentInputNames, "Input");
                currentInputNames.push(newName);

                inputColumnsToAdd.push({
                  "@_id": generateUuid(),
                  inputExpression: {
                    "@_id": generateUuid(),
                    "@_typeRef": undefined,
                    text: { __$$text: newName },
                  },
                });
              }

              const nextInputColumns = [...(prev.input ?? [])];
              for (/* Add new columns */ let i = 0; i < inputColumnsToAdd.length; i++) {
                nextInputColumns.splice(localIndexInsideGroup + i, 0, inputColumnsToAdd[i]);
              }

              for (/* Add new cells to each row */ let i = 0; i < nextRows.length; i++) {
                const row = nextRows[i];
                const nextInputEntries = [...(row.inputEntry ?? [])];

                for (/* Add new cells to row */ let j = 0; j < args.columnsCount; j++) {
                  nextInputEntries.splice(localIndexInsideGroup + j, 0, createInputEntry());
                }
                nextRows[i] = { ...row, inputEntry: nextInputEntries };
              }

              // Do not inline this variable for type safety. See https://github.com/microsoft/TypeScript/issues/241
              const retInput: Normalized<BoxedDecisionTable> = {
                ...prev,
                input: nextInputColumns,
                rule: nextRows,
              };

              return retInput;

            case DecisionTableColumnType.OutputClause:
              const outputColumnsToAdd: Normalized<DMN15__tOutputClause>[] = [];

              const currentOutputColumnNames = prev.output?.map((c) => c["@_name"] ?? "") ?? [];
              for (let i = 0; i < args.columnsCount; i++) {
                const name = getNextAvailablePrefixedName(currentOutputColumnNames, "Output");
                currentOutputColumnNames.push(name);
                outputColumnsToAdd.push({
                  "@_id": generateUuid(),
                  "@_name": name,
                  "@_typeRef": undefined,
                });
              }

              const nextOutputColumns = [
                ...(prev.output ?? []).map((outputColumn, index) => {
                  const outputCopy = { ...outputColumn };
                  if (outputCopy["@_name"] === undefined) {
                    outputCopy["@_name"] = `Output-${index + 1}`;
                  }
                  return outputCopy;
                }),
              ];
              for (/* Add new columns */ let i = 0; i < outputColumnsToAdd.length; i++) {
                nextOutputColumns.splice(localIndexInsideGroup + i, 0, outputColumnsToAdd[i]);
              }

              for (/* Add new cells to each row */ let i = 0; i < nextRows.length; i++) {
                const row = nextRows[i];
                const nextOutputEntries = [...(row.outputEntry ?? [])];

                for (/* Add new cells to row */ let j = 0; j < args.columnsCount; j++) {
                  nextOutputEntries.splice(localIndexInsideGroup + j, 0, createOutputEntry());
                }

                nextRows[i] = { ...row, outputEntry: nextOutputEntries };
              }

              // Do not inline this variable for type safety. See https://github.com/microsoft/TypeScript/issues/241
              const retOutput: Normalized<BoxedDecisionTable> = {
                ...prev,
                output: nextOutputColumns,
                rule: nextRows,
              };

              return retOutput;

            case DecisionTableColumnType.Annotation:
              const annotationColumnsToAdd: Normalized<DMN15__tRuleAnnotationClause>[] = [];

              const currentAnnotationColumnNames = prev.annotation?.map((c) => c["@_name"] ?? "") ?? [];
              for (let i = 0; i < args.columnsCount; i++) {
                const newName = getNextAvailablePrefixedName(currentAnnotationColumnNames, "Annotations");
                currentAnnotationColumnNames.push(newName);
                annotationColumnsToAdd.push({ "@_name": newName });
              }

              const nextAnnotationColumns = [...(prev.annotation ?? [])];
              for (/* Add new columns */ let i = 0; i < annotationColumnsToAdd.length; i++) {
                nextAnnotationColumns.splice(localIndexInsideGroup + i, 0, annotationColumnsToAdd[i]);
              }

              for (/* Add new cells to each row */ let i = 0; i < nextRows.length; i++) {
                const row = nextRows[i];
                const nextAnnotationEntries = [...(row.annotationEntry ?? [])];

                for (/* Add new cells to row */ let j = 0; j < args.columnsCount; j++) {
                  nextAnnotationEntries.splice(localIndexInsideGroup + j, 0, createAnnotationEntry());
                }
                nextRows[i] = { ...row, annotationEntry: nextAnnotationEntries };
              }

              // Do not inline this variable for type safety. See https://github.com/microsoft/TypeScript/issues/241
              const retAnnotation: Normalized<BoxedDecisionTable> = {
                ...prev,
                annotation: nextAnnotationColumns,
                rule: nextRows,
              };

              return retAnnotation;

            default:
              assertUnreachable(groupType);
          }
        },
        expressionChangedArgs: {
          action: Action.ColumnAdded,
          columnIndex: args.beforeIndex,
          columnCount: args.columnsCount,
        },
      });

      setWidthsById(({ newMap }) => {
        const prev = newMap.get(id) ?? [];
        const defaultWidth =
          args.groupType === DecisionTableColumnType.InputClause
            ? DECISION_TABLE_INPUT_DEFAULT_WIDTH
            : args.groupType === DecisionTableColumnType.OutputClause
              ? DECISION_TABLE_OUTPUT_DEFAULT_WIDTH
              : DECISION_TABLE_ANNOTATION_DEFAULT_WIDTH;

        const nextValues = [...prev];
        const minValuesLength = args.beforeIndex + args.columnsCount;
        nextValues.push(...Array(Math.max(0, minValuesLength - nextValues.length)));
        for (let i = 0; i < args.columnsCount; i++) {
          const widthIndex = args.beforeIndex + i + 1; // + 1 to account for the rowIndex column.
          nextValues.splice(widthIndex, 0, defaultWidth);
        }
        newMap.set(id, nextValues);
      });
    },
    [getLocalIndexInsideGroupType, setExpression, setWidthsById, id]
  );

  const onColumnDeleted = useCallback(
    (args: { columnIndex: number; groupType: DecisionTableColumnType }) => {
      setExpression({
        setExpressionAction: (prev: Normalized<BoxedDecisionTable>) => {
          const groupType = args.groupType;
          if (!groupType) {
            throw new Error("Column without groupType for Decision table.");
          }

          const localIndexInsideGroup = getLocalIndexInsideGroupType(args.columnIndex, groupType);

          switch (groupType) {
            case DecisionTableColumnType.InputClause:
              const newInputs = [...(prev.input ?? [])];
              newInputs.splice(localIndexInsideGroup, 1);

              // Do not inline this variable for type safety. See https://github.com/microsoft/TypeScript/issues/241
              const retInput: Normalized<BoxedDecisionTable> = {
                ...prev,
                input: newInputs,
                rule: [...(prev.rule ?? [])].map((rule) => {
                  const newInputEntry = [...(rule.inputEntry ?? [])];
                  newInputEntry.splice(localIndexInsideGroup, 1);
                  return {
                    ...rule,
                    inputEntry: newInputEntry,
                  };
                }),
              };
              return retInput;
            case DecisionTableColumnType.OutputClause:
              const newOutputs = [...(prev.output ?? [])];
              newOutputs.splice(localIndexInsideGroup, 1);
              //Output name shouldn't be displayed when there is single output column(kie-issues#1466)
              const updatedOutputForSingleOutputColumns = [
                ...(newOutputs ?? []).map((outputColumn) => {
                  const outputCopy = { ...outputColumn };
                  if (newOutputs.length === 1) {
                    outputCopy["@_name"] = undefined;
                    outputCopy["@_typeRef"] = undefined;
                  }
                  return outputCopy;
                }),
              ];
              // Do not inline this variable for type safety. See https://github.com/microsoft/TypeScript/issues/241
              const retOutput: Normalized<BoxedDecisionTable> = {
                ...prev,
                output: updatedOutputForSingleOutputColumns,
                rule: [...(prev.rule ?? [])].map((rule) => {
                  const newOutputEntry = [...rule.outputEntry];
                  newOutputEntry.splice(localIndexInsideGroup, 1);
                  return {
                    ...rule,
                    outputEntry: newOutputEntry,
                  };
                }),
              };

              return retOutput;
            case DecisionTableColumnType.Annotation:
              const newAnnotations = [...(prev.annotation ?? [])];
              newAnnotations.splice(localIndexInsideGroup, 1);

              // Do not inline this variable for type safety. See https://github.com/microsoft/TypeScript/issues/241
              const retAnnotation: Normalized<BoxedDecisionTable> = {
                ...prev,
                annotation: newAnnotations,
                rule: [...(prev.rule ?? [])].map((rule) => {
                  const newAnnotationEntry = [...(rule.annotationEntry ?? [])];
                  newAnnotationEntry.splice(localIndexInsideGroup, 1);
                  return {
                    ...rule,
                    annotationEntry: newAnnotationEntry,
                  };
                }),
              };
              return retAnnotation;
            default:
              assertUnreachable(groupType);
          }
        },
        expressionChangedArgs: { action: Action.ColumnRemoved, columnIndex: args.columnIndex },
      });

      setWidthsById(({ newMap }) => {
        const prev = newMap.get(id) ?? [];
        const newValues = [...prev];
        newValues.splice(args.columnIndex + 1, 1); // + 1 to account for the rowIndex column
        newMap.set(id, newValues);
      });
    },
    [getLocalIndexInsideGroupType, id, setExpression, setWidthsById]
  );

  const onRowDeleted = useCallback(
    (args: { rowIndex: number }) => {
      setExpression({
        setExpressionAction: (prev: Normalized<BoxedDecisionTable>) => {
          const newRules = [...(prev.rule ?? [])];
          newRules.splice(args.rowIndex, 1);

          // Do not inline this variable for type safety. See https://github.com/microsoft/TypeScript/issues/241
          const ret: Normalized<BoxedDecisionTable> = {
            ...prev,
            rule: newRules,
          };
          return ret;
        },
        expressionChangedArgs: { action: Action.RowRemoved, rowIndex: args.rowIndex },
      });
    },
    [setExpression]
  );

  const onRowDuplicated = useCallback(
    (args: { rowIndex: number }) => {
      setExpression({
        setExpressionAction: (prev: Normalized<BoxedDecisionTable>) => {
          const duplicatedRule = {
            "@_id": generateUuid(),
            inputEntry: prev.rule![args.rowIndex].inputEntry?.map((input) => ({
              ...input,
              "@_id": generateUuid(),
            })),
            outputEntry: prev.rule![args.rowIndex].outputEntry.map((output) => ({
              ...output,
              "@_id": generateUuid(),
            })),
            annotationEntry: prev.rule![args.rowIndex].annotationEntry?.slice(),
          };

          const newRules = [...(prev.rule ?? [])];
          newRules.splice(args.rowIndex, 0, duplicatedRule);

          // Do not inline this variable for type safety. See https://github.com/microsoft/TypeScript/issues/241
          const ret: Normalized<BoxedDecisionTable> = {
            ...prev,
            rule: newRules,
          };

          return ret;
        },
        expressionChangedArgs: { action: Action.RowDuplicated, rowIndex: args.rowIndex },
      });
    },
    [setExpression]
  );

  const beeTableHeaderVisibility = useMemo(() => {
    return isNested ? BeeTableHeaderVisibility.LastLevel : BeeTableHeaderVisibility.AllLevels;
  }, [isNested]);

  const allowedOperations = useCallback(
    (conditions: BeeTableContextMenuAllowedOperationsConditions) => {
      if (!conditions.selection.selectionStart || !conditions.selection.selectionEnd) {
        return [];
      }

      const columnIndex = conditions.selection.selectionStart.columnIndex;

      const atLeastTwoColumnsOfTheSameGroupType = conditions.column?.groupType
        ? _.groupBy(conditions.columns, (column) => column?.groupType)[conditions.column.groupType].length > 1
        : true;

      const columnCanBeDeleted =
        columnIndex > 0 &&
        atLeastTwoColumnsOfTheSameGroupType &&
        (conditions.columns?.length ?? 0) > 2 && // That's a regular column and the rowIndex column
        (conditions.column?.columns?.length ?? 0) <= 0;

      const columnOperations =
        columnIndex === 0 // This is the rowIndex column
          ? []
          : [
              BeeTableOperation.ColumnInsertLeft,
              BeeTableOperation.ColumnInsertRight,
              BeeTableOperation.ColumnInsertN,
              ...(columnCanBeDeleted ? [BeeTableOperation.ColumnDelete] : []),
            ];

      return [
        ...columnOperations,
        BeeTableOperation.SelectionCopy,
        ...(conditions.selection.selectionStart.rowIndex >= 0 && columnIndex > 0
          ? [BeeTableOperation.SelectionCut, BeeTableOperation.SelectionPaste, BeeTableOperation.SelectionReset]
          : []),
        ...(conditions.selection.selectionStart.rowIndex >= 0
          ? [
              BeeTableOperation.RowInsertAbove,
              BeeTableOperation.RowInsertBelow,
              BeeTableOperation.RowInsertN,
              ...(beeTableRows.length > 1 ? [BeeTableOperation.RowDelete] : []),
              BeeTableOperation.RowReset,
              BeeTableOperation.RowDuplicate,
            ]
          : []),
      ];
    },
    [beeTableRows.length]
  );

  const supportsEvaluationHitsCount = useCallback((row: ReactTable.Row<ROWTYPE>) => {
    return true;
  }, []);

  return (
    <div className={`decision-table-expression ${decisionTableExpression["@_id"]}`}>
      <BeeTable<ROWTYPE>
        isReadOnly={isReadOnly}
        isEditableHeader={!isReadOnly}
        resizerStopBehavior={
          isPivoting ? ResizerStopBehavior.SET_WIDTH_ALWAYS : ResizerStopBehavior.SET_WIDTH_WHEN_SMALLER
        }
        forwardRef={beeTableRef}
        headerLevelCountForAppendingRowIndexColumn={1}
        headerVisibility={beeTableHeaderVisibility}
        editColumnLabel={getEditColumnLabel}
        operationConfig={beeTableOperationConfig}
        allowedOperations={allowedOperations}
        columns={beeTableColumns}
        rows={beeTableRows}
        onColumnUpdates={onColumnUpdates}
        onCellUpdates={onCellUpdates}
        controllerCell={controllerCell}
        onRowAdded={onRowAdded}
        onRowDeleted={onRowDeleted}
        onRowDuplicated={onRowDuplicated}
        onColumnAdded={onColumnAdded}
        onColumnDeleted={onColumnDeleted}
        onColumnResizingWidthChange={onColumnResizingWidthChange}
        shouldRenderRowIndexColumn={true}
        shouldShowRowsInlineControls={true}
        shouldShowColumnsInlineControls={true}
        supportsEvaluationHitsCount={supportsEvaluationHitsCount}
        // lastColumnMinWidth={lastColumnMinWidth} // FIXME: Check if this is a good strategy or not when doing https://github.com/apache/incubator-kie-issues/issues/181
      />
    </div>
  );
}
