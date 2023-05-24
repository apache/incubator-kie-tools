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
import { OpenShiftService } from "@kie-tools-core/kubernetes-bridge/dist/service/OpenShiftService";
import { WorkspaceFile } from "@kie-tools-core/workspaces-git-fs/dist/context/WorkspacesContext";
import { WorkspaceDescriptor } from "@kie-tools-core/workspaces-git-fs/dist/worker/api/WorkspaceDescriptor";

export interface DeploymentStrategyArgs {
  resourceName: string;
  namespace: string;
  targetFile: WorkspaceFile;
  workspace: WorkspaceDescriptor;
  openShiftService: OpenShiftService;
  getFiles: (args: { workspaceId: string; globPattern?: string }) => Promise<WorkspaceFile[]>;
}

export type WebToolsOpenShiftDeployedModel = DeployedModel &
  (
    | {
        workspaceName: string;
        devMode: false;
      }
    | {
        devMode: true;
      }
  );

export type CompletedDeployOperation = string | undefined;

export enum DeploymentStrategyKind {
  KOGITO_SWF_MODEL,
  KOGITO_PROJECT,
  DASHBOARD_SINGLE_MODEL,
  DASHBOARD_WORKSPACE,
}

export type DeploymentStrategyFactoryArgs =
  | { kind: DeploymentStrategyKind.KOGITO_SWF_MODEL }
  | { kind: DeploymentStrategyKind.KOGITO_PROJECT }
  | { kind: DeploymentStrategyKind.DASHBOARD_SINGLE_MODEL }
  | { kind: DeploymentStrategyKind.DASHBOARD_WORKSPACE };

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
