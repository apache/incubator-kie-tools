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
import PageLayout from "@kie-tools/runtime-tools-components/dist/consolesCommon/components/layout/PageLayout/PageLayout";
import { User } from "@kie-tools/runtime-tools-components/dist/consolesCommon/environment/auth";
import DevUINav from "../DevUINav/DevUINav";
import { WorkflowDetailsContextProviderWithApolloClient } from "@kie-tools/runtime-tools-webapp-components/dist/WorkflowDetails";
import { WorkflowListContextProviderWithApolloClient } from "@kie-tools/runtime-tools-webapp-components/dist/WorkflowList";
import FormsListContextProvider from "../../../channel/FormsList/FormsListContextProvider";
import FormDetailsContextProvider from "../../../channel/FormDetails/FormDetailsContextProvider";
import DevUIAppContextProvider from "../../contexts/DevUIAppContextProvider";
import ProcessDefinitionListContextProvider from "../../../channel/ProcessDefinitionList/ProcessDefinitionListContextProvider";
import { CustomLabels } from "../../../api/CustomLabels";
import { DiagramPreviewSize } from "@kie-tools/runtime-tools-enveloped-components/dist/workflowDetails/api";
import WorkflowFormContextProvider from "../../../channel/WorkflowForm/WorkflowFormContextProvider";
import CustomDashboardListContextProvider from "../../../channel/CustomDashboardList/CustomDashboardListContextProvider";
import { CustomDashboardViewContextProvider } from "../../../channel/CustomDashboardView";
import CloudEventFormContextProvider from "../../../channel/CloudEventForm/CloudEventFormContextProvider";

interface IOwnProps {
  apolloClient: ApolloClient<any>;
  isProcessEnabled: boolean;
  isTracingEnabled: boolean;
  users: User[];
  children: React.ReactElement;
  devUIUrl: string;
  openApiPath: string;
  availablePages?: string[];
  customLabels?: CustomLabels;
  omittedProcessTimelineEvents?: string[];
  diagramPreviewSize?: DiagramPreviewSize;
  isStunnerEnabled: boolean;
}

const DevUILayout: React.FC<IOwnProps> = ({
  apolloClient,
  isProcessEnabled,
  isTracingEnabled,
  users,
  devUIUrl,
  openApiPath,
  availablePages,
  customLabels,
  omittedProcessTimelineEvents,
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
        isProcessEnabled={isProcessEnabled}
        isTracingEnabled={isTracingEnabled}
        availablePages={availablePages!}
        customLabels={customLabels}
        omittedProcessTimelineEvents={omittedProcessTimelineEvents!}
        diagramPreviewSize={diagramPreviewSize!}
        isStunnerEnabled={isStunnerEnabled}
      >
        <WorkflowListContextProviderWithApolloClient apolloClient={apolloClient}>
          <WorkflowDetailsContextProviderWithApolloClient apolloClient={apolloClient}>
            <ProcessDefinitionListContextProvider>
              <FormsListContextProvider>
                <CustomDashboardListContextProvider>
                  <CustomDashboardViewContextProvider>
                    <FormDetailsContextProvider>
                      <WorkflowFormContextProvider>
                        <CloudEventFormContextProvider>
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
            </ProcessDefinitionListContextProvider>
          </WorkflowDetailsContextProviderWithApolloClient>
        </WorkflowListContextProviderWithApolloClient>
      </DevUIAppContextProvider>
    </ApolloProvider>
  );
};

export default DevUILayout;
