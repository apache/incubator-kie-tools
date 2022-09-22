/*
 * Copyright 2022 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import { ReadWriteFsUsageManager } from "./ReadWriteFsUsageManager";
import { FlushStateStatus, FsFlushManager } from "./FsFlushManager";
import { FsCache } from "./FsCache";

// We expect that people will not use more than three tabs simultaneously.
const MAX_NUMBER_OF_CACHED_FS_INSTANCES = 3;

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

export class FsDeinitManager {
  constructor(
    private readonly fsCache: FsCache,
    private readonly readWriteFsUsageManager: ReadWriteFsUsageManager,
    private readonly fsFlushManager: FsFlushManager
  ) {}

  private readonly markedDeinits = new Map<string, PromiseImperativeHandle<void>>();

  public async manageForRequestOf(fsMountPoint: string) {
    // If fsMountPoint isn't cached, we need to check if caching it will make it go over the limit of cached instances.
    if (this.fsCache.cache.has(fsMountPoint) || this.fsCache.cache.size < MAX_NUMBER_OF_CACHED_FS_INSTANCES) {
      console.debug(`Nothing to deinit when getting reference to ${fsMountPoint}.`);
    }

    // When that's the case, we get one of the cached FSs and mark it for deinit.
    else {
      const fsMountPointToDeinit = [...this.fsCache.cache.keys()][0]; //FIXME: This strategy is not ideal. Most clever would be LRU.
      console.debug(`Deinitting ${fsMountPointToDeinit} when getting reference to ${fsMountPoint}`);
      if (this.markedDeinits.has(fsMountPointToDeinit)) {
        return;
      }

      const { promise, resolve, reject } = imperativePromiseHandle<void>();
      const deinitPromiseHandle = {
        promise: promise.finally(() => this.markedDeinits.delete(fsMountPointToDeinit)),
        resolve,
        reject,
      };

      this.markedDeinits.set(fsMountPointToDeinit, deinitPromiseHandle);

      // Can immediately deinit FS if it's not in use.
      if (!this.readWriteFsUsageManager.isInUse(fsMountPointToDeinit)) {
        // No need to block the caller by a deinit.
        setTimeout(() => this.deinitFs(fsMountPointToDeinit, deinitPromiseHandle), 0);
      }
    }

    // Can't forget to wait for deinits that might be happening for the requested FS.
    await this.markedDeinits.get(fsMountPoint)?.promise;
  }

  public maybeDeinit(fsMountPoint: string) {
    const deinitPromiseHandle = this.markedDeinits.get(fsMountPoint);
    if (deinitPromiseHandle) {
      // No need to block the caller by a deinit.
      setTimeout(() => this.deinitFs(fsMountPoint, deinitPromiseHandle), 0);
      return { didTriggerDeinit: true };
    } else {
      return { didTriggerDeinit: false };
    }
  }

  private async deinitFs(fsMountPoint: string, deinitPromiseHandle: PromiseImperativeHandle<void>) {
    const flushState = this.fsFlushManager.stateControl.get(fsMountPoint);

    // not requested, not paused, need to flush anyway, as it would've been requested
    if (!flushState) {
      await this.fsFlushManager.executeFlush(this.fsCache, fsMountPoint);
    }

    // wait for ongoing flush to end, then deinit
    else if (flushState?.status === FlushStateStatus.FLUSH_IN_PROGRESS) {
      await flushState.flushPromise;
    }

    // deschedule, execute flush, then deinit
    else if (flushState?.status === FlushStateStatus.FLUSH_SCHEDULED) {
      clearTimeout(flushState.scheduledFlush);
      await this.fsFlushManager.executeFlush(this.fsCache, fsMountPoint);
    }

    // simply execute flush, then deinit
    else if (flushState?.status === FlushStateStatus.FLUSH_PAUSED) {
      await this.fsFlushManager.executeFlush(this.fsCache, fsMountPoint);
    }

    // should never reach this point
    else {
      throw new Error(`Catastrophic error while managing flush before deinitting ${fsMountPoint}.`);
    }

    this.fsCache.deinitFs(fsMountPoint);
    deinitPromiseHandle.resolve();
  }
}
