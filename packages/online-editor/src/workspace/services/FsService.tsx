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
import { FlushOrDeinitStateStatus, FsFlushManager } from "./FsFlushManager";

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
  constructor(
    private readonly args: { name: string; debounceFlushesIfBigFs: boolean },
    private readonly fsFlushManager: FsFlushManager,
    private readonly fsCache = new FsCache()
  ) {}

  private readonly readWriteFsUsageCounter = new Map<string, number>();

  private readonly bigFsDebounceTimeoutInMs = 2000;
  private readonly bigFsSizeInEntryCount = 1000;

  public async withReadonlyFsSchema<T>(fsMountPoint: string, callback: (args: { fsSchema: FsSchema }) => Promise<T>) {
    // If there's a `deinit` in progress, there's not much we can do other than wait for it to finish
    // and request a new FS again.
    await this.awaitIfDeinitInProgress(fsMountPoint);
    const fsSchema = await this.fsCache.getOrCreateFsSchema(fsMountPoint);
    return await callback({ fsSchema });
  }

  public async withReadWriteInMemoryFs<T>(
    fsMountPoint: string,
    callback: (args: { fs: KieSandboxWorkspacesFs; broadcaster: BroadcasterDispatch }) => Promise<T>
  ) {
    // If there's a `deinit` in progress, there's not much we can do other than wait for it to finish
    // and request a new FS again.
    await this.awaitIfDeinitInProgress(fsMountPoint);

    // Count this usage in. 1 if that's the first time.
    console.log(`[${this.args.name}] Adding self to usage counter ${fsMountPoint}`);
    this.readWriteFsUsageCounter.set(fsMountPoint, (this.readWriteFsUsageCounter.get(fsMountPoint) ?? 0) + 1);

    // Get the FS, bringing it to memory if necessary.
    const readWriteFs = await this.fsCache.getOrCreateFs(fsMountPoint);

    try {
      // If there's a flush scheduled, no need to keep it there, as we'll schedule one right after using the FS.
      this.fsFlushManager.descheduleFlushIfScheduled(fsMountPoint);
      return await callback({ fs: readWriteFs, broadcaster: new Broadcaster() });
    } finally {
      // After using the FS, we need to decide if we're going to flush it or not.
      // Regardless of exceptions that may have occurred.

      // At this point, we still have our 'self' usage included in the shared usage counter.
      const countWithSelf = this.readWriteFsUsageCounter.get(fsMountPoint);
      if (!countWithSelf || countWithSelf < 1) {
        console.error(`[${this.args.name}] Catastrophic error after using FS for ${fsMountPoint}.`);
      }

      // Phew! Nothing to worry about, everything according to the plan.
      else {
        const countWithoutSelf = countWithSelf - 1;

        // And remove our 'self' usage from the shared usage counter.
        this.readWriteFsUsageCounter.set(fsMountPoint, countWithoutSelf);
        console.log(`[${this.args.name}] Removing self from usage counter ${fsMountPoint}`);

        // Without our 'self' usage, if there's still someone using the FS, we let them request the flush when they're done.
        if (countWithoutSelf > 0) {
          console.log(
            `[${this.args.name}] Skipping flush for ${fsMountPoint} as another usage will do it. (${countWithoutSelf} more before flushing)`
          );
        }

        // If this usage is the last one using the FS, it's its job to request the flush.
        else {
          console.log(`[${this.args.name}] Requesting flush for ${fsMountPoint}`);
          await this.fsFlushManager.requestFsFlush(
            this.fsCache,
            fsMountPoint,
            { deinit: false },
            { debounceTimeoutInMs: await this.getDebounceTimeoutInMs(fsMountPoint) }
          );
        }
      }
    }
  }

  private async getDebounceTimeoutInMs(fsMountPoint: string) {
    if (!this.args.debounceFlushesIfBigFs) {
      return 100;
    } else if ((await this.fsCache.getOrCreateFsSchema(fsMountPoint)).size > this.bigFsSizeInEntryCount) {
      return this.bigFsDebounceTimeoutInMs;
    } else {
      return 100;
    }
  }

  public async withReadonlyInMemoryFs<T>(
    fsMountPoint: string,
    callback: (args: { fs: KieSandboxWorkspacesFs }) => Promise<T>
  ) {
    // If there's a `deinit` in progress, there's not much we can do other than wait for it to finish
    // and request a new FS again.
    await this.awaitIfDeinitInProgress(fsMountPoint);
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

  private async awaitIfDeinitInProgress(fsMountPoint: string) {
    const state = this.fsFlushManager.stateControl.get(fsMountPoint);
    if (state?.status === FlushOrDeinitStateStatus.DEINIT_IN_PROGRESS) {
      console.info(`[${this.args.name}] FS requested while deinit is in progress ${fsMountPoint}. Awaiting.`);
      await state.runningTaskPromise;
    }
  }
}
