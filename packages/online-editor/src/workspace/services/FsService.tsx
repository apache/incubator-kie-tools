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
import { FsFlushManager } from "./FsFlushManager";
import { ReadWriteFsUsageManager } from "./ReadWriteFsUsageManager";
import { FsDeinitManager } from "./FsDeinitManager";

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

const DEFAULT_FS_FLUSH_DEBOUNCE_TIMEOUT_IN_MS = 100;
const BIG_FS_FLUSH_DEBOUNCE_TIMEOUT_IN_MS = 2000;
const BIG_FS_SIZE_IN_ENTRIES_COUNT = 1000;

export class FsService {
  constructor(
    private readonly args: { name: string },
    private readonly fsFlushManager: FsFlushManager,
    private readonly readWriteFsUsageManager = new ReadWriteFsUsageManager(),
    private readonly fsCache = new FsCache(),
    private readonly fsDeinitManager = new FsDeinitManager(fsCache, readWriteFsUsageManager, fsFlushManager)
  ) {}

  public async withReadonlyFsSchema<T>(fsMountPoint: string, callback: (args: { fsSchema: FsSchema }) => Promise<T>) {
    // FS Schemas are never taken out of memory, so only getting is enough :)
    const fsSchema = await this.fsCache.getOrCreateFsSchema(fsMountPoint);
    return await callback({ fsSchema });
  }

  public async withReadWriteInMemoryFs<T>(
    fsMountPoint: string,
    callback: (args: { fs: KieSandboxWorkspacesFs; broadcaster: BroadcasterDispatch }) => Promise<T>
  ) {
    // If there's a `deinit` in progress, there's not much we can do other than wait for it to finish
    // and request a new FS again.
    await this.fsDeinitManager.manageForRequestOf(fsMountPoint);

    // Count this usage in. 1 if that's the first time.
    this.readWriteFsUsageManager.ackUsageFor(fsMountPoint);

    // Get the FS, bringing it to memory if necessary.
    const readWriteFs = await this.fsCache.getOrCreateFs(fsMountPoint);

    try {
      // If there's a flush scheduled, no need to keep it there, as we'll schedule one right after using the FS.
      this.fsFlushManager.pauseFlushScheduleIfScheduled(fsMountPoint);
      return await callback({ fs: readWriteFs, broadcaster: new Broadcaster() });
    } finally {
      // After using the FS, we need to decide if we're going to flush/deinit it or not.
      // Regardless of exceptions that may have occurred.

      // Without our 'self' usage, if there's still someone using the FS, we let them request the flush/deinit when they're done.
      const { usagesLeft } = this.readWriteFsUsageManager.releaseUsageFor(fsMountPoint);
      if (usagesLeft > 0) {
        console.log(`[${this.args.name}] Skipping flush/deinit for ${fsMountPoint}. (${usagesLeft} usages left.)`);
      }

      // If this usage is the last one using the FS, it's its job to request the flush.
      else {
        const { didTriggerDeinit } = this.fsDeinitManager.maybeDeinit(fsMountPoint);
        if (didTriggerDeinit) {
          console.log(`[${this.args.name}] Deinit triggered for ${fsMountPoint}`);
        } else {
          console.log(`[${this.args.name}] Requesting flush for ${fsMountPoint}`);
          const debounceTimeoutInMs = await this.getFlushDebounceTimeoutInMs(this.fsCache, fsMountPoint);
          await this.fsFlushManager.requestFsFlush(this.fsCache, fsMountPoint, { debounceTimeoutInMs });
        }
      }
    }
  }

  public async withReadonlyInMemoryFs<T>(
    fsMountPoint: string,
    callback: (args: { fs: KieSandboxWorkspacesFs }) => Promise<T>
  ) {
    // If there's a `deinit` in progress, there's not much we can do other than wait for it to finish
    // and request a new FS again.
    await this.fsDeinitManager.manageForRequestOf(fsMountPoint);

    // Get the FS, bringing it to memory if necessary.
    const readWriteFs = await this.fsCache.getOrCreateFs(fsMountPoint);

    const throwCantMutateReadonlyFsException = async () => {
      throw new Error(`[${this.args.name}] Can't mutate read-only FS - ${fsMountPoint}`);
    };

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

  private async getFlushDebounceTimeoutInMs(fsCache: FsCache, fsMountPoint: string) {
    if ((await fsCache.getOrCreateFsSchema(fsMountPoint)).size > BIG_FS_SIZE_IN_ENTRIES_COUNT) {
      return BIG_FS_FLUSH_DEBOUNCE_TIMEOUT_IN_MS;
    } else {
      return DEFAULT_FS_FLUSH_DEBOUNCE_TIMEOUT_IN_MS;
    }
  }
}
