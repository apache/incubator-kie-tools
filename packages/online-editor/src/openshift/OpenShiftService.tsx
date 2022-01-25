/*
 * Copyright 2021 Red Hat, Inc. and/or its affiliates.
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

import { OpenShiftDeployedModel, OpenShiftDeployedModelState } from "./OpenShiftDeployedModel";
import {
  CreateDeployment,
  Deployment,
  DeploymentCondition,
  Deployments,
  GetDeployment,
  ListDeployments,
} from "./resources/Deployment";
import { GetProject } from "./resources/Project";
import { KOGITO_CREATED_BY, KOGITO_URI, KOGITO_WORKSPACE_NAME, Resource, ResourceFetch } from "./resources/Resource";
import { CreateRoute, DeleteRoute, ListRoutes, Route, Routes } from "./resources/Route";
import { CreateService, DeleteService } from "./resources/Service";
import { isConfigValid, OpenShiftSettingsConfig } from "./OpenShiftSettingsConfig";
import { getUploadStatus, postUpload, UploadStatus } from "../editor/DmnDevSandbox/DmnDevSandboxQuarkusAppApi";

export const DEVELOPER_SANDBOX_URL = "https://developers.redhat.com/developer-sandbox";
export const DEVELOPER_SANDBOX_GET_STARTED_URL = "https://developers.redhat.com/developer-sandbox/get-started";
export const DEFAULT_CREATED_BY = "online-editor";

const CHECK_UPLOAD_STATUS_POLLING_TIME = 3000;

export class OpenShiftService {
  private readonly RESOURCE_NAME_PREFIX = "dmn-dev-sandbox";

  public constructor(private readonly proxyUrl: string) {}

  public async isConnectionEstablished(config: OpenShiftSettingsConfig): Promise<boolean> {
    try {
      await this.fetchResource(
        new GetProject({
          host: config.host,
          namespace: config.namespace,
          token: config.token,
        })
      );

      return true;
    } catch (error) {
      return false;
    }
  }

  public async onCheckConfig(config: OpenShiftSettingsConfig) {
    return isConfigValid(config) && (await this.isConnectionEstablished(config));
  }

  public async loadDeployments(config: OpenShiftSettingsConfig): Promise<OpenShiftDeployedModel[]> {
    const commonArgs = {
      host: config.host,
      namespace: config.namespace,
      token: config.token,
      createdBy: DEFAULT_CREATED_BY,
    };

    const deployments = await this.fetchResource<Deployments>(new ListDeployments(commonArgs));

    if (deployments.items.length === 0) {
      return [];
    }

    const routes = (await this.fetchResource<Routes>(new ListRoutes(commonArgs))).items.filter(
      (route: Route) => route.metadata.labels[KOGITO_CREATED_BY] === DEFAULT_CREATED_BY
    );

    const uploadStatuses = await Promise.all(
      routes
        .map((route) => this.composeBaseUrl(route))
        .map(async (url) => ({ url: url, uploadStatus: await getUploadStatus({ baseUrl: url }) }))
    );

    return deployments.items
      .filter(
        (deployment: Deployment) =>
          KOGITO_CREATED_BY in deployment.metadata.labels &&
          deployment.metadata.labels[KOGITO_CREATED_BY] === DEFAULT_CREATED_BY &&
          routes.some((route: Route) => route.metadata.name === deployment.metadata.name)
      )
      .map((deployment: Deployment) => {
        const route = routes.find((route: Route) => route.metadata.name === deployment.metadata.name)!;
        const baseUrl = this.composeBaseUrl(route);
        const uploadStatus = uploadStatuses.find((status) => status.url === baseUrl)!.uploadStatus;
        return {
          resourceName: deployment.metadata.name,
          uri: deployment.metadata.annotations[KOGITO_URI],
          baseUrl: baseUrl,
          creationTimestamp: new Date(deployment.metadata.creationTimestamp),
          state: this.extractDeploymentStateWithUploadStatus(deployment, uploadStatus),
          workspaceName: deployment.metadata.annotations[KOGITO_WORKSPACE_NAME],
        };
      });
  }

  public async deploy(args: {
    targetFilePath: string;
    workspaceName: string;
    workspaceZipBlob: Blob;
    config: OpenShiftSettingsConfig;
    onlineEditorUrl: (baseUrl: string) => string;
  }): Promise<void> {
    const commonArgs = {
      host: args.config.host,
      namespace: args.config.namespace,
      token: args.config.token,
      resourceName: `${this.RESOURCE_NAME_PREFIX}-${this.generateRandomId()}`,
      createdBy: DEFAULT_CREATED_BY,
    };

    const rollbacks = [new DeleteRoute(commonArgs), new DeleteService(commonArgs)];

    await this.fetchResource(new CreateService(commonArgs), rollbacks.slice(1));
    const route = await this.fetchResource<Route>(new CreateRoute(commonArgs), rollbacks.slice(2));
    const baseUrl = this.composeBaseUrl(route);

    const deployment = await this.fetchResource<Deployment>(
      new CreateDeployment({
        ...commonArgs,
        uri: args.targetFilePath,
        baseUrl: baseUrl,
        workspaceName: args.workspaceName,
      }),
      rollbacks
    );

    new Promise<void>((resolve, reject) => {
      let deploymentState = this.extractDeploymentState(deployment);
      const interval = setInterval(async () => {
        if (deploymentState !== OpenShiftDeployedModelState.UP) {
          const deployment = await this.fetchResource<Deployment>(new GetDeployment(commonArgs));
          deploymentState = this.extractDeploymentState(deployment);
        }

        if (deploymentState === OpenShiftDeployedModelState.UP) {
          try {
            const uploadStatus = await getUploadStatus({ baseUrl: baseUrl });
            if (uploadStatus === "NOT_READY") {
              return;
            }

            clearInterval(interval);

            if (uploadStatus === "WAITING") {
              await postUpload({ baseUrl: baseUrl, workspaceZipBlob: args.workspaceZipBlob });
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

  public async fetchResource<T = Resource>(target: ResourceFetch, rollbacks?: ResourceFetch[]): Promise<Readonly<T>> {
    const response = await fetch(this.proxyUrl, await target.requestInit());

    if (!response.ok) {
      if (rollbacks && rollbacks.length > 0) {
        for (const resource of rollbacks) {
          await this.fetchResource(resource);
        }
      }

      throw new Error(`Error fetching ${target.name()}`);
    }

    return (await response.json()) as T;
  }

  private generateRandomId(): string {
    const randomPart = Math.random().toString(36).substr(2, 9);
    const milliseconds = new Date().getMilliseconds();
    return `${randomPart}${milliseconds}`;
  }

  private composeBaseUrl(route: Route): string {
    return `https://${route.spec.host}`;
  }

  private extractDeploymentState(deployment: Deployment): OpenShiftDeployedModelState {
    if (!deployment.status.replicas || +deployment.status.replicas === 0) {
      return OpenShiftDeployedModelState.DOWN;
    }

    const progressingCondition = deployment.status.conditions?.find(
      (condition: DeploymentCondition) => condition.type === "Progressing"
    );

    if (!progressingCondition || progressingCondition.status !== "True") {
      return OpenShiftDeployedModelState.DOWN;
    }

    if (!deployment.status.readyReplicas || +deployment.status.readyReplicas === 0) {
      return OpenShiftDeployedModelState.IN_PROGRESS;
    }

    return OpenShiftDeployedModelState.UP;
  }

  private extractDeploymentStateWithUploadStatus(
    deployment: Deployment,
    uploadStatus: UploadStatus
  ): OpenShiftDeployedModelState {
    const state = this.extractDeploymentState(deployment);

    if (state !== OpenShiftDeployedModelState.UP) {
      return state;
    }

    if (uploadStatus === "ERROR") {
      return OpenShiftDeployedModelState.ERROR;
    }

    if (uploadStatus !== "UPLOADED") {
      return OpenShiftDeployedModelState.IN_PROGRESS;
    }

    return OpenShiftDeployedModelState.UP;
  }
}
