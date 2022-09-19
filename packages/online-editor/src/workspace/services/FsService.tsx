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

import { flushFs, FsCache } from "./FsCache";
import { WorkspacesEvents } from "../hooks/WorkspacesHooks";
import { WorkspaceFileEvents } from "../hooks/WorkspaceFileHooks";
import { WorkspaceEvents } from "../hooks/WorkspaceHooks";
import { KieSandboxWorkspacesFs } from "./KieSandboxWorkspaceFs";

export interface BroadcasterEvent {
  channel: string;
  message: () => Promise<WorkspacesEvents | WorkspaceEvents | WorkspaceFileEvents>;
}

export interface BroadcasterDispatch {
  broadcast(args: BroadcasterEvent): void;
}

export class Broadcaster implements BroadcasterDispatch {
  private readonly messages = new Array<BroadcasterEvent>();

  broadcast(args: BroadcasterEvent): void {
    this.messages.push(args);
  }

  async sendAll() {
    return Promise.all(
      this.messages.map(async (bce) => {
        const bc = new BroadcastChannel(bce.channel);
        bc.postMessage(await bce.message());
        bc.close();
      })
    );
  }
}

export class FsService {
  constructor(private readonly fsCache = new FsCache()) {}

  public async withReadWriteInMemoryFs<T>(
    fsMountPoint: string,
    callback: (args: { fs: KieSandboxWorkspacesFs; broadcaster: BroadcasterDispatch }) => Promise<T>
  ) {
    const { fs, flush } = await this.getInMemoryWorkspaceFs(fsMountPoint);
    const broadcaster = new Broadcaster();

    const ret = await callback({ fs, broadcaster });

    await flush();
    await broadcaster.sendAll();

    return ret;
  }

  public async withReadonlyInMemoryFs<T>(
    fsMountPoint: string,
    callback: (args: { fs: KieSandboxWorkspacesFs }) => Promise<T>
  ) {
    const { fs } = await this.getInMemoryWorkspaceFs(fsMountPoint);
    const readonlyFs = {
      promises: {
        writeFile: async (path: string, data: Uint8Array | string, options: any) => {
          throw new Error(`Can't mutate read-only FS - ${fsMountPoint}`);
        },
        unlink: async (path: string) => {
          throw new Error(`Can't mutate read-only FS - ${fsMountPoint}`);
        },
        mkdir: async (path: string, mode?: number) => {
          throw new Error(`Can't mutate read-only FS - ${fsMountPoint}`);
        },
        rmdir: async (path: string) => {
          throw new Error(`Can't mutate read-only FS - ${fsMountPoint}`);
        },
        symlink: async (target: string, path: string, type: any) => {
          throw new Error(`Can't mutate read-only FS - ${fsMountPoint}`);
        },
        chmod: async (path: string, mode: any) => {
          throw new Error(`Can't mutate read-only FS - ${fsMountPoint}`);
        },
        rename: async (path: string, newPath: string) => {
          throw new Error(`Can't mutate read-only FS - ${fsMountPoint}`);
        },
        readFile: async (path: string, options: any) => {
          return fs.promises.readFile(path, options);
        },
        readdir: async (path: string, options: any) => {
          return fs.promises.readdir(path, options);
        },
        stat: async (path: string) => {
          return fs.promises.stat(path);
        },
        lstat: async (path: string) => {
          return fs.promises.lstat(path);
        },
        readlink: async (path: string, options: any) => {
          return fs.promises.readlink(path, options);
        },
      },
    };

    return await callback({ fs: readonlyFs });
  }

  private async getInMemoryWorkspaceFs(fsMountPoint: string) {
    const fs = await this.fsCache.getOrCreateFs(fsMountPoint);

    const flush = () => {
      console.time("Flush FS - " + fsMountPoint);
      console.debug("Flushing FS - " + fsMountPoint);
      const ret = flushFs(fs, fsMountPoint);
      console.timeEnd("Flush FS - " + fsMountPoint);
      return ret;
    };

    return { fs, flush };
  }
}
