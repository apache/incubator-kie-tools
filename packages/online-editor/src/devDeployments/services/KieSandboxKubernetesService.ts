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

import { UploadStatus, getUploadStatus, postUpload } from "../DevDeploymentUploadAppApi";
import {
  DeploymentResource,
  IngressResource,
  KieSandboxDeployment,
  ServiceResource,
  defaultAnnotationTokens,
  defaultLabelTokens,
} from "./types";
import {
  parseK8sResourceYaml,
  callK8sApiServer,
  interpolateK8sResourceYamls,
  K8sResourceYaml,
} from "@kie-tools-core/k8s-yaml-to-apiserver-requests/dist";
import { KubernetesConnectionStatus, KubernetesService } from "./KubernetesService";
import { createDeploymentYaml } from "./resources/kubernetes/Deployment";
import { createServiceYaml } from "./resources/kubernetes/Service";
import { createIngressYaml } from "./resources/kubernetes/Ingress";
import { createSelfSubjectAccessReviewYaml } from "./resources/kubernetes/SelfSubjectAccessReview";
import {
  CHECK_UPLOAD_STATUS_POLLING_TIME,
  DeployArgs,
  KieSandboxDevDeploymentsService,
} from "./KieSandboxDevDeploymentsService";
import { DeploymentState } from "./common";

export class KieSandboxKubernetesService extends KieSandboxDevDeploymentsService {
  public async isConnectionEstablished(): Promise<KubernetesConnectionStatus> {
    try {
      const namespaceApiPath = this.args.k8sApiServerEndpointsByResourceKind.get("Namespace")?.get("v1")?.path.global;
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
        k8sResourceYamls: parseK8sResourceYaml(
          requiredResources.map((resource) =>
            interpolateK8sResourceYamls(createSelfSubjectAccessReviewYaml, {
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
    const rawIngressApiUrl = this.args.k8sApiServerEndpointsByResourceKind.get("Ingress")?.get("networking.k8s.io/v1");
    const ingressApiPath = rawIngressApiUrl?.path.namespaced ?? rawIngressApiUrl?.path.global;
    const selector = defaultLabelTokens.createdBy ? `?labelSelector=${defaultLabelTokens.createdBy}` : "";
    if (ingressApiPath) {
      const ingresses = await this.kubernetesService
        .kubernetesFetch(`${ingressApiPath.replace(":namespace", this.args.connection.namespace)}${selector}`)
        .then((data) => data.json());
      return ingresses.items as IngressResource[];
    }

    return [];
  }

  public async listServices(): Promise<ServiceResource[]> {
    const rawServicesApiUrl = this.args.k8sApiServerEndpointsByResourceKind.get("Service")?.get("v1");
    const servicesApiPath = rawServicesApiUrl?.path.namespaced ?? rawServicesApiUrl?.path.global;
    const selector = defaultLabelTokens.createdBy ? `?labelSelector=${defaultLabelTokens.createdBy}` : "";

    if (servicesApiPath) {
      const services = await this.kubernetesService
        .kubernetesFetch(`${servicesApiPath.replace(":namespace", this.args.connection.namespace)}${selector}`)
        .then((data) => data.json());
      return services.items as ServiceResource[];
    }

    return [];
  }

  public async listDeployments(): Promise<DeploymentResource[]> {
    const rawDeploymentsApiUrl = this.args.k8sApiServerEndpointsByResourceKind.get("Deployment")?.get("apps/v1");
    const deploymentsApiPath = rawDeploymentsApiUrl?.path.namespaced ?? rawDeploymentsApiUrl?.path.global;
    const selector = defaultLabelTokens.createdBy ? `?labelSelector=${defaultLabelTokens.createdBy}` : "";

    if (deploymentsApiPath) {
      const deployments = await this.kubernetesService
        .kubernetesFetch(`${deploymentsApiPath.replace(":namespace", this.args.connection.namespace)}${selector}`)
        .then((data) => data.json());
      return deployments.items as DeploymentResource[];
    }

    return [];
  }

  public async getDeployment(resourceId: string): Promise<DeploymentResource> {
    const rawDeploymentsApiUrl = this.args.k8sApiServerEndpointsByResourceKind.get("Deployment")?.get("apps/v1");
    const deploymentsApiPath = rawDeploymentsApiUrl?.path.namespaced ?? rawDeploymentsApiUrl?.path.global;

    if (!deploymentsApiPath) {
      throw new Error("No Deployment API path");
    }

    return await this.kubernetesService
      .kubernetesFetch(`${deploymentsApiPath.replace(":namespace", this.args.connection.namespace)}/${resourceId}`)
      .then((data) => data.json());
  }

  public async loadDevDeployments(): Promise<KieSandboxDeployment[]> {
    const deployments = await this.listDeployments();

    if (!deployments.length) {
      return [];
    }

    const ingresses = await this.listIngress();

    const services = await this.listServices();

    const uploadStatuses = await Promise.all(
      ingresses
        .map((ingress) => this.getIngressUrl(ingress))
        .map(async (url) => ({ url: url, uploadStatus: await getUploadStatus({ baseUrl: url }) }))
    );

    return deployments
      .filter(
        (deployment) =>
          deployment.status &&
          deployment.metadata.name &&
          deployment.metadata.annotations &&
          deployment.metadata.labels &&
          deployment.metadata.labels[defaultLabelTokens.createdBy] === "kie-tools" &&
          ingresses.some((ingress) => ingress.metadata.name === deployment.metadata.name)
      )
      .map((deployment) => {
        const ingress = ingresses.find((ingress) => ingress.metadata.name === deployment.metadata.name)!;
        const service = services.find((service) => service.metadata.name === deployment.metadata.name)!;
        const baseUrl = this.getIngressUrl(ingress);
        const uploadStatus = uploadStatuses.find((status) => status.url === baseUrl)!.uploadStatus;
        return {
          name: deployment.metadata.name,
          resourceName: deployment.metadata.annotations![defaultAnnotationTokens.uri],
          routeUrl: baseUrl,
          creationTimestamp: new Date(deployment.metadata.creationTimestamp ?? Date.now()),
          state: this.extractDeploymentStateWithUploadStatus(deployment, uploadStatus),
          workspaceId: deployment.metadata.annotations![defaultAnnotationTokens.workspaceId],
          resources: [deployment, ingress, service],
        };
      });
  }

  public async uploadAssets(args: {
    deployment: DeploymentResource;
    workspaceZipBlob: Blob;
    baseUrl: string;
    apiKey: string;
  }) {
    return new Promise<void>((resolve, reject) => {
      let deploymentState = this.kubernetesService.extractDeploymentState({ deployment: args.deployment });
      const interval = setInterval(async () => {
        if (deploymentState !== DeploymentState.UP) {
          const deployment = await this.getDeployment(args.deployment.metadata.name);
          deploymentState = this.kubernetesService.extractDeploymentState({ deployment });
        } else {
          try {
            const uploadStatus = await getUploadStatus({ baseUrl: args.baseUrl });
            if (uploadStatus === "NOT_READY") {
              return;
            }
            clearInterval(interval);
            if (uploadStatus === "READY") {
              await postUpload({ baseUrl: args.baseUrl, workspaceZipBlob: args.workspaceZipBlob, apiKey: args.apiKey });
              resolve();
            }
          } catch (e) {
            console.error(e);
            reject(e);
            clearInterval(interval);
          }
        }
      }, CHECK_UPLOAD_STATUS_POLLING_TIME);
    });
  }

  public async deploy(args: DeployArgs): Promise<void> {
    const resources = await this.kubernetesService.applyResourceYamls(
      [createDeploymentYaml, createServiceYaml, createIngressYaml],
      args.tokenMap
    );

    const deployment = resources.find((resource) => resource.kind === "Deployment") as DeploymentResource;
    const ingress = resources.find((resource) => resource.kind === "Ingress") as IngressResource;
    const ingressUrl = this.getIngressUrl(ingress);

    const apiKey = args.tokenMap.devDeployment.uploadService.apiKey;

    this.uploadAssets({ deployment, workspaceZipBlob: args.workspaceZipBlob, baseUrl: ingressUrl, apiKey });
  }

  public async deleteDeployment(resourceName: string) {
    // await this.service.withFetch((fetcher: ResourceFetcher) =>
    //   fetcher.execute({
    //     target: new DeleteDeployment({
    //       resourceName,
    //       namespace: this.args.connection.namespace,
    //     }),
    //   })
    // );
  }
  public async deleteService(resourceName: string) {
    // await this.service.withFetch((fetcher: ResourceFetcher) =>
    //   fetcher.execute({
    //     target: new DeleteService({
    //       resourceName,
    //       namespace: this.args.connection.namespace,
    //     }),
    //   })
    // );
  }

  public async deleteDevDeployment(resourceName: string): Promise<void> {
    await this.deleteDeployment(resourceName);
    await this.deleteService(resourceName);
    await this.deleteIngress(resourceName);
  }

  public extractDeploymentStateWithUploadStatus(
    deployment: DeploymentResource,
    uploadStatus: UploadStatus
  ): DeploymentState {
    const state = this.extractDevDeploymentState({ deployment });

    if (state !== DeploymentState.UP) {
      return state;
    }

    if (uploadStatus === "ERROR") {
      return DeploymentState.ERROR;
    }

    if (uploadStatus !== "UPLOADED") {
      return DeploymentState.IN_PROGRESS;
    }

    return DeploymentState.UP;
  }

  // public composeDeploymentUrlFromIngress(ingress: any): string {
  //   ;
  // }

  getIngressUrl(resource: IngressResource): string {
    return `${new URL(this.args.connection.host).origin}/${resource.metadata?.name}`;
  }

  // async createIngress(
  //   resourceArgs: ResourceArgs,
  //   getUpdatedRollbacks: () => ResourceFetch[]
  // ): Promise<IngressDescriptor> {
  //   const route = await this.service.withFetch((fetcher: ResourceFetcher) =>
  //     fetcher.execute<IngressDescriptor>({
  //       target: new CreateIngress({ ...resourceArgs, resourceDataSource: ResourceDataSource.TEMPLATE }),
  //       rollbacks: getUpdatedRollbacks(),
  //     })
  //   );
  //   return route;
  // }

  // async listIngresses(): Promise<IngressDescriptor[]> {
  //   const ingresses = (
  //     await this.service.withFetch((fetcher: ResourceFetcher) =>
  //       fetcher.execute<IngressGroupDescriptor>({
  //         target: new ListIngresses({
  //           namespace: this.args.connection.namespace,
  //         }),
  //       })
  //     )
  //   ).items.filter(
  //     (route) => route.metadata.labels && route.metadata.labels[ResourceLabelNames.CREATED_BY] === RESOURCE_OWNER
  //   );

  //   return ingresses;
  // }

  async deleteIngress(resourceName: string) {
    // await this.service.withFetch((fetcher: ResourceFetcher) =>
    //   fetcher.execute({
    //     target: new DeleteIngress({
    //       resourceName,
    //       namespace: this.args.connection.namespace,
    //     }),
    //   })
    // );
  }
}
