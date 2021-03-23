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
import { buildFile, File } from "@kie-tooling-core/editor/dist/channel";
import { Minimatch } from "minimatch";
import { basename, dirname, join, parse, resolve } from "path";
import {
  AddFileBatchEvent,
  AddFileEvent,
  ChannelKind,
  DeleteFileBatchEvent,
  DeleteFileEvent,
  MoveFileBatchEvent,
  MoveFileEvent,
  UpdateFileEvent,
} from "../model/Event";
import { BroadcastService } from "./BroadcastService";

export class StorageService {
  private readonly FOLDER_SEPARATOR = "/";
  private readonly encoder: TextEncoder = new TextEncoder();
  private readonly decoder: TextDecoder = new TextDecoder("utf-8");

  public readonly fs;
  private readonly fsp;

  public constructor(
    private readonly dbName: string,
    private readonly broadcastService: BroadcastService,
    fsBackend?: FSBackend
  ) {
    this.fs = new LightningFS(this.dbName, { backend: fsBackend } as FSConstructorOptions);
    this.fsp = this.fs.promises;
  }

  public get rootPath(): string {
    return this.FOLDER_SEPARATOR;
  }

  public async createFile(file: File, broadcast: boolean): Promise<void> {
    if (!file.path) {
      throw new Error("File path is not defined");
    }

    if (await this.exists(file.path)) {
      throw new Error(`File ${file.path} already exists`);
    }

    await this.createFolderStructure(file.path);
    await this.writeFile(file);

    if (broadcast) {
      this.broadcastService.send<AddFileEvent>(ChannelKind.ADD_FILE, { path: file.path });
    }
  }

  public async updateFile(file: File, broadcast: boolean): Promise<void> {
    if (!file.path || !(await this.exists(file.path))) {
      throw new Error(`File ${file.path} does not exist`);
    }

    await this.writeFile(file);

    if (broadcast) {
      this.broadcastService.send<UpdateFileEvent>(ChannelKind.UPDATE_FILE, { path: file.path });
    }
  }

  public async deleteFile(file: File, broadcast: boolean): Promise<void> {
    if (!file.path || !(await this.exists(file.path))) {
      throw new Error(`File ${file.path} does not exist`);
    }

    await this.fsp.unlink(file.path);

    if (broadcast) {
      this.broadcastService.send<DeleteFileEvent>(ChannelKind.DELETE_FILE, { path: file.path });
    }
  }

  public async renameFile(file: File, newFileName: string, broadcast: boolean): Promise<File> {
    if (!file.path || !(await this.exists(file.path))) {
      throw new Error(`File ${file.path} does not exist`);
    }

    if (file.fileName === newFileName) {
      return file;
    }

    const newPath = join(dirname(file.path), `${newFileName}.${file.fileExtension}`);

    if (await this.exists(newPath)) {
      throw new Error(`File ${newPath} already exists`);
    }

    const newFile = buildFile(newPath, file.getFileContents);
    await this.fsp.rename(file.path, newFile.path!);

    if (broadcast) {
      this.broadcastService.send<MoveFileEvent>(ChannelKind.MOVE_FILE, {
        path: file.path,
        newPath: newFile.path!,
      });
    }

    return newFile;
  }

  public async moveFile(file: File, newFolderPath: string, broadcast: boolean): Promise<File> {
    if (!file.path || !(await this.exists(file.path))) {
      throw new Error(`File ${file.path} does not exist`);
    }

    const newPath = join(newFolderPath, basename(file.path));
    const newFile = buildFile(newPath, file.getFileContents);
    await this.createFile(newFile, false);
    await this.deleteFile(file, false);

    if (broadcast) {
      this.broadcastService.send<MoveFileEvent>(ChannelKind.MOVE_FILE, {
        path: file.path,
        newPath: newFile.path!,
      });
    }

    return newFile;
  }

  public async createFiles(files: File[], broadcast: boolean): Promise<void> {
    for (const file of files) {
      await this.createFile(file, false);
    }

    if (broadcast) {
      const paths = files.map((file: File) => file.path!);
      this.broadcastService.send<AddFileBatchEvent>(ChannelKind.ADD_FILE_BATCH, { paths: paths });
    }
  }

  public async deleteFiles(files: File[], broadcast: boolean): Promise<void> {
    for (const file of files) {
      await this.deleteFile(file, false);
    }

    if (broadcast) {
      const paths = files.map((file: File) => file.path!);
      this.broadcastService.send<DeleteFileBatchEvent>(ChannelKind.DELETE_FILE_BATCH, { paths: paths });
    }
  }

  public async moveFiles(files: File[], newFolderPath: string, broadcast: boolean): Promise<void> {
    const paths = [];
    const pathMap = new Map<string, string>();
    for (const file of files) {
      const f = await this.moveFile(file, newFolderPath, false);
      paths.push(f.path!);
      pathMap.set(file.path!, f.path!);
    }

    if (broadcast) {
      this.broadcastService.send<MoveFileBatchEvent>(ChannelKind.MOVE_FILE_BATCH, { paths: paths, pathMap: pathMap });
    }
  }

  public async getFile(path: string): Promise<File | undefined> {
    if (!(await this.exists(path))) {
      return;
    }

    return buildFile(path, this.buildGetFileContentsCallback(path));
  }

  public async getFiles(folderPath: string, globPattern?: string): Promise<File[]> {
    const filePaths = await this.getFilePaths(folderPath);

    const files = filePaths.map((path: string) => {
      return buildFile(path, this.buildGetFileContentsCallback(path));
    });

    if (!globPattern) {
      return files;
    }

    const matcher = new Minimatch(globPattern, { dot: true });
    return files.filter((file: File) => matcher.match(parse(file.path!).base));
  }

  public async wipeStorage(): Promise<void> {
    return new Promise<void>((resolve, reject) => {
      const request = indexedDB.deleteDatabase(this.dbName);
      request.onsuccess = function () {
        resolve();
      };
      request.onerror = function () {
        reject("Could not delete database");
      };
      request.onblocked = function () {
        reject("Could not delete database due to the operation being blocked");
      };
    });
  }

  public asRelativePath(folder: string, file: File): string {
    if (!file.path) {
      throw new Error("File path is not defined");
    }

    if (!file.path.startsWith(folder)) {
      throw new Error(`File ${file.path} is not in the folder ${folder}`);
    }

    const newPath = file.path.replace(folder, "");
    return newPath.startsWith(this.FOLDER_SEPARATOR) ? newPath.substring(1) : newPath;
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

  private buildGetFileContentsCallback(path: string): () => Promise<string | undefined> {
    return async () => {
      if (!(await this.exists(path))) {
        return;
      }
      return this.decoder.decode(await this.fsp.readFile(path));
    };
  }

  private async writeFile(file: File): Promise<void> {
    if (!file.path) {
      throw new Error("File path is not defined");
    }

    const content = (await file.getFileContents()) ?? "";
    await this.fsp.writeFile(file.path, this.encoder.encode(content));
  }

  private async getFilePaths(folder: string): Promise<string[]> {
    if (!(await this.exists(folder))) {
      throw new Error(`Filder ${folder} does not exist`);
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
