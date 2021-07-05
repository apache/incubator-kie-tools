/*
 * Copyright 2021 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import { ConnectionConfig } from "../ConnectionConfig";
import { DeployedModel, DeployedModelState } from "../DeployedModel";
import { Build, Builds, CreateBuild, DeleteBuild, ListBuilds } from "./resources/Build";
import { CreateBuildConfig, DeleteBuildConfig } from "./resources/BuildConfig";
import {
  CreateDeployment,
  Deployment,
  DeploymentCondition,
  Deployments,
  ListDeployments,
} from "./resources/Deployment";
import { CreateImageStream, DeleteImageStream } from "./resources/ImageStream";
import { GetProject } from "./resources/Project";
import { KOGITO_CREATED_BY, KOGITO_FILENAME, Resource, ResourceFetch } from "./resources/Resource";
import { CreateRoute, DeleteRoute } from "./resources/Route";
import { CreateService, DeleteService } from "./resources/Service";

export const DEVELOPER_SANDBOX_URL = "https://developers.redhat.com/developer-sandbox";
export const DEVELOPER_SANDBOX_GET_STARTED_URL = "https://developers.redhat.com/developer-sandbox/get-started";

export class DeveloperSandboxService {
  private readonly RESOURCE_NAME_PREFIX = "dmn-dev-sandbox";
  private readonly NAMESPACE_SUFFIX = "-dev";

  public constructor(private readonly createdBy: string, private readonly proxyUrl: string) {}

  public async isConnectionEstablished(config: ConnectionConfig): Promise<boolean> {
    try {
      await this.fetchResource(
        new GetProject({
          host: config.host,
          namespace: this.composeNamespace(config.username),
          token: config.token,
        })
      );

      return true;
    } catch (e) {
      return false;
    }
  }

  public async loadDeployments(config: ConnectionConfig): Promise<DeployedModel[]> {
    const commonArgs = {
      host: config.host,
      namespace: this.composeNamespace(config.username),
      token: config.token,
    };

    const deployments = await this.fetchResource<Deployments>(new ListDeployments(commonArgs));
    const builds = await this.fetchResource<Builds>(new ListBuilds(commonArgs));

    return deployments.items
      .filter(
        (deployment: Deployment) =>
          KOGITO_CREATED_BY in deployment.metadata.labels &&
          deployment.metadata.labels[KOGITO_CREATED_BY] === this.createdBy
      )
      .map((deployment: Deployment) => {
        const build = builds.items.find((build: Build) => build.metadata.name === deployment.metadata.name);
        const baseUrl = this.composeBaseUrl(commonArgs.host, commonArgs.namespace, deployment.metadata.name);
        return {
          resourceName: deployment.metadata.name,
          filename: deployment.metadata.annotations[KOGITO_FILENAME],
          urls: {
            index: baseUrl,
            console: this.composeConsoleUrl(config.host, commonArgs.namespace, deployment.metadata.uid),
            swaggerUI: this.composeSwaggerUIUrl(baseUrl),
          },
          creationTimestamp: new Date(deployment.metadata.creationTimestamp),
          state: this.extractDeploymentState(deployment, build),
        };
      });
  }

  public async deploy(filename: string, diagramContent: string, config: ConnectionConfig): Promise<void> {
    const commonArgs = {
      host: config.host,
      namespace: this.composeNamespace(config.username),
      token: config.token,
      resourceName: `${this.RESOURCE_NAME_PREFIX}-${this.generateRandomId()}`,
    };

    const rollbacks = [
      new DeleteBuild(commonArgs),
      new DeleteBuildConfig(commonArgs),
      new DeleteRoute(commonArgs),
      new DeleteService(commonArgs),
      new DeleteImageStream(commonArgs),
    ];

    const baseUrl = this.composeBaseUrl(commonArgs.host, commonArgs.namespace, commonArgs.resourceName);

    await this.fetchResource(new CreateImageStream(commonArgs));
    await this.fetchResource(new CreateService(commonArgs), rollbacks.slice(4));
    await this.fetchResource(new CreateRoute(commonArgs), rollbacks.slice(3));

    const buildConfig = await this.fetchResource(new CreateBuildConfig(commonArgs), rollbacks.slice(2));

    await this.fetchResource(
      new CreateBuild({
        ...commonArgs,
        buildConfigUid: buildConfig.metadata.uid,
        model: {
          filename: filename,
          content: diagramContent,
        },
        urls: {
          index: baseUrl,
          swaggerUI: this.composeSwaggerUIUrl(baseUrl),
          onlineEditor: this.composeOnlineEditorUrl(baseUrl, filename),
        },
      }),
      rollbacks.slice(1)
    );

    await this.fetchResource(
      new CreateDeployment({ ...commonArgs, filename: filename, createdBy: this.createdBy }),
      rollbacks
    );
  }

  private async fetchResource<T = Resource>(target: ResourceFetch, rollbacks?: ResourceFetch[]): Promise<Readonly<T>> {
    const response = await fetch(this.proxyUrl, target.requestInit());

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

  private composeNamespace(username: string): string {
    return `${username}${this.NAMESPACE_SUFFIX}`;
  }

  private composeBaseUrl(host: string, namespace: string, resourceName: string): string {
    return `https://${resourceName}-${namespace}.apps.${new URL(host).hostname.replace("api.", "")}`;
  }

  private composeConsoleUrl(host: string, namespace: string, deploymentUid: string): string {
    return `https://console-openshift-console.apps.${new URL(host).hostname.replace(
      "api.",
      ""
    )}/topology/ns/${namespace}?view=graph&selectId=${deploymentUid}`;
  }

  private composeSwaggerUIUrl(baseUrl: string): string {
    return `${baseUrl}/q/swagger-ui`;
  }

  private composeOnlineEditorUrl(baseUrl: string, filename: string): string {
    return `https://kiegroup.github.io/kogito-online/?file=${baseUrl}/${encodeURIComponent(filename)}#/editor/dmn`;
  }

  private extractDeploymentState(deployment: Deployment, build: Build | undefined): DeployedModelState {
    if (!build) {
      return DeployedModelState.DOWN;
    }

    if (["New", "Pending"].includes(build.status.phase)) {
      return DeployedModelState.PREPARING;
    }

    if (["Failed", "Error", "Cancelled"].includes(build.status.phase)) {
      return DeployedModelState.DOWN;
    }

    if (build.status.phase === "Running") {
      return DeployedModelState.IN_PROGRESS;
    }

    if (!deployment.status.replicas || +deployment.status.replicas === 0) {
      return DeployedModelState.DOWN;
    }

    const progressingCondition = deployment.status.conditions?.find(
      (condition: DeploymentCondition) => condition.type === "Progressing"
    );

    if (!progressingCondition || progressingCondition.status !== "True") {
      return DeployedModelState.DOWN;
    }

    if (!deployment.status.readyReplicas || +deployment.status.readyReplicas === 0) {
      return DeployedModelState.IN_PROGRESS;
    }

    return DeployedModelState.UP;
  }
}
