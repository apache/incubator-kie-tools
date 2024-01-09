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

import React from "react";
import { componentOuiaProps, OUIAProps } from "@kie-tools/runtime-tools-components/dist/ouiaTools";
import { WorkflowDefinition } from "@kie-tools/runtime-tools-gateway-api/dist/types";
import { WorkflowFormGatewayApi, useWorkflowFormGatewayApi } from "../WorkflowForm";
import { EmbeddedWorkflowForm } from "@kie-tools/runtime-tools-enveloped-components/dist/workflowForm";

interface WorkflowFormContainerProps {
  workflowDefinitionData: WorkflowDefinition;
  onResetForm: () => void;
  onStartWorkflowSuccess: (workflowId: string) => void;
  onStartWorkflowError: (error: any) => void;
  targetOrigin?: string;
}
export const WorkflowFormContainer: React.FC<WorkflowFormContainerProps & OUIAProps> = ({
  workflowDefinitionData,
  onStartWorkflowSuccess,
  onStartWorkflowError,
  onResetForm,
  ouiaId,
  ouiaSafe,
  targetOrigin,
}) => {
  const gatewayApi: WorkflowFormGatewayApi = useWorkflowFormGatewayApi();

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
              onStartWorkflowSuccess(id);
            })
            .catch((error: any) => {
              onStartWorkflowError(error);
            });
        },
      }}
      targetOrigin={targetOrigin || window.location.origin}
      workflowDefinition={{
        workflowName: workflowDefinitionData.workflowName,
        endpoint: workflowDefinitionData.endpoint,
      }}
    />
  );
};
