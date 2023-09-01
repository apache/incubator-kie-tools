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

import { DeployedModel, DeploymentDescriptor } from "@kie-tools-core/kubernetes-bridge/dist/resources";
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
  dynamicDeploy(args: DeployArgs): Promise<void>;
  deleteDevDeployment(resourceName: string): Promise<void>;
  uploadAssets(args: {
    resourceArgs: ResourceArgs;
    deployment: DeploymentDescriptor;
    workspaceZipBlob: Blob;
    baseUrl: string;
  }): Promise<void>;
};

export type DevDeploymentTokens = {
  uniqueId: string;
  name: string;
  defaultContainerImageUrl: string;
};

export type WorkspaceTokens = {
  id: string;
  name: string;
  resourceName: string;
};

export type KubernetesTokens = {
  namespace: string;
};

export type UploadServiceTokens = {
  apiKey: string;
};

export type LabelTokens = {
  createdBy: string;
};

export type AnnotationTokens = {
  uri: string;
  workspaceId: string;
};

export const defaultLabelTokens: LabelTokens = {
  createdBy: "tools.kie.org/created-by",
} as const;

export const defaultAnnotationTokens: AnnotationTokens = {
  uri: "tools.kie.org/uri",
  workspaceId: "tools.kie.org/workspace-id",
} as const;

export const CREATED_BY_KIE_TOOLS = "kie-tools";

export const TOKENS_PREFIX = "devDeployment";

export type Tokens = DevDeploymentTokens & {
  workspace: WorkspaceTokens;
  kubernetes: KubernetesTokens;
  uploadService: UploadServiceTokens;
  labels: LabelTokens;
  annotations: AnnotationTokens;
};

export type TokensArg = Omit<Tokens, "labels" | "annotations"> & Partial<Tokens>;
