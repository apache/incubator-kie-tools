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
import React, { useCallback, useEffect } from "react";
import { Card } from "@patternfly/react-core/dist/js/components/Card";
import { Tasks } from "./Tasks";
import { useEnv } from "../env/hooks/EnvContext";
import { useRuntimeInfo, useRuntimeSpecificRoutes } from "../runtime/RuntimeContext";
import { useHistory } from "react-router";
import { useRoutes } from "../navigation/Hooks";
import { AuthSession, useAuthSessionsDispatch } from "../authSessions";
import { useRuntimePageLayoutDispatch } from "../runtime/RuntimePageLayoutContext";
import { ImpersonationPageSection } from "./components/ImpersonationPageSection";

export const TasksPage: React.FC = (ouiaId, ouiaSafe) => {
  const { env } = useEnv();
  const history = useHistory();
  const runtimeRoutes = useRuntimeSpecificRoutes();
  const routes = useRoutes();
  const { runtimeDisplayInfo } = useRuntimeInfo();
  const { setOnSelectAuthSession } = useAuthSessionsDispatch();
  const { setCurrentPageTitle, setBreadcrumbText, setBreadcrumbPath } = useRuntimePageLayoutDispatch();

  useEffect(() => {
    document.title = `${env.RUNTIME_TOOLS_MANAGEMENT_CONSOLE_APP_NAME} :: Tasks`;
  }, [env.RUNTIME_TOOLS_MANAGEMENT_CONSOLE_APP_NAME]);

  useEffect(() => {
    setOnSelectAuthSession(() => (authSession: AuthSession) => {
      history.push(runtimeRoutes.tasks(authSession));
    });

    return () => {
      setOnSelectAuthSession(undefined);
    };
  }, [history, runtimeRoutes, setOnSelectAuthSession]);

  useEffect(() => {
    setCurrentPageTitle("Tasks");
    setBreadcrumbText(["Home", runtimeDisplayInfo?.fullDisplayName ?? "Runtime", "Tasks"]);
    setBreadcrumbPath([routes.home.path({}), runtimeRoutes.processes(), runtimeRoutes.tasks()]);

    return () => {
      setCurrentPageTitle("");
      setBreadcrumbText([]);
      setBreadcrumbPath([]);
    };
  }, [
    routes.home,
    runtimeDisplayInfo?.fullDisplayName,
    runtimeRoutes,
    setBreadcrumbPath,
    setBreadcrumbText,
    setCurrentPageTitle,
  ]);

  const onNavigateToTaskDetails = useCallback(
    (taskId: string) => {
      history.push(runtimeRoutes.taskDetails(taskId));
    },
    [history, runtimeRoutes]
  );

  return (
    <>
      <ImpersonationPageSection />
      <Card className="kogito-management-console__card-size">
        <Tasks onNavigateToTaskDetails={onNavigateToTaskDetails} />
      </Card>
    </>
  );
};
