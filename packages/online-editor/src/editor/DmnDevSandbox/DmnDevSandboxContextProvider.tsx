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
import { DmnDevSandboxModalConfirmDelete } from "./DmnDevSandboxModalConfirmDelete";
import { useController } from "@kie-tools-core/react-hooks/dist/useController";
import { AlertsController } from "../../alerts/Alerts";
import { useAlerts } from "../../alerts/AlertsContext";

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
  const [isConfirmDeleteModalOpen, setConfirmDeleteModalOpen] = useState(false);
  const [deploymentsToBeDeleted, setDeploymentsToBeDeleted] = useState<string[]>([]);
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

  const deleteDeployment = useCallback(
    async (resourceName: string) => {
      try {
        await settingsDispatch.openshift.service.deleteDeployment(resourceName);
        return true;
      } catch (error) {
        console.error(error);
        return false;
      }
    },
    [settingsDispatch.openshift.service]
  );

  const deleteDeployments = useCallback(async () => {
    const result = await Promise.all(deploymentsToBeDeleted.map((resourceName) => deleteDeployment(resourceName)));
    setDeploymentsToBeDeleted([]);
    return result.every(Boolean);
  }, [deleteDeployment, deploymentsToBeDeleted]);

  const loadDeployments = useCallback(
    async (errCallback?: () => void) => {
      return settingsDispatch.openshift.service
        .loadDeployments()
        .then((deployments) => setDeployments(deployments))
        .catch((error) => {
          setDeployments([]);
          errCallback?.();
          console.error(error);
        });
    },
    [settingsDispatch.openshift.service]
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
        loadDeployments(() => window.clearInterval(loadDeploymentsTask));
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
    loadDeployments,
  ]);

  const value = useMemo(
    () => ({
      deployments,
      isDropdownOpen,
      isDeploymentsDropdownOpen,
      isConfirmDeployModalOpen,
      isConfirmDeleteModalOpen,
      deploymentsToBeDeleted,
      setDeployments,
      setDropdownOpen,
      setConfirmDeployModalOpen,
      setConfirmDeleteModalOpen,
      setDeploymentsDropdownOpen,
      setDeploymentsToBeDeleted,
      deploy,
      deleteDeployment,
      deleteDeployments,
      loadDeployments,
    }),
    [
      deployments,
      isDropdownOpen,
      isDeploymentsDropdownOpen,
      isConfirmDeployModalOpen,
      isConfirmDeleteModalOpen,
      deploymentsToBeDeleted,
      deploy,
      deleteDeployment,
      deleteDeployments,
      loadDeployments,
    ]
  );

  return (
    <DmnDevSandboxContext.Provider value={value}>
      {props.children}
      <DmnDevSandboxModalConfirmDelete />
    </DmnDevSandboxContext.Provider>
  );
}
