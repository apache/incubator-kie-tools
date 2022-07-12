/*
 * Copyright 2021 Red Hat, Inc. and/or its affiliates.
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

import { WorkspaceDescriptor } from "../model/WorkspaceDescriptor";
import { v4 as uuid } from "uuid";
import { WorkspaceKind, WorkspaceOrigin } from "../model/WorkspaceOrigin";
import { GIST_DEFAULT_BRANCH, GIT_DEFAULT_BRANCH } from "../commonServices/GitService";
import { DescriptorService, DescriptorServiceConfig } from "../commonServices/DescriptorService";
import KieSandboxFs from "@kie-tools/kie-sandbox-fs";
import { StorageService } from "../commonServices/StorageService";

const WORKSPACE_DESCRIPTORS_FS_NAME = "workspaces";
export const NEW_WORKSPACE_DEFAULT_NAME = `Untitled Folder`;

type CreateWorkspaceDescriptorArgs = { origin: WorkspaceOrigin; preferredName?: string };

export class WorkspaceDescriptorService extends DescriptorService<WorkspaceDescriptor, CreateWorkspaceDescriptorArgs> {
  constructor(protected readonly storageService: StorageService, descriptorsFs?: KieSandboxFs) {
    super(
      storageService,
      {
        descriptorFsName: WORKSPACE_DESCRIPTORS_FS_NAME,
        idField: "workspaceId",
        nameField: "name",
      },
      descriptorsFs
    );
  }

  public getDescriptorPath(id: string): string {
    return `/${id}`;
  }

  public createNewDescriptor(args: CreateWorkspaceDescriptorArgs): WorkspaceDescriptor {
    const id = this.newWorkspaceId();
    return {
      workspaceId: id,
      name: args.preferredName?.trim() || NEW_WORKSPACE_DEFAULT_NAME,
      origin: args.origin,
      createdDateISO: new Date().toISOString(),
      lastUpdatedDateISO: new Date().toISOString(),
    };
  }

  public async turnIntoGist(workspaceId: string, gistUrl: URL) {
    await this.storageService.updateFile(
      this.descriptorsFs,
      this.toStorageFile({
        ...(await this.get(workspaceId)),
        origin: {
          kind: WorkspaceKind.GITHUB_GIST,
          url: gistUrl,
          branch: GIST_DEFAULT_BRANCH,
        },
      })
    );
  }

  public async turnIntoGit(workspaceId: string, url: URL) {
    await this.storageService.updateFile(
      this.descriptorsFs,
      this.toStorageFile({
        ...(await this.get(workspaceId)),
        origin: {
          kind: WorkspaceKind.GIT,
          url,
          branch: GIT_DEFAULT_BRANCH,
        },
      })
    );
  }

  public async turnIntoLocal(workspaceId: string) {
    await this.storageService.updateFile(
      this.descriptorsFs,
      this.toStorageFile({
        ...(await this.get(workspaceId)),
        origin: {
          kind: WorkspaceKind.LOCAL,
          branch: GIT_DEFAULT_BRANCH,
        },
      })
    );
  }

  public newWorkspaceId(): string {
    return uuid();
  }
}
