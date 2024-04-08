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
import React from "react";
import { Button, ButtonVariant } from "@patternfly/react-core/dist/js/components/Button";
import { ClipboardCopy, ClipboardCopyVariant } from "@patternfly/react-core/dist/js/components/ClipboardCopy";
import { EmptyState, EmptyStateBody, EmptyStateIcon } from "@patternfly/react-core/dist/js/components/EmptyState";
import { PageSection } from "@patternfly/react-core/dist/js/components/Page";
import { Text, TextContent, TextVariants } from "@patternfly/react-core/dist/js/components/Text";
import { ExclamationTriangleIcon } from "@patternfly/react-icons/dist/js/icons/exclamation-triangle-icon";
import { useMemo, useState } from "react";
import { Link } from "react-router-dom";
import { WorkflowDefinition } from "@kie-tools/runtime-tools-swf-gateway-api/dist/types";
import { routes } from "../routes";
import { BasePage } from "./BasePage";

export enum ErrorKind {
  APPDATA_JSON = "AppDataJson",
  OPENAPI = "OpenApi",
  WORKFLOW = "Workflow",
}

export type ErrorPageProps = { errors: string[] } & (
  | { kind: ErrorKind.APPDATA_JSON }
  | { kind: ErrorKind.OPENAPI }
  | {
      kind: ErrorKind.WORKFLOW;
      workflowId: WorkflowDefinition["workflowName"];
    }
);

export function ErrorPage(props: ErrorPageProps) {
  const [showDetails, setShowDetails] = useState(false);

  const errorDetails = useMemo(() => props.errors.filter(Boolean).join("\n"), [props.errors]);

  const title = useMemo(() => {
    if (props.kind === ErrorKind.WORKFLOW) {
      return "Cannot open workflow";
    }
    return "Cannot open the requested page";
  }, [props.kind]);

  const description = useMemo(() => {
    if (props.kind === ErrorKind.APPDATA_JSON || props.kind === ErrorKind.OPENAPI) {
      return `There was an error contacting the server.`;
    }
    if (props.kind === ErrorKind.WORKFLOW) {
      return `There was an error opening the workflow with name "${props.workflowId}".`;
    }
    return "There was an error opening the requested page.";
  }, [props]);

  return (
    <BasePage>
      <PageSection isFilled aria-label={`${props.kind.toLowerCase()}-error-section`}>
        <PageSection variant={"light"} padding={{ default: "noPadding" }}>
          <EmptyState>
            <EmptyStateIcon icon={ExclamationTriangleIcon} />
            <TextContent>
              <Text component={"h2"}>{title}</Text>
            </TextContent>
            <EmptyStateBody>
              <PageSection>
                <TextContent style={{ textOverflow: "ellipsis", overflow: "hidden" }}>
                  <Text component={TextVariants.p}>{description}</Text>
                </TextContent>
                <br />
                {props.errors && (
                  <>
                    <Button variant={ButtonVariant.link} onClick={() => setShowDetails((prev) => !prev)}>
                      {showDetails ? "Hide details" : "Show details"}
                    </Button>

                    {showDetails && (
                      <PageSection variant={"light"} isFilled={true} style={{ height: "100%", minWidth: "1000px" }}>
                        <ClipboardCopy
                          variant={ClipboardCopyVariant.expansion}
                          isReadOnly={true}
                          hoverTip="Copy"
                          clickTip="Copied"
                        >{`${errorDetails}`}</ClipboardCopy>
                      </PageSection>
                    )}
                  </>
                )}
                <br />
              </PageSection>
            </EmptyStateBody>
            <Link to={routes.home.path({})}>Return home</Link>
          </EmptyState>
        </PageSection>
      </PageSection>
    </BasePage>
  );
}
