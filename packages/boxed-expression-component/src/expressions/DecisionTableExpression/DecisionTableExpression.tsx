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
import { useCallback, useMemo, useRef } from "react";
import * as ReactTable from "react-table";
import {
  BeeTableHeaderVisibility,
  BeeTableOperation,
  BeeTableOperationConfig,
  DecisionTableExpressionDefinition,
  DecisionTableExpressionDefinitionBuiltInAggregation,
  DecisionTableExpressionDefinitionHitPolicy,
  DmnBuiltInDataType,
  generateUuid,
  getNextAvailablePrefixedName,
} from "../../api";
import { useBoxedExpressionEditorI18n } from "../../i18n";
import { usePublishedBeeTableResizableColumns } from "../../resizing/BeeTableResizableColumnsContextProvider";
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
import { useBoxedExpressionEditorDispatch } from "../BoxedExpressionEditor/BoxedExpressionEditorContext";
import { DEFAULT_EXPRESSION_NAME } from "../ExpressionDefinitionHeaderMenu";
import { assertUnreachable } from "../ExpressionDefinitionRoot/ExpressionDefinitionLogicTypeSelector";
import "./DecisionTableExpression.css";
import { HitPolicySelector, HIT_POLICIES_THAT_SUPPORT_AGGREGATION } from "./HitPolicySelector";

type ROWTYPE = any; // FIXME: Tiago

enum DecisionTableColumnType {
  InputClause = "input",
  OutputClause = "output",
  Annotation = "annotation",
}

export const DECISION_TABLE_INPUT_DEFAULT_VALUE = "-";
export const DECISION_TABLE_OUTPUT_DEFAULT_VALUE = "";
export const DECISION_TABLE_ANNOTATION_DEFAULT_VALUE = "";

export function DecisionTableExpression(
  decisionTableExpression: DecisionTableExpressionDefinition & { isNested: boolean }
) {
  const { i18n } = useBoxedExpressionEditorI18n();
  const { setExpression } = useBoxedExpressionEditorDispatch();

  const generateOperationConfig = useCallback(
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

  const beeTableOperationConfig = useMemo<BeeTableOperationConfig>(() => {
    const config: BeeTableOperationConfig = {};
    config[""] = generateOperationConfig(i18n.inputClause);
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
      ...(decisionTableExpression.input ?? []),
      ...(decisionTableExpression.output ?? []),
      ...(decisionTableExpression.annotations ?? []),
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
    BEE_TABLE_ROW_INDEX_COLUMN_WIDTH + columns.length + 1, //1px for each column border-left, 1px for the last column border-right.
    columns,
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
        label: outputClause.name,
        dataType: outputClause.dataType,
        width: outputClause.width ?? DECISION_TABLE_OUTPUT_MIN_WIDTH,
        setWidth: setOutputColumnWidth(outputIndex),
        minWidth: DECISION_TABLE_OUTPUT_MIN_WIDTH,
        groupType: DecisionTableColumnType.OutputClause,
        cssClasses: "decision-table--output",
        isRowIndexColumn: false,
      })
    );

    const annotationColumns: ReactTable.Column<ROWTYPE>[] = (decisionTableExpression.annotations ?? []).map(
      (annotation, annotationIndex) => ({
        accessor: annotation.id ?? (generateUuid() as any),
        id: annotation.id,
        label: annotation.name,
        width: annotation.width ?? DECISION_TABLE_ANNOTATION_MIN_WIDTH,
        setWidth: setAnnotationColumnWidth(annotationIndex),
        minWidth: DECISION_TABLE_ANNOTATION_MIN_WIDTH,
        isInlineEditable: true,
        groupType: DecisionTableColumnType.Annotation,
        cssClasses: "decision-table--annotation",
        isRowIndexColumn: false,
        dataType: undefined as any,
      })
    );

    const inputSection = {
      groupType: DecisionTableColumnType.InputClause,
      id: "Inputs",
      accessor: "Inputs" as any,
      label: "Inputs",
      dataType: undefined as any,
      cssClasses: "decision-table--input",
      isRowIndexColumn: false,
      columns: inputColumns,
      width: undefined,
    };

    const outputSection = {
      groupType: DecisionTableColumnType.OutputClause,
      id: "Outputs",
      accessor: "decision-table-expression" as any, // FIXME: Tiago -> ?
      label: decisionTableExpression.name ?? DEFAULT_EXPRESSION_NAME,
      dataType: decisionTableExpression.dataType ?? DmnBuiltInDataType.Undefined,
      cssClasses: "decision-table--output",
      isRowIndexColumn: false,
      columns: outputColumns,
      width: undefined,
    };

    const annotationSection = {
      groupType: DecisionTableColumnType.Annotation,
      id: "Annotations",
      accessor: "Annotations" as any,
      label: "Annotations",
      cssClasses: "decision-table--annotation",
      columns: annotationColumns,
      isInlineEditable: false,
      isRowIndexColumn: false,
      dataType: undefined as any,
      width: undefined,
    };

    return [inputSection, outputSection, annotationSection];
  }, [
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
              newInputEntries[u.columnIndex] = u.value;
              newRules[u.rowIndex].inputEntries = newInputEntries;
              n.rules = newRules;
              break;
            case DecisionTableColumnType.OutputClause:
              const newOutputEntries = [...newRules[u.rowIndex].outputEntries];
              newOutputEntries[u.columnIndex - (prev.input?.length ?? 0)] = u.value;
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

  const onRowAdded = useCallback(
    (args: { beforeIndex: number }) => {
      setExpression((prev: DecisionTableExpressionDefinition) => {
        const newRules = [...(prev.rules ?? [])];
        newRules.splice(args.beforeIndex, 0, {
          id: generateUuid(),
          inputEntries: Array.from(new Array(prev.input?.length ?? 0)).map(() => DECISION_TABLE_INPUT_DEFAULT_VALUE),
          outputEntries: Array.from(new Array(prev.output?.length ?? 0)).map(() => DECISION_TABLE_OUTPUT_DEFAULT_VALUE),
          annotationEntries: Array.from(new Array(prev.annotations?.length ?? 0)).map(
            () => DECISION_TABLE_ANNOTATION_DEFAULT_VALUE
          ),
        });

        return {
          ...prev,
          rules: newRules,
        };
      });
    },
    [setExpression]
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
    (args: { beforeIndex: number; groupType: DecisionTableColumnType | undefined }) => {
      const groupType = args.groupType;
      if (!groupType) {
        throw new Error("Column without groupType for Decision table.");
      }

      const sectionIndex = getSectionIndexForGroupType(args.beforeIndex, groupType);

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

            newRules.forEach((r) => r.inputEntries.splice(sectionIndex, 0, DECISION_TABLE_INPUT_DEFAULT_VALUE));

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

            newRules.forEach((r) => r.outputEntries.splice(sectionIndex, 0, DECISION_TABLE_OUTPUT_DEFAULT_VALUE));

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

            newRules.forEach((r) =>
              r.annotationEntries.splice(sectionIndex, 0, DECISION_TABLE_ANNOTATION_DEFAULT_VALUE)
            );

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
    [getSectionIndexForGroupType, setExpression]
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
          ...JSON.parse(JSON.stringify(prev.rules?.[args.rowIndex])),
          id: generateUuid(),
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

  return (
    <div className={`decision-table-expression ${decisionTableExpression.id}`}>
      <BeeTable
        resizerStopBehavior={
          isPivoting ? ResizerStopBehavior.SET_WIDTH_ALWAYS : ResizerStopBehavior.SET_WIDTH_WHEN_SMALLER
        }
        forwardRef={beeTableRef}
        headerLevelCount={1}
        headerVisibility={beeTableHeaderVisibility}
        editColumnLabel={getEditColumnLabel}
        operationConfig={beeTableOperationConfig}
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
        // lastColumnMinWidth={lastColumnMinWidth} // FIXME: Tiago -> What to do?
      />
    </div>
  );
}
