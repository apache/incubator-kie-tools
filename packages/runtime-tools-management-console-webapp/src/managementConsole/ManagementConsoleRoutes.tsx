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
import React, { FC, useCallback } from "react";
import { Outlet, Route } from "react-router-dom";
import { ProcessListPage, ProcessDefinitionsListPage } from "../process";
import { Routes, useNavigate, useLocation, useParams } from "react-router-dom";
import { ManagementConsoleHome } from "./ManagementConsoleHome";
import { NewAuthSessionLoginSuccessPage, NewAuthSessionModal } from "../authSessions/components";
import { useRoutes } from "../navigation/Hooks";
import { RuntimeContextProvider } from "../runtime/RuntimeContext";
import { AuthSession, isOpenIdConnectAuthSession } from "../authSessions";
import { ProcessDetailsPage } from "../process/details/ProcessDetailsPage";
import { JobsPage } from "../jobs";
import { TaskDetailsPage, TasksPage } from "../tasks";
import { RuntimePageLayoutContextProvider } from "../runtime/RuntimePageLayoutContext";
import { ProcessDefinitionFormPage } from "../process/form/ProcessDefinitionFormPage";

export const ManagementConsoleRoutes: FC = () => {
  const routes = useRoutes();
  const navigate = useNavigate();

  const onAddAuthSession = useCallback(
    (authSession: AuthSession) => {
      navigate({
        pathname: routes.runtime.processes.path({
          runtimeUrl: encodeURIComponent(authSession.runtimeUrl),
        }),
        search: isOpenIdConnectAuthSession(authSession) ? `?user=${authSession.username}` : "",
      });
    },
    [navigate, routes.runtime.processes]
  );

  return (
    <>
      <Routes>
        <Route element={<RuntimeRoutesContext />}>
          <Route
            path={routes.login.path({})}
            element={<NewAuthSessionLoginSuccessPage onAddAuthSession={onAddAuthSession} />}
          />
          <Route path={routes.runtime.processes.path({ runtimeUrl: ":runtimeUrl" })} element={<ProcessListPage />} />
          <Route
            path={routes.runtime.processDetails.path({
              runtimeUrl: ":runtimeUrl",
              processInstanceId: ":processInstanceId",
            })}
            element={<ProcessDetailsPage />}
          />
          <Route path={routes.runtime.jobs.path({ runtimeUrl: ":runtimeUrl" })} element={<JobsPage />} />
          <Route path={routes.runtime.tasks.path({ runtimeUrl: ":runtimeUrl" })} element={<TasksPage />} />
          <Route
            path={routes.runtime.processDefinitions.path({ runtimeUrl: ":runtimeUrl" })}
            element={<ProcessDefinitionsListPage />}
          />
          <Route
            path={routes.runtime.processDefinitionForm.path({
              runtimeUrl: ":runtimeUrl",
              processName: ":processName",
            })}
            element={<ProcessDefinitionFormPage />}
          />
          <Route
            path={routes.runtime.taskDetails.path({ runtimeUrl: ":runtimeUrl", taskId: ":taskId" })}
            element={<TaskDetailsPage />}
          />
          <Route path={routes.home.path({})} element={<ManagementConsoleHome />} />
        </Route>
      </Routes>
      <NewAuthSessionModal onAddAuthSession={onAddAuthSession} />
    </>
  );
};

function RuntimeRoutesContext() {
  const { runtimeUrl } = useParams<{ runtimeUrl?: string }>();
  const { pathname } = useLocation();

  return runtimeUrl && pathname ? (
    <RuntimeContextProvider runtimeUrl={runtimeUrl && decodeURIComponent(runtimeUrl)} fullPath={pathname}>
      <RuntimePageLayoutContextProvider>
        <Outlet />
      </RuntimePageLayoutContextProvider>
    </RuntimeContextProvider>
  ) : (
    <Outlet />
  );
}
