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
import { useCallback, useEffect } from "react";
import { Button, ButtonVariant } from "@patternfly/react-core/dist/js/components/Button";
import {
  EmptyState,
  EmptyStateBody,
  EmptyStateIcon,
  EmptyStateActions,
  EmptyStateHeader,
  EmptyStateFooter,
} from "@patternfly/react-core/dist/js/components/EmptyState";
import { ExternalLinkAltIcon } from "@patternfly/react-icons/dist/js/icons/external-link-alt-icon";
import { ClipboardCopy, ClipboardCopyVariant } from "@patternfly/react-core/dist/js/components/ClipboardCopy";
import { Flex } from "@patternfly/react-core/dist/js/layouts/Flex";

import { FallbackProps } from "react-error-boundary";
import { useTestScenarioEditorI18n } from "./i18n";
import { useTestScenarioEditor } from "./TestScenarioEditorContext";

export function TestScenarioEditorErrorFallback({ error, resetErrorBoundary }: FallbackProps) {
  const { i18n } = useTestScenarioEditorI18n();
  const { testScenarioEditorModelBeforeEditingRef, issueTrackerHref } = useTestScenarioEditor();

  const resetToLastWorkingState = useCallback(() => {
    resetErrorBoundary(testScenarioEditorModelBeforeEditingRef.current);
  }, [testScenarioEditorModelBeforeEditingRef, resetErrorBoundary]);

  useEffect(() => {
    console.error(error);
  }, [error]);

  return (
    <Flex justifyContent={{ default: "justifyContentCenter" }} style={{ marginTop: "100px" }}>
      <EmptyState style={{ maxWidth: "1280px" }}>
        <EmptyStateHeader
          titleText={<>{i18n.errorFallBack.title}</>}
          icon={<EmptyStateIcon icon={() => <div style={{ fontSize: "3em" }}>😕</div>} />}
          headingLevel={"h4"}
        />
        <EmptyStateBody>{i18n.errorFallBack.body}</EmptyStateBody>
        <EmptyStateFooter>
          <br />
          <ClipboardCopy
            isReadOnly={true}
            isExpanded={false}
            hoverTip={"Copy"}
            clickTip={"Copied"}
            variant={ClipboardCopyVariant.expansion}
            style={{ textAlign: "left", whiteSpace: "pre-wrap", fontFamily: "monospace" }}
          >
            {JSON.stringify(
              {
                name: error.name,
                message: error.message,
                cause: error.cause,
                stack: error.stack,
              },
              null,
              2
            ).replaceAll("\\n", "\n")}
          </ClipboardCopy>
          <br />
          <EmptyStateActions>
            <Button variant={ButtonVariant.link} onClick={resetToLastWorkingState}>
              {i18n.errorFallBack.lastActionButton}
            </Button>
            {issueTrackerHref && (
              <a href={issueTrackerHref} target={"_blank"}>
                <Button variant={ButtonVariant.link} icon={<ExternalLinkAltIcon />}>
                  {i18n.errorFallBack.fileIssueHref}...
                </Button>
              </a>
            )}
          </EmptyStateActions>
        </EmptyStateFooter>
      </EmptyState>
    </Flex>
  );
}
