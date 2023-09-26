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
import { componentOuiaProps, OUIAProps } from "@kie-tools/runtime-tools-common/dist/ouiaTools";
import { useCloudEventFormGatewayApi } from "../CloudEventForm";
import {
  CloudEventRequest,
  EmbeddedCloudEventForm,
} from "@kie-tools/runtime-tools-common/dist/components/CloudEventForm";
import { useParams } from "react-router";
import { useGlobalAlert } from "../../../alerts/GlobalAlertsContext";
import { Alert, AlertActionCloseButton } from "@patternfly/react-core/dist/js/components/Alert";

export type CloudEventFormContainerProps = {
  isTriggerNewInstance: boolean;
};

export type CloudEventFormContainerParams = {
  instanceId?: string;
};

const CloudEventFormContainer: React.FC<CloudEventFormContainerProps & OUIAProps> = ({
  isTriggerNewInstance,
  ouiaId,
  ouiaSafe,
}) => {
  const gatewayApi = useCloudEventFormGatewayApi();

  const { instanceId } = useParams<CloudEventFormContainerParams>();

  const triggerEventSuccessAlert = useGlobalAlert<{ message: string }>(
    useCallback(({ close }, { message }) => {
      return (
        <Alert
          className="pf-u-mb-md"
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
          className="pf-u-mb-md"
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

  const triggerStartCloudEvent = useCallback(
    (event: CloudEventRequest) => {
      return gatewayApi
        .triggerStartCloudEvent(event)
        .then((businessKey) => {
          triggerEventSuccessAlert.show({
            message: `A workflow with business key ${businessKey} has been successfully triggered.`,
          });
        })
        .catch((error) => handleError(error));
    },
    [gatewayApi, triggerEventSuccessAlert]
  );

  const triggerCloudEvent = useCallback(
    (event: CloudEventRequest) => {
      return gatewayApi
        .triggerCloudEvent(event)
        .then((response) => {
          console.log(response);
          triggerEventSuccessAlert.show({ message: "The cloud event has been successfully triggered." });
        })
        .catch((error) => handleError(error));
    },
    [gatewayApi, triggerEventSuccessAlert]
  );

  const handleError = useCallback(
    (error) => {
      const message = error?.message || "Unknown error. More details in the developer tools console.";
      startWorkflowErrorAlert.show({ message });
    },
    [gatewayApi, startWorkflowErrorAlert]
  );

  return (
    <EmbeddedCloudEventForm
      {...componentOuiaProps(ouiaId, "cloud-event-form-container", ouiaSafe)}
      targetOrigin={window.location.origin}
      isNewInstanceEvent={isTriggerNewInstance}
      defaultValues={{
        cloudEventSource: "/local/kubesmarts",
        instanceId: instanceId ?? undefined,
      }}
      driver={{
        triggerCloudEvent(event: CloudEventRequest): Promise<void> {
          const doTrigger = isTriggerNewInstance ? triggerStartCloudEvent : triggerCloudEvent;
          return doTrigger(event);
        },
      }}
    />
  );
};

export default CloudEventFormContainer;
