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
import { Route } from "react-router-dom";
import { ProcessListPage } from "../process";
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
        <Route
          path={routes.login.path({})}
          element={<NewAuthSessionLoginSuccessPage onAddAuthSession={onAddAuthSession} />}
        />
        <Route
          path={routes.runtime.context.path({
            runtimeUrl: ":runtimeUrl",
          })}
          element={<RuntimeContextRouteElement />}
        />
        <Route path={routes.home.path({})} element={<ManagementConsoleHome />} />
      </Routes>
      <NewAuthSessionModal onAddAuthSession={onAddAuthSession} />
    </>
  );
};

function RuntimeContextRouteElement() {
  const { runtimeUrl } = useParams<{ runtimeUrl?: string }>();
  const { pathname } = useLocation();

  return (
    <RuntimeContextProvider runtimeUrl={runtimeUrl && decodeURIComponent(runtimeUrl)} fullPath={pathname}>
      <RuntimePageLayoutContextProvider>
        <Routes>
          <Route path={"/processes"} element={<ProcessListPage />} />
          <Route path={"/process/:processInstanceId"} element={<ProcessDetailsPage />} />
          <Route path={"/jobs"} element={<JobsPage />} />
          <Route path={"/tasks"} element={<TasksPage />} />
          <Route path={"/task/:taskId"} element={<TaskDetailsPage />} />
        </Routes>
      </RuntimePageLayoutContextProvider>
    </RuntimeContextProvider>
  );
}
