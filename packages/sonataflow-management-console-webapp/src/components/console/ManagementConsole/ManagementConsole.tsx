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
import { useCallback } from "react";
import { HashRouter as Router, Route, Routes } from "react-router-dom";
import { ApolloProvider } from "react-apollo";
import { ApolloClient } from "apollo-client";
import {
  KogitoAppContextProvider,
  UserContext,
} from "@kie-tools/runtime-tools-components/dist/contexts/KogitoAppContext";
import { WorkflowListContextProviderWithApolloClient } from "@kie-tools/runtime-tools-swf-webapp-components/dist/WorkflowList";
import { WorkflowDefinitionListContextProviderWithApolloClient } from "@kie-tools/runtime-tools-swf-webapp-components/dist/WorkflowDefinitionList";
import { WorkflowFormContextProvider } from "@kie-tools/runtime-tools-swf-webapp-components/dist/WorkflowForm";
import { WorkflowDetailsContextProviderWithApolloClient } from "@kie-tools/runtime-tools-swf-webapp-components/dist/WorkflowDetails";
import { CloudEventFormContextProvider } from "@kie-tools/runtime-tools-swf-webapp-components/dist/CloudEventForm";
import { GlobalAlertsContextProvider } from "../../../alerts/GlobalAlertsContext";
import { BasePage } from "../../pages/BasePage";

interface IOwnProps {
  apolloClient: ApolloClient<any>;
  userContext: UserContext;
  children: React.ReactElement;
}

const ManagementConsole: React.FC<IOwnProps> = ({ apolloClient, userContext, children }) => {
  return (
    <ApolloProvider client={apolloClient}>
      <KogitoAppContextProvider userContext={userContext}>
        <GlobalAlertsContextProvider>
          <CloudEventFormContextProvider>
            <WorkflowDetailsContextProviderWithApolloClient apolloClient={apolloClient}>
              <WorkflowListContextProviderWithApolloClient apolloClient={apolloClient}>
                <WorkflowDefinitionListContextProviderWithApolloClient apolloClient={apolloClient}>
                  <WorkflowFormContextProvider>
                    <Router>
                      <Routes>
                        <Route path="*" element={<BasePage>{children}</BasePage>} />
                      </Routes>
                    </Router>
                  </WorkflowFormContextProvider>
                </WorkflowDefinitionListContextProviderWithApolloClient>
              </WorkflowListContextProviderWithApolloClient>
            </WorkflowDetailsContextProviderWithApolloClient>
          </CloudEventFormContextProvider>
        </GlobalAlertsContextProvider>
      </KogitoAppContextProvider>
    </ApolloProvider>
  );
};

export default ManagementConsole;
