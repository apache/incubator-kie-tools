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
import React, { useEffect } from "react";
import { Card } from "@patternfly/react-core/dist/js/components/Card";
import { Jobs } from "./Jobs";
import { AuthSession, useAuthSessionsDispatch } from "../authSessions";
import { useHistory } from "react-router";
import { useRuntimeInfo, useRuntimeSpecificRoutes } from "../runtime/RuntimeContext";
import { useEnv } from "../env/hooks/EnvContext";
import { useRuntimePageLayoutDispatch } from "../runtime/RuntimePageLayoutContext";
import { useRoutes } from "../navigation/Hooks";

export const JobsPage: React.FC = () => {
  const history = useHistory();
  const runtimeRoutes = useRuntimeSpecificRoutes();
  const routes = useRoutes();
  const { runtimeDisplayInfo } = useRuntimeInfo();
  const { setOnSelectAuthSession } = useAuthSessionsDispatch();
  const { setCurrentPageTitle, setBreadcrumbText, setBreadcrumbPath } = useRuntimePageLayoutDispatch();
  const { env } = useEnv();

  useEffect(() => {
    setOnSelectAuthSession(() => (authSession: AuthSession) => {
      history.push(runtimeRoutes.jobs(authSession));
    });
    return () => {
      setOnSelectAuthSession(undefined);
    };
  }, [history, runtimeRoutes, setOnSelectAuthSession]);

  useEffect(() => {
    setCurrentPageTitle("Jobs");
    setBreadcrumbText(["Home", runtimeDisplayInfo?.fullDisplayName ?? "Runtime", "Jobs"]);
    setBreadcrumbPath([routes.home.path({}), runtimeRoutes.processes(), runtimeRoutes.jobs()]);

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

  useEffect(() => {
    document.title = `${env.RUNTIME_TOOLS_MANAGEMENT_CONSOLE_APP_NAME} :: Jobs`;
  }, [env.RUNTIME_TOOLS_MANAGEMENT_CONSOLE_APP_NAME]);

  return (
    <Card className="kogito-management-console__card-size">
      <Jobs />
    </Card>
  );
};
