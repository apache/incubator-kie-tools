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
import { useHistory } from "react-router-dom";
import { useRuntimeInfo, useRuntimeSpecificRoutes } from "../runtime/RuntimeContext";
import { AuthSession, useAuthSessionsDispatch } from "../authSessions";
import { useEnv } from "../env/hooks/EnvContext";
import { useRoutes } from "../navigation/Hooks";
import { TaskDetails } from "./TaskDetails";
import { useRuntimePageLayoutDispatch } from "../runtime/RuntimePageLayoutContext";
import { ImpersonationPageSection } from "./components/ImpersonationPageSection";

interface Props {
  taskId?: string;
}

export const TaskDetailsPage: React.FC<Props> = ({ taskId }) => {
  const history = useHistory();
  const runtimeRoutes = useRuntimeSpecificRoutes();
  const { runtimeDisplayInfo } = useRuntimeInfo();
  const { env } = useEnv();
  const routes = useRoutes();
  const { setOnSelectAuthSession } = useAuthSessionsDispatch();
  const { setCurrentPageTitle, setBreadcrumbText, setBreadcrumbPath } = useRuntimePageLayoutDispatch();

  useEffect(() => {
    document.title = `${env.RUNTIME_TOOLS_MANAGEMENT_CONSOLE_APP_NAME} :: Task :: ${taskId}`;
  }, [env.RUNTIME_TOOLS_MANAGEMENT_CONSOLE_APP_NAME, taskId]);

  const onNavigateToTaskDetails = useCallback(
    (authSession?: AuthSession) => {
      if (taskId) {
        history.push(runtimeRoutes.taskDetails(taskId, authSession));
      } else {
        history.push(runtimeRoutes.tasks(authSession));
      }
    },
    [history, taskId, runtimeRoutes]
  );

  useEffect(() => {
    setOnSelectAuthSession(() => onNavigateToTaskDetails);

    return () => {
      setOnSelectAuthSession(undefined);
    };
  }, [history, onNavigateToTaskDetails, setOnSelectAuthSession]);

  useEffect(() => {
    setBreadcrumbText(["Home", runtimeDisplayInfo?.fullDisplayName ?? "Runtime", "Tasks", taskId ?? ""]);
    setBreadcrumbPath([
      routes.home.path({}),
      runtimeRoutes.processes(),
      runtimeRoutes.tasks(),
      taskId ? runtimeRoutes.taskDetails(taskId) : runtimeRoutes.tasks(),
    ]);

    return () => {
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
    taskId,
  ]);

  return (
    <>
      <ImpersonationPageSection />
      <TaskDetails taskId={taskId} />
    </>
  );
};
