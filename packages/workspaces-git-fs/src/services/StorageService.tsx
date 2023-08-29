/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

import { basename, dirname, extname, join, relative } from "path";
import { EmscriptenFs, KieSandboxWorkspacesFs } from "./KieSandboxWorkspaceFs";
import { FsSchema } from "./FsCache";
import { extractExtension } from "../relativePath/WorkspaceFileRelativePathParser";

// comes from fsMain.fs
declare let FS: EmscriptenFs;

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
  public async createOrOverwriteFile(fs: KieSandboxWorkspacesFs, file: StorageFile) {
    const contents = await file.getFileContents();
    try {
      await fs.promises.writeFile(file.path, contents);
    } catch (err) {
      await this.mkdirDeep(fs, dirname(file.path));
      await fs.promises.writeFile(file.path, contents);
    }
  }

  public async updateFile(
    fs: KieSandboxWorkspacesFs,
    path: string,
    getFileContents: () => Promise<Uint8Array>
  ): Promise<void> {
    if (!(await this.exists(fs, path))) {
      throw new Error(`File ${path} does not exist`);
    }

    const content = await getFileContents();
    await fs.promises.writeFile(path, content);
  }

  public async deleteFile(fs: KieSandboxWorkspacesFs, path: string): Promise<void> {
    await fs.promises.unlink(path);
  }

  public async renameFile(fs: KieSandboxWorkspacesFs, file: StorageFile, newFileName: string): Promise<StorageFile> {
    if (!(await this.exists(fs, file.path))) {
      throw new Error(`File ${file.path} does not exist`);
    }

    if (basename(file.path) === newFileName) {
      return file;
    }

    const extension = extractExtension(file.path);
    const newPath = join(dirname(file.path), `${newFileName}${extension ? "." + extension : ""}`);

    if (await this.exists(fs, newPath)) {
      throw new Error(`File ${newPath} already exists`);
    }

    const newFile = new StorageFile({
      path: newPath,
      getFileContents: () => this.getFileContent(fs, newPath),
    });

    await fs.promises.rename(file.path, newFile.path);

    return newFile;
  }

  public async moveFile(fs: KieSandboxWorkspacesFs, file: StorageFile, newDirPath: string): Promise<StorageFile> {
    if (!(await this.exists(fs, file.path))) {
      throw new Error(`File ${file.path} does not exist`);
    }

    await this.mkdirDeep(fs, newDirPath);
    const newPath = join(newDirPath, basename(file.path));
    const fileToMove = new StorageFile({
      getFileContents: file.getFileContents,
      path: newPath,
    });
    await this.createOrOverwriteFile(fs, fileToMove);
    await this.deleteFile(fs, file.path);

    return new StorageFile({
      getFileContents: () => this.getFileContent(fs, newPath),
      path: newPath,
    });
  }

  public async moveFiles(
    fs: KieSandboxWorkspacesFs,
    files: StorageFile[],
    newDirPath: string
  ): Promise<Map<string, string>> {
    const paths = new Map<string, string>();
    for (const fileToMove of files) {
      const movedFile = await this.moveFile(fs, fileToMove, newDirPath);
      paths.set(fileToMove.path, movedFile.path);
    }
    return paths;
  }

  public async getFileContent(fs: KieSandboxWorkspacesFs, path: string): Promise<Uint8Array> {
    if (!(await this.exists(fs, path))) {
      throw new Error(`File '${path}' doesn't exist`);
    }

    return (await fs.promises.readFile(path)) as Uint8Array;
  }

  public async getFile(fs: KieSandboxWorkspacesFs, path: string): Promise<StorageFile | undefined> {
    if (!(await this.exists(fs, path))) {
      return;
    }

    return new StorageFile({
      path,
      getFileContents: () => fs.promises.readFile(path) as Promise<Uint8Array>,
    });
  }

  async mkdirDeep(fs: KieSandboxWorkspacesFs, dirPath: string, _selfCall = false) {
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
        await this.mkdirDeep(fs, parent);
        await this.mkdirDeep(fs, dirPath, true);
      }
    }
  }

  public async exists(fs: KieSandboxWorkspacesFs, path: string): Promise<boolean> {
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

  public async walk<T = string>(args: {
    schema: FsSchema;
    baseAbsolutePath: string;
    shouldExcludeAbsolutePath: (dirPath: string) => boolean;
    onVisit: (args: { absolutePath: string; relativePath: string }) => Promise<T | undefined>;
  }): Promise<T[]> {
    const files = await Promise.all(
      [...args.schema.entries()].flatMap(async ([absolutePath, { ino, mode }]) => {
        if (FS.isDir(mode)) {
          return [];
        }

        if (args.shouldExcludeAbsolutePath(absolutePath)) {
          return [];
        }

        const relativePath = relative(args.baseAbsolutePath, absolutePath);
        const visit = await args.onVisit({ absolutePath, relativePath });
        return visit ? [visit] : [];
      })
    );

    return files.reduce((res: T[], acc) => (acc ? res.concat(acc) : res), []) as T[];
  }
}
