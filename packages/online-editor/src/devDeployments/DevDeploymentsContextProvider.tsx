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
import { useCallback, useMemo, useState } from "react";
import { useRoutes } from "../navigation/Hooks";
import { useExtendedServices } from "../kieSandboxExtendedServices/KieSandboxExtendedServicesContext";
import { KieSandboxOpenShiftService } from "../openshift/KieSandboxOpenShiftService";
import { ConfirmDeployModalState, DeleteDeployModalState, DevDeploymentsContext } from "./DevDeploymentsContext";
import { OpenShiftConnection } from "@kie-tools-core/openshift/dist/service/OpenShiftConnection";
import { useWorkspaces, WorkspaceFile } from "@kie-tools-core/workspaces-git-fs/dist/context/WorkspacesContext";
import { NEW_WORKSPACE_DEFAULT_NAME } from "@kie-tools-core/workspaces-git-fs/dist/worker/api/WorkspaceDescriptor";
import { DevDeploymentsConfirmDeleteModal } from "./DevDeploymentsConfirmDeleteModal";

interface Props {
  children: React.ReactNode;
}

export function DevDeploymentsContextProvider(props: Props) {
  const routes = useRoutes();
  const extendedServices = useExtendedServices();
  const workspaces = useWorkspaces();

  // Dropdowns
  const [isDeployDropdownOpen, setDeployDropdownOpen] = useState(false);
  const [isDeploymentsDropdownOpen, setDeploymentsDropdownOpen] = useState(false);

  // Modals
  const [confirmDeployModalState, setConfirmDeployModalState] = useState<ConfirmDeployModalState>({ isOpen: false });
  const [confirmDeleteModalState, setConfirmDeleteModalState] = useState<DeleteDeployModalState>({ isOpen: false });

  const deleteDeployment = useCallback(
    async (args: { connection: OpenShiftConnection; resourceName: string }) => {
      const service = new KieSandboxOpenShiftService({
        connection: args.connection,
        proxyUrl: extendedServices.config.url.corsProxy,
      });

      try {
        await service.deleteDeployment(args.resourceName);
        return true;
      } catch (error) {
        console.error(error);
        return false;
      }
    },
    [extendedServices.config.url.corsProxy]
  );

  const deleteDeployments = useCallback(
    async (args: { connection: OpenShiftConnection; resourceNames: string[] }) => {
      const result = await Promise.all(
        args.resourceNames.map((resourceName) => {
          return deleteDeployment({ connection: args.connection, resourceName });
        })
      );

      return result.every(Boolean);
    },
    [deleteDeployment]
  );

  const loadDeployments = useCallback(
    async (args: { connection: OpenShiftConnection }) => {
      const service = new KieSandboxOpenShiftService({
        connection: args.connection,
        proxyUrl: extendedServices.config.url.corsProxy,
      });

      return service.loadDeployments().catch((e) => {
        console.error(e);
        throw e;
      });
    },
    [extendedServices.config.url.corsProxy]
  );

  const deploy = useCallback(
    async (workspaceFile: WorkspaceFile, connection: OpenShiftConnection) => {
      const service = new KieSandboxOpenShiftService({
        connection,
        proxyUrl: extendedServices.config.url.corsProxy,
      });

      if (!(await service.isConnectionEstablished())) {
        return false;
      }

      const zipBlob = await workspaces.prepareZip({
        workspaceId: workspaceFile.workspaceId,
        onlyExtensions: ["dmn"],
      });

      const workspace = await workspaces.getWorkspace({ workspaceId: workspaceFile.workspaceId });

      const workspaceName = workspace.name !== NEW_WORKSPACE_DEFAULT_NAME ? workspace.name : workspaceFile.name;

      try {
        await service.deploy({
          targetFilePath: workspaceFile.relativePath,
          workspaceName,
          workspaceZipBlob: zipBlob,
          onlineEditorUrl: (baseUrl) =>
            routes.import.url({
              base: process.env.WEBPACK_REPLACE__devDeployments_onlineEditorUrl,
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
    [extendedServices.config.url.corsProxy, routes.import, workspaces]
  );

  const value = useMemo(
    () => ({
      isDeployDropdownOpen,
      isDeploymentsDropdownOpen,
      confirmDeployModalState,
      confirmDeleteModalState,
      setDeployDropdownOpen,
      setConfirmDeployModalState,
      setConfirmDeleteModalState,
      setDeploymentsDropdownOpen,
      deploy,
      deleteDeployment,
      deleteDeployments,
      loadDeployments,
    }),
    [
      isDeployDropdownOpen,
      isDeploymentsDropdownOpen,
      confirmDeployModalState,
      confirmDeleteModalState,
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
