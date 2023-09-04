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
import React, { useState, useEffect, useMemo } from "react";
import { PromiseStateStatus } from "@kie-tools-core/react-hooks/dist/PromiseState";
import { EmptyState, EmptyStateIcon } from "@patternfly/react-core/dist/js/components/EmptyState";
import { PageSection } from "@patternfly/react-core/dist/js/components/Page";
import { Spinner } from "@patternfly/react-core/dist/js/components/Spinner";
import { Text, TextContent, TextVariants } from "@patternfly/react-core/dist/js/components/Text";
import { Title } from "@patternfly/react-core/dist/js/components/Title";
import { WorkflowDefinition } from "../../api";
import { WorkflowForm, CustomWorkflowForm } from "../../components";
import { useOpenApi } from "../../context/OpenApiContext";
import { BasePage } from "../BasePage";
import { ErrorPage } from "../ErrorPage";
import { Card, CardBody } from "@patternfly/react-core/dist/js/components/Card";

export function WorkflowFormPage(props: { workflowId: string }) {
  const openApi = useOpenApi();
  const [customFormSchema, setCustomFormSchema] = useState<Record<string, any>>();
  const workflowDefinition = useMemo<WorkflowDefinition>(
    () => ({
      workflowName: props.workflowId,
      endpoint: `/${props.workflowId}`,
    }),
    [props.workflowId]
  );

  useEffect(() => {
    if (openApi.gatewayApi) {
      openApi.gatewayApi.getCustomWorkflowSchema(props.workflowId).then((data) => {
        if (data) {
          setCustomFormSchema(data);
        }
      });
    }
  }, [openApi.gatewayApi, props.workflowId]);

  if (
    openApi.openApiPromise.status === PromiseStateStatus.RESOLVED &&
    !openApi.openApiData?.tags?.find((t) => t.name === workflowDefinition.workflowName)
  ) {
    return <ErrorPage kind="Workflow" workflowId={workflowDefinition.workflowName} errors={["Workflow not found"]} />;
  }

  return (
    <BasePage>
      <PageSection variant={"light"}>
        <TextContent>
          <Text component={TextVariants.h1}>Start New Workflow</Text>
        </TextContent>
      </PageSection>

      <PageSection isFilled>
        <Card isFullHeight>
          <CardBody isFilled>
            {openApi.openApiPromise.status === PromiseStateStatus.PENDING ? (
              <EmptyState>
                <EmptyStateIcon variant="container" component={Spinner} />
                <Title size="lg" headingLevel="h4">
                  Loading...
                </Title>
              </EmptyState>
            ) : customFormSchema ? (
              <CustomWorkflowForm workflowDefinition={workflowDefinition} customFormSchema={customFormSchema} />
            ) : (
              <WorkflowForm workflowDefinition={workflowDefinition} />
            )}
          </CardBody>
        </Card>
      </PageSection>
    </BasePage>
  );
}
