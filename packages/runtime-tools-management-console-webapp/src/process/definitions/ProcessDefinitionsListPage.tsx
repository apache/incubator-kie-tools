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
import { ProcessDefinitionsList } from "./ProcessDefinitionsList";
import { useRuntimeInfo, useRuntimeSpecificRoutes } from "../../runtime/RuntimeContext";
import { AuthSession, useAuthSessionsDispatch } from "../../authSessions";
import { useNavigate } from "react-router-dom";
import { useRoutes } from "../../navigation/Hooks";
import { useEnv } from "../../env/hooks/EnvContext";
import { useRuntimePageLayoutDispatch } from "../../runtime/RuntimePageLayoutContext";

export const ProcessDefinitionsListPage: React.FC = () => {
  const { env } = useEnv();
  const navigate = useNavigate();
  const runtimeRoutes = useRuntimeSpecificRoutes();
  const routes = useRoutes();
  const { runtimeDisplayInfo } = useRuntimeInfo();
  const { setOnSelectAuthSession } = useAuthSessionsDispatch();
  const { setCurrentPageTitle, setBreadcrumbText, setBreadcrumbPath } = useRuntimePageLayoutDispatch();

  useEffect(() => {
    setOnSelectAuthSession(() => (authSession: AuthSession) => {
      navigate(runtimeRoutes.processDefinitions(authSession));
    });

    return () => {
      setOnSelectAuthSession(undefined);
    };
  }, [navigate, runtimeRoutes, setOnSelectAuthSession]);

  useEffect(() => {
    setCurrentPageTitle("Process Definitions");
    setBreadcrumbText(["Home", runtimeDisplayInfo?.fullDisplayName ?? "Runtime", "Process Definitions"]);
    setBreadcrumbPath([routes.home.path({}), runtimeRoutes.processes(), runtimeRoutes.processDefinitions()]);

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

  const onNavigateToProcessDefinitionForm = useCallback(
    (processName: string) => {
      navigate(runtimeRoutes.processDefinitionForm(processName));
    },
    [navigate, runtimeRoutes]
  );

  useEffect(() => {
    document.title = `${env.RUNTIME_TOOLS_MANAGEMENT_CONSOLE_APP_NAME} :: Process Definitions`;
  }, [env.RUNTIME_TOOLS_MANAGEMENT_CONSOLE_APP_NAME]);

  return (
    <Card className="kogito-management-console__card-size">
      <ProcessDefinitionsList onNavigateToProcessDefinitionForm={onNavigateToProcessDefinitionForm} />
    </Card>
  );
};
