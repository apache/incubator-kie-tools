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
import { useState, useEffect, useCallback, useMemo } from "react";
import { Card } from "@patternfly/react-core/dist/js/components/Card";
import { Bullseye } from "@patternfly/react-core/dist/js/layouts/Bullseye";
import { ProcessDefinition, ProcessInstance } from "@kie-tools/runtime-tools-process-gateway-api/dist/types";
import { ServerErrors } from "@kie-tools/runtime-tools-components/dist/components/ServerErrors";
import { KogitoSpinner } from "@kie-tools/runtime-tools-components/dist/components/KogitoSpinner";
import {
  useProcessFormGatewayApi,
  ProcessFormGatewayApi,
} from "@kie-tools/runtime-tools-process-webapp-components/dist/ProcessForm";
import {
  EmptyState,
  EmptyStateBody,
  EmptyStateIcon,
  EmptyStateVariant,
} from "@patternfly/react-core/dist/js/components/EmptyState";
import { SearchIcon } from "@patternfly/react-icons/dist/js/icons/search-icon";
import { Title } from "@patternfly/react-core/dist/js/components/Title";
import { Button } from "@patternfly/react-core/dist/js/components/Button";
import { useCancelableEffect } from "@kie-tools-core/react-hooks/dist/useCancelableEffect";
import { EmbeddedProcessForm } from "@kie-tools/runtime-tools-process-enveloped-components/dist/processForm";
import {
  ProcessDefinitionsListGatewayApi,
  useProcessDefinitionsListGatewayApi,
} from "@kie-tools/runtime-tools-process-webapp-components/dist/ProcessDefinitionsList";
import { Form } from "@kie-tools/runtime-tools-shared-gateway-api/dist/types";

interface Props {
  processName: string;
  onReturnToProcessDefinitionsList: () => void;
  onCreateNewProcessInstance: (processInstanceId: string) => void;
}

export const ProcessDefinitionForm: React.FC<Props> = ({
  processName,
  onReturnToProcessDefinitionsList,
  onCreateNewProcessInstance,
}) => {
  const processFormGatewayApi: ProcessFormGatewayApi = useProcessFormGatewayApi();
  const processDefinitionGatewayApi: ProcessDefinitionsListGatewayApi = useProcessDefinitionsListGatewayApi();
  const [processDefinition, setProcessDefinition] = useState<ProcessDefinition>();
  const [isLoading, setIsLoading] = useState<boolean>(true);
  const [error, setError] = useState<string>();

  useCancelableEffect(
    useCallback(
      ({ canceled }) => {
        if (canceled.get()) {
          return;
        }
        setIsLoading(true);
        processDefinitionGatewayApi
          .getProcessDefinitionByName(processName)
          .then((response) => {
            if (canceled.get()) {
              return;
            }
            setProcessDefinition(response);
          })
          .catch((error) => {
            setError(error);
          })
          .finally(() => {
            setIsLoading(false);
          });
      },
      [processDefinitionGatewayApi, processName]
    )
  );

  const processFormDriver = useMemo(
    () => ({
      getProcessFormSchema(processDefinitionData: ProcessDefinition): Promise<any> {
        return processFormGatewayApi.getProcessFormSchema(processDefinitionData);
      },
      getCustomForm(processDefinitionData: ProcessDefinition): Promise<Form> {
        return processFormGatewayApi.getCustomForm(processDefinitionData);
      },
      getProcessDefinitionSvg(processDefinition: ProcessDefinition): Promise<string> {
        return processFormGatewayApi.getProcessDefinitionSvg(processDefinition);
      },
      startProcess(processDefinitionData: ProcessDefinition, formData: any): Promise<string> {
        return processFormGatewayApi
          .startProcess(processDefinitionData, formData)
          .then((id: string) => {
            processFormGatewayApi.setBusinessKey("");
            onCreateNewProcessInstance(id);
            return id;
          })
          .catch((error) => {
            const message = error.response ? `${error.response.statusText} : ${error.message}` : error.message;
            setError(message);
            return "";
          });
      },
    }),
    [processFormGatewayApi, onCreateNewProcessInstance]
  );

  // Loading State
  if (isLoading) {
    return (
      <Card>
        <KogitoSpinner spinnerText="Loading Process Definition..." />
      </Card>
    );
  }

  // Error State
  if (error) {
    return (
      <Bullseye>
        <ServerErrors error={error} variant="large" />
      </Bullseye>
    );
  }

  if (processDefinition) {
    return (
      <EmbeddedProcessForm
        driver={processFormDriver}
        targetOrigin={window.location.origin}
        processDefinition={processDefinition}
        customFormDisplayerEnvelopePath="/resources/form-displayer.html"
      />
    );
  }

  return (
    <Bullseye>
      <EmptyState variant={EmptyStateVariant.full}>
        <EmptyStateIcon icon={SearchIcon} />
        <Title headingLevel="h1" size="4xl">
          Process Definition not found
        </Title>
        <EmptyStateBody>{`Process Definition with the name ${processName} not found`}</EmptyStateBody>
        <Button variant="primary" onClick={onReturnToProcessDefinitionsList} data-testid="redirect-button">
          Back to Process Definitions
        </Button>
      </EmptyState>
    </Bullseye>
  );
};
