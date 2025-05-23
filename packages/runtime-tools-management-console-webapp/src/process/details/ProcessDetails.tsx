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
import { useState, useEffect, useCallback } from "react";
import { Card } from "@patternfly/react-core/dist/js/components/Card";
import { Bullseye } from "@patternfly/react-core/dist/js/layouts/Bullseye";
import { ProcessInstance } from "@kie-tools/runtime-tools-process-gateway-api/dist/types";
import { ServerErrors } from "@kie-tools/runtime-tools-components/dist/components/ServerErrors";
import { KogitoSpinner } from "@kie-tools/runtime-tools-components/dist/components/KogitoSpinner";
import {
  EmptyState,
  EmptyStateBody,
  EmptyStateIcon,
  EmptyStateVariant,
  EmptyStateHeader,
  EmptyStateFooter,
} from "@patternfly/react-core/dist/js/components/EmptyState";
import { SearchIcon } from "@patternfly/react-icons/dist/js/icons/search-icon";
import { Button } from "@patternfly/react-core/dist/js/components/Button";
import { useCancelableEffect } from "@kie-tools-core/react-hooks/dist/useCancelableEffect";
import { EmbeddedProcessDetails } from "@kie-tools/runtime-tools-process-enveloped-components/dist/processDetails";
import { useProcessDetailsChannelApi } from "@kie-tools/runtime-tools-process-webapp-components/dist/ProcessDetails";
import { useRuntimeSpecificRoutes } from "../../runtime/RuntimeContext";
import { useNavigate } from "react-router-dom";

interface Props {
  processInstanceId: string;
  onReturnToProcessList: () => void;
}

export const ProcessDetails: React.FC<Props> = ({ processInstanceId, onReturnToProcessList }) => {
  const channelApi = useProcessDetailsChannelApi();
  const runtimeRoutes = useRuntimeSpecificRoutes();
  const navigate = useNavigate();
  const [processInstance, setProcessInstance] = useState<ProcessInstance>();
  const [isLoading, setIsLoading] = useState<boolean>(true);
  const [error, setError] = useState<string>();

  useCancelableEffect(
    useCallback(
      ({ canceled }) => {
        if (canceled.get()) {
          return;
        }
        setIsLoading(true);
        channelApi
          .processDetails__getProcessDetails(processInstanceId)
          .then((response) => {
            if (canceled.get()) {
              return;
            }
            setProcessInstance(response);
          })
          .catch((error) => {
            setError(error);
          })
          .finally(() => {
            setIsLoading(false);
          });
      },
      [channelApi, processInstanceId]
    )
  );

  useEffect(() => {
    const unsubscriber = channelApi.processDetails__onOpenProcessInstanceDetailsListener({
      onOpen(id: string) {
        navigate(runtimeRoutes.processDetails(id));
      },
    });

    return () => {
      unsubscriber.then((unsubscribeHandler) => unsubscribeHandler.unSubscribe());
    };
  }, [channelApi, navigate, runtimeRoutes]);

  // Loading State
  if (isLoading) {
    return (
      <Card>
        <KogitoSpinner spinnerText="Loading process details..." />
      </Card>
    );
  }

  // Error State
  if (error) {
    return <ServerErrors error={error} variant="large" onGoBack={() => navigate(runtimeRoutes.processes())} />;
  }

  // Process Instance Details
  if (processInstance) {
    return (
      <EmbeddedProcessDetails
        channelApi={channelApi}
        targetOrigin={window.location.origin}
        processInstance={processInstance}
        singularProcessLabel={"process"}
        pluralProcessLabel={"processes"}
      />
    );
  }

  return (
    <Bullseye>
      <EmptyState variant={EmptyStateVariant.full}>
        <EmptyStateHeader
          titleText="Process instance not found"
          icon={<EmptyStateIcon icon={SearchIcon} />}
          headingLevel="h1"
        />
        <EmptyStateBody>{`Process instance with the id ${processInstanceId} not found`}</EmptyStateBody>
        <EmptyStateFooter>
          <Button variant="primary" onClick={onReturnToProcessList} data-testid="redirect-button">
            Back to Process Instances
          </Button>
        </EmptyStateFooter>
      </EmptyState>
    </Bullseye>
  );
};
