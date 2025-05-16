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
import { Navigate, Route, Routes } from "react-router-dom";
import { JobsManagementPage, ProcessesPage } from "../../pages";
import ProcessDetailsPage from "../../pages/ProcessDetailsPage/ProcessDetailsPage";
import TaskListPage from "../../pages/TaskListPage/TaskListPage";
import TaskDetailsPage from "../../pages/TaskDetailsPage/TaskDetailsPage";
import FormsListPage from "../../pages/FormsListPage/FormsListPage";
import FormDetailPage from "../../pages/FormDetailsPage/FormDetailsPage";
import ProcessFormPage from "../../pages/ProcessFormPage/ProcessFormPage";
import { PageNotFound } from "@kie-tools/runtime-tools-shared-webapp-components/dist/PageNotFound";
import { NoData } from "@kie-tools/runtime-tools-shared-webapp-components/dist/NoData";

interface IOwnProps {
  navigate: string;
}

const DEFAULT_PATH = "Jobs";
const DEFAULT_BUTTON = "Go to Jobs";

const DevUIRoutes: React.FC<IOwnProps> = ({ navigate }) => {
  return (
    <Routes>
      <Route path={"/"} element={<Navigate replace to={navigate} />} />
      <Route path={"Processes"} element={<ProcessesPage />} />,
      <Route path={"Process/:processId"} element={<ProcessDetailsPage />} />,
      <Route path={"ProcessDefinition/Form/:processName"} element={<ProcessFormPage />} />,
      <Route path={"Jobs"} element={<JobsManagementPage />} />,
      <Route path={"Tasks"} element={<TaskListPage />} />,
      <Route path={"TaskDetails/:taskId"} element={<TaskDetailsPage />} />
      <Route path={"Forms"} element={<FormsListPage />} />,
      <Route path={"Forms/:formName"} element={<FormDetailPage />} />,
      <Route path={"NoData"} element={<NoData defaultPath={DEFAULT_PATH} defaultButton={DEFAULT_BUTTON} />} />
      <Route path={"*"} element={<PageNotFound defaultPath={DEFAULT_PATH} defaultButton={DEFAULT_BUTTON} />} />
    </Routes>
  );
};

export default DevUIRoutes;
