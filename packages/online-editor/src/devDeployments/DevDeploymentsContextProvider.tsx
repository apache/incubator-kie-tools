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
import { KieSandboxOpenShiftService } from "./services/openshift/KieSandboxOpenShiftService";
import { ConfirmDeployModalState, DeleteDeployModalState, DevDeploymentsContext } from "./DevDeploymentsContext";
import { useWorkspaces, WorkspaceFile } from "@kie-tools-core/workspaces-git-fs/dist/context/WorkspacesContext";
import { NEW_WORKSPACE_DEFAULT_NAME } from "@kie-tools-core/workspaces-git-fs/dist/worker/api/WorkspaceDescriptor";
import { DevDeploymentsConfirmDeleteModal } from "./DevDeploymentsConfirmDeleteModal";
import { KieSandboxKubernetesService } from "./services/KieSandboxKubernetesService";
import { CloudAuthSession } from "../authSessions/AuthSessionApi";
import { KubernetesConnectionStatus } from "@kie-tools-core/kubernetes-bridge/dist/service";
import { useEnv } from "../env/hooks/EnvContext";
import {
  parseK8sResourceYaml,
  buildK8sApiServerEndpointsByResourceKind,
  callK8sApiServer,
} from "@kie-tools-core/k8s-yaml-to-apiserver-requests/dist";
import { TokenInterpolationService } from "./services/TokenInterpolationService";
import { useRoutes } from "../navigation/Hooks";

interface Props {
  children: React.ReactNode;
}

export function DevDeploymentsContextProvider(props: Props) {
  const workspaces = useWorkspaces();
  const { env } = useEnv();
  const routes = useRoutes();

  // Dropdowns
  const [isDeployDropdownOpen, setDeployDropdownOpen] = useState(false);
  const [isDeploymentsDropdownOpen, setDeploymentsDropdownOpen] = useState(false);

  // Modals
  const [confirmDeployModalState, setConfirmDeployModalState] = useState<ConfirmDeployModalState>({ isOpen: false });
  const [confirmDeleteModalState, setConfirmDeleteModalState] = useState<DeleteDeployModalState>({ isOpen: false });

  // Service
  const getService = useCallback(
    (authSession: CloudAuthSession) => {
      if (authSession.type === "openshift") {
        return new KieSandboxOpenShiftService({
          connection: authSession,
          proxyUrl: env.KIE_SANDBOX_CORS_PROXY_URL,
        });
      } else if (authSession.type === "kubernetes") {
        return new KieSandboxKubernetesService({
          connection: authSession,
        });
      }
      throw new Error("Invalid AuthSession type.");
    },
    [env.KIE_SANDBOX_CORS_PROXY_URL]
  );

  const deleteDeployment = useCallback(
    async (args: { authSession: CloudAuthSession; resourceName: string }) => {
      const service = getService(args.authSession);

      try {
        await service.deleteDevDeployment(args.resourceName);
        return true;
      } catch (error) {
        console.error(error);
        return false;
      }
    },
    [getService]
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

  const loadDeployments = useCallback(
    async (args: { authSession: CloudAuthSession }) => {
      const service = getService(args.authSession);

      return service.loadDeployedModels().catch((e) => {
        console.error(e);
        throw e;
      });
    },
    [getService]
  );

  const deploy = useCallback(
    async (workspaceFile: WorkspaceFile, authSession: CloudAuthSession) => {
      const service = getService(authSession);

      if ((await service.isConnectionEstablished()) !== KubernetesConnectionStatus.CONNECTED) {
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
          containerImageUrl: env.KIE_SANDBOX_DMN_DEV_DEPLOYMENT_BASE_IMAGE_URL,
        });
        return true;
      } catch (error) {
        console.error(error);
        return false;
      }
    },
    [env.KIE_SANDBOX_DMN_DEV_DEPLOYMENT_BASE_IMAGE_URL, getService, workspaces]
  );

  const dynamicDeploy = useCallback(
    async (workspaceFile: WorkspaceFile, authSession: CloudAuthSession) => {
      const service = getService(authSession);

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

      const k8sApiServerEndpointsByResourceKind = await buildK8sApiServerEndpointsByResourceKind(
        authSession.host,
        authSession.token
      );

      console.log(k8sApiServerEndpointsByResourceKind);

      const tokenInterpolationService = new TokenInterpolationService({
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
      });

      const deploymentResource = await fetch(routes.static.devDeployments.kubernetes.resources.deployment.path({}))
        .then((res) => res.blob())
        .then((res) => res.text());
      const serviceResource = await fetch(routes.static.devDeployments.kubernetes.resources.service.path({}))
        .then((res) => res.blob())
        .then((res) => res.text());
      const ingressResource = await fetch(routes.static.devDeployments.kubernetes.resources.ingress.path({}))
        .then((res) => res.blob())
        .then((res) => res.text());

      const interpolatedDeploymentResource = tokenInterpolationService.doInterpolation(deploymentResource);
      const interpolatedServiceResource = tokenInterpolationService.doInterpolation(serviceResource);
      const interpolatedIngressResource = tokenInterpolationService.doInterpolation(ingressResource);

      await callK8sApiServer({
        k8sApiServerEndpointsByResourceKind,
        k8sResourceYamls: parseK8sResourceYaml([
          interpolatedDeploymentResource,
          interpolatedServiceResource,
          interpolatedIngressResource,
        ]),
        k8sApiServerUrl: authSession.host,
        k8sNamespace: authSession.namespace,
        k8sServiceAccountToken: authSession.token,
      });

      // try {
      //   await service.deploy({
      //     targetFilePath: workspaceFile.relativePath,
      //     workspaceName,
      //     workspaceZipBlob: zipBlob,
      //     containerImageUrl: env.KIE_SANDBOX_DMN_DEV_DEPLOYMENT_BASE_IMAGE_URL,
      //   });
      //   return true;
      // } catch (error) {
      //   console.error(error);
      //   return false;
      // }
      return true;
    },
    [
      env.KIE_SANDBOX_DMN_DEV_DEPLOYMENT_BASE_IMAGE_URL,
      getService,
      routes.static.devDeployments.kubernetes.resources,
      workspaces,
    ]
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
      dynamicDeploy,
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
      dynamicDeploy,
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
