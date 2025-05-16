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
import { useState, useEffect, useCallback, ReactElement } from "react";
import { Card } from "@patternfly/react-core/dist/js/components/Card";
import { Bullseye } from "@patternfly/react-core/dist/js/layouts/Bullseye";
import { ProcessDefinition } from "@kie-tools/runtime-tools-process-gateway-api/dist/types";
import { ServerErrors } from "@kie-tools/runtime-tools-components/dist/components/ServerErrors";
import { KogitoSpinner } from "@kie-tools/runtime-tools-components/dist/components/KogitoSpinner";
import { useProcessFormChannelApi } from "@kie-tools/runtime-tools-process-webapp-components/dist/ProcessForm";
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
import { useProcessDefinitionsListChannelApi } from "@kie-tools/runtime-tools-process-webapp-components/dist/ProcessDefinitionsList";
import {
  Action,
  FormNotification,
  Notification,
} from "@kie-tools/runtime-tools-components/dist/components/FormNotification";

interface Props {
  processName: string;
  onReturnToProcessDefinitionsList: () => void;
  onNavigateToProcessInstanceDetails?: (processInstanceId: string) => void;
}

export const ProcessDefinitionForm: React.FC<Props> = ({
  processName,
  onReturnToProcessDefinitionsList,
  onNavigateToProcessInstanceDetails,
}) => {
  const processFormChannelApi = useProcessFormChannelApi();
  const processDefinitionChannelApi = useProcessDefinitionsListChannelApi();
  const [processDefinition, setProcessDefinition] = useState<ProcessDefinition>();
  const [isLoading, setIsLoading] = useState<boolean>(true);
  const [error, setError] = useState<ReactElement | string>();
  const [notification, setNotification] = useState<Notification>();

  const showNotification = useCallback(
    (
      notificationType: "error" | "success",
      submitMessage: string,
      notificationDetails?: ReactElement | string,
      customActions?: Action[]
    ) => {
      setNotification({
        type: notificationType,
        message: submitMessage,
        details: notificationDetails,
        customActions,
        close: () => {
          setNotification(undefined);
        },
      });
    },
    []
  );

  useEffect(() => {
    const unsubscriber = processFormChannelApi.processForm__onStartProcessListen({
      onSuccess(processInstanceId) {
        processFormChannelApi.processForm__setBusinessKey("");
        const message = `The process with id: ${processInstanceId} has started successfully`;
        showNotification("success", message, undefined, [
          {
            label: "Go to Process Instance",
            onClick: () => {
              setNotification(undefined);
              onNavigateToProcessInstanceDetails?.(processInstanceId);
            },
          },
        ]);
      },
      onError(error) {
        const message = "Failed to start the process.";
        const details = error.response ? (
          <>
            <b>
              {error.response.statusText}: {error.message}
            </b>
            {error.response.data?.message && (
              <>
                <br />
                {error.response.data.message}
              </>
            )}
          </>
        ) : (
          error.message
        );
        showNotification("error", message, details);
      },
    });

    return () => {
      unsubscriber.then((unsubscribeHandler) => unsubscribeHandler.unSubscribe());
    };
  }, [onNavigateToProcessInstanceDetails, processFormChannelApi, showNotification]);

  useCancelableEffect(
    useCallback(
      ({ canceled }) => {
        if (canceled.get()) {
          return;
        }
        setIsLoading(true);
        processDefinitionChannelApi
          .processDefinitionsList__getProcessDefinitionByName(processName)
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
      [processDefinitionChannelApi, processName]
    )
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
    return <ServerErrors error={error} variant="large" onGoBack={() => setError(undefined)} />;
  }

  if (processDefinition) {
    return (
      <>
        {notification && (
          <div style={{ marginBottom: "16px" }}>
            <FormNotification notification={notification} />
          </div>
        )}
        <EmbeddedProcessForm
          channelApi={processFormChannelApi}
          targetOrigin={window.location.origin}
          processDefinition={processDefinition}
          customFormDisplayerEnvelopePath="/resources/form-displayer.html"
          shouldLoadCustomForms={false}
        />
      </>
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
