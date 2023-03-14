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

import { KubernetesApiVersions } from "@kie-tools-core/openshift/dist/api/ApiConstants";
import {
  CreateDeployment,
  ListDeployments,
  UpdateDeployment,
} from "@kie-tools-core/openshift/dist/api/kubernetes/Deployment";
import { CreateRoute, DeleteRoute, GetRoute } from "@kie-tools-core/openshift/dist/api/kubernetes/Route";
import { CreateService, DeleteService } from "@kie-tools-core/openshift/dist/api/kubernetes/Service";
import {
  DeploymentDescriptor,
  DeploymentGroupDescriptor,
  RouteDescriptor,
} from "@kie-tools-core/openshift/dist/api/types";
import { ResourceFetcher } from "@kie-tools-core/openshift/dist/fetch/ResourceFetcher";
import { OpenShiftDeploymentState } from "@kie-tools-core/openshift/dist/service/types";
import { commonLabels, runtimeLabels } from "@kie-tools-core/openshift/dist/template/TemplateConstants";
import { resolveDevModeResourceName } from "../devMode/DevModeContext";
import { RESOURCE_OWNER } from "../OpenShiftConstants";
import { OpenShiftPipeline, OpenShiftPipelineArgs } from "../OpenShiftPipeline";

interface SpinUpDevModePipelineArgs {
  webToolsId: string;
}

export class SpinUpDevModePipeline extends OpenShiftPipeline<string | undefined> {
  constructor(protected readonly args: OpenShiftPipelineArgs & SpinUpDevModePipelineArgs) {
    super(args);
  }

  public async execute(): Promise<string | undefined> {
    const deployments = (
      await this.args.openShiftService.withFetch((fetcher: ResourceFetcher) =>
        fetcher.execute<DeploymentGroupDescriptor>({
          target: new ListDeployments({
            namespace: this.args.namespace,
            labelSelector: this.args.webToolsId,
          }),
        })
      )
    ).items
      .filter((d) => d.metadata.name === resolveDevModeResourceName(this.args.webToolsId))
      .sort(
        (a, b) =>
          new Date(b.metadata.creationTimestamp ?? 0).getTime() - new Date(a.metadata.creationTimestamp ?? 0).getTime()
      );

    if (deployments.length === 0) {
      return this.createDevModeDeployment();
    }

    const latestDeployment = deployments[0];

    const latestDeploymentStatus = this.args.openShiftService.kubernetes.extractDeploymentState({
      deployment: latestDeployment,
    });

    if (latestDeploymentStatus === OpenShiftDeploymentState.ERROR) {
      throw new Error("Invalid state for the dev mode deployment");
    }

    const route = await this.args.openShiftService.withFetch<RouteDescriptor>((fetcher: ResourceFetcher) =>
      fetcher.execute({
        target: new GetRoute({
          namespace: this.args.namespace,
          resourceName: latestDeployment.metadata.name,
        }),
      })
    );

    const routeUrl = this.args.openShiftService.kubernetes.composeRouteUrl(route);

    if (latestDeploymentStatus !== OpenShiftDeploymentState.UP) {
      const currentReplicas = latestDeployment.spec.replicas;
      const updatedDeployment: DeploymentDescriptor = {
        ...latestDeployment,
        spec: {
          ...latestDeployment.spec,
          replicas: currentReplicas + 1,
        },
      };

      await this.args.openShiftService.withFetch((fetcher: ResourceFetcher) =>
        fetcher.execute({
          target: new UpdateDeployment({
            namespace: this.args.namespace,
            resourceName: latestDeployment.metadata.name,
            descriptor: updatedDeployment,
          }),
        })
      );
    }

    return routeUrl;
  }

  private async createDevModeDeployment(): Promise<string | undefined> {
    const resourceArgs = {
      namespace: this.args.namespace,
      resourceName: resolveDevModeResourceName(this.args.webToolsId),
      createdBy: RESOURCE_OWNER,
    };

    const rollbacks = [new DeleteRoute(resourceArgs), new DeleteService(resourceArgs)];
    let rollbacksCount = rollbacks.length;

    try {
      await this.args.openShiftService.withFetch((fetcher: ResourceFetcher) =>
        fetcher.execute({ target: new CreateService(resourceArgs) })
      );

      const route = await this.args.openShiftService.withFetch<RouteDescriptor>((fetcher: ResourceFetcher) =>
        fetcher.execute({
          target: new CreateRoute(resourceArgs),
          rollbacks: rollbacks.slice(--rollbacksCount),
        })
      );

      const routeUrl = this.args.openShiftService.kubernetes.composeRouteUrl(route);

      const appLabels = {
        [this.args.webToolsId]: "true",
      };

      const deploymentDescriptor: DeploymentDescriptor = {
        apiVersion: KubernetesApiVersions.DEPLOYMENT,
        kind: "Deployment",
        metadata: {
          name: resourceArgs.resourceName,
          namespace: resourceArgs.namespace,
          labels: {
            ...commonLabels({ ...resourceArgs }),
            ...runtimeLabels(),
            ...appLabels,
          },
        },
        spec: {
          replicas: 1,
          selector: {
            matchLabels: {
              app: resourceArgs.resourceName,
            },
          },
          template: {
            metadata: {
              labels: {
                app: resourceArgs.resourceName,
                deploymentconfig: resourceArgs.resourceName,
              },
            },
            spec: {
              containers: [
                {
                  name: resourceArgs.resourceName,
                  image: process.env.WEBPACK_REPLACE__devModeImageFullUrl!,
                  ports: [
                    {
                      containerPort: 8080,
                      protocol: "TCP",
                    },
                  ],
                  env: [{ name: "DATA_INDEX_URL", value: routeUrl }],
                },
              ],
            },
          },
        },
      };

      await this.args.openShiftService.withFetch((fetcher: ResourceFetcher) =>
        fetcher.execute({
          target: new CreateDeployment({
            ...resourceArgs,
            kind: "provided",
            descriptor: deploymentDescriptor,
          }),
          rollbacks: rollbacks.slice(--rollbacksCount),
        })
      );

      return this.args.openShiftService.kubernetes.composeRouteUrl(route);
    } catch (e) {
      console.error(e);
    }
  }
}
