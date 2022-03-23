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
import { DeployArgs, DeploymentWorkflow, OpenShiftContext } from "./OpenShiftContext";
import { OpenShiftService } from "./OpenShiftService";
import { OpenShiftSettingsConfig } from "../settings/openshift/OpenShiftSettingsConfig";

interface Props {
  children: React.ReactNode;
}

export function OpenShiftProvider(props: Props) {
  const service = useMemo(() => new OpenShiftService(), []);

  const deploy = useCallback(
    async (args: DeployArgs) => {
      return await service.deploy(args);
    },
    [service]
  );

  const fetchWorkflowRoute = useCallback(
    async (config: OpenShiftSettingsConfig, resourceName: string) => {
      try {
        return await service.getDeploymentRoute({ config, resourceName });
      } catch (error) {
        console.error(error);
        return;
      }
    },
    [service]
  );

  const fetchOpenApiFile = useCallback(
    async (config: OpenShiftSettingsConfig, resourceName: string) => {
      try {
        const routeUrl = await fetchWorkflowRoute(config, resourceName);
        if (!routeUrl) {
          return;
        }

        const response = await fetch(`${routeUrl}/q/openapi`);
        if (!response.ok) {
          return;
        }

        return await response.text();
      } catch (error) {
        console.error(error);
        return;
      }
    },
    [fetchWorkflowRoute]
  );

  const fetchWorkflowName = useCallback(
    async (config: OpenShiftSettingsConfig, resourceName: string) => {
      try {
        return await service.getWorkflowFileName({ config, resourceName });
      } catch (error) {
        console.error(error);
        return;
      }
    },
    [service]
  );

  const fetchWorkflow = useCallback(
    async (config: OpenShiftSettingsConfig, resourceName: string) => {
      try {
        const workflowFileName = await fetchWorkflowName(config, resourceName);
        if (!workflowFileName) {
          return;
        }

        const routeUrl = await fetchWorkflowRoute(config, resourceName);
        if (!routeUrl) {
          return;
        }

        const response = await fetch(`${routeUrl}/${workflowFileName}`);
        if (!response.ok) {
          return;
        }

        const content = await response.text();

        return {
          name: workflowFileName,
          content: content,
        };
      } catch (error) {
        console.error(error);
        return;
      }
    },
    [fetchWorkflowName, fetchWorkflowRoute]
  );

  const fetchWorkflows = useCallback(
    async (config: OpenShiftSettingsConfig) => {
      const workflows: DeploymentWorkflow[] = [];
      try {
        const resourceRouteMap = await service.getResourceRouteMap(config);

        for (const [resourceName] of resourceRouteMap) {
          try {
            const workflow = await fetchWorkflow(config, resourceName);
            if (workflow) {
              workflows.push(workflow);
            }
          } catch {
            continue;
          }
        }
      } catch (error) {
        console.error(error);
      }
      return workflows;
    },
    [fetchWorkflow, service]
  );

  const listServices = useCallback(
    async (config: OpenShiftSettingsConfig) => {
      try {
        return await service.listServices(config);
      } catch (error) {
        console.error(error);
      }
    },
    [service]
  );

  const listDeployments = useCallback(
    async (config: OpenShiftSettingsConfig) => {
      try {
        return await service.listDeployments(config);
      } catch (error) {
        console.error(error);
      }
    },
    [service]
  );

  const value = useMemo(
    () => ({
      deploy,
      fetchOpenApiFile,
      fetchWorkflow,
      fetchWorkflows,
      fetchWorkflowName,
      fetchWorkflowRoute,
      listServices,
      listDeployments,
    }),
    [
      deploy,
      fetchOpenApiFile,
      fetchWorkflow,
      fetchWorkflows,
      fetchWorkflowName,
      fetchWorkflowRoute,
      listServices,
      listDeployments,
    ]
  );

  return <OpenShiftContext.Provider value={value}>{props.children}</OpenShiftContext.Provider>;
}
