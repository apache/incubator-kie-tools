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
import { Navigate, Route, Routes } from "react-router-dom";
import { routes } from "../../../navigation/Routes";
import { TriggerCloudEventPage } from "../../pages/TriggerCloudEventPage/TriggerCloudEventPage";
import MonitoringPage from "../../pages/MonitoringPage/MonitoringPage";
import { WorkflowDefinitionsPage } from "../../pages/WorkflowDefinitionsPage/WorkflowDefinitionsPage";
import { WorkflowInstancesPage } from "../../pages/WorkflowInstancesPage/WorkflowInstancesPage";
import { WorkflowDetailsPage } from "../../pages/WorkflowDetailsPage/WorkflowDetailsPage";
import { WorkflowFormPage } from "../../pages/WorkflowFormPage/WorkflowFormPage";

const ManagementConsoleRoutes: React.FC = () => {
  return (
    <Routes>
      <Route
        path={routes.home.path({})}
        element={<Navigate replace to={routes.runtimeToolsWorkflowInstances.path({})} />}
      />
      <Route path={routes.runtimeToolsWorkflowInstances.path({})} element={<WorkflowInstancesPage />} />
      <Route path={routes.runtimeToolsWorkflowDefinitions.path({})} element={<WorkflowDefinitionsPage />} />
      <Route
        path={routes.runtimeToolsWorkflowDetails.path({ workflowId: ":workflowId" })}
        element={<WorkflowDetailsPage />}
      />
      <Route
        path={routes.runtimeToolsTriggerCloudEventForWorkflowDefinition.path({ workflowName: ":workflowName" })}
        element={<TriggerCloudEventPage />}
      />
      <Route
        path={routes.runtimeToolsWorkflowForm.path({ workflowName: ":workflowName" })}
        element={<WorkflowFormPage />}
      />
      <Route
        path={routes.monitoring.path({})}
        element={<MonitoringPage dataIndexUrl={(window as any)["DATA_INDEX_ENDPOINT"]} />}
      />
    </Routes>
  );
};

export default ManagementConsoleRoutes;
