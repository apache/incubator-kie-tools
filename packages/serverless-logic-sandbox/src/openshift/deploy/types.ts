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

import { OpenShiftService } from "@kie-tools-core/openshift/dist/service/OpenShiftService";
import { OpenShiftDeployedModel } from "@kie-tools-core/openshift/dist/service/types";
import { WorkspaceFile } from "@kie-tools-core/workspaces-git-fs/dist/context/WorkspacesContext";
import { WorkspaceDescriptor } from "@kie-tools-core/workspaces-git-fs/dist/worker/api/WorkspaceDescriptor";

export interface KafkaSourceArgs {
  serviceAccount: {
    clientId: string;
    clientSecret: string;
  };
  bootstrapServers: string[];
  topics: string[];
}

export interface DeploymentStrategyArgs {
  resourceName: string;
  namespace: string;
  targetFile: WorkspaceFile;
  workspace: WorkspaceDescriptor;
  kafkaSourceArgs?: KafkaSourceArgs;
  openShiftService: OpenShiftService;
  getFiles: (args: { workspaceId: string; globPattern?: string }) => Promise<WorkspaceFile[]>;
}

export type WebToolsOpenShiftDeployedModel = OpenShiftDeployedModel & {
  uri: string;
  workspaceName: string;
};

export type CompletedDeployOperation = string | undefined;

export enum DeploymentStrategyKind {
  KOGITO_SWF_MODEL,
  KOGITO_PROJECT,
}

export type DeploymentStrategyFactoryArgs =
  | {
      kind: DeploymentStrategyKind.KOGITO_SWF_MODEL;
      shouldAttachKafkaSource: boolean;
    }
  | {
      kind: DeploymentStrategyKind.KOGITO_PROJECT;
      shouldAttachKafkaSource: boolean;
    };

export interface InitDeployArgs {
  factoryArgs: DeploymentStrategyFactoryArgs;
  targetFile: WorkspaceFile;
}

export type InitSwfDeployArgs = InitDeployArgs & {
  shouldUploadOpenApi: boolean;
  factoryArgs: DeploymentStrategyFactoryArgs & {
    kind: DeploymentStrategyKind.KOGITO_PROJECT | DeploymentStrategyKind.KOGITO_SWF_MODEL;
  };
};
