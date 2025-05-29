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

import React, { useEffect, useRef, useState } from "react";
import { Card, CardBody } from "@patternfly/react-core/dist/js/components/Card";
import { PageSection } from "@patternfly/react-core/dist/js/components/Page";
import {
  OUIAProps,
  ouiaPageTypeAndObjectId,
  componentOuiaProps,
} from "@kie-tools/runtime-tools-components/dist/ouiaTools";
import { WorkflowFormContainer } from "@kie-tools/runtime-tools-swf-webapp-components/dist/WorkflowFormContainer";
import "../../styles.css";
import { PageTitle } from "@kie-tools/runtime-tools-components/dist/components/PageTitle";
import { FormNotification, Notification } from "@kie-tools/runtime-tools-components/dist/components/FormNotification";
import { useLocation, useNavigate } from "react-router-dom";
import { InlineEdit, InlineEditApi } from "@kie-tools/runtime-tools-components/dist/components/InlineEdit";
import { useWorkflowFormGatewayApi } from "@kie-tools/runtime-tools-swf-webapp-components/dist/WorkflowForm";
import { WorkflowDefinition } from "@kie-tools/runtime-tools-swf-gateway-api/dist/types";
import { useDevUIAppContext } from "../../contexts/DevUIAppContext";

interface WorkflowFormPageState {
  workflowDefinition: WorkflowDefinition;
}

const WorkflowFormPage: React.FC<OUIAProps> = ({ ouiaId, ouiaSafe }) => {
  const [notification, setNotification] = useState<Notification>();
  const inlineEditRef = useRef<InlineEditApi | null>(null);

  const navigate = useNavigate();
  const location = useLocation();
  const gatewayApi = useWorkflowFormGatewayApi();
  const apiContext = useDevUIAppContext();

  const initialState = location.state as WorkflowFormPageState;

  const workflowDefinition: WorkflowDefinition | undefined = initialState.workflowDefinition;

  const goToWorkflowList = () => {
    navigate("/Workflows");
  };

  const showNotification = (
    notificationType: "error" | "success",
    submitMessage: string,
    notificationDetails?: string
  ) => {
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
  };

  const onSubmitSuccess = (message: string): void => {
    showNotification("success", `A workflow with id ${message} was started successfully.`);
  };

  const onSubmitError = (details?: string) => {
    const message = "Failed to trigger workflow.";
    showNotification("error", message, details);
  };

  const onResetForm = () => {
    gatewayApi.setBusinessKey("");
    inlineEditRef.current?.reset();
  };

  const getBusinessKey = () => {
    return gatewayApi.getBusinessKey();
  };

  useEffect(() => {
    onResetForm();
    return ouiaPageTypeAndObjectId("workflow-form");
  }, []);

  return (
    <React.Fragment>
      <PageSection
        {...componentOuiaProps(`title${ouiaId ? "-" + ouiaId : ""}`, "workflow-form-page-section", ouiaSafe)}
        variant="light"
      >
        <PageTitle
          title={`Start New Workflow`}
          extra={
            <InlineEdit
              ref={inlineEditRef}
              setBusinessKey={(bk) => gatewayApi.setBusinessKey(bk)}
              getBusinessKey={getBusinessKey}
            />
          }
        />
        {notification && (
          <div>
            <FormNotification notification={notification} />
          </div>
        )}
      </PageSection>
      <PageSection
        {...componentOuiaProps(`content${ouiaId ? "-" + ouiaId : ""}`, "workflow-form-page-section", ouiaSafe)}
      >
        <Card className="Dev-ui__card-size">
          <CardBody className="pf-v5-u-h-100">
            <WorkflowFormContainer
              workflowDefinitionData={workflowDefinition}
              onResetForm={onResetForm}
              onStartWorkflowError={onSubmitError}
              onStartWorkflowSuccess={onSubmitSuccess}
              targetOrigin={apiContext.getDevUIUrl()}
            />
          </CardBody>
        </Card>
      </PageSection>
    </React.Fragment>
  );
};

export default WorkflowFormPage;
