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
import { WorkspaceDescriptor } from "../model/WorkspaceDescriptor";
import { StorageFile, StorageService } from "./StorageService";
import { WorkspaceEvents } from "../hooks/WorkspaceHooks";
import { WorkspacesEvents } from "../hooks/WorkspacesHooks";
import { WorkspaceFileEvents } from "../hooks/WorkspaceFileHooks";
import { extname, join, relative } from "path";
import { Minimatch } from "minimatch";
import KieSandboxFs from "@kie-tools/kie-sandbox-fs";
import { WorkspaceDescriptorService } from "./WorkspaceDescriptorService";
import { WorkspaceFsService } from "./WorkspaceFsService";
import { WorkspaceOrigin } from "../model/WorkspaceOrigin";
import {
  WorkspaceWorkerFile,
  WorkspaceWorkerFileDescriptor,
} from "../../workspacesWorker/api/WorkspaceWorkerFileDescriptor";

export const WORKSPACES_BROADCAST_CHANNEL = "workspaces";

export class WorkspaceService {
  public constructor(
    public readonly storageService: StorageService,
    private readonly workspaceDescriptorService: WorkspaceDescriptorService,
    private readonly fsService: WorkspaceFsService
  ) {}

  public async create(args: {
    useInMemoryFs: boolean;
    storeFiles: (fs: KieSandboxFs, workspace: WorkspaceDescriptor) => Promise<WorkspaceFile[]>;
    broadcastArgs: { broadcast: boolean };
    origin: WorkspaceOrigin;
    preferredName?: string;
  }) {
    const workspace = await this.workspaceDescriptorService.create({
      origin: args.origin,
      preferredName: args.preferredName,
    });

    try {
      const files = await (args.useInMemoryFs
        ? this.fsService.withInMemoryFs(workspace.workspaceId, (fs) => args.storeFiles(fs, workspace))
        : args.storeFiles(await this.fsService.getWorkspaceFs(workspace.workspaceId), workspace));

      if (args.broadcastArgs.broadcast) {
        const broadcastChannel1 = new BroadcastChannel(WORKSPACES_BROADCAST_CHANNEL);
        const broadcastChannel2 = new BroadcastChannel(workspace.workspaceId);
        broadcastChannel1.postMessage({
          type: "ADD_WORKSPACE",
          workspaceId: workspace.workspaceId,
        } as WorkspacesEvents);
        broadcastChannel2.postMessage({ type: "ADD", workspaceId: workspace.workspaceId } as WorkspaceEvents);
      }

      return { workspace, files };
    } catch (e) {
      await this.workspaceDescriptorService.delete(workspace.workspaceId);
      throw e;
    }
  }

  public async getFilesWithLazyContent(
    fs: KieSandboxFs,
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

  public async delete(workspaceId: string, broadcastArgs: { broadcast: boolean }): Promise<void> {
    await this.workspaceDescriptorService.delete(workspaceId);
    indexedDB.deleteDatabase(workspaceId);

    if (broadcastArgs.broadcast) {
      const broadcastChannel1 = new BroadcastChannel(WORKSPACES_BROADCAST_CHANNEL);
      const broadcastChannel2 = new BroadcastChannel(workspaceId);
      broadcastChannel1.postMessage({ type: "DELETE_WORKSPACE", workspaceId } as WorkspacesEvents);
      broadcastChannel2.postMessage({ type: "DELETE", workspaceId } as WorkspaceEvents);
    }
  }

  public async rename(workspaceId: string, newName: string, broadcastArgs: { broadcast: boolean }): Promise<void> {
    await this.workspaceDescriptorService.rename(workspaceId, newName);

    if (broadcastArgs.broadcast) {
      await this.workspaceDescriptorService.bumpLastUpdatedDate(workspaceId);

      const broadcastChannel1 = new BroadcastChannel(WORKSPACES_BROADCAST_CHANNEL);
      const broadcastChannel2 = new BroadcastChannel(workspaceId);
      broadcastChannel1.postMessage({ type: "RENAME_WORKSPACE", workspaceId } as WorkspacesEvents);
      broadcastChannel2.postMessage({ type: "RENAME", workspaceId } as WorkspaceEvents);
    }
  }

  public async prepareZip(fs: KieSandboxFs, workspaceId: string, onlyExtensions?: string[]): Promise<Blob> {
    const workspaceRootDirPath = this.getAbsolutePath({ workspaceId });

    const gitDirPath = this.getAbsolutePath({ workspaceId, relativePath: ".git" });
    const paths = await this.storageService.walk({
      fs,
      startFromDirPath: workspaceRootDirPath,
      shouldExcludeDir: (dirPath) => dirPath === gitDirPath,
      onVisit: async ({ absolutePath }) => absolutePath,
    });

    const files = (await this.storageService.getFiles(fs, paths)).filter(
      (f) => !onlyExtensions || onlyExtensions.includes(extname(f.path).slice(1))
    );

    const zip = new JSZip();
    for (const file of files) {
      zip.file(relative(workspaceRootDirPath, file.path), file.content);
    }

    return await zip.generateAsync({ type: "blob" });
  }

  public async createOrOverwriteFile(
    fs: KieSandboxFs,
    file: WorkspaceWorkerFile,
    broadcastArgs: { broadcast: boolean }
  ): Promise<void> {
    await this.storageService.createOrOverwriteFile(
      fs,
      this.toStorageFile(file, async () => file.content)
    );

    if (broadcastArgs.broadcast) {
      await this.workspaceDescriptorService.bumpLastUpdatedDate(file.workspaceId);

      const broadcastChannel1 = new BroadcastChannel(this.getUniqueFileIdentifier(file));
      const broadcastChannel2 = new BroadcastChannel(file.workspaceId);
      broadcastChannel1.postMessage({
        type: "ADD",
        relativePath: file.relativePath,
      } as WorkspaceFileEvents);
      broadcastChannel2.postMessage({
        type: "ADD_FILE",
        relativePath: file.relativePath,
      } as WorkspaceEvents);
    }
  }

  public async getFile(args: {
    fs: KieSandboxFs;
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
    fs: KieSandboxFs,
    wwfd: WorkspaceWorkerFileDescriptor,
    getNewContents: () => Promise<string>,
    broadcastArgs: { broadcast: boolean }
  ): Promise<void> {
    await this.storageService.updateFile(fs, this.getAbsolutePath(wwfd), () =>
      getNewContents().then((c) => encoder.encode(c))
    );

    if (broadcastArgs.broadcast) {
      await this.workspaceDescriptorService.bumpLastUpdatedDate(wwfd.workspaceId);

      const broadcastChannel1 = new BroadcastChannel(this.getUniqueFileIdentifier(wwfd));
      const broadcastChannel2 = new BroadcastChannel(wwfd.workspaceId);
      broadcastChannel1.postMessage({
        type: "UPDATE",
        relativePath: wwfd.relativePath,
      } as WorkspaceFileEvents);
      broadcastChannel2.postMessage({
        type: "UPDATE_FILE",
        relativePath: wwfd.relativePath,
      } as WorkspaceEvents);
    }
  }

  public async deleteFile(
    fs: KieSandboxFs,
    wwfd: WorkspaceWorkerFileDescriptor,
    broadcastArgs: { broadcast: boolean }
  ): Promise<void> {
    await this.storageService.deleteFile(fs, this.toExistingStorageFile(wwfd).path);

    if (broadcastArgs.broadcast) {
      await this.workspaceDescriptorService.bumpLastUpdatedDate(wwfd.workspaceId);

      const broadcastChannel1 = new BroadcastChannel(this.getUniqueFileIdentifier(wwfd));
      const broadcastChannel2 = new BroadcastChannel(wwfd.workspaceId);
      broadcastChannel1.postMessage({
        type: "DELETE",
        relativePath: wwfd.relativePath,
      } as WorkspaceFileEvents);
      broadcastChannel2.postMessage({
        type: "DELETE_FILE",
        relativePath: wwfd.relativePath,
      } as WorkspaceEvents);
    }
  }

  public async renameFile(args: {
    fs: KieSandboxFs;
    wwfd: WorkspaceWorkerFileDescriptor;
    newFileNameWithoutExtension: string;
    broadcastArgs: { broadcast: boolean };
  }): Promise<WorkspaceWorkerFileDescriptor> {
    const renamedStorageFile = await this.storageService.renameFile(
      args.fs,
      this.toExistingStorageFile(args.wwfd),
      args.newFileNameWithoutExtension
    );
    const renamedWorkspaceFile = await this.toWorkspaceFile(args.wwfd.workspaceId, renamedStorageFile);

    if (args.broadcastArgs.broadcast) {
      await this.workspaceDescriptorService.bumpLastUpdatedDate(args.wwfd.workspaceId);

      const broadcastChannel1 = new BroadcastChannel(this.getUniqueFileIdentifier(args.wwfd));
      const broadcastChannel2 = new BroadcastChannel(this.getUniqueFileIdentifier(renamedWorkspaceFile));
      const broadcastChannel3 = new BroadcastChannel(args.wwfd.workspaceId);
      broadcastChannel1.postMessage({
        type: "RENAME",
        oldRelativePath: args.wwfd.relativePath,
        newRelativePath: renamedWorkspaceFile.relativePath,
      } as WorkspaceFileEvents);
      broadcastChannel2.postMessage({
        type: "ADD",
        relativePath: renamedWorkspaceFile.relativePath,
      } as WorkspaceFileEvents);
      broadcastChannel3.postMessage({
        type: "RENAME_FILE",
        oldRelativePath: args.wwfd.relativePath,
        newRelativePath: renamedWorkspaceFile.relativePath,
      } as WorkspaceEvents);
    }

    return renamedWorkspaceFile;
  }

  public async existsFile(args: { fs: KieSandboxFs; workspaceId: string; relativePath: string }): Promise<boolean> {
    return this.storageService.exists(args.fs, this.getAbsolutePath(args));
  }

  public getAbsolutePath(args: { workspaceId: string; relativePath?: string }) {
    return join("/", args.relativePath ?? "");
  }

  public async deleteFiles(
    fs: KieSandboxFs,
    files: WorkspaceWorkerFile[],
    broadcastArgs: { broadcast: boolean }
  ): Promise<void> {
    if (files.length === 0) {
      return;
    }

    await Promise.all(
      files.map((f) => this.toExistingStorageFile(f)).map((f) => this.storageService.deleteFile(fs, f.path))
    );

    if (broadcastArgs.broadcast) {
      await this.workspaceDescriptorService.bumpLastUpdatedDate(files[0].workspaceId);

      const relativePaths = files.map((file) => file.relativePath);
      const broadcastChannel = new BroadcastChannel(files[0].workspaceId);
      broadcastChannel.postMessage({
        type: "DELETE_BATCH",
        workspaceId: files[0].workspaceId,
        relativePaths,
      } as WorkspaceEvents);
    }
  }

  public async moveFiles(
    fs: KieSandboxFs,
    files: WorkspaceWorkerFile[],
    newDirPath: string,
    broadcastArgs: { broadcast: boolean }
  ): Promise<void> {
    if (files.length === 0) {
      return;
    }

    const relativePaths = await this.storageService.moveFiles(
      fs,
      files.map((f) => this.toExistingStorageFile(f)),
      newDirPath
    );

    if (broadcastArgs.broadcast) {
      await this.workspaceDescriptorService.bumpLastUpdatedDate(files[0].workspaceId);

      const broadcastChannel = new BroadcastChannel(files[0].workspaceId);
      broadcastChannel.postMessage({
        type: "MOVE_BATCH",
        workspaceId: files[0].workspaceId,
        relativePaths,
      } as WorkspaceEvents);
    }
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

  private toExistingStorageFile(wwfd: WorkspaceWorkerFileDescriptor): StorageFile {
    return this.toStorageFile(wwfd, async () =>
      this.storageService.getFileContent(
        await this.fsService.getWorkspaceFs(wwfd.workspaceId),
        this.getAbsolutePath(wwfd)
      )
    );
  }

  public getUniqueFileIdentifier(args: { workspaceId: string; relativePath: string }) {
    return args.workspaceId + "__" + this.getAbsolutePath(args); // FIXME: DUPLICATED
  }
}
