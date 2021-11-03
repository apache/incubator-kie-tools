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
import { join, relative } from "path";
import { Minimatch } from "minimatch";
import LightningFS from "@isomorphic-git/lightning-fs";
import { WorkspaceDescriptorService } from "./WorkspaceDescriptorService";
import { WorkspaceFsService } from "./WorkspaceFsService";
import { WorkspaceOrigin } from "../model/WorkspaceOrigin";

export class WorkspaceService {
  public constructor(
    public readonly storageService: StorageService,
    private readonly workspaceDescriptorService: WorkspaceDescriptorService,
    private readonly fsService: WorkspaceFsService
  ) {}

  public async create(args: {
    useInMemoryFs: boolean;
    storeFiles: (fs: LightningFS, workspace: WorkspaceDescriptor) => Promise<WorkspaceFile[]>;
    broadcastArgs: { broadcast: boolean };
    origin: WorkspaceOrigin;
    preferredName?: string;
  }) {
    const workspace = await this.workspaceDescriptorService.create({
      origin: args.origin,
      preferredName: args.preferredName,
    });

    const files = await (args.useInMemoryFs
      ? this.fsService.withInMemoryFs(workspace.workspaceId, (fs) => args.storeFiles(fs, workspace))
      : args.storeFiles(await this.fsService.getWorkspaceFs(workspace.workspaceId), workspace));

    if (args.broadcastArgs.broadcast) {
      const broadcastChannel1 = new BroadcastChannel("workspaces");
      const broadcastChannel2 = new BroadcastChannel(workspace.workspaceId);
      broadcastChannel1.postMessage({
        type: "ADD_WORKSPACE",
        workspaceId: workspace.workspaceId,
      } as WorkspacesEvents);
      broadcastChannel2.postMessage({ type: "ADD", workspaceId: workspace.workspaceId } as WorkspaceEvents);
    }

    return { workspace, files };
  }

  public async getFilesWithLazyContent(
    fs: LightningFS,
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
      const broadcastChannel1 = new BroadcastChannel("workspaces");
      const broadcastChannel2 = new BroadcastChannel(workspaceId);
      broadcastChannel1.postMessage({ type: "DELETE_WORKSPACE", workspaceId } as WorkspacesEvents);
      broadcastChannel2.postMessage({ type: "DELETE", workspaceId } as WorkspaceEvents);
    }
  }

  public async rename(workspaceId: string, newName: string, broadcastArgs: { broadcast: boolean }): Promise<void> {
    await this.workspaceDescriptorService.rename(workspaceId, newName);

    if (broadcastArgs.broadcast) {
      await this.workspaceDescriptorService.bumpLastUpdatedDate(workspaceId);

      const broadcastChannel1 = new BroadcastChannel("workspaces");
      const broadcastChannel2 = new BroadcastChannel(workspaceId);
      broadcastChannel1.postMessage({ type: "RENAME_WORKSPACE", workspaceId } as WorkspacesEvents);
      broadcastChannel2.postMessage({ type: "RENAME", workspaceId } as WorkspaceEvents);
    }
  }

  public async prepareZip(fs: LightningFS, workspaceId: string): Promise<Blob> {
    const workspaceRootDirPath = this.getAbsolutePath({ workspaceId });

    const gitDirPath = this.getAbsolutePath({ workspaceId, relativePath: ".git" });
    const paths = await this.storageService.walk({
      fs,
      startFromDirPath: workspaceRootDirPath,
      shouldExcludeDir: (dirPath) => dirPath === gitDirPath,
      onVisit: async ({ absolutePath }) => absolutePath,
    });

    const files = await this.storageService.getFiles(fs, paths);

    const zip = new JSZip();
    for (const file of files) {
      zip.file(relative(workspaceRootDirPath, file.path), file.content);
    }

    console.time(`WorkspaceService#prepareZip#generateAsync--${workspaceId}`);
    const blob = await zip.generateAsync({ type: "blob" });
    console.timeEnd(`WorkspaceService#prepareZip#generateAsync--${workspaceId}`);
    return blob;
  }
  //

  public async createOrOverwriteFile(
    fs: LightningFS,
    file: WorkspaceFile,
    broadcastArgs: { broadcast: boolean }
  ): Promise<void> {
    await this.storageService.createOrOverwriteFile(fs, this.toStorageFile(file));

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
    fs: LightningFS;
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

  public async updateFile(
    fs: LightningFS,
    file: WorkspaceFile,
    getNewContents: () => Promise<string>,
    broadcastArgs: { broadcast: boolean }
  ): Promise<void> {
    await this.storageService.updateFile(
      fs,
      this.toStorageFile(
        new WorkspaceFile({
          relativePath: file.relativePath,
          workspaceId: file.workspaceId,
          getFileContents: () => getNewContents().then((c) => encoder.encode(c)),
        })
      )
    );

    if (broadcastArgs.broadcast) {
      await this.workspaceDescriptorService.bumpLastUpdatedDate(file.workspaceId);

      const broadcastChannel1 = new BroadcastChannel(this.getUniqueFileIdentifier(file));
      const broadcastChannel2 = new BroadcastChannel(file.workspaceId);
      broadcastChannel1.postMessage({
        type: "UPDATE",
        relativePath: file.relativePath,
      } as WorkspaceFileEvents);
      broadcastChannel2.postMessage({
        type: "UPDATE_FILE",
        relativePath: file.relativePath,
      } as WorkspaceEvents);
    }
  }

  public async deleteFile(fs: LightningFS, file: WorkspaceFile, broadcastArgs: { broadcast: boolean }): Promise<void> {
    await this.storageService.deleteFile(fs, this.toStorageFile(file).path);

    if (broadcastArgs.broadcast) {
      await this.workspaceDescriptorService.bumpLastUpdatedDate(file.workspaceId);

      const broadcastChannel1 = new BroadcastChannel(this.getUniqueFileIdentifier(file));
      const broadcastChannel2 = new BroadcastChannel(file.workspaceId);
      broadcastChannel1.postMessage({
        type: "DELETE",
        relativePath: file.relativePath,
      } as WorkspaceFileEvents);
      broadcastChannel2.postMessage({
        type: "DELETE_FILE",
        relativePath: file.relativePath,
      } as WorkspaceEvents);
    }
  }

  public async renameFile(args: {
    fs: LightningFS;
    file: WorkspaceFile;
    newFileNameWithoutExtension: string;
    broadcastArgs: { broadcast: boolean };
  }): Promise<WorkspaceFile> {
    const renamedStorageFile = await this.storageService.renameFile(
      args.fs,
      this.toStorageFile(args.file),
      args.newFileNameWithoutExtension
    );
    const renamedWorkspaceFile = this.toWorkspaceFile(args.file.workspaceId, renamedStorageFile);

    if (args.broadcastArgs.broadcast) {
      await this.workspaceDescriptorService.bumpLastUpdatedDate(args.file.workspaceId);

      const broadcastChannel1 = new BroadcastChannel(this.getUniqueFileIdentifier(args.file));
      const broadcastChannel2 = new BroadcastChannel(this.getUniqueFileIdentifier(renamedWorkspaceFile));
      const broadcastChannel3 = new BroadcastChannel(args.file.workspaceId);
      broadcastChannel1.postMessage({
        type: "RENAME",
        oldRelativePath: args.file.relativePath,
        newRelativePath: renamedWorkspaceFile.relativePath,
      } as WorkspaceFileEvents);
      broadcastChannel2.postMessage({
        type: "ADD",
        relativePath: renamedWorkspaceFile.relativePath,
      } as WorkspaceFileEvents);
      broadcastChannel3.postMessage({
        type: "RENAME_FILE",
        oldRelativePath: args.file.relativePath,
        newRelativePath: renamedWorkspaceFile.relativePath,
      } as WorkspaceEvents);
    }

    return renamedWorkspaceFile;
  }

  public async existsFile(args: { fs: LightningFS; workspaceId: string; relativePath: string }): Promise<boolean> {
    return this.storageService.exists(args.fs, this.getAbsolutePath(args));
  }

  public getAbsolutePath(args: { workspaceId: string; relativePath?: string }) {
    return join("/", args.relativePath ?? "");
  }

  public async deleteFiles(
    fs: LightningFS,
    files: WorkspaceFile[],
    broadcastArgs: { broadcast: boolean }
  ): Promise<void> {
    if (files.length === 0) {
      return;
    }

    await Promise.all(files.map((f) => this.toStorageFile(f)).map((f) => this.storageService.deleteFile(fs, f.path)));

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
    fs: LightningFS,
    files: WorkspaceFile[],
    newDirPath: string,
    broadcastArgs: { broadcast: boolean }
  ): Promise<void> {
    if (files.length === 0) {
      return;
    }

    const relativePaths = await this.storageService.moveFiles(
      fs,
      files.map((f) => this.toStorageFile(f)),
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

  private toWorkspaceFile(workspaceId: string, storageFile: StorageFile): WorkspaceFile {
    return new WorkspaceFile({
      workspaceId,
      getFileContents: storageFile.getFileContents,
      relativePath: relative(this.getAbsolutePath({ workspaceId }), storageFile.path),
    });
  }

  private toStorageFile(workspaceFile: WorkspaceFile): StorageFile {
    return new StorageFile({
      path: this.getAbsolutePath(workspaceFile),
      getFileContents: workspaceFile.getFileContents,
    });
  }

  public getUniqueFileIdentifier(args: { workspaceId: string; relativePath: string }) {
    return args.workspaceId + "__" + this.getAbsolutePath(args);
  }
}
