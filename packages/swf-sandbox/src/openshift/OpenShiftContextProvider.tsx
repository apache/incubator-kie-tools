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
import { SwfServiceCatalogStore } from "../editor/api/SwfServiceCatalogStore";
import { useKieSandboxExtendedServices } from "../kieSandboxExtendedServices/KieSandboxExtendedServicesContext";
import { KieSandboxExtendedServicesStatus } from "../kieSandboxExtendedServices/KieSandboxExtendedServicesStatus";
import { isOpenShiftConfigValid } from "../settings/openshift/OpenShiftSettingsConfig";
import { isServiceAccountConfigValid } from "../settings/serviceAccount/ServiceAccountConfig";
import { isServiceRegistryConfigValid } from "../settings/serviceRegistry/ServiceRegistryConfig";
import { useSettings, useSettingsDispatch } from "../settings/SettingsContext";
import { NEW_WORKSPACE_DEFAULT_NAME } from "../workspace/services/WorkspaceDescriptorService";
import { encoder, useWorkspaces, WorkspaceFile } from "../workspace/WorkspacesContext";
import { createDockerfileContent } from "./FileTemplate";
import { OpenShiftContext } from "./OpenShiftContext";
import { OpenShiftDeployedModel } from "./OpenShiftDeployedModel";
import { OpenShiftInstanceStatus } from "./OpenShiftInstanceStatus";

interface Props {
  children: React.ReactNode;
}

const DEFAULT_GROUP_ID = "org.kie";
const LOAD_DEPLOYMENTS_POLLING_TIME = 2500;

export function OpenShiftContextProvider(props: Props) {
  const settings = useSettings();
  const settingsDispatch = useSettingsDispatch();
  const workspaces = useWorkspaces();
  const kieSandboxExtendedServices = useKieSandboxExtendedServices();
  const [deployments, setDeployments] = useState([] as OpenShiftDeployedModel[]);
  const [isDeployDropdownOpen, setDeployDropdownOpen] = useState(false);
  const [isDeploymentsDropdownOpen, setDeploymentsDropdownOpen] = useState(false);
  const [isConfirmDeployModalOpen, setConfirmDeployModalOpen] = useState(false);

  const onDisconnect = useCallback(
    (closeModals: boolean) => {
      settingsDispatch.openshift.setStatus(OpenShiftInstanceStatus.DISCONNECTED);
      setDeploymentsDropdownOpen(false);
      setDeployments([]);

      if (closeModals) {
        setConfirmDeployModalOpen(false);
      }
    },
    [settingsDispatch.openshift]
  );

  const deploy = useCallback(
    async (args: { workspaceFile: WorkspaceFile; shouldAttachKafkaSource: boolean }) => {
      if (!isOpenShiftConfigValid(settings.openshift.config)) {
        throw new Error("Invalid OpenShift config");
      }

      try {
        const descriptor = await workspaces.descriptorService.get(args.workspaceFile.workspaceId);
        const fs = await workspaces.fsService.getWorkspaceFs(args.workspaceFile.workspaceId);
        const filesToBeDeployed = await workspaces.getFiles({
          fs,
          workspaceId: args.workspaceFile.workspaceId,
          globPattern: "**/*.sw.+(json|yml|yaml)",
        });

        const workspaceName =
          filesToBeDeployed.length > 1 && descriptor.name !== NEW_WORKSPACE_DEFAULT_NAME
            ? descriptor.name
            : args.workspaceFile.name;

        const dockerfile = new WorkspaceFile({
          workspaceId: args.workspaceFile.workspaceId,
          relativePath: "Dockerfile",
          getFileContents: async () => encoder.encode(createDockerfileContent()),
        });

        filesToBeDeployed.push(dockerfile);

        const zipBlob = await workspaces.prepareZipWithFiles({
          workspaceId: args.workspaceFile.workspaceId,
          files: filesToBeDeployed,
        });

        return settingsDispatch.openshift.service.deploy({
          workspaceName: workspaceName,
          targetFilePath: args.workspaceFile.relativePath,
          workspaceZipBlob: zipBlob,
          shouldAttachKafkaSource: args.shouldAttachKafkaSource,
        });
      } catch (e) {
        console.error(e);
      }
    },
    [settings.openshift.config, settingsDispatch.openshift.service, workspaces]
  );

  const fetchOpenApiFile = useCallback(
    async (resourceName: string) => {
      if (!isOpenShiftConfigValid(settings.openshift.config)) {
        throw new Error("Invalid OpenShift config");
      }

      try {
        const routeUrl = await settingsDispatch.openshift.service.getDeploymentRoute(resourceName);
        if (!routeUrl) {
          return;
        }

        const response = await fetch(`${routeUrl}/q/openapi?format=json`);
        if (!response.ok) {
          return;
        }

        return await response.text();
      } catch (error) {
        console.error(error);
        return;
      }
    },
    [settings.openshift.config, settingsDispatch.openshift.service]
  );

  const uploadArtifactToServiceRegistry = useCallback(
    async (artifactId: string, content: string) => {
      if (!isServiceAccountConfigValid(settings.serviceAccount.config)) {
        throw new Error("Invalid service account config");
      }

      if (!isServiceRegistryConfigValid(settings.serviceRegistry.config)) {
        throw new Error("Invalid service registry config");
      }

      await SwfServiceCatalogStore.uploadArtifact({
        artifactId: artifactId,
        groupId: DEFAULT_GROUP_ID,
        content: content,
        proxyUrl: settings.kieSandboxExtendedServices.config.buildUrl(),
        serviceAccountConfig: settings.serviceAccount.config,
        serviceRegistryConfig: settings.serviceRegistry.config,
      });
    },
    [settings.serviceAccount.config, settings.serviceRegistry.config, settings.kieSandboxExtendedServices.config]
  );

  useEffect(() => {
    if (kieSandboxExtendedServices.status !== KieSandboxExtendedServicesStatus.RUNNING) {
      onDisconnect(true);
      return;
    }

    if (!isOpenShiftConfigValid(settings.openshift.config)) {
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
        .then((ds) => setDeployments(ds))
        .catch((error) => console.error(error));
      return;
    }

    if (settings.openshift.status === OpenShiftInstanceStatus.CONNECTED && isDeploymentsDropdownOpen) {
      const loadDeploymentsTask = window.setInterval(() => {
        settingsDispatch.openshift.service
          .loadDeployments()
          .then((ds) => setDeployments(ds))
          .catch((error) => {
            setDeployments([]);
            window.clearInterval(loadDeploymentsTask);
            console.error(error);
          });
      }, LOAD_DEPLOYMENTS_POLLING_TIME);
      return () => window.clearInterval(loadDeploymentsTask);
    }
  }, [
    settings.openshift,
    settingsDispatch.openshift.service,
    deployments.length,
    settingsDispatch.openshift,
    isDeploymentsDropdownOpen,
    kieSandboxExtendedServices.status,
    onDisconnect,
  ]);

  const value = useMemo(
    () => ({
      deployments,
      isDeployDropdownOpen,
      setDeployDropdownOpen,
      isDeploymentsDropdownOpen,
      setDeploymentsDropdownOpen,
      isConfirmDeployModalOpen,
      setConfirmDeployModalOpen,
      deploy,
      uploadArtifactToServiceRegistry,
      fetchOpenApiFile,
    }),
    [
      deployments,
      isDeployDropdownOpen,
      isDeploymentsDropdownOpen,
      isConfirmDeployModalOpen,
      deploy,
      uploadArtifactToServiceRegistry,
      fetchOpenApiFile,
    ]
  );

  return <OpenShiftContext.Provider value={value}>{props.children}</OpenShiftContext.Provider>;
}
