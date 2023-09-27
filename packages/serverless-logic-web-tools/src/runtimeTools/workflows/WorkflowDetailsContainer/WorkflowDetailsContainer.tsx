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

import React, { useEffect } from "react";
import { useHistory } from "react-router-dom";
import { componentOuiaProps, OUIAProps } from "@kie-tools/runtime-tools-components/dist/ouiaTools";
import { EmbeddedWorkflowDetails } from "@kie-tools/runtime-tools-enveloped-components/dist/workflowDetails/embedded";
import { WorkflowDetailsGatewayApi, useWorkflowDetailsGatewayApi } from "../WorkflowDetails";
import { WorkflowInstance } from "@kie-tools/runtime-tools-gateway-api/dist/types";
import { routes } from "../../../navigation/Routes";

interface WorkflowListContainerProps {
  workflowInstance: WorkflowInstance;
}

const WorkflowDetailsContainer: React.FC<WorkflowListContainerProps & OUIAProps> = ({
  workflowInstance,
  ouiaId,
  ouiaSafe,
}) => {
  const history = useHistory();
  const gatewayApi: WorkflowDetailsGatewayApi = useWorkflowDetailsGatewayApi();

  useEffect(() => {
    const unSubscribeHandler = gatewayApi.onOpenWorkflowInstanceDetailsListener({
      onOpen(id: string) {
        history.push(`/`);
        history.push(routes.runtimeToolsWorkflowDetails.path({ workflowId: id }));
      },
    });
    return () => {
      unSubscribeHandler.unSubscribe();
    };
  }, []);

  return (
    <EmbeddedWorkflowDetails
      {...componentOuiaProps(ouiaId, "workflow-list-container", ouiaSafe)}
      driver={gatewayApi}
      targetOrigin={window.location.origin}
      workflowInstance={workflowInstance}
    />
  );
};

export default WorkflowDetailsContainer;
