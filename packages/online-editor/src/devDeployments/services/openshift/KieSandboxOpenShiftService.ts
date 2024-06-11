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

import {
  Condition,
  DeploymentResource,
  KubernetesConnectionStatus,
  ResourceMetadata,
  kubernetesResourcesApi,
} from "../KubernetesService";
import { DeployArgs, KieSandboxDevDeploymentsService } from "../KieSandboxDevDeploymentsService";
import { K8sResourceYaml } from "@kie-tools-core/k8s-yaml-to-apiserver-requests/dist";
import { KieSandboxDeployment, ResourceActions, defaultAnnotationTokens, defaultLabelTokens } from "../types";
import {
  K8S_RESOURCE_CREATED_BY,
  KieSandboxDevDeploymentRequiredPatches,
} from "../deploymentOptions/KieSandboxDevDeploymentRequiredPatches";
import { shouldSkipAction } from "../deploymentOptions/types";

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

export type RouteResource = K8sResourceYaml & {
  kind: "Route";
  apiVersion: "route.openshift.io/v1";
  metadata: ResourceMetadata;
  spec: {
    host: string;
    path: string;
  };
  status: {
    ingress: {
      host: string;
      routerCanonicalHostname: string;
      routerName: string;
      conditions: Condition[];
    }[];
  };
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
    return await this.kubernetesService.listResources<RouteResource>({
      kind: openShiftResourcesApi.route.kind,
      apiVersion: openShiftResourcesApi.route.apiVersion,
      queryParams: [`labelSelector=${defaultLabelTokens.createdBy}`],
    });
  }

  public async loadDevDeployments(): Promise<KieSandboxDeployment[]> {
    const deployments = await this.listDeployments();

    const routes = await this.listRoutes();

    const services = await this.listServices();

    const healthStatusList = await Promise.all(
      routes
        .filter(
          (route) => route.metadata.labels && route.metadata.name === route.metadata.labels[defaultLabelTokens.partOf]
        )
        .map((route) => ({ url: this.getRouteUrl(route), name: route.metadata.name }))
        .map(async ({ url, name }) => ({
          url,
          healtStatus: await this.getHealthStatus({ endpoint: url, deploymentName: name }),
        }))
    );

    return deployments
      .filter(
        (deployment) =>
          deployment.status &&
          deployment.metadata.name &&
          deployment.metadata.annotations &&
          deployment.metadata.labels &&
          deployment.metadata.labels[defaultLabelTokens.createdBy] === K8S_RESOURCE_CREATED_BY &&
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
          routeUrl: formWebappUrl ? `${formWebappUrl}/` : `${baseUrl}/q/swagger-ui/`,
          creationTimestamp: new Date(deployment.metadata.creationTimestamp ?? Date.now()),
          state: this.extractDeploymentStateWithHealthStatus(deployment, healthStatus),
          workspaceId: deployment.metadata.annotations![defaultAnnotationTokens.workspaceId],
          workspaceName: deployment.metadata.annotations![defaultAnnotationTokens.workspaceName],
          resources: [deployment, ...routesList, ...servicesList],
        };
      });
  }

  public async deploy(args: DeployArgs): Promise<void> {
    if (!args.deploymentOption) {
      throw new Error("Invalid deployment option!");
    }

    const actions: ResourceActions[] | undefined = [
      ...(Object.values(args.deploymentOption.parameters ?? {})
        .filter((parameter) => !shouldSkipAction(parameter, args.parametersTokenMap.parameters[parameter.id]))
        .map((parameter) => ({
          resourcePatches: parameter.resourcePatches,
          appendYamls: parameter.appendYamls,
        })) ?? []),
      { resourcePatches: KieSandboxDevDeploymentRequiredPatches() },
      {
        resourcePatches: args.deploymentOption.resourcePatches,
        appendYamls: args.deploymentOption.appendYamls,
      },
    ];

    let resources = [];
    try {
      resources = await this.kubernetesService.applyResourceYamls({
        k8sResourceYamls: [args.deploymentOption.content],
        actions,
        tokens: args.tokenMap,
        parametersTokens: args.parametersTokenMap,
      });

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
      console.error(e);
      if (resources.length) {
        this.deleteDevDeployment(resources);
      }
      throw new Error("Failed to deploy resources.");
    }
  }

  getRouteUrl(resource: RouteResource): string {
    if (!resource.status) {
      return "";
    }
    return `https://${resource.status.ingress[0].host}${resource.spec.path ?? ""}`;
  }
}
