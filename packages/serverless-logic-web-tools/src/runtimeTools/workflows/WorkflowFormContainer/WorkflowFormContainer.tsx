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
import { WorkflowDefinition } from "@kie-tools/runtime-tools-gateway-api/dist/types";
import { WorkflowFormGatewayApi, useWorkflowFormGatewayApi } from "../WorkflowForm";
import { EmbeddedWorkflowForm } from "@kie-tools/runtime-tools-enveloped-components/dist/workflowForm";
import { useGlobalAlert } from "../../../alerts/GlobalAlertsContext";
import { Alert, AlertActionCloseButton, AlertActionLink } from "@patternfly/react-core/dist/js/components/Alert";
import { useHistory } from "react-router";

interface WorkflowFormContainerProps {
  workflowDefinitionData: WorkflowDefinition;
  onResetForm: () => void;
}
const WorkflowFormContainer: React.FC<WorkflowFormContainerProps & OUIAProps> = ({
  workflowDefinitionData,
  onResetForm,
  ouiaId,
  ouiaSafe,
}) => {
  const gatewayApi: WorkflowFormGatewayApi = useWorkflowFormGatewayApi();
  const history = useHistory();

  const openWorkflowInstance = useCallback(
    (id: string) => {
      history.push({
        pathname: `/runtime-tools/workflow-details/${id}`,
      });
    },
    [history]
  );

  const startWorkflowSuccessAlert = useGlobalAlert<{ id: string }>(
    useCallback(
      ({ close }, { id }) => {
        const viewDetails = () => {
          openWorkflowInstance(id);
          close();
        };

        return (
          <Alert
            className="pf-u-mb-md"
            variant="success"
            title={`A workflow with id ${id} was started successfully.`}
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
      [openWorkflowInstance]
    ),
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

  return (
    <EmbeddedWorkflowForm
      {...componentOuiaProps(ouiaId, "workflow-form-container", ouiaSafe)}
      driver={{
        async getCustomWorkflowSchema(): Promise<Record<string, any>> {
          return gatewayApi.getCustomWorkflowSchema(workflowDefinitionData.workflowName);
        },
        async resetBusinessKey() {
          onResetForm();
        },
        async startWorkflow(endpoint: string, data: Record<string, any>): Promise<void> {
          return gatewayApi
            .startWorkflow(endpoint, data)
            .then((id: string) => {
              startWorkflowSuccessAlert.show({ id });
            })
            .catch((error: any) => {
              const message =
                error?.response?.data?.message + " " + error?.response?.data?.cause ||
                error?.message ||
                "Unknown error. More details in the developer tools console.";
              startWorkflowErrorAlert.show({ message });
            });
        },
      }}
      targetOrigin={window.location.origin}
      workflowDefinition={{
        workflowName: workflowDefinitionData.workflowName,
        endpoint: workflowDefinitionData.endpoint,
      }}
    />
  );
};

export default WorkflowFormContainer;
