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
import { SUPPORTED_FILES_PATTERN } from "../SupportedFiles";
import { StorageFile, StorageService } from "./StorageService";
import { WorkspaceEvents } from "../hooks/WorkspaceHooks";
import { WorkspacesEvents } from "../hooks/WorkspacesHooks";
import { WorkspaceFileEvents } from "../hooks/WorkspaceFileHooks";
import { basename, join } from "path";
import { Minimatch } from "minimatch";
import LightningFS from "@isomorphic-git/lightning-fs";
import { WorkspaceDescriptorService } from "./WorkspaceDescriptorService";
import { WorkspaceFsService } from "./WorkspaceFsService";

export class WorkspaceService {
  public constructor(
    public readonly storageService: StorageService,
    private readonly workspaceDescriptorService: WorkspaceDescriptorService,
    private readonly fsService: WorkspaceFsService
  ) {}

  public get rootPath(): string {
    return this.storageService.rootPath;
  }

  public async create(
    storeFiles: (fs: LightningFS, workspace: WorkspaceDescriptor) => Promise<WorkspaceFile[]>,
    broadcastArgs: { broadcast: boolean }
  ) {
    const workspace = await this.workspaceDescriptorService.create();
    const files = await this.fsService.withInMemoryFs(workspace.workspaceId, async (fs) => {
      await this.storageService.createDirStructureAtRoot(fs, workspace.workspaceId);
      return storeFiles(fs, workspace);
    });

    if (broadcastArgs.broadcast) {
      const broadcastChannel1 = new BroadcastChannel(this.rootPath);
      const broadcastChannel2 = new BroadcastChannel(workspace.workspaceId);
      broadcastChannel1.postMessage({
        type: "ADD_WORKSPACE",
        workspaceId: workspace.workspaceId,
      } as WorkspacesEvents);
      broadcastChannel2.postMessage({ type: "ADD", workspaceId: workspace.workspaceId } as WorkspaceEvents);
    }

    return { workspace, files };
  }

  public async getFilesLazy(fs: LightningFS, workspaceId: string, globPattern?: string): Promise<WorkspaceFile[]> {
    console.info(`WorkspaceService#getFilesLazy--${workspaceId}-----------begin`);
    console.time(`WorkspaceService#getFilesLazy--${workspaceId}`);
    const matcher = globPattern ? new Minimatch(globPattern, { dot: true }) : undefined;
    const gitDirPath = this.getAbsolutePath({ workspaceId, relativePath: ".git" });
    const rootDirPath = this.getAbsolutePath({ workspaceId, relativePath: "/" });

    const files = await this.storageService.getFilePaths({
      fs,
      dirPath: await this.resolveRootPath(fs, workspaceId),
      excludeDir: (dirPath) => dirPath === gitDirPath,
      visit: (path) => {
        const workspaceFile = new WorkspaceFile({
          workspaceId,
          relativePath: path.replace(rootDirPath, ""),
          getFileContents: () => this.storageService.getFile(fs, path).then((f) => f!.getFileContents()),
        });

        if (matcher && !matcher.match(workspaceFile.name)) {
          return undefined;
        }

        return workspaceFile;
      },
    });
    console.timeEnd(`WorkspaceService#getFilesLazy--${workspaceId}`);
    return files;
  }

  public async delete(workspaceId: string, broadcastArgs: { broadcast: boolean }): Promise<void> {
    await this.workspaceDescriptorService.delete(workspaceId);
    indexedDB.deleteDatabase(workspaceId);

    if (broadcastArgs.broadcast) {
      const broadcastChannel1 = new BroadcastChannel(this.rootPath);
      const broadcastChannel2 = new BroadcastChannel(workspaceId);
      broadcastChannel1.postMessage({ type: "DELETE_WORKSPACE", workspaceId } as WorkspacesEvents);
      broadcastChannel2.postMessage({ type: "DELETE", workspaceId } as WorkspaceEvents);
    }
  }

  public async rename(workspaceId: string, newName: string, broadcastArgs: { broadcast: boolean }): Promise<void> {
    await this.workspaceDescriptorService.rename(workspaceId, newName);

    if (broadcastArgs.broadcast) {
      await this.workspaceDescriptorService.bumpLastUpdatedDate(workspaceId);

      const broadcastChannel1 = new BroadcastChannel(this.rootPath);
      const broadcastChannel2 = new BroadcastChannel(workspaceId);
      broadcastChannel1.postMessage({ type: "RENAME_WORKSPACE", workspaceId } as WorkspacesEvents);
      broadcastChannel2.postMessage({ type: "RENAME", workspaceId } as WorkspaceEvents);
    }
  }

  public async prepareZip(workspaceId: string): Promise<Blob> {
    console.time(`WorkspaceService#assembleZip--${workspaceId}`);
    const fs = this.fsService.getWorkspaceFs(workspaceId);
    const workspaceRootPath = await this.resolveRootPath(fs, workspaceId);

    const matcher = new Minimatch(SUPPORTED_FILES_PATTERN, { dot: true });
    const gitDirPath = this.getAbsolutePath({ workspaceId, relativePath: ".git" });
    const paths = await this.storageService.getFilePaths({
      fs,
      dirPath: await this.resolveRootPath(fs, workspaceId),
      excludeDir: (dirPath) => dirPath === gitDirPath,
      visit: (path) => (!matcher.match(basename(path)) ? undefined : path),
    });

    const files = await this.storageService.getFiles(fs, paths);

    const zip = new JSZip();
    for (const file of files) {
      zip.file(file.path.replace(workspaceRootPath, ""), file.content);
    }
    console.timeEnd(`WorkspaceService#assembleZip--${workspaceId}`);

    console.time(`WorkspaceService#prepareZip#generateAsync--${workspaceId}`);
    const blob = await zip.generateAsync({ type: "blob" });
    console.timeEnd(`WorkspaceService#prepareZip#generateAsync--${workspaceId}`);
    return blob;
  }

  public async resolveRootPath(fs: LightningFS, workspaceId: string): Promise<string> {
    const workspaceRootPath = this.getAbsolutePath({ workspaceId, relativePath: "" });
    if (!(await this.storageService.exists(fs, workspaceRootPath))) {
      throw new Error(`Root '${workspaceRootPath}' does not exist`);
    }

    return workspaceRootPath;
  }

  //

  public async createFile(file: WorkspaceFile, broadcastArgs: { broadcast: boolean }): Promise<void> {
    await this.storageService.createFile(this.fsService.getWorkspaceFs(file.workspaceId), this.toStorageFile(file));

    if (broadcastArgs.broadcast) {
      await this.workspaceDescriptorService.bumpLastUpdatedDate(file.workspaceId);

      const broadcastChannel1 = new BroadcastChannel(this.getAbsolutePath(file));
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

  public async getFile(args: { workspaceId: string; relativePath: string }): Promise<WorkspaceFile | undefined> {
    const absolutePath = this.getAbsolutePath(args);
    console.info(`Reading file '${absolutePath}'`);
    const storageFile = await this.storageService.getFile(
      this.fsService.getWorkspaceFs(args.workspaceId),
      absolutePath
    );
    if (!storageFile) {
      return;
    }
    return this.toWorkspaceFile(args.workspaceId, storageFile);
  }

  public async updateFile(
    file: WorkspaceFile,
    getNewContents: () => Promise<string>,
    broadcastArgs: { broadcast: boolean }
  ): Promise<void> {
    await this.storageService.updateFile(
      await this.fsService.getWorkspaceFs(file.workspaceId),
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

      const broadcastChannel1 = new BroadcastChannel(this.getAbsolutePath(file));
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

  public async deleteFile(file: WorkspaceFile, broadcastArgs: { broadcast: boolean }): Promise<void> {
    await this.storageService.deleteFile(
      this.fsService.getWorkspaceFs(file.workspaceId),
      this.toStorageFile(file).path
    );

    if (broadcastArgs.broadcast) {
      await this.workspaceDescriptorService.bumpLastUpdatedDate(file.workspaceId);

      const broadcastChannel1 = new BroadcastChannel(this.getAbsolutePath(file));
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

  public async renameFile(
    file: WorkspaceFile,
    newFileName: string,
    broadcastArgs: { broadcast: boolean }
  ): Promise<WorkspaceFile> {
    const renamedStorageFile = await this.storageService.renameFile(
      this.fsService.getWorkspaceFs(file.workspaceId),
      this.toStorageFile(file),
      newFileName
    );
    const renamedWorkspaceFile = this.toWorkspaceFile(file.workspaceId, renamedStorageFile);

    if (broadcastArgs.broadcast) {
      await this.workspaceDescriptorService.bumpLastUpdatedDate(file.workspaceId);

      const broadcastChannel1 = new BroadcastChannel(this.getAbsolutePath(file));
      const broadcastChannel2 = new BroadcastChannel(this.getAbsolutePath(renamedWorkspaceFile));
      const broadcastChannel3 = new BroadcastChannel(file.workspaceId);
      broadcastChannel1.postMessage({
        type: "RENAME",
        oldRelativePath: file.relativePath,
        newRelativePath: renamedWorkspaceFile.relativePath,
      } as WorkspaceFileEvents);
      broadcastChannel2.postMessage({
        type: "ADD",
        relativePath: renamedWorkspaceFile.relativePath,
      } as WorkspaceFileEvents);
      broadcastChannel3.postMessage({
        type: "RENAME_FILE",
        oldRelativePath: file.relativePath,
        newRelativePath: renamedWorkspaceFile.relativePath,
      } as WorkspaceEvents);
    }

    return renamedWorkspaceFile;
  }

  public async moveFile(
    file: WorkspaceFile,
    newDirPath: string,
    broadcastArgs: { broadcast: boolean }
  ): Promise<WorkspaceFile> {
    //FIXME: I'm not sure this works correctly.

    const movedStorageFile = await this.storageService.renameFile(
      await this.fsService.getWorkspaceFs(file.workspaceId),
      this.toStorageFile(file),
      newDirPath
    );
    const movedWorkspaceFile = this.toWorkspaceFile(file.workspaceId, movedStorageFile);

    if (broadcastArgs.broadcast) {
      await this.workspaceDescriptorService.bumpLastUpdatedDate(file.workspaceId);

      const broadcastChannel1 = new BroadcastChannel(this.getAbsolutePath(file));
      const broadcastChannel2 = new BroadcastChannel(file.workspaceId);
      broadcastChannel1.postMessage({
        type: "MOVE",
        oldRelativePath: file.relativePath,
        newRelativePath: movedWorkspaceFile.relativePath,
      } as WorkspaceFileEvents);
      broadcastChannel2.postMessage({
        type: "MOVE_FILE",
        oldRelativePath: file.relativePath,
        newRelativePath: movedWorkspaceFile.relativePath,
      } as WorkspaceEvents);
    }

    return movedWorkspaceFile;
  }

  public async existsFile(args: { fs: LightningFS; workspaceId: string; relativePath: string }): Promise<boolean> {
    return this.storageService.exists(args.fs, this.getAbsolutePath(args));
  }

  public getAbsolutePath(args: { workspaceId: string; relativePath: string }) {
    return join(this.rootPath, args.workspaceId, args.relativePath);
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
      relativePath: storageFile.path.replace(this.getAbsolutePath({ workspaceId, relativePath: "/" }), ""),
    });
  }

  private toStorageFile(workspaceFile: WorkspaceFile): StorageFile {
    return new StorageFile({
      path: this.getAbsolutePath(workspaceFile),
      getFileContents: workspaceFile.getFileContents,
    });
  }
}
