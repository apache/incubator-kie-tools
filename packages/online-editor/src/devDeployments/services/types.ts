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

import { DeployedModel } from "@kie-tools-core/kubernetes-bridge/dist/resources";
import { KubernetesConnectionStatus } from "@kie-tools-core/kubernetes-bridge/dist/service";

export type KieSandboxDeployedModel = DeployedModel & {
  uri: string;
  workspaceName: string;
};

export interface DeployArgs {
  targetFilePath: string;
  workspaceName: string;
  workspaceZipBlob: Blob;
  containerImageUrl: string;
}

export type ResourceArgs = {
  namespace: string;
  resourceName: string;
  createdBy: string;
};

export type KieSandboxDeploymentService = {
  isConnectionEstablished(): Promise<KubernetesConnectionStatus>;
  loadDeployedModels(): Promise<KieSandboxDeployedModel[]>;
  deploy(args: DeployArgs): Promise<void>;
  deleteDevDeployment(resourceName: string): Promise<void>;
};
