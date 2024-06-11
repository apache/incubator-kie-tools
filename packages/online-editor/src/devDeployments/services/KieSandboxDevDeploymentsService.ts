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

import { K8sResourceYaml, TokenMap } from "@kie-tools-core/k8s-yaml-to-apiserver-requests/dist";
import { CloudAuthSessionType } from "../../authSessions/AuthSessionApi";
import {
  DeploymentResource,
  KubernetesConnectionStatus,
  KubernetesService,
  KubernetesServiceArgs,
  ServiceResource,
  kubernetesResourcesApi,
} from "./KubernetesService";
import { KieSandboxDeployment, Tokens, defaultLabelTokens } from "./types";
import { DeploymentState, HealthStatus } from "./common";
import { UploadStatus, getUploadStatus, postUpload } from "./KieSandboxDevDeploymentsUploadAppApi";
import { DeploymentOption } from "./deploymentOptions/types";

export interface DeployArgs {
  workspaceZipBlob: Blob;
  tokenMap: { devDeployment: Tokens };
  parametersTokenMap: { parameters: TokenMap };
  deploymentOption: DeploymentOption;
}

export type KieSandboxDevDeploymentsServiceProps = {
  id: string;
  type: CloudAuthSessionType;
  args: KubernetesServiceArgs;
};

export type KieSandboxDevDeploymentsServiceType = KieSandboxDevDeploymentsServiceProps & {
  isConnectionEstablished(): Promise<KubernetesConnectionStatus>;
  loadDevDeployments(): Promise<KieSandboxDeployment[]>;
  deploy(args: DeployArgs): Promise<void>;
  deleteDevDeployment(resources: K8sResourceYaml[]): Promise<void>;
  uploadAssets(args: { deployment: K8sResourceYaml; workspaceZipBlob: Blob; baseUrl: string }): Promise<void>;
  getHealthStatus(args: { endpoint: string; deploymentName: string }): Promise<HealthStatus>;
  extractDevDeploymentState(args: { deployment: DeploymentResource }): DeploymentState;
  newResourceName(): string;
};

export const RESOURCE_PREFIX = "dev-deployment";
export const CHECK_UPLOAD_STATUS_POLLING_TIME = 3000;
export const LIVENESS_TIMEOUT = 15000;

const deploymentUploadStatusMap = new Map<string, { status: UploadStatus; timestamp?: number }>();

export abstract class KieSandboxDevDeploymentsService implements KieSandboxDevDeploymentsServiceType {
  id: string;
  type: CloudAuthSessionType.None;

  constructor(readonly args: KubernetesServiceArgs) {}

  get kubernetesService() {
    return new KubernetesService(this.args);
  }

  abstract isConnectionEstablished(): Promise<KubernetesConnectionStatus>;

  abstract loadDevDeployments(): Promise<KieSandboxDeployment[]>;

  abstract deploy(args: DeployArgs): Promise<void>;

  public async getHealthStatus(args: { endpoint: string; deploymentName: string }): Promise<HealthStatus> {
    return await fetch(`${args.endpoint}/q/health`)
      .then((data) => data.json())
      .then((response) => {
        // If the app is up, no need for the upload status anymore.
        deploymentUploadStatusMap.delete(args.deploymentName);
        return response.status as HealthStatus;
      })
      .catch((e) => {
        return HealthStatus.ERROR;
      });
  }

  public extractDevDeploymentState(args: { deployment: DeploymentResource }): DeploymentState {
    if (!args.deployment || !args.deployment.status) {
      // Deployment still being created
      return DeploymentState.IN_PROGRESS;
    }

    if (!args.deployment.status.replicas) {
      // Deployment with no replicas is down
      return DeploymentState.DOWN;
    }

    const progressingCondition = args.deployment.status.conditions?.find(
      (condition: any) => condition.type === "Progressing"
    );

    if (!progressingCondition || progressingCondition.status !== "True") {
      // Without `Progressing` condition, the deployment will never be up
      return DeploymentState.DOWN;
    }

    if (!args.deployment.status.readyReplicas) {
      // Deployment is progressing but no replicas are ready yet
      return DeploymentState.IN_PROGRESS;
    }

    return DeploymentState.UP;
  }

  public extractDeploymentStateWithHealthStatus(deployment: DeploymentResource, healtStatus: string): DeploymentState {
    const state = this.extractDevDeploymentState({ deployment });

    if (state !== DeploymentState.UP) {
      return state;
    }

    // Check if it's uploading or about to.
    // If it has been uploaded check the timestamp to see if enough time has passed.
    const uploadStatus = deploymentUploadStatusMap.get(deployment.metadata.name);
    if (
      uploadStatus &&
      ([UploadStatus.UPLOADING, UploadStatus.NOT_READY, UploadStatus.READY].includes(uploadStatus.status) ||
        (uploadStatus.timestamp &&
          uploadStatus.status === UploadStatus.UPLOADED &&
          Date.now() - uploadStatus.timestamp <= LIVENESS_TIMEOUT))
    ) {
      return DeploymentState.IN_PROGRESS;
    }

    // No need for the upload status anymore, it should have failed or succeeded by now.
    deploymentUploadStatusMap.delete(deployment.metadata.name);

    if (healtStatus !== HealthStatus.UP) {
      return DeploymentState.ERROR;
    }

    return DeploymentState.UP;
  }

  public async uploadAssets(args: {
    deployment: DeploymentResource;
    workspaceZipBlob: Blob;
    baseUrl: string;
    apiKey: string;
  }) {
    return new Promise<void>((resolve, reject) => {
      deploymentUploadStatusMap.set(args.deployment.metadata.name, { status: UploadStatus.NOT_READY });
      let deploymentState = this.kubernetesService.extractDeploymentState({ deployment: args.deployment });
      const interval = setInterval(async () => {
        if (deploymentState !== DeploymentState.UP) {
          const deployment = await this.getDeployment(args.deployment.metadata.name);
          deploymentState = this.kubernetesService.extractDeploymentState({ deployment });
        } else {
          try {
            const uploadStatus = await getUploadStatus({ baseUrl: args.baseUrl });
            if (uploadStatus === UploadStatus.NOT_READY) {
              return;
            }
            clearInterval(interval);
            if (uploadStatus === UploadStatus.READY) {
              deploymentUploadStatusMap.set(args.deployment.metadata.name, { status: UploadStatus.UPLOADING });
              await postUpload({ baseUrl: args.baseUrl, workspaceZipBlob: args.workspaceZipBlob, apiKey: args.apiKey });
              deploymentUploadStatusMap.set(args.deployment.metadata.name, {
                status: UploadStatus.UPLOADED,
                timestamp: Date.now(),
              });
              resolve();
            }
          } catch (e) {
            deploymentUploadStatusMap.delete(args.deployment.metadata.name);
            console.error(e);
            reject(e);
            clearInterval(interval);
          }
        }
      }, CHECK_UPLOAD_STATUS_POLLING_TIME);
    });
  }

  public newResourceName(): string {
    return KubernetesService.newResourceName(RESOURCE_PREFIX);
  }

  public async listServices(): Promise<ServiceResource[]> {
    return await this.kubernetesService.listResources<ServiceResource>({
      kind: kubernetesResourcesApi.service.kind,
      apiVersion: kubernetesResourcesApi.service.apiVersion,
      queryParams: [`labelSelector=${defaultLabelTokens.createdBy}`],
    });
  }

  public async listDeployments(): Promise<DeploymentResource[]> {
    return await this.kubernetesService.listResources<DeploymentResource>({
      kind: kubernetesResourcesApi.deployment.kind,
      apiVersion: kubernetesResourcesApi.deployment.apiVersion,
      queryParams: [`labelSelector=${defaultLabelTokens.createdBy}`],
    });
  }

  public async getDeployment(resourceId: string): Promise<DeploymentResource> {
    return await this.kubernetesService.getResource<DeploymentResource>({
      kind: kubernetesResourcesApi.deployment.kind,
      apiVersion: kubernetesResourcesApi.deployment.apiVersion,
      resourceId,
    });
  }

  public async deleteDeployment(resourceId: string) {
    return await this.kubernetesService.deleteResource({
      kind: kubernetesResourcesApi.deployment.kind,
      apiVersion: kubernetesResourcesApi.deployment.apiVersion,
      resourceId,
    });
  }

  public async deleteService(resourceId: string) {
    return await this.kubernetesService.deleteResource({
      kind: kubernetesResourcesApi.service.kind,
      apiVersion: kubernetesResourcesApi.service.apiVersion,
      resourceId,
    });
  }

  public async deleteDevDeployment(resources: K8sResourceYaml[]): Promise<void> {
    await Promise.all(
      resources.map(async (resource) => {
        await this.kubernetesService.deleteResource({
          kind: resource.kind,
          apiVersion: resource.apiVersion,
          resourceId: resource.metadata!.name!,
        });
      })
    );
  }
}
