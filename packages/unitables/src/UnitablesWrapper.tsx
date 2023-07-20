/*
 * Copyright 2023 Red Hat, Inc. and/or its affiliates.
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
import { useEffect, useRef } from "react";
import { Unitables, UnitablesProps } from "./Unitables";
import { UnitablesContextProvider } from "./UnitablesContextProvider";
import { EmptyState, EmptyStateBody, EmptyStateIcon } from "@patternfly/react-core/dist/js/components/EmptyState";
import { ExclamationIcon } from "@patternfly/react-icons/dist/js/icons/exclamation-icon";
import { Text, TextContent } from "@patternfly/react-core/dist/js/components/Text";
import { ErrorBoundary } from "@kie-tools/dmn-runner/dist/ErrorBoundary";

export function UnitablesWrapper(props: UnitablesProps) {
  const inputErrorBoundaryRef = useRef<ErrorBoundary>(null);

  // Resets the ErrorBoundary everytime the FormSchema is updated
  useEffect(() => {
    inputErrorBoundaryRef.current?.reset();
  }, [props.jsonSchemaBridge]);

  return (
    <UnitablesContextProvider rowsInputs={props.rows}>
      {props.error ? (
        <InputError />
      ) : (
        <ErrorBoundary ref={inputErrorBoundaryRef} setHasError={props.setError} error={<InputError />}>
          <Unitables {...props} />
        </ErrorBoundary>
      )}
    </UnitablesContextProvider>
  );
}

function InputError() {
  return (
    <div style={{ width: "50vw" }}>
      <EmptyState>
        <EmptyStateIcon icon={ExclamationIcon} />
        <TextContent>
          <Text component={"h2"}>Error</Text>
        </TextContent>
        <EmptyStateBody>
          <p>An error has happened while trying to show your inputs</p>
        </EmptyStateBody>
      </EmptyState>
    </div>
  );
}
