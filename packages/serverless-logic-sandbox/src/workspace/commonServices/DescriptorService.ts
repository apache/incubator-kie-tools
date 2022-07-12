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

import KieSandboxFs from "@kie-tools/kie-sandbox-fs";
import DefaultBackend from "@kie-tools/kie-sandbox-fs/dist/DefaultBackend";
import DexieBackend from "@kie-tools/kie-sandbox-fs/dist/DexieBackend";
import { StorageFile, StorageService } from "./StorageService";
import { jsonParseWithUrl } from "../../json/JsonParse";
import { decoder, encoder } from "./BaseFile";

export const NEW_WORKSPACE_DEFAULT_NAME = `Untitled Folder`;

export type DescriptorServiceConfig = {
  descriptorFsName: string;
  idField: string;
  nameField: string;
};

export type DescriptorBase = {
  [key: string]: any;
  createdDateISO: string;
  lastUpdatedDateISO: string;
};

export abstract class DescriptorService<T extends DescriptorBase, CreateTArgs extends any> {
  protected descriptorsFs: KieSandboxFs;

  constructor(
    protected readonly storageService: StorageService,
    protected readonly config: DescriptorServiceConfig,
    descriptorsFs?: KieSandboxFs
  ) {
    this.descriptorsFs =
      descriptorsFs ||
      new KieSandboxFs(this.config.descriptorFsName, {
        backend: new DefaultBackend({
          idbBackendDelegate: (fileDbName, fileStoreName) => {
            return new DexieBackend(fileDbName, fileStoreName);
          },
        }) as any,
      });
  }

  public abstract getDescriptorPath(id: string): string;

  public abstract createNewDescriptor(args: CreateTArgs): T;

  public getDescriptorId(descriptor: T): string {
    return descriptor[this.config.idField];
  }

  public async listAll(): Promise<T[]> {
    const descriptorsFilePaths = await this.storageService.walk({
      fs: this.descriptorsFs,
      startFromDirPath: "/",
      shouldExcludeDir: () => false,
      onVisit: async ({ absolutePath }) => absolutePath,
    });

    const descriptorFiles = await this.storageService.getFiles(this.descriptorsFs, descriptorsFilePaths);

    return descriptorFiles.map((descriptorFile) => jsonParseWithUrl(decoder.decode(descriptorFile.content)));
  }

  public async bumpLastUpdatedDate(id: string): Promise<void> {
    await this.storageService.updateFile(
      this.descriptorsFs,
      this.toStorageFile({
        ...(await this.get(id)),
        lastUpdatedDateISO: new Date().toISOString(),
      })
    );
  }

  public async get(id: string): Promise<T> {
    const descriptorFile = await this.storageService.getFile(this.descriptorsFs, this.getDescriptorPath(id));
    if (!descriptorFile) {
      throw new Error(`Descriptor not found (${id})`);
    }
    return jsonParseWithUrl(decoder.decode(await descriptorFile.getFileContents()));
  }

  public async create(args: CreateTArgs) {
    const descriptor = this.createNewDescriptor(args);
    await this.storageService.createOrOverwriteFile(this.descriptorsFs, this.toStorageFile(descriptor));
    return descriptor;
  }

  public async delete(id: string) {
    await this.storageService.deleteFile(this.descriptorsFs, this.getDescriptorPath(id));
  }

  public async rename(id: string, newName: string) {
    await this.storageService.updateFile(
      this.descriptorsFs,
      this.toStorageFile({
        ...(await this.get(id)),
        [this.config.nameField]: newName,
      })
    );
  }

  protected toStorageFile(descriptor: T) {
    return new StorageFile({
      path: this.getDescriptorPath(this.getDescriptorId(descriptor)),
      getFileContents: () => Promise.resolve(encoder.encode(JSON.stringify(descriptor))),
    });
  }
}
