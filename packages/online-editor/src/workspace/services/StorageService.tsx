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

import LightningFS, { FSBackend, FSConstructorOptions } from "@isomorphic-git/lightning-fs";
import { Minimatch } from "minimatch";
import { basename, dirname, join, parse, resolve } from "path";
import { WorkspaceFile } from "../WorkspacesContext";
import { WorkspaceFileEvents } from "../hooks/WorkspaceFileHooks";
import { WorkspaceEvents } from "../hooks/WorkspaceHooks";

export class StorageService {
  private readonly FOLDER_SEPARATOR = "/";
  private readonly encoder: TextEncoder = new TextEncoder();
  private readonly decoder: TextDecoder = new TextDecoder("utf-8");

  public readonly fs;
  private readonly fsp;

  public constructor(private readonly dbName: string, fsBackend?: FSBackend) {
    this.fs = new LightningFS(this.dbName, { backend: fsBackend } as FSConstructorOptions);
    this.fsp = this.fs.promises;
  }

  public get rootPath(): string {
    return this.FOLDER_SEPARATOR;
  }

  public async createFile(file: WorkspaceFile, broadcastArgs: { broadcast: boolean }): Promise<void> {
    if (await this.exists(file.path)) {
      throw new Error(`File ${file.path} already exists`);
    }

    await this.createFolderStructure(file.path);
    await this.writeFile(file);

    if (broadcastArgs.broadcast) {
      const broadcastChannel1 = new BroadcastChannel(file.path);
      const broadcastChannel2 = new BroadcastChannel(file.workspaceId);
      broadcastChannel1.postMessage({ type: "ADD", path: file.path } as WorkspaceFileEvents);
      broadcastChannel2.postMessage({ type: "ADD_FILE", path: file.path } as WorkspaceEvents);
    }
  }

  public async updateFile(file: WorkspaceFile, broadcastArgs: { broadcast: boolean }): Promise<void> {
    if (!(await this.exists(file.path))) {
      throw new Error(`File ${file.path} does not exist`);
    }

    await this.writeFile(file);

    if (broadcastArgs.broadcast) {
      const broadcastChannel1 = new BroadcastChannel(file.path);
      const broadcastChannel2 = new BroadcastChannel(file.workspaceId);
      broadcastChannel1.postMessage({ type: "UPDATE", path: file.path } as WorkspaceFileEvents);
      broadcastChannel2.postMessage({ type: "UPDATE_FILE", path: file.path } as WorkspaceEvents);
    }
  }

  public async deleteFile(file: WorkspaceFile, broadcastArgs: { broadcast: boolean }): Promise<void> {
    if (!(await this.exists(file.path))) {
      throw new Error(`File ${file.path} does not exist`);
    }

    await this.fsp.unlink(file.path);

    if (broadcastArgs.broadcast) {
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
    if (!(await this.exists(file.path))) {
      throw new Error(`File ${file.path} does not exist`);
    }

    if (basename(file.path) === newFileName) {
      return file;
    }

    const newPath = join(dirname(file.path), `${newFileName}.${file.extension}`);

    if (await this.exists(newPath)) {
      throw new Error(`File ${newPath} already exists`);
    }

    const newFile = new WorkspaceFile({
      getFileContents: file.getFileContents,
      path: newPath,
    });
    await this.fsp.rename(file.path, newFile.path);

    if (broadcastArgs.broadcast) {
      const broadcastChannel1 = new BroadcastChannel(file.path);
      const broadcastChannel2 = new BroadcastChannel(file.workspaceId);
      broadcastChannel1.postMessage({
        type: "RENAME",
        oldPath: file.path,
        newPath: newFile.path,
      } as WorkspaceFileEvents);
      broadcastChannel2.postMessage({
        type: "RENAME_FILE",
        oldPath: file.path,
        newPath: newFile.path,
      } as WorkspaceEvents);
    }

    return newFile;
  }

  public async moveFile(
    file: WorkspaceFile,
    newFolderPath: string,
    broadcastArgs: { broadcast: boolean }
  ): Promise<WorkspaceFile> {
    if (!(await this.exists(file.path))) {
      throw new Error(`File ${file.path} does not exist`);
    }

    const newPath = join(newFolderPath, basename(file.path));
    const newFile = new WorkspaceFile({
      getFileContents: file.getFileContents,
      path: newPath,
    });
    await this.createFile(newFile, { broadcast: false });
    await this.deleteFile(file, { broadcast: false });

    if (broadcastArgs.broadcast) {
      const broadcastChannel1 = new BroadcastChannel(file.path);
      const broadcastChannel2 = new BroadcastChannel(file.workspaceId);
      broadcastChannel1.postMessage({ type: "MOVE", oldPath: file.path, newPath: newFile.path } as WorkspaceFileEvents);
      broadcastChannel2.postMessage({
        type: "MOVE_FILE",
        oldPath: file.path,
        newPath: newFile.path,
      } as WorkspaceEvents);
    }

    return newFile;
  }

  public async createFiles(files: WorkspaceFile[], broadcastArgs: { broadcast: boolean }): Promise<void> {
    for (const file of files) {
      await this.createFile(file, { broadcast: false });
    }

    if (broadcastArgs.broadcast) {
      if (files[0]) {
        const paths = files.map((file) => file.path);
        const broadcastChannel = new BroadcastChannel(files[0].workspaceId);
        broadcastChannel.postMessage({
          type: "ADD_BATCH",
          workspaceId: files[0].workspaceId,
          paths,
        } as WorkspaceEvents);
      }
    }
  }

  public async deleteFiles(files: WorkspaceFile[], broadcastArgs: { broadcast: boolean }): Promise<void> {
    for (const file of files) {
      await this.deleteFile(file, { broadcast: false });
    }

    if (broadcastArgs.broadcast) {
      if (files[0]) {
        const paths = files.map((file) => file.path);
        const broadcastChannel = new BroadcastChannel(files[0].workspaceId);
        broadcastChannel.postMessage({
          type: "DELETE_BATCH",
          workspaceId: files[0].workspaceId,
          paths,
        } as WorkspaceEvents);
      }
    }
  }

  public async moveFiles(
    files: WorkspaceFile[],
    newFolderPath: string,
    broadcastArgs: { broadcast: boolean }
  ): Promise<void> {
    const paths = new Map<string, string>();
    for (const fileToMove of files) {
      const movedFile = await this.moveFile(fileToMove, newFolderPath, { broadcast: false });
      paths.set(fileToMove.path, movedFile.path);
    }

    if (broadcastArgs.broadcast) {
      if (files[0]) {
        const broadcastChannel = new BroadcastChannel(files[0].workspaceId);
        broadcastChannel.postMessage({
          type: "MOVE_BATCH",
          workspaceId: files[0].workspaceId,
          paths,
        } as WorkspaceEvents);
      }
    }
  }

  public async getFile(path: string): Promise<WorkspaceFile | undefined> {
    if (!(await this.exists(path))) {
      return;
    }

    return new WorkspaceFile({
      getFileContents: this.buildGetFileContentsCallback(path),
      path,
    });
  }

  public async getFiles(folderPath: string, globPattern?: string): Promise<WorkspaceFile[]> {
    const filePaths = await this.getFilePaths(folderPath);

    const files = filePaths.map((path: string) => {
      return new WorkspaceFile({
        getFileContents: this.buildGetFileContentsCallback(path),
        path,
      });
    });

    if (!globPattern) {
      return files;
    }

    const matcher = new Minimatch(globPattern, { dot: true });
    return files.filter((file: WorkspaceFile) => matcher.match(parse(file.path).base));
  }

  public async wipeStorage(): Promise<void> {
    return new Promise<void>((resolve, reject) => {
      const request = indexedDB.deleteDatabase(this.dbName);
      request.onsuccess = () => resolve();
      request.onerror = () => reject("Could not delete database");
      request.onblocked = () => reject("Could not delete database due to the operation being blocked");
    });
  }

  public async createFolderStructure(path: string): Promise<void> {
    const folderPath = path.endsWith(this.FOLDER_SEPARATOR) ? path : dirname(path);
    const folders = folderPath.split(this.FOLDER_SEPARATOR).filter((p: string) => p);

    let currentPath = this.rootPath;
    for (const folder of folders) {
      currentPath = join(currentPath, folder);
      if (!(await this.exists(currentPath))) {
        await this.fsp.mkdir(currentPath);
      }
    }
  }

  public async exists(filePath: string): Promise<boolean> {
    try {
      await this.fs.promises.stat(filePath);
      return true;
    } catch (err) {
      if (err.code === "ENOENT" || err.code === "ENOTDIR") {
        return false;
      } else {
        console.log("Unexpected error when trying to check if file exists", err);
        throw err;
      }
    }
  }

  private buildGetFileContentsCallback(path: string): () => Promise<string> {
    return async () => {
      if (!(await this.exists(path))) {
        throw new Error(`Can't read non-existent file '${path}'`);
      }
      return this.decoder.decode(await this.fsp.readFile(path));
    };
  }

  private async writeFile(file: WorkspaceFile): Promise<void> {
    const content = await file.getFileContents();
    await this.fsp.writeFile(file.path, this.encoder.encode(content));
  }

  private async getFilePaths(folder: string): Promise<string[]> {
    if (!(await this.exists(folder))) {
      throw new Error(`Folder ${folder} does not exist`);
    }

    const subFolders = await this.fsp.readdir(folder);
    const files = await Promise.all(
      subFolders.map(async (subFolder: string) => {
        const path = resolve(folder, subFolder);
        return (await this.fsp.stat(path)).isDirectory() ? this.getFilePaths(path) : path;
      })
    );
    return files.reduce((paths: string[], path: string) => paths.concat(path), []) as string[];
  }
}
