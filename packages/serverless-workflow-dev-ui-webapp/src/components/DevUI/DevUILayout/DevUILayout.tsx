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
import { Route, Switch } from "react-router-dom";
import { ApolloProvider } from "react-apollo";
import { ApolloClient } from "apollo-client";
import { MemoryRouter } from "react-router";
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
import { WorkflowDefinitionListContextProvider } from "@kie-tools/runtime-tools-swf-webapp-components/dist/WorkflowDefinitionList";

interface IOwnProps {
  apolloClient: ApolloClient<any>;
  isWorkflowEnabled: boolean;
  children: React.ReactElement;
  devUIUrl: string;
  openApiBaseUrl: string;
  openApiPath: string;
  availablePages?: string[];
  omittedWorkflowTimelineEvents?: string[];
  diagramPreviewSize?: DiagramPreviewSize;
  isStunnerEnabled: boolean;
}

const DevUILayout: React.FC<IOwnProps> = ({
  apolloClient,
  isWorkflowEnabled: isWorkflowEnabled,
  devUIUrl,
  openApiBaseUrl,
  openApiPath,
  availablePages,
  omittedWorkflowTimelineEvents,
  diagramPreviewSize,
  isStunnerEnabled,
  children,
}) => {
  const renderPage = (routeProps: { location: { pathname: string } }) => {
    return (
      <PageLayout pageNavOpen={true} PageNav={<DevUINav pathname={routeProps.location.pathname} />}>
        {children}
      </PageLayout>
    );
  };

  return (
    <ApolloProvider client={apolloClient}>
      <DevUIAppContextProvider
        devUIUrl={devUIUrl}
        openApiBaseUrl={openApiBaseUrl}
        openApiPath={openApiPath}
        isWorkflowEnabled={isWorkflowEnabled}
        availablePages={availablePages!}
        omittedWorkflowTimelineEvents={omittedWorkflowTimelineEvents!}
        diagramPreviewSize={diagramPreviewSize!}
        isStunnerEnabled={isStunnerEnabled}
      >
        <WorkflowListContextProviderWithApolloClient apolloClient={apolloClient}>
          <WorkflowDetailsContextProviderWithApolloClient apolloClient={apolloClient}>
            <WorkflowDefinitionListContextProvider kogitoServiceUrl={`${openApiBaseUrl}`}>
              <FormsListContextProvider>
                <CustomDashboardListContextProvider>
                  <CustomDashboardViewContextProvider>
                    <FormDetailsContextProvider>
                      <WorkflowFormContextProvider kogitoServiceUrl={`${openApiBaseUrl}`}>
                        <CloudEventFormContextProvider kogitoServiceUrl={`${openApiBaseUrl}`}>
                          <MemoryRouter>
                            <Switch>
                              <Route path="/" render={renderPage} />
                            </Switch>
                          </MemoryRouter>
                        </CloudEventFormContextProvider>
                      </WorkflowFormContextProvider>
                    </FormDetailsContextProvider>
                  </CustomDashboardViewContextProvider>
                </CustomDashboardListContextProvider>
              </FormsListContextProvider>
            </WorkflowDefinitionListContextProvider>
          </WorkflowDetailsContextProviderWithApolloClient>
        </WorkflowListContextProviderWithApolloClient>
      </DevUIAppContextProvider>
    </ApolloProvider>
  );
};

export default DevUILayout;
