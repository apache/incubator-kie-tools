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
import { SUPPORTED_FILES_EDITABLE, SUPPORTED_FILES_PATTERN } from "../SupportedFiles";
import { StorageFile, StorageService } from "./StorageService";
import { WorkspaceEvents } from "../hooks/WorkspaceHooks";
import { WorkspacesEvents } from "../hooks/WorkspacesHooks";
import { v4 as uuid } from "uuid";
import { WorkspaceFileEvents } from "../hooks/WorkspaceFileHooks";

export class WorkspaceService {
  private readonly WORKSPACE_CONFIG_PATH = "/workspaces.json";

  public constructor(private readonly storageService: StorageService) {}

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
    const broadcastChannel = new BroadcastChannel(this.storageService.rootPath);
    broadcastChannel.postMessage({ type: "DELETE_ALL" } as WorkspacesEvents);
  }

  public async listFiles(descriptor: WorkspaceDescriptor, globPattern?: string): Promise<WorkspaceFile[]> {
    const contextPath = await this.resolveContextPath(descriptor);
    const storageFiles = await this.storageService.getFiles(contextPath, globPattern);
    return this.toWorkspaceFiles(storageFiles);
  }

  public async getByFile(file: WorkspaceFile): Promise<WorkspaceDescriptor> {
    return this.getByFilePath(file.path);
  }

  public async getByFilePath(path: string): Promise<WorkspaceDescriptor> {
    const contextMatches = path.match(/\/(.+?)\//);

    if (!contextMatches || contextMatches.length < 2) {
      throw new Error(`Invalid path: ${path}`);
    }

    const context = contextMatches[1];
    const descriptor = await this.get(context);

    if (!descriptor) {
      throw new Error(`Workspace ${context} not found`);
    }

    return descriptor;
  }

  public async get(workspaceId: string): Promise<WorkspaceDescriptor | undefined> {
    const descriptors = await this.list();
    return descriptors.find((descriptor: WorkspaceDescriptor) => descriptor.workspaceId === workspaceId);
  }

  public async list(): Promise<WorkspaceDescriptor[]> {
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

  public async create(
    descriptor: WorkspaceDescriptor,
    fileHandler: FileHandler,
    broadcastArgs: { broadcast: boolean }
  ): Promise<WorkspaceFile[]> {
    const descriptors = await this.list();
    descriptors.push(descriptor);

    const configFile = await this.configAsFile(async () => JSON.stringify(descriptors));
    await this.storageService.updateFile(configFile);
    await this.storageService.createFolderStructure(`/${descriptor.workspaceId}/`);

    const createdFiles = await fileHandler.store(descriptor);
    const supportedFiles = createdFiles.filter((file: WorkspaceFile) =>
      SUPPORTED_FILES_EDITABLE.includes(file.extension)
    );

    if (broadcastArgs.broadcast) {
      const broadcastChannel1 = new BroadcastChannel(this.storageService.rootPath);
      const broadcastChannel2 = new BroadcastChannel(descriptor.workspaceId);
      broadcastChannel1.postMessage({ type: "ADD_WORKSPACE", workspaceId: descriptor.workspaceId } as WorkspacesEvents);
      broadcastChannel2.postMessage({ type: "ADD", workspaceId: descriptor.workspaceId } as WorkspaceEvents);
    }

    return supportedFiles;
  }

  public async delete(descriptor: WorkspaceDescriptor, broadcastArgs: { broadcast: boolean }): Promise<void> {
    const descriptors = await this.list();
    const index = descriptors.findIndex(({ workspaceId }) => workspaceId === descriptor.workspaceId);
    if (index === -1) {
      throw new Error(`Workspace ${descriptor.workspaceId} not found`);
    }

    const files = await this.listFiles(descriptor);
    await this.storageService.deleteFiles(files.map(this.toStorageFile));

    descriptors.splice(index, 1);
    const configFile = await this.configAsFile(async () => JSON.stringify(descriptors));
    await this.storageService.updateFile(configFile);

    if (broadcastArgs.broadcast) {
      const broadcastChannel1 = new BroadcastChannel(this.storageService.rootPath);
      const broadcastChannel2 = new BroadcastChannel(descriptor.workspaceId);
      broadcastChannel1.postMessage({
        type: "DELETE_WORKSPACE",
        workspaceId: descriptor.workspaceId,
      } as WorkspacesEvents);
      broadcastChannel2.postMessage({ type: "DELETE", workspaceId: descriptor.workspaceId } as WorkspaceEvents);
    }
  }

  public async rename(
    descriptor: WorkspaceDescriptor,
    newName: string,
    broadcastArgs: { broadcast: boolean }
  ): Promise<void> {
    const descriptors = await this.list();
    const index = descriptors.findIndex((w) => w.workspaceId === descriptor.workspaceId);
    if (index === -1) {
      throw new Error(`Workspace ${descriptor.workspaceId} not found`);
    }

    descriptors[index].name = newName;
    const configFile = await this.configAsFile(async () => JSON.stringify(descriptors));
    await this.storageService.updateFile(configFile);

    if (broadcastArgs.broadcast) {
      await this.bumpLastUpdatedDate(descriptor.workspaceId);

      const broadcastChannel1 = new BroadcastChannel(this.storageService.rootPath);
      const broadcastChannel2 = new BroadcastChannel(descriptor.workspaceId);
      broadcastChannel1.postMessage({
        type: "RENAME_WORKSPACE",
        workspaceId: descriptor.workspaceId,
      } as WorkspacesEvents);
      broadcastChannel2.postMessage({ type: "RENAME", workspaceId: descriptor.workspaceId } as WorkspaceEvents);
    }
  }

  public newContext(): string {
    return uuid();
  }

  public async prepareZip(descriptor: WorkspaceDescriptor): Promise<Blob> {
    const contextPath = await this.resolveContextPath(descriptor);

    const zip = new JSZip();
    const storageFiles = await this.storageService.getFiles(contextPath, SUPPORTED_FILES_PATTERN);

    for (const file of this.toWorkspaceFiles(storageFiles)) {
      zip.file(file.pathRelativeToWorkspaceRoot, (await file.getFileContents()) ?? "");
    }

    return await zip.generateAsync({ type: "blob" });
  }

  public async resolveContextPath(descriptor: WorkspaceDescriptor): Promise<string> {
    const contextPath = `${this.storageService.rootPath}${descriptor.workspaceId}`;
    if (!(await this.storageService.exists(contextPath))) {
      throw new Error(`Context ${context} does not exist`);
    }

    return contextPath;
  }

  private async configAsFile(getFileContents: () => Promise<string>) {
    return new StorageFile({
      path: this.WORKSPACE_CONFIG_PATH,
      getFileContents,
    });
  }

  private toWorkspaceFiles(storageFiles: StorageFile[]): WorkspaceFile[] {
    return storageFiles.map((storageFile: StorageFile) => this.toWorkspaceFile(storageFile));
  }

  private toStorageFiles(workspaceFiles: WorkspaceFile[]): StorageFile[] {
    return workspaceFiles.map((workspaceFile: WorkspaceFile) => this.toStorageFile(workspaceFile));
  }

  private toWorkspaceFile(storageFile: StorageFile): WorkspaceFile {
    return new WorkspaceFile({
      path: storageFile.path,
      getFileContents: storageFile.getFileContents,
    });
  }

  private toStorageFile(workspaceFile: WorkspaceFile): StorageFile {
    return new StorageFile({
      path: workspaceFile.path,
      getFileContents: workspaceFile.getFileContents,
    });
  }

  private async bumpLastUpdatedDate(workspaceId: string): Promise<void> {
    const descriptors = await this.list();
    const updatedDescriptors = descriptors.map((descriptor: WorkspaceDescriptor) => {
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

  public get rootPath(): string {
    return this.storageService.rootPath;
  }

  public async createFile(file: WorkspaceFile, broadcastArgs: { broadcast: boolean }): Promise<void> {
    await this.storageService.createFile(this.toStorageFile(file));

    if (broadcastArgs.broadcast) {
      await this.bumpLastUpdatedDate(file.workspaceId);

      const broadcastChannel1 = new BroadcastChannel(file.path);
      const broadcastChannel2 = new BroadcastChannel(file.workspaceId);
      broadcastChannel1.postMessage({ type: "ADD", path: file.path } as WorkspaceFileEvents);
      broadcastChannel2.postMessage({ type: "ADD_FILE", path: file.path } as WorkspaceEvents);
    }
  }

  public async updateFile(file: WorkspaceFile, broadcastArgs: { broadcast: boolean }): Promise<void> {
    await this.storageService.updateFile(this.toStorageFile(file));

    if (broadcastArgs.broadcast) {
      await this.bumpLastUpdatedDate(file.workspaceId);

      const broadcastChannel1 = new BroadcastChannel(file.path);
      const broadcastChannel2 = new BroadcastChannel(file.workspaceId);
      broadcastChannel1.postMessage({ type: "UPDATE", path: file.path } as WorkspaceFileEvents);
      broadcastChannel2.postMessage({ type: "UPDATE_FILE", path: file.path } as WorkspaceEvents);
    }
  }

  public async deleteFile(file: WorkspaceFile, broadcastArgs: { broadcast: boolean }): Promise<void> {
    await this.storageService.deleteFile(this.toStorageFile(file));

    if (broadcastArgs.broadcast) {
      await this.bumpLastUpdatedDate(file.workspaceId);

      const broadcastChannel1 = new BroadcastChannel(file.path);
      const broadcastChannel2 = new BroadcastChannel(file.workspaceId);
      broadcastChannel1.postMessage({ type: "DELETE", path: file.path } as WorkspaceFileEvents);
      broadcastChannel2.postMessage({ type: "DELETE_FILE", path: file.path } as WorkspaceEvents);
    }
  }

  public async renameFile(
    file: WorkspaceFile,
    newFileName: string,
    broadcastArgs: { broadcast: boolean }
  ): Promise<WorkspaceFile> {
    const renamedStorageFile = await this.storageService.renameFile(this.toStorageFile(file), newFileName);
    const renamedWorkspaceFile = this.toWorkspaceFile(renamedStorageFile);

    if (broadcastArgs.broadcast) {
      await this.bumpLastUpdatedDate(file.workspaceId);

      const broadcastChannel1 = new BroadcastChannel(file.path);
      const broadcastChannel2 = new BroadcastChannel(renamedWorkspaceFile.path);
      const broadcastChannel3 = new BroadcastChannel(file.workspaceId);
      broadcastChannel1.postMessage({
        type: "RENAME",
        oldPath: file.path,
        newPath: renamedWorkspaceFile.path,
      } as WorkspaceFileEvents);
      broadcastChannel2.postMessage({
        type: "ADD",
        path: renamedWorkspaceFile.path,
      } as WorkspaceFileEvents);
      broadcastChannel3.postMessage({
        type: "RENAME_FILE",
        oldPath: file.path,
        newPath: renamedWorkspaceFile.path,
      } as WorkspaceEvents);
    }

    return renamedWorkspaceFile;
  }

  public async moveFile(
    file: WorkspaceFile,
    newFolderPath: string,
    broadcastArgs: { broadcast: boolean }
  ): Promise<WorkspaceFile> {
    const movedStorageFile = await this.storageService.renameFile(this.toStorageFile(file), newFolderPath);
    const movedWorkspaceFile = this.toWorkspaceFile(movedStorageFile);

    if (broadcastArgs.broadcast) {
      await this.bumpLastUpdatedDate(file.workspaceId);

      const broadcastChannel1 = new BroadcastChannel(file.path);
      const broadcastChannel2 = new BroadcastChannel(file.workspaceId);
      broadcastChannel1.postMessage({
        type: "MOVE",
        oldPath: file.path,
        newPath: movedWorkspaceFile.path,
      } as WorkspaceFileEvents);
      broadcastChannel2.postMessage({
        type: "MOVE_FILE",
        oldPath: file.path,
        newPath: movedWorkspaceFile.path,
      } as WorkspaceEvents);
    }

    return movedWorkspaceFile;
  }

  public async createFiles(files: WorkspaceFile[], broadcastArgs: { broadcast: boolean }): Promise<void> {
    if (files.length === 0) {
      return;
    }

    await this.storageService.createFiles(this.toStorageFiles(files));

    if (broadcastArgs.broadcast) {
      await this.bumpLastUpdatedDate(files[0].workspaceId);

      const paths = files.map((file) => file.path);
      const broadcastChannel = new BroadcastChannel(files[0].workspaceId);
      broadcastChannel.postMessage({
        type: "ADD_BATCH",
        workspaceId: files[0].workspaceId,
        paths,
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

      const paths = files.map((file) => file.path);
      const broadcastChannel = new BroadcastChannel(files[0].workspaceId);
      broadcastChannel.postMessage({
        type: "DELETE_BATCH",
        workspaceId: files[0].workspaceId,
        paths,
      } as WorkspaceEvents);
    }
  }

  public async moveFiles(
    files: WorkspaceFile[],
    newFolderPath: string,
    broadcastArgs: { broadcast: boolean }
  ): Promise<void> {
    if (files.length === 0) {
      return;
    }

    const pathMap = await this.storageService.moveFiles(this.toStorageFiles(files), newFolderPath);

    if (broadcastArgs.broadcast) {
      await this.bumpLastUpdatedDate(files[0].workspaceId);

      const broadcastChannel = new BroadcastChannel(files[0].workspaceId);
      broadcastChannel.postMessage({
        type: "MOVE_BATCH",
        workspaceId: files[0].workspaceId,
        paths: pathMap,
      } as WorkspaceEvents);
    }
  }

  public async getFile(path: string): Promise<WorkspaceFile | undefined> {
    const storageFile = await this.storageService.getFile(path);
    if (!storageFile) {
      return;
    }
    return this.toWorkspaceFile(storageFile);
  }

  public async getFiles(folderPath: string, globPattern?: string): Promise<WorkspaceFile[]> {
    const storageFiles = await this.storageService.getFiles(folderPath, globPattern);
    return this.toWorkspaceFiles(storageFiles);
  }

  public async exists(filePath: string): Promise<boolean> {
    return this.storageService.exists(filePath);
  }
}
