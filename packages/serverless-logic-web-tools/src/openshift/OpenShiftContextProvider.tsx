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
import { useCallback, useMemo, useState, useEffect } from "react";
import { useSettings, useSettingsDispatch } from "../settings/SettingsContext";
import { InitDeployArgs, InitSwfDeployArgs } from "./deploy/types";
import { useDeploymentStrategy } from "./hooks/useDeploymentStrategy";
import { useOpenApi } from "./hooks/useOpenApi";
import { useRemoteServiceRegistry } from "./hooks/useRemoteServiceRegistry";
import { OpenShiftContext } from "./OpenShiftContext";
import { KnativeDeploymentLoaderPipeline } from "./pipelines/KnativeDeploymentLoaderPipeline";
import { ExtendedServicesStatus } from "../extendedServices/ExtendedServicesStatus";
import { useExtendedServices } from "../extendedServices/ExtendedServicesContext";
import { OpenShiftInstanceStatus } from "./OpenShiftInstanceStatus";
import {
  KubernetesConnectionStatus,
  isKubernetesConnectionValid,
} from "@kie-tools-core/kubernetes-bridge/dist/service";

const FETCH_OPEN_API_POLLING_TIME = 5000;

export function OpenShiftContextProvider(props: React.PropsWithChildren<{}>) {
  const settings = useSettings();
  const settingsDispatch = useSettingsDispatch();
  const extendedServices = useExtendedServices();
  const { createDeploymentStrategy } = useDeploymentStrategy();
  const { fetchOpenApiContent } = useOpenApi();
  const { uploadArtifact } = useRemoteServiceRegistry();

  const [isDeployDropdownOpen, setDeployDropdownOpen] = useState(false);
  const [isDeploymentsDropdownOpen, setDeploymentsDropdownOpen] = useState(false);
  const [isConfirmDeployModalOpen, setConfirmDeployModalOpen] = useState(false);

  const deploymentLoaderPipeline = useMemo(
    () =>
      new KnativeDeploymentLoaderPipeline({
        namespace: settings.openshift.config.namespace,
        openShiftService: settingsDispatch.openshift.service,
      }),
    [settings.openshift.config.namespace, settingsDispatch.openshift.service]
  );

  const onDisconnect = useCallback(() => {
    settingsDispatch.openshift.setStatus(OpenShiftInstanceStatus.DISCONNECTED);

    setDeployDropdownOpen(false);
    setDeploymentsDropdownOpen(false);
    setConfirmDeployModalOpen(false);
  }, [settingsDispatch.openshift]);

  const deploy = useCallback(
    async (args: InitDeployArgs) => {
      try {
        const strategy = await createDeploymentStrategy({ ...args });
        const pipeline = await strategy.buildPipeline();
        await pipeline.execute();
        return strategy.resourceName;
      } catch (e) {
        console.error(e);
      }
    },
    [createDeploymentStrategy]
  );

  const deploySwf = useCallback(
    async (args: InitSwfDeployArgs) => {
      const resourceName = await deploy({ ...args });

      if (resourceName && args.shouldUploadOpenApi) {
        const fetchOpenApiTask = window.setInterval(async () => {
          try {
            const openApiContent = await fetchOpenApiContent(resourceName);
            if (!openApiContent) {
              return;
            }

            await uploadArtifact({
              artifactId: `${args.targetFile.nameWithoutExtension} ${resourceName}`,
              content: openApiContent,
            });
          } catch (e) {
            console.error(e);
          }
          window.clearInterval(fetchOpenApiTask);
        }, FETCH_OPEN_API_POLLING_TIME);
      }

      return resourceName;
    },
    [deploy, fetchOpenApiContent, uploadArtifact]
  );

  const loadDeployments = useCallback(
    async () => deploymentLoaderPipeline?.execute() ?? [],
    [deploymentLoaderPipeline]
  );

  useEffect(() => {
    if (extendedServices.status !== ExtendedServicesStatus.RUNNING) {
      onDisconnect();
      return;
    }

    if (!isKubernetesConnectionValid(settings.openshift.config)) {
      return;
    }

    if (settings.openshift.status === OpenShiftInstanceStatus.DISCONNECTED) {
      settingsDispatch.openshift.service
        .isConnectionEstablished(settings.openshift.config)
        .then((configStatus: KubernetesConnectionStatus) => {
          const isConfigOk = configStatus === KubernetesConnectionStatus.CONNECTED;
          settingsDispatch.openshift.setStatus(
            isConfigOk ? OpenShiftInstanceStatus.CONNECTED : OpenShiftInstanceStatus.EXPIRED
          );
        })
        .catch((error) => console.error(error));
    }
  }, [
    deploymentLoaderPipeline,
    isDeploymentsDropdownOpen,
    extendedServices.status,
    onDisconnect,
    settings.openshift.config,
    settings.openshift.status,
    settingsDispatch.openshift,
  ]);

  const value = useMemo(
    () => ({
      isDeployDropdownOpen,
      setDeployDropdownOpen,
      isDeploymentsDropdownOpen,
      setDeploymentsDropdownOpen,
      isConfirmDeployModalOpen,
      setConfirmDeployModalOpen,
      deploy,
      deploySwf,
      loadDeployments,
    }),
    [isDeployDropdownOpen, isDeploymentsDropdownOpen, isConfirmDeployModalOpen, deploy, deploySwf, loadDeployments]
  );

  return <OpenShiftContext.Provider value={value}>{props.children}</OpenShiftContext.Provider>;
}
