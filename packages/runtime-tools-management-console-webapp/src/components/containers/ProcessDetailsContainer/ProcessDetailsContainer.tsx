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
import * as React from "react";
import { useEffect } from "react";
import { useHistory } from "react-router-dom";
import { OUIAProps, componentOuiaProps } from "@kie-tools/runtime-tools-components/dist/ouiaTools";
import { ProcessInstance } from "@kie-tools/runtime-tools-process-gateway-api/dist/types";
import {
  ProcessDetailsGatewayApi,
  useProcessDetailsGatewayApi,
} from "@kie-tools/runtime-tools-process-webapp-components/dist/ProcessDetails";
import { EmbeddedProcessDetails } from "@kie-tools/runtime-tools-process-enveloped-components/dist/processDetails";

interface ProcessDetailsContainerProps {
  processInstance: ProcessInstance;
}

const ProcessDetailsContainer: React.FC<ProcessDetailsContainerProps & OUIAProps> = ({
  processInstance,
  ouiaId,
  ouiaSafe,
}) => {
  const history = useHistory();
  const gatewayApi: ProcessDetailsGatewayApi = useProcessDetailsGatewayApi();
  useEffect(() => {
    const unSubscribeHandler = gatewayApi.onOpenProcessInstanceDetailsListener({
      onOpen(id: string) {
        history.push(`/`);
        history.push(`/Process/${id}`);
      },
    });

    return () => {
      unSubscribeHandler.unSubscribe();
    };
  }, [processInstance]);

  return (
    <EmbeddedProcessDetails
      {...componentOuiaProps(ouiaId, "process-details-container", ouiaSafe)}
      driver={gatewayApi}
      targetOrigin={window.location.origin}
      processInstance={processInstance}
      singularProcessLabel={"process"}
      pluralProcessLabel={"processes"}
    />
  );
};

export default ProcessDetailsContainer;
