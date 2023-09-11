/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

import { KubernetesConnectionStatus } from "./KubernetesService";
import { getProjectApiPath } from "./resources/openshift/Project";
import {
  DeployArgs,
  KieSandboxDeployment,
  KieSandboxDevDeploymentsService,
  ResourceArgs,
} from "./KieSandboxDevDeploymentsService";
import { K8sResourceYaml } from "@kie-tools-core/k8s-yaml-to-apiserver-requests/dist";

export class KieSandboxOpenShiftService extends KieSandboxDevDeploymentsService {
  uploadAssets(args: {
    resourceArgs: ResourceArgs;
    deployment: K8sResourceYaml;
    workspaceZipBlob: Blob;
    baseUrl: string;
  }): Promise<void> {
    return this.uploadAssets(args);
  }

  public async isConnectionEstablished(): Promise<KubernetesConnectionStatus> {
    try {
      await this.kubernetesService.kubernetesFetch(getProjectApiPath(this.args.connection.namespace));
      return KubernetesConnectionStatus.CONNECTED;
    } catch (_e) {
      return KubernetesConnectionStatus.ERROR;
    }
  }

  public async loadDevDeployments(): Promise<KieSandboxDeployment[]> {
    // const deployments = await this.kieSandboxKubernetesService.listDeployments();

    // if (!deployments.length) {
    //   return [];
    // }

    // const routes = await this.listRoutes();

    // const uploadStatuses = await Promise.all(
    //   routes
    //     .map((route) => this.getRouteUrl(route))
    //     .map(async (url) => ({ url: url, uploadStatus: await getUploadStatus({ baseUrl: url }) }))
    // );

    // return deployments
    //   .filter(
    //     (deployment) =>
    //       deployment.status &&
    //       deployment.metadata.name &&
    //       deployment.metadata.annotations &&
    //       deployment.metadata.labels &&
    //       deployment.metadata.labels[ResourceLabelNames.CREATED_BY] === RESOURCE_OWNER &&
    //       routes.some((route) => route.metadata.name === deployment.metadata.name)
    //   )
    //   .map((deployment) => {
    //     const route = routes.find((route) => route.metadata.name === deployment.metadata.name)!;
    //     const baseUrl = this.getRouteUrl(route);
    //     const uploadStatus = uploadStatuses.find((status) => status.url === baseUrl)!.uploadStatus;
    //     return {
    //       resourceName: deployment.metadata.name!,
    //       uri: deployment.metadata.annotations![ResourceLabelNames.URI],
    //       routeUrl: baseUrl,
    //       creationTimestamp: new Date(deployment.metadata.creationTimestamp ?? Date.now()),
    //       state: this.kieSandboxKubernetesService.extractDeploymentStateWithUploadStatus(deployment, uploadStatus),
    //       workspaceName: deployment.metadata.annotations![ResourceLabelNames.WORKSPACE_NAME],
    //     };
    //   });
    return [];
  }

  public async deploy(args: DeployArgs): Promise<void> {
    // const resourceArgs: ResourceArgs = {
    //   namespace: this.args.connection.namespace,
    //   resourceName: this.kieSandboxKubernetesService.newResourceName(),
    //   createdBy: RESOURCE_OWNER,
    // };
    // const rollbacks = [new DeleteRoute(resourceArgs), new DeleteService(resourceArgs)];
    // let rollbacksCount = rollbacks.length;
    // await this.openshiftService.withFetch((fetcher: ResourceFetcher) =>
    //   fetcher.execute({
    //     target: new CreateService({ ...resourceArgs, resourceDataSource: ResourceDataSource.TEMPLATE }),
    //   })
    // );
    // const getUpdatedRollbacks = () => rollbacks.slice(--rollbacksCount);
    // const route = await this.createRoute(resourceArgs, getUpdatedRollbacks);
    // const routeUrl = this.getRouteUrl(route);
    // const deployment = await this.kieSandboxKubernetesService.createDeployment({
    //   deployArgs: args,
    //   routeUrl,
    //   resourceArgs,
    //   rootPath: "",
    //   getUpdatedRollbacks,
    // });
    // this.kieSandboxKubernetesService.uploadAssets({
    //   resourceArgs,
    //   deployment,
    //   workspaceZipBlob: args.workspaceZipBlob,
    //   baseUrl: routeUrl,
    // });
  }

  public async deleteDevDeployment(resourceName: string): Promise<void> {
    // this.kieSandboxKubernetesService.deleteDeployment(resourceName);
    // this.kieSandboxKubernetesService.deleteService(resourceName);
    // this.deleteRoute(resourceName);
  }

  getRouteUrl(resource: K8sResourceYaml): string {
    // return this.kieSandboxKubernetesService.composeDeploymentUrlFromRoute(resource);
    return "";
  }

  // async createRoute(resourceArgs: ResourceArgs, getUpdatedRollbacks: () => ResourceFetch[]): Promise<RouteDescriptor> {
  //   // const route = await this.openshiftService.withFetch((fetcher: ResourceFetcher) =>
  //   //   fetcher.execute<RouteDescriptor>({
  //   //     target: new CreateRoute({ ...resourceArgs, resourceDataSource: ResourceDataSource.TEMPLATE }),
  //   //     rollbacks: getUpdatedRollbacks(),
  //   //   })
  //   // );
  //   // return route;
  // }

  async listRoutes(): Promise<K8sResourceYaml[]> {
    // const routes = (
    //   await this.openshiftService.withFetch((fetcher: ResourceFetcher) =>
    //     fetcher.execute<RouteGroupDescriptor>({
    //       target: new ListRoutes({
    //         namespace: this.args.connection.namespace,
    //       }),
    //     })
    //   )
    // ).items.filter(
    //   (route) => route.metadata.labels && route.metadata.labels[ResourceLabelNames.CREATED_BY] === RESOURCE_OWNER
    // );

    // return routes;
    return Promise.resolve([]);
  }

  async deleteRoute(resourceName: string): Promise<void> {
    // await this.openshiftService.withFetch((fetcher: ResourceFetcher) =>
    //   fetcher.execute({
    //     target: new DeleteRoute({
    //       resourceName,
    //       namespace: this.args.connection.namespace,
    //     }),
    //   })
    // );
  }
}
