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
import { basename, dirname, extname, join, parse, resolve } from "path";

export class StorageFile {
  constructor(private readonly args: { path: string; getFileContents: () => Promise<string> }) {}
  get path() {
    return this.args.path;
  }
  get getFileContents() {
    return this.args.getFileContents;
  }
}

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

  public async createFile(file: StorageFile): Promise<void> {
    if (await this.exists(file.path)) {
      throw new Error(`File ${file.path} already exists`);
    }

    await this.createFolderStructure(file.path);
    await this.writeFile(file);
  }

  public async updateFile(file: StorageFile): Promise<void> {
    if (!(await this.exists(file.path))) {
      throw new Error(`File ${file.path} does not exist`);
    }

    await this.writeFile(file);
  }

  public async deleteFile(file: StorageFile): Promise<void> {
    if (!(await this.exists(file.path))) {
      throw new Error(`File ${file.path} does not exist`);
    }

    await this.fsp.unlink(file.path);
  }

  public async renameFile(file: StorageFile, newFileName: string): Promise<StorageFile> {
    if (!(await this.exists(file.path))) {
      throw new Error(`File ${file.path} does not exist`);
    }

    if (basename(file.path) === newFileName) {
      return file;
    }

    const newPath = join(dirname(file.path), `${newFileName}${extname(file.path)}`);

    if (await this.exists(newPath)) {
      throw new Error(`File ${newPath} already exists`);
    }

    const newFile = new StorageFile({
      path: newPath,
      getFileContents: file.getFileContents,
    });
    await this.fsp.rename(file.path, newFile.path);

    return newFile;
  }

  public async moveFile(file: StorageFile, newFolderPath: string): Promise<StorageFile> {
    if (!(await this.exists(file.path))) {
      throw new Error(`File ${file.path} does not exist`);
    }

    const newPath = join(newFolderPath, basename(file.path));
    const newFile = new StorageFile({
      getFileContents: file.getFileContents,
      path: newPath,
    });
    await this.createFile(newFile);
    await this.deleteFile(file);

    return newFile;
  }

  public async createFiles(files: StorageFile[]): Promise<void> {
    for (const file of files) {
      await this.createFile(file);
    }
  }

  public async deleteFiles(files: StorageFile[]): Promise<void> {
    for (const file of files) {
      await this.deleteFile(file);
    }
  }

  public async moveFiles(files: StorageFile[], newFolderPath: string): Promise<Map<string, string>> {
    const paths = new Map<string, string>();
    for (const fileToMove of files) {
      const movedFile = await this.moveFile(fileToMove, newFolderPath);
      paths.set(fileToMove.path, movedFile.path);
    }
    return paths;
  }

  public async getFile(path: string): Promise<StorageFile | undefined> {
    if (!(await this.exists(path))) {
      return;
    }

    return new StorageFile({
      getFileContents: this.buildGetFileContentsCallback(path),
      path,
    });
  }

  public async getFiles(folderPath: string, globPattern?: string): Promise<StorageFile[]> {
    const filePaths = await this.getFilePaths(folderPath);

    const files = filePaths.map((path: string) => {
      return new StorageFile({
        getFileContents: this.buildGetFileContentsCallback(path),
        path,
      });
    });

    if (!globPattern) {
      return files;
    }

    const matcher = new Minimatch(globPattern, { dot: true });
    return files.filter((file: StorageFile) => matcher.match(parse(file.path).base));
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

  private async writeFile(file: StorageFile): Promise<void> {
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
