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
import { Switch, useHistory } from "react-router";
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
  const history = useHistory();

  const onAddAuthSession = useCallback(
    (authSession: AuthSession) => {
      history.push({
        pathname: routes.runtime.processes.path({
          runtimeUrl: encodeURIComponent(authSession.runtimeUrl),
        }),
        search: isOpenIdConnectAuthSession(authSession) ? `?user=${authSession.username}` : "",
      });
    },
    [history, routes.runtime.processes]
  );

  return (
    <>
      <Switch>
        <Route exact path={routes.login.path({})}>
          <NewAuthSessionLoginSuccessPage onAddAuthSession={onAddAuthSession} />
        </Route>

        <Route
          path={routes.runtime.context.path({
            runtimeUrl: ":runtimeUrl",
          })}
        >
          {({ match }) => {
            return (
              <RuntimeContextProvider
                runtimeUrl={match?.params.runtimeUrl && decodeURIComponent(match.params.runtimeUrl)}
                fullPath={match?.path}
              >
                <RuntimePageLayoutContextProvider>
                  <Switch>
                    <Route
                      path={routes.runtime.processes.path({
                        runtimeUrl: ":runtimeUrl",
                      })}
                    >
                      <ProcessListPage />
                    </Route>
                    <Route
                      path={routes.runtime.processDetails.path({
                        runtimeUrl: ":runtimeUrl",
                        processInstanceId: ":processInstanceId",
                      })}
                    >
                      {({ match }) => {
                        return <ProcessDetailsPage processInstanceId={match?.params.processInstanceId} />;
                      }}
                    </Route>
                    <Route
                      path={routes.runtime.jobs.path({
                        runtimeUrl: ":runtimeUrl",
                      })}
                    >
                      <JobsPage />
                    </Route>
                    <Route
                      path={routes.runtime.tasks.path({
                        runtimeUrl: ":runtimeUrl",
                      })}
                    >
                      <TasksPage />
                    </Route>
                    <Route
                      path={routes.runtime.taskDetails.path({
                        runtimeUrl: ":runtimeUrl",
                        taskId: ":taskId",
                      })}
                    >
                      {({ match }) => {
                        return <TaskDetailsPage taskId={match?.params.taskId} />;
                      }}
                    </Route>
                  </Switch>
                </RuntimePageLayoutContextProvider>
              </RuntimeContextProvider>
            );
          }}
        </Route>
        <Route exact path={routes.home.path({})}>
          <ManagementConsoleHome />
        </Route>
      </Switch>
      <NewAuthSessionModal onAddAuthSession={onAddAuthSession} />
    </>
  );
};
