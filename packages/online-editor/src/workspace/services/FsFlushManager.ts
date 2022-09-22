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
import { Subscribable } from "./Subscribable";

export enum FlushStateStatus {
  FLUSH_IN_PROGRESS,
  FLUSH_SCHEDULED,
  FLUSH_PAUSED,
}

export type FlushState =
  | { scheduledFlush: ReturnType<typeof setTimeout>; status: FlushStateStatus.FLUSH_SCHEDULED }
  | { flushPromise: Promise<void>; status: FlushStateStatus.FLUSH_IN_PROGRESS }
  | { status: FlushStateStatus.FLUSH_PAUSED };

export class FsFlushManager {
  public readonly stateControl = new Map<string, FlushState>();

  public readonly subscribable = new Subscribable<string[]>({
    newValueSupplier: () => [...this.stateControl.keys()],
  });

  public pauseFlushScheduleIfScheduled(fsMountPoint: string) {
    const state = this.stateControl.get(fsMountPoint);
    if (state?.status === FlushStateStatus.FLUSH_SCHEDULED) {
      console.debug(`Pausing scheduled flush for ${fsMountPoint}`);
      clearTimeout(state.scheduledFlush);
      this.stateControl.set(fsMountPoint, { status: FlushStateStatus.FLUSH_PAUSED });
      this.subscribable._notifySubscribers();
    }
  }

  public async requestFsFlush(fsCache: FsCache, fsMountPoint: string, debounceArgs: { debounceTimeoutInMs: number }) {
    const state = this.stateControl.get(fsMountPoint);

    // No flush scheduled yet, simply schedule it.
    if (!state) {
      console.debug(`Scheduling flush for ${fsMountPoint}`);
      this.scheduleFsFlush(fsCache, fsMountPoint, debounceArgs);
    }

    // If flush is scheduled, we cancel the scheduled flush and schedule a new one.
    else if (state.status === FlushStateStatus.FLUSH_SCHEDULED) {
      console.debug(`Debouncing flush request for ${fsMountPoint}`);
      clearTimeout(state.scheduledFlush);
      this.scheduleFsFlush(fsCache, fsMountPoint, debounceArgs);
    }

    // If a flush is paused, it means it was scheduled, but we know that it will be scheduled again for sure.
    else if (state.status === FlushStateStatus.FLUSH_PAUSED) {
      console.debug(`Resuming paused flush for ${fsMountPoint}`);
      this.scheduleFsFlush(fsCache, fsMountPoint, debounceArgs);
    }

    // Independent of the new request, if a flush is in progress, we need to wait for it to finish
    // So we can process the new flush request.
    else if (state.status === FlushStateStatus.FLUSH_IN_PROGRESS) {
      console.debug(`Flush requested while in progress for ${fsMountPoint}. Requesting another flush after completed.`);
      await state.flushPromise;
      console.debug(`Flush requested right after one completed for ${fsMountPoint}.`);
      await this.requestFsFlush(fsCache, fsMountPoint, debounceArgs);
    }

    // Execution should never, ever reach this point.
    else {
      throw new Error(`Oops! Impossible scenario for flushing '${fsMountPoint}'`);
    }
  }

  public async executeFlush(fsCache: FsCache, fsMountPoint: string) {
    const flush = fsCache.flushFs(fsMountPoint).then(() => {
      console.debug(`Flush complete for ${fsMountPoint}`);
      this.stateControl.delete(fsMountPoint);
      this.subscribable._notifySubscribers();
    });

    this.stateControl.set(fsMountPoint, {
      status: FlushStateStatus.FLUSH_IN_PROGRESS,
      flushPromise: flush,
    });

    this.subscribable._notifySubscribers();

    return flush;
  }

  private scheduleFsFlush(fsCache: FsCache, fsMountPoint: string, debounceArgs: { debounceTimeoutInMs: number }) {
    const flushScheduledTask = setTimeout(
      () => this.executeFlush(fsCache, fsMountPoint),
      debounceArgs.debounceTimeoutInMs
    );

    this.stateControl.set(fsMountPoint, {
      status: FlushStateStatus.FLUSH_SCHEDULED,
      scheduledFlush: flushScheduledTask,
    });

    this.subscribable._notifySubscribers();

    return flushScheduledTask;
  }
}
