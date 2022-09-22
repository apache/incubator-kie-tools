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

export enum FlushOrDeinitStateStatus {
  FLUSH_IN_PROGRESS,
  FLUSH_SCHEDULED,
  FLUSH_PAUSED,
  DEINIT_IN_PROGRESS,
  DEINIT_SCHEDULED,
}

export type FlushOrDeinitState =
  | { scheduledTask: ReturnType<typeof setTimeout>; status: FlushOrDeinitStateStatus.FLUSH_SCHEDULED }
  | { scheduledTask: ReturnType<typeof setTimeout>; status: FlushOrDeinitStateStatus.DEINIT_SCHEDULED }
  | { runningTaskPromise: Promise<void>; status: FlushOrDeinitStateStatus.FLUSH_IN_PROGRESS }
  | { runningTaskPromise: Promise<void>; status: FlushOrDeinitStateStatus.DEINIT_IN_PROGRESS }
  | { status: FlushOrDeinitStateStatus.FLUSH_PAUSED };

export class FsFlushManager {
  public readonly stateControl = new Map<string, FlushOrDeinitState>();

  public readonly subscriptions = new Set<(active: string[]) => void>();

  private async _internalExecuteFlush(fsCache: FsCache, fsMountPoint: string, deinitArgs: { deinit: boolean }) {
    await fsCache.flushFs(fsMountPoint);
    if (deinitArgs.deinit) {
      await fsCache.deinitFs(fsMountPoint);
    }
  }

  public descheduleFlushIfScheduled(fsMountPoint: string) {
    const state = this.stateControl.get(fsMountPoint);
    if (state?.status === FlushOrDeinitStateStatus.FLUSH_SCHEDULED) {
      console.debug(`Descheduling flush for ${fsMountPoint}`);
      clearTimeout(state.scheduledTask);
      this.stateControl.set(fsMountPoint, { status: FlushOrDeinitStateStatus.FLUSH_PAUSED });
      this.notifySubscribers();
    }
  }

  public async executeFlush(fsCache: FsCache, fsMountPoint: string, deinitArgs: { deinit: boolean }) {
    const status = deinitArgs.deinit
      ? FlushOrDeinitStateStatus.DEINIT_IN_PROGRESS
      : FlushOrDeinitStateStatus.FLUSH_IN_PROGRESS;

    this.stateControl.set(fsMountPoint, {
      status,
      runningTaskPromise: this._internalExecuteFlush(fsCache, fsMountPoint, deinitArgs).then(() => {
        console.debug(`${deinitArgs.deinit ? "Deinit" : "Flush"} complete for ${fsMountPoint}`);
        this.stateControl.delete(fsMountPoint);
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
    this.stateControl.set(fsMountPoint, {
      status: FlushOrDeinitStateStatus.FLUSH_SCHEDULED,
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
    const state = this.stateControl.get(fsMountPoint);

    // No flush scheduled yet, simply schedule it.
    if (!state) {
      console.debug(`Scheduling flush for ${fsMountPoint}`);
      await this.scheduleFsFlush(fsCache, fsMountPoint, deinitArgs, debounceArgs);
    }

    // If flush is scheduled, we can always cancel it and put a flush or a deinit on its place.
    else if (state.status === FlushOrDeinitStateStatus.FLUSH_SCHEDULED) {
      console.debug(`Debouncing flush request for ${fsMountPoint}`);
      clearTimeout(state.scheduledTask);
      await this.scheduleFsFlush(fsCache, fsMountPoint, deinitArgs, debounceArgs);
    }

    // If a flush is paused, it means it was scheduled, but we know that it will be scheduled again for sure.
    else if (state.status === FlushOrDeinitStateStatus.FLUSH_PAUSED) {
      console.debug(`Resuming paused flush for ${fsMountPoint}`);
      await this.scheduleFsFlush(fsCache, fsMountPoint, deinitArgs, debounceArgs);
    }

    //
    else if (state.status === FlushOrDeinitStateStatus.DEINIT_SCHEDULED) {
      if (deinitArgs.deinit) {
        console.debug(`Debouncing deinit request for ${fsMountPoint}`);
        clearTimeout(state.scheduledTask);
        await this.scheduleFsFlush(fsCache, fsMountPoint, deinitArgs, debounceArgs);
      } else {
        // TODO: What to do?
        console.error(`Flush requested while deinit is in scheduled for ${fsMountPoint}!`);
      }
    }

    // Independent of the new request, if a flush is in progress, we need to wait for it to finish
    // So we can process the new flush request.
    else if (state.status === FlushOrDeinitStateStatus.FLUSH_IN_PROGRESS) {
      console.debug(`Flush requested while in progress for ${fsMountPoint}. Requesting another flush after completed.`);
      await state.runningTaskPromise;
      await this.requestFsFlush(fsCache, fsMountPoint, deinitArgs, debounceArgs);
    }

    // If a deinit is in progress, any non-flushed changes will be lost. This should never happen.
    else if (state.status === FlushOrDeinitStateStatus.DEINIT_IN_PROGRESS) {
      if (deinitArgs.deinit) {
        throw new Error(`Deinit requested while deinit is in progress for ${fsMountPoint}!`);
      } else {
        throw new Error(`Flush requested while deinit is in progress for ${fsMountPoint}!`);
      }
    }

    // Execution should never, ever reach this point.
    else {
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
      subscription([...this.stateControl.keys()]);
    });
  }
}
