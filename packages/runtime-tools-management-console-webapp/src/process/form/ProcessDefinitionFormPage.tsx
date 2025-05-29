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
import { ProcessDefinitionForm } from "./ProcessDefinitionForm";
import { useRuntimeInfo, useRuntimeSpecificRoutes } from "../../runtime/RuntimeContext";
import { AuthSession, useAuthSessionsDispatch } from "../../authSessions";
import { useNavigate, useParams } from "react-router-dom";
import { KogitoSpinner } from "@kie-tools/runtime-tools-components/dist/components/KogitoSpinner";
import { useEnv } from "../../env/hooks/EnvContext";
import { useRoutes } from "../../navigation/Hooks";
import { useRuntimePageLayoutDispatch } from "../../runtime/RuntimePageLayoutContext";

export const ProcessDefinitionFormPage: React.FC = () => {
  const { processName } = useParams<{ processName?: string }>();
  const navigate = useNavigate();
  const runtimeRoutes = useRuntimeSpecificRoutes();
  const routes = useRoutes();
  const { runtimeDisplayInfo } = useRuntimeInfo();
  const { setOnSelectAuthSession } = useAuthSessionsDispatch();
  const { setCurrentPageTitle, setBreadcrumbPath, setBreadcrumbText } = useRuntimePageLayoutDispatch();
  const { env } = useEnv();

  const onNavigateToProcessDefinitionForm = useCallback(
    (authSession?: AuthSession) => {
      if (processName) {
        navigate(runtimeRoutes.processDefinitionForm(processName, authSession));
      } else {
        navigate(runtimeRoutes.processDefinitions(authSession));
      }
    },
    [navigate, processName, runtimeRoutes]
  );

  const onNavigateToProcessDefinitionsList = useCallback(() => {
    navigate(runtimeRoutes.processDefinitions());
  }, [runtimeRoutes, navigate]);

  const onNavigateToProcessInstanceDetails = useCallback(
    (processInstanceId: string) => {
      navigate(runtimeRoutes.processDetails(processInstanceId));
    },
    [runtimeRoutes, navigate]
  );

  useEffect(() => {
    setOnSelectAuthSession(() => onNavigateToProcessDefinitionForm);

    return () => {
      setOnSelectAuthSession(undefined);
    };
  }, [onNavigateToProcessDefinitionForm, setOnSelectAuthSession]);

  useEffect(() => {
    setCurrentPageTitle(
      <>
        New <b>{processName}</b> process
      </>
    );
    setBreadcrumbText([
      "Home",
      runtimeDisplayInfo?.fullDisplayName ?? "Runtime",
      "Process Definitions",
      processName ?? "",
    ]);
    setBreadcrumbPath([
      routes.home.path({}),
      runtimeRoutes.processes(),
      runtimeRoutes.processDefinitions(),
      processName ? runtimeRoutes.processDefinitionForm(processName) : runtimeRoutes.processes(),
    ]);

    return () => {
      setCurrentPageTitle("");
      setBreadcrumbText([]);
      setBreadcrumbPath([]);
    };
  }, [
    processName,
    routes.home,
    runtimeDisplayInfo?.fullDisplayName,
    runtimeRoutes,
    setBreadcrumbPath,
    setBreadcrumbText,
    setCurrentPageTitle,
  ]);

  useEffect(() => {
    document.title = `${env.RUNTIME_TOOLS_MANAGEMENT_CONSOLE_APP_NAME} :: Process Definition :: ${processName}`;
  }, [env.RUNTIME_TOOLS_MANAGEMENT_CONSOLE_APP_NAME, processName]);

  return (
    <>
      {processName ? (
        <ProcessDefinitionForm
          processName={processName}
          onReturnToProcessDefinitionsList={onNavigateToProcessDefinitionsList}
          onNavigateToProcessInstanceDetails={onNavigateToProcessInstanceDetails}
        />
      ) : (
        <KogitoSpinner spinnerText="Loading process details..." />
      )}
    </>
  );
};
