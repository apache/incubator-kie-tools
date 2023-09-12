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
import { PromiseStateStatus } from "@kie-tools-core/react-hooks/dist/PromiseState";
import { Card, CardBody } from "@patternfly/react-core/dist/js/components/Card";
import { EmptyState, EmptyStateIcon } from "@patternfly/react-core/dist/js/components/EmptyState";
import { PageSection } from "@patternfly/react-core/dist/js/components/Page";
import { Spinner } from "@patternfly/react-core/dist/js/components/Spinner";
import { Text, TextContent, TextVariants } from "@patternfly/react-core/dist/js/components/Text";
import { Title } from "@patternfly/react-core/dist/js/components/Title";
import React, { useCallback, useMemo, useState } from "react";
import { useHistory } from "react-router";
import { CloudEventFormDriver, CloudEventRequest } from "../../apis";
import { FormNotification, Notification } from "../../components";
import { CloudEventForm, CloudEventFormDefaultValues } from "../../components/CloudEventForm";
import { useOpenApi } from "../../context/OpenApiContext";
import { CloudEventFormGatewayApiImpl } from "../../impl/CloudEventFormGatewayApiImpl";
import { routes } from "../../routes";
import { BasePage } from "../BasePage";
import { ErrorPage } from "../ErrorPage";

const defaultValues: CloudEventFormDefaultValues = { cloudEventSource: "/from/form" };

export function CloudEventFormPage() {
  const [notification, setNotification] = useState<Notification>();
  const openApi = useOpenApi();
  const history = useHistory();

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

  const gatewayApi = useMemo(() => new CloudEventFormGatewayApiImpl(window.location.href.split("/#")[0]), []);

  const triggerStartCloudEvent = useCallback(
    async (event: CloudEventRequest) => {
      return gatewayApi
        .triggerStartCloudEvent(event)
        .then((businessKey) => {
          onSubmitSuccess(`A workflow with business key ${businessKey} has been successfully triggered.`);
        })
        .catch((error) =>
          onSubmitError(error?.message || "Unknown error. More details in the developer tools console.")
        );
    },
    [gatewayApi, onSubmitSuccess, onSubmitError]
  );

  const driver: CloudEventFormDriver = useMemo(
    () => ({
      triggerCloudEvent(event: CloudEventRequest): Promise<void> {
        return triggerStartCloudEvent(event);
      },
    }),
    [triggerStartCloudEvent]
  );

  if (openApi.openApiPromise.status === PromiseStateStatus.REJECTED) {
    return <ErrorPage kind="OpenApi" errors={["OpenAPI service not available"]} />;
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
            ) : (
              <CloudEventForm driver={driver} defaultValues={defaultValues} />
            )}
          </CardBody>
        </Card>
      </PageSection>
    </BasePage>
  );
}
