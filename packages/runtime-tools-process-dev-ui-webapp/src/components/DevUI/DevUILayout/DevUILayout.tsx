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
import { MemoryRouter, Route, Routes } from "react-router-dom";
import { ApolloProvider } from "react-apollo";
import { ApolloClient } from "apollo-client";
import DevUINav from "../DevUINav/DevUINav";
import { FormsListContextProvider } from "../../../channel/FormsList/FormsListContextProvider";
import FormDetailsContextProvider from "../../../channel/FormDetails/FormDetailsContextProvider";
import DevUIAppContextProvider from "../../contexts/DevUIAppContextProvider";
import ProcessContextProvider from "../../contexts/ProcessContextProvider";
import { CustomLabels } from "../../../api/CustomLabels";
import { User } from "@kie-tools/runtime-tools-process-enveloped-components/dist/taskForm";
import { DiagramPreviewSize } from "@kie-tools/runtime-tools-process-enveloped-components/dist/processDetails";
import { PageLayout } from "@kie-tools/runtime-tools-components/dist/components/PageLayout";
import { JobsManagementContextProvider } from "@kie-tools/runtime-tools-process-webapp-components/dist/JobsManagement";
import { TaskFormContextProvider } from "../../contexts/TaskFormContextProvider";
import { TaskListContextProvider } from "../../contexts/TaskListContextProvider";
import { ProcessFormContextProvider } from "@kie-tools/runtime-tools-process-webapp-components/dist/ProcessForm/ProcessFormContextProvider";

interface IOwnProps {
  apolloClient: ApolloClient<any>;
  isProcessEnabled: boolean;
  users: User[];
  children: React.ReactElement;
  devUIOrigin: string;
  devUIUrl: string;
  quarkusAppOrigin: string;
  quarkusAppRootPath: string;
  shouldReplaceQuarkusAppOriginWithWebappOrigin: boolean;
  availablePages?: string[];
  customLabels: CustomLabels;
  omittedProcessTimelineEvents?: string[];
  diagramPreviewSize?: DiagramPreviewSize;
}

const DevUILayout: React.FC<IOwnProps> = ({
  apolloClient,
  isProcessEnabled,
  users,
  devUIOrigin,
  devUIUrl,
  quarkusAppOrigin,
  quarkusAppRootPath,
  shouldReplaceQuarkusAppOriginWithWebappOrigin,
  availablePages,
  customLabels,
  omittedProcessTimelineEvents,
  diagramPreviewSize,
  children,
}) => {
  return (
    <ApolloProvider client={apolloClient}>
      <DevUIAppContextProvider
        users={users}
        devUIOrigin={devUIOrigin}
        devUIUrl={devUIUrl}
        quarkusAppOrigin={quarkusAppOrigin}
        quarkusAppRootPath={quarkusAppRootPath}
        shouldReplaceQuarkusAppOriginWithWebappOrigin={shouldReplaceQuarkusAppOriginWithWebappOrigin}
        isProcessEnabled={isProcessEnabled}
        availablePages={availablePages}
        customLabels={customLabels}
        omittedProcessTimelineEvents={omittedProcessTimelineEvents}
        diagramPreviewSize={diagramPreviewSize}
      >
        <TaskListContextProvider apolloClient={apolloClient}>
          <TaskFormContextProvider>
            <ProcessContextProvider apolloClient={apolloClient}>
              <JobsManagementContextProvider apolloClient={apolloClient}>
                <FormsListContextProvider>
                  <FormDetailsContextProvider>
                    <ProcessFormContextProvider>
                      <MemoryRouter>
                        <Routes>
                          <Route
                            path="*"
                            element={
                              <PageLayout pageNavOpen={true} withHeader={false} PageNav={<DevUINav />}>
                                {children}
                              </PageLayout>
                            }
                          />
                        </Routes>
                      </MemoryRouter>
                    </ProcessFormContextProvider>
                  </FormDetailsContextProvider>
                </FormsListContextProvider>
              </JobsManagementContextProvider>
            </ProcessContextProvider>
          </TaskFormContextProvider>
        </TaskListContextProvider>
      </DevUIAppContextProvider>
    </ApolloProvider>
  );
};

export default DevUILayout;
