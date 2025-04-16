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

import { K8sResourceYaml, ResourcePatch } from "@kie-tools-core/k8s-yaml-to-apiserver-requests/dist";
import { DeploymentState } from "./common";

/**
 * Replaces leaf nodes of T with V
 */
export type RecursiveReplace<T, V> = {
  [P in keyof T]: T[P] extends (infer U)[]
    ? RecursiveReplace<U, V>[]
    : T[P] extends number | string | symbol | undefined
      ? V
      : RecursiveReplace<T[P], V>;
};

export type KieSandboxDeployment = {
  name: string;
  routeUrl: string;
  creationTimestamp: Date;
  state: DeploymentState;
  resources: K8sResourceYaml[];
  workspaceId: string;
  workspaceName: string;
};

export type DevDeploymentTokens = {
  uniqueName: string;
};

export type WorkspaceTokens = {
  id: string;
  name: string;
};

export type KubernetesTokens = {
  namespace: string;
};

export type UploadServiceTokens = {
  apiKey: string;
};

export type LabelTokens = {
  createdBy: string;
  partOf: string;
};

export type AnnotationTokens = {
  workspaceId: string;
  workspaceName: string;
};

export const defaultLabelTokens: LabelTokens = {
  createdBy: "tools.kie.org/created-by",
  partOf: "tools.kie.org/part-of",
} as const;

export const defaultAnnotationTokens: AnnotationTokens = {
  workspaceId: "tools.kie.org/workspace-id",
  workspaceName: "tools.kie.org/workspace-name",
} as const;

export const TOKENS_PREFIX = "devDeployment";

export type Tokens = DevDeploymentTokens & {
  workspace: WorkspaceTokens;
  kubernetes: KubernetesTokens;
  uploadService: UploadServiceTokens;
  labels: LabelTokens;
  annotations: AnnotationTokens;
};

export type TokensArg = Omit<Tokens, "labels" | "annotations"> & Partial<Tokens>;

export type DeploymentOptionArgs = {
  quarkusBlankAppImageUrl: string;
  baseImageUrl: string;
  dmnFormWebappImageUrl: string;
  imagePullPolicy: string;
};

export type ResourceActions = {
  resourcePatches?: ResourcePatch[];
  appendYamls?: string[];
};
