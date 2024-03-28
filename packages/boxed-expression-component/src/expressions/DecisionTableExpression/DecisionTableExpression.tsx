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
  BeeTableContextMenuAllowedOperationsConditions,
  BeeTableHeaderVisibility,
  BeeTableOperation,
  BeeTableOperationConfig,
  BoxedDecisionTable,
  DmnBuiltInDataType,
  generateUuid,
  getNextAvailablePrefixedName,
  InsertRowColumnsDirection,
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
import {
  useBoxedExpressionEditor,
  useBoxedExpressionEditorDispatch,
} from "../BoxedExpressionEditor/BoxedExpressionEditorContext";
import { DEFAULT_EXPRESSION_NAME } from "../ExpressionDefinitionHeaderMenu";
import { assertUnreachable } from "../ExpressionDefinitionRoot/ExpressionDefinitionLogicTypeSelector";
import { HIT_POLICIES_THAT_SUPPORT_AGGREGATION, HitPolicySelector } from "./HitPolicySelector";
import _ from "lodash";
import {
  DMN15__tBuiltinAggregator,
  DMN15__tDecisionRule,
  DMN15__tHitPolicy,
} from "@kie-tools/dmn-marshaller/dist/schemas/dmn-1_5/ts-gen/types";
import "./DecisionTableExpression.css";

type ROWTYPE = any; // FIXME: https://github.com/kiegroup/kie-issues/issues/169

enum DecisionTableColumnType {
  InputClause = "input",
  OutputClause = "output",
  Annotation = "annotation",
}

export const DECISION_TABLE_INPUT_DEFAULT_VALUE = "-";
export const DECISION_TABLE_OUTPUT_DEFAULT_VALUE = "";
export const DECISION_TABLE_ANNOTATION_DEFAULT_VALUE = "";

function createInputEntry() {
  return {
    "@_id": generateUuid(),
    text: { __$$text: DECISION_TABLE_INPUT_DEFAULT_VALUE },
  };
}

function createOutputEntry() {
  return {
    "@_id": generateUuid(),
    text: { __$$text: DECISION_TABLE_OUTPUT_DEFAULT_VALUE },
  };
}

export function DecisionTableExpression(
  decisionTableExpression: BoxedDecisionTable & {
    isNested: boolean;
    parentElementId: string;
  }
) {
  const { i18n } = useBoxedExpressionEditorI18n();
  const { expressionHolderId, widthsById, variables } = useBoxedExpressionEditor();
  const { setExpression, setWidthById } = useBoxedExpressionEditorDispatch();

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
      setWidthById(id, (prev) => {
        const inputWidth = getInputWidth(inputIndex, prev);
        const newWidth = typeof newWidthAction === "function" ? newWidthAction(inputWidth?.width) : newWidthAction;

        if (newWidth && inputWidth) {
          const minSize = inputWidth.index + 1;
          const newValues = [...prev];
          newValues.push(...Array(Math.max(0, minSize - newValues.length)));
          newValues.splice(inputWidth.index, 1, newWidth);
          return newValues;
        }
        return prev;
      });
    },
    [id, getInputWidth, setWidthById]
  );

  const setOutputColumnWidth = useCallback(
    (outputIndex: number) => (newWidthAction: React.SetStateAction<number | undefined>) => {
      setWidthById(id, (prev) => {
        const outputWidth = getOutputWidth(outputIndex, prev);
        const newWidth = typeof newWidthAction === "function" ? newWidthAction(outputWidth?.width) : newWidthAction;

        if (newWidth && outputWidth) {
          const minSize = outputWidth.index + 1;
          const newValues = [...prev];
          newValues.push(...Array(Math.max(0, minSize - newValues.length)));
          newValues.splice(outputWidth.index, 1, newWidth);
          return newValues;
        }

        return prev;
      });
    },
    [id, getOutputWidth, setWidthById]
  );

  const setAnnotationColumnWidth = useCallback(
    (annotationIndex: number) => (newWidthAction: React.SetStateAction<number | undefined>) => {
      setWidthById(id, (prev) => {
        const annotationWidth = getAnnotationWidth(annotationIndex, prev);
        const newWidth = typeof newWidthAction === "function" ? newWidthAction(annotationWidth?.width) : newWidthAction;

        if (newWidth && annotationWidth) {
          const minSize = annotationWidth.index + 1;
          const newValues = [...prev];
          newValues.push(...Array(Math.max(0, minSize - newValues.length)));
          newValues.splice(annotationWidth.index, 1, newWidth);
          return newValues;
        }

        return prev;
      });
    },
    [id, getAnnotationWidth, setWidthById]
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
    decisionTableExpression.isNested,
    BEE_TABLE_ROW_INDEX_COLUMN_WIDTH,
    columns,
    columnResizingWidths,
    decisionTableExpression.rule ?? []
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
        cssClasses: "decision-table--input",
        isRowIndexColumn: false,
      })
    );

    const outputColumns: ReactTable.Column<ROWTYPE>[] = (decisionTableExpression.output ?? []).map(
      (outputClause, outputIndex) => ({
        accessor: outputClause["@_id"] ?? generateUuid(),
        id: outputClause["@_id"],
        label:
          decisionTableExpression.output?.length == 1
            ? decisionTableExpression["@_label"] ?? DEFAULT_EXPRESSION_NAME
            : outputClause["@_name"] ?? outputClause["@_label"] ?? DEFAULT_EXPRESSION_NAME,
        dataType:
          decisionTableExpression.output?.length == 1
            ? decisionTableExpression["@_typeRef"] ?? DmnBuiltInDataType.Undefined
            : outputClause["@_typeRef"] ?? DmnBuiltInDataType.Undefined,
        width: getOutputWidth(outputIndex, widths)?.width ?? DECISION_TABLE_OUTPUT_MIN_WIDTH,
        setWidth: setOutputColumnWidth(outputIndex),
        minWidth: DECISION_TABLE_OUTPUT_MIN_WIDTH,
        groupType: DecisionTableColumnType.OutputClause,
        cssClasses: "decision-table--output",
        isRowIndexColumn: false,
      })
    );

    const outputSection = {
      groupType: DecisionTableColumnType.OutputClause,
      id: expressionHolderId as any, // FIXME: https://github.com/kiegroup/kie-issues/issues/169,
      accessor: "decision-table-expression" as any, // FIXME: https://github.com/kiegroup/kie-issues/issues/169
      label: decisionTableExpression["@_label"] ?? DEFAULT_EXPRESSION_NAME,
      dataType: decisionTableExpression["@_typeRef"] ?? DmnBuiltInDataType.Undefined,
      cssClasses: "decision-table--output",
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
          cssClasses: "decision-table--annotation",
          isRowIndexColumn: false,
          dataType: undefined!,
        };
      }
    );

    if (outputColumns.length == 1) {
      return [...inputColumns, ...outputColumns, ...annotationColumns];
    } else {
      return [...inputColumns, outputSection, ...annotationColumns];
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

  const beeTableRows = useMemo(
    () =>
      (decisionTableExpression.rule ?? []).map((rule) => {
        const ruleRow = [
          ...(rule.inputEntry ?? []),
          ...(rule.outputEntry ?? new Array(decisionTableExpression.output.length)),
          ...(rule.annotationEntry ?? []),
        ];

        return getColumnsAtLastLevel(beeTableColumns).reduce(
          (tableRow: ROWTYPE, column, columnIndex) => {
            tableRow[column.accessor] = ruleRow[columnIndex]?.text?.__$$text ?? "";
            return tableRow;
          },
          { id: rule["@_id"] }
        );
      }),
    [beeTableColumns, decisionTableExpression.output.length, decisionTableExpression.rule]
  );

  const onCellUpdates = useCallback(
    (cellUpdates: BeeTableCellUpdate<ROWTYPE>[]) => {
      setExpression((prev: BoxedDecisionTable) => {
        let previousExpression = { ...prev };

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
      });
    },
    [setExpression]
  );

  const onColumnUpdates = useCallback(
    (columnUpdates: BeeTableColumnUpdate<ROWTYPE>[]) => {
      setExpression((prev: BoxedDecisionTable) => {
        const n = { ...prev };
        for (const columnUpdate of columnUpdates) {
          // This is the Output column aggregator column, which represents the entire expression name and typeRef
          if (
            columnUpdate.column.depth === 0 &&
            columnUpdate.column.groupType === DecisionTableColumnType.OutputClause
          ) {
            n["@_label"] = columnUpdate.name;
            n["@_typeRef"] = columnUpdate.typeRef;
            // Single output column is merged with the aggregator column and should have the same typeRef
            if (n.output?.length === 1) {
              n.output[0] = {
                ...n.output[0],
                "@_typeRef": columnUpdate.typeRef,
              };
            }
            continue;
          }

          // These are the other columns.
          const groupType = columnUpdate.column.groupType as DecisionTableColumnType;
          switch (groupType) {
            case DecisionTableColumnType.InputClause:
              const newInputs = [...(n.input ?? [])];
              newInputs[columnUpdate.columnIndex] = {
                ...newInputs[columnUpdate.columnIndex],
                inputExpression: {
                  ...newInputs[columnUpdate.columnIndex].inputExpression,
                  "@_typeRef": columnUpdate.typeRef,
                  text: { __$$text: columnUpdate.name },
                },
              };
              n.input = newInputs;
              break;
            case DecisionTableColumnType.OutputClause:
              const newOutputs = [...(n.output ?? [])];
              const outputIndex = columnUpdate.columnIndex - (prev.input?.length ?? 0);
              newOutputs[outputIndex] = {
                ...newOutputs[outputIndex],
                "@_typeRef": columnUpdate.typeRef,
                "@_name": columnUpdate.name,
              };

              n.output = newOutputs;
              break;
            case DecisionTableColumnType.Annotation:
              const newAnnotations = [...(n.annotation ?? [])];
              const annotationIndex = columnUpdate.columnIndex - (prev.input?.length ?? 0) - (prev.output?.length ?? 0);
              newAnnotations[annotationIndex] = {
                ...newAnnotations[annotationIndex],
                "@_name": columnUpdate.name,
              };
              n.annotation = newAnnotations;
              break;
            default:
              assertUnreachable(groupType);
          }
        }

        return n;
      });
    },
    [setExpression]
  );

  const onHitPolicySelect = useCallback(
    (hitPolicy: string) => {
      setExpression((prev) => {
        return {
          ...prev,
          "@_hitPolicy": hitPolicy as DMN15__tHitPolicy,
          "@_aggregation": HIT_POLICIES_THAT_SUPPORT_AGGREGATION.includes(hitPolicy)
            ? (prev as BoxedDecisionTable)["@_aggregation"]
            : undefined!,
        };
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
      setExpression((prev) => {
        return {
          ...prev,
          "@_aggregation": getAggregation(aggregation),
        };
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
      />
    ),
    [decisionTableExpression, getAggregationKey, onBuiltInAggregatorSelect, onHitPolicySelect]
  );

  const addVariables = useCallback(
    (newRules: DMN15__tDecisionRule[]) => {
      for (const rule of newRules) {
        if (rule.inputEntry) {
          for (const inputEntry of rule.inputEntry) {
            variables?.repository.addVariableToContext(
              inputEntry["@_id"]!,
              inputEntry["@_id"]!,
              decisionTableExpression.parentElementId
            );
          }
        }

        if (rule.outputEntry) {
          for (const outputEntry of rule.outputEntry) {
            variables?.repository.addVariableToContext(
              outputEntry["@_id"]!,
              outputEntry["@_id"]!,
              decisionTableExpression.parentElementId
            );
          }
        }
      }
    },
    [decisionTableExpression.parentElementId, variables?.repository]
  );

  const onRowAdded = useCallback(
    (args: { beforeIndex: number; rowsCount: number }) => {
      setExpression((prev: BoxedDecisionTable) => {
        const newRules = [...(prev.rule ?? [])];
        const newItems = [];

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

        addVariables(newRules);

        return {
          ...prev,
          rule: newRules,
        };
      });
    },
    [addVariables, setExpression]
  );

  const getSectionIndexForGroupType = useCallback(
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

      const sectionIndex = getSectionIndexForGroupType(args.beforeIndex, groupType);

      setExpression((prev: BoxedDecisionTable) => {
        const newRules = [...(prev.rule ?? [])];

        switch (groupType) {
          case DecisionTableColumnType.InputClause:
            const newInputClauses = [];

            const currentNames = prev.input?.map((c) => c.inputExpression.text?.__$$text ?? "") ?? [];

            for (let i = 0; i < args.columnsCount; i++) {
              const name = getNextAvailablePrefixedName(currentNames, "Input");
              currentNames.push(name);

              newInputClauses.push({
                "@_id": generateUuid(),
                inputExpression: {
                  "@_id": generateUuid(),
                  "@_typeRef": DmnBuiltInDataType.Undefined,
                  text: { __$$text: name },
                },
              });
            }

            const newInputs = [...(prev.input ?? [])];

            for (const newEntry of newInputClauses) {
              newInputs.splice(args.beforeIndex, 0, newEntry);
            }

            for (let i = 0; i < newRules.length; i++) {
              const r = newRules[i];
              const newInputEntries = [...(r.inputEntry ?? [])];
              for (let j = 0; j < args.columnsCount; j++) {
                const inputEntry = createInputEntry();
                variables?.repository.addVariableToContext(
                  inputEntry["@_id"]!,
                  inputEntry["@_id"]!,
                  decisionTableExpression.parentElementId
                );

                newInputEntries.splice(sectionIndex, 0, inputEntry);
              }
              newRules[i] = {
                ...r,
                inputEntry: newInputEntries,
              };
            }

            return {
              ...prev,
              input: newInputs,
              rule: newRules,
            };

          case DecisionTableColumnType.OutputClause:
            const newOutputClauses = [];
            const currentOutputNames = prev.output?.map((c) => c["@_name"] ?? "") ?? [];

            for (let i = 0; i < args.columnsCount; i++) {
              const name = getNextAvailablePrefixedName(currentOutputNames, "Output");
              currentOutputNames.push(name);

              newOutputClauses.push({
                "@_id": generateUuid(),
                "@_name": name,
                "@_typeRef": DmnBuiltInDataType.Undefined,
              });
            }

            const newOutputs = [...(prev.output ?? [])];

            for (const newEntry of newOutputClauses) {
              newOutputs.splice(args.beforeIndex, 0, newEntry);
            }

            for (let i = 0; i < newRules.length; i++) {
              const r = newRules[i];
              const newOutputEntries = [...(r.outputEntry ?? [])];
              for (let j = 0; j < args.columnsCount; j++) {
                const outputEntry = createOutputEntry();
                variables?.repository.addVariableToContext(
                  outputEntry["@_id"],
                  outputEntry["@_id"],
                  decisionTableExpression.parentElementId
                );

                newOutputEntries.splice(sectionIndex, 0, outputEntry);
              }

              newRules[i] = {
                ...r,
                outputEntry: newOutputEntries,
              };
            }

            return {
              ...prev,
              output: newOutputs,
              rule: newRules,
            };

          case DecisionTableColumnType.Annotation:
            const newAnnotations = [...(prev.annotation ?? [])];
            const newAnnotationsItems = [];
            const currentAnnotationNames = prev.annotation?.map((c) => c["@_name"] ?? "") ?? [];

            for (let i = 0; i < args.columnsCount; i++) {
              const name = getNextAvailablePrefixedName(currentAnnotationNames, DecisionTableColumnType.Annotation);
              currentAnnotationNames.push(name);

              newAnnotationsItems.push({
                "@_name": name,
              });
            }

            for (const newEntry of newAnnotationsItems) {
              newAnnotations.splice(args.beforeIndex, 0, newEntry);
            }

            for (let i = 0; i < newRules.length; i++) {
              const r = newRules[i];
              const newAnnotationEntries = [...(r.annotationEntry ?? [])];
              for (let j = 0; j < args.columnsCount; j++) {
                const newEntry = {
                  text: { __$$text: DECISION_TABLE_ANNOTATION_DEFAULT_VALUE },
                };

                newAnnotationEntries.splice(sectionIndex, 0, newEntry);
              }
              newRules[i] = {
                ...r,
                annotationEntry: newAnnotationEntries,
              };
            }

            return {
              ...prev,
              annotation: newAnnotations,
              rule: newRules,
            };

          default:
            assertUnreachable(groupType);
        }
      });

      setWidthById(id, (prev) => {
        const defaultWidth =
          args.groupType === DecisionTableColumnType.InputClause
            ? DECISION_TABLE_INPUT_DEFAULT_WIDTH
            : args.groupType === DecisionTableColumnType.OutputClause
            ? DECISION_TABLE_OUTPUT_DEFAULT_WIDTH
            : DECISION_TABLE_ANNOTATION_DEFAULT_WIDTH;

        const newWidthsById = [...prev];
        for (let i = 0; i < args.columnsCount; i++) {
          newWidthsById.splice(args.beforeIndex + 1, 0, defaultWidth); // + 1 to account for rowIndex column
        }
        return newWidthsById;
      });
    },
    [
      decisionTableExpression.parentElementId,
      getSectionIndexForGroupType,
      setExpression,
      setWidthById,
      variables?.repository,
      id,
    ]
  );

  const onColumnDeleted = useCallback(
    (args: { columnIndex: number; groupType: DecisionTableColumnType }) => {
      setExpression((prev: BoxedDecisionTable) => {
        const groupType = args.groupType;
        if (!groupType) {
          throw new Error("Column without groupType for Decision table.");
        }

        const sectionIndex = getSectionIndexForGroupType(args.columnIndex, groupType);

        switch (groupType) {
          case DecisionTableColumnType.InputClause:
            const newInputs = [...(prev.input ?? [])];
            newInputs.splice(sectionIndex, 1);
            return {
              ...prev,
              input: newInputs,
              rule: [...(prev.rule ?? [])].map((rule) => {
                const newInputEntry = [...(rule.inputEntry ?? [])];
                newInputEntry.splice(sectionIndex, 1);
                return {
                  ...rule,
                  inputEntry: newInputEntry,
                };
              }),
            };
          case DecisionTableColumnType.OutputClause:
            const newOutputs = [...(prev.output ?? [])];
            newOutputs.splice(sectionIndex, 1);
            return {
              ...prev,
              output: newOutputs,
              rule: [...(prev.rule ?? [])].map((rule) => {
                const newOutputEntry = [...rule.outputEntry];
                newOutputEntry.splice(sectionIndex, 1);
                return {
                  ...rule,
                  outputEntry: newOutputEntry,
                };
              }),
            };
          case DecisionTableColumnType.Annotation:
            const newAnnotations = [...(prev.annotation ?? [])];
            newAnnotations.splice(sectionIndex, 1);
            return {
              ...prev,
              annotation: newAnnotations,
              rule: [...(prev.rule ?? [])].map((rule) => {
                const newAnnotationEntry = [...(rule.annotationEntry ?? [])];
                newAnnotationEntry.splice(sectionIndex, 1);
                return {
                  ...rule,
                  annotationEntry: newAnnotationEntry,
                };
              }),
            };
          default:
            assertUnreachable(groupType);
        }
      });

      setWidthById(id, (prev) => {
        const n = [...prev];
        // FIXME: Tiago
        return n;
      });
    },
    [getSectionIndexForGroupType, setExpression, setWidthById, id]
  );

  const onRowDeleted = useCallback(
    (args: { rowIndex: number }) => {
      setExpression((prev: BoxedDecisionTable) => {
        const newRules = [...(prev.rule ?? [])];
        newRules.splice(args.rowIndex, 1);
        return {
          ...prev,
          rule: newRules,
        };
      });
    },
    [setExpression]
  );

  const onRowDuplicated = useCallback(
    (args: { rowIndex: number }) => {
      setExpression((prev: BoxedDecisionTable) => {
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
        return {
          ...prev,
          rule: newRules,
        };
      });
    },
    [setExpression]
  );

  const beeTableHeaderVisibility = useMemo(() => {
    return decisionTableExpression.isNested ? BeeTableHeaderVisibility.LastLevel : BeeTableHeaderVisibility.AllLevels;
  }, [decisionTableExpression.isNested]);

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

  return (
    <div className={`decision-table-expression ${decisionTableExpression["@_id"]}`}>
      <BeeTable
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
        variables={variables}
        // lastColumnMinWidth={lastColumnMinWidth} // FIXME: Check if this is a good strategy or not when doing https://github.com/kiegroup/kie-issues/issues/181
      />
    </div>
  );
}
