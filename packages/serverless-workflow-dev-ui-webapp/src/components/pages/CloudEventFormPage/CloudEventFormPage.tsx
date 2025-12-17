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

import React, { useCallback, useEffect, useMemo, useState } from "react";
import { Card, CardBody } from "@patternfly/react-core/dist/js/components/Card";
import { PageSection } from "@patternfly/react-core/dist/js/components/Page";
import {
  OUIAProps,
  ouiaPageTypeAndObjectId,
  componentOuiaProps,
} from "@kie-tools/runtime-tools-components/dist/ouiaTools";
import "../../styles.css";
import { PageTitle } from "@kie-tools/runtime-tools-components/dist/components/PageTitle";
import { FormNotification, Notification } from "@kie-tools/runtime-tools-components/dist/components/FormNotification";
import { useLocation, useNavigate } from "react-router-dom";
import { CloudEventFormContainer } from "@kie-tools/runtime-tools-swf-webapp-components/dist/CloudEventFormContainer";
import { useDevUIAppContext } from "../../contexts/DevUIAppContext";

export interface CloudEventPageState {
  source?: CloudEventPageSource;
}

export enum CloudEventPageSource {
  DEFINITIONS = "definitions",
  INSTANCES = "instances",
}

const CloudEventFormPage: React.FC<OUIAProps> = ({ ouiaId, ouiaSafe }) => {
  const [notification, setNotification] = useState<Notification>();

  const context = useDevUIAppContext();

  const navigate = useNavigate();
  const location = useLocation();

  const initialState = location && (location.state as CloudEventPageState);

  const isTriggerNewInstance = useMemo(() => {
    const source = initialState.source;
    return source !== CloudEventPageSource.INSTANCES;
  }, [initialState.source]);

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
              navigate("/Workflows");
            },
          },
        ],
        close: () => {
          setNotification(undefined);
        },
      });
    },
    []
  );

  const onSubmitSuccess = useCallback((): void => {
    showNotification("success", "The CloudEvent has been successfully triggered.");
  }, []);

  const onSubmitError = useCallback((details?: string) => {
    const message = "Failed to trigger workflow.";
    showNotification("error", message, details);
  }, []);

  useEffect(() => {
    return ouiaPageTypeAndObjectId("trigger-cloud-event-form");
  }, []);

  return (
    <React.Fragment>
      <PageSection
        {...componentOuiaProps(`title${ouiaId ? "-" + ouiaId : ""}`, "trigger-cloud-event-form-page-section", ouiaSafe)}
        variant="light"
      >
        <PageTitle title={`Trigger Cloud Event`} />
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
          <CardBody className="pf-v5-u-h-100">
            <CloudEventFormContainer
              isTriggerNewInstance={isTriggerNewInstance}
              cloudEventSource={"/local/devui"}
              onStartWorkflowError={(details) => onSubmitError(details)}
              onTriggerCloudEventSuccess={() => onSubmitSuccess()}
              onTriggerStartCloudEventSuccess={() => onSubmitSuccess()}
              serviceUrl={context.getDevUIUrl()}
              targetOrigin={context.getDevUIUrl()}
            />
          </CardBody>
        </Card>
      </PageSection>
    </React.Fragment>
  );
};

export default CloudEventFormPage;
