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
import { useEffect, useMemo, useRef, useState } from "react";
import { ErrorBoundary } from "../reactExt/ErrorBoundary";
import { useDmnRunnerState } from "./DmnRunnerContext";
import { useOnlineI18n } from "../i18n";
import { EmptyState, EmptyStateBody, EmptyStateIcon } from "@patternfly/react-core/dist/js/components/EmptyState";
import { I18nWrapped } from "@kie-tools-core/i18n/dist/react-components";
import { ExclamationTriangleIcon } from "@patternfly/react-icons/dist/js/icons/exclamation-triangle-icon";
import { Text, TextContent } from "@patternfly/react-core/dist/js/components/Text";

const ISSUES_URL = "https://github.com/apache/incubator-kie-issues/issues";

export function DmnRunnerErrorBoundary({ children }: React.PropsWithChildren<{}>) {
  const [_, setDmnRunnerError] = useState<boolean>(false);
  const [errorBoundaryRef, setErrorBoundaryRef] = useState<ErrorBoundary | null>(null);
  const { mode, isExpanded, jsonSchema } = useDmnRunnerState();
  const { i18n } = useOnlineI18n();

  useEffect(() => {
    errorBoundaryRef?.reset();
  }, [errorBoundaryRef, jsonSchema, isExpanded, mode]);

  const errorMessage = useMemo(
    () => (
      <div>
        <EmptyState>
          <EmptyStateIcon icon={ExclamationTriangleIcon} />
          <TextContent>
            <Text component={"h2"}>{i18n.dmnRunner.error.title}</Text>
          </TextContent>
          <EmptyStateBody>
            <TextContent>{i18n.dmnRunner.error.explanation}</TextContent>
            <br />
            <TextContent>
              <I18nWrapped
                components={{
                  jira: (
                    <a href={ISSUES_URL} target={"_blank"} rel={"noopener noreferrer"}>
                      {ISSUES_URL}
                    </a>
                  ),
                }}
              >
                {i18n.dmnRunner.error.message}
              </I18nWrapped>
            </TextContent>
          </EmptyStateBody>
        </EmptyState>
      </div>
    ),
    [i18n]
  );

  return (
    <>
      <ErrorBoundary ref={setErrorBoundaryRef} error={errorMessage} setHasError={setDmnRunnerError}>
        {children}
      </ErrorBoundary>
    </>
  );
}
