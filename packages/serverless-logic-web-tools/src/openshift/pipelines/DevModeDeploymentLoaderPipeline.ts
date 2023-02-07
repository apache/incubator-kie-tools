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

import { ListDeployments } from "@kie-tools-core/openshift/dist/api/kubernetes/Deployment";
import { ListRoutes } from "@kie-tools-core/openshift/dist/api/kubernetes/Route";
import {
  DeploymentDescriptor,
  DeploymentGroupDescriptor,
  RouteGroupDescriptor,
} from "@kie-tools-core/openshift/dist/api/types";
import { ResourceFetcher } from "@kie-tools-core/openshift/dist/fetch/ResourceFetcher";
import { OpenShiftDeploymentState } from "@kie-tools-core/openshift/dist/service/types";
import { ResourceLabelNames } from "@kie-tools-core/openshift/dist/template/TemplateConstants";
import { WebToolsOpenShiftDeployedModel } from "../deploy/types";
import { buildEndpoints, DevModeEndpoints, fetchWithTimeout } from "../devMode/DevModeConstants";
import { OpenShiftPipeline, OpenShiftPipelineArgs } from "../OpenShiftPipeline";

interface ExtendedDeployment {
  endpoints: DevModeEndpoints;
  deployment: DeploymentDescriptor;
  state: OpenShiftDeploymentState;
}

interface DevModeDeploymentLoaderPipelineArgs {
  webToolsId: string;
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
              labelSelector: this.args.webToolsId,
            }),
          })
        )
      ).items.filter((d) => d.metadata.name === this.resolveResourceName());

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

      const extendedDeployments = devModeRoutes.map((route) => {
        const routeUrl = this.args.openShiftService.kubernetes.composeRouteUrl(route);
        const deployment = deployments.filter((d) => d.metadata.name === route.metadata.name)[0];
        const state = this.args.openShiftService.kubernetes.extractDeploymentState({ deployment });
        return { endpoints: buildEndpoints(routeUrl), state, deployment };
      });

      const healthCheckedExtendedDeployments = [];
      for (const ed of extendedDeployments) {
        if (ed.state !== OpenShiftDeploymentState.UP) {
          healthCheckedExtendedDeployments.push(ed);
        } else {
          try {
            const readyResponse = await fetchWithTimeout(ed.endpoints.health.ready, { timeout: 1000 });
            healthCheckedExtendedDeployments.push({
              ...ed,
              state: readyResponse.ok ? OpenShiftDeploymentState.UP : OpenShiftDeploymentState.IN_PROGRESS,
            });
          } catch (e) {
            console.debug(e);
            healthCheckedExtendedDeployments.push({
              ...ed,
              state: OpenShiftDeploymentState.IN_PROGRESS,
            });
          }
        }
      }

      return healthCheckedExtendedDeployments.map((ed: ExtendedDeployment) => ({
        resourceName: ed.deployment.metadata.name,
        uri: ed.deployment.metadata.annotations![ResourceLabelNames.URI],
        routeUrl: ed.endpoints.base,
        creationTimestamp: new Date(ed.deployment.metadata.creationTimestamp ?? Date.now()),
        state: ed.state,
        workspaceName: "Dev Mode",
        devMode: true,
      }));
    } catch (e) {
      throw new Error(`Failed to load dev mode deployments: ${e.message}`);
    }
  }

  // TODO CAPONETTO: maybe move this to a common place
  private resolveResourceName(): string {
    return `devmode-${this.args.webToolsId}`;
  }
}
