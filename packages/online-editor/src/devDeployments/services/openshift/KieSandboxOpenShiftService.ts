/*
 * Copyright 2022 Red Hat, Inc. and/or its affiliates.
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

import {
  CreateRoute,
  DeleteRoute,
  ListRoutes,
  RouteDescriptor,
  RouteGroupDescriptor,
  ResourceDescriptor,
  ResourceLabelNames,
} from "@kie-tools-core/kubernetes-bridge/dist/resources";
import { ResourceFetcher, ResourceFetch } from "@kie-tools-core/kubernetes-bridge/dist/fetch";
import { OpenShiftService, KubernetesServiceArgs } from "@kie-tools-core/kubernetes-bridge/dist/service";
import { KieSandboxBaseKubernetesService, RESOURCE_OWNER, ResourceArgs } from "../KieSandboxBaseKubernetesService";

export class KieSandboxOpenShiftService extends KieSandboxBaseKubernetesService {
  service: OpenShiftService;
  constructor(readonly args: KubernetesServiceArgs) {
    super(args);
    this.service = new OpenShiftService(args);
  }

  public async listRoutes(): Promise<RouteDescriptor[]> {
    const routes = (
      await this.service.withFetch((fetcher: ResourceFetcher) =>
        fetcher.execute<RouteGroupDescriptor>({
          target: new ListRoutes({
            namespace: this.args.connection.namespace,
          }),
        })
      )
    ).items.filter(
      (route) => route.metadata?.labels && route.metadata.labels[ResourceLabelNames.CREATED_BY] === RESOURCE_OWNER
    );

    return routes;
  }

  public getRouteUrl(resource: RouteDescriptor): string {
    return this.service.composeDeploymentUrlFromRoute(resource);
  }

  public getRouteRollback(resourceArgs: ResourceArgs): ResourceFetch {
    return new DeleteRoute(resourceArgs);
  }

  public async createRoute(
    resourceArgs: ResourceArgs,
    getUpdatedRollbacks: () => ResourceFetch[]
  ): Promise<ResourceDescriptor> {
    const route = await this.service.withFetch((fetcher: ResourceFetcher) =>
      fetcher.execute<RouteDescriptor>({
        target: new CreateRoute(resourceArgs),
        rollbacks: getUpdatedRollbacks(),
      })
    );
    return route;
  }

  public async deleteRoute(resourceName: string): Promise<void> {
    await this.service.withFetch((fetcher: ResourceFetcher) =>
      fetcher.execute({
        target: new DeleteRoute({
          resourceName,
          namespace: this.args.connection.namespace,
        }),
      })
    );
  }
}
