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

import { StorageFile, StorageService } from "../../../commonServices/StorageService";
import { join, relative } from "path";
import KieSandboxFs from "@kie-tools/kie-sandbox-fs";
import { VirtualServiceRegistryFsService } from "./VirtualServiceRegistryFsService";
import { groupPath, VirtualServiceRegistryGroup } from "../models/VirtualServiceRegistry";
import { ServiceRegistryFile } from "../models/ServiceRegistryFile";
import { WorkspaceDescriptor } from "../../../model/WorkspaceDescriptor";
import { VirtualServiceRegistryGroupService } from "./VirtualServiceRegistryGroupService";
import { Minimatch } from "minimatch";

export const VIRTUAL_SERVICE_REGISTRY_BROADCAST_CHANNEL = "virtualServiceRegistry";
export const VIRTUAL_SERVICE_REGISTRY_GROUP_BROADCAST_CHANNEL = (groupId: string) =>
  `${VIRTUAL_SERVICE_REGISTRY_BROADCAST_CHANNEL}_${groupId}`;

export class VirtualServiceRegistryService {
  public constructor(
    public readonly storageService: StorageService,
    public readonly vsrGroupService: VirtualServiceRegistryGroupService,
    public readonly fsService: VirtualServiceRegistryFsService
  ) {}

  public async create(args: {
    useInMemoryFs: boolean;
    storeRegistryFiles: (fs: KieSandboxFs, vsrGroup: VirtualServiceRegistryGroup) => Promise<ServiceRegistryFile[]>;
    broadcastArgs: { broadcast: boolean };
    workspaceDescriptor: WorkspaceDescriptor;
  }) {
    const vsrGroup = await this.vsrGroupService.create({
      workspaceDescriptor: args.workspaceDescriptor,
    });

    try {
      const files = await (args.useInMemoryFs
        ? this.fsService.withInMemoryFs(vsrGroup.groupId, (fs) => args.storeRegistryFiles(fs, vsrGroup))
        : args.storeRegistryFiles(await this.fsService.getFs(vsrGroup.groupId), vsrGroup));

      return { vsrGroup, files };
    } catch (e) {
      await this.vsrGroupService.delete(vsrGroup.groupId);
      throw e;
    }
  }

  public async delete(groupId: string, broadcastArgs: { broadcast: boolean }): Promise<void> {
    await this.vsrGroupService.delete(groupId);
    indexedDB.deleteDatabase(groupPath({ groupId }));
  }

  public async rename(groupId: string, newName: string, broadcastArgs: { broadcast: boolean }): Promise<void> {
    await this.vsrGroupService.rename(groupId, newName);
  }

  public async createOrOverwriteFile(
    fs: KieSandboxFs,
    file: ServiceRegistryFile,
    broadcastArgs: { broadcast: boolean }
  ): Promise<void> {
    await this.storageService.createOrOverwriteFile(fs, this.toStorageFile(file));
  }

  public async getFile(args: {
    fs: KieSandboxFs;
    groupId: string;
    relativePath: string;
  }): Promise<ServiceRegistryFile | undefined> {
    const absolutePath = this.getAbsolutePath(args);
    const storageFile = await this.storageService.getFile(args.fs, absolutePath);
    if (!storageFile) {
      return;
    }
    return this.toServiceRegistryFile(args.groupId, storageFile);
  }

  public async updateFile(
    fs: KieSandboxFs,
    file: ServiceRegistryFile,
    getNewContents: () => Promise<Uint8Array>,
    broadcastArgs: { broadcast: boolean }
  ): Promise<void> {
    await this.storageService.updateFile(
      fs,
      this.toStorageFile(
        new ServiceRegistryFile(
          {
            workspaceId: file.groupId,
            relativePath: file.relativePath,
            getFileContents: () => getNewContents(),
          },
          true
        )
      )
    );
  }

  public async getFilesWithLazyContent(
    fs: KieSandboxFs,
    groupId: string,
    globPattern?: string
  ): Promise<ServiceRegistryFile[]> {
    const matcher = globPattern ? new Minimatch(globPattern, { dot: true }) : undefined;

    return await this.storageService.walk({
      fs,
      startFromDirPath: this.getAbsolutePath({ groupId }),
      shouldExcludeDir: (dirPath) => false,
      onVisit: async ({ absolutePath, relativePath }) => {
        const serviceRegistryFile = new ServiceRegistryFile(
          {
            workspaceId: groupId,
            relativePath,
            getFileContents: () => this.storageService.getFile(fs, absolutePath).then((f) => f!.getFileContents()),
          },
          true
        );

        if (matcher && !matcher.match(serviceRegistryFile.name)) {
          return undefined;
        }

        return serviceRegistryFile;
      },
    });
  }

  public async deleteFile(
    fs: KieSandboxFs,
    relativePath: string,
    broadcastArgs: { broadcast: boolean }
  ): Promise<void> {
    await this.storageService.deleteFile(fs, relativePath);
  }

  public async renameFile(args: {
    fs: KieSandboxFs;
    file: ServiceRegistryFile;
    newFileNameWithoutExtension: string;
    broadcastArgs: { broadcast: boolean };
  }): Promise<ServiceRegistryFile> {
    const renamedStorageFile = await this.storageService.renameFile(
      args.fs,
      this.toStorageFile(args.file),
      args.newFileNameWithoutExtension
    );
    const renamedWorkspaceFile = this.toServiceRegistryFile(args.file.groupId, renamedStorageFile);
    return renamedWorkspaceFile;
  }

  public async existsFile(args: { fs: KieSandboxFs; groupId: string; relativePath: string }): Promise<boolean> {
    return this.storageService.exists(args.fs, this.getAbsolutePath(args));
  }

  public getAbsolutePath(args: { groupId: string; relativePath?: string }) {
    return join("/", args.relativePath ?? "");
  }

  public async deleteFiles(
    fs: KieSandboxFs,
    files: ServiceRegistryFile[],
    broadcastArgs: { broadcast: boolean }
  ): Promise<void> {
    if (files.length === 0) {
      return;
    }

    await Promise.all(files.map((f) => this.toStorageFile(f)).map((f) => this.storageService.deleteFile(fs, f.path)));
  }

  public async moveFile(args: {
    fs: KieSandboxFs;
    file: ServiceRegistryFile;
    newDirPath: string;
    broadcastArgs: { broadcast: boolean };
  }): Promise<ServiceRegistryFile> {
    const movedFile = this.toServiceRegistryFile(
      args.file.groupId,
      await this.storageService.moveFile(args.fs, this.toStorageFile(args.file), args.newDirPath)
    );

    return movedFile;
  }

  public async moveFiles(args: {
    fs: KieSandboxFs;
    files: ServiceRegistryFile[];
    newDirPath: string;
    broadcastArgs: { broadcast: boolean };
  }): Promise<Map<string, string>> {
    if (args.files.length === 0) {
      return new Map();
    }

    const relativePaths = await this.storageService.moveFiles(
      args.fs,
      args.files.map((f) => this.toStorageFile(f)),
      args.newDirPath
    );

    return relativePaths;
  }

  private toServiceRegistryFile(groupId: string, storageFile: StorageFile): ServiceRegistryFile {
    return new ServiceRegistryFile(
      {
        workspaceId: groupId,
        getFileContents: storageFile.getFileContents,
        relativePath: relative(this.getAbsolutePath({ groupId }), storageFile.path),
      },
      true
    );
  }

  private toStorageFile(serviceRegistryFile: ServiceRegistryFile): StorageFile {
    return new StorageFile({
      path: this.getAbsolutePath(serviceRegistryFile),
      getFileContents: serviceRegistryFile.getFileContents,
    });
  }

  public getUniqueFileIdentifier(args: { groupId: string; relativePath: string }) {
    return args.groupId + "__" + this.getAbsolutePath(args);
  }
}
