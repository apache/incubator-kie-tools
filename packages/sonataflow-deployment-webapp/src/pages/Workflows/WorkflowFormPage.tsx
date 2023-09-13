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
import React, { useState, useEffect, useMemo, useCallback } from "react";
import { PromiseStateStatus } from "@kie-tools-core/react-hooks/dist/PromiseState";
import { EmptyState, EmptyStateIcon } from "@patternfly/react-core/dist/js/components/EmptyState";
import { PageSection } from "@patternfly/react-core/dist/js/components/Page";
import { Spinner } from "@patternfly/react-core/dist/js/components/Spinner";
import { Text, TextContent, TextVariants } from "@patternfly/react-core/dist/js/components/Text";
import { Title } from "@patternfly/react-core/dist/js/components/Title";
import { WorkflowDefinition, WorkflowFormDriver } from "../../apis";
import { WorkflowForm, CustomWorkflowForm, FormNotification, Notification } from "../../components";
import { useOpenApi } from "../../context/OpenApiContext";
import { BasePage } from "../BasePage";
import { ErrorPage } from "../ErrorPage";
import { Card, CardBody } from "@patternfly/react-core/dist/js/components/Card";
import { routes } from "../../routes";
import { useHistory } from "react-router";
import { WorkflowFormGatewayApiImpl } from "../../impl/WorkflowFormGatewayApiImpl";
import { WorkflowResponse } from "../../apis/WorkflowResponse";
import { WorkflowResult } from "../../components/WorkflowResult";

export function WorkflowFormPage(props: { workflowId: string }) {
  const [notification, setNotification] = useState<Notification>();
  const [workflowResponse, setWorkflowResponse] = useState<WorkflowResponse>();
  const openApi = useOpenApi();
  const [customFormSchema, setCustomFormSchema] = useState<Record<string, any>>();
  const history = useHistory();
  const gatewayApi = useMemo(
    () =>
      openApi.openApiPromise.status === PromiseStateStatus.RESOLVED && openApi.openApiData
        ? new WorkflowFormGatewayApiImpl(openApi.openApiData)
        : null,
    [openApi]
  );
  const workflowDefinition = useMemo<WorkflowDefinition>(
    () => ({
      workflowName: props.workflowId,
      endpoint: `/${props.workflowId}`,
    }),
    [props.workflowId]
  );

  const goToWorkflowList = useCallback(() => {
    history.push(routes.workflows.home.path({}));
  }, [history]);

  const showNotification = useCallback(
    (notificationType: "error" | "success", submitMessage: string, notificationDetails?: string) => {
      setNotification({
        type: notificationType,
        message: submitMessage,
        details: notificationDetails,
        customActions: [
          {
            label: "Go to workflow list",
            onClick: () => {
              setNotification(undefined);
              goToWorkflowList();
            },
          },
        ],
        close: () => {
          setNotification(undefined);
        },
      });
    },
    [goToWorkflowList]
  );

  const onSubmitSuccess = useCallback(
    (message: string): void => {
      showNotification("success", message);
    },
    [showNotification]
  );

  const onSubmitError = useCallback(
    (details?: string) => {
      const message = "Failed to trigger workflow.";
      showNotification("error", message, details);
    },
    [showNotification]
  );

  const driver: WorkflowFormDriver = useMemo(
    () => ({
      async getCustomWorkflowSchema(): Promise<Record<string, any>> {
        return gatewayApi?.getCustomWorkflowSchema(workflowDefinition.workflowName) ?? {};
      },
      async resetBusinessKey() {},
      async startWorkflow(endpoint: string, data: Record<string, any>): Promise<void> {
        return gatewayApi
          ?.startWorkflow(endpoint, data)
          .then((response: WorkflowResponse) => {
            onSubmitSuccess(`A workflow with id ${response.id} was triggered successfully.`);
            setWorkflowResponse(response);
          })
          .catch((error) => {
            const message =
              error?.response?.data?.message + " " + error?.response?.data?.cause ||
              error?.message ||
              "Unknown error. More details in the developer tools console.";
            onSubmitError(message);
          });
      },
    }),
    [gatewayApi, workflowDefinition, onSubmitError, onSubmitSuccess]
  );

  useEffect(() => {
    if (gatewayApi) {
      gatewayApi.getCustomWorkflowSchema(props.workflowId).then((data) => {
        if (data) {
          setCustomFormSchema(data);
        }
      });
    }
  }, [gatewayApi, props.workflowId]);

  if (openApi.openApiPromise.status === PromiseStateStatus.REJECTED) {
    return <ErrorPage kind="OpenApi" errors={["OpenAPI service not available"]} />;
  }

  if (!openApi.openApiData?.tags?.find((t) => t.name === workflowDefinition.workflowName)) {
    return <ErrorPage kind="Workflow" workflowId={workflowDefinition.workflowName} errors={["Workflow not found"]} />;
  }

  return (
    <BasePage>
      <PageSection variant={"light"} title="Start New Workflow">
        <TextContent>
          <Text component={TextVariants.h1}>Start New Workflow</Text>
        </TextContent>
        {notification && (
          <div>
            <FormNotification notification={notification} />
          </div>
        )}
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
            ) : workflowResponse ? (
              <WorkflowResult response={workflowResponse} />
            ) : customFormSchema ? (
              <CustomWorkflowForm
                workflowDefinition={workflowDefinition}
                driver={driver}
                customFormSchema={customFormSchema}
              />
            ) : (
              <WorkflowForm workflowDefinition={workflowDefinition} driver={driver} />
            )}
          </CardBody>
        </Card>
      </PageSection>
    </BasePage>
  );
}
