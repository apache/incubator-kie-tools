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

import { FsCache, FsSchema } from "./FsCache";
import { KieSandboxWorkspacesFs } from "./KieSandboxWorkspaceFs";
import { FsFlushManager } from "./FsFlushManager";
import { FsUsageCounter } from "./FsUsageCounter";
import { FsUnloadManager } from "./FsUnloadManager";
import { WorkspaceBroadcastEvents } from "../worker/api/WorkspaceBroadcastEvents";
import { WorkspaceFileBroadcastEvents } from "../worker/api/WorkspaceFileBroadcastEvents";
import {
  WORKSPACES_BROADCAST_CHANNEL,
  WORKSPACES_FILES_BROADCAST_CHANNEL,
  WorkspacesBroadcastEvents,
  WorkspacesFilesBroadcastEvents,
} from "../worker/api/WorkspacesBroadcastEvents";

export type BroadcasterWatchEvent =
  | {
      channel: typeof WORKSPACES_BROADCAST_CHANNEL;
      onMessage: (message: WorkspacesBroadcastEvents) => void;
    }
  | {
      channel: typeof WORKSPACES_FILES_BROADCAST_CHANNEL;
      onMessage: (message: WorkspacesFilesBroadcastEvents) => void;
    }
  | {
      channel: string;
      onMessage: (message: WorkspaceBroadcastEvents | WorkspaceFileBroadcastEvents) => void;
    };

export class Broadcastee {
  watch(args: BroadcasterWatchEvent): void {}
  dispose() {}
}

export type BroadcasterEvent =
  | {
      channel: typeof WORKSPACES_BROADCAST_CHANNEL;
      message: () => Promise<WorkspacesBroadcastEvents>;
    }
  | {
      channel: typeof WORKSPACES_FILES_BROADCAST_CHANNEL;
      message: () => Promise<WorkspacesFilesBroadcastEvents>;
    }
  | {
      channel: string;
      message: () => Promise<WorkspaceBroadcastEvents | WorkspaceFileBroadcastEvents>;
    };

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
const BIG_FS_FLUSH_DEBOUNCE_TIMEOUT_IN_MS = 500;
const BIG_FS_SIZE_IN_ENTRIES_COUNT = 1000;

export class FsService {
  constructor(
    private readonly args: { name: string },
    private readonly fsFlushManager: FsFlushManager,
    private readonly readWriteFsUsageCounter = new FsUsageCounter(),
    private readonly readonlyFsUsageCounter = new FsUsageCounter(),
    private readonly fsCache = new FsCache(),
    private readonly fsUnloadManager = new FsUnloadManager(
      fsCache,
      readWriteFsUsageCounter,
      readonlyFsUsageCounter,
      fsFlushManager
    )
  ) {}

  public async withReadonlyFsSchema<T>(fsMountPoint: string, callback: (args: { schema: FsSchema }) => Promise<T>) {
    // FS Schemas are never taken out of memory, so only getting is enough :)
    const schema = await this.fsCache.getOrLoadFsSchema(fsMountPoint);
    return await callback({ schema });
  }

  public async withReadWriteInMemoryFs<T>(
    fsMountPoint: string,
    callback: (args: { fs: KieSandboxWorkspacesFs; schema: FsSchema; broadcaster: BroadcasterDispatch }) => Promise<T>
  ) {
    // If there's an unloading in progress, there's not much we can do other than wait for it to finish
    // and request a new FS again.
    await this.fsUnloadManager.makeSpaceForOrWaitUnloadOf(fsMountPoint);

    // Count this usage in.
    this.readWriteFsUsageCounter.addUsage(fsMountPoint);

    // Get the schema and the FS, loading it if necessary.
    const schema = await this.fsCache.getOrLoadFsSchema(fsMountPoint);
    const fs = await this.fsCache.getOrLoadFs(fsMountPoint);

    try {
      // If there's a flush scheduled, no need to keep it there, as we'll schedule one right after using the FS.
      this.fsFlushManager.pauseScheduledFlushIfScheduled(fsMountPoint);
      return await callback({ fs, schema, broadcaster: new Broadcaster() });
    } finally {
      // After using the FS, we need to decide if we're going to flush/unload it or not.
      // Regardless of exceptions that may have occurred.

      // If there's still someone using the FS, let them request the flush/unload when they're done.
      const { usagesLeft } = this.readWriteFsUsageCounter.releaseUsage(fsMountPoint);
      if (usagesLeft > 0) {
        console.log(`[${this.args.name}] Skipping flush/unload for ${fsMountPoint}. (${usagesLeft} usages left.)`);
      }

      // If this usage is the last one using the FS, it's its job to request the flush.
      else {
        const { didTriggerUnload } = this.fsUnloadManager.unloadFsIfMarkedAndNotInUse(fsMountPoint);
        if (didTriggerUnload) {
          console.log(`[${this.args.name}] Unload triggered for ${fsMountPoint}`);
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
    callback: (args: { fs: KieSandboxWorkspacesFs; schema: FsSchema }) => Promise<T>
  ) {
    await this.fsUnloadManager.makeSpaceForOrWaitUnloadOf(fsMountPoint);

    this.readonlyFsUsageCounter.addUsage(fsMountPoint);
    const schema = await this.fsCache.getOrLoadFsSchema(fsMountPoint);
    const fs = await this.fsCache.getOrLoadFs(fsMountPoint);

    try {
      return await callback({ fs: this.getReadonlyFs(fsMountPoint, fs), schema });
    } finally {
      this.readonlyFsUsageCounter.releaseUsage(fsMountPoint);
      this.fsUnloadManager.unloadFsIfMarkedAndNotInUse(fsMountPoint);
    }
  }

  private getReadonlyFs(fsMountPoint: string, readWriteFs: KieSandboxWorkspacesFs) {
    const throwCantMutateReadonlyFsException = async () => {
      throw new Error(`[${this.args.name}] Can't mutate read-only FS - ${fsMountPoint}`);
    };
    return {
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
    };
  }

  private async getFlushDebounceTimeoutInMs(fsCache: FsCache, fsMountPoint: string) {
    if ((await fsCache.getOrLoadFsSchema(fsMountPoint)).size > BIG_FS_SIZE_IN_ENTRIES_COUNT) {
      return BIG_FS_FLUSH_DEBOUNCE_TIMEOUT_IN_MS;
    } else {
      return DEFAULT_FS_FLUSH_DEBOUNCE_TIMEOUT_IN_MS;
    }
  }
}
