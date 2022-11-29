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
import { useCallback, useEffect, useMemo, useRef, useState } from "react";
import { EmptyState, EmptyStateBody, EmptyStateIcon } from "@patternfly/react-core/dist/js/components/EmptyState";
import { ExclamationIcon } from "@patternfly/react-icons/dist/js/icons/exclamation-icon";
import { Text, TextContent } from "@patternfly/react-core/dist/js/components/Text";
import nextId from "react-id-generator";
import { BoxedExpressionEditorContextProvider } from "@kie-tools/boxed-expression-component/dist/components/BoxedExpressionEditor/BoxedExpressionEditorContext";
import { ColumnInstance } from "react-table";
import { CubeIcon } from "@patternfly/react-icons/dist/js/icons/cube-icon";
import { useDmnBoxedOutputs } from "./DmnBoxedOutputs";
import { BeeTableOperation } from "@kie-tools/boxed-expression-component/dist/api";
import { DecisionResult } from "./DmnTypes";
import { DmnUnitablesJsonSchemaBridge } from "./uniforms/DmnUnitablesJsonSchemaBridge";
import { ErrorBoundary } from "@kie-tools/form";
import { CustomTable } from "@kie-tools/unitables/dist/boxed";
import { BoxedExpressionOutputRule, UnitablesClause } from "@kie-tools/unitables";
import { DmnUnitablesI18n } from "./i18n";

interface Props {
  i18n: DmnUnitablesI18n;
  results?: Array<DecisionResult[] | undefined>;
  rowCount: number;
  jsonSchemaBridge: DmnUnitablesJsonSchemaBridge;
  onRowNumberUpdate: (rowQtt: number, tableOperation: BeeTableOperation, rowIndex: number) => void;
}

export function DmnTableResults(props: Props) {
  const outputUid = useMemo(() => nextId(), []);
  const outputErrorBoundaryRef = useRef<ErrorBoundary>(null);
  const outputColumnsCache = useRef<ColumnInstance[]>([]);

  const [outputError, setOutputError] = useState<boolean>(false);

  const { outputs, outputRules, updateOutputCellsWidth } = useDmnBoxedOutputs(
    props.jsonSchemaBridge,
    props.results,
    props.rowCount,
    outputColumnsCache
  );

  const onOutputColumnsUpdate = useCallback(
    (columns: ColumnInstance[]) => {
      outputColumnsCache.current = columns;
      updateOutputCellsWidth(outputs);
    },
    [outputs, updateOutputCellsWidth]
  );

  useEffect(() => {
    outputErrorBoundaryRef.current?.reset();
  }, [outputs]);

  const outputEntriesLength = useMemo(
    () => outputRules.reduce((length, rules) => length + (rules.outputEntries?.length ?? 0), 0),
    [outputRules]
  );

  return (
    <>
      {outputError ? (
        outputError
      ) : outputEntriesLength > 0 ? (
        <ErrorBoundary ref={outputErrorBoundaryRef} setHasError={setOutputError} error={<OutputError />}>
          <BoxedExpressionEditorContextProvider
            expressionDefinition={{}}
            isRunnerTable={true}
            decisionNodeId={outputUid}
            dataTypes={[]}
          >
            <CustomTable
              name={"DMN Runner Output"}
              i18n={props.i18n}
              onColumnsUpdate={onOutputColumnsUpdate}
              output={outputs as UnitablesClause[]}
              rules={outputRules as BoxedExpressionOutputRule[]}
              id={outputUid}
              onRowNumberUpdate={props.onRowNumberUpdate}
            />
          </BoxedExpressionEditorContextProvider>
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
