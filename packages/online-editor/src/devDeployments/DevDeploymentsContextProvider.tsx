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
import { useRoutes } from "../navigation/Hooks";
import { useExtendedServices } from "../kieSandboxExtendedServices/KieSandboxExtendedServicesContext";
import { KieSandboxExtendedServicesStatus } from "../kieSandboxExtendedServices/KieSandboxExtendedServicesStatus";
import { KieSandboxOpenShiftDeployedModel } from "../openshift/KieSandboxOpenShiftService";
import { DevDeploymentsContext } from "./DevDeploymentsContext";
import { OpenShiftInstanceStatus } from "../openshift/OpenShiftInstanceStatus";
import { useSettings, useSettingsDispatch } from "../settings/SettingsContext";
import { isOpenShiftConnectionValid } from "@kie-tools-core/openshift/dist/service/OpenShiftConnection";
import { useWorkspaces, WorkspaceFile } from "@kie-tools-core/workspaces-git-fs/dist/context/WorkspacesContext";
import { NEW_WORKSPACE_DEFAULT_NAME } from "@kie-tools-core/workspaces-git-fs/dist/worker/api/WorkspaceDescriptor";
import { DevDeploymentsConfirmDeleteModal } from "./DevDeploymentsConfirmDeleteModal";

interface Props {
  children: React.ReactNode;
}

const LOAD_DEPLOYMENTS_POLLING_TIME = 2500;

export function DevDeploymentsContextProvider(props: Props) {
  const settings = useSettings();
  const settingsDispatch = useSettingsDispatch();
  const routes = useRoutes();
  const extendedServices = useExtendedServices();
  const workspaces = useWorkspaces();

  const [isDropdownOpen, setDropdownOpen] = useState(false);
  const [isDeploymentsDropdownOpen, setDeploymentsDropdownOpen] = useState(false);
  const [isConfirmDeployModalOpen, setConfirmDeployModalOpen] = useState(false);
  const [deployments, setDeployments] = useState([] as KieSandboxOpenShiftDeployedModel[]);
  const [isConfirmDeleteModalOpen, setConfirmDeleteModalOpen] = useState(false);
  const [deploymentsToBeDeleted, setDeploymentsToBeDeleted] = useState<string[]>([]);

  const onDisconnect = useCallback((closeModals: boolean) => {
    setDropdownOpen(false);
    setDeployments([]);

    if (closeModals) {
      setConfirmDeployModalOpen(false);
    }
  }, []);

  const deleteDeployment = useCallback(async (resourceName: string) => {
    //   try {
    //     await settingsDispatch.openshift.service.deleteDeployment(resourceName);
    //     return true;
    //   } catch (error) {
    //     console.error(error);
    //     return false;
    //   }
    return false;
  }, []);

  const deleteDeployments = useCallback(async () => {
    // const result = await Promise.all(deploymentsToBeDeleted.map((resourceName) => deleteDeployment(resourceName)));
    // setDeploymentsToBeDeleted([]);
    // return result.every(Boolean);
    return false;
  }, []);

  const loadDeployments = useCallback(async (errCallback?: () => void) => {
    // return settingsDispatch.openshift.service
    //   .loadDeployments()
    //   .then((deployments) => setDeployments(deployments))
    //   .catch((error) => {
    //     setDeployments([]);
    //     errCallback?.();
    //     console.error(error);
    //   });
  }, []);

  const deploy = useCallback(async (workspaceFile: WorkspaceFile) => {
    return false;
    // if (
    //   !(
    //     isOpenShiftConnectionValid(settings.openshift.config) &&
    //     (await settingsDispatch.openshift.service.isConnectionEstablished(settings.openshift.config))
    //   )
    // ) {
    //   return false;
    // }

    // const zipBlob = await workspaces.prepareZip({
    //   workspaceId: workspaceFile.workspaceId,
    //   onlyExtensions: ["dmn"],
    // });

    // const descriptorService = await workspaces.getWorkspace({ workspaceId: workspaceFile.workspaceId });

    // const workspaceName =
    //   descriptorService.name !== NEW_WORKSPACE_DEFAULT_NAME ? descriptorService.name : workspaceFile.name;

    // try {
    //   await settingsDispatch.openshift.service.deploy({
    //     targetFilePath: workspaceFile.relativePath,
    //     workspaceName,
    //     workspaceZipBlob: zipBlob,
    //     onlineEditorUrl: (baseUrl) =>
    //       routes.import.url({
    //         base: process.env.WEBPACK_REPLACE__devDeployments_onlineEditorUrl,
    //         pathParams: {},
    //         queryParams: { url: `${baseUrl}/${workspaceFile.relativePath}` },
    //       }),
    //   });
    //   return true;
    // } catch (error) {
    //   console.error(error);
    //   return false;
    // }
  }, []);

  useEffect(() => {
    // if (extendedServices.status !== KieSandboxExtendedServicesStatus.RUNNING) {
    //   onDisconnect(true);
    //   return;
    // }
    // if (!isOpenShiftConnectionValid(settings.openshift.config)) {
    //   if (deployments.length > 0) {
    //     setDeployments([]);
    //   }
    //   return;
    // }
    // if (settings.openshift.status === OpenShiftInstanceStatus.DISCONNECTED) {
    //   settingsDispatch.openshift.service
    //     .isConnectionEstablished(settings.openshift.config)
    //     .then((isConfigOk: boolean) => {
    //       settingsDispatch.openshift.setStatus(
    //         isConfigOk ? OpenShiftInstanceStatus.CONNECTED : OpenShiftInstanceStatus.EXPIRED
    //       );
    //       return isConfigOk ? settingsDispatch.openshift.service.loadDeployments() : [];
    //     })
    //     .then((deployments) => setDeployments(deployments))
    //     .catch((error) => console.error(error));
    //   return;
    // }
    // if (settings.openshift.status === OpenShiftInstanceStatus.CONNECTED && isDeploymentsDropdownOpen) {
    //   const loadDeploymentsTask = window.setInterval(() => {
    //     settingsDispatch.openshift.service
    //       .loadDeployments()
    //       .then((deployments) => setDeployments(deployments))
    //       .catch((error) => {
    //         setDeployments([]);
    //         window.clearInterval(loadDeploymentsTask);
    //         console.error(error);
    //       });
    //   }, LOAD_DEPLOYMENTS_POLLING_TIME);
    //   return () => window.clearInterval(loadDeploymentsTask);
    // }
  }, []);

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
    <DevDeploymentsContext.Provider value={value}>
      <>
        {props.children}
        <DevDeploymentsConfirmDeleteModal />
      </>
    </DevDeploymentsContext.Provider>
  );
}
