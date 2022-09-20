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

import { FsCache, FsSchema } from "./FsCache";
import { WorkspacesEvents } from "../hooks/WorkspacesHooks";
import { WorkspaceFileEvents } from "../hooks/WorkspaceFileHooks";
import { WorkspaceEvents } from "../hooks/WorkspaceHooks";
import { KieSandboxWorkspacesFs } from "./KieSandboxWorkspaceFs";

export interface BroadcasterWatchEvent {
  channel: string;
  onMessage: (message: WorkspacesEvents | WorkspaceEvents | WorkspaceFileEvents) => void;
}

export class Broadcastee {
  watch(args: BroadcasterWatchEvent): void {}
  dispose() {}
}

export interface BroadcasterEvent {
  channel: string;
  message: () => Promise<WorkspacesEvents | WorkspaceEvents | WorkspaceFileEvents>;
}

export interface BroadcasterDispatch {
  broadcast(args: BroadcasterEvent): void;
}

export class Broadcaster implements BroadcasterDispatch {
  async broadcast(args: BroadcasterEvent): Promise<void> {
    const bc = new BroadcastChannel(args.channel);
    bc.postMessage(await args.message());
    bc.close();
  }
}

export class FsService {
  constructor(private readonly fsCache = new FsCache()) {}

  public async withReadWriteInMemoryFs<T>(
    fsMountPoint: string,
    callback: (args: { fs: KieSandboxWorkspacesFs; broadcaster: BroadcasterDispatch }) => Promise<T>
  ) {
    const readWriteFs = await this.fsCache.getOrCreateFs(fsMountPoint);
    const callbackResult = await callback({ fs: readWriteFs, broadcaster: new Broadcaster() });

    await this.fsCache.requestFlush(readWriteFs, fsMountPoint, { deinit: false });

    return callbackResult;
  }

  public async withReadonlyFsSchema<T>(fsMountPoint: string, callback: (args: { fsSchema: FsSchema }) => Promise<T>) {
    const fsSchema = await this.fsCache.getOrCreateFsSchema(fsMountPoint);
    return callback({ fsSchema });
  }

  public async withReadonlyInMemoryFs<T>(
    fsMountPoint: string,
    callback: (args: { fs: KieSandboxWorkspacesFs }) => Promise<T>
  ) {
    const throwCantMutateReadonlyFsException = async () => {
      throw new Error(`Can't mutate read-only FS - ${fsMountPoint}`);
    };

    const readWriteFs = await this.fsCache.getOrCreateFs(fsMountPoint);

    return await callback({
      fs: {
        promises: {
          writeFile: throwCantMutateReadonlyFsException,
          unlink: throwCantMutateReadonlyFsException,
          mkdir: throwCantMutateReadonlyFsException,
          rmdir: throwCantMutateReadonlyFsException,
          symlink: throwCantMutateReadonlyFsException,
          chmod: throwCantMutateReadonlyFsException,
          rename: throwCantMutateReadonlyFsException,
          readFile: async (path: string, options: any) => {
            return readWriteFs.promises.readFile(path, options);
          },
          readdir: async (path: string, options: any) => {
            return readWriteFs.promises.readdir(path, options);
          },
          stat: async (path: string) => {
            return readWriteFs.promises.stat(path);
          },
          lstat: async (path: string) => {
            return readWriteFs.promises.lstat(path);
          },
          readlink: async (path: string, options: any) => {
            return readWriteFs.promises.readlink(path, options);
          },
        },
      },
    });
  }
}
