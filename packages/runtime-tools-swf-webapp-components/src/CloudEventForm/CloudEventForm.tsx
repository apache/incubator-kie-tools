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

import React, { useEffect, useState } from "react";
import { FormNotification, Notification } from "@kie-tools/runtime-tools-components/dist/components/FormNotification";
import {
  OUIAProps,
  componentOuiaProps,
  ouiaPageTypeAndObjectId,
} from "@kie-tools/runtime-tools-components/dist/ouiaTools";
import { Card, CardBody } from "@patternfly/react-core/dist/js/components/Card";
import { PageSection } from "@patternfly/react-core/dist/js/components/Page";
import { Text, TextContent, TextVariants } from "@patternfly/react-core/dist/js/components/Text";
import { CloudEventFormContainer } from "../CloudEventFormContainer/CloudEventFormContainer";

interface CloudEventFormProps {
  instanceId?: string;
  cloudEventSource: string;
  isTriggerNewInstance: boolean;
  onStartWorkflowError: (error: any) => void;
  onTriggerCloudEventSuccess: () => void;
  onTriggerStartCloudEventSuccess: (businessKey: string) => void;
}

export enum CloudEventPageSource {
  DEFINITIONS = "definitions",
  INSTANCES = "instances",
}

export const CloudEventForm: React.FC<CloudEventFormProps & OUIAProps> = ({
  instanceId,
  isTriggerNewInstance,
  cloudEventSource,
  onStartWorkflowError,
  onTriggerCloudEventSuccess,
  onTriggerStartCloudEventSuccess,
  ouiaId,
  ouiaSafe,
}) => {
  const [notification, setNotification] = useState<Notification>();

  useEffect(() => {
    return ouiaPageTypeAndObjectId("trigger-cloud-event-form");
  }, []);

  return (
    <React.Fragment>
      <PageSection
        {...componentOuiaProps(`title${ouiaId ? "-" + ouiaId : ""}`, "trigger-cloud-event-form-page-section", ouiaSafe)}
        variant="light"
      >
        <TextContent>
          <Text component={TextVariants.h1}>{"Trigger Cloud Event"}</Text>
        </TextContent>
        {notification && (
          <div>
            <FormNotification notification={notification} />
          </div>
        )}
      </PageSection>
      <PageSection
        {...componentOuiaProps(`content${ouiaId ? "-" + ouiaId : ""}`, "cloud-event-form-page-section", ouiaSafe)}
      >
        <Card className="Dev-ui__card-size">
          <CardBody className="pf-u-h-100">
            <CloudEventFormContainer
              instanceId={instanceId}
              isTriggerNewInstance={isTriggerNewInstance}
              cloudEventSource={cloudEventSource}
              onStartWorkflowError={onStartWorkflowError}
              onTriggerCloudEventSuccess={onTriggerCloudEventSuccess}
              onTriggerStartCloudEventSuccess={onTriggerStartCloudEventSuccess}
            />
          </CardBody>
        </Card>
      </PageSection>
    </React.Fragment>
  );
};
