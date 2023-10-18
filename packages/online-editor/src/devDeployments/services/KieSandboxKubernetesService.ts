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

import { getUploadStatus, postUpload } from "../DevDeploymentUploadAppApi";
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
import {
  CHECK_UPLOAD_STATUS_POLLING_TIME,
  DeployArgs,
  KieSandboxDevDeploymentsService,
} from "./KieSandboxDevDeploymentsService";
import { DeploymentState } from "./common";
import { selfSubjectAccessReviewYaml } from "./resources/kubernetes/SelfSubjectAccessReviewYaml";
import { createDeploymentYamls } from "./resources/kubernetes";

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
            interpolateK8sResourceYamls(selfSubjectAccessReviewYaml, {
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
      return ingresses.items.map((item: IngressResource) => ({ ...item, kind: "Ingress" })) as IngressResource[];
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
      return services.items.map((item: ServiceResource) => ({ ...item, kind: "Service" })) as ServiceResource[];
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
      return deployments.items.map((item: DeploymentResource) => ({
        ...item,
        kind: "Deployment",
      })) as DeploymentResource[];
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

    const healthStatusList = await Promise.all(
      ingresses
        .filter(
          (ingress) =>
            ingress.metadata.labels && ingress.metadata.name === ingress.metadata.labels[defaultLabelTokens.partOf]
        )
        .map((ingress) => this.getIngressUrl(ingress))
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
        const baseUrl = this.getIngressUrl(ingressList.find((ingress) => ingress.metadata.name === deploymentPartOf)!);
        const healthStatus = healthStatusList.find((status) => status.url === baseUrl)!.healtStatus;
        return {
          name: deployment.metadata.name,
          resourceName: deployment.metadata.annotations![defaultAnnotationTokens.uri],
          routeUrl: ingressList.find((ingress) => ingress.metadata.name.includes("form-webapp"))
            ? `${baseUrl}/form-webapp`
            : `${baseUrl}/q/dev`,
          creationTimestamp: new Date(deployment.metadata.creationTimestamp ?? Date.now()),
          state: this.extractDeploymentStateWithHealthStatus(deployment, healthStatus),
          workspaceId: deployment.metadata.annotations![defaultAnnotationTokens.workspaceId],
          resources: [deployment, ...ingressList, ...servicesList],
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
    const deploymentYamls = createDeploymentYamls.find(
      (deploymentOptionYamls) => deploymentOptionYamls.name === args.deploymentOption
    );

    if (!deploymentYamls) {
      throw new Error("Invalid deployment option!");
    }

    let resources = [];
    try {
      resources = await this.kubernetesService.applyResourceYamls([deploymentYamls.content], args.tokenMap);

      const mainDeployment = resources.find(
        (resource) =>
          resource.kind === "Deployment" && resource.metadata.name === args.tokenMap.devDeployment.uniqueName
      ) as DeploymentResource;
      const mainIngress = resources.find(
        (resource) => resource.kind === "Ingress" && resource.metadata.name === args.tokenMap.devDeployment.uniqueName
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
      console.log({ resources });
      console.error(e);
      if (resources.length) {
        this.deleteDevDeployment(resources);
      }
      throw new Error("Failed to deploy resources.");
    }
  }

  public async deleteDeployment(resource: string) {
    const rawDeploymentsApiUrl = this.args.k8sApiServerEndpointsByResourceKind.get("Deployment")?.get("apps/v1");
    const deploymentsApiPath = rawDeploymentsApiUrl?.path.namespaced ?? rawDeploymentsApiUrl?.path.global;

    if (!deploymentsApiPath) {
      throw new Error("No Deployment API path");
    }

    return await this.kubernetesService
      .kubernetesFetch(`${deploymentsApiPath.replace(":namespace", this.args.connection.namespace)}/${resource}`, {
        method: "DELETE",
      })
      .then((data) => data.json());
  }

  public async deleteService(resource: string) {
    const rawServicesApiUrl = this.args.k8sApiServerEndpointsByResourceKind.get("Service")?.get("v1");
    const servicesApiPath = rawServicesApiUrl?.path.namespaced ?? rawServicesApiUrl?.path.global;

    if (!servicesApiPath) {
      throw new Error("No Service API path");
    }

    return await this.kubernetesService
      .kubernetesFetch(`${servicesApiPath.replace(":namespace", this.args.connection.namespace)}/${resource}`, {
        method: "DELETE",
      })
      .then((data) => data.json());
  }

  async deleteIngress(resource: string) {
    const rawIngressApiUrl = this.args.k8sApiServerEndpointsByResourceKind.get("Ingress")?.get("networking.k8s.io/v1");
    const ingressApiPath = rawIngressApiUrl?.path.namespaced ?? rawIngressApiUrl?.path.global;

    if (!ingressApiPath) {
      throw new Error("No Ingress API path");
    }

    return await this.kubernetesService
      .kubernetesFetch(`${ingressApiPath.replace(":namespace", this.args.connection.namespace)}/${resource}`, {
        method: "DELETE",
      })
      .then((data) => data.json());
  }

  public async deleteDevDeployment(resources: K8sResourceYaml[]): Promise<void> {
    await Promise.all(
      resources.map(async (resource) => {
        switch (resource.kind) {
          case "Deployment":
            await this.deleteDeployment(resource.metadata!.name!);
            break;
          case "Service":
            await this.deleteService(resource.metadata!.name!);
            break;
          case "Ingress":
            await this.deleteIngress(resource.metadata!.name!);
            break;
          default:
            console.error("Invalid resource kind. Can't delete.");
        }
      })
    );
  }

  public extractDeploymentStateWithHealthStatus(deployment: DeploymentResource, healtStatus: string): DeploymentState {
    const state = this.extractDevDeploymentState({ deployment });

    if (state !== DeploymentState.UP) {
      return state;
    }

    if (healtStatus !== "UP") {
      return DeploymentState.ERROR;
    }

    return DeploymentState.UP;
  }

  getIngressUrl(resource: IngressResource): string {
    return `${new URL(this.args.connection.host).origin}/${resource.metadata?.name}`;
  }
}
