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

import React from "react";
import { MemoryRouter, Route, Routes } from "react-router-dom";
import { ApolloProvider } from "react-apollo";
import { ApolloClient } from "apollo-client";
import { PageLayout } from "@kie-tools/runtime-tools-components/dist/components/PageLayout";
import DevUINav from "../DevUINav/DevUINav";
import { WorkflowDetailsContextProviderWithApolloClient } from "@kie-tools/runtime-tools-swf-webapp-components/dist/WorkflowDetails";
import { WorkflowListContextProviderWithApolloClient } from "@kie-tools/runtime-tools-swf-webapp-components/dist/WorkflowList";
import FormsListContextProvider from "../../../channel/FormsList/FormsListContextProvider";
import FormDetailsContextProvider from "../../../channel/FormDetails/FormDetailsContextProvider";
import DevUIAppContextProvider from "../../contexts/DevUIAppContextProvider";
import { DiagramPreviewSize } from "@kie-tools/runtime-tools-swf-enveloped-components/dist/workflowDetails/api";
import { WorkflowFormContextProvider } from "@kie-tools/runtime-tools-swf-webapp-components/dist/WorkflowForm";
import CustomDashboardListContextProvider from "../../../channel/CustomDashboardList/CustomDashboardListContextProvider";
import { CustomDashboardViewContextProvider } from "../../../channel/CustomDashboardView";
import { CloudEventFormContextProvider } from "@kie-tools/runtime-tools-swf-webapp-components/dist/CloudEventForm";
import { WorkflowDefinitionListContextProviderWithApolloClient } from "@kie-tools/runtime-tools-swf-webapp-components/dist/WorkflowDefinitionList";

interface IOwnProps {
  apolloClient: ApolloClient<any>;
  availablePages?: string[];
  children: React.ReactElement;
  devUIUrl: string;
  diagramPreviewSize?: DiagramPreviewSize;
  isLocalCluster?: boolean;
  isStunnerEnabled: boolean;
  isWorkflowEnabled: boolean;
  omittedWorkflowTimelineEvents?: string[];
  openApiBaseUrl: string;
  openApiPath: string;
}

const DevUILayout: React.FC<IOwnProps> = ({
  apolloClient,
  availablePages,
  children,
  devUIUrl,
  diagramPreviewSize,
  isLocalCluster,
  isStunnerEnabled,
  isWorkflowEnabled: isWorkflowEnabled,
  omittedWorkflowTimelineEvents,
  openApiBaseUrl,
  openApiPath,
}) => {
  return (
    <ApolloProvider client={apolloClient}>
      <DevUIAppContextProvider
        availablePages={availablePages!}
        devUIUrl={devUIUrl}
        diagramPreviewSize={diagramPreviewSize!}
        isLocalCluster={isLocalCluster}
        isStunnerEnabled={isStunnerEnabled}
        isWorkflowEnabled={isWorkflowEnabled}
        omittedWorkflowTimelineEvents={omittedWorkflowTimelineEvents!}
        openApiBaseUrl={openApiBaseUrl}
        openApiPath={openApiPath}
      >
        <WorkflowListContextProviderWithApolloClient apolloClient={apolloClient}>
          <WorkflowDetailsContextProviderWithApolloClient apolloClient={apolloClient}>
            <WorkflowDefinitionListContextProviderWithApolloClient apolloClient={apolloClient}>
              <FormsListContextProvider>
                <CustomDashboardListContextProvider>
                  <CustomDashboardViewContextProvider>
                    <FormDetailsContextProvider>
                      <WorkflowFormContextProvider>
                        <CloudEventFormContextProvider>
                          <MemoryRouter>
                            <Routes>
                              <Route
                                path="*"
                                element={
                                  <PageLayout pageNavOpen={true} PageNav={<DevUINav />}>
                                    {children}
                                  </PageLayout>
                                }
                              />
                            </Routes>
                          </MemoryRouter>
                        </CloudEventFormContextProvider>
                      </WorkflowFormContextProvider>
                    </FormDetailsContextProvider>
                  </CustomDashboardViewContextProvider>
                </CustomDashboardListContextProvider>
              </FormsListContextProvider>
            </WorkflowDefinitionListContextProviderWithApolloClient>
          </WorkflowDetailsContextProviderWithApolloClient>
        </WorkflowListContextProviderWithApolloClient>
      </DevUIAppContextProvider>
    </ApolloProvider>
  );
};

export default DevUILayout;
