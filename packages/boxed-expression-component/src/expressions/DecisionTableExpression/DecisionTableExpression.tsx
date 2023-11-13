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
  DecisionTableExpressionDefinition,
  DecisionTableExpressionDefinitionBuiltInAggregation,
  DecisionTableExpressionDefinitionHitPolicy,
  DecisionTableExpressionDefinitionRule,
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
import { HitPolicySelector, HIT_POLICIES_THAT_SUPPORT_AGGREGATION } from "./HitPolicySelector";
import "./DecisionTableExpression.css";
import _ from "lodash";

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
    id: generateUuid(),
    content: DECISION_TABLE_INPUT_DEFAULT_VALUE,
  };
}

function createOutputEntry() {
  return {
    id: generateUuid(),
    content: DECISION_TABLE_OUTPUT_DEFAULT_VALUE,
  };
}

export function DecisionTableExpression(
  decisionTableExpression: DecisionTableExpressionDefinition & { isNested: boolean; parentElementId: string }
) {
  const { i18n } = useBoxedExpressionEditorI18n();
  const { decisionNodeId } = useBoxedExpressionEditor();
  const { setExpression } = useBoxedExpressionEditorDispatch();
  const { variables } = useBoxedExpressionEditor();

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
      setExpression((prev: DecisionTableExpressionDefinition) => {
        const newInputs = [...(prev.input ?? [])];
        const newWidth =
          typeof newWidthAction === "function" ? newWidthAction(newInputs[inputIndex].width) : newWidthAction;
        newInputs[inputIndex].width = newWidth;
        return { ...prev, input: newInputs };
      });
    },
    [setExpression]
  );

  const setOutputColumnWidth = useCallback(
    (outputIndex: number) => (newWidthAction: React.SetStateAction<number | undefined>) => {
      setExpression((prev: DecisionTableExpressionDefinition) => {
        const newOutputs = [...(prev.output ?? [])];
        const newWidth =
          typeof newWidthAction === "function" ? newWidthAction(newOutputs[outputIndex].width) : newWidthAction;
        newOutputs[outputIndex].width = newWidth;
        return { ...prev, output: newOutputs };
      });
    },
    [setExpression]
  );

  const setAnnotationColumnWidth = useCallback(
    (annotationIndex: number) => (newWidthAction: React.SetStateAction<number | undefined>) => {
      setExpression((prev: DecisionTableExpressionDefinition) => {
        const newAnnotations = [...(prev.annotations ?? [])];
        const newWidth =
          typeof newWidthAction === "function" ? newWidthAction(newAnnotations[annotationIndex].width) : newWidthAction;
        newAnnotations[annotationIndex].width = newWidth;
        return { ...prev, annotations: newAnnotations };
      });
    },
    [setExpression]
  );

  /// //////////////////////////////////////////////////////
  /// ///////////// RESIZING WIDTHS ////////////////////////
  /// //////////////////////////////////////////////////////

  const columns = useMemo(
    () => [
      ...(decisionTableExpression.input ?? []).map((c) => ({ ...c, minWidth: DECISION_TABLE_INPUT_MIN_WIDTH })),
      ...(decisionTableExpression.output ?? []).map((c) => ({ ...c, minWidth: DECISION_TABLE_OUTPUT_MIN_WIDTH })),
      ...(decisionTableExpression.annotations ?? []).map((c) => ({
        ...c,
        minWidth: DECISION_TABLE_ANNOTATION_MIN_WIDTH,
      })),
    ],
    [decisionTableExpression.annotations, decisionTableExpression.input, decisionTableExpression.output]
  );

  const beeTableRef = useRef<BeeTableRef>(null);
  const { onColumnResizingWidthChange, columnResizingWidths, isPivoting } = usePublishedBeeTableResizableColumns(
    decisionTableExpression.id,
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
    decisionTableExpression.rules ?? []
  );

  /// //////////////////////////////////////////////////////

  const beeTableColumns = useMemo<ReactTable.Column<ROWTYPE>[]>(() => {
    const inputColumns: ReactTable.Column<ROWTYPE>[] = (decisionTableExpression.input ?? []).map(
      (inputClause, inputIndex) => ({
        accessor: inputClause.id ?? generateUuid(),
        label: inputClause.name,
        id: inputClause.id,
        dataType: inputClause.dataType,
        width: inputClause.width ?? DECISION_TABLE_INPUT_MIN_WIDTH,
        setWidth: setInputColumnWidth(inputIndex),
        minWidth: DECISION_TABLE_INPUT_MIN_WIDTH,
        groupType: DecisionTableColumnType.InputClause,
        cssClasses: "decision-table--input",
        isRowIndexColumn: false,
      })
    );

    const outputColumns: ReactTable.Column<ROWTYPE>[] = (decisionTableExpression.output ?? []).map(
      (outputClause, outputIndex) => ({
        accessor: outputClause.id ?? generateUuid(),
        id: outputClause.id,
        label:
          decisionTableExpression.output?.length == 1
            ? decisionTableExpression.name ?? DEFAULT_EXPRESSION_NAME
            : outputClause.name,
        dataType:
          decisionTableExpression.output?.length == 1 ? decisionTableExpression.dataType : outputClause.dataType,
        width: outputClause.width ?? DECISION_TABLE_OUTPUT_MIN_WIDTH,
        setWidth: setOutputColumnWidth(outputIndex),
        minWidth: DECISION_TABLE_OUTPUT_MIN_WIDTH,
        groupType: DecisionTableColumnType.OutputClause,
        cssClasses: "decision-table--output",
        isRowIndexColumn: false,
      })
    );

    const outputSection = {
      groupType: DecisionTableColumnType.OutputClause,
      id: decisionNodeId as any, // FIXME: https://github.com/kiegroup/kie-issues/issues/169,
      accessor: "decision-table-expression" as any, // FIXME: https://github.com/kiegroup/kie-issues/issues/169
      label: decisionTableExpression.name ?? DEFAULT_EXPRESSION_NAME,
      dataType: decisionTableExpression.dataType,
      cssClasses: "decision-table--output",
      isRowIndexColumn: false,
      width: undefined,
      columns: outputColumns,
    };

    const annotationColumns: ReactTable.Column<ROWTYPE>[] = (decisionTableExpression.annotations ?? []).map(
      (annotation, annotationIndex) => {
        const annotationId = generateUuid();
        return {
          accessor: annotationId,
          id: annotationId,
          label: annotation.name,
          width: annotation.width ?? DECISION_TABLE_ANNOTATION_MIN_WIDTH,
          setWidth: setAnnotationColumnWidth(annotationIndex),
          minWidth: DECISION_TABLE_ANNOTATION_MIN_WIDTH,
          isInlineEditable: true,
          groupType: DecisionTableColumnType.Annotation,
          cssClasses: "decision-table--annotation",
          isRowIndexColumn: false,
          dataType: undefined as any,
        };
      }
    );

    if (outputColumns.length == 1) {
      return [...inputColumns, ...outputColumns, ...annotationColumns];
    } else {
      return [...inputColumns, outputSection, ...annotationColumns];
    }
  }, [
    decisionNodeId,
    decisionTableExpression.annotations,
    decisionTableExpression.dataType,
    decisionTableExpression.input,
    decisionTableExpression.name,
    decisionTableExpression.output,
    setAnnotationColumnWidth,
    setInputColumnWidth,
    setOutputColumnWidth,
  ]);

  const beeTableRows = useMemo(
    () =>
      (decisionTableExpression.rules ?? []).map((rule) => {
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
    [beeTableColumns, decisionTableExpression.rules]
  );

  const onCellUpdates = useCallback(
    (cellUpdates: BeeTableCellUpdate<ROWTYPE>[]) => {
      setExpression((prev: DecisionTableExpressionDefinition) => {
        const n = { ...prev };

        cellUpdates.forEach((u) => {
          const newRules = [...(n.rules ?? [])];
          const groupType = u.column.groupType as DecisionTableColumnType;
          switch (groupType) {
            case DecisionTableColumnType.InputClause:
              const newInputEntries = [...newRules[u.rowIndex].inputEntries];
              newInputEntries[u.columnIndex].content = u.value;
              newRules[u.rowIndex].inputEntries = newInputEntries;
              n.rules = newRules;
              break;
            case DecisionTableColumnType.OutputClause:
              const newOutputEntries = [...newRules[u.rowIndex].outputEntries];
              newOutputEntries[u.columnIndex - (prev.input?.length ?? 0)].content = u.value;
              newRules[u.rowIndex].outputEntries = newOutputEntries;
              n.rules = newRules;
              break;
            case DecisionTableColumnType.Annotation:
              const newAnnotationEntries = [...newRules[u.rowIndex].annotationEntries];
              newAnnotationEntries[u.columnIndex - (prev.input?.length ?? 0) - (prev.output?.length ?? 0)] = u.value;
              newRules[u.rowIndex].annotationEntries = newAnnotationEntries;
              n.rules = newRules;
              break;
            default:
              assertUnreachable(groupType);
          }
        });

        return n;
      });
    },
    [setExpression]
  );

  const onColumnUpdates = useCallback(
    (columnUpdates: BeeTableColumnUpdate<ROWTYPE>[]) => {
      setExpression((prev: DecisionTableExpressionDefinition) => {
        const n = { ...prev };
        for (const u of columnUpdates) {
          // This is the Output column aggregator column, which represents the entire expression name and dataType
          if (u.column.depth === 0 && u.column.groupType === DecisionTableColumnType.OutputClause) {
            n.name = u.name;
            n.dataType = u.dataType;
            // Single output column is merged with the aggregator column and should have the same datatype
            if (n.output?.length === 1) {
              n.output[0].dataType = u.dataType;
            }
            continue;
          }

          // These are the other columns.
          const groupType = u.column.groupType as DecisionTableColumnType;
          switch (groupType) {
            case DecisionTableColumnType.InputClause:
              const newInputs = [...(n.input ?? [])];
              newInputs[u.columnIndex] = {
                ...newInputs[u.columnIndex],
                dataType: u.dataType,
                name: u.name,
              };
              n.input = newInputs;
              break;
            case DecisionTableColumnType.OutputClause:
              const newOutputs = [...(n.output ?? [])];
              newOutputs[u.columnIndex - (prev.input?.length ?? 0)] = {
                ...newOutputs[u.columnIndex - (prev.input?.length ?? 0)],
                dataType: u.dataType,
                name: u.name,
              };

              n.output = newOutputs;
              break;
            case DecisionTableColumnType.Annotation:
              const newAnnotations = [...(n.annotations ?? [])];
              newAnnotations[u.columnIndex - (prev.input?.length ?? 0) - (prev.output?.length ?? 0)] = {
                ...newAnnotations[u.columnIndex - (prev.input?.length ?? 0) - (prev.output?.length ?? 0)],
                name: u.name,
              };
              n.annotations = newAnnotations;
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
        selectedHitPolicy={decisionTableExpression.hitPolicy}
        selectedBuiltInAggregator={decisionTableExpression.aggregation}
        onHitPolicySelected={onHitPolicySelect}
        onBuiltInAggregatorSelected={onBuiltInAggregatorSelect}
      />
    ),
    [
      decisionTableExpression.aggregation,
      decisionTableExpression.hitPolicy,
      onBuiltInAggregatorSelect,
      onHitPolicySelect,
    ]
  );

  const addVariables = useCallback(
    (newRules: DecisionTableExpressionDefinitionRule[]) => {
      for (const rule of newRules) {
        if (rule.inputEntries) {
          for (const inputEntry of rule.inputEntries) {
            variables?.repository.addVariableToContext(
              inputEntry.id,
              inputEntry.id,
              decisionTableExpression.parentElementId
            );
          }
        }

        if (rule.outputEntries) {
          for (const outputEntry of rule.outputEntries) {
            variables?.repository.addVariableToContext(
              outputEntry.id,
              outputEntry.id,
              decisionTableExpression.parentElementId
            );
          }
        }
      }
    },
    [decisionTableExpression.parentElementId, variables?.repository]
  );

  const onRowAdded = useCallback(
    (args: { beforeIndex: number; rowsCount: number; insertDirection: InsertRowColumnsDirection }) => {
      setExpression((prev: DecisionTableExpressionDefinition) => {
        const newRules = [...(prev.rules ?? [])];
        const newItems = [];

        for (let i = 0; i < args.rowsCount; i++) {
          newItems.push({
            id: generateUuid(),
            inputEntries: Array.from(new Array(prev.input?.length ?? 0)).map(() => {
              return createInputEntry();
            }),
            outputEntries: Array.from(new Array(prev.output?.length ?? 0)).map(() => {
              return createOutputEntry();
            }),
            annotationEntries: Array.from(new Array(prev.annotations?.length ?? 0)).map(
              () => DECISION_TABLE_ANNOTATION_DEFAULT_VALUE
            ),
          });
        }

        for (const newEntry of newItems) {
          let index = args.beforeIndex;
          newRules.splice(index, 0, newEntry);
          if (args.insertDirection === InsertRowColumnsDirection.AboveOrRight) {
            index++;
          }
        }

        addVariables(newRules);

        return {
          ...prev,
          rules: newRules,
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
    (args: {
      beforeIndex: number;
      groupType: DecisionTableColumnType | undefined;
      columnsCount: number;
      insertDirection: InsertRowColumnsDirection;
    }) => {
      const groupType = args.groupType;
      if (!groupType) {
        throw new Error("Column without groupType for Decision table.");
      }

      const sectionIndex = getSectionIndexForGroupType(args.beforeIndex, groupType);

      setExpression((prev: DecisionTableExpressionDefinition) => {
        const newRules = [...(prev.rules ?? [])];

        switch (groupType) {
          case DecisionTableColumnType.InputClause:
            const newInputClauses = [];

            const currentNames = prev.input?.map((c) => c.name) ?? [];

            for (let i = 0; i < args.columnsCount; i++) {
              const name = getNextAvailablePrefixedName(currentNames, "input");
              currentNames.push(name);

              newInputClauses.push({
                id: generateUuid(),
                idLiteralExpression: generateUuid(),
                name: name,
                dataType: DmnBuiltInDataType.Undefined,
                width: DECISION_TABLE_INPUT_DEFAULT_WIDTH,
              });
            }

            const newInputs = [...(prev.input ?? [])];

            for (const newEntry of newInputClauses) {
              let index = args.beforeIndex;
              newInputs.splice(index, 0, newEntry);
              if (args.insertDirection === InsertRowColumnsDirection.BelowOrLeft) {
                index++;
              }
            }

            newRules.forEach((r) => {
              for (let i = 0; i < args.columnsCount; i++) {
                const inputEntry = createInputEntry();
                variables?.repository.addVariableToContext(
                  inputEntry.id,
                  inputEntry.id,
                  decisionTableExpression.parentElementId
                );
                r.inputEntries.splice(sectionIndex, 0, inputEntry);
              }
            });

            return {
              ...prev,
              input: newInputs,
              rules: newRules,
            };

          case DecisionTableColumnType.OutputClause:
            const newOutputClauses = [];
            const currentOutputNames = prev.output?.map((c) => c.name) ?? [];

            for (let i = 0; i < args.columnsCount; i++) {
              const name = getNextAvailablePrefixedName(currentOutputNames, "output");
              currentOutputNames.push(name);

              newOutputClauses.push({
                id: generateUuid(),
                name: name,
                dataType: DmnBuiltInDataType.Undefined,
                width: DECISION_TABLE_OUTPUT_DEFAULT_WIDTH,
              });
            }

            const newOutputs = [...(prev.output ?? [])];

            for (const newEntry of newOutputClauses) {
              let index = args.beforeIndex;
              newOutputs.splice(index, 0, newEntry);
              if (args.insertDirection === InsertRowColumnsDirection.BelowOrLeft) {
                index++;
              }
            }

            newRules.forEach((r) => {
              for (let i = 0; i < args.columnsCount; i++) {
                const outputEntry = createOutputEntry();
                variables?.repository.addVariableToContext(
                  outputEntry.id,
                  outputEntry.id,
                  decisionTableExpression.parentElementId
                );
                r.outputEntries.splice(sectionIndex, 0, outputEntry);
              }
            });

            return {
              ...prev,
              output: newOutputs,
              rules: newRules,
            };

          case DecisionTableColumnType.Annotation:
            const newAnnotations = [...(prev.annotations ?? [])];
            const newAnnotationsItems = [];
            const currentAnnotationNames = prev.annotations?.map((c) => c.name) ?? [];

            for (let i = 0; i < args.columnsCount; i++) {
              const name = getNextAvailablePrefixedName(currentAnnotationNames, "annotation");
              currentAnnotationNames.push(name);

              newAnnotationsItems.push({
                name: name,
                width: DECISION_TABLE_ANNOTATION_DEFAULT_WIDTH,
              });
            }

            for (const newEntry of newAnnotationsItems) {
              let index = args.beforeIndex;
              newAnnotations.splice(index, 0, newEntry);
              if (args.insertDirection === InsertRowColumnsDirection.BelowOrLeft) {
                index++;
              }
            }

            newRules.forEach((r) => {
              for (let i = 0; i < args.columnsCount; i++) {
                r.annotationEntries.splice(sectionIndex, 0, DECISION_TABLE_ANNOTATION_DEFAULT_VALUE);
              }
            });

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
    [decisionTableExpression.parentElementId, getSectionIndexForGroupType, setExpression, variables?.repository]
  );

  const onColumnDeleted = useCallback(
    (args: { columnIndex: number; groupType: DecisionTableColumnType }) => {
      setExpression((prev: DecisionTableExpressionDefinition) => {
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
              rules: [...(prev.rules ?? [])].map((rule) => {
                const newInputEntry = [...rule.inputEntries];
                newInputEntry.splice(sectionIndex, 1);
                return {
                  ...rule,
                  inputEntries: newInputEntry,
                };
              }),
            };
          case DecisionTableColumnType.OutputClause:
            const newOutputs = [...(prev.output ?? [])];
            newOutputs.splice(sectionIndex, 1);
            return {
              ...prev,
              output: newOutputs,
              rules: [...(prev.rules ?? [])].map((rule) => {
                const newOutputEntry = [...rule.outputEntries];
                newOutputEntry.splice(sectionIndex, 1);
                return {
                  ...rule,
                  outputEntries: newOutputEntry,
                };
              }),
            };
          case DecisionTableColumnType.Annotation:
            const newAnnotations = [...(prev.annotations ?? [])];
            newAnnotations.splice(sectionIndex, 1);
            return {
              ...prev,
              annotations: newAnnotations,
              rules: [...(prev.rules ?? [])].map((rule) => {
                const newAnnotationEntry = [...rule.annotationEntries];
                newAnnotationEntry.splice(sectionIndex, 1);
                return {
                  ...rule,
                  annotationEntries: newAnnotationEntry,
                };
              }),
            };
          default:
            assertUnreachable(groupType);
        }
      });
    },
    [getSectionIndexForGroupType, setExpression]
  );

  const onRowDeleted = useCallback(
    (args: { rowIndex: number }) => {
      setExpression((prev: DecisionTableExpressionDefinition) => {
        const newRules = [...(prev.rules ?? [])];
        newRules.splice(args.rowIndex, 1);
        return {
          ...prev,
          rules: newRules,
        };
      });
    },
    [setExpression]
  );

  const onRowDuplicated = useCallback(
    (args: { rowIndex: number }) => {
      setExpression((prev: DecisionTableExpressionDefinition) => {
        const duplicatedRule = {
          id: generateUuid(),
          inputEntries: prev.rules![args.rowIndex].inputEntries.map((input) => ({
            ...input,
            id: generateUuid(),
          })),
          outputEntries: prev.rules![args.rowIndex].outputEntries.map((output) => ({
            ...output,
            id: generateUuid(),
          })),
          annotationEntries: prev.rules![args.rowIndex].annotationEntries.slice(),
        };

        const newRules = [...(prev.rules ?? [])];
        newRules.splice(args.rowIndex, 0, duplicatedRule);
        return {
          ...prev,
          rules: newRules,
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
    <div className={`decision-table-expression ${decisionTableExpression.id}`}>
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
