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
import { ProcessList } from "./ProcessList";
import { useRuntimeInfo, useRuntimeSpecificRoutes } from "../../runtime/RuntimeContext";
import { AuthSession, useAuthSessionsDispatch } from "../../authSessions";
import { useHistory } from "react-router";
import { useRoutes } from "../../navigation/Hooks";
import { useEnv } from "../../env/hooks/EnvContext";
import { useRuntimePageLayoutDispatch } from "../../runtime/RuntimePageLayoutContext";

export const ProcessListPage: React.FC = () => {
  const { env } = useEnv();
  const history = useHistory();
  const runtimeRoutes = useRuntimeSpecificRoutes();
  const routes = useRoutes();
  const { runtimeDisplayInfo } = useRuntimeInfo();
  const { setOnSelectAuthSession } = useAuthSessionsDispatch();
  const { setCurrentPageTitle, setBreadcrumbText, setBreadcrumbPath } = useRuntimePageLayoutDispatch();

  useEffect(() => {
    setOnSelectAuthSession(() => (authSession: AuthSession) => {
      history.push(runtimeRoutes.processes(authSession));
    });

    return () => {
      setOnSelectAuthSession(undefined);
    };
  }, [history, runtimeRoutes, setOnSelectAuthSession]);

  useEffect(() => {
    setCurrentPageTitle("Process Instances");
    setBreadcrumbText(["Home", runtimeDisplayInfo?.fullDisplayName ?? "Runtime", "Process Instances"]);
    setBreadcrumbPath([routes.home.path({}), runtimeRoutes.processes(), runtimeRoutes.processes()]);

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

  const onNavigateToProcessDetails = useCallback(
    (processInstanceId: string) => {
      history.push(runtimeRoutes.processDetails(processInstanceId));
    },
    [history, runtimeRoutes]
  );

  useEffect(() => {
    document.title = `${env.RUNTIME_TOOLS_MANAGEMENT_CONSOLE_APP_NAME} :: Process Instances`;
  }, [env.RUNTIME_TOOLS_MANAGEMENT_CONSOLE_APP_NAME]);

  return (
    <Card className="kogito-management-console__card-size">
      <ProcessList onNavigateToProcessDetails={onNavigateToProcessDetails} />
    </Card>
  );
};
