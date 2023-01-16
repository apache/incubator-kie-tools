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
  RouteDescriptor,
  RouteGroupDescriptor,
} from "@kie-tools-core/openshift/dist/api/types";
import { ResourceFetcher } from "@kie-tools-core/openshift/dist/fetch/ResourceFetcher";
import { OpenShiftDeploymentState } from "@kie-tools-core/openshift/dist/service/types";
import { ResourceLabelNames } from "@kie-tools-core/openshift/dist/template/TemplateConstants";
import { __RouterContext } from "react-router";
import { AppLabelNames, WebToolsOpenShiftDeployedModel } from "../deploy/types";
import { buildEndpoints, DevModeEndpoints } from "../devMode/DevModeConstants";
import { OpenShiftPipeline } from "../OpenShiftPipeline";

interface ExtendedDeployment {
  endpoints: DevModeEndpoints;
  deployment: DeploymentDescriptor;
  state: OpenShiftDeploymentState;
}

export class DevModeDeploymentLoaderPipeline extends OpenShiftPipeline<WebToolsOpenShiftDeployedModel[]> {
  public async execute(): Promise<WebToolsOpenShiftDeployedModel[]> {
    try {
      const deployments = await this.args.openShiftService.withFetch((fetcher: ResourceFetcher) =>
        fetcher.execute<DeploymentGroupDescriptor>({
          target: new ListDeployments({
            namespace: this.args.namespace,
            labelSelector: AppLabelNames.DEV_MODE,
          }),
        })
      );

      const devModeDeployments = deployments.items.filter(
        (d) => d.metadata.labels && !!d.metadata.labels[AppLabelNames.DEV_MODE]
      );

      if (deployments.items.length === 0) {
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
      ).items.filter((route) => devModeDeployments.some((d) => route.metadata.name === d.metadata.name));

      const extendedDeployments = await this.buildExtendedDeployments({
        routes: devModeRoutes,
        deployments: devModeDeployments,
      });

      return extendedDeployments.map((ed: ExtendedDeployment) => ({
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

  private async buildExtendedDeployments(args: {
    routes: RouteDescriptor[];
    deployments: DeploymentDescriptor[];
  }): Promise<ExtendedDeployment[]> {
    return Promise.all(
      args.routes
        .map((route) => {
          const routeUrl = this.args.openShiftService.kubernetes.composeRouteUrl(route);
          return {
            resourceName: route.metadata.name,
            endpoints: buildEndpoints(routeUrl),
          };
        })
        .map(async (mapArgs: { resourceName: string; endpoints: DevModeEndpoints }) => {
          const deployment = args.deployments.filter((d) => d.metadata.name === mapArgs.resourceName)[0];
          const state = this.args.openShiftService.kubernetes.extractDeploymentState({ deployment });
          const extended = { endpoints: mapArgs.endpoints, state, deployment };
          if (state !== OpenShiftDeploymentState.UP) {
            return extended;
          }
          try {
            const readyResponse = await fetch(mapArgs.endpoints.health.ready);
            if (readyResponse.ok) {
              return { ...extended, state: OpenShiftDeploymentState.UP };
            }
          } catch (e) {
            console.debug(e);
          }

          return { ...extended, state: OpenShiftDeploymentState.IN_PROGRESS };
        })
    );
  }
}
