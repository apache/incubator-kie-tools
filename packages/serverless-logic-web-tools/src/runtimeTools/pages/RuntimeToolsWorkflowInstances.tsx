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

import React, { useCallback } from "react";
import { Page, PageSection } from "@patternfly/react-core/dist/js/components/Page";
import { Text, TextContent, TextVariants } from "@patternfly/react-core/dist/js/components/Text";
import { WorkflowListContainer } from "@kie-tools/runtime-tools-swf-webapp-components/dist/WorkflowListContainer";
import { CloudEventPageSource } from "@kie-tools/runtime-tools-swf-webapp-components/dist/CloudEventForm";
import { useLocation, useNavigate } from "react-router-dom";
import { WorkflowListState } from "@kie-tools/runtime-tools-swf-gateway-api/dist/types";
import { routes } from "../../navigation/Routes";

const PAGE_TITLE = "Workflow Instances";

export function RuntimeToolsWorkflowInstances() {
  const navigate = useNavigate();
  const location = useLocation();

  const initialState: WorkflowListState = location && (location.state as WorkflowListState);

  const onOpenWorkflowDetails = useCallback(
    (args: { workflowId: string; state: WorkflowListState }) => {
      navigate(
        {
          pathname: routes.runtimeToolsWorkflowDetails.path({ workflowId: args.workflowId }),
        },
        { state: args.state }
      );
    },
    [navigate]
  );

  const onOpenTriggerCloudEventForWorkflow = useCallback(
    (workflowId: string) => {
      navigate(
        {
          pathname: routes.runtimeToolsTriggerCloudEventForWorkflowInstance.path({ workflowId }),
        },
        {
          state: {
            source: CloudEventPageSource.INSTANCES,
          },
        }
      );
    },
    [navigate]
  );

  return (
    <Page>
      <PageSection variant={"light"}>
        <TextContent>
          <Text component={TextVariants.h1}>{PAGE_TITLE}</Text>
          <Text component={TextVariants.p}>
            List and view workflows from the Data Index linked in your Runtime Tools settings.
          </Text>
        </TextContent>
      </PageSection>

      <PageSection isFilled aria-label="workflow-instances-section">
        <WorkflowListContainer
          initialState={initialState}
          onOpenWorkflowDetails={onOpenWorkflowDetails}
          onOpenTriggerCloudEventForWorkflow={onOpenTriggerCloudEventForWorkflow}
        />
      </PageSection>
    </Page>
  );
}
