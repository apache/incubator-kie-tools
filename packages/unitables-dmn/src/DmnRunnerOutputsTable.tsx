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
import { useEffect, useMemo, useRef, useState } from "react";
import nextId from "react-id-generator";
import { useDmnBoxedOutputs } from "./DmnBoxedOutputs";
import { DecisionResult } from "./DmnTypes";
import { DmnUnitablesI18n } from "./i18n";
import { DmnUnitablesJsonSchemaBridge } from "./uniforms/DmnUnitablesJsonSchemaBridge";

import * as ReactTable from "react-table";
import {
  BeeTableHeaderVisibility,
  BeeTableOperationConfig,
  DmnBuiltInDataType,
} from "@kie-tools/boxed-expression-component/dist/api";
import { getColumnsAtLastLevel } from "@kie-tools/boxed-expression-component/dist/table/BeeTable";
import { StandaloneBeeTable } from "@kie-tools/boxed-expression-component/dist/table/BeeTable/StandaloneBeeTable";
import { UnitablesCell } from "@kie-tools/unitables/dist/UnitablesTypes";
import { BoxedExpressionEditorI18n } from "@kie-tools/boxed-expression-component/dist/i18n";
import "@kie-tools/boxed-expression-component/dist/@types/react-table";
import { ResizerStopBehavior } from "@kie-tools/boxed-expression-component/dist/resizing/ResizingWidthsContext";

interface Props {
  i18n: DmnUnitablesI18n;
  results?: Array<DecisionResult[] | undefined>;
  jsonSchemaBridge: DmnUnitablesJsonSchemaBridge;
}

export function DmnRunnerOutputsTable({ i18n, results, jsonSchemaBridge }: Props) {
  const outputUid = useMemo(() => nextId(), []);
  const outputErrorBoundaryRef = useRef<ErrorBoundary>(null);
  const [outputError, setOutputError] = useState<boolean>(false);

  const { outputs } = useDmnBoxedOutputs(jsonSchemaBridge, results);

  useEffect(() => {
    outputErrorBoundaryRef.current?.reset();
  }, [outputs]);

  const config = useMemo(() => {
    return {
      outputs,
      rows: (results ?? []).map((result) => ({
        outputEntries: (result ?? []).map(({ result }) => {
          return result as string; // FIXME: Tiago -> This `string` here is absolutely wrong.
        }),
      })),
    };
  }, [outputs, results]);

  // FIXME: Tiago -> Weird error happening without this. Column headers grow in size inexplicably.
  const key = useMemo(() => {
    console.info(JSON.stringify(config));
    return Date.now() + config.outputs.length;
  }, [config]);

  return (
    <>
      {outputError ? (
        outputError
      ) : config.outputs.length > 0 ? (
        <ErrorBoundary ref={outputErrorBoundaryRef} setHasError={setOutputError} error={<OutputError />}>
          <OutputsBeeTable i18n={i18n} config={config} id={outputUid} key={key} />
        </ErrorBoundary>
      ) : (
        <EmptyState>
          <EmptyStateIcon icon={CubeIcon} />
          <TextContent>
            <Text component={"h2"}>Without Responses Yet</Text>
          </TextContent>
          <EmptyStateBody>
            <TextContent>Add decision nodes and fill the input nodes!</TextContent>
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
          <p>An error has happened while trying to show your outputs</p>
        </EmptyStateBody>
      </EmptyState>
    </div>
  );
}

export const CELL_MINIMUM_WIDTH = 150;

type ROWTYPE = any; // FIXME: Tiago

const DASH_SYMBOL = "-";
const EMPTY_SYMBOL = "";

export interface OutputsTableProps {
  id: string;
  i18n: BoxedExpressionEditorI18n;
  config: {
    rows: {
      outputEntries: string[];
    }[];
    outputs?: UnitablesCell[];
  };
}

export function OutputsBeeTable({ id, i18n, config }: OutputsTableProps) {
  const beeTableOperationConfig = useMemo<BeeTableOperationConfig>(
    () => [
      {
        group: i18n.rows,
        items: [],
      },
    ],
    [i18n]
  );

  const beeTableColumns = useMemo<ReactTable.Column<ROWTYPE>[]>(() => {
    return (config.rows?.[0]?.outputEntries ?? []).flatMap((outputEntry, outputIndex) => {
      // Lists
      const output = config.outputs?.[outputIndex];
      if (Array.isArray(outputEntry)) {
        console.info("TIAGO-A:" + outputEntry);
        return [
          {
            label: `${output?.name}`,
            accessor: `output-array-parent-${output?.name}` as any,
            dataType: output?.dataType ?? DmnBuiltInDataType.Undefined,
            isRowIndexColumn: false,
            width: undefined,
            columns: outputEntry.map((entry, entryIndex) => ({
              label: `[${entryIndex}]`,
              accessor: `output-array-${entryIndex}` as any,
              dataType: undefined as any,
              isRowIndexColumn: false,
              width: CELL_MINIMUM_WIDTH,
              minWidth: CELL_MINIMUM_WIDTH,
            })),
          },
        ];
      }
      // Contexts/Structures
      else if (typeof outputEntry === "object") {
        console.info("TIAGO-B:" + outputEntry);
        return [
          {
            label: output?.name ?? "",
            accessor: `output-object-parent-${output?.name}` as any,
            dataType: output?.dataType ?? DmnBuiltInDataType.Undefined,
            isRowIndexColumn: false,
            width: undefined,
            minWidth: CELL_MINIMUM_WIDTH,
            columns: Object.keys(outputEntry).map((entryKey) => {
              const filteredOutputs = output?.insideProperties?.find((property) =>
                Object.values(property).find((value) => value === entryKey)
              );
              return {
                label: entryKey,
                accessor: `output-object-${entryKey}` as any,
                dataType: filteredOutputs?.dataType ?? DmnBuiltInDataType.Undefined,
                isRowIndexColumn: false,
                width: CELL_MINIMUM_WIDTH,
                minWidth: CELL_MINIMUM_WIDTH,
              };
            }),
          },
        ];
      }
      // Primitives
      else {
        console.info("TIAGO-C:" + outputEntry);
        return [
          {
            label: "",
            accessor: `output-parent-${output?.name}` as any,
            dataType: undefined as any,
            isRowIndexColumn: false,
            width: undefined,
            minWidth: CELL_MINIMUM_WIDTH,
            columns: [
              {
                label: output?.name ?? "",
                accessor: `output-${output?.name}` as any,
                dataType: output?.dataType ?? DmnBuiltInDataType.Undefined,
                isRowIndexColumn: false,
                width: CELL_MINIMUM_WIDTH,
                minWidth: CELL_MINIMUM_WIDTH,
              },
            ],
          },
        ];
      }
    });
  }, [config]);

  const beeTableRows = useMemo<ROWTYPE[]>(() => {
    return config.rows.map((row) => {
      const rowArray = row.outputEntries.reduce((acc, entry) => {
        if (Array.isArray(entry)) {
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
        return tableRow;
      }, {});
    });
  }, [config, beeTableColumns]);

  return (
    <StandaloneBeeTable
      tableId={id}
      isEditableHeader={false}
      headerLevelCount={1}
      headerVisibility={BeeTableHeaderVisibility.AllLevels}
      operationConfig={beeTableOperationConfig}
      columns={beeTableColumns}
      rows={beeTableRows}
      enableKeyboardNavigation={true}
      shouldRenderRowIndexColumn={false}
      shouldShowRowsInlineControls={true}
      shouldShowColumnsInlineControls={false}
      resizerStopBehavior={ResizerStopBehavior.SET_WIDTH_ALWAYS}
    />
  );
}
