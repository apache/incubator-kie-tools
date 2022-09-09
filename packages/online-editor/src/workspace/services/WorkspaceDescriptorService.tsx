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
import KieSandboxFs from "@kie-tools/kie-sandbox-fs";
import DefaultBackend from "@kie-tools/kie-sandbox-fs/dist/DefaultBackend";
import DexieBackend from "@kie-tools/kie-sandbox-fs/dist/DexieBackend";
import { StorageFile, StorageService } from "./StorageService";
import { decoder, encoder } from "../WorkspacesContext";
import { WorkspaceKind, WorkspaceOrigin } from "../model/WorkspaceOrigin";
import { GIST_DEFAULT_BRANCH, GIT_DEFAULT_BRANCH } from "./GitService";
import { jsonParseWithUrl } from "../../json/JsonParse";

const WORKSPACE_DESCRIPTORS_FS_NAME = "workspaces";
export const NEW_WORKSPACE_DEFAULT_NAME = `Untitled Folder`;

export class WorkspaceDescriptorService {
  constructor(
    private readonly storageService: StorageService,
    private readonly descriptorsFs = new KieSandboxFs(WORKSPACE_DESCRIPTORS_FS_NAME, {
      backend: new DefaultBackend({
        idbBackendDelegate: (fileDbName, fileStoreName) => {
          return new DexieBackend(fileDbName, fileStoreName);
        },
      }) as any,
    })
  ) {}

  public async listAll(): Promise<WorkspaceDescriptor[]> {
    const workspaceDescriptorsFilePaths = await this.storageService.walk({
      fs: this.descriptorsFs,
      startFromDirPath: "/",
      shouldExcludeDir: () => false,
      onVisit: async ({ absolutePath }) => absolutePath,
    });

    const workspaceDescriptorFiles = await this.storageService.getFiles(
      this.descriptorsFs,
      workspaceDescriptorsFilePaths
    );

    return workspaceDescriptorFiles.map((workspaceDescriptorFile) =>
      jsonParseWithUrl(decoder.decode(workspaceDescriptorFile.content))
    );
  }

  public async bumpLastUpdatedDate(workspaceId: string): Promise<void> {
    const file = this.toStorageFile({
      ...(await this.get(workspaceId)),
      lastUpdatedDateISO: new Date().toISOString(),
    });
    await this.storageService.updateFile(this.descriptorsFs, file.path, file.getFileContents);
  }

  public async get(workspaceId: string): Promise<WorkspaceDescriptor> {
    const workspaceDescriptorFile = await this.storageService.getFile(this.descriptorsFs, `/${workspaceId}`);
    if (!workspaceDescriptorFile) {
      throw new Error(`Workspace not found (${workspaceId})`);
    }
    return jsonParseWithUrl(decoder.decode(await workspaceDescriptorFile.getFileContents()));
  }

  public async create(args: { origin: WorkspaceOrigin; preferredName?: string }) {
    const workspace: WorkspaceDescriptor = {
      workspaceId: this.newWorkspaceId(),
      name: args.preferredName?.trim() || NEW_WORKSPACE_DEFAULT_NAME,
      origin: args.origin,
      createdDateISO: new Date().toISOString(),
      lastUpdatedDateISO: new Date().toISOString(),
    };
    await this.storageService.createOrOverwriteFile(this.descriptorsFs, this.toStorageFile(workspace));
    return workspace;
  }

  public async delete(workspaceId: string) {
    await this.storageService.deleteFile(this.descriptorsFs, `/${workspaceId}`);
  }

  public async rename(workspaceId: string, newName: string) {
    const file = this.toStorageFile({
      ...(await this.get(workspaceId)),
      name: newName,
    });
    await this.storageService.updateFile(this.descriptorsFs, file.path, file.getFileContents);
  }

  public async turnIntoGist(workspaceId: string, gistUrl: URL) {
    const file = this.toStorageFile({
      ...(await this.get(workspaceId)),
      origin: {
        kind: WorkspaceKind.GITHUB_GIST,
        url: gistUrl.toString(),
        branch: GIST_DEFAULT_BRANCH,
      },
    });
    await this.storageService.updateFile(this.descriptorsFs, file.path, file.getFileContents);
  }

  public async turnIntoGit(workspaceId: string, url: URL) {
    const file = this.toStorageFile({
      ...(await this.get(workspaceId)),
      origin: {
        kind: WorkspaceKind.GIT,
        url: url.toString(),
        branch: GIT_DEFAULT_BRANCH,
      },
    });
    await this.storageService.updateFile(this.descriptorsFs, file.path, file.getFileContents);
  }

  private toStorageFile(descriptor: WorkspaceDescriptor) {
    return new StorageFile({
      path: `/${descriptor.workspaceId}`,
      getFileContents: () => Promise.resolve(encoder.encode(JSON.stringify(descriptor))),
    });
  }

  public newWorkspaceId(): string {
    return uuid();
  }
}
