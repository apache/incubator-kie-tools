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
import { isOpenShiftConfigValid } from "../settings/openshift/OpenShiftSettingsConfig";
import { isServiceAccountConfigValid } from "../settings/serviceAccount/ServiceAccountConfig";
import { isServiceRegistryConfigValid } from "../settings/serviceRegistry/ServiceRegistryConfig";
import { useSettings, useSettingsDispatch } from "../settings/SettingsContext";
import { NEW_WORKSPACE_DEFAULT_NAME } from "../workspace/services/WorkspaceDescriptorService";
import { useWorkspaces } from "../workspace/WorkspacesContext";
import { DeploymentWorkflow, OpenShiftContext } from "./OpenShiftContext";
import { OpenShiftDeployedModel } from "./OpenShiftDeployedModel";
import { OpenShiftInstanceStatus } from "./OpenShiftInstanceStatus";

interface Props {
  children: React.ReactNode;
}

const DEFAULT_GROUP_ID = "org.kie";
const LOAD_DEPLOYMENTS_POLLING_TIME = 1000;

export function OpenShiftContextProvider(props: Props) {
  const settings = useSettings();
  const settingsDispatch = useSettingsDispatch();
  const workspaces = useWorkspaces();
  const [deployments, setDeployments] = useState([] as OpenShiftDeployedModel[]);
  const [isDeploymentsDropdownOpen, setDeploymentsDropdownOpen] = useState(false);

  const deploy = useCallback(
    async (workflow: DeploymentWorkflow) => {
      if (!isOpenShiftConfigValid(settings.openshift.config)) {
        throw new Error("Invalid OpenShift config");
      }

      const descriptorService = await workspaces.descriptorService.get(workflow.workspaceFile.workspaceId);
      const workspaceName =
        descriptorService.name !== NEW_WORKSPACE_DEFAULT_NAME ? descriptorService.name : workflow.workspaceFile.name;

      return settingsDispatch.openshift.service.deploy({
        workflow: workflow,
        workspaceName: workspaceName,
        openShiftConfig: settings.openshift.config,
        kafkaConfig: settings.apacheKafka.config,
        serviceAccountConfig: settings.serviceAccount.config,
      });
    },
    [
      settings.apacheKafka.config,
      settings.openshift.config,
      settings.serviceAccount.config,
      settingsDispatch.openshift.service,
      workspaces.descriptorService,
    ]
  );

  const fetchOpenApiFile = useCallback(
    async (resourceName: string) => {
      if (!isOpenShiftConfigValid(settings.openshift.config)) {
        throw new Error("Invalid OpenShift config");
      }

      try {
        const routeUrl = await settingsDispatch.openshift.service.getDeploymentRoute({
          config: settings.openshift.config,
          resourceName,
        });
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

      await settingsDispatch.openshift.service.uploadOpenApiToServiceRegistry({
        proxyUrl: settings.openshift.config.proxy,
        groupId: DEFAULT_GROUP_ID,
        artifactId: artifactId,
        serviceAccountConfig: settings.serviceAccount.config,
        serviceRegistryConfig: settings.serviceRegistry.config,
        openApiContent: content,
      });
    },
    [
      settingsDispatch.openshift.service,
      settings.serviceAccount.config,
      settings.serviceRegistry.config,
      settings.openshift.config,
    ]
  );

  useEffect(() => {
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
          return isConfigOk ? settingsDispatch.openshift.service.loadDeployments(settings.openshift.config) : [];
        })
        .then((ds) => setDeployments(ds))
        .catch((error) => console.error(error));
      return;
    }

    if (settings.openshift.status === OpenShiftInstanceStatus.CONNECTED && isDeploymentsDropdownOpen) {
      const loadDeploymentsTask = window.setInterval(() => {
        settingsDispatch.openshift.service
          .loadDeployments(settings.openshift.config)
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
  ]);

  const value = useMemo(
    () => ({
      deployments,
      isDeploymentsDropdownOpen,
      setDeploymentsDropdownOpen,
      deploy,
      uploadArtifactToServiceRegistry,
      fetchOpenApiFile,
    }),
    [deployments, isDeploymentsDropdownOpen, deploy, uploadArtifactToServiceRegistry, fetchOpenApiFile]
  );

  return <OpenShiftContext.Provider value={value}>{props.children}</OpenShiftContext.Provider>;
}
