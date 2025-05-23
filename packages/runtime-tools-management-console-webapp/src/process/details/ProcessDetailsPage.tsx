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
import { ProcessDetails } from "./ProcessDetails";
import { useRuntimeInfo, useRuntimeSpecificRoutes } from "../../runtime/RuntimeContext";
import { AuthSession, useAuthSessionsDispatch } from "../../authSessions";
import { useNavigate, useParams } from "react-router-dom";
import { KogitoSpinner } from "@kie-tools/runtime-tools-components/dist/components/KogitoSpinner";
import { useEnv } from "../../env/hooks/EnvContext";
import { useRoutes } from "../../navigation/Hooks";
import { useRuntimePageLayoutDispatch } from "../../runtime/RuntimePageLayoutContext";

export const ProcessDetailsPage: React.FC = () => {
  const { processInstanceId } = useParams<{ processInstanceId?: string }>();
  const navigate = useNavigate();
  const runtimeRoutes = useRuntimeSpecificRoutes();
  const routes = useRoutes();
  const { runtimeDisplayInfo } = useRuntimeInfo();
  const { setOnSelectAuthSession } = useAuthSessionsDispatch();
  const { setCurrentPageTitle, setBreadcrumbPath, setBreadcrumbText } = useRuntimePageLayoutDispatch();
  const { env } = useEnv();

  const onNavigateToProcessDetails = useCallback(
    (authSession?: AuthSession) => {
      if (processInstanceId) {
        navigate(runtimeRoutes.processDetails(processInstanceId, authSession));
      } else {
        navigate(runtimeRoutes.processes(authSession));
      }
    },
    [navigate, processInstanceId, runtimeRoutes]
  );

  const onNavigateToProcessesList = useCallback(() => {
    navigate(runtimeRoutes.processes());
  }, [runtimeRoutes, navigate]);

  useEffect(() => {
    setOnSelectAuthSession(() => onNavigateToProcessDetails);

    return () => {
      setOnSelectAuthSession(undefined);
    };
  }, [onNavigateToProcessDetails, setOnSelectAuthSession]);

  useEffect(() => {
    setCurrentPageTitle("Process Instance");
    setBreadcrumbText([
      "Home",
      runtimeDisplayInfo?.fullDisplayName ?? "Runtime",
      "Process Instances",
      processInstanceId ?? "",
    ]);
    setBreadcrumbPath([
      routes.home.path({}),
      runtimeRoutes.processes(),
      runtimeRoutes.processes(),
      processInstanceId ? runtimeRoutes.processDetails(processInstanceId) : runtimeRoutes.processes(),
    ]);

    return () => {
      setCurrentPageTitle("");
      setBreadcrumbText([]);
      setBreadcrumbPath([]);
    };
  }, [
    processInstanceId,
    routes.home,
    runtimeDisplayInfo?.fullDisplayName,
    runtimeRoutes,
    setBreadcrumbPath,
    setBreadcrumbText,
    setCurrentPageTitle,
  ]);

  useEffect(() => {
    document.title = `${env.RUNTIME_TOOLS_MANAGEMENT_CONSOLE_APP_NAME} :: Process Instance :: ${processInstanceId}`;
  }, [env.RUNTIME_TOOLS_MANAGEMENT_CONSOLE_APP_NAME, processInstanceId]);

  return (
    <>
      {processInstanceId ? (
        <ProcessDetails processInstanceId={processInstanceId} onReturnToProcessList={onNavigateToProcessesList} />
      ) : (
        <KogitoSpinner spinnerText="Loading process details..." />
      )}
    </>
  );
};
