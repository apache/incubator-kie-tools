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
import { Route, Switch } from "react-router-dom";
import { ApolloProvider } from "react-apollo";
import { ApolloClient } from "apollo-client";
import { MemoryRouter } from "react-router";
import PageLayout from "@kogito-apps/consoles-common/dist/components/layout/PageLayout/PageLayout";
import { User } from "@kogito-apps/consoles-common/dist/environment/auth";
import DevUINav from "../DevUINav/DevUINav";
import JobsManagementContextProvider from "../../../channel/JobsManagement/JobsManagementContextProvider";
import ProcessDetailsContextProvider from "../../../channel/ProcessDetails/ProcessDetailsContextProvider";
import ProcessListContextProvider from "../../../channel/ProcessList/ProcessListContextProvider";
import TaskConsoleContextsProvider from "../../../channel/TaskInbox/TaskInboxContextProvider";
import TaskFormContextProvider from "../../../channel/TaskForms/TaskFormContextProvider";
import FormsListContextProvider from "../../../channel/FormsList/FormsListContextProvider";
import FormDetailsContextProvider from "../../../channel/FormDetails/FormDetailsContextProvider";
import DevUIAppContextProvider from "../../contexts/DevUIAppContextProvider";
import ProcessDefinitionListContextProvider from "../../../channel/ProcessDefinitionList/ProcessDefinitionListContextProvider";
import ProcessFormContextProvider from "../../../channel/ProcessForm/ProcessFormContextProvider";
import { CustomLabels } from "../../../api/CustomLabels";
import { DiagramPreviewSize } from "@kogito-apps/process-details/dist/api";

interface IOwnProps {
  apolloClient: ApolloClient<any>;
  isProcessEnabled: boolean;
  users: User[];
  children: React.ReactElement;
  devUIUrl: string;
  openApiPath: string;
  availablePages?: string[];
  customLabels: CustomLabels;
  omittedProcessTimelineEvents?: string[];
  diagramPreviewSize?: DiagramPreviewSize;
}

const DevUILayout: React.FC<IOwnProps> = ({
  apolloClient,
  isProcessEnabled,
  users,
  devUIUrl,
  openApiPath,
  availablePages,
  customLabels,
  omittedProcessTimelineEvents,
  diagramPreviewSize,
  children,
}) => {
  const renderPage = (routeProps) => {
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
        availablePages={availablePages}
        customLabels={customLabels}
        omittedProcessTimelineEvents={omittedProcessTimelineEvents}
        diagramPreviewSize={diagramPreviewSize}
      >
        <TaskConsoleContextsProvider apolloClient={apolloClient}>
          <TaskFormContextProvider>
            <ProcessListContextProvider apolloClient={apolloClient}>
              <ProcessDetailsContextProvider apolloClient={apolloClient}>
                <JobsManagementContextProvider apolloClient={apolloClient}>
                  <ProcessDefinitionListContextProvider>
                    <FormsListContextProvider>
                      <FormDetailsContextProvider>
                        <ProcessFormContextProvider>
                          <MemoryRouter>
                            <Switch>
                              <Route path="/" render={renderPage} />
                            </Switch>
                          </MemoryRouter>
                        </ProcessFormContextProvider>
                      </FormDetailsContextProvider>
                    </FormsListContextProvider>
                  </ProcessDefinitionListContextProvider>
                </JobsManagementContextProvider>
              </ProcessDetailsContextProvider>
            </ProcessListContextProvider>
          </TaskFormContextProvider>
        </TaskConsoleContextsProvider>
      </DevUIAppContextProvider>
    </ApolloProvider>
  );
};

export default DevUILayout;
