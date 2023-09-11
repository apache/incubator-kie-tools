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
import { v4 as uuid } from "uuid";
import { CloudAuthSessionType } from "../../authSessions/AuthSessionApi";
import { KubernetesConnectionStatus, KubernetesService, KubernetesServiceArgs } from "./KubernetesService";

export enum DeploymentState {
  UP = "UP",
  DOWN = "DOWN",
  IN_PROGRESS = "IN_PROGRESS",
  PREPARING = "PREPARING",
  ERROR = "ERROR",
}

export type KieSandboxDeployment = {
  name: string;
  routeUrl: string;
  creationTimestamp: Date;
  state: DeploymentState;
  resources: K8sResourceYaml[];
  workspaceId: string;
  resourceName: string;
};

export interface DeployArgs {
  workspaceZipBlob: Blob;
  tokenMap: TokenMap;
}

export type ResourceArgs = {
  namespace: string;
  resourceName: string;
  createdBy: string;
};

export type KieSandboxDevDeploymentsServiceProps = {
  id: string;
  type: CloudAuthSessionType;
  args: KubernetesServiceArgs;
};

export type KieSandboxDevDeploymentsServiceType = KieSandboxDevDeploymentsServiceProps & {
  isConnectionEstablished(): Promise<KubernetesConnectionStatus>;
  loadDevDeployments(): Promise<KieSandboxDeployment[]>;
  deploy(args: DeployArgs): Promise<void>;
  deleteDevDeployment(resourceName: string): Promise<void>;
  uploadAssets(args: {
    resourceArgs: ResourceArgs;
    deployment: K8sResourceYaml;
    workspaceZipBlob: Blob;
    baseUrl: string;
  }): Promise<void>;
  extractDevDeploymentState(args: { deployment?: any }): DeploymentState;
};

export abstract class KieSandboxDevDeploymentsService implements KieSandboxDevDeploymentsServiceType {
  id: string;
  type: CloudAuthSessionType.None;

  constructor(readonly args: KubernetesServiceArgs) {}

  get kubernetesService() {
    return new KubernetesService(this.args);
  }

  public extractDevDeploymentState(args: { deployment?: any }): DeploymentState {
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

  abstract isConnectionEstablished(): Promise<KubernetesConnectionStatus>;

  abstract loadDevDeployments(): Promise<KieSandboxDeployment[]>;

  abstract deploy(args: DeployArgs): Promise<void>;

  abstract deleteDevDeployment(resourceName: string): Promise<void>;

  abstract uploadAssets(args: {
    resourceArgs: ResourceArgs;
    deployment: K8sResourceYaml;
    workspaceZipBlob: Blob;
    baseUrl: string;
  }): Promise<void>;
}
