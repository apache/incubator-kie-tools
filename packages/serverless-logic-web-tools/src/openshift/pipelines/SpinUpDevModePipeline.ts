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
  CreateDeployment,
  CreateRoute,
  CreateService,
  DeleteRoute,
  DeleteService,
  DeploymentDescriptor,
  DeploymentGroupDescriptor,
  DeploymentState,
  GetRoute,
  ListDeployments,
  ResourceDataSource,
  RouteDescriptor,
  UpdateDeployment,
  commonLabels,
  runtimeLabels,
} from "@kie-tools-core/kubernetes-bridge/dist/resources";
import { Deployment } from "kubernetes-models/apps/v1";
import { DeployConstants } from "../DeployConstants";
import { APP_VERSION_KUBERNETES_LABEL, RESOURCE_OWNER } from "../OpenShiftConstants";
import { OpenShiftPipeline, OpenShiftPipelineArgs } from "../OpenShiftPipeline";
import { SwfDevMode } from "../deploy/BaseContainerImages";
import { DEV_MODE_ID_KUBERNETES_LABEL, resolveDevModeResourceName } from "../swfDevMode/DevModeConstants";

interface SpinUpDevModePipelineArgs {
  devModeId: string;
  version: string;
}

type SpinUpDevModePipelineResponse =
  | {
      isCompleted: true;
      isNew: boolean;
      routeUrl: string;
    }
  | {
      isCompleted: false;
      reason: string;
    };

export class SpinUpDevModePipeline extends OpenShiftPipeline<SpinUpDevModePipelineResponse> {
  constructor(protected readonly args: OpenShiftPipelineArgs & SpinUpDevModePipelineArgs) {
    super(args);
  }

  public async execute(): Promise<SpinUpDevModePipelineResponse> {
    const deployments = (
      await this.args.openShiftService.withFetch((fetcher: ResourceFetcher) =>
        fetcher.execute<DeploymentGroupDescriptor>({
          target: new ListDeployments({
            namespace: this.args.namespace,
            labelSelector: DEV_MODE_ID_KUBERNETES_LABEL,
          }),
        })
      )
    ).items
      .filter(
        (d) =>
          d.metadata.labels &&
          d.metadata.labels[DEV_MODE_ID_KUBERNETES_LABEL] === this.args.devModeId &&
          d.metadata.labels[APP_VERSION_KUBERNETES_LABEL] === this.args.version
      )
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

    if (latestDeploymentStatus === DeploymentState.ERROR || !latestDeployment.spec) {
      return {
        isCompleted: false,
        reason: "Invalid state for the dev mode deployment",
      };
    }

    const route = await this.args.openShiftService.withFetch<RouteDescriptor>((fetcher: ResourceFetcher) =>
      fetcher.execute({
        target: new GetRoute({
          namespace: this.args.namespace,
          resourceName: latestDeployment.metadata.name,
        }),
      })
    );

    const routeUrl = this.args.openShiftService.composeDeploymentUrlFromRoute(route);

    if ([DeploymentState.DOWN, DeploymentState.ERROR].includes(latestDeploymentStatus)) {
      const currentReplicas = latestDeployment.spec.replicas || 0;
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
            resourceName: updatedDeployment.metadata.name,
            descriptor: updatedDeployment,
          }),
        })
      );
    }

    return {
      isCompleted: true,
      isNew: false,
      routeUrl,
    };
  }

  private async createDevModeDeployment(): Promise<SpinUpDevModePipelineResponse> {
    const resourceArgs = {
      namespace: this.args.namespace,
      resourceName: resolveDevModeResourceName({ appVersion: this.args.version, devModeId: this.args.devModeId }),
      createdBy: RESOURCE_OWNER,
    };

    const rollbacks = [new DeleteRoute(resourceArgs), new DeleteService(resourceArgs)];
    let rollbacksCount = rollbacks.length;

    try {
      await this.args.openShiftService.withFetch((fetcher: ResourceFetcher) =>
        fetcher.execute({
          target: new CreateService({ ...resourceArgs, resourceDataSource: ResourceDataSource.TEMPLATE }),
        })
      );

      const route = await this.args.openShiftService.withFetch<RouteDescriptor>((fetcher: ResourceFetcher) =>
        fetcher.execute({
          target: new CreateRoute({ ...resourceArgs, resourceDataSource: ResourceDataSource.TEMPLATE }),
          rollbacks: rollbacks.slice(--rollbacksCount),
        })
      );

      const routeUrl = this.args.openShiftService.composeDeploymentUrlFromRoute(route);

      const appLabels = {
        [DEV_MODE_ID_KUBERNETES_LABEL]: this.args.devModeId,
        [APP_VERSION_KUBERNETES_LABEL]: this.args.version,
      };

      const deploymentDescriptor = new Deployment({
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
                  image: SwfDevMode.CONTAINER_IMAGE,
                  ports: [
                    {
                      containerPort: 8080,
                      protocol: "TCP",
                    },
                  ],
                  env: [
                    { name: DeployConstants.KOGITO_DATA_INDEX_URL_KEY, value: routeUrl },
                    { name: DeployConstants.KOGITO_DEV_UI_URL_KEY, value: routeUrl },
                  ],
                },
              ],
            },
          },
        },
      }).toJSON() as DeploymentDescriptor;

      await this.args.openShiftService.withFetch((fetcher: ResourceFetcher) =>
        fetcher.execute({
          target: new CreateDeployment({
            ...resourceArgs,
            descriptor: deploymentDescriptor,
            resourceDataSource: ResourceDataSource.PROVIDED,
          }),
          rollbacks: rollbacks.slice(--rollbacksCount),
        })
      );

      return {
        isCompleted: true,
        isNew: true,
        routeUrl: this.args.openShiftService.composeDeploymentUrlFromRoute(route),
      };
    } catch (e) {
      return {
        isCompleted: false,
        reason: e,
      };
    }
  }
}
