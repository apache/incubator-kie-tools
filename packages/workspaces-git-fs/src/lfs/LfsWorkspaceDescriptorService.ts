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

import { v4 as uuid } from "uuid";
import KieSandboxFs from "@kie-tools/kie-sandbox-fs";
import DefaultBackend from "@kie-tools/kie-sandbox-fs/dist/DefaultBackend";
import DexieBackend from "@kie-tools/kie-sandbox-fs/dist/DexieBackend";
import { join } from "path";
import { decoder, encoder } from "../encoderdecoder/EncoderDecoder";
import { WorkspaceDescriptor } from "../worker/api/WorkspaceDescriptor";
import { LfsStorageFile, LfsStorageService } from "./LfsStorageService";
import { WorkspaceKind } from "../worker/api/WorkspaceOrigin";
import { GIT_DEFAULT_BRANCH } from "../constants/GitConstants";

export type CreateDescriptorArgs = Partial<Pick<WorkspaceDescriptor, "workspaceId" | "origin">> &
  Pick<WorkspaceDescriptor, "name">;

export class LfsWorkspaceDescriptorService {
  private readonly descriptorFs: KieSandboxFs;

  constructor(databaseName: string, private readonly storageService: LfsStorageService) {
    this.descriptorFs = new KieSandboxFs(databaseName, {
      backend: new DefaultBackend({
        idbBackendDelegate: (fileDbName, fileStoreName) => new DexieBackend(fileDbName, fileStoreName),
      }) as any,
    });
  }

  public async listAll(): Promise<WorkspaceDescriptor[]> {
    const descriptorsFilePaths = await this.storageService.walk({
      fs: this.descriptorFs,
      startFromDirPath: "/",
      shouldExcludeDir: () => false,
      onVisit: async ({ absolutePath }) => absolutePath,
    });

    const descriptorFiles = await this.storageService.getFiles(this.descriptorFs, descriptorsFilePaths);
    return Promise.all(descriptorFiles.map(async (f) => JSON.parse(decoder.decode(await f.getFileContents()))));
  }

  public async bumpLastUpdatedDate(id: string): Promise<void> {
    await this.storageService.updateFile(
      this.descriptorFs,
      this.toStorageFile({
        ...(await this.get(id)),
        lastUpdatedDateISO: new Date().toISOString(),
      })
    );
  }

  public async exists(workspaceId: string): Promise<boolean> {
    return (await this.storageService.getFile(this.descriptorFs, this.getAbsolutePath(workspaceId))) != undefined;
  }

  public async get(workspaceId: string): Promise<WorkspaceDescriptor> {
    const descriptorFile = await this.storageService.getFile(this.descriptorFs, this.getAbsolutePath(workspaceId));
    if (!descriptorFile) {
      throw new Error(`Descriptor not found (${workspaceId})`);
    }
    return JSON.parse(decoder.decode(await descriptorFile.getFileContents()));
  }

  public async create(args: CreateDescriptorArgs): Promise<WorkspaceDescriptor> {
    const descriptor: WorkspaceDescriptor = {
      workspaceId: args.workspaceId ?? this.newWorkspaceId(),
      name: args.name,
      origin: args.origin ?? { kind: WorkspaceKind.LOCAL, branch: GIT_DEFAULT_BRANCH },
      createdDateISO: new Date().toISOString(),
      lastUpdatedDateISO: new Date().toISOString(),
      gitAuthSessionId: undefined,
    };
    await this.storageService.createOrOverwriteFile(this.descriptorFs, this.toStorageFile(descriptor));
    return descriptor;
  }

  public async delete(workspaceId: string) {
    await this.storageService.deleteFile(this.descriptorFs, this.getAbsolutePath(workspaceId));
  }

  public async rename(workspaceId: string, newName: string) {
    await this.storageService.updateFile(
      this.descriptorFs,
      this.toStorageFile({
        ...(await this.get(workspaceId)),
        name: newName,
      })
    );
  }

  private toStorageFile(descriptor: WorkspaceDescriptor) {
    return new LfsStorageFile({
      path: this.getAbsolutePath(descriptor.workspaceId),
      getFileContents: async () => encoder.encode(JSON.stringify(descriptor)),
    });
  }

  private getAbsolutePath(relativePath: string) {
    return join("/", relativePath ?? "");
  }

  private newWorkspaceId(): string {
    return uuid();
  }
}
