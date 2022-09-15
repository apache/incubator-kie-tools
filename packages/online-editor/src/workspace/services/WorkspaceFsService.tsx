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

import type KieSandboxFs from "@kie-tools/kie-sandbox-fs";
import { WorkspaceDescriptorService } from "./WorkspaceDescriptorService";
import { flushFs, FsCache } from "./FsCache";

export class WorkspaceFsService {
  constructor(
    private readonly workspaceDescriptorService: WorkspaceDescriptorService,
    private readonly fsCache = new FsCache()
  ) {}

  public async withReadWriteInMemoryFs<T>(workspaceId: string, callback: (fs: KieSandboxFs) => Promise<T>) {
    const { fs, flush } = await this.createInMemoryWorkspaceFs(workspaceId);
    const ret = await callback(fs);
    await flush();
    return ret;
  }

  public async withReadonlyInMemoryFs<T>(workspaceId: string, callback: (fs: KieSandboxFs) => Promise<T>) {
    const { fs } = await this.createInMemoryWorkspaceFs(workspaceId);
    const readonlyFs = {
      promises: {
        writeFile: async (path: any, data: any, options: any) => {
          throw new Error("Can't mutate read-only FS - " + workspaceId);
        },
        unlink: async (path: any) => {
          throw new Error("Can't mutate read-only FS - " + workspaceId);
        },
        mkdir: async (path: any, mode: any) => {
          throw new Error("Can't mutate read-only FS - " + workspaceId);
        },
        rmdir: async (path: any) => {
          throw new Error("Can't mutate read-only FS - " + workspaceId);
        },
        symlink: async (target: any, path: any, type: any) => {
          throw new Error("Can't mutate read-only FS - " + workspaceId);
        },
        chmod: async (path: any, mode: any) => {
          throw new Error("Can't mutate read-only FS - " + workspaceId);
        },
        readFile: async (path: string, options: any) => {
          return fs.promises.readFile(path, options);
        },
        readdir: async (path: any, options: any) => {
          return fs.promises.readdir(path, options);
        },
        stat: async (path: any, options: any) => {
          return fs.promises.stat(path, options);
        },
        lstat: async (path: any, options: any) => {
          return fs.promises.lstat(path, options);
        },
        readlink: async (path: any, options: any) => {
          return fs.promises.readlink(path, options);
        },
      },
    };

    return await callback(readonlyFs as any);
  }

  private async createInMemoryWorkspaceFs(workspaceId: string) {
    const fs = await this.fsCache.getOrCreateFs(workspaceId);
    const flush = () => {
      console.time("Flush FS - " + workspaceId);
      console.log("Flushing FS - " + workspaceId);
      const ret = flushFs(fs, workspaceId);
      console.timeEnd("Flush FS - " + workspaceId);
      return ret;
    };
    return { fs, flush };
  }
}
