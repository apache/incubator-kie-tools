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
import { GLOB_PATTERN } from "../extension";
import { useKieSandboxExtendedServices } from "../kieSandboxExtendedServices/KieSandboxExtendedServicesContext";
import { KieSandboxExtendedServicesStatus } from "../kieSandboxExtendedServices/KieSandboxExtendedServicesStatus";
import { PROJECT_FILES } from "../project";
import { isOpenShiftConfigValid } from "../settings/openshift/OpenShiftSettingsConfig";
import { isServiceAccountConfigValid } from "../settings/serviceAccount/ServiceAccountConfig";
import { isServiceRegistryConfigValid } from "../settings/serviceRegistry/ServiceRegistryConfig";
import { useSettings, useSettingsDispatch } from "../settings/SettingsContext";
import { encoder } from "../workspace/commonServices/BaseFile";
import { NEW_WORKSPACE_DEFAULT_NAME } from "../workspace/services/WorkspaceDescriptorService";
import { useWorkspaces, WorkspaceFile } from "../workspace/WorkspacesContext";
import {
  createDockerfileContentForBaseJdk11MvnImage,
  createDockerfileContentForBaseQuarkusProjectImage,
  createDockerIgnoreContent,
} from "./FileTemplate";
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
    (close: boolean) => {
      settingsDispatch.openshift.setStatus(OpenShiftInstanceStatus.DISCONNECTED);
      setDeployments([]);

      if (close) {
        setDeployDropdownOpen(false);
        setDeploymentsDropdownOpen(false);
        setConfirmDeployModalOpen(false);
      }
    },
    [settingsDispatch.openshift]
  );

  const deploy = useCallback(
    async (args: {
      workspaceFile: WorkspaceFile;
      shouldAttachKafkaSource: boolean;
      shouldDeployAsProject: boolean;
    }) => {
      if (!isOpenShiftConfigValid(settings.openshift.config)) {
        throw new Error("Invalid OpenShift config");
      }

      try {
        const descriptor = await workspaces.descriptorService.get(args.workspaceFile.workspaceId);
        const fs = await workspaces.fsService.getFs(args.workspaceFile.workspaceId);
        const filesToBeDeployed = (
          await workspaces.getFiles({
            fs,
            workspaceId: args.workspaceFile.workspaceId,
            globPattern: !args.shouldDeployAsProject ? GLOB_PATTERN.sw : undefined,
          })
        ).filter((f) => f.name !== PROJECT_FILES.dockerIgnore);

        const workspaceName =
          filesToBeDeployed.length > 1 && descriptor.name !== NEW_WORKSPACE_DEFAULT_NAME
            ? descriptor.name
            : args.workspaceFile.name;

        const dockerfileFile = new WorkspaceFile({
          workspaceId: args.workspaceFile.workspaceId,
          relativePath: PROJECT_FILES.dockerFile,
          getFileContents: async () => {
            const dockerfileContent = args.shouldDeployAsProject
              ? createDockerfileContentForBaseJdk11MvnImage({
                  projectName: workspaceName,
                  openShiftConfig: settings.openshift.config,
                })
              : createDockerfileContentForBaseQuarkusProjectImage();
            return encoder.encode(dockerfileContent);
          },
        });

        const dockerIgnoreFile = new WorkspaceFile({
          workspaceId: args.workspaceFile.workspaceId,
          relativePath: PROJECT_FILES.dockerIgnore,
          getFileContents: async () => encoder.encode(createDockerIgnoreContent()),
        });

        filesToBeDeployed.push(dockerfileFile, dockerIgnoreFile);

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

      settingsDispatch.serviceRegistry.catalogStore.uploadArtifact({
        artifactId: artifactId,
        groupId: DEFAULT_GROUP_ID,
        content: content,
      });
    },
    [settings.serviceAccount.config, settings.serviceRegistry.config, settingsDispatch.serviceRegistry.catalogStore]
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
            onDisconnect(true);
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
