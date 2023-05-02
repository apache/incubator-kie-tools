/*
 * Copyright 2022 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import * as React from "react";
import { ErrorBoundary } from "@kie-tools/dmn-runner/dist/ErrorBoundary";
import { EmptyState, EmptyStateBody, EmptyStateIcon } from "@patternfly/react-core/dist/js/components/EmptyState";
import { Text, TextContent } from "@patternfly/react-core/dist/js/components/Text";
import { CubeIcon } from "@patternfly/react-icons/dist/js/icons/cube-icon";
import { ExclamationIcon } from "@patternfly/react-icons/dist/js/icons/exclamation-icon";
import { useCallback, useEffect, useMemo, useRef, useState } from "react";
import nextId from "react-id-generator";
import { OutputFields, useDmnRunnerOutputs as useDmnRunnerOutputs } from "./DmnRunnerOutputs";
import { DmnUnitablesI18n } from "./i18n";
import { DmnUnitablesJsonSchemaBridge } from "./uniforms/DmnUnitablesJsonSchemaBridge";
import * as ReactTable from "react-table";
import {
  BeeTableHeaderVisibility,
  BeeTableOperationConfig,
  DmnBuiltInDataType,
  generateUuid,
} from "@kie-tools/boxed-expression-component/dist/api";
import { getColumnsAtLastLevel } from "@kie-tools/boxed-expression-component/dist/table/BeeTable";
import { StandaloneBeeTable } from "@kie-tools/boxed-expression-component/dist/table/BeeTable/StandaloneBeeTable";
import { UnitablesColumnType } from "@kie-tools/unitables/dist/UnitablesTypes";
import { BoxedExpressionEditorI18n } from "@kie-tools/boxed-expression-component/dist/i18n";
import "@kie-tools/boxed-expression-component/dist/@types/react-table";
import { ResizerStopBehavior } from "@kie-tools/boxed-expression-component/dist/resizing/ResizingWidthsContext";
import "./DmnRunnerOutputsTable.css";
import { DecisionResult, DmnEvaluationResult } from "@kie-tools/extended-services-api";

interface Props {
  i18n: DmnUnitablesI18n;
  results: Array<DecisionResult[] | undefined> | undefined;
  jsonSchemaBridge: DmnUnitablesJsonSchemaBridge;
  scrollableParentRef: React.RefObject<HTMLElement>;
}

export function DmnRunnerOutputsTable({ i18n, jsonSchemaBridge, results, scrollableParentRef }: Props) {
  const outputUid = useMemo(() => nextId(), []);
  const outputErrorBoundaryRef = useRef<ErrorBoundary>(null);
  const [outputError, setOutputError] = useState<boolean>(false);

  const { outputs, outputTypeMap } = useDmnRunnerOutputs(jsonSchemaBridge, results);

  useEffect(() => {
    outputErrorBoundaryRef.current?.reset();
  }, [outputs]);

  const rows = useMemo(
    () =>
      (results ?? []).map((result) => ({
        outputEntries: (result ?? []).map(({ result, decisionName }) => ({ result, decisionName })),
      })),
    [results]
  );

  return (
    <>
      {outputError ? (
        outputError
      ) : outputs.length > 0 ? (
        <ErrorBoundary ref={outputErrorBoundaryRef} setHasError={setOutputError} error={<OutputError />}>
          <OutputsBeeTable
            scrollableParentRef={scrollableParentRef}
            i18n={i18n}
            outputs={outputs}
            outputTypeMap={outputTypeMap}
            rows={rows}
            id={outputUid}
          />
        </ErrorBoundary>
      ) : (
        <EmptyState>
          <EmptyStateIcon icon={CubeIcon} />
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
        <EmptyStateIcon icon={ExclamationIcon} />
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

export const DMN_RUNNER_OUTPUT_COLUMN_MIN_WIDTH = 150;

interface ROWTYPE {
  id: string;
}

const EMPTY_SYMBOL = "";

interface OutputsTableProps {
  id: string;
  i18n: BoxedExpressionEditorI18n;
  rows: { outputEntries: { result: DmnEvaluationResult; decisionName: string }[] }[];
  outputs?: UnitablesColumnType[];
  outputTypeMap: Map<string, OutputFields> | undefined;
  scrollableParentRef: React.RefObject<HTMLElement>;
}

function OutputsBeeTable({ id, i18n, outputs, outputTypeMap, rows, scrollableParentRef }: OutputsTableProps) {
  const beeTableOperationConfig = useMemo<BeeTableOperationConfig>(
    () => [
      {
        group: i18n.rows,
        items: [],
      },
    ],
    [i18n]
  );

  const uuid = useMemo(() => {
    return generateUuid();
  }, []);

  const deepFlattenObjectColumn = useCallback(
    (myObject: Record<string, any>, parentKey?: string): ReactTable.Column<ROWTYPE>[] => {
      return Object.entries(myObject).flatMap(([myObjectKey, value]) => {
        if (value !== null && typeof value === "object") {
          const myKey = parentKey ? `${parentKey}-${myObjectKey}` : myObjectKey;
          return deepFlattenObjectColumn(value, myKey);
        }

        const label = parentKey ? `${parentKey}-${myObjectKey}` : myObjectKey;
        const myObjectProperties = outputTypeMap?.get(myObjectKey);
        const dataType = myObjectProperties ? myObjectProperties.dataType : DmnBuiltInDataType.Any;

        return {
          originalId: label + generateUuid(),
          label,
          accessor: (`output-object-${label}` + generateUuid()) as any,
          dataType,
          isRowIndexColumn: false,
          groupType: "dmn-runner-output",
          width: DMN_RUNNER_OUTPUT_COLUMN_MIN_WIDTH,
          minWidth: DMN_RUNNER_OUTPUT_COLUMN_MIN_WIDTH,
        };
      });
    },
    [outputTypeMap]
  );

  const deepFlattenObjectRow = useCallback(
    (myObject: Record<string, any>, order?: Record<string, any>, parentKey?: string): Record<string, any>[] => {
      return Object.entries(myObject).reduce(
        (acc: Record<string, any>[], [myObjectKey, value]) => {
          if (value !== null && typeof value === "object") {
            return [...acc, ...deepFlattenObjectRow(value, order, myObjectKey)];
          }
          const index = order?.[myObjectKey];
          if (index !== undefined) {
            acc[index] = value;
          } else {
            acc = [...acc, value];
          }
          return acc;
        },
        order ? Array(Object.keys(order).length).fill("null") : []
      );
    },
    []
  );

  const beeTableColumns = useMemo<ReactTable.Column<ROWTYPE>[]>(() => {
    return (rows?.[0]?.outputEntries ?? []).flatMap((outputEntry, outputIndex: number) => {
      const output: UnitablesColumnType | undefined = outputs?.[outputIndex];

      // Primitives and null;
      if (
        typeof outputEntry.result === "string" ||
        typeof outputEntry.result === "number" ||
        typeof outputEntry.result === "boolean" ||
        outputEntry.result === null
      ) {
        return [
          {
            originalId: `parent-${output?.name}-` + generateUuid(),
            label: "",
            accessor: (`output-parent-${output?.name}-` + generateUuid()) as any,
            dataType: undefined as any,
            isRowIndexColumn: false,
            groupType: "dmn-runner-output",
            minWidth: DMN_RUNNER_OUTPUT_COLUMN_MIN_WIDTH,
            columns: [
              {
                originalId: `${output?.name}-` + generateUuid(),
                label: output?.name ?? "",
                accessor: (`output-${output?.name}-` + generateUuid()) as any,
                dataType: output?.dataType ?? DmnBuiltInDataType.Undefined,
                isRowIndexColumn: false,
                groupType: "dmn-runner-output",
                width: DMN_RUNNER_OUTPUT_COLUMN_MIN_WIDTH,
                minWidth: DMN_RUNNER_OUTPUT_COLUMN_MIN_WIDTH,
              },
            ],
          },
        ];
      }
      // Lists
      if (Array.isArray(outputEntry.result)) {
        return [
          {
            originalId: `${output?.name}-` + generateUuid(),
            label: `${output?.name}`,
            accessor: (`output-array-parent-${output?.name}-` + generateUuid()) as any,
            dataType: output?.dataType ?? DmnBuiltInDataType.Undefined,
            isRowIndexColumn: false,
            groupType: "dmn-runner-output",
            columns: outputEntry.result.map((entry, entryIndex) => ({
              originalId: `${entryIndex}-` + generateUuid(),
              label: `[${entryIndex}]`,
              accessor: (`output-array-${entryIndex}-` + generateUuid()) as any,
              dataType: undefined as any,
              groupType: "dmn-runner-output",
              isRowIndexColumn: false,
              width: DMN_RUNNER_OUTPUT_COLUMN_MIN_WIDTH,
              minWidth: DMN_RUNNER_OUTPUT_COLUMN_MIN_WIDTH,
            })),
          },
        ];
      }

      // Contexts/Structures
      if (output?.dataType === "context") {
        // collect results from all rows;
        const collectedOutputs = rows.flatMap((row) =>
          row.outputEntries
            .filter((outputEntry) => outputEntry.decisionName === output.joinedName)
            .flatMap((outputEntry) => outputEntry.result)
        );
        let contextIndex = 0;
        return [
          {
            originalId: `${output?.name}-` + generateUuid(),
            label: output?.name ?? "",
            accessor: (`output-object-parent-${output?.name}-` + generateUuid()) as any,
            dataType: output?.dataType,
            isRowIndexColumn: false,
            groupType: "dmn-runner-output",
            minWidth: DMN_RUNNER_OUTPUT_COLUMN_MIN_WIDTH,
            columns: collectedOutputs
              .flatMap((collectedOutput) => {
                if (collectedOutput !== null && typeof collectedOutput === "object") {
                  return deepFlattenObjectColumn(collectedOutput);
                }
                return {
                  originalId: "context-" + generateUuid(),
                  label: "context",
                  accessor: (`output-context-` + generateUuid()) as any,
                  dataType: output?.dataType ?? DmnBuiltInDataType.Any,
                  isRowIndexColumn: false,
                  groupType: "dmn-runner-output",
                  width: DMN_RUNNER_OUTPUT_COLUMN_MIN_WIDTH,
                  minWidth: DMN_RUNNER_OUTPUT_COLUMN_MIN_WIDTH,
                };
              })
              .reduce((acc: ReactTable.Column<ROWTYPE>[], column) => {
                if (acc.find((e) => e.label === column.label)) {
                  return acc;
                }
                const outputType = outputTypeMap?.get(output?.name);
                if (outputType) {
                  // Save order;
                  outputType.infos = { ...(outputType.infos ?? {}), [`${column.label}`]: contextIndex };
                  contextIndex++;
                  outputTypeMap?.set(output?.name, outputType);
                }
                return [...acc, column];
              }, []),
          },
        ];
      }

      // Structures
      if (typeof outputEntry.result === "object") {
        return [
          {
            originalId: `${output?.name}-` + generateUuid(),
            label: output?.name ?? "",
            accessor: (`output-object-parent-${output?.name}-` + generateUuid()) as any,
            dataType: output?.dataType ?? DmnBuiltInDataType.Undefined,
            isRowIndexColumn: false,
            groupType: "dmn-runner-output",
            minWidth: DMN_RUNNER_OUTPUT_COLUMN_MIN_WIDTH,
            columns: deepFlattenObjectColumn(outputEntry.result),
          },
        ];
      }
      return [] as ReactTable.Column<ROWTYPE>[];
    });
  }, [deepFlattenObjectColumn, outputTypeMap, outputs, rows]);

  const beeTableRows = useMemo<ROWTYPE[]>(() => {
    return rows.map((row, rowIndex) => {
      const rowArray = row.outputEntries.reduce(
        (acc: DmnEvaluationResult[], { result, decisionName }, index): DmnEvaluationResult[] => {
          const outputType = outputTypeMap?.get(decisionName);
          // use type;
          if (result === undefined || outputType === undefined) {
            return acc;
          } else if (outputType.dataType === "context") {
            const contextDefaultIndex: number | undefined = outputType.infos?.["context"];
            if (result === null) {
              const tempArray = [];
              if (contextDefaultIndex) {
                tempArray[contextDefaultIndex] = "null";
                return [...acc, ...tempArray];
              } else {
                return [...acc, "null"];
              }
            } else if (typeof result === "object") {
              return [
                ...acc,
                ...deepFlattenObjectRow(result, outputType.infos).map((element) =>
                  JSON.stringify(element, null, 2).replace(/"([^"]+)":/g, "$1:")
                ),
              ];
            } else {
              const tempArray = [];
              if (contextDefaultIndex) {
                tempArray[contextDefaultIndex] = JSON.stringify(result);
                return [...acc, ...tempArray];
              } else {
                return [...acc, JSON.stringify(result)];
              }
            }
            //
          } else if (result === null) {
            return [...acc, "null"];
          } else if (Array.isArray(result)) {
            return [...acc, ...result.map((element) => JSON.stringify(element, null, 2).replace(/"([^"]+)":/g, "$1:"))];
          } else if (typeof result === "object") {
            return [
              ...acc,
              ...deepFlattenObjectRow(result).map((element) =>
                JSON.stringify(element, null, 2).replace(/"([^"]+)":/g, "$1:")
              ),
            ];
          } else {
            return [...acc, JSON.stringify(result)];
          }
        },
        []
      );

      return getColumnsAtLastLevel(beeTableColumns).reduce((tableRow: any, column, columnIndex) => {
        tableRow[column.accessor] = rowArray[columnIndex] || EMPTY_SYMBOL;
        tableRow.id = uuid + "-" + rowIndex;
        return tableRow;
      }, {});
    });
  }, [rows, beeTableColumns, outputTypeMap, deepFlattenObjectRow, uuid]);

  const getColumnKey = useCallback((column: ReactTable.ColumnInstance<ROWTYPE>) => {
    return column.originalId ?? column.id;
  }, []);

  const getRowKey = useCallback((row: ReactTable.Row<ROWTYPE>) => {
    return row.original.id;
  }, []);

  return (
    <StandaloneBeeTable
      scrollableParentRef={scrollableParentRef}
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
