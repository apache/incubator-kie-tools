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
import { BrowserRouter as Router, Route, Switch } from "react-router-dom";
import { ApolloProvider } from "react-apollo";
import { ApolloClient } from "apollo-client";
import ManagementConsoleNav from "../ManagementConsoleNav/ManagementConsoleNav";
import managementConsoleLogo from "../../../static/sonataflowManagementConsoleLogo.svg";
import {
  KogitoAppContextProvider,
  UserContext,
} from "@kie-tools/runtime-tools-components/dist/contexts/KogitoAppContext";
import { PageLayout } from "@kie-tools/runtime-tools-components/dist/components/PageLayout";
import { WorkflowDefinitionListContextProvider } from "@kie-tools/runtime-tools-swf-webapp-components/dist/WorkflowDefinitionList";
import { GlobalAlertsContextProvider } from "../../../alerts/GlobalAlertsContext";
import { WorkflowFormContextProvider } from "@kie-tools/runtime-tools-swf-webapp-components/dist/WorkflowForm";

interface IOwnProps {
  apolloClient: ApolloClient<any>;
  userContext: UserContext;
  children: React.ReactElement;
  openApiBaseUrl: string;
}

const ManagementConsole: React.FC<IOwnProps> = ({ apolloClient, userContext, children, openApiBaseUrl }) => {
  const renderPage = useCallback(
    (routeProps) => {
      return (
        <PageLayout
          BrandSrc={managementConsoleLogo}
          pageNavOpen={true}
          BrandAltText={"SonataFlow Management Console Logo"}
          BrandClick={() => routeProps.history.push("/")}
          withHeader={true}
          PageNav={<ManagementConsoleNav pathname={routeProps.location.pathname} />}
          ouiaId="management-console"
        >
          {children}
        </PageLayout>
      );
    },
    [children]
  );

  return (
    <ApolloProvider client={apolloClient}>
      <KogitoAppContextProvider userContext={userContext}>
        <GlobalAlertsContextProvider>
          <WorkflowDefinitionListContextProvider kogitoServiceUrl={openApiBaseUrl}>
            <WorkflowFormContextProvider kogitoServiceUrl={`${openApiBaseUrl}`}>
              <Router>
                <Switch>
                  <Route path="/" render={renderPage} />
                </Switch>
              </Router>
            </WorkflowFormContextProvider>
          </WorkflowDefinitionListContextProvider>
        </GlobalAlertsContextProvider>
      </KogitoAppContextProvider>
    </ApolloProvider>
  );
};

export default ManagementConsole;
