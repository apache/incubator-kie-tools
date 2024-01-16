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
import { useDmnEditor } from "./DmnEditorContext";
import { Bullseye } from "@patternfly/react-core/dist/js/layouts/Bullseye";
import { Flex } from "@patternfly/react-core/dist/js/layouts/Flex";
import {
  EmptyState,
  EmptyStateBody,
  EmptyStateIcon,
  EmptyStatePrimary,
} from "@patternfly/react-core/dist/js/components/EmptyState";
import { Title } from "@patternfly/react-core/dist/js/components/Title";
import { Button, ButtonVariant } from "@patternfly/react-core/dist/js/components/Button";
import { FallbackProps } from "react-error-boundary";
import { ClipboardCopy, ClipboardCopyVariant } from "@patternfly/react-core/dist/js/components/ClipboardCopy";
import { ExternalLinkAltIcon } from "@patternfly/react-icons/dist/js/icons/external-link-alt-icon";
import { useCallback, useEffect } from "react";

export function DmnEditorErrorFallback({ error, resetErrorBoundary }: FallbackProps) {
  const { dmnModelBeforeEditingRef, issueTrackerHref } = useDmnEditor();

  const resetToLastWorkingState = useCallback(() => {
    resetErrorBoundary(dmnModelBeforeEditingRef.current);
  }, [dmnModelBeforeEditingRef, resetErrorBoundary]);

  useEffect(() => {
    console.error(error);
  }, [error]);

  return (
    <Flex justifyContent={{ default: "justifyContentCenter" }} style={{ marginTop: "100px" }}>
      <EmptyState style={{ maxWidth: "1280px" }}>
        <EmptyStateIcon icon={() => <div style={{ fontSize: "3em" }}>😕</div>} />
        <Title size={"lg"} headingLevel={"h4"}>
          An unexpected error happened
        </Title>
        <EmptyStateBody>
          This is a bug. Please consider reporting it so the DMN Editor can continue improving. See the details below.
        </EmptyStateBody>
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
        <EmptyStatePrimary>
          <Button variant={ButtonVariant.link} onClick={resetToLastWorkingState}>
            Try undoing last action
          </Button>
          {issueTrackerHref && (
            <a href={issueTrackerHref} target={"_blank"}>
              <Button variant={ButtonVariant.link} icon={<ExternalLinkAltIcon />}>
                File an issue...
              </Button>
            </a>
          )}
        </EmptyStatePrimary>
      </EmptyState>
    </Flex>
  );
}
