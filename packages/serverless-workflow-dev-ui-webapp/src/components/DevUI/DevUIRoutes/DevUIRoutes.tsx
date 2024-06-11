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

import { NoData } from "@kie-tools/runtime-tools-shared-webapp-components/dist/NoData";
import { PageNotFound, PageNotFoundProps } from "@kie-tools/runtime-tools-shared-webapp-components/dist/PageNotFound";
import React, { useMemo } from "react";
import { Redirect, Route, Switch } from "react-router-dom";
import { useDevUIAppContext } from "../../contexts/DevUIAppContext";
import { WorkflowsPage } from "../../pages";
import CloudEventFormPage from "../../pages/CloudEventFormPage/CloudEventFormPage";
import CustomDashboardListPage from "../../pages/CustomDashboardListPage/CustomDashboardListPage";
import CustomDashboardViewPage from "../../pages/CustomDashboardViewPage/CustomDashboardViewPage";
import FormDetailPage from "../../pages/FormDetailsPage/FormDetailsPage";
import FormsListPage from "../../pages/FormsListPage/FormsListPage";
import MonitoringPage from "../../pages/MonitoringPage/MonitoringPage";
import WorkflowFormPage from "../../pages/WorkflowFormPage/WorkflowFormPage";
import WorkflowDetailsPage from "../../pages/WorkflowDetailsPage/WorkflowDetailsPage";

interface IOwnProps {
  dataIndexUrl: string;
  navigate: string;
}

type DevUIRoute = { enabled: () => boolean; node: React.ReactNode };

const DevUIRoutes: React.FC<IOwnProps> = ({ dataIndexUrl, navigate }) => {
  const context = useDevUIAppContext();

  const routes: DevUIRoute[] = useMemo(
    () => [
      {
        enabled: () => true,
        node: <Route key="0" exact path="/" render={() => <Redirect to={`/${navigate}`} />} />,
      },
      {
        enabled: () => context.isWorkflowEnabled,
        node: <Route key="1" exact path="/Workflows" component={WorkflowsPage} />,
      },
      {
        enabled: () => context.isWorkflowEnabled,
        node: <Route key="2" exact path="/Workflow/:instanceID" component={WorkflowDetailsPage} />,
      },
      {
        enabled: () => context.isWorkflowEnabled,
        node: <Route key="5" exact path="/Forms" component={FormsListPage} />,
      },
      {
        enabled: () => context.isWorkflowEnabled,
        node: <Route key="6" exact path="/Forms/:formName" component={FormDetailPage} />,
      },
      {
        enabled: () => context.isWorkflowEnabled,
        node: <Route key="8" exact path="/WorkflowDefinition/Form/:workflowName" component={WorkflowFormPage} />,
      },
      {
        enabled: () => context.isWorkflowEnabled,
        node: <Route key="9" exact path="/CustomDashboard" component={CustomDashboardListPage} />,
      },
      {
        enabled: () => context.isWorkflowEnabled,
        node: <Route key="10" exact path="/CustomDashboard/:customDashboardName" component={CustomDashboardViewPage} />,
      },
      {
        enabled: () => context.isWorkflowEnabled,
        node: (
          <Route key="13" path="/Monitoring">
            <MonitoringPage dataIndexUrl={dataIndexUrl} />
          </Route>
        ),
      },
      {
        enabled: () => context.isWorkflowEnabled,
        node: <Route key="16" exact path="/Workflows/CloudEvent/:instanceId?" component={CloudEventFormPage} />,
      },
      {
        enabled: () => context.isWorkflowEnabled,
        node: <Route key="17" exact path="/WorkflowDefinitions/CloudEvent" component={CloudEventFormPage} />,
      },
    ],
    [context.isWorkflowEnabled]
  );

  return <Switch>{routes.filter((r) => r.enabled()).map((r) => r.node)}</Switch>;
};

export default DevUIRoutes;
