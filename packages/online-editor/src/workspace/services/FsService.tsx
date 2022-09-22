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
import { FlushStateStatus, FsFlushManager } from "./FsFlushManager";

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

const MAX_NUMBER_OF_CACHED_FS_INSTANCES = 1;

const DEFAULT_FS_FLUSH_DEBOUNCE_TIMEOUT_IN_MS = 100;
const BIG_FS_FLUSH_DEBOUNCE_TIMEOUT_IN_MS = 2000;
const BIG_FS_SIZE_IN_ENTRIES_COUNT = 1000;

interface PromiseImperativeHandle<T> {
  promise: Promise<T>;
  resolve: (value: T) => void;
  reject: (err: unknown) => void;
}

function imperativePromiseHandle<T>(): PromiseImperativeHandle<T> {
  let resolve: PromiseImperativeHandle<T>["resolve"] | undefined;
  let reject: PromiseImperativeHandle<T>["reject"] | undefined;

  const promise = new Promise<T>((res, rej) => {
    resolve = res;
    reject = rej;
  });

  // Promise constructors run synchronously, so resolve and reject will always be assigned.
  return { promise, resolve: resolve!, reject: reject! };
}

export class FsService {
  constructor(
    private readonly args: { name: string; debounceFlushesIfBigFs: boolean },
    private readonly fsFlushManager: FsFlushManager,
    private readonly fsCache = new FsCache()
  ) {}

  public async withReadonlyFsSchema<T>(fsMountPoint: string, callback: (args: { fsSchema: FsSchema }) => Promise<T>) {
    const fsSchema = await this.fsCache.getOrCreateFsSchema(fsMountPoint);
    return await callback({ fsSchema });
  }

  private readonly readWriteFsUsageCounter = new Map<string, number>();
  private readonly markedDeinits = new Map<string, PromiseImperativeHandle<void>>();

  public async deinitFs(fsMountPoint: string, deinit: PromiseImperativeHandle<void>) {
    const flushState = this.fsFlushManager.stateControl.get(fsMountPoint);

    if (!flushState) {
      // nothing to do, can deinit directly
    }

    // wait for ongoing flush to end, then deinit
    else if (flushState?.status === FlushStateStatus.FLUSH_IN_PROGRESS) {
      await flushState.runningTaskPromise;
    }

    // deschedule, execute flush, then deinit
    else if (flushState?.status === FlushStateStatus.FLUSH_SCHEDULED) {
      clearTimeout(flushState.scheduledTask);
      await this.fsFlushManager.executeFlush(this.fsCache, fsMountPoint);
    }

    // simply execute flush, then deinit
    else if (flushState?.status === FlushStateStatus.FLUSH_PAUSED) {
      await this.fsFlushManager.executeFlush(this.fsCache, fsMountPoint);
    }

    // should never reach this point
    else {
      throw new Error("Catastrophic error while managing flush before deinitting.");
    }

    await this.fsCache.deinitFs(fsMountPoint);
    deinit.resolve();
  }

  private async deinitManagement(fsMountPoint: string) {
    if (this.fsCache.cache.has(fsMountPoint) || this.fsCache.cache.size < MAX_NUMBER_OF_CACHED_FS_INSTANCES) {
      console.debug(`[${this.args.name}] Nothing to deinit when requesting to use ${fsMountPoint}.`);
    } else {
      // Need to deinit one of the file systems. Let's decide it at chance. Most clever would be LRU.
      // Need to deinit FS when it's not in use anymore.
      // From this point on, no new references to this FS can be done until it is completely deinitted.
      const fsMountPointToDeinit = [...this.fsCache.cache.keys()][0];
      console.debug(`[${this.args.name}] Deinitting ${fsMountPointToDeinit}`);
      if (this.markedDeinits.has(fsMountPointToDeinit)) {
        return;
      }

      const { promise, resolve, reject } = imperativePromiseHandle<void>();

      this.markedDeinits.set(fsMountPointToDeinit, {
        promise: promise.finally(() => this.markedDeinits.delete(fsMountPointToDeinit)),
        resolve,
        reject,
      });

      const deinitImmediately = !this.readWriteFsUsageCounter.get(fsMountPointToDeinit);
      if (deinitImmediately) {
        setTimeout(() => {
          this.deinitFs(fsMountPointToDeinit, this.markedDeinits.get(fsMountPointToDeinit)!);
        }, 0);
      }
    }

    await this.markedDeinits.get(fsMountPoint)?.promise;
  }

  public async withReadWriteInMemoryFs<T>(
    fsMountPoint: string,
    callback: (args: { fs: KieSandboxWorkspacesFs; broadcaster: BroadcasterDispatch }) => Promise<T>
  ) {
    // If there's a `deinit` in progress, there's not much we can do other than wait for it to finish
    // and request a new FS again.
    await this.deinitManagement(fsMountPoint);

    // Count this usage in. 1 if that's the first time.
    console.log(`[${this.args.name}] Summing self to usage counter ${fsMountPoint}`);
    this.readWriteFsUsageCounter.set(fsMountPoint, (this.readWriteFsUsageCounter.get(fsMountPoint) ?? 0) + 1);

    // Get the FS, bringing it to memory if necessary.
    const readWriteFs = await this.fsCache.getOrCreateFs(fsMountPoint);

    try {
      // If there's a flush scheduled, no need to keep it there, as we'll schedule one right after using the FS.
      this.fsFlushManager.pauseFlushScheduleIfScheduled(fsMountPoint);
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
        console.log(`[${this.args.name}] Subtracting self from usage counter ${fsMountPoint}`);

        // Without our 'self' usage, if there's still someone using the FS, we let them request the flush when they're done.
        if (countWithoutSelf > 0) {
          console.log(
            `[${this.args.name}] Skipping flush for ${fsMountPoint} as another usage will do it. (${countWithoutSelf} more before flushing)`
          );
        }

        // If this usage is the last one using the FS, it's its job to request the flush.
        else {
          this.readWriteFsUsageCounter.delete(fsMountPoint);
          console.log(`[${this.args.name}] Requesting flush for ${fsMountPoint}`);
          const deinit = this.markedDeinits.get(fsMountPoint);
          if (!deinit) {
            await this.fsFlushManager.requestFsFlush(this.fsCache, fsMountPoint, {
              debounceTimeoutInMs: await this.getDebounceTimeoutInMs(fsMountPoint),
            });
          } else {
            setTimeout(async () => {
              await this.deinitFs(fsMountPoint, deinit);
            }, 0);
          }
        }
      }
    }
  }

  private async getDebounceTimeoutInMs(fsMountPoint: string) {
    if (!this.args.debounceFlushesIfBigFs) {
      return DEFAULT_FS_FLUSH_DEBOUNCE_TIMEOUT_IN_MS;
    } else if ((await this.fsCache.getOrCreateFsSchema(fsMountPoint)).size > BIG_FS_SIZE_IN_ENTRIES_COUNT) {
      return BIG_FS_FLUSH_DEBOUNCE_TIMEOUT_IN_MS;
    } else {
      return DEFAULT_FS_FLUSH_DEBOUNCE_TIMEOUT_IN_MS;
    }
  }

  public async withReadonlyInMemoryFs<T>(
    fsMountPoint: string,
    callback: (args: { fs: KieSandboxWorkspacesFs }) => Promise<T>
  ) {
    // If there's a `deinit` in progress, there's not much we can do other than wait for it to finish
    // and request a new FS again.
    await this.deinitManagement(fsMountPoint);
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
}
