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
import DefaultBackend from "@isomorphic-git/lightning-fs/src/DefaultBackend";
import DexieBackend from "@isomorphic-git/lightning-fs/src/DexieBackend";
import { InMemoryBackend } from "./InMemoryBackend";

export class EagerStorageFile {
  constructor(private readonly args: { path: string; content: Uint8Array }) {}

  get path() {
    return this.args.path;
  }

  get content() {
    return this.args.content;
  }
}
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

  public readonly fsInstance;

  public constructor(private readonly dbName: string) {
    this.fsInstance = new LightningFS(this.dbName, {
      backend: new DefaultBackend({
        idbBackendDelegate: (fileDbName, fileStoreName) => {
          return new DexieBackend(fileDbName, fileStoreName);
        },
      }) as any,
    });
  }

  public fsBatch() {
    return new LightningFS(this.dbName, {
      backend: new DefaultBackend({
        idbBackendDelegate: (fileDbName, fileStoreName) => {
          return new InMemoryBackend(new DexieBackend(fileDbName, fileStoreName));
        },
      }) as any,
    });
  }

  public async fs() {
    return this.fsInstance;

    // return new Promise<LightningFS>((res) => {
    //   const i = setInterval(() => {
    //     if (isFsBatchInProgress) {
    //       console.info("Waiting until fsBatchInProgress is `false`...");
    //       return;
    //     }
    //
    //     console.info("Acquiring regular FS...");
    //     res(this.fsInstance);
    //     clearInterval(i);
    //   }, 100);
    // });
  }

  public get rootPath(): string {
    return this.SEPARATOR;
  }

  public async createFile(fs: LightningFS, file: StorageFile) {
    const contents = await file.getFileContents();
    try {
      await fs.promises.writeFile(file.path, contents);
    } catch (err) {
      await this.mkdir(fs, dirname(file.path));
      await fs.promises.writeFile(file.path, contents);
    }
  }

  public async createFiles(fs: LightningFS, files: StorageFile[]) {
    if (!fs.promises.writeFileBulk) {
      throw new Error("Can't write bulk");
    }

    for (const file of files) {
      await this.mkdir(fs, dirname(file.path));
    }

    const filesArray = await Promise.all(
      files.map(async (f) => [f.path, await f.getFileContents()] as [string, Uint8Array])
    );

    await fs.promises.writeFileBulk(filesArray);
  }

  public async updateFile(fs: LightningFS, file: StorageFile): Promise<void> {
    if (!(await this.exists(fs, file.path))) {
      throw new Error(`File ${file.path} does not exist`);
    }

    await this.writeFile(fs, file);
  }

  public async deleteFile(fs: LightningFS, path: string): Promise<void> {
    await fs.promises.unlink(path);
  }

  public async renameFile(fs: LightningFS, file: StorageFile, newFileName: string): Promise<StorageFile> {
    if (!(await this.exists(fs, file.path))) {
      throw new Error(`File ${file.path} does not exist`);
    }

    if (basename(file.path) === newFileName) {
      return file;
    }

    const newPath = join(dirname(file.path), `${newFileName}${extname(file.path)}`);

    if (await this.exists(fs, newPath)) {
      throw new Error(`File ${newPath} already exists`);
    }

    const newFile = new StorageFile({
      path: newPath,
      getFileContents: file.getFileContents,
    });
    await fs.promises.rename(file.path, newFile.path);

    return newFile;
  }

  public async moveFile(fs: LightningFS, file: StorageFile, newDirPath: string): Promise<StorageFile> {
    if (!(await this.exists(fs, file.path))) {
      throw new Error(`File ${file.path} does not exist`);
    }

    const newPath = join(newDirPath, basename(file.path));
    const newFile = new StorageFile({
      getFileContents: file.getFileContents,
      path: newPath,
    });
    await this.createFile(fs, newFile);
    await this.deleteFile(fs, file.path);

    return newFile;
  }

  public async moveFiles(fs: LightningFS, files: StorageFile[], newDirPath: string): Promise<Map<string, string>> {
    const paths = new Map<string, string>();
    for (const fileToMove of files) {
      const movedFile = await this.moveFile(fs, fileToMove, newDirPath);
      paths.set(fileToMove.path, movedFile.path);
    }
    return paths;
  }

  public async getFile(fs: LightningFS, path: string): Promise<StorageFile | undefined> {
    if (!(await this.exists(fs, path))) {
      return;
    }

    return new StorageFile({
      path,
      getFileContents: () => fs.promises.readFile(path),
    });
  }

  public async getFiles(fs: LightningFS, paths: string[]): Promise<EagerStorageFile[]> {
    if (!fs.promises.readFileBulk) {
      throw new Error("Can't read bulk");
    }

    const files = await fs.promises.readFileBulk(paths);
    return files.map(([path, content]) => {
      return new EagerStorageFile({
        path,
        content,
      });
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

  public async createDirStructureAtRoot(fs: LightningFS, pathRelativeToRoot: string) {
    await this.mkdir(fs, this.rootPath + pathRelativeToRoot + this.SEPARATOR);
  }

  async mkdir(fs: LightningFS, dirPath: string, _selfCall = false) {
    try {
      await fs.promises.mkdir(dirPath);
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
        await this.mkdir(fs, parent);
        await this.mkdir(fs, dirPath, true);
      }
    }
  }

  public async exists(fs: LightningFS, path: string): Promise<boolean> {
    try {
      await fs.promises.stat(path);
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
  private async writeFile(fs: LightningFS, file: StorageFile): Promise<void> {
    const content = await file.getFileContents();
    await fs.promises.writeFile(file.path, content);
  }

  public async getFilePaths<T = string>(args: {
    fs: LightningFS;
    dirPath: string;
    excludeDir: (dirPath: string) => boolean;
    visit: (path: string) => T | undefined;
  }): Promise<T[]> {
    const subDirPaths = await args.fs.promises.readdir(args.dirPath);
    const files = await Promise.all(
      subDirPaths.map(async (subDirPath: string) => {
        const path = resolve(args.dirPath, subDirPath);
        return !(await args.fs.promises.stat(path)).isDirectory()
          ? args.visit(path)
          : args.excludeDir(path)
          ? []
          : this.getFilePaths({ fs: args.fs, dirPath: path, excludeDir: args.excludeDir, visit: args.visit });
      })
    );
    return files.reduce((paths: T[], path: T) => (path ? paths.concat(path) : paths), []) as T[];
  }

  async deleteFiles(fs: LightningFS, paths: string[]) {
    if (!fs.promises.unlinkBulk) {
      throw new Error("Can't unlink bulk");
    }

    await fs.promises.unlinkBulk(paths);
  }
}
