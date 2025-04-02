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
import { APPDATA_JSON_FILENAME } from "../AppConstants";
import { ErrorKind, ErrorPage } from "../pages/ErrorPage";
import { NoMatchPage } from "../pages/NoMatchPage";
import { CloudEventFormPage } from "../pages/Workflows/CloudEventFormPage";
import { WorkflowFormPage } from "../pages/Workflows/WorkflowFormPage";
import { routes } from "../routes";
import { RuntimeToolsWorkflowDefinitions } from "../runtimeTools/pages/RuntimeToolsWorkflowDefinitions";
import { RuntimeToolsWorkflowInstances } from "../runtimeTools/pages/RuntimeToolsWorkflowInstances";
import { RuntimeToolsWorkflowDetails } from "../runtimeTools/pages/RuntimeToolsWorkflowDetails";

export function RoutesSwitch() {
  return (
    <Routes>
      <Route path={routes.workflows.form.path({ workflowId: ":workflowId" })} element={<WorkflowFormPage />} />
      <Route path={routes.workflows.cloudEvent.path({})} element={<CloudEventFormPage />} />
      <Route path={routes.runtimeTools.workflowDefinitions.path({})} element={<RuntimeToolsWorkflowDefinitions />} />
      <Route path={routes.runtimeTools.workflowInstances.path({})} element={<RuntimeToolsWorkflowInstances />} />
      <Route
        path={routes.runtimeTools.workflowDetails.path({ workflowId: ":workflowId" })}
        element={<RuntimeToolsWorkflowDetails />}
      />
      <Route
        path={routes.dataJsonError.path({})}
        element={
          <ErrorPage kind={ErrorKind.APPDATA_JSON} errors={[`There was an error with the ${APPDATA_JSON_FILENAME}`]} />
        }
      />
      <Route
        path={routes.home.path({})}
        element={<Navigate replace to={routes.runtimeTools.workflowDefinitions.path({})} />}
      />
      <Route path={"*"} element={<NoMatchPage />} />
    </Routes>
  );
}
