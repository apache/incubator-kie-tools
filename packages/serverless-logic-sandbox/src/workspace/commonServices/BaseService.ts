/*
 * Copyright 2022 Red Hat, Inc. and/or its affiliates.
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

import JSZip from "jszip";
import { EagerStorageFile, StorageFile, StorageService } from "../commonServices/StorageService";
import { join, relative } from "path";
import { Minimatch } from "minimatch";
import KieSandboxFs from "@kie-tools/kie-sandbox-fs";
import { DescriptorBase, DescriptorService } from "./DescriptorService";
import { FsService } from "./FsService";
import { BaseFile, encoder } from "./BaseFile";

export type BaseServiceEvents =
  | { type: "ADD"; descriptorId: string }
  | { type: "RENAME"; descriptorId: string }
  | { type: "DELETE"; descriptorId: string }
  | { type: "ADD_FILE"; descriptorId: string; relativePath: string }
  | { type: "MOVE_FILE"; descriptorId: string; newRelativePath: string; oldRelativePath: string }
  | { type: "RENAME_FILE"; descriptorId: string; newRelativePath: string; oldRelativePath: string }
  | { type: "UPDATE_FILE"; descriptorId: string; relativePath: string }
  | { type: "DELETE_FILE"; descriptorId: string; relativePath: string }
  | { type: "MOVE_BATCH"; descriptorId: string; relativePaths: Map<string, string> }
  | { type: "DELETE_BATCH"; descriptorId: string; relativePaths: string[] };

export type BaseServiceCreateProps<T, F> = {
  useInMemoryFs: boolean;
  storeFiles: (fs: KieSandboxFs, descriptor: T) => Promise<F[]>;
  broadcastArgs: { broadcast: boolean };
};

export type BaseServiceConfig = {
  idFieldName: string;
  descriptorBroadcastChannel: string;
};

export abstract class BaseService<
  T extends DescriptorBase,
  F extends BaseFile,
  CreateProps extends BaseServiceCreateProps<T, F>
> {
  public constructor(
    public readonly storageService: StorageService,
    protected readonly descriptorService: DescriptorService<T, any>,
    protected readonly fsService: FsService<DescriptorService<T, any>>
  ) {}

  protected abstract broadcastMessage(args: BaseServiceEvents): void;

  protected abstract newFile(id: string, relativePath: string, getFileContents: () => Promise<Uint8Array>): F;

  protected getDescriptorId(descriptor: T): string {
    return this.descriptorService.getDescriptorId(descriptor);
  }

  protected getDescriptorPath(descriptorId: string): string {
    return this.descriptorService.getDescriptorPath(descriptorId);
  }

  public async create(args: CreateProps) {
    const { useInMemoryFs, storeFiles, broadcastArgs, ...createProps } = args;
    const descriptor = await this.descriptorService.create(createProps);
    const descriptorId = this.getDescriptorId(descriptor);

    try {
      const files = await (args.useInMemoryFs
        ? this.fsService.withInMemoryFs(descriptorId, (fs) => args.storeFiles(fs, descriptor))
        : args.storeFiles(await this.fsService.getFs(descriptorId), descriptor));

      if (args.broadcastArgs.broadcast) {
        this.broadcastMessage({ type: "ADD", descriptorId });
      }

      return { descriptor, files };
    } catch (e) {
      await this.descriptorService.delete(descriptorId);
      throw e;
    }
  }

  public async getFilesWithLazyContent(fs: KieSandboxFs, descriptorId: string, globPattern?: string): Promise<F[]> {
    const matcher = globPattern ? new Minimatch(globPattern, { dot: true }) : undefined;
    const gitDirPath = this.getAbsolutePath({ descriptorId, relativePath: ".git" });

    return await this.storageService.walk({
      fs,
      startFromDirPath: this.getAbsolutePath({ descriptorId }),
      shouldExcludeDir: (dirPath) => dirPath === gitDirPath,
      onVisit: async ({ absolutePath, relativePath }) => {
        const file = this.newFile(descriptorId, relativePath, () =>
          this.storageService.getFile(fs, absolutePath).then((f) => f!.getFileContents())
        );

        if (matcher && !matcher.match(file.name)) {
          return undefined;
        }

        return file;
      },
    });
  }

  public async delete(descriptorId: string, broadcastArgs: { broadcast: boolean }): Promise<void> {
    await this.descriptorService.delete(descriptorId);
    indexedDB.deleteDatabase(this.getDescriptorPath(descriptorId).replace("/", ""));

    if (broadcastArgs.broadcast) {
      this.broadcastMessage({ type: "DELETE", descriptorId });
    }
  }

  public async rename(descriptorId: string, newName: string, broadcastArgs: { broadcast: boolean }): Promise<void> {
    await this.descriptorService.rename(descriptorId, newName);
    await this.descriptorService.bumpLastUpdatedDate(descriptorId);

    if (broadcastArgs.broadcast) {
      this.broadcastMessage({ type: "RENAME", descriptorId });
    }
  }

  public async prepareZip(fs: KieSandboxFs, descriptorId: string): Promise<Blob> {
    const files = await this.getFilesWithLazyContent(fs, descriptorId);
    return this.prepareZipWithFiles(descriptorId, files);
  }

  public async prepareZipWithFiles(descriptorId: string, files: F[]): Promise<Blob> {
    const descriptorRootDirPath = this.getAbsolutePath({ descriptorId });

    const zip = new JSZip();
    for (const file of files) {
      const eagerFile = await this.toEagerFile(file);
      zip.file(relative(descriptorRootDirPath, eagerFile.path), eagerFile.content);
    }

    return zip.generateAsync({ type: "blob" });
  }

  public async createOrOverwriteFile(fs: KieSandboxFs, file: F, broadcastArgs: { broadcast: boolean }): Promise<void> {
    await this.storageService.createOrOverwriteFile(fs, this.toStorageFile(file));
    await this.descriptorService.bumpLastUpdatedDate(file.parentId);

    if (broadcastArgs.broadcast) {
      this.broadcastMessage({ type: "ADD_FILE", descriptorId: file.parentId, relativePath: file.relativePath });
    }
  }

  public async getFile(args: { fs: KieSandboxFs; descriptorId: string; relativePath: string }): Promise<F | undefined> {
    const absolutePath = this.getAbsolutePath(args);
    const storageFile = await this.storageService.getFile(args.fs, absolutePath);
    if (!storageFile) {
      return;
    }
    return this.toDescriptorFile(args.descriptorId, storageFile);
  }

  public async updateFile(
    fs: KieSandboxFs,
    file: F,
    getNewContents: () => Promise<string>,
    broadcastArgs: { broadcast: boolean }
  ): Promise<void> {
    await this.storageService.updateFile(
      fs,
      this.toStorageFile(
        this.newFile(file.parentId, file.relativePath, () => getNewContents().then((c) => encoder.encode(c)))
      )
    );
    await this.descriptorService.bumpLastUpdatedDate(file.parentId);

    if (broadcastArgs.broadcast) {
      this.broadcastMessage({ type: "UPDATE_FILE", descriptorId: file.parentId, relativePath: file.relativePath });
    }
  }

  public async deleteFile(fs: KieSandboxFs, file: F, broadcastArgs: { broadcast: boolean }): Promise<void> {
    await this.storageService.deleteFile(fs, this.toStorageFile(file).path);
    await this.descriptorService.bumpLastUpdatedDate(file.parentId);

    if (broadcastArgs.broadcast) {
      this.broadcastMessage({ type: "DELETE_FILE", descriptorId: file.parentId, relativePath: file.relativePath });
    }
  }

  public async renameFile(args: {
    fs: KieSandboxFs;
    file: F;
    newFileNameWithoutExtension: string;
    broadcastArgs: { broadcast: boolean };
  }): Promise<F> {
    const renamedStorageFile = await this.storageService.renameFile(
      args.fs,
      this.toStorageFile(args.file),
      args.newFileNameWithoutExtension
    );
    const renamedFile = this.toDescriptorFile(args.file.parentId, renamedStorageFile);
    await this.descriptorService.bumpLastUpdatedDate(args.file.parentId);

    if (args.broadcastArgs.broadcast) {
      this.broadcastMessage({
        type: "RENAME_FILE",
        descriptorId: args.file.parentId,
        newRelativePath: renamedFile.relativePath,
        oldRelativePath: args.file.relativePath,
      });
    }

    return renamedFile;
  }

  public async existsFile(args: { fs: KieSandboxFs; descriptorId: string; relativePath: string }): Promise<boolean> {
    return this.storageService.exists(args.fs, this.getAbsolutePath(args));
  }

  public getAbsolutePath(args: { descriptorId: string; relativePath?: string }) {
    return join("/", args.relativePath ?? "");
  }

  public async deleteFiles(fs: KieSandboxFs, files: F[], broadcastArgs: { broadcast: boolean }): Promise<void> {
    if (files.length === 0) {
      return;
    }

    await Promise.all(files.map((f) => this.toStorageFile(f)).map((f) => this.storageService.deleteFile(fs, f.path)));
    await this.descriptorService.bumpLastUpdatedDate(files[0].parentId);

    if (broadcastArgs.broadcast) {
      this.broadcastMessage({
        type: "DELETE_BATCH",
        descriptorId: files[0].parentId,
        relativePaths: files.map((file) => file.relativePath),
      });
    }
  }

  public async moveFile(args: {
    fs: KieSandboxFs;
    file: F;
    newDirPath: string;
    broadcastArgs: { broadcast: boolean };
  }): Promise<F> {
    const movedFile = this.toDescriptorFile(
      args.file.parentId,
      await this.storageService.moveFile(args.fs, this.toStorageFile(args.file), args.newDirPath)
    );
    await this.descriptorService.bumpLastUpdatedDate(args.file.parentId);

    if (args.broadcastArgs.broadcast) {
      this.broadcastMessage({
        type: "MOVE_FILE",
        descriptorId: args.file.parentId,
        newRelativePath: movedFile.relativePath,
        oldRelativePath: args.file.relativePath,
      });
    }

    return movedFile;
  }

  public async moveFiles(args: {
    fs: KieSandboxFs;
    files: F[];
    newDirPath: string;
    broadcastArgs: { broadcast: boolean };
  }): Promise<Map<string, string>> {
    if (args.files.length === 0) {
      return new Map();
    }

    const relativePaths = await this.storageService.moveFiles(
      args.fs,
      args.files.map((f) => this.toStorageFile(f)),
      args.newDirPath
    );
    await this.descriptorService.bumpLastUpdatedDate(args.files[0].parentId);

    if (args.broadcastArgs.broadcast) {
      this.broadcastMessage({ type: "MOVE_BATCH", descriptorId: args.files[0].parentId, relativePaths });
    }

    return relativePaths;
  }

  private toDescriptorFile(descriptorId: string, storageFile: StorageFile): F {
    return this.newFile(
      descriptorId,
      relative(this.getAbsolutePath({ descriptorId }), storageFile.path),
      storageFile.getFileContents
    );
  }

  private toStorageFile(file: F): StorageFile {
    return new StorageFile({
      path: this.getAbsolutePath({ descriptorId: file.parentId, relativePath: file.relativePath }),
      getFileContents: file.getFileContents,
    });
  }

  private async toEagerFile(file: F): Promise<EagerStorageFile> {
    return new EagerStorageFile({
      path: this.getAbsolutePath({ descriptorId: file.parentId, relativePath: file.relativePath }),
      content: await file.getFileContents(),
    });
  }

  public getUniqueFileIdentifier(args: { descriptorId: string; relativePath: string }) {
    return args.descriptorId + "__" + this.getAbsolutePath(args);
  }
}
