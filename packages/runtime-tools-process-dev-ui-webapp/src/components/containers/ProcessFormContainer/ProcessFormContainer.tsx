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
import React from "react";
import { OUIAProps, componentOuiaProps } from "@kie-tools/runtime-tools-components/dist/ouiaTools";
import { useDevUIAppContext } from "../../contexts/DevUIAppContext";
import { ProcessDefinition } from "@kie-tools/runtime-tools-process-gateway-api/dist/types";
import { EmbeddedProcessForm } from "@kie-tools/runtime-tools-process-enveloped-components/dist/processForm";
import {
  ProcessFormGatewayApi,
  useProcessFormGatewayApi,
} from "@kie-tools/runtime-tools-process-webapp-components/dist/ProcessForm";
import { Form } from "@kie-tools/runtime-tools-shared-gateway-api/dist/types";

interface ProcessFormContainerProps {
  processDefinitionData: ProcessDefinition;
  onSubmitSuccess: (id: string) => void;
  onSubmitError: (details?: string) => void;
}
const ProcessFormContainer: React.FC<ProcessFormContainerProps & OUIAProps> = ({
  processDefinitionData,
  onSubmitSuccess,
  onSubmitError,
  ouiaId,
  ouiaSafe,
}) => {
  const gatewayApi: ProcessFormGatewayApi = useProcessFormGatewayApi();
  const appContext = useDevUIAppContext();
  return (
    <EmbeddedProcessForm
      {...componentOuiaProps(ouiaId, "process-form-container", ouiaSafe)}
      processDefinition={processDefinitionData}
      driver={{
        getProcessFormSchema(processDefinitionData: ProcessDefinition): Promise<any> {
          return gatewayApi.getProcessFormSchema(processDefinitionData);
        },
        getCustomForm(processDefinitionData: ProcessDefinition): Promise<Form> {
          return gatewayApi.getCustomForm(processDefinitionData);
        },
        getProcessDefinitionSvg(processDefinition: ProcessDefinition): Promise<string> {
          return gatewayApi.getProcessDefinitionSvg(processDefinition);
        },
        async startProcess(formData: any): Promise<string> {
          return gatewayApi
            .startProcess(formData, processDefinitionData)
            .then((id: string) => {
              gatewayApi.setBusinessKey("");
              onSubmitSuccess(id);
              return id;
            })
            .catch((error) => {
              const message = error.response ? `${error.response.statusText} : ${error.message}` : error.message;
              onSubmitError(message);
              return "";
            });
        },
      }}
      targetOrigin={appContext.getDevUIUrl()}
      customFormDisplayerEnvelopePath="resources/form-displayer.html"
      shouldLoadCustomForms={true}
    />
  );
};

export default ProcessFormContainer;
