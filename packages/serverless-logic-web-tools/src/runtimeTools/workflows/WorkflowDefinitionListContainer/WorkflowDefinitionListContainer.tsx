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
import { WorkflowDefinition } from "@kie-tools/runtime-tools-gateway-api/dist/types";
import { useWorkflowDefinitionListGatewayApi, WorkflowDefinitionListGatewayApi } from "../WorkflowDefinitionList";
import { EmbeddedWorkflowDefinitionList } from "@kie-tools/runtime-tools-enveloped-components/dist/workflowDefinitions";
import { routes } from "../../../navigation/Routes";

const WorkflowDefinitionListContainer: React.FC<OUIAProps> = ({ ouiaId, ouiaSafe }) => {
  const history = useHistory();
  const gatewayApi: WorkflowDefinitionListGatewayApi = useWorkflowDefinitionListGatewayApi();

  useEffect(() => {
    const onOpenDefinitionUnsubscriber = gatewayApi.onOpenWorkflowFormListen({
      onOpen(workflowDefinition: WorkflowDefinition) {
        history.push({
          pathname: routes.runtimeToolsWorkflowForm.path({ workflowName: workflowDefinition.workflowName }),
          state: {
            workflowDefinition: {
              workflowName: workflowDefinition.workflowName,
              endpoint: workflowDefinition.endpoint,
            },
          },
        });
      },
    });

    return () => {
      onOpenDefinitionUnsubscriber.unSubscribe();
    };
  }, []);

  return (
    <EmbeddedWorkflowDefinitionList
      {...componentOuiaProps(ouiaId, "workflow-definitions-container", ouiaSafe)}
      driver={gatewayApi}
      targetOrigin={window.location.origin}
    />
  );
};

export default WorkflowDefinitionListContainer;
