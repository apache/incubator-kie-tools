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

import React, { useState, useEffect } from "react";
import { Card } from "@patternfly/react-core/dist/js/components/Card";
import { PageSection } from "@patternfly/react-core/dist/js/components/Page";
import { Bullseye } from "@patternfly/react-core/dist/js/layouts/Bullseye";
import { ServerErrors } from "@kie-tools/runtime-tools-components/dist/components/ServerErrors";
import { KogitoSpinner } from "@kie-tools/runtime-tools-components/dist/components/KogitoSpinner";
import {
  OUIAProps,
  ouiaPageTypeAndObjectId,
  componentOuiaProps,
} from "@kie-tools/runtime-tools-components/dist/ouiaTools";
import { useLocation, useNavigate, useParams } from "react-router-dom";
import { PageSectionHeader } from "@kie-tools/runtime-tools-components/dist/components/PageSectionHeader";
import { WorkflowDetailsContainer } from "@kie-tools/runtime-tools-swf-webapp-components/dist/WorkflowDetailsContainer";
import {
  WorkflowDetailsGatewayApi,
  useWorkflowDetailsGatewayApi,
} from "@kie-tools/runtime-tools-swf-webapp-components/dist/WorkflowDetails/";
import "../../styles.css";
import { WorkflowInstance } from "@kie-tools/runtime-tools-swf-gateway-api/dist/types";
import { useDevUIAppContext } from "../../contexts/DevUIAppContext";

const WorkflowDetailsPage: React.FC<OUIAProps> = ({ ouiaId, ouiaSafe, ...props }) => {
  useEffect(() => {
    return ouiaPageTypeAndObjectId("workflow-details");
  });

  const gatewayApi: WorkflowDetailsGatewayApi = useWorkflowDetailsGatewayApi();
  const appContext = useDevUIAppContext();

  const navigate = useNavigate();
  const { instanceID: workflowId } = useParams<{ instanceID: string }>();
  const location = useLocation();
  const [workflowInstance, setWorkflowInstance] = useState<WorkflowInstance>({} as WorkflowInstance);
  const [isLoading, setIsLoading] = useState<boolean>(false);
  const [fetchError, setFetchError] = useState<string>("");
  let currentPage = JSON.parse(window.localStorage.getItem("state") ?? "null");
  useEffect(() => {
    window.onpopstate = () => {
      navigate({}, { state: Object.assign({}, location.state) });
    };
  });

  async function fetchDetails() {
    let response: WorkflowInstance = {} as WorkflowInstance;
    let responseError: string = "";
    try {
      setIsLoading(true);
      response = await gatewayApi.workflowDetailsQuery(workflowId!);
      setWorkflowInstance(response);
    } catch (error) {
      responseError = error;
      setFetchError(error);
    } finally {
      setIsLoading(false);
      if (responseError.length === 0 && fetchError.length === 0 && Object.keys(response).length === 0) {
        let prevPath;
        if (currentPage) {
          currentPage = Object.assign({}, currentPage, location.state);
          const tempPath = currentPage.prev.split("/");
          prevPath = tempPath.filter((item: string) => item);
        }
        navigate(
          {
            pathname: "/NoData",
          },
          {
            state: {
              prev: currentPage ? currentPage.prev : "/WorkflowInstances",
              title: "Workflow not found",
              description: `Workflow instance with the id ${workflowId} not found`,
              buttonText: currentPage
                ? `Go to ${prevPath[0]
                    .replace(/([A-Z])/g, " $1")
                    .trim()
                    .toLowerCase()}`
                : "Go to workflow instances",
              rememberedData: Object.assign({}, location.state),
            },
          }
        );
      }
    }
  }

  useEffect(() => {
    if (workflowId) {
      fetchDetails();
    }
  }, [workflowId]);

  const renderItems = () => {
    if (!isLoading) {
      return (
        <>
          {workflowInstance && Object.keys(workflowInstance).length > 0 && !fetchError ? (
            <WorkflowDetailsContainer
              workflowInstance={workflowInstance}
              onOpenWorkflowInstanceDetails={() => {}}
              targetOrigin={appContext.getDevUIUrl()}
            />
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
        </>
      );
    } else {
      return (
        <Card>
          <KogitoSpinner spinnerText="Loading workflow details..." />
        </Card>
      );
    }
  };

  return (
    <>
      <PageSectionHeader titleText={`Workflow Details`} ouiaId={ouiaId} />
      <PageSection {...componentOuiaProps(ouiaId, "workflow-details-page-section", ouiaSafe)}>
        {renderItems()}
      </PageSection>
    </>
  );
};

export default WorkflowDetailsPage;
