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

import { FsCache } from "./FsCache";

export enum FlushStatus {
  FLUSH_IN_PROGRESS,
  FLUSH_AND_DEINIT_IN_PROGRESS,
  FLUSH_SCHEDULED,
  FLUSH_PAUSED,
  FLUSH_AND_DEINIT_SCHEDULED,
}

export type FlushControl =
  | { scheduledTask: ReturnType<typeof setTimeout>; status: FlushStatus.FLUSH_SCHEDULED }
  | { scheduledTask: ReturnType<typeof setTimeout>; status: FlushStatus.FLUSH_AND_DEINIT_SCHEDULED }
  | { operationPromise: Promise<void>; status: FlushStatus.FLUSH_IN_PROGRESS }
  | { operationPromise: Promise<void>; status: FlushStatus.FLUSH_AND_DEINIT_IN_PROGRESS }
  | { status: FlushStatus.FLUSH_PAUSED };

export class FsFlushManager {
  public readonly control = new Map<string, FlushControl>();

  public readonly subscriptions = new Set<(flushes: string[]) => void>();

  private async _internalExecuteFlush(fsCache: FsCache, fsMountPoint: string, deinitArgs: { deinit: boolean }) {
    await fsCache.flushFs(fsMountPoint);
    if (deinitArgs.deinit) {
      await fsCache.deinitFs(fsMountPoint);
    }
  }

  public descheduleFlush(fsMountPoint: string) {
    const control = this.control.get(fsMountPoint);
    if (control?.status === FlushStatus.FLUSH_SCHEDULED) {
      console.log(`Descheduling flush for ${fsMountPoint}`);
      clearTimeout(control.scheduledTask);
      this.control.set(fsMountPoint, { status: FlushStatus.FLUSH_PAUSED });
      this.notifySubscribers();
    }
  }

  public async executeFlush(fsCache: FsCache, fsMountPoint: string, deinitArgs: { deinit: boolean }) {
    this.control.set(fsMountPoint, {
      status: deinitArgs.deinit ? FlushStatus.FLUSH_AND_DEINIT_IN_PROGRESS : FlushStatus.FLUSH_IN_PROGRESS,
      operationPromise: this._internalExecuteFlush(fsCache, fsMountPoint, deinitArgs).then(() => {
        console.log(`Flush complete for ${fsMountPoint}`);
        this.control.delete(fsMountPoint);
        this.notifySubscribers();
      }),
    });
    this.notifySubscribers();
  }

  private async scheduleFsFlush(
    fsCache: FsCache,
    fsMountPoint: string,
    deinitArgs: { deinit: boolean },
    debounceArgs: { debounceTimeoutInMs: number }
  ) {
    this.control.set(fsMountPoint, {
      status: FlushStatus.FLUSH_SCHEDULED,
      scheduledTask: setTimeout(
        () => this.executeFlush(fsCache, fsMountPoint, deinitArgs),
        debounceArgs.debounceTimeoutInMs
      ),
    });
    this.notifySubscribers();
  }

  public async requestFsFlush(
    fsCache: FsCache,
    fsMountPoint: string,
    deinitArgs: { deinit: boolean },
    debounceArgs: { debounceTimeoutInMs: number }
  ) {
    const flushControl = this.control.get(fsMountPoint);

    if (!flushControl) {
      console.debug(`Scheduling flush for ${fsMountPoint}`);
      await this.scheduleFsFlush(fsCache, fsMountPoint, deinitArgs, debounceArgs);
    } else if (flushControl.status === FlushStatus.FLUSH_SCHEDULED) {
      // If flush is scheduled, we can always cancel it and put a flush and deinit in its place.
      console.debug(`Debouncing flush request for ${fsMountPoint}`);
      clearTimeout(flushControl.scheduledTask);
      await this.scheduleFsFlush(fsCache, fsMountPoint, deinitArgs, debounceArgs);
    } else if (flushControl.status === FlushStatus.FLUSH_PAUSED) {
      console.debug(`Resuming paused flush for ${fsMountPoint}`);
      await this.scheduleFsFlush(fsCache, fsMountPoint, deinitArgs, debounceArgs);
    } else if (flushControl.status === FlushStatus.FLUSH_AND_DEINIT_SCHEDULED) {
      if (deinitArgs.deinit) {
        console.debug(`Debouncing flush and deinit request for ${fsMountPoint}`);
        clearTimeout(flushControl.scheduledTask);
        await this.scheduleFsFlush(fsCache, fsMountPoint, deinitArgs, debounceArgs);
      } else {
        console.error(`Flush requested while flush and deinit is in scheduled!!!! ${fsMountPoint}`);
      }
    } else if (flushControl.status === FlushStatus.FLUSH_IN_PROGRESS) {
      if (deinitArgs.deinit) {
        console.error(`Flush and deinit requested while flush is in progress!!!! ${fsMountPoint}`);
      } else {
        console.error(`Flush requested while flush is in progress!!!! ${fsMountPoint}`);
      }
    } else if (flushControl.status === FlushStatus.FLUSH_AND_DEINIT_IN_PROGRESS) {
      if (deinitArgs.deinit) {
        console.error(`Flush and deinit requested while flush and deinit is in progress!!!! ${fsMountPoint}`);
      } else {
        console.error(`Flush requested while flush and deinit is in progress!!!! ${fsMountPoint}`);
      }
    } else {
      throw new Error(`Oops! Impossible scenario for flushing '${fsMountPoint}'`);
    }
  }

  subscribe(subscription: (flushes: string[]) => void) {
    this.subscriptions.add(subscription);
    return subscription;
  }

  unsubscribe(subscription: (flushes: string[]) => void) {
    this.subscriptions.delete(subscription);
  }

  private notifySubscribers() {
    this.subscriptions.forEach((subscription) => {
      subscription([...this.control.keys()]);
    });
  }
}
