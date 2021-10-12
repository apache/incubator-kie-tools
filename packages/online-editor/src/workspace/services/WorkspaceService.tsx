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

import { WorkspaceFile } from "../WorkspacesContext";
import JSZip from "jszip";
import { FileHandler } from "../handler/FileHandler";
import { WorkspaceDescriptor } from "../model/WorkspaceDescriptor";
import { SUPPORTED_FILES_PATTERN } from "../SupportedFiles";
import { StorageFile, StorageService } from "./StorageService";
import { WorkspaceEvents } from "../hooks/WorkspaceHooks";
import { WorkspacesEvents } from "../hooks/WorkspacesHooks";
import { v4 as uuid } from "uuid";
import { WorkspaceFileEvents } from "../hooks/WorkspaceFileHooks";
import { join } from "path";

export class WorkspaceService {
  private readonly WORKSPACE_CONFIG_PATH = "/workspaces.json";

  public constructor(private readonly storageService: StorageService) {}

  public get rootPath(): string {
    return this.storageService.rootPath;
  }

  public async init(): Promise<void> {
    const configFile = await this.storageService.getFile(this.WORKSPACE_CONFIG_PATH);

    if (!configFile) {
      const freshConfigFile = await this.configAsFile(async () => JSON.stringify([]));
      await this.storageService.createFile(freshConfigFile);
    }
  }

  public async deleteAll() {
    await this.storageService.wipeStorage();
    await this.init();
    const broadcastChannel = new BroadcastChannel(this.rootPath);
    broadcastChannel.postMessage({ type: "DELETE_ALL" } as WorkspacesEvents);
  }

  public async listAll(): Promise<WorkspaceDescriptor[]> {
    const configFile = await this.storageService.getFile(this.WORKSPACE_CONFIG_PATH);

    if (!configFile) {
      throw new Error("Workspaces config file not found");
    }

    const fileContent = await configFile.getFileContents();

    if (!fileContent) {
      throw new Error("No workspaces found");
    }

    return JSON.parse(fileContent) as WorkspaceDescriptor[];
  }

  //

  public newWorkspaceId(): string {
    return uuid();
  }

  public async create(
    descriptor: WorkspaceDescriptor,
    fileHandler: FileHandler,
    broadcastArgs: { broadcast: boolean }
  ): Promise<WorkspaceFile[]> {
    await this.storageService.createDirStructureAtRoot(descriptor.workspaceId);
    const createdFiles = await fileHandler.store(descriptor);

    const descriptors = await this.listAll();
    descriptors.push(descriptor);
    const configFile = await this.configAsFile(async () => JSON.stringify(descriptors));
    await this.storageService.updateFile(configFile);

    if (broadcastArgs.broadcast) {
      const broadcastChannel1 = new BroadcastChannel(this.rootPath);
      const broadcastChannel2 = new BroadcastChannel(descriptor.workspaceId);
      broadcastChannel1.postMessage({
        type: "ADD_WORKSPACE",
        workspaceId: descriptor.workspaceId,
      } as WorkspacesEvents);
      broadcastChannel2.postMessage({ type: "ADD", workspaceId: descriptor.workspaceId } as WorkspaceEvents);
    }

    return createdFiles;
  }

  public async get(workspaceId: string): Promise<WorkspaceDescriptor> {
    const descriptors = await this.listAll();
    const descriptor = descriptors.find((descriptor) => descriptor.workspaceId === workspaceId);
    if (!descriptor) {
      throw new Error(`Workspace '${workspaceId}' not found`);
    }

    return descriptor;
  }

  public async listFiles(workspaceId: string, globPattern?: string): Promise<WorkspaceFile[]> {
    const workspaceRootPath = await this.resolveRootPath(workspaceId);
    const storageFiles = await this.storageService.getFiles(workspaceRootPath, globPattern);
    return this.toWorkspaceFiles(workspaceId, storageFiles);
  }

  public async delete(workspaceId: string, broadcastArgs: { broadcast: boolean }): Promise<void> {
    const descriptors = await this.listAll();
    const index = descriptors.findIndex((d) => d.workspaceId === workspaceId);
    if (index === -1) {
      throw new Error(`Workspace '${workspaceId}' not found`);
    }

    const files = await this.listFiles(workspaceId);
    await this.storageService.deleteFiles(files.map((f) => this.toStorageFile(f)));

    descriptors.splice(index, 1);
    const configFile = await this.configAsFile(async () => JSON.stringify(descriptors));
    await this.storageService.updateFile(configFile);

    if (broadcastArgs.broadcast) {
      const broadcastChannel1 = new BroadcastChannel(this.rootPath);
      const broadcastChannel2 = new BroadcastChannel(workspaceId);
      broadcastChannel1.postMessage({ type: "DELETE_WORKSPACE", workspaceId } as WorkspacesEvents);
      broadcastChannel2.postMessage({ type: "DELETE", workspaceId } as WorkspaceEvents);
    }
  }

  public async rename(workspaceId: string, newName: string, broadcastArgs: { broadcast: boolean }): Promise<void> {
    const descriptors = await this.listAll();
    const index = descriptors.findIndex((w) => w.workspaceId === workspaceId);
    if (index === -1) {
      throw new Error(`Workspace ${workspaceId} not found`);
    }

    descriptors[index].name = newName;
    const configFile = await this.configAsFile(async () => JSON.stringify(descriptors));
    await this.storageService.updateFile(configFile);

    if (broadcastArgs.broadcast) {
      await this.bumpLastUpdatedDate(workspaceId);

      const broadcastChannel1 = new BroadcastChannel(this.rootPath);
      const broadcastChannel2 = new BroadcastChannel(workspaceId);
      broadcastChannel1.postMessage({ type: "RENAME_WORKSPACE", workspaceId } as WorkspacesEvents);
      broadcastChannel2.postMessage({ type: "RENAME", workspaceId } as WorkspaceEvents);
    }
  }

  public async prepareZip(workspaceId: string): Promise<Blob> {
    const workspaceRootPath = await this.resolveRootPath(workspaceId);

    const zip = new JSZip();
    const storageFiles = await this.storageService.getFiles(workspaceRootPath, SUPPORTED_FILES_PATTERN);

    for (const file of this.toWorkspaceFiles(workspaceId, storageFiles)) {
      zip.file(file.relativePath, (await file.getFileContents()) ?? "");
    }

    return await zip.generateAsync({ type: "blob" });
  }

  public async resolveRootPath(workspaceId: string): Promise<string> {
    const workspaceRootPath = this.getAbsolutePath({ workspaceId, relativePath: "" });
    if (!(await this.storageService.exists(workspaceRootPath))) {
      throw new Error(`Root '${workspaceRootPath}' does not exist`);
    }

    return workspaceRootPath;
  }

  //

  public async createFile(file: WorkspaceFile, broadcastArgs: { broadcast: boolean }): Promise<void> {
    await this.storageService.createFile(this.toStorageFile(file));

    if (broadcastArgs.broadcast) {
      await this.bumpLastUpdatedDate(file.workspaceId);

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
    const storageFile = await this.storageService.getFile(absolutePath);
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
      this.toStorageFile(
        new WorkspaceFile({
          relativePath: file.relativePath,
          workspaceId: file.workspaceId,
          getFileContents: getNewContents,
        })
      )
    );

    if (broadcastArgs.broadcast) {
      await this.bumpLastUpdatedDate(file.workspaceId);

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
    await this.storageService.deleteFile(this.toStorageFile(file));

    if (broadcastArgs.broadcast) {
      await this.bumpLastUpdatedDate(file.workspaceId);

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
    const renamedStorageFile = await this.storageService.renameFile(this.toStorageFile(file), newFileName);
    const renamedWorkspaceFile = this.toWorkspaceFile(file.workspaceId, renamedStorageFile);

    if (broadcastArgs.broadcast) {
      await this.bumpLastUpdatedDate(file.workspaceId);

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

    const movedStorageFile = await this.storageService.renameFile(this.toStorageFile(file), newDirPath);
    const movedWorkspaceFile = this.toWorkspaceFile(file.workspaceId, movedStorageFile);

    if (broadcastArgs.broadcast) {
      await this.bumpLastUpdatedDate(file.workspaceId);

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

  public async existsFile(args: { workspaceId: string; relativePath: string }): Promise<boolean> {
    return this.storageService.exists(this.getAbsolutePath(args));
  }

  public getAbsolutePath(args: { workspaceId: string; relativePath: string }) {
    return join(this.rootPath, args.workspaceId, args.relativePath);
  }

  public async createFiles(files: WorkspaceFile[], broadcastArgs: { broadcast: boolean }): Promise<void> {
    if (files.length === 0) {
      return;
    }

    await this.storageService.createFiles(this.toStorageFiles(files));

    if (broadcastArgs.broadcast) {
      await this.bumpLastUpdatedDate(files[0].workspaceId);

      const relativePaths = files.map((file) => file.relativePath);
      const broadcastChannel = new BroadcastChannel(files[0].workspaceId);
      broadcastChannel.postMessage({
        type: "ADD_BATCH",
        workspaceId: files[0].workspaceId,
        relativePaths,
      } as WorkspaceEvents);
    }
  }

  public async deleteFiles(files: WorkspaceFile[], broadcastArgs: { broadcast: boolean }): Promise<void> {
    if (files.length === 0) {
      return;
    }

    await this.storageService.deleteFiles(this.toStorageFiles(files));

    if (broadcastArgs.broadcast) {
      await this.bumpLastUpdatedDate(files[0].workspaceId);

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
    files: WorkspaceFile[],
    newDirPath: string,
    broadcastArgs: { broadcast: boolean }
  ): Promise<void> {
    if (files.length === 0) {
      return;
    }

    const pathMap = await this.storageService.moveFiles(this.toStorageFiles(files), newDirPath);

    if (broadcastArgs.broadcast) {
      await this.bumpLastUpdatedDate(files[0].workspaceId);

      const broadcastChannel = new BroadcastChannel(files[0].workspaceId);
      broadcastChannel.postMessage({
        type: "MOVE_BATCH",
        workspaceId: files[0].workspaceId,
        relativePaths: pathMap,
      } as WorkspaceEvents);
    }
  }

  public async getFiles(workspaceId: string, globPattern?: string): Promise<WorkspaceFile[]> {
    const workspaceRootPath = await this.resolveRootPath(workspaceId);
    const storageFiles = await this.storageService.getFiles(workspaceRootPath, globPattern);
    return this.toWorkspaceFiles(workspaceId, storageFiles);
  }

  private async configAsFile(getFileContents: () => Promise<string>) {
    return new StorageFile({
      path: this.WORKSPACE_CONFIG_PATH,
      getFileContents,
    });
  }

  private toWorkspaceFiles(workspaceId: string, storageFiles: StorageFile[]): WorkspaceFile[] {
    return storageFiles.map((storageFile: StorageFile) => this.toWorkspaceFile(workspaceId, storageFile));
  }

  private toStorageFiles(workspaceFiles: WorkspaceFile[]): StorageFile[] {
    return workspaceFiles.map((workspaceFile: WorkspaceFile) => this.toStorageFile(workspaceFile));
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

  private async bumpLastUpdatedDate(workspaceId: string): Promise<void> {
    const descriptors = await this.listAll();
    const updatedDescriptors = descriptors.map((descriptor) => {
      if (descriptor.workspaceId !== workspaceId) {
        return descriptor;
      }
      return {
        ...descriptor,
        lastUpdatedDateISO: new Date().toISOString(),
      };
    });
    const configFile = await this.configAsFile(async () => JSON.stringify(updatedDescriptors));
    await this.storageService.updateFile(configFile);
  }
}
