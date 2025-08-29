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
import { useEffect, useRef } from "react";
import { Unitables, UnitablesProps } from "./Unitables";
import { UnitablesContextProvider } from "./UnitablesContextProvider";
import {
  EmptyState,
  EmptyStateBody,
  EmptyStateIcon,
  EmptyStateHeader,
} from "@patternfly/react-core/dist/js/components/EmptyState";
import { ExclamationIcon } from "@patternfly/react-icons/dist/js/icons/exclamation-icon";
import { Text, TextContent } from "@patternfly/react-core/dist/js/components/Text";
import { ErrorBoundary } from "@kie-tools/dmn-runner/dist/ErrorBoundary";
import { UnitablesI18n } from "./i18n";

export function UnitablesWrapper(props: UnitablesProps) {
  const inputErrorBoundaryRef = useRef<ErrorBoundary>(null);

  // Resets the ErrorBoundary everytime the FormSchema is updated
  useEffect(() => {
    inputErrorBoundaryRef.current?.reset();
  }, [props.jsonSchemaBridge]);

  return (
    <UnitablesContextProvider rowsInputs={props.rows}>
      {props.error ? (
        <InputError i18n={props.i18n} />
      ) : (
        <ErrorBoundary
          ref={inputErrorBoundaryRef}
          setHasError={props.setError}
          error={<InputError i18n={props.i18n} />}
        >
          <Unitables {...props} />
        </ErrorBoundary>
      )}
    </UnitablesContextProvider>
  );
}

function InputError(props: { i18n: UnitablesI18n }) {
  return (
    <div style={{ width: "50vw" }}>
      <EmptyState>
        <EmptyStateHeader icon={<EmptyStateIcon icon={ExclamationIcon} />} />
        <TextContent>
          <Text component={"h2"}>{props.i18n.error}</Text>
        </TextContent>
        <EmptyStateBody>
          <p>{props.i18n.errorMessage}</p>
        </EmptyStateBody>
      </EmptyState>
    </div>
  );
}
