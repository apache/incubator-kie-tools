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
import { StorageService } from "./StorageService";
import { WorkspaceEvents } from "../hooks/WorkspaceHooks";
import { WorkspacesEvents } from "../hooks/WorkspacesHooks";

export class WorkspaceService {
  private readonly WORKSPACE_CONTEXT_PREFIX = "w";
  private readonly WORKSPACE_CONFIG_PATH = "/workspaces.json";

  public constructor(
    public readonly storageService: StorageService //TODO: make this private again
  ) {}

  public async init(): Promise<void> {
    const configFile = await this.storageService.getFile(this.WORKSPACE_CONFIG_PATH);

    if (!configFile) {
      const freshConfigFile = await this.configAsFile(async () => JSON.stringify([]));
      await this.storageService.createFile(freshConfigFile, { broadcast: false });
    }
  }

  public async listFiles(descriptor: WorkspaceDescriptor, globPattern?: string): Promise<WorkspaceFile[]> {
    const contextPath = await this.resolveContextPath(descriptor);
    return await this.storageService.getFiles(contextPath, globPattern);
  }

  public async getByFile(file: WorkspaceFile): Promise<WorkspaceDescriptor> {
    if (!file.path) {
      throw new Error("File path not found");
    }

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
    broadcast: boolean
  ): Promise<WorkspaceFile[]> {
    const descriptors = await this.list();
    descriptors.push(descriptor);

    const configFile = await this.configAsFile(async () => JSON.stringify(descriptors));
    await this.storageService.updateFile(configFile, { broadcast: false });
    await this.storageService.createFolderStructure(`/${descriptor.workspaceId}/`);

    const createdFiles = await fileHandler.store(descriptor);
    const supportedFiles = createdFiles.filter((file: WorkspaceFile) =>
      SUPPORTED_FILES_EDITABLE.includes(file.extension)
    );

    if (broadcast) {
      const broadcastChannel1 = new BroadcastChannel(this.storageService.rootPath);
      const broadcastChannel2 = new BroadcastChannel(descriptor.workspaceId);
      broadcastChannel1.postMessage({ type: "ADD_WORKSPACE", workspaceId: descriptor.workspaceId } as WorkspacesEvents);
      broadcastChannel2.postMessage({ type: "ADD", workspaceId: descriptor.workspaceId } as WorkspaceEvents);
    }

    return supportedFiles;
  }

  public async delete(descriptor: WorkspaceDescriptor, broadcast: boolean): Promise<void> {
    const descriptors = await this.list();
    const index = descriptors.findIndex((w) => w.workspaceId === descriptor.workspaceId);
    if (index === -1) {
      throw new Error(`Workspace ${descriptor.workspaceId} not found`);
    }

    const files = await this.listFiles(descriptor);
    await this.storageService.deleteFiles(files, { broadcast: false });

    descriptors.splice(index, 1);
    const configFile = await this.configAsFile(async () => JSON.stringify(descriptors));
    await this.storageService.updateFile(configFile, { broadcast: false });

    if (broadcast) {
      const broadcastChannel1 = new BroadcastChannel(this.storageService.rootPath);
      const broadcastChannel2 = new BroadcastChannel(descriptor.workspaceId);
      broadcastChannel1.postMessage({
        type: "DELETE_WORKSPACE",
        workspaceId: descriptor.workspaceId,
      } as WorkspacesEvents);
      broadcastChannel2.postMessage({ type: "DELETE", workspaceId: descriptor.workspaceId } as WorkspaceEvents);
    }
  }

  public async newContext(): Promise<string> {
    const descriptors = await this.list();
    return `${this.WORKSPACE_CONTEXT_PREFIX}${descriptors.length + 1}`;
  }

  public async newName(preferredName?: string): Promise<string> {
    const descriptors = await this.list();
    const names = descriptors.map((descriptor: WorkspaceDescriptor) => descriptor.name);
    if (preferredName && names.includes(preferredName)) {
      throw new Error(`Workspace ${preferredName} already exists`);
    }
    return preferredName || `Workspace ${names.length + 1}`;
  }

  public async prepareZip(descriptor: WorkspaceDescriptor): Promise<Blob> {
    const contextPath = await this.resolveContextPath(descriptor);

    const zip = new JSZip();
    const files = await this.storageService.getFiles(contextPath, SUPPORTED_FILES_PATTERN);

    for (const file of files) {
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

  private async configAsFile(getFileContents: () => Promise<string | undefined>) {
    return new WorkspaceFile({
      path: this.WORKSPACE_CONFIG_PATH,
      getFileContents,
    });
  }
}
