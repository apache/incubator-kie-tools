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

import { UploadStatus, getUploadStatus, postUpload } from "../DmnDevDeploymentQuarkusAppApi";
import { defaultLabelTokens } from "./types";
import {
  parseK8sResourceYaml,
  callK8sApiServer,
  interpolateK8sResourceYamls,
  K8sResourceYaml,
} from "@kie-tools-core/k8s-yaml-to-apiserver-requests/dist";
import { KubernetesConnectionStatus, KubernetesService } from "./KubernetesService";
import { createDeploymentYaml, getDeploymentListApiPath } from "./resources/kubernetes/Deployment";
import { createServiceYaml } from "./resources/kubernetes/Service";
import { createIngressYaml } from "./resources/kubernetes/Ingress";
import { getNamespaceApiPath } from "./resources/kubernetes/Namespace";
import { createSelfSubjectAccessReviewYaml } from "./resources/kubernetes/SelfSubjectAccessReview";
import {
  DeployArgs,
  KieSandboxDeployment,
  KieSandboxDevDeploymentsService,
  ResourceArgs,
} from "./KieSandboxDevDeploymentsService";
import { DeploymentState } from "./common";

export const RESOURCE_PREFIX = "dmn-dev-deployment";
export const RESOURCE_OWNER = "kie-sandbox";
export const CHECK_UPLOAD_STATUS_POLLING_TIME = 3000;

type ResourceMetadata = {
  annotations?: Record<string, string>;
  labels?: Record<string, string>;
  name: string;
  namespace: string;
  creationTimestamp: string;
  uid: string;
};

type IngressResource = {
  metadata: ResourceMetadata;
  spec: {
    rules: {
      http: {
        paths: {
          backend: {
            service: {
              name: string;
              port: {
                number: number;
              };
            };
            path: string;
            pathType: string;
          };
        }[];
      };
    };
  };
  status: {
    loadBalancer: {
      ingress: {
        hostname: string;
      }[];
    };
  };
};

type ServiceResource = {
  metadata: ResourceMetadata;
  spec: any;
};

type DeploymentResource = {
  metadata: ResourceMetadata;
  spec: {
    replicas: number;
    selector: {
      matchLabels: string;
    };
    template: {
      spec: {
        containers: {
          env: { name: string; value: string }[];
          image: string;
          imagePullPolicy: string;
          name: string;
        }[];
      };
    };
  };
  status: {
    availableReplicas: number;
    readyReplicas: number;
    replicas: number;
    updatedReplicas: number;
    conditions: {
      type: string;
      status: string;
      reason: string;
      message: string;
      lastTransitionTime: string;
      lastUpdateTime: string;
    }[];
  };
};

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

  public newResourceName(): string {
    return this.kubernetesService.newResourceName(RESOURCE_PREFIX);
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

    // TO DO: Parse this.

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

    // TO DO: Parse this.

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

    // TO DO: Parse this.

    return [];
  }

  public async loadDevDeployments(): Promise<KieSandboxDeployment[]> {
    // const deployments = await this.listDeployments();

    // if (!deployments.length) {
    //   return [];
    // }

    // const ingresses = await this.listIngresses();

    // const uploadStatuses = await Promise.all(
    //   ingresses
    //     .map((ingress) => this.getIngressUrl(ingress))
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
    //       ingresses.some((ingress) => ingress.metadata.name === deployment.metadata.name)
    //   )
    //   .map((deployment) => {
    //     const ingress = ingresses.find((ingress) => ingress.metadata.name === deployment.metadata.name)!;
    //     const baseUrl = this.getIngressUrl(ingress);
    //     const uploadStatus = uploadStatuses.find((status) => status.url === baseUrl)!.uploadStatus;
    //     return {
    //       resourceName: deployment.metadata.name!,
    //       uri: deployment.metadata.annotations![ResourceLabelNames.URI],
    //       routeUrl: baseUrl,
    //       creationTimestamp: new Date(deployment.metadata.creationTimestamp ?? Date.now()),
    //       state: this.extractDeploymentStateWithUploadStatus(deployment, uploadStatus),
    //       workspaceName: deployment.metadata.annotations![ResourceLabelNames.WORKSPACE_NAME],
    //     };
    //   });
    console.log(await this.listDeployments());
    console.log(await this.listServices());
    console.log(await this.listIngress());
    return [];
  }

  // public async createDeployment(args: {
  //   deployArgs: DeployArgs;
  //   routeUrl: string;
  //   resourceArgs: ResourceArgs;
  //   rootPath: string;
  //   getUpdatedRollbacks: () => ResourceFetch[];
  // }): Promise<DeploymentDescriptor> {
  //   return await this.service.withFetch((fetcher: ResourceFetcher) =>
  //     fetcher.execute<DeploymentDescriptor>({
  //       target: new CreateDeployment({
  //         ...args.resourceArgs,
  //         uri: args.deployArgs.targetFilePath,
  //         baseUrl: args.routeUrl,
  //         workspaceName: args.deployArgs.workspaceName,
  //         containerImageUrl: args.deployArgs.containerImageUrl,
  //         envVars: [
  //           {
  //             name: "BASE_URL",
  //             value: args.routeUrl,
  //           },
  //           {
  //             name: "QUARKUS_PLATFORM_VERSION",
  //             value: process.env.WEBPACK_REPLACE__quarkusPlatformVersion!,
  //           },
  //           {
  //             name: "KOGITO_RUNTIME_VERSION",
  //             value: process.env.WEBPACK_REPLACE__kogitoRuntimeVersion!,
  //           },
  //           {
  //             name: "ROOT_PATH",
  //             value: `/${args.rootPath}`,
  //           },
  //         ],
  //         resourceDataSource: ResourceDataSource.TEMPLATE,
  //         imagePullPolicy: process.env
  //           .WEBPACK_REPLACE__dmnDevDeployment_imagePullPolicy! as CreateDeploymentTemplateArgs["imagePullPolicy"],
  //       }),
  //       rollbacks: args.getUpdatedRollbacks(),
  //     })
  //   );
  // }

  public async uploadAssets(args: {
    resourceArgs: ResourceArgs;
    deployment: K8sResourceYaml;
    workspaceZipBlob: Blob;
    baseUrl: string;
  }) {
    // return new Promise<void>((resolve, reject) => {
    //   let deploymentState = this.service.extractDeploymentState({ deployment: args.deployment });
    //   const interval = setInterval(async () => {
    //     if (deploymentState !== DeploymentState.UP) {
    //       const deployment = await this.service.withFetch((fetcher: ResourceFetcher) =>
    //         fetcher.execute<DeploymentDescriptor>({
    //           target: new GetDeployment(args.resourceArgs),
    //         })
    //       );
    //       deploymentState = this.service.extractDeploymentState({ deployment });
    //     } else {
    //       try {
    //         const uploadStatus = await getUploadStatus({ baseUrl: args.baseUrl });
    //         if (uploadStatus === "NOT_READY") {
    //           return;
    //         }
    //         clearInterval(interval);
    //         if (uploadStatus === "WAITING") {
    //           await postUpload({ baseUrl: args.baseUrl, workspaceZipBlob: args.workspaceZipBlob });
    //           resolve();
    //         }
    //       } catch (e) {
    //         console.error(e);
    //         reject(e);
    //         clearInterval(interval);
    //       }
    //     }
    //   }, CHECK_UPLOAD_STATUS_POLLING_TIME);
    // });
  }

  // public async deploy(args: DeployArgs): Promise<void> {
  //   const resourceArgs: ResourceArgs = {
  //     namespace: this.args.connection.namespace,
  //     resourceName: this.newResourceName(),
  //     createdBy: RESOURCE_OWNER,
  //   };

  //   const rollbacks = [new DeleteIngress(resourceArgs), new DeleteService(resourceArgs)];
  //   let rollbacksCount = rollbacks.length;

  //   await this.service.withFetch((fetcher: ResourceFetcher) =>
  //     fetcher.execute({
  //       target: new CreateService({ ...resourceArgs, resourceDataSource: ResourceDataSource.TEMPLATE }),
  //     })
  //   );

  //   const getUpdatedRollbacks = () => rollbacks.slice(--rollbacksCount);

  //   const ingress = await this.createIngress(resourceArgs, getUpdatedRollbacks);

  //   const ingressUrl = this.getIngressUrl(ingress);

  //   const deployment = await this.createDeployment({
  //     deployArgs: args,
  //     routeUrl: ingressUrl,
  //     resourceArgs,
  //     rootPath: resourceArgs.resourceName,
  //     getUpdatedRollbacks,
  //   });

  //   this.uploadAssets({ resourceArgs, deployment, workspaceZipBlob: args.workspaceZipBlob, baseUrl: ingressUrl });
  // }

  public async deploy(args: DeployArgs): Promise<void> {
    console.log(
      await this.kubernetesService.applyResourceYamls(
        [createDeploymentYaml, createServiceYaml, createIngressYaml],
        args.tokenMap
      )
    );
    // this.uploadAssets({ resourceArgs, deployment, workspaceZipBlob: args.workspaceZipBlob, baseUrl: ingressUrl });
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
    deployment: K8sResourceYaml,
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
