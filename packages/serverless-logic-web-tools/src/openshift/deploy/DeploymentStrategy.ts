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

import { WorkspaceFile } from "@kie-tools-core/workspaces-git-fs/dist/context/WorkspacesContext";
import { encoder } from "@kie-tools-core/workspaces-git-fs/dist/encoderdecoder/EncoderDecoder";
import { NEW_WORKSPACE_DEFAULT_NAME } from "@kie-tools-core/workspaces-git-fs/dist/worker/api/WorkspaceDescriptor";
import { DEFAULT_DOCKER_IGNORE_CONTENT, PROJECT_FILES } from "../../project";
import { OpenShiftPipeline } from "../OpenShiftPipeline";
import { DeploymentStrategyArgs } from "./types";

export abstract class DeploymentStrategy {
  constructor(protected readonly args: DeploymentStrategyArgs) {}

  public get resourceName(): string {
    return this.args.resourceName;
  }

  public abstract buildPipeline(): Promise<OpenShiftPipeline>;

  protected abstract prepareDockerfileContent(): string;

  protected prepareDockerignoreContent(): string {
    return DEFAULT_DOCKER_IGNORE_CONTENT;
  }

  protected async createDockerfileFile(): Promise<WorkspaceFile> {
    return new WorkspaceFile({
      workspaceId: this.args.workspace.workspaceId,
      relativePath: PROJECT_FILES.dockerFile,
      getFileContents: async () => encoder.encode(this.prepareDockerfileContent()),
    });
  }

  protected async createDockerignoreFile(): Promise<WorkspaceFile> {
    return new WorkspaceFile({
      workspaceId: this.args.workspace.workspaceId,
      relativePath: PROJECT_FILES.dockerIgnore,
      getFileContents: async () => encoder.encode(this.prepareDockerignoreContent()),
    });
  }

  protected resolveWorkspaceName(filesToBeDeployed: WorkspaceFile[]): string {
    return filesToBeDeployed.length > 1 && this.args.workspace.name !== NEW_WORKSPACE_DEFAULT_NAME
      ? this.args.workspace.name
      : this.args.targetFile.name;
  }
}
