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
import { useKieSandboxExtendedServices } from "../kieSandboxExtendedServices/KieSandboxExtendedServicesContext";
import { KieSandboxExtendedServicesStatus } from "../kieSandboxExtendedServices/KieSandboxExtendedServicesStatus";
import { useSettings, useSettingsDispatch } from "../settings/SettingsContext";
import { InitDeployArgs, InitSwfDeployArgs, WebToolsOpenShiftDeployedModel } from "./deploy/types";
import { useDeploymentStrategy } from "./hooks/useDeploymentStrategy";
import { useOpenApi } from "./hooks/useOpenApi";
import { useRemoteServiceRegistry } from "./hooks/useRemoteServiceRegistry";
import { OpenShiftContext } from "./OpenShiftContext";
import { OpenShiftInstanceStatus } from "./OpenShiftInstanceStatus";
import { KnativeDeploymentLoaderPipeline } from "./pipelines/KnativeDeploymentLoaderPipeline";
import { DevModeDeploymentLoaderPipeline } from "./pipelines/DevModeDeploymentLoaderPipeline";
import { useEnv } from "../env/EnvContext";
import { resolveWebToolsId, useDevMode } from "./devMode/DevModeContext";
import { AppDistributionMode } from "../AppConstants";
import {
  KubernetesConnectionStatus,
  isKubernetesConnectionValid,
} from "@kie-tools-core/kubernetes-bridge/dist/service";

interface Props {
  children: React.ReactNode;
}

const LOAD_DEPLOYMENTS_POLLING_TIME = 2500;
const FETCH_OPEN_API_POLLING_TIME = 5000;

export function OpenShiftContextProvider(props: Props) {
  const { env } = useEnv();
  const settings = useSettings();
  const settingsDispatch = useSettingsDispatch();
  const kieSandboxExtendedServices = useKieSandboxExtendedServices();
  const devMode = useDevMode();
  const { createDeploymentStrategy } = useDeploymentStrategy();
  const { fetchOpenApiContent } = useOpenApi();
  const { uploadArtifact } = useRemoteServiceRegistry();

  const [deployments, setDeployments] = useState([] as WebToolsOpenShiftDeployedModel[]);
  const [isDeployDropdownOpen, setDeployDropdownOpen] = useState(false);
  const [isDeploymentsDropdownOpen, setDeploymentsDropdownOpen] = useState(false);
  const [isConfirmDeployModalOpen, setConfirmDeployModalOpen] = useState(false);

  const deploymentLoaderPipeline = useMemo(() => {
    if (env.FEATURE_FLAGS.MODE !== AppDistributionMode.COMMUNITY) {
      return;
    }
    return new KnativeDeploymentLoaderPipeline({
      namespace: settings.openshift.config.namespace,
      openShiftService: settingsDispatch.openshift.service,
    });
  }, [env.FEATURE_FLAGS.MODE, settings.openshift.config.namespace, settingsDispatch.openshift.service]);

  const devModeDeploymentLoaderPipeline = useMemo(() => {
    if (!devMode.isEnabled) {
      return;
    }
    return new DevModeDeploymentLoaderPipeline({
      webToolsId: resolveWebToolsId(),
      namespace: settings.openshift.config.namespace,
      openShiftService: settingsDispatch.openshift.service,
    });
  }, [devMode.isEnabled, settings.openshift.config.namespace, settingsDispatch.openshift.service]);

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

    if (!isKubernetesConnectionValid(settings.openshift.config)) {
      setDeployments([]);
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

          if (isConfigOk) {
            return Promise.all([
              deploymentLoaderPipeline?.execute() ?? Promise.resolve([]),
              devModeDeploymentLoaderPipeline?.execute() ?? Promise.resolve([]),
            ]).then((res) => res.flat());
          }
          return [];
        })
        .then((ds) => setDeployments(ds.flat()))
        .catch((error) => console.error(error));
      return;
    }

    if (settings.openshift.status === OpenShiftInstanceStatus.CONNECTED && isDeploymentsDropdownOpen) {
      let loadDeploymentsTask: number | undefined;

      (function callImmediatelyAndPoll() {
        try {
          Promise.all([
            deploymentLoaderPipeline?.execute() ?? Promise.resolve([]),
            devModeDeploymentLoaderPipeline?.execute() ?? Promise.resolve([]),
          ])
            .then((res) => res.flat())
            .then((ds) => setDeployments(ds));

          loadDeploymentsTask = window.setTimeout(callImmediatelyAndPoll, LOAD_DEPLOYMENTS_POLLING_TIME);
        } catch (error) {
          onDisconnect();
          window.clearTimeout(loadDeploymentsTask);
          console.error(error);
        }
      })();

      return () => window.clearTimeout(loadDeploymentsTask);
    }
  }, [
    deploymentLoaderPipeline,
    devModeDeploymentLoaderPipeline,
    isDeploymentsDropdownOpen,
    kieSandboxExtendedServices.status,
    onDisconnect,
    settings.openshift.config,
    settings.openshift.status,
    settingsDispatch.openshift,
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
