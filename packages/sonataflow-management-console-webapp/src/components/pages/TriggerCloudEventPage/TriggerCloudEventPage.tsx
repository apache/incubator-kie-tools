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
import { CloudEventFormContainer } from "@kie-tools/runtime-tools-swf-webapp-components/dist/CloudEventFormContainer";
import { Alert, AlertActionCloseButton } from "@patternfly/react-core/dist/js/components/Alert";
import { useGlobalAlert } from "../../../alerts/GlobalAlertsContext";
import { WorkflowDefinition } from "@kie-tools/runtime-tools-swf-gateway-api/dist/types";
import { useLocation } from "react-router-dom";

const PAGE_TITLE = "Trigger Cloud Event";
const KUBESMARTS_CLOUD_SOURCE = "/local/kubesmarts";

export function TriggerCloudEventPage() {
  const location = useLocation();
  const workflowDefinition: WorkflowDefinition = (location.state as any)["workflowDefinition"];

  const triggerEventSuccessAlert = useGlobalAlert<{ message: string }>(
    useCallback(({ close }, { message }) => {
      return (
        <Alert
          className="pf-v5-u-mb-md"
          variant="success"
          title={message}
          aria-live="polite"
          actionClose={<AlertActionCloseButton onClose={close} />}
        />
      );
    }, []),
    { durationInSeconds: 5 }
  );

  const startWorkflowErrorAlert = useGlobalAlert<{ message: string }>(
    useCallback(({ close }, { message }) => {
      return (
        <Alert
          className="pf-v5-u-mb-md"
          variant="danger"
          title={
            <>
              Something went wrong while triggering your workflow.
              <br />
              {`Reason: ${message}`}
            </>
          }
          aria-live="polite"
          data-testid="alert-upload-error"
          actionClose={<AlertActionCloseButton onClose={close} />}
        />
      );
    }, []),
    { durationInSeconds: 5 }
  );

  const onStartWorkflowError = useCallback(
    (error) => {
      const message = error?.message || "Unknown error. More details in the developer tools console.";
      startWorkflowErrorAlert.show({ message });
    },
    [startWorkflowErrorAlert]
  );

  const onTriggerEventSuccessAlert = useCallback(() => {
    triggerEventSuccessAlert.show({ message: "The cloud event has been successfully triggered." });
  }, [triggerEventSuccessAlert]);

  const onTriggerStartCloudEventSuccess = useCallback(
    (businessKey: string) => {
      triggerEventSuccessAlert.show({
        message: `A workflow with business key ${businessKey} has been successfully triggered.`,
      });
    },
    [triggerEventSuccessAlert]
  );

  return (
    <Page>
      <PageSection variant={"light"}>
        <TextContent>
          <Text component={TextVariants.h1}>{PAGE_TITLE}</Text>
          <Text component={TextVariants.p}>
            Trigger a cloud event to start new workflow instances or to send HTTP Cloud Events to active workflow
            instances that are waiting for an event to advance.
          </Text>
        </TextContent>
      </PageSection>

      <PageSection isFilled aria-label="trigger-cloud-event-section">
        <CloudEventFormContainer
          isTriggerNewInstance={true}
          cloudEventSource={KUBESMARTS_CLOUD_SOURCE}
          onStartWorkflowError={onStartWorkflowError}
          onTriggerCloudEventSuccess={onTriggerEventSuccessAlert}
          onTriggerStartCloudEventSuccess={onTriggerStartCloudEventSuccess}
          serviceUrl={workflowDefinition.serviceUrl}
        />
      </PageSection>
    </Page>
  );
}
