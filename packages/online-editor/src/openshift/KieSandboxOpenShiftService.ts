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
  GetDeployment,
  ListDeployments,
} from "@kie-tools-core/openshift/dist/api/kubernetes/Deployment";
import { CreateRoute, DeleteRoute, ListRoutes } from "@kie-tools-core/openshift/dist/api/kubernetes/Route";
import { CreateService, DeleteService } from "@kie-tools-core/openshift/dist/api/kubernetes/Service";
import {
  DeploymentDescriptor,
  DeploymentGroupDescriptor,
  RouteDescriptor,
  RouteGroupDescriptor,
} from "@kie-tools-core/openshift/dist/api/types";
import { ResourceFetcher } from "@kie-tools-core/openshift/dist/fetch/ResourceFetcher";
import { OpenShiftConnection } from "@kie-tools-core/openshift/dist/service/OpenShiftConnection";
import { OpenShiftService, OpenShiftServiceArgs } from "@kie-tools-core/openshift/dist/service/OpenShiftService";
import { OpenShiftDeployedModel, OpenShiftDeploymentState } from "@kie-tools-core/openshift/dist/service/types";
import { ResourceLabelNames } from "@kie-tools-core/openshift/dist/template/TemplateConstants";
import { getUploadStatus, postUpload, UploadStatus } from "../editor/DmnDevSandbox/DmnDevSandboxQuarkusAppApi";

const RESOURCE_PREFIX = "dmn-dev-sandbox";
const RESOURCE_OWNER = "online-editor";
const CHECK_UPLOAD_STATUS_POLLING_TIME = 3000;

export type KieSandboxOpenShiftDeployedModel = OpenShiftDeployedModel & {
  uri: string;
  workspaceName: string;
};

interface DeployArgs {
  targetFilePath: string;
  workspaceName: string;
  workspaceZipBlob: Blob;
  onlineEditorUrl: (baseUrl: string) => string;
}

export class KieSandboxOpenShiftService {
  private readonly openShiftService: OpenShiftService;
  constructor(private readonly args: OpenShiftServiceArgs) {
    this.openShiftService = new OpenShiftService(args);
  }

  public async isConnectionEstablished(connection: OpenShiftConnection): Promise<boolean> {
    return this.openShiftService.isConnectionEstablished(connection);
  }

  public newResourceName(): string {
    return this.openShiftService.newResourceName(RESOURCE_PREFIX);
  }

  public async loadDeployments(): Promise<KieSandboxOpenShiftDeployedModel[]> {
    const deployments = await this.openShiftService.withFetch((fetcher: ResourceFetcher) =>
      fetcher.execute<DeploymentGroupDescriptor>({
        target: new ListDeployments({
          namespace: this.args.connection.namespace,
          labelSelector: ResourceLabelNames.CREATED_BY,
        }),
      })
    );

    if (deployments.items.length === 0) {
      return [];
    }

    const routes = (
      await this.openShiftService.withFetch((fetcher: ResourceFetcher) =>
        fetcher.execute<RouteGroupDescriptor>({
          target: new ListRoutes({
            namespace: this.args.connection.namespace,
          }),
        })
      )
    ).items.filter(
      (route) => route.metadata.labels && route.metadata.labels[ResourceLabelNames.CREATED_BY] === RESOURCE_OWNER
    );

    const uploadStatuses = await Promise.all(
      routes
        .map((route) => this.openShiftService.kubernetes.composeRouteUrl(route))
        .map(async (url) => ({ url: url, uploadStatus: await getUploadStatus({ baseUrl: url }) }))
    );
    return deployments.items
      .filter(
        (deployment) =>
          deployment.status &&
          deployment.metadata.annotations &&
          deployment.metadata.labels &&
          deployment.metadata.labels[ResourceLabelNames.CREATED_BY] === RESOURCE_OWNER &&
          routes.some((route) => route.metadata.name === deployment.metadata.name)
      )
      .map((deployment) => {
        const route = routes.find((route) => route.metadata.name === deployment.metadata.name)!;
        const baseUrl = this.openShiftService.kubernetes.composeRouteUrl(route);
        const uploadStatus = uploadStatuses.find((status) => status.url === baseUrl)!.uploadStatus;
        return {
          resourceName: deployment.metadata.name,
          uri: deployment.metadata.annotations![ResourceLabelNames.URI],
          routeUrl: baseUrl,
          creationTimestamp: new Date(deployment.metadata.creationTimestamp ?? Date.now()),
          state: this.extractDeploymentStateWithUploadStatus(deployment, uploadStatus),
          workspaceName: deployment.metadata.annotations![ResourceLabelNames.WORKSPACE_NAME],
        };
      });
  }

  public async deploy(args: DeployArgs): Promise<void> {
    const resourceArgs = {
      namespace: this.args.connection.namespace,
      resourceName: this.newResourceName(),
      createdBy: RESOURCE_OWNER,
    };

    const rollbacks = [new DeleteRoute(resourceArgs), new DeleteService(resourceArgs)];
    let rollbacksCount = rollbacks.length;

    await this.openShiftService.withFetch((fetcher: ResourceFetcher) =>
      fetcher.execute({ target: new CreateService(resourceArgs) })
    );

    const route = await this.openShiftService.withFetch((fetcher: ResourceFetcher) =>
      fetcher.execute<RouteDescriptor>({
        target: new CreateRoute(resourceArgs),
        rollbacks: rollbacks.slice(--rollbacksCount),
      })
    );

    const routeUrl = this.openShiftService.kubernetes.composeRouteUrl(route);

    const deployment = await this.openShiftService.withFetch((fetcher: ResourceFetcher) =>
      fetcher.execute<DeploymentDescriptor>({
        target: new CreateDeployment({
          ...resourceArgs,
          uri: args.targetFilePath,
          baseUrl: routeUrl,
          workspaceName: args.workspaceName,
          containerImageUrl: process.env.WEBPACK_REPLACE__dmnDevSandbox_baseImageFullUrl!,
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
        rollbacks: rollbacks.slice(--rollbacksCount),
      })
    );

    new Promise<void>((resolve, reject) => {
      let deploymentState = this.openShiftService.kubernetes.extractDeploymentState({ deployment });
      const interval = setInterval(async () => {
        if (deploymentState !== OpenShiftDeploymentState.UP) {
          const deployment = await this.openShiftService.withFetch((fetcher: ResourceFetcher) =>
            fetcher.execute<DeploymentDescriptor>({
              target: new GetDeployment(resourceArgs),
            })
          );

          deploymentState = this.openShiftService.kubernetes.extractDeploymentState({ deployment });
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

  private extractDeploymentStateWithUploadStatus(
    deployment: DeploymentDescriptor,
    uploadStatus: UploadStatus
  ): OpenShiftDeploymentState {
    const state = this.openShiftService.kubernetes.extractDeploymentState({ deployment });

    if (state !== OpenShiftDeploymentState.UP) {
      return state;
    }

    if (uploadStatus === "ERROR") {
      return OpenShiftDeploymentState.ERROR;
    }

    if (uploadStatus !== "UPLOADED") {
      return OpenShiftDeploymentState.IN_PROGRESS;
    }

    return OpenShiftDeploymentState.UP;
  }
}
