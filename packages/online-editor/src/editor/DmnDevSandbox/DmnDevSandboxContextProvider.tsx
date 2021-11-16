/*
 * Copyright 2021 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import * as React from "react";
import { useCallback, useEffect, useMemo, useState } from "react";
import { useGlobals } from "../../globalCtx/GlobalContext";
import { useKieToolingExtendedServices } from "../../kieToolingExtendedServices/KieToolingExtendedServicesContext";
import { KieToolingExtendedServicesStatus } from "../../kieToolingExtendedServices/KieToolingExtendedServicesStatus";
import { OpenShiftDeployedModel } from "../../openshift/OpenShiftDeployedModel";
import { DmnDevSandboxContext } from "./DmnDevSandboxContext";
import { OpenShiftInstanceStatus } from "../../openshift/OpenShiftInstanceStatus";
import { useSettings, useSettingsDispatch } from "../../settings/SettingsContext";
import { isConfigValid } from "../../openshift/OpenShiftSettingsConfig";
import { useWorkspaces, WorkspaceFile } from "../../workspace/WorkspacesContext";

interface Props {
  children: React.ReactNode;
}

const LOAD_DEPLOYMENTS_POLLING_TIME = 2500;

export function DmnDevSandboxContextProvider(props: Props) {
  const settings = useSettings();
  const settingsDispatch = useSettingsDispatch();
  const globals = useGlobals();
  const kieToolingExtendedServices = useKieToolingExtendedServices();
  const workspaces = useWorkspaces();

  const [isDropdownOpen, setDropdownOpen] = useState(false);
  const [isDeploymentsDropdownOpen, setDeploymentsDropdownOpen] = useState(false);
  const [isConfirmDeployModalOpen, setConfirmDeployModalOpen] = useState(false);
  const [deployments, setDeployments] = useState([] as OpenShiftDeployedModel[]);

  const onDisconnect = useCallback(
    (closeModals: boolean) => {
      settingsDispatch.openshift.setStatus(OpenShiftInstanceStatus.DISCONNECTED);
      setDropdownOpen(false);
      setDeployments([]);

      if (closeModals) {
        setConfirmDeployModalOpen(false);
      }
    },
    [settingsDispatch.openshift]
  );

  const deploy = useCallback(
    async (workspaceFile: WorkspaceFile) => {
      if (
        !(
          isConfigValid(settings.openshift.config) &&
          (await settingsDispatch.openshift.service.isConnectionEstablished(settings.openshift.config))
        )
      ) {
        return false;
      }

      const fs = await workspaces.fsService.getWorkspaceFs(workspaceFile.workspaceId);
      const zipBlob = await workspaces.prepareZip({
        fs,
        workspaceId: workspaceFile.workspaceId,
        onlyExtensions: ["dmn"],
      });

      try {
        await settingsDispatch.openshift.service.deploy({
          targetFilePath: workspaceFile.relativePath,
          workspaceZipBlob: zipBlob,
          config: settings.openshift.config,
          onlineEditorUrl: (baseUrl) =>
            globals.routes.importModel.url({
              base: process.env.WEBPACK_REPLACE__dmnDevSandbox_onlineEditorUrl,
              pathParams: {},
              queryParams: { url: `${baseUrl}/${workspaceFile.relativePath}` },
            }),
        });
        return true;
      } catch (error) {
        console.error(error);
        return false;
      }
    },
    [settings.openshift.config, settingsDispatch.openshift.service, workspaces, globals.routes.importModel]
  );

  useEffect(() => {
    if (kieToolingExtendedServices.status !== KieToolingExtendedServicesStatus.RUNNING) {
      onDisconnect(true);
      return;
    }

    if (!isConfigValid(settings.openshift.config)) {
      if (deployments.length > 0) {
        setDeployments([]);
      }
      return;
    }

    if (settings.openshift.status === OpenShiftInstanceStatus.DISCONNECTED) {
      settingsDispatch.openshift.service
        .isConnectionEstablished(settings.openshift.config)
        .then((isConfigOk: boolean) => {
          settingsDispatch.openshift.setStatus(
            isConfigOk ? OpenShiftInstanceStatus.CONNECTED : OpenShiftInstanceStatus.EXPIRED
          );
          return isConfigOk ? settingsDispatch.openshift.service.loadDeployments(settings.openshift.config) : [];
        })
        .then((deployments) => setDeployments(deployments))
        .catch((error) => console.error(error));
      return;
    }

    if (settings.openshift.status === OpenShiftInstanceStatus.CONNECTED) {
      const loadDeploymentsTask = setInterval(() => {
        settingsDispatch.openshift.service
          .loadDeployments(settings.openshift.config)
          .then((deployments) => setDeployments(deployments))
          .catch((error) => {
            setDeployments([]);
            clearInterval(loadDeploymentsTask);
            console.error(error);
          });
      }, LOAD_DEPLOYMENTS_POLLING_TIME);
      return () => clearInterval(loadDeploymentsTask);
    }
  }, [
    onDisconnect,
    settings.openshift,
    settingsDispatch.openshift.service,
    kieToolingExtendedServices.status,
    deployments.length,
    settingsDispatch.openshift,
  ]);

  const value = useMemo(
    () => ({
      deployments,
      isDropdownOpen,
      isDeploymentsDropdownOpen,
      isConfirmDeployModalOpen,
      setDeployments,
      setDropdownOpen,
      setConfirmDeployModalOpen,
      setDeploymentsDropdownOpen,
      deploy,
    }),
    [deploy, deployments, isConfirmDeployModalOpen, isDeploymentsDropdownOpen, isDropdownOpen]
  );

  return <DmnDevSandboxContext.Provider value={value}>{props.children}</DmnDevSandboxContext.Provider>;
}
