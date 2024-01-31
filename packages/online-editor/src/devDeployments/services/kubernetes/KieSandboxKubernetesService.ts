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

import { KieSandboxDeployment, ResourceActions, defaultAnnotationTokens, defaultLabelTokens } from "../types";
import {
  parseK8sResourceYamls,
  callK8sApiServer,
  interpolateK8sResourceYaml,
} from "@kie-tools-core/k8s-yaml-to-apiserver-requests/dist";
import {
  DeploymentResource,
  IngressResource,
  KubernetesConnectionStatus,
  KubernetesService,
  kubernetesResourcesApi,
} from "../KubernetesService";
import { DeployArgs, KieSandboxDevDeploymentsService } from "../KieSandboxDevDeploymentsService";
import { SelfSubjectAccessReviewYaml } from "./resources/SelfSubjectAccessReviewYaml";
import {
  K8S_RESOURCE_CREATED_BY,
  KieSandboxDevDeploymentRequiredPatches,
} from "../deploymentOptions/KieSandboxDevDeploymentRequiredPatches";
import { shouldSkipAction } from "../deploymentOptions/types";

export class KieSandboxKubernetesService extends KieSandboxDevDeploymentsService {
  public async isConnectionEstablished(): Promise<KubernetesConnectionStatus> {
    try {
      const namespaceApiPath = this.args.k8sApiServerEndpointsByResourceKind
        .get(kubernetesResourcesApi.namespace.kind)
        ?.get(kubernetesResourcesApi.namespace.apiVersion)?.path.global;
      if (!namespaceApiPath) {
        return KubernetesConnectionStatus.ERROR;
      }

      const response = await this.kubernetesService.kubernetesFetch(
        `${namespaceApiPath}/${this.args.connection.namespace}`
      );
      if (response.status === 401) {
        return KubernetesConnectionStatus.MISSING_PERMISSIONS;
      } else if (response.status === 404) {
        return KubernetesConnectionStatus.NAMESPACE_NOT_FOUND;
      } else if (response.status !== 200) {
        return KubernetesConnectionStatus.ERROR;
      }

      const requiredResources = ["deployments", "services", "ingresses"];

      const permissionsResultMap = await callK8sApiServer({
        k8sApiServerEndpointsByResourceKind: this.args.k8sApiServerEndpointsByResourceKind,
        k8sResourceYamls: parseK8sResourceYamls(
          requiredResources.map((resource) =>
            interpolateK8sResourceYaml(SelfSubjectAccessReviewYaml(), {
              namespace: this.args.connection.namespace,
              resource,
            })
          )
        ),
        k8sApiServerUrl: KubernetesService.getBaseUrl(this.args),
        k8sNamespace: this.args.connection.namespace,
        k8sServiceAccountToken: this.args.connection.token,
      }).then((results) =>
        results.map((result) => ({
          resource: result.spec.resourceAttributes.resource,
          allowed: result.status.allowed,
        }))
      );

      if (permissionsResultMap.some((permissionResult) => !permissionResult.allowed)) {
        return KubernetesConnectionStatus.MISSING_PERMISSIONS;
      }

      return KubernetesConnectionStatus.CONNECTED;
    } catch (error) {
      console.error(error);
      return KubernetesConnectionStatus.ERROR;
    }
  }

  public async listIngress(): Promise<IngressResource[]> {
    return await this.kubernetesService.listResources<IngressResource>({
      kind: kubernetesResourcesApi.ingress.kind,
      apiVersion: kubernetesResourcesApi.ingress.apiVersion,
      queryParams: [`labelSelector=${defaultLabelTokens.createdBy}`],
    });
  }

  public async loadDevDeployments(): Promise<KieSandboxDeployment[]> {
    try {
      const deployments = await this.listDeployments();

      const ingresses = await this.listIngress();

      const services = await this.listServices();

      const healthStatusList = await Promise.all(
        ingresses
          .filter(
            (ingress) =>
              ingress.metadata.labels && ingress.metadata.name === ingress.metadata.labels[defaultLabelTokens.partOf]
          )
          .map((ingress) => ({ url: this.getIngressUrl(ingress), name: ingress.metadata.name }))
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
            ingresses.some((ingress) => ingress.metadata.name === deployment.metadata.name)
        )
        .map((deployment) => {
          const deploymentPartOf =
            (deployment.metadata.labels && deployment.metadata.labels[defaultLabelTokens.partOf]) ??
            deployment.metadata.name;
          const ingressList = ingresses.filter(
            (ingress) =>
              ingress.metadata.labels && ingress.metadata.labels[defaultLabelTokens.partOf] === deploymentPartOf
          )!;
          const servicesList = services.filter(
            (service) =>
              service.metadata.labels && service.metadata.labels[defaultLabelTokens.partOf] === deploymentPartOf
          )!;
          const baseUrl = this.getIngressUrl(
            ingressList.find((ingress) => ingress.metadata.name === deploymentPartOf)!
          );
          const healthStatus = healthStatusList.find((status) => status.url === baseUrl)!.healtStatus;
          return {
            name: deployment.metadata.name,
            routeUrl: ingressList.find((ingress) => ingress.metadata.name.includes("form-webapp"))
              ? `${baseUrl}/form-webapp/`
              : `${baseUrl}/q/swagger-ui/`,
            creationTimestamp: new Date(deployment.metadata.creationTimestamp ?? Date.now()),
            state: this.extractDeploymentStateWithHealthStatus(deployment, healthStatus),
            workspaceId: deployment.metadata.annotations![defaultAnnotationTokens.workspaceId],
            workspaceName: deployment.metadata.annotations![defaultAnnotationTokens.workspaceName],
            resources: [deployment, ...ingressList, ...servicesList],
          };
        });
    } catch (error) {
      throw new Error("Error: Failed to load Dev deployments from Kubernetes provider.", error);
    }
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
          resource.kind === kubernetesResourcesApi.deployment.kind &&
          resource.metadata.name === args.tokenMap.devDeployment.uniqueName
      ) as DeploymentResource;
      const mainIngress = resources.find(
        (resource) =>
          resource.kind === kubernetesResourcesApi.ingress.kind &&
          resource.metadata.name === args.tokenMap.devDeployment.uniqueName
      ) as IngressResource;
      const ingressUrl = this.getIngressUrl(mainIngress);

      const apiKey = args.tokenMap.devDeployment.uploadService.apiKey;

      this.uploadAssets({
        deployment: mainDeployment,
        workspaceZipBlob: args.workspaceZipBlob,
        baseUrl: ingressUrl,
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

  getIngressUrl(resource: IngressResource): string {
    return `${new URL(this.args.connection.host).origin}/${resource.metadata?.name}`;
  }
}
