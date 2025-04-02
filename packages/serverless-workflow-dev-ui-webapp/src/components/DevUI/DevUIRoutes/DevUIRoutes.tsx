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
import { Navigate, Route, Routes } from "react-router-dom";
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

const DevUIRoutes: React.FC<IOwnProps> = ({ dataIndexUrl, navigate }) => {
  return (
    <Routes>
      <Route path="/" element={<Navigate to={`/${navigate}`} />} />,
      <Route path="/Workflows" element={<WorkflowsPage />} />,
      <Route path="/Workflow/:instanceID" element={<WorkflowDetailsPage />} />,
      <Route path="/Forms" element={<FormsListPage />} />,
      <Route path="/Forms/:formName" element={<FormDetailPage />} />,
      <Route path="/WorkflowDefinition/Form/:workflowName" element={<WorkflowFormPage />} />,
      <Route path="/CustomDashboard" element={<CustomDashboardListPage />} />,
      <Route path="/CustomDashboard/:customDashboardName" element={<CustomDashboardViewPage />} />,
      <Route path="/Monitoring" element={<MonitoringPage dataIndexUrl={dataIndexUrl} />} />
      <Route path="/Workflows/CloudEvent/:instanceId" element={<CloudEventFormPage />} />,
      <Route path="/WorkflowDefinitions/CloudEvent" element={<CloudEventFormPage />} />,
    </Routes>
  );
};

export default DevUIRoutes;
