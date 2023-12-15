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
import PageLayout from "@kie-tools/runtime-tools-components/dist/components/PageLayout/PageLayout";
import { User } from "@kie-tools/runtime-tools-components/dist/contexts/Auth";
import DevUINav from "../DevUINav/DevUINav";
import { WorkflowDetailsContextProviderWithApolloClient } from "@kie-tools/runtime-tools-webapp-components/dist/WorkflowDetails";
import { WorkflowListContextProviderWithApolloClient } from "@kie-tools/runtime-tools-webapp-components/dist/WorkflowList";
import FormsListContextProvider from "../../../channel/FormsList/FormsListContextProvider";
import FormDetailsContextProvider from "../../../channel/FormDetails/FormDetailsContextProvider";
import DevUIAppContextProvider from "../../contexts/DevUIAppContextProvider";
import { CustomLabels } from "../../../api/CustomLabels";
import { DiagramPreviewSize } from "@kie-tools/runtime-tools-enveloped-components/dist/workflowDetails/api";
import WorkflowFormContextProvider from "../../../channel/WorkflowForm/WorkflowFormContextProvider";
import CustomDashboardListContextProvider from "../../../channel/CustomDashboardList/CustomDashboardListContextProvider";
import { CustomDashboardViewContextProvider } from "../../../channel/CustomDashboardView";
import { CloudEventFormContextProvider } from "@kie-tools/runtime-tools-webapp-components/dist/CloudEventForm";
import { WorkflowDefinitionListContextProvider } from "@kie-tools/runtime-tools-webapp-components/dist/WorkflowDefinitionList";

interface IOwnProps {
  apolloClient: ApolloClient<any>;
  isWorkflowEnabled: boolean;
  isTracingEnabled: boolean;
  users: User[];
  children: React.ReactElement;
  devUIUrl: string;
  openApiPath: string;
  availablePages?: string[];
  customLabels?: CustomLabels;
  omittedWorkflowTimelineEvents?: string[];
  diagramPreviewSize?: DiagramPreviewSize;
  isStunnerEnabled: boolean;
}

const DevUILayout: React.FC<IOwnProps> = ({
  apolloClient,
  isWorkflowEnabled: isWorkflowEnabled,
  isTracingEnabled,
  users,
  devUIUrl,
  openApiPath,
  availablePages,
  customLabels,
  omittedWorkflowTimelineEvents,
  diagramPreviewSize,
  isStunnerEnabled,
  children,
}) => {
  const renderPage = (routeProps: { location: { pathname: string } }) => {
    return (
      <PageLayout pageNavOpen={true} withHeader={false} PageNav={<DevUINav pathname={routeProps.location.pathname} />}>
        {children}
      </PageLayout>
    );
  };

  return (
    <ApolloProvider client={apolloClient}>
      <DevUIAppContextProvider
        users={users}
        devUIUrl={devUIUrl}
        openApiPath={openApiPath}
        isWorkflowEnabled={isWorkflowEnabled}
        isTracingEnabled={isTracingEnabled}
        availablePages={availablePages!}
        customLabels={customLabels}
        omittedWorkflowTimelineEvents={omittedWorkflowTimelineEvents!}
        diagramPreviewSize={diagramPreviewSize!}
        isStunnerEnabled={isStunnerEnabled}
      >
        <WorkflowListContextProviderWithApolloClient apolloClient={apolloClient}>
          <WorkflowDetailsContextProviderWithApolloClient apolloClient={apolloClient}>
            <WorkflowDefinitionListContextProvider kogitoServiceUrl="openApiPath">
              <FormsListContextProvider>
                <CustomDashboardListContextProvider>
                  <CustomDashboardViewContextProvider>
                    <FormDetailsContextProvider>
                      <WorkflowFormContextProvider>
                        <CloudEventFormContextProvider kogitoServiceUrl="openApiPath">
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
