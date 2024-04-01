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
import { componentOuiaProps, OUIAProps } from "@kie-tools/runtime-tools-components/dist/ouiaTools";
import { useCloudEventFormGatewayApi } from "../CloudEventForm";
import { EmbeddedCloudEventForm } from "@kie-tools/runtime-tools-swf-enveloped-components/dist/cloudEventForm";
import { CloudEventRequest } from "@kie-tools/runtime-tools-swf-gateway-api/dist/types";

interface CloudEventFormContainerProps {
  instanceId?: string;
  cloudEventSource: string;
  isTriggerNewInstance: boolean;
  onStartWorkflowError: (error: any) => void;
  onTriggerCloudEventSuccess: () => void;
  onTriggerStartCloudEventSuccess: (businessKey: string) => void;
}

export const CloudEventFormContainer: React.FC<CloudEventFormContainerProps & OUIAProps> = ({
  instanceId,
  cloudEventSource,
  isTriggerNewInstance,
  onStartWorkflowError,
  onTriggerCloudEventSuccess,
  onTriggerStartCloudEventSuccess,
  ouiaId,
  ouiaSafe,
}) => {
  const gatewayApi = useCloudEventFormGatewayApi();

  const triggerStartCloudEvent = useCallback(
    (event: CloudEventRequest) => {
      return gatewayApi
        .triggerStartCloudEvent(event)
        .then((businessKey) => {
          onTriggerStartCloudEventSuccess(businessKey);
        })
        .catch((error) => onStartWorkflowError(error));
    },
    [gatewayApi, onStartWorkflowError, onTriggerStartCloudEventSuccess]
  );

  const triggerCloudEvent = useCallback(
    (event: CloudEventRequest) => {
      return gatewayApi
        .triggerCloudEvent(event)
        .then((_response) => {
          onTriggerCloudEventSuccess();
        })
        .catch((error) => onStartWorkflowError(error));
    },
    [gatewayApi, onStartWorkflowError, onTriggerCloudEventSuccess]
  );

  return (
    <EmbeddedCloudEventForm
      {...componentOuiaProps(ouiaId, "cloud-event-form-container", ouiaSafe)}
      targetOrigin={window.location.origin}
      isNewInstanceEvent={isTriggerNewInstance}
      defaultValues={{ cloudEventSource, instanceId }}
      driver={{
        triggerCloudEvent(event: CloudEventRequest): Promise<void> {
          const doTrigger = isTriggerNewInstance ? triggerStartCloudEvent : triggerCloudEvent;
          return doTrigger(event);
        },
      }}
    />
  );
};
