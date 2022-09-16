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

import { encoder, WorkspaceFile } from "../WorkspacesContext";
import JSZip from "jszip";
import { WorkspaceDescriptor } from "../worker/api/WorkspaceDescriptor";
import { StorageFile, StorageService } from "./StorageService";
import { extname, join, relative } from "path";
import { Minimatch } from "minimatch";
import { WorkspaceDescriptorService } from "./WorkspaceDescriptorService";
import { BroadcasterDispatch } from "./FsService";
import { WorkspaceOrigin } from "../worker/api/WorkspaceOrigin";
import { WorkspaceWorkerFileDescriptor } from "../worker/api/WorkspaceWorkerFileDescriptor";
import { WorkspaceWorkerFile } from "../worker/api/WorkspaceWorkerFile";
import { KieSandboxWorkspacesFs } from "./KieSandboxWorkspaceFs";
import { WorkspaceDescriptorFsService } from "./WorkspaceDescriptorFsService";
import { WorkspaceFsService } from "./WorkspaceFsService";

export const WORKSPACES_BROADCAST_CHANNEL = "workspaces";

export class WorkspaceService {
  public constructor(
    public readonly storageService: StorageService,
    private readonly descriptorsFsService: WorkspaceDescriptorFsService,
    private readonly workspaceDescriptorService: WorkspaceDescriptorService,
    private readonly fsService: WorkspaceFsService
  ) {}

  public async create(args: {
    storeFiles: (fs: KieSandboxWorkspacesFs, workspace: WorkspaceDescriptor) => Promise<WorkspaceFile[]>;
    origin: WorkspaceOrigin;
    preferredName?: string;
  }) {
    const workspace = await this.descriptorsFsService.withReadWriteInMemoryFs(({ fs }) => {
      return this.workspaceDescriptorService.create({
        fs,
        origin: args.origin,
        preferredName: args.preferredName,
      });
    });

    try {
      return this.fsService.withReadWriteInMemoryFs(workspace.workspaceId, async ({ fs, broadcaster }) => {
        const files = await args.storeFiles(fs, workspace);

        broadcaster.broadcast({
          channel: WORKSPACES_BROADCAST_CHANNEL,
          message: async () => ({
            type: "ADD_WORKSPACE",
            workspaceId: workspace.workspaceId,
          }),
        });

        broadcaster.broadcast({
          channel: workspace.workspaceId,
          message: async () => ({
            type: "ADD",
            workspaceId: workspace.workspaceId,
          }),
        });

        return { workspace, files };
      });
    } catch (e) {
      await this.descriptorsFsService.withReadWriteInMemoryFs(async ({ fs }) => {
        await this.workspaceDescriptorService.delete(fs, workspace.workspaceId);
      });
      throw e;
    }
  }

  public async getFilesWithLazyContent(
    fs: KieSandboxWorkspacesFs,
    workspaceId: string,
    globPattern?: string
  ): Promise<WorkspaceFile[]> {
    const matcher = globPattern ? new Minimatch(globPattern, { dot: true }) : undefined;
    const gitDirPath = this.getAbsolutePath({ workspaceId, relativePath: ".git" });

    return await this.storageService.walk({
      fs,
      startFromDirPath: this.getAbsolutePath({ workspaceId }),
      shouldExcludeDir: (dirPath) => dirPath === gitDirPath,
      onVisit: async ({ absolutePath, relativePath }) => {
        const workspaceFile = new WorkspaceFile({
          workspaceId,
          relativePath,
          getFileContents: () => this.storageService.getFile(fs, absolutePath).then((f) => f!.getFileContents()),
        });

        if (matcher && !matcher.match(workspaceFile.name)) {
          return undefined;
        }

        return workspaceFile;
      },
    });
  }

  public async delete(workspaceId: string): Promise<void> {
    await this.descriptorsFsService.withReadWriteInMemoryFs(async ({ fs, broadcaster }) => {
      await this.workspaceDescriptorService.delete(fs, workspaceId);
      indexedDB.deleteDatabase(this.fsService.getMountPoint(workspaceId));

      broadcaster.broadcast({
        channel: WORKSPACES_BROADCAST_CHANNEL,
        message: async () => ({ type: "DELETE_WORKSPACE", workspaceId }),
      });

      broadcaster.broadcast({
        channel: workspaceId,
        message: async () => ({ type: "DELETE", workspaceId }),
      });
    });
  }

  public async rename(workspaceId: string, newName: string): Promise<void> {
    await this.descriptorsFsService.withReadWriteInMemoryFs(async ({ fs, broadcaster }) => {
      await this.workspaceDescriptorService.rename(fs, workspaceId, newName);
      await this.workspaceDescriptorService.bumpLastUpdatedDate(fs, workspaceId);

      broadcaster.broadcast({
        channel: WORKSPACES_BROADCAST_CHANNEL,
        message: async () => ({ type: "RENAME_WORKSPACE", workspaceId }),
      });

      broadcaster.broadcast({
        channel: workspaceId,
        message: async () => ({ type: "RENAME", workspaceId }),
      });
    });
  }

  public async prepareZip(fs: KieSandboxWorkspacesFs, workspaceId: string, onlyExtensions?: string[]): Promise<Blob> {
    const workspaceRootDirPath = this.getAbsolutePath({ workspaceId });

    const gitDirPath = this.getAbsolutePath({ workspaceId, relativePath: ".git" });
    const paths = await this.storageService.walk({
      fs,
      startFromDirPath: workspaceRootDirPath,
      shouldExcludeDir: (dirPath) => dirPath === gitDirPath,
      onVisit: async ({ absolutePath }) => absolutePath,
    });

    const files = await Promise.all(
      paths
        .filter((p) => !onlyExtensions || onlyExtensions.includes(extname(p).slice(1)))
        .map(async (p) => ({
          path: p,
          content: await this.storageService.getFileContent(fs, p),
        }))
    );

    const zip = new JSZip();
    for (const file of files) {
      zip.file(relative(workspaceRootDirPath, file.path), file.content);
    }

    return await zip.generateAsync({ type: "blob" });
  }

  public async createOrOverwriteFile(
    fs: KieSandboxWorkspacesFs,
    file: WorkspaceWorkerFile,
    broadcaster: BroadcasterDispatch
  ): Promise<void> {
    await this.storageService.createOrOverwriteFile(
      fs,
      this.toStorageFile(file, async () => file.content)
    );
    await this.descriptorsFsService.withReadWriteInMemoryFs(async ({ fs }) => {
      await this.workspaceDescriptorService.bumpLastUpdatedDate(fs, file.workspaceId);
    });

    broadcaster.broadcast({
      channel: file.workspaceId,
      message: async () => ({
        type: "ADD_FILE",
        relativePath: file.relativePath,
      }),
    });

    broadcaster.broadcast({
      channel: this.getUniqueFileIdentifier(file),
      message: async () => ({
        type: "ADD",
        relativePath: file.relativePath,
      }),
    });
  }

  public async getFile(args: {
    fs: KieSandboxWorkspacesFs;
    workspaceId: string;
    relativePath: string;
  }): Promise<WorkspaceWorkerFile | undefined> {
    const absolutePath = this.getAbsolutePath(args);
    const storageFile = await this.storageService.getFile(args.fs, absolutePath);
    if (!storageFile) {
      return;
    }
    return this.toWorkspaceFile(args.workspaceId, storageFile);
  }

  public async updateFile(
    fs: KieSandboxWorkspacesFs,
    wwfd: WorkspaceWorkerFileDescriptor,
    getNewContents: () => Promise<string>,
    broadcaster: BroadcasterDispatch
  ): Promise<void> {
    await this.storageService.updateFile(fs, this.getAbsolutePath(wwfd), () =>
      getNewContents().then((c) => encoder.encode(c))
    );
    await this.descriptorsFsService.withReadWriteInMemoryFs(async ({ fs }) => {
      await this.workspaceDescriptorService.bumpLastUpdatedDate(fs, wwfd.workspaceId);
    });

    broadcaster.broadcast({
      channel: this.getUniqueFileIdentifier(wwfd),
      message: async () => ({
        type: "UPDATE",
        relativePath: wwfd.relativePath,
      }),
    });

    broadcaster.broadcast({
      channel: wwfd.workspaceId,
      message: async () => ({
        type: "UPDATE_FILE",
        relativePath: wwfd.relativePath,
      }),
    });
  }

  public async deleteFile(
    fs: KieSandboxWorkspacesFs,
    wwfd: WorkspaceWorkerFileDescriptor,
    broadcaster: BroadcasterDispatch
  ): Promise<void> {
    await this.storageService.deleteFile(fs, this.toExistingStorageFile(fs, wwfd).path);
    await this.descriptorsFsService.withReadWriteInMemoryFs(async ({ fs }) => {
      await this.workspaceDescriptorService.bumpLastUpdatedDate(fs, wwfd.workspaceId);
    });

    broadcaster.broadcast({
      channel: this.getUniqueFileIdentifier(wwfd),
      message: async () => ({
        type: "DELETE",
        relativePath: wwfd.relativePath,
      }),
    });

    broadcaster.broadcast({
      channel: wwfd.workspaceId,
      message: async () => ({
        type: "DELETE_FILE",
        relativePath: wwfd.relativePath,
      }),
    });
  }

  public async renameFile(args: {
    fs: KieSandboxWorkspacesFs;
    wwfd: WorkspaceWorkerFileDescriptor;
    newFileNameWithoutExtension: string;
    broadcaster: BroadcasterDispatch;
  }): Promise<WorkspaceWorkerFileDescriptor> {
    const renamedStorageFile = await this.storageService.renameFile(
      args.fs,
      this.toExistingStorageFile(args.fs, args.wwfd),
      args.newFileNameWithoutExtension
    );
    const renamedWorkspaceFile = await this.toWorkspaceFile(args.wwfd.workspaceId, renamedStorageFile);

    await this.descriptorsFsService.withReadWriteInMemoryFs(async ({ fs }) => {
      await this.workspaceDescriptorService.bumpLastUpdatedDate(fs, args.wwfd.workspaceId);
    });

    args.broadcaster.broadcast({
      channel: this.getUniqueFileIdentifier(args.wwfd),
      message: async () => ({
        type: "RENAME",
        oldRelativePath: args.wwfd.relativePath,
        newRelativePath: renamedWorkspaceFile.relativePath,
      }),
    });

    args.broadcaster.broadcast({
      channel: this.getUniqueFileIdentifier(renamedWorkspaceFile),
      message: async () => ({
        type: "ADD",
        relativePath: renamedWorkspaceFile.relativePath,
      }),
    });

    args.broadcaster.broadcast({
      channel: args.wwfd.workspaceId,
      message: async () => ({
        type: "RENAME_FILE",
        oldRelativePath: args.wwfd.relativePath,
        newRelativePath: renamedWorkspaceFile.relativePath,
      }),
    });
    return renamedWorkspaceFile;
  }

  public async existsFile(args: {
    fs: KieSandboxWorkspacesFs;
    workspaceId: string;
    relativePath: string;
  }): Promise<boolean> {
    return this.storageService.exists(args.fs, this.getAbsolutePath(args));
  }

  public getAbsolutePath(args: { workspaceId: string; relativePath?: string }) {
    return join("/", this.fsService.getMountPoint(args.workspaceId), args.relativePath ?? "");
  }

  public async deleteFiles(
    fs: KieSandboxWorkspacesFs,
    files: WorkspaceWorkerFile[],
    broadcaster: BroadcasterDispatch
  ): Promise<void> {
    if (files.length === 0) {
      return;
    }

    await Promise.all(
      files.map((f) => this.toExistingStorageFile(fs, f)).map((f) => this.storageService.deleteFile(fs, f.path))
    );

    await this.descriptorsFsService.withReadWriteInMemoryFs(async ({ fs }) => {
      await this.workspaceDescriptorService.bumpLastUpdatedDate(fs, files[0].workspaceId);
    });

    const relativePaths = files.map((file) => file.relativePath);

    broadcaster.broadcast({
      channel: files[0].workspaceId,
      message: async () => ({
        type: "DELETE_BATCH",
        workspaceId: files[0].workspaceId,
        relativePaths,
      }),
    });
  }

  public async moveFiles(
    fs: KieSandboxWorkspacesFs,
    files: WorkspaceWorkerFile[],
    newDirPath: string,
    broadcaster: BroadcasterDispatch
  ): Promise<void> {
    if (files.length === 0) {
      return;
    }

    const relativePaths = await this.storageService.moveFiles(
      fs,
      files.map((f) => this.toExistingStorageFile(fs, f)),
      newDirPath
    );

    await this.descriptorsFsService.withReadWriteInMemoryFs(async ({ fs }) => {
      await this.workspaceDescriptorService.bumpLastUpdatedDate(fs, files[0].workspaceId);
    });

    broadcaster.broadcast({
      channel: files[0].workspaceId,
      message: async () => ({
        type: "MOVE_BATCH",
        workspaceId: files[0].workspaceId,
        relativePaths,
      }),
    });
  }

  private async toWorkspaceFile(workspaceId: string, storageFile: StorageFile): Promise<WorkspaceWorkerFile> {
    return {
      workspaceId,
      content: await storageFile.getFileContents(),
      relativePath: relative(this.getAbsolutePath({ workspaceId }), storageFile.path),
    };
  }

  private toStorageFile(wwfd: WorkspaceWorkerFileDescriptor, getFileContents: () => Promise<Uint8Array>): StorageFile {
    return new StorageFile({
      path: this.getAbsolutePath(wwfd),
      getFileContents,
    });
  }

  private toExistingStorageFile(fs: KieSandboxWorkspacesFs, wwfd: WorkspaceWorkerFileDescriptor): StorageFile {
    return this.toStorageFile(wwfd, async () => this.storageService.getFileContent(fs, this.getAbsolutePath(wwfd)));
  }

  public getUniqueFileIdentifier(args: { workspaceId: string; relativePath: string }) {
    return args.workspaceId + "__" + this.getAbsolutePath(args);
  }
}
