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

import type KieSandboxFs from "@kie-tools/kie-sandbox-fs";
import { basename, dirname, join, relative, resolve } from "path";
import { extractExtension } from "../relativePath/WorkspaceFileRelativePathParser";

export class LfsStorageFile {
  constructor(private readonly args: { path: string; getFileContents: () => Promise<Uint8Array> }) {}

  get path() {
    return this.args.path;
  }

  get getFileContents() {
    return this.args.getFileContents;
  }
}

export class LfsStorageService {
  public async createOrOverwriteFile(fs: KieSandboxFs, file: LfsStorageFile) {
    const contents = await file.getFileContents();
    try {
      await fs.promises.writeFile(file.path, contents);
    } catch (err) {
      await this.mkdirDeep(fs, dirname(file.path));
      await fs.promises.writeFile(file.path, contents);
    }
  }

  public async updateFile(fs: KieSandboxFs, file: LfsStorageFile): Promise<void> {
    if (!(await this.exists(fs, file.path))) {
      throw new Error(`File ${file.path} does not exist`);
    }

    const content = await file.getFileContents();
    await fs.promises.writeFile(file.path, content);
  }

  public async deleteFile(fs: KieSandboxFs, path: string): Promise<void> {
    await fs.promises.unlink(path);
  }

  public async renameFile(fs: KieSandboxFs, file: LfsStorageFile, newFileName: string): Promise<LfsStorageFile> {
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

    const newFile = new LfsStorageFile({
      path: newPath,
      getFileContents: file.getFileContents,
    });

    await fs.promises.rename(file.path, newFile.path);

    return newFile;
  }

  public async getFile(fs: KieSandboxFs, path: string): Promise<LfsStorageFile | undefined> {
    if (!(await this.exists(fs, path))) {
      return;
    }

    return new LfsStorageFile({
      path,
      getFileContents: () => fs.promises.readFile(path),
    });
  }

  async mkdirDeep(fs: KieSandboxFs, dirPath: string, _selfCall = false) {
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

  public async exists(fs: KieSandboxFs, path: string): Promise<boolean> {
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

  public async createFiles(fs: KieSandboxFs, files: LfsStorageFile[]) {
    if (!fs.promises.writeFileBulk) {
      throw new Error("Can't write bulk");
    }

    for (const file of files) {
      await this.mkdirDeep(fs, dirname(file.path));
    }

    const filesArray = await Promise.all(
      files.map(async (f) => [f.path, await f.getFileContents()] as [string, Uint8Array])
    );

    await fs.promises.writeFileBulk(filesArray);
  }

  public async walk<T = string>(args: {
    fs: KieSandboxFs;
    startFromDirPath: string;
    shouldExcludeDir: (dirPath: string) => boolean;
    onVisit: (args: { absolutePath: string; relativePath: string }) => Promise<T | undefined>;
    originalStartingDirPath?: string;
  }): Promise<T[]> {
    const subDirPaths = await args.fs.promises.readdir(args.startFromDirPath);
    const files = await Promise.all(
      subDirPaths.map(async (subDirPath) => {
        const absolutePath = resolve(args.startFromDirPath, subDirPath);
        const relativePath = relative(args.originalStartingDirPath ?? args.startFromDirPath, absolutePath);
        return !(await args.fs.promises.stat(absolutePath)).isDirectory()
          ? args.onVisit({ absolutePath, relativePath })
          : args.shouldExcludeDir(absolutePath)
          ? []
          : this.walk({
              fs: args.fs,
              startFromDirPath: absolutePath,
              shouldExcludeDir: args.shouldExcludeDir,
              onVisit: args.onVisit,
              originalStartingDirPath: args.originalStartingDirPath ?? args.startFromDirPath,
            });
      })
    );

    return files.reduce((paths: T[], path) => {
      return path ? paths.concat(path) : paths;
    }, []) as T[];
  }

  public async getFiles(fs: KieSandboxFs, paths: string[]): Promise<LfsStorageFile[]> {
    if (!fs.promises.readFileBulk) {
      throw new Error("Can't read bulk");
    }

    const files = await fs.promises.readFileBulk(paths);
    return files.map(([path, content]) => {
      return new LfsStorageFile({
        path,
        getFileContents: async () => content,
      });
    });
  }
}
