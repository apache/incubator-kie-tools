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
  GetDeployment,
  ListDeployments,
} from "@kie-tools-core/openshift/dist/api/kubernetes/Deployment";
import { CreateIngress, DeleteIngress, ListIngress } from "@kie-tools-core/openshift/dist/api/kubernetes/Ingress";
import { CreateService, DeleteService } from "@kie-tools-core/openshift/dist/api/kubernetes/Service";
import {
  DeploymentDescriptor,
  DeploymentGroupDescriptor,
  IngressDescriptor,
  IngressGroupDescriptor,
  RouteDescriptor,
  RouteGroupDescriptor,
} from "@kie-tools-core/openshift/dist/api/types";
import { OpenShiftService, OpenShiftServiceArgs } from "@kie-tools-core/openshift/dist/service/OpenShiftService";
import { OpenShiftDeployedModel, OpenShiftDeploymentState } from "@kie-tools-core/openshift/dist/service/types";
import { ResourceLabelNames } from "@kie-tools-core/openshift/dist/template/TemplateConstants";
import { getUploadStatus, postUpload, UploadStatus } from "../devDeployments/DmnDevDeploymentQuarkusAppApi";
// import { KubernetesResourceFetcher } from "./KubernetesResourceFetcher";
import { PingCluster } from "./PingResource";
import { KubernetesService } from "@kie-tools-core/openshift/dist/service/KubernetesService";
import { OpenShiftConnection } from "@kie-tools-core/openshift/dist/service/OpenShiftConnection";
import { ResourceFetcher } from "@kie-tools-core/openshift/dist/fetch/ResourceFetcher";

const RESOURCE_PREFIX = "dmn-dev-deployment";
const RESOURCE_OWNER = "kie-sandbox";
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

export type KieSandboxKubernetesServiceArgs = {
  connection: OpenShiftConnection;
  proxyUrl: string;
};

export class KieSandboxKubernetesService {
  fetcher: ResourceFetcher;
  kubernetesService: KubernetesService;
  constructor(private readonly args: KieSandboxKubernetesServiceArgs) {
    this.fetcher = new ResourceFetcher({
      connection: this.args.connection,
    });
    this.kubernetesService = new KubernetesService({ fetcher: this.fetcher, namespace: args.connection.namespace });
  }

  public composeIngressUrl(ingress: IngressDescriptor) {
    return `http://localhost/${ingress.metadata.name}`;
  }

  public async isConnectionEstablished(): Promise<boolean> {
    try {
      await this.fetcher.execute({ target: new PingCluster({ namespace: this.args.connection.namespace }) });

      return true;
    } catch (error) {
      return false;
    }
  }

  public newResourceName(): string {
    const randomPart = Math.random().toString(36).substring(2, 9);
    const milliseconds = new Date().getMilliseconds();
    const suffix = `${randomPart}${milliseconds}`;
    return `${RESOURCE_PREFIX}-${suffix}`;
  }

  public async loadDeployments(): Promise<KieSandboxOpenShiftDeployedModel[]> {
    const deployments = await this.fetcher.execute<DeploymentGroupDescriptor>({
      target: new ListDeployments({
        namespace: this.args.connection.namespace,
        labelSelector: ResourceLabelNames.CREATED_BY,
      }),
    });

    if (deployments.items.length === 0) {
      return [];
    }

    const ingresses = (
      await this.fetcher.execute<IngressGroupDescriptor>({
        target: new ListIngress({
          namespace: this.args.connection.namespace,
        }),
      })
    ).items.filter(
      (ingress) => ingress.metadata.labels && ingress.metadata.labels[ResourceLabelNames.CREATED_BY] === RESOURCE_OWNER
    );

    const uploadStatuses = await Promise.all(
      ingresses
        .map((ingress) => this.composeIngressUrl(ingress))
        .map(async (url) => ({ url: url, uploadStatus: await getUploadStatus({ baseUrl: url }) }))
    );
    return deployments.items
      .filter(
        (deployment) =>
          deployment.status &&
          deployment.metadata.annotations &&
          deployment.metadata.labels &&
          deployment.metadata.labels[ResourceLabelNames.CREATED_BY] === RESOURCE_OWNER &&
          ingresses.some((ingress) => ingress.metadata.name === deployment.metadata.name)
      )
      .map((deployment) => {
        const ingress = ingresses.find((ingress) => ingress.metadata.name === deployment.metadata.name)!;
        const baseUrl = this.composeIngressUrl(ingress);
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

    const rollbacks = [new DeleteIngress(resourceArgs), new DeleteService(resourceArgs)];
    let rollbacksCount = rollbacks.length;

    await this.fetcher.execute({ target: new CreateService(resourceArgs) });

    const ingress = await this.fetcher.execute<IngressDescriptor>({
      target: new CreateIngress(resourceArgs),
      rollbacks: rollbacks.slice(--rollbacksCount),
    });

    const routeUrl = this.composeIngressUrl(ingress);

    const deployment = await this.fetcher.execute<DeploymentDescriptor>({
      target: new CreateDeployment({
        ...resourceArgs,
        uri: args.targetFilePath,
        baseUrl: routeUrl,
        workspaceName: args.workspaceName,
        containerImageUrl:
          "quay.io/thiagoelg/dmn-dev-deployment-base-image@sha256:72106f31afa1c5b07ba2b4a52855b9a7dc31bd882ff37d18f95b24993919a8ad", //process.env.WEBPACK_REPLACE__dmnDevDeployment_baseImageFullUrl!,
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
          {
            name: "ROOT_PATH",
            value: `/${resourceArgs.resourceName}`,
          },
        ],
      }),
      rollbacks: rollbacks.slice(--rollbacksCount),
    });

    new Promise<void>((resolve, reject) => {
      let deploymentState = this.kubernetesService.extractDeploymentState({ deployment });
      const interval = setInterval(async () => {
        if (deploymentState !== OpenShiftDeploymentState.UP) {
          const deployment = await this.fetcher.execute<DeploymentDescriptor>({
            target: new GetDeployment(resourceArgs),
          });

          deploymentState = this.kubernetesService.extractDeploymentState({ deployment });
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

  public async deleteDeployment(resourceName: string) {
    await this.fetcher.execute({
      target: new DeleteDeployment({
        resourceName,
        namespace: this.args.connection.namespace,
      }),
    });

    await this.fetcher.execute({
      target: new DeleteService({
        resourceName,
        namespace: this.args.connection.namespace,
      }),
    });

    await this.fetcher.execute({
      target: new DeleteIngress({
        resourceName,
        namespace: this.args.connection.namespace,
      }),
    });
  }

  private extractDeploymentStateWithUploadStatus(
    deployment: DeploymentDescriptor,
    uploadStatus: UploadStatus
  ): OpenShiftDeploymentState {
    const state = this.kubernetesService.extractDeploymentState({ deployment });

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
