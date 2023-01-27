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
import { ErrorBoundary } from "@kie-tools/form";
import { EmptyState, EmptyStateBody, EmptyStateIcon } from "@patternfly/react-core/dist/js/components/EmptyState";
import { Text, TextContent } from "@patternfly/react-core/dist/js/components/Text";
import { CubeIcon } from "@patternfly/react-icons/dist/js/icons/cube-icon";
import { ExclamationIcon } from "@patternfly/react-icons/dist/js/icons/exclamation-icon";
import { useCallback, useEffect, useMemo, useRef, useState } from "react";
import nextId from "react-id-generator";
import { useDmnRunnerOutputs as useDmnRunnerOutputs } from "./DmnRunnerOutputs";
import { DecisionResult } from "./DmnTypes";
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

  const { outputs } = useDmnRunnerOutputs(jsonSchemaBridge, results);

  useEffect(() => {
    outputErrorBoundaryRef.current?.reset();
  }, [outputs]);

  const rows = useMemo(
    () =>
      (results ?? []).map((result) => ({
        outputEntries: (result ?? []).map(({ result }) => {
          return result as string; // FIXME: Tiago -> This `string` here is absolutely wrong.
        }),
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

type ROWTYPE = any; // FIXME: Tiago

const DASH_SYMBOL = "-";
const EMPTY_SYMBOL = "";

export interface OutputsTableProps {
  id: string;
  i18n: BoxedExpressionEditorI18n;
  rows: { outputEntries: string[] }[];
  outputs?: UnitablesColumnType[];
  scrollableParentRef: React.RefObject<HTMLElement>;
}

export function OutputsBeeTable({ id, i18n, outputs, rows, scrollableParentRef }: OutputsTableProps) {
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

  const beeTableColumns = useMemo<ReactTable.Column<ROWTYPE>[]>(() => {
    return (rows?.[0]?.outputEntries ?? []).flatMap((outputEntry, outputIndex) => {
      if (outputEntry === null || outputEntry === undefined) {
        return [];
      }

      // Lists
      const output = outputs?.[outputIndex];
      if (Array.isArray(outputEntry)) {
        return [
          {
            originalId: uuid + `${output?.name}}`,
            label: `${output?.name}`,
            accessor: `output-array-parent-${output?.name}` as any,
            dataType: output?.dataType ?? DmnBuiltInDataType.Undefined,
            isRowIndexColumn: false,
            groupType: "dmn-runner-output",
            width: undefined,
            columns: outputEntry.map((entry, entryIndex) => ({
              originalId: uuid + `${entryIndex}`,
              label: `[${entryIndex}]`,
              accessor: `output-array-${entryIndex}` as any,
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
      else if (typeof outputEntry === "object") {
        return [
          {
            originalId: uuid + `${output?.name}`,
            label: output?.name ?? "",
            accessor: `output-object-parent-${output?.name}` as any,
            dataType: output?.dataType ?? DmnBuiltInDataType.Undefined,
            isRowIndexColumn: false,
            groupType: "dmn-runner-output",
            width: undefined,
            minWidth: DMN_RUNNER_OUTPUT_COLUMN_MIN_WIDTH,
            columns: Object.keys(outputEntry).map((entryKey) => {
              const filteredOutputs = output?.insideProperties?.find((property) =>
                Object.values(property).find((value) => value === entryKey)
              );
              return {
                originalId: uuid + `${entryKey}`,
                label: entryKey,
                accessor: `output-object-${entryKey}` as any,
                dataType: filteredOutputs?.dataType ?? DmnBuiltInDataType.Undefined,
                isRowIndexColumn: false,
                groupType: "dmn-runner-output",
                width: DMN_RUNNER_OUTPUT_COLUMN_MIN_WIDTH,
                minWidth: DMN_RUNNER_OUTPUT_COLUMN_MIN_WIDTH,
              };
            }),
          },
        ];
      }
      // Primitives
      else {
        return [
          {
            originalId: uuid + `-parent-${output?.name}`,
            label: "",
            accessor: `output-parent-${output?.name}` as any,
            dataType: undefined as any,
            isRowIndexColumn: false,
            groupType: "dmn-runner-output",
            width: undefined,
            minWidth: DMN_RUNNER_OUTPUT_COLUMN_MIN_WIDTH,
            columns: [
              {
                originalId: uuid + `${output?.name}`,
                label: output?.name ?? "",
                accessor: `output-${output?.name}` as any,
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
    });
  }, [outputs, rows, uuid]);

  const beeTableRows = useMemo<ROWTYPE[]>(() => {
    return rows.map((row, rowIndex) => {
      const rowArray = row.outputEntries.reduce((acc, entry) => {
        if (entry == null || entry === undefined) {
          return acc;
        } else if (Array.isArray(entry)) {
          return [...acc, ...entry.map((element) => JSON.stringify(element, null, 2).replace(/"([^"]+)":/g, "$1:"))];
        } else if (typeof entry === "object") {
          return [
            ...acc,
            ...Object.values(entry).map((element) => JSON.stringify(element, null, 2).replace(/"([^"]+)":/g, "$1:")),
          ];
        } else {
          return [...acc, JSON.stringify(entry)];
        }
      }, []);

      return getColumnsAtLastLevel(beeTableColumns).reduce((tableRow: any, column, columnIndex) => {
        tableRow[column.accessor] = rowArray[columnIndex] || EMPTY_SYMBOL;
        tableRow.id = uuid + "-" + rowIndex;
        return tableRow;
      }, {});
    });
  }, [rows, beeTableColumns, uuid]);

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
      headerLevelCount={1}
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
