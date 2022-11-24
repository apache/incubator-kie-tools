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
import { isOpenShiftConnectionValid } from "@kie-tools-core/openshift/dist/service/OpenShiftConnection";
import { useCallback, useEffect, useMemo, useState } from "react";
import { useKieSandboxExtendedServices } from "../kieSandboxExtendedServices/KieSandboxExtendedServicesContext";
import { KieSandboxExtendedServicesStatus } from "../kieSandboxExtendedServices/KieSandboxExtendedServicesStatus";
import { useSettings, useSettingsDispatch } from "../settings/SettingsContext";
import { InitDeployArgs, InitSwfDeployArgs, WebToolsOpenShiftDeployedModel } from "./deploy/types";
import { useDeploymentStrategy } from "./hooks/useDeploymentStrategy";
import { useOpenApi } from "./hooks/useOpenApi";
import { useRemoteServiceRegistry } from "./hooks/useRemoteServiceRegistry";
import { OpenShiftContext } from "./OpenShiftContext";
import { OpenShiftInstanceStatus } from "./OpenShiftInstanceStatus";
import { KNativeDeploymentLoaderPipeline } from "./pipelines/KNativeDeploymentLoaderPipeline";

interface Props {
  children: React.ReactNode;
}

const LOAD_DEPLOYMENTS_POLLING_TIME = 2500;
const FETCH_OPEN_API_POLLING_TIME = 5000;

export function OpenShiftContextProvider(props: Props) {
  const settings = useSettings();
  const settingsDispatch = useSettingsDispatch();
  const kieSandboxExtendedServices = useKieSandboxExtendedServices();
  const { createDeploymentStrategy } = useDeploymentStrategy();
  const { fetchOpenApiContent } = useOpenApi();
  const { uploadArtifact } = useRemoteServiceRegistry();

  const [deployments, setDeployments] = useState([] as WebToolsOpenShiftDeployedModel[]);
  const [isDeployDropdownOpen, setDeployDropdownOpen] = useState(false);
  const [isDeploymentsDropdownOpen, setDeploymentsDropdownOpen] = useState(false);
  const [isConfirmDeployModalOpen, setConfirmDeployModalOpen] = useState(false);

  const deploymentLoaderPipeline = useMemo(
    () =>
      new KNativeDeploymentLoaderPipeline({
        namespace: settings.openshift.config.namespace,
        openShiftService: settingsDispatch.openshift.service,
      }),
    [settings.openshift.config.namespace, settingsDispatch.openshift.service]
  );

  const onDisconnect = useCallback(() => {
    settingsDispatch.openshift.setStatus(OpenShiftInstanceStatus.DISCONNECTED);
    setDeployments([]);

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

  useEffect(() => {
    if (kieSandboxExtendedServices.status !== KieSandboxExtendedServicesStatus.RUNNING) {
      onDisconnect();
      return;
    }

    if (!isOpenShiftConnectionValid(settings.openshift.config)) {
      setDeployments([]);
      return;
    }

    if (settings.openshift.status === OpenShiftInstanceStatus.DISCONNECTED) {
      settingsDispatch.openshift.service
        .isConnectionEstablished(settings.openshift.config)
        .then((isConfigOk: boolean) => {
          settingsDispatch.openshift.setStatus(
            isConfigOk ? OpenShiftInstanceStatus.CONNECTED : OpenShiftInstanceStatus.EXPIRED
          );
          return isConfigOk ? deploymentLoaderPipeline.execute() : [];
        })
        .then((ds) => setDeployments(ds))
        .catch((error) => console.error(error));
      return;
    }

    if (settings.openshift.status === OpenShiftInstanceStatus.CONNECTED && isDeploymentsDropdownOpen) {
      const loadDeploymentsTask = window.setInterval(() => {
        deploymentLoaderPipeline
          .execute()
          .then((ds) => setDeployments(ds))
          .catch((error) => {
            onDisconnect();
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
    deploymentLoaderPipeline,
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
      deploySwf,
    }),
    [deployments, isDeployDropdownOpen, isDeploymentsDropdownOpen, isConfirmDeployModalOpen, deploy, deploySwf]
  );

  return <OpenShiftContext.Provider value={value}>{props.children}</OpenShiftContext.Provider>;
}
