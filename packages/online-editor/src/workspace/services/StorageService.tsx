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

import LightningFS from "@isomorphic-git/lightning-fs";
import { basename, dirname, extname, join, resolve } from "path";

export class StorageFile {
  constructor(private readonly args: { path: string; getFileContents: () => Promise<Uint8Array> }) {}

  get path() {
    return this.args.path;
  }

  get getFileContents() {
    return this.args.getFileContents;
  }
}

export class StorageService {
  private readonly SEPARATOR = "/";

  public readonly fs;
  public readonly fsp;

  public constructor(private readonly dbName: string) {
    this.fs = new LightningFS(this.dbName);
    this.fsp = this.fs.promises;
  }

  public get rootPath(): string {
    return this.SEPARATOR;
  }

  public async createFile(file: StorageFile) {
    const contents = await file.getFileContents();
    try {
      await this.fsp.writeFile(file.path, contents);
    } catch (err) {
      await this.mkdir(dirname(file.path));
      await this.fsp.writeFile(file.path, contents);
    }
  }

  public async updateFile(file: StorageFile): Promise<void> {
    if (!(await this.exists(file.path))) {
      throw new Error(`File ${file.path} does not exist`);
    }

    await this.writeFile(file);
  }

  public async deleteFile(path: string): Promise<void> {
    await this.fsp.unlink(path);
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

  public async moveFile(file: StorageFile, newDirPath: string): Promise<StorageFile> {
    if (!(await this.exists(file.path))) {
      throw new Error(`File ${file.path} does not exist`);
    }

    const newPath = join(newDirPath, basename(file.path));
    const newFile = new StorageFile({
      getFileContents: file.getFileContents,
      path: newPath,
    });
    await this.createFile(newFile);
    await this.deleteFile(file.path);

    return newFile;
  }

  public async moveFiles(files: StorageFile[], newDirPath: string): Promise<Map<string, string>> {
    const paths = new Map<string, string>();
    for (const fileToMove of files) {
      const movedFile = await this.moveFile(fileToMove, newDirPath);
      paths.set(fileToMove.path, movedFile.path);
    }
    return paths;
  }

  public async getFile(path: string): Promise<StorageFile | undefined> {
    if (!(await this.exists(path))) {
      return;
    }

    return new StorageFile({
      path,
      getFileContents: () => this.fsp.readFile(path),
    });
  }

  public async wipeStorage(): Promise<void> {
    return new Promise<void>((resolve, reject) => {
      const request = indexedDB.deleteDatabase(this.dbName);
      request.onsuccess = () => resolve();
      request.onerror = () => reject("Could not delete database");
      request.onblocked = () => reject("Could not delete database due to the operation being blocked");
    });
  }

  public async createDirStructureAtRoot(pathRelativeToRoot: string) {
    await this.mkdir(this.rootPath + pathRelativeToRoot + this.SEPARATOR);
  }

  async mkdir(dirPath: string, _selfCall = false) {
    try {
      await this.fsp.mkdir(dirPath);
      return;
    } catch (err) {
      // If err is null then operation succeeded!
      if (err === null) {
        return;
      }

      // If the directory already exists, that's OK!
      if (err.code === "EEXIST") {
        return;
      }

      // Avoid infinite loops of failure
      if (_selfCall) {
        throw err;
      }

      // If we got a "no such file or directory error" backup and try again.
      if (err.code === "ENOENT") {
        const parent = dirname(dirPath);

        // Check to see if we've gone too far
        if (parent === "." || parent === "/" || parent === dirPath) {
          throw err;
        }

        // Infinite recursion, what could go wrong?
        await this.mkdir(parent);
        await this.mkdir(dirPath, true);
      }
    }
  }

  public async exists(path: string): Promise<boolean> {
    try {
      await this.fsp.stat(path);
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
  private async writeFile(file: StorageFile): Promise<void> {
    const content = await file.getFileContents();
    await this.fsp.writeFile(file.path, content);
  }

  public async getFilePaths<T = string>(args: {
    dirPath: string;
    visit: (path: string) => T | undefined;
  }): Promise<T[]> {
    const subDirPaths = await this.fsp.readdir(args.dirPath);
    const files = await Promise.all(
      subDirPaths.map(async (subDirPath: string) => {
        const path = resolve(args.dirPath, subDirPath);
        return (await this.fsp.stat(path)).isDirectory()
          ? this.getFilePaths({ dirPath: path, visit: args.visit })
          : args.visit(path);
      })
    );
    return files.reduce((paths: T[], path: T) => (path ? paths.concat(path) : paths), []) as T[];
  }
}
