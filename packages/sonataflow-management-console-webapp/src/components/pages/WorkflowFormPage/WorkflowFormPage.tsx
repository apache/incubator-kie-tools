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

import React, { useCallback, useEffect, useRef } from "react";
import { InlineEdit, InlineEditApi } from "@kie-tools/runtime-tools-components/dist/components/InlineEdit";
import { ouiaPageTypeAndObjectId } from "@kie-tools/runtime-tools-components/dist/ouiaTools";
import { WorkflowDefinition } from "@kie-tools/runtime-tools-swf-gateway-api/dist/types";
import {
  WorkflowFormGatewayApi,
  useWorkflowFormGatewayApi,
} from "@kie-tools/runtime-tools-swf-webapp-components/dist/WorkflowForm";
import { WorkflowFormContainer } from "@kie-tools/runtime-tools-swf-webapp-components/dist/WorkflowFormContainer";
import { Alert, AlertActionCloseButton, AlertActionLink } from "@patternfly/react-core/dist/js/components/Alert";
import { Card, CardBody } from "@patternfly/react-core/dist/js/components/Card";
import { Page, PageSection } from "@patternfly/react-core/dist/js/components/Page";
import { Text, TextContent, TextVariants } from "@patternfly/react-core/dist/js/components/Text";
import { useLocation, useNavigate } from "react-router-dom";
import { routes } from "../../../navigation/Routes";
import { useGlobalAlert } from "../../../alerts/GlobalAlertsContext";

const PAGE_TITLE = "Start new workflow";

export function WorkflowFormPage() {
  const navigate = useNavigate();
  const location = useLocation();
  const gatewayApi: WorkflowFormGatewayApi = useWorkflowFormGatewayApi();

  const inlineEditRef = useRef<InlineEditApi>(null);

  const workflowDefinition: WorkflowDefinition = (location.state as any)["workflowDefinition"];

  const onResetForm = useCallback(() => {
    gatewayApi.setBusinessKey("");
    inlineEditRef.current!.reset();
  }, [gatewayApi]);

  const getBusinessKey = () => {
    return gatewayApi.getBusinessKey();
  };

  useEffect(() => {
    onResetForm();
    return ouiaPageTypeAndObjectId("workflow-form");
  }, [onResetForm]);

  const startWorkflowSuccessAlert = useGlobalAlert<{ workflowId: string }>(
    useCallback(
      ({ close }, { workflowId }) => {
        const viewDetails = () => {
          navigate({
            pathname: routes.runtimeToolsWorkflowDetails.path({ workflowId }),
          });
          close();
        };

        return (
          <Alert
            className="pf-v5-u-mb-md"
            variant="success"
            title={`A workflow with id ${workflowId} was started successfully.`}
            aria-live="polite"
            actionClose={<AlertActionCloseButton onClose={close} />}
            actionLinks={
              <>
                <AlertActionLink onClick={viewDetails}>{"View details"}</AlertActionLink>
                <AlertActionLink onClick={close}>{"Ignore"}</AlertActionLink>
              </>
            }
          />
        );
      },
      [navigate]
    ),
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
    (error: any) => {
      const message =
        error?.response?.data?.message + " " + error?.response?.data?.cause ||
        error?.message ||
        "Unknown error. More details in the developer tools console.";
      startWorkflowErrorAlert.show({ message });
    },
    [startWorkflowErrorAlert]
  );

  const onStartWorkflowSuccess = useCallback(
    (workflowId: string) => {
      startWorkflowSuccessAlert.show({ workflowId });
    },
    [startWorkflowSuccessAlert]
  );

  return (
    <Page>
      <PageSection variant={"light"}>
        <TextContent>
          <Text component={TextVariants.h1}>{PAGE_TITLE}</Text>
          <InlineEdit
            ref={inlineEditRef}
            setBusinessKey={(bk: string) => gatewayApi.setBusinessKey(bk)}
            getBusinessKey={getBusinessKey}
          />
        </TextContent>
      </PageSection>

      <PageSection isFilled aria-label="workflow-definitions-section">
        <Card>
          <CardBody>
            <WorkflowFormContainer
              workflowDefinitionData={workflowDefinition}
              onResetForm={onResetForm}
              onStartWorkflowError={onStartWorkflowError}
              onStartWorkflowSuccess={onStartWorkflowSuccess}
            />
          </CardBody>
        </Card>
      </PageSection>
    </Page>
  );
}
