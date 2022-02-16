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

import { DeploymentWorkflow } from "./OpenShiftContext";
import { OpenShiftSettingsConfig } from "./OpenShiftSettingsConfig";
import { CreateBuild, DeleteBuild } from "./resources/Build";
import { CreateBuildConfig, DeleteBuildConfig } from "./resources/BuildConfig";
import { CreateDeployment, Deployment, Deployments, ListDeployments } from "./resources/Deployment";
import { CreateImageStream, DeleteImageStream } from "./resources/ImageStream";
import { KOGITO_CREATED_BY, KOGITO_WORKFLOW_FILE, Resource, ResourceFetch } from "./resources/Resource";
import { CreateRoute, DeleteRoute, GetRoute, ListRoutes, Route, Routes } from "./resources/Route";
import { CreateService, DeleteService } from "./resources/Service";

export const DEFAULT_CREATED_BY = "kie-tools-chrome-extension";

export class OpenShiftService {
  public async getWorkflowFileName(args: {
    config: OpenShiftSettingsConfig;
    resourceName: string;
  }): Promise<string | undefined> {
    const commonArgs = {
      host: args.config.host,
      namespace: args.config.namespace,
      token: args.config.token,
      createdBy: DEFAULT_CREATED_BY,
    };

    const deployments = await this.fetchResource<Deployments>(args.config.proxy, new ListDeployments(commonArgs));

    if (deployments.items.length === 0) {
      return;
    }

    const deployment = deployments.items.find((item) => item.metadata.name === args.resourceName);

    if (!deployment) {
      return;
    }

    return deployment.metadata.annotations[KOGITO_WORKFLOW_FILE];
  }

  public async getDeploymentRoute(args: {
    config: OpenShiftSettingsConfig;
    resourceName: string;
  }): Promise<string | undefined> {
    const commonArgs = {
      host: args.config.host,
      namespace: args.config.namespace,
      token: args.config.token,
      createdBy: DEFAULT_CREATED_BY,
    };

    const route = await this.fetchResource<Route>(
      args.config.proxy,
      new GetRoute({ ...commonArgs, resourceName: args.resourceName })
    );

    return this.composeBaseUrl(route);
  }

  public async getResourceRouteMap(config: OpenShiftSettingsConfig): Promise<Map<string, string>> {
    const commonArgs = {
      host: config.host,
      namespace: config.namespace,
      token: config.token,
      createdBy: DEFAULT_CREATED_BY,
    };

    const deployments = await this.fetchResource<Deployments>(config.proxy, new ListDeployments(commonArgs));

    if (deployments.items.length === 0) {
      return new Map();
    }

    const routes = (await this.fetchResource<Routes>(config.proxy, new ListRoutes(commonArgs))).items.filter(
      (route: Route) => route.metadata.labels[KOGITO_CREATED_BY] === DEFAULT_CREATED_BY
    );

    return new Map(
      deployments.items
        .filter(
          (deployment: Deployment) =>
            KOGITO_CREATED_BY in deployment.metadata.labels &&
            deployment.metadata.labels[KOGITO_CREATED_BY] === DEFAULT_CREATED_BY &&
            routes.some((route: Route) => route.metadata.name === deployment.metadata.name)
        )
        .map((deployment: Deployment) => {
          const route = routes.find((route: Route) => route.metadata.name === deployment.metadata.name)!;
          return [deployment.metadata.name, this.composeBaseUrl(route)];
        })
    );
  }

  private composeBaseUrl(route: Route): string {
    return `https://${route.spec.host}`;
  }

  public async deploy(args: { config: OpenShiftSettingsConfig; workflow: DeploymentWorkflow }): Promise<void> {
    const resourceName = `swf-${this.generateRandomId()}`;

    const commonArgs = {
      host: args.config.host,
      namespace: args.config.namespace,
      token: args.config.token,
      resourceName: resourceName,
      createdBy: DEFAULT_CREATED_BY,
    };

    const rollbacks = [
      new DeleteBuild(commonArgs),
      new DeleteBuildConfig(commonArgs),
      new DeleteRoute(commonArgs),
      new DeleteService(commonArgs),
      new DeleteImageStream(commonArgs),
    ];

    await this.fetchResource(args.config.proxy, new CreateImageStream(commonArgs));
    await this.fetchResource(args.config.proxy, new CreateService(commonArgs), rollbacks.slice(4));
    await this.fetchResource(args.config.proxy, new CreateRoute(commonArgs), rollbacks.slice(3));

    const buildConfig = await this.fetchResource(
      args.config.proxy,
      new CreateBuildConfig(commonArgs),
      rollbacks.slice(2)
    );

    const processedFileContent = args.workflow.content
      .replace(/(\r\n|\n|\r)/gm, "") // Remove line breaks
      .replace(/"/g, '\\"') // Escape double quotes
      .replace(/'/g, "\\x27"); // Escape single quotes

    await this.fetchResource(
      args.config.proxy,
      new CreateBuild({
        ...commonArgs,
        buildConfigUid: buildConfig.metadata.uid,
        file: {
          name: args.workflow.name,
          content: processedFileContent,
        },
      }),
      rollbacks.slice(1)
    );

    await this.fetchResource(
      args.config.proxy,
      new CreateDeployment({ ...commonArgs, fileName: args.workflow.name }),
      rollbacks
    );
  }

  public async fetchResource<T = Resource>(
    proxyUrl: string,
    target: ResourceFetch,
    rollbacks?: ResourceFetch[]
  ): Promise<Readonly<T>> {
    const response = await fetch(proxyUrl + "/devsandbox", await target.requestInit());

    if (!response.ok) {
      if (rollbacks && rollbacks.length > 0) {
        for (const resource of rollbacks) {
          await this.fetchResource(proxyUrl, resource);
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
}
