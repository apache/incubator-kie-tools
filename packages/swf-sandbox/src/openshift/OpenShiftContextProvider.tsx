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
import { useCallback, useMemo } from "react";
import { isOpenShiftConfigValid } from "../settings/openshift/OpenShiftSettingsConfig";
import { isServiceAccountConfigValid } from "../settings/serviceAccount/ServiceAccountConfig";
import { isServiceRegistryConfigValid } from "../settings/serviceRegistry/ServiceRegistryConfig";
import { useSettings } from "../settings/SettingsContext";
import { DeploymentWorkflow, OpenShiftContext } from "./OpenShiftContext";
import { OpenShiftService } from "./OpenShiftService";

interface Props {
  children: React.ReactNode;
}

const DEFAULT_GROUP_ID = "org.kie";

export function OpenShiftContextProvider(props: Props) {
  const settings = useSettings();
  const service = useMemo(() => new OpenShiftService(), []);

  const deploy = useCallback(
    async (workflow: DeploymentWorkflow) => {
      if (!isOpenShiftConfigValid(settings.openshift.config)) {
        throw new Error("Invalid OpenShift config");
      }

      return service.deploy({
        workflow: workflow,
        openShiftConfig: settings.openshift.config,
        kafkaConfig: settings.apacheKafka.config,
        serviceAccountConfig: settings.serviceAccount.config,
      });
    },
    [service, settings.apacheKafka.config, settings.openshift.config, settings.serviceAccount.config]
  );

  const fetchOpenApiFile = useCallback(
    async (resourceName: string) => {
      if (!isOpenShiftConfigValid(settings.openshift.config)) {
        throw new Error("Invalid OpenShift config");
      }

      try {
        const routeUrl = await service.getDeploymentRoute({ config: settings.openshift.config, resourceName });
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
    [service, settings.openshift.config]
  );

  const uploadArtifactToServiceRegistry = useCallback(
    async (artifactId: string, content: string) => {
      if (!isServiceAccountConfigValid(settings.serviceAccount.config)) {
        throw new Error("Invalid service account config");
      }

      if (!isServiceRegistryConfigValid(settings.serviceRegistry.config)) {
        throw new Error("Invalid service registry config");
      }

      await service.uploadOpenApiToServiceRegistry({
        proxyUrl: settings.openshift.config.proxy,
        groupId: DEFAULT_GROUP_ID,
        artifactId: artifactId,
        serviceAccountConfig: settings.serviceAccount.config,
        serviceRegistryConfig: settings.serviceRegistry.config,
        openApiContent: content,
      });
    },
    [service, settings.serviceAccount.config, settings.serviceRegistry.config, settings.openshift.config]
  );

  const value = useMemo(
    () => ({
      deploy,
      uploadArtifactToServiceRegistry,
      fetchOpenApiFile,
    }),
    [deploy, uploadArtifactToServiceRegistry, fetchOpenApiFile]
  );

  return <OpenShiftContext.Provider value={value}>{props.children}</OpenShiftContext.Provider>;
}
