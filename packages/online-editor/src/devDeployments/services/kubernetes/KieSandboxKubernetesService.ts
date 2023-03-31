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

import {
  CreateIngress,
  DeleteIngress,
  ListIngresses,
  IngressDescriptor,
  IngressGroupDescriptor,
  CreateDeployment,
  DeploymentDescriptor,
  ResourceDescriptor,
  ResourceLabelNames,
} from "@kie-tools-core/kubernetes-bridge/dist/resources";
import { ResourceFetcher, ResourceFetch } from "@kie-tools-core/kubernetes-bridge/dist/fetch";
import { KubernetesService, KubernetesServiceArgs } from "@kie-tools-core/kubernetes-bridge/dist/service";
import {
  DeployArgs,
  KieSandboxBaseKubernetesService,
  RESOURCE_OWNER,
  ResourceArgs,
} from "../KieSandboxBaseKubernetesService";

export class KieSandboxKubernetesService extends KieSandboxBaseKubernetesService {
  service: KubernetesService;
  constructor(readonly args: KubernetesServiceArgs) {
    super(args);
    this.service = new KubernetesService(args);
  }

  public async listRoutes(): Promise<IngressDescriptor[]> {
    const ingresses = (
      await this.service.withFetch((fetcher: ResourceFetcher) =>
        fetcher.execute<IngressGroupDescriptor>({
          target: new ListIngresses({
            namespace: this.args.connection.namespace,
          }),
        })
      )
    ).items.filter(
      (route) => route.metadata?.labels && route.metadata.labels[ResourceLabelNames.CREATED_BY] === RESOURCE_OWNER
    );

    return ingresses;
  }

  public getRouteUrl(resource: IngressDescriptor): string {
    return this.service.composeDeploymentUrlFromIngress(resource);
  }

  public getRouteRollback(resourceArgs: ResourceArgs): ResourceFetch {
    return new DeleteIngress(resourceArgs);
  }

  public async createRoute(
    resourceArgs: ResourceArgs,
    getUpdatedRollbacks: () => ResourceFetch[]
  ): Promise<ResourceDescriptor> {
    const route = await this.service.withFetch((fetcher: ResourceFetcher) =>
      fetcher.execute<IngressDescriptor>({
        target: new CreateIngress(resourceArgs),
        rollbacks: getUpdatedRollbacks(),
      })
    );
    return route;
  }

  public async createDeployment(
    args: DeployArgs,
    routeUrl: string,
    resourceArgs: ResourceArgs,
    getUpdatedRollbacks: () => ResourceFetch[]
  ): Promise<DeploymentDescriptor> {
    return await this.service.withFetch((fetcher: ResourceFetcher) =>
      fetcher.execute<DeploymentDescriptor>({
        target: new CreateDeployment({
          ...resourceArgs,
          uri: args.targetFilePath,
          baseUrl: routeUrl,
          workspaceName: args.workspaceName,
          // TODO: return value to process.env.WEBPACK_REPLACE__dmnDevDeployment_baseImageFullUrl
          containerImageUrl:
            "quay.io/thiagoelg/dmn-dev-deployment-base-image@sha256:6f7df7b8ec64afec3148f02c154b15b25fca177328b7e2a21708401b98a9d84f",
          envVars: [
            {
              name: "BASE_URL",
              value: routeUrl,
            },
            {
              name: "QUARKUS_PLATFORM_VERSION",
              value: process.env.WEBPACK_REPLACE__quarkusPlatformVersion!,
            },
            {
              name: "KOGITO_RUNTIME_VERSION",
              value: process.env.WEBPACK_REPLACE__kogitoRuntimeVersion!,
            },
            {
              name: "ROOT_PATH",
              value: `/${resourceArgs.resourceName}`,
            },
          ],
        }),
        rollbacks: getUpdatedRollbacks(),
      })
    );
  }

  public async deleteRoute(resourceName: string): Promise<void> {
    await this.service.withFetch((fetcher: ResourceFetcher) =>
      fetcher.execute({
        target: new DeleteIngress({
          resourceName,
          namespace: this.args.connection.namespace,
        }),
      })
    );
  }
}
