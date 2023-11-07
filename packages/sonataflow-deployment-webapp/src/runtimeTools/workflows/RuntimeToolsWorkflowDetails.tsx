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

import React, { useEffect, useState } from "react";
import { PageSection } from "@patternfly/react-core/dist/js/components/Page";
import { Text, TextContent, TextVariants } from "@patternfly/react-core/dist/js/components/Text";
import { ouiaPageTypeAndObjectId } from "@kie-tools/runtime-tools-components/dist/ouiaTools";
import { useWorkflowDetailsGatewayApi, WorkflowDetailsGatewayApi } from "./WorkflowDetails";
import WorkflowDetailsContainer from "./WorkflowDetailsContainer/WorkflowDetailsContainer";
import { WorkflowInstance } from "@kie-tools/runtime-tools-gateway-api/dist/types";
import { ServerErrors } from "@kie-tools/runtime-tools-components/dist/components/ServerErrors";
import { KogitoSpinner } from "@kie-tools/runtime-tools-components/dist/components/KogitoSpinner";
import { Card } from "@patternfly/react-core/dist/js/components/Card";
import { Bullseye } from "@patternfly/react-core/dist/js/layouts/Bullseye";
import { BasePage } from "../../pages/BasePage";
import { useApp } from "../../context/AppContext";

const PAGE_TITLE = "Workflow Details";

interface WorkflowListContainerProps {
  workflowId: string;
}
export function RuntimeToolsWorkflowDetails(props: WorkflowListContainerProps) {
  const gatewayApi: WorkflowDetailsGatewayApi = useWorkflowDetailsGatewayApi();
  const app = useApp();

  const [workflowInstance, setWorkflowInstance] = useState<WorkflowInstance>({} as WorkflowInstance);
  const [isLoading, setIsLoading] = useState<boolean>(false);
  const [fetchError, setFetchError] = useState<string>("");

  useEffect(() => {
    gatewayApi
      .workflowDetailsQuery(props.workflowId)
      .then((response) => {
        if (!response) {
          return setFetchError(`There was an error opening the instance with id "${props.workflowId}".`);
        }
        setWorkflowInstance(response);
      })
      .catch((error) => {
        setFetchError(error);
      })
      .finally(() => {
        setIsLoading(false);
      });
  }, [gatewayApi, props.workflowId]);

  useEffect(() => {
    return ouiaPageTypeAndObjectId("workflow-details");
  });

  return (
    <>
      <BasePage>
        <PageSection variant={"light"}>
          <TextContent>
            <Text component={TextVariants.h1}>{PAGE_TITLE}</Text>
            <Text component={TextVariants.p}>
              Explore the execution status, details, timeline and variables of a workflow instance.
              <br />
              Your Data Index URL is: {app.fullDataIndexUrl}
            </Text>
          </TextContent>
        </PageSection>

        <PageSection isFilled aria-label="workflow-details-section">
          {isLoading && (
            <Card>
              <KogitoSpinner spinnerText="Loading workflow details..." />
            </Card>
          )}

          {!isLoading && workflowInstance && Object.keys(workflowInstance).length > 0 && !fetchError ? (
            <WorkflowDetailsContainer workflowInstance={workflowInstance} />
          ) : (
            <>
              {fetchError.length > 0 && (
                <Card className="kogito-management-console__card-size">
                  <Bullseye>
                    <ServerErrors error={fetchError} variant="large" />
                  </Bullseye>
                </Card>
              )}
            </>
          )}
        </PageSection>
      </BasePage>
    </>
  );
}
