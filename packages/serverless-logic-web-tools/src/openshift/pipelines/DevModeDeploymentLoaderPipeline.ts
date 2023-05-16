/*
 * Copyright 2023 Red Hat, Inc. and/or its affiliates.
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

import { ResourceFetcher } from "@kie-tools-core/kubernetes-bridge/dist/fetch";
import {
  DeploymentDescriptor,
  DeploymentGroupDescriptor,
  DeploymentState,
  ListDeployments,
  ListRoutes,
  RouteGroupDescriptor,
} from "@kie-tools-core/kubernetes-bridge/dist/resources";
import { fetchWithTimeout } from "../../fetch";
import { OpenShiftPipeline, OpenShiftPipelineArgs } from "../OpenShiftPipeline";
import { WebToolsOpenShiftDeployedModel } from "../deploy/types";
import { DEV_MODE_ID_KUBERNETES_LABEL, DevModeEndpoints, buildEndpoints } from "../swfDevMode/DevModeConstants";
import { APP_VERSION_KUBERNETES_LABEL } from "../OpenShiftConstants";

interface ExtendedDeployment {
  endpoints: DevModeEndpoints;
  deployment: DeploymentDescriptor;
  state: DeploymentState;
}

interface DevModeDeploymentLoaderPipelineArgs {
  devModeId: string;
  version: string;
}

export class DevModeDeploymentLoaderPipeline extends OpenShiftPipeline<WebToolsOpenShiftDeployedModel[]> {
  constructor(protected readonly args: OpenShiftPipelineArgs & DevModeDeploymentLoaderPipelineArgs) {
    super(args);
  }

  public async execute(): Promise<WebToolsOpenShiftDeployedModel[]> {
    try {
      const deployments = (
        await this.args.openShiftService.withFetch((fetcher: ResourceFetcher) =>
          fetcher.execute<DeploymentGroupDescriptor>({
            target: new ListDeployments({
              namespace: this.args.namespace,
              labelSelector: DEV_MODE_ID_KUBERNETES_LABEL,
            }),
          })
        )
      ).items.filter(
        (d) =>
          d.metadata.labels &&
          d.metadata.labels[DEV_MODE_ID_KUBERNETES_LABEL] === this.args.devModeId &&
          d.metadata.labels[APP_VERSION_KUBERNETES_LABEL] === this.args.version
      );

      if (deployments.length === 0) {
        return [];
      }

      const devModeRoutes = (
        await this.args.openShiftService.withFetch((fetcher: ResourceFetcher) =>
          fetcher.execute<RouteGroupDescriptor>({
            target: new ListRoutes({
              namespace: this.args.namespace,
            }),
          })
        )
      ).items.filter((route) => deployments.some((d) => route.metadata.name === d.metadata.name));

      if (devModeRoutes.length === 0) {
        return [];
      }

      const extendedDeployments = devModeRoutes.map((route) => {
        const routeUrl = this.args.openShiftService.composeDeploymentUrlFromRoute(route);
        const deployment = deployments.filter((d) => d.metadata.name === route.metadata.name)[0];
        const state = this.args.openShiftService.kubernetes.extractDeploymentState({ deployment });
        return { endpoints: buildEndpoints(routeUrl), state, deployment };
      });

      const healthCheckedExtendedDeployments = [];
      for (const ed of extendedDeployments) {
        if (ed.state !== DeploymentState.UP) {
          healthCheckedExtendedDeployments.push(ed);
        } else {
          try {
            const readyResponse = await fetchWithTimeout(ed.endpoints.health.ready, { timeout: 1000 });
            healthCheckedExtendedDeployments.push({
              ...ed,
              state: readyResponse.ok ? DeploymentState.UP : DeploymentState.IN_PROGRESS,
            });
          } catch (e) {
            console.debug(e);
            healthCheckedExtendedDeployments.push({
              ...ed,
              state: DeploymentState.IN_PROGRESS,
            });
          }
        }
      }

      return healthCheckedExtendedDeployments.map((ed: ExtendedDeployment) => ({
        resourceName: ed.deployment.metadata.name,
        routeUrl: ed.endpoints.base,
        creationTimestamp: new Date(ed.deployment.metadata.creationTimestamp ?? Date.now()),
        state: ed.state,
        devMode: true,
      }));
    } catch (e) {
      throw new Error(`Failed to load dev mode deployments: ${e.message}`);
    }
  }
}
