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
  CreateDeployment,
  DeleteDeployment,
  DeploymentDescriptor,
  DeploymentGroupDescriptor,
  GetDeployment,
  ListDeployments,
  CreateService,
  DeleteService,
  DeploymentState,
  ResourceLabelNames,
  DeleteIngress,
  IngressDescriptor,
  CreateIngress,
  ListIngresses,
  IngressGroupDescriptor,
  ResourceDataSource,
  CreateDeploymentTemplateArgs,
} from "@kie-tools-core/kubernetes-bridge/dist/resources";
import { ResourceFetcher, ResourceFetch } from "@kie-tools-core/kubernetes-bridge/dist/fetch";
import {
  KubernetesConnectionStatus,
  KubernetesService,
  KubernetesServiceArgs,
} from "@kie-tools-core/kubernetes-bridge/dist/service";
import { UploadStatus, getUploadStatus, postUpload } from "../DmnDevDeploymentQuarkusAppApi";
import { DeployArgs, KieSandboxDeployedModel, KieSandboxDeploymentService, ResourceArgs } from "./types";

export const RESOURCE_PREFIX = "dmn-dev-deployment";
export const RESOURCE_OWNER = "kie-sandbox";
export const CHECK_UPLOAD_STATUS_POLLING_TIME = 3000;

export class KieSandboxKubernetesService implements KieSandboxDeploymentService {
  service: KubernetesService;
  constructor(readonly args: KubernetesServiceArgs) {
    this.service = new KubernetesService(args);
  }

  public async isConnectionEstablished(): Promise<KubernetesConnectionStatus> {
    return this.service.isConnectionEstablished(this.args.connection);
  }

  public newResourceName(): string {
    return this.service.newResourceName(RESOURCE_PREFIX);
  }

  public async listDeployments(): Promise<DeploymentDescriptor[]> {
    const deployments = await this.service.withFetch((fetcher: ResourceFetcher) =>
      fetcher.execute<DeploymentGroupDescriptor>({
        target: new ListDeployments({
          namespace: this.args.connection.namespace,
          labelSelector: ResourceLabelNames.CREATED_BY,
        }),
      })
    );
    return deployments.items ?? [];
  }

  public async loadDeployedModels(): Promise<KieSandboxDeployedModel[]> {
    const deployments = await this.listDeployments();

    if (!deployments.length) {
      return [];
    }

    const ingresses = await this.listIngresses();

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
          deployment.metadata.labels[ResourceLabelNames.CREATED_BY] === RESOURCE_OWNER &&
          ingresses.some((ingress) => ingress.metadata.name === deployment.metadata.name)
      )
      .map((deployment) => {
        const ingress = ingresses.find((ingress) => ingress.metadata.name === deployment.metadata.name)!;
        const baseUrl = this.getIngressUrl(ingress);
        const uploadStatus = uploadStatuses.find((status) => status.url === baseUrl)!.uploadStatus;
        return {
          resourceName: deployment.metadata.name!,
          uri: deployment.metadata.annotations![ResourceLabelNames.URI],
          routeUrl: baseUrl,
          creationTimestamp: new Date(deployment.metadata.creationTimestamp ?? Date.now()),
          state: this.extractDeploymentStateWithUploadStatus(deployment, uploadStatus),
          workspaceName: deployment.metadata.annotations![ResourceLabelNames.WORKSPACE_NAME],
        };
      });
  }

  public async createDeployment(args: {
    deployArgs: DeployArgs;
    routeUrl: string;
    resourceArgs: ResourceArgs;
    rootPath: string;
    getUpdatedRollbacks: () => ResourceFetch[];
  }): Promise<DeploymentDescriptor> {
    return await this.service.withFetch((fetcher: ResourceFetcher) =>
      fetcher.execute<DeploymentDescriptor>({
        target: new CreateDeployment({
          ...args.resourceArgs,
          uri: args.deployArgs.targetFilePath,
          baseUrl: args.routeUrl,
          workspaceName: args.deployArgs.workspaceName,
          containerImageUrl: args.deployArgs.containerImageUrl,
          envVars: [
            {
              name: "BASE_URL",
              value: args.routeUrl,
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
              value: `/${args.rootPath}`,
            },
          ],
          resourceDataSource: ResourceDataSource.TEMPLATE,
          imagePullPolicy: process.env
            .WEBPACK_REPLACE__dmnDevDeployment_imagePullPolicy! as CreateDeploymentTemplateArgs["imagePullPolicy"],
        }),
        rollbacks: args.getUpdatedRollbacks(),
      })
    );
  }

  public async uploadAssets(args: {
    resourceArgs: ResourceArgs;
    deployment: DeploymentDescriptor;
    workspaceZipBlob: Blob;
    baseUrl: string;
  }) {
    return new Promise<void>((resolve, reject) => {
      let deploymentState = this.service.extractDeploymentState({ deployment: args.deployment });
      const interval = setInterval(async () => {
        if (deploymentState !== DeploymentState.UP) {
          const deployment = await this.service.withFetch((fetcher: ResourceFetcher) =>
            fetcher.execute<DeploymentDescriptor>({
              target: new GetDeployment(args.resourceArgs),
            })
          );

          deploymentState = this.service.extractDeploymentState({ deployment });
        } else {
          try {
            const uploadStatus = await getUploadStatus({ baseUrl: args.baseUrl });
            if (uploadStatus === "NOT_READY") {
              return;
            }
            clearInterval(interval);
            if (uploadStatus === "WAITING") {
              await postUpload({ baseUrl: args.baseUrl, workspaceZipBlob: args.workspaceZipBlob });
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
    const resourceArgs: ResourceArgs = {
      namespace: this.args.connection.namespace,
      resourceName: this.newResourceName(),
      createdBy: RESOURCE_OWNER,
    };

    const rollbacks = [new DeleteIngress(resourceArgs), new DeleteService(resourceArgs)];
    let rollbacksCount = rollbacks.length;

    await this.service.withFetch((fetcher: ResourceFetcher) =>
      fetcher.execute({
        target: new CreateService({ ...resourceArgs, resourceDataSource: ResourceDataSource.TEMPLATE }),
      })
    );

    const getUpdatedRollbacks = () => rollbacks.slice(--rollbacksCount);

    const ingress = await this.createIngress(resourceArgs, getUpdatedRollbacks);

    const ingressUrl = this.getIngressUrl(ingress);

    const deployment = await this.createDeployment({
      deployArgs: args,
      routeUrl: ingressUrl,
      resourceArgs,
      rootPath: resourceArgs.resourceName,
      getUpdatedRollbacks,
    });

    this.uploadAssets({ resourceArgs, deployment, workspaceZipBlob: args.workspaceZipBlob, baseUrl: ingressUrl });
  }

  public async deleteDeployment(resourceName: string) {
    await this.service.withFetch((fetcher: ResourceFetcher) =>
      fetcher.execute({
        target: new DeleteDeployment({
          resourceName,
          namespace: this.args.connection.namespace,
        }),
      })
    );
  }
  public async deleteService(resourceName: string) {
    await this.service.withFetch((fetcher: ResourceFetcher) =>
      fetcher.execute({
        target: new DeleteService({
          resourceName,
          namespace: this.args.connection.namespace,
        }),
      })
    );
  }

  public async deleteDevDeployment(resourceName: string): Promise<void> {
    await this.deleteDeployment(resourceName);
    await this.deleteService(resourceName);
    await this.deleteIngress(resourceName);
  }

  public extractDeploymentStateWithUploadStatus(
    deployment: DeploymentDescriptor,
    uploadStatus: UploadStatus
  ): DeploymentState {
    const state = this.service.extractDeploymentState({ deployment });

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

  getIngressUrl(resource: IngressDescriptor): string {
    return this.service.composeDeploymentUrlFromIngress(resource);
  }

  async createIngress(
    resourceArgs: ResourceArgs,
    getUpdatedRollbacks: () => ResourceFetch[]
  ): Promise<IngressDescriptor> {
    const route = await this.service.withFetch((fetcher: ResourceFetcher) =>
      fetcher.execute<IngressDescriptor>({
        target: new CreateIngress({ ...resourceArgs, resourceDataSource: ResourceDataSource.TEMPLATE }),
        rollbacks: getUpdatedRollbacks(),
      })
    );
    return route;
  }

  async listIngresses(): Promise<IngressDescriptor[]> {
    const ingresses = (
      await this.service.withFetch((fetcher: ResourceFetcher) =>
        fetcher.execute<IngressGroupDescriptor>({
          target: new ListIngresses({
            namespace: this.args.connection.namespace,
          }),
        })
      )
    ).items.filter(
      (route) => route.metadata.labels && route.metadata.labels[ResourceLabelNames.CREATED_BY] === RESOURCE_OWNER
    );

    return ingresses;
  }

  async deleteIngress(resourceName: string) {
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
