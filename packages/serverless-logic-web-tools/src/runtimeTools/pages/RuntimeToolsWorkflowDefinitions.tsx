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
import { WorkflowDefinitionListContainer } from "@kie-tools/runtime-tools-swf-webapp-components/dist/WorkflowDefinitionListContainer";
import { Card } from "@patternfly/react-core/dist/esm/components/Card";
import { Page, PageSection } from "@patternfly/react-core/dist/js/components/Page";
import { Text, TextContent, TextVariants } from "@patternfly/react-core/dist/js/components/Text";
import { useNavigate } from "react-router-dom";
import { routes } from "../../navigation/Routes";
import { WorkflowDefinition } from "@kie-tools/runtime-tools-swf-gateway-api/dist/types";
import { CloudEventPageSource } from "@kie-tools/runtime-tools-swf-webapp-components/dist/CloudEventForm";

const PAGE_TITLE = "Workflow Definitions";

export function RuntimeToolsWorkflowDefinitions() {
  const navigate = useNavigate();

  const onOpenWorkflowForm = useCallback(
    (workflowDefinition: WorkflowDefinition) => {
      navigate(
        {
          pathname: routes.runtimeToolsWorkflowForm.path({ workflowName: workflowDefinition.workflowName }),
        },
        {
          state: {
            workflowDefinition: {
              workflowName: workflowDefinition.workflowName,
              endpoint: workflowDefinition.endpoint,
              serviceUrl: workflowDefinition.serviceUrl,
            },
          },
        }
      );
    },
    [navigate]
  );

  const onOpenTriggerCloudEventForWorkflow = useCallback(
    (workflowDefinition: WorkflowDefinition) => {
      navigate(
        {
          pathname: routes.runtimeToolsTriggerCloudEventForWorkflowDefinition.path({
            workflowName: workflowDefinition.workflowName,
          }),
        },
        {
          state: {
            workflowDefinition: {
              workflowName: workflowDefinition.workflowName,
              endpoint: workflowDefinition.endpoint,
              serviceUrl: workflowDefinition.serviceUrl,
            },
            source: CloudEventPageSource.DEFINITIONS,
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
            Start new workflow instances from the SonataFlow service linked in your Runtime Tools settings.
          </Text>
        </TextContent>
      </PageSection>

      <PageSection isFilled aria-label="workflow-definitions-section">
        <Card>
          <WorkflowDefinitionListContainer
            onOpenWorkflowForm={onOpenWorkflowForm}
            onOpenTriggerCloudEventForWorkflow={onOpenTriggerCloudEventForWorkflow}
          />
        </Card>
      </PageSection>
    </Page>
  );
}
