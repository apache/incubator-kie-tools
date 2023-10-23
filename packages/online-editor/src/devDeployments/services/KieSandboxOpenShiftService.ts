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

import { KubernetesConnectionStatus, kubernetesResourcesApi } from "./KubernetesService";
import { DeployArgs, KieSandboxDevDeploymentsService, ResourceArgs } from "./KieSandboxDevDeploymentsService";
import { K8sResourceYaml } from "@kie-tools-core/k8s-yaml-to-apiserver-requests/dist";
import {
  DeploymentResource,
  KieSandboxDeployment,
  RouteResource,
  defaultAnnotationTokens,
  defaultLabelTokens,
} from "./types";

export const openShiftResourcesApi = {
  ...kubernetesResourcesApi,
  route: {
    kind: "Route",
    apiVersion: "route.openshift.io/v1",
  },
  project: {
    kind: "Project",
    apiVersion: "project.openshift.io/v1",
  },
};

export class KieSandboxOpenShiftService extends KieSandboxDevDeploymentsService {
  public async isConnectionEstablished(): Promise<KubernetesConnectionStatus> {
    try {
      const projectApiPath = this.args.k8sApiServerEndpointsByResourceKind
        .get(openShiftResourcesApi.project.kind)
        ?.get(openShiftResourcesApi.project.apiVersion)?.path.global;
      if (!projectApiPath) {
        return KubernetesConnectionStatus.ERROR;
      }

      const response = await this.kubernetesService.kubernetesFetch(
        `${projectApiPath}/${this.args.connection.namespace}`
      );
      if (response.status === 401) {
        return KubernetesConnectionStatus.MISSING_PERMISSIONS;
      } else if (response.status === 404) {
        return KubernetesConnectionStatus.NAMESPACE_NOT_FOUND;
      } else if (response.status !== 200) {
        return KubernetesConnectionStatus.ERROR;
      } else {
        return KubernetesConnectionStatus.CONNECTED;
      }
    } catch (_e) {
      return KubernetesConnectionStatus.ERROR;
    }
  }

  public async listRoutes(): Promise<RouteResource[]> {
    const rawRouteApiUrl = this.args.k8sApiServerEndpointsByResourceKind
      .get(openShiftResourcesApi.route.kind)
      ?.get(openShiftResourcesApi.route.apiVersion);
    const routeApiPath = rawRouteApiUrl?.path.namespaced ?? rawRouteApiUrl?.path.global;
    const selector = defaultLabelTokens.createdBy ? `?labelSelector=${defaultLabelTokens.createdBy}` : "";
    if (routeApiPath) {
      const routes = await this.kubernetesService
        .kubernetesFetch(`${routeApiPath.replace(":namespace", this.args.connection.namespace)}${selector}`)
        .then((data) => data.json());
      return routes.items.map((item: RouteResource) => ({
        ...item,
        kind: openShiftResourcesApi.route.kind,
      })) as RouteResource[];
    }

    return [];
  }

  public async loadDevDeployments(): Promise<KieSandboxDeployment[]> {
    const deployments = await this.listDeployments();

    if (!deployments.length) {
      return [];
    }

    const routes = await this.listRoutes();

    const services = await this.listServices();

    const healthStatusList = await Promise.all(
      routes
        .filter(
          (route) => route.metadata.labels && route.metadata.name === route.metadata.labels[defaultLabelTokens.partOf]
        )
        .map((route) => this.getRouteUrl(route))
        .map(async (url) => ({
          url,
          healtStatus: await fetch(`${url}/q/health`)
            .then((data) => data.json())
            .then((response) => response.status)
            .catch(() => "ERROR"),
        }))
    );

    return deployments
      .filter(
        (deployment) =>
          deployment.status &&
          deployment.metadata.name &&
          deployment.metadata.annotations &&
          deployment.metadata.labels &&
          deployment.metadata.labels[defaultLabelTokens.createdBy] === "kie-tools" &&
          routes.some((route) => route.metadata.name === deployment.metadata.name)
      )
      .map((deployment) => {
        const deploymentPartOf =
          (deployment.metadata.labels && deployment.metadata.labels[defaultLabelTokens.partOf]) ??
          deployment.metadata.name;
        const routesList = routes.filter(
          (routes) => routes.metadata.labels && routes.metadata.labels[defaultLabelTokens.partOf] === deploymentPartOf
        )!;
        const servicesList = services.filter(
          (service) =>
            service.metadata.labels && service.metadata.labels[defaultLabelTokens.partOf] === deploymentPartOf
        )!;
        const baseUrl = this.getRouteUrl(routesList.find((route) => route.metadata.name === deploymentPartOf)!);
        const formWebappRoute = routesList.find((route) => route.metadata.name.includes("form-webapp"));
        const formWebappUrl = formWebappRoute && this.getRouteUrl(formWebappRoute);
        const healthStatus = healthStatusList.find((status) => status.url === baseUrl)!.healtStatus;
        return {
          name: deployment.metadata.name,
          resourceName: deployment.metadata.annotations![defaultAnnotationTokens.uri],
          routeUrl: formWebappUrl ? `${formWebappUrl}/` : `${baseUrl}/q/dev/`,
          creationTimestamp: new Date(deployment.metadata.creationTimestamp ?? Date.now()),
          state: this.extractDeploymentStateWithHealthStatus(deployment, healthStatus),
          workspaceId: deployment.metadata.annotations![defaultAnnotationTokens.workspaceId],
          resources: [deployment, ...routesList, ...servicesList],
        };
      });
  }

  public async deploy(args: DeployArgs): Promise<void> {
    if (!args.deploymentOption) {
      throw new Error("Invalid deployment option!");
    }

    let resources = [];
    try {
      resources = await this.kubernetesService.applyResourceYamls([args.deploymentOption], args.tokenMap);

      const mainDeployment = resources.find(
        (resource) =>
          resource.kind === openShiftResourcesApi.deployment.kind &&
          resource.metadata.name === args.tokenMap.devDeployment.uniqueName
      ) as DeploymentResource;
      const mainRoute = (await this.listRoutes()).find(
        (route) => route.metadata.name === args.tokenMap.devDeployment.uniqueName
      ) as RouteResource;
      const routeUrl = this.getRouteUrl(mainRoute);

      const apiKey = args.tokenMap.devDeployment.uploadService.apiKey;

      this.uploadAssets({
        deployment: mainDeployment,
        workspaceZipBlob: args.workspaceZipBlob,
        baseUrl: routeUrl,
        apiKey,
      });
    } catch (e) {
      console.log({ resources });
      console.error(e);
      if (resources.length) {
        this.deleteDevDeployment(resources);
      }
      throw new Error("Failed to deploy resources.");
    }
  }

  async deleteRoute(resource: string) {
    const rawRouteApiUrl = this.args.k8sApiServerEndpointsByResourceKind
      .get(openShiftResourcesApi.route.kind)
      ?.get(openShiftResourcesApi.route.apiVersion);
    const routeApiPath = rawRouteApiUrl?.path.namespaced ?? rawRouteApiUrl?.path.global;

    if (!routeApiPath) {
      throw new Error("No Route API path");
    }

    return await this.kubernetesService
      .kubernetesFetch(`${routeApiPath.replace(":namespace", this.args.connection.namespace)}/${resource}`, {
        method: "DELETE",
      })
      .then((data) => data.json());
  }

  public async deleteDevDeployment(resources: K8sResourceYaml[]): Promise<void> {
    await Promise.all(
      resources.map(async (resource) => {
        switch (resource.kind) {
          case openShiftResourcesApi.deployment.kind:
            await this.deleteDeployment(resource.metadata!.name!);
            break;
          case openShiftResourcesApi.service.kind:
            await this.deleteService(resource.metadata!.name!);
            break;
          case openShiftResourcesApi.route.kind:
            await this.deleteRoute(resource.metadata!.name!);
            break;
          default:
            console.error("Invalid resource kind. Can't delete.");
        }
      })
    );
  }

  getRouteUrl(resource: RouteResource): string {
    if (!resource.status) {
      return "";
    }
    return `https://${resource.status.ingress[0].host}${resource.spec.path ?? ""}`;
  }
}
