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
import { EmptyState, EmptyStateIcon, EmptyStateHeader } from "@patternfly/react-core/dist/js/components/EmptyState";
import { PageSection } from "@patternfly/react-core/dist/js/components/Page";
import { Spinner } from "@patternfly/react-core/dist/js/components/Spinner";
import { Text, TextContent, TextVariants } from "@patternfly/react-core/dist/js/components/Text";

import React, { useCallback, useMemo, useState } from "react";
import { useNavigate } from "react-router-dom";
import { CloudEventRequest } from "@kie-tools/runtime-tools-swf-gateway-api/dist/types";
import { FormNotification, Notification } from "@kie-tools/runtime-tools-components/dist/components/FormNotification";
import { CloudEventForm } from "@kie-tools/runtime-tools-swf-enveloped-components/dist/cloudEventForm/envelope/components/CloudEventForm/CloudEventForm";
import { useOpenApi } from "../../context/OpenApiContext";
import { CloudEventFormGatewayApiImpl } from "../../impl/CloudEventFormGatewayApiImpl";
import { routes } from "../../routes";
import { BasePage } from "../BasePage";
import { ErrorKind, ErrorPage } from "../ErrorPage";
import { CloudEventFormDefaultValues } from "@kie-tools/runtime-tools-swf-enveloped-components/dist/cloudEventForm";
import { CloudEventFormDriver } from "@kie-tools/runtime-tools-swf-enveloped-components/dist/cloudEventForm/api/CloudEventFormDriver";
import { KOGITO_PROCESS_REFERENCE_ID } from "@kie-tools/runtime-tools-shared-gateway-api/dist/types";

const defaultValues: CloudEventFormDefaultValues = {
  instanceId: "",
  cloudEventSource: "",
};

export function CloudEventFormPage() {
  const [notification, setNotification] = useState<Notification>();
  const openApi = useOpenApi();
  const navigate = useNavigate();

  const gatewayApi = useMemo(() => new CloudEventFormGatewayApiImpl(window.location.href.split("/#")[0]), []);

  const goToWorkflowList = useCallback(() => {
    navigate(routes.workflows.home.path({}));
  }, [navigate]);

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

  const triggerCloudEvent = useCallback(
    async (event: CloudEventRequest) => {
      return gatewayApi
        .triggerCloudEvent(event)
        .then((response) => {
          console.log(response);
          onSubmitSuccess("The CloudEvent has been successfully triggered.");
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
        const isTriggerNewInstance = !event.headers.extensions[KOGITO_PROCESS_REFERENCE_ID]?.length;
        const doTrigger = isTriggerNewInstance ? triggerStartCloudEvent : triggerCloudEvent;
        return doTrigger(event);
      },
    }),
    [triggerStartCloudEvent, triggerCloudEvent]
  );

  if (openApi.openApiPromise.status === PromiseStateStatus.REJECTED) {
    return <ErrorPage kind={ErrorKind.OPENAPI} errors={["OpenAPI service not available"]} />;
  }

  return (
    <BasePage>
      <PageSection variant={"light"} title="Start New Workflow">
        <TextContent>
          <Text component={TextVariants.h1}>Trigger Cloud Event</Text>
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
            ) : (
              <CloudEventForm
                driver={driver}
                defaultValues={defaultValues}
                serviceUrl={window.location.href.split("/#")[0]}
              />
            )}
          </CardBody>
        </Card>
      </PageSection>
    </BasePage>
  );
}
