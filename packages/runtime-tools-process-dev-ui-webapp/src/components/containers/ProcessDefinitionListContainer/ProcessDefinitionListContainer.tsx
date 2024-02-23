/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
import React, { useEffect } from "react";
import {
  ProcessDefinitionListGatewayApi,
  useProcessDefinitionListGatewayApi,
} from "../../../channel/ProcessDefinitionList";
import { useHistory } from "react-router-dom";
import { useDevUIAppContext } from "../../contexts/DevUIAppContext";
import { OUIAProps, componentOuiaProps } from "@kie-tools/runtime-tools-components/dist/ouiaTools";
import { ProcessDefinition } from "@kie-tools/runtime-tools-process-gateway-api/dist/types";
import { EmbeddedProcessDefinitionList } from "@kie-tools/runtime-tools-process-enveloped-components/dist/processDefinitionList";

const ProcessDefinitionListContainer: React.FC<OUIAProps> = ({ ouiaId, ouiaSafe }) => {
  const history = useHistory();
  const gatewayApi: ProcessDefinitionListGatewayApi = useProcessDefinitionListGatewayApi();
  const appContext = useDevUIAppContext();

  useEffect(() => {
    const onOpenProcess = {
      onOpen(processDefinition: ProcessDefinition) {
        history.push({
          pathname: `ProcessDefinition/Form/${processDefinition.processName}`,
          state: {
            processDefinition: processDefinition,
          },
        });
      },
    };

    const onOpenInstanceUnsubscriber = gatewayApi.onOpenProcessFormListen(onOpenProcess);

    return () => {
      onOpenInstanceUnsubscriber.unSubscribe();
    };
  }, []);

  return (
    <EmbeddedProcessDefinitionList
      {...componentOuiaProps(ouiaId, "process-definition-list-container", ouiaSafe)}
      driver={gatewayApi}
      targetOrigin={appContext.getDevUIUrl()}
      singularProcessLabel={appContext.customLabels.singularProcessLabel}
    />
  );
};

export default ProcessDefinitionListContainer;
