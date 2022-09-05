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
import DexieBackend from "@kie-tools/kie-sandbox-fs/dist/DexieBackend";
import { InMemoryBackend } from "./InMemoryBackend";
import DefaultBackend from "@kie-tools/kie-sandbox-fs/dist/DefaultBackend";
import { WorkspaceDescriptorService } from "./WorkspaceDescriptorService";
import { FsCache } from "./FsCache";

export class WorkspaceFsService {
  constructor(
    private readonly descriptorService: WorkspaceDescriptorService,
    private readonly fsCache = new FsCache()
  ) {}

  public async getWorkspaceFs(workspaceId: string) {
    if (!(await this.descriptorService.get(workspaceId))) {
      throw new Error(`Can't get FS for non-existent workspace '${workspaceId}'`);
    }
    return this.fsCache.getOrCreateFs(workspaceId);
  }

  public async withInMemoryFs<T>(workspaceId: string, callback: (fs: KieSandboxFs) => Promise<T>) {
    const { fs, flush } = await this.createInMemoryWorkspaceFs(workspaceId);
    const ret = await callback(fs);
    await flush();
    return ret;
  }

  private async createInMemoryWorkspaceFs(workspaceId: string) {
    const readEntireFs = async (dexieBackend: DexieBackend) => {
      console.debug("MEM :: Reading FS to memory");
      await dexieBackend._dexie.open();
      const keys = await dexieBackend._dexie.table(dexieBackend._storename).toCollection().keys();
      const data = await dexieBackend.readFileBulk(keys);
      const fsAsMapConstructorParameter: any[] = [];
      for (let i = 0; i < data.length; i++) {
        fsAsMapConstructorParameter[i] = [keys[i], data[i]];
      }
      return fsAsMapConstructorParameter;
    };

    const dbName = workspaceId; // don't change. (This is hardcoded on KieSandboxFs).
    const storeName = workspaceId + "_files"; // don't change (This is hardcoded on KieSandboxFs).
    const dexieBackend = new DexieBackend(dbName, storeName);
    const inMemoryBackend = new InMemoryBackend(new Map(await readEntireFs(dexieBackend)));

    const flush = async () => {
      // TODO: Mutate `inMemoryBackend` to not allow further use after flush.
      // TODO: Make a lock mechanism to not allow interactions with this FS while the in-memory operation is in progress
      // TODO: Improve `autoinc` performance. Right now it's iterating over the superblock on each write.
      return new Promise<void>((res) => {
        setTimeout(async () => {
          const inodeBulk = Array.from(inMemoryBackend.fs.keys());
          const dataBulk = Array.from(inMemoryBackend.fs.values());
          console.debug("MEM :: Flushing in memory FS");
          await dexieBackend.writeFileBulk(inodeBulk, dataBulk);
          res();
        }, 500); // necessary to wait for debounce of 500ms (This is hardcoded on KieSandboxFs).
      });
    };

    const fs = new KieSandboxFs(dbName, {
      backend: new DefaultBackend({ idbBackendDelegate: () => inMemoryBackend }) as any,
    });

    return { fs, flush };
  }
}
