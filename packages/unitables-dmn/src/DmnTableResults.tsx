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
import { BeeTableWrapper } from "@kie-tools/unitables/dist/bee";
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

interface Props {
  i18n: DmnUnitablesI18n;
  results?: Array<DecisionResult[] | undefined>;
  jsonSchemaBridge: DmnUnitablesJsonSchemaBridge;
}

export function DmnTableResults({ i18n, results, jsonSchemaBridge }: Props) {
  const outputUid = useMemo(() => nextId(), []);
  const outputErrorBoundaryRef = useRef<ErrorBoundary>(null);
  const [outputError, setOutputError] = useState<boolean>(false);

  const { outputs } = useDmnBoxedOutputs(jsonSchemaBridge, results);

  useEffect(() => {
    outputErrorBoundaryRef.current?.reset();
  }, [outputs]);

  const config = useMemo(() => {
    return {
      type: "outputs" as const,
      rows: (results ?? []).map((result) => ({
        outputEntries: (result ?? []).map(({ result }) => {
          return result as string; // FIXME: Tiago -> This `string` here is absolutely wrong.
        }),
      })),
      outputs,
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
          <BeeTableWrapper i18n={i18n} config={config} id={outputUid} key={key} />
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
