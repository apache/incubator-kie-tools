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

import { FsUsageCounter } from "./FsUsageCounter";
import { FsFlushManager } from "./FsFlushManager";
import { FsCache } from "./FsCache";
import {
  imperativePromiseHandle,
  PromiseImperativeHandle,
} from "@kie-tools-core/react-hooks/dist/useImperativePromiseHandler";

export class FsUnloadManager {
  constructor(
    private readonly fsCache: FsCache,
    private readonly readWriteFsUsageCounter: FsUsageCounter,
    private readonly readonlyFsUsageCounter: FsUsageCounter,
    private readonly fsFlushManager: FsFlushManager
  ) {}

  private readonly ongoingUnloads = new Map<string, PromiseImperativeHandle<void>>();

  public async makeSpaceForOrWaitUnloadOf(fsMountPoint: string) {
    if (!this.fsCache.hasSpaceFor(fsMountPoint)) {
      this.makeSpaceFor(fsMountPoint);
    } else {
      console.debug(`No need to make space for ${fsMountPoint}.`);
    }

    // Can't forget to wait for an unloading that might be happening for the requested FS.
    await this.ongoingUnloads.get(fsMountPoint)?.promise;
  }

  public unloadFsIfMarkedAndNotInUse(fsMountPoint: string) {
    const unloadPromiseHandle = this.ongoingUnloads.get(fsMountPoint);

    // If not marked to unload, do nothing.
    if (!unloadPromiseHandle) {
      return { didTriggerUnload: false };
    }

    // Can't unload if is in use
    if (this.readWriteFsUsageCounter.isInUse(fsMountPoint) || this.readonlyFsUsageCounter.isInUse(fsMountPoint)) {
      return { didTriggerUnload: false };
    }

    // Read-write FS usages must always trigger a flush. If there's no more usages of read-write FS, then
    // We certainly need to trigger a flush.
    const flushArgs = { executeEvenIfNotScheduled: !this.readWriteFsUsageCounter.isInUse(fsMountPoint) };

    // No need to block the caller by an unloading.
    setTimeout(() => this.flushAndUnloadFs(fsMountPoint, unloadPromiseHandle, flushArgs), 0);
    return { didTriggerUnload: true };
  }

  private makeSpaceFor(fsMountPoint: string) {
    const fsMountPointToUnload = this.fsCache.getLastRecentlyUsed();
    if (this.ongoingUnloads.has(fsMountPointToUnload)) {
      return;
    }

    console.debug(`Making space for ${fsMountPoint} by unloading ${fsMountPointToUnload}.`);

    const { promise, resolve, reject } = imperativePromiseHandle<void>();
    const unloadPromiseHandle = {
      promise: promise.finally(() => this.ongoingUnloads.delete(fsMountPointToUnload)),
      resolve,
      reject,
    };

    this.ongoingUnloads.set(fsMountPointToUnload, unloadPromiseHandle);
    this.unloadFsIfMarkedAndNotInUse(fsMountPointToUnload);
  }

  private async flushAndUnloadFs(
    fsMountPoint: string,
    unloadPromiseHandle: PromiseImperativeHandle<void>,
    flushArgs: { executeEvenIfNotScheduled: boolean }
  ) {
    await this.fsFlushManager.expediteFlush(this.fsCache, fsMountPoint, flushArgs);
    this.fsCache.unloadFs(fsMountPoint);
    unloadPromiseHandle.resolve();
  }
}
