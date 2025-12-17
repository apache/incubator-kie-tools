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
import { PromiseStateStatus } from "@kie-tools-core/react-hooks/dist/PromiseState";
import {
  Action,
  FormNotification,
  Notification,
} from "@kie-tools/runtime-tools-components/dist/components/FormNotification";
import { WorkflowFormDriver } from "@kie-tools/runtime-tools-swf-enveloped-components/dist/workflowForm/api/WorkflowFormDriver";
import CustomWorkflowForm from "@kie-tools/runtime-tools-swf-enveloped-components/dist/workflowForm/envelope/components/CustomWorkflowForm/CustomWorkflowForm";
import WorkflowForm from "@kie-tools/runtime-tools-swf-enveloped-components/dist/workflowForm/envelope/components/WorkflowForm/WorkflowForm";
import WorkflowResult from "@kie-tools/runtime-tools-swf-enveloped-components/dist/workflowForm/envelope/components/WorkflowResult/WorkflowResult";
import { WorkflowDefinition, WorkflowResponse } from "@kie-tools/runtime-tools-swf-gateway-api/dist/types";
import { Card, CardBody } from "@patternfly/react-core/dist/js/components/Card";
import { EmptyState, EmptyStateIcon, EmptyStateHeader } from "@patternfly/react-core/dist/js/components/EmptyState";
import { PageSection } from "@patternfly/react-core/dist/js/components/Page";
import { Spinner } from "@patternfly/react-core/dist/js/components/Spinner";
import { Text, TextContent, TextVariants } from "@patternfly/react-core/dist/js/components/Text";

import { useCallback, useEffect, useMemo, useState } from "react";
import { useNavigate, useParams } from "react-router-dom";
import { useOpenApi } from "../../context/OpenApiContext";
import { WorkflowFormGatewayApiImpl } from "../../impl/WorkflowFormGatewayApiImpl";
import { routes } from "../../routes";
import { BasePage } from "../BasePage";
import { ErrorKind, ErrorPage } from "../ErrorPage";
import { useApp } from "../../context/AppContext";

export function WorkflowFormPage() {
  const [notification, setNotification] = useState<Notification>();
  const [workflowResponse, setWorkflowResponse] = useState<WorkflowResponse>();
  const openApi = useOpenApi();
  const [customFormSchema, setCustomFormSchema] = useState<Record<string, any>>();
  const app = useApp();
  const navigate = useNavigate();
  const { workflowId } = useParams<{ workflowId: string }>();
  const gatewayApi = useMemo(
    () =>
      openApi.openApiPromise.status === PromiseStateStatus.RESOLVED && openApi.openApiData
        ? new WorkflowFormGatewayApiImpl(openApi.openApiData)
        : null,
    [openApi]
  );
  const workflowDefinition = useMemo<WorkflowDefinition>(
    () => ({
      workflowName: workflowId!,
      endpoint: `/${workflowId}`,
      serviceUrl: window.location.href.split("/#")[0],
    }),
    [workflowId]
  );

  const goToWorkflowList = useCallback(() => {
    navigate(routes.workflows.home.path({}));
  }, [navigate]);

  const openWorkflowInstance = useCallback(
    (id: string) => {
      navigate({
        pathname: routes.runtimeTools.workflowDetails.path({ workflowId: id }),
      });
    },
    [navigate]
  );

  const showNotification = useCallback(
    (
      notificationType: "error" | "success",
      submitMessage: string,
      notificationDetails?: string,
      customActions?: Action[]
    ) => {
      setNotification({
        type: notificationType,
        message: submitMessage,
        details: notificationDetails,
        customActions,
        close: () => {
          setNotification(undefined);
        },
      });
    },
    []
  );

  const onSubmitSuccess = useCallback(
    (message: string, id: string): void => {
      showNotification("success", message, undefined, [
        !app.dataIndexAvailable
          ? {
              label: "Go to workflow list",
              onClick: () => {
                setNotification(undefined);
                goToWorkflowList();
              },
            }
          : {
              label: "View details",
              onClick: () => {
                setNotification(undefined);
                openWorkflowInstance(id);
              },
            },
      ]);
    },
    [showNotification, openWorkflowInstance, goToWorkflowList, app]
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
            onSubmitSuccess(`A workflow with id ${response.id} was started successfully.`, response.id);
            setWorkflowResponse(response);
          })
          .catch((error) => {
            const message =
              (error?.response?.data?.message && error?.response?.data?.message + " " + error?.response?.data?.cause) ||
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
      gatewayApi.getCustomWorkflowSchema(workflowId!).then((data) => {
        if (data) {
          setCustomFormSchema(data);
        }
      });
    }
  }, [gatewayApi, workflowId]);

  if (openApi.openApiPromise.status === PromiseStateStatus.REJECTED) {
    return <ErrorPage kind={ErrorKind.OPENAPI} errors={["OpenAPI service not available"]} />;
  }

  if (
    openApi.openApiPromise.status === PromiseStateStatus.RESOLVED &&
    !openApi.openApiData?.tags?.find((t) => t.name === workflowDefinition.workflowName)
  ) {
    return (
      <ErrorPage
        kind={ErrorKind.WORKFLOW}
        workflowId={workflowDefinition.workflowName}
        errors={["Workflow not found"]}
      />
    );
  }

  return (
    <BasePage>
      <PageSection variant={"light"}>
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
                <EmptyStateHeader
                  titleText={
                    <>
                      Loading...
                      <EmptyStateIcon icon={Spinner} />
                    </>
                  }
                  headingLevel="h4"
                />
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
