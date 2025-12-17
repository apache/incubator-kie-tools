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
import { PageSection } from "@patternfly/react-core/dist/js/components/Page";
import {
  EmptyState,
  EmptyStateBody,
  EmptyStateIcon,
  EmptyStateHeader,
  EmptyStateFooter,
} from "@patternfly/react-core/dist/js/components/EmptyState";
import { ExclamationTriangleIcon } from "@patternfly/react-icons/dist/js/icons/exclamation-triangle-icon";
import { Text, TextContent, TextVariants } from "@patternfly/react-core/dist/js/components/Text";
import { Button, ButtonVariant } from "@patternfly/react-core/dist/js/components/Button";
import { useCallback, useMemo, useState } from "react";
import { useNavigate } from "react-router-dom";
import { ClipboardCopy, ClipboardCopyVariant } from "@patternfly/react-core/dist/js/components/ClipboardCopy";
import { WorkspaceDescriptor } from "@kie-tools-core/workspaces-git-fs/dist/worker/api/WorkspaceDescriptor";
import { useRoutes } from "../navigation/Hooks";

export type ErrorPageProps = { errors: string[] } & (
  | {
      kind: "WorkspaceFiles";
      workspaceId: WorkspaceDescriptor["workspaceId"];
    }
  | {
      kind: "File";
      filePath: string;
    }
  | {
      kind: "Sample";
      sampleId: string;
    }
  | {
      kind: "Url";
      url: string;
    }
  | {
      kind: "SampleCatalog";
    }
);

export function ErrorPage(props: ErrorPageProps) {
  const routes = useRoutes();
  const navigate = useNavigate();
  const [showDetails, setShowDetails] = useState(false);

  const returnRecentModels = useCallback(() => {
    navigate({ pathname: routes.home.path({}) }, { replace: true });
  }, [navigate, routes]);

  const errorDetails = useMemo(() => props.errors.filter(Boolean).join("\n"), [props.errors]);

  const title = useMemo(() => {
    if (props.kind === "WorkspaceFiles") {
      return "Cannot open workspace";
    }
    if (props.kind === "File") {
      return "Cannot open file";
    }
    if (props.kind === "SampleCatalog") {
      return "Cannot load the sample catalog";
    }
    if (props.kind === "Sample") {
      return "Cannot load sample";
    }
    if (props.kind === "Url") {
      return "Cannot load from URL";
    }
    return "Cannot open the requested page";
  }, [props.kind]);

  const description = useMemo(() => {
    if (props.kind === "WorkspaceFiles") {
      return `There was an error opening the workspace with id "${props.workspaceId}".`;
    }
    if (props.kind === "File") {
      return `There was an error opening the file "${props.filePath}".`;
    }
    if (props.kind === "Sample") {
      return `There was an error opening the sample "${props.sampleId}".`;
    }
    if (props.kind === "Url") {
      return `There was an error opening the URL "${props.url}".`;
    }
    if (props.kind === "SampleCatalog") {
      return "There was an error loading the sample catalog.";
    }
    return "There was an error opening the requested page.";
  }, [props]);

  return (
    <PageSection isFilled aria-label={`${props.kind.toLowerCase()}-error-section`}>
      <PageSection variant={"light"} padding={{ default: "noPadding" }}>
        <EmptyState>
          <EmptyStateHeader icon={<EmptyStateIcon icon={ExclamationTriangleIcon} />} />
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
          <EmptyStateFooter>
            <Button variant={ButtonVariant.tertiary} onClick={returnRecentModels}>
              Return home
            </Button>
          </EmptyStateFooter>
        </EmptyState>
      </PageSection>
    </PageSection>
  );
}
