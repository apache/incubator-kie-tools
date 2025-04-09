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
import React, { useCallback, useMemo } from "react";
import {
  EmptyState,
  EmptyStateBody,
  EmptyStateIcon,
  EmptyStateHeader,
} from "@patternfly/react-core/dist/js/components/EmptyState";
import { PageSection } from "@patternfly/react-core/dist/js/components/Page";
import { Text, TextContent, TextVariants } from "@patternfly/react-core/dist/js/components/Text";

import { Bullseye } from "@patternfly/react-core/dist/js/layouts/Bullseye";
import { CubesIcon } from "@patternfly/react-icons/dist/js/icons";
import { useHistory } from "react-router";
import { useApp } from "../../context/AppContext";
import { WorkflowListState } from "@kie-tools/runtime-tools-swf-gateway-api/dist/types";
import { WorkflowListContainer } from "@kie-tools/runtime-tools-swf-webapp-components/dist/WorkflowListContainer";
import { BasePage } from "../../pages/BasePage";
import { SONATAFLOW_DEPLOYMENT_DATAINDEX_DOCUMENTATION_URL } from "../../AppConstants";
import { routes } from "../../routes";

const PAGE_TITLE = "Workflow Instances";

export function RuntimeToolsWorkflowInstances() {
  const history = useHistory();
  const app = useApp();
  const initialState: WorkflowListState = history.location && (history.location.state as WorkflowListState);

  const dataIndexNotAvailable = useMemo(
    () => (
      <PageSection variant="light">
        <Bullseye>
          <EmptyState>
            <EmptyStateHeader
              titleText={
                <>
                  Data Index service not available
                  <EmptyStateIcon icon={CubesIcon} />{" "}
                </>
              }
              headingLevel="h4"
            />
            <EmptyStateBody>
              <TextContent>
                <Text>
                  Read more on &nbsp;
                  <a href={SONATAFLOW_DEPLOYMENT_DATAINDEX_DOCUMENTATION_URL} target="_blank" rel="noopener noreferrer">
                    SonataFlow Guides.
                  </a>
                </Text>
              </TextContent>
            </EmptyStateBody>
          </EmptyState>
        </Bullseye>
      </PageSection>
    ),
    []
  );

  const onOpenWorkflowDetails = useCallback(
    (args: { workflowId: string; state: WorkflowListState }) => {
      history.push({
        pathname: routes.runtimeTools.workflowDetails.path({ workflowId: args.workflowId }),
        state: args.state,
      });
    },
    [history]
  );

  return (
    <BasePage>
      <PageSection variant={"light"}>
        <TextContent>
          <Text component={TextVariants.h1}>{PAGE_TITLE}</Text>
          <Text component={TextVariants.p}>
            List and view workflows from the Data Index linked in your Runtime Tools settings.
            <br />
            Your Data Index URL is: {app.fullDataIndexUrl}
          </Text>
        </TextContent>
      </PageSection>

      <PageSection isFilled aria-label="workflow-instances-section">
        {app.dataIndexAvailable === false ? (
          dataIndexNotAvailable
        ) : (
          <WorkflowListContainer initialState={initialState} onOpenWorkflowDetails={onOpenWorkflowDetails} />
        )}
      </PageSection>
    </BasePage>
  );
}
