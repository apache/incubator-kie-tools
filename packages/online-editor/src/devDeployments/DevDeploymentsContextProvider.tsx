/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

import React, { useCallback, useMemo, useState, useEffect } from "react";
import { KieSandboxOpenShiftService } from "./services/KieSandboxOpenShiftService";
import { ConfirmDeployModalState, DeleteDeployModalState, DevDeploymentsContext } from "./DevDeploymentsContext";
import { useWorkspaces, WorkspaceFile } from "@kie-tools-core/workspaces-git-fs/dist/context/WorkspacesContext";
import { NEW_WORKSPACE_DEFAULT_NAME } from "@kie-tools-core/workspaces-git-fs/dist/worker/api/WorkspaceDescriptor";
import { DevDeploymentsConfirmDeleteModal } from "./DevDeploymentsConfirmDeleteModal";
import { KieSandboxKubernetesService } from "./services/KieSandboxKubernetesService";
import { AuthSession, CloudAuthSession, isCloudAuthSession } from "../authSessions/AuthSessionApi";
import { KubernetesConnectionStatus } from "@kie-tools-core/kubernetes-bridge/dist/service";
import { useEnv } from "../env/hooks/EnvContext";
import { useRoutes } from "../navigation/Hooks";
import { defaultAnnotationTokens, defaultLabelTokens } from "./services/types";
import { KubernetesService } from "./services/KubernetesService";
import { useAuthSessions } from "../authSessions/AuthSessionsContext";
import { KieSandboxDevDeploymentsService } from "./services/KieSandboxDevDeploymentsService";

interface Props {
  children: React.ReactNode;
}

export function DevDeploymentsContextProvider(props: Props) {
  const workspaces = useWorkspaces();
  const { authSessions } = useAuthSessions();
  const { env } = useEnv();

  // Dropdowns
  const [isDeployDropdownOpen, setDeployDropdownOpen] = useState(false);
  const [isDeploymentsDropdownOpen, setDeploymentsDropdownOpen] = useState(false);

  // Modals
  const [confirmDeployModalState, setConfirmDeployModalState] = useState<ConfirmDeployModalState>({ isOpen: false });
  const [confirmDeleteModalState, setConfirmDeleteModalState] = useState<DeleteDeployModalState>({ isOpen: false });

  // Services
  const [devDeploymentsServices, setDevDeploymentsServices] = useState<Map<string, KieSandboxDevDeploymentsService>>(
    new Map()
  );

  const getService = useCallback(
    (authSession: CloudAuthSession) => {
      if (authSession.type === "openshift") {
        return new KieSandboxOpenShiftService({
          connection: authSession,
          proxyUrl: env.KIE_SANDBOX_CORS_PROXY_URL,
          k8sApiServerEndpointsByResourceKind: authSession.k8sApiServerEndpointsByResourceKind,
        });
      } else if (authSession.type === "kubernetes") {
        return new KieSandboxKubernetesService({
          connection: authSession,
          k8sApiServerEndpointsByResourceKind: authSession.k8sApiServerEndpointsByResourceKind,
        });
      }
      throw new Error("Invalid AuthSession type.");
    },
    [env.KIE_SANDBOX_CORS_PROXY_URL]
  );

  useEffect(() => {
    const newDevDeploymentsServices = new Map<string, KieSandboxDevDeploymentsService>();
    authSessions.forEach(async (authSession) => {
      if (!authSession || !isCloudAuthSession(authSession)) {
        return;
      }
      try {
        newDevDeploymentsServices.set(authSession.id, getService(authSession));
      } catch (e) {
        console.error(`Failed to create service for authSession: ${authSession.id}`);
      }
    });
    setDevDeploymentsServices(newDevDeploymentsServices);
  }, [authSessions, getService]);

  // Deployments
  const deleteDeployment = useCallback(
    async (args: { authSession: CloudAuthSession; resourceName: string }) => {
      try {
        await devDeploymentsServices.get(args.authSession.id)?.deleteDevDeployment(args.resourceName);
        return true;
      } catch (error) {
        console.error(error);
        return false;
      }
    },
    [devDeploymentsServices]
  );
  const deleteDeployments = useCallback(
    async (args: { authSession: CloudAuthSession; resourceNames: string[] }) => {
      const result = await Promise.all(
        args.resourceNames.map((resourceName) => {
          return deleteDeployment({ authSession: args.authSession, resourceName });
        })
      );

      return result.every(Boolean);
    },
    [deleteDeployment]
  );

  const loadDevDeployments = useCallback(
    async (args: { authSession: CloudAuthSession }) => {
      return (
        devDeploymentsServices
          .get(args.authSession.id)
          ?.loadDevDeployments()
          .catch((e) => {
            console.error(e);
            throw e;
          }) || []
      );
    },
    [devDeploymentsServices]
  );

  // const deploy = useCallback(
  //   async (workspaceFile: WorkspaceFile, authSession: CloudAuthSession) => {
  //     const service = devDeploymentServices.get(authSession.id);
  //     if (!service) {
  //       throw new Error(`Missing service for authSession with id ${authSession.id}.`);
  //     }

  //     if ((await service.isConnectionEstablished()) !== KubernetesConnectionStatus.CONNECTED) {
  //       return false;
  //     }

  //     const zipBlob = await workspaces.prepareZip({
  //       workspaceId: workspaceFile.workspaceId,
  //       onlyExtensions: ["dmn"],
  //     });

  //     const workspace = await workspaces.getWorkspace({ workspaceId: workspaceFile.workspaceId });

  //     const workspaceName = workspace.name !== NEW_WORKSPACE_DEFAULT_NAME ? workspace.name : workspaceFile.name;

  //     try {
  //       await service.deploy({
  //         targetFilePath: workspaceFile.relativePath,
  //         workspaceName,
  //         workspaceZipBlob: zipBlob,
  //         containerImageUrl: env.KIE_SANDBOX_DMN_DEV_DEPLOYMENT_BASE_IMAGE_URL,
  //       });
  //       return true;
  //     } catch (error) {
  //       console.error(error);
  //       return false;
  //     }
  //   },
  //   [env.KIE_SANDBOX_DMN_DEV_DEPLOYMENT_BASE_IMAGE_URL, devDeploymentServices, workspaces]
  // );

  const deploy = useCallback(
    async (workspaceFile: WorkspaceFile, authSession: CloudAuthSession) => {
      const service = devDeploymentsServices.get(authSession.id);
      if (!service) {
        throw new Error(`Missing service for authSession with id ${authSession.id}.`);
      }

      if ((await service.isConnectionEstablished()) !== KubernetesConnectionStatus.CONNECTED) {
        return false;
      }

      const zipBlob = await workspaces.prepareZip({
        workspaceId: workspaceFile.workspaceId,
        onlyExtensions: ["dmn"],
      });

      const workspace = await workspaces.getWorkspace({ workspaceId: workspaceFile.workspaceId });

      const workspaceName = workspace.name !== NEW_WORKSPACE_DEFAULT_NAME ? workspace.name : workspaceFile.name;
      const workspaceId = workspace.workspaceId;

      const tokenMap = {
        devDeployment: {
          labels: defaultLabelTokens,
          annotations: defaultAnnotationTokens,
          name: "dev-deployment",
          uniqueId: "123",
          uploadService: {
            apiKey: "abc123",
          },
          workspace: {
            id: workspaceId,
            name: workspaceName,
            resourceName: workspaceFile.relativePath,
          },
          kubernetes: {
            namespace: authSession.namespace,
          },
          defaultContainerImageUrl: env.KIE_SANDBOX_DMN_DEV_DEPLOYMENT_BASE_IMAGE_URL,
        },
      };

      try {
        await service.deploy({
          workspaceZipBlob: zipBlob,
          tokenMap,
        });
        return true;
      } catch (error) {
        console.error(error);
        return false;
      }
    },
    [devDeploymentsServices, env.KIE_SANDBOX_DMN_DEV_DEPLOYMENT_BASE_IMAGE_URL, workspaces]
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
      loadDevDeployments,
      devDeploymentsServices,
    }),
    [
      isDeployDropdownOpen,
      isDeploymentsDropdownOpen,
      confirmDeployModalState,
      confirmDeleteModalState,
      deploy,
      deleteDeployment,
      deleteDeployments,
      loadDevDeployments,
      devDeploymentsServices,
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
