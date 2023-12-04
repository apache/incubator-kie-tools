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
import { useCloudEventFormGatewayApi } from "../../../channel/CloudEventForm";
import { useParams } from "react-router";
import { useDevUIAppContext } from "../../contexts/DevUIAppContext";
import { EmbeddedCloudEventForm } from "@kie-tools/runtime-tools-enveloped-components/dist/cloudEventForm";
import { CloudEventRequest } from "@kie-tools/runtime-tools-gateway-api/dist/types";

export type CloudEventFormContainerProps = {
  isTriggerNewInstance: boolean;
  onSuccess: (id: string) => void;
  onError: (details?: string) => void;
};

export type CloudEventFormContainerParams = {
  instanceId?: string;
};

const CloudEventFormContainer: React.FC<CloudEventFormContainerProps & OUIAProps> = ({
  isTriggerNewInstance,
  onSuccess,
  onError,
  ouiaId,
  ouiaSafe,
}) => {
  const gatewayApi = useCloudEventFormGatewayApi();
  const appContext = useDevUIAppContext();

  const { instanceId } = useParams<CloudEventFormContainerParams>();

  const triggerStartCloudEvent = useCallback(
    (event: CloudEventRequest) => {
      return gatewayApi
        .triggerStartCloudEvent(event)
        .then((businessKey) => {
          onSuccess(`A workflow with business key ${businessKey} has been successfully triggered.`);
        })
        .catch((error) => handleError(error));
    },
    [gatewayApi, onSuccess, onError]
  );

  const triggerCloudEvent = useCallback(
    (event: CloudEventRequest) => {
      return gatewayApi
        .triggerCloudEvent(event)
        .then((response) => {
          console.log(response);
          onSuccess("The CloudEvent has been successfully triggered.");
        })
        .catch((error) => handleError(error));
    },
    [gatewayApi, onSuccess, onError]
  );

  const handleError = useCallback(
    (error) => {
      const message = error?.message || "Unknown error. More details in the developer tools console.";
      onError(message);
    },
    [gatewayApi, onSuccess, onError]
  );

  return (
    <EmbeddedCloudEventForm
      {...componentOuiaProps(ouiaId, "cloud-event-form-container", ouiaSafe)}
      targetOrigin={appContext.getDevUIUrl()}
      isNewInstanceEvent={isTriggerNewInstance}
      defaultValues={{
        cloudEventSource: "/local/quarkus-devUi",
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
