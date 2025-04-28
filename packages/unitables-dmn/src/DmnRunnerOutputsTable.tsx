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
import { ErrorBoundary } from "@kie-tools/dmn-runner/dist/ErrorBoundary";
import {
  EmptyState,
  EmptyStateBody,
  EmptyStateIcon,
  EmptyStateHeader,
} from "@patternfly/react-core/dist/js/components/EmptyState";
import { Text, TextContent } from "@patternfly/react-core/dist/js/components/Text";
import { CubeIcon } from "@patternfly/react-icons/dist/js/icons/cube-icon";
import { ExclamationIcon } from "@patternfly/react-icons/dist/js/icons/exclamation-icon";
import { useCallback, useEffect, useMemo, useRef, useState } from "react";
import nextId from "react-id-generator";
import { OutputField, useDmnRunnerOutputs as useDmnRunnerOutputs } from "./DmnRunnerOutputs";
import { DmnUnitablesI18n } from "./i18n";
import { DmnUnitablesJsonSchemaBridge } from "./uniforms/DmnUnitablesJsonSchemaBridge";
import * as ReactTable from "react-table";
import {
  BeeTableContextMenuAllowedOperationsConditions,
  BeeTableHeaderVisibility,
  BeeTableOperation,
  BeeTableOperationConfig,
  DmnBuiltInDataType,
  generateUuid,
} from "@kie-tools/boxed-expression-component/dist/api";
import { getColumnsAtLastLevel } from "@kie-tools/boxed-expression-component/dist/table/BeeTable";
import { StandaloneBeeTable } from "@kie-tools/boxed-expression-component/dist/table/BeeTable/StandaloneBeeTable";
import { BoxedExpressionEditorI18n } from "@kie-tools/boxed-expression-component/dist/i18n";
import "@kie-tools/boxed-expression-component/dist/@types/react-table";
import { ResizerStopBehavior } from "@kie-tools/boxed-expression-component/dist/resizing/ResizingWidthsContext";
import "./DmnRunnerOutputsTable.css";
import { DecisionResult, DmnEvaluationResult } from "@kie-tools/extended-services-api";
import { Button } from "@patternfly/react-core/dist/js/components/Button";
import { ArrowUpIcon } from "@patternfly/react-icons/dist/js/icons/arrow-up-icon";
import { getDefaultColumnWidth } from "@kie-tools/boxed-expression-component/dist/resizing/WidthsToFitData";

interface Props {
  i18n: DmnUnitablesI18n;
  results: Array<DecisionResult[] | undefined> | undefined;
  jsonSchemaBridge: DmnUnitablesJsonSchemaBridge;
  scrollableParentRef: React.RefObject<HTMLElement>;
  openBoxedExpressionEditor?: (nodeId: string) => void;
  openedBoxedExpressionEditorNodeId: string | undefined;
}

export function DmnRunnerOutputsTable({
  i18n,
  jsonSchemaBridge,
  results,
  scrollableParentRef,
  openBoxedExpressionEditor,
  openedBoxedExpressionEditorNodeId,
}: Props) {
  const outputUid = useMemo(() => nextId(), []);
  const outputErrorBoundaryRef = useRef<ErrorBoundary>(null);
  const [outputError, setOutputError] = useState<boolean>(false);

  const { outputsPropertiesMap } = useDmnRunnerOutputs(jsonSchemaBridge, results);

  useEffect(() => {
    outputErrorBoundaryRef.current?.reset();
  }, [outputsPropertiesMap]);

  const numberOfResults = useMemo(
    () => results?.reduce((acc, result) => acc + (result?.length ?? 0), 0) ?? 0,
    [results]
  );

  return (
    <>
      {outputError ? (
        outputError
      ) : numberOfResults > 0 ? (
        <ErrorBoundary ref={outputErrorBoundaryRef} setHasError={setOutputError} error={<OutputError />}>
          <OutputsBeeTable
            scrollableParentRef={scrollableParentRef}
            i18n={i18n}
            outputsPropertiesMap={outputsPropertiesMap}
            results={results}
            id={outputUid}
            openBoxedExpressionEditor={openBoxedExpressionEditor}
            openedBoxedExpressionEditorNodeId={openedBoxedExpressionEditorNodeId}
          />
        </ErrorBoundary>
      ) : (
        <EmptyState>
          <EmptyStateHeader icon={<EmptyStateIcon icon={CubeIcon} />} />
          <TextContent>
            <Text component={"h2"}>No Decision results yet...</Text>
          </TextContent>
          <EmptyStateBody>
            <TextContent>
              Add input and decision nodes, provide values to the inputs at the left and see the Decisions results here.
            </TextContent>
          </EmptyStateBody>
        </EmptyState>
      )}
    </>
  );
}

function OutputError() {
  return (
    <div>
      <EmptyState>
        <EmptyStateHeader icon={<EmptyStateIcon icon={ExclamationIcon} />} />
        <TextContent>
          <Text component={"h2"}>Error</Text>
        </TextContent>
        <EmptyStateBody>
          <p>An error has happened while trying to show your Decision results</p>
        </EmptyStateBody>
      </EmptyState>
    </div>
  );
}

interface ROWTYPE {
  id: string;
}

const EMPTY_SYMBOL = "";

interface OutputsTableProps {
  id: string;
  i18n: BoxedExpressionEditorI18n;
  results: (DecisionResult[] | undefined)[] | undefined;
  outputsPropertiesMap: Map<string, OutputField>;
  scrollableParentRef: React.RefObject<HTMLElement>;
  openBoxedExpressionEditor?: (nodeId: string) => void;
  openedBoxedExpressionEditorNodeId: string | undefined;
}

function OutputsBeeTable({
  id,
  i18n,
  outputsPropertiesMap,
  results,
  scrollableParentRef,
  openBoxedExpressionEditor,
  openedBoxedExpressionEditorNodeId,
}: OutputsTableProps) {
  const beeTableOperationConfig = useMemo<BeeTableOperationConfig>(
    () => [
      {
        group: i18n.terms.selection.toUpperCase(),
        items: [{ name: i18n.terms.copy, type: BeeTableOperation.SelectionCopy }],
      },
    ],
    [i18n]
  );

  const uuid = useMemo(() => {
    return generateUuid();
  }, []);

  /**
   * Wrapping 'getDefaultColumnWidth' to add additional space for the '<ArrowUp />' icon in the runner outputs columns.
   */
  const getDefaultDmnRunnerOutputColumnWidth = useCallback((label, dataType) => {
    const OPEN_BOXED_EXPRESSION_HEADER_ARROW_UP_BUTTON_WIDTH = 50;
    const ADDITIONAL_WIDTH_TO_CENTER_HEADER_CONTENT = 2 * OPEN_BOXED_EXPRESSION_HEADER_ARROW_UP_BUTTON_WIDTH;
    return getDefaultColumnWidth({ name: label, typeRef: dataType }) + ADDITIONAL_WIDTH_TO_CENTER_HEADER_CONTENT;
  }, []);

  const deepFlattenObjectColumn = useCallback(
    (
      myObject: Record<string, any>,
      propertiesTypes?: Record<string, any>,
      parentKey?: string,
      parentColumnMinWidth?: number
    ): ReactTable.Column<ROWTYPE>[] => {
      return Object.entries(myObject).flatMap(([myObjectKey, value]) => {
        if (value !== null && typeof value === "object") {
          const myKey = parentKey ? `${parentKey}-${myObjectKey}` : myObjectKey;
          return deepFlattenObjectColumn(
            value,
            propertiesTypes?.[myObjectKey]?.properties ?? propertiesTypes?.[myObjectKey]?.items,
            myKey,
            parentColumnMinWidth
          );
        }

        const label = parentKey ? `${parentKey}-${myObjectKey}` : myObjectKey;
        const myObjectProperties = propertiesTypes?.[myObjectKey] ?? propertiesTypes;
        const dataType = myObjectProperties ? myObjectProperties.type : DmnBuiltInDataType.Undefined;
        const columnMinWidth = getDefaultDmnRunnerOutputColumnWidth(label, dataType);

        return {
          originalId: label + generateUuid(),
          label,
          accessor: (`output-object-${label}` + generateUuid()) as any,
          dataType,
          isRowIndexColumn: false,
          groupType: "dmn-runner-output",
          minWidth: Math.max(
            (parentColumnMinWidth ?? columnMinWidth) / Object.entries(myObject).length,
            columnMinWidth
          ),
        };
      });
    },
    [getDefaultDmnRunnerOutputColumnWidth]
  );

  const getRowValue = useCallback((value: DmnEvaluationResult) => {
    if (value === undefined) {
      return;
    } else if (value === null) {
      return "null";
    } else if (Array.isArray(value)) {
      return value.map((element) => JSON.stringify(element, null, 2).replace(/"([^"]+)":/g, "$1:"));
    } else {
      return JSON.stringify(value);
    }
  }, []);

  const deepFlattenObjectRow = useCallback(
    (
      myObject: Record<string, DmnEvaluationResult>,
      parentKey?: string,
      flattenedObject?: Record<string, any>
    ): Record<string, any> => {
      return Object.entries(myObject).reduce((acc: Record<string, any>, [myObjectKey, value]) => {
        const myKey = parentKey ? `${parentKey}-${myObjectKey}` : myObjectKey;

        if (value !== null && !Array.isArray(value) && typeof value === "object") {
          return deepFlattenObjectRow(value, myKey, acc);
        }
        if (value !== null && Array.isArray(value)) {
          return value.reduce((acc, v, index) => {
            if (v !== null && !Array.isArray(v) && typeof v === "object") {
              return { ...acc, ...deepFlattenObjectRow(v, `${myKey}-${index}`, acc) };
            } else {
              const rowValue = getRowValue(v);
              if (rowValue) {
                acc[`${myKey}-${index}`] = rowValue;
              }
              return acc;
            }
          }, acc);
        }
        const rowValue = getRowValue(value);
        if (rowValue) {
          acc[myKey] = rowValue;
        }
        return acc;
      }, flattenedObject ?? {});
    },
    [getRowValue]
  );

  const resultsDecisionIds = useMemo(
    () =>
      results?.[0]?.reduce((set, result) => {
        set.add(result.decisionId);
        return set;
      }, new Set()),
    [results]
  );

  const openBoxedExpressionHeaderButton = useCallback(
    ({ decisionId, decisionName }) => {
      return (
        <Button
          variant={"plain"}
          title={`Open '${decisionName}' expression`}
          icon={<ArrowUpIcon />}
          onClick={() => resultsDecisionIds!.has(decisionId) && openBoxedExpressionEditor?.(decisionId)}
        />
      );
    },
    [openBoxedExpressionEditor, resultsDecisionIds]
  );

  const beeTableColumns = useMemo<ReactTable.Column<ROWTYPE>[]>(() => {
    return (results?.[0] ?? []).flatMap(({ result, decisionName, decisionId }) => {
      const outputProperties = outputsPropertiesMap.get(decisionName);
      if (!outputProperties) {
        return [];
      }

      // Contexts/Structures
      if (outputProperties?.dataType === "context") {
        // collect results from all rows;
        const collectedOutputs = results?.flatMap((result) =>
          result
            ?.filter((decisionResult) => decisionResult.decisionName === outputProperties.joinedName)
            ?.flatMap((decisionResult) => decisionResult.result)
        );
        const parentLabel = outputProperties?.name ?? "";
        const parentDataType = outputProperties?.dataType ?? DmnBuiltInDataType.Undefined;
        const parentColumnMinWidth = getDefaultDmnRunnerOutputColumnWidth(parentLabel, parentDataType);
        return [
          {
            originalId: `${outputProperties?.name}-${generateUuid()}`,
            cssClasses: decisionId === openedBoxedExpressionEditorNodeId ? "runner-column-highlight" : "",
            headerCellElementExtension: openBoxedExpressionHeaderButton({ decisionId, decisionName }),
            label: parentLabel,
            accessor: (`output-object-parent-${outputProperties?.name}-` + generateUuid()) as any,
            dataType: parentDataType,
            isRowIndexColumn: false,
            groupType: "dmn-runner-output",
            columns:
              collectedOutputs
                ?.flatMap((collectedOutput) => {
                  if (collectedOutput !== null && typeof collectedOutput === "object") {
                    return deepFlattenObjectColumn(
                      collectedOutput,
                      outputProperties?.properties,
                      undefined,
                      parentColumnMinWidth
                    );
                  }
                  const label = "context";
                  const dataType = outputProperties?.dataType ?? DmnBuiltInDataType.Undefined;
                  const columnMinWidth = getDefaultDmnRunnerOutputColumnWidth(label, dataType);
                  return {
                    originalId: `context-${generateUuid()}`,
                    label: label,
                    accessor: (`output-context-` + generateUuid()) as any,
                    dataType: dataType,
                    isRowIndexColumn: false,
                    groupType: "dmn-runner-output",
                    minWidth: Math.max(parentColumnMinWidth / collectedOutputs.length, columnMinWidth),
                  };
                })
                .reduce((acc: ReactTable.Column<ROWTYPE>[], column) => {
                  if (acc.find((e) => e.label === column.label)) {
                    return acc;
                  }
                  if (outputProperties) {
                    outputsPropertiesMap?.set(outputProperties?.name, outputProperties);
                  }
                  return [...acc, column];
                }, []) ?? [],
          },
        ];
      }

      // Primitives and null;
      if (typeof result === "string" || typeof result === "number" || typeof result === "boolean" || result === null) {
        const label = outputProperties?.name ?? "";
        const dataType = outputProperties?.dataType ?? DmnBuiltInDataType.Undefined;
        const columnMinWidth = getDefaultDmnRunnerOutputColumnWidth(label, dataType);
        return [
          {
            originalId: `parent-${outputProperties?.name}-${generateUuid()}`,
            label: "",
            accessor: (`output-parent-${outputProperties?.name}-` + generateUuid()) as any,
            dataType: undefined as any,
            isRowIndexColumn: false,
            groupType: "dmn-runner-output",
            columns: [
              {
                originalId: `${outputProperties?.name}-${generateUuid()}-${outputProperties?.properties?.id}`,
                cssClasses: decisionId === openedBoxedExpressionEditorNodeId ? "runner-column-highlight" : "",
                headerCellElementExtension: openBoxedExpressionHeaderButton({ decisionId, decisionName }),
                label: label,
                accessor: (`output-${outputProperties?.name}-` + generateUuid()) as any,
                dataType: dataType,
                isRowIndexColumn: false,
                groupType: "dmn-runner-output",
                minWidth: columnMinWidth,
              },
            ],
          },
        ];
      }
      // Lists
      if (Array.isArray(result)) {
        const parentLabel = outputProperties?.name ?? "";
        const parentDataType = outputProperties?.dataType ?? DmnBuiltInDataType.Undefined;
        const parentColumnMinWidth = getDefaultDmnRunnerOutputColumnWidth(parentLabel, parentDataType);
        return [
          {
            originalId: `${outputProperties?.name}-${generateUuid()}`,
            cssClasses: decisionId === openedBoxedExpressionEditorNodeId ? "runner-column-highlight" : "",
            headerCellElementExtension: openBoxedExpressionHeaderButton({ decisionId, decisionName }),
            label: parentLabel,
            accessor: (`output-array-parent-${outputProperties?.name}-` + generateUuid()) as any,
            dataType: parentDataType,
            isRowIndexColumn: false,
            groupType: "dmn-runner-output",
            columns: result.map((entry, entryIndex) => {
              const label = `[${entryIndex}]`;
              const dataType = DmnBuiltInDataType.Undefined;
              const columnMinWidth = getDefaultDmnRunnerOutputColumnWidth(label, dataType);
              return {
                originalId: `${entryIndex}-${generateUuid()}`,
                label: label,
                accessor: (`output-array-${entryIndex}-` + generateUuid()) as any,
                dataType: dataType,
                groupType: "dmn-runner-output",
                isRowIndexColumn: false,
                minWidth: Math.max(parentColumnMinWidth / result.length, columnMinWidth),
              };
            }),
          },
        ];
      }

      // Structures
      if (typeof result === "object") {
        const parentLabel = outputProperties?.name ?? "";
        const parentDataType = outputProperties?.dataType ?? DmnBuiltInDataType.Undefined;
        const parentColumnMinWidth = getDefaultDmnRunnerOutputColumnWidth(parentLabel, parentDataType);
        return [
          {
            originalId: `${outputProperties?.name}-${generateUuid()}`,
            cssClasses: decisionId === openedBoxedExpressionEditorNodeId ? "runner-column-highlight" : "",
            headerCellElementExtension: openBoxedExpressionHeaderButton({ decisionId, decisionName }),
            label: parentLabel,
            accessor: (`output-object-parent-${outputProperties?.name}-` + generateUuid()) as any,
            dataType: parentDataType,
            isRowIndexColumn: false,
            groupType: "dmn-runner-output",
            columns: deepFlattenObjectColumn(result, outputProperties?.properties, undefined, parentColumnMinWidth),
          },
        ];
      }
      return [] as ReactTable.Column<ROWTYPE>[];
    });
  }, [
    deepFlattenObjectColumn,
    getDefaultDmnRunnerOutputColumnWidth,
    openBoxedExpressionHeaderButton,
    openedBoxedExpressionEditorNodeId,
    outputsPropertiesMap,
    results,
  ]);

  const beeTableRows = useMemo<ROWTYPE[]>(() => {
    return (results ?? []).map((decisionResult, rowIndex) => {
      const rowValues = decisionResult?.flatMap(({ result, decisionName }): DmnEvaluationResult[] => {
        // Get the header column with the same decisionName of the decisionResult
        const headerColumn = beeTableColumns.find((column) => {
          if (column.label === "") {
            return (column.columns?.findIndex((subHeader) => subHeader.label === decisionName) ?? -1) > -1;
          }
          return column.label === decisionName;
        });
        if (!headerColumn) {
          return [];
        }

        let columnResults: Record<string, any>;
        if (result !== null && !Array.isArray(result) && typeof result === "object") {
          columnResults = deepFlattenObjectRow(result);
        } else {
          if (headerColumn.dataType === "context") {
            columnResults = { context: getRowValue(result) };
          } else {
            columnResults = { [`${decisionName}`]: getRowValue(result) };
          }
        }

        return headerColumn.columns?.map((column) => columnResults[column.label] as DmnEvaluationResult) ?? [];
      }, []);

      return getColumnsAtLastLevel(beeTableColumns).reduce((tableRow: any, column, columnIndex) => {
        tableRow[column.accessor] = rowValues?.[columnIndex] ?? EMPTY_SYMBOL;
        tableRow.id = uuid + "-" + rowIndex;
        return tableRow;
      }, {});
    });
  }, [results, beeTableColumns, getRowValue, deepFlattenObjectRow, uuid]);

  const getColumnKey = useCallback((column: ReactTable.ColumnInstance<ROWTYPE>) => {
    return column.originalId ?? column.id;
  }, []);

  const getRowKey = useCallback((row: ReactTable.Row<ROWTYPE>) => {
    return row.original.id;
  }, []);

  const allowedOperations = useCallback((conditions: BeeTableContextMenuAllowedOperationsConditions) => {
    return [BeeTableOperation.SelectionCopy];
  }, []);

  return (
    <StandaloneBeeTable
      scrollableParentRef={scrollableParentRef}
      allowedOperations={allowedOperations}
      getColumnKey={getColumnKey}
      getRowKey={getRowKey}
      tableId={id}
      isEditableHeader={false}
      headerLevelCountForAppendingRowIndexColumn={1}
      headerVisibility={BeeTableHeaderVisibility.AllLevels}
      operationConfig={beeTableOperationConfig}
      columns={beeTableColumns}
      rows={beeTableRows}
      isReadOnly={true}
      enableKeyboardNavigation={true}
      shouldRenderRowIndexColumn={false}
      shouldShowRowsInlineControls={false}
      shouldShowColumnsInlineControls={false}
      resizerStopBehavior={ResizerStopBehavior.SET_WIDTH_ALWAYS}
    />
  );
}
