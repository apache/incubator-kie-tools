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
import { useRoutes } from "../../navigation/Hooks";
import { useKieSandboxExtendedServices } from "../../kieSandboxExtendedServices/KieSandboxExtendedServicesContext";
import { KieSandboxExtendedServicesStatus } from "../../kieSandboxExtendedServices/KieSandboxExtendedServicesStatus";
import { KieSandboxOpenShiftDeployedModel } from "../../openshift/KieSandboxOpenShiftService";
import { DmnDevSandboxContext } from "./DmnDevSandboxContext";
import { OpenShiftInstanceStatus } from "../../openshift/OpenShiftInstanceStatus";
import { useSettings, useSettingsDispatch } from "../../settings/SettingsContext";
import { isOpenShiftConnectionValid } from "@kie-tools-core/openshift/dist/service/OpenShiftConnection";
import { useWorkspaces, WorkspaceFile } from "@kie-tools-core/workspaces-git-fs/dist/context/WorkspacesContext";
import { NEW_WORKSPACE_DEFAULT_NAME } from "@kie-tools-core/workspaces-git-fs/dist/worker/api/WorkspaceDescriptor";

interface Props {
  children: React.ReactNode;
}

const LOAD_DEPLOYMENTS_POLLING_TIME = 2500;

export function DmnDevSandboxContextProvider(props: Props) {
  const settings = useSettings();
  const settingsDispatch = useSettingsDispatch();
  const routes = useRoutes();
  const kieSandboxExtendedServices = useKieSandboxExtendedServices();
  const workspaces = useWorkspaces();

  const [isDropdownOpen, setDropdownOpen] = useState(false);
  const [isDeploymentsDropdownOpen, setDeploymentsDropdownOpen] = useState(false);
  const [isConfirmDeployModalOpen, setConfirmDeployModalOpen] = useState(false);
  const [deployments, setDeployments] = useState([] as KieSandboxOpenShiftDeployedModel[]);

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
          isOpenShiftConnectionValid(settings.openshift.config) &&
          (await settingsDispatch.openshift.service.isConnectionEstablished(settings.openshift.config))
        )
      ) {
        return false;
      }

      const zipBlob = await workspaces.prepareZip({
        workspaceId: workspaceFile.workspaceId,
        onlyExtensions: ["dmn"],
      });

      const descriptorService = await workspaces.getWorkspace({ workspaceId: workspaceFile.workspaceId });

      const workspaceName =
        descriptorService.name !== NEW_WORKSPACE_DEFAULT_NAME ? descriptorService.name : workspaceFile.name;

      try {
        await settingsDispatch.openshift.service.deploy({
          targetFilePath: workspaceFile.relativePath,
          workspaceName,
          workspaceZipBlob: zipBlob,
          onlineEditorUrl: (baseUrl) =>
            routes.import.url({
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
    [settings.openshift.config, settingsDispatch.openshift.service, workspaces, routes.import]
  );

  useEffect(() => {
    if (kieSandboxExtendedServices.status !== KieSandboxExtendedServicesStatus.RUNNING) {
      onDisconnect(true);
      return;
    }

    if (!isOpenShiftConnectionValid(settings.openshift.config)) {
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
          return isConfigOk ? settingsDispatch.openshift.service.loadDeployments() : [];
        })
        .then((deployments) => setDeployments(deployments))
        .catch((error) => console.error(error));
      return;
    }

    if (settings.openshift.status === OpenShiftInstanceStatus.CONNECTED && isDeploymentsDropdownOpen) {
      const loadDeploymentsTask = window.setInterval(() => {
        settingsDispatch.openshift.service
          .loadDeployments()
          .then((deployments) => setDeployments(deployments))
          .catch((error) => {
            setDeployments([]);
            window.clearInterval(loadDeploymentsTask);
            console.error(error);
          });
      }, LOAD_DEPLOYMENTS_POLLING_TIME);
      return () => window.clearInterval(loadDeploymentsTask);
    }
  }, [
    onDisconnect,
    settings.openshift,
    settingsDispatch.openshift.service,
    kieSandboxExtendedServices.status,
    deployments.length,
    settingsDispatch.openshift,
    isDeploymentsDropdownOpen,
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
