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
  CreateDeployment,
  DeleteDeployment,
  DeploymentDescriptor,
  DeploymentGroupDescriptor,
  GetDeployment,
  ListDeployments,
} from "@kie-tools-core/kubernetes-bridge/dist/api/kubernetes/Deployment";
import { CreateService, DeleteService } from "@kie-tools-core/kubernetes-bridge/dist/api/kubernetes/Service";
import { ResourceFetcher } from "@kie-tools-core/kubernetes-bridge/dist/fetch/ResourceFetcher";
import { ResourceLabelNames } from "@kie-tools-core/kubernetes-bridge/dist/template/TemplateConstants";
import {
  KubernetesService,
  KubernetesServiceArgs,
} from "@kie-tools-core/kubernetes-bridge/dist/service/KubernetesService";
import { DeployedModel, DeploymentState } from "@kie-tools-core/kubernetes-bridge/dist/service/types";
import { ResourceFetch } from "@kie-tools-core/kubernetes-bridge/dist/fetch/ResourceFetch";
import { ResourceDescriptor } from "@kie-tools-core/kubernetes-bridge/dist/api/types";
import { UploadStatus, getUploadStatus, postUpload } from "../DmnDevDeploymentQuarkusAppApi";

export const RESOURCE_PREFIX = "dmn-dev-deployment";
export const RESOURCE_OWNER = "kie-sandbox";
export const CHECK_UPLOAD_STATUS_POLLING_TIME = 3000;

export type KieSandboxDeployedModel = DeployedModel & {
  uri: string;
  workspaceName: string;
};

export interface DeployArgs {
  targetFilePath: string;
  workspaceName: string;
  workspaceZipBlob: Blob;
  onlineEditorUrl: (baseUrl: string) => string;
}

export type ResourceArgs = {
  namespace: string;
  resourceName: string;
  createdBy: string;
};

export abstract class KieSandboxBaseKubernetesService {
  service: KubernetesService;
  constructor(readonly args: KubernetesServiceArgs) {}

  public async isConnectionEstablished(): Promise<boolean> {
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

  public abstract listRoutes(): Promise<ResourceDescriptor[]>;

  public abstract getRouteUrl(resource: ResourceDescriptor): string;

  public async loadDeployedModels(): Promise<KieSandboxDeployedModel[]> {
    const deployments = await this.listDeployments();

    if (!deployments.length) {
      return [];
    }

    const routes = await this.listRoutes();

    const uploadStatuses = await Promise.all(
      routes
        .map((route) => this.getRouteUrl(route))
        .map(async (url) => ({ url: url, uploadStatus: await getUploadStatus({ baseUrl: url }) }))
    );

    return deployments
      .filter(
        (deployment) =>
          deployment.status &&
          deployment.metadata?.name &&
          deployment.metadata.annotations &&
          deployment.metadata.labels &&
          deployment.metadata.labels[ResourceLabelNames.CREATED_BY] === RESOURCE_OWNER &&
          routes.some((route) => route.metadata?.name === deployment.metadata?.name)
      )
      .map((deployment) => {
        const route = routes.find((route) => route.metadata?.name === deployment.metadata?.name)!;
        const baseUrl = this.getRouteUrl(route);
        const uploadStatus = uploadStatuses.find((status) => status.url === baseUrl)!.uploadStatus;
        return {
          resourceName: deployment.metadata!.name!,
          uri: deployment.metadata!.annotations![ResourceLabelNames.URI],
          routeUrl: baseUrl,
          creationTimestamp: new Date(deployment.metadata?.creationTimestamp ?? Date.now()),
          state: this.extractDeploymentStateWithUploadStatus(deployment, uploadStatus),
          workspaceName: deployment.metadata!.annotations![ResourceLabelNames.WORKSPACE_NAME],
        };
      });
  }

  public abstract getRouteRollback(resourceArgs: ResourceArgs): ResourceFetch;

  public abstract createRoute(
    resourceArgs: ResourceArgs,
    getUpdatedRollbacks: () => ResourceFetch[]
  ): Promise<ResourceDescriptor>;

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
          containerImageUrl: process.env.WEBPACK_REPLACE__dmnDevDeployment_baseImageFullUrl!,
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
          ],
        }),
        rollbacks: getUpdatedRollbacks(),
      })
    );
  }

  public async deploy(args: DeployArgs): Promise<void> {
    const resourceArgs: ResourceArgs = {
      namespace: this.args.connection.namespace,
      resourceName: this.newResourceName(),
      createdBy: RESOURCE_OWNER,
    };

    const rollbacks = [this.getRouteRollback(resourceArgs), new DeleteService(resourceArgs)];
    let rollbacksCount = rollbacks.length;

    await this.service.withFetch((fetcher: ResourceFetcher) =>
      fetcher.execute({ target: new CreateService(resourceArgs) })
    );

    const getUpdatedRollbacks = () => rollbacks.slice(--rollbacksCount);

    const route = await this.createRoute(resourceArgs, getUpdatedRollbacks);

    const routeUrl = this.getRouteUrl(route);

    const deployment = await this.createDeployment(args, routeUrl, resourceArgs, getUpdatedRollbacks);

    new Promise<void>((resolve, reject) => {
      let deploymentState = this.service.extractDeploymentState({ deployment });
      const interval = setInterval(async () => {
        if (deploymentState !== DeploymentState.UP) {
          const deployment = await this.service.withFetch((fetcher: ResourceFetcher) =>
            fetcher.execute<DeploymentDescriptor>({
              target: new GetDeployment(resourceArgs),
            })
          );

          deploymentState = this.service.extractDeploymentState({ deployment });
        } else {
          try {
            const uploadStatus = await getUploadStatus({ baseUrl: routeUrl });
            if (uploadStatus === "NOT_READY") {
              return;
            }
            clearInterval(interval);
            if (uploadStatus === "WAITING") {
              await postUpload({ baseUrl: routeUrl, workspaceZipBlob: args.workspaceZipBlob });
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

  public abstract deleteRoute(resourceName: string): Promise<void>;

  public async deleteDeployment(resourceName: string) {
    await this.service.withFetch((fetcher: ResourceFetcher) =>
      fetcher.execute({
        target: new DeleteDeployment({
          resourceName,
          namespace: this.args.connection.namespace,
        }),
      })
    );

    await this.service.withFetch((fetcher: ResourceFetcher) =>
      fetcher.execute({
        target: new DeleteService({
          resourceName,
          namespace: this.args.connection.namespace,
        }),
      })
    );

    await this.deleteRoute(resourceName);
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
}
