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

import { encoder } from "../encoderdecoder/EncoderDecoder";
import { downloadZip, predictLength } from "client-zip";
import { WorkspaceDescriptor } from "../worker/api/WorkspaceDescriptor";
import { StorageFile, StorageService } from "./StorageService";
import { basename, join, relative } from "path";
import { Minimatch } from "minimatch";
import { WorkspaceDescriptorService } from "./WorkspaceDescriptorService";
import { BroadcasterDispatch } from "./FsService";
import { WorkspaceOrigin } from "../worker/api/WorkspaceOrigin";
import { WorkspaceWorkerFileDescriptor } from "../worker/api/WorkspaceWorkerFileDescriptor";
import { WorkspaceWorkerFile } from "../worker/api/WorkspaceWorkerFile";
import { KieSandboxWorkspacesFs } from "./KieSandboxWorkspaceFs";
import { WorkspaceDescriptorFsService } from "./WorkspaceDescriptorFsService";
import { WorkspaceFsService } from "./WorkspaceFsService";
import {
  WORKSPACES_BROADCAST_CHANNEL,
  WORKSPACES_FILES_BROADCAST_CHANNEL,
} from "../worker/api/WorkspacesBroadcastEvents";
import { FsSchema } from "./FsCache";
import { extractExtension } from "../relativePath/WorkspaceFileRelativePathParser";

export class WorkspaceService {
  public constructor(
    public readonly storageService: StorageService,
    private readonly descriptorsFsService: WorkspaceDescriptorFsService,
    private readonly workspaceDescriptorService: WorkspaceDescriptorService,
    private readonly fsService: WorkspaceFsService
  ) {}

  public async create(args: {
    storeFiles: (
      fs: KieSandboxWorkspacesFs,
      schema: FsSchema,
      workspace: WorkspaceDescriptor
    ) => Promise<WorkspaceWorkerFileDescriptor[]>;
    origin: WorkspaceOrigin;
    preferredName?: string;
    gitAuthSessionId: string | undefined;
  }) {
    const workspace = await this.descriptorsFsService.withReadWriteInMemoryFs(({ fs }) => {
      return this.workspaceDescriptorService.create({
        fs,
        origin: args.origin,
        preferredName: args.preferredName,
        gitAuthSessionId: args.gitAuthSessionId,
      });
    });

    try {
      return await this.fsService.withReadWriteInMemoryFs(
        workspace.workspaceId,
        async ({ fs, schema, broadcaster }) => {
          const files = await args.storeFiles(fs, schema, workspace);

          broadcaster.broadcast({
            channel: WORKSPACES_BROADCAST_CHANNEL,
            message: async () => ({
              type: "WSS_ADD_WORKSPACE",
              workspaceId: workspace.workspaceId,
            }),
          });

          broadcaster.broadcast({
            channel: workspace.workspaceId,
            message: async () => ({
              type: "WS_ADD",
              workspaceId: workspace.workspaceId,
            }),
          });

          return { workspace, files };
        }
      );
    } catch (e) {
      await this.delete(workspace.workspaceId);
      throw e;
    }
  }

  public async getFilteredWorkspaceFileDescriptors(
    schema: FsSchema,
    workspaceId: string,
    globPattern?: string
  ): Promise<WorkspaceWorkerFileDescriptor[]> {
    const matcher = globPattern ? new Minimatch(globPattern, { dot: true }) : undefined;
    const gitDirAbsolutePath = this.getAbsolutePath({ workspaceId, relativePath: ".git" });

    return this.storageService.walk({
      schema,
      baseAbsolutePath: this.getAbsolutePath({ workspaceId }),
      shouldExcludeAbsolutePath: (absolutePath) => absolutePath.startsWith(gitDirAbsolutePath),
      onVisit: async ({ relativePath }) => {
        if (matcher && !matcher.match(basename(relativePath))) {
          return undefined;
        } else {
          return { workspaceId, relativePath };
        }
      },
    });
  }

  public async delete(workspaceId: string): Promise<void> {
    await this.descriptorsFsService.withReadWriteInMemoryFs(async ({ fs, broadcaster }) => {
      await this.workspaceDescriptorService.delete(fs, workspaceId);

      indexedDB.deleteDatabase(this.fsService.getFsMountPoint(workspaceId));
      indexedDB.deleteDatabase(this.fsService.getFsSchemaMountPoint(workspaceId));

      broadcaster.broadcast({
        channel: WORKSPACES_BROADCAST_CHANNEL,
        message: async () => ({ type: "WSS_DELETE_WORKSPACE", workspaceId }),
      });

      broadcaster.broadcast({
        channel: workspaceId,
        message: async () => ({ type: "WS_DELETE", workspaceId }),
      });
    });
  }

  public async rename(workspaceId: string, newName: string): Promise<void> {
    await this.descriptorsFsService.withReadWriteInMemoryFs(async ({ fs, broadcaster }) => {
      await this.workspaceDescriptorService.rename(fs, workspaceId, newName);
      await this.workspaceDescriptorService.bumpLastUpdatedDate(fs, workspaceId);

      broadcaster.broadcast({
        channel: WORKSPACES_BROADCAST_CHANNEL,
        message: async () => ({ type: "WSS_RENAME_WORKSPACE", workspaceId }),
      });

      broadcaster.broadcast({
        channel: workspaceId,
        message: async () => ({ type: "WS_RENAME", workspaceId }),
      });
    });
  }

  public async prepareZip(
    fs: KieSandboxWorkspacesFs,
    schema: FsSchema,
    workspaceId: string,
    onlyExtensions?: string[]
  ): Promise<Blob> {
    const wwfds = await this.getFilteredWorkspaceFileDescriptors(schema, workspaceId);

    const filesToZip = (
      await Promise.all(
        wwfds
          .filter((wwfd) => !onlyExtensions || onlyExtensions.includes(extractExtension(wwfd.relativePath)))
          .map(async (wwfd) => ({
            relativePath: wwfd.relativePath,
            content: await this.storageService.getFileContent(fs, this.getAbsolutePath(wwfd)),
          }))
      )
    ).map((file) => ({ name: file.relativePath, input: file.content }));

    return downloadZip(filesToZip, { length: predictLength(filesToZip) }).blob();
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
        type: "WS_ADD_FILE",
        relativePath: file.relativePath,
      }),
    });

    broadcaster.broadcast({
      channel: this.getUniqueFileIdentifier(file),
      message: async () => ({
        type: "WSF_ADD",
        relativePath: file.relativePath,
      }),
    });

    broadcaster.broadcast({
      channel: WORKSPACES_FILES_BROADCAST_CHANNEL,
      message: async () => ({
        type: "WSSFS_ADD",
        workspaceId: file.workspaceId,
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
        type: "WSF_UPDATE",
        relativePath: wwfd.relativePath,
      }),
    });

    broadcaster.broadcast({
      channel: wwfd.workspaceId,
      message: async () => ({
        type: "WS_UPDATE_FILE",
        relativePath: wwfd.relativePath,
      }),
    });

    broadcaster.broadcast({
      channel: WORKSPACES_FILES_BROADCAST_CHANNEL,
      message: async () => ({
        type: "WSSFS_UPDATE",
        workspaceId: wwfd.workspaceId,
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
        type: "WSF_DELETE",
        relativePath: wwfd.relativePath,
      }),
    });

    broadcaster.broadcast({
      channel: wwfd.workspaceId,
      message: async () => ({
        type: "WS_DELETE_FILE",
        relativePath: wwfd.relativePath,
      }),
    });

    broadcaster.broadcast({
      channel: WORKSPACES_FILES_BROADCAST_CHANNEL,
      message: async () => ({
        type: "WSSFS_DELETE",
        workspaceId: wwfd.workspaceId,
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
        type: "WSF_RENAME",
        oldRelativePath: args.wwfd.relativePath,
        newRelativePath: renamedWorkspaceFile.relativePath,
      }),
    });

    args.broadcaster.broadcast({
      channel: this.getUniqueFileIdentifier(renamedWorkspaceFile),
      message: async () => ({
        type: "WSF_ADD",
        relativePath: renamedWorkspaceFile.relativePath,
      }),
    });

    args.broadcaster.broadcast({
      channel: args.wwfd.workspaceId,
      message: async () => ({
        type: "WS_RENAME_FILE",
        oldRelativePath: args.wwfd.relativePath,
        newRelativePath: renamedWorkspaceFile.relativePath,
      }),
    });

    args.broadcaster.broadcast({
      channel: WORKSPACES_FILES_BROADCAST_CHANNEL,
      message: async () => ({
        type: "WSSFS_RENAME",
        workspaceId: args.wwfd.workspaceId,
        oldRelativePath: args.wwfd.relativePath,
        newRelativePath: renamedWorkspaceFile.relativePath,
      }),
    });

    args.broadcaster.broadcast({
      channel: WORKSPACES_FILES_BROADCAST_CHANNEL,
      message: async () => ({
        type: "WSSFS_ADD",
        workspaceId: args.wwfd.workspaceId,
        relativePath: renamedWorkspaceFile.relativePath,
      }),
    });

    return renamedWorkspaceFile;
  }

  public async moveFile(args: {
    fs: KieSandboxWorkspacesFs;
    wwfd: WorkspaceWorkerFileDescriptor;
    newDirPath: string;
    broadcaster: BroadcasterDispatch;
  }): Promise<WorkspaceWorkerFile> {
    const movedStorageFile = await this.storageService.moveFile(
      args.fs,
      this.toExistingStorageFile(args.fs, args.wwfd),
      this.getAbsolutePath({ workspaceId: args.wwfd.workspaceId, relativePath: args.newDirPath })
    );
    const movedWorkspaceFile = await this.toWorkspaceFile(args.wwfd.workspaceId, movedStorageFile);

    await this.descriptorsFsService.withReadWriteInMemoryFs(async ({ fs }) => {
      await this.workspaceDescriptorService.bumpLastUpdatedDate(fs, args.wwfd.workspaceId);
    });

    args.broadcaster.broadcast({
      channel: this.getUniqueFileIdentifier(args.wwfd),
      message: async () => ({
        type: "WSF_MOVE",
        oldRelativePath: args.wwfd.relativePath,
        newRelativePath: movedWorkspaceFile.relativePath,
      }),
    });

    args.broadcaster.broadcast({
      channel: this.getUniqueFileIdentifier(movedWorkspaceFile),
      message: async () => ({
        type: "WSF_ADD",
        relativePath: movedWorkspaceFile.relativePath,
      }),
    });

    args.broadcaster.broadcast({
      channel: args.wwfd.workspaceId,
      message: async () => ({
        type: "WS_MOVE_FILE",
        oldRelativePath: args.wwfd.relativePath,
        newRelativePath: movedWorkspaceFile.relativePath,
      }),
    });

    args.broadcaster.broadcast({
      channel: WORKSPACES_FILES_BROADCAST_CHANNEL,
      message: async () => ({
        type: "WSSFS_MOVE",
        workspaceId: args.wwfd.workspaceId,
        oldRelativePath: args.wwfd.relativePath,
        newRelativePath: movedWorkspaceFile.relativePath,
      }),
    });

    args.broadcaster.broadcast({
      channel: WORKSPACES_FILES_BROADCAST_CHANNEL,
      message: async () => ({
        type: "WSSFS_ADD",
        workspaceId: args.wwfd.workspaceId,
        relativePath: movedWorkspaceFile.relativePath,
      }),
    });

    return movedWorkspaceFile;
  }

  public async existsFile(args: {
    fs: KieSandboxWorkspacesFs;
    workspaceId: string;
    relativePath: string;
  }): Promise<boolean> {
    return this.storageService.exists(args.fs, this.getAbsolutePath(args));
  }

  public getAbsolutePath(args: { workspaceId: string; relativePath?: string }) {
    return join("/", this.fsService.getFsMountPoint(args.workspaceId), args.relativePath ?? "");
  }

  public async toWorkspaceFile(workspaceId: string, storageFile: StorageFile): Promise<WorkspaceWorkerFile> {
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
    return `${args.workspaceId}__${this.getAbsolutePath(args)}`;
  }
}
