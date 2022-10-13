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

import KieSandboxFs from "@kie-tools/kie-sandbox-fs";
import { Minimatch } from "minimatch";
import { join, relative } from "path";
import { encoder } from "../encoderdecoder/EncoderDecoder";
import { WorkspaceDescriptor } from "../worker/api/WorkspaceDescriptor";
import { WorkspaceFile } from "../WorkspacesContext";
import { LfsStorageFile, LfsStorageService } from "./LfsStorageService";
import { LfsWorkspaceDescriptorService } from "./LfsWorkspaceDescriptorService";

export class LfsWorkspaceService {
  public constructor(
    private readonly storageService: LfsStorageService,
    private readonly descriptorService: LfsWorkspaceDescriptorService,
    private readonly fsMountPoint: string
  ) {}

  public async create(args: {
    storeFiles: (descriptor: WorkspaceDescriptor) => Promise<WorkspaceFile[]>;
    workspaceDescriptor: WorkspaceDescriptor;
  }) {
    const descriptor = await this.descriptorService.create({ workspaceDescriptor: args.workspaceDescriptor });

    try {
      const files = await args.storeFiles(descriptor);
      return { descriptor, files };
    } catch (e) {
      await this.descriptorService.delete(descriptor.workspaceId);
      throw e;
    }
  }

  public async getFilesWithLazyContent(args: {
    fs: KieSandboxFs;
    workspaceId: string;
    globPattern?: string;
  }): Promise<WorkspaceFile[]> {
    const matcher = args.globPattern ? new Minimatch(args.globPattern, { dot: true }) : undefined;
    const gitDirPath = this.getAbsolutePath({ workspaceId: args.workspaceId, relativePath: ".git" });

    return this.storageService.walk({
      fs: args.fs,
      startFromDirPath: this.getAbsolutePath({ workspaceId: args.workspaceId }),
      shouldExcludeDir: (dirPath) => dirPath === gitDirPath,
      onVisit: async ({ absolutePath, relativePath }) => {
        const workspaceFile = new WorkspaceFile({
          workspaceId: args.workspaceId,
          relativePath,
          getFileContents: () => this.storageService.getFile(args.fs, absolutePath).then((f) => f!.getFileContents()),
        });

        if (matcher && !matcher.match(workspaceFile.name)) {
          return undefined;
        }

        return workspaceFile;
      },
    });
  }

  public async delete(workspaceId: string): Promise<void> {
    await this.descriptorService.delete(workspaceId);
    indexedDB.deleteDatabase(`${this.fsMountPoint}${workspaceId}`);
  }

  public async rename(args: { workspaceId: string; newName: string }): Promise<void> {
    await this.descriptorService.rename(args.workspaceId, args.newName);
    await this.descriptorService.bumpLastUpdatedDate(args.workspaceId);
  }

  public async createOrOverwriteFile(args: { fs: KieSandboxFs; file: WorkspaceFile }): Promise<void> {
    await this.storageService.createOrOverwriteFile(args.fs, this.toStorageFile(args.file));
    await this.descriptorService.bumpLastUpdatedDate(args.file.workspaceId);
  }

  public async getFile(args: {
    fs: KieSandboxFs;
    workspaceId: string;
    relativePath: string;
  }): Promise<WorkspaceFile | undefined> {
    const absolutePath = this.getAbsolutePath(args);
    const storageFile = await this.storageService.getFile(args.fs, absolutePath);
    if (!storageFile) {
      return;
    }
    return this.toWorkspaceFile(args.workspaceId, storageFile);
  }

  public async updateFile(args: {
    fs: KieSandboxFs;
    file: WorkspaceFile;
    getNewContents: () => Promise<string>;
  }): Promise<void> {
    await this.storageService.updateFile(
      args.fs,
      this.toStorageFile(
        new WorkspaceFile({
          relativePath: args.file.relativePath,
          workspaceId: args.file.workspaceId,
          getFileContents: () => args.getNewContents().then((c) => encoder.encode(c)),
        })
      )
    );
    await this.descriptorService.bumpLastUpdatedDate(args.file.workspaceId);
  }

  public async deleteFile(args: { fs: KieSandboxFs; file: WorkspaceFile }): Promise<void> {
    await this.storageService.deleteFile(args.fs, this.toStorageFile(args.file).path);
    await this.descriptorService.bumpLastUpdatedDate(args.file.workspaceId);
  }

  public async renameFile(args: {
    fs: KieSandboxFs;
    file: WorkspaceFile;
    newFileNameWithoutExtension: string;
  }): Promise<WorkspaceFile> {
    const renamedStorageFile = await this.storageService.renameFile(
      args.fs,
      this.toStorageFile(args.file),
      args.newFileNameWithoutExtension
    );
    const renamedWorkspaceFile = this.toWorkspaceFile(args.file.workspaceId, renamedStorageFile);
    await this.descriptorService.bumpLastUpdatedDate(args.file.workspaceId);
    return renamedWorkspaceFile;
  }

  public async existsFile(args: { fs: KieSandboxFs; workspaceId: string; relativePath: string }): Promise<boolean> {
    return this.storageService.exists(args.fs, this.getAbsolutePath(args));
  }

  private getAbsolutePath(args: { workspaceId: string; relativePath?: string }) {
    return join("/", args.relativePath ?? "");
  }

  private toWorkspaceFile(workspaceId: string, storageFile: LfsStorageFile): WorkspaceFile {
    return new WorkspaceFile({
      workspaceId,
      getFileContents: storageFile.getFileContents,
      relativePath: relative(this.getAbsolutePath({ workspaceId }), storageFile.path),
    });
  }

  private toStorageFile(file: WorkspaceFile): LfsStorageFile {
    return new LfsStorageFile({
      path: this.getAbsolutePath({ workspaceId: file.workspaceId, relativePath: file.relativePath }),
      getFileContents: file.getFileContents,
    });
  }
}
